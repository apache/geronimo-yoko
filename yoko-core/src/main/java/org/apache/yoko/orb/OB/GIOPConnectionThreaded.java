/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.yoko.orb.OB;

import org.apache.yoko.giop.MessageType;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.io.Buffer;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.orb.OCI.Transport;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.rmi.util.ObjectUtil;
import org.apache.yoko.util.Assert;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSIENT;
import org.omg.GIOP.MsgType_1_1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static org.apache.yoko.orb.OB.Connection.Access.READ;
import static org.apache.yoko.orb.OB.Connection.Access.WRITE;
import static org.apache.yoko.orb.OB.Connection.State.CLOSED;
import static org.apache.yoko.orb.OB.Connection.State.CLOSING;
import static org.apache.yoko.orb.OB.Connection.State.ERROR;
import static org.apache.yoko.orb.OB.Connection.State.STALE;
import static org.apache.yoko.util.MinorCodes.MinorSend;
import static org.apache.yoko.util.MinorCodes.MinorThreadLimit;
import static org.apache.yoko.util.MinorCodes.describeCommFailure;
import static org.apache.yoko.util.MinorCodes.describeImpLimit;
import static org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly;
import static org.apache.yoko.orb.OCI.SendReceiveMode.SendOnly;
import static org.apache.yoko.logging.VerboseLogging.CONN_IN_LOG;
import static org.apache.yoko.logging.VerboseLogging.CONN_LOG;
import static org.apache.yoko.logging.VerboseLogging.REQ_OUT_LOG;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final class GIOPConnectionThreaded extends GIOPConnection {

    private final class Shutdown implements Runnable {

        public void run() {
            try {
                execShutdown();
            } catch (RuntimeException ex) {
                throw Assert.fail(ex);
            }
        }
    }

    private final class Receiver implements Runnable {
        Receiver() {
            receiverLock.readLock().lock();
        }

        public void run() {
            try {
                execReceive();
            } catch (RuntimeException ex) {
                throw Assert.fail(ex);
            } finally {
                receiverLock.readLock().unlock();
            }
        }
    }

    private static final class ThreadGate {
        private boolean closed = true;

        /** Admit the current thread once open */
        synchronized void admit() {
            while (closed) {
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
        }

        synchronized void open() {
            if (closed) {
                this.closed = false;
                notifyAll();
            }
        }

        synchronized void close() {
            this.closed = true;
        }
    }

    private static final class SendMutex{}

    private final ThreadGate gate = new ThreadGate();
    private final SendMutex sendMutex = new SendMutex();
    private final ReentrantReadWriteLock receiverLock = new ReentrantReadWriteLock(true);
    private boolean shuttingDown;

    // add a new receiver thread
    // Assumes 'this' is synchronized on entry
    private void addReceiverThread() {
        getExecutor().submit(new Receiver());
    }

    // abortive shutdown method from GIOPConnection
    void abortiveShutdown() {
        // disable any ACM timeouts now
        ACM_disableIdleMonitor();

        // The transport must be able to send in order to send the error
        // message...
        if (transport_.mode() != ReceiveOnly) {
            try {
                // Send a MessageError message
                try (OutputStream out = new OutputStream(12)) {

                    ProfileInfo profileInfo = new ProfileInfo();

                    synchronized (this) {
                        profileInfo.major = giopVersion_.major;
                        profileInfo.minor = giopVersion_.minor;
                    }

                    GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out, profileInfo);

                    outgoing.writeMessageHeader(MsgType_1_1.MessageError, false, 0);
                    out.setPosition(0);

                    synchronized (sendMutex) {
                        final ReadBuffer readBuffer = out.getBufferReader();
                        transport_.send(readBuffer, true);
                        Assert.ensure(readBuffer.isComplete());
                    }
                }
            } catch (SystemException ex) {
                processException(CLOSED, ex, false);
                return;
            }
        }

        // If we are in StateError, we don't go through all the hula hoop
        // with continuing to receive messages until the peer
        // closes. Instead, we just close the connection, meaning that we
        // can't be 100% sure that the peer gets the last message.
        processException(CLOSED, Transients.FORCED_SHUTDOWN.create(), false);
        arrive();

    }

    // graceful shutdown method
    synchronized void gracefulShutdown() {
        // disable any ACM idle timeouts now
        ACM_disableIdleMonitor();

        // don't shutdown if there are pending upcalls
        if (upcallsInProgress_ > 0 || getState() != CLOSING) {
            CONN_IN_LOG.info("pending upcalls: " + upcallsInProgress_ + " state: " + getState());
            return;
        }

        // send a CloseConnection if we can
        if (canSendCloseConnection()) {
            try (OutputStream out = new OutputStream(12)) {

                ProfileInfo profileInfo = new ProfileInfo();
                profileInfo.major = giopVersion_.major;
                profileInfo.minor = giopVersion_.minor;

                GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out, profileInfo);
                outgoing.writeMessageHeader(MsgType_1_1.CloseConnection, false, 0);

                messageQueue_.add(orbInstance_, out.getBufferReader());
            }
        } else {
            CONN_IN_LOG.fine("could not send close connection message");
        }

        // now create the shutdown thread
        try {
            if (shuttingDown)
                return;

            shuttingDown = true;
            // start the shutdown thread
            try {
                getExecutor().submit(new Shutdown());
            } catch (RejectedExecutionException ree) {
                CONN_IN_LOG.log(Level.WARNING, "Could not submit shutdown task", ree);
            }
        } catch (OutOfMemoryError ex) {
            processException(CLOSED, new IMP_LIMIT(describeImpLimit(MinorThreadLimit), MinorThreadLimit, COMPLETED_NO), false);
        } finally {
            arrive();
        }
    }

    private void arrive() {
        if (this.isOutbound())
            orbInstance_.getClientPhaser().arriveAndDeregister();
        else
            orbInstance_.getServerPhaser().arriveAndDeregister();
    }

    // client-side constructor
    GIOPConnectionThreaded(ORBInstance orbInstance, Transport transport, GIOPClient client) {
        super(orbInstance, transport, client);
        orbInstance.getClientPhaser().register();
        start();
    }

    // server-side constructor
    GIOPConnectionThreaded(ORBInstance orbInstance, Transport transport, OAInterface oa) {
        super(orbInstance, transport, oa);
        orbInstance.getServerPhaser().register();
    }

    private ExecutorService getExecutor() {
        if (this.isOutbound())
            return orbInstance_.getClientExecutor();
        else
            return orbInstance_.getServerExecutor();
    }

    // called from the shutdown thread to initiate shutdown
    private void execShutdown() {
        if (canSendCloseConnection() && transport_.mode() != ReceiveOnly) {
            try {
                synchronized (this) {
                    while (messageQueue_.hasUnsent()) {
                        // It's possible the CloseConnection message got sent via another means.
                        ReadBuffer readBuffer = messageQueue_.getFirstUnsentBuffer();
                        if (readBuffer != null) {
                            synchronized (sendMutex) {
                                transport_.send(readBuffer, true);
                            }

                            messageQueue_.moveFirstUnsentToPending();
                        }
                    }
                }
            } catch (SystemException ex) {
                processException(CLOSED, ex, false);
                return;
            }
        }

        // shutdown the transport
        // synchronization on sendMutex_ is needed to avoid a deadlock in some oracle and ibm jdks between send and shutdown
        // https://bugs.openjdk.java.net/browse/JDK-8013809 deadlock in SSLSocketImpl between between write and close
        synchronized (sendMutex) {
            transport_.shutdown();
        }

        // Shutdown the receiver threads. There may not be a receiver
        // thread if the transport is SendOnly.
        try {
            receiverLock.writeLock().tryLock(shutdownTimeout_, SECONDS);
        } catch (InterruptedException ignored) {
        }

        try {
            // We now close the connection actively, since it may still be
            // open under certain circumstances. For example, the receiver
            // thread may not have terminated yet or the receive thread might
            // set the state to GIOPState::Error before termination.
            processException(CLOSED, Transients.FORCED_SHUTDOWN.create(), false);
        } finally {
            if (receiverLock.isWriteLockedByCurrentThread()) {
                receiverLock.writeLock().unlock();
            }
        }
    }


    // called from a receiver thread to perform a reception
    private void execReceive() {
        if (CONN_IN_LOG.isLoggable(FINE)) CONN_IN_LOG.fine("Receiving incoming message " + this);
        GIOPIncomingMessage inMsg = new GIOPIncomingMessage(orbInstance_);

        while (true) {
            // Setup the incoming message buffer
            WriteBuffer writer = Buffer.createWriteBuffer(12);

            // Receive header, blocking, detect connection loss
            try {
                CONN_IN_LOG.finest("Reading message header");
                transport_.receive(writer, true);
                Assert.ensure(writer.isComplete());
            } catch (SystemException ex) {
                processException(CLOSED, ex, false);
                break;
            }

            // Header is complete
            try {
                inMsg.extractHeader(writer.readFromStart());
                if (CONN_IN_LOG.isLoggable(FINER)) CONN_IN_LOG.finer("Header received for message of size " + inMsg.size());
                // grow the buffer
                writer.ensureAvailable(inMsg.size());
            } catch (SystemException ex) {
                processException(ERROR, ex, false);
                break;
            }

            if (!writer.isComplete()) {
                // Receive body, blocking
                try {
                    if (CONN_IN_LOG.isLoggable(FINER)) CONN_IN_LOG.finer("Receiving message body of size " + inMsg.size());
                    transport_.receive(writer, true);
                    Assert.ensure(writer.isComplete());
                } catch (SystemException ex) {
                    processException(CLOSED, ex, false);
                    break;
                }
            }

            MessageType.logIncomingGiopMessage(writer);

            gate.admit();

            // If we are not in StateActive or StateClosing, stop this
            // thread. We do *not* stop this thread if we are in
            // StateClosing, since we must continue to read data from
            // the Transport to make sure that no messages can get
            // lost upon close, and to make sure that CloseConnection
            // messages from the peer are processed.
            synchronized (this) { if (getState().forbids(READ)) break; }

            // the upcall to invoke
            Upcall upcall = null;

            try {
                if (inMsg.consumeBuffer(writer)) upcall = processMessage(inMsg);

            } catch (SystemException ex) {
                processException(ERROR, ex, false);
                break;
            }

            if (upcall == null) {continue;}

            // A valid upcall means we have a full message and not just
            // a fragment or error, so we can proceed to invoke it
            if (CONN_IN_LOG.isLoggable(FINER)) CONN_IN_LOG.finer("Processing message using upcall " + upcall.getClass().getName());
            // in the BiDir case, this upcall could result in a
            // nested call back and forth. This requires a new
            // receiverThread to handle the reply (the invocation of
            // the upcall doesn't return back into a receiving state
            // until the function processing is done)
            boolean receivedBidirContext = transport_.get_info().received_bidir_service_context();

            // if we have received a bidirectional context then we need
            // to spawn a new thread to handle nested calls (just in case)
            if (receivedBidirContext) {
                if (CONN_IN_LOG.isLoggable(FINER)) CONN_IN_LOG.finer("In bidirectional mode, so submitting a new receiver task");
                addReceiverThread();
            }

            upcall.invoke();

            // if we've spawned a new thread to handle nested calls
            // then we can quit this thread because we know another
            // will be ready to take over anyway
            if (receivedBidirContext) break;
        }
    }

    // ACM callback method on ACM signal
    synchronized void ACM_callback() {
        if (acmTimer_ != null) {
            acmTimer_.cancel();
            acmTimer_ = null;
        }

        if (acmTask_ != null) {
            acmTask_.cancel();
            acmTask_ = null;
        }

        // don't shutdown if there are unsent messages or if there are
        // upcalls in progress
        if (messageQueue_.hasUnsent() || (upcallsInProgress_ > 0)) {
            ACM_enableIdleMonitor();
            return;
        }

        // shutdown gracefully
        setState(CLOSING);
    }

    // client-side send method (from DowncallEmitter)
    public boolean send(Downcall down, boolean block) {
        Assert.ensure(transport_.mode() != ReceiveOnly);
        Assert.ensure(down.unsent());

        if (REQ_OUT_LOG.isLoggable(FINE)) REQ_OUT_LOG.fine("Sending a request downcall=" + down + " transport=" + transport_);

        // if we send off a message in the loop, this var might help us
        // to prevent a further locking to check the status
        boolean msgSentMarked = false;

        // if we don't have writing turned on then we must throw a
        // TRANSIENT to the caller indicating this
        synchronized (this) {

            if (checkWriteProhibited(down)) return true;

            // make the downcall thread-safe
            if (down.responseExpected()) {
                down.allowWaiting();
            }

            // buffer the request
            messageQueue_.add(orbInstance_, down);

            // check the sent status while we're locked
            if (isRequestSent()) {
                msgSentMarked = true;
            }
        }

        // now prepare to send it either blocking or non-blocking
        // depending on the call mode param
        if (block) { // TODO - deduplicate these if and else blocks
            // Get the request timeout
            int t = down.policies().requestTimeout;
            int msgcount = 0;

            // now we can start sending off the messages
            for (;;) {
                // Get a message to send from the unsent queue
                final ReadBuffer readBuffer;
                final Downcall nextDown;

                synchronized (this) {
                    if (!down.unsent()) break;
                    Assert.ensure(messageQueue_.hasUnsent());
                    readBuffer = messageQueue_.getFirstUnsentBuffer();
                    nextDown = messageQueue_.moveFirstUnsentToPending();
                }

                // Send the message
                try {
                    synchronized (sendMutex) {
                        if (t <= 0) {
                            // Send buffer, blocking
                            transport_.send(readBuffer, true);
                            Assert.ensure(readBuffer.isComplete());
                        } else {
                            // Send buffer, with timeout
                            transport_.send_timeout(readBuffer, t);

                            // Timeout?
                            if (!readBuffer.isComplete()) throw new NO_RESPONSE();
                        }
                    }
                } catch (SystemException ex) {
                    processException(CLOSED, ex, false);
                    return true;
                }

                // a message should be sent by now so we have to mark it as sent for the GIOPClient
                if (!(msgSentMarked || nextDown == null || nextDown.operation().equals("_locate"))) {
                    msgSentMarked = true;
                    markRequestSent();
                    if (REQ_OUT_LOG.isLoggable(FINE)) REQ_OUT_LOG.fine(format("Sent message blocking=%s msgcount=%d size=%d", block, msgcount++, readBuffer.length()));
                }
            }
        } else { // Non blocking
            synchronized (this) {
            	int msgcount = 0;
                for (;;) {
                    if (!down.unsent()) break;
                    Assert.ensure(messageQueue_.hasUnsent());
                    ReadBuffer readBuffer = messageQueue_.getFirstUnsentBuffer();

                    // send this buffer, non-blocking
                    try {
                        synchronized (sendMutex) {
                            transport_.send(readBuffer, false);
                        }
                    } catch (SystemException ex) {
                        processException(CLOSED, ex, false);
                        return true;
                    }

                    // if the buffer isn't full, it hasn't been sent because
                    // the call would have blocked.
                    if (!readBuffer.isComplete())
                        return false;

                    // now move to the pending pile
                    Downcall dummy = messageQueue_.moveFirstUnsentToPending();

                    // update the message sent property
                    if (!msgSentMarked && dummy != null) {
                        if (dummy.responseExpected() && dummy.operation().equals("_locate")) {
                            msgSentMarked = true;
                            markRequestSent();
                            if (REQ_OUT_LOG.isLoggable(FINE)) REQ_OUT_LOG.fine(format("Sent message blocking=%s msgcount=%d size=%d", block, msgcount++, readBuffer.length()));
                        }
                    }
                }
            }
        }

        if (REQ_OUT_LOG.isLoggable(FINER)) REQ_OUT_LOG.finer(" Request send completed downcall=" + down);
        return !down.responseExpected();
    }

    private boolean checkWriteProhibited(Downcall down) {
        final State state = getState();
        final boolean writeProhibited;
        switch (state) {
        case ACTIVE:
        case HOLDING:
        case CLOSING:
            writeProhibited = false;
            break;

        case STALE:
            // This connection has already thrown a TRANSIENT and is now being re-used.
            // Ensure this connection is cleaned up but the retry and hop counts are not incremented.
            down.notifyStaleConnection();
        case CLOSED:
            setState(STALE);
        case ERROR:
            CONN_LOG.fine("writing not enabled for this connection");
            down.setFailureException(new TRANSIENT());
            writeProhibited = true;
            break;
        default:
            throw Assert.fail("Unknown connection state: " + state );
        }
        return writeProhibited;
    }

    // client-side receive method (from DowncallEmitter)
    public boolean receive(Downcall down, boolean block) {
        if (REQ_OUT_LOG.isLoggable(FINER)) REQ_OUT_LOG.finer("Receiving response downcall=" + down + " transport=" + transport_);
        // Try to receive the reply
        try {
            boolean result = down.waitUntilCompleted(block);
            if (REQ_OUT_LOG.isLoggable(FINE)) REQ_OUT_LOG.fine("Received response downcall=" + down + " from transport " + transport_);
            return result;
        } catch (SystemException ex) {
            processException(CLOSED, ex, false);
            return true;
        }
    }

    // client-side sendReceive (from DowncallEmitter)
    public boolean sendReceive(Downcall down) {
        ACM_disableIdleMonitor();

        try {
            return send(down, true) || receive(down, true);
        } finally {
            ACM_enableIdleMonitor();
        }
    }

    void start() {
        // unpause any paused threads
        gate.open();

        // check if we need to add a receiver thread
        if (transport_.mode() != SendOnly) {
            try {
                // If the write lock is obtainable there are no receivers outstanding.
                // We can then add a receiver, which implicitly obtains a read lock.
                // ReentrantReadWriteLock explicitly allows downgrading a write lock to a read lock.
                if(receiverLock.writeLock().tryLock()) {
                    try {
                        addReceiverThread();
                    } finally {
                        receiverLock.writeLock().unlock();
                    }
                }
            } catch (OutOfMemoryError ex) {
                SystemException sysEx = new IMP_LIMIT(describeImpLimit(MinorThreadLimit), MinorThreadLimit, COMPLETED_NO);
                sysEx.initCause(ex);
                processException(CLOSED, sysEx, false);
                throw sysEx;
            }
        }
    }

    void refresh() {
        boolean msgSentMarked = false;
        gate.open();

        synchronized (this) {
            // if we can't write messages then don't bother to proceed
            if (getState().forbids(WRITE)) return;

            // check if we've sent a message before while we are locked
            if (isRequestSent()) msgSentMarked = true;
        }

        // another check if we can write or not
        if (transport_.mode() == ReceiveOnly)
            return;

        // now send off any queued messages
        while (true) {
            ReadBuffer readBuffer;
            Downcall dummy;

            try {
                synchronized (this) {
                    // stop when no messages left
                    if (!messageQueue_.hasUnsent())
                        break;

                    readBuffer = messageQueue_.getFirstUnsentBuffer();
                    readBuffer.rewindToStart();
                    dummy = messageQueue_.moveFirstUnsentToPending();
                }

                // make sure no two threads are sending at once
                synchronized (sendMutex) {
                    transport_.send(readBuffer, true);
                }

                // check if the buffer has been read to the end
                // Some of the OCI plugins (bidir for example) will
                // simply return instead of throwing an exception if the
                // send fails
                if (!readBuffer.isComplete()) throw new COMM_FAILURE(describeCommFailure(MinorSend), MinorSend, COMPLETED_NO);

                // mark the message sent flag
                if (!msgSentMarked && (dummy != null)) {
                    if (dummy.responseExpected() && dummy.operation().equals("_locate")) {
                        synchronized (this) {
                            msgSentMarked = true;
                            markRequestSent();
                        }
                    }
                }
            } catch (SystemException ex) {
                processException(CLOSED, ex, false);
                return;
            }
        }
    }

    void pause() { gate.close(); }

    @Override
    public String toString() { return label + ": state = " + this.getState(); }
    private final String label = ObjectUtil.getNextObjectLabel(this.getClass());
}

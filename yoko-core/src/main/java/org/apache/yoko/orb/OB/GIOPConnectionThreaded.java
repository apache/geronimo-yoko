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

import java.util.logging.Level;
import java.util.logging.Logger;

public final class GIOPConnectionThreaded extends GIOPConnection {
    static final Logger logger = Logger.getLogger(GIOPConnectionThreaded.class.getName());
    // ----------------------------------------------------------------
    // Inner helper classes
    // ----------------------------------------------------------------

    //
    // thread to handle connection shutdown
    // 
    public final class ShutdownThread extends Thread {
        private GIOPConnectionThreaded parent_;

        ShutdownThread(ThreadGroup group, GIOPConnectionThreaded parent) {
            super(group, "Yoko:GIOPConnectionThreaded:ShutdownThread");
            parent_ = parent;
        }

        public void run() {
            try {
                parent_.execShutdown();
            } catch (RuntimeException ex) {
                Assert._OB_assert(ex);
            }

            //
            // break cyclic dependency with parent
            //
            parent_ = null;
        }
    }

    //
    // thread to handle reception of messages
    // 
    public final class ReceiverThread extends Thread {
        private GIOPConnectionThreaded parent_;

        ReceiverThread(ThreadGroup group, GIOPConnectionThreaded parent) {
            super(group, "Yoko:GIOPConnectionThreaded:ReceiverThread");
            parent_ = parent;
        }

        public void run() {
            try {
                parent_.execReceive();
            } catch (RuntimeException ex) {
                Assert._OB_assert(ex);
            }

            //
            // break cyclic dependency with parent
            //
            parent_ = null;
        }
    }

    // ----------------------------------------------------------------
    // Member data
    // ----------------------------------------------------------------
    // 

    //
    // the shutdown thread handle
    //
    protected Thread shutdownThread_ = null;

    //
    // the list of receiver threads
    //
    protected java.util.LinkedList receiverThreads_ = new java.util.LinkedList();

    //
    // the holding monitor to pause the receiver threads
    //
    protected java.lang.Object holdingMonitor_ = new java.lang.Object();

    //
    // are we holding or not
    //
    protected boolean holding_ = true;

    //
    // sending mutex to prevent multiple threads from sending at once
    //
    protected java.lang.Object sendMutex_ = new java.lang.Object();

    // ----------------------------------------------------------------
    // Protected Methods
    // ----------------------------------------------------------------

    //
    // add a new receiver thread
    // Assumes 'this' is synchronized on entry
    //
    protected void addReceiverThread() {
        //
        // Retrieve the thread group
        //
        ThreadGroup group;
        if ((properties_ & Property.CreatedByClient) != 0) {
            group = orbInstance_.getClientWorkerGroup();
        }
        else {
            group = orbInstance_.getServerWorkerGroup();
        }

        //
        // Start receiver thread
        //
        Thread thr = new ReceiverThread(group, this);
        thr.setDaemon(true); 
        thr.start();

        //
        // add the thread to our list of threads
        //
        receiverThreads_.addLast(thr);
    }

    //
    // clean up any dead receiver threads
    // assumes 'this' is synchronized on entry
    //
    protected void cleanupDeadReceiverThreads() {
        java.util.ListIterator i = receiverThreads_.listIterator(0);

        while (i.hasNext()) {
            Thread thr = (Thread) i.next();

            if (!thr.isAlive()) {
                i.remove();
            }
        }
    }

    //
    // pause a thread on a holding monitor if turned on
    //
    protected void pauseThread() {
        synchronized (holdingMonitor_) {
            while (holding_) {
                try {
                    holdingMonitor_.wait();
                } catch (InterruptedException ex) {
                    //
                    // ignore exception and continue to wait
                    // 
                }
            }
        }
    }

    //
    // abortive shutdown method from GIOPConnection
    //
    protected void abortiveShutdown() {
        //
        // disable any ACM timeouts now
        //
        ACM_disableIdleMonitor();

        //
        // The transport must be able to send in order to send the error
        // message...
        // 
        if (transport_.mode() != org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly) {
            try {
                //
                // Send a MessageError message
                //
                org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                        12);
                org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                        buf);

                org.apache.yoko.orb.OCI.ProfileInfo profileInfo = new org.apache.yoko.orb.OCI.ProfileInfo();

                synchronized (this) {
                    profileInfo.major = giopVersion_.major;
                    profileInfo.minor = giopVersion_.minor;
                }

                GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(
                        orbInstance_, out, profileInfo);

                outgoing.writeMessageHeader(
                        org.omg.GIOP.MsgType_1_1.MessageError, false, 0);
                out._OB_pos(0);

                synchronized (sendMutex_) {
                    transport_.send(out._OB_buffer(), true);
                }
                Assert._OB_assert(out._OB_buffer().is_full());
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Closed, ex, false);
                return;
            }
        }

        //
        // If we are in StateError, we don't go through all the hula hoop
        // with continuing to receive messages until the peer
        // closes. Instead, we just close the connection, meaning that we
        // can't be 100% sure that the peer gets the last message.
        //
        processException(State.Closed, new org.omg.CORBA.TRANSIENT(org.apache.yoko.orb.OB.MinorCodes
                .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorForcedShutdown),
                org.apache.yoko.orb.OB.MinorCodes.MinorForcedShutdown,
                org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);

    }

    //
    // graceful shutdown method
    //
    synchronized protected void gracefulShutdown() {
        //
        // disable any ACM idle timeouts now
        //
        ACM_disableIdleMonitor();

        //
        // don't shutdown if there are pending upcalls
        // 
        if (upcallsInProgress_ > 0 || state_ != State.Closing)
            return;

        //
        // send a CloseConnection if we can
        //
        if (canSendCloseConnection()) {
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    12);
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);

            org.apache.yoko.orb.OCI.ProfileInfo profileInfo = new org.apache.yoko.orb.OCI.ProfileInfo();
            profileInfo.major = giopVersion_.major;
            profileInfo.minor = giopVersion_.minor;

            GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(
                    orbInstance_, out, profileInfo);
            outgoing.writeMessageHeader(
                    org.omg.GIOP.MsgType_1_1.CloseConnection, false, 0);

            messageQueue_.add(orbInstance_, out._OB_buffer());
        }

        //
        // now create the startup thread
        //
        try {
            if (shutdownThread_ != null)
                return;

            //
            // Retrieve the thread group
            //
            ThreadGroup group;
            if ((properties_ & Property.CreatedByClient) != 0)
                group = orbInstance_.getClientWorkerGroup();
            else
                group = orbInstance_.getServerWorkerGroup();

            //
            // start the shutdown thread
            //
            shutdownThread_ = new ShutdownThread(group, this);
            shutdownThread_.setDaemon(true);
            shutdownThread_.start();
        } catch (OutOfMemoryError ex) {
            processException(State.Closed, new org.omg.CORBA.IMP_LIMIT(
                    org.apache.yoko.orb.OB.MinorCodes.describeImpLimit(org.apache.yoko.orb.OB.MinorCodes.MinorThreadLimit),
                    org.apache.yoko.orb.OB.MinorCodes.MinorThreadLimit,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO), false);
        }
    }

    // ----------------------------------------------------------------
    // Public Methods
    // ----------------------------------------------------------------

    //
    // client-side constructor
    //
    public GIOPConnectionThreaded(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Transport transport, GIOPClient client) {
        super(orbInstance, transport, client);
        start();
    }

    //
    // server-side constructor
    //
    public GIOPConnectionThreaded(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Transport transport, OAInterface oa) {
        super(orbInstance, transport, oa);
    }

    //
    // called from the shutdown thread to initiate shutdown
    // 
    public void execShutdown() {
        if (canSendCloseConnection()
                && transport_.mode() != org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly) {
            try {
                synchronized (this) {
                    while (messageQueue_.hasUnsent()) {
                        //
                        // Its possible the CloseConnection message got sent
                        // via another means.
                        //
                        org.apache.yoko.orb.OCI.Buffer buf = messageQueue_
                                .getFirstUnsentBuffer();
                        if (buf != null) {
                            synchronized (sendMutex_) {
                                transport_.send(buf, true);
                            }

                            messageQueue_.moveFirstUnsentToPending();
                        }
                    }
                }
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Closed, ex, false);
                return;
            }
        }

        // 
        // shutdown the transport
        // 
        transport_.shutdown();

        //
        // Shutdown the receiver threads. There may not be a receiver
        // thread if the transport is SendOnly.
        //
        if (transport_.mode() == org.apache.yoko.orb.OCI.SendReceiveMode.SendReceive
                || transport_.mode() == org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly) {
            int timeout = shutdownTimeout_ * 1000;

            synchronized (this) {
                java.util.ListIterator i = receiverThreads_.listIterator();

                while (i.hasNext()) {
                    Thread t = (Thread) i.next();

                    try {
                        if (timeout > 0) {
                            t.join(timeout);
                        }
                        else {
                            t.join();
                        }
                    } catch (InterruptedException ex) {
                        continue;
                    }

                    i.remove();
                }
            }
        }

        //
        // We now close the connection actively, since it may still be
        // open under certain circumstances. For example, the reciver
        // thread may not have terminated yet or the receive thread might
        // set the state to GIOPState::Error before termination.
        //
        processException(State.Closed, new org.omg.CORBA.TRANSIENT(org.apache.yoko.orb.OB.MinorCodes
                .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorForcedShutdown),
                org.apache.yoko.orb.OB.MinorCodes.MinorForcedShutdown,
                org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
    }

    //
    // called from a receiver thread to perform a reception
    //
    public void execReceive() {
        
        logger.fine("Receiving incoming message " + this); 
        GIOPIncomingMessage inMsg = new GIOPIncomingMessage(orbInstance_);
        org.apache.yoko.orb.OCI.Buffer buf = null;

        while (true) {
            //
            // Setup the incoming message buffer
            //
            Assert._OB_assert(buf == null);
            buf = new org.apache.yoko.orb.OCI.Buffer(12);

            //
            // Receive header, blocking, detect connection loss
            //
            try {
                logger.fine("Reading message header"); 
                transport_.receive(buf, true);
                Assert._OB_assert(buf.is_full());
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Closed, ex, false);
                break;
            }

            //
            // Header is complete
            //
            try {
                inMsg.extractHeader(buf);
                logger.fine("Header received for message of size " + inMsg.size()); 
                buf.realloc(12 + inMsg.size());
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Error, ex, false);
                break;
            }

            if (!buf.is_full()) {
                //
                // Receive body, blocking
                //
                try {
                    logger.fine("Receiving message body of size " + inMsg.size()); 
                    transport_.receive(buf, true);
                    Assert._OB_assert(buf.is_full());
                } catch (org.omg.CORBA.SystemException ex) {
                    processException(State.Closed, ex, false);
                    break;
                }
                logger.fine("Message body received "); 
            }

            //
            // pause thread if necessary
            //
            pauseThread();

            //
            // If we are not in StateActive or StateClosing, stop this
            // thread. We do *not* stop this thread if we are in
            // StateClosing, since we must continue to read data from
            // the Transport to make sure that no messages can get
            // lost upon close, and to make sure that CloseConnection
            // messages from the peer are processed.
            //
            synchronized (this) {
                if ((enabledOps_ & AccessOp.Read) == 0) 
                {
                    break;
                }
            }

            //
            // the upcall to invoke
            // 
            Upcall upcall = null;

            try {
                org.apache.yoko.orb.OCI.Buffer bufCopy = buf;
                buf = null;
                if (inMsg.consumeBuffer(bufCopy) == true) {
                    upcall = processMessage(inMsg);
                }
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Error, ex, false);
                break;
            }

            // 
            // A valid upcall means we have a full message and not just
            // a fragment or error, so we can proceed to invoke it
            // 
            if (upcall != null) {
                logger.fine("Processing message using upcall " + upcall.getClass().getName()); 
                // 
                // in the BiDir case, this upcall could result in a
                // nested call back and forth. This requires a new
                // receiverThread to handle the reply (the invocation of
                // the upcall doesn't return back into a receiving state
                // until the function processing is done)
                //
                boolean haveBidirSCL = transport_.get_info().received_bidir_SCL();

                //
                // if we have received a bidirectional SCL then we need
                // to spawn a new thread to handle nested calls (just in
                // case)
                // 
                if (haveBidirSCL) {
                    synchronized (this) {
                        addReceiverThread();
                    }
                }

                upcall.invoke();

                //
                // if we've spawned a new thread to handle nested calls
                // then we can quit this thread because we know another
                // will be ready to take over anyway
                // 
                if (haveBidirSCL) {
                    break;
                }
            }
        }
    }

    //
    // ACM callback method on ACM signal
    //
    synchronized public void ACM_callback() {
        if (acmTimer_ != null) {
            acmTimer_.cancel();
            acmTimer_ = null;
        }

        if (acmTask_ != null) {
            acmTask_.cancel();
            acmTask_ = null;
        }

        //
        // don't shutdown if there are unsent messages or if there are
        // upcalls in progress
        // 
        if (messageQueue_.hasUnsent() || (upcallsInProgress_ > 0)) {
            ACM_enableIdleMonitor();
            return;
        }

        //
        // shutdown gracefully
        //
        setState(State.Closing);
    }

    //
    // client-side send method (from DowncallEmitter)
    //
    public boolean send(Downcall down, boolean block) {
        Assert._OB_assert(transport_.mode() != org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly);
        Assert._OB_assert(down.unsent() == true);
        
        logger.fine("Sending a request with Downcall of type " + down.getClass().getName() + " for operation " + down.operation() + " on transport " + transport_); 

        //
        // if we send off a message in the loop, this var might help us
        // to prevent a further locking to check the status
        //
        boolean msgSentMarked = false;

        //
        // if we don't have writing turned on then we must throw a
        // TRANSIENT to the caller indicating this
        //
        synchronized (this) {
            if ((enabledOps_ & AccessOp.Write) == 0) {
                logger.fine("writing not enabled for this connection"); 
                down.setFailureException(new org.omg.CORBA.TRANSIENT());
                return true;
            }

            //
            // make the downcall thread-safe
            //
            if (down.responseExpected()) {
                down.initStateMonitor();
            }

            // 
            // buffer the request
            //
            messageQueue_.add(orbInstance_, down);

            //
            // check the sent status while we're locked
            //
            if ((properties_ & Property.RequestSent) != 0) {
                msgSentMarked = true;
            }
        }

        //
        // now prepare to send it either blocking or non-blocking
        // depending on the call mode param
        // 
        if (block) {
            //
            // Get the request timeout
            //
            int t = down.policies().requestTimeout;

            //
            // now we can start sending off the messages
            // 
            while (true) {
                //
                // Get a message to send from the unsent queue
                //
                org.apache.yoko.orb.OCI.Buffer buf;
                Downcall nextDown;

                synchronized (this) {
                    if (!down.unsent()) {
                        break;
                    }

                    Assert._OB_assert(messageQueue_.hasUnsent());

                    buf = messageQueue_.getFirstUnsentBuffer();
                    nextDown = messageQueue_.moveFirstUnsentToPending();
                }

                //
                // Send the message
                //
                try {
                    synchronized (sendMutex_) {
                        if (t <= 0) {
                            //
                            // Send buffer, blocking
                            //
                            transport_.send(buf, true);
                            Assert._OB_assert(buf.is_full());
                        } else {
                            //
                            // Send buffer, with timeout
                            //
                            transport_.send_timeout(buf, t);

                            // 
                            // Timeout?
                            // 
                            if (!buf.is_full()) {
                                throw new org.omg.CORBA.NO_RESPONSE();
                            }
                        }
                    }
                } catch (org.omg.CORBA.SystemException ex) {
                    processException(State.Closed, ex, false);
                    return true;
                }

                //
                // a message should be sent by now so we have to
                // mark it as sent for the GIOPClient
                //
                if (!msgSentMarked && (nextDown != null)
                        && !nextDown.operation().equals("_locate")) {
                    msgSentMarked = true;
                    properties_ |= Property.RequestSent;
                }
            }
        } else // Non blocking
        {
            synchronized (this) {
                while (true) {
                    if (!down.unsent())
                        break;

                    Assert._OB_assert(messageQueue_.hasUnsent());

                    //
                    // get the first message to send
                    //
                    org.apache.yoko.orb.OCI.Buffer buf = messageQueue_
                            .getFirstUnsentBuffer();

                    //
                    // send this buffer, non-blocking
                    //
                    try {
                        synchronized (sendMutex_) {
                            transport_.send(buf, false);
                        }
                    } catch (org.omg.CORBA.SystemException ex) {
                        processException(State.Closed, ex, false);
                        return true;
                    }

                    //
                    // if the buffer isn't full, it hasn't been sent because
                    // the call would have blocked.
                    //
                    if (!buf.is_full())
                        return false;

                    //
                    // now move to the pending pile
                    //
                    Downcall dummy = messageQueue_.moveFirstUnsentToPending();

                    //
                    // update the message sent property
                    //
                    if (!msgSentMarked && dummy != null) {
                        if (dummy.responseExpected()
                                && dummy.operation().equals("_locate")) {
                            msgSentMarked = true;
                            properties_ |= Property.RequestSent;
                        }
                    }
                }
            }
        }

        logger.fine(" Request send completed with Downcall of type " + down.getClass().getName()); 
        return !down.responseExpected();
    }

    //
    // client-side receive method (from DowncallEmitter)
    //
    public boolean receive(Downcall down, boolean block) {
        logger.fine("Receiving response with Downcall of type " + down.getClass().getName() + " for operation " + down.operation() + " from transport " + transport_); 
        //
        // Try to receive the reply
        //
        try {
            boolean result = down.waitUntilCompleted(block);
            logger.fine("Completed eceiving response with Downcall of type " + down.getClass().getName()); 
            return result; 
        } catch (org.omg.CORBA.SystemException ex) {
            processException(State.Closed, ex, false);
            return true;
        }
    }

    //
    // client-side sendReceive (from DowncallEmitter)
    //
    public boolean sendReceive(Downcall down) {
        ACM_disableIdleMonitor();

        try {
            if (send(down, true)) {
                return true;
            }
            return receive(down, true);
        } finally {
            ACM_enableIdleMonitor();
        }
    }

    //
    // connection start (from GIOPConnection)
    //
    public void start() {
        //
        // unpause any paused threads
        //
        synchronized (holdingMonitor_) {
            if (holding_) {
                holding_ = false;
                holdingMonitor_.notifyAll();
            }
        }

        //
        // check if we need to add a receiver thread
        //
        if (transport_.mode() != org.apache.yoko.orb.OCI.SendReceiveMode.SendOnly) {
            try {
                synchronized (this) {
                    if (receiverThreads_.size() > 0) {
                        return;
                    }

                    addReceiverThread();
                }
            } catch (OutOfMemoryError ex) {
                synchronized (this) {
                    transport_.close();
                    state_ = State.Closed;
                    throw new org.omg.CORBA.IMP_LIMIT(org.apache.yoko.orb.OB.MinorCodes
                            .describeImpLimit(org.apache.yoko.orb.OB.MinorCodes.MinorThreadLimit),
                            org.apache.yoko.orb.OB.MinorCodes.MinorThreadLimit,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                }
            }
        }
    }

    //
    // connection refresh status (from GIOPConnection)
    //
    public void refresh() {
        boolean msgSentMarked = false;

        //
        // wake up any paused threads
        // 
        synchronized (holdingMonitor_) {
            if (holding_) {
                holding_ = false;
                holdingMonitor_.notifyAll();
            }
        }

        synchronized (this) {
            //
            // cleanup any defunct receiver threads now
            // 
            cleanupDeadReceiverThreads();

            //
            // if we can't write messages then don't bother to proceed
            //
            if ((enabledOps_ & AccessOp.Write) == 0)
                return;

            //
            // check if we've sent a message before while we are locked
            //
            if ((properties_ & Property.RequestSent) != 0)
                msgSentMarked = true;
        }

        //
        // another check if we can write or not
        // 
        if (transport_.mode() == org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly)
            return;

        //
        // now send off any queued messages
        // 
        while (true) {
            org.apache.yoko.orb.OCI.Buffer buf;
            Downcall dummy;

            try {
                synchronized (this) {
                    //
                    // stop when no messages left
                    // 
                    if (!messageQueue_.hasUnsent())
                        break;

                    buf = messageQueue_.getFirstUnsentBuffer();
                    buf.pos(0);
                    dummy = messageQueue_.moveFirstUnsentToPending();
                }

                //
                // make sure no two threads are sending at once
                // 
                synchronized (sendMutex_) {
                    transport_.send(buf, true);
                }

                //
                // check if the buffer is full
                // Some of the OCI plugins (bidir for example) will
                // simply return instead of throwing an exception if the
                // send fails
                //
                if (!buf.is_full())
                    throw new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSend),
                            org.apache.yoko.orb.OB.MinorCodes.MinorSend,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                //
                // mark the message sent flag
                //
                if (!msgSentMarked && (dummy != null)) {
                    if (dummy.responseExpected()
                            && dummy.operation().equals("_locate")) {
                        synchronized (this) {
                            msgSentMarked = true;
                            properties_ |= Property.RequestSent;
                        }
                    }
                }
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Closed, ex, false);
                return;
            }
        }
    }

    //
    // connection pause (from GIOPConnection)
    //
    public void pause() {
        synchronized (holdingMonitor_) {
            holding_ = true;
        }
    }

    //
    // enabled connection 'sides' (from GIOPConnection)
    //
    public void enableConnectionModes(boolean client, boolean server) {
        //
        // do nothing
        // 
    }
}

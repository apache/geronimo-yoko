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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OBPortableServer.POAManagerFactory;
import org.apache.yoko.orb.OBPortableServer.POAManager_impl;
import org.apache.yoko.io.Buffer;
import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.orb.OCI.SendReceiveMode;
import org.apache.yoko.orb.OCI.Transport;
import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.util.Assert;
import org.omg.CONV_FRAME.CodeSetContext;
import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.StringHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.SystemExceptionHelper;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UserException;
import org.omg.GIOP.KeyAddr;
import org.omg.GIOP.LocateStatusType_1_2;
import org.omg.GIOP.LocateStatusType_1_2Holder;
import org.omg.GIOP.ReplyStatusType_1_2;
import org.omg.GIOP.ReplyStatusType_1_2Holder;
import org.omg.GIOP.TargetAddressHolder;
import org.omg.IOP.CodeSets;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;
import org.omg.IOP.IORHolder;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.UnknownExceptionInfo;
import org.omg.PortableServer.POAManager;
import org.omg.SendingContext.CodeBase;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static org.apache.yoko.orb.OB.Connection.State.ACTIVE;
import static org.apache.yoko.orb.OB.Connection.State.CLOSED;
import static org.apache.yoko.orb.OB.Connection.State.CLOSING;
import static org.apache.yoko.orb.OB.Connection.State.ERROR;
import static org.apache.yoko.orb.OB.Connection.State.HOLDING;
import static org.apache.yoko.util.MinorCodes.MinorMessageError;
import static org.apache.yoko.util.MinorCodes.MinorNotSupportedByLocalObject;
import static org.apache.yoko.util.MinorCodes.MinorUnknownMessage;
import static org.apache.yoko.util.MinorCodes.MinorUnknownReplyMessage;
import static org.apache.yoko.util.MinorCodes.MinorUnknownReqId;
import static org.apache.yoko.util.MinorCodes.MinorWrongMessage;
import static org.apache.yoko.util.MinorCodes.describeCommFailure;
import static org.apache.yoko.util.MinorCodes.describeNoImplement;
import static org.apache.yoko.logging.VerboseLogging.CONN_IN_LOG;
import static org.apache.yoko.logging.VerboseLogging.CONN_LOG;
import static org.apache.yoko.logging.VerboseLogging.CONN_OUT_LOG;
import static org.apache.yoko.logging.VerboseLogging.REQ_IN_LOG;
import static org.apache.yoko.logging.VerboseLogging.REQ_OUT_LOG;
import static org.apache.yoko.logging.VerboseLogging.logged;
import static org.apache.yoko.logging.VerboseLogging.warned;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;
import static org.omg.GIOP.LocateStatusType_1_2.OBJECT_FORWARD;
import static org.omg.GIOP.LocateStatusType_1_2.OBJECT_FORWARD_PERM;
import static org.omg.GIOP.LocateStatusType_1_2._LOC_NEEDS_ADDRESSING_MODE;
import static org.omg.GIOP.LocateStatusType_1_2._LOC_SYSTEM_EXCEPTION;
import static org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD;
import static org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD_PERM;
import static org.omg.GIOP.LocateStatusType_1_2._OBJECT_HERE;
import static org.omg.GIOP.LocateStatusType_1_2._UNKNOWN_OBJECT;
import static org.omg.GIOP.MsgType_1_1.LocateReply;
import static org.omg.GIOP.MsgType_1_1.Reply;
import static org.omg.GIOP.MsgType_1_1._CancelRequest;
import static org.omg.GIOP.MsgType_1_1._CloseConnection;
import static org.omg.GIOP.MsgType_1_1._Fragment;
import static org.omg.GIOP.MsgType_1_1._LocateReply;
import static org.omg.GIOP.MsgType_1_1._LocateRequest;
import static org.omg.GIOP.MsgType_1_1._MessageError;
import static org.omg.GIOP.MsgType_1_1._Reply;
import static org.omg.GIOP.MsgType_1_1._Request;

abstract class GIOPConnection extends Connection implements DowncallEmitter, UpcallReturn {
    /** the next request id */
    private final AtomicInteger nextRequestId;

    /** the ORB instance this connection is bound with */
    ORBInstance orbInstance_ = null;

    /** transport this connection represents */
    Transport transport_ = null;

    /** Client parent (null if server-side only) */
    private final ConnectorInfo outboundConnectionKey;

    /** Object-adapter interface (null if client-side only) */
    private OAInterface oaInterface_ = null;

    /** storage space for unsent/pending messages */
    final MessageQueue messageQueue_ = new MessageQueue();

    /** number of upcalls in progress */
    int upcallsInProgress_ = 0;

    /** code converters used by the connection */
    private CodeConverters codeConverters_ = null;

    /** maximum GIOP version encountered during message transactions */
    final org.omg.GIOP.Version giopVersion_ = new org.omg.GIOP.Version((byte) 0, (byte) 0);

    /** ACM timeout variables */
    int shutdownTimeout_ = 2;

    private int idleTimeout_ = 0;

    /** timer used for ACM management */
    Timer acmTimer_ = null;

    private CodeBase serverRuntime_;

    TimerTask acmTask_ = null;

    // check if its compliant for this connection to send a
    // CloseConnection message to its peer
    synchronized boolean canSendCloseConnection() {
        // any GIOP versioned server can send a CloseConnection
        if (isServerEnabled()) return true;

        // anything >= GIOP 1.2 can send a CloseConnection
        if (giopVersion_.major > 1 || (giopVersion_.major == 1 && giopVersion_.minor >= 2)) return true;

        // otherwise we can't send it
        return false;
    }

    /** read the codeset information from the service contexts */
    private void readCodeConverters(ServiceContexts contexts) {
        if (codeConverters_ != null) return;
        ServiceContext csSC = contexts.get(CodeSets.value);
        if (csSC == null) return;
        CodeSetContext csCtx = CodeSetUtil.extractCodeSetContext(csSC);

        this.codeConverters_ = CodeConverters.create(orbInstance_, csCtx.char_data, csCtx.wchar_data);

        if (CONN_IN_LOG.isLoggable(Level.FINEST)) {
            String msg = String.format("receiving transmission code sets%nchar code set: %s%nwchar code set: %s",
                    CodeSetInfo.describe(csCtx.char_data),
                    CodeSetInfo.describe(csCtx.wchar_data));
            CONN_IN_LOG.finest(msg);
        }
    }

    /**
     * set the OAInterface used by BiDir clients to handle requests
     * @return true iff an OAInterface is found
     */
    private boolean setOAInterface(ProfileInfo pi) {
        // Release the old OAInterface
        oaInterface_ = null;

        // make sure we're allowed to do server processing as well as
        // being bidir enabled. A server's OAInterface should not
        // change whereas a bidir client would need to change regularly
        Assert.ensure(isOutbound());
        Assert.ensure(isServerEnabled());
        Assert.ensure(orbInstance_ != null);

        POAManagerFactory poamanFactory = orbInstance_.getPOAManagerFactory();
        Assert.ensure(poamanFactory != null);

        POAManager[] poaManagers = poamanFactory.list();

        for (POAManager poaManager : poaManagers) {
            try {
                POAManager_impl poamanImpl = (POAManager_impl) poaManager;

                OAInterface oaImpl = poamanImpl._OB_getOAInterface();

                IORHolder refIOR = new IORHolder();
                if (oaImpl.findByKey(pi.key, refIOR) == OAInterface.OBJECT_HERE) {
                    oaInterface_ = oaImpl;
                    return true;
                }
            } catch (ClassCastException ignore) {}
        }

        return false;
    }

    /** log the closing of this connection */
    private void logClose(boolean initiatedClosure) {
        if (!!!markClosingLogged()) return;
        final Logger conn_log = initiatedClosure ? CONN_OUT_LOG : CONN_IN_LOG;
        if (conn_log.isLoggable(FINE)) conn_log.fine("Closing connection: " + transport_);
    }

    /** main entry point into message processing - delegate to a specific methods */
    Upcall processMessage(GIOPIncomingMessage msg) {
        // update the version of GIOP found
        synchronized (this) {
            if (msg.version().major > giopVersion_.major) {
                giopVersion_.major = msg.version().major;
                giopVersion_.minor = msg.version().minor;
            } else if (msg.version().major == giopVersion_.major
                    && msg.version().minor > giopVersion_.minor) {
                giopVersion_.minor = msg.version().minor;
            }
        }

        // hand off message type processing
        switch (msg.type().value()) {
            case _Reply:
                processReply(msg);
                break;

            case _Request:
                return processRequest(msg);

            case _LocateRequest:
                processLocateRequest(msg);
                break;

            case _CancelRequest:
                break;

            case _LocateReply:
                processLocateReply(msg);
                break;

            case _CloseConnection:
                processCloseConnection(msg);
                break;

            case _MessageError:
                processMessageError(msg);
                break;

            case _Fragment:
                processFragment(msg);
                break;

            default:
                processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorUnknownMessage), MinorUnknownMessage, COMPLETED_MAYBE), false);
                break;
        }

        return null;
    }

    /** process a request message */
    private synchronized Upcall processRequest(GIOPIncomingMessage msg) {
        if (isServerEnabled() == false) {
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return null;
        }

        if (getState() != ACTIVE) return null;

        int reqId;
        BooleanHolder response = new BooleanHolder();
        StringHolder op = new StringHolder();
        ServiceContexts contexts = new ServiceContexts();
        TargetAddressHolder target = new TargetAddressHolder();

        try {
            reqId = msg.readRequestHeader(response, target, op, contexts);
            if (target.value.discriminator() != KeyAddr.value) {
                processException(ERROR, new NO_IMPLEMENT(describeNoImplement(MinorNotSupportedByLocalObject), MinorNotSupportedByLocalObject, COMPLETED_NO), false);
                return null;
            }
        } catch (SystemException ex) {
            processException(ERROR, ex, false);
            return null;
        }

        // Setup upcall data
        org.omg.GIOP.Version version = msg.version();

        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.major = version.major;
        profileInfo.minor = version.minor;
        profileInfo.key = target.value.object_key();

        InputStream in = msg.input();

        // We have some decision making to do here if BiDir is
        // enabled:
        // - If this is a client then make sure to properly
        // evaluate the message and obtain the correct OAInterface
        // to create the upcalls
        // - If this is a server then take the listen points from
        // the service contexts (if the POA has the BiDir policy enabled)
        // and store them in this' transport for connection reuse
        if (this.isOutbound()) {
            if (!!!setOAInterface(profileInfo)) {
                // we can't find an appropriate OAInterface in order
                // to direct the upcall so we must simply not handle
                // this request
                return null;
            }
        }

        // Parse the service contexts for various codeset info
        readCodeConverters(contexts);
        in._OB_codeConverters(codeConverters_, GiopVersion.get(version.major, version.minor));

        // read in the peer's sending context runtime object
        assignSendingContextRuntime(in, contexts);

        // New upcall will be started
        if (response.value) upcallsInProgress_++;

        orbInstance_.getLogger().fine("Processing request reqId=" + reqId + " op=" + op.value);

        return oaInterface_.createUpcall(
                response.value ? upcallReturnInterface() : null, profileInfo,
                transport_.get_info(), reqId, op.value, in, contexts);
    }

    /** process a reply message */
    private synchronized void processReply(GIOPIncomingMessage msg) {
        if (isClientEnabled() == false) {
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return;
        }

        int reqId;
        ReplyStatusType_1_2Holder status = new ReplyStatusType_1_2Holder();
        ServiceContexts contexts = new ServiceContexts();

        try {
            reqId = msg.readReplyHeader(status, contexts);
        } catch (SystemException ex) {
            processException(ERROR, ex, false);
            return;
        }

        Downcall down = messageQueue_.findAndRemovePending(reqId);
        if (down == null) {
            // Request id is unknown
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorUnknownReqId) + ": " + reqId, MinorUnknownReqId, COMPLETED_MAYBE), false);
            return;
        }

        down.setReplyContexts(contexts);
        InputStream in = msg.input();

        // read in the peer's sending context runtime object
        assignSendingContextRuntime(in, contexts);

        orbInstance_.getLogger().fine("Processing reply for reqId=" + reqId + " status=" + status.value.value());

        switch (status.value.value()) {
            case ReplyStatusType_1_2._NO_EXCEPTION:
                down.setNoException(in);
                break;

            case ReplyStatusType_1_2._USER_EXCEPTION:
                down.setUserException(in);
                break;

            case ReplyStatusType_1_2._SYSTEM_EXCEPTION: {
                try {
                    SystemException ex = Util.unmarshalSystemException(in);
                    ex = convertToUnknownExceptionIfAppropriate(ex, in, contexts);
                    down.setSystemException(ex);
                } catch (SystemException ex) {
                    processException(ERROR, ex, false);
                }

                break;
            }

            case ReplyStatusType_1_2._LOCATION_FORWARD: {
                try {
                    IOR ior = IORHelper.read(in);
                    down.setLocationForward(ior, false);
                } catch (SystemException ex) {
                    processException(ERROR, ex, false);
                }

                break;
            }

            case ReplyStatusType_1_2._LOCATION_FORWARD_PERM: {
                try {
                    IOR ior = IORHelper.read(in);
                    down.setLocationForward(ior, true);
                    break;
                } catch (SystemException ex) {
                    processException(ERROR, ex, false);
                }
            }

            case ReplyStatusType_1_2._NEEDS_ADDRESSING_MODE:
                // TODO: implement
                processException(ERROR, new NO_IMPLEMENT(describeNoImplement(MinorNotSupportedByLocalObject), MinorNotSupportedByLocalObject, COMPLETED_NO), false);
                break;

            default:
                processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorUnknownReplyMessage), MinorUnknownReplyMessage, COMPLETED_MAYBE), false);
                break;
        }
    }

    private SystemException convertToUnknownExceptionIfAppropriate(SystemException ex, InputStream is, ServiceContexts contexts) {
        if (!(ex instanceof UNKNOWN)) return ex;
        ServiceContext sc = contexts.get(UnknownExceptionInfo.value);
        if (sc == null) return ex;
        return new UnresolvedException((UNKNOWN) ex, sc.context_data, is);
    }

    private void assignSendingContextRuntime(InputStream in, ServiceContexts contexts) {
        if (serverRuntime_ == null) serverRuntime_ = Util.getSendingContextRuntime(orbInstance_, contexts);
        in.__setSendingContextRuntime(serverRuntime_);
    }

    /** process a LocateRequest message */
    private synchronized void processLocateRequest(GIOPIncomingMessage msg) {
        if (isServerEnabled() == false) {
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return;
        }
        Assert.ensure(getState() == ACTIVE);

        // Make sure the transport can send a reply
        if (transport_.mode() == SendReceiveMode.ReceiveOnly) {
            REQ_IN_LOG.warning("Discarding locate request - transport does not support two-way invocations: " + transport_);
            return;
        }

        try {
            int reqId;
            TargetAddressHolder target = new TargetAddressHolder();
            reqId = msg.readLocateRequestHeader(target);

            if (target.value.discriminator() != KeyAddr.value) {
                processException(ERROR, new NO_IMPLEMENT(describeNoImplement(MinorNotSupportedByLocalObject), MinorNotSupportedByLocalObject, COMPLETED_NO), false);
                return;
            }

            // Get the key
            byte[] key = target.value.object_key();

            // Find the IOR for the key
            IORHolder ior = new IORHolder();
            int val = oaInterface_.findByKey(key, ior);
            LocateStatusType_1_2 status = LocateStatusType_1_2.from_int(val);

            // Send back locate reply message
            try (OutputStream out = new OutputStream(Buffer.createWriteBuffer(12).padAll())) {
                ProfileInfo profileInfo = new ProfileInfo();
                profileInfo.major = msg.version().major;
                profileInfo.minor = msg.version().minor;
                GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out, profileInfo);
                outgoing.writeLocateReplyHeader(reqId, status);

                // If the reply status is OBJECT_FORWARD or OBJECT_FORWARD_PERM
                // the IOR is appended to the end of the LocateReply.
                if (status == OBJECT_FORWARD || status == OBJECT_FORWARD_PERM) IORHelper.write(out, ior.value);

                // TODO:
                // LOC_SYSTEM_EXCEPTION,
                // LOC_NEEDS_ADDRESSING_MODE
                int pos = out.getPosition();
                out.setPosition(0);
                outgoing.writeMessageHeader(LocateReply, false, pos - 12);
                out.setPosition(pos);

                // A locate request is treated just like an upcall
                upcallsInProgress_++;

                // Send the locate reply
                sendUpcallReply(out.getBufferReader());
            }
        } catch (SystemException ex) {
            processException(ERROR, ex, false);
        }
    }

    /** process a LocateReply message */
    private synchronized void processLocateReply(GIOPIncomingMessage msg) {
        if (isClientEnabled() == false) {
            processException(CLOSED, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return;
        }

        int reqId;
        LocateStatusType_1_2Holder status = new LocateStatusType_1_2Holder();

        try {
            reqId = msg.readLocateReplyHeader(status);
        } catch (SystemException ex) {
            processException(ERROR, ex, false);
            return;
        }

        Downcall down = messageQueue_.findAndRemovePending(reqId);
        if (down == null) {
            // Request id is unknown
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorUnknownReqId), MinorUnknownReqId, COMPLETED_MAYBE), false);
            return;
        }

        // Was this a LocateRequest?
        String op = down.operation();
        if (!op.equals("_locate")) {
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return;
        }

        InputStream in = msg.input();

        switch (status.value.value()) {
            case _UNKNOWN_OBJECT:
                down.setSystemException(new OBJECT_NOT_EXIST());
                break;

            case _OBJECT_HERE:
                down.setNoException(in);
                break;

            case _OBJECT_FORWARD:
                try {
                    IOR ior = IORHelper.read(in);
                    down.setLocationForward(ior, false);
                    if (REQ_OUT_LOG.isLoggable(FINE)) REQ_OUT_LOG.fine("Locate request forwarded to " + IORDump.PrintObjref(orbInstance_.getORB(), ior));

                } catch (SystemException ex) {
                    warned(REQ_OUT_LOG, ex, "An error occurred while reading a locate reply, possibly indicating "
                            + "an interoperability problem. You may need to set the LocateRequestPolicy to false.");
                    down.setSystemException(ex);
                    processException(ERROR, ex, false);
                }
                break;

            case _OBJECT_FORWARD_PERM:
                try {
                    IOR ior = IORHelper.read(in);
                    down.setLocationForward(ior, true);
                    if (REQ_OUT_LOG.isLoggable(FINE)) REQ_OUT_LOG.fine("Locate request forwarded to " + IORDump.PrintObjref(orbInstance_.getORB(), ior));
                } catch (SystemException ex) {
                    warned(REQ_OUT_LOG, ex,"An error occurred while reading a locate reply, possibly indicating "
                                    + "an interoperability problem. You may need to set the LocateRequestPolicy to false.");
                    down.setSystemException(ex);
                    processException(ERROR, ex, false);
                }

                break;

            case _LOC_SYSTEM_EXCEPTION:
                try {
                    SystemException ex = SystemExceptionHelper.read(in);
                    down.setSystemException(ex);
                } catch (SystemException ex) {
                    logged(REQ_IN_LOG, ex, "Could not read incoming system exception");
                    down.setSystemException(ex);
                    processException(ERROR, ex, false);
                }

                break;

            case _LOC_NEEDS_ADDRESSING_MODE:
                // TODO: implement
                processException(ERROR, new NO_IMPLEMENT(), false);
                break;
        }
    }

    /** process a CloseConnection message */
    private void processCloseConnection(GIOPIncomingMessage msg) {
        orbInstance_.getLogger().fine("Close connection request received from peer");
        if (isClientEnabled()) {
            // If the peer closes the connection, all outstanding
            // requests can safely be reissued. Thus we send all
            // of them a TRANSIENT exception with a completion
            // status of COMPLETED_NO. This is done by calling
            // exception() with the notCompleted parameter set to
            // true.
            processException(CLOSED, Transients.CLOSE_CONNECTION.create(), true);
        } else {
            setState(CLOSED);
        }
    }

    /** process a MessageError message */
    private void processMessageError(GIOPIncomingMessage msg) {
        processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorMessageError), MinorMessageError, COMPLETED_NO), false);
    }

    /** process a Fragment message */
    private void processFragment(GIOPIncomingMessage msg) {
        throw Assert.fail("At this point there should be no fragments, only complete messages.");
    }

    /** process a system exception */
    boolean processException(State newState, SystemException ex, boolean completed) {
        Assert.ensure(newState == ERROR || newState == CLOSED);
        logged(CONN_LOG, ex, "processing an exception, state=" + newState);

        if (setState(newState) == false) return false;

        synchronized (this) {
            orbInstance_.getOutboundConnectionCache().remove(outboundConnectionKey, this);
            // propagate any exceptions to the message queue
            messageQueue_.setException(ex, completed);
        }

        return true;
    }

    /** transmits a reply back once the upcall completes */
    private void sendUpcallReply(ReadBuffer readBuffer) {
        synchronized (this) {
            // no need to do anything if we are closed
            if (getState().isClosed()) return;

            // decrement the number of upcalls in progress
            Assert.ensure(upcallsInProgress_ > 0);
            upcallsInProgress_--;

            // add this message to the message Queue
            messageQueue_.add(orbInstance_, readBuffer);
        }

        refresh();

        // if that was the last upcall and we are in the closing state then shutdown now
        synchronized (this) {
            if (upcallsInProgress_ == 0 && getState() == CLOSING) gracefulShutdown();
        }
    }

    /** turn on ACM idle connection monitoring */
    synchronized void ACM_enableIdleMonitor() {
        if (idleTimeout_ > 0) {
            acmTimer_ = new Timer(true);
            acmTask_ = new TimerTask() {
                public void run() {
                    ACM_callback();
                }
            };

            acmTimer_.schedule(acmTask_, idleTimeout_ * 1000);
        }
    }

    /** turn off ACM idle connection monitoring */
    synchronized void ACM_disableIdleMonitor() {
        if (acmTimer_ != null) {
            acmTimer_.cancel();
            acmTimer_ = null;
        }

        if (acmTask_ != null) {
            acmTask_.cancel();
            acmTask_ = null;
        }
    }

    /** client-side constructor */
    GIOPConnection(ORBInstance orbInstance, Transport transport, GIOPClient client) {
        super(ACTIVE);
        // set member properties
        nextRequestId = new AtomicInteger(0xA);
        orbInstance_ = orbInstance;
        transport_ = transport;
        outboundConnectionKey = client.connectorInfo();
        markOutbound();
        markClientEnabled();

        // read ACM properties
        String value;
        Properties properties = orbInstance_.getProperties();

        // the shutdown timeout for the client
        value = properties.getProperty("yoko.orb.client_shutdown_timeout");
        if (value != null)
            shutdownTimeout_ = Integer.parseInt(value);

        // the idle timeout for the client
        value = properties.getProperty("yoko.orb.client_timeout");
        if (value != null)
            idleTimeout_ = Integer.parseInt(value);

        // Trace new outgoing connection
        if (CONN_OUT_LOG.isLoggable(FINE)) CONN_OUT_LOG.fine("new connection " + transport_);
    }

    /** server-side constructor */
    GIOPConnection(ORBInstance orbInstance, Transport transport, OAInterface oa) {
        super(HOLDING);
        // set members
        nextRequestId = new AtomicInteger(0xB);
        orbInstance_ = orbInstance;
        transport_ = transport;
        outboundConnectionKey = null;
        oaInterface_ = oa;
        markServerEnabled();

        // read ACM properties
        String value;
        Properties properties = orbInstance_.getProperties();

        // the shutdown timeout for the client
        value = properties.getProperty("yoko.orb.server_shutdown_timeout");
        if (value != null)
            shutdownTimeout_ = Integer.parseInt(value);

        // the idle timeout for the client
        value = properties.getProperty("yoko.orb.server_timeout");
        if (value != null)
            idleTimeout_ = Integer.parseInt(value);
    }

    /** @return the next request id to use */
    int getNewRequestId() {
        // In the case of BiDir connections, the client should use
        // even numbered requestIds and the server should use odd
        // numbered requestIds... the += 2 keeps this pattern intact
        // assuming it's correct at startup
        return nextRequestId.getAndAdd(2);
    }

    /** start populating the reply data */
    public void upcallBeginReply(Upcall upcall, ServiceContexts contexts) {
        upcall.createOutputStream(12);
        OutputStream out = upcall.output();
        ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out, profileInfo);

        int reqId = upcall.requestId();

        try {
            synchronized (this) {
                outgoing.writeReplyHeader(reqId, ReplyStatusType_1_2.NO_EXCEPTION, contexts);
            }
        } catch (SystemException ex) {
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            throw Assert.fail(ex);
        }
    }

    /** finished reply construction; ready its return */
    public void upcallEndReply(Upcall upcall) {
        // Make sure the transport can send a reply
        if (transport_.mode() == SendReceiveMode.ReceiveOnly) {
            REQ_IN_LOG.warning("Discarding reply - transport does not support two-way invocations: "
                    + "\noperation name: \"" + upcall.operation() + '"'
                    + "\n transport: " + transport_);

            return;
        }

        OutputStream out = upcall.output();
        ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int pos = out.getPosition();
        out.setPosition(0);
        try {
            outgoing.writeMessageHeader(Reply, false, pos - 12);
        } catch (SystemException ex) {
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            throw Assert.fail(ex);
        }

        sendUpcallReply(out.getBufferReader());
    }

    /** start populating the reply with a user exception */
    public void upcallBeginUserException(Upcall upcall, ServiceContexts contexts) {
        upcall.createOutputStream(12);

        OutputStream out = upcall.output();
        ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out, profileInfo);

        int reqId = upcall.requestId();

        try {
            outgoing.writeReplyHeader(reqId, ReplyStatusType_1_2.USER_EXCEPTION, contexts);
        } catch (SystemException ex) {
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            throw Assert.fail(ex);
        }
    }

    /** finished reply construction; ready its return */
    public void upcallEndUserException(Upcall upcall) {
        upcallEndReply(upcall);
    }

    /** populate and send the reply with a UserException */
    public void upcallUserException(Upcall upcall, UserException ex, ServiceContexts contexts) {
        upcall.createOutputStream(12);

        OutputStream out = upcall.output();
        ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int reqId = upcall.requestId();

        try {
            outgoing.writeReplyHeader(reqId, ReplyStatusType_1_2.USER_EXCEPTION, contexts);

            // Cannot marshal the exception without the Helper
            // ex._OB_marshal(out);
            throw Assert.fail(); // TODO: verify this logic
        } catch (SystemException e) {
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            throw Assert.fail(ex);
        }

        // TODO: this is currently unreachable - investigate and reinstate
        // upcallEndReply(upcall);
    }

    /** populate and end the reply with a system exception */
    public void upcallSystemException(Upcall upcall, SystemException ex, ServiceContexts contexts) {
        upcall.createOutputStream(12);

        OutputStream out = upcall.output();
        ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out, profileInfo);

        int reqId = upcall.requestId();
        try {
            // print this exception out here so applications have at stack trace to work
            // with for problem determination.

            orbInstance_.getLogger().log(FINE, "upcall exception", ex);
            outgoing.writeReplyHeader(reqId, ReplyStatusType_1_2.SYSTEM_EXCEPTION, contexts);
            Util.marshalSystemException(out, ex);
        } catch (SystemException e) {
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            throw Assert.fail(ex);
        }

        upcallEndReply(upcall);
    }

    /** prepare the reply for location forwarding */
    public void upcallForward(Upcall upcall, IOR ior, boolean perm, ServiceContexts contexts) {
        upcall.createOutputStream(12);

        OutputStream out = upcall.output();
        ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int reqId = upcall.requestId();
        ReplyStatusType_1_2 status = perm ? ReplyStatusType_1_2.LOCATION_FORWARD_PERM : ReplyStatusType_1_2.LOCATION_FORWARD;
        try {
            outgoing.writeReplyHeader(reqId, status, contexts);
            if (REQ_IN_LOG.isLoggable(FINE)) REQ_IN_LOG.fine("Sending forward reply to " + IORDump.PrintObjref(orbInstance_.getORB(), ior));
            IORHelper.write(out, ior);
        } catch (SystemException ex) {
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            throw Assert.fail(ex);
        }

        upcallEndReply(upcall);
    }

    /** enable this connection for processing as a client */
    synchronized public void activateClientSide() {
        markClientEnabled();
    }

    /** enable this connection for processing as a server */
    synchronized public void activateServerSide() {
        Assert.ensure(this.isOutbound());
        markServerEnabled();
    }

    /** @return a reference to the DowncallEmitter interface */
    public DowncallEmitter emitterInterface() {
        Assert.ensure(isClientEnabled());
        return this;
    }

    /** @return a reference to the UpcallReturn interface */
    private UpcallReturn upcallReturnInterface() {
        Assert.ensure(isServerEnabled());
        return this;
    }

    /** return the transport we represent */
    Transport transport() { return transport_; }

    /** check if a reply has been sent yet */
    public boolean replySent() { return isReplySent(); }

    /** check if this connection was already destroyed */
    synchronized boolean destroyed() { return isDestroyed(); }

    /** destroy this connection */
    void destroy() { setState(CLOSING); }

    void close() {
        logClose(true);
        transport_.close();
    }
}

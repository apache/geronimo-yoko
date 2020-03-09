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
import org.apache.yoko.orb.OCI.Buffer;
import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.ReadBuffer;
import org.apache.yoko.orb.OCI.SendReceiveMode;
import org.apache.yoko.orb.OCI.Transport;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.omg.CONV_FRAME.CodeSetContext;
import org.omg.CONV_FRAME.CodeSetContextHolder;
import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.StringHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.SystemExceptionHelper;
import org.omg.CORBA.TRANSIENT;
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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.unmodifiableSet;
import static org.apache.yoko.orb.OB.Assert.ensure;
import static org.apache.yoko.orb.OB.CodeSetDatabase.getConverter;
import static org.apache.yoko.orb.OB.GIOPConnection.Access.CLOSE;
import static org.apache.yoko.orb.OB.GIOPConnection.Access.READ;
import static org.apache.yoko.orb.OB.GIOPConnection.Access.WRITE;
import static org.apache.yoko.orb.OB.GIOPConnection.ConnState.ACTIVE;
import static org.apache.yoko.orb.OB.GIOPConnection.ConnState.CLOSED;
import static org.apache.yoko.orb.OB.GIOPConnection.ConnState.CLOSING;
import static org.apache.yoko.orb.OB.GIOPConnection.ConnState.ERROR;
import static org.apache.yoko.orb.OB.GIOPConnection.ConnState.HOLDING;
import static org.apache.yoko.orb.OB.MinorCodes.MinorCloseConnection;
import static org.apache.yoko.orb.OB.MinorCodes.MinorMessageError;
import static org.apache.yoko.orb.OB.MinorCodes.MinorNotSupportedByLocalObject;
import static org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage;
import static org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReplyMessage;
import static org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReqId;
import static org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage;
import static org.apache.yoko.orb.OB.MinorCodes.describeCommFailure;
import static org.apache.yoko.orb.OB.MinorCodes.describeNoImplement;
import static org.apache.yoko.orb.OB.MinorCodes.describeTransient;
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

abstract public class GIOPConnection implements DowncallEmitter, UpcallReturn {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GIOPConnection.class.getName());

    enum Access { READ, WRITE, CLOSE }

    public static final class Property {
        public static final int RequestSent = 1;
        public static final int ReplySent = 2;
        public static final int Destroyed = 4;
        public static final int CreatedByClient = 8;
        public static final int ClientEnabled = 16;
        public static final int ServerEnabled = 32;
        public static final int ClosingLogged = 64;
    }

    enum ConnState {
        ACTIVE(READ, WRITE),
        HOLDING(WRITE),
        CLOSING(READ, WRITE, CLOSE),
        ERROR(CLOSE),
        CLOSED();

        private final Set<Access> permissions;

        ConnState() { this.permissions = Collections.EMPTY_SET; }

        ConnState(Access...permissions) {
            // EnumSet.of() requires an initial element, but it's ok to add the element twice
            this.permissions = unmodifiableSet(EnumSet.of(permissions[0], permissions));
        }

        boolean cannotTransitionTo(ConnState next) {
            if (this == next) return true;
            if (this == HOLDING) return false;
            return this.compareTo(next) > 0;
        }

        boolean forbids(Access op) { return !!!permissions.contains(op); }
    }

    /* task to execute when ACM timer signal arrives */
    final class ACMTask extends TimerTask {
        GIOPConnection connection_;

        public ACMTask(GIOPConnection parent) {
            connection_ = parent;
        }

        public void run() {
            connection_.ACM_callback();
            connection_ = null;
        }
    }

    /** the next request id */
    private final AtomicInteger nextRequestId;

    /** the ORB instance this connection is bound with */
    protected ORBInstance orbInstance_ = null;

    /** transport this connection represents */
    protected Transport transport_ = null;

    /** Client parent (null if server-side only) */
    private final ConnectorInfo outboundConnectionKey;

    /** Object-adapter interface (null if client-side only) */
    private OAInterface oaInterface_ = null;

    /** storage space for unsent/pending messages */
    protected final MessageQueue messageQueue_ = new MessageQueue();

    /** enabled connection property flags */
    protected int properties_ = 0;

    /** state of this connection */
    protected ConnState connState = HOLDING;

    /** number of upcalls in progress */
    protected int upcallsInProgress_ = 0;

    /** code converters used by the connection */
    private CodeConverters codeConverters_ = null;

    /** maximum GIOP version encountered during message transactions */
    protected final org.omg.GIOP.Version giopVersion_ = new org.omg.GIOP.Version((byte) 0, (byte) 0);

    /** ACM timeout variables */
    protected int shutdownTimeout_ = 2;

    private int idleTimeout_ = 0;

    /** timer used for ACM management */
    protected Timer acmTimer_ = null;

    private CodeBase serverRuntime_;

    protected ACMTask acmTask_ = null;

    // check if its compliant for this connection to send a
    // CloseConnection message to its peer
    synchronized protected boolean canSendCloseConnection() {
        // any GIOP versioned server can send a CloseConnection
        if ((properties_ & Property.ServerEnabled) != 0) return true;

        // anything >= GIOP 1.2 can send a CloseConnection
        if (giopVersion_.major > 1 || (giopVersion_.major == 1 && giopVersion_.minor >= 2)) return true;

        // otherwise we can't send it
        return false;
    }

    /** read the codeset information from the service contexts */
    private void readCodeConverters(ServiceContexts contexts) {
        if (codeConverters_ != null) return;
        ServiceContext cssc = contexts.get(CodeSets.value);
        if (cssc == null) return;
        CodeSetContextHolder codeSetContextH = new CodeSetContextHolder();
        CodeSetUtil.extractCodeSetContext(cssc, codeSetContextH);
        CodeSetContext codeSetContext = codeSetContextH.value;

        final int nativeCs = orbInstance_.getNativeCs();
        final int alienCs = codeSetContext.char_data;
        final int nativeWcs = orbInstance_.getNativeWcs();
        final int alienWcs = codeSetContext.wchar_data;
        final CodeConverterBase inputCharConverter = getConverter(nativeCs, alienCs);
        final CodeConverterBase outputCharConverter = getConverter(alienCs, nativeCs);
        final CodeConverterBase inputWcharConverter = getConverter(nativeWcs, alienWcs);
        final CodeConverterBase outputWcharConverter = getConverter(alienWcs, nativeWcs);
        codeConverters_ = new CodeConverters(inputCharConverter, outputWcharConverter, inputWcharConverter, outputWcharConverter);

        CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
        if (coreTraceLevels.traceConnections() >= 2) {
            String msg = "receiving transmission code sets";
            msg += "\nchar code set: ";
            if (codeConverters_.inputCharConverter != null) {
                msg += codeConverters_.inputCharConverter.getSourceCodeSet().description;
            } else {
                if (alienCs == 0) {
                    msg += "none";
                } else {
                    CodeSetInfo info = CodeSetInfo.forRegistryId(nativeCs);
                    msg += info != null ? info.description : null;
                }
            }
            msg += "\nwchar code set: ";
            if (codeConverters_.inputWcharConverter != null) {
                msg += codeConverters_.inputWcharConverter.getSourceCodeSet().description;
            } else {
                if (alienWcs == 0) {
                    msg += "none";
                } else {
                    CodeSetInfo info = CodeSetInfo.forRegistryId(nativeWcs);
                    msg += info != null ? info.description : null;
                }
            }

            orbInstance_.getLogger().trace("incoming", msg);
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
        Assert.ensure((properties_ & Property.CreatedByClient) != 0);
        Assert.ensure((properties_ & Property.ServerEnabled) != 0);
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
    private synchronized void logClose(boolean initiatedClosure) {
        if ((properties_ & Property.ClosingLogged) != 0)
            return;

        properties_ |= Property.ClosingLogged;

        CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
        if (coreTraceLevels.traceConnections() > 0) {
            TransportInfo info = transport_.get_info();
            String msg = "closing connection\n";
            msg += info.describe();

            orbInstance_.getLogger().trace(initiatedClosure ? "outgoing" : "incoming", msg);
        }
    }

    /** main entry point into message processing - delegate to a specific methods */
    protected Upcall processMessage(GIOPIncomingMessage msg) {
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
        if ((properties_ & Property.ServerEnabled) == 0) {
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return null;
        }

        if (connState != ACTIVE) return null;

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
        if ((properties_ & Property.CreatedByClient) != 0) {
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

        orbInstance_.getLogger().debug("Processing request reqId=" + reqId + " op=" + op.value);

        return oaInterface_.createUpcall(
                response.value ? upcallReturnInterface() : null, profileInfo,
                transport_.get_info(), reqId, op.value, in, contexts);
    }

    /** process a reply message */
    private synchronized void processReply(GIOPIncomingMessage msg) {
        if ((properties_ & Property.ClientEnabled) == 0) {
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
            //
            // Request id is unknown
            //
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorUnknownReqId) + ": " + reqId, MinorUnknownReqId, COMPLETED_MAYBE), false);
            return;
        }

        down.setReplyContexts(contexts);
        InputStream in = msg.input();

        // read in the peer's sending context runtime object
        assignSendingContextRuntime(in, contexts);

        orbInstance_.getLogger().debug("Processing reply for reqId=" + reqId + " status=" + status.value.value());

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
                //
                // TODO: implement
                //
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
        if ((properties_ & Property.ServerEnabled) == 0) {
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return;
        }
        Assert.ensure(connState == ACTIVE);

        //
        // Make sure the transport can send a reply
        //
        if (transport_.mode() == SendReceiveMode.ReceiveOnly) {
            String message = "Discarding locate request - transport "
                    + "does not support twoway invocations";

            TransportInfo transportInfo = transport_
                    .get_info();
            if (transportInfo != null) {
                String desc = transportInfo.describe();
                message += '\n';
                message += desc;
            } else {
                message += "\nCollocated method call";
            }

            Logger logger = orbInstance_.getLogger();
            logger.warning(message);

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

            //
            // Get the key
            //
            byte[] key = target.value.object_key();

            //
            // Find the IOR for the key
            //
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

                //
                // TODO:
                // LOC_SYSTEM_EXCEPTION,
                // LOC_NEEDS_ADDRESSING_MODE
                //
                int pos = out.getPosition();
                out.setPosition(0);
                outgoing.writeMessageHeader(LocateReply, false, pos - 12);
                out.setPosition(pos);

                //
                // A locate request is treated just like an upcall
                //
                upcallsInProgress_++;

                //
                // Send the locate reply
                //
                sendUpcallReply(out.getBufferReader());
            }
        } catch (SystemException ex) {
            processException(ERROR, ex, false);
        }
    }

    /** process a LocateReply message */
    private synchronized void processLocateReply(GIOPIncomingMessage msg) {
        if ((properties_ & Property.ClientEnabled) == 0) {
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
            //
            // Request id is unknown
            //
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorUnknownReqId), MinorUnknownReqId, COMPLETED_MAYBE), false);
            return;
        }

        //
        // Was this a LocateRequest?
        //
        String op = down.operation();
        if (!op.equals("_locate")) {
            processException(ERROR, new COMM_FAILURE(describeCommFailure(MinorWrongMessage), MinorWrongMessage, COMPLETED_MAYBE), false);
            return;
        }

        InputStream in = msg.input();
        Logger logger = orbInstance_.getLogger();

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
                    if (logger.isDebugEnabled()) {
                        logger.debug("Locate request forwarded to " + IORDump.PrintObjref(orbInstance_.getORB(), ior));
                    }
                } catch (SystemException ex) {
                    logger.warning("An error occurred while reading a "
                                    + "locate reply, possibly indicating\n"
                                    + "an interoperability problem. You may "
                                    + "need to set the LocateRequestPolicy\n"
                                    + "to false.");
                    down.setSystemException(ex);
                    processException(ERROR, ex, false);
                }
                break;

            case _OBJECT_FORWARD_PERM:
                try {
                    IOR ior = IORHelper.read(in);
                    down.setLocationForward(ior, true);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Locate request forwarded to " + IORDump.PrintObjref(orbInstance_.getORB(), ior));
                    }
                } catch (SystemException ex) {
                    logger.warning("An error occurred while reading a "
                                    + "locate reply, possibly indicating\n"
                                    + "an interoperability problem. You may "
                                    + "need to set the LocateRequestPolicy\n"
                                    + "to false.");
                    down.setSystemException(ex);
                    processException(ERROR, ex, false);
                }

                break;

            case _LOC_SYSTEM_EXCEPTION:
                try {
                    SystemException ex = SystemExceptionHelper.read(in);
                    down.setSystemException(ex);
                } catch (SystemException ex) {
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
        orbInstance_.getLogger().debug("Close connection request received from peer");
        if ((properties_ & Property.ClientEnabled) != 0) {
            //
            // If the peer closes the connection, all outstanding
            // requests can safely be reissued. Thus we send all
            // of them a TRANSIENT exception with a completion
            // status of COMPLETED_NO. This is done by calling
            // exception() with the notCompleted parameter set to
            // true.
            //
            processException(CLOSED, new TRANSIENT(describeTransient(MinorCloseConnection), MinorCloseConnection, COMPLETED_NO), true);
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
    protected boolean processException(ConnState state, SystemException ex, boolean completed) {
        Assert.ensure(state == ERROR || state == CLOSED);

        orbInstance_.getLogger().debug("processing an exception, state=" + state, ex);

        synchronized (this) {
            // Don't do anything if there is no state change and it is
            // not possible to transition backwards.
            if (connState.cannotTransitionTo(state)) return false;

            connState = state;

            orbInstance_.getOutboundConnectionCache().remove(outboundConnectionKey, this);

            // propagate any exceptions to the message queue
            messageQueue_.setException(ex, completed);
        }

        // apply the shutdown
        switch (state) {
        case ERROR:
            abortiveShutdown();
            break;
        case CLOSED:
            logClose(true);
            transport_.close();
            break;
        }

        // set 'this' properties
        synchronized (this) {
            properties_ |= Property.Destroyed;
        }

        //
        // update the connection status
        //
        refresh();
        return true;
    }

    /** transmits a reply back once the upcall completes */
    private void sendUpcallReply(ReadBuffer readBuffer) {
        synchronized (this) {
            //
            // no need to do anything if we are closed
            //
            if (connState == CLOSED)
                return;

            //
            // decrement the number of upcalls in progress
            //
            Assert.ensure(upcallsInProgress_ > 0);
            upcallsInProgress_--;

            //
            // add this message to the message Queue
            //
            messageQueue_.add(orbInstance_, readBuffer);
        }

        //
        // refresh the connection status
        //
        refresh();

        //
        // if that was the last upcall and we are in the closing state
        // then shutdown now
        //
        synchronized (this) {
            if (upcallsInProgress_ == 0 && connState == CLOSING)
                gracefulShutdown();
        }
    }

    /** shutdown the connection forcefully and immediately */
    abstract protected void abortiveShutdown();

    /** shutdown the connection gracefully */
    abstract protected void gracefulShutdown();

    /** turn on ACM idle connection monitoring */
    synchronized protected void ACM_enableIdleMonitor() {
        if (idleTimeout_ > 0) {
            acmTimer_ = new Timer(true);
            acmTask_ = new ACMTask(this);

            acmTimer_.schedule(acmTask_, idleTimeout_ * 1000);
        }
    }

    /** turn off ACM idle connection monitoring */
    synchronized protected void ACM_disableIdleMonitor() {
        if (acmTimer_ != null) {
            acmTimer_.cancel();
            acmTimer_ = null;
        }

        if (acmTask_ != null) {
            acmTask_.cancel();
            acmTask_ = null;
        }
    }

    // ----------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------

    /** client-side constructor */
    public GIOPConnection(ORBInstance orbInstance, Transport transport, GIOPClient client) {
        // set member properties
        nextRequestId = new AtomicInteger(0xA);
        orbInstance_ = orbInstance;
        transport_ = transport;
        outboundConnectionKey = client.connectorInfo();
        connState = ACTIVE;
        properties_ = Property.CreatedByClient | Property.ClientEnabled;

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
        CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
        if (coreTraceLevels.traceConnections() > 0) {
            TransportInfo info = transport_.get_info();
            String msg = "new connection\n";
            msg += info.describe();
            orbInstance_.getLogger().trace("client-side", msg);
        }
    }

    /** server-side constructor */
    public GIOPConnection(ORBInstance orbInstance, Transport transport, OAInterface oa) {
        //
        // set members
        //
        nextRequestId = new AtomicInteger(0xB);
        orbInstance_ = orbInstance;
        transport_ = transport;
        outboundConnectionKey = null;
        oaInterface_ = oa;
        properties_ = Property.ServerEnabled;

        //
        // read ACM properties
        //
        String value;
        Properties properties = orbInstance_.getProperties();

        //
        // the shutdown timeout for the client
        //
        value = properties.getProperty("yoko.orb.server_shutdown_timeout");
        if (value != null)
            shutdownTimeout_ = Integer.parseInt(value);

        //
        // the idle timeout for the client
        //
        value = properties.getProperty("yoko.orb.server_timeout");
        if (value != null)
            idleTimeout_ = Integer.parseInt(value);
    }

    /** @return true iff this connection was initiated by the other party */
    private boolean isInbound() {
        return (properties_ & Property.CreatedByClient) == 0;
    }

    /** @return true iff this connection was initiated by this party */
    public final boolean isOutbound() {
        return !!! isInbound();
    }

    /** @return the next request id to use */
    public int getNewRequestId() {
        // In the case of BiDir connections, the client should use
        // even numbered requestIds and the server should use odd
        // numbered requestIds... the += 2 keeps this pattern intact
        // assuming its correct at startup
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
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            throw Assert.fail(ex);
        }
    }

    /** finished reply construction; ready its return */
    public void upcallEndReply(Upcall upcall) {
        //
        // Make sure the transport can send a reply
        //
        if (transport_.mode() == SendReceiveMode.ReceiveOnly) {
            String msg = "Discarding reply - transport does not "
                    + "support twoway invocations";

            msg += "\noperation name: \"";
            msg += upcall.operation();
            msg += '"';

            TransportInfo transportInfo = transport_
                    .get_info();
            if (transportInfo != null) {
                String desc = transportInfo.describe();
                msg += '\n';
                msg += desc;
            } else {
                msg += "\nCollocated method call";
            }

            Logger logger = orbInstance_.getLogger();
            logger.warning(msg);

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
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
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
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
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

            //
            // Cannot marshal the exception without the Helper
            //
            // ex._OB_marshal(out);
            throw Assert.fail(); // TODO: verify this logic
        } catch (SystemException e) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
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

            orbInstance_.getLogger().debug("upcall exception", ex);
            outgoing.writeReplyHeader(reqId, ReplyStatusType_1_2.SYSTEM_EXCEPTION, contexts);
            Util.marshalSystemException(out, ex);
        } catch (SystemException e) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
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
            Logger logger = orbInstance_.getLogger();

            if (logger.isDebugEnabled()) {
                logger.debug("Sending forward reply to " + IORDump.PrintObjref(orbInstance_.getORB(), ior));
            }

            IORHelper.write(out, ior);
        } catch (SystemException ex) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            throw Assert.fail(ex);
        }

        upcallEndReply(upcall);
    }

    /** enable this connection for processing as a client */
    synchronized public void activateClientSide() {
        properties_ |= Property.ClientEnabled;
        enableConnectionModes(true, true);
    }

    /** enable this connection for processing as a server */
    synchronized public void activateServerSide() {
        Assert.ensure((properties_ & Property.CreatedByClient) != 0);

        if ((properties_ & Property.ServerEnabled) == 0) {
            properties_ |= Property.ServerEnabled;
            enableConnectionModes(true, true);
        }
    }

    /** @return a reference to the DowncallEmitter interface */
    public DowncallEmitter emitterInterface() {
        Assert.ensure((properties_ & Property.ClientEnabled) != 0);
        return this;
    }

    /** @return a reference to the UpcallReturn interface */
    private UpcallReturn upcallReturnInterface() {
        Assert.ensure((properties_ & Property.ServerEnabled) != 0);
        return this;
    }

    /** return the transport we represent */
    public Transport transport() {
        return transport_;
    }

    /** check if a request has been sent yet */
    synchronized public boolean requestSent() {
        return (properties_ & Property.RequestSent) != 0;
    }

    /** check if a reply has been sent yet */
    synchronized public boolean replySent() {
        return (properties_ & Property.ReplySent) != 0;
    }

    /** check if this connection was already destroyed */
    synchronized public boolean destroyed() {
        return (properties_ & Property.Destroyed) != 0;
    }

    /** change the state of this connection */
    public void setState(ConnState newState) {
        synchronized (this) {
            if (connState.cannotTransitionTo(newState)) {
                logger.fine("No state change from " + connState + " to "  + newState);
                return;
            }

            // make sure to update the state since some of the actions will key off this new state
            connState = newState;
        }

        switch (newState) {
        case ACTIVE:
            // start and refresh the connection
            start();
            refresh();
            break;

        case HOLDING:
            // pause the connection
            pause();
            break;

        case CLOSING:
            // gracefully shutdown by sending off pending messages,
            // reading any messages left on the wire and then closing
            gracefulShutdown();
            // refresh this status
            refresh();
            break;

        case ERROR:
            // there is an error so shutdown abortively
            abortiveShutdown();
            // mark the connection as destroyed now
            synchronized (this) { properties_ |= Property.Destroyed; }
            // refresh the connection status
            refresh();
            break;

        case CLOSED:
            logClose(true);
            transport_.close();
            // mark the connection as destroyed
            synchronized (this) { properties_ |= Property.Destroyed; }
            // and refresh the connection
            refresh();
            break;

        default:
            throw Assert.fail();
        }
    }

    /** destroy this connection */
    public void destroy() {
        setState(CLOSING);
    }

    /** callback method when the ACM signals a timeout */
    abstract public void ACM_callback();

    /** activate the connection */
    abstract public void start();

    /** refresh the connection status after a change in internal state */
    abstract public void refresh();

    /** tell the connection to stop processing; resumable with a refresh() */
    abstract public void pause();

    /** change the connection mode to [client, server, both] */
    abstract public void enableConnectionModes(boolean enableClient, boolean enableServer);
}

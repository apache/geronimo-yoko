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
import org.apache.yoko.orb.OB.Logger;
import org.omg.IOP.ServiceContext;
import org.omg.SendingContext.CodeBase;

abstract public class GIOPConnection implements DowncallEmitter, UpcallReturn {
    // ----------------------------------------------------------------
    // Inner classes
    // ----------------------------------------------------------------

    //
    // access operations class
    //
    public static final class AccessOp {
        public static final int Nil = 0;

        public static final int Read = 1;

        public static final int Write = 2;

        public static final int Close = 4;

        public static final int All = 7;
    }

    //
    // connection properties
    //
    public static final class Property {
        public static final int RequestSent = 1;

        public static final int ReplySent = 2;

        public static final int Destroyed = 4;

        public static final int CreatedByClient = 8;

        public static final int ClientEnabled = 16;

        public static final int ServerEnabled = 32;

        public static final int ClosingLogged = 64;
    }

    //
    // connection states
    //
    public static final class State {
        public static final int Active = 1;

        public static final int Holding = 2;

        public static final int Closing = 3;

        public static final int Error = 4;

        public static final int Closed = 5;
    }

    //
    // task to execute when ACM timer signal arrives
    //
    final class ACMTask extends java.util.TimerTask {
        GIOPConnection connection_;

        public ACMTask(GIOPConnection parent) {
            connection_ = parent;
        }

        public void run() {
            //
            // execute the callback method
            //
            connection_.ACM_callback();

            //
            // break cyclic dependency
            //
            connection_ = null;
        }
    }

    // ----------------------------------------------------------------
    // Member data
    // ----------------------------------------------------------------

    //
    // the ORB instance this connection is bound with
    //
    protected ORBInstance orbInstance_ = null;

    //
    // transport this connection represents
    //
    protected org.apache.yoko.orb.OCI.Transport transport_ = null;

    //
    // Client parent (null if server-side only)
    //
    protected GIOPClient client_ = null;

    //
    // Object-adapter interface (null if client-side only)
    //
    protected OAInterface oaInterface_ = null;

    //
    // storage space for unsent/pending messages
    //
    protected MessageQueue messageQueue_ = new MessageQueue();

    //
    // enabled processing operations
    //
    protected int enabledOps_ = AccessOp.Nil;

    //
    // enabled connection property flags
    //
    protected int properties_ = 0;

    //
    // state of this connection
    //
    protected int state_ = State.Holding;

    //
    // number of upcalls in progress
    //
    protected int upcallsInProgress_ = 0;

    //
    // code converters used by the connection
    //
    protected CodeConverters codeConverters_ = null;

    //
    // maximum GIOP version encountered during message transactions
    //
    protected org.omg.GIOP.Version giopVersion_ = new org.omg.GIOP.Version(
            (byte) 0, (byte) 0);

    //
    // ACM timeout variables
    //
    protected int shutdownTimeout_ = 2;

    protected int idleTimeout_ = 0;

    //
    // timer used for ACM management
    //
    protected java.util.Timer acmTimer_ = null;

	private CodeBase serverRuntime_;

    protected ACMTask acmTask_ = null;

    // ----------------------------------------------------------------
    // Protected methods
    // ----------------------------------------------------------------

    //
    // check if its compliant for this connection to send a
    // CloseConnection message to its peer
    //
    synchronized protected boolean canSendCloseConnection() {
        //
        // any GIOP versioned server can send a CloseConnection
        //
        if ((properties_ & Property.ServerEnabled) != 0)
            return true;

        //
        // anything >= than GIOP 1.2 can send a CloseConnection
        //
        if (giopVersion_.major > 1
                || (giopVersion_.major == 1 && giopVersion_.minor >= 2))
            return true;

        //
        // otherwise we can't send it
        //
        return false;
    }

    //
    // read the codeset information from the SCL
    //
    protected void readCodeConverters(org.omg.IOP.ServiceContext[] scl) {
        if (codeConverters_ != null)
            return;

        for (int i = 0; i < scl.length; i++) {
            if (scl[i].context_id == org.omg.IOP.CodeSets.value) {
                org.omg.CONV_FRAME.CodeSetContextHolder codeSetContextH = new org.omg.CONV_FRAME.CodeSetContextHolder();
                CodeSetUtil.extractCodeSetContext(scl[i], codeSetContextH);
                org.omg.CONV_FRAME.CodeSetContext codeSetContext = codeSetContextH.value;

                CodeSetDatabase db = CodeSetDatabase.instance();

                codeConverters_ = new CodeConverters();
                codeConverters_.inputCharConverter = db.getConverter(
                        orbInstance_.getNativeCs(), codeSetContext.char_data);
                codeConverters_.outputCharConverter = db.getConverter(
                        codeSetContext.char_data, orbInstance_.getNativeCs());
                codeConverters_.inputWcharConverter = db.getConverter(
                        orbInstance_.getNativeWcs(), codeSetContext.wchar_data);
                codeConverters_.outputWcharConverter = db.getConverter(
                        codeSetContext.wchar_data, orbInstance_.getNativeWcs());

                CoreTraceLevels coreTraceLevels = orbInstance_
                        .getCoreTraceLevels();
                if (coreTraceLevels.traceConnections() >= 2) {
                    String msg = "receiving transmission code sets";
                    msg += "\nchar code set: ";
                    if (codeConverters_.inputCharConverter != null)
                        msg += codeConverters_.inputCharConverter.getFrom().description;
                    else {
                        if (codeSetContext.char_data == 0)
                            msg += "none";
                        else {
                            CodeSetInfo info = db.getCodeSetInfo(orbInstance_
                                    .getNativeCs());
                            msg += info.description;
                        }
                    }
                    msg += "\nwchar code set: ";
                    if (codeConverters_.inputWcharConverter != null)
                        msg += codeConverters_.inputWcharConverter.getFrom().description;
                    else {
                        if (codeSetContext.wchar_data == 0)
                            msg += "none";
                        else {
                            CodeSetInfo info = db.getCodeSetInfo(orbInstance_
                                    .getNativeWcs());
                            msg += info.description;
                        }
                    }

                    orbInstance_.getLogger().trace("incoming", msg);
                }
                break;
            }
        }
    }

    //
    // set the OAInterface used by BiDir clients to handle requests
    // Returns true if an OAInterface is found; false otherwise
    //
    protected boolean setOAInterface(org.apache.yoko.orb.OCI.ProfileInfo pi) {
        //
        // Release the old OAInterface
        //
        oaInterface_ = null;

        //
        // make sure we're allowed to do server processing as well as
        // being bidir enabled. A server's OAInterface should not
        // change whereas a bidir client would need to change regularly
        //
        Assert._OB_assert((properties_ & Property.CreatedByClient) != 0);
        Assert._OB_assert((properties_ & Property.ServerEnabled) != 0);
        Assert._OB_assert(orbInstance_ != null);

        org.apache.yoko.orb.OBPortableServer.POAManagerFactory poamanFactory = orbInstance_
                .getPOAManagerFactory();
        Assert._OB_assert(poamanFactory != null);

        org.omg.PortableServer.POAManager[] poaManagers = poamanFactory.list();

        for (int i = 0; i < poaManagers.length; i++) {
            try {
                org.apache.yoko.orb.OBPortableServer.POAManager_impl poamanImpl = (org.apache.yoko.orb.OBPortableServer.POAManager_impl) poaManagers[i];

                org.apache.yoko.orb.OB.OAInterface oaImpl = poamanImpl
                        ._OB_getOAInterface();

                org.omg.IOP.IORHolder refIOR = new org.omg.IOP.IORHolder();
                if (oaImpl.findByKey(pi.key, refIOR) == org.apache.yoko.orb.OB.OAInterface.OBJECT_HERE) {
                    oaInterface_ = oaImpl;
                    return true;
                }
            } catch (java.lang.ClassCastException ex) {
                continue;
            }
        }

        return false;
    }

    //
    // log the closing of this connection
    //
    synchronized protected void logClose(boolean initiatedClosure) {
        if ((properties_ & Property.ClosingLogged) != 0)
            return;

        properties_ |= Property.ClosingLogged;

        CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
        if (coreTraceLevels.traceConnections() > 0) {
            org.apache.yoko.orb.OCI.TransportInfo info = transport_.get_info();
            String msg = "closing connection\n";
            msg += info.describe();

            if (initiatedClosure)
                orbInstance_.getLogger().trace("outgoing", msg);
            else
                orbInstance_.getLogger().trace("incoming", msg);

        }
    }

    //
    // main entry point into message processing
    // This method delegates to one of the specific methods
    //
    protected Upcall processMessage(GIOPIncomingMessage msg) {
        //
        // update the version of GIOP found
        //
        synchronized (this) {
            if (msg.version().major > giopVersion_.major) {
                giopVersion_.major = msg.version().major;
                giopVersion_.minor = msg.version().minor;
            } else if (msg.version().major == giopVersion_.major
                    && msg.version().minor > giopVersion_.minor) {
                giopVersion_.minor = msg.version().minor;
            }
        }

        //
        // hand off message type processing
        //
        switch (msg.type().value()) {
        case org.omg.GIOP.MsgType_1_1._Reply:
            processReply(msg);
            break;

        case org.omg.GIOP.MsgType_1_1._Request:
            return processRequest(msg);

        case org.omg.GIOP.MsgType_1_1._LocateRequest:
            processLocateRequest(msg);
            break;

        case org.omg.GIOP.MsgType_1_1._CancelRequest:
            break;

        case org.omg.GIOP.MsgType_1_1._LocateReply:
            processLocateReply(msg);
            break;

        case org.omg.GIOP.MsgType_1_1._CloseConnection:
            processCloseConnection(msg);
            break;

        case org.omg.GIOP.MsgType_1_1._MessageError:
            processMessageError(msg);
            break;

        case org.omg.GIOP.MsgType_1_1._Fragment:
            processFragment(msg);
            break;

        default:
            processException(
                    State.Error,
                    new org.omg.CORBA.COMM_FAILURE(
                            MinorCodes
                                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage),
                            org.apache.yoko.orb.OB.MinorCodes.MinorUnknownMessage,
                            org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE),
                    false);
            break;
        }

        return null;
    }

    //
    // process a request message
    //
    synchronized protected Upcall processRequest(GIOPIncomingMessage msg) {
        if ((properties_ & Property.ServerEnabled) == 0) {
            processException(State.Error, new org.omg.CORBA.COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage),
                    org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
            return null;
        }

        if (state_ != State.Active)
            return null;

        int reqId;
        org.omg.CORBA.BooleanHolder response = new org.omg.CORBA.BooleanHolder();
        org.omg.CORBA.StringHolder op = new org.omg.CORBA.StringHolder();
        org.omg.IOP.ServiceContextListHolder scl = new org.omg.IOP.ServiceContextListHolder();
        org.omg.GIOP.TargetAddressHolder target = new org.omg.GIOP.TargetAddressHolder();

        try {
            reqId = msg.readRequestHeader(response, target, op, scl);
            if (target.value.discriminator() != org.omg.GIOP.KeyAddr.value) {
                processException(
                        State.Error,
                        new org.omg.CORBA.NO_IMPLEMENT(
                                MinorCodes
                                        .describeNoImplement(org.apache.yoko.orb.OB.MinorCodes.MinorNotSupportedByLocalObject),
                                org.apache.yoko.orb.OB.MinorCodes.MinorNotSupportedByLocalObject,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO),
                        false);
                return null;
            }
        } catch (org.omg.CORBA.SystemException ex) {
            processException(State.Error, ex, false);
            return null;
        }

        //
        // Setup upcall data
        //
        org.omg.GIOP.Version version = msg.version();

        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = new org.apache.yoko.orb.OCI.ProfileInfo();
        profileInfo.major = version.major;
        profileInfo.minor = version.minor;
        profileInfo.key = target.value.object_key();

        org.apache.yoko.orb.CORBA.InputStream in = msg.input();

        //
        // We have some decision making to do here if BiDir is
        // enabled:
        // - If this is a client then make sure to properly
        // evaluate the message and obtain the correct OAInterface
        // to create the upcalls
        // - If this is a server then take the listen points from
        // the SCL (if the POA has the BiDir policy enabled) and
        // store them in this' transport for connection reuse
        //
        if ((properties_ & Property.CreatedByClient) != 0) {
            if (setOAInterface(profileInfo) == false) {
                //
                // we can't find an appropriate OAInterface in order
                // to direct the upcall so we must simply not handle
                // this request
                //
                return null;
            }
        }

        //
        // Parse the SCL, examining it for various codeset info
        //
        readCodeConverters(scl.value);
        if (codeConverters_ != null)
            in._OB_codeConverters(codeConverters_, (version.major << 8)
                    | version.minor);

        //
        // read in the peer's sendig context runtime object
        //
        assignSendingContextRuntime(in, scl.value);

        //
        // New upcall will be started
        //
        if (response.value)
            upcallsInProgress_++;
        
        orbInstance_.getLogger().debug("Processing request reqId=" + reqId + " op=" + op.value); 

        return oaInterface_.createUpcall(
                response.value ? upcallReturnInterface() : null, profileInfo,
                transport_.get_info(), reqId, op.value, in, scl.value);
    }

    //
    // process a reply message
    //
    synchronized protected void processReply(GIOPIncomingMessage msg) {
        if ((properties_ & Property.ClientEnabled) == 0) {
            processException(State.Error, new org.omg.CORBA.COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage),
                    org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
            return;
        }

        int reqId = 0;
        org.omg.GIOP.ReplyStatusType_1_2Holder status = new org.omg.GIOP.ReplyStatusType_1_2Holder();
        org.omg.IOP.ServiceContextListHolder scl = new org.omg.IOP.ServiceContextListHolder();

        try {
            reqId = msg.readReplyHeader(status, scl);
        } catch (org.omg.CORBA.SystemException ex) {
            processException(State.Error, ex, false);
            return;
        }
        
        Downcall down = messageQueue_.findAndRemovePending(reqId);
        if (down == null) {
            //
            // Request id is unknown
            //
            processException(State.Error, new org.omg.CORBA.COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReqId)
                            + ": " + reqId, org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReqId,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
            return;
        }

        down.setReplySCL(scl.value);
        org.apache.yoko.orb.CORBA.InputStream in = msg.input();

        //
        // read in the peer's sendig context runtime object
        //
        assignSendingContextRuntime(in, scl.value);
        
        orbInstance_.getLogger().debug("Processing reply for reqId=" + reqId + " status=" + status.value.value()); 

        switch (status.value.value()) {
        case org.omg.GIOP.ReplyStatusType_1_2._NO_EXCEPTION:
            down.setNoException(in);
            break;

        case org.omg.GIOP.ReplyStatusType_1_2._USER_EXCEPTION:
            down.setUserException(in);
            break;

        case org.omg.GIOP.ReplyStatusType_1_2._SYSTEM_EXCEPTION: {
            try {
                org.omg.CORBA.SystemException ex = Util
                        .unmarshalSystemException(in);
                down.setSystemException(ex);
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Error, ex, false);
            }

            break;
        }

        case org.omg.GIOP.ReplyStatusType_1_2._LOCATION_FORWARD: {
            try {
                org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(in);
                down.setLocationForward(ior, false);
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Error, ex, false);
            }

            break;
        }

        case org.omg.GIOP.ReplyStatusType_1_2._LOCATION_FORWARD_PERM: {
            try {
                org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(in);
                down.setLocationForward(ior, true);
                break;
            } catch (org.omg.CORBA.SystemException ex) {
                processException(State.Error, ex, false);
            }
        }

        case org.omg.GIOP.ReplyStatusType_1_2._NEEDS_ADDRESSING_MODE:
            //
            // TODO: implement
            //
            processException(
                    State.Error,
                    new org.omg.CORBA.NO_IMPLEMENT(
                            MinorCodes
                                    .describeNoImplement(org.apache.yoko.orb.OB.MinorCodes.MinorNotSupportedByLocalObject),
                            org.apache.yoko.orb.OB.MinorCodes.MinorNotSupportedByLocalObject,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO), false);
            break;

        default:
            processException(
                    State.Error,
                    new org.omg.CORBA.COMM_FAILURE(
                            MinorCodes
                                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReplyMessage),
                            org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReplyMessage,
                            org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE),
                    false);
            break;
        }
    }

    private void assignSendingContextRuntime(InputStream in, ServiceContext[] scl) {
        if (serverRuntime_ == null) {
            serverRuntime_
                = Util.getSendingContextRuntime (orbInstance_, scl);
        }

        in.__setSendingContextRuntime(serverRuntime_);

	}

	//
    // process a LocateRequest message
    //
    synchronized protected void processLocateRequest(GIOPIncomingMessage msg) {
        if ((properties_ & Property.ServerEnabled) == 0) {
            processException(State.Error, new org.omg.CORBA.COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage),
                    org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
            return;
        }
        Assert._OB_assert(state_ == State.Active);

        //
        // Make sure the transport can send a reply
        //
        if (transport_.mode() == org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly) {
            String message = "Discarding locate request - transport "
                    + "does not support twoway invocations";

            org.apache.yoko.orb.OCI.TransportInfo transportInfo = transport_
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
            org.omg.GIOP.TargetAddressHolder target = new org.omg.GIOP.TargetAddressHolder();
            reqId = msg.readLocateRequestHeader(target);

            if (target.value.discriminator() != org.omg.GIOP.KeyAddr.value) {
                processException(
                        State.Error,
                        new org.omg.CORBA.NO_IMPLEMENT(
                                MinorCodes
                                        .describeNoImplement(org.apache.yoko.orb.OB.MinorCodes.MinorNotSupportedByLocalObject),
                                org.apache.yoko.orb.OB.MinorCodes.MinorNotSupportedByLocalObject,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO),
                        false);
                return;
            }

            //
            // Get the key
            //
            byte[] key = target.value.object_key();
            
            //
            // Find the IOR for the key
            //
            org.omg.IOP.IORHolder ior = new org.omg.IOP.IORHolder();
            int val = oaInterface_.findByKey(key, ior);
            org.omg.GIOP.LocateStatusType_1_2 status = org.omg.GIOP.LocateStatusType_1_2
                    .from_int(val);

            //
            // Send back locate reply message
            //
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    12);
            buf.pos(12);
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo = new org.apache.yoko.orb.OCI.ProfileInfo();
            profileInfo.major = msg.version().major;
            profileInfo.minor = msg.version().minor;
            GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(
                    orbInstance_, out, profileInfo);
            outgoing.writeLocateReplyHeader(reqId, status);

            //
            // If the reply status is OBJECT_FORWARD or
            // OBJECT_FORWARD_PERM the IOR is appended to the end of the
            // LocateReply.
            //
            if (status == org.omg.GIOP.LocateStatusType_1_2.OBJECT_FORWARD
                    || status == org.omg.GIOP.LocateStatusType_1_2.OBJECT_FORWARD_PERM)
                org.omg.IOP.IORHelper.write(out, ior.value);

            //
            // TODO:
            // LOC_SYSTEM_EXCEPTION,
            // LOC_NEEDS_ADDRESSING_MODE
            //
            int pos = out._OB_pos();
            out._OB_pos(0);
            outgoing.writeMessageHeader(org.omg.GIOP.MsgType_1_1.LocateReply,
                    false, pos - 12);
            out._OB_pos(pos);

            //
            // A locate request is treated just like an upcall
            //
            upcallsInProgress_++;

            //
            // Send the locate reply
            //
            sendUpcallReply(out._OB_buffer());
        } catch (org.omg.CORBA.SystemException ex) {
            processException(State.Error, ex, false);
        }
    }

    //
    // process a LocateReply message
    //
    synchronized protected void processLocateReply(GIOPIncomingMessage msg) {
        if ((properties_ & Property.ClientEnabled) == 0) {
            processException(State.Closed, new org.omg.CORBA.COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage),
                    org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
            return;
        }

        int reqId;
        org.omg.GIOP.LocateStatusType_1_2Holder status = new org.omg.GIOP.LocateStatusType_1_2Holder();

        try {
            reqId = msg.readLocateReplyHeader(status);
        } catch (org.omg.CORBA.SystemException ex) {
            processException(State.Error, ex, false);
            return;
        }

        Downcall down = messageQueue_.findAndRemovePending(reqId);
        if (down == null) {
            //
            // Request id is unknown
            //
            processException(State.Error, new org.omg.CORBA.COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReqId),
                    org.apache.yoko.orb.OB.MinorCodes.MinorUnknownReqId,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
            return;
        }

        //
        // Was this a LocateRequest?
        //
        String op = down.operation();
        if (!op.equals("_locate")) {
            processException(State.Error, new org.omg.CORBA.COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage),
                    org.apache.yoko.orb.OB.MinorCodes.MinorWrongMessage,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
            return;
        }

        org.apache.yoko.orb.CORBA.InputStream in = msg.input();
        Logger logger = orbInstance_.getLogger();

        switch (status.value.value()) {
        case org.omg.GIOP.LocateStatusType_1_2._UNKNOWN_OBJECT:
            down.setSystemException(new org.omg.CORBA.OBJECT_NOT_EXIST());
            break;

        case org.omg.GIOP.LocateStatusType_1_2._OBJECT_HERE:
            down.setNoException(in);
            break;

        case org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD:
            try {
                org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(in);
                down.setLocationForward(ior, false);
                if (logger.isDebugEnabled()) {
                    logger.debug("Locate request forwarded to " + IORDump.PrintObjref(orbInstance_.getORB(), ior)); 
                }
            } catch (org.omg.CORBA.SystemException ex) {
                logger
                        .warning("An error occurred while reading a "
                                + "locate reply, possibly indicating\n"
                                + "an interoperability problem. You may "
                                + "need to set the LocateRequestPolicy\n"
                                + "to false.");
                down.setSystemException(ex);
                processException(State.Error, ex, false);
            }
            break;

        case org.omg.GIOP.LocateStatusType_1_2._OBJECT_FORWARD_PERM:
            try {
                org.omg.IOP.IOR ior = org.omg.IOP.IORHelper.read(in);
                down.setLocationForward(ior, true);
                if (logger.isDebugEnabled()) {
                    logger.debug("Locate request forwarded to " + IORDump.PrintObjref(orbInstance_.getORB(), ior)); 
                }
            } catch (org.omg.CORBA.SystemException ex) {
                logger
                        .warning("An error occurred while reading a "
                                + "locate reply, possibly indicating\n"
                                + "an interoperability problem. You may "
                                + "need to set the LocateRequestPolicy\n"
                                + "to false.");
                down.setSystemException(ex);
                processException(State.Error, ex, false);
            }

            break;

        case org.omg.GIOP.LocateStatusType_1_2._LOC_SYSTEM_EXCEPTION:
            try {
                org.omg.CORBA.SystemException ex = org.omg.CORBA.SystemExceptionHelper
                        .read(in);
                down.setSystemException(ex);
            } catch (org.omg.CORBA.SystemException ex) {
                down.setSystemException(ex);
                processException(State.Error, ex, false);
            }

            break;

        case org.omg.GIOP.LocateStatusType_1_2._LOC_NEEDS_ADDRESSING_MODE:
            // TODO: implement
            processException(State.Error, new org.omg.CORBA.NO_IMPLEMENT(),
                    false);
            break;
        }
    }

    //
    // process a CloseConnection message
    //
    protected void processCloseConnection(GIOPIncomingMessage msg) {
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
            processException(
                    State.Closed,
                    new org.omg.CORBA.TRANSIENT(
                            MinorCodes
                                    .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorCloseConnection),
                            org.apache.yoko.orb.OB.MinorCodes.MinorCloseConnection,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO), true);
        } else {
            setState(State.Closed);
        }
    }

    //
    // process a MessageError message
    //
    protected void processMessageError(GIOPIncomingMessage msg) {
        processException(State.Error, new org.omg.CORBA.COMM_FAILURE(org.apache.yoko.orb.OB.MinorCodes
                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorMessageError),
                org.apache.yoko.orb.OB.MinorCodes.MinorMessageError,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO), false);
    }

    //
    // process a Fragment message
    //
    protected void processFragment(GIOPIncomingMessage msg) {
        //
        // At this point there should be no fragments, only complete
        // messages.
        //
        Assert._OB_assert(false);
    }

    //
    // process a system exception
    //
    protected boolean processException(int state,
            org.omg.CORBA.SystemException ex, boolean completed) {
        Assert._OB_assert(state == State.Error || state == State.Closed);
        
        orbInstance_.getLogger().debug("processing an exception, state=" + state, ex);

        synchronized (this) {
            //
            // Don't do anything if there is no state change and it is
            // not possible to transition backwards.
            //
            if (state <= state_)
                return false;

            //
            // update the state
            //
            state_ = state;

            //
            // change the enabled/disable operations and break the
            // cyclic dependency with GIOPClient
            //
            switch (state) {
            case State.Error:
                enabledOps_ &= ~(AccessOp.Read | AccessOp.Write);
                enabledOps_ |= AccessOp.Close;
                if (client_ != null)
                    client_.removeConnection(this);
                break;

            case State.Closed:
                enabledOps_ = AccessOp.Nil;
                if (client_ != null)
                    client_.removeConnection(this);
                break;
            }

            //
            // propogate any exceptions to the message queue
            //
            messageQueue_.setException(state, ex, completed);
        }

        //
        // apply the shutdown
        //
        if (state == State.Error) {
            abortiveShutdown();
        } else if (state == State.Closed) {
            logClose(true);
            transport_.close();
        }

        //
        // set 'this' properties
        //
        synchronized (this) {
            properties_ |= Property.Destroyed;
            client_ = null;
        }

        //
        // update the connection status
        //
        refresh();
        return true;
    }

    //
    // transmits a reply back once the upcall completes
    //
    protected void sendUpcallReply(org.apache.yoko.orb.OCI.Buffer buf) {
        synchronized (this) {
            //
            // no need to do anything if we are closed
            //
            if (state_ == State.Closed)
                return;

            //
            // decrement the number of upcalls in progress
            //
            Assert._OB_assert(upcallsInProgress_ > 0);
            upcallsInProgress_--;

            //
            // add this message to the message Queue
            //
            messageQueue_.add(orbInstance_, buf);
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
            if (upcallsInProgress_ == 0 && state_ == State.Closing)
                gracefulShutdown();
        }
    }

    //
    // shutdown the connection forcefully and immediately
    //
    abstract protected void abortiveShutdown();

    //
    // shutdown the connection gracefully
    //
    abstract protected void gracefulShutdown();

    //
    // turn on ACM idle connection monitoring
    //
    synchronized protected void ACM_enableIdleMonitor() {
        if (idleTimeout_ > 0) {
            acmTimer_ = new java.util.Timer(true);
            acmTask_ = new ACMTask(this);

            acmTimer_.schedule(acmTask_, idleTimeout_ * 1000);
        }
    }

    //
    // turn off ACM idle connection monitoring
    //
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

    //
    // client-side constructor
    //
    public GIOPConnection(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Transport transport, GIOPClient client) {
        //
        // set member properties
        //
        orbInstance_ = orbInstance;
        transport_ = transport;
        client_ = client;
        state_ = State.Active;
        properties_ = Property.CreatedByClient | Property.ClientEnabled;
        enabledOps_ = AccessOp.Read | AccessOp.Write;

        //
        // read ACM properties
        //
        String value;
        java.util.Properties properties = orbInstance_.getProperties();

        //
        // the shutdown timeout for the client
        //
        value = properties.getProperty("yoko.orb.client_shutdown_timeout");
        if (value != null)
            shutdownTimeout_ = Integer.parseInt(value);

        //
        // the idle timeout for the client
        //
        value = properties.getProperty("yoko.orb.client_timeout");
        if (value != null)
            idleTimeout_ = Integer.parseInt(value);

        //
        // Trace new outgoing connection
        //
        CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
        if (coreTraceLevels.traceConnections() > 0) {
            org.apache.yoko.orb.OCI.TransportInfo info = transport_.get_info();
            String msg = "new connection\n";
            msg += info.describe();
            orbInstance_.getLogger().trace("client-side", msg);
        }
    }

    //
    // server-side constructor
    //
    public GIOPConnection(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Transport transport, OAInterface oa) {
        //
        // set members
        //
        orbInstance_ = orbInstance;
        transport_ = transport;
        oaInterface_ = oa;
        properties_ = Property.ServerEnabled;

        //
        // read ACM properties
        //
        String value;
        java.util.Properties properties = orbInstance_.getProperties();

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

    //
    // start populating the reply data
    //
    public void upcallBeginReply(Upcall upcall, org.omg.IOP.ServiceContext[] scl) {
        upcall.createOutputStream(12);
        org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int reqId = upcall.requestId();

        try {
            synchronized (this) {
                outgoing.writeReplyHeader(reqId,
                        org.omg.GIOP.ReplyStatusType_1_2.NO_EXCEPTION, scl);
            }
        } catch (org.omg.CORBA.SystemException ex) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            Assert._OB_assert(ex);
        }
    }

    //
    // finished reply construction; ready its return
    //
    public void upcallEndReply(Upcall upcall) {
        //
        // Make sure the transport can send a reply
        //
        if (transport_.mode() == org.apache.yoko.orb.OCI.SendReceiveMode.ReceiveOnly) {
            String msg = "Discarding reply - transport does not "
                    + "support twoway invocations";

            msg += "\noperation name: \"";
            msg += upcall.operation();
            msg += '"';

            org.apache.yoko.orb.OCI.TransportInfo transportInfo = transport_
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

        org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int pos = out._OB_pos();
        out._OB_pos(0);
        try {
            outgoing.writeMessageHeader(org.omg.GIOP.MsgType_1_1.Reply, false,
                    pos - 12);
        } catch (org.omg.CORBA.SystemException ex) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            Assert._OB_assert(ex);
        }

        sendUpcallReply(out._OB_buffer());
    }

    //
    // start populating the reply with a user exception
    //
    public void upcallBeginUserException(Upcall upcall,
            org.omg.IOP.ServiceContext[] scl) {
        upcall.createOutputStream(12);

        org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int reqId = upcall.requestId();

        try {
            outgoing.writeReplyHeader(reqId,
                    org.omg.GIOP.ReplyStatusType_1_2.USER_EXCEPTION, scl);
        } catch (org.omg.CORBA.SystemException ex) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            Assert._OB_assert(ex);
        }
    }

    //
    // finished reply construction; ready its return
    //
    public void upcallEndUserException(Upcall upcall) {
        upcallEndReply(upcall);
    }

    //
    // populate and send the reply with a UserException
    //
    public void upcallUserException(Upcall upcall,
            org.omg.CORBA.UserException ex, org.omg.IOP.ServiceContext[] scl) {
        upcall.createOutputStream(12);

        org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int reqId = upcall.requestId();

        try {
            outgoing.writeReplyHeader(reqId,
                    org.omg.GIOP.ReplyStatusType_1_2.USER_EXCEPTION, scl);

            //
            // Cannot marshal the exception without the Helper
            //
            // ex._OB_marshal(out);
            Assert._OB_assert(false);
        } catch (org.omg.CORBA.SystemException e) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            Assert._OB_assert(ex);
        }

        upcallEndReply(upcall);
    }

    //
    // populate and end the reply with a system exception
    //
    public void upcallSystemException(Upcall upcall,
            org.omg.CORBA.SystemException ex, org.omg.IOP.ServiceContext[] scl) {
        upcall.createOutputStream(12);

        org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int reqId = upcall.requestId();
        try {
            // print this exception out here so applications have at stack trace to work 
            // cwith for problem determination.
            
            orbInstance_.getLogger().debug("upcall exception", ex);
            outgoing.writeReplyHeader(reqId,
                    org.omg.GIOP.ReplyStatusType_1_2.SYSTEM_EXCEPTION, scl);
            Util.marshalSystemException(out, ex);
        } catch (org.omg.CORBA.SystemException e) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            Assert._OB_assert(ex);
        }

        upcallEndReply(upcall);
    }

    //
    // prepare the reply for location forwarding
    //
    public void upcallForward(Upcall upcall, org.omg.IOP.IOR ior, boolean perm,
            org.omg.IOP.ServiceContext[] scl) {
        upcall.createOutputStream(12);

        org.apache.yoko.orb.CORBA.OutputStream out = upcall.output();
        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = upcall.profileInfo();
        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out, profileInfo);

        int reqId = upcall.requestId();
        org.omg.GIOP.ReplyStatusType_1_2 status = perm ? org.omg.GIOP.ReplyStatusType_1_2.LOCATION_FORWARD_PERM
                : org.omg.GIOP.ReplyStatusType_1_2.LOCATION_FORWARD;
        try {
            outgoing.writeReplyHeader(reqId, status, scl);
            Logger logger = orbInstance_.getLogger(); 
            
            if (logger.isDebugEnabled()) {
                logger.debug("Sending forward reply to " + IORDump.PrintObjref(orbInstance_.getORB(), ior)); 
            }
            
            org.omg.IOP.IORHelper.write(out, ior);
        } catch (org.omg.CORBA.SystemException ex) {
            //
            // Nothing may go wrong here, otherwise we might have a
            // recursion
            //
            Assert._OB_assert(ex);
        }

        upcallEndReply(upcall);
    }

    //
    // enable this connection for processing as a client
    //
    synchronized public void activateClientSide(GIOPClient client) {
        Assert._OB_assert(client_ == null);

        client_ = client;
        properties_ |= Property.ClientEnabled;
        enableConnectionModes(true, true);
    }

    //
    // enable this connection for processing as a server
    //
    synchronized public void activateServerSide() {
        Assert._OB_assert((properties_ & Property.CreatedByClient) != 0);

        if ((properties_ & Property.ServerEnabled) == 0) {
            properties_ |= Property.ServerEnabled;
            enableConnectionModes(true, true);
        }
    }

    //
    // return a reference to the DowncallEmitter interface
    //
    public DowncallEmitter emitterInterface() {
        Assert._OB_assert((properties_ & Property.ClientEnabled) != 0);
        return this;
    }

    //
    // return a reference to the UpcallReturn interface
    //
    public UpcallReturn upcallReturnInterface() {
        Assert._OB_assert((properties_ & Property.ServerEnabled) != 0);
        return this;
    }

    //
    // return the transport we represent
    //
    public org.apache.yoko.orb.OCI.Transport transport() {
        return transport_;
    }

    //
    // get the state of this connection
    //
    synchronized public int state() {
        Assert._OB_assert(state_ >= State.Active && state_ <= State.Closed);
        return state_;
    }

    //
    // check if a request has been sent yet
    //
    synchronized public boolean requestSent() {
        return (properties_ & Property.RequestSent) != 0;
    }

    //
    // check if a reply has been sent yet
    //
    synchronized public boolean replySent() {
        return (properties_ & Property.ReplySent) != 0;
    }

    //
    // check if this connection was already destroyed
    //
    synchronized public boolean destroyed() {
        return (properties_ & Property.Destroyed) != 0;
    }

    //
    // check if this connection is enabled for BiDir communication
    //
    synchronized public boolean bidirConnection() {
        if (client_ == null)
            return false;
        return client_.sharedConnection();
    }

    //
    // change the state of this connection
    //
    public void setState(int newState) {
        synchronized (this) {
            if (state_ == newState
                    || (state_ != State.Holding && newState < state_))
                return;

            //
            // make sure to update the state since some of the actions
            // will key off this new state
            //
            state_ = newState;
        }

        switch (newState) {
        case State.Active:

            //
            // set the new accessable operations
            //
            synchronized (this) {
                enabledOps_ = AccessOp.Read | AccessOp.Write;
            }

            //
            // start and refresh the connection
            start();
            refresh();
            break;

        case State.Holding:

            //
            // holding connections can't read new messages but can write
            // pending messages
            //
            synchronized (this) {
                enabledOps_ &= ~AccessOp.Read;
            }

            //
            // pause the connection
            //
            pause();
            break;

        case State.Closing:

            //
            // during the closing, the connection can read/write/close
            //
            synchronized (this) {
                enabledOps_ = AccessOp.All;
            }

            //
            // gracefully shutdown by sending off pending messages,
            // reading any messages left on the wire and then closing
            //
            gracefulShutdown();

            //
            // refresh this status
            //
            refresh();
            break;

        case State.Error:

            //
            // we can't read or write in the error state but we can
            // close ourself down
            //
            synchronized (this) {
                enabledOps_ = AccessOp.Close;
            }

            //
            // there is an error so shutdown abortively
            //
            abortiveShutdown();

            //
            // mark the connection as destroyed now
            //
            synchronized (this) {
                properties_ |= Property.Destroyed;
            }

            //
            // refresh the connection status
            //
            refresh();
            break;

        case State.Closed:

            //
            // once closed, nothing else can take place
            //
            synchronized (this) {
                enabledOps_ = AccessOp.Nil;
            }

            //
            // log the connection closure
            //
            logClose(true);

            //
            // close the transport
            //
            transport_.close();

            //
            // mark the connection as destroyed
            //
            synchronized (this) {
                properties_ |= Property.Destroyed;
            }

            //
            // and refresh the connection
            //
            refresh();
            break;

        default:
            Assert._OB_assert(false);
            break;
        }
    }

    //
    // destroy this connection
    //
    public void destroy(boolean terminateNow) {
        if (!terminateNow)
            setState(State.Closing);
        else
            processException(State.Closed, new org.omg.CORBA.TRANSIENT(
                    MinorCodes
                            .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorForcedShutdown),
                    org.apache.yoko.orb.OB.MinorCodes.MinorForcedShutdown,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE), false);
    }

    //
    // callback method when the ACM signals a timeout
    //
    abstract public void ACM_callback();

    //
    // activate the connection
    //
    abstract public void start();

    //
    // refresh the connection status after a change in internal state
    //
    abstract public void refresh();

    //
    // tell the connection to stop processing; resumable with a
    // refresh()
    //
    abstract public void pause();

    //
    // change the connection mode to [client, server, both]
    //
    abstract public void enableConnectionModes(boolean enableClient,
            boolean enableServer);
}

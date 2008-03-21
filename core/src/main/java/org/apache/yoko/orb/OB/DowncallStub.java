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

import org.apache.yoko.orb.OB.RETRY_ALWAYS;

//
// DowncallStub is equivalent to the C++ class OB::MarshalStubImpl
//
public final class DowncallStub {
    static final Logger logger = Logger.getLogger(DowncallStub.class.getName());
    //
    // The ORBInstance object
    //
    private ORBInstance orbInstance_;
    
    //
    // The IOR and the original IOR
    //
    private org.omg.IOP.IOR IOR_;

    private org.omg.IOP.IOR origIOR_;

    //
    // The list of policies
    //
    private RefCountPolicyList policies_;

    //
    // All client/profile pairs
    //
    private java.util.Vector clientProfilePairs_;

    //
    // We need a class to carry the DowncallStub and Downcall across
    // a portable stub invocation
    //
    private class InvocationContext {
        DowncallStub downcallStub;

        Downcall downcall;
    }

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private synchronized Client getClientProfilePair(
            org.apache.yoko.orb.OCI.ProfileInfoHolder profileInfo)
            throws FailureException {
        //
        // Lazy initialization of the client/profile pairs
        //
        if (clientProfilePairs_ == null) {
            //
            // Get all clients that can be used
            //
            ClientManager clientManager = orbInstance_.getClientManager();
            clientProfilePairs_ = clientManager.getClientProfilePairs(IOR_, policies_.value);
        }

        //
        // If we can't get any client/profile pairs, set and raise the
        // failure exception, and let the stub handle this.
        //
        if (clientProfilePairs_.isEmpty()) {
            CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
            if (coreTraceLevels.traceRetry() >= 2) {
                logger.fine("retry: no profiles available");
            }

            throw new FailureException(new org.omg.CORBA.TRANSIENT(org.apache.yoko.orb.OB.MinorCodes
                    .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorNoUsableProfileInIOR),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoUsableProfileInIOR,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO));
        }

        ClientProfilePair clientProfilePair = (ClientProfilePair) clientProfilePairs_.elementAt(0);
        profileInfo.value = clientProfilePair.profile;
        return clientProfilePair.client;
    }

    private void destroy(boolean terminate) {
        //
        // If the ORB has been destroyed then the clientManager can be nil
        //
        ClientManager clientManager = orbInstance_.getClientManager();

        if (clientManager != null && clientProfilePairs_ != null) {
            for (int i = 0; i < clientProfilePairs_.size(); i++) {
                ClientProfilePair pair = (ClientProfilePair) clientProfilePairs_
                        .elementAt(i);
                clientManager.releaseClient(pair.client, terminate);
            }
        }

        clientProfilePairs_.removeAllElements();
    }

    protected void finalize() throws Throwable {
        destroy(false);

        super.finalize();
    }

    // ------------------------------------------------------------------
    // Public member implementations
    // ------------------------------------------------------------------

    public DowncallStub(ORBInstance orbInstance, org.omg.IOP.IOR ior,
            org.omg.IOP.IOR origIOR, RefCountPolicyList policies) {
        clientProfilePairs_ = null;

        //
        // Save the ORBInstance object
        //
        orbInstance_ = orbInstance;
        //
        // Save the IOR
        //
        IOR_ = ior;
        origIOR_ = origIOR;

        //
        // Save the policies
        //
        policies_ = policies;
    }

    //
    // Operations to create new Downcall objects
    //
    public Downcall createDowncall(String op, boolean resp)
            throws FailureException {
        org.apache.yoko.orb.OCI.ProfileInfoHolder profile = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert._OB_assert(client != null);

        if (!policies_.interceptor) {
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);
        }

        PIManager piManager = orbInstance_.getPIManager();
        if (piManager.haveClientInterceptors()) {
            return new PIDowncall(orbInstance_, client, profile.value,
                    policies_, op, resp, IOR_, origIOR_, piManager);
        } else {
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);
        }
    }

    public Downcall createLocateRequestDowncall() throws FailureException {
        org.apache.yoko.orb.OCI.ProfileInfoHolder profile = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert._OB_assert(client != null);

        //
        // A LocateRequest is not seen by the interceptors
        //
        return new Downcall(orbInstance_, client, profile.value, policies_, "_locate", true);
    }

    public Downcall createPIArgsDowncall(String op, boolean resp,
            ParameterDesc[] argDesc, ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) throws FailureException {
        org.apache.yoko.orb.OCI.ProfileInfoHolder profile = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert._OB_assert(client != null);

        if (!policies_.interceptor)
            return new Downcall(orbInstance_, client, profile.value, policies_,
                    op, resp);

        PIManager piManager = orbInstance_.getPIManager();
        if (piManager.haveClientInterceptors()) {
            return new PIArgsDowncall(orbInstance_, client, profile.value,
                    policies_, op, resp, IOR_, origIOR_, piManager, argDesc,
                    retDesc, exceptionTC);
        } else {
            return new Downcall(orbInstance_, client, profile.value, policies_,
                    op, resp);
        }
    }

    public Downcall createPIDIIDowncall(String op, boolean resp,
            org.omg.CORBA.NVList args, org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList exceptions) throws FailureException {
        org.apache.yoko.orb.OCI.ProfileInfoHolder profile = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert._OB_assert(client != null);

        if (!policies_.interceptor)
            return new Downcall(orbInstance_, client, profile.value, policies_,
                    op, resp);

        PIManager piManager = orbInstance_.getPIManager();
        if (piManager.haveClientInterceptors()) {
            return new PIDIIDowncall(orbInstance_, client, profile.value,
                    policies_, op, resp, IOR_, origIOR_, piManager, args,
                    result, exceptions);
        } else {
            return new Downcall(orbInstance_, client, profile.value, policies_,
                    op, resp);
        }
    }

    //
    // Marshalling interception points
    //

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal(Downcall down)
            throws LocationForward, FailureException {
        return down.preMarshal();
    }

    public void marshalEx(Downcall down, org.omg.CORBA.SystemException ex)
            throws LocationForward, FailureException {
        down.marshalEx(ex);
    }

    public void postMarshal(Downcall down) throws LocationForward,
            FailureException {
        down.postMarshal();
    }

    //
    // Methods to invoke requests and pick up replies
    //

    public void locate(Downcall down) throws LocationForward, FailureException {
        down.locate();
    }

    public void request(Downcall down) throws LocationForward, FailureException {
        down.request();
    }

    public void oneway(Downcall down) throws LocationForward, FailureException {
        down.oneway();
    }

    public void deferred(Downcall down) throws LocationForward,
            FailureException {
        down.deferred();
    }

    public void response(Downcall down) throws LocationForward,
            FailureException {
        down.response();
    }

    public boolean poll(Downcall down) throws LocationForward, FailureException {
        return down.poll();
    }

    //
    // Unmarshalling interception points
    //

    public org.apache.yoko.orb.CORBA.InputStream preUnmarshal(Downcall down)
            throws LocationForward, FailureException {
        return down.preUnmarshal();
    }

    public org.apache.yoko.orb.CORBA.InputStream preUnmarshal(Downcall down,
            org.omg.CORBA.BooleanHolder uex) throws LocationForward,
            FailureException {
        org.apache.yoko.orb.CORBA.InputStream in = down.preUnmarshal();
        uex.value = down.userException();
        return in;
    }

    public void unmarshalEx(Downcall down, org.omg.CORBA.SystemException ex)
            throws LocationForward, FailureException {
        down.unmarshalEx(ex);
    }

    public void postUnmarshal(Downcall down) throws LocationForward,
            FailureException {
        down.postUnmarshal();
    }

    //
    // Operations for handling user exceptions
    //

    public String unmarshalExceptionId(Downcall down) {
        return down.unmarshalExceptionId();
    }

    public void setUserException(Downcall down, org.omg.CORBA.UserException ex,
            String exId) {
        down.setUserException(ex, exId);
    }

    public void setUserException(Downcall down, org.omg.CORBA.UserException ex) {
        down.setUserException(ex);
    }

    //
    // Handle a FailureException
    //
    public synchronized void handleFailureException(Downcall down,
            FailureException ex) throws FailureException {
        //
        // Only called if there is really a failure
        //
        Assert._OB_assert(ex.exception != null);

        //
        // If there was a failure, release the client and remove the
        // faulty client/profile pair, whether we retry or not
        //
        Client client = down.client();
        org.apache.yoko.orb.OCI.ProfileInfo profile = down.profileInfo();

        for (int i = 0; i < clientProfilePairs_.size(); i++) {
            ClientProfilePair pair = (ClientProfilePair) clientProfilePairs_
                    .elementAt(i);
            if (pair.client == client && pair.profile == profile) {
                ClientManager clientManager = orbInstance_.getClientManager();

                //
                // Make sure the ORB has not been destroyed
                //
                if (clientManager == null)
                    throw new org.omg.CORBA.BAD_INV_ORDER(
                            MinorCodes
                                    .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorShutdownCalled),
                            org.apache.yoko.orb.OB.MinorCodes.MinorShutdownCalled,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);

                clientManager.releaseClient(pair.client, false);
                clientProfilePairs_.removeElementAt(i);
                break;
            }
        }

        //
        // We only retry upon COMM_FAILURE, TRANSIENT, and NO_RESPONSE
        //
        try {
            throw ex.exception;
        } catch (org.omg.CORBA.COMM_FAILURE e) {
        } catch (org.omg.CORBA.TRANSIENT e) {
        } catch (org.omg.CORBA.NO_RESPONSE e) {
        } catch (org.omg.CORBA.SystemException e) {
            throw ex; // Not "throw e;"!
        }

        //
        // We can't retry if RETRY_STRICT or RETRY_NEVER is set and the
        // completion status is not COMPLETED_NO
        //
        if (policies_.retry.mode != RETRY_ALWAYS.value
                && ex.exception.completed != org.omg.CORBA.CompletionStatus.COMPLETED_NO) {
            throw ex;
        }

        //
        // If no client/profile pairs are left, we cannot retry either
        //
        if (clientProfilePairs_.isEmpty()) {
            logger.log(Level.FINE, "no profiles left to try", ex.exception);
            throw ex;
        }

        //
        // OK, let's continue with the next profile
        //
        CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
        logger.log(Level.FINE, "trying next profile", ex.exception);
    }

    public boolean locate_request() throws LocationForward, FailureException {
        logger.fine("performing a locate_request"); 
        while (true) {
            org.apache.yoko.orb.OB.Downcall down = createLocateRequestDowncall();

            try {
                try {
                    //
                    // Get the client and force a binding
                    //
                    Client client = down.client();
                    client.bind(policies_.connectTimeout);

                    //
                    // If the LocateRequest policy is false, then return now
                    //
                    if (!policies_.locateRequest) {
                        logger.fine("LocateRequest policy is false, returning true"); 
                        return true;
                    }

                    //
                    // If the client doesn't support twoway invocations,
                    // then silently pretend the locate request succeeded
                    //
                    if (!client.twoway()) {
                        logger.fine("Twoway invocations not supported, returning true"); 
                        return true;
                    }
                } catch (org.omg.CORBA.SystemException ex) {
                    logger.log(Level.FINE, "Exception occurred during locate request", ex); 
                    throw new FailureException(ex);
                }

                preMarshal(down);
                postMarshal(down);
                locate(down);
                preUnmarshal(down);
                postUnmarshal(down);
                logger.fine("Object located"); 
                return true;
            } catch (org.omg.CORBA.OBJECT_NOT_EXIST ex) {
                logger.log(Level.FINE, "Object does not exist", ex); 
                return false;
            } catch (FailureException ex) {
                logger.log(Level.FINE, "Object lookup failure", ex); 
                handleFailureException(down, ex);
            }
        }
    }

    public org.apache.yoko.orb.OCI.ConnectorInfo get_oci_connector_info() {
        try {
            org.apache.yoko.orb.OCI.ProfileInfoHolder profileInfo = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
            org.apache.yoko.orb.OB.Client client = getClientProfilePair(profileInfo);
            org.apache.yoko.orb.OB.Assert._OB_assert(client != null);
            return client.connectorInfo();
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
            Assert._OB_assert(ex);
            return null; // The compiler needs this
        }
    }

    public org.apache.yoko.orb.OCI.TransportInfo get_oci_transport_info() {
        try {
            org.apache.yoko.orb.OCI.ProfileInfoHolder profileInfo = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
            org.apache.yoko.orb.OB.Client client = getClientProfilePair(profileInfo);
            org.apache.yoko.orb.OB.Assert._OB_assert(client != null);
            return client.transportInfo();
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
            Assert._OB_assert(ex);
            return null; // The compiler needs this
        }
    }

    //
    // Prepare a request from a portable stub
    //
    public org.apache.yoko.orb.CORBA.OutputStream setupRequest(
            org.omg.CORBA.Object self, String operation,
            boolean responseExpected) throws LocationForward, FailureException {
        while (true) {
            org.apache.yoko.orb.OB.Downcall downcall = createDowncall(
                    operation, responseExpected);

            try {
                org.apache.yoko.orb.CORBA.OutputStream out = preMarshal(downcall);
                //
                // The InvocationContext is associated with the OutputStream
                // and retrieved by invoke()
                //
                InvocationContext ctx = new InvocationContext();
                ctx.downcallStub = this;
                ctx.downcall = downcall;
                out._OB_invocationContext(ctx);
                out._OB_ORBInstance(this._OB_getORBInstance());
                return out;
            } catch (FailureException ex) {
                
                handleFailureException(downcall, ex);
            }
        }
    }

    //
    // Prepare a polling request from a portable stub
    //
    // public org.apache.yoko.orb.CORBA.OutputStream
    public org.apache.yoko.orb.OB.CodeConverters setupPollingRequest(
            org.omg.IOP.ServiceContextListHolder sclHolder,
            org.apache.yoko.orb.CORBA.OutputStreamHolder out)
            throws FailureException {
        //
        // Create buffer to contain out marshalable data
        //
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();

        //
        // Obtain information regarding our target
        //
        org.apache.yoko.orb.OCI.ProfileInfoHolder info = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
        Client client = getClientProfilePair(info);

        out.value = new org.apache.yoko.orb.CORBA.OutputStream(buf, client
                .codeConverters(), 258);

        sclHolder.value = client.getAMIRouterSCL();

        //
        // Be sure to add the invocation context before returning
        //
        InvocationContext ctx = new InvocationContext();
        ctx.downcallStub = this;
        ctx.downcall = null;
        out.value._OB_invocationContext(ctx);

        return client.codeConverters();
    }

    //
    // Creates an output stream that holds an AMI router request. Note
    // that this needs to be done in two parts. The first (this method)
    // creates the initial stream and writes the request header. The
    // second will complete the request by writing the message header into
    // the stream (this needs to be done after we write the requests
    // parameters)
    //
    public GIOPOutgoingMessage AMIRouterPreMarshal(String operation,
            boolean responseExpected,
            org.apache.yoko.orb.CORBA.OutputStreamHolder out,
            org.apache.yoko.orb.OCI.ProfileInfoHolder info)
            throws FailureException {
        //
        // Create buffer to contain our marshalable data
        //
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                12);
        buf.pos(12);

        //
        // Obtain information regarding our target
        //
        Client client = getClientProfilePair(info);

        out.value = new org.apache.yoko.orb.CORBA.OutputStream(buf, client
                .codeConverters(), 258);
        org.omg.IOP.ServiceContext[] scl = client.getAMIRouterSCL();

        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_,
                out.value, info.value);

        //
        // Put the request header into the stream
        //
        outgoing.writeRequestHeader(client.requestId(), operation,
                responseExpected, scl);

        return outgoing;
    }

    public void AMIRouterPostMarshal(GIOPOutgoingMessage outgoing,
            org.apache.yoko.orb.CORBA.OutputStreamHolder out) {
        int pos = out.value._OB_pos();
        out.value._OB_pos(0);
        outgoing.writeMessageHeader(org.omg.GIOP.MsgType_1_1.Request, false,
                pos - 12);
        out.value._OB_pos(pos);

        //
        // The InvocationContext is associated with the OutputStream
        // and retrieved by ami_*_request
        //
        InvocationContext ctx = new InvocationContext();
        ctx.downcallStub = this;
        ctx.downcall = null; // no downcall associated with an AMI request
        out.value._OB_invocationContext(ctx);
    }

    //
    // Invoke a request from a portable stub
    //
    public org.apache.yoko.orb.CORBA.InputStream invoke(
            org.omg.CORBA.Object self,
            org.apache.yoko.orb.CORBA.OutputStream out)
            throws org.omg.CORBA.portable.ApplicationException,
            org.omg.CORBA.portable.RemarshalException, LocationForward,
            FailureException {
        //
        // We should have an InvocationContext associated with the
        // OutputStream
        //
        org.apache.yoko.orb.CORBA.OutputStream o = out;
        InvocationContext ctx = (InvocationContext) o._OB_invocationContext();
        Assert._OB_assert(ctx != null);

        //
        // If the DowncallStub has changed, then remarshal
        //
        if (ctx.downcallStub != this) {
            throw new org.omg.CORBA.portable.RemarshalException();
        }

        Downcall down = ctx.downcall;

        //
        // No while loop here - if we need to reinvoke, we must raise
        // RemarshalException
        //
        try {
            down.postMarshal();

            boolean response = down.responseExpected();
            if (response) {
                down.request();
            }
            else {
                down.oneway();
            }

            if (response) {
                org.apache.yoko.orb.CORBA.InputStream in = down.preUnmarshal();

                if (down.userException()) {
                    String id = null;

                    try {
                        //
                        // Extract the exception's repository ID
                        //
                        id = down.unmarshalExceptionId();
                    } catch (org.omg.CORBA.SystemException ex) {
                        down.unmarshalEx(ex);
                    }

                    //
                    // We're using portable stubs, so we'll never
                    // be given the user exception instance. Therefore,
                    // we might as well invoke the interceptors now.
                    //
                    down.setUserException(id);
                    down.postUnmarshal();

                    throw new org.omg.CORBA.portable.ApplicationException(id,
                            in);
                } else {
                    //
                    // We're using portable stubs, so we'll never
                    // know the unmarshalled results. Therefore,
                    // we might as well invoke the interceptors now.
                    //
                    down.postUnmarshal();

                    return in;
                }
            } else {
                down.preUnmarshal();
                down.postUnmarshal();
                return null;
            }
        } catch (FailureException ex) {
            handleFailureException(down, ex);
        }

        //
        // If we reach this point, then we need to reinvoke
        //
        throw new org.omg.CORBA.portable.RemarshalException();
    }

    public org.omg.CORBA.Object getAMIPollTarget() {
        //
        // Since we don't have access to the IOR information in the
        // generated stub, we will call this method to create the target
        // object for a polling request
        //
        org.apache.yoko.orb.OB.ObjectFactory objectFactory = orbInstance_
                .getObjectFactory();
        return objectFactory.createObject(IOR_);
    }

    public org.omg.MessageRouting.PersistentRequest ami_poll_request(
            org.omg.CORBA.portable.OutputStream out, String operation,
            org.omg.IOP.ServiceContext[] scl)
            throws org.omg.CORBA.portable.RemarshalException {
        //
        // setup the ORBInstance
        //
        // poller._OB_ORBInstance(orbInstance_);
        Assert._OB_assert(out != null);

        //
        // We should have an InvocationContext associated with the
        // OutputStream
        //
        org.apache.yoko.orb.CORBA.OutputStream o = (org.apache.yoko.orb.CORBA.OutputStream) out;
        InvocationContext ctx = (InvocationContext) o._OB_invocationContext();
        Assert._OB_assert(ctx != null);

        //
        // If the DowncallStub has changed, then remarshal
        //
        if (ctx.downcallStub != this)
            throw new org.omg.CORBA.portable.RemarshalException();

        //
        // Obtain the ORB
        //
        org.omg.CORBA.ORB orb = orbInstance_.getORB();
        Assert._OB_assert(orb != null);

        //
        // Obtain the PersistentRequestRouter
        //
        org.omg.MessageRouting.PersistentRequestRouter router = org.apache.yoko.orb.OB.MessageRoutingUtil
                .getPersistentRouterFromConfig(orbInstance_);
        org.apache.yoko.orb.OB.Assert._OB_assert(router != null);

        //
        // Obtain information regarding our target
        //
        org.apache.yoko.orb.OCI.ProfileInfoHolder info = new org.apache.yoko.orb.OCI.ProfileInfoHolder();
        info.value = null;
        Client client = null;
        try {
            client = getClientProfilePair(info);
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
            throw new org.omg.CORBA.portable.RemarshalException();
        }

        //
        // Get the profile index
        //
        short index = (short) info.value.index;

        //
        // Create the router to_visit list
        //
        org.omg.MessageRouting.RouterListHolder to_visit = new org.omg.MessageRouting.RouterListHolder();
        to_visit.value = new org.omg.MessageRouting.Router[0];
        org.apache.yoko.orb.OB.MessageRoutingUtil.getRouterListFromComponents(
                orbInstance_, info.value, to_visit);

        //
        // Obtain the target objects
        //
        org.apache.yoko.orb.OB.ObjectFactory objectFactory = orbInstance_
                .getObjectFactory();
        org.omg.CORBA.Object target = objectFactory.createObject(IOR_);

        //
        // Populate the RequestMessage payload
        //
        org.omg.MessageRouting.RequestMessage payload = new org.omg.MessageRouting.RequestMessage();
        // payload.service_contexts = new org.omg.IOP.ServiceContext[0];
        //
        // XXX
        //
        payload.service_contexts = scl;
        payload.giop_version = new org.omg.GIOP.Version();
        payload.giop_version.major = info.value.major;
        payload.giop_version.minor = info.value.minor;
        payload.response_flags = 1;
        payload.reserved = new byte[3];
        payload.reserved[0] = 0;
        payload.reserved[1] = 0;
        payload.reserved[2] = 0;
        payload.operation = operation;
        payload.object_key = new byte[info.value.key.length];
        System.arraycopy(info.value.key, 0, payload.object_key, 0,
                info.value.key.length);

        o._OB_pos(0);
        org.apache.yoko.orb.OCI.Buffer buf = o._OB_buffer();
        org.omg.MessageRouting.MessageBody messageBody = new org.omg.MessageRouting.MessageBody();
        messageBody.byte_order = false; // Java is always false
        messageBody.body = new byte[buf.rest_length()];
        System.arraycopy(buf.data(), buf.pos(), messageBody.body, 0, buf
                .rest_length());
        payload.body = messageBody;

        //
        // Empty QoS list
        //
        org.omg.CORBA.Policy[] qosList = new org.omg.CORBA.Policy[0];

        //
        // Create a new Persistent request
        //
        org.omg.MessageRouting.PersistentRequest request = router
                .create_persistent_request(index, to_visit.value, target,
                        qosList, payload);

        //
        // Return the persistent request back to the stub
        //
        return request;
    }

    public boolean ami_callback_request(
            org.omg.CORBA.portable.OutputStream out,
            org.omg.Messaging.ReplyHandler reply,
            org.apache.yoko.orb.OCI.ProfileInfo info)
            throws org.omg.CORBA.portable.RemarshalException {
        //
        // We should have an InvocationContext associated with the
        // OutputStream
        //
        org.apache.yoko.orb.CORBA.OutputStream o = (org.apache.yoko.orb.CORBA.OutputStream) out;
        InvocationContext ctx = (InvocationContext) o._OB_invocationContext();
        Assert._OB_assert(ctx != null);

        //
        // If the DowncallStub has changed, then remarshal
        //
        if (ctx.downcallStub != this)
            throw new org.omg.CORBA.portable.RemarshalException();

        org.apache.yoko.orb.CORBA.InputStream tmpIn = (org.apache.yoko.orb.CORBA.InputStream) out
                .create_input_stream();

        //
        // Unmarshal the message header
        //
        org.omg.GIOP.MessageHeader_1_1 msgHeader = org.omg.GIOP.MessageHeader_1_2Helper
                .read(tmpIn);

        //
        // Check the GIOP version
        //
        if (!(msgHeader.GIOP_version.major >= 1 && msgHeader.GIOP_version.minor >= 2)) {
            //
            // Report error - throw exception?
            //
            return false;
        }

        //
        // Check the message type
        //
        if (msgHeader.message_type != (byte) org.omg.GIOP.MsgType_1_1._Request) {
            //
            // Report error - throw exception
            //
            return false;
        }

        //
        // Create and populate a RequestInfo to send to the router
        //
        org.omg.MessageRouting.RequestInfo requestInfo = new org.omg.MessageRouting.RequestInfo();

        //
        // Unmarshal the request header
        // 
        org.omg.GIOP.RequestHeader_1_2 requestHeader = org.omg.GIOP.RequestHeader_1_2Helper
                .read(tmpIn);

        //
        // Create and populate a RequestInfo structure to send to the
        // Router
        //
        org.omg.MessageRouting.RouterListHolder configRouterList = new org.omg.MessageRouting.RouterListHolder();
        configRouterList.value = new org.omg.MessageRouting.Router[0];

        //
        // Populate the configRouterList
        //
        org.apache.yoko.orb.OB.MessageRoutingUtil.getRouterListFromComponents(
                orbInstance_, info, configRouterList);

        requestInfo.visited = new org.omg.MessageRouting.Router[0];
        requestInfo.to_visit = new org.omg.MessageRouting.Router[0];

        //
        // Get the target for this request
        //
        org.apache.yoko.orb.OB.ObjectFactory objectFactory = orbInstance_
                .getObjectFactory();
        //
        // REVISIT: Should we be using IOR_ or origIOR_?
        //
        requestInfo.target = objectFactory.createObject(IOR_);

        //
        // Get the index of the profile being used
        //
        requestInfo.profile_index = (short) info.index;

        //
        // Get the reply destination for this request
        //
        org.omg.MessageRouting.ReplyDestination replyDest = new org.omg.MessageRouting.ReplyDestination();
        replyDest.handler_type = org.omg.MessageRouting.ReplyDisposition.TYPED;
        replyDest.handler = reply;
        requestInfo.reply_destination = replyDest;

        //
        // Get the selected qos for this request
        //
        org.omg.Messaging.PolicyValueSeqHolder invocPoliciesHolder = new org.omg.Messaging.PolicyValueSeqHolder();
        invocPoliciesHolder.value = new org.omg.Messaging.PolicyValue[0];
        org.apache.yoko.orb.OB.MessageRoutingUtil.getInvocationPolicyValues(
                policies_, invocPoliciesHolder);
        requestInfo.selected_qos = invocPoliciesHolder.value;

        //
        // Create payload (RequestMessage) for this request
        //
        org.omg.MessageRouting.RequestMessage requestMessage = new org.omg.MessageRouting.RequestMessage();
        requestMessage.giop_version = new org.omg.GIOP.Version();
        requestMessage.giop_version.major = info.major;
        requestMessage.giop_version.minor = info.minor;

        //
        // Get the service contexts for this request
        //
        requestMessage.service_contexts = requestHeader.service_context;

        //
        // Add the invocation policies service context for this request.
        // Note that this can change from request to request
        //
        org.omg.IOP.ServiceContext invocPoliciesSC = new org.omg.IOP.ServiceContext();
        invocPoliciesSC.context_id = org.omg.IOP.INVOCATION_POLICIES.value;

        //
        // Create an output stream an write the PolicyValueSeq
        //
        if (invocPoliciesHolder.value != null) {
            org.apache.yoko.orb.OCI.Buffer scBuf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream scOut = new org.apache.yoko.orb.CORBA.OutputStream(
                    scBuf);
            scOut._OB_writeEndian();
            org.omg.Messaging.PolicyValueSeqHelper.write(scOut,
                    invocPoliciesHolder.value);
            invocPoliciesSC.context_data = new byte[scOut._OB_pos()];
            System.arraycopy(invocPoliciesSC.context_data, 0, scBuf.data(), 0,
                    scBuf.length());
        }

        //
        // Add the service context to the list of current service contexts
        //
        int scLength = requestMessage.service_contexts.length;
        org.omg.IOP.ServiceContext[] scList = new org.omg.IOP.ServiceContext[scLength + 1];
        System.arraycopy(requestMessage.service_contexts, 0, scList, 0,
                scLength);
        scList[scLength] = invocPoliciesSC;

        //
        // Get the response flags for the request
        //
        requestMessage.response_flags = requestHeader.response_flags;

        //
        // Reserved octets
        //
        requestMessage.reserved = new byte[3];
        requestMessage.reserved[0] = requestHeader.reserved[0];
        requestMessage.reserved[1] = requestHeader.reserved[1];
        requestMessage.reserved[2] = requestHeader.reserved[2];

        //
        // Get the object key for the request
        //
        int keyLen = info.key.length;
        requestMessage.object_key = new byte[keyLen];
        System.arraycopy(info.key, 0, requestMessage.object_key, 0, keyLen);

        //
        // Get the operation name for the request
        //
        requestMessage.operation = requestHeader.operation;

        //
        // Get the body of the request message
        //
        org.omg.MessageRouting.MessageBody messageBody = new org.omg.MessageRouting.MessageBody();

        //
        // Java is always big endian
        //
        messageBody.byte_order = false;

        org.apache.yoko.orb.OCI.Buffer buf = tmpIn._OB_buffer();

        //
        // Align to an 8 byte boundary if we have something left
        //
        if (buf.rest_length() > 0) {
            buf.pos((buf.pos() + 7) & ~7);
        }

        //
        // Copy in the rest of the message body
        //
        messageBody.body = new byte[buf.rest_length()];
        System.arraycopy(buf.data_, buf.pos(), messageBody.body, 0, buf.rest_length());
        requestMessage.body = messageBody;

        //
        // Add the payload to the RequestInfo
        //
        requestInfo.payload = requestMessage;

        //
        // Now we have to try send the request to a router
        //
        boolean delivered = false;
        int numRouters = configRouterList.value.length;

        for (int i = numRouters - 1; (delivered == false) && (i >= 0); --i) {
            org.omg.MessageRouting.Router curRouter = configRouterList.value[i];

            //
            // We only add the routers that we have attempted to contact to
            // the to_visit list. This ensures that if a router accepts
            // the request, then the lower priority routers are not added
            //
            int curLength = requestInfo.to_visit.length;
            org.omg.MessageRouting.Router[] toVisit = new org.omg.MessageRouting.Router[curLength + 1];
            if (curLength > 0) {
                System.arraycopy(requestInfo.to_visit, 0, toVisit, 1, curLength);
            }
            toVisit[0] = curRouter;
            requestInfo.to_visit = toVisit;

            try {
                curRouter.send_request(requestInfo);

                //
                // Success: stop processing
                //
                delivered = true;
            } catch (org.omg.CORBA.SystemException ex) {
                logger.log(Level.FINE, "Failed to contact router: " + ex.getMessage(), ex); 
                //
                // Failure: try the next router in the list
                //
            }
        }

        //
        // return whether we were successful or not
        //
        return delivered;
    }

    public void _OB_closeConnection(boolean terminate) {
        destroy(terminate);
    }

    //
    // Need to be able to access the ORB instance from a stub for AMI
    // polling
    //
    public ORBInstance _OB_getORBInstance() {
        return orbInstance_;
    }
}

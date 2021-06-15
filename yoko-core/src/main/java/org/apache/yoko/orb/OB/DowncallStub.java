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
import org.apache.yoko.orb.CORBA.OutputStreamHolder;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.io.Buffer;
import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.ProfileInfoHolder;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.GIOP.MessageHeader_1_1;
import org.omg.GIOP.MessageHeader_1_2Helper;
import org.omg.GIOP.MsgType_1_1;
import org.omg.GIOP.RequestHeader_1_2;
import org.omg.GIOP.RequestHeader_1_2Helper;
import org.omg.IOP.INVOCATION_POLICIES;
import org.omg.IOP.IOR;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.ServiceContextListHolder;
import org.omg.MessageRouting.MessageBody;
import org.omg.MessageRouting.PersistentRequest;
import org.omg.MessageRouting.PersistentRequestRouter;
import org.omg.MessageRouting.ReplyDestination;
import org.omg.MessageRouting.ReplyDisposition;
import org.omg.MessageRouting.RequestInfo;
import org.omg.MessageRouting.RequestMessage;
import org.omg.MessageRouting.Router;
import org.omg.MessageRouting.RouterListHolder;
import org.omg.Messaging.PolicyValue;
import org.omg.Messaging.PolicyValueSeqHelper;
import org.omg.Messaging.PolicyValueSeqHolder;
import org.omg.Messaging.ReplyHandler;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.yoko.io.AlignmentBoundary.EIGHT_BYTE_BOUNDARY;
import static org.apache.yoko.orb.OCI.GiopVersion.GIOP1_2;
import static org.apache.yoko.logging.VerboseLogging.RETRY_LOG;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

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
    private IOR IOR_;

    private IOR origIOR_;

    //
    // The list of policies
    //
    private RefCountPolicyList policies_;

    //
    // All client/profile pairs
    //
    private Vector<ClientProfilePair> clientProfilePairs_;

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

    private synchronized Client getClientProfilePair(ProfileInfoHolder profileInfo)
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
            RETRY_LOG.fine("No profiles available");
            throw new FailureException(Transients.NO_USABLE_PROFILE_IN_IOR.create());
        }

        ClientProfilePair clientProfilePair = (ClientProfilePair) clientProfilePairs_.elementAt(0);
        profileInfo.value = clientProfilePair.profile;
        return clientProfilePair.client;
    }

    private void destroy() {
        //
        // If the ORB has been destroyed then the clientManager can be nil
        //
        ClientManager clientManager = orbInstance_.getClientManager();

        if (clientManager != null && clientProfilePairs_ != null) {
            for (ClientProfilePair pair: clientProfilePairs_) {
                clientManager.releaseClient(pair.client);
            }
        }

        clientProfilePairs_.removeAllElements();
    }

    protected void finalize() throws Throwable {
        destroy();

        super.finalize();
    }

    // ------------------------------------------------------------------
    // Public member implementations
    // ------------------------------------------------------------------

    public DowncallStub(ORBInstance orbInstance, IOR ior, IOR origIOR, RefCountPolicyList policies) {
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
    public Downcall createDowncall(String op, boolean resp) throws FailureException {
        ProfileInfoHolder profile = new ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert.ensure(client != null);

        if (!policies_.interceptor) {
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);
        }

        PIManager piManager = orbInstance_.getPIManager();
        if (piManager.haveClientInterceptors()) {
            return new PIVoidDowncall(orbInstance_, client, profile.value, policies_, op, resp, IOR_, origIOR_, piManager);
        } else {
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);
        }
    }

    public Downcall createLocateRequestDowncall() throws FailureException {
        ProfileInfoHolder profile = new ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert.ensure(client != null);

        //
        // A LocateRequest is not seen by the interceptors
        //
        return new Downcall(orbInstance_, client, profile.value, policies_, "_locate", true);
    }

    public Downcall createPIArgsDowncall(String op, boolean resp, ParameterDesc[] argDesc, ParameterDesc retDesc, TypeCode[] exceptionTC) throws FailureException {
        ProfileInfoHolder profile = new ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert.ensure(client != null);

        if (!policies_.interceptor)
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);

        PIManager piManager = orbInstance_.getPIManager();
        if (piManager.haveClientInterceptors()) {
            return new PIArgsDowncall(orbInstance_, client, profile.value, policies_, op, resp, IOR_, origIOR_, piManager, argDesc, retDesc, exceptionTC);
        } else {
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);
        }
    }

    public Downcall createPIDIIDowncall(String op, boolean resp, NVList args, NamedValue result, ExceptionList exceptions) throws FailureException {
        ProfileInfoHolder profile = new ProfileInfoHolder();
        Client client = getClientProfilePair(profile);
        Assert.ensure(client != null);

        if (!policies_.interceptor)
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);

        PIManager piManager = orbInstance_.getPIManager();
        if (piManager.haveClientInterceptors()) {
            return new PIDIIDowncall(orbInstance_, client, profile.value, policies_, op, resp, IOR_, origIOR_, piManager, args, result, exceptions);
        } else {
            return new Downcall(orbInstance_, client, profile.value, policies_, op, resp);
        }
    }

    //
    // Marshalling interception points
    //

    public org.apache.yoko.orb.CORBA.OutputStream preMarshal(Downcall down) throws LocationForward, FailureException {
        return down.preMarshal();
    }

    public void marshalEx(Downcall down, SystemException ex) throws LocationForward, FailureException {
        down.marshalEx(ex);
    }

    public void postMarshal(Downcall down) throws LocationForward, FailureException {
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

    public void deferred(Downcall down) throws LocationForward, FailureException {
        down.deferred();
    }

    public void response(Downcall down) throws LocationForward, FailureException {
        down.response();
    }

    public boolean poll(Downcall down) throws LocationForward, FailureException {
        return down.poll();
    }

    //
    // Unmarshalling interception points
    //

    public InputStream preUnmarshal(Downcall down) throws LocationForward, FailureException {
        return down.preUnmarshal();
    }

    public InputStream preUnmarshal(Downcall down, BooleanHolder uex) throws LocationForward, FailureException {
        InputStream in = down.preUnmarshal();
        uex.value = down.userException();
        return in;
    }

    public void unmarshalEx(Downcall down, SystemException ex) throws LocationForward, FailureException {
        down.unmarshalEx(ex);
    }

    public void postUnmarshal(Downcall down) throws LocationForward, FailureException {
        down.postUnmarshal();
    }

    //
    // Operations for handling user exceptions
    //

    public String unmarshalExceptionId(Downcall down) {
        return down.unmarshalExceptionId();
    }

    public void setUserException(Downcall down, UserException ex, String exId) {
        down.setUserException(ex, exId);
    }

    public void setUserException(Downcall down, UserException ex) {
        down.setUserException(ex);
    }

    //
    // Handle a FailureException
    //
    public synchronized void handleFailureException(Downcall down, FailureException ex) throws FailureException {
        //
        // Only called if there is really a failure
        //
        Assert.ensure(ex.exception != null);

        //
        // If there was a failure, release the client and remove the
        // faulty client/profile pair, whether we retry or not
        //
        Client client = down.client();
        ProfileInfo profile = down.profileInfo();

        final ClientManager clientManager = orbInstance_.getClientManager();
        //
        // Make sure the ORB has not been destroyed
        //
        if (clientManager == null)
            throw new BAD_INV_ORDER(
                    MinorCodes.describeBadInvOrder(
                            MinorCodes.MinorShutdownCalled),
                            MinorCodes.MinorShutdownCalled,
                            COMPLETED_NO);

        for (ClientProfilePair pair : clientProfilePairs_) {
            if (pair.client == client && pair.profile == profile) {
                clientManager.releaseClient(pair.client);
                clientProfilePairs_.remove(pair);
                break;
            }
        }

        //
        // We only retry upon COMM_FAILURE, TRANSIENT, and NO_RESPONSE
        //
        try {
            throw ex.exception;
        } catch (COMM_FAILURE|TRANSIENT|NO_RESPONSE forceRetry) {
            // These exceptions indicate the current connection is never going to work again,
            // so make sure the client is not re-used
            clientManager.besmirchClient(client);
        } catch (SystemException systemException) {
            throw ex; // Not "throw e;"!
        }

        //
        // We can't retry if RETRY_STRICT or RETRY_NEVER is set and the
        // completion status is not COMPLETED_NO
        //
        if (policies_.retry.mode != RETRY_ALWAYS.value && ex.exception.completed != COMPLETED_NO) {
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
        logger.log(Level.FINE, "trying next profile", ex.exception);
    }

    public boolean locate_request() throws LocationForward, FailureException {
        logger.fine("performing a locate_request"); 
        while (true) {
            Downcall down = createLocateRequestDowncall();

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
                    // If the client doesn't support two-way invocations,
                    // then silently pretend the locate request succeeded
                    //
                    if (!client.twoway()) {
                        logger.fine("Two-way invocations not supported, returning true");
                        return true;
                    }
                } catch (SystemException ex) {
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
            } catch (OBJECT_NOT_EXIST ex) {
                logger.log(Level.FINE, "Object does not exist", ex); 
                return false;
            } catch (FailureException ex) {
                logger.log(Level.FINE, "Object lookup failure", ex); 
                handleFailureException(down, ex);
            }
        }
    }

    public ConnectorInfo get_oci_connector_info() {
        try {
            ProfileInfoHolder profileInfo = new ProfileInfoHolder();
            Client client = getClientProfilePair(profileInfo);
            Assert.ensure(client != null);
            return client.connectorInfo();
        } catch (FailureException ex) {
            throw Assert.fail(ex);
        }
    }

    public TransportInfo get_oci_transport_info() {
        try {
            ProfileInfoHolder profileInfo = new ProfileInfoHolder();
            Client client = getClientProfilePair(profileInfo);
            Assert.ensure(client != null);
            return client.transportInfo();
        } catch (FailureException ex) {
            throw Assert.fail(ex);
        }
    }

    //
    // Prepare a request from a portable stub
    //
    public org.apache.yoko.orb.CORBA.OutputStream setupRequest(
            org.omg.CORBA.Object self, String operation, boolean responseExpected) throws LocationForward, FailureException {
        while (true) {
            Downcall downcall = createDowncall(
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
    public CodeConverters setupPollingRequest(ServiceContextListHolder sclHolder, OutputStreamHolder out) throws FailureException {

        //
        // Obtain information regarding our target
        //
        ProfileInfoHolder info = new ProfileInfoHolder();
        Client client = getClientProfilePair(info);

        out.value = new org.apache.yoko.orb.CORBA.OutputStream(client.codeConverters(), GIOP1_2);

        sclHolder.value = client.getAMIRouterContexts().toArray();

        //
        // Be sure to add the invocation context before returning
        //
        InvocationContext ctx = new InvocationContext();
        ctx.downcallStub = this;
        ctx.downcall = null;
        out.value._OB_invocationContext(ctx);

        return client.codeConverters();
    }

    // Creates an output stream that holds an AMI router request. Note
    // that this needs to be done in two parts. The first (this method)
    // creates the initial stream and writes the request header. The
    // second will complete the request by writing the message header into
    // the stream (this needs to be done after we write the requests
    // parameters)
    public GIOPOutgoingMessage AMIRouterPreMarshal(String operation,
                                                   boolean responseExpected,
                                                   OutputStreamHolder out,
                                                   ProfileInfoHolder info) throws FailureException {
        // Create buffer to contain our marshalable data
        WriteBuffer writeBuffer = Buffer.createWriteBuffer(12).padAll();

        // Obtain information regarding our target
        Client client = getClientProfilePair(info);

        out.value = new org.apache.yoko.orb.CORBA.OutputStream(writeBuffer, client.codeConverters(), GIOP1_2);
        ServiceContexts contexts = client.getAMIRouterContexts();

        GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out.value, info.value);

        // Put the request header into the stream
        outgoing.writeRequestHeader(client.getNewRequestID(), operation, responseExpected, contexts);

        return outgoing;
    }

    public void AMIRouterPostMarshal(GIOPOutgoingMessage outgoing, OutputStreamHolder out) {
        int pos = out.value.getPosition();
        out.value.setPosition(0);
        outgoing.writeMessageHeader(MsgType_1_1.Request, false, pos - 12);
        out.value.setPosition(pos);

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
    public InputStream invoke(
            org.omg.CORBA.Object self,
            org.apache.yoko.orb.CORBA.OutputStream out)
            throws ApplicationException,
            RemarshalException, LocationForward,
            FailureException {
        //
        // We should have an InvocationContext associated with the
        // OutputStream
        //
        org.apache.yoko.orb.CORBA.OutputStream o = out;
        InvocationContext ctx = (InvocationContext) o._OB_invocationContext();
        Assert.ensure(ctx != null);

        //
        // If the DowncallStub has changed, then remarshal
        //
        if (ctx.downcallStub != this) {
            throw new RemarshalException();
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
                InputStream in = down.preUnmarshal();

                if (down.userException()) {
                    String id = null;

                    try {
                        //
                        // Extract the exception's repository ID
                        //
                        id = down.unmarshalExceptionId();
                    } catch (SystemException ex) {
                        down.unmarshalEx(ex);
                    }

                    //
                    // We're using portable stubs, so we'll never
                    // be given the user exception instance. Therefore,
                    // we might as well invoke the interceptors now.
                    //
                    down.setUserException(id);
                    down.postUnmarshal();

                    throw new ApplicationException(id, in);
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
        throw new RemarshalException();
    }

    public org.omg.CORBA.Object getAMIPollTarget() {
        //
        // Since we don't have access to the IOR information in the
        // generated stub, we will call this method to create the target
        // object for a polling request
        //
        ObjectFactory objectFactory = orbInstance_.getObjectFactory();
        return objectFactory.createObject(IOR_);
    }

    public PersistentRequest ami_poll_request(OutputStream out, String operation, ServiceContext[] scl) throws RemarshalException {
        //
        // setup the ORBInstance
        //
        Assert.ensure(out != null);

        //
        // We should have an InvocationContext associated with the OutputStream
        //
        org.apache.yoko.orb.CORBA.OutputStream o = (org.apache.yoko.orb.CORBA.OutputStream) out;
        InvocationContext ctx = (InvocationContext) o._OB_invocationContext();
        Assert.ensure(ctx != null);

        //
        // If the DowncallStub has changed, then remarshal
        //
        if (ctx.downcallStub != this)
            throw new RemarshalException();

        //
        // Obtain the ORB
        //
        ORB orb = orbInstance_.getORB();
        Assert.ensure(orb != null);

        //
        // Obtain the PersistentRequestRouter
        //
        PersistentRequestRouter router = MessageRoutingUtil.getPersistentRouterFromConfig(orbInstance_);
        Assert.ensure(router != null);

        //
        // Obtain information regarding our target
        //
        ProfileInfoHolder info = new ProfileInfoHolder();
        info.value = null;
        try {
            getClientProfilePair(info);
        } catch (FailureException ex) {
            throw new RemarshalException();
        }

        //
        // Get the profile index
        //
        short index = (short) info.value.index;

        //
        // Create the router to_visit list
        //
        RouterListHolder to_visit = new RouterListHolder();
        to_visit.value = new Router[0];
        MessageRoutingUtil.getRouterListFromComponents(orbInstance_, info.value, to_visit);

        //
        // Obtain the target objects
        //
        ObjectFactory objectFactory = orbInstance_.getObjectFactory();
        org.omg.CORBA.Object target = objectFactory.createObject(IOR_);

        //
        // Populate the RequestMessage payload
        //
        RequestMessage payload = new RequestMessage();
        // payload.service_contexts = new ServiceContext[0];
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
        System.arraycopy(info.value.key, 0, payload.object_key, 0, info.value.key.length);

        MessageBody messageBody = new MessageBody();
        messageBody.byte_order = false; // Java is always false
        messageBody.body = o.getBufferReader().copyRemainingBytes();
        payload.body = messageBody;

        //
        // Empty QoS list
        //
        Policy[] qosList = new Policy[0];

        //
        // Create a new Persistent request
        //
        PersistentRequest request = router.create_persistent_request(index, to_visit.value, target, qosList, payload);

        //
        // Return the persistent request back to the stub
        //
        return request;
    }

    public boolean ami_callback_request(OutputStream out, ReplyHandler reply, ProfileInfo info) throws RemarshalException {
        //
        // We should have an InvocationContext associated with the
        // OutputStream
        //
        org.apache.yoko.orb.CORBA.OutputStream o = (org.apache.yoko.orb.CORBA.OutputStream) out;
        InvocationContext ctx = (InvocationContext) o._OB_invocationContext();
        Assert.ensure(ctx != null);

        //
        // If the DowncallStub has changed, then remarshal
        //
        if (ctx.downcallStub != this)
            throw new RemarshalException();

        InputStream tmpIn = (InputStream) out.create_input_stream();

        //
        // Unmarshal the message header
        //
        MessageHeader_1_1 msgHeader = MessageHeader_1_2Helper.read(tmpIn);

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
        if (msgHeader.message_type != (byte) MsgType_1_1._Request) {
            //
            // Report error - throw exception
            //
            return false;
        }

        //
        // Create and populate a RequestInfo to send to the router
        //
        RequestInfo requestInfo = new RequestInfo();

        //
        // Unmarshal the request header
        // 
        RequestHeader_1_2 requestHeader = RequestHeader_1_2Helper.read(tmpIn);

        //
        // Create and populate a RequestInfo structure to send to the
        // Router
        //
        RouterListHolder configRouterList = new RouterListHolder();
        configRouterList.value = new Router[0];

        //
        // Populate the configRouterList
        //
        MessageRoutingUtil.getRouterListFromComponents(orbInstance_, info, configRouterList);

        requestInfo.visited = new Router[0];
        requestInfo.to_visit = new Router[0];

        //
        // Get the target for this request
        //
        ObjectFactory objectFactory = orbInstance_.getObjectFactory();
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
        ReplyDestination replyDest = new ReplyDestination();
        replyDest.handler_type = ReplyDisposition.TYPED;
        replyDest.handler = reply;
        requestInfo.reply_destination = replyDest;

        //
        // Get the selected qos for this request
        //
        PolicyValueSeqHolder invocPoliciesHolder = new PolicyValueSeqHolder();
        invocPoliciesHolder.value = new PolicyValue[0];
        MessageRoutingUtil.getInvocationPolicyValues(policies_, invocPoliciesHolder);
        requestInfo.selected_qos = invocPoliciesHolder.value;

        //
        // Create payload (RequestMessage) for this request
        //
        RequestMessage requestMessage = new RequestMessage();
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
        ServiceContext invocPoliciesSC = new ServiceContext();
        invocPoliciesSC.context_id = INVOCATION_POLICIES.value;

        //
        // Create an output stream an write the PolicyValueSeq
        //
        if (invocPoliciesHolder.value != null) {
            try (org.apache.yoko.orb.CORBA.OutputStream scOut = new org.apache.yoko.orb.CORBA.OutputStream()) {
                scOut._OB_writeEndian();
                PolicyValueSeqHelper.write(scOut, invocPoliciesHolder.value);
                invocPoliciesSC.context_data = scOut.copyWrittenBytes();
            }
        }

        //
        // Add the service context to the list of current service contexts
        //
        int scLength = requestMessage.service_contexts.length;
        ServiceContext[] scList = new ServiceContext[scLength + 1];
        System.arraycopy(requestMessage.service_contexts, 0, scList, 0, scLength);
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
        MessageBody messageBody = new MessageBody();

        //
        // Java is always big endian
        //
        messageBody.byte_order = false;

        ReadBuffer rbuf = tmpIn.getBuffer();
        rbuf.align(EIGHT_BYTE_BOUNDARY);

        //
        // Copy in the rest of the message body
        //
        messageBody.body = rbuf.copyRemainingBytes();
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
            Router curRouter = configRouterList.value[i];

            //
            // We only add the routers that we have attempted to contact to
            // the to_visit list. This ensures that if a router accepts
            // the request, then the lower priority routers are not added
            //
            int curLength = requestInfo.to_visit.length;
            Router[] toVisit = new Router[curLength + 1];
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
            } catch (SystemException ex) {
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

    //
    // Need to be able to access the ORB instance from a stub for AMI
    // polling
    //
    public ORBInstance _OB_getORBInstance() {
        return orbInstance_;
    }
}

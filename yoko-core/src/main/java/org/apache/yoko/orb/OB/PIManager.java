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

final public class PIManager {
    //
    // The ORB (Java only)
    //
    private org.omg.CORBA.ORB orb_;

    //
    // The ORB Instance
    //
    private ORBInstance orbInstance_;

    //
    // The next interceptor id and mutex
    //
    private int id_;

    private java.lang.Object idMut_ = new java.lang.Object();

    //
    // The PortableInterceptor::Current implementation
    //
    private org.apache.yoko.orb.PortableInterceptor.Current_impl current_;

    //
    // The registered ClientRequestInterceptors
    //
    private java.util.Vector clientReqInterceptors_ = new java.util.Vector();

    //
    // The registered ServerRequestInterceptors
    //
    private java.util.Vector serverReqInterceptors_ = new java.util.Vector();

    //
    // The registered IORInterceptors
    //
    private java.util.Vector iorInterceptors_ = new java.util.Vector();

    //
    // The number of state slots
    //
    private int maxSlots_;

    //
    // Have all ORB initializers been invoked?
    //
    private boolean complete_;

    // ----------------------------------------------------------------------
    // PIManager private member implementation
    // ----------------------------------------------------------------------

    private int nextID() {
        synchronized (idMut_) {
            return id_++;
        }
    }

    // ----------------------------------------------------------------------
    // PIManager public member implementation
    // ----------------------------------------------------------------------

    public PIManager(org.omg.CORBA.ORB orb) {
        orb_ = orb;
        id_ = 0;
        current_ = null;
        maxSlots_ = 0;
        complete_ = false;
    }

    void destroy() {
        java.util.Enumeration e;

        e = clientReqInterceptors_.elements();
        while (e.hasMoreElements()) {
            org.omg.PortableInterceptor.Interceptor interceptor = (org.omg.PortableInterceptor.Interceptor) e
                    .nextElement();
            try {
                interceptor.destroy();
            } catch (RuntimeException ex) {
                // ignore
            }
        }

        e = serverReqInterceptors_.elements();
        while (e.hasMoreElements()) {
            org.omg.PortableInterceptor.Interceptor interceptor = (org.omg.PortableInterceptor.Interceptor) e
                    .nextElement();
            try {
                interceptor.destroy();
            } catch (RuntimeException ex) {
                // ignore
            }
        }

        e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            org.omg.PortableInterceptor.Interceptor interceptor = (org.omg.PortableInterceptor.Interceptor) e
                    .nextElement();
            try {
                interceptor.destroy();
            } catch (RuntimeException ex) {
                // ignore
            }
        }

        orbInstance_ = null;
    }

    public void addIORInterceptor(
            org.omg.PortableInterceptor.IORInterceptor interceptor,
            boolean insertAtHead)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName {
        //
        // Ensure that this interceptor isn't already registered. Ignore
        // anonymous interceptors (that is interceptors with no name).
        //
        String name = interceptor.name();
        if (name.length() != 0) {
            java.util.Enumeration e = iorInterceptors_.elements();
            while (e.hasMoreElements()) {
                String curr = ((org.omg.PortableInterceptor.IORInterceptor) e
                        .nextElement()).name();
                if (curr.length() == 0)
                    continue;
                if (curr.equals(name))
                    throw new org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName(
                            name);
            }
        }

        if (insertAtHead)
            iorInterceptors_.insertElementAt(interceptor, 0);
        else
            iorInterceptors_.addElement(interceptor);
    }

    public void addClientRequestInterceptor(
            org.omg.PortableInterceptor.ClientRequestInterceptor interceptor)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName {
        //
        // Ensure that this interceptor isn't already registered. Ignore
        // anonymous interceptors (that is interceptors with no name).
        //
        String name = interceptor.name();
        if (name.length() != 0) {
            java.util.Enumeration e = clientReqInterceptors_.elements();
            while (e.hasMoreElements()) {
                String curr = ((org.omg.PortableInterceptor.ClientRequestInterceptor) e
                        .nextElement()).name();
                if (curr.length() == 0)
                    continue;
                if (curr.equals(name))
                    throw new org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName(
                            name);
            }
        }

        clientReqInterceptors_.addElement(interceptor);
    }

    public void addServerRequestInterceptor(
            org.omg.PortableInterceptor.ServerRequestInterceptor interceptor)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName {
        //
        // Ensure that this interceptor isn't already registered. Ignore
        // anonymous interceptors (that is interceptors with no name).
        //
        String name = interceptor.name();
        if (name.length() != 0) {
            java.util.Enumeration e = serverReqInterceptors_.elements();
            while (e.hasMoreElements()) {
                String curr = ((org.omg.PortableInterceptor.ServerRequestInterceptor) e
                        .nextElement()).name();
                if (curr.length() == 0)
                    continue;
                if (curr.equals(name))
                    throw new org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName(
                            name);
            }
        }

        serverReqInterceptors_.addElement(interceptor);
    }

    public int allocateSlotId() {
        return maxSlots_++;
    }

    public void registerPolicyFactory(int type,
            org.omg.PortableInterceptor.PolicyFactory factory) {
        Assert._OB_assert(orbInstance_ != null);
        orbInstance_.getPolicyFactoryManager().registerPolicyFactory(type,
                factory, false);
    }

    public void setORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;

        //
        // Allocate the PICurrent object
        //
        Assert._OB_assert(current_ == null);
        current_ = new org.apache.yoko.orb.PortableInterceptor.Current_impl(
                orb_);

        //
        // Register the initial reference
        //
        InitialServiceManager ism = orbInstance_.getInitialServiceManager();
        try {
            ism.addInitialReference("PICurrent", current_);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            Assert._OB_assert(ex);
        }
    }

    public void setupComplete() {
        //
        // Set the number of available slots
        //
        current_._OB_setMaxSlots(maxSlots_);

        //
        // Add the default value of the interceptor policy to the ORB
        // policy manager to false if there are no client request
        // interceptors. It's not necessary to set this if there are
        // client request interceptors since the default value is true.
        //
        if (clientReqInterceptors_.isEmpty()) {
            InitialServiceManager ism = orbInstance_.getInitialServiceManager();
            try {
                org.omg.CORBA.PolicyManager pm = org.omg.CORBA.PolicyManagerHelper
                        .narrow(ism
                                .resolveInitialReferences("ORBPolicyManager"));
                org.omg.CORBA.Policy[] pl = new org.omg.CORBA.Policy[1];
                pl[0] = new InterceptorPolicy_impl(false);
                pm.set_policy_overrides(pl,
                        org.omg.CORBA.SetOverrideType.ADD_OVERRIDE);
            } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
                Assert._OB_assert(ex);
            } catch (org.omg.CORBA.InvalidPolicies ex) {
                Assert._OB_assert(ex);
            }
        }

        complete_ = true;
    }

    //
    // No argument information available
    //
    org.omg.PortableInterceptor.ClientRequestInfo clientSendRequest(String op,
            boolean responseExpected, org.omg.IOP.IOR IOR,
            org.omg.IOP.IOR origIOR,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.omg.CORBA.Policy[] policies, java.util.Vector requestSCL,
            java.util.Vector replySCL) throws LocationForward {
        Assert._OB_assert(current_ != null);
        org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl info = new org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl(
                orb_, nextID(), op, responseExpected, IOR, origIOR,
                profileInfo, policies, requestSCL, replySCL, orbInstance_,
                current_);

        info._OB_request(clientReqInterceptors_);

        return info;
    }

    //
    // DII style
    //
    org.omg.PortableInterceptor.ClientRequestInfo clientSendRequest(String op,
            boolean responseExpected, org.omg.IOP.IOR IOR,
            org.omg.IOP.IOR origIOR,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.omg.CORBA.Policy[] policies, java.util.Vector requestSCL,
            java.util.Vector replySCL, org.omg.CORBA.NVList args,
            org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList exceptions) throws LocationForward {
        Assert._OB_assert(current_ != null);
        org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl info = new org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl(
                orb_, nextID(), op, responseExpected, IOR, origIOR,
                profileInfo, policies, requestSCL, replySCL, orbInstance_,
                current_, args, result, exceptions);

        info._OB_request(clientReqInterceptors_);

        return info;
    }

    //
    // SII style
    //
    org.omg.PortableInterceptor.ClientRequestInfo clientSendRequest(String op,
            boolean responseExpected, org.omg.IOP.IOR IOR,
            org.omg.IOP.IOR origIOR,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.omg.CORBA.Policy[] policies, java.util.Vector requestSCL,
            java.util.Vector replySCL, ParameterDesc argDesc[],
            ParameterDesc retDesc, org.omg.CORBA.TypeCode[] exceptionTC)
            throws LocationForward {
        Assert._OB_assert(current_ != null);
        org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl info = new org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl(
                orb_, nextID(), op, responseExpected, IOR, origIOR,
                profileInfo, policies, requestSCL, replySCL, orbInstance_,
                current_, argDesc, retDesc, exceptionTC);

        info._OB_request(clientReqInterceptors_);

        return info;
    }

    void clientReceiveReply(org.omg.PortableInterceptor.ClientRequestInfo info)
            throws LocationForward {
        org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl) info;
        impl._OB_setReplyStatus(org.omg.PortableInterceptor.SUCCESSFUL.value);
        impl._OB_reply();
    }

    void clientReceiveException(
            org.omg.PortableInterceptor.ClientRequestInfo info,
            boolean wasSystem, Exception ex, String exId)
            throws LocationForward {
        org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl) info;
        impl
                ._OB_setReplyStatus(wasSystem ? org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value
                        : org.omg.PortableInterceptor.USER_EXCEPTION.value);
        impl._OB_setReceivedException(ex, exId);
        impl._OB_reply();
    }

    void clientReceiveLocationForward(
            org.omg.PortableInterceptor.ClientRequestInfo info,
            org.omg.IOP.IOR ior) throws LocationForward {
        org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl) info;
        impl
                ._OB_setReplyStatus(org.omg.PortableInterceptor.LOCATION_FORWARD.value);
        impl._OB_setForwardReference(ior);
        impl._OB_reply();
    }

    org.omg.PortableInterceptor.ServerRequestInfo serverCreateRequestInfo(
            String op,
            boolean responseExpected,
            org.omg.CORBA.Policy[] policies,
            byte[] adapterId,
            byte[] objectId,
            org.omg.PortableInterceptor.ObjectReferenceTemplate adapterTemplate,
            java.util.Vector in, java.util.Vector out, org.apache.yoko.orb.OCI.TransportInfo transportInfo) throws LocationForward {
        Assert._OB_assert(current_ != null);
        return new org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl(
                orb_, nextID(), op, responseExpected, policies, adapterId,
                objectId, adapterTemplate, in, out, orbInstance_, current_, transportInfo);
    }

    //
    // Setup the servant and POA information
    //
    void serverSetupServant(org.omg.PortableInterceptor.ServerRequestInfo info,
            org.omg.PortableServer.Servant servant,
            org.omg.PortableServer.POA poa) {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_servant(servant, poa);
    }

    //
    // Notify the ServerRequestInfo about a context switch
    //
    void serverContextSwitch(org.omg.PortableInterceptor.ServerRequestInfo info) {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_contextSwitch();
    }

    //
    // Set the parameter information (SSI case)
    //
    void serverParameterDesc(
            org.omg.PortableInterceptor.ServerRequestInfo info,
            ParameterDesc[] argDesc, ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_parameterDesc(argDesc, retDesc, exceptionTC);
    }

    //
    // Set the arguments (DSI case)
    //
    void serverArguments(org.omg.PortableInterceptor.ServerRequestInfo info,
            org.omg.CORBA.NVList args) {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_arguments(args);
    }

    //
    // Set the result (DSI case)
    //
    void serverResult(org.omg.PortableInterceptor.ServerRequestInfo info,
            org.omg.CORBA.Any result) {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_result(result);
    }

    //
    // Call the receive_request_service_contexts interception point
    //
    void serverReceiveRequestServiceContexts(
            org.omg.PortableInterceptor.ServerRequestInfo info)
            throws LocationForward {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_requestServiceContext(serverReqInterceptors_);
    }

    //
    // Call the receive_request interception point
    //
    void serverReceiveRequest(org.omg.PortableInterceptor.ServerRequestInfo info)
            throws LocationForward {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_request();
    }

    //
    // Call the send_reply interception point
    //
    void serverSendReply(org.omg.PortableInterceptor.ServerRequestInfo info) {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl._OB_setReplyStatus(org.omg.PortableInterceptor.SUCCESSFUL.value);
        impl._OB_sendReply();
    }

    //
    // Call the send_location_forward interception point
    //
    void serverSendLocationForward(
            org.omg.PortableInterceptor.ServerRequestInfo info,
            org.omg.IOP.IOR ior) throws LocationForward {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl
                ._OB_setReplyStatus(org.omg.PortableInterceptor.LOCATION_FORWARD.value);
        impl._OB_setForwardReference(ior);
        impl._OB_sendOther();
    }

    //
    // Call the send_exception interception point
    //
    void serverSendException(
            org.omg.PortableInterceptor.ServerRequestInfo info,
            boolean wasSystem, Exception ex) throws LocationForward {
        org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl impl = (org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl) info;
        impl
                ._OB_setReplyStatus(wasSystem ? org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value
                        : org.omg.PortableInterceptor.USER_EXCEPTION.value);
        impl._OB_setReceivedException(ex, null);
        impl._OB_sendException();
    }

    //
    // For IORInterceptors
    //
    public void establishComponents(org.omg.PortableInterceptor.IORInfo info) {
        java.util.Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                ((org.omg.PortableInterceptor.IORInterceptor) e.nextElement())
                        .establish_components(info);
            } catch (org.omg.CORBA.SystemException ex) {
            }
        }
    }

    public void componentsEstablished(org.omg.PortableInterceptor.IORInfo info) {
        java.util.Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                org.omg.PortableInterceptor.IORInterceptor_3_0 ir = org.omg.PortableInterceptor.IORInterceptor_3_0Helper
                        .narrow((org.omg.CORBA.Object) e.nextElement());
                if (ir != null) {
                    ir.components_established(info);
                }
            } catch (org.omg.CORBA.BAD_PARAM ex) {
            }
        }
    }

    public void adapterStateChange(
            org.omg.PortableInterceptor.ObjectReferenceTemplate[] templates,
            short state) {
        java.util.Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                org.omg.PortableInterceptor.IORInterceptor_3_0 ir = org.omg.PortableInterceptor.IORInterceptor_3_0Helper
                        .narrow((org.omg.CORBA.Object) e.nextElement());
                if (ir != null) {
                    ir.adapter_state_changed(templates, state);
                }
            } catch (org.omg.CORBA.SystemException ex) {

            }

        }

    }

    public void adapterManagerStateChange(String id, short state) {
        java.util.Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                org.omg.PortableInterceptor.IORInterceptor_3_0 ir = org.omg.PortableInterceptor.IORInterceptor_3_0Helper
                        .narrow((org.omg.CORBA.Object) e.nextElement());
                if (ir != null) {
                    ir.adapter_manager_state_changed(id, state);
                }
            } catch (org.omg.CORBA.SystemException ex) {

            }

        }
    }

    //
    // Do we have client request interceptors?
    //
    public boolean haveClientInterceptors() {
        return complete_ && !clientReqInterceptors_.isEmpty();
    }

    //
    // Do we have server request interceptors?
    //
    public boolean haveServerInterceptors() {
        return complete_ && !serverReqInterceptors_.isEmpty();
    }
}

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

import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl;
import org.apache.yoko.orb.PortableInterceptor.Current_impl;
import org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.InvalidPolicies;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyManager;
import org.omg.CORBA.PolicyManagerHelper;
import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.IOR;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.IORInterceptor_3_0Helper;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

import java.util.Enumeration;
import java.util.Vector;

final public class PIManager {
    //
    // The ORB (Java only)
    //
    private ORB orb_;

    //
    // The ORB Instance
    //
    private ORBInstance orbInstance_;

    //
    // The next interceptor id and mutex
    //
    private int id_;

    private Object idMut_ = new Object();

    //
    // The PortableInterceptor::Current implementation
    //
    private Current_impl current_;

    //
    // The registered ClientRequestInterceptors
    //
    private Vector clientReqInterceptors_ = new Vector();

    //
    // The registered ServerRequestInterceptors
    //
    private Vector serverReqInterceptors_ = new Vector();

    //
    // The registered IORInterceptors
    //
    private Vector iorInterceptors_ = new Vector();

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

    public PIManager(ORB orb) {
        orb_ = orb;
        id_ = 0;
        current_ = null;
        maxSlots_ = 0;
        complete_ = false;
    }

    void destroy() {
        Enumeration e;

        e = clientReqInterceptors_.elements();
        while (e.hasMoreElements()) {
            Interceptor interceptor = (Interceptor) e.nextElement();
            try {
                interceptor.destroy();
            } catch (RuntimeException ignored) {
            }
        }

        e = serverReqInterceptors_.elements();
        while (e.hasMoreElements()) {
            Interceptor interceptor = (Interceptor) e.nextElement();
            try {
                interceptor.destroy();
            } catch (RuntimeException ignored) {
            }
        }

        e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            Interceptor interceptor = (Interceptor) e
                    .nextElement();
            try {
                interceptor.destroy();
            } catch (RuntimeException ignored) {
            }
        }

        orbInstance_ = null;
    }

    public void addIORInterceptor(IORInterceptor interceptor, boolean insertAtHead) throws DuplicateName {
        //
        // Ensure that this interceptor isn't already registered. Ignore
        // anonymous interceptors (that is interceptors with no name).
        //
        String name = interceptor.name();
        if (name.length() != 0) {
            Enumeration e = iorInterceptors_.elements();
            while (e.hasMoreElements()) {
                String curr = ((IORInterceptor) e.nextElement()).name();
                if (curr.length() == 0)
                    continue;
                if (curr.equals(name))
                    throw new DuplicateName(name);
            }
        }

        if (insertAtHead)
            iorInterceptors_.insertElementAt(interceptor, 0);
        else
            iorInterceptors_.addElement(interceptor);
    }

    public void addClientRequestInterceptor(ClientRequestInterceptor interceptor) throws DuplicateName {
        //
        // Ensure that this interceptor isn't already registered. Ignore
        // anonymous interceptors (that is interceptors with no name).
        //
        String name = interceptor.name();
        if (name.length() != 0) {
            Enumeration e = clientReqInterceptors_.elements();
            while (e.hasMoreElements()) {
                String curr = ((ClientRequestInterceptor) e.nextElement()).name();
                if (curr.length() == 0)
                    continue;
                if (curr.equals(name))
                    throw new DuplicateName(name);
            }
        }

        clientReqInterceptors_.addElement(interceptor);
    }

    public void addServerRequestInterceptor(ServerRequestInterceptor interceptor) throws DuplicateName {
        //
        // Ensure that this interceptor isn't already registered. Ignore
        // anonymous interceptors (that is interceptors with no name).
        //
        String name = interceptor.name();
        if (name.length() != 0) {
            Enumeration e = serverReqInterceptors_.elements();
            while (e.hasMoreElements()) {
                String curr = ((ServerRequestInterceptor) e.nextElement()).name();
                if (curr.length() == 0)
                    continue;
                if (curr.equals(name))
                    throw new DuplicateName(name);
            }
        }

        serverReqInterceptors_.addElement(interceptor);
    }

    public int allocateSlotId() {
        return maxSlots_++;
    }

    public void registerPolicyFactory(int type, PolicyFactory factory) {
        Assert._OB_assert(orbInstance_ != null);
        orbInstance_.getPolicyFactoryManager().registerPolicyFactory(type, factory, false);
    }

    public void setORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;

        //
        // Allocate the PICurrent object
        //
        Assert._OB_assert(current_ == null);
        current_ = new Current_impl(
                orb_);

        //
        // Register the initial reference
        //
        InitialServiceManager ism = orbInstance_.getInitialServiceManager();
        try {
            ism.addInitialReference("PICurrent", current_);
        } catch (InvalidName ex) {
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
                PolicyManager pm = PolicyManagerHelper.narrow(ism.resolveInitialReferences("ORBPolicyManager"));
                Policy[] pl = new Policy[1];
                pl[0] = new InterceptorPolicy_impl(false);
                pm.set_policy_overrides(pl, SetOverrideType.ADD_OVERRIDE);
            } catch (InvalidName ex) {
                Assert._OB_assert(ex);
            } catch (InvalidPolicies ex) {
                Assert._OB_assert(ex);
            }
        }

        complete_ = true;
    }

    //
    // No argument information available
    //
    ClientRequestInfo clientSendRequest(String op,
            boolean responseExpected, IOR IOR,
            IOR origIOR,
            ProfileInfo profileInfo,
            Policy[] policies, Vector requestSCL,
            Vector replySCL) throws LocationForward {
        Assert._OB_assert(current_ != null);
        ClientRequestInfo_impl info = new ClientRequestInfo_impl(
                orb_, nextID(), op, responseExpected, IOR, origIOR,
                profileInfo, policies, requestSCL, replySCL, orbInstance_,
                current_);

        info._OB_request(clientReqInterceptors_);

        return info;
    }

    //
    // DII style
    //
    ClientRequestInfo clientSendRequest(String op,
            boolean responseExpected, IOR IOR,
            IOR origIOR,
            ProfileInfo profileInfo,
            Policy[] policies, Vector requestSCL,
            Vector replySCL, NVList args,
            NamedValue result,
            ExceptionList exceptions) throws LocationForward {
        Assert._OB_assert(current_ != null);
        ClientRequestInfo_impl info = new ClientRequestInfo_impl(
                orb_, nextID(), op, responseExpected, IOR, origIOR,
                profileInfo, policies, requestSCL, replySCL, orbInstance_,
                current_, args, result, exceptions);

        info._OB_request(clientReqInterceptors_);

        return info;
    }

    //
    // SII style
    //
    ClientRequestInfo clientSendRequest(String op,
            boolean responseExpected, IOR IOR,
            IOR origIOR,
            ProfileInfo profileInfo,
            Policy[] policies, Vector requestSCL,
            Vector replySCL, ParameterDesc argDesc[],
            ParameterDesc retDesc, TypeCode[] exceptionTC)
            throws LocationForward {
        Assert._OB_assert(current_ != null);
        ClientRequestInfo_impl info = new ClientRequestInfo_impl(
                orb_, nextID(), op, responseExpected, IOR, origIOR,
                profileInfo, policies, requestSCL, replySCL, orbInstance_,
                current_, argDesc, retDesc, exceptionTC);

        info._OB_request(clientReqInterceptors_);

        return info;
    }

    void clientReceiveReply(ClientRequestInfo info)
            throws LocationForward {
        ClientRequestInfo_impl impl = (ClientRequestInfo_impl) info;
        impl._OB_setReplyStatus(SUCCESSFUL.value);
        impl._OB_reply();
    }

    void clientReceiveException(
            ClientRequestInfo info,
            boolean wasSystem, Exception ex, String exId)
            throws LocationForward {
        ClientRequestInfo_impl impl = (ClientRequestInfo_impl) info;
        impl._OB_setReplyStatus(wasSystem ? SYSTEM_EXCEPTION.value : USER_EXCEPTION.value);
        impl._OB_setReceivedException(ex, exId);
        impl._OB_reply();
    }

    void clientReceiveLocationForward(
            ClientRequestInfo info,
            IOR ior) throws LocationForward {
        ClientRequestInfo_impl impl = (ClientRequestInfo_impl) info;
        impl._OB_setReplyStatus(LOCATION_FORWARD.value);
        impl._OB_setForwardReference(ior);
        impl._OB_reply();
    }

    ServerRequestInfo serverCreateRequestInfo(
            String op,
            boolean responseExpected,
            Policy[] policies,
            byte[] adapterId,
            byte[] objectId,
            ObjectReferenceTemplate adapterTemplate,
            Vector in, Vector out, TransportInfo transportInfo) throws LocationForward {
        Assert._OB_assert(current_ != null);
        return new ServerRequestInfo_impl(
                orb_, nextID(), op, responseExpected, policies, adapterId,
                objectId, adapterTemplate, in, out, orbInstance_, current_, transportInfo);
    }

    //
    // Setup the servant and POA information
    //
    void serverSetupServant(ServerRequestInfo info,
                            Servant servant,
                            POA poa) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_servant(servant, poa);
    }

    //
    // Notify the ServerRequestInfo about a context switch
    //
    void serverContextSwitch(ServerRequestInfo info) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_contextSwitch();
    }

    //
    // Set the parameter information (SSI case)
    //
    void serverParameterDesc(
            ServerRequestInfo info,
            ParameterDesc[] argDesc, ParameterDesc retDesc,
            TypeCode[] exceptionTC) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_parameterDesc(argDesc, retDesc, exceptionTC);
    }

    //
    // Set the arguments (DSI case)
    //
    void serverArguments(ServerRequestInfo info, NVList args) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_arguments(args);
    }

    //
    // Set the result (DSI case)
    //
    void serverResult(ServerRequestInfo info, Any result) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_result(result);
    }

    //
    // Call the receive_request_service_contexts interception point
    //
    void serverReceiveRequestServiceContexts(ServerRequestInfo info) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_requestServiceContext(serverReqInterceptors_);
    }

    //
    // Call the receive_request interception point
    //
    void serverReceiveRequest(ServerRequestInfo info) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_request();
    }

    //
    // Call the send_reply interception point
    //
    void serverSendReply(ServerRequestInfo info) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_setReplyStatus(SUCCESSFUL.value);
        impl._OB_sendReply();
    }

    //
    // Call the send_location_forward interception point
    //
    void serverSendLocationForward(
            ServerRequestInfo info,
            IOR ior) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl
                ._OB_setReplyStatus(LOCATION_FORWARD.value);
        impl._OB_setForwardReference(ior);
        impl._OB_sendOther();
    }

    //
    // Call the send_exception interception point
    //
    void serverSendException(ServerRequestInfo info, boolean wasSystem, Exception ex) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_setReplyStatus(wasSystem ? SYSTEM_EXCEPTION.value : USER_EXCEPTION.value);
        impl._OB_setReceivedException(ex, null);
        impl._OB_sendException();
    }

    //
    // For IORInterceptors
    //
    public void establishComponents(IORInfo info) {
        Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                ((IORInterceptor) e.nextElement()).establish_components(info);
            } catch (SystemException ex) {
            }
        }
    }

    public void componentsEstablished(IORInfo info) {
        Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                IORInterceptor_3_0 ir = IORInterceptor_3_0Helper.narrow((org.omg.CORBA.Object) e.nextElement());
                if (ir != null) {
                    ir.components_established(info);
                }
            } catch (BAD_PARAM ex) {
            }
        }
    }

    public void adapterStateChange(ObjectReferenceTemplate[] templates, short state) {
        Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                IORInterceptor_3_0 ir = IORInterceptor_3_0Helper.narrow((org.omg.CORBA.Object) e.nextElement());
                if (ir != null) {
                    ir.adapter_state_changed(templates, state);
                }
            } catch (SystemException ex) {

            }

        }

    }

    public void adapterManagerStateChange(String id, short state) {
        Enumeration e = iorInterceptors_.elements();
        while (e.hasMoreElements()) {
            try {
                IORInterceptor_3_0 ir = IORInterceptor_3_0Helper
                        .narrow((org.omg.CORBA.Object) e.nextElement());
                if (ir != null) {
                    ir.adapter_manager_state_changed(id, state);
                }
            } catch (SystemException ex) {

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

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

import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.orb.PortableInterceptor.ClientRequestInfo_impl;
import org.apache.yoko.orb.PortableInterceptor.Current_impl;
import org.apache.yoko.orb.PortableInterceptor.ServerRequestInfo_impl;
import org.apache.yoko.util.Assert;
import org.omg.CORBA.Any;
import org.omg.CORBA.InvalidPolicies;
import org.omg.CORBA.NVList;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.logging.Level.CONFIG;
import static org.apache.yoko.orb.OB.PIManager.Interceptors.describe;
import static org.apache.yoko.logging.VerboseLogging.IOR_LOG;
import static org.apache.yoko.logging.VerboseLogging.REQ_IN_LOG;
import static org.apache.yoko.logging.VerboseLogging.REQ_OUT_LOG;
import static org.apache.yoko.util.CollectionExtras.allOf;
import static org.apache.yoko.util.CollectionExtras.filterByType;
import static org.apache.yoko.util.CollectionExtras.newSynchronizedList;

final public class PIManager {
    private final ORB orb;
    private ORBInstance orbInstance;
    private final AtomicInteger nextInterceptorId = new AtomicInteger();
    private Current_impl current;
    private final List<ClientRequestInterceptor> clientRequestInterceptors = newSynchronizedList();
    private final List<ServerRequestInterceptor> serverRequestInterceptors = newSynchronizedList();
    private final List<IORInterceptor> iorInterceptors = newSynchronizedList();
    private int numberOfStateSlots;
    private boolean allOrbInitializersHaveBeenInvoked;

    private int nextID() {
        return nextInterceptorId.getAndIncrement();
    }

    public PIManager(ORB orb) {
        this.orb = orb;
        current = null;
        numberOfStateSlots = 0;
        allOrbInitializersHaveBeenInvoked = false;
    }

    void destroy() {
        for (Interceptor interceptor: allOf(clientRequestInterceptors, serverRequestInterceptors, iorInterceptors)) {
            try { interceptor.destroy(); } catch (RuntimeException ignored) {}
        }
        orbInstance = null;
    }

    public void addIORInterceptor(IORInterceptor interceptor, boolean insertAtHead) throws DuplicateName {
        if (IOR_LOG.isLoggable(CONFIG)) IOR_LOG.config("Registering " + describe(interceptor));
        addInterceptorToList(interceptor, this.iorInterceptors, insertAtHead);
    }

    public void addClientRequestInterceptor(ClientRequestInterceptor interceptor) throws DuplicateName {
        if (REQ_OUT_LOG.isLoggable(CONFIG)) REQ_OUT_LOG.config("Registering " + describe(interceptor));
        addInterceptorToList(interceptor, clientRequestInterceptors, false);
    }

    public void addServerRequestInterceptor(ServerRequestInterceptor interceptor) throws DuplicateName {
        if (REQ_IN_LOG.isLoggable(CONFIG)) REQ_IN_LOG.config("Registering " + describe(interceptor));
        addInterceptorToList(interceptor, serverRequestInterceptors, false);
    }

    private static <I extends Interceptor> void addInterceptorToList(I interceptor, List<I> interceptors, boolean insertAtHead) throws DuplicateName {
        // Ensure that this interceptor isn't already registered. Ignore
        // anonymous interceptors (that is interceptors with no name).
        final String name = interceptor.name();
        if (name.length() != 0) {
            for (Interceptor existing: interceptors) {
                if (name.equals(existing.name())) throw new DuplicateName(name);
            }
        }

        if (insertAtHead) interceptors.add(0, interceptor);
        else interceptors.add(interceptor);
    }

    public int allocateSlotId() {
        return numberOfStateSlots++;
    }

    public void registerPolicyFactory(int type, PolicyFactory factory) {
        Assert.ensure(orbInstance != null);
        orbInstance.getPolicyFactoryManager().registerPolicyFactory(type, factory, false);
    }

    public void setORBInstance(ORBInstance orbInstance) {
        this.orbInstance = orbInstance;

        Assert.ensure(current == null);
        current = new Current_impl(orb);

        InitialServiceManager ism = this.orbInstance.getInitialServiceManager();
        try {
            ism.addInitialReference("PICurrent", current);
        } catch (InvalidName ex) {
            throw Assert.fail(ex);
        }
    }

    public void setupComplete() {
        current._OB_setMaxSlots(numberOfStateSlots);

        // Set the default value of the interceptor policy to the ORB
        // policy manager to false if there are no client request
        // interceptors. It's not necessary to set this if there are
        // client request interceptors since the default value is true.
        if (clientRequestInterceptors.isEmpty()) {
            InitialServiceManager ism = orbInstance.getInitialServiceManager();
            try {
                PolicyManager pm = PolicyManagerHelper.narrow(ism.resolveInitialReferences("ORBPolicyManager"));
                Policy[] pl = {new InterceptorPolicy_impl(false)};
                pm.set_policy_overrides(pl, SetOverrideType.ADD_OVERRIDE);
            } catch (InvalidName | InvalidPolicies ex) {
                throw Assert.fail(ex);
            }
        }

        allOrbInitializersHaveBeenInvoked = true;
    }

    ClientRequestInfo clientSendRequest(PIDowncall downcall) throws LocationForward {
        ClientRequestInfo_impl info = new ClientRequestInfo_impl(orb, orbInstance, current, downcall);
        info._OB_request(clientRequestInterceptors);
        return info;
    }

    void clientReceiveReply(ClientRequestInfo info) throws LocationForward {
        ClientRequestInfo_impl impl = (ClientRequestInfo_impl) info;
        impl._OB_setReplyStatus(SUCCESSFUL.value);
        impl._OB_reply();
    }

    void clientReceiveException(ClientRequestInfo info, boolean wasSystem, Exception ex, String exId) throws LocationForward {
        ClientRequestInfo_impl impl = (ClientRequestInfo_impl) info;
        impl._OB_setReplyStatus(wasSystem ? SYSTEM_EXCEPTION.value : USER_EXCEPTION.value);
        impl._OB_setReceivedException(ex, exId);
        impl._OB_reply();
    }

    void clientReceiveLocationForward(ClientRequestInfo info, IOR ior) throws LocationForward {
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
            ServiceContexts requestContexts,
            ServiceContexts replyContexts,
            TransportInfo transportInfo) {
        Assert.ensure(current != null);
        return new ServerRequestInfo_impl(orb, nextID(), op, responseExpected, policies, adapterId,
                objectId, adapterTemplate, requestContexts, replyContexts, orbInstance, current, transportInfo);
    }

    void serverSetupServant(ServerRequestInfo info, Servant servant, POA poa) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_servant(servant, poa);
    }

    // Notify the ServerRequestInfo about a context switch
    void serverContextSwitch(ServerRequestInfo info) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_contextSwitch();
    }

    // Set the parameter information (SSI case)
    void serverParameterDesc(ServerRequestInfo info, ParameterDesc[] argDesc, ParameterDesc retDesc, TypeCode[] exceptionTC) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_parameterDesc(argDesc, retDesc, exceptionTC);
    }

    // Set the arguments (DSI case)
    void serverArguments(ServerRequestInfo info, NVList args) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_arguments(args);
    }

    // Set the result (DSI case)
    void serverResult(ServerRequestInfo info, Any result) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_result(result);
    }

    // Call the receive_request_service_contexts interception point
    void serverReceiveRequestServiceContexts(ServerRequestInfo info) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_requestServiceContext(serverRequestInterceptors);
    }

    // Call the receive_request interception point
    void serverReceiveRequest(ServerRequestInfo info) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_request();
    }

    // Call the send_reply interception point
    void serverSendReply(ServerRequestInfo info) {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_setReplyStatus(SUCCESSFUL.value);
        impl._OB_sendReply();
    }

    // Call the send_location_forward interception point
    void serverSendLocationForward(ServerRequestInfo info, IOR ior) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_setReplyStatus(LOCATION_FORWARD.value);
        impl._OB_setForwardReference(ior);
        impl._OB_sendOther();
    }

    // Call the send_exception interception point
    void serverSendException(ServerRequestInfo info, boolean wasSystem, Exception ex) throws LocationForward {
        ServerRequestInfo_impl impl = (ServerRequestInfo_impl) info;
        impl._OB_setReplyStatus(wasSystem ? SYSTEM_EXCEPTION.value : USER_EXCEPTION.value);
        impl._OB_setReceivedException(ex, null);
        impl._OB_sendException();
    }

    // For IORInterceptors
    public void establishComponents(IORInfo info) {
        for (IORInterceptor interceptor: iorInterceptors) {
            try { interceptor.establish_components(info); } catch (SystemException ignored) { }
        }
    }

    public void componentsEstablished(IORInfo info) {
        for (IORInterceptor_3_0 interceptor: filterByType(iorInterceptors, IORInterceptor_3_0.class)) {
            try { interceptor.components_established(info); } catch (SystemException ignored) { }
        }
    }

    public void adapterStateChange(ObjectReferenceTemplate[] templates, short state) {
        for (IORInterceptor_3_0 interceptor: filterByType(iorInterceptors, IORInterceptor_3_0.class)) {
            try { interceptor.adapter_state_changed(templates, state); } catch (SystemException ignored) { }
        }
    }

    public void adapterManagerStateChange(String id, short state) {
        for (IORInterceptor_3_0 interceptor: filterByType(iorInterceptors, IORInterceptor_3_0.class)) {
            try { interceptor.adapter_manager_state_changed(id, state); } catch (SystemException ignored) { }
        }
    }

    public boolean haveClientInterceptors() {
        return allOrbInitializersHaveBeenInvoked && !clientRequestInterceptors.isEmpty();
    }

    public boolean haveServerInterceptors() {
        return allOrbInitializersHaveBeenInvoked && !serverRequestInterceptors.isEmpty();
    }

    enum Interceptors {
        ;

        static String describe(ClientRequestInterceptor i) { return "client request interceptor " + describe0(i); }
        static String describe(ServerRequestInterceptor i) { return "server request interceptor " + describe0(i); }
        static String describe(IORInterceptor i) { return "ior interceptor " + describe0(i); }
        private static String describe0(Interceptor i) { return i.name() + " of type " + i.getClass().getName(); }
    }
}

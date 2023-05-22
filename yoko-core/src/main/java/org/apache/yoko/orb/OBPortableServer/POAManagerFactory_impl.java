/*
 * Copyright 2021 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OBPortableServer;

import org.apache.yoko.orb.IMR.ActiveState;
import org.apache.yoko.orb.IMR.Domain;
import org.apache.yoko.orb.IMR.DomainHelper;
import org.apache.yoko.orb.IMR.NoSuchOAD;
import org.apache.yoko.orb.IMR.NoSuchServer;
import org.apache.yoko.orb.IMR.OADNotRunning;
import org.apache.yoko.orb.IMR.ServerStatus;
import org.apache.yoko.orb.OAD.AlreadyLinked;
import org.apache.yoko.orb.OAD.ProcessEndpoint;
import org.apache.yoko.orb.OAD.ProcessEndpointManagerHolder;
import org.apache.yoko.orb.OAD.ProcessEndpoint_impl;
import org.apache.yoko.util.Assert;
import org.apache.yoko.orb.OB.InitialServiceManager;
import org.apache.yoko.orb.OB.LocationForward;
import org.apache.yoko.orb.OB.Logger;
import org.apache.yoko.orb.OB.ORBControl;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.PIManager;
import org.apache.yoko.orb.OB.ParseParams;
import org.apache.yoko.orb.OB.RefCountPolicyList;
import org.apache.yoko.orb.OCI.AccFactory;
import org.apache.yoko.orb.OCI.AccFactoryRegistry;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.InvalidParam;
import org.apache.yoko.orb.OCI.NoSuchFactory;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.PortableInterceptor.IMRIORInterceptor_impl;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.SystemException;
import org.omg.IOP.IOR;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableServer.POAManagerFactoryPackage.ManagerAlreadyExists;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static org.apache.yoko.util.Assert.ensure;
import static org.omg.PortableServer.POAManagerPackage.State.INACTIVE;

final public class POAManagerFactory_impl extends LocalObject implements POAManagerFactory {
    public static final String DEFAULT_ENDPOINT_SPEC = String.format("iiop --bind %1$s --host %1$s", InetAddress.getLoopbackAddress().getHostAddress());
    private ORBInstance orbInstance_;
    private Map<String, POAManager_impl> managers_;
    // Running count for generating unique names
    private AtomicInteger count_ = new AtomicInteger();
    private ActiveState activeState_;
    private POALocator poaLocator_;
    private ProcessEndpoint_impl processEndpoint_;

    // This has to be a member of the ORB since we need to keep the
    // connection open for the lifespan of the process.
    // TODO: When we have connection reaping then we'll have to have
    // to set a policy on this object to prevent the connection from
    // being reaped.
    //
    private org.omg.CORBA.Object endpointManager_;

    private String getUniqueName() {
        // TODO: elsewhere, the POA Manager that uses this generated id uses the *next* value of count as its adapterManagerId.
        // This should almost certainly marry up just to avoid confusion, so change getAndIncrement() to incrementAndGet();
        String name = String.format("POAManager-%d-%d", System.currentTimeMillis(), count_.getAndIncrement());
        ensure(!managers_.containsKey(name));
        return name;
    }

    private void validateName(String name) throws ManagerAlreadyExists {
        // Does the POAManager exist?
        POAManager manager = managers_.get(name);
        if (manager != null) {
            if (manager.get_state() == INACTIVE) managers_.remove(name);
            else throw new ManagerAlreadyExists();
        }
    }

    private AcceptorConfig[] parseEndpointString(String endpoint) {
        Logger logger = orbInstance_.getLogger();

        java.util.List<AcceptorConfig> acceptorConfigs = new ArrayList<>();

        AccFactoryRegistry registry = orbInstance_.getAccFactoryRegistry();
        AccFactory[] factories = registry.get_factories();

        int pos = 0;
        PROTOCOL_LOOP:
        while (pos != -1) {
            List<String> params = new ArrayList<>();
            pos = ParseParams.parse(endpoint, pos, params);
            if (params.isEmpty()) continue;
            String protocol = params.remove(0);

            for (AccFactory factory : factories) {
                if (! protocol.equals(factory.id())) continue;
                AcceptorConfig config = new AcceptorConfig(protocol, params.toArray(new String[0]));
                acceptorConfigs.add(config);
                continue PROTOCOL_LOOP;
            }
            logger.severe("unknown endpoint protocol `" + protocol + "'");
            throw new INITIALIZE("unknown endpoint protocol `" + protocol + "'");
        }

        if (acceptorConfigs.isEmpty()) {
            logger.severe("no endpoints defined");
            throw new INITIALIZE("no endpoints defined");
        }

        return acceptorConfigs.toArray(new AcceptorConfig[0]);
    }

    // ----------------------------------------------------------------------
    // OBPOAManagerFactory_impl public member implementation
    // ----------------------------------------------------------------------

    public POAManagerFactory_impl() {
        managers_ = new Hashtable<>(7);
        poaLocator_ = new POALocator();
    }

    // ----------------------------------------------------------------------
    // Standard IDL to Java mapping
    // ----------------------------------------------------------------------

    @Override
    public POAManager_impl create_POAManager(String id, Policy[] policies) throws ManagerAlreadyExists, PolicyError {
        synchronized (managers_) {
            if (id.isEmpty()) {
                id = getUniqueName();
            } else {
                validateName(id);
                count_.incrementAndGet();
            }

            Properties props = orbInstance_.getProperties();
            Logger logger = orbInstance_.getLogger();

            // If no endpoint config policy is defined, this info will
            // have to be retrieved from the orb properties.
            EndpointConfigurationPolicy endpointPolicy = null;

            // We are only concerned with the endpoint config policy
            // here; other policies will be passed on to the POAManager_impl
            // constructor.
            List<Policy> policyList = new ArrayList<>();

            int nTmpPolicies = 0;

            int nPolicies = policies.length;
            if (nPolicies != 0) {
                for (Policy policy : policies) {
                    int policyType = policy.policy_type();
                    if (policyType == ENDPOINT_CONFIGURATION_POLICY_ID.value) {
                        endpointPolicy = EndpointConfigurationPolicyHelper.narrow(policy);
                    } else {
                        ++nTmpPolicies;
                        policyList.add(policy);
                    }
                }
            }
            Policy[] tmpPolicies = policyList.toArray(new Policy[0]);

            AcceptorConfig[] configs;

            if (endpointPolicy == null) {
                // Get the endpoint configuration
                String defaultEndpointSpec = DEFAULT_ENDPOINT_SPEC;
                if (id.equals("RootPOAManager")) defaultEndpointSpec = props.getProperty("yoko.orb.oa.endpoint", defaultEndpointSpec);
                String paramStr = props.getProperty("yoko.orb.poamanager." + id + ".endpoint", defaultEndpointSpec);
                configs = parseEndpointString(paramStr);
            } else {
                // Create acceptors based on the endpoint config policy
                configs = endpointPolicy.value();
            }

            AccFactoryRegistry registry = orbInstance_.getAccFactoryRegistry();

            List<Acceptor> acceptors = new ArrayList<>();
            for (AcceptorConfig acceptorConfig : configs) {
                try {
                    AccFactory factory = registry.get_factory(acceptorConfig.id);
                    acceptors.add(factory.create_acceptor(acceptorConfig.params));
                } catch (NoSuchFactory ex) {
                    String err = "cannot find factory: " + ex;
                    logger.log(Level.SEVERE, err, ex);
                    throw (INITIALIZE) new INITIALIZE(err).initCause(ex);
                } catch (InvalidParam ex) {
                    String err = "unable to create acceptor: " + ex.reason;
                    logger.log(Level.SEVERE, err, ex);
                    throw (INITIALIZE) new INITIALIZE(err).initCause(ex);
                }
            }

            // Create the new POAManager_impl and add to the table
            Acceptor[] arr = acceptors.toArray(new Acceptor[0]);
            POAManager_impl manager = new POAManager_impl(orbInstance_, poaLocator_, id, count_.toString(), arr, tmpPolicies);
            managers_.put(id, manager);
            return manager;
        }
    }

    @Override
    public org.omg.PortableServer.POAManager[] list() {
        List<POAManager_impl> list = new ArrayList<>(managers_.values());
        while(list.remove(null));
        return list.toArray(new org.omg.PortableServer.POAManager[0]);
    }

    @Override
    public POAManager_impl find(String id) {
        return managers_.get(id);
    }

    public void destroy() {
        orbInstance_ = null;
        // Shutdown, if necessary
        if (!managers_.isEmpty()) _OB_deactivate();
    }

    public EndpointConfigurationPolicy create_endpoint_configuration_policy(String value) throws PolicyError {
        AcceptorConfig[] configArray = parseEndpointString(value);
        return new EndpointConfigurationPolicy_impl(configArray);
    }

    public CommunicationsConcurrencyPolicy create_communications_concurrency_policy(short value) throws PolicyError {
        return new CommunicationsConcurrencyPolicy_impl(value);
    }

    public GIOPVersionPolicy create_giop_version_policy(short value) throws PolicyError {
        return new GIOPVersionPolicy_impl(value);
    }

    public void _OB_deactivate() {
        for (POAManager manager: managers_.values()) {
            if (manager == null) continue;
            try {
                manager.deactivate(true, true);
            } catch (AdapterInactive ignored) {}
        }
        managers_.clear();

        // Tell the IMR that the ORB is STOPPING
        if (activeState_ == null) return;

        Logger logger = orbInstance_.getLogger();
        String serverInstance = orbInstance_.getServerInstance();
        try {
            activeState_.set_status(serverInstance, ServerStatus.STOPPING);
        } catch (SystemException ex) {
            logger.log(Level.WARNING, orbInstance_.getServerId() + ": Cannot contact IMR on shutdown", ex);
        }
        activeState_ = null;
    }

    public DirectServant _OB_getDirectServant(IOR ior, RefCountPolicyList policies) throws LocationForward {
        for (POAManager_impl manager: managers_.values()) {
            if (manager == null) continue;
            try {
                for (Acceptor acceptor : manager.get_acceptors()) {
                    ProfileInfo[] profileInfos = acceptor.get_local_profiles(ior);
                    // If the IOR is local then at least one ProfileInfo will be returned
                    if (profileInfos.length == 0) continue;
                    return manager._OB_getDirectServant(profileInfos[0].key, policies);
                }
            } catch (AdapterInactive|OBJECT_NOT_EXIST ignored) {
                // At this point, we just want to determine if there is a local version.
            }
        }
        // In the case that the servant cannot support a direct invocation, null will be returned
        return null;
    }

    public void _OB_setORBInstance(ORBInstance instance) {
        orbInstance_ = instance;
    }

    public void _OB_initializeIMR(POA_impl root, ORBControl orbControl) {
        String serverId = orbInstance_.getServerId();
        String serverInstance = orbInstance_.getServerInstance();

        Properties properties = orbInstance_.getProperties();
        String noIMR = properties.getProperty("yoko.orb.noIMR");
        if (serverId.isEmpty() || noIMR != null) return;

        // Create the OAD::ProcessMonitor servant
        processEndpoint_ = new ProcessEndpoint_impl(serverId, serverInstance, root, orbControl);

        Domain imrDomain = null;
        try {
            InitialServiceManager initServiceManager = orbInstance_.getInitialServiceManager();
            org.omg.CORBA.Object imrObj = initServiceManager.resolveInitialReferences("IMR");
            imrDomain = DomainHelper.narrow(imrObj);
        } catch (InvalidName | BAD_PARAM ignored) {
        }


        Logger logger = orbInstance_.getLogger();

        // IMR::IMRDomain not reachable?
        if (imrDomain == null) {
            logger.severe(serverId + ": IMRDomain not reachable");
            throw new INITIALIZE(serverId + ": IMRDomain not reachable");
        }

        // Check if need to register with the IMR
        String exec = properties.getProperty("yoko.orb.imr.register");
        if (exec != null) {
            // TODO: What do we do for Java?
            logger.severe(serverId + ": Self registration not implemented for java servers");
            throw new INITIALIZE(serverId + ": Self registration not implemented for java servers");
        }

        // Tell the IMR that we're starting up
        try {
            ProcessEndpointManagerHolder endpoint = new ProcessEndpointManagerHolder();

            ObjectReferenceTemplate primary = root.adapter_template();

            // Tell the IMR that we are STARTING.
            activeState_ = imrDomain.startup(serverId, serverInstance, primary, endpoint);

            // Link with the OAD ProcessEndpoint
            ProcessEndpoint ref = processEndpoint_._this(orbInstance_.getORB());
            endpoint.value.establish_link(serverId, serverInstance, 0xFFFFFFFF, ref);
            endpointManager_ = endpoint.value;

            // Create an register the IORInterceptor for the IMR
            IORInterceptor i = new IMRIORInterceptor_impl(logger, activeState_, serverInstance);
            PIManager piManager = orbInstance_.getPIManager();

            try {
                piManager.addIORInterceptor(i, true);
            } catch (DuplicateName ex) {
                throw Assert.fail(ex);
            }
        } catch (BAD_PARAM ex) {
            logger.log(Level.SEVERE, serverId + ": (IMR) Server already running", ex);
            throw (INITIALIZE)new INITIALIZE(serverId + ": (IMR) Server already running").initCause(ex);
        } catch (NoSuchServer ex) {
            logger.log(Level.SEVERE, serverId + ": (IMR) Not registered with IMR", ex);
            throw (INITIALIZE)new INITIALIZE(serverId + ": (IMR) Not registered with IMR").initCause(ex);
        } catch (NoSuchOAD ex) {
            logger.log(Level.SEVERE, serverId + ": (IMR) No OAD for host", ex);
            throw (INITIALIZE)new INITIALIZE(serverId + ": (IMR) No OAD for host").initCause(ex);
        } catch (OADNotRunning ex) {
            logger.log(Level.SEVERE, serverId + ": (IMR) OAD not running", ex);
            throw (INITIALIZE)new INITIALIZE(serverId + ": (IMR) OAD not running").initCause(ex);
        } catch (AlreadyLinked ex) {
            logger.log(Level.SEVERE, serverId + ": (IMR) Process registered with OAD", ex);
            throw (INITIALIZE)new INITIALIZE(serverId + ": (IMR) Process registered with OAD").initCause(ex);
        }
    }
}

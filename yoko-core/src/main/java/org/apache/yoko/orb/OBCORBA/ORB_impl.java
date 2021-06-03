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

package org.apache.yoko.orb.OBCORBA;

import org.apache.yoko.orb.CORBA.Context;
import org.apache.yoko.orb.CORBA.ContextList;
import org.apache.yoko.orb.CORBA.Delegate;
import org.apache.yoko.orb.CORBA.Environment;
import org.apache.yoko.orb.CORBA.ExceptionList;
import org.apache.yoko.orb.CORBA.NamedValue;
import org.apache.yoko.orb.CORBA.ORBPolicyFactory_impl;
import org.apache.yoko.orb.CORBA.ORBPolicyManager_impl;
import org.apache.yoko.orb.CORBA.ORBSingleton;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.CORBA.PolicyMap;
import org.apache.yoko.orb.DynamicAny.DynAnyFactory_impl;
import org.apache.yoko.orb.IOP.CodecFactory_impl;
import org.apache.yoko.orb.Messaging.RebindPolicy_impl;
import org.apache.yoko.orb.Messaging.SyncScopePolicy_impl;
import org.apache.yoko.util.Assert;
import org.apache.yoko.orb.OB.BootManager_impl;
import org.apache.yoko.orb.OB.CONNECTION_REUSE_POLICY_ID;
import org.apache.yoko.orb.OB.CONNECT_TIMEOUT_POLICY_ID;
import org.apache.yoko.orb.OB.Client;
import org.apache.yoko.orb.OB.ClientManager;
import org.apache.yoko.orb.OB.CodeSetIORInterceptor_impl;
import org.apache.yoko.orb.OB.CodeSetInfo;
import org.apache.yoko.orb.OB.ConnectTimeoutPolicy_impl;
import org.apache.yoko.orb.OB.ConnectionReusePolicy_impl;
import org.apache.yoko.orb.OB.CorbalocURLScheme_impl;
import org.apache.yoko.orb.OB.CorbanameURLScheme_impl;
import org.apache.yoko.orb.OB.CoreTraceLevels;
import org.apache.yoko.orb.OB.DispatchStrategyFactory_impl;
import org.apache.yoko.orb.OB.FileURLScheme_impl;
import org.apache.yoko.orb.OB.GIOPIncomingMessage;
import org.apache.yoko.orb.OB.GIOPOutgoingMessage;
import org.apache.yoko.orb.OB.INTERCEPTOR_POLICY_ID;
import org.apache.yoko.orb.OB.IORURLScheme_impl;
import org.apache.yoko.orb.OB.InitialServiceManager;
import org.apache.yoko.orb.OB.InterceptorPolicy_impl;
import org.apache.yoko.orb.OB.LOCATION_TRANSPARENCY_POLICY_ID;
import org.apache.yoko.orb.OB.LOCATION_TRANSPARENCY_RELAXED;
import org.apache.yoko.orb.OB.LOCATION_TRANSPARENCY_STRICT;
import org.apache.yoko.orb.OB.LocateRequestPolicy_impl;
import org.apache.yoko.orb.OB.LocationTransparencyPolicy_impl;
import org.apache.yoko.orb.OB.Logger;
import org.apache.yoko.orb.OB.Logger_impl;
import org.apache.yoko.orb.OB.MessageRoutingIORInterceptor_impl;
import org.apache.yoko.orb.OB.MessageRoutingUtil;
import org.apache.yoko.util.MinorCodes;
import org.apache.yoko.orb.OB.MultiRequestSender;
import org.apache.yoko.orb.OB.ORBControl;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.ObjectFactory;
import org.apache.yoko.orb.OB.OptionFilter;
import org.apache.yoko.orb.OB.OptionFilter.Option;
import org.apache.yoko.orb.OB.PIManager;
import org.apache.yoko.orb.OB.PROTOCOL_POLICY_ID;
import org.apache.yoko.orb.OB.ParseParams;
import org.apache.yoko.orb.OB.PluginManager;
import org.apache.yoko.orb.OB.PolicyFactoryManager;
import org.apache.yoko.orb.OB.ProtocolPolicy_impl;
import org.apache.yoko.orb.OB.REQUEST_TIMEOUT_POLICY_ID;
import org.apache.yoko.orb.OB.RETRY_ALWAYS;
import org.apache.yoko.orb.OB.RETRY_NEVER;
import org.apache.yoko.orb.OB.RETRY_POLICY_ID;
import org.apache.yoko.orb.OB.RETRY_STRICT;
import org.apache.yoko.orb.OB.ReplyTimeoutPolicy_impl;
import org.apache.yoko.orb.OB.RequestTimeoutPolicy_impl;
import org.apache.yoko.orb.OB.RetryPolicy_impl;
import org.apache.yoko.orb.OB.TIMEOUT_POLICY_ID;
import org.apache.yoko.orb.OB.TimeoutPolicy_impl;
import org.apache.yoko.orb.OB.URLRegistryPackage.SchemeAlreadyExists;
import org.apache.yoko.orb.OB.URLRegistry_impl;
import org.apache.yoko.orb.OB.UnknownExceptionStrategy;
import org.apache.yoko.orb.OB.UnknownExceptionStrategy_impl;
import org.apache.yoko.orb.OB.ValueFactoryManager;
import org.apache.yoko.orb.OB.Version;
import org.apache.yoko.orb.OB.ZERO_PORT_POLICY_ID;
import org.apache.yoko.orb.OB.ZeroPortPolicy_impl;
import org.apache.yoko.orb.OBMessageRouting.DecayPolicyFactory_impl;
import org.apache.yoko.orb.OBMessageRouting.ImmediateSuspendPolicyFactory_impl;
import org.apache.yoko.orb.OBMessageRouting.LimitedPingPolicyFactory_impl;
import org.apache.yoko.orb.OBMessageRouting.ResumePolicyFactory_impl;
import org.apache.yoko.orb.OBMessageRouting.UnlimitedPingPolicyFactory_impl;
import org.apache.yoko.orb.OBMessaging.ExceptionHolderFactory_impl;
import org.apache.yoko.orb.OBPortableInterceptor.IMRORTFactory_impl;
import org.apache.yoko.orb.OBPortableInterceptor.ORBInitInfo_impl;
import org.apache.yoko.orb.OBPortableInterceptor.PersistentORTFactory_impl;
import org.apache.yoko.orb.OBPortableInterceptor.TransientORTFactory_impl;
import org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl;
import org.apache.yoko.orb.OCI.AccFactoryRegistry;
import org.apache.yoko.orb.OCI.AccFactoryRegistry_impl;
import org.apache.yoko.orb.OCI.ConFactoryRegistry;
import org.apache.yoko.orb.OCI.ConFactoryRegistry_impl;
import org.apache.yoko.orb.OCI.Plugin;
import org.apache.yoko.orb.cmsf.CmsfClientInterceptor;
import org.apache.yoko.orb.cmsf.CmsfIORInterceptor;
import org.apache.yoko.orb.cmsf.CmsfServerInterceptor;
import org.apache.yoko.orb.yasf.YasfClientInterceptor;
import org.apache.yoko.orb.yasf.YasfIORInterceptor;
import org.apache.yoko.orb.yasf.YasfServerInterceptor;
import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.util.PrivilegedActions;
import org.apache.yoko.util.concurrent.AutoLock;
import org.apache.yoko.util.concurrent.AutoReadWriteLock;
import org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE;
import org.omg.CORBA.ARG_IN;
import org.omg.CORBA.ARG_INOUT;
import org.omg.CORBA.ARG_OUT;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Contained;
import org.omg.CORBA.ContainedPackage.Description;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NVList;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.OperationDef;
import org.omg.CORBA.OperationDefHelper;
import org.omg.CORBA.OperationDescription;
import org.omg.CORBA.OperationDescriptionHelper;
import org.omg.CORBA.ParameterDescription;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.PolicyManager;
import org.omg.CORBA.Repository;
import org.omg.CORBA.RepositoryHelper;
import org.omg.CORBA.Request;
import org.omg.CORBA.ServiceDetail;
import org.omg.CORBA.ServiceInformation;
import org.omg.CORBA.ServiceInformationHolder;
import org.omg.CORBA.StringSeqHolder;
import org.omg.CORBA.ValueDefHelper;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ValueFactory;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;
import org.omg.IOP.TaggedProfile;
import org.omg.MessageRouting.Router;
import org.omg.MessageRouting.RouterListHolder;
import org.omg.Messaging.MAX_HOPS_POLICY_TYPE;
import org.omg.Messaging.NO_REBIND;
import org.omg.Messaging.NO_RECONNECT;
import org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE;
import org.omg.Messaging.REBIND_POLICY_TYPE;
import org.omg.Messaging.RELATIVE_REQ_TIMEOUT_POLICY_TYPE;
import org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE;
import org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE;
import org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE;
import org.omg.Messaging.REPLY_START_TIME_POLICY_TYPE;
import org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE;
import org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE;
import org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE;
import org.omg.Messaging.ROUTING_POLICY_TYPE;
import org.omg.Messaging.SYNC_NONE;
import org.omg.Messaging.SYNC_SCOPE_POLICY_TYPE;
import org.omg.Messaging.SYNC_WITH_SERVER;
import org.omg.Messaging.SYNC_WITH_TARGET;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.omg.Messaging.TRANSPARENT;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableServer.Servant;

import java.applet.Applet;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;

import static java.security.AccessController.doPrivileged;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_LATIN_1;
import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_16;
import static org.apache.yoko.orb.OB.CodeSetInfo.UTF_8;
import static org.apache.yoko.util.PrivilegedActions.GET_SYSPROPS_OR_EMPTY_MAP;

// This class must be public and not final
public class ORB_impl extends ORBSingleton {
    // All registered ORBInitializers
    private final Map<String, ORBInitializer> initializers = new Hashtable<>();

    // The ORB Control
    private ORBControl orbControl_;

    // Has the ORB been destroyed?
    private final AutoReadWriteLock destroyLock_ = new AutoReadWriteLock();
    private boolean destroy_;
    // The OCI Plugin Manager
    private PluginManager pluginManager_;

    // The ORBInstance object
    private ORBInstance orbInstance_;

    // Default set of policies
    private final PolicyMap policies = new PolicyMap();

    // The ORB option filter
    private static OptionFilter orbOptionFilter_;

    // The OA option filter
    private static OptionFilter oaOptionFilter_;

    // Whether DII operations should raise system exceptions
    private boolean raiseDIIExceptions_ = true;

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    // This method does most of the ORB initialization that would
    // normally be done in the constructor. However, the constructor
    // cannot perform these steps because some of the initialization
    // requires that the command-line options and properties have
    // already been processed, which doesn't occur until set_parameters
    // is called.
    private void initialize(StringSeqHolder args, String orbId,
                            String serverId, String serverInstance, int concModel,
                            CoreTraceLevels coreTraceLevels,
                            Properties properties,
                            Logger logger, int nativeCs, int nativeWcs,
                            int defaultWcs) {
        try (AutoLock writeLock = destroyLock_.getWriteLock()) {
            destroy_ = false;

            // Create the ORBControl
            orbControl_ = new ORBControl();

            // Create the OCI Plugin Manager
            pluginManager_ = new PluginManager(this);

            // Create the ORBInstance object
            InitialServiceManager initServiceManager = new InitialServiceManager();
            ClientManager clientManager = new ClientManager(concModel);
            ObjectFactory objectFactory = new ObjectFactory();
            PolicyFactoryManager pfManager = new PolicyFactoryManager();
            PIManager piManager = new PIManager(this);
            ValueFactoryManager valueFactoryManager = new ValueFactoryManager();
            CodecFactory_impl codecFactory = new CodecFactory_impl();
            POAManagerFactory_impl pmFactory = new POAManagerFactory_impl();
            MultiRequestSender multiRequestSender = new MultiRequestSender();
            DispatchStrategyFactory_impl dsf = new DispatchStrategyFactory_impl();
            BootManager_impl bootManager = new BootManager_impl(this);
            ConFactoryRegistry conFactoryRegistry = new ConFactoryRegistry_impl();
            AccFactoryRegistry accFactoryRegistry = new AccFactoryRegistry_impl();
            UnknownExceptionStrategy unknownExceptionStrategy = new UnknownExceptionStrategy_impl();
            URLRegistry_impl urlRegistry = new URLRegistry_impl();

            orbInstance_ = new ORBInstance(this, orbId,
                    serverId, serverInstance, objectFactory, clientManager,
                    pfManager, piManager, initServiceManager,
                    valueFactoryManager, codecFactory, pmFactory,
                    multiRequestSender, properties, dsf, bootManager, logger,
                    coreTraceLevels, conFactoryRegistry, accFactoryRegistry,
                    unknownExceptionStrategy, urlRegistry, nativeCs, nativeWcs,
                    defaultWcs);

            objectFactory.setORBInstance(orbInstance_);
            initServiceManager.setORBInstance(orbInstance_);
            pmFactory._OB_setORBInstance(orbInstance_);
            piManager.setORBInstance(orbInstance_);
            codecFactory._OB_setORBInstance(orbInstance_);
            clientManager.setORBInstance(orbInstance_);
            dsf._OB_setORBInstance(orbInstance_);
            try {
                urlRegistry.add_scheme(new IORURLScheme_impl(orbInstance_));
                urlRegistry.add_scheme(new FileURLScheme_impl(false, urlRegistry));
                urlRegistry.add_scheme(new FileURLScheme_impl(true, urlRegistry));
                urlRegistry.add_scheme(new CorbalocURLScheme_impl(orbInstance_));
                urlRegistry.add_scheme(new CorbanameURLScheme_impl(this, urlRegistry));
            } catch (SchemeAlreadyExists ex) {
                throw Assert.fail(ex);
            }

            // Set the ORBInstance on the ORBControl
            orbControl_.setORBInstance(orbInstance_);

            // Initialize default policies
            initializeDefaultPolicies();

            // Create the DynamicAny::DynAnyFactory object
            DynAnyFactory dynAnyFactory = new DynAnyFactory_impl(
                    orbInstance_);

            // Add initial references
            try {
                initServiceManager.addInitialReference("POAManagerFactory", pmFactory);
                initServiceManager.addInitialReference("DynAnyFactory", dynAnyFactory);
                initServiceManager.addInitialReference("CodecFactory", codecFactory);
                initServiceManager.addInitialReference("DispatchStrategyFactory", dsf);
                initServiceManager.addInitialReference("BootManager", bootManager);
                initServiceManager.addInitialReference("RootPOA", null); // Dummy
                initServiceManager.addInitialReference("OCIConFactoryRegistry", conFactoryRegistry);
                initServiceManager.addInitialReference("OCIAccFactoryRegistry", accFactoryRegistry);
                initServiceManager.addInitialReference("URLRegistry", urlRegistry);
            } catch (InvalidName ex) {
                throw Assert.fail(ex);
            }


            { // Initialize the OCI client plug-ins
                String prop = properties.getProperty("yoko.oci.client", "iiop");
                int pos = 0;
                while (pos != -1) {
                    List<String> paramList = new ArrayList<>();
                    pos = ParseParams.parse(prop, pos, paramList);
                    String name = paramList.remove(0);
                    String[] params = paramList.toArray(new String[0]);

                    Plugin plugin = pluginManager_.initPlugin(name, args);
                    if (plugin == null) throw new INITIALIZE("OCI client initialization failed for '" + name + "'");
                    plugin.init_client(params);
                }
            }

            { // Initialize the OCI server plug-ins
                String prop = properties.getProperty("yoko.oci.server", "iiop");
                int pos = 0;
                while (pos != -1) {
                    List<String> paramList = new ArrayList<>();
                    pos = ParseParams.parse(prop, pos, paramList);
                    String name = paramList.remove(0);
                    String[] params = paramList.toArray(new String[0]);

                    Plugin plugin = pluginManager_.initPlugin(name, args);
                    if (plugin == null) {
                        String err = "OCI server initialization failed for '" + name + "'";
                        throw new INITIALIZE(err);
                    } else
                        plugin.init_server(params);
                }
            }

            // Initialize Portable Interceptors - this must be done after
            // installing the OCI plug-ins to allow an ORBInitializer
            // or interceptor to make a remote invocation

            // Install IOR interceptor for code sets
            try {
                piManager.addIORInterceptor(new CodeSetIORInterceptor_impl(nativeCs, nativeWcs), false);
            } catch (DuplicateName ex) {
                throw Assert.fail(ex);
            }
            
            // Install interceptors for Yoko Auxilliary Stream Format
            try {
                piManager.addIORInterceptor(new YasfIORInterceptor(), true);
                piManager.addClientRequestInterceptor(new YasfClientInterceptor());
                piManager.addServerRequestInterceptor(new YasfServerInterceptor(piManager.allocateSlotId()));
            } catch (DuplicateName ex) {
                throw Assert.fail(ex);
            }

            // Install interceptors for Custom Marshal Stream Format negotiation
            try {
                piManager.addIORInterceptor(new CmsfIORInterceptor(), false);
                piManager.addClientRequestInterceptor(new CmsfClientInterceptor());
                piManager.addServerRequestInterceptor(new CmsfServerInterceptor(piManager.allocateSlotId()));
            } catch (DuplicateName ex) {
                throw Assert.fail(ex);
            }

            // Install IOR interceptor for Message Routing
            try {
                // Get the router list from configuration data
                RouterListHolder routerListHolder = new RouterListHolder();
                routerListHolder.value = new Router[0];

                MessageRoutingUtil.getRouterListFromConfig(orbInstance_, routerListHolder);
                piManager.addIORInterceptor(new MessageRoutingIORInterceptor_impl(routerListHolder.value), false);
            } catch (DuplicateName ex) {
                throw Assert.fail(ex);
            }

            // Register the valuetype factory for ExceptionHolders
            ValueFactory exhFactory = new ExceptionHolderFactory_impl();
            valueFactoryManager.registerValueFactory("IDL:omg.org/Messaging/ExceptionHolder:1.0", exhFactory);

            // Register the appropriate router admin factory for each router admin policy.
            ValueFactory routerAdminPolicyFactory = new ImmediateSuspendPolicyFactory_impl();
            valueFactoryManager.registerValueFactory("IDL:omg.org/MessageRouting/ImmediateSuspendPolicy:1.0", routerAdminPolicyFactory);

            routerAdminPolicyFactory = new UnlimitedPingPolicyFactory_impl();
            valueFactoryManager.registerValueFactory("IDL:omg.org/MessageRouting/UnlimitedPingPolicy:1.0", routerAdminPolicyFactory);

            routerAdminPolicyFactory = new LimitedPingPolicyFactory_impl();
            valueFactoryManager.registerValueFactory("IDL:omg.org/MessageRouting/LimitedPingPolicy:1.0", routerAdminPolicyFactory);

            routerAdminPolicyFactory = new DecayPolicyFactory_impl();
            valueFactoryManager.registerValueFactory("IDL:omg.org/MessageRouting/DecayPolicy:1.0", routerAdminPolicyFactory);

            routerAdminPolicyFactory = new ResumePolicyFactory_impl();
            valueFactoryManager.registerValueFactory("IDL:omg.org/MessageRouting/ResumePolicy:1.0", routerAdminPolicyFactory);

            // Register the valuetype factory for the persistent POA Object
            // Reference Template and the IMR Object Reference Template.
            ValueFactory ortFactory = new TransientORTFactory_impl(orbInstance_);
            valueFactoryManager.registerValueFactory("IDL:orb.yoko.apache.org/OBPortableInterceptor/TransientORT:1.0", ortFactory);

            ortFactory = new PersistentORTFactory_impl(orbInstance_);
            valueFactoryManager.registerValueFactory("IDL:orb.yoko.apache.org/OBPortableInterceptor/PersistentORT:1.0", ortFactory);

            ortFactory = new IMRORTFactory_impl();
            valueFactoryManager.registerValueFactory("IDL:orb.yoko.apache.org/OBPortableInterceptor/IMRORT:1.0", ortFactory);

            // Instantiate ORB initializers using the properties given
            // to ORB.init()
            instantiateORBInitializers(properties, logger);

            // Instantiate ORB initializers using the System properties.
            // Note that a SecurityException may be raised for applets.
            try {
                Properties sysProperties = System.getProperties();
                instantiateORBInitializers(sysProperties, logger);
            } catch (SecurityException ignored) { }

            // Call each of the ORB initializers. If there are no ORB
            // initializers it's not necessary to setup the PIManager
            // since no interceptors will be called.
            if (!initializers.isEmpty()) {
                ORBInitInfo_impl info = new ORBInitInfo_impl(this, args.value, orbId, piManager, initServiceManager, codecFactory);

                for (ORBInitializer initializer: initializers.values())
                    initializer.pre_init(info);

                // TODO: change state
                for (ORBInitializer initializer: initializers.values())
                    initializer.post_init(info);

                info._OB_destroy();
            }
            piManager.setupComplete();
        } catch (RuntimeException ex) {
            // Here the same thing as ORB::destroy must be done since
            // although the ORB itself isn't fully initialized all of the
            // ORB components may be.
            if (orbControl_ != null) {
                orbControl_.shutdownServerClient();
                orbControl_.destroy();
            }
            if (pluginManager_ != null) {
                pluginManager_.destroy();
            }
            if (orbInstance_ != null) {
                orbInstance_.destroy();
                orbInstance_ = null;
            }
            throw ex;
        }
    }

    protected void finalize() throws Throwable {
        if (orbInstance_ != null) {
            Logger logger = orbInstance_.getLogger();
            logger.fine("ORB.destroy() was not called. This may result in resource leaks.");
        }
        super.finalize();
    }

    private void initializeDefaultPolicies() {
        Logger logger = orbInstance_.getLogger();
        Properties properties = orbInstance_.getProperties();

        for (String key: properties.stringPropertyNames()) {
            if (!key.startsWith("yoko.orb.policy.")) continue;
            String value = properties.getProperty(key);

            switch (key) {
            case "yoko.orb.policy.protocol": {
                StringTokenizer tokenizer = new StringTokenizer(value, ", ");
                String[] seq = new String[tokenizer.countTokens()];
                int n = 0;
                while (tokenizer.hasMoreTokens()) seq[n++] = tokenizer.nextToken();

                if (seq.length == 0) {
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.protocol: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new ProtocolPolicy_impl(seq));
                break;
            }
            case "yoko.orb.policy.connection_reuse": {
                boolean b;
                if (value.equals("true")) b = true;
                else if (value.equals("false")) b = false;
                else {
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.connection_reuse: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new ConnectionReusePolicy_impl(b));
                break;
            }
            case "yoko.orb.policy.zero_port": {
                boolean b;
                if (value.equals("true")) b = true;
                else if (value.equals("false")) b = false;
                else {
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.zero_port: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new ZeroPortPolicy_impl(b));
                break;
            }
            case "yoko.orb.policy.retry":
            case "yoko.orb.policy.retry.max":
            case "yoko.orb.policy.retry.interval":
            case "yoko.orb.policy.retry.remote":
                // Ignore here
                break;
            case "yoko.orb.policy.timeout": {
                int val = Integer.parseInt(value);
                if (val != -1) {
                    policies.add(new TimeoutPolicy_impl(val));
                }
                break;
            }
            case "yoko.orb.policy.location_transparency": {
                short val;
                if (value.equals("strict")) val = LOCATION_TRANSPARENCY_STRICT.value;
                else if (value.equals("relaxed")) val = LOCATION_TRANSPARENCY_RELAXED.value;
                else {
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.location_transparency: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new LocationTransparencyPolicy_impl(val));
                break;
            }
            case "yoko.orb.policy.interceptor": {
                boolean b;
                if (value.equals("true")) b = true;
                else if (value.equals("false")) b = false;
                else {
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.interceptor: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new InterceptorPolicy_impl(b));
                break;
            }
            case "yoko.orb.policy.connect_timeout": {
                int val = Integer.parseInt(value);
                if (val != -1) {
                    policies.add(new ConnectTimeoutPolicy_impl(val));
                }
                break;
            }
            case "yoko.orb.policy.request_timeout": {
                int val = Integer.parseInt(value);
                if (val != -1) {
                    policies.add(new RequestTimeoutPolicy_impl(val));
                }
                break;
            }
            case "yoko.orb.policy.reply_timeout": {
                int val = Integer.parseInt(value);
                if (val != -1) {
                    policies.add(new ReplyTimeoutPolicy_impl(val));
                }
                break;
            }
            case "yoko.orb.policy.locate_request": {
                boolean b;
                if (value.equals("true")) b = true;
                else if (value.equals("false")) b = false;
                else {
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.locate_request: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new LocateRequestPolicy_impl(b));
                break;
            }
            case "yoko.orb.policy.rebind": {
                short val;
                switch (value) {
                case "transparent":
                    val = TRANSPARENT.value;
                    break;
                case "no_rebind":
                    val = NO_REBIND.value;
                    break;
                case "no_reconnect":
                    val = NO_RECONNECT.value;
                    break;
                default:
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.rebind: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new RebindPolicy_impl(val));
                break;
            }
            case "yoko.orb.policy.sync_scope": {
                short val;
                switch (value) {
                case "none":
                    val = SYNC_NONE.value;
                    break;
                case "transport":
                    val = SYNC_WITH_TRANSPORT.value;
                    break;
                case "server":
                    val = SYNC_WITH_SERVER.value;
                    break;
                case "target":
                    val = SYNC_WITH_TARGET.value;
                    break;
                default:
                    String err = "ORB.init: invalid value for " + "yoko.orb.policy.sync_scope: `" + value + "'";
                    logger.severe(err);
                    throw new INITIALIZE(err);
                }

                policies.add(new SyncScopePolicy_impl(val));
                break;
            }
            /*
             * TODO - Add the config keys for the new Message routing policies
             * here...
             *
             * else if(key.equals("yoko.orb.policy.max_hops")) {
             *  } else if(key.equals("yoko.orb.policy.queue_order")) {
             *  }
             */
            default:
                String err = "ORB.init: unknown property `" + key + "'";
                logger.severe(err);
                throw new INITIALIZE(err);
            }
        }

        // Set the default policies, if not already set
        if (properties.getProperty("yoko.orb.policy.connection_reuse") == null) {
            policies.add(new ConnectionReusePolicy_impl(true));
        }

        // Set the retry policy
        short retry_mode = RETRY_STRICT.value;
        int retry_interval = 0;
        int max_retries = 1;
        boolean retry_remote = false;

        String value;
        if ((value = properties.getProperty("yoko.orb.policy.retry")) != null) {
            switch (value) {
            case "never":
                retry_mode = RETRY_NEVER.value;
                break;
            case "strict":
                retry_mode = RETRY_STRICT.value;
                break;
            case "always":
                retry_mode = RETRY_ALWAYS.value;
                break;
            default:
                String err = "ORB.init: invalid value for " + "yoko.orb.policy.retry: `" + value + "'";
                logger.severe(err);
                throw new INITIALIZE(err);
            }
        }
        if ((value = properties.getProperty("yoko.orb.policy.retry.interval")) != null) {
            try {
                retry_interval = Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                String err = "ORB.init: invalid value for yoko.orb.policy.retry.interval: `" + value + "'";
                logger.log(Level.SEVERE, err, ex);
                throw new INITIALIZE(err);
            }
        }
        if ((value = properties.getProperty("yoko.orb.policy.retry.max")) != null) {
            try {
                max_retries = Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                String err = "ORB.init: invalid value for "
                        + "yoko.orb.policy.retry.max: `" + value + "'";
                logger.log(Level.SEVERE, err, ex);
                throw new INITIALIZE(err);
            }
        }
        if ((value = properties.getProperty("yoko.orb.policy.retry.remote")) != null)
            retry_remote = value.equals("true");

        policies.add(new RetryPolicy_impl(retry_mode, retry_interval, max_retries, retry_remote));

        // Create the ORBPolicyManager
        PolicyManager pm = new ORBPolicyManager_impl(policies);
        InitialServiceManager initServiceManager = orbInstance_.getInitialServiceManager();
        try {
            initServiceManager.addInitialReference("ORBPolicyManager", pm);
        } catch (InvalidName ex) {
            throw Assert.fail(ex);
        }
        ObjectFactory objectFactory = orbInstance_.getObjectFactory();
        objectFactory.setPolicyManager(pm);

        // Register the default PolicyFactory policies for the ORB
        PolicyFactoryManager pfm = orbInstance_.getPolicyFactoryManager();

        PolicyFactory factory = new ORBPolicyFactory_impl();
        pfm.registerPolicyFactory(CONNECTION_REUSE_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(ZERO_PORT_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(PROTOCOL_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(RETRY_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(TIMEOUT_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(LOCATION_TRANSPARENCY_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(REBIND_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(SYNC_SCOPE_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(INTERCEPTOR_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(CONNECT_TIMEOUT_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(REQUEST_TIMEOUT_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(REQUEST_START_TIME_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(REQUEST_END_TIME_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(REPLY_START_TIME_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(REPLY_END_TIME_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(RELATIVE_REQ_TIMEOUT_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(RELATIVE_RT_TIMEOUT_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(BIDIRECTIONAL_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(REQUEST_PRIORITY_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(REPLY_PRIORITY_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(ROUTING_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(MAX_HOPS_POLICY_TYPE.value, factory, true);
        pfm.registerPolicyFactory(QUEUE_ORDER_POLICY_TYPE.value, factory, true);
    }

    private void instantiateORBInitializers(Properties properties, Logger logger) {
        final String magic = "org.omg.PortableInterceptor.ORBInitializerClass.";

        for (String key: properties.stringPropertyNames()) {
            if (key.startsWith(magic)) {
                // The remaining portion of the key is the initializer
                // class name. The value of the property is ignored.
                String className = key.substring(magic.length());
                if (!initializers.containsKey(className)) {
                    try {
                        // get the appropriate class for the loading.
                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        final Class<?> initClass = ProviderLocator.loadClass(className, getClass(), loader);
                        initializers.put(className, (ORBInitializer) initClass.newInstance());
                    }
                    // Exceptions have to be ignored here
                    catch (ClassNotFoundException ex) {
                        logger.log(Level.WARNING, "ORB.init: initializer class " + className + " not found", ex);
                    } catch (InstantiationException ex) {
                        logger.log(Level.WARNING, "ORB.init: error occurred while instantiating initializer class " + className, ex);
                    } catch (IllegalAccessException ex) {
                        logger.log(Level.WARNING, "ORB.init: cannot access initializer class " + className, ex);
                    }
                }
            }
        }
    }

    private static boolean loadConfigFile(String configFile, Properties properties, Logger logger) {
        try {
            InputStream in;
            try {
                // Try to open URL connection first
                URL url = new URL(configFile);
                in = url.openStream();
            } catch (MalformedURLException e) {
                // Try to open plain file, if `configFile' is not a URL specification
                in = new FileInputStream(configFile);
            }
            try (BufferedInputStream bin = new BufferedInputStream(in)) {
                properties.load(bin);
                return true;
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, "ORB.init: could not load configuration file " + configFile, ex);
            return false;
        }
    }

    private static String[] parseAppletParams(Applet app) {
        String[] args = new String[0];

        // Check for parameter list
        String paramList = app.getParameter("ORBparams");
        if (paramList != null) {
            StringTokenizer p = new StringTokenizer(paramList);
            args = new String[p.countTokens()];
            int i = 0;
            while (p.hasMoreTokens()) args[i++] = p.nextToken();
        }

        return args;
    }

    private void setParameters(StringSeqHolder args, final Properties initialProps, Logger logger) {
        if (args.value == null) args.value = new String[0];

        // Initialize the Logger
        if (logger == null) logger = new Logger_impl();

        // Initialize the properties - make a local copy to avoid modifying the original
        final Properties properties = new Properties();
        properties.putAll(initialProps == null ? doPrivileged(GET_SYSPROPS_OR_EMPTY_MAP) : initialProps);

        args.value = ParseArgs(args.value, properties, logger);

        // Process each property
        String orbId = "";
        String serverId = "";
        String serverInstance = "";
        int concModel = Client.Blocking;
        int nativeCs = ISO_LATIN_1.id;
        int nativeWcs = UTF_16.id;
        int defaultWcs = 0;

        for (String key: properties.stringPropertyNames()) {
            if (!key.startsWith("yoko.orb.")) continue;

            String value = properties.getProperty(key);
            Assert.ensure(value != null);

            if (key.equals("yoko.orb.conc_model")) {
                if (value.equals("threaded")) {
                    concModel = Client.Threaded;
                }
                else {
                    logger.warning("ORB.init: unknown value for yoko.orb.conc_model: " + value);
                }
            } else if (key.startsWith("yoko.orb.trace.")) {
                // Ignore -- handled in CoreTraceLevels
            } else if (key.startsWith("yoko.orb.policy.")) {
                // Ignore -- handled in initializeDefaultPolicies()
            } else if (key.equals("yoko.orb.id")) {
                orbId = value;
            } else if (key.equals("yoko.orb.server_name")) {
                // The server name must begin with an alpha-numeric character
                if (value.length() == 0 || !Character.isLetterOrDigit(value.charAt(0))) {
                    logger.severe("ORB.init: illegal value for yoko.orb.server_name: " + value);
                    throw new INITIALIZE("ORB.init: illegal value for yoko.orb.server_name: " + value);
                }
                serverId = value;
            } else if (key.equals("yoko.orb.server_instance")) {
                serverInstance = value;
            } else if (key.equals("yoko.orb.raise_dii_exceptions")) {
                if (value.equalsIgnoreCase("true"))
                    raiseDIIExceptions_ = true;
                else
                    raiseDIIExceptions_ = false;
            } else if (key.equals("yoko.orb.native_cs")) {
                int csid = CodeSetInfo
                        .getRegistryIdForName(value);
                if (csid != 0 && csid != UTF_8.id)
                    nativeCs = csid;
                else {
                    logger.severe("ORB.init: unknown value for yoko.orb.native_cs: " + value);
                    throw new INITIALIZE("ORB.init: unknown value for yoko.orb.native_cs: " + value);
                }
            } else if (key.equals("yoko.orb.native_wcs")) {
                int csid = CodeSetInfo.getRegistryIdForName(value);
                if (csid != 0 && csid != UTF_8.id)
                    nativeWcs = csid;
                else {
                    logger.severe("ORB.init: unknown value for yoko.orb.native_wcs: " + value);
                    throw new INITIALIZE("ORB.init: unknown value for yoko.orb.native_wcs: " + value);
                }
            } else if (key.equals("yoko.orb.default_wcs")) {
                int csid = CodeSetInfo
                        .getRegistryIdForName(value);
                if (csid != 0 && csid != UTF_8.id)
                    defaultWcs = csid;
                else {
                    logger.severe("ORB.init: unknown value for yoko.orb.default_wcs: " + value);
                    throw new INITIALIZE("ORB.init: unknown value for yoko.orb.default_wcs: " + value);
                }
            } else if (key.equals("yoko.orb.extended_wchar")) {
                if (!value.equals("true") && !value.equals("false")) {
                    logger.severe("ORB.init: unknown value for yoko.orb.extended_wchar: " + value);
                    throw new INITIALIZE("ORB.init: unknown value for yoko.orb.extended_wchar: " + value);
                }
            } else if (key.equals("yoko.orb.default_init_ref")) {
                if (value.length() == 0)
                    logger.warning("ORB.init: invalid value for yoko.orb.default_init_ref");
            } else if (key.equals("yoko.orb.server_timeout") || key.equals("yoko.orb.server_shutdown_timeout")) {
                // Used by GIOPServerWorker
            } else if (key.equals("yoko.orb.client_timeout") || key.equals("yoko.orb.client_shutdown_timeout")) {
                // Used by GIOPClientWorker
            } else if (key.startsWith("yoko.orb.service.")) {
                // Ignore
            } else if (key.startsWith("yoko.orb.oa.")) {
                // Ignore
            } else if (key.startsWith("yoko.orb.poamanager.")) {
                // Ignore
            } else if (key.equals("yoko.orb.noIMR")) {
                // Ignore
            } else if (key.equals("yoko.orb.use_type_code_cache")) {
                if (!value.equals("true") && !value.equals("false")) {
                    logger.severe("ORB.init: unknown value for yoko.orb.use_type_code_cache: " + value);
                    throw new INITIALIZE("ORB.init: unknown value for yoko.orb.use_type_code_cache: " + value);
                }
            } else if (key.equals("yoko.orb.giop.max_message_size")) {
                try {
                    int max = Integer.parseInt(value);
                    GIOPIncomingMessage.setMaxMessageSize(max);
                    GIOPOutgoingMessage.setMaxMessageSize(max);
                } catch (NumberFormatException ex) {
                    logger.log(Level.SEVERE, "ORB.init: invalid value for yoko.orb.giop.max_message_size: " + value, ex);
                    throw new INITIALIZE("ORB.init: invalid value for yoko.orb.giop.max_message_size: " + value);
                }
            } else if (key.equals("yoko.orb.ami_workers")) {
                // ignore
            } else {
                logger.warning("ORB.init: unknown property `" + key + "'");
            }
        }

        // Parse the tracing levels from the properties
        CoreTraceLevels coreTraceLevels = new CoreTraceLevels(logger, properties);

        // Initialize the ORB state - this must be done after processing
        // command-line options and properties
        initialize(args, orbId, serverId, serverInstance, concModel,
                coreTraceLevels, properties, logger, nativeCs, nativeWcs,
                defaultWcs);
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String[] list_initial_services() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return orbInstance_.getInitialServiceManager().listInitialServices();
        }
    }

    public org.omg.CORBA.Object resolve_initial_references(String identifier) throws InvalidName {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            InitialServiceManager initServiceManager = orbInstance_.getInitialServiceManager();

            try {
                return initServiceManager.resolveInitialReferences(identifier);
            } catch (InvalidName ex) {
                // If the service is the RootPOA and it hasn't yet been
                // initialized, create it. We could put in some automatic method
                // here for late binding of objects at some later point.
                if (identifier.equals("RootPOA")) {
                    orbControl_.initializeRootPOA(this);
                    return resolve_initial_references(identifier);
                } else {
                    throw ex;
                }
            }
        }
    }

    public void register_initial_reference(String name, org.omg.CORBA.Object obj) throws InvalidName {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            orbInstance_.getInitialServiceManager().addInitialReference(name, obj);
        }
    }

    public String object_to_string(org.omg.CORBA.Object p) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");

            IOR ior;

            if (p == null) {
                ior = new IOR("", new TaggedProfile[0]);
            } else {
                if (p instanceof LocalObject)
                    throw new MARSHAL(
                            MinorCodes
                            .describeMarshal(MinorCodes.MinorLocalObject),
                            MinorCodes.MinorLocalObject,
                            CompletionStatus.COMPLETED_NO);

                Delegate delegate = (Delegate) ((ObjectImpl) p)._get_delegate();
                ior = delegate._OB_origIOR();
            }

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                IORHelper.write(out, ior);

                return "IOR:" + out.writtenBytesToAscii();
            }
        }
    }

    public org.omg.CORBA.Object string_to_object(String ior) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");

            return orbInstance_.getObjectFactory().stringToObject(ior);
        }
    }

    public NVList create_list(int count) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {

            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");

            if (count < 0) count = 0;

            return new org.apache.yoko.orb.CORBA.NVList(this, count);
        }
    }

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public NVList create_operation_list(OperationDef oper) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");

            // Get operation description
            Description d = oper.describe();
            OperationDescription desc = OperationDescriptionHelper.extract(d.value);

            // Create list
            org.apache.yoko.orb.CORBA.NVList list = new org.apache.yoko.orb.CORBA.NVList(this);
            for (int i = 0; i < desc.parameters.length; i++) {
                ParameterDescription par = desc.parameters[i];

                Any any = create_any();
                any.type(par.type);

                int flags = 0;
                switch (par.mode.value()) {
                    case ParameterMode._PARAM_IN:
                        flags = ARG_IN.value;
                        break;

                    case ParameterMode._PARAM_OUT:
                        flags = ARG_OUT.value;
                        break;

                    case ParameterMode._PARAM_INOUT:
                        flags = ARG_INOUT.value;
                        break;

                    default:
                        throw Assert.fail();
                }

                list.add_value(par.name, any, flags);
            }

            return list;
        }
    }

    public  NVList create_operation_list(org.omg.CORBA.Object oper) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            OperationDef def = OperationDefHelper.narrow(oper);
            return create_operation_list(def);
        }
    }

    public org.omg.CORBA.NamedValue create_named_value(String name, Any value, int flags) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return new NamedValue(name, value, flags);
        }
    }

    public org.omg.CORBA.ExceptionList create_exception_list() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return new ExceptionList();
        }
    }

    public org.omg.CORBA.ContextList create_context_list() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return new ContextList();
        }
    }

    public org.omg.CORBA.Context get_default_context() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return new Context(this, "");
        }
    }

    public org.omg.CORBA.Environment create_environment() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return new Environment();
        }
    }

    public void send_multiple_requests_oneway(Request[] requests) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            MultiRequestSender multi = orbInstance_.getMultiRequestSender();
            multi.sendMultipleRequestsOneway(requests);
        }
    }

    public void send_multiple_requests_deferred(Request[] requests) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            MultiRequestSender multi = orbInstance_.getMultiRequestSender();
            multi.sendMultipleRequestsDeferred(requests);
        }
    }

    public boolean poll_next_response() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            MultiRequestSender multi = orbInstance_.getMultiRequestSender();
            return multi.pollNextResponse();
        }
    }

    public Request get_next_response() throws WrongTransaction {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            MultiRequestSender multi = orbInstance_.getMultiRequestSender();
            return multi.getNextResponse();
        }
    }

    public boolean get_service_information(short service_type, ServiceInformationHolder service_info) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            service_info.value = new ServiceInformation();
            service_info.value.service_options = new int[0];
            service_info.value.service_details = new ServiceDetail[0];
            return false;
        }
    }

    public boolean work_pending() {
        // Ensure that the ORB mutex is not locked during the call to
        // ORBControl methods
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            // Ensure that other threads get a chance to execute if
            // work_pending() is being called in a tight loop.
            Thread.yield();
            return orbControl_.workPending();
        }
    }

    public void perform_work() {
        // Ensure that the ORB mutex is not locked during the call to
        // ORBControl methods
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            orbControl_.performWork();
        }
    }

    public void run() {
        // Ensure that the ORB mutex is not locked during the call to
        // ORBControl methods
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            orbControl_.run();
        }
    }

    public void shutdown(boolean wait_for_completion) {
        // Ensure that the ORB mutex is not locked during the call to
        // ORBControl methods
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            orbControl_.shutdownServer(wait_for_completion);
        }
    }

    /**
     * Destroys the ORB.
     * <p>
     * From the specification:
     * <p>
     * This operation destroys the ORB so that its resources can be
     * reclaimed by the application. Any operation invoked on a
     * destroyed ORB reference will raise the OBJECT_NOT_EXIST
     * exception. Once an ORB has been destroyed, another call to
     * ORB_init with the same ORBid will return a reference to a newly
     * constructed ORB.
     * <p>
     * If destroy is called on an ORB that has not been shut down, it
     * will start the shut down process and block until the ORB has
     * shut down before it destroys the ORB. If an application calls
     * destroy in a thread that is currently servicing an invocation,
     * the BAD_INV_ORDER system exception will be raised with the OMG
     * minor code 3, since blocking would result in a deadlock.
     * <p>
     * For maximum portability and to avoid resource leaks, an
     * application should always call shutdown and destroy on all ORB
     * instances before exiting.
     */
    public void destroy() {
        try (AutoLock writelock = destroyLock_.getWriteLock()) {

            // Has the ORB been destroyed yet?
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");

            orbControl_.shutdownServerClient();
            orbControl_.destroy();

            // Destroy the ORBInstance object
            orbInstance_.destroy();
            orbInstance_ = null;

            // Destroy the OCI Plugin Manager. This must be done after all
            // the OCI objects have been destroyed.
            pluginManager_.destroy();
            pluginManager_ = null;

            // Mark the ORB as destroyed
            destroy_ = true;
        }
    }

    public org.omg.CORBA.portable.OutputStream create_output_stream() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");

            OutputStream out = new OutputStream();
            out._OB_ORBInstance(orbInstance_);
            return out;
        }
    }

    public org.omg.CORBA.Object get_value_def(String repid) throws BAD_PARAM {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            try {
                org.omg.CORBA.Object obj = resolve_initial_references("InterfaceRepository");
                Repository repository = RepositoryHelper.narrow(obj);
                Contained cont = repository.lookup_id(repid);
                if (cont != null) return ValueDefHelper.narrow(cont);
                throw new BAD_PARAM("Repository lookup failed for " + repid);
            } catch (InvalidName ex) {
                throw (BAD_PARAM) new BAD_PARAM("Repository lookup failed for " + repid).initCause(ex);
            }
        }
    }

    public void set_delegate(Object wrapper) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            Servant servant = (Servant) wrapper;
            servant._set_delegate(new org.apache.yoko.orb.PortableServer.Delegate(this));
        } catch (ClassCastException ex) {
            throw (BAD_PARAM) new BAD_PARAM("Argument is not of type org.omg.PortableServer.Servant").initCause(ex);
        }
    }

    protected void set_parameters(String[] args, Properties properties) {
        setParameters(new StringSeqHolder(args), properties, null);
    }

    protected void set_parameters(Applet app, Properties properties) {
        String[] args = parseAppletParams(app);
        setParameters(new StringSeqHolder(args), properties, null);
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    public static ORB init(String[] args, Properties props, Logger logger) {
        return init(new StringSeqHolder(args), props, logger);
    }

    public static ORB init(StringSeqHolder args, Properties props, Logger logger) {
        final String propName = "org.omg.CORBA.ORBClass";
        String orbClassName = null;

        ORB_impl orb;

        try {
            orb = (ORB_impl) ProviderLocator.getService(propName, ORB_impl.class, Thread.currentThread().getContextClassLoader());
            if (orb == null) {
                if (props != null) orbClassName = props.getProperty(propName);

                try {
                    if (orbClassName == null) orbClassName = doPrivileged(PrivilegedActions.getSysProp(propName));
                } catch (SecurityException ignored) {}

                if (orbClassName == null) orbClassName = "org.apache.yoko.orb.CORBA.ORB";
                orb = (ORB_impl) ProviderLocator.loadClass(orbClassName, ORB_impl.class, Thread.currentThread().getContextClassLoader()).newInstance();
            }
        } catch (Throwable ex) {
            throw (INITIALIZE)new INITIALIZE("Invalid ORB class: " + orbClassName + '\n' + ex.getMessage()).initCause(ex);
        }

        orb.setParameters(args, props, logger);

        return orb;
    }

    public static ORB init(Applet app, Properties props, Logger logger) {
        final String propName = "org.omg.CORBA.ORBClass";
        String orbClassName = null;
        ORB_impl orb;

        try {
            orb = (ORB_impl) ProviderLocator.getService(propName, ORB_impl.class, Thread.currentThread().getContextClassLoader());
            if (orb == null) {

                if (props != null) orbClassName = props.getProperty(propName);

                try {
                    if (orbClassName == null) orbClassName = doPrivileged(PrivilegedActions.getSysProp(propName));
                } catch (SecurityException ignored) {}

                if (orbClassName == null) orbClassName = "org.apache.yoko.orb.CORBA.ORB";
                orb = (ORB_impl) ProviderLocator.loadClass(orbClassName, ORB_impl.class, Thread.currentThread().getContextClassLoader()).newInstance();
            }
        } catch (Throwable ex) {
            throw (INITIALIZE)new INITIALIZE("Invalid ORB class: " + orbClassName + '\n' + ex.getMessage()).initCause(ex);
        }

        String[] args = parseAppletParams(app);
        orb.setParameters(new StringSeqHolder(args), props, logger);

        return orb;
    }

    static public String[] ParseArgs(String[] args, Properties properties, Logger logger) {
        if (logger == null) logger = new Logger_impl();

        // If the yoko.config property is defined, and the yoko.config_loaded
        // property is NOT defined, then load the configuration file into
        // the given property set and define yoko.config_loaded so we
        // don't load it again
        String yokoConfig = properties.getProperty("yoko.config");
        String yokoConfigLoaded = properties.getProperty("yoko.config_loaded");
        if (yokoConfig != null && yokoConfigLoaded == null) {
            if (loadConfigFile(yokoConfig, properties, logger)) properties.put("yoko.config_loaded", "true");
        }

        // Create list with options supported by the ORB
        if (orbOptionFilter_ == null) {
            orbOptionFilter_ = new OptionFilter("ORB.init", "-ORB");
            orbOptionFilter_.add("id", 1);
            orbOptionFilter_.add("service", 2);
            orbOptionFilter_.add("InitRef", 1);
            orbOptionFilter_.add("DefaultInitRef", 1);
            orbOptionFilter_.add("property", 1);
            orbOptionFilter_.add("repository", 1);
            orbOptionFilter_.add("naming", 1);
            orbOptionFilter_.add("config", 1);
            orbOptionFilter_.add("threaded", 0);
            orbOptionFilter_.add("version", 0);
            orbOptionFilter_.add("native_cs", 1);
            orbOptionFilter_.add("native_wcs", 1);
            orbOptionFilter_.add("default_wcs", 1);
            orbOptionFilter_.add("ServerId", 1);
            orbOptionFilter_.add("register", 1);
            orbOptionFilter_.add("ListenEndpoints", 1);
            orbOptionFilter_.add("NoProprietaryAction", 0);
            orbOptionFilter_.add("server_instance", 1);
            orbOptionFilter_.add("trace_connections", 1);
            orbOptionFilter_.add("trace_requests", 1);
            orbOptionFilter_.add("trace_requests_in", 1);
            orbOptionFilter_.add("trace_requests_out", 1);
            orbOptionFilter_.add("trace_retry", 1);
            orbOptionFilter_.add("_AmiWorkers", 1);
        }

        // Create list with options supported by the Object Adaptor
        if (oaOptionFilter_ == null) {
            oaOptionFilter_ = new OptionFilter("ORB.init", "-OA");
            oaOptionFilter_.add("host", 1); // Deprecated
            oaOptionFilter_.add("port", 1); // Deprecated
            oaOptionFilter_.add("numeric", 0); // Deprecated
            oaOptionFilter_.add("version", 1);
            oaOptionFilter_.add("threaded", 0);
            oaOptionFilter_.add("thread_per_client", 0);
            oaOptionFilter_.add("thread_per_request", 0);
            oaOptionFilter_.add("thread_pool", 1);
        }

        String configFile = null;

        // First scan through the argument list looking for the config
        // file option. We need to do this since command line arguments
        // have precedence over the properties.
        if (args != null) {
            int i = 0;
            while (i < args.length) {
                if (args[i].equals("-ORBconfig")) {
                    if (i + 1 >= args.length) {
                        String msg = "ORB.init: argument expected for -ORBconfig";
                        logger.severe(msg);
                        throw new INITIALIZE(msg);
                    }

                    configFile = args[i + 1];
                    break;
                } else
                    ++i;
            }
        }

        // Load the contents of the configuration file if present
        if (configFile != null && configFile.length() > 0) {
            loadConfigFile(configFile, properties, logger);
        }

        // Set the default value of the ORB and OA concurrency models if
        // they are not already set
        if (properties.getProperty("yoko.orb.conc_model") == null) {
            properties.put("yoko.orb.conc_model", "threaded");
        }

        if (properties.getProperty("yoko.orb.oa.conc_model") == null) {
            properties.put("yoko.orb.oa.conc_model", "thread_per_request");
        }

        // set the default number of AMI workers if not already set
        if (properties.getProperty("yoko.orb.ami_workers") == null) {
            properties.put("yoko.orb.ami_workers", "1");
        }

        // Process each argument. Turn each argument into an appropriate
        // property.
        Option[] options = orbOptionFilter_.parse(logger, args);
        for (Option item : options) {
            String name = item.name;
            String[] value = item.value;

            switch (name) {
            case "id":
                properties.put("yoko.orb.id", value[0]);
                break;
            case "register": // fall through TODO: should this do something different from "server"?
            case "ServerId": // fall through
            case "server_name":
                properties.put("yoko.orb.server_name", value[0]);
                break;
            case "server_instance":
                properties.put("yoko.orb.server_instance", value[0]);
                break;
            case "ListenEndpoints":
                properties.put("yoko.orb.oa.endpoint", value[0]);
                break;
            case "NoProprietaryAction":
                properties.put("yoko.orb.noIMR", "true");
                break;
            case "service":
                properties.put("yoko.orb.service." + value[0], value[1]);
                break;
            case "InitRef": {
                int n = value[0].indexOf('=');
                if (n <= 0 || value[0].length() == n + 1) {
                    logger.severe("ORB.init: invalid value for -ORBInitRef");
                    throw new INITIALIZE();
                }
                String svc = value[0].substring(0, n);
                String url = value[0].substring(n + 1);
                properties.put("yoko.orb.service." + svc, url);
                break;
            }
            case "DefaultInitRef":
                properties.put("yoko.orb.default_init_ref", value[0]);
                break;
            case "property": {
                int n = value[0].indexOf('=');
                if (n <= 0 || value[0].length() == n + 1) {
                    logger.severe("ORB.init: invalid value for -ORBproperty");
                    throw new INITIALIZE();
                }
                String propName = value[0].substring(0, n);
                String propValue = value[0].substring(n + 1);
                properties.put(propName, propValue);
                break;
            }
            case "repository":
                properties.put("yoko.orb.service.InterfaceRepository", value[0]);
                break;
            case "naming":
                properties.put("yoko.orb.service.NameService", value[0]);
                break;
            case "trace_connections":
            case "trace_retry":
            case "trace_requests":
            case "trace_requests_in":
            case "trace_requests_out":
                String prop = "yoko.orb.trace.";
                prop += name.substring(6);
                properties.put(prop, value[0]);
                break;
            case "threaded":
                properties.put("yoko.orb.conc_model", "threaded");
                break;
            case "version":
                logger.info(Version.getVersion());
                break;
            case "native_cs":
                properties.put("yoko.orb.native_cs", value[0]);
                break;
            case "native_wcs":
                properties.put("yoko.orb.native_wcs", value[0]);
                break;
            case "default_wcs":
                properties.put("yoko.orb.default_wcs", value[0]);
                break;
            case "_AmiWorkers":
                properties.put("yoko.orb.ami_workers", value[0]);
                break;
            }
        }

        // Process each argument. Turn each argument into an appropriate property.
        options = oaOptionFilter_.parse(logger, args);
        for (Option option : options) {
            String name = option.name;
            String[] value = option.value;

            switch (name) {
            case "host":
                // Deprecated - see IIOP plug-in
                properties.put("yoko.iiop.host", value[0]);
                break;
            case "port":
                // Deprecated - see IIOP plug-in
                properties.put("yoko.iiop.port", value[0]);
                break;
            case "numeric":
                // Deprecated - see IIOP plug-in
                properties.put("yoko.iiop.numeric", "true");
                break;
            case "version":
                properties.put("yoko.orb.oa.version", value[0]);
                break;
            case "threaded":
                properties.put("yoko.orb.oa.conc_model", "threaded");
                break;
            case "thread_per_client":
                properties.put("yoko.orb.oa.conc_model", "thread_per_client");
                break;
            case "thread_per_request":
                properties.put("yoko.orb.oa.conc_model", "thread_per_request");
                break;
            case "thread_pool":
                properties.put("yoko.orb.oa.conc_model", "thread_pool");
                properties.put("yoko.orb.oa.thread_pool", value[0]);
                break;
            }
        }

        args = orbOptionFilter_.filter(args);
        args = oaOptionFilter_.filter(args);
        return args;
    }

    public Policy create_policy(int type, Any any) throws PolicyError {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return orbInstance_.getPolicyFactoryManager().createPolicy(type, any);
        }
    }

    public ValueFactory register_value_factory(String id, ValueFactory factory) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            ValueFactoryManager valueFactoryManager = orbInstance_.getValueFactoryManager();
            return valueFactoryManager.registerValueFactory(id, factory);
        }
    }

    public void unregister_value_factory(String id) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            ValueFactoryManager valueFactoryManager = orbInstance_.getValueFactoryManager();
            valueFactoryManager.unregisterValueFactory(id);
        }
    }

    public ValueFactory lookup_value_factory(String id) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            ValueFactoryManager valueFactoryManager = orbInstance_.getValueFactoryManager();
            return valueFactoryManager.lookupValueFactory(id);
        }
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    public Properties properties() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return orbInstance_.getProperties();
        }
    }

    public Logger logger() {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return orbInstance_.getLogger();
        }
    }

    public UnknownExceptionStrategy set_unknown_exception_strategy(UnknownExceptionStrategy strategy) {
        try (AutoLock readLock = destroyLock_.getReadLock()) {
            if (destroy_) throw new OBJECT_NOT_EXIST("ORB is destroyed");
            return orbInstance_.setUnknownExceptionStrategy(strategy);
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public boolean _OB_raiseDIIExceptions() {
        return raiseDIIExceptions_;
    }

    public ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }
}

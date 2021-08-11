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

import org.apache.yoko.orb.OBPortableServer.POAManagerFactory;
import org.apache.yoko.orb.OCI.AccFactoryRegistry;
import org.apache.yoko.orb.OCI.ConFactoryRegistry;
import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.Cache;
import org.apache.yoko.util.concurrent.WeakCountedCache;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.PolicyManager;
import org.omg.CORBA.PolicyManagerHelper;
import org.omg.IOP.CodecFactory;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ORBInstance {
    private final Cache<ConnectorInfo, GIOPConnection> outboundConnectionCache = new WeakCountedCache<>(GIOPConnection::destroy, 0, 100);

    private final ORB orb;

    private final int nativeCodeSet;
    private final int nativeWcharCodeSet;
    private final int defaultWcharCodeSet;

    private final String orbId;
    private final String serverID;
    private final String serverInstanceID;

    private ObjectFactory objectFactory;
    private ClientManager clientManager;
    private PolicyFactoryManager policyFactoryManager;
    private PIManager interceptorManager;
    private InitialServiceManager initServiceManager;
    private ValueFactoryManager valueFactoryManager;
    private CodecFactory codecFactory;
    private POAManagerFactory pmFactory;
    private MultiRequestSender multiRequestSender;
    private final Properties properties;
    private DispatchStrategyFactory dispatchStrategyFactory;
    private BootManager bootManager;
    private final Logger logger;
    private final CoreTraceLevels coreTraceLevels;
    private final RecursiveMutex orbSyncMutex = new RecursiveMutex();
    private final ExecutorService serverExecutor;
    private final Phaser serverPhaser = new Phaser(1);
    private final ExecutorService clientExecutor;
    private final Phaser clientPhaser = new Phaser(1);
    private ConFactoryRegistry conFactoryRegistry;
    private AccFactoryRegistry accFactoryRegistry;
    private UnknownExceptionStrategy unknownExceptionStrategy;
    private final URLRegistry urlRegistry;
    private final boolean useTypeCodeCache;
    private final boolean extendedWchar;
    private OrbAsyncHandler asyncHandler;
    private final AtomicBoolean destroyCalled = new AtomicBoolean(); // True if destroy() was called

    protected void finalize() throws Throwable {
        Assert.ensure(destroyCalled.get());
        super.finalize();
    }

    public ORBInstance(ORB orb, String orbId, String serverID,
                       String serverInstance, ObjectFactory objectFactory,
                       ClientManager clientManager,
                       PolicyFactoryManager policyFactoryManager, PIManager piManager,
                       InitialServiceManager initServiceManager,
                       ValueFactoryManager valueFactoryManager,
                       CodecFactory codecFactory,
                       POAManagerFactory pmFactory,
                       MultiRequestSender multiRequestSender,
                       Properties properties,
                       DispatchStrategyFactory dispatchStrategyFactory,
                       BootManager bootManager, Logger logger,
                       CoreTraceLevels coreTraceLevels,
                       ConFactoryRegistry conFactoryRegistry,
                       AccFactoryRegistry accFactoryRegistry,
                       UnknownExceptionStrategy unknownExceptionStrategy,
                       URLRegistry urlRegistry, int nativeCs, int nativeWcs, int defaultWcs) {
        this.orb = orb;
        this.orbId = orbId;
        this.serverID = serverID;
        serverInstanceID = serverInstance;
        this.objectFactory = objectFactory;
        this.clientManager = clientManager;
        this.policyFactoryManager = policyFactoryManager;
        interceptorManager = piManager;
        this.initServiceManager = initServiceManager;
        this.valueFactoryManager = valueFactoryManager;
        this.codecFactory = codecFactory;
        this.pmFactory = pmFactory;
        this.multiRequestSender = multiRequestSender;
        this.properties = properties;
        this.dispatchStrategyFactory = dispatchStrategyFactory;
        this.bootManager = bootManager;
        this.logger = logger;
        this.coreTraceLevels = coreTraceLevels;
        this.conFactoryRegistry = conFactoryRegistry;
        this.accFactoryRegistry = accFactoryRegistry;
        this.unknownExceptionStrategy = unknownExceptionStrategy;
        this.urlRegistry = urlRegistry;
        nativeCodeSet = nativeCs;
        nativeWcharCodeSet = nativeWcs;
        defaultWcharCodeSet = defaultWcs;

        // Create the server and client executors
        // TODO why are these separate?
        clientExecutor = Executors.newCachedThreadPool(
                r -> {
                    Thread result = new Thread(r);
                    result.setDaemon(true);
                    return result;
                }
        );
        serverExecutor = Executors.newCachedThreadPool(
                r -> {
                    Thread result = new Thread(r);
                    result.setDaemon(true);
                    return result;
                }
        );

        // Use the TypeCode cache?
        String tcc = this.properties.getProperty("yoko.orb.use_type_code_cache");
        useTypeCodeCache = tcc == null || !tcc.equals("false");

        // Support wchar/wstring for IIOP 1.0?
        String extWchar = this.properties.getProperty("yoko.orb.extended_wchar");
        extendedWchar = extWchar != null && extWchar.equals("true");

        // get the number of AMI worker threads
        String amiWorkersStr = this.properties.getProperty("yoko.orb.ami_workers");
        int amiWorkers = amiWorkersStr == null ? 1 : Math.max(1, Integer.parseInt(amiWorkersStr));

        asyncHandler = new OrbAsyncHandler(amiWorkers);
    }

    public void destroy() {
        boolean firstCallToDestroy = destroyCalled.compareAndSet(false, true);
        Assert.ensure(firstCallToDestroy); // May only be destroyed once

        // Destroy the POAManagerFactory
        pmFactory.destroy();
        pmFactory = null;

        // Destroy the Initial Service manager
        initServiceManager.destroy();
        initServiceManager = null;

        // Destroy the Object factory
        objectFactory.destroy();
        objectFactory = null;

        // ORBControl destroys the ClientManager
        clientManager = null;

        // Destroy the PolicyFactoryManager
        policyFactoryManager.destroy();
        policyFactoryManager = null;

        // Destroy the PortableInterceptor manager
        interceptorManager.destroy();
        interceptorManager = null;

        // Destroy the ValueFactory manager
        valueFactoryManager.destroy();
        valueFactoryManager = null;

        // Destroy the CodecFactory
        codecFactory = null;

        // Destroy the MultiRequestSender factory
        multiRequestSender = null;

        // Properties are not destroyed -- they are indestructible

        // Destroy the dispatch strategy factory
        // NOTE: destruction is taken care of in ORBControl
        dispatchStrategyFactory = null;

        // Destroy the BootManager
        bootManager = null;

        // Logger is not destroyed -- it is indestructible

        // CoreTraceLevels is not destroyed -- it is indestructible

        // Client and server executors shut down in the ORBControl
        
        conFactoryRegistry = null;
        accFactoryRegistry = null;
        unknownExceptionStrategy = null;
        asyncHandler.shutdown();
        asyncHandler = null;
    }

    public ORB getORB() {
        return orb;
    }

    public int getNativeCs() {
        return nativeCodeSet;
    }

    public int getNativeWcs() {
        return nativeWcharCodeSet;
    }

    public int getDefaultWcs() {
        return defaultWcharCodeSet;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public PolicyFactoryManager getPolicyFactoryManager() {
        return policyFactoryManager;
    }

    public PolicyManager getPolicyManager() {
        try {
            return PolicyManagerHelper.narrow(orb.resolve_initial_references("ORBPolicyManager"));
        } catch (InvalidName invalidName) {
            throw new INTERNAL("Could not find PolicyManager");
        }
    }

    public PIManager getPIManager() {
        return interceptorManager;
    }

    public InitialServiceManager getInitialServiceManager() {
        return initServiceManager;
    }

    public ValueFactoryManager getValueFactoryManager() {
        return valueFactoryManager;
    }

    public CodecFactory getCodecFactory() {
        return codecFactory;
    }

    public POAManagerFactory getPOAManagerFactory() {
        return pmFactory;
    }

    public MultiRequestSender getMultiRequestSender() {
        return multiRequestSender;
    }

    public Properties getProperties() {
        return properties;
    }

    public DispatchStrategyFactory getDispatchStrategyFactory() {
        return dispatchStrategyFactory;
    }

    public BootManager getBootManager() {
        return bootManager;
    }

    public Logger getLogger() {
        return logger;
    }

    public CoreTraceLevels getCoreTraceLevels() {
        return coreTraceLevels;
    }

    public RecursiveMutex getORBSyncMutex() {
        return orbSyncMutex;
    }

    public ExecutorService getServerExecutor() {
        return serverExecutor;
    }

    public Phaser getServerPhaser() {
        return serverPhaser;
    }
    
    public ExecutorService getClientExecutor() {
        return clientExecutor;
    }
    
    public Phaser getClientPhaser() {
        return clientPhaser;
    }

    public ConFactoryRegistry getConFactoryRegistry() {
        return conFactoryRegistry;
    }

    public AccFactoryRegistry getAccFactoryRegistry() {
        return accFactoryRegistry;
    }

    public UnknownExceptionStrategy getUnknownExceptionStrategy() {
        return unknownExceptionStrategy;
    }

    public UnknownExceptionStrategy setUnknownExceptionStrategy(UnknownExceptionStrategy strategy) {
        UnknownExceptionStrategy result = unknownExceptionStrategy;
        unknownExceptionStrategy = strategy;
        return result;
    }

    public URLRegistry getURLRegistry() {
        return urlRegistry;
    }

    public String getOrbId() {
        return orbId;
    }

    public String getServerId() {
        return serverID;
    }

    public String getServerInstance() {
        return serverInstanceID;
    }

    public boolean useTypeCodeCache() {
        return useTypeCodeCache;
    }

    public boolean extendedWchar() {
        return extendedWchar;
    }

    public OrbAsyncHandler getAsyncHandler() {
        return asyncHandler;
    }

    public Cache<ConnectorInfo, GIOPConnection> getOutboundConnectionCache() {return outboundConnectionCache;}
}

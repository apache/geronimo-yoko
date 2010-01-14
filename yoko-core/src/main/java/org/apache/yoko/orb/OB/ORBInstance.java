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

import org.apache.yoko.orb.OB.BootManager;
import org.apache.yoko.orb.OB.DispatchStrategyFactory;
import org.apache.yoko.orb.OB.Logger;
import org.apache.yoko.orb.OB.URLRegistry;
import org.apache.yoko.orb.OB.UnknownExceptionStrategy;

public final class ORBInstance {
    private boolean destroy_; // True if destroy() was called

    //
    // Reference to ORB is needed in Java
    //
    private org.omg.CORBA.ORB orb_;

    //
    // The native codesets
    //
    private int nativeCs_;

    private int nativeWcs_;

    //
    // The default wchar codeset (should be 0 according to specification)
    //
    private int defaultWcs_;

    //
    // The ORB id
    //
    private String orbId_;

    //
    // The Server id
    //
    private String serverId_;

    //
    // The Server instance-id
    //
    private String serverInstance_;

    private ObjectFactory objectFactory_;

    private ClientManager clientManager_;

    private PolicyFactoryManager policyFactoryManager_;

    private PIManager interceptorManager_;

    private InitialServiceManager initServiceManager_;

    private ValueFactoryManager valueFactoryManager_;

    private org.omg.IOP.CodecFactory codecFactory_;

    private org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactory_;

    private MultiRequestSender multiRequestSender_;

    private java.util.Properties properties_;

    private DispatchStrategyFactory dispatchStrategyFactory_;

    private BootManager bootManager_;

    private Logger logger_;

    private CoreTraceLevels coreTraceLevels_;

    private RecursiveMutex orbSyncMutex_ = new RecursiveMutex();

    private ThreadGroup serverWorkerGroup_;

    private ThreadGroup clientWorkerGroup_;

    private org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry_;

    private org.apache.yoko.orb.OCI.AccFactoryRegistry accFactoryRegistry_;

    private UnknownExceptionStrategy unknownExceptionStrategy_;

    private URLRegistry urlRegistry_;

    private boolean useTypeCodeCache_;

    private boolean extendedWchar_;

    //
    // the async message handler
    //
    OrbAsyncHandler asyncHandler_ = null;

    // ----------------------------------------------------------------------
    // ORBInstance private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert._OB_assert(destroy_);

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // ORBInstance package member implementations
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // ORBInstance public member implementations
    // ----------------------------------------------------------------------

    public ORBInstance(org.omg.CORBA.ORB orb, String orbId, String serverId,
            String serverInstance, ObjectFactory objectFactory,
            ClientManager clientManager,
            PolicyFactoryManager policyFactoryManager, PIManager piManager,
            InitialServiceManager initServiceManager,
            ValueFactoryManager valueFactoryManager,
            org.omg.IOP.CodecFactory codecFactory,
            org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactory,
            MultiRequestSender multiRequestSender,
            java.util.Properties properties,
            DispatchStrategyFactory dispatchStrategyFactory,
            BootManager bootManager, Logger logger,
            CoreTraceLevels coreTraceLevels,
            org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry,
            org.apache.yoko.orb.OCI.AccFactoryRegistry accFactoryRegistry,
            UnknownExceptionStrategy unknownExceptionStrategy,
            URLRegistry urlRegistry, int nativeCs, int nativeWcs, int defaultWcs) {
        orb_ = orb;
        orbId_ = orbId;
        serverId_ = serverId;
        serverInstance_ = serverInstance;
        objectFactory_ = objectFactory;
        clientManager_ = clientManager;
        policyFactoryManager_ = policyFactoryManager;
        interceptorManager_ = piManager;
        initServiceManager_ = initServiceManager;
        valueFactoryManager_ = valueFactoryManager;
        codecFactory_ = codecFactory;
        pmFactory_ = pmFactory;
        multiRequestSender_ = multiRequestSender;
        properties_ = properties;
        dispatchStrategyFactory_ = dispatchStrategyFactory;
        bootManager_ = bootManager;
        logger_ = logger;
        coreTraceLevels_ = coreTraceLevels;
        conFactoryRegistry_ = conFactoryRegistry;
        accFactoryRegistry_ = accFactoryRegistry;
        unknownExceptionStrategy_ = unknownExceptionStrategy;
        urlRegistry_ = urlRegistry;
        nativeCs_ = nativeCs;
        nativeWcs_ = nativeWcs;
        defaultWcs_ = defaultWcs;

        //
        // Create the server and client worker groups
        //
        clientWorkerGroup_ = new ThreadGroup("ClientWorkers");
        serverWorkerGroup_ = new ThreadGroup("ServerWorkers");

        //
        // Use the TypeCode cache?
        //
        String tcc = properties_.getProperty("yoko.orb.use_type_code_cache");
        if (tcc != null && tcc.equals("false"))
            useTypeCodeCache_ = false;
        else
            useTypeCodeCache_ = true;

        //
        // Support wchar/wstring for IIOP 1.0?
        //
        String extWchar = properties_.getProperty("yoko.orb.extended_wchar");
        if (extWchar != null && extWchar.equals("true"))
            extendedWchar_ = true;
        else
            extendedWchar_ = false;

        //
        // get the number of AMI worker threads
        //
        String amiWorkersStr = properties_.getProperty("yoko.orb.ami_workers");
        int amiWorkers = 1;
        if (amiWorkersStr != null) {
            amiWorkers = Integer.parseInt(amiWorkersStr);
            if (amiWorkers <= 0)
                amiWorkers = 1;
        }

        //
        // the Asynchonous message handler
        //
        asyncHandler_ = new OrbAsyncHandler(amiWorkers);
    }

    public void destroy() {
        Assert._OB_assert(!destroy_); // May only be destroyed once
        destroy_ = true;

        //
        // Destroy the POAManagerFactory
        //
        pmFactory_.destroy();
        pmFactory_ = null;

        //
        // Destroy the Initial Service manager
        //
        initServiceManager_.destroy();
        initServiceManager_ = null;

        //
        // Destroy the Object factory
        //
        objectFactory_.destroy();
        objectFactory_ = null;

        //
        // Destroy the ClientManager
        //
        // ORBControl destroys the ClientManager
        // clientManager_.destroy();
        clientManager_ = null;

        //
        // Destroy the PolicyFactoryManager
        //
        policyFactoryManager_.destroy();
        policyFactoryManager_ = null;

        //
        // Destroy the PortableInterceptor manager
        //
        interceptorManager_.destroy();
        interceptorManager_ = null;

        //
        // Destroy the ValueFactory manager
        //
        valueFactoryManager_.destroy();
        valueFactoryManager_ = null;

        //
        // Destroy the CodecFactory
        //
        // codecFactory_.destroy(); // No destroy operation defined
        codecFactory_ = null;

        //
        // Destroy the MultiRequestSender factory
        //
        // multiRequestSender_.destroy(); // No destroy operation defined
        multiRequestSender_ = null;

        //
        // Properties are not destroyed -- they are indestructible
        //
        // properties_.destroy(); // No destroy operation defined
        // properties_ = null;

        //
        // Destroy the dispatch strategy factory
        //
        // NOTE: destruction is taken care of in ORBControl
        //
        // ((DispatchStrategyFactory_impl)dispatchStrategyFactory_).
        // _OB_destroy();
        dispatchStrategyFactory_ = null;

        //
        // Destroy the BootManager
        //
        // bootManager_.destroy(); // No destroy operation defined
        bootManager_ = null;

        //
        // Logger is not destroyed -- it is indestructible
        //
        // logger_.destroy();
        // logger_ = null;

        //
        // CoreTraceLevels is not destroyed -- it is indestructible
        //
        // coreTraceLevels_.destroy();
        // coreTraceLevels_ = null;

        try {
            //
            // Destroy the client and server worker groups
            //
            serverWorkerGroup_.destroy();
        } catch (IllegalThreadStateException ex) {
            // we ignore this...occasionally, it is necessary 
            // to kick the threads to force them to shutdown. 
        }

        try {
            clientWorkerGroup_.destroy();
        } catch (IllegalThreadStateException ex) {
            // we ignore this...occasionally, it is necessary 
            // to kick the threads to force them to shutdown. 
        }

        //
        // Destroy the ConFactoryRegistry
        //
        // conFactoryRegistry_.destroy(); // No destroy operation defined
        conFactoryRegistry_ = null;

        //
        // Destroy the AccFactoryRegistry
        //
        // accFactoryRegistry_.destroy(); // No destroy operation defined
        accFactoryRegistry_ = null;

        //
        // Destroy the UnknownExceptionStrategy
        //
        unknownExceptionStrategy_.destroy();
        unknownExceptionStrategy_ = null;

        //
        // Destroy the Asynchonous message handler
        //
        asyncHandler_.shutdown();
        asyncHandler_ = null;
    }

    //
    // IMPORTANT: Only use this when required by the Java mapping
    //
    public org.omg.CORBA.ORB getORB() {
        return orb_;
    }

    public int getNativeCs() {
        return nativeCs_;
    }

    public int getNativeWcs() {
        return nativeWcs_;
    }

    public int getDefaultWcs() {
        return defaultWcs_;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory_;
    }

    public ClientManager getClientManager() {
        return clientManager_;
    }

    public PolicyFactoryManager getPolicyFactoryManager() {
        return policyFactoryManager_;
    }

    public PIManager getPIManager() {
        return interceptorManager_;
    }

    public InitialServiceManager getInitialServiceManager() {
        return initServiceManager_;
    }

    public ValueFactoryManager getValueFactoryManager() {
        return valueFactoryManager_;
    }

    public org.omg.IOP.CodecFactory getCodecFactory() {
        return codecFactory_;
    }

    public org.apache.yoko.orb.OBPortableServer.POAManagerFactory getPOAManagerFactory() {
        return pmFactory_;
    }

    public MultiRequestSender getMultiRequestSender() {
        return multiRequestSender_;
    }

    public java.util.Properties getProperties() {
        return properties_;
    }

    public DispatchStrategyFactory getDispatchStrategyFactory() {
        return dispatchStrategyFactory_;
    }

    public BootManager getBootManager() {
        return bootManager_;
    }

    public Logger getLogger() {
        return logger_;
    }

    public CoreTraceLevels getCoreTraceLevels() {
        return coreTraceLevels_;
    }

    public RecursiveMutex getORBSyncMutex() {
        return orbSyncMutex_;
    }

    public ThreadGroup getServerWorkerGroup() {
        return serverWorkerGroup_;
    }

    public ThreadGroup getClientWorkerGroup() {
        return clientWorkerGroup_;
    }

    public org.apache.yoko.orb.OCI.ConFactoryRegistry getConFactoryRegistry() {
        return conFactoryRegistry_;
    }

    public org.apache.yoko.orb.OCI.AccFactoryRegistry getAccFactoryRegistry() {
        return accFactoryRegistry_;
    }

    public UnknownExceptionStrategy getUnknownExceptionStrategy() {
        return unknownExceptionStrategy_;
    }

    public UnknownExceptionStrategy setUnknownExceptionStrategy(
            UnknownExceptionStrategy strategy) {
        UnknownExceptionStrategy result = unknownExceptionStrategy_;
        unknownExceptionStrategy_ = strategy;
        return result;
    }

    public URLRegistry getURLRegistry() {
        return urlRegistry_;
    }

    public String getOrbId() {
        return orbId_;
    }

    public String getServerId() {
        return serverId_;
    }

    public String getServerInstance() {
        return serverInstance_;
    }

    public boolean useTypeCodeCache() {
        return useTypeCodeCache_;
    }

    public boolean extendedWchar() {
        return extendedWchar_;
    }

    public OrbAsyncHandler getAsyncHandler() {
        return asyncHandler_;
    }
}

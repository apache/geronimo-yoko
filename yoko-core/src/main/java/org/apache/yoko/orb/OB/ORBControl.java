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

import org.apache.yoko.orb.OB.DispatchStrategyFactory;

public final class ORBControl {
    //
    // The ORB instance
    //
    ORBInstance orbInstance_;

    //
    // The ORBControl state
    //
    private final static int StateNotRunning = 0;

    private final static int StateRunning = 1;

    private final static int StateServerShutdown = 2;

    private final static int StateClientShutdown = 3;

    private final static int StateDestroyed = 4;

    private int state_; // State of the ORB

    //
    // Has shutdown been called?
    //
    private boolean shutdown_;

    //
    // Condition variable used to block until the server has been shutdown
    //
    private java.lang.Object shutdownCond_ = new java.lang.Object();

    //
    // The Root POA
    //
    private org.omg.PortableServer.POA rootPOA_; // The Root POA

    //
    // The thread id of the main thread (that is the first thread that
    // calls run, perform_work or work_pending)
    //
    private Thread mainThread_;

    // ----------------------------------------------------------------------
    // ORBControl private and protected member implementations
    // ----------------------------------------------------------------------

    //
    // Complete shutdown of the ORB, if necessary
    //
    private synchronized void completeServerShutdown() {
        //
        // If the shutdown_ is false, or the server side has already
        // shutdown then do nothing
        //
        if (!shutdown_ || state_ == StateServerShutdown)
            return;

        Assert._OB_assert(state_ != StateClientShutdown
                && state_ != StateDestroyed);

        //
        // If run was called then only the main thread may complete the
        // shutdown
        //
        Assert._OB_assert(state_ == StateNotRunning
                || mainThread_ == Thread.currentThread());

        //
        // Get the POAManagerFactory implementation
        //
        org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactory = orbInstance_
                .getPOAManagerFactory();

        org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl factory = (org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl) pmFactory;

        //
        // Deactivate all of the POAManagers
        //
        factory._OB_deactivate();

        //
        // Wait for all the threads in the server worker group to
        // terminate
        //
        ThreadGroup group = orbInstance_.getServerWorkerGroup();
        synchronized (group) {
            int timeOuts = 0; 

            // it's possible that a thread will get stalled in a read(), which seems 
            // to happen most often with SSLSockets.  We'll do some retry loops here 
            // and interrupt any threads that seem to be taking an excessively long time 
            // to clean up. 
            int count = group.activeCount();
            while (count > 0) {
                try {
                    group.wait(200);
                } catch (InterruptedException ex) {
                }
                int newCount = group.activeCount();
                // we woke up because of a timeout.  
                if (newCount == count) {
                    timeOuts++; 
                    // after 2 timeouts, interrupt any remaining threads in the 
                    // group. 
                    if (timeOuts == 2) {
                        group.interrupt(); 
                    }
                    // we've waited a full second, and we still have active threads. 
                    // time to give up waiting. 
                    if (timeOuts >= 5) {
                        break;
                    }
                }
                count = newCount; 
            }

            //
            // Get the DispatchStrategyFactory implementation and
            // destroy it. It must be destroyed here so that the
            // thread pools get destroyed before OCI::Current_impl
            // gets destroyed by the destruction of the Root
            // POA. Otherwise, thread specific data for the thread
            // pool threads will not get released.
            //
            DispatchStrategyFactory dsFactory = orbInstance_
                    .getDispatchStrategyFactory();

            DispatchStrategyFactory_impl dsFactoryImpl = (DispatchStrategyFactory_impl) dsFactory;

            dsFactoryImpl._OB_destroy();

            //
            // Mark the server side state as shutdown and notify any
            // waiting threads
            //
            state_ = StateServerShutdown;

            //
            // Destroy the root POA
            //
            if (rootPOA_ != null) {
                rootPOA_.destroy(true, true);
                rootPOA_ = null;
            }

            notifyAll();
        }
    }

    //
    // Validate the state
    //
    private synchronized void validateState() {
        //
        // The ORB destroys this object, so it's an initialization
        // error if the this operation is called after ORB destruction
        //
        if (state_ == StateDestroyed)
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (state_ == StateServerShutdown || state_ == StateClientShutdown)
            throw new org.omg.CORBA.BAD_INV_ORDER(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorShutdownCalled),
                    org.apache.yoko.orb.OB.MinorCodes.MinorShutdownCalled,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (state_ == StateNotRunning) {
            //
            // Remember the main thread id
            //
            mainThread_ = Thread.currentThread();

            //
            // Set the state to StateRunning
            //
            state_ = StateRunning;
        }
    }

    private synchronized void blockServerShutdownComplete() {
        //
        // Wait for the server side to shutdown. Note that the client
        // side shutting down or the state being destroyed also
        // implies that the server side has shutdown
        //
        while (state_ == StateRunning) {
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    // ----------------------------------------------------------------------
    // ORBControl public member implementations
    // ----------------------------------------------------------------------

    public ORBControl() {
        state_ = StateNotRunning;
        shutdown_ = false;
    }

    //
    // Destroy the ORBControl
    //
    public synchronized void destroy() {
        //
        // destroy() may not be called unless the client side has been
        // shutdown
        //
        Assert._OB_assert(state_ == StateClientShutdown);
        state_ = StateDestroyed;

        //
        // Set the ORBInstance object to nil
        //
        orbInstance_ = null;
    }

    //
    // Set the ORBInstance object
    //
    public void setORBInstance(ORBInstance instance) {
        orbInstance_ = instance;
    }

    //
    // Determine if there if the ORB needs the main thread to perform
    // some work
    //
    public boolean workPending() {
        validateState();

        //
        // If this is not the main thread then do nothing
        //
        if (mainThread_ != Thread.currentThread())
            return false;

        //
        // Validate state will throw an exception if state is
        // ServerShutdown, ClientShutdown or Destroyed. Therefore if
        // shutdown_ is true, then a server side shutdown is pending.
        //
        if (shutdown_)
            return true;

        return false;
    }

    //
    // Perform one unit of work
    //
    public void performWork() {
        validateState();

        //
        // If this is not the main thread then do nothing
        //
        if (mainThread_ != Thread.currentThread())
            return;

        completeServerShutdown();
    }

    //
    // Run the ORB event loop
    //
    public void run() {
        validateState();

        //
        // If this is not the main thread then block until the ORB is
        // shutdown
        //
        if (mainThread_ != Thread.currentThread()) {
            blockServerShutdownComplete();
            return;
        }

        //
        // Validate state will throw an exception if state is
        // ServerShutdown, ClientShutdown or Destroyed. Therefore if
        // shutdown_ is true, then a server side shutdown is pending
        // so complete it now.
        //
        if (shutdown_) {
            completeServerShutdown();
            return;
        }

        //
        // Block until the ORB server side has shutdown. Note that the
        // client side shutting down or the state being destroyed also
        // implies that the server side has shutdown
        //
        do {
            synchronized (shutdownCond_) {
                try {
                    shutdownCond_.wait();
                } catch (InterruptedException ex) {
                }
            }

            //
            // After this call state is either ShutdownClient, or
            // Running
            //
            completeServerShutdown();
        } while (state_ == StateRunning);
    }

    //
    // Shutdown the server side of the ORB
    //
    // ASYNC SAFE if waitForCompletion == false
    //
    public void shutdownServer(boolean waitForCompletion) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if the this operation is called after ORB destruction
        //
        if (state_ == StateDestroyed)
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        if (state_ == StateServerShutdown || state_ == StateClientShutdown)
            throw new org.omg.CORBA.BAD_INV_ORDER(org.apache.yoko.orb.OB.MinorCodes
                    .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorShutdownCalled),
                    org.apache.yoko.orb.OB.MinorCodes.MinorShutdownCalled,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        //
        // If waitForCompletion is true then find out whether we're inside
        // a method invocation -- if so throw a BAD_INV_ORDER exception
        //
        if (waitForCompletion) {
            boolean inInvocation = false;
            try {
                InitialServiceManager initialServiceManager = orbInstance_
                        .getInitialServiceManager();
                org.omg.CORBA.Object o = initialServiceManager
                        .resolveInitialReferences("POACurrent");
                org.apache.yoko.orb.PortableServer.Current_impl current = (org.apache.yoko.orb.PortableServer.Current_impl) o;
                inInvocation = current._OB_inUpcall();
                if (inInvocation) {
                    //
                    // Check whether or not the request is dispatched in this
                    // POAManager's ORB or another ORB.
                    //
                    try {
                        org.apache.yoko.orb.OBPortableServer.POA_impl p = (org.apache.yoko.orb.OBPortableServer.POA_impl) current
                                .get_POA();
                        inInvocation = (p._OB_ORBInstance() == orbInstance_);
                    } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
                    }
                }
            } catch (ClassCastException ex) {
            } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            }

            if (inInvocation)
                throw new org.omg.CORBA.BAD_INV_ORDER(
                        MinorCodes
                                .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorDestroyWouldBlock),
                        org.apache.yoko.orb.OB.MinorCodes.MinorDestroyWouldBlock,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        shutdown_ = true;

        //
        // Unblock run(). This should be done immediately before
        // the return since this can cause the main loop to wake and
        // complete the shutdown (thus, for instance, destroying the
        // POAManagerFactory).
        //
        synchronized (shutdownCond_) {
            shutdownCond_.notifyAll();
        }

        //
        // waitForCompletion false? We're done.
        //
        if (!waitForCompletion)
            return;

        //
        // If run was called and this is not the main thread and
        // waitForCompletion is true then wait for the shutdown to
        // complete.
        //
        if (state_ == StateRunning && mainThread_ != Thread.currentThread()) {
            blockServerShutdownComplete();
            return;
        }

        //
        // This is the main thread -- complete the shutdown process
        //
        completeServerShutdown();
    }

    //
    // Shutdown the server (if necessary) & client side of the ORB
    //
    public synchronized void shutdownServerClient() {
        //
        // The ORB destroys this object, so it's an initialization
        // error if the this operation is called after ORB destruction
        //
        if (state_ == StateDestroyed)
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        //
        // If the ORB client side is already shutdown, then we're done
        //
        if (state_ == StateClientShutdown)
            return;

        if (orbInstance_ != null) {
            //
            // First shutdown the server side, if necessary
            //
            if (state_ != StateServerShutdown)
                shutdownServer(true);

            //
            // The server shutdown must have completed
            //
            Assert._OB_assert(state_ == StateServerShutdown);

            //
            // Shutdown the client side. Continue to dispatch events until all
            // client type event handlers have unregistered.
            //
            ClientManager clientManager = orbInstance_.getClientManager();
            clientManager.destroy();

            //
            // Wait for all the threads in the client worker group to
            // terminate
            //
            ThreadGroup group = orbInstance_.getClientWorkerGroup();
            synchronized (group) {
                while (group.activeCount() > 0) {
                    try {
                        group.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }

        //
        // Mark the ORB's client side as shutdown and notify any
        // waiters
        //
        state_ = StateClientShutdown;
        notifyAll();
    }

    //
    // Initialize the Root POA
    //
    public void initializeRootPOA(org.omg.CORBA.ORB orb) {
        String serverId = orbInstance_.getServerId();

        //
        // If there is no server id then set to "_RootPOA"
        //
        if (serverId.length() == 0)
            serverId = "_RootPOA";

        //
        // Get the initial service manager
        //
        InitialServiceManager ism = orbInstance_.getInitialServiceManager();

        //
        // Create the Root POAManager
        //
        org.apache.yoko.orb.OBPortableServer.POAManagerFactory factory = null;
        try {
            factory = org.apache.yoko.orb.OBPortableServer.POAManagerFactoryHelper
                    .narrow(ism.resolveInitialReferences("POAManagerFactory"));
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            Assert._OB_assert(ex);
        }

        //
        // First attempt to locate the root POAManager
        //
        org.apache.yoko.orb.OBPortableServer.POAManager manager = null;

        org.omg.PortableServer.POAManager[] managers = factory.list();
        for (int i = 0; i < managers.length; i++) {
            if (managers[i].get_id().equals("RootPOAManager")) {
                manager = (org.apache.yoko.orb.OBPortableServer.POAManager) managers[i];
                break;
            }
        }

        //
        // If the root POAManager doesn't exist then create it
        //
        if (manager == null) {
            try {
                org.omg.CORBA.Policy[] emptyPl = new org.omg.CORBA.Policy[0];
                manager = (org.apache.yoko.orb.OBPortableServer.POAManager) (factory
                        .create_POAManager("RootPOAManager", emptyPl));
            } catch (org.omg.PortableServer.POAManagerFactoryPackage.ManagerAlreadyExists ex) {
                Assert._OB_assert(ex);
            }
            // catch(org.apache.yoko.orb.OCI.InvalidParam ex)
            // {
            // Logger logger = orbInstance_.getLogger();
            // String err = "invalid configuration parameter " +
            // "for RootPOAManager: " + ex.reason;
            // logger.error(err);
            // throw new org.omg.CORBA.INITIALIZE(err);
            // }
            catch (org.omg.CORBA.PolicyError ex) {
                // TODO : Is this correct?
                Assert._OB_assert(ex);
            }
        }

        //
        // Create the Root POA
        //
        org.apache.yoko.orb.OBPortableServer.POA_impl root = new org.apache.yoko.orb.OBPortableServer.POA_impl(
                orb, orbInstance_, serverId, manager);
        root._OB_addPolicyFactory();
        rootPOA_ = root;

        try {
            ism.addInitialReference("RootPOA", root, true);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            Assert._OB_assert(ex);
        }

        //
        // Ask the POAManagerFactory to initialize this servers connection
        // to the IMR
        //
        // TODO-B3: Is there some other point that can be used to do this?
        //
        org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl factoryImpl = (org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl) factory;
        factoryImpl._OB_initializeIMR(root, this);
    }
}

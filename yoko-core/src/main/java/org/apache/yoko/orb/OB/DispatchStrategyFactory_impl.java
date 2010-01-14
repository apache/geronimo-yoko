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

import org.apache.yoko.orb.OB.DispatchRequest;
import org.apache.yoko.orb.OB.DispatchStrategy;
import org.apache.yoko.orb.OB.DispatchStrategyFactory;
import org.apache.yoko.orb.OB.InvalidThreadPool;
import org.apache.yoko.orb.OB.SAME_THREAD;
import org.apache.yoko.orb.OB.THREAD_PER_REQUEST;
import org.apache.yoko.orb.OB.THREAD_POOL;

// ----------------------------------------------------------------------
// DispatchThreadSameThread
// ----------------------------------------------------------------------

class DispatchSameThread_impl extends org.omg.CORBA.LocalObject implements
        DispatchStrategy {
    DispatchSameThread_impl() {
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public int id() {
        return SAME_THREAD.value;
    }

    public org.omg.CORBA.Any info() {
        return new org.apache.yoko.orb.CORBA.Any();
    }

    public void dispatch(DispatchRequest request) {
        //
        // Invoke the request
        //
        request.invoke();
    }
}

// ----------------------------------------------------------------------
// DispatchThreadPerRequest
// ----------------------------------------------------------------------

class DispatchThreadPerRequest_impl extends org.omg.CORBA.LocalObject implements
        DispatchStrategy {
    class Dispatcher extends Thread {
        private DispatchRequest request_;

        Dispatcher(DispatchRequest request) {
            super("Yoko:ThreadPerRequest:Dispatcher");
            request_ = request;
        }

        public void run() {
            //
            // Invoke the request
            //
            request_.invoke();
        }
    }

    DispatchThreadPerRequest_impl() {
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public int id() {
        return THREAD_PER_REQUEST.value;
    }

    public org.omg.CORBA.Any info() {
        return new org.apache.yoko.orb.CORBA.Any();
    }

    public void dispatch(DispatchRequest request) {
        try {
            Thread t = new Dispatcher(request);
            t.start();
        } catch (OutOfMemoryError e) {
            throw new org.omg.CORBA.TRANSIENT();
        }
    }
}

// ----------------------------------------------------------------------
// DispatchThreadPool_impl
// ----------------------------------------------------------------------

class DispatchThreadPool_impl extends org.omg.CORBA.LocalObject implements
        DispatchStrategy {
    private int id_;

    private ThreadPool pool_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public int id() {
        return THREAD_POOL.value;
    }

    public org.omg.CORBA.Any info() {
        org.omg.CORBA.Any any = new org.apache.yoko.orb.CORBA.Any();
        any.insert_ulong(id_);
        return any;
    }

    public void dispatch(DispatchRequest request) {
        pool_.add(request);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    DispatchThreadPool_impl(int id, ThreadPool pool) {
        id_ = id;
        pool_ = pool;
    }
}

public class DispatchStrategyFactory_impl extends org.omg.CORBA.LocalObject
        implements DispatchStrategyFactory {
    static final Logger logger = Logger.getLogger(DispatchStrategyFactory.class.getName());
    //
    // A sequence of thread pools. The index in the sequence is the
    // thread pool id.
    //
    private java.util.Vector pools_ = new java.util.Vector();

    //
    // Has the default thread pool been created yet?
    //
    private boolean haveDefaultThreadPool_ = false;

    //
    // If so, what is the thread pool id?
    //
    private int defaultThreadPool_;

    //
    // Has the factory been destroyed?
    //
    private boolean destroy_ = false;

    //
    // The ORB instance
    //
    private ORBInstance orbInstance_ = null;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized int create_thread_pool(int nthreads) {
        //
        // The ORB destroys this object, so it's an initialization
        // error if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        //
        // Find the first empty thread pool
        //
        int i;
        for (i = 0; i < pools_.size(); i++) {
            if (pools_.elementAt(i) == null) {
                break;
            }
        }

        //
        // If there is no empty slot then append an empty slot
        //
        if (i >= pools_.size()) {
            pools_.addElement(null);
        }

        //
        // Allocate a new ThreadPool
        //
        pools_.setElementAt(new ThreadPool(i, nthreads), i);

        return i;
    }

    public synchronized void destroy_thread_pool(int id)
            throws InvalidThreadPool {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        if (id < 0 || id > pools_.size() || pools_.elementAt(id) == null) {
            throw new InvalidThreadPool();
        }

        //
        // Destroy the ThreadPool
        //
        ((ThreadPool) pools_.elementAt(id)).destroy();

        //
        // Empty the slot associated with this thread pool
        //
        pools_.setElementAt(null, id);
    }

    public synchronized DispatchStrategy create_thread_pool_strategy(int id)
            throws InvalidThreadPool {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        if (id < 0 || id > pools_.size() || pools_.elementAt(id) == null) {
            throw new InvalidThreadPool();
        }

        return new DispatchThreadPool_impl(id, (ThreadPool) pools_
                .elementAt(id));
    }

    public synchronized DispatchStrategy create_same_thread_strategy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return new DispatchSameThread_impl();
    }

    public synchronized DispatchStrategy create_thread_per_request_strategy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return new DispatchThreadPerRequest_impl();
    }

    public synchronized DispatchStrategy create_default_dispatch_strategy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        //
        // Get the ORB properties
        //
        java.util.Properties properties = orbInstance_.getProperties();

        //
        // Set the dispatch strategy policy as specified by the
        // conc_model property
        //
        String value = properties.getProperty("yoko.orb.oa.conc_model");

        if (value != null) {
            logger.fine("Defined concurrency model is " + value); 
            if (value.equals("threaded") || value.equals("thread_per_client")) {
                logger.fine("Using same thread dispatch strategy"); 
                return create_same_thread_strategy();
            } else if (value.equals("thread_per_request")) {
                logger.fine("Using thread per request dispatch strategy"); 
                return create_thread_per_request_strategy();
            } else if (value.equals("thread_pool")) {
                //
                // If there is no default thread pool yet then create one,
                // with a default of 10 threads.
                //
                if (!haveDefaultThreadPool_) {
                    haveDefaultThreadPool_ = true;
                    value = properties.getProperty("yoko.orb.oa.thread_pool");
                    int nthreads = 0;
                    if (value != null) {
                        nthreads = Integer.parseInt(value);
                    }
                    if (nthreads == 0) {
                        nthreads = 10;
                    }
                    logger.fine("Creating a thread pool of size " + nthreads); 
                    defaultThreadPool_ = create_thread_pool(nthreads);
                }
                try {
                    logger.fine("Using a thread pool dispatch strategy"); 
                    return create_thread_pool_strategy(defaultThreadPool_);
                } catch (InvalidThreadPool ex) {
                    Assert._OB_assert(ex);
                }
            } else {
                String err = "yoko.orb.oa.conc_model: Unknown value `";
                err += value;
                err += "'";
                orbInstance_.getLogger().warning(err);
            }
        }

        //
        // The default is to use a thread-per-request.  Not doing this can cause 
        // deadlocks, so the single thread 
        //
        logger.fine("Using default thread per request strategy"); 
        return create_thread_per_request_strategy();
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public DispatchStrategyFactory_impl() {
    }

    public synchronized void _OB_setORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    protected synchronized void _OB_destroy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        destroy_ = true;

        orbInstance_ = null;

        //
        // Destroy each of the thread pools
        //
        for (int i = 0; i < pools_.size(); i++) {
            ThreadPool pool = (ThreadPool) pools_.elementAt(i);
            if (pool != null) {
                pool.destroy();
                pools_.setElementAt(null, i);
            }
        }
    }
}

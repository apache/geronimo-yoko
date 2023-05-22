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
package org.apache.yoko.orb.OB;
 
import org.apache.yoko.util.Assert;
import org.omg.CORBA.Any;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.LocalObject;

import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import static org.apache.yoko.util.MinorCodes.*;
import static org.omg.CORBA.CompletionStatus.*;

// ----------------------------------------------------------------------
// DispatchThreadSameThread
// ----------------------------------------------------------------------

final class DispatchSameThread_impl extends LocalObject implements DispatchStrategy {
    DispatchSameThread_impl() {}

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public int id() {
        return SAME_THREAD.value;
    }

    public Any info() {
        return new org.apache.yoko.orb.CORBA.Any();
    }

    public void dispatch(DispatchRequest request) {
        request.invoke();
    }
}

// ----------------------------------------------------------------------
// DispatchThreadPerRequest
// ----------------------------------------------------------------------

final class DispatchThreadPerRequest_impl extends LocalObject implements DispatchStrategy {
    static final class Dispatcher extends Thread {
        private final DispatchRequest request_;

        Dispatcher(DispatchRequest request) {
            super("Yoko:ThreadPerRequest:Dispatcher");
            request_ = request;
        }

        public void run() {
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

    public Any info() {
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

final class DispatchThreadPool_impl extends LocalObject implements DispatchStrategy {
    private final int id_;

    private final ThreadPool pool_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public int id() {
        return THREAD_POOL.value;
    }

    public Any info() {
        Any any = new org.apache.yoko.orb.CORBA.Any();
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

public final class DispatchStrategyFactory_impl extends LocalObject implements DispatchStrategyFactory {
    private static final Logger logger = Logger.getLogger(DispatchStrategyFactory.class.getName());
    //
    // A sequence of thread pools. The index in the sequence is the
    // thread pool id.
    //
    private final Vector<ThreadPool> pools_ = new Vector<>();

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
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);
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
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);
        }

        if (id < 0 || id > pools_.size() || pools_.elementAt(id) == null) {
            throw new InvalidThreadPool();
        }

        //
        // Destroy the ThreadPool
        //
        pools_.elementAt(id).destroy();

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
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);
        }

        if (id < 0 || id > pools_.size() || pools_.elementAt(id) == null) {
            throw new InvalidThreadPool();
        }

        return new DispatchThreadPool_impl(id, pools_.elementAt(id));
    }

    public synchronized DispatchStrategy create_same_thread_strategy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);
        }
        return new DispatchSameThread_impl();
    }

    public synchronized DispatchStrategy create_thread_per_request_strategy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);
        }
        return new DispatchThreadPerRequest_impl();
    }

    public synchronized DispatchStrategy create_default_dispatch_strategy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);
        }

        //
        // Get the ORB properties
        //
        Properties properties = orbInstance_.getProperties();

        //
        // Set the dispatch strategy policy as specified by the
        // conc_model property
        //
        String value = properties.getProperty("yoko.orb.oa.conc_model");

        if (value != null) {
            logger.fine("Defined concurrency model is " + value);
            switch (value) {
                case "threaded":
                case "thread_per_client":
                    logger.fine("Using same thread dispatch strategy");
                    return create_same_thread_strategy();
                case "thread_per_request":
                    logger.fine("Using thread per request dispatch strategy");
                    return create_thread_per_request_strategy();
                case "thread_pool":
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
                        throw Assert.fail(ex);
                    }
                default:
                    String err = "yoko.orb.oa.conc_model: Unknown value `";
                    err += value;
                    err += "'";
                    orbInstance_.getLogger().warning(err);
                    break;
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

    public DispatchStrategyFactory_impl() {}

    public synchronized void _OB_setORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    protected synchronized void _OB_destroy() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);

        destroy_ = true;

        orbInstance_ = null;

        //
        // Destroy each of the thread pools
        //
        for (int i = 0; i < pools_.size(); i++) {
            ThreadPool pool = pools_.elementAt(i);
            if (pool != null) {
                pool.destroy();
                pools_.setElementAt(null, i);
            }
        }
    }
}

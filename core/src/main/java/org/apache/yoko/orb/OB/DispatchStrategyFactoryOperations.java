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

//
// IDL:orb.yoko.apache.org/OB/DispatchStrategyFactory:1.0
//
/**
 *
 * This interface is a factory to create dispatch strategies, and to
 * manage thread pools.
 *
 * @see DispatchStrategy
 *
 **/

public interface DispatchStrategyFactoryOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategyFactory/create_thread_pool:1.0
    //
    /**
     *
     * Create a thread pool containing nthreads.
     *
     * @param nthreads The number of threads the pool in the pool
     *
     **/

    int
    create_thread_pool(int nthreads);

    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategyFactory/destroy_thread_pool:1.0
    //
    /**
     *
     * Destroy a thread pool with the given id. If the thread pool is
     * is use by an object adapter any new requests will cause an
     * <code>OBJ_ADAPTER</code> exception.
     *
     * @param id The thread pool id
     *
     * @exception org.apache.yoko.orb.OB.InvalidThreadPool If the thread pool id is valid.
     *
     **/

    void
    destroy_thread_pool(int id)
        throws InvalidThreadPool;

    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategyFactory/create_thread_pool_strategy:1.0
    //
    /**
     *
     * Create a thread pool dispatch strategy.
     *
     * @param id The thread pool id
     *
     * @return A dispatch strategy
     *
     * @exception org.apache.yoko.orb.OB.InvalidThreadPool If the thread pool id is valid.
     *
     **/

    DispatchStrategy
    create_thread_pool_strategy(int id)
        throws InvalidThreadPool;

    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategyFactory/create_same_thread_strategy:1.0
    //
    /**
     *
     * Create a same thread dispatch strategy.
     *
     * @return A dispatch strategy
     *
     **/

    DispatchStrategy
    create_same_thread_strategy();

    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategyFactory/create_thread_per_request_strategy:1.0
    //
    /**
     *
     * Create a same thread per request dispatch strategy.
     *
     * @return A dispatch strategy
     *
     **/

    DispatchStrategy
    create_thread_per_request_strategy();

    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategyFactory/create_default_dispatch_strategy:1.0
    //
    /**
     *
     * Create a default dispatch strategy. The default dispatch
     * strategy is created according to the ooc.orb.oa.conc_model
     * property.
     *
     * @return A dispatch strategy
     *
     **/

    DispatchStrategy
    create_default_dispatch_strategy();
}

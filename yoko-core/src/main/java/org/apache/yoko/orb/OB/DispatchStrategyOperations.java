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
// IDL:orb.yoko.apache.org/OB/DispatchStrategy:1.0
//
/**
 *
 * This interface represents a dispatch strategy. To dispatch a
 * request the ORB will pass a DispatchRequest. The dispatch strategy
 * should call DispatchRequest::invoke() in the correct thread
 * context.
 *
 * @see DispatchRequest
 * @see DispatchStrategyFactory
 *
 **/

public interface DispatchStrategyOperations
{
    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategy/id:1.0
    //
    /**
     *
     * Determine the id of this DispatchStrategy.  The ids of the
     * built-in DispatchStrategy objects are <code>SAME_THREAD</code>,
     * <code>THREAD_PER_REQUEST</code> and <code>THREAD_POOL</code>.
     * Use DispatchStrategyFactory to create instances of these objects.
     *
     **/

    int
    id();

    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategy/info:1.0
    //
    /**
     *
     * Determine information specific to this DispatchStrategy.
     *
     **/

    org.omg.CORBA.Any
    info();

    //
    // IDL:orb.yoko.apache.org/OB/DispatchStrategy/dispatch:1.0
    //
    /**
     *
     * Called to cause a DispatchRequest to be run.
     *
     * @param r The request to execute.
     *
     **/

    void
    dispatch(DispatchRequest r);
}

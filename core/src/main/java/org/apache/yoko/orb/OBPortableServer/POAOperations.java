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

package org.apache.yoko.orb.OBPortableServer;

//
// IDL:orb.yoko.apache.org/OBPortableServer/POA:1.0
//
/**
 *
 * This interface is a proprietary extension to the standard POA.
 *
 * @see PortableServer::POAManager
 *
 **/

public interface POAOperations extends org.omg.PortableServer.POAOperations
{
    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POA/the_policies:1.0
    //
    /**
     *
     * Determine the policies that this POA was created with
     *
     **/

    org.omg.CORBA.Policy[]
    the_policies();

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POA/the_dispatch_strategy:1.0
    //
    /**
     *
     * Determine the DispatchStrategy in use by this POA
     *
     **/

    org.apache.yoko.orb.OB.DispatchStrategy
    the_dispatch_strategy();

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POA/adapter_template:1.0
    //
    /**
     *
     * Retrieve the Primary Object Reference Template
     *
     **/

    org.omg.PortableInterceptor.ObjectReferenceTemplate
    adapter_template();

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POA/current_factory:1.0
    //
    /**
     *
     * Retrieve the Secondary Object Reference Template
     *
     **/

    org.omg.PortableInterceptor.ObjectReferenceFactory
    current_factory();

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POA/the_ORB:1.0
    //
    /**
     *
     * Retrieve the ORB on which the POA was created
     *
     **/

    org.omg.CORBA.ORB
    the_ORB();

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POA/create_synchronization_policy:1.0
    //
    /**
     *
     * Create a synchronization policy
     *
     * @param value The SynchronizationPolicyValue
     *
     * @return A new SynchronizationPolicy
     *
     **/

    SynchronizationPolicy
    create_synchronization_policy(SynchronizationPolicyValue value);

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POA/create_dispatch_strategy_policy:1.0
    //
    /**
     *
     * Create a dispatch strategy policy
     *
     * @param value The DispatchStrategyPolicy
     *
     * @return A new DisptachStrategy
     *
     **/

    DispatchStrategyPolicy
    create_dispatch_strategy_policy(org.apache.yoko.orb.OB.DispatchStrategy value);
}

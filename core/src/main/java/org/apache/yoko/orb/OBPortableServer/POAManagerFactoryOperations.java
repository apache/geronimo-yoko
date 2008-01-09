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
// IDL:orb.yoko.apache.org/OBPortableServer/POAManagerFactory:1.0
//
/**
 *
 * This interface is a proprietary extension of the standard
 * POAManagerFactory.
 *
 * @see PortableServer::POAManagerFactory
 *
 **/

public interface POAManagerFactoryOperations extends org.omg.PortableServer.POAManagerFactoryOperations
{
    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POAManagerFactory/destroy:1.0
    //
    /**
     *
     * Destroy all POAManagers.
     *
     **/

    void
    destroy();

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POAManagerFactory/create_endpoint_configuration_policy:1.0
    //
    /**
     *
     * These policy factory methods creates POAManager policies only.
     *
     **/

    EndpointConfigurationPolicy
    create_endpoint_configuration_policy(String value)
        throws org.omg.CORBA.PolicyError;

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POAManagerFactory/create_communications_concurrency_policy:1.0
    //
    /***/

    CommunicationsConcurrencyPolicy
    create_communications_concurrency_policy(short value)
        throws org.omg.CORBA.PolicyError;

    //
    // IDL:orb.yoko.apache.org/OBPortableServer/POAManagerFactory/create_giop_version_policy:1.0
    //
    /***/

    GIOPVersionPolicy
    create_giop_version_policy(short value)
        throws org.omg.CORBA.PolicyError;
}

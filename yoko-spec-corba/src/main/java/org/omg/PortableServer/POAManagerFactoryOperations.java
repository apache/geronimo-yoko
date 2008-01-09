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

package org.omg.PortableServer;

//
// IDL:omg.org/PortableServer/POAManagerFactory:1.0
//
/**
 *
 * A Factory for PortableServer::POAManager.
 *
 * @see PortableServer::POAManager
 *
 **/

public interface POAManagerFactoryOperations
{
    //
    // IDL:omg.org/PortableServer/POAManagerFactory/create_POAManager:1.0
    //
    /**
     *
     * Create a new POAManager. The configuration properties for this
     * POA manager will be held in "ooc.orb.poamanager.<name>." If the
     * name is the empty string then a POA manager with a unique name
     * will be created. The POAManager will be created with a single
     * IIOP Acceptor.
     *
     * @param id The POAManager id.
     *
     * @param policies The policy list for the POAManager.
     *
     * @return A POAManager.
     *
     * @exception org.omg.PortableServer.POAManagerFactoryPackage.ManagerAlreadyExists If a POAManager with the same
     * name already exists.
     *
     * @exception PolicyError If the supplied policy list contains
     * a conflict.
     *
     **/

    POAManager
    create_POAManager(String id,
                      org.omg.CORBA.Policy[] policies)
        throws org.omg.PortableServer.POAManagerFactoryPackage.ManagerAlreadyExists,
               org.omg.CORBA.PolicyError;

    //
    // IDL:omg.org/PortableServer/POAManagerFactory/list:1.0
    //
    /**
     *
     * List all POA Managers.
     *
     * @return A sequence of POAManagers.
     *
     **/

    POAManager[]
    list();

    //
    // IDL:omg.org/PortableServer/POAManagerFactory/find:1.0
    //
    /**
     *
     * Find a specific POA Manager.
     *
     * @param id The POAManager id.
     *
     * @return The POAManager given by id, or null if not found.
     *
     **/

    POAManager
    find(String id);
}

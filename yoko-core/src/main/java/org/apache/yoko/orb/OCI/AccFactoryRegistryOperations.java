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

package org.apache.yoko.orb.OCI;

//
// IDL:orb.yoko.apache.org/OCI/AccFactoryRegistry:1.0
//
/**
 *
 * A registry for Acceptor factories.
 *
 * @see Acceptor
 * @see AccFactory
 *
 **/

public interface AccFactoryRegistryOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/AccFactoryRegistry/add_factory:1.0
    //
    /**
     *
     * Adds an Acceptor factory to the registry.
     *
     * @param factory The Acceptor factory to add.
     *
     * @exception org.apache.yoko.orb.OCI.FactoryAlreadyExists If a factory already exists with the
     * same plugin id as the given factory.
     *
     **/

    void
    add_factory(AccFactory factory)
        throws FactoryAlreadyExists;

    //
    // IDL:orb.yoko.apache.org/OCI/AccFactoryRegistry/get_factory:1.0
    //
    /**
     *
     * Returns the factory with the given plugin id.
     *
     * @param id The plugin id.
     *
     * @return The Acceptor factory.
     *
     * @exception org.apache.yoko.orb.OCI.NoSuchFactory If no factory was found with a matching
     * plugin id.
     *
     **/

    AccFactory
    get_factory(String id)
        throws NoSuchFactory;

    //
    // IDL:orb.yoko.apache.org/OCI/AccFactoryRegistry/get_factories:1.0
    //
    /**
     *
     * Returns all registered factories.
     *
     * @return The Acceptor factories.
     *
     **/

    AccFactory[]
    get_factories();
}

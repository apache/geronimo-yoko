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
// IDL:orb.yoko.apache.org/OCI/AccFactory:1.0
//
/**
 *
 * An interface for an AccFactory object, which is used by CORBA
 * servers to create Acceptors.
 *
 * @see Acceptor
 * @see AccFactoryRegistry
 *
 **/

public interface AccFactoryOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/AccFactory/id:1.0
    //
    /** The plugin id. */

    String
    id();

    //
    // IDL:orb.yoko.apache.org/OCI/AccFactory/tag:1.0
    //
    /** The profile id tag. */

    int
    tag();

    //
    // IDL:orb.yoko.apache.org/OCI/AccFactory/create_acceptor:1.0
    //
    /**
     *
     * Create an Acceptor using the given configuration parameters.
     * Refer to the plug-in documentation for a description of the
     * configuration parameters supported for a particular protocol.
     *
     * @param params The configuration parameters.
     *
     * @return The new Acceptor.
     *
     * @exception org.apache.yoko.orb.OCI.InvalidParam If any of the parameters are invalid.
     *
     **/

    Acceptor
    create_acceptor(String[] params)
        throws InvalidParam;

    //
    // IDL:orb.yoko.apache.org/OCI/AccFactory/change_key:1.0
    //
    /**
     *
     * Change the object-key in the IOR profile for this given
     * protocol.
     *
     * @param ior The IOR
     *
     * @param key The new object key
     *
     **/

    void
    change_key(org.omg.IOP.IORHolder ior,
               byte[] key);

    //
    // IDL:orb.yoko.apache.org/OCI/AccFactory/get_info:1.0
    //
    /**
     *
     * Returns the information object associated with the Acceptor
     * factory.
     *
     * @return The Acceptor
     *
     **/

    AccFactoryInfo
    get_info();
}

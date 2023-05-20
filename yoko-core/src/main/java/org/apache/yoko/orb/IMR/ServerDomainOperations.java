/*
 * Copyright 2010 IBM Corporation and others.
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
package org.apache.yoko.orb.IMR;

//
// IDL:orb.yoko.apache.org/IMR/ServerDomain:1.0
//
/***/

public interface ServerDomainOperations extends DomainOperations
{
    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/get_server_factory:1.0
    //
    /**
     *
     * Retrieve the Server Factory.
     *
     * @return The reference to the server factory.
     *
     **/

    ServerFactory
    get_server_factory();

    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/create_oad_record:1.0
    //
    /**
     *
     * Create OAD record.
     *
     * @param host The host name
     *
     **/

    void
    create_oad_record(String host)
        throws OADAlreadyExists;

    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/remove_oad_record:1.0
    //
    /**
     *
     * Remove an OAD record.
     *
     * @param host The OAD's host
     *
     **/

    void
    remove_oad_record(String host)
        throws NoSuchOAD,
               OADRunning;

    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/get_oad_record:1.0
    //
    /**
     *
     * Retrieve an OAD record.
     *
     * @param host The OAD's host
     *
     * @return The OAD record.
     *
     **/

    OADInfo
    get_oad_record(String host)
        throws NoSuchOAD;

    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/list_oads:1.0
    //
    /**
     *
     * List the Object Activation Daemons.
     *
     * @return A sequence of OADInfo's.
     *
     **/

    OADInfo[]
    list_oads();
}

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
// IDL:orb.yoko.apache.org/OCI/Connector:1.0
//
/**
 *
 * An interface for Connector objects. A Connector is used by CORBA
 * clients to initiate a connection to a server. It also provides
 * operations for the management of IOR profiles.
 *
 * @see ConFactory
 * @see Transport
 *
 **/

public interface ConnectorOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/Connector/id:1.0
    //
    /** The plugin id. */

    String
    id();

    //
    // IDL:orb.yoko.apache.org/OCI/Connector/tag:1.0
    //
    /** The profile id tag. */

    int
    tag();

    //
    // IDL:orb.yoko.apache.org/OCI/Connector/connect:1.0
    //
    /**
     *
     * Used by CORBA clients to establish a connection to a CORBA
     * server. It returns a Transport object, which can be used for
     * sending and receiving octet streams to and from the server.
     *
     * @return The new Transport object.
     *
     * @exception TRANSIENT If the server cannot be contacted.
     * @exception COMM_FAILURE In case of other errors.
     *
     **/

    Transport
    connect();

    //
    // IDL:orb.yoko.apache.org/OCI/Connector/connect_timeout:1.0
    //
    /**
     *
     * Similar to <code>connect</code>, but it is possible to specify
     * a timeout. On return the caller can test whether there was a
     * timeout by checking whether a nil object reference was returned.
     *
     * @param timeout The timeout value in milliseconds.
     *
     * @return The new Transport object.
     *
     * @exception TRANSIENT If the server cannot be contacted.
     * @exception COMM_FAILURE In case of other errors.
     *
     **/

    Transport
    connect_timeout(int timeout);

    //
    // IDL:orb.yoko.apache.org/OCI/Connector/get_usable_profiles:1.0
    //
    /**
     *
     * From the given IOR and list of policies, get basic information
     * about all profiles for which this Connector can be used.
     *
     * @param ref The IOR from which the profiles are taken.
     *
     * @param policies The policies that must be satisfied.
     *
     * @return The sequence of basic information about profiles. If
     * this sequence is empty, there is no profile in the IOR that
     * matches this Connector and the list of policies.
     *
     **/

    ProfileInfo[]
    get_usable_profiles(org.omg.IOP.IOR ref,
                        org.omg.CORBA.Policy[] policies);

    //
    // IDL:orb.yoko.apache.org/OCI/Connector/equal:1.0
    //
    /**
     *
     * Find out whether this Connector is equal to another
     * Connector. Two Connectors are considered equal if they are
     * interchangeable.
     *
     * @param con The connector to compare with.
     *
     * @return <code>TRUE</code> if the Connectors are equal,
     * <code>FALSE</code> otherwise.
     *
     **/

    boolean
    equal(Connector con);

    //
    // IDL:orb.yoko.apache.org/OCI/Connector/get_info:1.0
    //
    /**
     *
     * Returns the information object associated with
     * the Connector.
     *
     * @return The Connector information object.
     *
     **/

    ConnectorInfo
    get_info();
}

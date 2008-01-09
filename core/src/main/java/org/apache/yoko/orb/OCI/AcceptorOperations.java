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
// IDL:orb.yoko.apache.org/OCI/Acceptor:1.0
//
/**
 *
 * An interface for an Acceptor object, which is used by CORBA
 * servers to accept client connection requests. It also provides
 * operations for the management of IOR profiles.
 *
 * @see AccRegistry
 * @see AccFactory
 * @see Transport
 *
 **/

public interface AcceptorOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/id:1.0
    //
    /** The plugin id. */

    String
    id();

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/tag:1.0
    //
    /** The profile id tag. */

    int
    tag();

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/handle:1.0
    //
    /**
     *
     * The "handle" for this Acceptor. Like with the handle for
     * Transports, the handle may <em>only</em> be used with
     * operations like <code>select()</code>. A handle value of -1
     * indicates that the protocol plug-in does not support
     * "selectable" Transports.
     *
     **/

    int
    handle();

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/close:1.0
    //
    /**
     *
     * Closes the Acceptor. <code>accept</code> or <code>listen</code>
     * may not be called after <code>close</code> has been called.
     *
     * @exception COMM_FAILURE In case of an error.
     *
     **/

    void
    close();

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/shutdown:1.0
    //
    /**
     *
     * Shutdown the Acceptor. After shutdown, the socket will not
     * listen to further connection requests.
     *
     * @exception COMM_FAILURE In case of an error.
     *
     **/

    void
    shutdown();

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/listen:1.0
    //
    /**
     *
     * Sets the acceptor up to listen for incoming connections. Until
     * this method is called on the acceptor, new connection requests
     * should result in a connection request failure.
     * 
     * @exception COMM_FAILURE In case of an error.
     *
     **/

    void
    listen();

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/accept:1.0
    //
    /**
     *
     * Used by CORBA servers to accept client connection requests. It
     * returns a Transport object, which can be used for sending and
     * receiving octet streams to and from the client.
     *
     * @param block If set to <code>TRUE</code>, the operation blocks
     * until a new connection has been accepted. If set to
     * <code>FALSE</code>, the operation returns a nil object
     * reference if there is no new connection ready to be accepted.
     *
     * @return The new Transport object.
     *
     * @exception COMM_FAILURE In case of an error.
     *
     **/

    Transport
    accept(boolean block);

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/connect_self:1.0
    //
    /**
     *
     * Connect to this acceptor. This operation can be used to unblock
     * threads that are blocking in <code>accept</code>.
     *
     * @return The new Transport object.
     *
     * @exception TRANSIENT If the server cannot be contacted.
     * @exception COMM_FAILURE In case of other errors.
     *
     **/

    Transport
    connect_self();

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/add_profiles:1.0
    //
    /**
     *
     * Add new profiles that match this Acceptor to an IOR.
     *
     * @param profile_info The basic profile information to use for
     * the new profiles.
     *
     * @param ref The IOR.
     *
     **/

    void
    add_profiles(ProfileInfo profile_info, org.apache.yoko.orb.OBPortableServer.POAPolicies policies, 
                 org.omg.IOP.IORHolder ref);

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/get_local_profiles:1.0
    //
    /**
     *
     * From the given IOR, get basic information about all profiles
     * for which are local to this Acceptor.
     *
     * @param ref The IOR from which the profiles are taken.
     *
     * @return The sequence of basic information about profiles. If
     * this sequence is empty, there is no profile in the IOR that
     * is local to the Acceptor.
     *
     **/

    ProfileInfo[]
    get_local_profiles(org.omg.IOP.IOR ref);

    //
    // IDL:orb.yoko.apache.org/OCI/Acceptor/get_info:1.0
    //
    /**
     *
     * Returns the information object associated with
     * the Acceptor.
     *
     * @return The Acceptor information object.
     *
     **/

    AcceptorInfo
    get_info();
}

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
// IDL:orb.yoko.apache.org/OCI/TransportInfo:1.0
//
/**
 *
 * Information on an OCI Transport object. Objects of this type must
 * be narrowed to a Transport information object for a concrete
 * protocol implementation, for example to
 * <code>OCI::IIOP::TransportInfo</code> in case the plug-in
 * implements IIOP.
 *
 * @see Transport
 *
 **/

public interface TransportInfoOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/id:1.0
    //
    /** The plugin id. */

    String
    id();

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/tag:1.0
    //
    /** The profile id tag. */

    int
    tag();

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/connector_info:1.0
    //
    /**
     *
     * The ConnectorInfo object for the Connector that created the
     * Transport object that this TransportInfo object belongs to.
     * If the Transport for this TransportInfo was not created by a
     * Connector, this attribute is set to the nil object reference.
     *
     **/

    ConnectorInfo
    connector_info();

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/acceptor_info:1.0
    //
    /**
     *
     * The AcceptorInfo object for the Acceptor that created the
     * Transport object that this TransportInfo object belongs to.
     * If the Transport for this TransportInfo was not created by an
     * Acceptor, this attribute is set to the nil object reference.
     *
     **/

    AcceptorInfo
    acceptor_info();

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/origin:1.0
    //
    /**
     *
     * The origin indicates whether the transport was originally
     * created by a server side accept or a client side connect.  This
     * is information is required for connection lifecycle management
     * in bidirectional communications.  This information cannot be
     * inferred by the connector_info and acceptor_info attributes as
     * they may both be set in a bidirectional case.
     *
     * @return <code>CLIENT_SIDE</code> if transport was initially
     * created as a client side connection.  <code>SERVER_SIDE</code>
     * if transport was initially created to handle incoming requests.
     *
     **/

    short
    origin();

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/describe:1.0
    //
    /**
     *
     * Returns a human readable description of the transport.
     *
     * @return The description.
     *
     **/

    String
    describe();

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/add_close_cb:1.0
    //
    /**
     *
     * Add a callback that is called before a connection is closed. If
     * the callback has already been registered, this method has no
     * effect.
     *
     * @param cb The callback to add.
     *
     **/

    void
    add_close_cb(CloseCB cb);

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/remove_close_cb:1.0
    //
    /**
     *
     * Remove a close callback. If the callback was not registered,
     * this method has no effect.
     *
     * @param cb The callback to remove.
     *
     **/

    void
    remove_close_cb(CloseCB cb);

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/get_service_contexts:1.0
    //
    /**
     *
     * Returns a sequence of service contexts for this transport based
     * on the policies.  Certain policies result in service contexts
     * being applied to requests.
     *
     * @param policies The CORBA Policy list.
     *
     * @return The service contexts for the given polices.
     *
     **/

    org.omg.IOP.ServiceContext[]
    get_service_contexts(org.omg.CORBA.Policy[] policies);

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/handle_service_contexts:1.0
    //
    /**
     *
     * Handles service contexts that might be received during a
     * request.  This allows transports to change their internal state
     * or behavior during their runtime.
     *
     * @param contexts The service context list
     *
     **/

    void
    handle_service_contexts(org.omg.IOP.ServiceContext[] contexts);

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/received_bidir_SCL:1.0
    //
    /**
     *
     * Queries whether this' transport has received a BiDir SCL in a
     * request.
     *
     **/

    boolean
    received_bidir_SCL();

    //
    // IDL:orb.yoko.apache.org/OCI/TransportInfo/endpoint_alias_match:1.0
    //
    /**
     *
     * Uses the BiDir SCL information in this TransportInfo to check
     * whether we can be used as a BiDir connection alias instead of
     * creating a new connection with the information specified in the
     * ConnectorInfo paramater
     *
     **/

    boolean
    endpoint_alias_match(ConnectorInfo connInfo);
}

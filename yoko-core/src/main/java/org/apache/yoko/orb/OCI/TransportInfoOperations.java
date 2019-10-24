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

import org.apache.yoko.orb.IOP.ServiceContexts;
import org.omg.CORBA.Policy;

/**
 * Information on an OCI Transport object. Objects of this type must
 * be narrowed to a Transport information object for a concrete
 * protocol implementation, for example to
 * <code>OCI::IIOP::TransportInfo</code> in case the plug-in
 * implements IIOP.
 *
 * @see Transport
 **/

public interface TransportInfoOperations {
    /** The plugin id. */
    String id();

    /** The profile id tag. */
    int tag();

    /**
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
     */
    short origin();

    /**
     * Returns a human readable description of the transport.
     *
     * @return The description.
     */

    String describe();

    /**
     * Returns a sequence of service contexts for this transport based
     * on the policies.  Certain policies result in service contexts
     * being applied to requests.
     *
     * @param policies The CORBA Policy list.
     *
     * @return The service contexts for the given polices.
     */
    ServiceContexts get_service_contexts(Policy[] policies);

    /**
     * Handles service contexts that might be received during a
     * request.  This allows transports to change their internal state
     * or behavior during their runtime.
     *
     * @param contexts The service context list
     */
    void handle_service_contexts(ServiceContexts contexts);

    /**
     * Queries whether this transport received a BiDir service context in a request.
     */
    boolean received_bidir_service_context();

    /**
     * Uses the BiDir service context information for this transport to
     * determine whether it can be used as a BiDir connection alias instead
     * of creating a new connection with the information specified in the
     * ConnectorInfo parameter
     */
    boolean endpoint_alias_match(ConnectorInfo connInfo);
}

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
// IDL:orb.yoko.apache.org/OCI/ConnectCB:1.0
//
/**
 *
 * An interface for a connect callback object.
 *
 * @see ConnectorInfo
 *
 **/

public interface ConnectCBOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/ConnectCB/connect_cb:1.0
    //
    /**
     *
     * Called after a new connection has been established. If the
     * application wishes to reject the connection
     * <code>CORBA::NO_PERMISSION</code> may be raised.
     *
     * @param transport_info The TransportInfo for the new connection.
     *
     **/

    void
    connect_cb(TransportInfo transport_info);
}

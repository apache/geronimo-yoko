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

package org.apache.yoko.orb.OCI.IIOP;

//
// IDL:orb.yoko.apache.org/OCI/IIOP/AcceptorInfo:1.0
//
/**
 *
 * Information on an IIOP OCI Acceptor object.
 *
 * @see Acceptor
 * @see AcceptorInfo
 *
 **/

public interface AcceptorInfoOperations extends org.apache.yoko.orb.OCI.AcceptorInfoOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/IIOP/AcceptorInfo/hosts:1.0
    //
    /** Hostnames used for creation of IIOP object references. */

    String[]
    hosts();

    //
    // IDL:orb.yoko.apache.org/OCI/IIOP/AcceptorInfo/addr:1.0
    //
    /** The local IP address on which this acceptor accepts. */

    String
    addr();

    //
    // IDL:orb.yoko.apache.org/OCI/IIOP/AcceptorInfo/port:1.0
    //
    /** The local port on which this acceptor accepts. */

    short
    port();
}

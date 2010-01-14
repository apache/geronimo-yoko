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
// IDL:orb.yoko.apache.org/OCI/Current:1.0
//
/**
 *
 * Interface to access Transport and Acceptor information objects
 * related to the current request.
 *
 **/

public interface CurrentOperations extends org.omg.CORBA.CurrentOperations
{
    //
    // IDL:orb.yoko.apache.org/OCI/Current/get_oci_transport_info:1.0
    //
    /**
     *
     * This method returns the Transport information object for the
     * Transport used to invoke the current request.
     *
     * @returns The Transport information object.
     *
     **/

    TransportInfo
    get_oci_transport_info();

    //
    // IDL:orb.yoko.apache.org/OCI/Current/get_oci_acceptor_info:1.0
    //
    /**
     *
     * This method returns the Acceptor information object for the
     * Acceptor which created the Transport used to invoke the current
     * request.
     *
     * @returns The Acceptor information object.
     *
     **/

    AcceptorInfo
    get_oci_acceptor_info();
}

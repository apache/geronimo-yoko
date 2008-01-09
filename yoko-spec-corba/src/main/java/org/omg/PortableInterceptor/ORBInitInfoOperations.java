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

package org.omg.PortableInterceptor;

//
// IDL:omg.org/PortableInterceptor/ORBInitInfo:1.0
//
/***/

public interface ORBInitInfoOperations
{
    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/arguments:1.0
    //
    /***/

    String[]
    arguments();

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/orb_id:1.0
    //
    /***/

    String
    orb_id();

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/codec_factory:1.0
    //
    /***/

    org.omg.IOP.CodecFactory
    codec_factory();

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/register_initial_reference:1.0
    //
    /***/

    void
    register_initial_reference(String id,
                               org.omg.CORBA.Object obj)
        throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/resolve_initial_references:1.0
    //
    /***/

    org.omg.CORBA.Object
    resolve_initial_references(String id)
        throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/add_client_request_interceptor:1.0
    //
    /***/

    void
    add_client_request_interceptor(ClientRequestInterceptor interceptor)
        throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/add_server_request_interceptor:1.0
    //
    /***/

    void
    add_server_request_interceptor(ServerRequestInterceptor interceptor)
        throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/add_ior_interceptor:1.0
    //
    /***/

    void
    add_ior_interceptor(IORInterceptor interceptor)
        throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/allocate_slot_id:1.0
    //
    /***/

    int
    allocate_slot_id();

    //
    // IDL:omg.org/PortableInterceptor/ORBInitInfo/register_policy_factory:1.0
    //
    /***/

    void
    register_policy_factory(int type,
                            PolicyFactory policy_factory);
}

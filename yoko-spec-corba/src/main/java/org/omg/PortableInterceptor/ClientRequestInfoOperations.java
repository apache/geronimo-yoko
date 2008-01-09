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
// IDL:omg.org/PortableInterceptor/ClientRequestInfo:1.0
//
/***/

public interface ClientRequestInfoOperations extends RequestInfoOperations
{
    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/target:1.0
    //
    /***/

    org.omg.CORBA.Object
    target();

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/effective_target:1.0
    //
    /***/

    org.omg.CORBA.Object
    effective_target();

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/effective_profile:1.0
    //
    /***/

    org.omg.IOP.TaggedProfile
    effective_profile();

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/received_exception:1.0
    //
    /***/

    org.omg.CORBA.Any
    received_exception();

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/received_exception_id:1.0
    //
    /***/

    String
    received_exception_id();

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/get_effective_component:1.0
    //
    /***/

    org.omg.IOP.TaggedComponent
    get_effective_component(int id);

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/get_effective_components:1.0
    //
    /***/

    org.omg.IOP.TaggedComponent[]
    get_effective_components(int id);

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/get_request_policy:1.0
    //
    /***/

    org.omg.CORBA.Policy
    get_request_policy(int type);

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInfo/add_request_service_context:1.0
    //
    /***/

    void
    add_request_service_context(org.omg.IOP.ServiceContext service_context,
                                boolean replace);
}

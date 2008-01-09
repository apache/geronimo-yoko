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
// IDL:omg.org/PortableInterceptor/ServerRequestInfo:1.0
//
/***/

public interface ServerRequestInfoOperations extends RequestInfoOperations
{
    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/sending_exception:1.0
    //
    /***/

    org.omg.CORBA.Any
    sending_exception();

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/object_id:1.0
    //
    /***/

    byte[]
    object_id();

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/adapter_id:1.0
    //
    /***/

    byte[]
    adapter_id();

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/target_most_derived_interface:1.0
    //
    /***/

    String
    target_most_derived_interface();

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/server_id:1.0
    //
    /***/

    String
    server_id();

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/orb_id:1.0
    //
    /***/

    String
    orb_id();

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/adapter_name:1.0
    //
    /***/

    String[]
    adapter_name();

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/get_server_policy:1.0
    //
    /***/

    org.omg.CORBA.Policy
    get_server_policy(int type);

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/set_slot:1.0
    //
    /***/

    void
    set_slot(int id,
             org.omg.CORBA.Any data)
        throws InvalidSlot;

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/target_is_a:1.0
    //
    /***/

    boolean
    target_is_a(String id);

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInfo/add_reply_service_context:1.0
    //
    /***/

    void
    add_reply_service_context(org.omg.IOP.ServiceContext service_context,
                              boolean replace);
}

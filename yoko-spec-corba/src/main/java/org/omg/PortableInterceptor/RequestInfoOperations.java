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
// IDL:omg.org/PortableInterceptor/RequestInfo:1.0
//
/***/

public interface RequestInfoOperations
{
    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/request_id:1.0
    //
    /***/

    int
    request_id();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/operation:1.0
    //
    /***/

    String
    operation();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/arguments:1.0
    //
    /***/

    org.omg.Dynamic.Parameter[]
    arguments();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/exceptions:1.0
    //
    /***/

    org.omg.CORBA.TypeCode[]
    exceptions();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/contexts:1.0
    //
    /***/

    String[]
    contexts();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/operation_context:1.0
    //
    /***/

    String[]
    operation_context();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/result:1.0
    //
    /***/

    org.omg.CORBA.Any
    result();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/response_expected:1.0
    //
    /***/

    boolean
    response_expected();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/sync_scope:1.0
    //
    /***/

    short
    sync_scope();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/reply_status:1.0
    //
    /***/

    short
    reply_status();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/forward_reference:1.0
    //
    /***/

    org.omg.CORBA.Object
    forward_reference();

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/get_slot:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_slot(int id)
        throws InvalidSlot;

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/get_request_service_context:1.0
    //
    /***/

    org.omg.IOP.ServiceContext
    get_request_service_context(int id);

    //
    // IDL:omg.org/PortableInterceptor/RequestInfo/get_reply_service_context:1.0
    //
    /***/

    org.omg.IOP.ServiceContext
    get_reply_service_context(int id);
}

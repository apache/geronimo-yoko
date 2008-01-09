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
// IDL:omg.org/PortableInterceptor/ServerRequestInterceptor:1.0
//
/***/

public interface ServerRequestInterceptorOperations extends InterceptorOperations
{
    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInterceptor/receive_request_service_contexts:1.0
    //
    /***/

    void
    receive_request_service_contexts(ServerRequestInfo ri)
        throws ForwardRequest;

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInterceptor/receive_request:1.0
    //
    /***/

    void
    receive_request(ServerRequestInfo ri)
        throws ForwardRequest;

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInterceptor/send_reply:1.0
    //
    /***/

    void
    send_reply(ServerRequestInfo ri);

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInterceptor/send_exception:1.0
    //
    /***/

    void
    send_exception(ServerRequestInfo ri)
        throws ForwardRequest;

    //
    // IDL:omg.org/PortableInterceptor/ServerRequestInterceptor/send_other:1.0
    //
    /***/

    void
    send_other(ServerRequestInfo ri)
        throws ForwardRequest;
}

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.omg.PortableInterceptor;

//
// IDL:omg.org/PortableInterceptor/ClientRequestInterceptor:1.0
//
/***/

public interface ClientRequestInterceptorOperations extends InterceptorOperations
{
    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInterceptor/send_request:1.0
    //
    /***/

    void
    send_request(ClientRequestInfo ri)
        throws ForwardRequest;

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInterceptor/send_poll:1.0
    //
    /***/

    void
    send_poll(ClientRequestInfo ri);

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInterceptor/receive_reply:1.0
    //
    /***/

    void
    receive_reply(ClientRequestInfo ri);

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInterceptor/receive_exception:1.0
    //
    /***/

    void
    receive_exception(ClientRequestInfo ri)
        throws ForwardRequest;

    //
    // IDL:omg.org/PortableInterceptor/ClientRequestInterceptor/receive_other:1.0
    //
    /***/

    void
    receive_other(ClientRequestInfo ri)
        throws ForwardRequest;
}

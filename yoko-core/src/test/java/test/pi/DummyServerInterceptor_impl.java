/*
 * Copyright 2010 IBM Corporation and others.
 *
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
package test.pi;

import org.omg.PortableInterceptor.*;

final class DummyServerInterceptor_impl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.ServerRequestInterceptor {
    //
    // IDL to Java Mapping
    //

    public String name() {
        return "dummy";
    }

    public void destroy() {
    }

    public void receive_request_service_contexts(ServerRequestInfo ri) {
    }

    public void receive_request(ServerRequestInfo ri) {
    }

    public void send_reply(ServerRequestInfo ri) {
    }

    public void send_other(ServerRequestInfo ri) {
    }

    public void send_exception(ServerRequestInfo ri) {
    }
}

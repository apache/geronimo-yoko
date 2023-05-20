/*
 * Copyright 2022 IBM Corporation and others.
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
package test.rmi.exceptionhandling;

import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

public class MyServerRequestInterceptor extends LocalObject implements ServerRequestInterceptor, ORBInitializer {

    @Override
    public void receive_request(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("receive_request(" + arg0.operation() + ")");
    }

    @Override
    public void receive_request_service_contexts(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("receive_request_service_contexts(" + arg0.operation() + ")");
    }

    @Override
    public void send_exception(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("send_exception(" + arg0.operation() + ")");
    }

    @Override
    public void send_other(ServerRequestInfo arg0) throws ForwardRequest {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("send_other(" + arg0.operation() + ")");
    }

    @Override
    public void send_reply(ServerRequestInfo arg0) {
        System.out.printf("%08x: ", Thread.currentThread().getId());
        System.out.println("send_reply(" + arg0.operation() + ")");
    }

    @Override
    public void destroy() {
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }

    @Override
    public void post_init(ORBInitInfo arg0) {
        try {
            arg0.add_server_request_interceptor(this);
        } catch (DuplicateName e) {
            throw new Error(e);
        }
    }

    @Override
    public void pre_init(ORBInitInfo arg0) {
    }
}

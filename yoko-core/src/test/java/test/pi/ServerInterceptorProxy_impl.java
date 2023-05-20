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

final class ServerInterceptorProxy_impl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.ServerRequestInterceptor {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    private ServerRequestInterceptor interceptor_;

    private int count_;

    protected void finalize() throws Throwable {
        TEST(count_ == 0);

        super.finalize();
    }

    //
    // IDL to Java Mappping
    //

    public String name() {
        if (interceptor_ != null)
            return interceptor_.name();

        return "";
    }

    public void destroy() {
    }

    public void receive_request_service_contexts(ServerRequestInfo ri)
            throws ForwardRequest {
        TEST(count_ >= 0);
        if (interceptor_ != null)
            interceptor_.receive_request_service_contexts(ri);
        count_++;
    }

    public void receive_request(ServerRequestInfo ri) throws ForwardRequest {
        TEST(count_ > 0);

        if (interceptor_ != null)
            interceptor_.receive_request(ri);
    }

    public void send_reply(ServerRequestInfo ri) {
        TEST(count_ > 0);
        count_--;
        if (interceptor_ != null)
            interceptor_.send_reply(ri);
    }

    public void send_other(ServerRequestInfo ri) throws ForwardRequest {
        TEST(count_ > 0);
        count_--;
        if (interceptor_ != null)
            interceptor_.send_other(ri);
    }

    public void send_exception(ServerRequestInfo ri) throws ForwardRequest {
        TEST(count_ > 0);
        count_--;
        if (interceptor_ != null)
            interceptor_.send_exception(ri);
    }

    void _OB_changeInterceptor(ServerRequestInterceptor interceptor) {
        interceptor_ = interceptor;
    }
}

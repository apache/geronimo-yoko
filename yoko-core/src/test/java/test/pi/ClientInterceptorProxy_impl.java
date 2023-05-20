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

final class ClientInterceptorProxy_impl extends org.omg.CORBA.LocalObject
        implements ClientRequestInterceptor {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    private ClientRequestInterceptor interceptor_;

    private int count_;

    //
    // IDL to Java Mapping
    //

    public String name() {
        if (interceptor_ != null)
            return interceptor_.name();

        return "";
    }

    public void destroy() {
    }

    public void send_request(ClientRequestInfo ri) throws ForwardRequest {
        TEST(count_ == 0);

        if (interceptor_ != null)
            interceptor_.send_request(ri);
        count_++;
    }

    public void send_poll(ClientRequestInfo ri) {
        TEST(false);
    }

    public void receive_reply(ClientRequestInfo ri) {
        TEST(count_ == 1);
        count_--;
        if (interceptor_ != null)
            interceptor_.receive_reply(ri);
    }

    public void receive_other(ClientRequestInfo ri) throws ForwardRequest {
        TEST(count_ == 1);
        count_--;
        if (interceptor_ != null)
            interceptor_.receive_other(ri);
    }

    public void receive_exception(ClientRequestInfo ri) throws ForwardRequest {
        TEST(count_ == 1);
        count_--;
        if (interceptor_ != null)
            interceptor_.receive_exception(ri);
    }

    void _OB_changeInterceptor(ClientRequestInterceptor ri) {
        interceptor_ = ri;
    }
}

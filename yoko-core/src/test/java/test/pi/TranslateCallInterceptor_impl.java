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

import org.omg.CORBA.*;
import org.omg.PortableInterceptor.*;
import test.pi.TestInterfacePackage.*;

final class TranslateCallInterceptor_impl extends org.omg.CORBA.LocalObject
        implements ClientRequestInterceptor {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    private SystemException requestEx_;

    private SystemException replyEx_;

    private SystemException exceptionEx_;

    private SystemException expected_;

    public String name() {
        return "CRI";
    }

    public void destroy() {
    }

    public void send_request(ClientRequestInfo ri) {
        if (requestEx_ != null)
            throw requestEx_;
    }

    public void send_poll(ClientRequestInfo ri) {
    }

    public void receive_reply(ClientRequestInfo ri) {
        TEST(expected_ == null);
        if (replyEx_ != null)
            throw replyEx_;
    }

    public void receive_other(ClientRequestInfo ri) {
    }

    public void receive_exception(ClientRequestInfo ri) {
        if (expected_ != null) {
            Any any = ri.received_exception();
            org.omg.CORBA.portable.InputStream in = any.create_input_stream();
            SystemException ex = org.apache.yoko.orb.OB.Util
                    .unmarshalSystemException(in);
            TEST(expected_.getClass().getName().equals(ex.getClass().getName()));
        }
        if (exceptionEx_ != null)
            throw exceptionEx_;
    }

    void throwOnRequest(SystemException ex) {
        requestEx_ = ex;
    }

    void noThrowOnRequest() {
        requestEx_ = null;
    }

    void throwOnReply(SystemException ex) {
        replyEx_ = ex;
    }

    void noThrowOnReply() {
        replyEx_ = null;
    }

    void throwOnException(SystemException ex) {
        exceptionEx_ = ex;
    }

    void noThrowOnException() {
        exceptionEx_ = null;
    }

    void expectException(SystemException ex) {
        expected_ = ex;
    }

    void noExpectedException() {
        expected_ = null;
    }
}

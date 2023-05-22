/*
 * Copyright 2021 IBM Corporation and others.
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

import org.apache.yoko.util.Assert;
import org.omg.PortableInterceptor.*;

final class ServerProxyManager {
    private ServerInterceptorProxy_impl[] p_ = new ServerInterceptorProxy_impl[3];

    ServerProxyManager(ORBInitInfo info) {
        p_[0] = new ServerInterceptorProxy_impl();
        p_[1] = new ServerInterceptorProxy_impl();
        p_[2] = new ServerInterceptorProxy_impl();

        //
        // Register the client side interceptor
        //
        try {
            info.add_server_request_interceptor(p_[1]);
            info.add_server_request_interceptor(p_[2]);
            info.add_server_request_interceptor(p_[0]);
        } catch (org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName ex) {
            throw new RuntimeException();
        }
    }

    void setInterceptor(int which, ServerRequestInterceptor i) {
        Assert.ensure(which >= 0 && which < 3);
        p_[which]._OB_changeInterceptor(i);
    }

    void clearInterceptors() {
        setInterceptor(0, null);
        setInterceptor(1, null);
        setInterceptor(2, null);
    }
}

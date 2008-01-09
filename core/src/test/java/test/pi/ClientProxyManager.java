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

package test.pi;

import org.omg.PortableInterceptor.*;

final class ClientProxyManager {
    private ClientInterceptorProxy_impl[] p_ = new ClientInterceptorProxy_impl[3];

    ClientProxyManager(ORBInitInfo info) {
        p_[0] = new ClientInterceptorProxy_impl();
        p_[1] = new ClientInterceptorProxy_impl();
        p_[2] = new ClientInterceptorProxy_impl();

        //
        // Register the client side interceptor
        //
        try {
            info.add_client_request_interceptor(p_[0]);
            info.add_client_request_interceptor(p_[1]);
            info.add_client_request_interceptor(p_[2]);
        } catch (org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName ex) {
            throw new RuntimeException();
        }
    }

    void setInterceptor(int which, ClientRequestInterceptor i) {
        org.apache.yoko.orb.OB.Assert._OB_assert(which >= 0 && which < 3);
        p_[which]._OB_changeInterceptor(i);
    }

    void clearInterceptors() {
        setInterceptor(0, null);
        setInterceptor(1, null);
        setInterceptor(2, null);
    }
}

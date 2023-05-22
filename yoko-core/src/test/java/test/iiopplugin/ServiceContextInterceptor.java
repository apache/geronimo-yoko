/*
 * Copyright 2015 IBM Corporation and others.
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
package test.iiopplugin;

import java.net.Socket;

import org.apache.yoko.orb.PortableInterceptor.ServerRequestInfoExt;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.orb.OCI.IIOP.TransportInfo_impl;
import org.omg.CORBA.LocalObject;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

/**
 * @version $Revision: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */
final class ServiceContextInterceptor extends LocalObject implements ServerRequestInterceptor {
    public ServiceContextInterceptor() {
    }

    public void receive_request(ServerRequestInfo ri) {
    }

    public void receive_request_service_contexts(ServerRequestInfo ri) {
        ServerRequestInfoExt riExt = (ServerRequestInfoExt) ri;
        TransportInfo_impl connection = (TransportInfo_impl)riExt.getTransportInfo();
        if (connection != null) {
            String remoteHost = connection.remote_addr();
            if (remoteHost != null && remoteHost.length() > 0) {
                System.out.println("Retrieved remote host successfully");
                return;
            }
        }
    }

    public void send_exception(ServerRequestInfo ri) {
    }

    public void send_other(ServerRequestInfo ri) {
    }

    public void send_reply(ServerRequestInfo ri) {
    }

    public void destroy() {
    }

    public String name() {
        return "test.iiopplugin.ServiceContextInterceptor";
    }
}


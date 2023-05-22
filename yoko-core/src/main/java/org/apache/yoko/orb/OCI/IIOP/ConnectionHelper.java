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
package org.apache.yoko.orb.OCI.IIOP;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//
// IDL:orb.yoko.apache.org/OCI/IIOP/AcceptorInfo:1.0
//
/**
 *
 * Information on an IIOP OCI Acceptor object.
 *
 * @see AcceptorInfo
 *
 **/

public interface ConnectionHelper extends UnifiedConnectionHelperProvider
{
    void init(ORB orb, String params);
    Socket createSocket(IOR ior, Policy[] policies, InetAddress address, int port) throws IOException;
    Socket createSelfConnection(InetAddress address, int port) throws IOException;
    ServerSocket createServerSocket(int port, int backlog)  throws IOException;
    ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException;
    default UnifiedConnectionHelper getUnifiedConnectionHelper() {
        return new UnifiedConnectionHelper() {
            @Override
            public void init(ORB orb, String params) {
                ConnectionHelper.this.init(orb, params);
            }

            @Override
            public Socket createSocket(String host, int port, IOR ior, Policy... policies) throws IOException {
                return ConnectionHelper.this.createSocket(ior, policies, Util.getInetAddress(host), port);
            }

            @Override
            public Socket createSelfConnection(InetAddress address, int port) throws IOException {
                return ConnectionHelper.this.createSelfConnection(address, port);
            }

            @Override
            public ServerSocket createServerSocket(int port, int backlog, String... ignored) throws IOException {
                return ConnectionHelper.this.createServerSocket(port, backlog);
            }

            @Override
            public ServerSocket createServerSocket(int port, int backlog, InetAddress address, String... ignored) throws IOException {
                return ConnectionHelper.this.createServerSocket(port, backlog, address);
            }
        };
    }
}


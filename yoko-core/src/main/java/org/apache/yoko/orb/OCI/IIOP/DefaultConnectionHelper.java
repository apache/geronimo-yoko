/*
 * Copyright 2023 IBM Corporation and others.
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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class DefaultConnectionHelper implements ConnectionHelper {
    public void init(ORB orb, String parms) {
        // no initializer parameters required by this version.
    }

    public Socket createSocket(IOR ior, Policy[] policies, InetAddress address, int port) throws IOException {
        return createSocket(address, port);
    }

    public Socket createSelfConnection(InetAddress address, int port) throws IOException {
        return createSocket(address, port);
    }

    private static Socket createSocket(InetAddress address, int port) throws IOException {
        final SocketAddress endpoint = new InetSocketAddress(address, port);
        final Socket socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.connect(endpoint);
        return socket;
    }

    public ServerSocket createServerSocket(int port, int backlog)  throws IOException {
        return createServerSocket(port, backlog, null);
    }

    public ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException {
        try {
            final SocketAddress endpoint = new InetSocketAddress(address, port);
            final ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.setPerformancePreferences(0, 2, 1);
            serverSocket.bind(endpoint, backlog);
            return serverSocket;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}

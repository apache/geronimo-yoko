/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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


/**
 * @version $Rev: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */

package org.apache.yoko.orb.OCI.IIOP;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;


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
        final Socket socket = new Socket(address, port);
        socket.setTcpNoDelay(true);
        return socket;
    }

    public ServerSocket createServerSocket(int port, int backlog)  throws IOException {
        return createServerSocket(port, backlog, null);
    }

    public ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port, backlog, address);
        serverSocket.setReuseAddress(true);
        serverSocket.setPerformancePreferences(0, 2, 1);
        return serverSocket;
    }
}


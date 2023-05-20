/*
 * Copyright 2019 IBM Corporation and others.
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

import org.apache.yoko.orb.OCI.IIOP.ConnectionHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerPlugin implements ConnectionHelper {
    public static final String SERVER_ARGS = "TestingServer";

    static private boolean constructed = false;
    static private boolean initialized = false;
    static private boolean createdSelfConnection = false;
    static private boolean createdServerConnection = false;

    public ServerPlugin() {
        System.out.println("Server-side connection helper constructed");
        constructed = true;
    }


    public void init(ORB orb, String parms) {
        System.out.println("Initializing server-side connection helper with parms " + parms);
        if (!parms.equals(SERVER_ARGS)) {
            throw new IllegalArgumentException("Invalid server initialization argument " + parms);
        }
        initialized = true;
    }

    public Socket createSocket(IOR ior, Policy[] policies, InetAddress address, int port) throws IOException, ConnectException {
        System.out.println("Plugin " + this + " creating client socket connection for IOR=" + ior + " address=" + address + " port=" + port);
        return new Socket(address, port);
    }

    public Socket createSelfConnection(InetAddress address, int port) throws IOException, ConnectException {
        System.out.println("Plugin " + this + " creating self client socket connection for address=" + address + " port=" + port);
        createdSelfConnection = true;
        return new Socket(address, port);
    }

    public ServerSocket createServerSocket(int port, int backlog)  throws IOException, ConnectException {
        System.out.println("Plugin " + this + " creating server socket for port=" + port + " backlog=" + backlog);;
        createdServerConnection = true;
        return new ServerSocket(port, backlog);
    }

    public ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException, ConnectException {
        System.out.println("Plugin " + this + " creating server socket for port=" + port + " backlog=" + backlog + " address=" + address);
        createdServerConnection = true;
        return new ServerSocket(port, backlog, address);
    }

    static public boolean testPassed() {
        System.out.println("constructed=" + constructed + " initalized=" + initialized + " createdSelfConnection=" + createdSelfConnection + " createdServerConnection=" + createdServerConnection);
        return constructed && initialized && createdServerConnection;
    }
}

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
package org.apache.yoko.orb.OCI.IIOP;


import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import testify.jupiter.annotation.iiop.ConfigureOrb;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

@ConfigureOrb(props = {
        // use the ports that show up in hex as AAAA and BBBB
        "yoko.orb.poamanager.poa1.endpoint=iiop --host xxx --port 43690, iiop --host yyy --port 48059",
})
public class EndpointHandlingTest {
    @UseWithOrb
    public static final class ConnectionHelperImpl extends DefaultConnectionHelper implements ConnectionHelper {
        private ORB orb;
        private final Map<Integer, Integer> portMap = new TreeMap<>();

        @Override
        public void init(ORB orb, String params) {
            this.orb = orb;

        }

        @Override
        public Socket createSelfConnection(InetAddress address, int port) throws IOException, ConnectException {
            return super.createSelfConnection(InetAddress.getLocalHost(), portMap.get(port));
        }

        @Override
        public ServerSocket createServerSocket(int port, int backlog) throws IOException, ConnectException {
            // Always use ephemeral ports to avoid conflicts.
            // The test won't use the ports so it should not matter.
            final ServerSocket serverSocket = new ServerSocket(0, backlog);
            int actualPort = serverSocket.getLocalPort();
            portMap.put(port, actualPort);
            return serverSocket;
        }

        @Override
        public ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException, ConnectException {
            return createServerSocket(port, backlog);
        }
    }

    @Test
    public void testViability(ORB orb) {

    }
}

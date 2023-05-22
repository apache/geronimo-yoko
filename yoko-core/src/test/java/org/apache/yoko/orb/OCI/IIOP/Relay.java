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
package org.apache.yoko.orb.OCI.IIOP;

import org.omg.CORBA.INTERNAL;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Forwards incoming connections on an anonymous port to the specified host and port.
 * Uses a {@link FragmentingPump} to fragment larger GIOP 1.2 request and reply messages.
 */
public class Relay {
    private final ExecutorService exec = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public final String forwardHost;
    public final int forwardPort;
    private final ServerSocket serverSocket;
    public final int relayPort;
    private final int maxMessageSize;

    Relay(String forwardHost, int forwardPort, int maxMessageSize) {
        try {
            this.forwardHost = forwardHost;
            this.forwardPort = forwardPort;
            this.serverSocket = new ServerSocket();
            this.serverSocket.bind(new InetSocketAddress(forwardHost, 0));
            this.relayPort = serverSocket.getLocalPort();
            this.maxMessageSize = maxMessageSize;
            exec.execute(this::listen);
        } catch (Exception e) {
            throw (INTERNAL) new INTERNAL().initCause(e);
        }
    }

    private void listen() {
        try {
            System.out.printf("### listening on port %d to forward to port %d ...%n", relayPort, forwardPort);
            for (;;) {
                FragmentingPump pump = new FragmentingPump(serverSocket.accept(), new Socket(forwardHost, forwardPort), maxMessageSize);
                System.out.printf("### received incoming connection on port %d, forwarding to port %d%n", relayPort, forwardPort);
                exec.execute(pump::pumpForward);
                exec.execute(pump::pumpReturn);
            }
        } catch (IOException e) {
            throw (INTERNAL)new INTERNAL().initCause(e);
        }
    }
}



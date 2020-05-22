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
package org.apache.yoko;

import acme.Echo;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import testify.bus.Bus;
import testify.bus.Buses;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ServerControl;

import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ConfigureServer
public class ServerRestartTest {
    @ConfigureServer.RemoteObject
    public static Echo stub;

    @ConfigureServer.Control
    public static ServerControl serverControl;

    @Test
    public void testServerControl(Bus bus, ORB clientOrb) throws Exception {
        // TODO: The IORs are not identical across restarts
        // TODO: and this test fails if the server has newProcess=true
        // TODO: fix both these things!
        assertEquals("hello", stub.echo("hello"));
        assertThrows(Exception.class, serverControl::start);
        serverControl.stop();
        assertThrows(RemoteException.class, () -> stub.echo(""));
        assertThrows(Exception.class, serverControl::stop);
        assertThrows(Exception.class, serverControl::restart);
        serverControl.start();
        serverControl.restart();
        serverControl.stop();
        Thread.sleep(2000);
        serverControl.start();
        System.out.println(Buses.dump(bus));
        assertEquals("hello", stub.echo("hello"));
        serverControl.stop();
    }
}

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
import acme.EchoImpl;
import org.apache.yoko.orb.OCI.Buffer;
import org.apache.yoko.util.HexConverter;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import testify.hex.HexBuilder;
import testify.hex.HexParser;
import testify.jupiter.annotation.Tracing;
import testify.jupiter.annotation.iiop.ConfigureOrb;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.Control;
import testify.jupiter.annotation.iiop.ConfigureServer.NameServiceUrl;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;
import testify.jupiter.annotation.iiop.ConfigureServer.CorbanameUrl;
import testify.jupiter.annotation.iiop.ServerControl;
import testify.util.Stubs;

import java.rmi.RemoteException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_ONLY;
import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_WRITE;

@ConfigureServer(orb = @ConfigureOrb(nameService = READ_WRITE))
public class ServerRestartTest {
    @Control
    public static ServerControl serverControl;

    @NameServiceUrl
    public static String nameServiceUrl;

    @CorbanameUrl(EchoImpl.class)
    public static String stubUrl;

    @ClientStub(EchoImpl.class)
    public static Echo stub;

    /** Test the framework is functioning correctly */
    @Test
    public void testServerControl(ORB clientOrb) throws Exception {
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
        assertEquals("hello", stub.echo("hello"));
    }

    /** Test the framework is functioning correctly */
    @Test
    public void testNameServiceStarted(ORB clientOrb) throws Exception {
        assertNotNull(nameServiceUrl);
        NamingContextHelper.narrow(clientOrb.string_to_object(nameServiceUrl));
    }

    /** Test the framework is functioning correctly */
    @Test
    public void testCorbanameUrl(ORB clientOrb) throws Exception {
        assertNotNull(stubUrl);
        Echo echo = Stubs.toStub(ServerRestartTest.stubUrl, clientOrb, Echo.class);
        echo.echo("wibble");
        String oldStubUrl = ServerRestartTest.stubUrl;
        serverControl.restart();
        assertEquals(oldStubUrl, ServerRestartTest.stubUrl);
        echo = Stubs.toStub(ServerRestartTest.stubUrl, clientOrb, Echo.class);
        echo.echo("splong");
    }

    /** Test client behaviour across server restart */
    @Test
    public void testTwoStubsAcrossRestart(ORB clientOrb) throws Exception {
        // get two stubs — note that the is_a() call will ensure these stubs connect to the server
        NamingContext ctx1 = NamingContextHelper.narrow(clientOrb.string_to_object(nameServiceUrl));
        NamingContext ctx2 = NamingContextHelper.narrow(clientOrb.string_to_object(nameServiceUrl));
        // restart the server
        serverControl.restart();
        // force one of the stubs to reconnect
        ctx1.bind_new_context(new NameComponent[]{new NameComponent("wibble", "")});
        // force the other stub to reconnect
        ctx2.bind_new_context(new NameComponent[]{new NameComponent("splong", "")});
    }
}

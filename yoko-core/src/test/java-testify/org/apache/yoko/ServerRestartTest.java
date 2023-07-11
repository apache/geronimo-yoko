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
package org.apache.yoko;

import acme.Echo;
import acme.EchoImpl;
import org.apache.yoko.util.Stubs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import testify.annotation.RetriedTest;
import testify.annotation.Logging;
import testify.iiop.annotation.ConfigureOrb;
import testify.iiop.annotation.ConfigureServer;
import testify.iiop.annotation.ConfigureServer.ClientStub;
import testify.iiop.annotation.ConfigureServer.Control;
import testify.iiop.annotation.ConfigureServer.CorbanameUrl;
import testify.iiop.annotation.ConfigureServer.NameServiceUrl;
import testify.iiop.annotation.ServerControl;

import java.rmi.RemoteException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static testify.annotation.Logging.LoggingLevel.FINE;
import static testify.iiop.annotation.ConfigureOrb.NameService.READ_WRITE;

@ConfigureServer(serverOrb = @ConfigureOrb(nameService = READ_WRITE))
public class ServerRestartTest {
    @Control
    public static ServerControl serverControl;

    @NameServiceUrl
    public static String nameServiceUrl;

    @CorbanameUrl(EchoImpl.class)
    public static String stubUrl;

    @ClientStub(EchoImpl.class)
    public static Echo stub;

    @Test
    @Logging("yoko.verbose.connection.in")
    void testRestart() {
        serverControl.restart();
    }

    @RetriedTest(maxRuns = 50)
    void testMultipleRestarts() {
        serverControl.restart();
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

    @Test
    @Logging
    public void testMultipleThreads(ORB clientOrb) throws Exception {
        assertNotNull(stubUrl);
        final int parallelism = 5;
        String actual  = new ForkJoinPool(parallelism + 1)
                .submit( () -> IntStream.range(0, parallelism)
                        .mapToObj(Integer::toString)
                        .parallel()
                        .map(expected -> {
                            System.out.println("Starting task " + expected);
                            Echo echo = Stubs.toStub(ServerRestartTest.stubUrl, clientOrb, Echo.class);
                            String result;
                            try { result = echo.echo(expected); } catch (RemoteException e) { throw (Error)Assertions.fail(e); }
                            assertEquals(expected, result, Thread.currentThread().getName() + " should successfully look up and invoke the echo object");
                            return result;
                        })
                        .sorted()
                        .collect(joining(""))
                ).get();
        System.out.println(actual);
        String expected = IntStream.range(0, parallelism).mapToObj(Integer::toString).sorted().collect(joining(""));
        assertEquals(expected, actual);
    }

    /**
     * Test the restart behaviour when the client is multi-threaded.
     * Since this is non-deterministic, we repeat this test enough times to be confident of seeing significant failures.
     * @param clientOrb
     * @throws Exception
     */
    @RetriedTest(maxRuns = 50)
    @Logging(value = "yoko.verbose.retry", level = FINE)
    @Logging(value = "yoko.verbose.connection", level = FINE)
    public void testMultipleThreadsAcrossRestart(ORB clientOrb) throws Exception {
        testMultipleThreads(clientOrb);
        serverControl.restart();
        testMultipleThreads(clientOrb);
        serverControl.restart();
        testMultipleThreads(clientOrb);
        serverControl.restart();
        testMultipleThreads(clientOrb);
    }
}

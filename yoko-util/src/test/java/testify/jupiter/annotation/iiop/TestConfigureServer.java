/*
 * =============================================================================
 * Copyright (c) 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.jupiter.annotation.iiop;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;
import testify.jupiter.annotation.iiop.ConfigureServer.BeforeServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static testify.jupiter.annotation.iiop.ConfigureServer.Separation.COLLOCATED;
import static testify.jupiter.annotation.iiop.ConfigureServer.Separation.INTER_ORB;
import static testify.jupiter.annotation.iiop.ConfigureServer.Separation.INTER_PROCESS;

public class TestConfigureServer {
    @ConfigureServer(
            clientOrb = @ConfigureOrb(value = "client orb", args = "base client orb"),
            serverOrb = @ConfigureOrb(value = "server orb", args = "base server orb")
    )
    abstract static class TestConfigureServerBase {
        static ORB clientOrb, serverOrb;
        static String clientOrbId, serverOrbId;

        @BeforeServer
        public static void recordServerOrb(ORB orb) {
            serverOrb = orb;
            System.out.println("### server ORB = " + serverOrb);
        }

        @BeforeAll
        public static void recordClientOrb(ORB orb) {
            clientOrb = orb;
            System.out.println("### client ORB = " + clientOrb);
        }

        @AfterAll
        public static void scrub() {
            clientOrb = serverOrb = null;
            clientOrbId = serverOrbId = null;
        }

        @UseWithOrb("client orb")
        public static class ClientOrbInitializer extends LocalObject implements ORBInitializer {
            public void pre_init(ORBInitInfo info) { clientOrbId = info.arguments()[0]; }
            public void post_init(ORBInitInfo info) {}
        }

        @UseWithOrb("server orb")
        public static class ServerOrbInitializer extends LocalObject implements ORBInitializer {
            public void pre_init(ORBInitInfo info) { serverOrbId = info.arguments()[0]; }
            public void post_init(ORBInitInfo info) {}
        }
    }

    @Nested
    @ConfigureServer(
            separation = INTER_ORB,
            clientOrb = @ConfigureOrb(value = "client orb", args = "inter-orb client orb"),
            serverOrb = @ConfigureOrb(value = "server orb", args = "inter-orb server orb")
    )
    class TestConfigureServerInterOrb extends TestConfigureServerBase {
        @Test
        public void testServerOrbIsDistinct() {
            assertEquals("inter-orb client orb", clientOrbId);
            assertEquals("inter-orb server orb", serverOrbId);
            assertNotNull(clientOrb);
            assertNotNull(serverOrb);
            assertNotSame(clientOrb, serverOrb, "Server ORB should be distinct from client ORB");
        }
    }

    @Nested
    @ConfigureServer(
            separation = INTER_PROCESS,
            clientOrb = @ConfigureOrb(value = "client orb", args = "inter-process client orb"),
            serverOrb = @ConfigureOrb(value = "server orb", args = "inter-process server orb")
    )
    class TestConfigureServerInterProcess extends TestConfigureServerBase {
        @Test
        public void testServerOrbIsNull() {
            // When we run inter process, the server-side stuff runs in the remote process, and none of the fields should be set locally
            assertEquals("inter-process client orb", clientOrbId);
            assertNotNull(clientOrb);
            assertNull(serverOrbId);
            assertNull(serverOrb, "Server ORB should be unavailable in client process");
        }
    }

    @Nested
    @ConfigureServer(
            separation = COLLOCATED,
            serverOrb = @ConfigureOrb(value = "server orb", args = "collocated server orb"),
            clientOrb = @ConfigureOrb(value = "client orb", args = "collocated client orb")
    )
    class TestConfigureServerCollocated extends TestConfigureServerBase {
        @Test
        public void testServerOrbIsNotDistinct() {
            assertNull(clientOrbId); // TODO: rethink how @UseWithOrb works when the client ORB and the server ORB are the same
            assertEquals("collocated server orb", serverOrbId);
            assertNotNull(clientOrb);
            assertSame(clientOrb, serverOrb, "Server ORB should be identical to client ORB");
        }
    }
}





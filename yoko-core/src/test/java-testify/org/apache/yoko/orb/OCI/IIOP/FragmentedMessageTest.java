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

import acme.Echo;
import acme.EchoImpl;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.PortableInterceptor.IORInfo_impl;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CSIIOP.TransportAddress;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.IORInfo;
import testify.bus.Bus;
import testify.iiop.TestIORInterceptor;
import testify.jupiter.annotation.Tracing;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.ClientStub;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test receiving fragment messages. This is hard to test because at the time of writing,
 * Yoko never fragments messages. In order to get around this, this test subverts the normal
 * course of an invocation so that message passes via a port relay that splits messages into
 * fragments before sending them on.
 */
@ConfigureServer
@Tracing
public class FragmentedMessageTest {
    @ClientStub(EchoImpl.class)
    public static Echo stub;

    private static Field getField(Class<?> cls, String name) {
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f;
        } catch (NoSuchFieldException e) {
            throw (INTERNAL) new INTERNAL().initCause(e);
        }
    }

    private interface Constants {
        int MAX_MESSAGE_SIZE = 1024;
        String PAYLOAD = "To be, or not to be, that is the question: " +
                "Whether 'tis nobler in the mind to suffer " +
                "The slings and arrows of outrageous fortune, " +
                "Or to take Arms against a Sea of troubles, " +
                "And by opposing end them: to die, to sleep; " +
                "No more; and by a sleep, to say we end " +
                "The heart-ache, and the thousand natural shocks " +
                "That Flesh is heir to? 'Tis a consummation " +
                "Devoutly to be wished. To die, to sleep, " +
                "perchance to Dream; aye, there's the rub, " +
                "For in that sleep of death, what dreams may come, " +
                "When we have shuffled off this mortal coil, " +
                "Must give us pause. There's the respect " +
                "That makes Calamity of so long life: " +
                "For who would bear the Whips and Scorns of time, " +
                "The Oppressor's wrong, the proud man's Contumely,  " +
                "The pangs of dispised Love, the Law’s delay,  " +
                "The insolence of Office, and the spurns " +
                "That patient merit of the unworthy takes, " +
                "When he himself might his Quietus make " +
                "With a bare Bodkin? Who would Fardels bear, " +
                "To grunt and sweat under a weary life, " +
                "But that the dread of something after death, " +
                "The undiscovered country, from whose bourn " +
                "No traveller returns, puzzles the will, " +
                "And makes us rather bear those ills we have, " +
                "Than fly to others that we know not of. " +
                "Thus conscience doth make cowards of us all, " +
                "And thus the native hue of Resolution " +
                "Is sicklied o'er, with the pale cast of Thought, " +
                "And enterprises of great pitch and moment, " +
                "With this regard their Currents turn awry, " +
                "And lose the name of Action. Soft you now, " +
                "The fair Ophelia? Nymph, in thy Orisons " +
                "Be all my sins remember'd.";
        int FRAG_TAG = 0xDEADFEED; // mmm, brains...
        Field ACCEPTORS_FIELD = getField(IORInfo_impl.class, "acceptors_");
        Field PORT_FIELD = getField(Acceptor_impl.class, "port_");
    }

    @Test
    public void testFragmentingBigJavaString(Bus bus) throws Exception {
        String result = stub.echo(Constants.PAYLOAD);
        assertThat("String should have been transmitted correctly.", EchoImpl.getLastMessage(bus), equalTo(Constants.PAYLOAD));
        assertThat("String should have been returned correctly.", result, equalTo(Constants.PAYLOAD));
    }

    /**
     * Look out for tagged components from the {@link ServerSideFragmenter} and redirect traffic via the specified
     * alternative port.
     */
    @UseWithOrb("client orb")
    public static class ClientSideFragmenter implements ExtendedConnectionHelper {
        private ConnectionHelper connHelper = new DefaultConnectionHelper();

        @Override
        public Socket createSocket(final String encodedHost, final int port) throws IOException {
            System.out.println("createSocket called with host and port : " + encodedHost + " " + port);
            String host = Util.decodeHost(encodedHost);
            System.out.println("createSocket decoded host: " + host);
            final String info = Util.decodeHostInfo(encodedHost);
            System.out.println("createSocket decoded host info: " + info);
            int relayPort = Integer.parseInt(info);
            Socket socket = new Socket(host, relayPort);
            socket.setTcpNoDelay(true);
            return socket;
        }

        @Override
        public TransportAddress[] getEndpoints(TaggedComponent tc, Policy[] policies) {
            String content = new String(tc.component_data, UTF_8);
            String[] parts = content.split(" ");
            assert parts.length == 3;
            String host = parts[0];
            String relayPort = parts[1];
            int targetPort = Integer.parseInt(parts[2]);
            System.out.println("Decoded address: " + host + " -> " + relayPort  + " -> " + targetPort);
            final String encodedHost = Util.encodeHost(host, "frag", relayPort);
            return new TransportAddress[]{new TransportAddress(encodedHost, (short)targetPort)};
        }

        @Override
        public void init(ORB orb, String s) { connHelper.init(orb, s); }

        @Override
        public Socket createSelfConnection(InetAddress addr, int port) throws IOException { return connHelper.createSelfConnection(addr, port); }

        @Override
        public ServerSocket createServerSocket(int i, int i1, String[] strings) throws IOException { throw new NO_IMPLEMENT(); }

        @Override
        public ServerSocket createServerSocket(int i, int i1, InetAddress inetAddress, String[] strings) throws IOException { throw new NO_IMPLEMENT(); }

        @Override
        public int[] tags() { return new int[]{Constants.FRAG_TAG}; }
    }

    /**
     * Inserts a tagged component into the Internet IOP profile in the IOR.
     * Specifically, it inserts one that describes an alternative port.
     * The {@link ClientSideFragmenter} sees this component and then redirects the traffic via the alternative port.
     */
    @UseWithOrb("server orb")
    public static class ServerSideFragmenter implements TestIORInterceptor {
        private volatile Relay relay;
        @Override
        public void establish_components(IORInfo iorInfo) {
            try {
                IORInfo_impl impl = (IORInfo_impl) iorInfo;
                // retrieve impl.acceptors_[0] by reflection
                Acceptor_impl acceptor = (Acceptor_impl) ((Acceptor[]) Constants.ACCEPTORS_FIELD.get(impl))[0];
                String host = acceptor.hosts_[0];
                // sigh...more reflection needed
                int port = (Integer) Constants.PORT_FIELD.get(acceptor);

                if (relay == null) {
                    relay = new Relay(host, port, Constants.MAX_MESSAGE_SIZE);
                } else {
                    assert relay.forwardHost.equals(host);
                    assert relay.forwardPort == port;
                }
                String content = String.format("%s %d %d", host, relay.relayPort, port);
                System.out.println("Injecting frag tag: " + content);
                System.out.flush();
                TaggedComponent tc = new TaggedComponent(Constants.FRAG_TAG, content.getBytes(UTF_8));
                iorInfo.add_ior_component(tc);
            } catch (Exception e) {
                throw (INTERNAL) new INTERNAL().initCause(e);
            }
        }
    }
}

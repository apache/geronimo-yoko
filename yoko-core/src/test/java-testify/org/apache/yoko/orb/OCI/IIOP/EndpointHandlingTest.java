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
import org.apache.yoko.rmi.impl.RMIServant;
import org.apache.yoko.rmi.impl.ServantFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IIOP.ProfileBody_1_1;
import org.omg.IIOP.ProfileBody_1_1Helper;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;
import org.omg.IOP.TaggedProfile;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import testify.jupiter.annotation.iiop.ConfigureOrb;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;
import testify.util.Lists;
import testify.util.Sets;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ConfigureOrb(props = {
        "yoko.orb.oa.endpoint=iiop --host aaa --port 1111, iiop --host bbb --port 2222, iiop --no-profile --host ccc --port 3333"
})
public class EndpointHandlingTest {
    @UseWithOrb
    public static final class ConnectionHelperImpl extends DefaultConnectionHelper implements ConnectionHelper {
        private ORB orb;
        @Override
        public void init(ORB orb, String params) {
            this.orb = orb;

        }

        @Override
        public Socket createSelfConnection(InetAddress address, int port) throws IOException, ConnectException {
            return super.createSelfConnection(InetAddress.getLocalHost(), port);
        }

        @Override
        public ServerSocket createServerSocket(int port, int backlog) throws IOException, ConnectException {
            System.out.printf("### createServerSocket(%d, %d) -> ", port, backlog);
            // Always use ephemeral ports to avoid conflicts.
            // The test won't use the ports so it should not matter.
            final ServerSocket serverSocket = new ServerSocket(0, backlog);
            int actualPort = serverSocket.getLocalPort();
            PORT_MAP.put(port, actualPort);
            System.out.printf("actual port is %d%n", actualPort);
            return serverSocket;
        }

        @Override
        public ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException, ConnectException {
            return createServerSocket(port, backlog);
        }

    }

    private static final Map<Integer, Integer> PORT_MAP = new TreeMap<>();

    private static POA poa;
    private static Echo impl;
    private static RMIServant servant;
    private static org.omg.CORBA.Object stub;

    @BeforeAll
    public static void setup(ORB orb) throws Exception {
        poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
        impl = s -> s;
        servant = ServantFactory.getServant(impl, orb);
        stub = servant._this_object(orb);
    }

    @Test
    public void testTwoIiopEndpoints(ORB orb) throws Exception {
        String str = orb.object_to_string(stub);
        System.out.println(str);
        IOR ior = object_to_ior(stub, orb);
        int expectedPort1 = PORT_MAP.get(1111);
        int expectedPort2 = PORT_MAP.get(2222);
        assertThat(getPorts(ior), is(Lists.of(expectedPort1, expectedPort2)));
    }

    @Test
    public void testThreeListeners() {
        // There's actually an additional listener created when we use ServantFactory.getServant()
        // TODO: fix PortableRemoteObject.exportObject() so that it doesn't create an ORB, then remove/refactor ServantFactory
        assertThat(PORT_MAP.keySet(), equalTo(Sets.of(0000, 1111, 2222, 3333)));
    }

    public static IOR object_to_ior(org.omg.CORBA.Object object, ORB orb) throws Exception {
        OutputStream out = orb.create_output_stream();
        out.write_Object(object);
        InputStream in = out.create_input_stream();
        IOR ior = IORHelper.read(in);
        return ior;
    }

    public List<Integer> getPorts(IOR ior) throws Exception {
        return Stream.of(ior.profiles)
                .map(EndpointHandlingTest::parseProfileBody_1_1)
                .map(pb -> 0xffff & pb.port)
                .collect(Collectors.toList());
    }

    static ProfileBody_1_1 parseProfileBody_1_1(TaggedProfile tp) {
        ORB singleton = ORB.init();
        Any any = singleton.create_any();
        System.out.println("=== profile body === len: " + tp.profile_data.length);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(tp.profile_data);
        System.out.println(in.dumpAllDataWithPosition());
        in.read_octet(); // the BOM?
        return ProfileBody_1_1Helper.read(in);
    }
}

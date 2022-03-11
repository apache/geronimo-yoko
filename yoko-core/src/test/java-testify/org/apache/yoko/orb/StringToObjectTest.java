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
package org.apache.yoko.orb;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.PortableInterceptor.ClientRequestInfo;
import testify.iiop.TestClientRequestInterceptor;
import testify.jupiter.annotation.RetriedTest;
import testify.jupiter.annotation.iiop.ConfigureOrb;
import testify.jupiter.annotation.iiop.ConfigureOrb.UseWithOrb;
import testify.jupiter.annotation.iiop.ConfigureServer;
import testify.jupiter.annotation.iiop.ConfigureServer.BeforeServer;
import testify.jupiter.annotation.iiop.ServerControl;
import testify.parts.PartRunner;

import static testify.jupiter.annotation.iiop.ConfigureOrb.NameService.READ_WRITE;

@ConfigureServer(serverOrb = @ConfigureOrb(nameService = READ_WRITE))
public class StringToObjectTest {
    private static final String MY_CONTEXT = "my_context";
    private static final NameComponent[] MY_CONTEXT_NC = {new NameComponent(MY_CONTEXT, "")};

    @ConfigureServer.NameServiceUrl
    public static String nameServiceUrl;

    @ConfigureServer.Control
    public static ServerControl serverControl;

    /** Traces request flows from the client ORB perspective */
    @UseWithOrb("client orb")
    public static class ClientInterceptor implements TestClientRequestInterceptor {
        public void send_request(ClientRequestInfo ri) { System.out.println("### client interceptor send_request op=" + ri.operation()); }
        public void send_poll(ClientRequestInfo ri) { System.out.println("### client interceptor send_poll op=" + ri.operation()); }
        public void receive_reply(ClientRequestInfo ri) { System.out.println("### client interceptor receive_reply op=" + ri.operation()); }
        public void receive_exception(ClientRequestInfo ri) { System.out.println("### client interceptor receive_exception op=" + ri.operation() + " ex=" + ri.received_exception_id()); }
        public void receive_other(ClientRequestInfo ri) { System.out.println("### client interceptor receive_other op=" + ri.operation()); }
    }

    @BeforeServer
    public static void bindContext(ORB orb) throws Exception {
        System.out.println("### binding context");
        NamingContext ctx = (NamingContext) orb.resolve_initial_references("NameService");
        ctx.bind_new_context(MY_CONTEXT_NC);
    }

    private static void destroy(ORB orb) {
        orb.shutdown(true);
        orb.destroy();
    }

    @RetriedTest(maxRuns = 10)
    public void testRestartServer(PartRunner runner, ORB orb) throws Exception {
        String url = nameServiceUrl + "#" + MY_CONTEXT;
        System.out.println("### url = " + url);
        orb.string_to_object(url);
        serverControl.restart();
        orb.string_to_object(url);
    }
}

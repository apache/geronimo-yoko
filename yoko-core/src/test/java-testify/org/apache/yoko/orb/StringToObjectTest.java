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
package org.apache.yoko.orb;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.PortableInterceptor.ClientRequestInfo;
import testify.iiop.TestClientRequestInterceptor;
import testify.annotation.RetriedTest;
import testify.iiop.annotation.ConfigureOrb;
import testify.iiop.annotation.ConfigureOrb.UseWithOrb;
import testify.iiop.annotation.ConfigureServer;
import testify.iiop.annotation.ConfigureServer.BeforeServer;
import testify.iiop.annotation.ServerControl;
import testify.parts.PartRunner;

import static testify.iiop.annotation.ConfigureOrb.NameService.READ_WRITE;

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

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
package org.apache.yoko.orb.PortableServer;

import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import test.poa.TestDSIRef_impl;
import test.poa.TestHelper;
import test.poa.Test_impl;
import testify.bus.Bus;
import testify.iiop.annotation.ConfigureServer;

import static org.apache.yoko.orb.PortableServer.PolicyValue.MULTIPLE_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.NO_IMPLICIT_ACTIVATION;
import static org.apache.yoko.orb.PortableServer.PolicyValue.PERSISTENT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.RETAIN;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USER_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USE_DEFAULT_SERVANT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.create_POA;

@ConfigureServer
public class DefaultServantTest {
    @ConfigureServer.BeforeServer
    public static void setup(ORB orb, POA root, Bus bus) throws Exception {
        POAManager mgr = root.the_POAManager();
        POA poa = create_POA("persistent", root, mgr, PERSISTENT, USER_ID, USE_DEFAULT_SERVANT, RETAIN, NO_IMPLICIT_ACTIVATION, MULTIPLE_ID);

        // Use a DSI servant as the default
        TestDSIRef_impl defaultServant = new TestDSIRef_impl(orb, "", false);
        defaultServant.setDefaultServant(true);
        poa.set_servant(defaultServant);

        poa.activate_object_with_id("test1".getBytes(), new Test_impl(orb, "test1", false));
        poa.activate_object_with_id("test2".getBytes(), new Test_impl(orb, "test2", false));

        // create a server object with references to these objects
        bus.put("test1", orb.object_to_string(poa.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0")));
        bus.put("test2", orb.object_to_string(poa.create_reference_with_id("test2".getBytes(), "IDL:Test:1.0")));
        bus.put("testDefault", orb.object_to_string(poa.create_reference_with_id("testDefault".getBytes(), "IDL:Test:1.0")));
    }

    @Test void test1(ORB orb, Bus bus) { TestHelper.narrow(orb.string_to_object(bus.get("test1"))).aMethod(); }
    @Test void test2(ORB orb, Bus bus) { TestHelper.narrow(orb.string_to_object(bus.get("test2"))).aMethod(); }
    @Test void testDefault(ORB orb, Bus bus) { TestHelper.narrow(orb.string_to_object(bus.get("testDefault"))).aMethod();}
}

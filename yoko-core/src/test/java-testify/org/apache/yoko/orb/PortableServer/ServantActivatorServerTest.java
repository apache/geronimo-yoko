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
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import test.poa.TestDSIRef_impl;
import test.poa.TestHelper;
import test.poa.Test_impl;
import testify.bus.Bus;
import testify.iiop.annotation.ConfigureServer;

import static org.apache.yoko.orb.PortableServer.PolicyValue.PERSISTENT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USER_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.create_POA;
import static org.junit.Assert.assertThrows;

@ConfigureServer
public class ServantActivatorServerTest {

    @ConfigureServer.BeforeServer
    public static void setup(ORB orb, POA root, Bus bus) throws Exception {
        POAManager rootMgr = root.the_POAManager();
        POA poa = create_POA("persistent", root, rootMgr, PERSISTENT, USER_ID, PolicyValue.USE_SERVANT_MANAGER);
        TestActivator_impl activatorImpl = new TestActivator_impl(orb);
        poa.set_servant_manager(activatorImpl);

        // Create three references, two good and one bad
        bus.put("test1", orb.object_to_string(poa.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0")));
        bus.put("testDSI", orb.object_to_string(poa.create_reference_with_id("testDSI".getBytes(), "IDL:Test:1.0")));
        bus.put("testNotExist", orb.object_to_string(poa.create_reference_with_id("testNotExist".getBytes(), "IDL:Test:1.0")));

        //TODO: Find a way to test this after the ORB is shutdown
//        assertTrue(activatorImpl.etherealize_called());
    }

    @Test
    void test1(ORB orb, Bus bus) { TestHelper.narrow(orb.string_to_object(bus.get("test1"))).aMethod(); }

    @Test
    void testDSI(ORB orb, Bus bus) { TestHelper.narrow(orb.string_to_object(bus.get("testDSI"))).aMethod(); }

    @Test
    void testNotExist(ORB orb, Bus bus) {
        assertThrows(OBJECT_NOT_EXIST.class,
            () -> TestHelper.narrow(orb.string_to_object(bus.get("testNotExist"))).aMethod());
    }

    final static class TestActivator_impl extends LocalObject implements ServantActivator{
        private final ORB orb;
        private boolean etherealizeCalled = false;

        TestActivator_impl(ORB orb) {
            this.orb = orb;
        }

        public org.omg.PortableServer.Servant incarnate(byte[] oid, POA poa) throws ForwardRequest {
            String oidString = new String(oid);

            // If the user is requesting the object "test" then oblige
            org.omg.PortableServer.Servant servant = null;
            if (oidString.equals("test1"))
                servant = new Test_impl(orb, "test1", false);
            else if (oidString.equals("testDSI"))
                servant = new TestDSIRef_impl(orb, "", false);

            if (servant != null) {
                // Verify that POA allows activator to explicitly activate a servant
                try {
                    poa.activate_object_with_id(oid, servant);
                } catch (ObjectAlreadyActive | WrongPolicy | ServantAlreadyActive ex) {
                    throw new RuntimeException();
                }
                return servant;
            }
            // Fail if not test1 or testDSI objects
            throw new OBJECT_NOT_EXIST();
        }

        public void etherealize(byte[] oid, POA poa, Servant servant, boolean cleanup, boolean remaining) {
            if (remaining) return;
            etherealizeCalled = true;
            // Etherealize is called when the orb -> shutdown()
            // method is called. The ORB shutdown calls
            // destroy(true, true) on each POAManagers. The
            // cleanup flag should be set to true here.
            if (!cleanup) throw new RuntimeException();
            if (new String(oid).equals("test")) servant = null;
        }

        public boolean etherealize_called() {
            return etherealizeCalled;
        }
    }
}

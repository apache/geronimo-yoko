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
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import test.poa.TestDSIRef_impl;
import test.poa.TestHelper;
import test.poa.Test_impl;
import testify.bus.Bus;
import testify.iiop.annotation.ConfigureServer;

import static org.apache.yoko.orb.PortableServer.PolicyValue.NON_RETAIN;
import static org.apache.yoko.orb.PortableServer.PolicyValue.NO_IMPLICIT_ACTIVATION;
import static org.apache.yoko.orb.PortableServer.PolicyValue.PERSISTENT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USER_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USE_SERVANT_MANAGER;
import static org.apache.yoko.orb.PortableServer.PolicyValue.create_POA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@ConfigureServer
public class ServantLocatorServerTest {

    @ConfigureServer.BeforeServer
    public static void setup(ORB orb, POA root, Bus bus) throws Exception {
            POAManager rootMgr = root.the_POAManager();
            POA persistentPOA = create_POA("persistent", root, rootMgr, PERSISTENT, USER_ID, USE_SERVANT_MANAGER, NON_RETAIN, NO_IMPLICIT_ACTIVATION);
            ServantLocator locator = new TestLocator_impl(orb);
            persistentPOA.set_servant_manager(locator);

            bus.put("test1", orb.object_to_string(persistentPOA.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0")));
            bus.put("testDSI", orb.object_to_string(persistentPOA.create_reference_with_id("testDSI".getBytes(), "IDL:Test:1.0")));
            bus.put("testEx", orb.object_to_string(persistentPOA.create_reference_with_id("testEx".getBytes(), "IDL:Test:1.0")));
            bus.put("testBad", orb.object_to_string(persistentPOA.create_reference_with_id("testBad".getBytes(), "IDL:Test:1.0")));
    }

    @Test
    void test1(ORB orb, Bus bus) { TestHelper.narrow(orb.string_to_object(bus.get("test1"))).aMethod(); }

    @Test
    void testDSI(ORB orb, Bus bus) { TestHelper.narrow(orb.string_to_object(bus.get("testDSI"))).aMethod(); }

    @Test
    void testNoPermissionError(ORB orb, Bus bus) {
        assertThrows(NO_PERMISSION.class,
                () -> TestHelper.narrow(orb.string_to_object(bus.get("testEx"))).aMethod());
    }

    @Test
    void testObjectNotExistError(ORB orb, Bus bus) {
        assertThrows(OBJECT_NOT_EXIST.class,
                () -> TestHelper.narrow(orb.string_to_object(bus.get("testBad"))).aMethod());
    }

    public final static class TestLocator_impl extends LocalObject implements ServantLocator {
        Test_impl testImpl;
        TestDSIRef_impl testDSI;

        TestLocator_impl(ORB orb) {
            testImpl = new Test_impl(orb, "test1", false);
            testDSI = new TestDSIRef_impl(orb, "", false);
        }

        public org.omg.PortableServer.Servant preinvoke(byte[] oid, POA poa, String operation, CookieHolder the_cookie) {
            String oidString = new String(oid);

            // If the user is requesting the object "test", "testDSI" or "testEx", then oblige
            if (oidString.equals("test1")) {
                the_cookie.value = oidString;
                return testImpl;
            }

            if (oidString.equals("testDSI")) {
                the_cookie.value = oidString;
                return testDSI;
            }

            // Use testImpl as the servant for testEx. We'll raise an exception in postinvoke().
            if (oidString.equals("testEx")) {
                the_cookie.value = oidString;
                return testImpl;
            }

            // XXX test ForwardRequest - Fail
            throw new OBJECT_NOT_EXIST();
        }

        public void postinvoke(byte[] oid, POA poa, String operation, Object the_cookie, Servant the_servant) {
            // Check the cookie
            String oidString = new String(oid);
            assertEquals(oidString, (String) the_cookie);

            if (oidString.equals("testEx") && !operation.equals("_locate")) {
                // The client must receive this exception as the result of an invocation on "testEx"
                throw new org.omg.CORBA.NO_PERMISSION();
            }
        }
    }
}

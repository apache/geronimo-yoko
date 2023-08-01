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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Request;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantActivatorPOA;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPOA;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import test.poa.TestDSIRef_impl;
import test.poa.TestHelper;
import test.poa.Test_impl;
import testify.iiop.annotation.ConfigureOrb;
import testify.util.function.RawConsumer;

import static org.apache.yoko.orb.PortableServer.PolicyValue.MULTIPLE_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.NON_RETAIN;
import static org.apache.yoko.orb.PortableServer.PolicyValue.NO_IMPLICIT_ACTIVATION;
import static org.apache.yoko.orb.PortableServer.PolicyValue.PERSISTENT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.RETAIN;
import static org.apache.yoko.orb.PortableServer.PolicyValue.UNIQUE_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USER_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USE_DEFAULT_SERVANT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USE_SERVANT_MANAGER;
import static org.apache.yoko.orb.PortableServer.PolicyValue.create_POA;

@ConfigureOrb
class TestPoaCollocated {
    enum InvokeAction {
        STATIC_INVOKE_STATIC_SERVANT(poa -> invokeStatic(poa, "test")),
        STATIC_INVOKE_DYNAMIC_SERVANT(poa -> invokeStatic(poa, "testDSI")),
        DYNAMIC_INVOKE_STATIC_SERVANT(poa -> invokeDynamic(poa, "test")),
        DYNAMIC_INVOKE_DYNAMIC_SERVANT(poa -> invokeDynamic(poa, "testDSI"));
        final RawConsumer<POA> invokeAction;
        InvokeAction(RawConsumer<POA> invokeAction) { this.invokeAction = invokeAction; }
        final void run(POA poa) throws Exception { invokeAction.acceptRaw(poa); }
        private static void invokeStatic(POA poa, String id) {
            TestHelper.narrow(poa.create_reference_with_id(id.getBytes(), "IDL:Test:1.0")).aMethod();
        }
        private static void invokeDynamic(POA poa, String id) throws Exception {
            org.omg.CORBA.Object object = poa.create_reference_with_id(id.getBytes(), "IDL:Test:1.0");
            Request request = object._request("aMethod");
            request.invoke(); // dynamic invocation
            if (null != request.env().exception()) throw request.env().exception();
        }
    }

    static ORB orb;
    static POA rootPoa;
    static POAManager rootPoaManager;
    static Test_impl defaultStaticServant;
    static Test_impl locatorSSI;
    POA poa;

    @BeforeAll
    static void setup(ORB orb, POA rootPoa) {
        TestPoaCollocated.orb = orb;
        TestPoaCollocated.rootPoa = rootPoa;
        TestPoaCollocated.rootPoaManager = rootPoa.the_POAManager();
        TestPoaCollocated.defaultStaticServant = new Test_impl(orb, "defaultStaticServant", false);
        TestPoaCollocated.locatorSSI = new Test_impl(orb, "locator_SSI", false);
    }

    @AfterEach
    void destroyPoa() {
        if (poa == null) return;
        poa.destroy(true, true);
        poa = null;
    }

    @AfterAll
    static void teardown() throws Exception {
        byte[] id = rootPoa.servant_to_id(defaultStaticServant);
        rootPoa.deactivate_object(id);
    }

    @ParameterizedTest @EnumSource(InvokeAction.class)
    public void testStaticDefaultServant(InvokeAction action) throws Exception {
        poa = create_POA("defaultSSI", rootPoa, rootPoaManager, PERSISTENT, USER_ID, NON_RETAIN, NO_IMPLICIT_ACTIVATION, MULTIPLE_ID, USE_DEFAULT_SERVANT);
        poa.set_servant(defaultStaticServant);
        action.run(poa);
    }

    @ParameterizedTest @EnumSource(InvokeAction.class)
    void testDynamicDefaultServant(InvokeAction action) throws Exception {
        poa = create_POA("defaultDSI", rootPoa, rootPoaManager, PERSISTENT, USER_ID, NON_RETAIN, NO_IMPLICIT_ACTIVATION, MULTIPLE_ID, USE_DEFAULT_SERVANT);
        poa.set_servant(new TestDSIRef_impl(orb, "defaultDSIServant", false));
        action.run(poa);
    }

    @ParameterizedTest @EnumSource(InvokeAction.class)
    void testServantLocator(InvokeAction action) throws Exception {
        poa = create_POA("servloc", rootPoa, rootPoaManager, PERSISTENT, USER_ID, NON_RETAIN, NO_IMPLICIT_ACTIVATION, UNIQUE_ID, USE_SERVANT_MANAGER);
        TestLocator locatorImpl = new TestLocator();
        ServantLocator locator = locatorImpl._this(orb);
        poa.set_servant_manager(locator);
        action.run(poa);
        rootPoa.deactivate_object(rootPoa.servant_to_id(locatorImpl));
    }

    @ParameterizedTest @EnumSource(InvokeAction.class)
    void testServantActivator(InvokeAction action) throws Exception {
        poa = create_POA("servant", rootPoa, rootPoaManager, PERSISTENT, USER_ID, RETAIN, NO_IMPLICIT_ACTIVATION, UNIQUE_ID, USE_SERVANT_MANAGER);
        TestActivator activatorImpl = new TestActivator();
        ServantActivator activator = activatorImpl._this(orb);
        poa.set_servant_manager(activator);
        action.run(poa);
        rootPoa.deactivate_object(rootPoa.servant_to_id(activatorImpl));
    }

    final static class TestActivator extends ServantActivatorPOA {
        public Servant incarnate(byte[] oid, POA poa) throws ForwardRequest {
            switch (new String(oid)) {
                case "test": return new Test_impl(orb, "locator_SSI", false);
                case "testDSI": return new TestDSIRef_impl(orb, "locator_DSI", false);
                default: return null; // fail
            }
        }

        public void etherealize(byte[] oid, POA poa, Servant servant, boolean cleanup, boolean remaining) {}
    }

    final static class TestLocator extends ServantLocatorPOA {
        private final Test_impl test;
        private final TestDSIRef_impl testDSI;

        TestLocator() {
            test = new Test_impl(orb, "locator_SSI", false);
            testDSI = new TestDSIRef_impl(orb, "locator_DSI", false);
        }

        public Servant preinvoke(byte[] oid, POA poa, String operation, CookieHolder the_cookie) {
            switch (new String(oid)) {
                case "test": return test;
                case "testDSI": return testDSI;
                default: return null;
            }
        }

        public void postinvoke(byte[] oid, POA poa, String operation, Object the_cookie, Servant the_servant) {}
    }
}

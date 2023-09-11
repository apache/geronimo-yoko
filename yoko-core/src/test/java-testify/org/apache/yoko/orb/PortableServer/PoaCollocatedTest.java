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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junitpioneer.jupiter.cartesian.CartesianTest;
import org.junitpioneer.jupiter.cartesian.CartesianTest.Enum;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Request;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantLocator;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import test.poa.TestDSIRef_impl;
import test.poa.TestHelper;
import test.poa.Test_impl;
import testify.iiop.annotation.ConfigureOrb;

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
public class PoaCollocatedTest {
    enum ConfigurePoa {
        STATIC_DEFAULT_SERVANT (PERSISTENT, USER_ID, NON_RETAIN, NO_IMPLICIT_ACTIVATION, MULTIPLE_ID, USE_DEFAULT_SERVANT),
        DYNAMIC_DEFAULT_SERVANT(PERSISTENT, USER_ID, NON_RETAIN, NO_IMPLICIT_ACTIVATION, MULTIPLE_ID, USE_DEFAULT_SERVANT),
        SERVANT_LOCATOR        (PERSISTENT, USER_ID, NON_RETAIN, NO_IMPLICIT_ACTIVATION,   UNIQUE_ID, USE_SERVANT_MANAGER),
        SERVANT_ACTIVATOR      (PERSISTENT, USER_ID,     RETAIN, NO_IMPLICIT_ACTIVATION,   UNIQUE_ID, USE_SERVANT_MANAGER);
        final PolicyValue[] policyValues;
        ConfigurePoa(PolicyValue... policyValues) { this.policyValues = policyValues; }

        void configurePoa(PoaCollocatedTest t) throws Exception {
            t.poa = create_POA(name(), t.rootPoa, t.rootPoaManager, policyValues);
            switch (this) {
            case STATIC_DEFAULT_SERVANT:  t.poa.set_servant(new Test_impl(t.orb, "defaultStaticServant", false)); return;
            case DYNAMIC_DEFAULT_SERVANT: t.poa.set_servant(new TestDSIRef_impl(t.orb, "defaultDSIServant", false)); return;
            case SERVANT_LOCATOR:         t.poa.set_servant_manager(t.new TestLocator()); return;
            case SERVANT_ACTIVATOR:       t.poa.set_servant_manager(t.new TestActivator()); return;
            }
        }
    }
    enum ChooseTarget { STATIC_SERVANT, DYNAMIC_SERVANT }
    enum InvokeMethod {
        STATIC_INVOKE {
            void invoke(POA poa, String id) {
                TestHelper.narrow(poa.create_reference_with_id(id.getBytes(), "IDL:Test:1.0")).aMethod();
            }
        },
        DYNAMIC_INVOKE {
            void invoke(POA poa, String id) throws Exception {
                org.omg.CORBA.Object object = poa.create_reference_with_id(id.getBytes(), "IDL:Test:1.0");
                Request request = object._request("aMethod");
                request.invoke(); // dynamic invocation
                if (null != request.env().exception()) throw request.env().exception();
            }
        };
        abstract void invoke(POA poa, String id) throws Exception ;
    }

    ORB orb;
    POA rootPoa;
    POAManager rootPoaManager;
    POA poa;

    @BeforeEach
    void setup(ORB orb, POA rootPoa) {
        this.orb = orb;
        this.rootPoa = rootPoa;
        this.rootPoaManager = rootPoa.the_POAManager();
        // this.poa is configured during each test
    }

    @AfterEach
    void destroyPoa() { poa.destroy(true, true); }

    @CartesianTest // runs every combination of the three enums
    void test(@Enum ConfigurePoa poaConfig, @Enum ChooseTarget target, @Enum InvokeMethod invoker) throws Exception {
        poaConfig.configurePoa(this);
        invoker.invoke(this.poa, target.name());
    }

    final class TestActivator extends LocalObject implements ServantActivator {
        public Servant incarnate(byte[] oid, POA poa) throws ForwardRequest {
            switch (ChooseTarget.valueOf(new String(oid))) {
                case STATIC_SERVANT: return new Test_impl(orb, "locator_SSI", false);
                case DYNAMIC_SERVANT: return new TestDSIRef_impl(orb, "locator_DSI", false);
                default: return null; // fail
            }
        }

        public void etherealize(byte[] oid, POA poa, Servant servant, boolean cleanup, boolean remaining) {}
    }

    final class TestLocator extends LocalObject implements ServantLocator {
        private final Test_impl test = new Test_impl(orb, "locator_SSI", false);
        private final TestDSIRef_impl testDSI = new TestDSIRef_impl(orb, "locator_DSI", false);

        public Servant preinvoke(byte[] oid, POA poa, String operation, CookieHolder the_cookie) {
            switch (ChooseTarget.valueOf(new String(oid))) {
                case STATIC_SERVANT: return test;
                case DYNAMIC_SERVANT: return testDSI;
                default: return null;
            }
        }

        public void postinvoke(byte[] oid, POA poa, String operation, Object the_cookie, Servant the_servant) {}
    }
}

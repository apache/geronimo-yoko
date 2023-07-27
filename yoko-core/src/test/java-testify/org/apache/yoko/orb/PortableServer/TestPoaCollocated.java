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
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.PortableServer.ForwardRequest;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
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

import static org.junit.Assert.assertTrue;

@ConfigureOrb
public class TestPoaCollocated {
    static void TestPOA(POA poa) {
        byte[] id;
        org.omg.CORBA.Object obj;
        Request request;
        test.poa.Test test;

        //
        // Invoke twice on each object - statically & DII
        //
        id = ("test").getBytes();
        obj = poa.create_reference_with_id(id, "IDL:Test:1.0");
        test = TestHelper.narrow(obj);
        test.aMethod();
        request = obj._request("aMethod");
        request.invoke();
        assertTrue(request.env().exception() == null);

        id = ("testDSI").getBytes();
        obj = poa.create_reference_with_id(id, "IDL:Test:1.0");
        test = TestHelper.narrow(obj);
        test.aMethod();
        request = obj._request("aMethod");
        request.invoke();
        assertTrue(request.env().exception() == null);
    }

    @Test
    public void testCollocated(ORB orb, POA rootPoa) throws Exception {
        POAManager manager = rootPoa.the_POAManager();
        manager.activate();
        POA poa;
        Servant servant;
        Policy[] policies;

        //
        // Setup policies for default servant
        //
        policies = new Policy[6];
        policies[0] = rootPoa
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[1] = rootPoa
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies[2] = rootPoa
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);
        policies[3] = rootPoa
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        policies[4] = rootPoa
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[5] = rootPoa
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT);

        //
        // Create POA w/ static Default Servant
        //
        try {
            poa = rootPoa.create_POA("defaultSSI", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        Test_impl staticServant = new Test_impl(orb, "defaultStaticServant",
                false);
        try {
            poa.set_servant(staticServant);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        TestPOA(poa);
        poa.destroy(true, true);

        //
        // Since staticServant is a stack-based servant, we need to deactivate
        // it before it goes out of scope
        //
        byte[] id = null;
        try {
            id = rootPoa.servant_to_id(staticServant);
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        try {
            rootPoa.deactivate_object(id);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ DSI Default Servant
        //
        try {
            poa = rootPoa.create_POA("defaultDSI", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        servant = new TestDSIRef_impl(orb, "defaultDSIServant", false);
        try {
            poa.set_servant(servant);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        TestPOA(poa);
        poa.destroy(true, true);
        servant = null;

        //
        // Clean up policies
        //
        for (int i = 0; i < policies.length; i++)
            policies[i].destroy();
        POA poa1;
        Servant servant1;
        Policy[] policies1;

        //
        // Setup policies for servant locator
        //
        policies1 = new Policy[6];
        policies1[0] = rootPoa
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies1[1] = rootPoa
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies1[2] = rootPoa
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);
        policies1[3] = rootPoa
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        policies1[4] = rootPoa
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies1[5] = rootPoa
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);

        //
        // Create POA w/ Servant Locator
        //
        try {
            poa1 = rootPoa.create_POA("servloc", manager, policies1);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        TestLocator_impl locatorImpl = new TestLocator_impl(orb);
        ServantLocator locator = locatorImpl._this(orb);
        try {
            poa1.set_servant_manager(locator);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        TestPOA(poa1);
        poa1.destroy(true, true);

        //
        // Clean up policies
        //
        for (int i = 0; i < policies1.length; i++)
            policies1[i].destroy();

        //
        // Since locatorImpl is a stack-based servant, we need to deactivate
        // it before it goes out of scope
        //
        byte[] id1 = null;
        try {
            id1 = rootPoa.servant_to_id(locatorImpl);
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        try {
            rootPoa.deactivate_object(id1);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        POA poa2;
        Servant servant2;
        Policy[] policies2;

        //
        // Setup policies for servant activator
        //
        policies2 = new Policy[6];
        policies2[0] = rootPoa
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies2[1] = rootPoa
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies2[2] = rootPoa
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies2[3] = rootPoa
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        policies2[4] = rootPoa
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies2[5] = rootPoa
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);

        //
        // Create POA w/ Servant Activator
        //
        try {
            poa2 = rootPoa.create_POA("servant", manager, policies2);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        TestActivator_impl activatorImpl = new TestActivator_impl(orb);
        ServantActivator activator = activatorImpl._this(orb);
        try {
            poa2.set_servant_manager(activator);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        TestPOA(poa2);
        poa2.destroy(true, true);

        //
        // Clean up policies
        //
        for (int i = 0; i < policies2.length; i++)
            policies2[i].destroy();

        //
        // Since activatorImpl is a stack-based servant, we need to deactivate
        // it before it goes out of scope
        //
        byte[] id2 = null;
        try {
            id2 = rootPoa.servant_to_id(activatorImpl);
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        try {
            rootPoa.deactivate_object(id2);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
    }

    final static class TestActivator_impl extends ServantActivatorPOA {
        private ORB orb_;

        private Test_impl test_;

        private TestDSIRef_impl testDSI_;

        TestActivator_impl(ORB orb) {
            orb_ = orb;

            test_ = new Test_impl(orb, "locator_SSI", false);
            testDSI_ = new TestDSIRef_impl(orb, "locator_DSI", false);
        }

        public Servant incarnate(byte[] oid, POA poa) throws ForwardRequest {
            String oidString = new String(oid);

            if (oidString.equals("test"))
                return test_;
            else if (oidString.equals("testDSI"))
                return testDSI_;

            //
            // Fail
            //
            return null;
        }

        public void etherealize(byte[] oid, POA poa, Servant servant,
                boolean cleanup, boolean remaining) {
            String oidString = new String(oid);

            if (!remaining) {
                if (oidString.equals("test")) {
                    servant = null;
                    test_ = null;
                } else if (oidString.equals("testDSI")) {
                    testDSI_ = null;
                }
            }
        }
    }

    final static class TestLocator_impl extends ServantLocatorPOA {
        private ORB orb_;

        private Test_impl test_;

        private TestDSIRef_impl testDSI_;

        TestLocator_impl(ORB orb) {
            orb_ = orb;

            test_ = new Test_impl(orb, "locator_SSI", false);
            testDSI_ = new TestDSIRef_impl(orb, "locator_DSI", false);
        }

        public Servant preinvoke(byte[] oid, POA poa, String operation,
                CookieHolder the_cookie) throws ForwardRequest {
            String oidString = new String(oid);

            if (oidString.equals("test"))
                return test_;
            else if (oidString.equals("testDSI"))
                return testDSI_;
            return null;
        }

        public void postinvoke(byte[] oid, POA poa, String operation,
                Object the_cookie, Servant the_servant) {
        }
    }
}

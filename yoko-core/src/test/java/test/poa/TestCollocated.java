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

package test.poa;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.ServantLocatorPackage.*;

public final class TestCollocated extends test.common.TestBase {
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
                java.lang.Object the_cookie, Servant the_servant) {
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

    static void TestPOA(POA poa) {
        byte[] id;
        org.omg.CORBA.Object obj;
        Request request;
        Test test;

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

    static void TestDefaultServant(ORB orb, POA root, POAManager manager) {
        POA poa;
        Servant servant;
        Policy[] policies;

        //
        // Setup policies for default servant
        //
        policies = new Policy[6];
        policies[0] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[1] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);
        policies[3] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        policies[4] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[5] = root
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT);

        //
        // Create POA w/ static Default Servant
        //
        try {
            poa = root.create_POA("defaultSSI", manager, policies);
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
            id = root.servant_to_id(staticServant);
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        try {
            root.deactivate_object(id);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ DSI Default Servant
        //
        try {
            poa = root.create_POA("defaultDSI", manager, policies);
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
    }

    static void TestServantLocator(ORB orb, POA root, POAManager manager) {
        POA poa;
        Servant servant;
        Policy[] policies;

        //
        // Setup policies for servant locator
        //
        policies = new Policy[6];
        policies[0] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[1] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);
        policies[3] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        policies[4] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies[5] = root
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);

        //
        // Create POA w/ Servant Locator
        //
        try {
            poa = root.create_POA("servloc", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        TestLocator_impl locatorImpl = new TestLocator_impl(orb);
        ServantLocator locator = locatorImpl._this(orb);
        try {
            poa.set_servant_manager(locator);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        TestPOA(poa);
        poa.destroy(true, true);

        //
        // Clean up policies
        //
        for (int i = 0; i < policies.length; i++)
            policies[i].destroy();

        //
        // Since locatorImpl is a stack-based servant, we need to deactivate
        // it before it goes out of scope
        //
        byte[] id = null;
        try {
            id = root.servant_to_id(locatorImpl);
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        try {
            root.deactivate_object(id);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
    }

    static void TestServantActivator(ORB orb, POA root, POAManager manager) {
        POA poa;
        Servant servant;
        Policy[] policies;

        //
        // Setup policies for servant activator
        //
        policies = new Policy[6];
        policies[0] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[1] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[3] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        policies[4] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies[5] = root
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);

        //
        // Create POA w/ Servant Activator
        //
        try {
            poa = root.create_POA("servant", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        TestActivator_impl activatorImpl = new TestActivator_impl(orb);
        ServantActivator activator = activatorImpl._this(orb);
        try {
            poa.set_servant_manager(activator);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        TestPOA(poa);
        poa.destroy(true, true);

        //
        // Clean up policies
        //
        for (int i = 0; i < policies.length; i++)
            policies[i].destroy();

        //
        // Since activatorImpl is a stack-based servant, we need to deactivate
        // it before it goes out of scope
        //
        byte[] id = null;
        try {
            id = root.servant_to_id(activatorImpl);
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        try {
            root.deactivate_object(id);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        ORB orb = null;

        try {
            orb = ORB.init(args, props);

            POA root = TestUtil.GetRootPOA(orb);
            POAManager manager = root.the_POAManager();
            try {
                manager.activate();
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                throw new RuntimeException();
            }

            System.out.print("Testing collocated default servant... ");
            System.out.flush();
            TestDefaultServant(orb, root, manager);
            System.out.println("Done!");
            System.out.print("Testing collocated servant locator... ");
            System.out.flush();
            TestServantLocator(orb, root, manager);
            System.out.println("Done!");
            System.out.print("Testing collocated servant activator... ");
            System.out.flush();
            TestServantActivator(orb, root, manager);
            System.out.println("Done!");
        } catch (SystemException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (SystemException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(0);
    }
}

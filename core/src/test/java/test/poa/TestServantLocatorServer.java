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

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.POAManagerPackage.*;
import org.omg.PortableServer.ServantLocatorPackage.*;
import java.io.*;
import java.util.Properties;

public final class TestServantLocatorServer extends test.common.TestBase {
    final static class TestLocator_impl extends ServantLocatorPOA {
        private ORB orb_;

        Test_impl test_;

        TestDSIRef_impl testDSI_;

        TestLocator_impl(ORB orb) {
            orb_ = orb;
            test_ = new Test_impl(orb_, "test", false);
            testDSI_ = new TestDSIRef_impl(orb_, "", false);
        }

        public Servant preinvoke(byte[] oid, POA poa, String operation,
                CookieHolder the_cookie) throws ForwardRequest {
            String oidString = new String(oid);

            //
            // If the user is requesting the object "test", "testDSI" or
            // "testEx", then oblige
            //
            if (oidString.equals("test")) {
                the_cookie.value = oidString;
                return test_;
            }

            if (oidString.equals("testDSI")) {
                the_cookie.value = oidString;
                return testDSI_;
            }

            //
            // Use test_ as the servant for testEx. We'll raise an
            // exception in postinvoke().
            //
            if (oidString.equals("testEx")) {
                the_cookie.value = oidString;
                return test_;
            }

            //
            // XXX test ForwardRequest
            //

            //
            // Fail
            //
            throw new OBJECT_NOT_EXIST();
        }

        public void postinvoke(byte[] oid, POA poa, String operation,
                java.lang.Object the_cookie, Servant the_servant) {
            //
            // Check the cookie
            //
            String oidString = new String(oid);
            TEST(oidString.equals((String) the_cookie));

            if (oidString.equals("testEx") && !operation.equals("_locate")) {
                //
                // The client must receive this exception as the result
                // of an invocation on "testEx"
                //
                throw new org.omg.CORBA.NO_PERMISSION();
            }
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
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            POA root = TestUtil.GetRootPOA(orb);
            POAManager manager = root.the_POAManager();

            Policy[] policies = new Policy[5];
            policies[0] = root
                    .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
            policies[1] = root
                    .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
            policies[2] = root
                    .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
            policies[3] = root
                    .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);
            policies[4] = root
                    .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);

            POA persistentPOA = null;
            try {
                persistentPOA = root
                        .create_POA("persistent", manager, policies);
            } catch (AdapterAlreadyExists ex) {
                throw new RuntimeException();
            } catch (InvalidPolicy ex) {
                throw new RuntimeException();
            }

            TestLocator_impl locatorImpl = new TestLocator_impl(orb);
            ServantLocator locator = locatorImpl._this(orb);

            try {
                persistentPOA.set_servant_manager(locator);
            } catch (WrongPolicy ex) {
                throw new RuntimeException();
            }

            //
            // Create four references, three good and one bad
            //
            byte[] oid1 = ("test").getBytes();
            byte[] oid2 = ("testDSI").getBytes();
            byte[] oid3 = ("testEx").getBytes();
            byte[] oid4 = ("testBad").getBytes();
            org.omg.CORBA.Object reference1 = persistentPOA
                    .create_reference_with_id(oid1, "IDL:Test:1.0");
            org.omg.CORBA.Object reference2 = persistentPOA
                    .create_reference_with_id(oid2, "IDL:Test:1.0");
            org.omg.CORBA.Object reference3 = persistentPOA
                    .create_reference_with_id(oid3, "IDL:Test:1.0");
            org.omg.CORBA.Object reference4 = persistentPOA
                    .create_reference_with_id(oid4, "IDL:Test:1.0");

            //
            // Create server
            //
            TestInfo[] info = new TestInfo[4];
            info[0] = new TestInfo();
            info[1] = new TestInfo();
            info[2] = new TestInfo();
            info[3] = new TestInfo();
            info[0].obj = TestHelper.narrow(reference1);
            info[0].except_id = "";
            info[1].obj = TestHelper.narrow(reference2);
            info[1].except_id = "";
            info[2].obj = TestHelper.narrow(reference3);
            info[2].except_id = "IDL:omg.org/CORBA/NO_PERMISSION:1.0";
            info[3].obj = TestHelper.narrow(reference4);
            info[3].except_id = "IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0";
            TestServer_impl serverImpl = new TestServer_impl(orb, info);
            TestServer server = serverImpl._this(orb);

            //
            // Save reference
            //
            String refFile = "Test.ref";
            try {
                FileOutputStream file = new FileOutputStream(refFile);
                PrintWriter out = new PrintWriter(file);
                out.println(orb.object_to_string(server));
                out.flush();
                file.close();
            } catch (IOException ex) {
                System.err.println("Can't write to `" + ex.getMessage() + "'");
                System.exit(1);
            }

            //
            // Run implementation
            //
            try {
                manager.activate();
            } catch (AdapterInactive ex) {
                throw new RuntimeException();
            }
            orb.run();

            File file = new File(refFile);
            file.delete();
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

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

import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import java.io.*;

public final class TestPOAManagerServer extends test.common.TestBase {
    final static class POAManagerProxy_impl extends POAManagerProxyPOA {
        private POAManager manager_;

        POAManagerProxy_impl(POAManager manager) {
            manager_ = manager;
        }

        //
        // Mapping for PortableServer::POAManager
        //
        public void activate()
                throws test.poa.POAManagerProxyPackage.AdapterInactive {
            try {
                manager_.activate();
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                throw new test.poa.POAManagerProxyPackage.AdapterInactive();
            }
        }

        public void hold_requests(boolean a)
                throws test.poa.POAManagerProxyPackage.AdapterInactive {
            try {
                manager_.hold_requests(a);
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                throw new test.poa.POAManagerProxyPackage.AdapterInactive();
            }
        }

        public void discard_requests(boolean a)
                throws test.poa.POAManagerProxyPackage.AdapterInactive {
            try {
                manager_.discard_requests(a);
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                throw new test.poa.POAManagerProxyPackage.AdapterInactive();
            }
        }

        public void deactivate(boolean a, boolean b)
                throws test.poa.POAManagerProxyPackage.AdapterInactive {
            try {
                manager_.deactivate(a, b);
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                throw new test.poa.POAManagerProxyPackage.AdapterInactive();
            }
        }

        public test.poa.POAManagerProxyPackage.State get_state() {
            return test.poa.POAManagerProxyPackage.State.from_int(manager_
                    .get_state().value());
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

            //
            // Create POA w/ RETAIN. This POA should use a seperate
            // POAManager.
            //
            Policy[] policies = new Policy[5];
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

            POA retain = null;
            try {
                retain = root.create_POA("retain", null, policies);
            } catch (AdapterAlreadyExists ex) {
                throw new RuntimeException();
            } catch (InvalidPolicy ex) {
                throw new RuntimeException();
            }

            POAManager retainManager = retain.the_POAManager();

            POAManagerProxy_impl proxyImpl = new POAManagerProxy_impl(
                    retainManager);
            POAManagerProxy proxy = proxyImpl._this(orb);

            Test_impl testImpl = new Test_impl(orb, retain);
            byte[] oid = ("test").getBytes();
            try {
                retain.activate_object_with_id(oid, testImpl);
            } catch (ObjectAlreadyActive ex) {
                TEST(false);
            } catch (ServantAlreadyActive ex) {
                TEST(false);
            } catch (WrongPolicy ex) {
                TEST(false);
            }

            Test test = testImpl._this();

            TestDSI_impl testDSIImpl = new TestDSI_impl(orb, retain);
            byte[] oidDSI = ("testDSI").getBytes();
            try {
                retain.activate_object_with_id(oidDSI, testDSIImpl);
            } catch (ObjectAlreadyActive ex) {
                TEST(false);
            } catch (ServantAlreadyActive ex) {
                TEST(false);
            } catch (WrongPolicy ex) {
                TEST(false);
            }

            org.omg.CORBA.Object objDSI = retain.create_reference_with_id(
                    oidDSI, "IDL:Test:1.0");
            Test testDSI = TestHelper.narrow(objDSI);

            //
            // Create server
            //
            TestInfo[] info = new TestInfo[2];
            info[0] = new TestInfo();
            info[1] = new TestInfo();
            info[0].obj = test;
            info[0].except_id = "";
            info[1].obj = testDSI;
            info[1].except_id = "";
            TestServer_impl serverImpl = new TestServer_impl(orb, info);
            TestServer server = serverImpl._this(orb);

            //
            // If JTC is available spawn a thread to find out whether a
            // method invocation on test is blocked until
            // POAManager::activate() is called.
            //
            PMSTestThread t = new PMSTestThread(test);
            t.start();

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }

            TEST(t.callState() == PMSTestThread.CALL_STARTED);

            //
            // Run implementation. This should cause the blocked call in
            // the thread to release.
            //
            try {
                manager.activate();
                retainManager.activate();
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                throw new RuntimeException();
            }

            //
            // Wait for the call to complete
            //
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // Ignore
            }

            TEST(t.callState() == PMSTestThread.CALL_SUCCESS);

            new TestPOAManagerCommon(proxy, info);

            //
            // Don't write references until we're ready to run
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
            // Save reference
            //
            String refFileMgr = "POAManagerProxy.ref";
            try {
                FileOutputStream file = new FileOutputStream(refFileMgr);
                PrintWriter out = new PrintWriter(file);
                out.println(orb.object_to_string(proxy));
                out.flush();
                file.close();
            } catch (IOException ex) {
                System.err.println("Can't write to `" + ex.getMessage() + "'");
                System.exit(1);
            }

            orb.run();

            File file = new File(refFile);
            file.delete();
            file = new File(refFileMgr);
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

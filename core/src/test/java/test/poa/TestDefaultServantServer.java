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

public final class TestDefaultServantServer {
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

            Policy[] policies = new Policy[6];
            policies[0] = root
                    .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
            policies[1] = root
                    .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
            policies[2] = root
                    .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT);
            policies[3] = root
                    .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
            policies[4] = root
                    .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
            policies[5] = root
                    .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);

            POA persistentPOA = null;
            try {
                persistentPOA = root
                        .create_POA("persistent", manager, policies);
            } catch (AdapterAlreadyExists ex) {
                throw new RuntimeException();
            } catch (InvalidPolicy ex) {
                throw new RuntimeException();
            }

            //
            // Use a DSI servant as the default
            //
            TestDSIRef_impl defaultServant = new TestDSIRef_impl(orb, "", false);
            defaultServant.setDefaultServant(true);

            try {
                persistentPOA.set_servant(defaultServant);
            } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
                throw new RuntimeException();
            }

            //
            // Create some servants
            //
            Test_impl test1 = new Test_impl(orb, "test1", false);
            Test_impl test2 = new Test_impl(orb, "test2", false);

            //
            // Create ObjectIds
            //
            byte[] oid1 = ("test1").getBytes();
            byte[] oid2 = ("test2").getBytes();
            byte[] oid3 = ("testDefault").getBytes();

            //
            // Activate servants
            //
            try {
                persistentPOA.activate_object_with_id(oid1, test1);
                persistentPOA.activate_object_with_id(oid2, test2);
            } catch (org.omg.PortableServer.POAPackage.ObjectAlreadyActive ex) {
                throw new RuntimeException();
            } catch (org.omg.PortableServer.POAPackage.ServantAlreadyActive ex) {
                throw new RuntimeException();
            } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
                throw new RuntimeException();
            }

            //
            // Create object references
            //
            org.omg.CORBA.Object reference1 = persistentPOA
                    .create_reference_with_id(oid1, "IDL:Test:1.0");
            org.omg.CORBA.Object reference2 = persistentPOA
                    .create_reference_with_id(oid2, "IDL:Test:1.0");
            org.omg.CORBA.Object reference3 = persistentPOA
                    .create_reference_with_id(oid3, "IDL:Test:1.0");

            //
            // Create server
            //
            TestInfo[] info = new TestInfo[3];
            info[0] = new TestInfo();
            info[1] = new TestInfo();
            info[2] = new TestInfo();
            info[0].obj = TestHelper.narrow(reference1);
            info[0].except_id = "";
            info[1].obj = TestHelper.narrow(reference2);
            info[1].except_id = "";
            info[2].obj = TestHelper.narrow(reference3);
            info[2].except_id = "";
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
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
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

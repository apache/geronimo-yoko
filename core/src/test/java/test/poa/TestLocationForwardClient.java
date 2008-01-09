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
import java.util.*;

final public class TestLocationForwardClient extends test.common.TestBase {
    public static void main(String args[]) {
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

            POA poa;
            Policy[] policies;

            POAManager manager = root.the_POAManager();

            //
            // Create POA
            //
            policies = new Policy[4];
            policies[0] = root
                    .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
            policies[1] = root
                    .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
            policies[2] = root
                    .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
            policies[3] = root
                    .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
            try {
                poa = root.create_POA("poa", manager, policies);
            } catch (InvalidPolicy ex) {
                throw new RuntimeException();
            } catch (AdapterAlreadyExists ex) {
                throw new RuntimeException();
            }

            TestLocationForwardActivator_impl activatorImpl = new TestLocationForwardActivator_impl();
            ServantActivator activator = activatorImpl._this(orb);
            try {
                poa.set_servant_manager(activator);
            } catch (WrongPolicy ex) {
                throw new RuntimeException();
            }

            byte[] oid = ("test").getBytes();
            org.omg.CORBA.Object reference = poa.create_reference_with_id(oid,
                    "IDL:Test:1.0");

            //
            // Read all object references from file
            //
            org.omg.CORBA.Object obj = orb
                    .string_to_object("relfile:///Test.ref");
            if (obj == null) {
                System.err.println("TestLocationForwardClient: "
                        + "cannot read IOR from Test.ref");
                System.exit(1);
            }
            TestLocationForwardServer server = TestLocationForwardServerHelper
                    .narrow(obj);
            TEST(server != null);
            org.omg.CORBA.Object servant = server.get_servant();

            activatorImpl.setForwardRequest(servant);

            TestLocationForward_impl testImpl = new TestLocationForward_impl(
                    orb);
            activatorImpl.setActivatedServant(testImpl);

            try {
                manager.activate();
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                throw new RuntimeException();
            }

            server.setForwardRequest(reference);

            //
            // Run some calls
            //
            TestLocationForward local = TestLocationForwardHelper
                    .narrow(reference);

            //
            // First should be local
            //
            local.aMethod();
            local.deactivate_servant();

            //
            // Second, should be remote
            //
            local.aMethod();
            local.deactivate_servant();

            //
            // Third should be local again
            //
            local.aMethod();
            local.deactivate_servant();

            //
            // Clean up
            //
            poa.destroy(true, true);

            server.deactivate();
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

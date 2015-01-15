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
import org.omg.PortableServer.POAManagerFactoryPackage.*;

import java.lang.System;

final class TestPOAManagerFactory extends test.common.TestBase {
    static void TestAll(ORB orb, POA root) {
        POAManagerFactory factory = root.the_POAManagerFactory();
        org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactory = org.apache.yoko.orb.OBPortableServer.POAManagerFactoryHelper
                .narrow(factory);

        POAManager[] managers;

        //
        // Initial count: RootPOAManager
        //
        int count = 1;

        managers = pmFactory.list();
        assertTrue(managers.length == 1);

        //
        // Create POA Managers without policies
        //

        Policy[] pl = new Policy[0];
        ;

        POAManager test = null;
        try {
            test = pmFactory.create_POAManager("TestPOAManager", pl);
            count++;
        } catch (ManagerAlreadyExists ex) {
            assertTrue(false);
        } catch (PolicyError ex) {
        }

        POAManager tmp = pmFactory.find("TestPOAManager");
        assertTrue(tmp == test);
        managers = pmFactory.list();

        //
        // count == 2
        //
        assertTrue(count == 2);
        assertTrue(managers.length == count);

        POAManager test2 = null;
        try {
            test2 = pmFactory.create_POAManager("TestPOAManager2", pl);
            count++;
        } catch (ManagerAlreadyExists ex) {
            assertTrue(false);
        } catch (PolicyError ex) {
        }

        tmp = pmFactory.find("TestPOAManager2");
        assertTrue(tmp == test2);
        managers = pmFactory.list();

        //
        // count == 3
        //
        assertTrue(count == 3);
        assertTrue(managers.length == count);

        try {
            test2 = pmFactory.create_POAManager("TestPOAManager2", pl);
            count++;

            //
            // 'create_POAManager' should not have succeeded
            //
            assertTrue(false);
        } catch (ManagerAlreadyExists ex) {
            //
            // Expected
            //
        } catch (PolicyError ex) {
        }

        assertTrue(tmp == test2);
        assertTrue(test != test2);

        managers = pmFactory.list();

        //
        // count == 3 (still)
        //
        assertTrue(count == 3);
        assertTrue(managers.length == count);

        //
        // Create POA Managers with proprietary policies
        //

        Policy[] policies = new Policy[2];
        boolean policiesOkay = true;
        try {
            policies[0] = pmFactory
                    .create_communications_concurrency_policy(org.apache.yoko.orb.OBPortableServer.COMMUNICATIONS_CONCURRENCY_POLICY_THREADED.value);

            policies[1] = pmFactory
                    .create_giop_version_policy(org.apache.yoko.orb.OBPortableServer.GIOP_VERSION_POLICY_1_2.value);
        } catch (PolicyError ex) {
            policiesOkay = false;
        }

        if (policiesOkay) {
            POAManager configManager = null;
            try {
                configManager = pmFactory.create_POAManager("ConfigManager",
                        policies);
                count++;
            } catch (ManagerAlreadyExists ex) {
                assertTrue(false);
            } catch (PolicyError ex) {
            }

            tmp = pmFactory.find("ConfigManager");
            assertTrue(tmp == configManager);
        }

        managers = pmFactory.list();

        //
        // count == 4
        //
        assertTrue(count == 4);
        assertTrue(managers.length == count);

        //
        // Check the policy values
        //

        org.apache.yoko.orb.OBPortableServer.CommunicationsConcurrencyPolicy commsPolicy = org.apache.yoko.orb.OBPortableServer.CommunicationsConcurrencyPolicyHelper
                .narrow(policies[0]);
        assertTrue(commsPolicy.value() == org.apache.yoko.orb.OBPortableServer.COMMUNICATIONS_CONCURRENCY_POLICY_THREADED.value);
        assertTrue(commsPolicy.policy_type() == org.apache.yoko.orb.OBPortableServer.COMMUNICATIONS_CONCURRENCY_POLICY_ID.value);

        org.apache.yoko.orb.OBPortableServer.GIOPVersionPolicy giopPolicy = org.apache.yoko.orb.OBPortableServer.GIOPVersionPolicyHelper
                .narrow(policies[1]);
        assertTrue(giopPolicy.value() == org.apache.yoko.orb.OBPortableServer.GIOP_VERSION_POLICY_1_2.value);
        assertTrue(giopPolicy.policy_type() == org.apache.yoko.orb.OBPortableServer.GIOP_VERSION_POLICY_ID.value);

        //
        // EndpointConfigurationPolicy
        //
        String config = "iiop --host localhost --port 10999 --bind localhost";

        Policy[] policies2 = new Policy[1];
        policiesOkay = true;
        try {
            policies2[0] = pmFactory
                    .create_endpoint_configuration_policy(config);
        } catch (PolicyError ex) {
            policiesOkay = false;
        }

        if (policiesOkay) {
            org.apache.yoko.orb.OBPortableServer.EndpointConfigurationPolicy endpointPolicy = org.apache.yoko.orb.OBPortableServer.EndpointConfigurationPolicyHelper
                    .narrow(policies2[0]);

            assertTrue(endpointPolicy.policy_type() == org.apache.yoko.orb.OBPortableServer.ENDPOINT_CONFIGURATION_POLICY_ID.value);

            //
            // Create POA Manager with endpoint policy
            //
            POAManager endpointManager = null;
            try {
                endpointManager = pmFactory.create_POAManager(
                        "EndpointManager", policies2);
                count++;
            } catch (ManagerAlreadyExists ex) {
                assertTrue(false);
            } catch (PolicyError ex) {
            }

            tmp = pmFactory.find("EndpointManager");
            assertTrue(tmp == endpointManager);
        }

        managers = pmFactory.list();

        //
        // count == 5
        //
        assertTrue(count == 5);
        assertTrue(managers.length == count);
    }

    static void runtests(ORB orb, POA root) {
        TestAll(orb, root);
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            POA root = TestUtil.GetRootPOA(orb);

            System.out.print("Testing POAManagerFactory... ");
            System.out.flush();

            //
            // Run the tests using the root POA
            //
            runtests(orb, root);

            System.out.println("Done!");
        } catch (SystemException ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (SystemException ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}

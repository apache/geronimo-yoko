/*
 * Copyright 2015 IBM Corporation and others.
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
package test.poa;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

public final class TestMisc extends test.common.TestBase {
    static void TestCreateReference(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA user, system;
        byte[] id1, id2, tmpid;
        Policy[] policies;

        POAManager manager = root.the_POAManager();

        policies = new Policy[1];
        policies[0] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        try {
            user = root.create_POA("user_id", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        policies = new Policy[1];
        policies[0] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID);
        try {
            system = root.create_POA("system_id", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: create_reference with wrong POA policies
        //
        try {
            obj = user.create_reference("IDL:Test:1.0");
            assertTrue(false); // create_reference should not have succeeded
        } catch (WrongPolicy ex) {
            // expected
        }

        //
        // Test: create_reference - should get a new ID for each invocation
        // on POA w/ SYSTEM_ID policy
        //
        try {
            obj = system.create_reference("IDL:Test:1.0");
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            id1 = system.reference_to_id(obj);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        try {
            obj = system.create_reference("IDL:Test:1.0");
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            id2 = system.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(!TestUtil.Compare(id1, id2));

        //
        // Test: create_reference_with_id using a system-generated ID
        //
        try {
            obj = system.create_reference_with_id(id1, "IDL:Test:1.0");
        } catch (BAD_PARAM ex) {
            assertTrue(false); // create_reference_with_id should have succeeded
        }

        id1 = ("id1").getBytes();

        //
        // Test: create_reference_with_id using a user-generated ID
        //
        try {
            obj = system.create_reference_with_id(id1, "IDL:Test:1.0");
            assertTrue(false); // create_reference_with_id should have not
                            // succeeded
        } catch (BAD_PARAM ex) {
            // Expected
        }

        //
        // Test: create_reference_with_id
        //
        obj = user.create_reference_with_id(id1, "IDL:Test:1.0");
        assertTrue(obj != null);
        try {
            tmpid = user.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(id1, tmpid));
        id2 = ("id2").getBytes();
        obj = user.create_reference_with_id(id2, "IDL:Test:1.0");
        assertTrue(obj != null);
        try {
            tmpid = user.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(id2, tmpid));

        user.destroy(true, true);
        system.destroy(true, true);
    }

    static void TestServantToId(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA unique, implicit, multiple;
        byte[] id1, id2, tmpid;
        Policy[] policies;
        Test_impl servant1;
        Test_impl servant2;

        POAManager manager = root.the_POAManager();

        //
        // Create POA w/ UNIQUE_ID, NO_IMPLICIT_ACTIVATION
        //
        policies = new Policy[4];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies[1] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[3] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        try {
            unique = root.create_POA("unique_id", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ UNIQUE_ID, IMPLICIT_ACTIVATION
        //
        policies = new Policy[3];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies[1] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[2] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
        try {
            implicit = root.create_POA("implicit", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ MULTIPLE_ID, IMPLICIT_ACTIVATION
        //
        policies = new Policy[3];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[1] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[2] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
        try {
            multiple = root.create_POA("multiple", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        servant1 = new Test_impl(orb, "test1", false);
        servant2 = new Test_impl(orb, "test2", false);

        //
        // Test: ServantNotActive exception
        //
        try {
            unique.servant_to_id(servant1);
            assertTrue(false); // servant_to_id should not have succeeded
        } catch (ServantNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        id1 = ("test1").getBytes();
        try {
            unique.activate_object_with_id(id1, servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        }

        //
        // Test: servant_to_id (UNIQUE_ID policy)
        //
        try {
            tmpid = unique.servant_to_id(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(id1, tmpid));

        //
        // Test: servant_to_id (IMPLICIT_ACTIVATION) - servant1 should
        // be automatically activated
        //
        try {
            id1 = implicit.servant_to_id(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }

        //
        // Test: Now that servant1 is activated, and since we have UNIQUE_ID,
        // we should get the same ID back
        //
        try {
            tmpid = implicit.servant_to_id(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(id1, tmpid));

        //
        // Test: Implicitly activating servant2 should produce a new ID
        //
        try {
            id2 = implicit.servant_to_id(servant2);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(!TestUtil.Compare(id1, id2));

        //
        // Test: servant_to_id (IMPLICIT_ACTIVATION, MULTIPLE_ID) - servant1
        // should be automatically activated
        //
        try {
            id1 = multiple.servant_to_id(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }

        //
        // Test: Since we have MULTIPLE_ID, we should get a new ID
        //
        try {
            tmpid = multiple.servant_to_id(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(!TestUtil.Compare(id1, tmpid));

        unique.destroy(true, true);
        implicit.destroy(true, true);
        multiple.destroy(true, true);
    }

    static void TestIdToServant(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA retain, defaultPOA;
        byte[] id1, id2, tmpid;
        Policy[] policies;
        Test_impl def;
        Test_impl servant1;
        Test_impl servant2;
        Servant tmpservant;

        POAManager manager = root.the_POAManager();

        //
        // Create POA w/ RETAIN
        //
        policies = new Policy[3];
        policies[0] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[1] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[2] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        try {
            retain = root.create_POA("retain", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ USE_DEFAULT_SERVANT
        //
        policies = new Policy[5];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[1] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[2] = root
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT);
        policies[3] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[4] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);

        try {
            defaultPOA = root.create_POA("default", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        def = new Test_impl(orb, "default", false);
        try {
            defaultPOA.set_servant(def);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        servant1 = new Test_impl(orb, "test1", false);
        servant2 = new Test_impl(orb, "test2", false);

        //
        // Test: ObjectNotActive exception
        //
        try {
            tmpid = ("bad_id").getBytes();
            retain.id_to_servant(tmpid);
            assertTrue(false); // id_to_servant should not have succeeded
        } catch (ObjectNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        id1 = ("test1").getBytes();
        id2 = ("test2").getBytes();
        try {
            retain.activate_object_with_id(id1, servant1);
            retain.activate_object_with_id(id2, servant2);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        }

        //
        // Test: servant_to_id (RETAIN policy)
        //
        try {
            tmpservant = retain.id_to_servant(id1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(servant1 == tmpservant);
        try {
            tmpservant = retain.id_to_servant(id2);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(servant2 == tmpservant);

        //
        // Test: id_to_servant (USE_DEFAULT_SERVANT)
        //
        try {
            defaultPOA.activate_object_with_id(id1, servant1);
            defaultPOA.activate_object_with_id(id2, servant2);
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        }
        try {
            tmpservant = defaultPOA.id_to_servant(id1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(servant1 == tmpservant);
        try {
            tmpservant = defaultPOA.id_to_servant(id2);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(servant2 == tmpservant);

        //
        // Test: id_to_servant (USE_DEFAULT_SERVANT) - should return
        // default servant for all unknown IDs
        //
        tmpid = ("test99").getBytes();
        try {
            tmpservant = defaultPOA.id_to_servant(tmpid);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(tmpservant == def);
        tmpservant = null;

        retain.destroy(true, true);
        defaultPOA.destroy(true, true);
    }

    static void TestServantToReference(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA unique, implicit, multiple;
        byte[] id1, id2, tmpid1, tmpid2;
        Policy[] policies;
        Test_impl servant1;
        Test_impl servant2;

        POAManager manager = root.the_POAManager();

        //
        // Create POA w/ UNIQUE_ID, NO_IMPLICIT_ACTIVATION
        //
        policies = new Policy[4];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies[1] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[3] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        try {
            unique = root.create_POA("unique_id", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ UNIQUE_ID, IMPLICIT_ACTIVATION
        //
        policies = new Policy[3];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies[1] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[2] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
        try {
            implicit = root.create_POA("implicit", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ MULTIPLE_ID, IMPLICIT_ACTIVATION
        //
        policies = new Policy[3];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[1] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[2] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
        try {
            multiple = root.create_POA("multiple", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        servant1 = new Test_impl(orb, "test1", false);
        servant2 = new Test_impl(orb, "test2", false);

        //
        // Test: ServantNotActive exception
        //
        try {
            unique.servant_to_reference(servant1);
            assertTrue(false); // servant_to_reference should not have succeeded
        } catch (ServantNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        id1 = ("test1").getBytes();
        try {
            unique.activate_object_with_id(id1, servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        }

        //
        // Test: servant_to_reference (UNIQUE_ID policy)
        //
        try {
            obj = unique.servant_to_reference(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid1 = unique.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(id1, tmpid1));

        //
        // Test: servant_to_reference (IMPLICIT_ACTIVATION) - servant1 should
        // be automatically activated
        //
        try {
            obj = implicit.servant_to_reference(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid1 = implicit.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: Now that servant1 is activated, and since we have UNIQUE_ID,
        // we should get the same ID back
        //
        try {
            obj = implicit.servant_to_reference(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid2 = implicit.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(tmpid1, tmpid2));

        //
        // Test: Implicitly activating servant2 should produce a new ID
        //
        try {
            obj = implicit.servant_to_reference(servant2);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid2 = implicit.reference_to_id(obj);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        assertTrue(!TestUtil.Compare(tmpid1, tmpid2));

        //
        // Test: servant_to_reference (IMPLICIT_ACTIVATION, MULTIPLE_ID) -
        // servant1 should be automatically activated
        //
        try {
            obj = multiple.servant_to_reference(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid1 = multiple.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: Since we have MULTIPLE_ID, we should get a new ID
        //
        try {
            obj = multiple.servant_to_reference(servant1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid2 = multiple.reference_to_id(obj);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        assertTrue(!TestUtil.Compare(tmpid1, tmpid2));

        unique.destroy(true, true);
        implicit.destroy(true, true);
        multiple.destroy(true, true);
    }

    static void TestIdToReference(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA retain, defaultPOA;
        byte[] id1, id2, tmpid;
        Policy[] policies;
        Test_impl servant1;
        Test_impl servant2;

        POAManager manager = root.the_POAManager();

        //
        // Create POA w/ RETAIN
        //
        policies = new Policy[3];
        policies[0] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[1] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[2] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        try {
            retain = root.create_POA("retain", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        servant1 = new Test_impl(orb, "test1", false);
        servant2 = new Test_impl(orb, "test2", false);

        //
        // Test: ObjectNotActive exception
        //
        try {
            tmpid = ("bad_id").getBytes();
            retain.id_to_reference(tmpid);
            assertTrue(false); // id_to_reference should not have succeeded
        } catch (ObjectNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        id1 = ("test1").getBytes();
        id2 = ("test2").getBytes();
        try {
            retain.activate_object_with_id(id1, servant1);
            retain.activate_object_with_id(id2, servant2);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        }

        //
        // Test: servant_to_reference
        //
        try {
            obj = retain.id_to_reference(id1);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid = retain.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(id1, tmpid));

        //
        // Test: servant_to_reference
        //
        try {
            obj = retain.id_to_reference(id2);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        }
        assertTrue(obj != null);
        try {
            tmpid = retain.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(id2, tmpid));

        retain.destroy(true, true);
    }

    static void TestReferenceToServant(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA retain, defaultPOA;
        byte[] id1, id2, tmpid;
        Policy[] policies;
        Test_impl def;
        Test_impl servant1;
        Test_impl servant2;
        Servant tmpservant;

        POAManager manager = root.the_POAManager();

        //
        // Create POA w/ RETAIN
        //
        policies = new Policy[3];
        policies[0] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[1] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[2] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        try {
            retain = root.create_POA("retain", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create POA w/ USE_DEFAULT_SERVANT
        //
        policies = new Policy[5];
        policies[0] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[1] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[2] = root
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT);
        policies[3] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[4] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);

        try {
            defaultPOA = root.create_POA("default", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        def = new Test_impl(orb, "default", false);
        try {
            defaultPOA.set_servant(def);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        servant1 = new Test_impl(orb, "test1", false);
        servant2 = new Test_impl(orb, "test2", false);

        //
        // Test: ObjectNotActive exception
        //
        try {
            tmpid = ("bad_id").getBytes();
            obj = retain.create_reference_with_id(tmpid, "IDL:Test:1.0");
            retain.reference_to_servant(obj);
            assertTrue(false); // reference_to_servant should not have succeeded
        } catch (ObjectNotActive ex) {
            // expected
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        id1 = ("test1").getBytes();
        id2 = ("test2").getBytes();
        try {
            retain.activate_object_with_id(id1, servant1);
            retain.activate_object_with_id(id2, servant2);
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        }

        //
        // Test: reference_to_servant (RETAIN policy)
        //
        obj = retain.create_reference_with_id(id1, "IDL:Test:1.0");
        try {
            tmpservant = retain.reference_to_servant(obj);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        assertTrue(servant1 == tmpservant);
        obj = retain.create_reference_with_id(id2, "IDL:Test:1.0");
        try {
            tmpservant = retain.reference_to_servant(obj);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        assertTrue(servant2 == tmpservant);

        //
        // Test: WrongAdapter exception
        //
        try {
            obj = retain.create_reference_with_id(id1, "IDL:Test:1.0");
            defaultPOA.reference_to_servant(obj);
            assertTrue(false); // reference_to_servant should not have succeeded
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            // expected
        }

        //
        // Test: reference_to_servant (USE_DEFAULT_SERVANT)
        //
        try {
            defaultPOA.activate_object_with_id(id1, servant1);
            defaultPOA.activate_object_with_id(id2, servant2);
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        }
        obj = defaultPOA.create_reference_with_id(id1, "IDL:Test:1.0");
        try {
            tmpservant = defaultPOA.reference_to_servant(obj);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        assertTrue(servant1 == tmpservant);
        obj = defaultPOA.create_reference_with_id(id2, "IDL:Test:1.0");
        try {
            tmpservant = defaultPOA.reference_to_servant(obj);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        assertTrue(servant2 == tmpservant);

        //
        // Test: reference_to_servant (USE_DEFAULT_SERVANT) - should return
        // default servant for all unknown IDs
        //
        tmpid = ("test99").getBytes();
        obj = defaultPOA.create_reference_with_id(tmpid, "IDL:Test:1.0");
        try {
            tmpservant = defaultPOA.reference_to_servant(obj);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        }
        assertTrue(tmpservant == def);
        tmpservant = null;

        retain.destroy(true, true);
        defaultPOA.destroy(true, true);
    }

    static void TestReferenceToId(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA poa;
        byte[] id1, id2, tmpid;
        Policy[] policies;

        POAManager manager = root.the_POAManager();

        //
        // Create POA
        //
        policies = new Policy[3];
        policies[0] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        policies[1] = root
                .create_lifespan_policy(org.omg.PortableServer.LifespanPolicyValue.PERSISTENT);
        policies[2] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        try {
            poa = root.create_POA("poa", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        id1 = ("test1").getBytes();
        id2 = ("test2").getBytes();

        //
        // Test: reference_to_id
        //
        obj = poa.create_reference_with_id(id1, "IDL:Test:1.0");
        try {
            tmpid = poa.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(tmpid, id1));
        obj = poa.create_reference_with_id(id2, "IDL:Test:1.0");
        try {
            tmpid = poa.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(TestUtil.Compare(tmpid, id2));

        //
        // Test: WrongAdapter exception
        //
        try {
            obj = poa.create_reference_with_id(id1, "IDL:Test:1.0");
            root.reference_to_id(obj);
            assertTrue(false); // reference_to_id should not have succeeded
        } catch (WrongAdapter ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        poa.destroy(true, true);
    }

    static void runtests(ORB orb, POA root) {
        TestCreateReference(orb, root);
        TestServantToId(orb, root);
        TestIdToServant(orb, root);
        TestServantToReference(orb, root);
        TestIdToReference(orb, root);
        TestReferenceToServant(orb, root);
        TestReferenceToId(orb, root);
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

            System.out.print("Testing miscellaneous POA functions... ");
            System.out.flush();

            //
            // Run the tests using the root POA
            //
            runtests(orb, root);

            //
            // Create a child POA and run the tests again using the
            // child as the root
            //
            Policy[] policies = new Policy[0];
            POAManager manager = root.the_POAManager();
            POA child = null;
            try {
                child = root.create_POA("child", manager, policies);
            } catch (AdapterAlreadyExists ex) {
                throw new RuntimeException();
            } catch (InvalidPolicy ex) {
                throw new RuntimeException();
            }
            runtests(orb, child);

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

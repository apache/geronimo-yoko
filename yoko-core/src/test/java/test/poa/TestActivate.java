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

public final class TestActivate extends test.common.TestBase {
    final static class TestActivator_impl extends ServantActivatorPOA {
        private byte[] oid_;

        private POA poa_;

        private Servant servant_;

        private boolean remaining_;

        private boolean valid_;

        void expect(byte[] oid, POA poa, Servant servant, boolean remaining) {
            oid_ = oid;
            poa_ = poa;
            servant_ = servant;
            remaining_ = remaining;
            valid_ = false;
        }

        boolean isValid() {
            return valid_;
        }

        public Servant incarnate(byte[] oid, POA poa) throws ForwardRequest {
            return null;
        }

        public void etherealize(byte[] oid, POA poa, Servant servant,
                boolean cleanup, boolean remaining) {
            assertTrue(TestUtil.Compare(oid_, oid));
            assertTrue(poa_._is_equivalent(poa));
            assertTrue(servant_ == servant);
            assertTrue(remaining_ == remaining);
            valid_ = true;
        }
    }

    private static void run(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        POA system, nonretain, multiple, ether;
        byte[] id1, id2, id3;
        Policy[] policies;
        Test_impl servant1;
        Test_impl servant2;
        Servant tmpserv;

        POAManager manager = root.the_POAManager();

        try {
            manager.activate();
        } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
            throw new RuntimeException();
        }

        //
        // Create POAs
        //

        policies = new Policy[3];
        policies[0] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID);
        policies[1] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        try {
            system = root.create_POA("system_id", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        policies = new Policy[5];
        policies[0] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
        policies[1] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);
        policies[3] = root
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT);
        policies[4] = root
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);
        try {
            nonretain = root.create_POA("nonretain", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        policies = new Policy[3];
        policies[0] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID);
        policies[1] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[2] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
        try {
            multiple = root.create_POA("multiple_id", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        policies = new Policy[3];
        policies[0] = root
                .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID);
        policies[1] = root
                .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID);
        policies[2] = root
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
        try {
            ether = root.create_POA("ether", manager, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }
        TestActivator_impl activatorImpl = new TestActivator_impl();
        ServantActivator activator = activatorImpl._this(orb);

        //
        // Start tests
        //

        //
        // Test: set_servant_manager with nil argument
        //
        try {
            ether.set_servant_manager(null);
            assertTrue(false); // set_servant_manager should not have succeeded
        } catch (OBJ_ADAPTER ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        try {
            ether.set_servant_manager(activator);
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        servant1 = new Test_impl(orb, "obj1", false);
        servant2 = new Test_impl(orb, "obj2", false);

        //
        // Test: activate_object w/ SYSTEM_ID POA
        //

        try {
            id1 = system.activate_object(servant1);
            id2 = system.activate_object(servant2);
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(!TestUtil.Compare(id1, id2));
        try {
            tmpserv = system.id_to_servant(id1);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        assertTrue(tmpserv == servant1);
        try {
            tmpserv = system.id_to_servant(id2);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(tmpserv == servant2);

        //
        // Test: ServantAlreadyActive exception
        //
        try {
            system.activate_object(servant1);
            assertTrue(false); // activate_object should not have succeeded
        } catch (ServantAlreadyActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        try {
            system.activate_object(servant2);
            assertTrue(false); // activate_object should not have succeeded
        } catch (ServantAlreadyActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: deactivate_object
        //
        try {
            system.deactivate_object(id2);
            system.deactivate_object(id1);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: ObjectNotActive exception
        //
        try {
            system.deactivate_object(id1);
            assertTrue(false); // deactivate_object should not have succeeded
        } catch (ObjectNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        try {
            system.deactivate_object(id2);
            assertTrue(false); // deactivate_object should not have succeeded
        } catch (ObjectNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: WrongPolicy exception
        //
        try {
            nonretain.activate_object(servant1);
            assertTrue(false); // activate_object should not have succeeded
        } catch (WrongPolicy ex) {
            // expected
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        }

        try {
            byte[] id = ("XXX").getBytes();
            nonretain.activate_object_with_id(id, servant1);
            assertTrue(false); // activate_object_with_id should not have succeeded
        } catch (WrongPolicy ex) {
            // expected
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (ObjectAlreadyActive ex) {
            throw new RuntimeException();
        }

        try {
            byte[] id = ("XXX").getBytes();
            nonretain.deactivate_object(id);
            assertTrue(false); // deactivate_object should not have succeeded
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            // expected
        }

        //
        // Test: activate_object w/ MULTIPLE_ID POA
        //

        try {
            id1 = multiple.activate_object(servant1);
            id2 = multiple.activate_object(servant1);
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(!TestUtil.Compare(id1, id2));
        try {
            tmpserv = multiple.id_to_servant(id1);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(tmpserv == servant1);
        try {
            tmpserv = multiple.id_to_servant(id2);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(tmpserv == servant1);

        //
        // Test: confirm servant1 is no longer active
        //
        try {
            multiple.deactivate_object(id1);
            multiple.deactivate_object(id2);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        try {
            multiple.id_to_servant(id1);
        } catch (ObjectNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        try {
            multiple.id_to_servant(id2);
        } catch (ObjectNotActive ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: confirm ServantActivator::etherealize is invoked on
        // deactivate
        //
        try {
            id1 = ether.activate_object(servant1);
            id2 = ether.activate_object(servant1);
            id3 = ether.activate_object(servant2);
        } catch (ServantAlreadyActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        activatorImpl.expect(id1, ether, servant1, true);
        try {
            ether.deactivate_object(id1);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(activatorImpl.isValid());
        activatorImpl.expect(id2, ether, servant1, false);
        try {
            ether.deactivate_object(id2);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(activatorImpl.isValid());
        activatorImpl.expect(id3, ether, servant2, false);
        try {
            ether.deactivate_object(id3);
        } catch (ObjectNotActive ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertTrue(activatorImpl.isValid());

        system.destroy(true, true);
        nonretain.destroy(true, true);
        multiple.destroy(true, true);
        ether.destroy(true, true);

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

        tmpserv = null;
        servant1 = null;
        servant2 = null;
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

            //
            // Run the test
            //
            System.out.print("Testing servant activator... ");
            System.out.flush();
            run(orb, root);
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

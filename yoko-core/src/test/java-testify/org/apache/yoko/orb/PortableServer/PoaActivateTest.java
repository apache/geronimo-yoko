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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantActivator;
import org.omg.PortableServer.ServantActivatorPOA;
import test.poa.Test_impl;
import testify.iiop.annotation.ConfigureOrb;

import java.util.Arrays;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID;
import static org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID;
import static org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID;
import static org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID;
import static org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION;
import static org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT;
import static org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER;
import static org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN;
import static org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN;

@ConfigureOrb
public class PoaActivateTest {
    private static Test_impl servant1;
    private static Test_impl servant2;

    @BeforeAll
    static void setup(ORB orb, POA rootPoa) throws Exception {
        rootPoa.the_POAManager().activate();
        servant1 = new Test_impl(orb, "obj1", false);
        servant2 = new Test_impl(orb, "obj2", false);
    }

    @Test
    void testSystemIdPolicy(POA rootPoa) throws Exception {
        POA poa = rootPoa.create_POA("system_id", rootPoa.the_POAManager(), new Policy[]{
                rootPoa.create_id_assignment_policy(SYSTEM_ID),
                rootPoa.create_id_uniqueness_policy(UNIQUE_ID),
                rootPoa.create_servant_retention_policy(RETAIN)});
        byte[] id1 = poa.activate_object(servant1);
        byte[] id2 = poa.activate_object(servant2);
        assertTrue(!Arrays.equals(id1, id2));
        Servant tmpserv = poa.id_to_servant(id1);
        assertTrue(tmpserv == servant1);
        tmpserv = poa.id_to_servant(id2);
        assertTrue(tmpserv == servant2);
        // Should not be able to activate already-activated objects
        assertThrows(ServantAlreadyActive.class, () -> poa.activate_object(servant1));
        assertThrows(ServantAlreadyActive.class, () -> poa.activate_object(servant2));
        // Deactivate should succeed on the still-activated objects
        poa.deactivate_object(id2);
        poa.deactivate_object(id1);
        // Now, deactivate should fail
        assertThrows(ObjectNotActive.class, () -> poa.deactivate_object(id1));
        assertThrows(ObjectNotActive.class, () -> poa.deactivate_object(id2));
        poa.destroy(true, true);
    }

    @Test
    void testWrongPolicy(POA rootPoa) throws Exception {
        POA poa = rootPoa.create_POA("nonretain", rootPoa.the_POAManager(), new Policy[]{
                rootPoa.create_id_assignment_policy(USER_ID),
                rootPoa.create_id_uniqueness_policy(MULTIPLE_ID),
                rootPoa.create_servant_retention_policy(NON_RETAIN),
                rootPoa.create_request_processing_policy(USE_DEFAULT_SERVANT),
                rootPoa.create_implicit_activation_policy(NO_IMPLICIT_ACTIVATION)});
        byte[] id = ("XXX").getBytes();
        assertThrows(WrongPolicy.class, () -> poa.activate_object(servant1));
        assertThrows(WrongPolicy.class, () -> poa.activate_object_with_id(id, servant1));
        assertThrows(WrongPolicy.class, () -> poa.deactivate_object(id));
        poa.destroy(true, true);
    }

    @Test
    void testMultipleIdPolicy(POA rootPoa) throws Exception {
        POA poa = rootPoa.create_POA("multiple_id", rootPoa.the_POAManager(), new Policy[]{
                rootPoa.create_id_assignment_policy(SYSTEM_ID),
                rootPoa.create_id_uniqueness_policy(MULTIPLE_ID),
                rootPoa.create_servant_retention_policy(RETAIN)});

        Servant tmpserv;
        byte[] id1 = poa.activate_object(servant1);
        byte[] id2 = poa.activate_object(servant1);
        assertTrue(!Arrays.equals(id1, id2));

        tmpserv = poa.id_to_servant(id1);
        assertTrue(tmpserv == servant1);

        tmpserv = poa.id_to_servant(id2);
        assertTrue(tmpserv == servant1);

        poa.deactivate_object(id1);
        poa.deactivate_object(id2);

        // Test: confirm servants are no longer active
        assertThrows(ObjectNotActive.class, () -> poa.id_to_servant(id1));
        assertThrows(ObjectNotActive.class, () -> poa.id_to_servant(id2));

        poa.destroy(true, true);
    }

    @Test
    public void testEtherialize(ORB orb, POA rootPoa) throws Exception {
        // A ServantActivator should have etherialize() called
        // when (and only when) its servants are deactivated.
        POA poa = rootPoa.create_POA("ether", rootPoa.the_POAManager(), new Policy[]{
                rootPoa.create_id_assignment_policy(SYSTEM_ID),
                rootPoa.create_id_uniqueness_policy(MULTIPLE_ID),
                rootPoa.create_request_processing_policy(USE_SERVANT_MANAGER)});

        // should throw OBJ_ADAPTER when set to null
        assertThrows(OBJ_ADAPTER.class, () -> poa.set_servant_manager(null));

        TestActivator_impl activatorImpl = new TestActivator_impl();
        ServantActivator activator = activatorImpl._this(orb);

        // otherwise should work
        poa.set_servant_manager(activator);

        //
        // Test: activate_object w/ MULTIPLE_ID POA
        //

        // Test: confirm ServantActivator::etherealize is invoked on deactivate
        byte[] id1 = poa.activate_object(servant1);
        byte[] id2 = poa.activate_object(servant1);
        byte[] id3 = poa.activate_object(servant2);
        assertTrue(activatorImpl.isValid());
        activatorImpl.expect(id1, poa, servant1, true);
        poa.deactivate_object(id1);
        assertTrue(activatorImpl.isValid());

        activatorImpl.expect(id2, poa, servant1, false);
        poa.deactivate_object(id2);
        assertTrue(activatorImpl.isValid());

        activatorImpl.expect(id3, poa, servant2, false);
        poa.deactivate_object(id3);
        assertTrue(activatorImpl.isValid());

        poa.destroy(true, true);
        assertTrue(activatorImpl.isValid());

        byte[] id = rootPoa.servant_to_id(activatorImpl);
        rootPoa.deactivate_object(id);
        assertTrue(activatorImpl.isValid());
    }

    final static class TestActivator_impl extends ServantActivatorPOA {
        private byte[] oid;
        private POA poa;
        private Servant servant;
        private boolean remaining;
        private boolean valid = true;
        private boolean expectingEtherialize;

        void expect(byte[] oid, POA poa, Servant servant, boolean remaining) {
            this.oid = oid;
            this.poa = poa;
            this.servant = servant;
            this.remaining = remaining;
            this.valid = false;
            this.expectingEtherialize = true;
        }

        boolean isValid() { return valid; }

        public Servant incarnate(byte[] oid, POA poa) { return null; }

        public void etherealize(byte[] oid, POA poa, Servant servant, boolean cleanup, boolean remaining) {
            try {
                valid = false;
                assertTrue(expectingEtherialize);
                assertTrue(Arrays.equals(this.oid, oid));
                assertTrue(this.poa._is_equivalent(poa));
                assertTrue(this.servant == servant);
                assertTrue(this.remaining == remaining);
                valid = true;
            } finally {
                expectingEtherialize = false;
            }
        }
    }
}

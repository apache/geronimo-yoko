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
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import test.poa.Test_impl;
import testify.iiop.annotation.ConfigureOrb;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.apache.yoko.orb.PortableServer.Policies.IMPLICIT_ACTIVATION;
import static org.apache.yoko.orb.PortableServer.Policies.MULTIPLE_ID;
import static org.apache.yoko.orb.PortableServer.Policies.NO_IMPLICIT_ACTIVATION;
import static org.apache.yoko.orb.PortableServer.Policies.PERSISTENT;
import static org.apache.yoko.orb.PortableServer.Policies.RETAIN;
import static org.apache.yoko.orb.PortableServer.Policies.UNIQUE_ID;
import static org.apache.yoko.orb.PortableServer.Policies.USER_ID;
import static org.apache.yoko.orb.PortableServer.Policies.USE_DEFAULT_SERVANT;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ConfigureOrb
public class TestPoaMisc {
    static POA rootPoa;
    static POA childPoa;

    @BeforeAll
    static void setup(POA rootPoa) throws Exception {
        TestPoaMisc.rootPoa = rootPoa;
        childPoa = rootPoa.create_POA("child", rootPoa.the_POAManager(), Policies.of(rootPoa));
    }

    @Test
    public void testMiscRootPoa(ORB orb) throws Exception {
        runTests(orb, rootPoa);
    }

    @Test
    public void testMiscChildPoa(ORB orb) throws Exception {
        runTests(orb, childPoa);
    }

    void runTests(ORB orb, POA root) throws Exception {
        testCreateReference(root);
        testServantToId(orb, root);
        testIdToServant(orb, root);
        testServantToReference(orb, root);
        testIdToReference(orb, root);
        testReferenceToServant(orb, root);
        testReferenceToId(root);
    }

    void testCreateReference(POA poa) throws Exception {
        POAManager manager = poa.the_POAManager();
        POA user = poa.create_POA("user_id", manager, Policies.of(poa, Policies.USER_ID));
        POA system = poa.create_POA("system_id", manager, Policies.of(poa, Policies.SYSTEM_ID));

        try {
            assertThrows(WrongPolicy.class, () -> user.create_reference("IDL:Test:1.0"));

            // Test: create_reference - should get a new ID for each invocation on POA w/ SYSTEM_ID policy
            final byte[] id1 = system.reference_to_id(requireNonNull(system.create_reference("IDL:Test:1.0")));
            final byte[] id2 = system.reference_to_id(requireNonNull(system.create_reference("IDL:Test:1.0")));
            assertFalse(Arrays.equals(id1, id2));

            // Test: create_reference_with_id using a system-generated ID
            system.create_reference_with_id(id1, "IDL:Test:1.0");

            // Test: create_reference_with_id using a user-generated ID
            final byte[] userId1 = ("id1").getBytes();
            assertThrows(BAD_PARAM.class, () -> system.create_reference_with_id(userId1, "IDL:Test:1.0"));

            // Test: create_reference_with_id
            assertArrayEquals(userId1,
                    user.reference_to_id(requireNonNull(user.create_reference_with_id(userId1, "IDL:Test:1.0"))));
            final byte[] userId2 = ("id2").getBytes();
            assertArrayEquals(userId2,
                    user.reference_to_id(requireNonNull(user.create_reference_with_id(userId2, "IDL:Test:1.0"))));
        } finally {
            user.destroy(true, true);
            system.destroy(true, true);
        }
    }

    void testServantToId(ORB orb, POA poa) throws Exception {
        POAManager manager = poa.the_POAManager();


        POA unique = poa.create_POA("unique_id", manager, Policies.of(poa, UNIQUE_ID, USER_ID, RETAIN, NO_IMPLICIT_ACTIVATION));
        POA implicit = poa.create_POA("implicit", manager, Policies.of(poa, UNIQUE_ID, RETAIN, IMPLICIT_ACTIVATION));
        POA multiple = poa.create_POA("multiple", manager, Policies.of(poa, MULTIPLE_ID, RETAIN, IMPLICIT_ACTIVATION));

        try {
            Test_impl servant1 = new Test_impl(orb, "test1", false);
            Test_impl servant2 = new Test_impl(orb, "test2", false);

            assertThrows(ServantNotActive.class, () -> unique.servant_to_id(servant1));

            unique.activate_object_with_id("test1".getBytes(), servant1);

            // Test: servant_to_id (UNIQUE_ID policy)
            assertArrayEquals("test1".getBytes(), unique.servant_to_id(servant1));

            // Test: servant_to_id (IMPLICIT_ACTIVATION) - servant1 should be automatically activated
            final byte[] implicitId = implicit.servant_to_id(servant1);

            // Test: Now that servant1 is activated, and since we have UNIQUE_ID, we should get the same ID back
            assertArrayEquals(implicitId, implicit.servant_to_id(servant1));

            // Test: Implicitly activating servant2 should produce a new ID
            assertFalse(Arrays.equals(implicitId, implicit.servant_to_id(servant2)));

            // Test: servant_to_id (IMPLICIT_ACTIVATION, MULTIPLE_ID) - servant1 should be automatically activated
            final byte[] multipleId1 = multiple.servant_to_id(servant1);

            // Test: Since we have MULTIPLE_ID, we should get a new ID
            assertFalse(Arrays.equals(multipleId1, multiple.servant_to_id(servant1)));
        } finally {
            unique.destroy(true, true);
            implicit.destroy(true, true);
            multiple.destroy(true, true);
        }
    }

    void testIdToServant(ORB orb, POA poa) throws Exception {
        POAManager manager = poa.the_POAManager();
        POA retain = poa.create_POA("retain", manager, Policies.of(poa, RETAIN, PERSISTENT, USER_ID));
        POA defaultPOA = poa.create_POA("default", manager, Policies.of(poa, MULTIPLE_ID, RETAIN, USE_DEFAULT_SERVANT, PERSISTENT, USER_ID));
        try {
            Test_impl def = new Test_impl(orb, "default", false);
            defaultPOA.set_servant(def);

            Test_impl servant1 = new Test_impl(orb, "test1", false);
            Test_impl servant2 = new Test_impl(orb, "test2", false);

            assertThrows(ObjectNotActive.class, () -> retain.id_to_servant("bad_id".getBytes()));

            final byte[] id1 = "test1".getBytes();
            final byte[] id2 = "test2".getBytes();

            retain.activate_object_with_id(id1, servant1);
            retain.activate_object_with_id(id2, servant2);

            // Test: servant_to_id (RETAIN policy)
            Servant tmpservant = retain.id_to_servant(id1);
            assertSame(servant1, tmpservant);

            tmpservant = retain.id_to_servant(id2);
            assertSame(servant2, tmpservant);

            // Test: id_to_servant (USE_DEFAULT_SERVANT)
            defaultPOA.activate_object_with_id(id1, servant1);
            defaultPOA.activate_object_with_id(id2, servant2);

            tmpservant = defaultPOA.id_to_servant(id1);
            assertSame(servant1, tmpservant);

            tmpservant = defaultPOA.id_to_servant(id2);
            assertSame(servant2, tmpservant);

            // Test: id_to_servant (USE_DEFAULT_SERVANT) - should return default servant for all unknown IDs
            tmpservant = defaultPOA.id_to_servant(("test99").getBytes());
            assertSame(tmpservant, def);
        } finally {
            retain.destroy(true, true);
            defaultPOA.destroy(true, true);
        }
    }

    void testServantToReference(ORB orb, POA root) throws Exception{
        POAManager manager = root.the_POAManager();
        POA unique = root.create_POA("unique_id", manager, Policies.of(root, UNIQUE_ID, USER_ID, RETAIN, NO_IMPLICIT_ACTIVATION));
        POA implicit = root.create_POA("implicit", manager, Policies.of(root, UNIQUE_ID, RETAIN, IMPLICIT_ACTIVATION));
        POA multiple = root.create_POA("multiple", manager, Policies.of(root, MULTIPLE_ID, RETAIN, IMPLICIT_ACTIVATION));

        Test_impl servant1 = new Test_impl(orb, "test1", false);
        Test_impl servant2 = new Test_impl(orb, "test2", false);
        try {
            assertThrows(ServantNotActive.class, () -> unique.servant_to_reference(servant1));

            byte[] id1 = "test1".getBytes();
            unique.activate_object_with_id(id1, servant1);

            // Test: servant_to_reference (UNIQUE_ID policy)
            byte[] tmpid1 = unique.reference_to_id(requireNonNull(unique.servant_to_reference(servant1)));
            assertArrayEquals(id1, tmpid1);

            // Test: servant_to_reference (IMPLICIT_ACTIVATION) - servant1 should be automatically activated
            tmpid1 = implicit.reference_to_id(requireNonNull(implicit.servant_to_reference(servant1)));

            // Test: Now that servant1 is activated, and since we have UNIQUE_ID, we should get the same ID back
            byte[] tmpid2 = implicit.reference_to_id(requireNonNull(implicit.servant_to_reference(servant1)));
            assertArrayEquals(tmpid1, tmpid2);

            // Test: Implicitly activating servant2 should produce a new ID
            tmpid2 = implicit.reference_to_id(requireNonNull(implicit.servant_to_reference(servant2)));
            assertFalse(Arrays.equals(tmpid1, tmpid2));

            // Test: servant_to_reference (IMPLICIT_ACTIVATION, MULTIPLE_ID) - servant1 should be automatically activated
            tmpid1 = multiple.reference_to_id(requireNonNull(multiple.servant_to_reference(servant1)));

            // Test: Since we have MULTIPLE_ID, we should get a new ID
            tmpid2 = multiple.reference_to_id(requireNonNull(multiple.servant_to_reference(servant1)));
            assertFalse(Arrays.equals(tmpid1, tmpid2));
        } finally {
            unique.destroy(true, true);
            implicit.destroy(true, true);
            multiple.destroy(true, true);
        }
    }

    void testIdToReference(ORB orb, POA root) throws Exception{
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
        retain = root.create_POA("retain", manager, Policies.of(root, RETAIN, PERSISTENT, USER_ID));


        servant1 = new Test_impl(orb, "test1", false);
        servant2 = new Test_impl(orb, "test2", false);

        //
        // Test: ObjectNotActive exception
        //
        try {
            tmpid = ("bad_id").getBytes();
            retain.id_to_reference(tmpid);
            fail(); // id_to_reference should not have succeeded
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
        assertNotNull(obj);
        try {
            tmpid = retain.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertArrayEquals(id1, tmpid);

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
        assertNotNull(obj);
        try {
            tmpid = retain.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertArrayEquals(id2, tmpid);

        retain.destroy(true, true);
    }

    void testReferenceToServant(ORB orb, POA root) throws Exception{
        org.omg.CORBA.Object obj;
        POA retain, defaultPOA;
        byte[] id1, id2, tmpid;
        Policy[] policies;
        Test_impl def;
        Test_impl servant1;
        Test_impl servant2;
        Servant tmpservant;

        POAManager manager = root.the_POAManager();

        // Create POA w/ RETAIN
        retain = root.create_POA("retain", manager, Policies.of(root, RETAIN, PERSISTENT, USER_ID));


        // Create POA w/ USE_DEFAULT_SERVANT
        defaultPOA = root.create_POA("default", manager, Policies.of(root, MULTIPLE_ID, RETAIN, USE_DEFAULT_SERVANT, PERSISTENT, USER_ID));

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
            fail(); // reference_to_servant should not have succeeded
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
        assertSame(servant1, tmpservant);
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
        assertSame(servant2, tmpservant);

        //
        // Test: WrongAdapter exception
        //
        try {
            obj = retain.create_reference_with_id(id1, "IDL:Test:1.0");
            defaultPOA.reference_to_servant(obj);
            fail(); // reference_to_servant should not have succeeded
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
        assertSame(servant1, tmpservant);
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
        assertSame(servant2, tmpservant);

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
        assertSame(tmpservant, def);
        tmpservant = null;

        retain.destroy(true, true);
        defaultPOA.destroy(true, true);
    }

    void testReferenceToId(POA root) throws Exception{
        org.omg.CORBA.Object obj;
        POA poa;
        byte[] id1, id2, tmpid;
        Policy[] policies;

        POAManager manager = root.the_POAManager();

        // Create POA
        poa = root.create_POA("poa", manager, Policies.of(root, RETAIN, PERSISTENT, USER_ID));


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
        assertArrayEquals(tmpid, id1);
        obj = poa.create_reference_with_id(id2, "IDL:Test:1.0");
        try {
            tmpid = poa.reference_to_id(obj);
        } catch (WrongAdapter ex) {
            throw new RuntimeException();
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }
        assertArrayEquals(tmpid, id2);

        //
        // Test: WrongAdapter exception
        //
        try {
            obj = poa.create_reference_with_id(id1, "IDL:Test:1.0");
            root.reference_to_id(obj);
            fail(); // reference_to_id should not have succeeded
        } catch (WrongAdapter ex) {
            // expected
        } catch (WrongPolicy ex) {
            throw new RuntimeException();
        }

        poa.destroy(true, true);
    }



}

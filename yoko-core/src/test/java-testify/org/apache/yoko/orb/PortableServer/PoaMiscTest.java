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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import test.poa.Test_impl;
import testify.iiop.annotation.ConfigureOrb;
import testify.io.EasyCloseable;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static org.apache.yoko.orb.PortableServer.PolicyValue.IMPLICIT_ACTIVATION;
import static org.apache.yoko.orb.PortableServer.PolicyValue.MULTIPLE_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.NO_IMPLICIT_ACTIVATION;
import static org.apache.yoko.orb.PortableServer.PolicyValue.PERSISTENT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.RETAIN;
import static org.apache.yoko.orb.PortableServer.PolicyValue.SYSTEM_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.UNIQUE_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USER_ID;
import static org.apache.yoko.orb.PortableServer.PolicyValue.USE_DEFAULT_SERVANT;
import static org.apache.yoko.orb.PortableServer.PolicyValue.create_POA;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ConfigureOrb
public class PoaMiscTest {
    static ORB orb;
    static POA rootPoa;
    static POA childPoa;
    static Test_impl servant1;
    static Test_impl servant2;
    static Test_impl defaultServant;

    enum PoaChoice {
        ROOT_POA, CHILD_POA;
        POA poa() { return this == ROOT_POA ? rootPoa : childPoa; }
    }

    interface Cleanup extends EasyCloseable {} // use this to register try-with-resources

    @BeforeAll
    static void setup(ORB orb, POA rootPoa) throws Exception {
        PoaMiscTest.orb = orb;
        PoaMiscTest.rootPoa = rootPoa;
        PoaMiscTest.childPoa = create_POA("child", rootPoa, rootPoa.the_POAManager());
        PoaMiscTest.servant1 = new Test_impl(orb, "test1", false);
        PoaMiscTest.servant2 = new Test_impl(orb, "test2", false);
        PoaMiscTest.defaultServant = new Test_impl(orb, "default", false);

    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testCreateReferenceSystem(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA system = create_POA("system_id", poa, manager, SYSTEM_ID);

        try (Cleanup x = () -> system.destroy(true, true)) {
            // Test: create_reference - should get a new ID for each invocation on POA w/ SYSTEM_ID policy
            final byte[] id1 = system.reference_to_id(requireNonNull(system.create_reference("IDL:Test:1.0")));
            final byte[] id2 = system.reference_to_id(requireNonNull(system.create_reference("IDL:Test:1.0")));
            assertFalse(Arrays.equals(id1, id2));
            // Test: create_reference_with_id using a system-generated ID
            system.create_reference_with_id(id1, "IDL:Test:1.0");
            // Test: create_reference_with_id using a user-supplied ID
            assertThrows(BAD_PARAM.class, () -> system.create_reference_with_id(("id1").getBytes(), "IDL:Test:1.0"));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testCreateReferenceUser(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA user = create_POA("user_id", poa, manager, USER_ID);
        try (Cleanup x = () -> user.destroy(true, true)) {
            assertThrows(WrongPolicy.class, () -> user.create_reference("IDL:Test:1.0"));
            // Test: create_reference_with_id
            assertArrayEquals(("id1").getBytes(), user.reference_to_id(requireNonNull(user.create_reference_with_id(("id1").getBytes(), "IDL:Test:1.0"))));
            assertArrayEquals(("id2").getBytes(), user.reference_to_id(requireNonNull(user.create_reference_with_id(("id2").getBytes(), "IDL:Test:1.0"))));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testServantToIdUnique(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA unique = create_POA("unique_id", poa, manager, UNIQUE_ID, USER_ID, RETAIN, NO_IMPLICIT_ACTIVATION);
        try (Cleanup x = () -> unique.destroy(true, true)) {
            assertThrows(ServantNotActive.class, () -> unique.servant_to_id(servant1));
            unique.activate_object_with_id("test1".getBytes(), servant1);
            // Test: servant_to_id (UNIQUE_ID policy)
            assertArrayEquals("test1".getBytes(), unique.servant_to_id(servant1));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testServantToIdImplicit(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA implicit = create_POA("implicit", poa, manager, UNIQUE_ID, RETAIN, IMPLICIT_ACTIVATION);
        try (Cleanup x = () -> implicit.destroy(true, true)) {
            // Test: servant_to_id (IMPLICIT_ACTIVATION) - servant1 should be automatically activated
            final byte[] implicitId = implicit.servant_to_id(servant1);
            // Test: Now that servant1 is activated, and since we have UNIQUE_ID, we should get the same ID back
            assertArrayEquals(implicitId, implicit.servant_to_id(servant1));
            // Test: Implicitly activating servant2 should produce a new ID
            assertFalse(Arrays.equals(implicitId, implicit.servant_to_id(servant2)));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testServantToIdMultiple(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA multiple = create_POA("multiple", poa, manager, MULTIPLE_ID, RETAIN, IMPLICIT_ACTIVATION);
        try (Cleanup x = () -> multiple.destroy(true, true)) {
            // Test: servant_to_id (IMPLICIT_ACTIVATION, MULTIPLE_ID) - servant1 should be automatically activated
            final byte[] multipleId1 = multiple.servant_to_id(servant1);
            // Test: Since we have MULTIPLE_ID, we should get a new ID
            assertFalse(Arrays.equals(multipleId1, multiple.servant_to_id(servant1)));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testServantToReferenceUnique(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA unique = create_POA("unique_id", poa, manager, UNIQUE_ID, USER_ID, RETAIN, NO_IMPLICIT_ACTIVATION);
        try (Cleanup x = () -> unique.destroy(true, true)) {
            assertThrows(ServantNotActive.class, () -> unique.servant_to_reference(servant1));
            unique.activate_object_with_id("test1".getBytes(), servant1);
            assertArrayEquals("test1".getBytes(), unique.reference_to_id(requireNonNull(unique.servant_to_reference(servant1))));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testServantToReferenceImplicit(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA implicit = create_POA("implicit", poa, manager, UNIQUE_ID, RETAIN, IMPLICIT_ACTIVATION);
        try (Cleanup x = () -> implicit.destroy(true, true)) {
            // Test: servant_to_reference (IMPLICIT_ACTIVATION) - servant1 should be automatically activated
            byte[] serv1_id = implicit.reference_to_id(requireNonNull(implicit.servant_to_reference(servant1)));
            // Test: Now that servant1 is activated, and since we have UNIQUE_ID, we should get the same ID back
            byte[] serv1_id_again = implicit.reference_to_id(requireNonNull(implicit.servant_to_reference(servant1)));
            assertArrayEquals(serv1_id, serv1_id_again);
            // Test: Implicitly activating servant2 should produce a new ID
            byte[] serv2_id = implicit.reference_to_id(requireNonNull(implicit.servant_to_reference(servant2)));
            assertFalse(Arrays.equals(serv1_id, serv2_id));
        }
    }

        @ParameterizedTest @EnumSource(PoaChoice.class)
    void testServantToReferenceMultiple(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA multiple = create_POA("multiple", poa, manager, MULTIPLE_ID, RETAIN, IMPLICIT_ACTIVATION);
        try (Cleanup x = () -> multiple.destroy(true, true)) {
            // Test: servant_to_reference (IMPLICIT_ACTIVATION, MULTIPLE_ID) - servant1 should be automatically activated
            byte[] id1 = multiple.reference_to_id(requireNonNull(multiple.servant_to_reference(servant1)));
            // Test: Since we have MULTIPLE_ID, we should get a new ID
            byte[] id2 = multiple.reference_to_id(requireNonNull(multiple.servant_to_reference(servant1)));
            assertFalse(Arrays.equals(id1, id2));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testIdToServantRetain(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA retain = create_POA("retain", poa, manager, RETAIN, PERSISTENT, USER_ID);
        try (Cleanup x = () -> retain.destroy(true, true)) {
            assertThrows(ObjectNotActive.class, () -> retain.id_to_servant("bad_id".getBytes()));

            retain.activate_object_with_id("test1".getBytes(), servant1);
            retain.activate_object_with_id("test2".getBytes(), servant2);

            // Test: servant_to_id (RETAIN policy)
            assertSame(servant1, retain.id_to_servant("test1".getBytes()));
            assertSame(servant2, retain.id_to_servant("test2".getBytes()));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testIdToServantDefault(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA defaultPOA = create_POA("default", poa, manager, MULTIPLE_ID, RETAIN, USE_DEFAULT_SERVANT, PERSISTENT, USER_ID);
        try (Cleanup y = () -> defaultPOA.destroy(true, true)) {
            defaultPOA.set_servant(defaultServant);
            // Test: id_to_servant (USE_DEFAULT_SERVANT)
            defaultPOA.activate_object_with_id("test1".getBytes(), servant1);
            defaultPOA.activate_object_with_id("test2".getBytes(), servant2);
            assertSame(servant1, defaultPOA.id_to_servant("test1".getBytes()));
            assertSame(servant2, defaultPOA.id_to_servant("test2".getBytes()));
            // Test: id_to_servant (USE_DEFAULT_SERVANT) - should return default servant for all unknown IDs
            assertSame(defaultPOA.id_to_servant(("test99").getBytes()), defaultServant);
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testIdToReference(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA retain = create_POA("retain", poa, manager, RETAIN, PERSISTENT, USER_ID);
        try (Cleanup x = () -> retain.destroy(true, true)) {
            assertThrows(ObjectNotActive.class, () -> retain.id_to_reference("bad_id".getBytes()));

            retain.activate_object_with_id("test1".getBytes(), servant1);
            retain.activate_object_with_id("test2".getBytes(), servant2);

            assertArrayEquals("test1".getBytes(), retain.reference_to_id(requireNonNull(retain.id_to_reference("test1".getBytes()))));
            assertArrayEquals("test2".getBytes(), retain.reference_to_id(requireNonNull(retain.id_to_reference("test2".getBytes()))));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testReferenceToServantRetain(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();
        POA retain = create_POA("retain", poa, manager, RETAIN, PERSISTENT, USER_ID);

        try (Cleanup x = () -> retain.destroy(true, true)) {
            org.omg.CORBA.Object badIdRef = retain.create_reference_with_id("bad_id".getBytes(), "IDL:Test:1.0");
            assertThrows(ObjectNotActive.class, () -> retain.reference_to_servant(badIdRef));

            retain.activate_object_with_id("test1".getBytes(), servant1);
            retain.activate_object_with_id("test2".getBytes(), servant2);

            assertSame(servant1, retain.reference_to_servant(retain.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0")));
            assertSame(servant2, retain.reference_to_servant(retain.create_reference_with_id("test2".getBytes(), "IDL:Test:1.0")));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testReferenceToServantDefault(PoaChoice which) throws Exception {
        POA poa = which.poa();
        POAManager manager = poa.the_POAManager();

        POA defaultPoa = create_POA("default", poa, manager, MULTIPLE_ID, RETAIN, USE_DEFAULT_SERVANT, PERSISTENT, USER_ID);
        POA otherPoa = create_POA("retain", poa, manager, RETAIN, PERSISTENT, USER_ID);

        try   (Cleanup x = () -> defaultPoa.destroy(true, true);
               Cleanup y = () -> otherPoa.destroy(true, true)) {
            defaultPoa.set_servant(defaultServant);
            // create a reference using a different POA
            Object ref = otherPoa.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0");
            assertThrows(WrongAdapter.class, () -> defaultPoa.reference_to_servant(ref));

            defaultPoa.activate_object_with_id("test1".getBytes(), servant1);
            defaultPoa.activate_object_with_id("test2".getBytes(), servant2);

            assertSame(servant1, defaultPoa.reference_to_servant(defaultPoa.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0")));
            assertSame(servant2, defaultPoa.reference_to_servant(defaultPoa.create_reference_with_id("test2".getBytes(), "IDL:Test:1.0")));
            assertSame(defaultServant, defaultPoa.reference_to_servant(defaultPoa.create_reference_with_id("test99".getBytes(), "IDL:Test:1.0")));
        }
    }

    @ParameterizedTest @EnumSource(PoaChoice.class)
    void testReferenceToId(PoaChoice which) throws Exception{
        POA parentPoa = which.poa();
        POAManager manager = parentPoa.the_POAManager();
        POA subPoa = create_POA("poa", parentPoa, manager, RETAIN, PERSISTENT, USER_ID);
        try (Cleanup x = () -> subPoa.destroy(true, true)) {
            assertArrayEquals(subPoa.reference_to_id(subPoa.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0")), "test1".getBytes());
            assertArrayEquals(subPoa.reference_to_id(subPoa.create_reference_with_id("test2".getBytes(), "IDL:Test:1.0")), "test2".getBytes());
            assertThrows(WrongAdapter.class, () -> parentPoa.reference_to_id(subPoa.create_reference_with_id("test1".getBytes(), "IDL:Test:1.0")));
        }
    }
}

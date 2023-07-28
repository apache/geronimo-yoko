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
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.State;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import testify.iiop.annotation.ConfigureOrb;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ConfigureOrb
public class TestPoaCreate {
    private static POAManager rootMgr;

    @BeforeAll
    static void setup(POA rootPoa) {
        rootMgr = rootPoa.the_POAManager();
    }

    @Test
    public void testCreatePOA(POA rootPoa) throws Exception {
        assertTrue(rootMgr != null);

        // Test: POAManager should be in HOLDING state
//      ***  assertTrue(rootMgr.get_state() == State.HOLDING);

        // Create child POA
        POA poa = rootPoa.create_POA("poa1", null, new Policy[]{});

        // Test: POAManager should NOT be the same as the root's manager
        POAManager mgr = poa.the_POAManager();
        assertTrue(!mgr._is_equivalent(rootMgr));

        // Test: POAManager should be in HOLDING state
        assertTrue(mgr.get_state() == State.HOLDING);

        // Test: Confirm name
        String poaName = poa.the_name();
        assertTrue(poaName.equals("poa1"));

        // Test: Confirm parent
        POA parent = poa.the_parent();
        assertTrue(parent._is_equivalent(rootPoa));

        // Test: AdapterAlreadyExists exception
        assertThrows(AdapterAlreadyExists.class, () -> rootPoa.create_POA("poa1", null, new Policy[]{}));

        // Test: InvalidPolicy exception
        Policy[] invalidPolicies = new Policy[] {
                rootPoa.create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN)};
        assertThrows(InvalidPolicy.class, () -> rootPoa.create_POA("invalid", null, invalidPolicies));

        poa.destroy(true, true);
    }

    @Test
    void testCreateChildOfChildPoa(POA rootPoa) throws Exception{
        // Create another child of root POA
        POA poa = rootPoa.create_POA("poa", rootMgr, new Policy[]{});

        // Test: POAManager should be the same as the root's manager
        POAManager mgr = poa.the_POAManager();
        assertTrue(mgr._is_equivalent(rootMgr));

        // Create child of child POA
        POA poa3 = poa.create_POA("child", rootMgr, new Policy[]{});

        // Test: Confirm parent
        POA parent = poa3.the_parent();
        assertTrue(parent._is_equivalent(poa));

        poa.destroy(true, true);
    }
}

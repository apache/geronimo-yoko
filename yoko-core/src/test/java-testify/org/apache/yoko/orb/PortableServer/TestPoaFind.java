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

import org.junit.jupiter.api.Test;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import testify.iiop.annotation.ConfigureOrb;

import static org.junit.Assert.assertTrue;

@ConfigureOrb
public class TestPoaFind {
    @Test
    public void testFind(ORB orb, POA rootPoa) {
        org.omg.CORBA.Object obj;
        Policy[] policies = new Policy[0];
        POA poa, parent, poa2, poa3;
        POAManager mgr;
        String str;

        POAManager rootMgr = rootPoa.the_POAManager();
        assertTrue(rootMgr != null);

        //
        // Create child POA
        //
        try {
            poa = rootPoa.create_POA("poa1", rootMgr, policies);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Test: find_POA
        //
        try {
            poa2 = rootPoa.find_POA("poa1", false);
        } catch (AdapterNonExistent ex) {
            throw new RuntimeException();
        }
        assertTrue(poa2 != null);
        assertTrue(poa2._is_equivalent(poa));

        //
        // Test: AdapterNonExistent exception
        //
        try {
            poa2 = rootPoa.find_POA("poaX", false);
            assertTrue(false); // find_POA should not have succeeded
        } catch (AdapterNonExistent ex) {
            // expected
        }

        //
        // Create child POA
        //
        try {
            poa2 = rootPoa.create_POA("poa2", rootMgr, policies);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Test: Confirm parent knows about child
        //
        try {
            poa3 = rootPoa.find_POA("poa2", false);
        } catch (AdapterNonExistent ex) {
            throw new RuntimeException();
        }

        assertTrue(poa3 != null);
        assertTrue(poa3._is_equivalent(poa2));
    }
}

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
import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import testify.iiop.annotation.ConfigureOrb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ConfigureOrb
public class PoaFindTest {
    @Test
    void testFind(POA rootPoa) throws Exception {
        POAManager rootMgr = rootPoa.the_POAManager();
        assertNotNull(rootMgr);

        // Create child POA
        POA poa = rootPoa.create_POA("poa1", rootMgr, new Policy[]{});

        // Test: find_POA
        POA poa2 = rootPoa.find_POA("poa1", false);
        assertNotNull(poa2);
        assertTrue(poa2._is_equivalent(poa));

        // Test: AdapterNonExistent exception
        assertThrows(AdapterNonExistent.class, () -> rootPoa.find_POA("poaX", false));
    }
}

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
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import test.poa.TestMisc;
import testify.iiop.annotation.ConfigureOrb;

@ConfigureOrb
public class TestPoaMisc {
    @Test
    public void testMisc(ORB orb, POA rootPoa) throws RuntimeException, InvalidPolicy, AdapterAlreadyExists {
            TestMisc.runtests(orb, rootPoa);
            // Create a child POA and run the tests again using the child as the root
            POAManager poaManager = rootPoa.the_POAManager();
            POA childPoa = rootPoa.create_POA("child", poaManager, new Policy[0]);
            TestMisc.runtests(orb, childPoa);
    }
}

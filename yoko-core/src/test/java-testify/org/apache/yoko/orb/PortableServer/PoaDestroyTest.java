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
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import test.poa.TestPOA;
import testify.iiop.annotation.ConfigureOrb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;


@ConfigureOrb
public class PoaDestroyTest {
    private static POAManager rootMgr;

    @BeforeAll
    static void setup(POA rootPoa) throws Exception {
        rootMgr = rootPoa.the_POAManager();
    }

    @Test
    void testDestroyPoa(POA rootPoa) throws Exception {
        assertNotNull(rootMgr);
        POA poa = rootPoa.create_POA("poa1", rootMgr, new Policy[]{});
        poa.destroy(true, true);
        // Ensure parent no longer knows about child
        assertThrows(AdapterNonExistent.class, () -> rootPoa.find_POA("poa1", false));
    }

    @Test
    void testDestroyPoaWithChild(ORB orb, POA rootPoa) throws Exception {
        assertNotNull(rootMgr);
        POA poa = rootPoa.create_POA("poa1", rootMgr, new Policy[]{});
        // Create child of child POA
        poa.create_POA("child1", rootMgr, new Policy[]{});
        // Test: destroy - should destroy poa1 and poa1/child1
        poa.destroy(true, true);
        // Ensure parent no longer knows about child
        assertThrows(AdapterNonExistent.class, () -> rootPoa.find_POA("poa1", false));
    }

    @Test
    void testDestroyWaitsForMethodCallCompletion(ORB orb, POA rootPoa) throws Exception {
        assertNotNull(rootMgr);

        // Create child POA
        POA poa1 = rootPoa.create_POA("poa1", rootMgr, new Policy[] {
                rootPoa.create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION)});

        Test_impl1 impl = new Test_impl1(poa1);
        test.poa.Test t = impl._this(orb);
        Thread thr = new LongCaller(t);
        thr.start();
        impl.blockUntilCalled();

        // Test: Destroy the POA while a method call is active
        poa1.destroy(true, true);

        // The destroy call shouldn't return until the aMethod call is complete
        assertTrue(impl.callComplete());

        while (thr.isAlive()) {
            try { thr.join(); } catch (InterruptedException ignored) {}
        }
    }

    final static class Test_impl1 extends TestPOA {
        private final POA poa;
        private boolean called = false;
        private boolean finished = false;
        Test_impl1(POA poa) {
            this.poa = poa;
        }

        synchronized public void aMethod() {
            called = true;
            notify();
            try { wait(1000); } catch (InterruptedException ignored) {}
            finished = true;
        }

        public POA _default_POA() {
            return poa;
        }

        synchronized void blockUntilCalled() {
            while (!called) {
                try { wait(); } catch (InterruptedException ignored) {}
            }
        }
        synchronized boolean callComplete() {
            return finished;
        }
    }

    final static class LongCaller extends Thread {
        private final test.poa.Test t;
        LongCaller(test.poa.Test t) {
            this.t = t;
        }
        public void run() {
            try {
                t.aMethod();
            } catch (SystemException se) {
                System.err.println(se.getMessage());
                se.printStackTrace();
            }
        }
    }
}

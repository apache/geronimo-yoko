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
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.POA;
import test.poa.TestPOA;
import testify.iiop.annotation.ConfigureOrb;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ConfigureOrb
public class PoaDeactivateTest {
    @Test
    void testDeactivate(ORB orb, POA rootPoa) throws Exception {
        test.poa.Test_impl impl = new test.poa.Test_impl(orb, "", false);
        test.poa.Test t = impl._this(orb);
        t.aMethod();
        byte[] oid = rootPoa.servant_to_id(impl);
        rootPoa.deactivate_object(oid);
        assertThrows(OBJECT_NOT_EXIST.class, () -> t.aMethod());
    }

    // In this test we want to spawn a thread to call a method on the Test
    // interface. This method call should take some time. While the thread
    // is calling the method we attempt to deactivate the object. This
    // should not complete for some time, since it should wait for all
    // outstanding method calls to complete.
    @Test
    public void testDeactivateWaitsForMethodCallCompletion(ORB orb, POA rootPoa) throws Exception {
        Test_impl1 impl = new Test_impl1(rootPoa);
        test.poa.Test t = impl._this(orb);
        byte[] oid = rootPoa.servant_to_id(impl);
        Thread thr = new LongCaller(t);
        thr.start();
        impl.blockUntilCalled();
        // Test: deactivate_object while method call is active
        rootPoa.deactivate_object(oid);
        // Once we've deactivated the object the re-activation shouldn't complete until the method call completes
        rootPoa.activate_object_with_id(oid, impl);
        // The destroy call shouldn't return until the aMethod call is complete
        assertTrue(impl.callComplete());

        // Wait for the thread to terminate
        while (thr.isAlive()) {
            try {
                thr.join();
            } catch (InterruptedException ignored) {
            }
        }
        rootPoa.deactivate_object(oid);
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

        public POA _default_POA() { return poa; }
        synchronized void blockUntilCalled() {
            while (!called) {
                try { wait(); } catch (InterruptedException ignored) {}
            }
        }
        synchronized boolean callComplete() { return finished; }
    }

    final static class LongCaller extends Thread {
        private final test.poa.Test test;
        LongCaller(test.poa.Test test) { this.test = test; }
        public void run() {
            try {
                test.aMethod();
            } catch (SystemException ex) {
                ex.printStackTrace();
            }
        }
    }
}

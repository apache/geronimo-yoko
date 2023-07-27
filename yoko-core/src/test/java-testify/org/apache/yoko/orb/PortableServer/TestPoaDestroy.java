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
import org.omg.CORBA.SystemException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import test.poa.TestPOA;
import testify.iiop.annotation.ConfigureOrb;

import static org.junit.Assert.assertTrue;


//
// This is a more rigorous test of POA::destroy. We want to ensure
// that the POA isn't destroyed during a method call.
//

@ConfigureOrb
public class TestPoaDestroy {
    @Test
    public void testDestroy(ORB orb, POA rootPoa) {
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
        // Test: destroy
        //
        poa.destroy(true, true);

        //
        // Ensure parent no longer knows about child
        //
        try {
            rootPoa.find_POA("poa1", false);
            assertTrue(false); // find_POA should not have succeeded
        } catch (AdapterNonExistent ex) {
            // expected
        }

        //
        // Create child POA
        //
        try {
            poa = rootPoa.create_POA("poa1", rootMgr, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Create child of child POA
        //
        try {
            poa2 = poa.create_POA("child1", rootMgr, policies);
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: destroy - should destroy poa1 and poa1/child1
        //
        poa.destroy(true, true);

        //
        // Ensure parent no longer knows about child
        //
        try {
            rootPoa.find_POA("poa1", false);
            assertTrue(false); // find_POA should not have succeeded
        } catch (AdapterNonExistent ex) {
            // expected
        }

        //
        // XXX Test: etherealize w/ servant manager
        //
        POAManager rootMgr1 = rootPoa.the_POAManager();
        assertTrue(rootMgr1 != null);

        Policy[] policies1 = new Policy[1];
        policies1[0] = rootPoa
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);

        //
        // Create child POA
        //
        POA poa1 = null;
        try {
            poa1 = rootPoa.create_POA("poa1", rootMgr1, policies1);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        Test_impl2 impl = new Test_impl2(poa1);

        test.poa.Test t = impl._this(orb);

        Thread thr = new LongCaller(t);

        thr.start();
        impl.blockUntilCalled();

        //
        // Test: Destroy the POA while a method call is active
        //
        poa1.destroy(true, true);

        //
        // The destroy call shouldn't return until the aMethod call is
        // complete
        //
        assertTrue(impl.callComplete());

        while (thr.isAlive()) {
            try {
                thr.join();
            } catch (InterruptedException ex) {
            }
        }
    }

    final static class Test_impl2 extends TestPOA {
        private POA poa_;

        private boolean called_ = false;

        private boolean finished_ = false;

        Test_impl2(POA poa) {
            poa_ = poa;
        }

        synchronized public void aMethod() {
            called_ = true;
            notify();

            try {
                wait(1000);
            } catch (InterruptedException ex) {
            }
            finished_ = true;
        }

        public POA _default_POA() {
            return poa_;
        }

        synchronized void blockUntilCalled() {
            while (!called_) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                }
            }
        }

        synchronized boolean callComplete() {
            return finished_;
        }
    }

    final static class LongCaller extends Thread {
        private test.poa.Test t_;

        LongCaller(test.poa.Test t) {
            t_ = t;
        }

        public void run() {
            try {
                t_.aMethod();
            } catch (SystemException se) {
                System.err.println(se.getMessage());
                se.printStackTrace();
            }
        }
    }
}

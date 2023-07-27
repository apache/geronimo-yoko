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
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import test.poa.TestPOA;
import test.poa.Test_impl;
import testify.iiop.annotation.ConfigureOrb;

import static org.junit.Assert.assertTrue;
//
// In this test we want to spawn a thread to call a method on the Test
// interface. This method call should take some time. While the thread
// is calling the method we attempt to deactivate the object. This
// should not complete for some time, since it should wait for all
// outstanding method calls to complete.
//
@ConfigureOrb
public class TestPoaDeactivate {
    @Test
    public void testDeactivate(ORB orb, POA rootPoa) {
        Test_impl impl = new Test_impl(orb, "", false);

        test.poa.Test t = impl._this(orb);

        t.aMethod();

        byte[] oid = null;
        try {
            oid = rootPoa.servant_to_id(impl);
        } catch (ServantNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }

        try {
            rootPoa.deactivate_object(oid);
        } catch (ObjectNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }

        try {
            t.aMethod();
            assertTrue(false); // expected OBJECT_NOT_EXIST
        } catch (OBJECT_NOT_EXIST ex) {
            // Expected
        }
        Test_impl2 impl1 = new Test_impl2(rootPoa);

        test.poa.Test t1 = impl1._this(orb);

        byte[] oid1 = null;
        try {
            oid1 = rootPoa.servant_to_id(impl1);
        } catch (ServantNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }

        Thread thr = new LongCaller(t1);

        thr.start();
        impl1.blockUntilCalled();

        //
        // Test: deactivate_object while method call is active
        //
        try {
            rootPoa.deactivate_object(oid1);
        } catch (ObjectNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }

        //
        // Once we've deactivated the object the re-activation shouldn't
        // complete until the method call completes
        //
        try {
            rootPoa.activate_object_with_id(oid1, impl1);
        } catch (ObjectAlreadyActive ex) {
            assertTrue(false);
        } catch (ServantAlreadyActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }

        //
        // The destroy call shouldn't return until the aMethod call is
        // complete
        //
        assertTrue(impl1.callComplete());

        //
        // Wait for the thread to terminate
        //
        while (thr.isAlive()) {
            try {
                thr.join();
            } catch (InterruptedException ex) {
            }
        }

        try {
            rootPoa.deactivate_object(oid1);
        } catch (ObjectNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
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
            } catch (SystemException ex) {
                ex.printStackTrace();
            }
        }
    }
}

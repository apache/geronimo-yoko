/*
 * Copyright 2015 IBM Corporation and others.
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
package test.poa;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

public final class TestDeactivate extends test.common.TestBase {
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
        private Test t_;

        LongCaller(Test t) {
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

    //
    // In this test we want to spawn a thread to call a method on the Test
    // interface. This method call should take some time. While the thread
    // is calling the method we attempt to deactivate the object. This
    // should not complete for some time, since it should wait for all
    // outstanding method calls to complete.
    //
    static void TestDeactivateThreaded(ORB orb, POA root) {
        Test_impl2 impl = new Test_impl2(root);

        Test t = impl._this(orb);

        byte[] oid = null;
        try {
            oid = root.servant_to_id(impl);
        } catch (ServantNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }

        Thread thr = new LongCaller(t);

        thr.start();
        impl.blockUntilCalled();

        //
        // Test: deactivate_object while method call is active
        //
        try {
            root.deactivate_object(oid);
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
            root.activate_object_with_id(oid, impl);
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
        assertTrue(impl.callComplete());

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
            root.deactivate_object(oid);
        } catch (ObjectNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }
    }

    static void TestDeactivateBlocking(ORB orb, POA root) {
        Test_impl impl = new Test_impl(orb, "", false);

        Test t = impl._this(orb);

        t.aMethod();

        byte[] oid = null;
        try {
            oid = root.servant_to_id(impl);
        } catch (ServantNotActive ex) {
            assertTrue(false);
        } catch (WrongPolicy ex) {
            assertTrue(false);
        }

        try {
            root.deactivate_object(oid);
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
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            POA root = TestUtil.GetRootPOA(orb);

            //
            // Activate the RootPOA manager
            //
            POAManager rootMgr = root.the_POAManager();
            assertTrue(rootMgr != null);

            try {
                rootMgr.activate();
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            System.out.print("Testing deactivate... ");
            System.out.flush();

            //
            // Run the tests using the root POA
            //
            TestDeactivateBlocking(orb, root);
            TestDeactivateThreaded(orb, root);

            System.out.println("Done!");
        } catch (SystemException ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (SystemException ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}

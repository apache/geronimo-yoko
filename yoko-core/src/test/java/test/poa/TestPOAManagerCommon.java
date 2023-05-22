/*
 * Copyright 2016 IBM Corporation and others.
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

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

import java.util.concurrent.CountDownLatch;

final class TestPOAManagerCommon extends test.common.TestBase {
    final static class TestHoldingState extends Thread {
        private Test test_;

        private final CountDownLatch startLatch = new CountDownLatch(1);
        public volatile Result result = null;

        public enum Result { SUCCESS, FAILURE, ERROR };

        TestHoldingState(Test test) {
            test_ = test;
        }

        public void run() {
            startLatch.countDown();
            try {
                test_.aMethod();
                result = Result.SUCCESS;
            } catch (TRANSIENT ex) {
                result = Result.FAILURE;
                return;
            } catch (SystemException ex) {
                result = Result.ERROR;
                System.err.println("Unexpected: " + ex);
                ex.printStackTrace();
            }
        }

        public void waitForStart() {
            do {
                try {
                    startLatch.await();
                    return;
                } catch (InterruptedException e) {
                }
            } while (true);
        }



        public void waitForEnd() {
            while (isAlive()) {
                try {
                    join();
                } catch (java.lang.InterruptedException e) {
                }
            }
        }
    }

    TestPOAManagerCommon(POAManagerProxy manager, TestInfo[] info) {
        for (int i = 0; i < info.length; i++) {
            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            info[i].obj.aMethod();

            //
            // Try discard_request with wait completion == true,
            // shouldn't work since the discard_request call is done
            // through the POAManagerProxy.
            // 
            try {
                manager.discard_requests(true);
                assertTrue(false);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER e) {
                // Expected, ensure the state didn't change
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.ACTIVE);
            }

            //
            // Test discard_requests when active.
            //
            try {
                manager.discard_requests(false);
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.DISCARDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            try {
                info[i].obj.aMethod();
                assertTrue(false);
            } catch (TRANSIENT ex) {
                // Expected
            }

            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            info[i].obj.aMethod();

            //
            // Try hold_request with wait completion == true,
            // shouldn't work since the hold_request call is done
            // through the POAManagerProxy.
            // 
            try {
                manager.hold_requests(true);
                assertTrue(false);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER e) {
                // Expected, ensure the state didn't change
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.ACTIVE);
            }

            //
            // Test hold_requests when active.
            //
            try {
                manager.hold_requests(false);
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.HOLDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            TestHoldingState t = new TestHoldingState(info[i].obj);
            t.start();
            t.waitForStart();

            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }
            t.waitForEnd();
            assertTrue(t.result == TestHoldingState.Result.SUCCESS);


            //
            // Test discard_requests when holding.
            //
            try {
                manager.hold_requests(false);
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.HOLDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            t = new TestHoldingState(info[i].obj);
            t.start();
            t.waitForStart();
            try {
                manager.discard_requests(false);
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.DISCARDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            try {
                info[i].obj.aMethod();
                assertTrue(false);
            } catch (TRANSIENT ex) {
                // Expected
            }

            t.waitForEnd();
            //
            // Queued call should have been discarded.
            //
            assertTrue(t.result == TestHoldingState.Result.FAILURE);

            //
            // Test hold_requests when discarding.
            //
            try {
                manager.hold_requests(false);
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.HOLDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            t = new TestHoldingState(info[i].obj);
            t.start();
            t.waitForStart();

            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            t.waitForEnd();
            assertTrue(t.result == TestHoldingState.Result.SUCCESS);

            //
            // Try deactivate with wait completion == true,
            // shouldn't work since the hold_request call is done
            // through the POAManagerProxy.
            // 
            try {
                manager.deactivate(true, true);
                assertTrue(false);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER e) {
                // Expected, ensure the state didn't change
                assertTrue(manager.get_state() == test.poa.POAManagerProxyPackage.State.ACTIVE);
            }
        }
    }
}

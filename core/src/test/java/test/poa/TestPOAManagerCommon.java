/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package test.poa;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

final class TestPOAManagerCommon extends test.common.TestBase {
    final static class TestHoldingState extends Thread {
        private Test test_;

        private int state_;

        final static int NONE = 0;

        final static int CALL_STARTED = 1;

        final static int CALL_FAILURE = 2;

        final static int CALL_SUCCESS = 3;

        private synchronized void setState(int val) {
            state_ = val;
        }

        TestHoldingState(Test test) {
            test_ = test;
            state_ = NONE;
        }

        public void run() {
            setState(CALL_STARTED);
            try {
                test_.aMethod();
            } catch (TRANSIENT ex) {
                setState(CALL_FAILURE);
                return;
            } catch (SystemException ex) {
                System.err.println("Unexpected: " + ex);
                ex.printStackTrace();
            }
            setState(CALL_SUCCESS);
        }

        synchronized int callState() {
            return state_;
        }
    }

    TestPOAManagerCommon(POAManagerProxy manager, TestInfo[] info) {
        for (int i = 0; i < info.length; i++) {
            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            info[i].obj.aMethod();

            //
            // Try discard_request with wait completion == true,
            // shouldn't work since the discard_request call is done
            // through the POAManagerProxy.
            // 
            try {
                manager.discard_requests(true);
                TEST(false);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER e) {
                // Expected, ensure the state didn't change
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.ACTIVE);
            }

            //
            // Test discard_requests when active.
            //
            try {
                manager.discard_requests(false);
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.DISCARDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            try {
                info[i].obj.aMethod();
                TEST(false);
            } catch (TRANSIENT ex) {
                // Expected
            }

            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            info[i].obj.aMethod();

            //
            // Try hold_request with wait completion == true,
            // shouldn't work since the hold_request call is done
            // through the POAManagerProxy.
            // 
            try {
                manager.hold_requests(true);
                TEST(false);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER e) {
                // Expected, ensure the state didn't change
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.ACTIVE);
            }

            //
            // Test hold_requests when active.
            //
            try {
                manager.hold_requests(false);
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.HOLDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            TestHoldingState t = new TestHoldingState(info[i].obj);
            t.start();

            //
            // Wait for the call to start
            //
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // Ignore
            }

            TEST(t.callState() == TestHoldingState.CALL_STARTED);

            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            //
            // Wait for the call to complete
            //
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }

            TEST(t.callState() == TestHoldingState.CALL_SUCCESS);

            //
            // Test discard_requests when holding.
            //
            try {
                manager.hold_requests(false);
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.HOLDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            t = new TestHoldingState(info[i].obj);
            t.start();

            //
            // Wait for the call to start
            //
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // Ignore
            }

            TEST(t.callState() == TestHoldingState.CALL_STARTED);

            try {
                manager.discard_requests(false);
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.DISCARDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            try {
                info[i].obj.aMethod();
                TEST(false);
            } catch (TRANSIENT ex) {
                // Expected
            }

            while (t.isAlive()) {
                try {
                    t.join();
                } catch (java.lang.InterruptedException e) {
                }
            }
            //
            // Queued call should have been discarded.
            //
            TEST(t.callState() == TestHoldingState.CALL_FAILURE);

            //
            // Test hold_requests when discarding.
            //
            try {
                manager.hold_requests(false);
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.HOLDING);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            t = new TestHoldingState(info[i].obj);
            t.start();

            //
            // Wait for the call to start
            //
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                // Ignore
            }

            TEST(t.callState() == TestHoldingState.CALL_STARTED);

            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            }

            //
            // Wait for the call to complete
            //
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
            }

            TEST(t.callState() == TestHoldingState.CALL_SUCCESS);

            //
            // Try deactivate with wait completion == true,
            // shouldn't work since the hold_request call is done
            // through the POAManagerProxy.
            // 
            try {
                manager.deactivate(true, true);
                TEST(false);
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER e) {
                // Expected, ensure the state didn't change
                TEST(manager.get_state() == test.poa.POAManagerProxyPackage.State.ACTIVE);
            }
        }
    }
}

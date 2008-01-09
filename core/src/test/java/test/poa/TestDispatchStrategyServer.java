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

import java.io.*;
import java.util.Properties;

public final class TestDispatchStrategyServer extends test.common.TestBase {
    //
    // Implementation to test same thread dispatch strategy
    //
    final static class TestSameThread_impl extends TestPOA {
        private Thread thread_;

        private boolean failed_;

        private boolean first_;

        TestSameThread_impl() {
            failed_ = false;
            first_ = true;
        }

        public void aMethod() {
            //
            // Test to ensure that all requests handled by the same thread
            //
            if (first_) {
                thread_ = Thread.currentThread();
                first_ = false;
            } else if (!Thread.currentThread().equals(thread_)) {
                failed_ = true;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }

        public boolean failed() {
            return failed_;
        }
    }

    //
    // Implementation to test thread per request dispatch strategy
    //
    final static class TestThreadPerReq_impl extends TestPOA {
        private Thread threads_[];

        private int thread_count_;

        private boolean failed_;

        TestThreadPerReq_impl() {
            failed_ = false;
            thread_count_ = 0;
            threads_ = new Thread[5];
        }

        public void aMethod() {
            int idx;
            synchronized (this) {
                //
                // Test to ensure that each request is being handled
                // by a different thread.
                //
                for (idx = 0; idx < thread_count_; idx++) {
                    if (Thread.currentThread().equals(threads_[idx]))
                        failed_ = true;
                }

                if (idx == thread_count_) {
                    threads_[thread_count_++] = Thread.currentThread();
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }

        public boolean failed() {
            return failed_;
        }
    }

    //
    // Implementation to test thread pool dispatch strategy
    //
    final static class TestThreadPool_impl extends TestPOA {
        private Thread threads_[];

        private int thread_count_;

        private boolean failed_;

        TestThreadPool_impl() {
            failed_ = false;
            thread_count_ = 0;
            threads_ = new Thread[2];
        }

        public void aMethod() {
            synchronized (this) {
                //
                // Test to ensure that all requests are handled only by
                // the two threads in the thread pool.
                //
                if (thread_count_ == 0) {
                    threads_[0] = Thread.currentThread();
                    ++thread_count_;
                } else if (!Thread.currentThread().equals(threads_[0])) {
                    if (thread_count_ == 1) {
                        threads_[1] = Thread.currentThread();
                        ++thread_count_;
                    } else if (!Thread.currentThread().equals(threads_[1])) {
                        failed_ = true;
                    }
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
        }

        public boolean failed() {
            return failed_;
        }
    }

    //
    // Simple custom dispatch strategy (uses same thread)
    //
    final static class CustDispatchStrategy extends org.omg.CORBA.LocalObject
            implements org.apache.yoko.orb.OB.DispatchStrategy {
        public int id() {
            return 4; // Some unique number
        }

        public org.omg.CORBA.Any info() {
            return org.omg.CORBA.ORB.init().create_any();
        }

        public void dispatch(org.apache.yoko.orb.OB.DispatchRequest request) {
            request.invoke();
        }
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        org.omg.CORBA.ORB orb = null;

        try {
            args = org.apache.yoko.orb.CORBA.ORB.ParseArgs(args, props, null);

            //
            // Set the communications concurrency model
            //
            props.put("yoko.orb.conc_model", "threaded");
            props.put("yoko.orb.oa.conc_model", "threaded");

            //
            // Create ORB
            //
            orb = org.omg.CORBA.ORB.init(args, props);

            //
            // Resolve Root POA and POA Manager
            //
            org.omg.CORBA.Object poaObj = orb
                    .resolve_initial_references("RootPOA");
            org.apache.yoko.orb.OBPortableServer.POA rootPOA = org.apache.yoko.orb.OBPortableServer.POAHelper
                    .narrow(poaObj);
            org.omg.PortableServer.POAManager manager = rootPOA
                    .the_POAManager();

            //
            // Resolve Dispatch Strategy Factory
            //
            org.omg.CORBA.Object dsfObj = orb
                    .resolve_initial_references("DispatchStrategyFactory");
            org.apache.yoko.orb.OB.DispatchStrategyFactory dsf = org.apache.yoko.orb.OB.DispatchStrategyFactoryHelper
                    .narrow(dsfObj);

            //
            // Create Dispatch Strategy objects
            //
            org.apache.yoko.orb.OB.DispatchStrategy ds_st = dsf
                    .create_same_thread_strategy();

            org.apache.yoko.orb.OB.DispatchStrategy ds_tpr = dsf
                    .create_thread_per_request_strategy();

            int tpid = dsf.create_thread_pool(2);
            org.apache.yoko.orb.OB.DispatchStrategy ds_tp = dsf
                    .create_thread_pool_strategy(tpid);

            org.apache.yoko.orb.OB.DispatchStrategy ds_cus = new CustDispatchStrategy();

            //
            // Create POAs with threaded Dispatch Strategy Policies
            // - same thread
            // - thread per request
            // - thread pool (same pool used for two POAs)
            // - custom strategy
            //
            org.omg.CORBA.Policy[] policies = new org.omg.CORBA.Policy[1];

            policies[0] = rootPOA.create_dispatch_strategy_policy(ds_st);
            org.omg.PortableServer.POA stPOA = rootPOA.create_POA("stPOA",
                    manager, policies);

            policies[0] = rootPOA.create_dispatch_strategy_policy(ds_tpr);
            org.omg.PortableServer.POA tprPOA = rootPOA.create_POA("tprPOA",
                    manager, policies);

            policies[0] = rootPOA.create_dispatch_strategy_policy(ds_tp);
            org.omg.PortableServer.POA tpPOA1 = rootPOA.create_POA("tpPOA1",
                    manager, policies);
            org.omg.PortableServer.POA tpPOA2 = rootPOA.create_POA("tpPOA2",
                    manager, policies);

            policies[0] = rootPOA.create_dispatch_strategy_policy(ds_cus);
            org.omg.PortableServer.POA cusPOA = rootPOA.create_POA("cusPOA",
                    manager, policies);

            //
            // Create test implementation object in each POA
            //
            TestSameThread_impl stTest = new TestSameThread_impl();
            byte[] stObjId = stPOA.activate_object(stTest);
            org.omg.CORBA.Object stObjRef = stPOA.id_to_reference(stObjId);

            TestThreadPerReq_impl tprTest = new TestThreadPerReq_impl();
            byte[] tprObjId = tprPOA.activate_object(tprTest);
            org.omg.CORBA.Object tprObjRef = tprPOA.id_to_reference(tprObjId);

            TestThreadPool_impl tpTest = new TestThreadPool_impl();
            byte[] tpObjId1 = tpPOA1.activate_object(tpTest);
            org.omg.CORBA.Object tpObjRef1 = tpPOA1.id_to_reference(tpObjId1);

            byte[] tpObjId2 = tpPOA2.activate_object(tpTest);
            org.omg.CORBA.Object tpObjRef2 = tpPOA2.id_to_reference(tpObjId2);

            TestSameThread_impl cusTest = new TestSameThread_impl();
            byte[] cusObjId = cusPOA.activate_object(cusTest);
            org.omg.CORBA.Object cusObjRef = cusPOA.id_to_reference(cusObjId);

            //
            // Create Test Server
            //
            TestInfo[] info = new TestInfo[5];
            info[0] = new TestInfo();
            info[1] = new TestInfo();
            info[2] = new TestInfo();
            info[3] = new TestInfo();
            info[4] = new TestInfo();
            info[0].obj = TestHelper.narrow(stObjRef);
            info[0].except_id = "";
            info[1].obj = TestHelper.narrow(tprObjRef);
            info[1].except_id = "";
            info[2].obj = TestHelper.narrow(tpObjRef1);
            info[2].except_id = "";
            info[3].obj = TestHelper.narrow(tpObjRef2);
            info[3].except_id = "";
            info[4].obj = TestHelper.narrow(cusObjRef);
            info[4].except_id = "";
            TestServer_impl serverImpl = new TestServer_impl(orb, info);
            TestServer server = serverImpl._this(orb);

            //
            // Save reference
            //
            String refFile = "Test.ref";
            try {
                FileOutputStream file = new FileOutputStream(refFile);
                PrintWriter out = new PrintWriter(file);
                out.println(orb.object_to_string(server));
                out.flush();
                file.close();
            } catch (IOException ex) {
                System.err.println("Can't write to `" + ex.getMessage() + "'");
                System.exit(1);
            }

            //
            // Run implementation
            //
            manager.activate();
            orb.run();

            //
            // Check for failure
            //
            if (stTest.failed() || cusTest.failed() || tpTest.failed()
                    || tprTest.failed())
                System.exit(1);

            File file = new File(refFile);
            file.delete();
        } catch (org.omg.CORBA.SystemException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (org.omg.CORBA.UserException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (org.omg.CORBA.SystemException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(0);
    }
}

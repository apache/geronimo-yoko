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

final public class TestMultipleOrbsThreadedServer extends test.common.TestBase {
    //
    // Define the number of ORB instances to run in separate threads.
    //
    static int NUM_ORBS = 5;

    //
    // Implementation to test multiple orbs
    //

    final static class TestOrb_impl extends TestPOA {
        private int count_;

        private org.omg.CORBA.ORB orb_;

        TestOrb_impl(org.omg.CORBA.ORB orb) {
            count_ = 0;
            orb_ = orb;
        }

        public void aMethod() {
            if (++count_ == 3) {
                //
                // Shutdown th orb after three method calls
                //
                orb_.shutdown(false);
            }
        }
    }

    static final class OrbTestThread extends Thread {
        private org.omg.CORBA.ORB orb_;

        private org.omg.PortableServer.POAManager manager_;

        private TestOrb_impl test_;

        private Test obj_;

        OrbTestThread(String orb_id) {
            java.util.Properties props = new Properties();
            props.putAll(System.getProperties());
            props
                    .put("org.omg.CORBA.ORBClass",
                            "org.apache.yoko.orb.CORBA.ORB");
            props.put("org.omg.CORBA.ORBSingletonClass",
                    "org.apache.yoko.orb.CORBA.ORBSingleton");
            props.put("yoko.orb.id", orb_id); // Use ORB ID passed

            try {
                String[] args = new String[0];
                orb_ = org.omg.CORBA.ORB.init(args, props);

                org.omg.CORBA.Object obj = orb_
                        .resolve_initial_references("RootPOA");
                org.omg.PortableServer.POA rootPOA = org.omg.PortableServer.POAHelper
                        .narrow(obj);
                manager_ = rootPOA.the_POAManager();

                test_ = new TestOrb_impl(orb_);
                byte[] objId = rootPOA.activate_object(test_);
                obj = rootPOA.id_to_reference(objId);
                obj_ = TestHelper.narrow(obj);
            } catch (org.omg.CORBA.UserException ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace();
                System.exit(1);
            }
        }

        public Test getObjectReference() {
            return obj_;
        }

        public void run() {
            try {
                manager_.activate();
                orb_.run();
            } catch (org.omg.CORBA.SystemException ex) {
                ex.printStackTrace();
                System.exit(1);
            } catch (org.omg.CORBA.UserException ex) {
                ex.printStackTrace();
                System.exit(1);
            }

            if (orb_ != null) {
                try {
                    ((org.apache.yoko.orb.CORBA.ORB) orb_).destroy();
                } catch (org.omg.CORBA.SystemException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }

        }
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");
        props.put("yoko.orb.id", ""); // Use default ORB

        org.omg.CORBA.ORB orb = null;

        try {
            args = org.apache.yoko.orb.CORBA.ORB.ParseArgs(args, props, null);
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
            org.omg.PortableServer.POA rootPOA = org.omg.PortableServer.POAHelper
                    .narrow(poaObj);
            org.omg.PortableServer.POAManager manager = rootPOA
                    .the_POAManager();

            //
            // Create threads to run other orb instances
            //
            OrbTestThread orb_thread[] = new OrbTestThread[NUM_ORBS];
            for (int i = 0; i < NUM_ORBS; i++) {
                orb_thread[i] = new OrbTestThread("orb" + (i + 1));
            }

            for (int i = 0; i < NUM_ORBS; i++) {
                orb_thread[i].start();
            }

            //
            // Create Test InsServer
            //
            TestInfo[] info = new TestInfo[NUM_ORBS];
            for (int i = 0; i < NUM_ORBS; i++) {
                info[i] = new TestInfo();
                info[i].obj = orb_thread[i].getObjectReference();
                info[i].except_id = "";
            }

            TestServer_impl serverImpl = new TestServer_impl(orb, info);
            TestServer server = serverImpl._this(orb);

            //
            // Save references
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

            // Run implementation
            //
            manager.activate();
            orb.run();

            //
            // Wait for all other threads to be finished
            //
            for (int i = 0; i < NUM_ORBS; i++) {
                while (orb_thread[i].isAlive()) {
                    try {
                        orb_thread[i].join();
                    } catch (java.lang.InterruptedException ex) {
                    }
                }
            }

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

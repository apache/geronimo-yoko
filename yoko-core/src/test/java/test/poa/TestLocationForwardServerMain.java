/*
 * Copyright 2010 IBM Corporation and others.
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

import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;
import org.omg.PortableServer.POAManagerPackage.*;
import java.io.*;

public final class TestLocationForwardServerMain {
    final static class Server_impl extends TestLocationForwardServerPOA {
        private ORB orb_;

        private TestLocationForwardActivator_impl activator_;

        private org.omg.CORBA.Object servant_;

        Server_impl(ORB orb, TestLocationForwardActivator_impl activator,
                org.omg.CORBA.Object servant) {
            orb_ = orb;
            activator_ = activator;
            servant_ = servant;
        }

        public void setForwardRequest(org.omg.CORBA.Object obj) {
            activator_.setForwardRequest(obj);
        }

        public org.omg.CORBA.Object get_servant() {
            return servant_;
        }

        public void deactivate() {
            orb_.shutdown(false);
        }
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            POA root = TestUtil.GetRootPOA(orb);

            POA poa;
            Policy[] policies;

            POAManager manager = root.the_POAManager();

            //
            // Create POAs
            //
            policies = new Policy[4];
            policies[0] = root
                    .create_id_assignment_policy(org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID);
            policies[1] = root
                    .create_id_uniqueness_policy(org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID);
            policies[2] = root
                    .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN);
            policies[3] = root
                    .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);

            try {
                poa = root.create_POA("poa", manager, policies);
            } catch (AdapterAlreadyExists ex) {
                throw new RuntimeException();
            } catch (InvalidPolicy ex) {
                throw new RuntimeException();
            }

            TestLocationForwardActivator_impl activatorImpl = new TestLocationForwardActivator_impl();
            org.omg.PortableServer.ServantActivator activator = activatorImpl
                    ._this(orb);

            try {
                poa.set_servant_manager(activator);
            } catch (WrongPolicy ex) {
                throw new RuntimeException();
            }

            byte[] oid = "test".getBytes();
            org.omg.CORBA.Object obj = poa.create_reference_with_id(oid,
                    "IDL:Test:1.0");

            TestLocationForward_impl testImpl = new TestLocationForward_impl(
                    orb);

            activatorImpl.setActivatedServant(testImpl);

            Server_impl serverImpl = new Server_impl(orb, activatorImpl, obj);
            TestLocationForwardServer server = serverImpl._this(orb);

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
            try {
                manager.activate();
            } catch (AdapterInactive ex) {
                throw new RuntimeException();
            }
            orb.run();

            File file = new File(refFile);
            file.delete();
        } catch (SystemException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (SystemException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(0);
    }
}

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
package test.retry;

import java.util.Properties;

class FwdLocator_impl extends org.omg.CORBA.LocalObject implements
        org.omg.PortableServer.ServantLocator {
    private org.omg.CORBA.Object obj_;

    public FwdLocator_impl(org.omg.CORBA.Object obj) {
        obj_ = obj;
    }

    public org.omg.PortableServer.Servant preinvoke(byte[] oid,
            org.omg.PortableServer.POA poa, String operation,
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder cookie)
            throws org.omg.PortableServer.ForwardRequest {
        throw new org.omg.PortableServer.ForwardRequest(obj_);
    }

    public void postinvoke(byte[] oid, org.omg.PortableServer.POA poa,
            String operation, java.lang.Object cookie,
            org.omg.PortableServer.Servant servant) {
    }
}

public class Server {
    public static int run(org.omg.CORBA.ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        //
        // Resolve Root POA
        //
        org.omg.PortableServer.POA rootPOA = org.omg.PortableServer.POAHelper
                .narrow(orb.resolve_initial_references("RootPOA"));

        //
        // Get a reference to the POA manager and activate it
        //
        org.omg.PortableServer.POAManager manager = rootPOA.the_POAManager();
        manager.activate();

        org.omg.CORBA.Policy[] policies = new org.omg.CORBA.Policy[3];
        policies[0] = rootPOA
                .create_request_processing_policy(org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER);
        policies[1] = rootPOA
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);
        policies[2] = rootPOA
                .create_implicit_activation_policy(org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION);

        //
        // Create two POAs with servant locators, each with its own
        // POAManager (and therefore, each with unique endpoints).
        // The locators will simply raise ForwardRequest for the
        // given object, thus creating an infinite loop.
        //
        org.omg.PortableServer.POA poa1 = rootPOA.create_POA("poa1", null,
                policies);
        org.omg.PortableServer.POA poa2 = rootPOA.create_POA("poa2", null,
                policies);

        org.omg.CORBA.Object obj1 = poa1.create_reference("IDL:Test:1.0");
        org.omg.CORBA.Object obj2 = poa2.create_reference("IDL:Test:1.0");

        org.omg.PortableServer.ServantLocator locator1 = new FwdLocator_impl(
                obj2);
        org.omg.PortableServer.ServantLocator locator2 = new FwdLocator_impl(
                obj1);

        poa1.set_servant_manager(locator1);
        poa2.set_servant_manager(locator2);

        Test test = TestHelper.narrow(obj1);

        Retry_impl retryImpl = new Retry_impl(rootPOA);
        Retry retry = retryImpl._this(orb);

        RetryServer_impl serverImpl = new RetryServer_impl(rootPOA, test, retry);
        RetryServer server = serverImpl._this(orb);

        //
        // Save reference. This must be done after POA manager
        // activation, otherwise there is a potential for a race
        // condition between the client sending a request and the
        // server not being ready yet.
        //
        String refFile = "Test.ref";
        try {
            String ref = orb.object_to_string(server);
            java.io.FileOutputStream file = new java.io.FileOutputStream(
                    refFile);
            java.io.PrintWriter out = new java.io.PrintWriter(file);
            out.println(ref);
            out.flush();
            file.close();
        } catch (java.io.IOException ex) {
            System.err.println("Can't write to `" + ex.getMessage() + "'");
            return 1;
        }

        //
        // Run implementation
        //
        org.omg.PortableServer.POAManager mgr = poa1.the_POAManager();
        mgr.activate();
        mgr = poa2.the_POAManager();
        mgr.activate();
        orb.run();

        //
        // Delete file
        //
        new java.io.File(refFile).delete();

        return 0;
    }

    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        org.omg.CORBA.ORB orb = null;

        try {
            orb = org.omg.CORBA.ORB.init(args, props);
            status = run(orb, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (Exception ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}

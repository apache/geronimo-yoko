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

package test.ins;

import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

public final class Server {
    //
    // Return the port used by an acceptor
    //
    static int getPort(org.apache.yoko.orb.OCI.IIOP.AcceptorInfo iiopInfo) {
        short portShort = iiopInfo.port();
        int port;

        if (portShort < 0)
            port = 0xffff + (int) portShort + 1;
        else
            port = (int) portShort;

        return port;
    }

    //
    // Helper class for writing IOR references out to files
    //
    public static class TempIORFile {
        private String iorFile;

        //
        // Constructor writes out an object reference to a file
        //
        public TempIORFile(ORB orb, org.omg.CORBA.Object obj, String fileName)
                throws java.io.IOException {
            iorFile = fileName;
            try {
                String ref = orb.object_to_string(obj);
                java.io.FileOutputStream file = new java.io.FileOutputStream(
                        iorFile);
                java.io.PrintWriter out = new java.io.PrintWriter(file);
                out.println(ref);
                out.flush();
                file.close();
            } catch (java.io.IOException ex) {
                System.err.println("Can't write to `" + ex.getMessage() + "'");
                throw ex;
            }
        }

        //
        // Destroy the object reference file
        //
        public void release() {
            if (iorFile != null)
                new java.io.File(iorFile).delete();
            iorFile = null;
        }

        //
        // Make sure it's destroyed
        //
        protected void finalize() throws Throwable {
            release();

            super.finalize();
        }
    }

    //
    // Simple server providing objects for corba URL tests
    //
    public static int run(ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        if (args.length != 2) {
            System.out.println("usage: test.ins.Server key_string "
                    + "ior_file");
            return 1;
        }

        //
        // corbaloc key
        //
        String keyStr = args[0];
        String iorFile = args[1];

        //
        // Resolve Root POA
        //
        POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

        //
        // Activate the POA manager
        //
        POAManager manager = poa.the_POAManager();
        manager.activate();

        //
        // Create POA
        //
        Policy[] policies = new Policy[2];
        policies[0] = poa
                .create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID);
        policies[1] = poa
                .create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
        POA testPOA = poa.create_POA("testPOA", manager, policies);

        //
        // Resolve the Boot Manager
        //
        org.apache.yoko.orb.OB.BootManager bootManager = org.apache.yoko.orb.OB.BootManagerHelper
                .narrow(orb.resolve_initial_references("BootManager"));

        //
        // Find the POA Manager's Acceptor Port
        //
        org.apache.yoko.orb.OBPortableServer.POAManager obManager = org.apache.yoko.orb.OBPortableServer.POAManagerHelper
                .narrow(manager);
        org.apache.yoko.orb.OCI.Acceptor[] acceptors = obManager
                .get_acceptors();

        org.apache.yoko.orb.OCI.IIOP.AcceptorInfo iiopInfo = null;

        for (int i = 0; i < acceptors.length; i++) {
            org.apache.yoko.orb.OCI.AcceptorInfo info = acceptors[i].get_info();
            iiopInfo = org.apache.yoko.orb.OCI.IIOP.AcceptorInfoHelper
                    .narrow(info);

            if (iiopInfo != null)
                break;
        }

        String[] hosts = iiopInfo.hosts();
        int port = getPort(iiopInfo);

        //
        // corbaloc test object
        //
        test.ins.URLTest.IIOPAddress corbaURLObj;

        test.ins.URLTest.IIOPAddress_impl urlServant = new test.ins.URLTest.IIOPAddress_impl(
                orb, hosts[0], port, keyStr, "corbaloc");
        byte[] oid = urlServant.getKey().getBytes();

        testPOA.activate_object_with_id(oid, urlServant);
        corbaURLObj = urlServant._this(orb);
        bootManager.add_binding(oid, corbaURLObj);

        //
        // Save references. This must be done after POA manager
        // activation, otherwise there is a potential for a race
        // condition between the client sending request and the server
        // not being ready yet.
        //
        TempIORFile iorRef;
        try {
            iorRef = new TempIORFile(orb, corbaURLObj, iorFile);
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            return 0;
        }

        //
        // Give up control to the ORB
        //
        orb.run();

        //
        // Remove the ref file
        //
        iorRef.release();

        return 0;
    }

    //
    // Start the INS test server
    //
    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            orb = ORB.init(args, props);
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

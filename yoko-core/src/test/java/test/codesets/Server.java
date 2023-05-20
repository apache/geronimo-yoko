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
package test.codesets;

import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import java.io.*;

final public class Server {
    public static int run(ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
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
        // Create implementation object
        //
        TestCodeSets_impl i = new TestCodeSets_impl(orb);
        TestCodeSets testCodeSets = i._this(orb);

        //
        // Save references. This must be done after POA manager activation,
        // otherwise there is a potential for a race condition between the
        // client sending request and the server not being ready yet.
        //
        String s = orb.object_to_string(testCodeSets);

        String refFile = "TestCodeSets.ref";
        try {
            String ref = orb.object_to_string(testCodeSets);
            FileOutputStream file = new FileOutputStream(refFile);
            PrintWriter out = new PrintWriter(file);
            out.println(ref);
            out.flush();
            file.close();
        } catch (IOException ex) {
            System.err.println("Can't write to `" + ex.getMessage() + "'");
            return 1;
        }

        //
        // Give up control to the ORB
        //
        orb.run();

        File file = new File(refFile);
        file.delete();

        return 0;
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

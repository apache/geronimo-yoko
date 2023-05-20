/*
 * Copyright 2019 IBM Corporation and others.
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
package test.iiopplugin;

import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class Server {
    private static int run(ORB orb) throws UserException {
        // Resolve Root POA
        POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

        // Get a reference to the POA manager and activate it
        POAManager manager = poa.the_POAManager();
        manager.activate();

        // Create implementation object
        Test_impl testImpl = new Test_impl(poa);
        Test test = testImpl._this(orb);

        // Save reference. This must be done after POA manager
        // activation, otherwise there is a potential for a race
        // condition between the client sending a request and the
        // server not being ready yet.
        String refFile = "Test.ref";
        try {
            String ref = orb.object_to_string(test);
            FileOutputStream file = new FileOutputStream(
                    refFile);
            PrintWriter out = new PrintWriter(file);
            out.println(ref);
            out.flush();
            file.close();
        } catch (IOException ex) {
            System.err.println("Can't write to `" + ex.getMessage() + "'");
            return 1;
        }

        // Run implementation
        orb.run();

        //
        // Delete file
        //
        new File(refFile).delete();

        return 0;
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");
        props.put("org.omg.PortableInterceptor.ORBInitializerClass.test.iiopplugin.TestORBInitializer", "");

        String[] arguments = new String[] { "-IIOPconnectionHelper", "test.iiopplugin.ServerPlugin", "-IIOPconnectionHelperArgs", ServerPlugin.SERVER_ARGS };

        int status;
        ORB orb = null;

        try {
            orb = ORB.init(arguments, props);
            status = run(orb);
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

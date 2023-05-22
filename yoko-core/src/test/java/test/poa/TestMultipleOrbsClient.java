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

import java.util.*;
import java.io.*;

final public class TestMultipleOrbsClient extends test.common.TestBase {
    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        org.omg.CORBA.ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = org.omg.CORBA.ORB.init(args, props);

            //
            // Get test servers object references
            //
            BufferedReader in = new BufferedReader(new FileReader("Test.ref"));
            String ref1 = in.readLine();
            org.omg.CORBA.Object obj = orb.string_to_object(ref1);
            if (obj == null) {
                System.err.println("TestMultipleOrbsClient: "
                        + " cannot read IOR from Test.ref");
                System.exit(1);
            }
            TestServer server1 = TestServerHelper.narrow(obj);

            String ref2 = in.readLine();
            obj = orb.string_to_object(ref2);
            if (obj == null) {
                System.err.println("TestMultipleOrbsClient: "
                        + " cannot read IOR from Test.ref");
                System.exit(1);
            }
            TestServer server2 = TestServerHelper.narrow(obj);

            in.close();

            //
            // Get object references from servers
            //
            TestInfo[] info1 = server1.get_info();
            TestInfo[] info2 = server2.get_info();

            //
            // Call a few methods on each object
            //
            info1[0].obj.aMethod();
            info2[0].obj.aMethod();
            info1[0].obj.aMethod();
            info2[0].obj.aMethod();

            //
            // Deactivate servers (and thus orbs)
            //
            server1.deactivate();
            server2.deactivate();
        } catch (java.io.FileNotFoundException ex) {
            System.err.println("TestMultipleOrbsClient: cannot open Test.ref");
            System.exit(1);
        } catch (java.io.IOException ex) {
            System.err.println("TestMultipleOrbsClient: File input error");
            System.exit(1);
        } catch (org.omg.CORBA.SystemException ex) {
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

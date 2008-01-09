/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
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


/**
 * @version $Rev: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */

package test.tnaming;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.apache.yoko.orb.CosNaming.tnaming.TransientNameService;
import org.apache.yoko.orb.CosNaming.tnaming.TransientServiceException;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CosNaming.NamingContextExtPackage.*;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.Properties;

public class Server extends test.common.TestBase {
    public static final int NS_PORT = 40001;
    public static final String NS_LOC = "corbaloc::localhost:40001/TNameService";

    public static int run(ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        //
        // Resolve Root POA
        //
        POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

        //
        // Get a reference to the POA manager and activate it
        //
        POAManager manager = poa.the_POAManager();
        manager.activate();

        System.out.println("Attempting to resolve NameService reference");
        org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");
        System.out.println("Resolved NameService reference=" + obj);
        NamingContextExt initialContext = NamingContextExtHelper.narrow(obj);

        NameComponent level1 = new NameComponent("level1", "test");
        NameComponent level2 = new NameComponent("level2", "");

        NameComponent ncTest1 = new NameComponent("Test1", "");
        NameComponent ncTest2 = new NameComponent("Test2", "");
        NameComponent ncTest3 = new NameComponent("Test3", "");


        NamingContext nc1 = initialContext.new_context();

        System.out.println("Binding context level1");
        initialContext.bind_context(new NameComponent[] { level1 }, nc1);

        NamingContext nc2 = initialContext.bind_new_context(new NameComponent[] { level1, level2} );

        try {
            org.omg.CORBA.Object o = initialContext.resolve(new NameComponent[] { ncTest1 });
            TEST(false);
        } catch (NotFound e) {
            // expected exception
        }

        //
        // Create implementation objects
        //
        org.omg.CORBA.Object test1 = new Test_impl(poa, "Test1")._this_object(orb);
        org.omg.CORBA.Object test2 = new Test_impl(poa, "Test2")._this_object(orb);
        org.omg.CORBA.Object test3 = new Test_impl(poa, "Test3")._this_object(orb);
        org.omg.CORBA.Object test3a = new Test_impl(poa, "Test3a")._this_object(orb);

        try {
            org.omg.CORBA.Object o = initialContext.resolve(new NameComponent[] { ncTest1 });
            TEST(false);
        } catch (NotFound e) {
            // expected exception
        }

        initialContext.bind(new NameComponent[] { ncTest1 }, test1);

        try {
            org.omg.CORBA.Object o = initialContext.resolve(new NameComponent[] { ncTest1 });
            Test test = TestHelper.narrow(o);
            TEST(test.get_id().equals("Test1"));
        } catch (NotFound e) {
            TEST(false);
        }

        nc1.bind(new NameComponent[] { ncTest2 }, test2);

        try {
            org.omg.CORBA.Object o = initialContext.resolve(new NameComponent[] { level1, ncTest2 });
            Test test = TestHelper.narrow(o);
            TEST(test.get_id().equals("Test2"));
        } catch (NotFound e) {
            TEST(false);
        }

        initialContext.bind(new NameComponent[] { level1, level2, ncTest3 }, test3);

        try {
            org.omg.CORBA.Object o = initialContext.resolve(new NameComponent[] { level1, level2, ncTest3 });
            Test test = TestHelper.narrow(o);
            TEST(test.get_id().equals("Test3"));
        } catch (NotFound e) {
            TEST(false);
        }

        nc2.rebind(new NameComponent[] { ncTest3 }, test3a);

        try {
            org.omg.CORBA.Object o = initialContext.resolve(new NameComponent[] { level1, level2, ncTest3 });
            Test test = TestHelper.narrow(o);
            TEST(test.get_id().equals("Test3a"));
        } catch (NotFound e) {
            TEST(false);
        }

        initialContext.unbind(new NameComponent[] { level1, level2, ncTest3 });

        try {
            org.omg.CORBA.Object o = nc2.resolve(new NameComponent[] { ncTest3 });
            TEST(false);
        } catch (NotFound e) {
            // expected exception
        }

        nc2.bind(new NameComponent[] { ncTest3 }, test3);
        nc1.unbind(new NameComponent[] { level2 });

        try {
            org.omg.CORBA.Object o = initialContext.resolve(new NameComponent[] { level1, level2, ncTest3 });
            TEST(false);
        } catch (NotFound e) {
            // expected exception
        }

        nc1.rebind_context(new NameComponent[] { level2 }, nc2);


        //
        // Save reference. This must be done after POA manager
        // activation, otherwise there is a potential for a race
        // condition between the client sending a request and the
        // server not being ready yet.
        //
        String refFile = "Test.ref";
        try {
            java.io.FileOutputStream file = new java.io.FileOutputStream(
                    refFile);
            java.io.PrintWriter out = new java.io.PrintWriter(file);
            writeRef(orb, out, test1, initialContext, new NameComponent[] { ncTest1 });
            writeRef(orb, out, test2, initialContext, new NameComponent[] { level1, ncTest2 });
            writeRef(orb, out, test3, initialContext, new NameComponent[] { level1, level2, ncTest3 });
            out.flush();
            file.close();
        } catch (java.io.IOException ex) {
            System.err.println("Can't write to `" + ex.getMessage() + "'");
            return 1;
        }

        //
        // Run implementation
        //
        orb.run();

        //
        // Delete file
        //
        new java.io.File(refFile).delete();

        return 0;
    }

    private static void writeRef(ORB orb, PrintWriter out, org.omg.CORBA.Object obj, NamingContextExt context, NameComponent[] name) throws InvalidName {
        String ref = orb.object_to_string(obj);
        out.println(ref);
        String nameString = context.to_string(name);
        out.println(nameString);
    }

    public static void main(String args[]) throws TransientServiceException {

        ORB orb = null;
        int status = 0;
        try {
            TransientNameService service = new TransientNameService("localhost", NS_PORT);
            System.out.println("Starting transient name service");
            service.run();
            System.out.println("Transient name service started");

            java.util.Properties props = new Properties();
            props.putAll(System.getProperties());
            props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
            props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");
            props.put("yoko.orb.oa.endpoint", "iiop --host localhost --port 40002");

            args = new String[] { "-ORBInitRef", "NameService=" + NS_LOC };


            orb = ORB.init(args, props);
            status = run(orb, args);
        } catch (Throwable ex) {
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

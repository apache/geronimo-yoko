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

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.util.Properties;

import org.apache.yoko.orb.CosNaming.separated.TransientNameService;
import org.apache.yoko.orb.CosNaming.separated.TransientServiceException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;

public class Server extends test.common.TestBase {
    public static final int NS_PORT = 40001;
    public static final String NS_LOC = "corbaloc::localhost:40001/TNameService";

    private static final NameComponent LEVEL1 = new NameComponent("level1", "test");
    private static final NameComponent LEVEL2 = new NameComponent("level2", "");

    private static final NameComponent TEST1 = new NameComponent("Test1", "");
    private static final NameComponent TEST2 = new NameComponent("Test2", "");
    private static final NameComponent TEST3 = new NameComponent("Test3", "");

    public static int run(ORB orb, String[] args) throws UserException {
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


        NamingContext nc1 = initialContext.new_context();

        System.out.println("Binding context level1");
        initialContext.bind_context(new NameComponent[] { LEVEL1 }, nc1);

        NamingContext nc2 = initialContext.bind_new_context(new NameComponent[] { LEVEL1, LEVEL2} );

        assertNameNotBound(initialContext, TEST1);

        //
        // Create implementation objects
        //
        org.omg.CORBA.Object test1 = new Test_impl(poa, "Test1")._this_object(orb);
        org.omg.CORBA.Object test2 = new Test_impl(poa, "Test2")._this_object(orb);
        org.omg.CORBA.Object test3 = new Test_impl(poa, "Test3")._this_object(orb);
        org.omg.CORBA.Object test3a = new Test_impl(poa, "Test3a")._this_object(orb);

        assertNameNotBound(initialContext, TEST1);

        initialContext.bind(new NameComponent[] { TEST1 }, test1);
        assertTestIsBound("Test1", initialContext, TEST1);

        nc1.bind(new NameComponent[] { TEST2 }, test2);
        assertTestIsBound("Test2", initialContext, LEVEL1, TEST2);

        initialContext.bind(new NameComponent[] { LEVEL1, LEVEL2, TEST3 }, test3);
        assertTestIsBound("Test3", initialContext, LEVEL1, LEVEL2, TEST3);

        nc2.rebind(new NameComponent[] { TEST3 }, test3a);
        assertTestIsBound("Test3a", initialContext, LEVEL1, LEVEL2, TEST3);

        initialContext.unbind(new NameComponent[] { LEVEL1, LEVEL2, TEST3 });
        assertNameNotBound(nc2, TEST3);

        nc2.bind(new NameComponent[] { TEST3 }, test3);
        assertTestIsBound("Test3", initialContext, LEVEL1, LEVEL2, TEST3);
        
        nc1.unbind(new NameComponent[] { LEVEL2 });
        assertNameNotBound(initialContext, LEVEL1, LEVEL2, TEST3);

        nc1.rebind_context(new NameComponent[] { LEVEL2 }, nc2);

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
            writeRef(orb, out, test1, initialContext, new NameComponent[] { TEST1 });
            writeRef(orb, out, test2, initialContext, new NameComponent[] { LEVEL1, TEST2 });
            writeRef(orb, out, test3, initialContext, new NameComponent[] { LEVEL1, LEVEL2, TEST3 });
            out.flush();
            file.close();
            //
            // Run implementation
            //
            orb.run();
        } catch (java.io.IOException ex) {
            System.err.println("Can't write to `" + ex.getMessage() + "'");
            return 1;
        } finally {
            //
            // Delete file
            //
            new java.io.File(refFile).delete();
        }

        return 0;
    }

    private static void assertTestIsBound(String expectedId, NamingContextExt ctx, NameComponent ...path) throws CannotProceed, InvalidName {
        assertNotNull(path);
        assertNotEquals(0, path.length);
        try {
            org.omg.CORBA.Object o = ctx.resolve(path);
            Test test = TestHelper.narrow(o);
            assertTrue(test.get_id().equals(expectedId));
        } catch (NotFound e) {
            fail("Should have found Test object at path: " + ctx.to_string(path) );
        }
    }
    
    private static void assertNameNotBound(NamingContext initialContext, NameComponent...path) throws CannotProceed, InvalidName {
        try {
            initialContext.resolve(path);
            fail("Expected NotFound exception");
        } catch (NotFound e) {
            // expected exception
        }
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
            System.out.println("Starting SEPARATED transient name service");
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

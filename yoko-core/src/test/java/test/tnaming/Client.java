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
 *  Unless required by applicable law or agreed to in writing, softwares
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


/**
 * @version $Rev: 491396 $ $Date: 2006-12-30 22:06:13 -0800 (Sat, 30 Dec 2006) $
 */

package test.tnaming;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.junit.Assert;
import org.omg.CORBA.ORB;
import org.omg.CORBA.UserException;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;

public class Client extends test.common.TestBase {
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

        final String refFile = "Test.ref";
        final String ref1, name1, ref2, name2, ref3, name3;
        try (FileReader fr = new FileReader(refFile); BufferedReader file = new BufferedReader(fr)) {
            String[] refStrings = new String[2];
            readRef(file, refStrings);
            ref1 = refStrings[0];
            name1 = refStrings[1];
            readRef(file, refStrings);
            ref2 = refStrings[0];
            name2 = refStrings[1];
            readRef(file, refStrings);
            ref3 = refStrings[0];
            name3 = refStrings[1];
        } catch (java.io.IOException ex) {
            System.err.println("Can't read from '" + ex.getMessage() + "'");
            return 1;
        } finally {
            try {
                Files.delete(Paths.get(refFile));
            } catch (IOException e) {}
        }
        //
        // Get "test" objects
        //
        org.omg.CORBA.Object obj1 = orb.string_to_object(ref1);
        org.omg.CORBA.Object obj2 = orb.string_to_object(ref2);
        org.omg.CORBA.Object obj3 = orb.string_to_object(ref3);
        Assert.assertNotNull("Should be able to create an object from the IOR on line 1 of Test.ref", obj1);
        Assert.assertNotNull("Should be able to create an object from the IOR on line 3 of Test.ref", obj2);
        Assert.assertNotNull("Should be able to create an object from the IOR on line 5 of Test.ref", obj3);

        Test test1 = TestHelper.narrow(obj1);
        assertNotNull("Should be able to narrow obj1 to a Test object", test1);

        Test test2 = TestHelper.narrow(obj2);
        assertNotNull("Should be able to narrow obj2 to a Test object", test1);

        Test test3 = TestHelper.narrow(obj3);
        assertNotNull("Should be able to narrow obj3 to a Test object", test1);

        try {
            org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");
            NamingContextExt initialContext = NamingContextExtHelper.narrow(obj);

            assertTestIsBound(test1, initialContext, name1);

            assertTestIsBound(test2, initialContext, name2);

            assertTestIsBound(test3, initialContext, name3);

            testBindingListsAndIterators(orb, poa, initialContext);
        } finally {
            // now shutdown the server orb
            test1.shutdown();
        }
        return 0;
    }

    private static void assertTestIsBound(Test expected, NamingContextExt initialContext, String name) throws UserException {
        Test test1a = TestHelper.narrow(initialContext.resolve_str(name));
        assertNotNull(test1a);
        assertEquals(test1a.get_id(),expected.get_id());
    }

    private static NameComponent[] makeName(String name) {
        return new NameComponent[]{new NameComponent(name, "")};
    }
    
    private static void testBindingListsAndIterators(ORB orb, POA poa, NamingContext initialContext) throws UserException {
        NamingContext nc = initialContext.bind_new_context(makeName("iterator"));

        for (String name : "test0 test1 test2 test3 test4 test5 test6 test7 test8 test9".split(" "))
            nc.bind(makeName(name), new Test_impl(poa, name)._this_object(orb));

        BindingListHolder blh = new BindingListHolder();
        BindingIteratorHolder bih = new BindingIteratorHolder();
        BindingHolder bh = new BindingHolder();

        nc.list(10, blh, bih);

        System.out.println("List returned count = " + blh.value.length);

        assertEquals(10, blh.value.length);
        assertFalse(bih.value.next_one(bh));
        assertEquals(0, bh.value.binding_name.length);

        nc.list(9, blh, bih);

        assertEquals(9, blh.value.length);
        assertTrue(bih.value.next_one(bh));
        assertArrayEquals(makeName("test9"), bh.value.binding_name);

        nc.list(11, blh, bih);

        assertEquals(10, blh.value.length);
        assertFalse(bih.value.next_one(bh));
        assertEquals(0, bh.value.binding_name.length);
    }

    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");

        args = new String[] { "-ORBInitRef", "NameService=" + Server.NS_LOC };

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

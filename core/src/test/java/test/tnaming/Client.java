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
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CosNaming.NamingContextExtPackage.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Client extends test.common.TestBase {
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

        String refFile = "Test.ref";
        String ref1;
        String name1;
        String ref2;
        String name2;
        String ref3;
        String name3;
        try {
            BufferedReader file = new BufferedReader(new FileReader(refFile));
            ref1 = file.readLine();
            name1 = file.readLine();
            ref2 = file.readLine();
            name2 = file.readLine();
            ref3 = file.readLine();
            name3 = file.readLine();
            file.close();
        } catch (java.io.IOException ex) {
            System.err.println("Can't read from '" + ex.getMessage() + "'");
            return 1;
        }
        //
        // Get "test" objects
        //
        org.omg.CORBA.Object obj1 = orb.string_to_object(ref1);
        org.omg.CORBA.Object obj2 = orb.string_to_object(ref2);
        org.omg.CORBA.Object obj3 = orb.string_to_object(ref3);
        if (obj1 == null || obj2 == null || obj3 == null) {
            System.err.println("cannot read IOR from Test.ref");
            return 1;
        }

        Test test1 = TestHelper.narrow(obj1);
        TEST(test1 != null);

        Test test2 = TestHelper.narrow(obj2);
        TEST(test2 != null);

        Test test3 = TestHelper.narrow(obj3);
        TEST(test3 != null);

        org.omg.CORBA.Object obj = orb.resolve_initial_references("NameService");
        NamingContextExt initialContext = NamingContextExtHelper.narrow(obj);

        Test test1a = TestHelper.narrow(initialContext.resolve_str(name1));
        TEST(test1a != null);
        TEST(test1a.get_id().equals(test1.get_id()));

        Test test2a = TestHelper.narrow(initialContext.resolve_str(name2));
        TEST(test2a != null);
        TEST(test2a.get_id().equals(test2.get_id()));

        Test test3a = TestHelper.narrow(initialContext.resolve_str(name3));
        TEST(test3a != null);
        TEST(test3a.get_id().equals(test3.get_id()));

        NamingContext nc = initialContext.bind_new_context(new NameComponent[] { new NameComponent("iterator", "") } );

        for (int i = 0; i < 10; i++) {
            String name = "Test" + i;
            org.omg.CORBA.Object test = new Test_impl(poa, name)._this_object(orb);
            nc.bind(new NameComponent[] { new NameComponent(name, "") }, test);
        }

        BindingListHolder blh = new BindingListHolder();
        BindingIteratorHolder bih = new BindingIteratorHolder();
        BindingHolder bh = new BindingHolder();

        nc.list(10, blh, bih);

        System.out.println("List returned count = " + blh.value.length);

        TEST(blh.value.length == 10);
        TEST(!bih.value.next_one(bh));
        TEST(bh.value.binding_name.length == 0);

        nc.list(9, blh, bih);

        TEST(blh.value.length == 9);
        TEST(bih.value.next_one(bh));
        TEST(bh.value.binding_name.length == 1);

        nc.list(11, blh, bih);

        TEST(blh.value.length == 10);
        TEST(!bih.value.next_one(bh));
        TEST(bh.value.binding_name.length == 0);

        // now shutdown the server orb
        test1.shutdown();

        return 0;
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

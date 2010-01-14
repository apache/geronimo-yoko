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

package test.iiopplugin;

import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

public class Client extends test.common.TestBase {
    public static int run(ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        //
        // Get "test" object
        //
        org.omg.CORBA.Object obj = orb.string_to_object("relfile:/Test.ref");
        if (obj == null) {
            System.err.println("cannot read IOR from Test.ref");
            return 1;
        }

        Test test = TestHelper.narrow(obj);
        TEST(test != null);

        Test localTest = new LocalTest_impl();

        org.omg.IOP.CodecFactory factory = org.omg.IOP.CodecFactoryHelper
                .narrow(orb.resolve_initial_references("CodecFactory"));
        TEST(factory != null);

        org.omg.IOP.Encoding how = new org.omg.IOP.Encoding();
        how.major_version = 0;
        how.minor_version = 0;
        how.format = org.omg.IOP.ENCODING_CDR_ENCAPS.value;

        org.omg.IOP.Codec codec = factory.create_codec(how);
        TEST(codec != null);

        System.out.print("Testing Codec... ");
        System.out.flush();
        try {
            org.omg.CORBA.Any a = orb.create_any();
            TestHelper.insert(a, test);
            byte[] data = codec.encode_value(a);
        } catch (org.omg.CORBA.SystemException ex) {
            TEST(false);
        }
        System.out.println("Done!");

        System.out.print("Testing simple RPC call... ");
        System.out.flush();
        test.say("Hi");
        System.out.println("Done!");

        System.out.print("Testing passing non-local object... ");
        System.out.flush();
        try {
            test.intest(test);
        } catch (org.omg.CORBA.SystemException ex) {
            TEST(false);
        }
        System.out.println("Done!");

        System.out.print("Testing passing local object... ");
        System.out.flush();
        try {
            test.intest(localTest);
            TEST(false);
        } catch (org.omg.CORBA.MARSHAL ex) {
            // Expected
        }
        System.out.println("Done!");

        System.out.print("Testing passing non-local object in any... ");
        System.out.flush();
        try {
            org.omg.CORBA.Any a = orb.create_any();
            TestHelper.insert(a, test);
            test.inany(a);
        } catch (org.omg.CORBA.SystemException ex) {
            TEST(false);
        }

//        if (!ServerPlugin.testPassed()) {
//            TEST(false);
//        }

        if (!ClientPlugin.testPassed()) {
            TEST(false);
        }

        System.out.println("Done!");
        test.shutdown();



        return 0;
    }

    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        String[] arguments = new String[] { "-IIOPconnectionHelper", "test.iiopplugin.ClientPlugin", "-IIOPconnectionHelperArgs", ClientPlugin.CLIENT_ARGS };
        int status = 0;
        ORB orb = null;

        try {
            orb = ORB.init(arguments, props);
            status = run(orb, arguments);
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

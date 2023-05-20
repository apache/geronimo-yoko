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

import org.omg.CORBA.Any;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryHelper;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import test.common.TestBase;

import java.util.Properties;

import static org.junit.Assert.*;

public class Client extends TestBase {
    private static int run(ORB orb) throws UserException {
        //
        // Get "test" object
        //
        org.omg.CORBA.Object obj = orb.string_to_object("relfile:/Test.ref");
        if (obj == null) {
            System.err.println("cannot read IOR from Test.ref");
            return 1;
        }

        Test test = TestHelper.narrow(obj);
        assertNotNull(test);

        Test localTest = new LocalTest_impl();

        CodecFactory factory = CodecFactoryHelper.narrow(orb.resolve_initial_references("CodecFactory"));
        assertNotNull(factory);

        Encoding how = new Encoding();
        how.major_version = 0;
        how.minor_version = 0;
        how.format = ENCODING_CDR_ENCAPS.value;

        Codec codec = factory.create_codec(how);
        assertNotNull(codec);

        System.out.print("Testing Codec... ");
        System.out.flush();
        try {
            Any a = orb.create_any();
            TestHelper.insert(a, test);
            codec.encode_value(a);
        } catch (SystemException ex) {
            fail();
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
        } catch (SystemException ex) {
            fail();
        }
        System.out.println("Done!");

        System.out.print("Testing passing local object... ");
        System.out.flush();
        try {
            test.intest(localTest);
            fail();
        } catch (MARSHAL ex) {
            // Expected
        }
        System.out.println("Done!");

        System.out.print("Testing passing non-local object in any... ");
        System.out.flush();
        try {
            Any a = orb.create_any();
            TestHelper.insert(a, test);
            test.inany(a);
        } catch (SystemException ex) {
            fail();
        }

//        if (!ServerPlugin.testPassed()) {
//            TEST(false);
//        }

        if (!ClientPlugin.testPassed()) {
            fail();
        }

        System.out.println("Done!");
        test.shutdown();



        return 0;
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        String[] arguments = new String[] { "-IIOPconnectionHelper", "test.iiopplugin.ClientPlugin", "-IIOPconnectionHelperArgs", ClientPlugin.CLIENT_ARGS };
        int status = 0;
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

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

package test.types;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;

public class TestUnion extends test.common.TestBase {
    TestUnion(ORB orb) {
        TestUnion1 u = new TestUnion1();
        u.l(123);
        assertTrue(u.l() == 123);
        assertTrue(u.discriminator() == -1);

        u.tc(-6, TestUnion1Helper.type());
        assertTrue(u.discriminator() == -6);
        assertTrue(u.tc().equal(TestUnion1Helper.type()));
        u.tc(4, TestUnion1Helper.type());
        assertTrue(u.discriminator() == 4);
        assertTrue(u.tc().equal(TestUnion1Helper.type()));
        u.tc(999, TestUnion1Helper.type());
        assertTrue(u.discriminator() == 999);
        assertTrue(u.tc().equal(TestUnion1Helper.type()));

        double arr[][][] = new double[10][20][30];
        arr[2][2][2] = 3.14;
        arr[0][0][0] = 1234;
        arr[9][19][29] = 1.23E23;

        u.a(arr);
        double arr2[][][] = u.a();
        assertTrue(arr2[2][2][2] == 3.14);
        assertTrue(arr2[0][0][0] == 1234);
        assertTrue(arr2[9][19][29] == 1.23E23);
        assertTrue(u.discriminator() == -2);

        u.s("Hello!");
        assertTrue(u.s().equals("Hello!"));
        assertTrue(u.discriminator() == -3);

        TestStruct2 ts = new TestStruct2();
        ts.s = new TestStruct1();
        ts.s.s = -32768;
        ts.s.l = 2147483647;
        ts.s.d = 1E200;
        ts.s.b = true;
        ts.s.c = 'x';
        ts.s.o = (byte) 0xff;
        ts.s.str = "abc";
        ts.a = orb.create_any();
        TestUnion1Helper.insert(ts.a, u);
        ts.da = new double[10][20][30];
        ts.da[0][0][0] = 1.23;
        ts.da[2][3][4] = -1.11;
        ts.sa = new String[100];
        for (int i = 0; i < 100; i++)
            ts.sa[i] = "";
        ts.sa[50] = "Hi!";
        u.str(0, ts);
        assertTrue(u.discriminator() == 0);
        u.str(1, ts);
        assertTrue(u.discriminator() == 1);
        u.str(2, ts);
        assertTrue(u.discriminator() == 2);
        u.str(3, ts);
        assertTrue(u.discriminator() == 3);
        u.str(-4, ts);
        assertTrue(u.discriminator() == -4);
        u.str(-5, ts);
        assertTrue(u.discriminator() == -5);
        assertTrue(u.str().s.s == -32768);
        assertTrue(u.str().s.l == 2147483647);
        assertTrue(u.str().s.d == 1E200);
        assertTrue(u.str().s.b == true);
        assertTrue(u.str().s.c == 'x');
        assertTrue(u.str().s.o == (byte) 0xff);
        assertTrue(u.str().s.str.equals("abc"));
        TestUnion1 u2 = TestUnion1Helper.extract(u.str().a);
        assertTrue(u2.s().equals("Hello!"));
        assertTrue(u2.discriminator() == -3);
        assertTrue(u.str().da[0][0][0] == 1.23);
        assertTrue(u.str().da[2][3][4] == -1.11);
        assertTrue(u.str().sa[50].equals("Hi!"));

        TestUnion2 u3 = new TestUnion2();
        u3.un(u);
        assertTrue(u3.discriminator() == TestEnum.C);
        assertTrue(u3.un().discriminator() == -5);
        assertTrue(u3.un().str().s.s == -32768);
        assertTrue(u3.un().str().s.l == 2147483647);
        assertTrue(u3.un().str().s.d == 1E200);
        assertTrue(u3.un().str().s.b == true);
        assertTrue(u3.un().str().s.c == 'x');
        assertTrue(u3.un().str().s.o == (byte) 0xff);
        assertTrue(u3.un().str().s.str.equals("abc"));
        TestUnion1 u4 = TestUnion1Helper.extract(u.str().a);
        assertTrue(u4.s().equals("Hello!"));
        assertTrue(u4.discriminator() == -3);
        assertTrue(u3.un().str().da[0][0][0] == 1.23);
        assertTrue(u3.un().str().da[2][3][4] == -1.11);
        assertTrue(u3.un().str().sa[50].equals("Hi!"));

        Any any = orb.create_any();
        TestUnion2Helper.insert(any, u3);
        TestUnion2 u5 = TestUnion2Helper.extract(any);
        assertTrue(u5.discriminator() == TestEnum.C);
        assertTrue(u5.un().discriminator() == -5);
        assertTrue(u5.un().str().s.s == -32768);
        assertTrue(u5.un().str().s.l == 2147483647);
        assertTrue(u5.un().str().s.d == 1E200);
        assertTrue(u5.un().str().s.b == true);
        assertTrue(u5.un().str().s.c == 'x');
        assertTrue(u5.un().str().s.o == (byte) 0xff);
        assertTrue(u5.un().str().s.str.equals("abc"));
        TestUnion1 u6 = TestUnion1Helper.extract(u.str().a);
        assertTrue(u6.s().equals("Hello!"));
        assertTrue(u6.discriminator() == -3);
        assertTrue(u5.un().str().da[0][0][0] == 1.23);
        assertTrue(u5.un().str().da[2][3][4] == -1.11);
        assertTrue(u5.un().str().sa[50].equals("Hi!"));

        TestUnion3 u7 = new TestUnion3();
        u7.__default();
        u7.c('a', '1');
        assertTrue(u7.discriminator() == 'a');
        assertTrue(u7.c() == '1');
        u7.c('b', '1');
        assertTrue(u7.discriminator() == 'b');
        assertTrue(u7.c() == '1');
    }

    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        org.omg.CORBA.ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = org.omg.CORBA.ORB.init(args, props);

            //
            // Run tests
            //
            System.out.print("Testing some union types... ");
            System.out.flush();
            new TestUnion(orb);
            System.out.println("Done!");
        } catch (org.omg.CORBA.SystemException ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (org.omg.CORBA.SystemException ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}

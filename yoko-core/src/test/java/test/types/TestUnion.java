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

import java.util.Properties;
import org.omg.CORBA.*;

public class TestUnion extends test.common.TestBase {
    TestUnion(ORB orb) {
        TestUnion1 u = new TestUnion1();
        u.l(123);
        TEST(u.l() == 123);
        TEST(u.discriminator() == -1);

        u.tc(-6, TestUnion1Helper.type());
        TEST(u.discriminator() == -6);
        TEST(u.tc().equal(TestUnion1Helper.type()));
        u.tc(4, TestUnion1Helper.type());
        TEST(u.discriminator() == 4);
        TEST(u.tc().equal(TestUnion1Helper.type()));
        u.tc(999, TestUnion1Helper.type());
        TEST(u.discriminator() == 999);
        TEST(u.tc().equal(TestUnion1Helper.type()));

        double arr[][][] = new double[10][20][30];
        arr[2][2][2] = 3.14;
        arr[0][0][0] = 1234;
        arr[9][19][29] = 1.23E23;

        u.a(arr);
        double arr2[][][] = u.a();
        TEST(arr2[2][2][2] == 3.14);
        TEST(arr2[0][0][0] == 1234);
        TEST(arr2[9][19][29] == 1.23E23);
        TEST(u.discriminator() == -2);

        u.s("Hello!");
        TEST(u.s().equals("Hello!"));
        TEST(u.discriminator() == -3);

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
        TEST(u.discriminator() == 0);
        u.str(1, ts);
        TEST(u.discriminator() == 1);
        u.str(2, ts);
        TEST(u.discriminator() == 2);
        u.str(3, ts);
        TEST(u.discriminator() == 3);
        u.str(-4, ts);
        TEST(u.discriminator() == -4);
        u.str(-5, ts);
        TEST(u.discriminator() == -5);
        TEST(u.str().s.s == -32768);
        TEST(u.str().s.l == 2147483647);
        TEST(u.str().s.d == 1E200);
        TEST(u.str().s.b == true);
        TEST(u.str().s.c == 'x');
        TEST(u.str().s.o == (byte) 0xff);
        TEST(u.str().s.str.equals("abc"));
        TestUnion1 u2 = TestUnion1Helper.extract(u.str().a);
        TEST(u2.s().equals("Hello!"));
        TEST(u2.discriminator() == -3);
        TEST(u.str().da[0][0][0] == 1.23);
        TEST(u.str().da[2][3][4] == -1.11);
        TEST(u.str().sa[50].equals("Hi!"));

        TestUnion2 u3 = new TestUnion2();
        u3.un(u);
        TEST(u3.discriminator() == TestEnum.C);
        TEST(u3.un().discriminator() == -5);
        TEST(u3.un().str().s.s == -32768);
        TEST(u3.un().str().s.l == 2147483647);
        TEST(u3.un().str().s.d == 1E200);
        TEST(u3.un().str().s.b == true);
        TEST(u3.un().str().s.c == 'x');
        TEST(u3.un().str().s.o == (byte) 0xff);
        TEST(u3.un().str().s.str.equals("abc"));
        TestUnion1 u4 = TestUnion1Helper.extract(u.str().a);
        TEST(u4.s().equals("Hello!"));
        TEST(u4.discriminator() == -3);
        TEST(u3.un().str().da[0][0][0] == 1.23);
        TEST(u3.un().str().da[2][3][4] == -1.11);
        TEST(u3.un().str().sa[50].equals("Hi!"));

        Any any = orb.create_any();
        TestUnion2Helper.insert(any, u3);
        TestUnion2 u5 = TestUnion2Helper.extract(any);
        TEST(u5.discriminator() == TestEnum.C);
        TEST(u5.un().discriminator() == -5);
        TEST(u5.un().str().s.s == -32768);
        TEST(u5.un().str().s.l == 2147483647);
        TEST(u5.un().str().s.d == 1E200);
        TEST(u5.un().str().s.b == true);
        TEST(u5.un().str().s.c == 'x');
        TEST(u5.un().str().s.o == (byte) 0xff);
        TEST(u5.un().str().s.str.equals("abc"));
        TestUnion1 u6 = TestUnion1Helper.extract(u.str().a);
        TEST(u6.s().equals("Hello!"));
        TEST(u6.discriminator() == -3);
        TEST(u5.un().str().da[0][0][0] == 1.23);
        TEST(u5.un().str().da[2][3][4] == -1.11);
        TEST(u5.un().str().sa[50].equals("Hi!"));

        TestUnion3 u7 = new TestUnion3();
        u7.__default();
        u7.c('a', '1');
        TEST(u7.discriminator() == 'a');
        TEST(u7.c() == '1');
        u7.c('b', '1');
        TEST(u7.discriminator() == 'b');
        TEST(u7.c() == '1');
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

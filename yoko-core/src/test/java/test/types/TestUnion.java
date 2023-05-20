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
package test.types;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import test.common.TestBase;

import java.util.Properties;

import static org.junit.Assert.*;

public class TestUnion extends TestBase {
    TestUnion(ORB orb) {
        TestUnion1 u = new TestUnion1();
        u.l(123);
        assertEquals(123, u.l());
        assertEquals(u.discriminator(), -1);

        u.tc(-6, TestUnion1Helper.type());
        assertEquals(u.discriminator(), -6);
        assertTrue(u.tc().equal(TestUnion1Helper.type()));
        u.tc(4, TestUnion1Helper.type());
        assertEquals(4, u.discriminator());
        assertTrue(u.tc().equal(TestUnion1Helper.type()));
        u.tc(999, TestUnion1Helper.type());
        assertEquals(999, u.discriminator());
        assertTrue(u.tc().equal(TestUnion1Helper.type()));

        double[][][] arr = new double[10][20][30];
        arr[2][2][2] = 3.14;
        arr[0][0][0] = 1234;
        arr[9][19][29] = 1.23E23;

        u.a(arr);
        double[][][] arr2 = u.a();
        assertEquals(3.14, arr2[2][2][2], 0.0);
        assertEquals(1234, arr2[0][0][0], 0.0);
        assertEquals(1.23E23, arr2[9][19][29], 0.0);
        assertEquals(u.discriminator(), -2);

        u.s("Hello!");
        assertEquals("Hello!", u.s());
        assertEquals(u.discriminator(), -3);

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
        assertEquals(0, u.discriminator());
        u.str(1, ts);
        assertEquals(1, u.discriminator());
        u.str(2, ts);
        assertEquals(2, u.discriminator());
        u.str(3, ts);
        assertEquals(3, u.discriminator());
        u.str(-4, ts);
        assertEquals(u.discriminator(), -4);
        u.str(-5, ts);
        assertEquals(u.discriminator(), -5);
        assertEquals(u.str().s.s, -32768);
        assertEquals(2147483647, u.str().s.l);
        assertEquals(1E200, u.str().s.d, 0.0);
        assertTrue(u.str().s.b);
        assertEquals('x', u.str().s.c);
        assertEquals(u.str().s.o, (byte) 0xff);
        assertEquals("abc", u.str().s.str);
        TestUnion1 u2 = TestUnion1Helper.extract(u.str().a);
        assertEquals("Hello!", u2.s());
        assertEquals(u2.discriminator(), -3);
        assertEquals(1.23, u.str().da[0][0][0], 0.0);
        assertEquals(u.str().da[2][3][4], -1.11, 0.0);
        assertEquals("Hi!", u.str().sa[50]);

        TestUnion2 u3 = new TestUnion2();
        u3.un(u);
        assertSame(u3.discriminator(), TestEnum.C);
        assertEquals(u3.un().discriminator(), -5);
        assertEquals(u3.un().str().s.s, -32768);
        assertEquals(2147483647, u3.un().str().s.l);
        assertEquals(1E200, u3.un().str().s.d, 0.0);
        assertTrue(u3.un().str().s.b);
        assertEquals('x', u3.un().str().s.c);
        assertEquals(u3.un().str().s.o, (byte) 0xff);
        assertEquals("abc", u3.un().str().s.str);
        TestUnion1 u4 = TestUnion1Helper.extract(u.str().a);
        assertEquals("Hello!", u4.s());
        assertEquals(u4.discriminator(), -3);
        assertEquals(1.23, u3.un().str().da[0][0][0], 0.0);
        assertEquals(u3.un().str().da[2][3][4], -1.11, 0.0);
        assertEquals("Hi!", u3.un().str().sa[50]);

        Any any = orb.create_any();
        TestUnion2Helper.insert(any, u3);
        TestUnion2 u5 = TestUnion2Helper.extract(any);
        assertSame(u5.discriminator(), TestEnum.C);
        assertEquals(u5.un().discriminator(), -5);
        assertEquals(u5.un().str().s.s, -32768);
        assertEquals(2147483647, u5.un().str().s.l);
        assertEquals(1E200, u5.un().str().s.d, 0.0);
        assertTrue(u5.un().str().s.b);
        assertEquals('x', u5.un().str().s.c);
        assertEquals(u5.un().str().s.o, (byte) 0xff);
        assertEquals("abc", u5.un().str().s.str);
        TestUnion1 u6 = TestUnion1Helper.extract(u.str().a);
        assertEquals("Hello!", u6.s());
        assertEquals(u6.discriminator(), -3);
        assertEquals(1.23, u5.un().str().da[0][0][0], 0.0);
        assertEquals(u5.un().str().da[2][3][4], -1.11, 0.0);
        assertEquals("Hi!", u5.un().str().sa[50]);

        TestUnion3 u7 = new TestUnion3();
        u7.__default();
        u7.c('a', '1');
        assertEquals('a', u7.discriminator());
        assertEquals('1', u7.c());
        u7.c('b', '1');
        assertEquals('b', u7.discriminator());
        assertEquals('1', u7.c());
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            //
            // Run tests
            //
            System.out.print("Testing some union types... ");
            System.out.flush();
            new TestUnion(orb);
            System.out.println("Done!");
        } finally {
            if (orb != null) orb.destroy();
        }
    }
}

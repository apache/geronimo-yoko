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
import org.omg.CORBA.TypeCode;
import test.common.TestBase;

import java.math.BigDecimal;
import java.util.Properties;

import static org.junit.Assert.*;

public class TestAny extends TestBase {
    public TestAny(ORB orb) {
        Any any = orb.create_any();
        Any anyEq = orb.create_any();
        int i;

        {
            short v = -32768;
            short vSave = v;

            any.insert_short(v);
            v = any.extract_short();

            assertEquals(v, vSave);
            anyEq.insert_short(v);
            assertTrue(any.equal(anyEq));
        }

        {
            short v = (short) 65535;
            short vSave = v;

            any.insert_ushort(v);
            v = any.extract_ushort();

            assertEquals(v, vSave);
            anyEq.insert_ushort(v);
            assertTrue(any.equal(anyEq));
        }

        {
            int v = -2147483647 - 1;
            int vSave = v;

            any.insert_long(v);
            v = any.extract_long();

            assertEquals(v, vSave);
            anyEq.insert_long(v);
            assertTrue(any.equal(anyEq));
        }

        {
            int v = 2147483647;
            int vSave = v;

            any.insert_ulong(v);
            v = any.extract_ulong();

            assertEquals(v, vSave);
            anyEq.insert_ulong(v);
            assertTrue(any.equal(anyEq));
        }

        {
            long v = -9223372036854775807L - 1;
            long vSave = v;

            any.insert_longlong(v);
            v = any.extract_longlong();

            assertEquals(v, vSave);
            anyEq.insert_longlong(v);
            assertTrue(any.equal(anyEq));
        }

        {
            long v = 9223372036854775807L;
            long vSave = v;

            any.insert_ulonglong(v);
            v = any.extract_ulonglong();

            assertEquals(v, vSave);
            anyEq.insert_ulonglong(v);
            assertTrue(any.equal(anyEq));
        }

        {
            float v = (float) 1.23456789;
            float vSave = v;

            any.insert_float(v);
            v = any.extract_float();

            assertEquals(v, vSave, 0.0);
            anyEq.insert_float(v);
            assertTrue(any.equal(anyEq));
        }

        {
            double v = 1E200;
            double vSave = v;

            any.insert_double(v);
            v = any.extract_double();

            assertEquals(v, vSave, 0.0);
            anyEq.insert_double(v);
            assertTrue(any.equal(anyEq));
        }

        {
            byte v = (byte) 0xff;
            byte vSave = v;

            any.insert_octet(v);
            v = any.extract_octet();

            assertEquals(v, vSave);
            anyEq.insert_octet(v);
            assertTrue(any.equal(anyEq));
        }

        {
            char v = 'x';
            char vSave = v;

            any.insert_char(v);
            v = any.extract_char();

            assertEquals(v, vSave);
            anyEq.insert_char(v);
            assertTrue(any.equal(anyEq));
        }

        {
            char v = 'x';
            char vSave = v;

            any.insert_wchar(v);
            v = any.extract_wchar();

            assertEquals(v, vSave);
            anyEq.insert_wchar(v);
            assertTrue(any.equal(anyEq));
        }

        {
            boolean v = true;
            boolean vSave = v;

            any.insert_boolean(v);
            v = any.extract_boolean();

            assertEquals(v, vSave);
            anyEq.insert_boolean(v);
            assertTrue(any.equal(anyEq));
        }

        {
            String v = "Hello";
            String vSave = v;

            any.insert_string(v);
            v = any.extract_string();

            assertEquals(v, vSave);
            anyEq.insert_string(v);
            assertTrue(any.equal(anyEq));
        }

        {
            String v = "Hello";
            String vSave = v;

            any.insert_wstring(v);
            v = any.extract_wstring();

            assertEquals(v, vSave);
            anyEq.insert_wstring(v);
            assertTrue(any.equal(anyEq));
        }

        {
            BigDecimal v1 = new BigDecimal("-123456789");
            BigDecimal v1Save = v1;

            any.insert_fixed(v1, orb.create_fixed_tc((short) 24, (short) 0));
            v1 = any.extract_fixed();

            assertEquals(0, v1.compareTo(v1Save));
            anyEq.insert_fixed(v1, orb.create_fixed_tc((short) 24, (short) 0));
            assertTrue(any.equal(anyEq));

            BigDecimal v2 = new BigDecimal("1.23456789");
            BigDecimal v2Save = v2;

            any.insert_fixed(v2, orb.create_fixed_tc((short) 24, (short) 8));
            v2 = any.extract_fixed();

            assertEquals(0, v2.compareTo(v2Save));
            anyEq.insert_fixed(v2, orb.create_fixed_tc((short) 24, (short) 8));
            assertTrue(any.equal(anyEq));
        }

        {
            double v = 1E200;
            double vSave = v;
            Any any2 = orb.create_any();
            Any any3 = orb.create_any();
            Any any4 = orb.create_any();

            Any any5;
            Any any6;
            Any any7;

            any.insert_double(v);
            any2.insert_any(any);
            any3.insert_any(any2);
            any4.insert_any(any3);

            any5 = any4.extract_any();
            any6 = any5.extract_any();
            any7 = any6.extract_any();
            v = any7.extract_double();

            assertEquals(v, vSave, 0.0);
            anyEq.insert_any(any3);
            assertTrue(any4.equal(anyEq));
        }

        {
            String v = "Hello world!";
            String vSave = v;
            Any any2 = orb.create_any();
            Any any3 = orb.create_any();
            Any any4 = orb.create_any();

            Any any5;
            Any any6;
            Any any7;

            any.insert_string(v);
            any2.insert_any(any);
            any3.insert_any(any2);
            any4.insert_any(any3);

            any5 = any4.extract_any();
            any6 = any5.extract_any();
            any7 = any6.extract_any();
            v = any7.extract_string();

            assertEquals(v, vSave);
            anyEq.insert_any(any3);
            assertTrue(any4.equal(anyEq));
        }

        {
            char v = '*';
            char vSave = v;

            Any any2 = orb.create_any();
            Any any3 = orb.create_any();
            Any any4 = orb.create_any();

            Any any5;
            Any any6;
            Any any7;

            any.insert_char(v);
            any2.insert_any(any);
            any3.insert_any(any2);
            any4.insert_any(any3);

            any5 = any4.extract_any();
            any6 = any5.extract_any();
            any7 = any6.extract_any();
            v = any7.extract_char();

            assertEquals(v, vSave);
            anyEq.insert_any(any3);
            assertTrue(any4.equal(anyEq));
        }

        {
            TestStruct1 v = new TestStruct1();
            v.s = -32768;
            v.l = -2147483647 - 1;
            v.d = 1E200;
            v.b = true;
            v.c = 'x';
            v.o = (byte) 0xff;
            v.str = "abc";
            TestStruct1 vSave = v;

            TestStruct1Helper.insert(any, v);
            v = TestStruct1Helper.extract(any);

            assertEquals(v.s, vSave.s);
            assertEquals(v.l, vSave.l);
            assertEquals(v.d, vSave.d, 0.0);
            assertEquals(v.c, vSave.c);
            assertEquals(v.b, vSave.b);
            assertEquals(v.o, vSave.o);
            assertEquals(v.str, vSave.str);
            TestStruct1Helper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));

            TestStruct2 v2 = new TestStruct2();
            v2.da = new double[10][20][30];
            v2.sa = new String[100];
            for (i = 0; i < 100; i++)
                v2.sa[i] = "";
            v2.s = v;
            v2.a = any;
            v2.da[0][0][0] = 1.23;
            v2.da[2][3][4] = -1.11;
            v2.sa[50] = "Hi!";

            Any any2 = orb.create_any();
            TestStruct2Helper.insert(any2, v2);
            v2 = TestStruct2Helper.extract(any2);

            assertEquals(v2.s.s, vSave.s);
            assertEquals(v2.s.l, vSave.l);
            assertEquals(v2.s.d, vSave.d, 0.0);
            assertEquals(v2.s.c, vSave.c);
            assertEquals(v2.s.b, vSave.b);
            assertEquals(v2.s.o, vSave.o);
            assertEquals(v2.s.str, vSave.str);
            assertEquals(1.23, v2.da[0][0][0], 0.0);
            assertEquals(v2.da[2][3][4], -1.11, 0.0);
            assertEquals("Hi!", v2.sa[50]);
            v = TestStruct1Helper.extract(v2.a);
            assertEquals(v.s, vSave.s);
            assertEquals(v.l, vSave.l);
            assertEquals(v.d, vSave.d, 0.0);
            assertEquals(v.c, vSave.c);
            assertEquals(v.b, vSave.b);
            assertEquals(v.o, vSave.o);
            assertEquals(v.str, vSave.str);

            TestStruct2Helper.insert(anyEq, v2);
            assertTrue(any2.equal(anyEq));
        }

        {
            TestEnum v = TestEnum.B;
            TestEnumHelper.insert(any, v);
            v = TestEnumHelper.extract(any);
            assertSame(v, TestEnum.B);

            TestEnumHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            byte[] v;
            byte o;

            v = new byte[100];

            for (o = 0; o < 100; o++)
                v[o] = o;

            OctetSeqHelper.insert(any, v);
            v = OctetSeqHelper.extract(any);

            for (o = 0; o < 100; o++)
                assertEquals(v[o], o);

            OctetSeqHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            double d;

            for (i = 0, d = 0; d < 10; i++, d += 0.1)
                ;
            double[] v = new double[i];

            for (i = 0, d = 0; d < 10; i++, d += 0.1)
                v[i] = d;

            DoubleSeqHelper.insert(any, v);
            double[] v2 = DoubleSeqHelper.extract(any);

            for (i = 0, d = 0; d < 10; i++, d += 0.1)
                assertEquals(v2[i], v[i], 0.0);

            DoubleSeqHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            double[] v = new double[10];

            for (i = 0; i < 10; i++)
                v[i] = (double) i;

            Double10SeqHelper.insert(any, v);
            double[] v2 = Double10SeqHelper.extract(any);

            for (i = 0; i < 10; i++)
                assertEquals(v2[i], v[i], 0.0);

            Double10SeqHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            char[] v;
            char o;

            v = new char[100];

            for (o = 0; o < 100; o++)
                v[o] = o;

            CharSeqHelper.insert(any, v);
            v = CharSeqHelper.extract(any);

            for (o = 0; o < 100; o++)
                assertEquals(v[o], o);

            CharSeqHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            String[] v = new String[40];

            for (i = 0; i < 40; i++)
                v[i] = "abc";

            String40SeqHelper.insert(any, v);
            v = String40SeqHelper.extract(any);

            for (i = 0; i < 40; i++)
                assertEquals("abc", v[i]);

            String40SeqHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            TestStruct1[] v = new TestStruct1[2];

            v[0] = new TestStruct1();
            v[0].s = -32768;
            v[0].l = -2147483647 - 1;
            v[0].d = 1E200;
            v[0].b = true;
            v[0].c = 'x';
            v[0].o = (byte) 0xff;
            v[0].str = "Hi!";

            v[1] = new TestStruct1();
            v[1].s = 32767;
            v[1].l = 2147483647;
            v[1].d = -1E200;
            v[1].b = false;
            v[1].c = 'y';
            v[1].o = (byte) 0x12;
            v[1].str = "Bye!";

            TestStruct1[] vSave = v;

            TestStruct1SeqHelper.insert(any, v);
            v = TestStruct1SeqHelper.extract(any);

            assertEquals(v[0].s, vSave[0].s);
            assertEquals(v[0].l, vSave[0].l);
            assertEquals(v[0].d, vSave[0].d, 0.0);
            assertEquals(v[0].b, vSave[0].b);
            assertEquals(v[0].c, vSave[0].c);
            assertEquals(v[0].o, vSave[0].o);
            assertEquals(v[0].str, vSave[0].str);

            assertEquals(v[1].s, vSave[1].s);
            assertEquals(v[1].l, vSave[1].l);
            assertEquals(v[1].d, vSave[1].d, 0.0);
            assertEquals(v[1].b, vSave[1].b);
            assertEquals(v[1].c, vSave[1].c);
            assertEquals(v[1].o, vSave[1].o);
            assertEquals(v[1].str, vSave[1].str);

            TestStruct1SeqHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            double[][][] v = new double[10][20][30];
            v[0][0][0] = -999;
            v[1][2][3] = 1.23;
            v[9][19][29] = 9.1929;

            DoubleArrayHelper.insert(any, v);
            v = DoubleArrayHelper.extract(any);

            assertEquals(v[0][0][0], -999, 0.0);
            assertEquals(1.23, v[1][2][3], 0.0);
            assertEquals(9.1929, v[9][19][29], 0.0);

            DoubleArrayHelper.insert(anyEq, v);
            assertTrue(any.equal(anyEq));
        }

        {
            TypeCode tc;

            any.insert_TypeCode(TestStruct1Helper.type());
            tc = any.extract_TypeCode();
            assertTrue(tc.equal(TestStruct1Helper.type()));

            anyEq.insert_TypeCode(TestStruct1Helper.type());
            assertTrue(any.equal(anyEq));
        }
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            //
            // Create ORB
            //
            String[] a = null;
            orb = ORB.init(a, props);

            //
            // Run tests
            //
            System.out.print("Testing any type... ");
            System.out.flush();
            new TestAny(orb);
            System.out.println("Done!");
        } finally {
            if (orb != null) orb.destroy();
        }
    }
}

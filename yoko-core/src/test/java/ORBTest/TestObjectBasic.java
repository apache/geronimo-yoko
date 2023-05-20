/*
 * Copyright 2015 IBM Corporation and others.
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
package ORBTest;

import static org.junit.Assert.assertTrue;

import org.omg.CORBA.*;

import ORBTest_Basic.*;

public class TestObjectBasic extends test.common.TestBase implements TestObject {
    private ORB m_orb;

    ORBTest.Intf m_test_intf;

    public TestObjectBasic(ORB orb, ORBTest.Intf test_intf) {
        m_orb = orb;
        m_test_intf = test_intf;
    }

    public boolean is_supported(org.omg.CORBA.Object obj) {
        boolean is_supported = false;

        if (obj != null) {
            try {
                ORBTest_Basic.Intf ti = ORBTest_Basic.IntfHelper.narrow(obj);
                is_supported = true;
            } catch (BAD_PARAM e) {
                is_supported = false;
            }
        }

        return is_supported;
    }

    public void test_SII(org.omg.CORBA.Object obj) {
        ORBTest_Basic.Intf ti = ORBTest_Basic.IntfHelper.narrow(obj);

        ORBType orb_type = m_test_intf.get_ORB_type();

        int i, j, k, l;

        {
            ti.opVoid();
        }

        {
            short ret;
            ti.attrShort((short) -32768);
            ret = ti.attrShort();
            assertTrue(ret == -32768);

            ti.attrShort((short) 32767);
            ret = ti.attrShort();
            assertTrue(ret == 32767);

            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();
            ret = ti.opShort((short) 10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            int ret;
            ti.attrLong(-2147483647 - 1);
            ret = ti.attrLong();
            assertTrue(ret == -2147483647 - 1);

            ti.attrLong(2147483647);
            ret = ti.attrLong();
            assertTrue(ret == 2147483647);

            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();
            ret = ti.opLong(10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            short ret;
            ti.attrUShort((short) 65535);
            ret = ti.attrUShort();
            assertTrue(ret == (short) 65535);

            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();
            ret = ti.opUShort((short) 10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            int ret;
            ti.attrULong(2147483647);
            ret = ti.attrULong();
            assertTrue(ret == 2147483647);

            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();
            ret = ti.opULong(10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            float ret;
            ti.attrFloat(3.40282347E+38F);
            ret = ti.attrFloat();
            assertTrue(ret == 3.40282347E+38F);

            ti.attrFloat(1.17549435E-38F);
            ret = ti.attrFloat();
            assertTrue(ret == 1.17549435E-38F);

            FloatHolder inOut = new FloatHolder(20);
            FloatHolder out = new FloatHolder();
            ret = ti.opFloat(10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            double ret;
            ti.attrDouble(1.7976931348623157E+308);
            ret = ti.attrDouble();
            assertTrue(ret == 1.7976931348623157E+308);

            ti.attrDouble(2.2250738585072014E-308);
            ret = ti.attrDouble();
            assertTrue(ret == 2.2250738585072014E-308);

            DoubleHolder inOut = new DoubleHolder(20);
            DoubleHolder out = new DoubleHolder();
            ret = ti.opDouble(10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            boolean ret;
            ti.attrBoolean(true);
            ret = ti.attrBoolean();
            assertTrue(ret == true);

            ti.attrBoolean(false);
            ret = ti.attrBoolean();
            assertTrue(ret == false);

            BooleanHolder inOut = new BooleanHolder(true);
            BooleanHolder out = new BooleanHolder();
            ret = ti.opBoolean(true, inOut, out);
            assertTrue(ret == true);
            assertTrue(inOut.value == true);
            assertTrue(out.value == true);

            inOut.value = true;
            ret = ti.opBoolean(false, inOut, out);
            assertTrue(ret == false);
            assertTrue(inOut.value == false);
            assertTrue(out.value == false);

            inOut.value = false;
            ret = ti.opBoolean(true, inOut, out);
            assertTrue(ret == false);
            assertTrue(inOut.value == false);
            assertTrue(out.value == false);
        }

        {
            char ret;
            ti.attrChar('a');
            ret = ti.attrChar();
            assertTrue(ret == 'a');
            ti.attrChar((char) 224);
            ret = ti.attrChar();
            assertTrue(ret == (char) 224);

            CharHolder inOut = new CharHolder((char) 1);
            CharHolder out = new CharHolder();
            ret = ti.opChar('a', inOut, out);
            assertTrue(ret == 'b');
            assertTrue(inOut.value == 'b');
            assertTrue(out.value == 'b');
        }

        {
            byte ret;
            ti.attrOctet((byte) 0xff);
            ret = ti.attrOctet();
            assertTrue(ret == (byte) 0xff);

            ti.attrOctet((byte) 0);
            ret = ti.attrOctet();
            assertTrue(ret == (byte) 0);

            ByteHolder inOut = new ByteHolder((byte) 20);
            ByteHolder out = new ByteHolder();
            ret = ti.opOctet((byte) 10, inOut, out);
            assertTrue(ret == 30);
            assertTrue(inOut.value == 30);
            assertTrue(out.value == 30);
        }

        {
            String ret;
            ti.attrString("Hello");
            ret = ti.attrString();
            assertTrue(ret.equals("Hello"));

            StringHolder inOut = new StringHolder("world!");
            StringHolder out = new StringHolder();
            ret = ti.opString("Hello, ", inOut, out);
            assertTrue(ret.equals("Hello, world!"));
            assertTrue(out.value.equals("Hello, world!"));
        }

        {
            Any any = m_orb.create_any();
            Any ret;

            for (i = 0; i < 2; i++) {
                AnyHolder inOut = new AnyHolder(m_orb.create_any());
                AnyHolder out = new AnyHolder();

                {
                    any.insert_string("abc");
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    String s;
                    s = ret.extract_string();
                    assertTrue(s.equals("abc"));
                }

                {
                    any.insert_long(3);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    int d;
                    d = ret.extract_long();
                    assertTrue(d == 3);
                }

                {
                    TestEnumHelper.insert(any, TestEnum.TestEnum3);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    TestEnum e;
                    e = TestEnumHelper.extract(ret);
                    assertTrue(e == TestEnum.TestEnum3);
                }

                {
                    VariableStruct vStruct = new VariableStruct();
                    vStruct.s = "xyz";
                    VariableStructHelper.insert(any, vStruct);
                    ret = ti.opAny(any, inOut, out);
                    VariableStruct vStructRet;
                    VariableStruct vStructInOut;
                    VariableStruct vStructOut;
                    vStructRet = VariableStructHelper.extract(ret);
                    vStructInOut = VariableStructHelper.extract(inOut.value);
                    vStructOut = VariableStructHelper.extract(out.value);
                    assertTrue(vStructRet.s.equals("xyz"));
                    assertTrue(vStructInOut.s.equals("xyz"));
                    assertTrue(vStructOut.s.equals("xyz"));
                }

                {
                    FixedUnion fUnion = new FixedUnion();
                    fUnion.l(1);
                    FixedUnionHelper.insert(any, fUnion);
                    ret = ti.opAny(any, inOut, out);
                    FixedUnion fUnionRet;
                    FixedUnion fUnionInOut;
                    FixedUnion fUnionOut;
                    fUnionRet = FixedUnionHelper.extract(ret);
                    fUnionInOut = FixedUnionHelper.extract(inOut.value);
                    fUnionOut = FixedUnionHelper.extract(out.value);
                    assertTrue(fUnionRet.discriminator() == 1);
                    assertTrue(fUnionInOut.discriminator() == 1);
                    assertTrue(fUnionOut.discriminator() == 1);
                    assertTrue(fUnionRet.l() == 1);
                    assertTrue(fUnionInOut.l() == 1);
                    assertTrue(fUnionOut.l() == 1);
                }

                {
                    VariableUnion vUnion = new VariableUnion();
                    vUnion.ti(ti);
                    VariableUnionHelper.insert(any, vUnion);
                    ret = ti.opAny(any, inOut, out);
                    VariableUnion vUnionRet;
                    VariableUnion vUnionInOut;
                    VariableUnion vUnionOut;
                    vUnionRet = VariableUnionHelper.extract(ret);
                    vUnionInOut = VariableUnionHelper.extract(ret);
                    vUnionOut = VariableUnionHelper.extract(ret);
                    assertTrue(!vUnionRet.discriminator());
                    assertTrue(!vUnionInOut.discriminator());
                    assertTrue(!vUnionOut.discriminator());
                }

                {
                    ORBTest_Basic.IntfHelper.insert(any, ti);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    org.omg.CORBA.Object extract_obj = ret.extract_Object();
                    ORBTest_Basic.Intf ti2 = (ORBTest_Basic.IntfHelper
                            .extract(ret));
                    assertTrue(ti._hash(1000) == ti2._hash(1000));
                    assertTrue(ti._is_equivalent(ti2));
                    assertTrue(ti._hash(1111) == extract_obj._hash(1111));
                    assertTrue(ti._is_equivalent(extract_obj));
                    assertTrue(extract_obj._hash(1234) == ti2._hash(1234));
                    assertTrue(extract_obj._is_equivalent(ti2));
                }

                {
                    char[] char_seq = { 'a', 'b', 'c', 'd' };
                    CharSeqHelper.insert(any, char_seq);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    char[] ret_char_seq = CharSeqHelper.extract(any);
                    assertTrue(ret_char_seq.length == 4);
                    for (int idx = 0; idx < 4; ++idx)
						assertTrue(char_seq[idx] == ret_char_seq[idx]);
                }

                {
                    char[] wchar_seq = { 'a', 'b', 'c', 'd' };
                    WCharSeqHelper.insert(any, wchar_seq);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    char[] ret_wchar_seq = WCharSeqHelper.extract(any);
                    assertTrue(ret_wchar_seq.length == 4);
                    for (int idx = 0; idx < 4; ++idx)
						assertTrue(wchar_seq[idx] == ret_wchar_seq[idx]);
                }
            }
        }

        {
            TestEnum ret;
            ti.attrTestEnum(TestEnum.TestEnum2);
            ret = ti.attrTestEnum();
            assertTrue(ret == TestEnum.TestEnum2);

            ti.attrTestEnum(TestEnum.TestEnum3);
            ret = ti.attrTestEnum();
            assertTrue(ret == TestEnum.TestEnum3);

            TestEnumHolder inOut = new TestEnumHolder(TestEnum.TestEnum2);
            TestEnumHolder out = new TestEnumHolder();
            ret = ti.opTestEnum(TestEnum.TestEnum3, inOut, out);
            assertTrue(ret == TestEnum.TestEnum3);
            assertTrue(inOut.value == TestEnum.TestEnum3);
            assertTrue(out.value == TestEnum.TestEnum3);
        }

        {
            ORBTest_Basic.Intf ret;
            ti.attrIntf(ti);
            ret = ti.attrIntf();
            assertTrue(ret._hash(999) == ti._hash(999));
            assertTrue(ret._is_equivalent(ti));

            ORBTest_Basic.IntfHolder inOut = (new ORBTest_Basic.IntfHolder(
                    (ORBTest_Basic.Intf) (ti._duplicate())));
            ORBTest_Basic.IntfHolder out = new ORBTest_Basic.IntfHolder();
            ret = ti.opIntf(ti, inOut, out);
            assertTrue(ret._hash(1001) == ti._hash(1001));
            assertTrue(ret._is_equivalent(ti));
            assertTrue(inOut.value._hash(5000) == ti._hash(5000));
            assertTrue(inOut.value._is_equivalent(ti));
            assertTrue(out.value._hash(2000) == ti._hash(2000));
            assertTrue(out.value._is_equivalent(ti));
        }

        {
            FixedStruct st = new FixedStruct();
            st.s = 100;
            st.l = -100;

            FixedStruct ret;
            ti.attrFixedStruct(st);
            ret = ti.attrFixedStruct();
            assertTrue(ret.s == st.s);
            assertTrue(ret.l == st.l);

            FixedStructHolder inOut = new FixedStructHolder(new FixedStruct());
            inOut.value.s = 10000;
            inOut.value.l = 100000;
            FixedStructHolder out = new FixedStructHolder();
            ret = ti.opFixedStruct(st, inOut, out);
            assertTrue(ret.s == st.s);
            assertTrue(ret.l == st.l);
            assertTrue(inOut.value.s == st.s);
            assertTrue(out.value.l == st.l);
            assertTrue(inOut.value.s == st.s);
            assertTrue(out.value.l == st.l);
        }

        {
            VariableStruct st = new VariableStruct();
            st.s = "$$$";

            VariableStruct ret;
            ti.attrVariableStruct(st);
            ret = ti.attrVariableStruct();
            assertTrue(ret.s.equals(st.s));

            VariableStructHolder inOut = new VariableStructHolder(
                    new VariableStruct());
            inOut.value.s = "bla";
            VariableStructHolder out = new VariableStructHolder();
            ret = ti.opVariableStruct(st, inOut, out);
            assertTrue(ret.s.equals(st.s));
            assertTrue(inOut.value.s.equals(st.s));
            assertTrue(out.value.s.equals(st.s));
        }

        {
            FixedUnion un = new FixedUnion();
            un.l(1);

            FixedUnion ret;
            ti.attrFixedUnion(un);
            ret = ti.attrFixedUnion();
            assertTrue(ret.discriminator() == 1);
            assertTrue(ret.l() == 1);

            un.b((short) 999, true);
            FixedUnionHolder inOut = new FixedUnionHolder();
            inOut.value = new FixedUnion();
            inOut.value.l(100);
            FixedUnionHolder out = new FixedUnionHolder();
            ret = ti.opFixedUnion(un, inOut, out);
            assertTrue(ret.discriminator() == 999);
            assertTrue(ret.b() == true);
            assertTrue(out.value.discriminator() == 999);
            assertTrue(out.value.b() == true);
            assertTrue(inOut.value.discriminator() == 999);
            assertTrue(inOut.value.b() == true);

            FixedStruct st = new FixedStruct();
            st.s = 10101;
            st.l = -10101;
            un.st(st);
            inOut.value = new FixedUnion();
            inOut.value.l(100);
            ret = ti.opFixedUnion(un, inOut, out);
            assertTrue(ret.discriminator() == 3);
            assertTrue(ret.st().s == 10101);
            assertTrue(ret.st().l == -10101);
            assertTrue(out.value.discriminator() == 3);
            assertTrue(out.value.st().s == 10101);
            assertTrue(out.value.st().l == -10101);
            assertTrue(inOut.value.discriminator() == 3);
            assertTrue(inOut.value.st().s == 10101);
            assertTrue(inOut.value.st().l == -10101);
        }

        {
            VariableUnion un = new VariableUnion();
            VariableStruct st = new VariableStruct();
            st.s = "$$$";
            un.st(st);

            VariableUnion ret;
            ti.attrVariableUnion(un);
            ret = ti.attrVariableUnion();
            assertTrue(ret.st().s.equals("$$$"));

            un.ti(ti);
            VariableUnionHolder inOut = new VariableUnionHolder(
                    new VariableUnion());
            VariableUnionHolder out = new VariableUnionHolder();
            inOut.value.st(st);
            ret = ti.opVariableUnion(un, inOut, out);
            assertTrue(ret.ti()._hash(1000) == ti._hash(1000));
            assertTrue(ret.ti()._is_equivalent(ti));
            assertTrue(inOut.value.ti()._hash(5000) == ti._hash(5000));
            assertTrue(inOut.value.ti()._is_equivalent(ti));
            assertTrue(out.value.ti()._hash(2000) == ti._hash(2000));
            assertTrue(out.value.ti()._is_equivalent(ti));
        }

        {
            String[] seq = new String[3];
            seq[0] = "!!!";
            seq[1] = "@@@";
            seq[2] = "###";

            String[] ret;
            ti.attrStringSequence(seq);
            ret = ti.attrStringSequence();
            assertTrue(ret.length == 3);
            assertTrue(ret[0].equals("!!!"));
            assertTrue(ret[1].equals("@@@"));
            assertTrue(ret[2].equals("###"));

            StringSequenceHolder inOut = new StringSequenceHolder(new String[2]);
            inOut.value[0] = "%";
            inOut.value[1] = "^^";
            StringSequenceHolder out = new StringSequenceHolder();
            ret = ti.opStringSequence(seq, inOut, out);
            assertTrue(ret.length == 5);
            assertTrue(ret[0].equals("!!!"));
            assertTrue(ret[1].equals("@@@"));
            assertTrue(ret[2].equals("###"));
            assertTrue(ret[3].equals("%"));
            assertTrue(ret[4].equals("^^"));
            assertTrue(inOut.value.length == 5);
            assertTrue(inOut.value[0].equals("!!!"));
            assertTrue(inOut.value[1].equals("@@@"));
            assertTrue(inOut.value[2].equals("###"));
            assertTrue(inOut.value[3].equals("%"));
            assertTrue(inOut.value[4].equals("^^"));
            assertTrue(out.value.length == 5);
            assertTrue(out.value[0].equals("!!!"));
            assertTrue(out.value[1].equals("@@@"));
            assertTrue(out.value[2].equals("###"));
            assertTrue(out.value[3].equals("%"));
            assertTrue(out.value[4].equals("^^"));
        }

        {
            short[][][] ar = {
                    { { 1, 2, 3, 4 }, { 10, -10, 11, -11 },
                            { -999, 0, 888, 123 } },
                    { { 17, 27, 37, 47 }, { 710, -710, 711, -711 },
                            { -99, 0, 88, 13 } } };

            short[][][] ret;
            ti.attrFixedArray(ar);
            ret = ti.attrFixedArray();
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++)
                    for (k = 0; k < 4; k++) {
                        assertTrue(ar[i][j][k] == ret[i][j][k]);
                    }

            FixedArrayHolder inOut = new FixedArrayHolder(new short[2][3][4]);
            FixedArrayHolder out = new FixedArrayHolder();
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++)
                    for (k = 0; k < 4; k++)
                        inOut.value[i][j][k] = (short) (i + j + k);
            ret = ti.opFixedArray(ar, inOut, out);
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++)
                    for (k = 0; k < 4; k++) {
                        assertTrue(ar[i][j][k] == ret[i][j][k]);
                        assertTrue(ar[i][j][k] == inOut.value[i][j][k]);
                        assertTrue(ar[i][j][k] == out.value[i][j][k]);
                    }
        }

        {
            String[][] ar = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ret;
            ti.attrVariableArray(ar);
            ret = ti.attrVariableArray();
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++) {
                    assertTrue((ar[i][j].equals(ret[i][j])));
                }

            VariableArrayHolder inOut = new VariableArrayHolder(
                    new String[2][3]);
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++)
                    inOut.value[i][j] = "abc";
            VariableArrayHolder out = new VariableArrayHolder();
            ret = ti.opVariableArray(ar, inOut, out);
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++) {
                    assertTrue(ar[i][j].equals(ret[i][j]));
                    assertTrue(ar[i][j].equals(inOut.value[i][j]));
                    assertTrue(ar[i][j].equals(out.value[i][j]));
                }
        }

        {
            short[][][] ar0 = {
                    { { 1, 2, 3, 4 }, { 10, -10, 11, -11 },
                            { -999, 0, 888, 123 } },
                    { { 17, 27, 37, 47 }, { 710, -710, 711, -711 },
                            { -99, 0, 88, 13 } } };

            short[][][] ar1 = {
                    { { 2, 3, 4, 1 }, { 10, 11, 11, -10 },
                            { -0, 939, 123, 888 } },
                    { { 17, 37, 47, 27 }, { 710, -710, 711, -711 },
                            { -0, -99, 13, 8338 } } };

            short[][][] ar2 = {
                    { { 1, 2, -3, -234 }, { 10, -11, 11, -10 },
                            { -999, 30, 1888, 123 } },
                    { { 27, 37, 117, 47 }, { 710, -7150, 711, -711 },
                            { -0, 13, 929, 88 } } };

            short[][][][] seq = new short[3][2][3][4];
            seq[0] = ar0;
            seq[1] = ar1;
            seq[2] = ar2;

            short[][][][] ret;

            ti.attrFixedArraySequence(seq);
            ret = ti.attrFixedArraySequence();

            for (l = 0; l < 3; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            assertTrue(seq[l][i][j][k] == ret[l][i][j][k]);
                        }

            FixedArraySequenceHolder inOut = new FixedArraySequenceHolder(
                    new short[4][2][3][4]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++)
                            inOut.value[l][i][j][k] = (short) (i + j + k + l);

            FixedArraySequenceHolder out = new FixedArraySequenceHolder();

            ret = ti.opFixedArraySequence(seq, inOut, out);

            assertTrue(ret.length == 7);
            assertTrue(inOut.value.length == 7);
            assertTrue(out.value.length == 7);

            for (l = 0; l < 3; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            assertTrue(seq[l][i][j][k] == ret[l][i][j][k]);
                            assertTrue(seq[l][i][j][k] == inOut.value[l][i][j][k]);
                            assertTrue(seq[l][i][j][k] == out.value[l][i][j][k]);
                        }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            assertTrue(ret[3 + l][i][j][k] == i + j + k + l);
                            assertTrue(inOut.value[3 + l][i][j][k] == i + j + k + l);
                            assertTrue(out.value[3 + l][i][j][k] == i + j + k + l);
                        }
        }

        {
            String[][] ar0 = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ar1 = { { "a-a", "b-b", "c-c" }, { "A-A", "B-B", "C-C" } };

            String[][][] seq = new String[2][][];
            seq[0] = ar0;
            seq[1] = ar1;

            String[][][] ret;

            ti.attrVariableArraySequence(seq);
            ret = ti.attrVariableArraySequence();

            for (l = 0; l < 2; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        assertTrue(seq[l][i][j].equals(ret[l][i][j]));
                    }

            VariableArraySequenceHolder inOut = new VariableArraySequenceHolder(
                    new String[4][2][3]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        inOut.value[l][i][j] = "***";

            VariableArraySequenceHolder out = new VariableArraySequenceHolder();

            ret = ti.opVariableArraySequence(seq, inOut, out);

            assertTrue(ret.length == 6);
            assertTrue(inOut.value.length == 6);
            assertTrue(out.value.length == 6);

            for (l = 0; l < 2; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        assertTrue(seq[l][i][j].equals(ret[l][i][j]));
                        assertTrue(seq[l][i][j].equals(inOut.value[l][i][j]));
                        assertTrue(seq[l][i][j].equals(out.value[l][i][j]));
                    }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        assertTrue(ret[2 + l][i][j].equals("***"));
                        assertTrue(inOut.value[2 + l][i][j].equals("***"));
                        assertTrue(out.value[2 + l][i][j].equals("***"));
                    }
        }

        {
            short[][][] ar0 = {
                    { { 1, 2, 3, 4 }, { 10, -10, 11, -11 },
                            { -999, 0, 888, 123 } },
                    { { 17, 27, 37, 47 }, { 710, -710, 711, -711 },
                            { -99, 0, 88, 13 } } };

            short[][][] ar1 = {
                    { { 2, 3, 4, 1 }, { 10, 11, 11, -10 },
                            { -0, 939, 123, 888 } },
                    { { 17, 37, 47, 27 }, { 710, -710, 711, -711 },
                            { -0, -99, 13, 8338 } } };

            short[][][] ar2 = {
                    { { 1, 2, -3, -234 }, { 10, -11, 11, -10 },
                            { -999, 30, 1888, 123 } },
                    { { 27, 37, 117, 47 }, { 710, -7150, 711, -711 },
                            { -0, 13, 929, 88 } } };

            short[][][][] seq = new short[3][2][3][4];
            seq[0] = ar0;
            seq[1] = ar1;
            seq[2] = ar2;

            short[][][][] ret;

            ti.attrFixedArrayBoundSequence(seq);
            ret = ti.attrFixedArrayBoundSequence();

            for (l = 0; l < 3; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            assertTrue(seq[l][i][j][k] == ret[l][i][j][k]);
                        }

            FixedArrayBoundSequenceHolder inOut = new FixedArrayBoundSequenceHolder(
                    new short[4][2][3][4]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++)
                            inOut.value[l][i][j][k] = (short) (i + j + k + l);

            FixedArrayBoundSequenceHolder out = new FixedArrayBoundSequenceHolder();

            ret = ti.opFixedArrayBoundSequence(seq, inOut, out);

            assertTrue(ret.length == 7);
            assertTrue(inOut.value.length == 7);
            assertTrue(out.value.length == 7);

            for (l = 0; l < 3; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            assertTrue(seq[l][i][j][k] == ret[l][i][j][k]);
                            assertTrue(seq[l][i][j][k] == inOut.value[l][i][j][k]);
                            assertTrue(seq[l][i][j][k] == out.value[l][i][j][k]);
                        }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            assertTrue(ret[3 + l][i][j][k] == i + j + k + l);
                            assertTrue(inOut.value[3 + l][i][j][k] == i + j + k + l);
                            assertTrue(out.value[3 + l][i][j][k] == i + j + k + l);
                        }
        }

        {
            String[][] ar0 = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ar1 = { { "a-a", "b-b", "c-c" }, { "A-A", "B-B", "C-C" } };

            String[][][] seq = new String[2][][];
            seq[0] = ar0;
            seq[1] = ar1;

            String[][][] ret;

            ti.attrVariableArrayBoundSequence(seq);
            ret = ti.attrVariableArrayBoundSequence();

            for (l = 0; l < 2; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        assertTrue(seq[l][i][j].equals(ret[l][i][j]));
                    }

            VariableArrayBoundSequenceHolder inOut = new VariableArrayBoundSequenceHolder(
                    new String[4][2][3]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        inOut.value[l][i][j] = "***";

            VariableArrayBoundSequenceHolder out = new VariableArrayBoundSequenceHolder();

            ret = ti.opVariableArrayBoundSequence(seq, inOut, out);

            assertTrue(ret.length == 6);
            assertTrue(inOut.value.length == 6);
            assertTrue(out.value.length == 6);

            for (l = 0; l < 2; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        assertTrue(seq[l][i][j].equals(ret[l][i][j]));
                        assertTrue(seq[l][i][j].equals(inOut.value[l][i][j]));
                        assertTrue(seq[l][i][j].equals(out.value[l][i][j]));
                    }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        assertTrue(ret[2 + l][i][j].equals("***"));
                        assertTrue(inOut.value[2 + l][i][j].equals("***"));
                        assertTrue(out.value[2 + l][i][j].equals("***"));
                    }
        }

        {
            try {
                ti.opVoidEx();
                assertTrue(false);
            } catch (ExVoid ex) {
            }
        }

        {
            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();

            try {
                ti.opShortEx((short) 10, inOut, out);
                assertTrue(false);
            } catch (ExShort ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();

            try {
                ti.opLongEx(10, inOut, out);
                assertTrue(false);
            } catch (ExLong ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();

            try {
                ti.opUShortEx((short) 10, inOut, out);
                assertTrue(false);
            } catch (ExUShort ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();

            try {
                ti.opULongEx(10, inOut, out);
                assertTrue(false);
            } catch (ExULong ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            FloatHolder inOut = new FloatHolder(20);
            FloatHolder out = new FloatHolder();

            try {
                ti.opFloatEx(10, inOut, out);
                assertTrue(false);
            } catch (ExFloat ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            DoubleHolder inOut = new DoubleHolder(20);
            DoubleHolder out = new DoubleHolder();

            try {
                ti.opDoubleEx(10, inOut, out);
                assertTrue(false);
            } catch (ExDouble ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            BooleanHolder inOut = new BooleanHolder(true);
            BooleanHolder out = new BooleanHolder();

            try {
                ti.opBooleanEx(true, inOut, out);
                assertTrue(false);
            } catch (ExBoolean ex) {
                assertTrue(ex.value == true);
            }
        }

        {
            CharHolder inOut = new CharHolder((char) 1);
            CharHolder out = new CharHolder();

            try {
                ti.opCharEx('a', inOut, out);
                assertTrue(false);
            } catch (ExChar ex) {
                assertTrue(ex.value == 'b');
            }
        }

        {
            ByteHolder inOut = new ByteHolder((byte) 20);
            ByteHolder out = new ByteHolder();

            try {
                ti.opOctetEx((byte) 10, inOut, out);
                assertTrue(false);
            } catch (ExOctet ex) {
                assertTrue(ex.value == 30);
            }
        }

        {
            StringHolder inOut = new StringHolder("world!");
            StringHolder out = new StringHolder();

            try {
                ti.opStringEx("Hello, ", inOut, out);
                assertTrue(false);
            } catch (ExString ex) {
                assertTrue(ex.value.equals("Hello, world!"));
            }
        }

        {
            VariableStruct vStruct = new VariableStruct();
            vStruct.s = "xyz";
            Any any = m_orb.create_any();
            VariableStructHelper.insert(any, vStruct);
            AnyHolder inOut = new AnyHolder(m_orb.create_any());
            AnyHolder out = new AnyHolder(m_orb.create_any());

            try {
                ti.opAnyEx(any, inOut, out);
                assertTrue(false);
            } catch (ExAny ex) {
                VariableStruct vStructRet;
                vStructRet = VariableStructHelper.extract(any);
                assertTrue(vStructRet.s.equals("xyz"));
            }
        }

        {
            TestEnumHolder inOut = new TestEnumHolder();
            TestEnumHolder out = new TestEnumHolder();
            inOut.value = TestEnum.TestEnum2;

            try {
                ti.opTestEnumEx(TestEnum.TestEnum3, inOut, out);
                assertTrue(false);
            } catch (ExTestEnum ex) {
                assertTrue(ex.value == TestEnum.TestEnum3);
            }
        }

        if (orb_type == ORBType.ORBacus4) {
            ORBTest_Basic.IntfHolder inOut = new ORBTest_Basic.IntfHolder();
            ORBTest_Basic.IntfHolder out = new ORBTest_Basic.IntfHolder();
            inOut.value = (ORBTest_Basic.Intf) ti._duplicate();

            try {
                ti.opIntfEx(ti, inOut, out);
                assertTrue(false);
            } catch (ORBTest_Basic.ExIntf ex) {
                assertTrue(ex.value._hash(1000) == ti._hash(1000));
                assertTrue(ex.value._is_equivalent(ti));
            }
        }

        {
            FixedStruct st = new FixedStruct();
            st.s = 100;
            st.l = -100;
            FixedStructHolder inOut = new FixedStructHolder(new FixedStruct());
            FixedStructHolder out = new FixedStructHolder();
            inOut.value.s = 10000;
            inOut.value.l = 100000;

            try {
                ti.opFixedStructEx(st, inOut, out);
                assertTrue(false);
            } catch (ExFixedStruct ex) {
                assertTrue(ex.value.s == st.s);
                assertTrue(ex.value.l == st.l);
            }
        }

        {
            VariableStruct st = new VariableStruct();
            st.s = "$$$";
            VariableStructHolder inOut = new VariableStructHolder(
                    new VariableStruct());
            VariableStructHolder out = new VariableStructHolder();
            inOut.value.s = "bla";

            try {
                ti.opVariableStructEx(st, inOut, out);
                assertTrue(false);
            } catch (ExVariableStruct ex) {
                assertTrue(ex.value.s.equals(st.s));
            }
        }

        {
            FixedUnion un = new FixedUnion();
            un.b((short) 999, true);
            FixedUnionHolder inOut = new FixedUnionHolder(new FixedUnion());
            FixedUnionHolder out = new FixedUnionHolder();
            inOut.value.l(100);

            try {
                ti.opFixedUnionEx(un, inOut, out);
                assertTrue(false);
            } catch (ExFixedUnion ex) {
                assertTrue(ex.value.discriminator() == 999);
                assertTrue(ex.value.b() == true);
            }
        }

        if (orb_type == ORBType.ORBacus4) {
            VariableUnion un = new VariableUnion();
            un.ti(ti);
            VariableUnionHolder inOut = new VariableUnionHolder(
                    new VariableUnion());
            VariableUnionHolder out = new VariableUnionHolder();
            VariableStruct st = new VariableStruct();
            st.s = "bla";
            inOut.value.st(st);

            try {
                ti.opVariableUnionEx(un, inOut, out);
                assertTrue(false);
            } catch (ExVariableUnion ex) {
                assertTrue(ex.value.ti()._hash(2000) == ti._hash(2000));
                assertTrue(ex.value.ti()._is_equivalent(ti));
            }
        }

        {
            String[] seq = new String[3];
            seq[0] = "!!!";
            seq[1] = "@@@";
            seq[2] = "###";
            StringSequenceHolder inOut = new StringSequenceHolder();
            StringSequenceHolder out = new StringSequenceHolder();
            inOut.value = new String[2];
            inOut.value[0] = "%";
            inOut.value[1] = "^^";

            try {
                ti.opStringSequenceEx(seq, inOut, out);
                assertTrue(false);
            } catch (ExStringSequence ex) {
                assertTrue(ex.value.length == 5);
                assertTrue(ex.value[0].equals("!!!"));
                assertTrue(ex.value[1].equals("@@@"));
                assertTrue(ex.value[2].equals("###"));
                assertTrue(ex.value[3].equals("%"));
                assertTrue(ex.value[4].equals("^^"));
            }
        }

        {
            short[][][] ar = {
                    { { 1, 2, 3, 4 }, { 10, -10, 11, -11 },
                            { -999, 0, 888, 123 } },
                    { { 17, 27, 37, 47 }, { 710, -710, 711, -711 },
                            { -99, 0, 88, 13 } } };

            short[][][] ret;
            FixedArrayHolder inOut = new FixedArrayHolder(new short[2][3][4]);
            FixedArrayHolder out = new FixedArrayHolder();
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++)
                    for (k = 0; k < 4; k++)
                        inOut.value[i][j][k] = (short) (i + j + k);

            try {
                ti.opFixedArrayEx(ar, inOut, out);
                assertTrue(false);
            } catch (ExFixedArray ex) {
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            assertTrue(ar[i][j][k] == ex.value[i][j][k]);
                        }
            }
        }

        {
            String[][] ar = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ret;
            VariableArrayHolder inOut = new VariableArrayHolder(
                    new String[2][3]);
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++)
                    inOut.value[i][j] = "abc";
            VariableArrayHolder out = new VariableArrayHolder();

            try {
                ti.opVariableArrayEx(ar, inOut, out);
                assertTrue(false);
            } catch (ExVariableArray ex) {
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        assertTrue(ar[i][j].equals(ex.value[i][j]));
                    }
            }
        }

        {
            short[][][] ar0 = {
                    { { 1, 2, 3, 4 }, { 10, -10, 11, -11 },
                            { -999, 0, 888, 123 } },
                    { { 17, 27, 37, 47 }, { 710, -710, 711, -711 },
                            { -99, 0, 88, 13 } } };

            short[][][] ar1 = {
                    { { 2, 3, 4, 1 }, { 10, 11, 11, -10 },
                            { -0, 239, 123, 888 } },
                    { { 17, 37, 47, 27 }, { 710, -710, 711, -711 },
                            { -0, -99, 13, 8338 } } };

            short[][][] ar2 = {
                    { { 1, 2, -3, -234 }, { 10, -11, 11, -10 },
                            { -999, 30, 1888, 123 } },
                    { { 27, 37, 117, 47 }, { 710, -7150, 711, -711 },
                            { -0, 13, 929, 88 } } };

            short[][][][] seq = new short[3][2][3][4];
            seq[0] = ar0;
            seq[1] = ar1;
            seq[2] = ar2;

            FixedArraySequenceHolder inOut = new FixedArraySequenceHolder(
                    new short[4][2][3][4]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++)
                            inOut.value[l][i][j][k] = (short) (i + j + k + l);

            short[][][][] ret;
            FixedArraySequenceHolder out = new FixedArraySequenceHolder();

            try {
                ret = ti.opFixedArraySequenceEx(seq, inOut, out);
                assertTrue(false);
            } catch (ExFixedArraySequence ex) {
                assertTrue(ex.value.length == 7);

                for (l = 0; l < 3; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++) {
                                assertTrue(seq[l][i][j][k] == ex.value[l][i][j][k]);
                            }

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++) {
                                assertTrue(ex.value[3 + l][i][j][k] == i + j + k + l);
                            }
            }
        }

        {
            String[][] ar0 = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ar1 = { { "a-a", "b-b", "c-c" }, { "A-A", "B-B", "C-C" } };

            String[][][] seq = new String[2][][];
            seq[0] = ar0;
            seq[1] = ar1;

            VariableArraySequenceHolder inOut = new VariableArraySequenceHolder(
                    new String[4][2][3]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        inOut.value[l][i][j] = "***";

            String[][][] ret;
            VariableArraySequenceHolder out = new VariableArraySequenceHolder();

            try {
                ret = ti.opVariableArraySequenceEx(seq, inOut, out);
                assertTrue(false);
            } catch (ExVariableArraySequence ex) {
                assertTrue(ex.value.length == 6);

                for (l = 0; l < 2; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++) {
                            assertTrue(seq[l][i][j].equals(ex.value[l][i][j]));
                        }

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++) {
                            assertTrue(ex.value[2 + l][i][j].equals("***"));
                        }

            }
        }

        {
            short[][][] ar0 = {
                    { { 1, 2, 3, 4 }, { 10, -10, 11, -11 },
                            { -999, 0, 888, 123 } },
                    { { 17, 27, 37, 47 }, { 710, -710, 711, -711 },
                            { -99, 0, 88, 13 } } };

            short[][][] ar1 = {
                    { { 2, 3, 4, 1 }, { 10, 11, 11, -10 },
                            { -0, 939, 123, 888 } },
                    { { 17, 37, 47, 27 }, { 710, -710, 711, -711 },
                            { -0, -99, 13, 8338 } } };

            short[][][] ar2 = {
                    { { 1, 2, -3, -234 }, { 10, -11, 11, -10 },
                            { -999, 30, 1888, 123 } },
                    { { 27, 37, 117, 47 }, { 710, -7150, 711, -711 },
                            { -0, 13, 929, 88 } } };

            short[][][][] seq = new short[3][2][3][4];
            seq[0] = ar0;
            seq[1] = ar1;
            seq[2] = ar2;

            FixedArrayBoundSequenceHolder inOut = new FixedArrayBoundSequenceHolder(
                    new short[4][2][3][4]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++)
                            inOut.value[l][i][j][k] = (short) (i + j + k + l);

            short[][][][] ret;
            FixedArrayBoundSequenceHolder out = new FixedArrayBoundSequenceHolder();

            try {
                ret = ti.opFixedArrayBoundSequenceEx(seq, inOut, out);
                assertTrue(false);
            } catch (ExFixedArrayBoundSequence ex) {
                assertTrue(ex.value.length == 7);

                for (l = 0; l < 3; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++)
								assertTrue(seq[l][i][j][k] == ex.value[l][i][j][k]);

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++)
								assertTrue(ex.value[3 + l][i][j][k] == i + j + k + l);
            }
        }

        {
            String[][] ar0 = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ar1 = { { "a-a", "b-b", "c-c" }, { "A-A", "B-B", "C-C" } };

            String[][][] seq = new String[2][][];
            seq[0] = ar0;
            seq[1] = ar1;

            VariableArrayBoundSequenceHolder inOut = new VariableArrayBoundSequenceHolder(
                    new String[4][2][3]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        inOut.value[l][i][j] = "***";

            String[][][] ret;
            VariableArrayBoundSequenceHolder out = new VariableArrayBoundSequenceHolder();

            try {
                ret = ti.opVariableArrayBoundSequenceEx(seq, inOut, out);
                assertTrue(false);
            } catch (ExVariableArrayBoundSequence ex) {
                assertTrue(ex.value.length == 6);

                for (l = 0; l < 2; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
							assertTrue(seq[l][i][j].equals(ex.value[l][i][j]));

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
							assertTrue(ex.value[2 + l][i][j].equals("***"));
            }
        }
        {
            try {
                ti.opExRecursiveStruct();
                assertTrue(false);
            } catch (ORBTest_Basic.ExRecursiveStruct ex) {
                assertTrue(ex.us == 1);
                assertTrue(ex.rs.s.equals("test"));
                assertTrue(ex.rs.i == 2);
                assertTrue(ex.rs.rs.length == 1);
                assertTrue(ex.rs.rs[0].s.equals("ORBTest_Basic_RecursiveStruct"));
                assertTrue(ex.rs.rs[0].i == 111);
                assertTrue(ex.rs.rs[0].rs.length == 0);
            }
        }
    }

    public void test_DII(org.omg.CORBA.Object obj) {
        ORBTest_Basic.Intf ti = ORBTest_Basic.IntfHelper.narrow(obj);

        int i, j;

        {
            Request request;

            request = ti._request("opVoid");
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
        }

        {
            Request request;

            short ret;
            short inOut;
            short out;

            request = ti._request("_set_attrShort");
            request.add_in_arg().insert_short((short) -32768);
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrShort");
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_short));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            ret = request.return_value().extract_short();
            assertTrue(ret == (short) -32768);

            request = ti._request("_set_attrShort");
            request.add_in_arg().insert_short((short) 32767);
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrShort");
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_short));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            ret = request.return_value().extract_short();
            assertTrue(ret == (short) 32767);

            request = ti._request("opShort");
            request.add_in_arg().insert_short((short) 10);
            Any inOutAny = request.add_inout_arg();
            inOutAny.insert_short((short) 20);
            Any outAny = request.add_out_arg();
            outAny.insert_short((short) 0);
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_short));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            inOut = inOutAny.extract_short();
            out = outAny.extract_short();
            ret = request.return_value().extract_short();
            assertTrue(ret == 30);
            assertTrue(inOut == 30);
            assertTrue(out == 30);
        }

        {
            Request request;

            double ret;
            double inOut;
            double out;

            request = ti._request("_set_attrDouble");
            request.add_in_arg().insert_double(1.7976931348623157E+308);
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrDouble");
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_double));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            ret = request.return_value().extract_double();
            assertTrue(ret == 1.7976931348623157E+308);

            request = ti._request("_set_attrDouble");
            request.add_in_arg().insert_double(2.2250738585072014E-308);
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrDouble");
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_double));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            ret = request.return_value().extract_double();
            assertTrue(ret == 2.2250738585072014E-308);

            request = ti._request("opDouble");
            request.add_in_arg().insert_double(10.0);
            Any inOutAny = request.add_inout_arg();
            inOutAny.insert_double(20.0);
            Any outAny = request.add_out_arg();
            outAny.insert_double(0);
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_double));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            inOut = inOutAny.extract_double();
            out = outAny.extract_double();
            ret = request.return_value().extract_double();
            assertTrue(ret == 30);
            assertTrue(inOut == 30);
            assertTrue(out == 30);
        }

        {
            Request request;

            String ret;
            String inOut;
            String out;

            request = ti._request("_set_attrString");
            request.add_in_arg().insert_string("Hello");
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrString");
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_string));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            ret = request.return_value().extract_string();
            assertTrue(ret.equals("Hello"));

            request = ti._request("opString");
            request.add_in_arg().insert_string("Hello, ");
            Any inOutAny = request.add_inout_arg();
            inOutAny.insert_string("world!");
            Any outAny = request.add_out_arg();
            outAny.insert_string("");
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_string));
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            inOut = inOutAny.extract_string();
            out = outAny.extract_string();
            ret = request.return_value().extract_string();
            assertTrue(ret.equals("Hello, world!"));
            assertTrue(out.equals("Hello, world!"));
        }

        {
            Request request;

            String[] seq = { "!!!", "@@@", "###" };

            String[] ret;
            String[] inOut;
            String[] out;

            request = ti._request("_set_attrStringSequence");
            StringSequenceHelper.insert(request.add_in_arg(), seq);
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrStringSequence");
            request.set_return_type(StringSequenceHelper.type());
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            ret = StringSequenceHelper.extract(request.return_value());
            assertTrue(ret.length == 3);
            assertTrue(ret[0].equals("!!!"));
            assertTrue(ret[1].equals("@@@"));
            assertTrue(ret[2].equals("###"));

            inOut = new String[2];
            inOut[0] = "%";
            inOut[1] = "^^";
            request = ti._request("opStringSequence");
            StringSequenceHelper.insert(request.add_in_arg(), seq);
            Any inOutAny = request.add_inout_arg();
            StringSequenceHelper.insert(inOutAny, inOut);
            Any outAny = request.add_out_arg();
            outAny.type(StringSequenceHelper.type());
            request.set_return_type(StringSequenceHelper.type());
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            ret = StringSequenceHelper.extract(request.return_value());
            inOut = StringSequenceHelper.extract(inOutAny);
            out = StringSequenceHelper.extract(outAny);
            assertTrue(ret.length == 5);
            assertTrue(ret[0].equals("!!!"));
            assertTrue(ret[1].equals("@@@"));
            assertTrue(ret[2].equals("###"));
            assertTrue(ret[3].equals("%"));
            assertTrue(ret[4].equals("^^"));
            assertTrue(inOut.length == 5);
            assertTrue(inOut[0].equals("!!!"));
            assertTrue(inOut[1].equals("@@@"));
            assertTrue(inOut[2].equals("###"));
            assertTrue(inOut[3].equals("%"));
            assertTrue(inOut[4].equals("^^"));
            assertTrue(out.length == 5);
            assertTrue(out[0].equals("!!!"));
            assertTrue(out[1].equals("@@@"));
            assertTrue(out[2].equals("###"));
            assertTrue(out[3].equals("%"));
            assertTrue(out[4].equals("^^"));
        }

        {
            Request request;

            char ret;
            char inOut;
            char out;

            request = ti._request("_set_attrChar");
            request.add_in_arg().insert_char('a');
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrChar");
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_char));
            request.send_deferred();
            try {
                request.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }
            ret = request.return_value().extract_char();
            assertTrue(ret == 'a');

            request = ti._request("opChar");
            request.add_in_arg().insert_char('a');
            Any inOutAny = request.add_inout_arg();
            inOutAny.insert_char((char) 1);
            Any outAny = request.add_out_arg();
            outAny.type(m_orb.get_primitive_tc(TCKind.tk_char));
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_char));
            request.send_deferred();
            try {
                request.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }
            inOut = inOutAny.extract_char();
            out = outAny.extract_char();
            ret = request.return_value().extract_char();
            assertTrue(ret == 'b');
            assertTrue(inOut == 'b');
            assertTrue(out == 'b');
        }

        {
            Request request;

            String[][] ar = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ret = new String[2][3];
            String[][] inOut = new String[2][3];
            String[][] out = new String[2][3];

            request = ti._request("_set_attrVariableArray");
            VariableArrayHelper.insert(request.add_in_arg(), ar);
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();
            request = ti._request("_get_attrVariableArray");
            request.set_return_type(VariableArrayHelper.type());
            request.send_deferred();
            while (!request.poll_response())
                Thread.yield();
            try {
                request.get_response();
            } catch (org.omg.CORBA.WrongTransaction ex) {
                assertTrue(false);
            }
            ret = VariableArrayHelper.extract(request.return_value());
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++) {
                    assertTrue(ar[i][j].equals(ret[i][j]));
                }

            String[][] ar2 = { { "abc", "abc", "abc" }, { "abc", "abc", "abc" } };

            request = ti._request("opVariableArray");
            VariableArrayHelper.insert(request.add_in_arg(), ar);
            Any inOutAny = request.add_inout_arg();
            VariableArrayHelper.insert(inOutAny, ar2);
            Any outAny = request.add_out_arg();
            outAny.type(VariableArrayHelper.type());
            request.set_return_type(VariableArrayHelper.type());
            request.send_deferred();
            while (!request.poll_response())
                Thread.yield();
            try {
                request.get_response();
            } catch (org.omg.CORBA.WrongTransaction ex) {
                assertTrue(false);
            }
            ret = VariableArrayHelper.extract(request.return_value());
            inOut = VariableArrayHelper.extract(inOutAny);
            out = VariableArrayHelper.extract(outAny);
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++) {
                    assertTrue(ar[i][j].equals(ret[i][j]));
                    assertTrue(ar[i][j].equals(inOut[i][j]));
                    assertTrue(ar[i][j].equals(out[i][j]));
                }
        }

        try {
            m_orb.poll_next_response();
            assertTrue(false);
        } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
            // Expected
        }

        try {
            m_orb.get_next_response();
            assertTrue(false);
        } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
            // Expected
        } catch (org.omg.CORBA.WrongTransaction ex) {
            assertTrue(false);
        }

        {
            Request request;

            request = ti._request("opVoid");
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();

            try {
                request.invoke();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_oneway();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_deferred();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.poll_response();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.get_response();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            } catch (org.omg.CORBA.WrongTransaction ex) {
                assertTrue(false);
            }
        }

        {
            Request request;

            request = ti._request("opVoid");

            try {
                request.poll_response();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.get_response();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            } catch (org.omg.CORBA.WrongTransaction ex) {
                assertTrue(false);
            }

            request.send_deferred();

            try {
                request.invoke();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_oneway();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_deferred();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }

            try {
                request.get_response();
            } catch (org.omg.CORBA.WrongTransaction ex) {
                assertTrue(false);
            }

            try {
                request.poll_response();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.get_response();
                assertTrue(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            } catch (org.omg.CORBA.WrongTransaction ex) {
                assertTrue(false);
            }
        }

        {
            Request request;

            request = ti._request("_set_attrUShort");
            request.add_in_arg().insert_ushort((short) 1234);
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();

            Request[] requests = new Request[5];
            for (i = 0; i < requests.length; i++) {
                requests[i] = ti._request("_get_attrUShort");
                requests[i].set_return_type(m_orb
                        .get_primitive_tc(TCKind.tk_ushort));
            }

            m_orb.send_multiple_requests_deferred(requests);

            for (i = 0; i < requests.length; i++) {
                while (!m_orb.poll_next_response())
                    Thread.yield();

                try {
                    request = m_orb.get_next_response();
                } catch (org.omg.CORBA.WrongTransaction ex) {
                    assertTrue(false);
                }

                short ret = request.return_value().extract_ushort();
                assertTrue(ret == (short) 1234);
            }
        }

        if (!m_test_intf.concurrent_request_execution()) {
            float ret;
            float inOut;
            float out;

            Request request1;
            request1 = ti._request("_set_attrFloat");
            request1.add_in_arg().insert_float(1);
            request1.send_deferred();

            Request request2;
            request2 = ti._request("_get_attrFloat");
            request2.set_return_type(m_orb.get_primitive_tc(TCKind.tk_float));
            request2.send_deferred();

            Request request3;
            request3 = ti._request("_set_attrFloat");
            request3.add_in_arg().insert_float(-1);
            request3.send_deferred();

            Request request4;
            request4 = ti._request("_get_attrFloat");
            request4.set_return_type(m_orb.get_primitive_tc(TCKind.tk_float));
            request4.send_deferred();

            Request request5;
            request5 = ti._request("opFloat");
            request5.add_in_arg().insert_float(10);
            Any inOutAny = request5.add_inout_arg();
            inOutAny.insert_float(20);
            Any outAny = request5.add_out_arg();
            outAny.insert_float(0);
            request5.set_return_type(m_orb.get_primitive_tc(TCKind.tk_float));
            request5.send_deferred();

            try {
                request5.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }
            inOut = inOutAny.extract_float();
            out = outAny.extract_float();
            ret = request5.return_value().extract_float();
            assertTrue(ret == 30);
            assertTrue(inOut == 30);
            assertTrue(out == 30);

            try {
                request2.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }
            ret = request2.return_value().extract_float();
            assertTrue(ret == 1);

            try {
                request4.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }
            ret = request4.return_value().extract_float();
            assertTrue(ret == -1);

            try {
                request1.get_response();
                request3.get_response();
            } catch (WrongTransaction ex) {
                assertTrue(false);
            }
        }

        Request request;

        {
            Exception ex;

            try {
                request = ti._request("opVoidEx");
                request.invoke();
                ex = request.env().exception();
                UNKNOWN dummy = (UNKNOWN) ex;
                assertTrue(dummy != null);
            } catch (UNKNOWN e) {
                // expected in JDK 1.2
            }

            request = ti._request("opVoidEx");
            request.exceptions().add(ExVoidHelper.type());
            request.invoke();
            ex = request.env().exception();
            UnknownUserException uex;
            uex = (UnknownUserException) ex;
            assertTrue(uex != null);
            ExVoid iex;
            iex = ExVoidHelper.extract(uex.except);
        }

        {
            request = ti._request("opShortEx");
            request.exceptions().add(ExShortHelper.type());
            request.add_in_arg().insert_short((short) 10);
            Any inOutAny = request.add_inout_arg();
            inOutAny.insert_short((short) 20);
            Any outAny = request.add_out_arg();
            outAny.insert_short((short) 0);
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_short));
            request.invoke();
            Exception ex = request.env().exception();
            UnknownUserException uex;
            uex = (UnknownUserException) ex;
            assertTrue(uex != null);
            ExShort iex;
            iex = ExShortHelper.extract(uex.except);
            assertTrue(iex.value == 30);
        }

        {
            request = ti._request("opDoubleEx");
            request.exceptions().add(ExDoubleHelper.type());
            request.add_in_arg().insert_double(10);
            Any inOutAny = request.add_inout_arg();
            inOutAny.insert_double(20);
            Any outAny = request.add_out_arg();
            outAny.insert_double(0);
            request.set_return_type(m_orb.get_primitive_tc(TCKind.tk_double));
            request.invoke();
            Exception ex = request.env().exception();
            UnknownUserException uex;
            uex = (UnknownUserException) ex;
            assertTrue(uex != null);
            ExDouble iex;
            iex = ExDoubleHelper.extract(uex.except);
            assertTrue(iex.value == 30);
        }

        {
            String[] in = new String[3];
            in[0] = "!!!";
            in[1] = "@@@";
            in[2] = "###";
            String[] inOut = new String[2];
            inOut[0] = "%";
            inOut[1] = "^^";
            request = ti._request("opStringSequenceEx");
            request.exceptions().add(ExStringSequenceHelper.type());
            StringSequenceHelper.insert(request.add_in_arg(), in);
            Any inOutAny = request.add_inout_arg();
            StringSequenceHelper.insert(inOutAny, inOut);
            Any outAny = request.add_out_arg();
            outAny.type(StringSequenceHelper.type());
            request.set_return_type(StringSequenceHelper.type());
            request.invoke();
            Exception ex = request.env().exception();
            UnknownUserException uex;
            uex = (UnknownUserException) ex;
            assertTrue(uex != null);
            ExStringSequence iex;
            iex = ExStringSequenceHelper.extract(uex.except);
            assertTrue(iex.value.length == 5);
            assertTrue(iex.value[0].equals("!!!"));
            assertTrue(iex.value[1].equals("@@@"));
            assertTrue(iex.value[2].equals("###"));
            assertTrue(iex.value[3].equals("%"));
            assertTrue(iex.value[4].equals("^^"));
        }

        {
            request = ti._request("opExRecursiveStruct");
            request.exceptions().add(
                    ORBTest_Basic.ExRecursiveStructHelper.type());
            request.invoke();
            Exception ex = request.env().exception();
            UnknownUserException uex;
            uex = (UnknownUserException) ex;
            assertTrue(uex != null);
            ORBTest_Basic.ExRecursiveStruct iex;
            iex = ORBTest_Basic.ExRecursiveStructHelper.extract(uex.except);

            assertTrue(iex.us == 1);
            assertTrue(iex.rs.s.equals("test"));
            assertTrue(iex.rs.i == 2);
            assertTrue(iex.rs.rs.length == 1);
            assertTrue(iex.rs.rs[0].s.equals("ORBTest_Basic_RecursiveStruct"));
            assertTrue(iex.rs.rs[0].i == 111);
            assertTrue(iex.rs.rs[0].rs.length == 0);
        }
    }
}

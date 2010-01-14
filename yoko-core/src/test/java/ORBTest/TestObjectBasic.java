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

package ORBTest;

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
            TEST(ret == -32768);

            ti.attrShort((short) 32767);
            ret = ti.attrShort();
            TEST(ret == 32767);

            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();
            ret = ti.opShort((short) 10, inOut, out);
            TEST(ret == 30);
            TEST(inOut.value == 30);
            TEST(out.value == 30);
        }

        {
            int ret;
            ti.attrLong(-2147483647 - 1);
            ret = ti.attrLong();
            TEST(ret == -2147483647 - 1);

            ti.attrLong(2147483647);
            ret = ti.attrLong();
            TEST(ret == 2147483647);

            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();
            ret = ti.opLong(10, inOut, out);
            TEST(ret == 30);
            TEST(inOut.value == 30);
            TEST(out.value == 30);
        }

        {
            short ret;
            ti.attrUShort((short) 65535);
            ret = ti.attrUShort();
            TEST(ret == (short) 65535);

            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();
            ret = ti.opUShort((short) 10, inOut, out);
            TEST(ret == 30);
            TEST(inOut.value == 30);
            TEST(out.value == 30);
        }

        {
            int ret;
            ti.attrULong(2147483647);
            ret = ti.attrULong();
            TEST(ret == 2147483647);

            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();
            ret = ti.opULong(10, inOut, out);
            TEST(ret == 30);
            TEST(inOut.value == 30);
            TEST(out.value == 30);
        }

        {
            float ret;
            ti.attrFloat(3.40282347E+38F);
            ret = ti.attrFloat();
            TEST(ret == 3.40282347E+38F);

            ti.attrFloat(1.17549435E-38F);
            ret = ti.attrFloat();
            TEST(ret == 1.17549435E-38F);

            FloatHolder inOut = new FloatHolder(20);
            FloatHolder out = new FloatHolder();
            ret = ti.opFloat(10, inOut, out);
            TEST(ret == 30);
            TEST(inOut.value == 30);
            TEST(out.value == 30);
        }

        {
            double ret;
            ti.attrDouble(1.7976931348623157E+308);
            ret = ti.attrDouble();
            TEST(ret == 1.7976931348623157E+308);

            ti.attrDouble(2.2250738585072014E-308);
            ret = ti.attrDouble();
            TEST(ret == 2.2250738585072014E-308);

            DoubleHolder inOut = new DoubleHolder(20);
            DoubleHolder out = new DoubleHolder();
            ret = ti.opDouble(10, inOut, out);
            TEST(ret == 30);
            TEST(inOut.value == 30);
            TEST(out.value == 30);
        }

        {
            boolean ret;
            ti.attrBoolean(true);
            ret = ti.attrBoolean();
            TEST(ret == true);

            ti.attrBoolean(false);
            ret = ti.attrBoolean();
            TEST(ret == false);

            BooleanHolder inOut = new BooleanHolder(true);
            BooleanHolder out = new BooleanHolder();
            ret = ti.opBoolean(true, inOut, out);
            TEST(ret == true);
            TEST(inOut.value == true);
            TEST(out.value == true);

            inOut.value = true;
            ret = ti.opBoolean(false, inOut, out);
            TEST(ret == false);
            TEST(inOut.value == false);
            TEST(out.value == false);

            inOut.value = false;
            ret = ti.opBoolean(true, inOut, out);
            TEST(ret == false);
            TEST(inOut.value == false);
            TEST(out.value == false);
        }

        {
            char ret;
            ti.attrChar('a');
            ret = ti.attrChar();
            TEST(ret == 'a');
            ti.attrChar((char) 224);
            ret = ti.attrChar();
            TEST(ret == (char) 224);

            CharHolder inOut = new CharHolder((char) 1);
            CharHolder out = new CharHolder();
            ret = ti.opChar('a', inOut, out);
            TEST(ret == 'b');
            TEST(inOut.value == 'b');
            TEST(out.value == 'b');
        }

        {
            byte ret;
            ti.attrOctet((byte) 0xff);
            ret = ti.attrOctet();
            TEST(ret == (byte) 0xff);

            ti.attrOctet((byte) 0);
            ret = ti.attrOctet();
            TEST(ret == (byte) 0);

            ByteHolder inOut = new ByteHolder((byte) 20);
            ByteHolder out = new ByteHolder();
            ret = ti.opOctet((byte) 10, inOut, out);
            TEST(ret == 30);
            TEST(inOut.value == 30);
            TEST(out.value == 30);
        }

        {
            String ret;
            ti.attrString("Hello");
            ret = ti.attrString();
            TEST(ret.equals("Hello"));

            StringHolder inOut = new StringHolder("world!");
            StringHolder out = new StringHolder();
            ret = ti.opString("Hello, ", inOut, out);
            TEST(ret.equals("Hello, world!"));
            TEST(out.value.equals("Hello, world!"));
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
                    TEST(s.equals("abc"));
                }

                {
                    any.insert_long(3);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    int d;
                    d = ret.extract_long();
                    TEST(d == 3);
                }

                {
                    TestEnumHelper.insert(any, TestEnum.TestEnum3);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    TestEnum e;
                    e = TestEnumHelper.extract(ret);
                    TEST(e == TestEnum.TestEnum3);
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
                    TEST(vStructRet.s.equals("xyz"));
                    TEST(vStructInOut.s.equals("xyz"));
                    TEST(vStructOut.s.equals("xyz"));
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
                    TEST(fUnionRet.discriminator() == 1);
                    TEST(fUnionInOut.discriminator() == 1);
                    TEST(fUnionOut.discriminator() == 1);
                    TEST(fUnionRet.l() == 1);
                    TEST(fUnionInOut.l() == 1);
                    TEST(fUnionOut.l() == 1);
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
                    TEST(!vUnionRet.discriminator());
                    TEST(!vUnionInOut.discriminator());
                    TEST(!vUnionOut.discriminator());
                }

                {
                    ORBTest_Basic.IntfHelper.insert(any, ti);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    org.omg.CORBA.Object extract_obj = ret.extract_Object();
                    ORBTest_Basic.Intf ti2 = (ORBTest_Basic.IntfHelper
                            .extract(ret));
                    TEST(ti._hash(1000) == ti2._hash(1000));
                    TEST(ti._is_equivalent(ti2));
                    TEST(ti._hash(1111) == extract_obj._hash(1111));
                    TEST(ti._is_equivalent(extract_obj));
                    TEST(extract_obj._hash(1234) == ti2._hash(1234));
                    TEST(extract_obj._is_equivalent(ti2));
                }

                {
                    char[] char_seq = { 'a', 'b', 'c', 'd' };
                    CharSeqHelper.insert(any, char_seq);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    char[] ret_char_seq = CharSeqHelper.extract(any);
                    TEST(ret_char_seq.length == 4);
                    for (int idx = 0; idx < 4; ++idx)
                        TEST(char_seq[idx] == ret_char_seq[idx]);
                }

                {
                    char[] wchar_seq = { 'a', 'b', 'c', 'd' };
                    WCharSeqHelper.insert(any, wchar_seq);
                    ti.attrAny(any);
                    ret = ti.attrAny();
                    char[] ret_wchar_seq = WCharSeqHelper.extract(any);
                    TEST(ret_wchar_seq.length == 4);
                    for (int idx = 0; idx < 4; ++idx)
                        TEST(wchar_seq[idx] == ret_wchar_seq[idx]);
                }
            }
        }

        {
            TestEnum ret;
            ti.attrTestEnum(TestEnum.TestEnum2);
            ret = ti.attrTestEnum();
            TEST(ret == TestEnum.TestEnum2);

            ti.attrTestEnum(TestEnum.TestEnum3);
            ret = ti.attrTestEnum();
            TEST(ret == TestEnum.TestEnum3);

            TestEnumHolder inOut = new TestEnumHolder(TestEnum.TestEnum2);
            TestEnumHolder out = new TestEnumHolder();
            ret = ti.opTestEnum(TestEnum.TestEnum3, inOut, out);
            TEST(ret == TestEnum.TestEnum3);
            TEST(inOut.value == TestEnum.TestEnum3);
            TEST(out.value == TestEnum.TestEnum3);
        }

        {
            ORBTest_Basic.Intf ret;
            ti.attrIntf(ti);
            ret = ti.attrIntf();
            TEST(ret._hash(999) == ti._hash(999));
            TEST(ret._is_equivalent(ti));

            ORBTest_Basic.IntfHolder inOut = (new ORBTest_Basic.IntfHolder(
                    (ORBTest_Basic.Intf) (ti._duplicate())));
            ORBTest_Basic.IntfHolder out = new ORBTest_Basic.IntfHolder();
            ret = ti.opIntf(ti, inOut, out);
            TEST(ret._hash(1001) == ti._hash(1001));
            TEST(ret._is_equivalent(ti));
            TEST(inOut.value._hash(5000) == ti._hash(5000));
            TEST(inOut.value._is_equivalent(ti));
            TEST(out.value._hash(2000) == ti._hash(2000));
            TEST(out.value._is_equivalent(ti));
        }

        {
            FixedStruct st = new FixedStruct();
            st.s = 100;
            st.l = -100;

            FixedStruct ret;
            ti.attrFixedStruct(st);
            ret = ti.attrFixedStruct();
            TEST(ret.s == st.s);
            TEST(ret.l == st.l);

            FixedStructHolder inOut = new FixedStructHolder(new FixedStruct());
            inOut.value.s = 10000;
            inOut.value.l = 100000;
            FixedStructHolder out = new FixedStructHolder();
            ret = ti.opFixedStruct(st, inOut, out);
            TEST(ret.s == st.s);
            TEST(ret.l == st.l);
            TEST(inOut.value.s == st.s);
            TEST(out.value.l == st.l);
            TEST(inOut.value.s == st.s);
            TEST(out.value.l == st.l);
        }

        {
            VariableStruct st = new VariableStruct();
            st.s = "$$$";

            VariableStruct ret;
            ti.attrVariableStruct(st);
            ret = ti.attrVariableStruct();
            TEST(ret.s.equals(st.s));

            VariableStructHolder inOut = new VariableStructHolder(
                    new VariableStruct());
            inOut.value.s = "bla";
            VariableStructHolder out = new VariableStructHolder();
            ret = ti.opVariableStruct(st, inOut, out);
            TEST(ret.s.equals(st.s));
            TEST(inOut.value.s.equals(st.s));
            TEST(out.value.s.equals(st.s));
        }

        {
            FixedUnion un = new FixedUnion();
            un.l(1);

            FixedUnion ret;
            ti.attrFixedUnion(un);
            ret = ti.attrFixedUnion();
            TEST(ret.discriminator() == 1);
            TEST(ret.l() == 1);

            un.b((short) 999, true);
            FixedUnionHolder inOut = new FixedUnionHolder();
            inOut.value = new FixedUnion();
            inOut.value.l(100);
            FixedUnionHolder out = new FixedUnionHolder();
            ret = ti.opFixedUnion(un, inOut, out);
            TEST(ret.discriminator() == 999);
            TEST(ret.b() == true);
            TEST(out.value.discriminator() == 999);
            TEST(out.value.b() == true);
            TEST(inOut.value.discriminator() == 999);
            TEST(inOut.value.b() == true);

            FixedStruct st = new FixedStruct();
            st.s = 10101;
            st.l = -10101;
            un.st(st);
            inOut.value = new FixedUnion();
            inOut.value.l(100);
            ret = ti.opFixedUnion(un, inOut, out);
            TEST(ret.discriminator() == 3);
            TEST(ret.st().s == 10101);
            TEST(ret.st().l == -10101);
            TEST(out.value.discriminator() == 3);
            TEST(out.value.st().s == 10101);
            TEST(out.value.st().l == -10101);
            TEST(inOut.value.discriminator() == 3);
            TEST(inOut.value.st().s == 10101);
            TEST(inOut.value.st().l == -10101);
        }

        {
            VariableUnion un = new VariableUnion();
            VariableStruct st = new VariableStruct();
            st.s = "$$$";
            un.st(st);

            VariableUnion ret;
            ti.attrVariableUnion(un);
            ret = ti.attrVariableUnion();
            TEST(ret.st().s.equals("$$$"));

            un.ti(ti);
            VariableUnionHolder inOut = new VariableUnionHolder(
                    new VariableUnion());
            VariableUnionHolder out = new VariableUnionHolder();
            inOut.value.st(st);
            ret = ti.opVariableUnion(un, inOut, out);
            TEST(ret.ti()._hash(1000) == ti._hash(1000));
            TEST(ret.ti()._is_equivalent(ti));
            TEST(inOut.value.ti()._hash(5000) == ti._hash(5000));
            TEST(inOut.value.ti()._is_equivalent(ti));
            TEST(out.value.ti()._hash(2000) == ti._hash(2000));
            TEST(out.value.ti()._is_equivalent(ti));
        }

        {
            String[] seq = new String[3];
            seq[0] = "!!!";
            seq[1] = "@@@";
            seq[2] = "###";

            String[] ret;
            ti.attrStringSequence(seq);
            ret = ti.attrStringSequence();
            TEST(ret.length == 3);
            TEST(ret[0].equals("!!!"));
            TEST(ret[1].equals("@@@"));
            TEST(ret[2].equals("###"));

            StringSequenceHolder inOut = new StringSequenceHolder(new String[2]);
            inOut.value[0] = "%";
            inOut.value[1] = "^^";
            StringSequenceHolder out = new StringSequenceHolder();
            ret = ti.opStringSequence(seq, inOut, out);
            TEST(ret.length == 5);
            TEST(ret[0].equals("!!!"));
            TEST(ret[1].equals("@@@"));
            TEST(ret[2].equals("###"));
            TEST(ret[3].equals("%"));
            TEST(ret[4].equals("^^"));
            TEST(inOut.value.length == 5);
            TEST(inOut.value[0].equals("!!!"));
            TEST(inOut.value[1].equals("@@@"));
            TEST(inOut.value[2].equals("###"));
            TEST(inOut.value[3].equals("%"));
            TEST(inOut.value[4].equals("^^"));
            TEST(out.value.length == 5);
            TEST(out.value[0].equals("!!!"));
            TEST(out.value[1].equals("@@@"));
            TEST(out.value[2].equals("###"));
            TEST(out.value[3].equals("%"));
            TEST(out.value[4].equals("^^"));
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
                        TEST(ar[i][j][k] == ret[i][j][k]);
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
                        TEST(ar[i][j][k] == ret[i][j][k]);
                        TEST(ar[i][j][k] == inOut.value[i][j][k]);
                        TEST(ar[i][j][k] == out.value[i][j][k]);
                    }
        }

        {
            String[][] ar = { { "aa", "bb", "cc" }, { "AA", "BB", "CC" } };

            String[][] ret;
            ti.attrVariableArray(ar);
            ret = ti.attrVariableArray();
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++) {
                    TEST((ar[i][j].equals(ret[i][j])));
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
                    TEST(ar[i][j].equals(ret[i][j]));
                    TEST(ar[i][j].equals(inOut.value[i][j]));
                    TEST(ar[i][j].equals(out.value[i][j]));
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
                            TEST(seq[l][i][j][k] == ret[l][i][j][k]);
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

            TEST(ret.length == 7);
            TEST(inOut.value.length == 7);
            TEST(out.value.length == 7);

            for (l = 0; l < 3; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            TEST(seq[l][i][j][k] == ret[l][i][j][k]);
                            TEST(seq[l][i][j][k] == inOut.value[l][i][j][k]);
                            TEST(seq[l][i][j][k] == out.value[l][i][j][k]);
                        }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            TEST(ret[3 + l][i][j][k] == i + j + k + l);
                            TEST(inOut.value[3 + l][i][j][k] == i + j + k + l);
                            TEST(out.value[3 + l][i][j][k] == i + j + k + l);
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
                        TEST(seq[l][i][j].equals(ret[l][i][j]));
                    }

            VariableArraySequenceHolder inOut = new VariableArraySequenceHolder(
                    new String[4][2][3]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        inOut.value[l][i][j] = "***";

            VariableArraySequenceHolder out = new VariableArraySequenceHolder();

            ret = ti.opVariableArraySequence(seq, inOut, out);

            TEST(ret.length == 6);
            TEST(inOut.value.length == 6);
            TEST(out.value.length == 6);

            for (l = 0; l < 2; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        TEST(seq[l][i][j].equals(ret[l][i][j]));
                        TEST(seq[l][i][j].equals(inOut.value[l][i][j]));
                        TEST(seq[l][i][j].equals(out.value[l][i][j]));
                    }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        TEST(ret[2 + l][i][j].equals("***"));
                        TEST(inOut.value[2 + l][i][j].equals("***"));
                        TEST(out.value[2 + l][i][j].equals("***"));
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
                            TEST(seq[l][i][j][k] == ret[l][i][j][k]);
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

            TEST(ret.length == 7);
            TEST(inOut.value.length == 7);
            TEST(out.value.length == 7);

            for (l = 0; l < 3; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            TEST(seq[l][i][j][k] == ret[l][i][j][k]);
                            TEST(seq[l][i][j][k] == inOut.value[l][i][j][k]);
                            TEST(seq[l][i][j][k] == out.value[l][i][j][k]);
                        }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            TEST(ret[3 + l][i][j][k] == i + j + k + l);
                            TEST(inOut.value[3 + l][i][j][k] == i + j + k + l);
                            TEST(out.value[3 + l][i][j][k] == i + j + k + l);
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
                        TEST(seq[l][i][j].equals(ret[l][i][j]));
                    }

            VariableArrayBoundSequenceHolder inOut = new VariableArrayBoundSequenceHolder(
                    new String[4][2][3]);
            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        inOut.value[l][i][j] = "***";

            VariableArrayBoundSequenceHolder out = new VariableArrayBoundSequenceHolder();

            ret = ti.opVariableArrayBoundSequence(seq, inOut, out);

            TEST(ret.length == 6);
            TEST(inOut.value.length == 6);
            TEST(out.value.length == 6);

            for (l = 0; l < 2; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        TEST(seq[l][i][j].equals(ret[l][i][j]));
                        TEST(seq[l][i][j].equals(inOut.value[l][i][j]));
                        TEST(seq[l][i][j].equals(out.value[l][i][j]));
                    }

            for (l = 0; l < 4; l++)
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        TEST(ret[2 + l][i][j].equals("***"));
                        TEST(inOut.value[2 + l][i][j].equals("***"));
                        TEST(out.value[2 + l][i][j].equals("***"));
                    }
        }

        {
            try {
                ti.opVoidEx();
                TEST(false);
            } catch (ExVoid ex) {
            }
        }

        {
            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();

            try {
                ti.opShortEx((short) 10, inOut, out);
                TEST(false);
            } catch (ExShort ex) {
                TEST(ex.value == 30);
            }
        }

        {
            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();

            try {
                ti.opLongEx(10, inOut, out);
                TEST(false);
            } catch (ExLong ex) {
                TEST(ex.value == 30);
            }
        }

        {
            ShortHolder inOut = new ShortHolder((short) 20);
            ShortHolder out = new ShortHolder();

            try {
                ti.opUShortEx((short) 10, inOut, out);
                TEST(false);
            } catch (ExUShort ex) {
                TEST(ex.value == 30);
            }
        }

        {
            IntHolder inOut = new IntHolder(20);
            IntHolder out = new IntHolder();

            try {
                ti.opULongEx(10, inOut, out);
                TEST(false);
            } catch (ExULong ex) {
                TEST(ex.value == 30);
            }
        }

        {
            FloatHolder inOut = new FloatHolder(20);
            FloatHolder out = new FloatHolder();

            try {
                ti.opFloatEx(10, inOut, out);
                TEST(false);
            } catch (ExFloat ex) {
                TEST(ex.value == 30);
            }
        }

        {
            DoubleHolder inOut = new DoubleHolder(20);
            DoubleHolder out = new DoubleHolder();

            try {
                ti.opDoubleEx(10, inOut, out);
                TEST(false);
            } catch (ExDouble ex) {
                TEST(ex.value == 30);
            }
        }

        {
            BooleanHolder inOut = new BooleanHolder(true);
            BooleanHolder out = new BooleanHolder();

            try {
                ti.opBooleanEx(true, inOut, out);
                TEST(false);
            } catch (ExBoolean ex) {
                TEST(ex.value == true);
            }
        }

        {
            CharHolder inOut = new CharHolder((char) 1);
            CharHolder out = new CharHolder();

            try {
                ti.opCharEx('a', inOut, out);
                TEST(false);
            } catch (ExChar ex) {
                TEST(ex.value == 'b');
            }
        }

        {
            ByteHolder inOut = new ByteHolder((byte) 20);
            ByteHolder out = new ByteHolder();

            try {
                ti.opOctetEx((byte) 10, inOut, out);
                TEST(false);
            } catch (ExOctet ex) {
                TEST(ex.value == 30);
            }
        }

        {
            StringHolder inOut = new StringHolder("world!");
            StringHolder out = new StringHolder();

            try {
                ti.opStringEx("Hello, ", inOut, out);
                TEST(false);
            } catch (ExString ex) {
                TEST(ex.value.equals("Hello, world!"));
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
                TEST(false);
            } catch (ExAny ex) {
                VariableStruct vStructRet;
                vStructRet = VariableStructHelper.extract(any);
                TEST(vStructRet.s.equals("xyz"));
            }
        }

        {
            TestEnumHolder inOut = new TestEnumHolder();
            TestEnumHolder out = new TestEnumHolder();
            inOut.value = TestEnum.TestEnum2;

            try {
                ti.opTestEnumEx(TestEnum.TestEnum3, inOut, out);
                TEST(false);
            } catch (ExTestEnum ex) {
                TEST(ex.value == TestEnum.TestEnum3);
            }
        }

        if (orb_type == ORBType.ORBacus4) {
            ORBTest_Basic.IntfHolder inOut = new ORBTest_Basic.IntfHolder();
            ORBTest_Basic.IntfHolder out = new ORBTest_Basic.IntfHolder();
            inOut.value = (ORBTest_Basic.Intf) ti._duplicate();

            try {
                ti.opIntfEx(ti, inOut, out);
                TEST(false);
            } catch (ORBTest_Basic.ExIntf ex) {
                TEST(ex.value._hash(1000) == ti._hash(1000));
                TEST(ex.value._is_equivalent(ti));
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
                TEST(false);
            } catch (ExFixedStruct ex) {
                TEST(ex.value.s == st.s);
                TEST(ex.value.l == st.l);
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
                TEST(false);
            } catch (ExVariableStruct ex) {
                TEST(ex.value.s.equals(st.s));
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
                TEST(false);
            } catch (ExFixedUnion ex) {
                TEST(ex.value.discriminator() == 999);
                TEST(ex.value.b() == true);
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
                TEST(false);
            } catch (ExVariableUnion ex) {
                TEST(ex.value.ti()._hash(2000) == ti._hash(2000));
                TEST(ex.value.ti()._is_equivalent(ti));
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
                TEST(false);
            } catch (ExStringSequence ex) {
                TEST(ex.value.length == 5);
                TEST(ex.value[0].equals("!!!"));
                TEST(ex.value[1].equals("@@@"));
                TEST(ex.value[2].equals("###"));
                TEST(ex.value[3].equals("%"));
                TEST(ex.value[4].equals("^^"));
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
                TEST(false);
            } catch (ExFixedArray ex) {
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++)
                        for (k = 0; k < 4; k++) {
                            TEST(ar[i][j][k] == ex.value[i][j][k]);
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
                TEST(false);
            } catch (ExVariableArray ex) {
                for (i = 0; i < 2; i++)
                    for (j = 0; j < 3; j++) {
                        TEST(ar[i][j].equals(ex.value[i][j]));
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
                TEST(false);
            } catch (ExFixedArraySequence ex) {
                TEST(ex.value.length == 7);

                for (l = 0; l < 3; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++) {
                                TEST(seq[l][i][j][k] == ex.value[l][i][j][k]);
                            }

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++) {
                                TEST(ex.value[3 + l][i][j][k] == i + j + k + l);
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
                TEST(false);
            } catch (ExVariableArraySequence ex) {
                TEST(ex.value.length == 6);

                for (l = 0; l < 2; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++) {
                            TEST(seq[l][i][j].equals(ex.value[l][i][j]));
                        }

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++) {
                            TEST(ex.value[2 + l][i][j].equals("***"));
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
                TEST(false);
            } catch (ExFixedArrayBoundSequence ex) {
                TEST(ex.value.length == 7);

                for (l = 0; l < 3; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++)
                                TEST(seq[l][i][j][k] == ex.value[l][i][j][k]);

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            for (k = 0; k < 4; k++)
                                TEST(ex.value[3 + l][i][j][k] == i + j + k + l);
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
                TEST(false);
            } catch (ExVariableArrayBoundSequence ex) {
                TEST(ex.value.length == 6);

                for (l = 0; l < 2; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            TEST(seq[l][i][j].equals(ex.value[l][i][j]));

                for (l = 0; l < 4; l++)
                    for (i = 0; i < 2; i++)
                        for (j = 0; j < 3; j++)
                            TEST(ex.value[2 + l][i][j].equals("***"));
            }
        }
        {
            try {
                ti.opExRecursiveStruct();
                TEST(false);
            } catch (ORBTest_Basic.ExRecursiveStruct ex) {
                TEST(ex.us == 1);
                TEST(ex.rs.s.equals("test"));
                TEST(ex.rs.i == 2);
                TEST(ex.rs.rs.length == 1);
                TEST(ex.rs.rs[0].s.equals("ORBTest_Basic_RecursiveStruct"));
                TEST(ex.rs.rs[0].i == 111);
                TEST(ex.rs.rs[0].rs.length == 0);
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
            TEST(ret == (short) -32768);

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
            TEST(ret == (short) 32767);

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
            TEST(ret == 30);
            TEST(inOut == 30);
            TEST(out == 30);
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
            TEST(ret == 1.7976931348623157E+308);

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
            TEST(ret == 2.2250738585072014E-308);

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
            TEST(ret == 30);
            TEST(inOut == 30);
            TEST(out == 30);
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
            TEST(ret.equals("Hello"));

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
            TEST(ret.equals("Hello, world!"));
            TEST(out.equals("Hello, world!"));
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
            TEST(ret.length == 3);
            TEST(ret[0].equals("!!!"));
            TEST(ret[1].equals("@@@"));
            TEST(ret[2].equals("###"));

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
            TEST(ret.length == 5);
            TEST(ret[0].equals("!!!"));
            TEST(ret[1].equals("@@@"));
            TEST(ret[2].equals("###"));
            TEST(ret[3].equals("%"));
            TEST(ret[4].equals("^^"));
            TEST(inOut.length == 5);
            TEST(inOut[0].equals("!!!"));
            TEST(inOut[1].equals("@@@"));
            TEST(inOut[2].equals("###"));
            TEST(inOut[3].equals("%"));
            TEST(inOut[4].equals("^^"));
            TEST(out.length == 5);
            TEST(out[0].equals("!!!"));
            TEST(out[1].equals("@@@"));
            TEST(out[2].equals("###"));
            TEST(out[3].equals("%"));
            TEST(out[4].equals("^^"));
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
                TEST(false);
            }
            ret = request.return_value().extract_char();
            TEST(ret == 'a');

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
                TEST(false);
            }
            inOut = inOutAny.extract_char();
            out = outAny.extract_char();
            ret = request.return_value().extract_char();
            TEST(ret == 'b');
            TEST(inOut == 'b');
            TEST(out == 'b');
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
                TEST(false);
            }
            ret = VariableArrayHelper.extract(request.return_value());
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++) {
                    TEST(ar[i][j].equals(ret[i][j]));
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
                TEST(false);
            }
            ret = VariableArrayHelper.extract(request.return_value());
            inOut = VariableArrayHelper.extract(inOutAny);
            out = VariableArrayHelper.extract(outAny);
            for (i = 0; i < 2; i++)
                for (j = 0; j < 3; j++) {
                    TEST(ar[i][j].equals(ret[i][j]));
                    TEST(ar[i][j].equals(inOut[i][j]));
                    TEST(ar[i][j].equals(out[i][j]));
                }
        }

        try {
            m_orb.poll_next_response();
            TEST(false);
        } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
            // Expected
        }

        try {
            m_orb.get_next_response();
            TEST(false);
        } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
            // Expected
        } catch (org.omg.CORBA.WrongTransaction ex) {
            TEST(false);
        }

        {
            Request request;

            request = ti._request("opVoid");
            request.invoke();
            if (request.env().exception() != null)
                throw (SystemException) request.env().exception();

            try {
                request.invoke();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_oneway();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_deferred();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.poll_response();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.get_response();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            } catch (org.omg.CORBA.WrongTransaction ex) {
                TEST(false);
            }
        }

        {
            Request request;

            request = ti._request("opVoid");

            try {
                request.poll_response();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.get_response();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            } catch (org.omg.CORBA.WrongTransaction ex) {
                TEST(false);
            }

            request.send_deferred();

            try {
                request.invoke();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_oneway();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.send_deferred();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }

            try {
                request.get_response();
            } catch (org.omg.CORBA.WrongTransaction ex) {
                TEST(false);
            }

            try {
                request.poll_response();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            }
            try {
                request.get_response();
                TEST(false);
            } catch (org.omg.CORBA.BAD_INV_ORDER ex) {
                // Expected
            } catch (org.omg.CORBA.WrongTransaction ex) {
                TEST(false);
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
                    TEST(false);
                }

                short ret = request.return_value().extract_ushort();
                TEST(ret == (short) 1234);
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
                TEST(false);
            }
            inOut = inOutAny.extract_float();
            out = outAny.extract_float();
            ret = request5.return_value().extract_float();
            TEST(ret == 30);
            TEST(inOut == 30);
            TEST(out == 30);

            try {
                request2.get_response();
            } catch (WrongTransaction ex) {
                TEST(false);
            }
            ret = request2.return_value().extract_float();
            TEST(ret == 1);

            try {
                request4.get_response();
            } catch (WrongTransaction ex) {
                TEST(false);
            }
            ret = request4.return_value().extract_float();
            TEST(ret == -1);

            try {
                request1.get_response();
                request3.get_response();
            } catch (WrongTransaction ex) {
                TEST(false);
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
                TEST(dummy != null);
            } catch (UNKNOWN e) {
                // expected in JDK 1.2
            }

            request = ti._request("opVoidEx");
            request.exceptions().add(ExVoidHelper.type());
            request.invoke();
            ex = request.env().exception();
            UnknownUserException uex;
            uex = (UnknownUserException) ex;
            TEST(uex != null);
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
            TEST(uex != null);
            ExShort iex;
            iex = ExShortHelper.extract(uex.except);
            TEST(iex.value == 30);
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
            TEST(uex != null);
            ExDouble iex;
            iex = ExDoubleHelper.extract(uex.except);
            TEST(iex.value == 30);
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
            TEST(uex != null);
            ExStringSequence iex;
            iex = ExStringSequenceHelper.extract(uex.except);
            TEST(iex.value.length == 5);
            TEST(iex.value[0].equals("!!!"));
            TEST(iex.value[1].equals("@@@"));
            TEST(iex.value[2].equals("###"));
            TEST(iex.value[3].equals("%"));
            TEST(iex.value[4].equals("^^"));
        }

        {
            request = ti._request("opExRecursiveStruct");
            request.exceptions().add(
                    ORBTest_Basic.ExRecursiveStructHelper.type());
            request.invoke();
            Exception ex = request.env().exception();
            UnknownUserException uex;
            uex = (UnknownUserException) ex;
            TEST(uex != null);
            ORBTest_Basic.ExRecursiveStruct iex;
            iex = ORBTest_Basic.ExRecursiveStructHelper.extract(uex.except);

            TEST(iex.us == 1);
            TEST(iex.rs.s.equals("test"));
            TEST(iex.rs.i == 2);
            TEST(iex.rs.rs.length == 1);
            TEST(iex.rs.rs[0].s.equals("ORBTest_Basic_RecursiveStruct"));
            TEST(iex.rs.rs[0].i == 111);
            TEST(iex.rs.rs[0].rs.length == 0);
        }
    }
}

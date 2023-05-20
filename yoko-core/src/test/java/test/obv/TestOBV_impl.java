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
package test.obv;

import static org.junit.Assert.assertTrue;

public class TestOBV_impl extends TestOBVPOA {
    private org.omg.CORBA.ORB orb_;

    private TestValue value_;

    private TestValueSub valueSub_;

    private TestCustom custom_;

    private TestNode node_;

    private TestAbstract absInterface_;

    private TestAbstract absValue_;

    private TestValueInterface valueInterface_;

    private TestInterfacePOATie valueInterfaceTie_; // Java only

    private void TEST(boolean b) {
        assertTrue(b);
    }

    public TestOBV_impl(org.omg.CORBA.ORB orb,
            TestValueValueFactory valueFactory,
            TestValueSubValueFactory valueSubFactory,
            TestCustomValueFactory customFactory,
            TestNodeValueFactory nodeFactory, TestAbstract absInterface,
            TestAbstract absValue,
            TestValueInterfaceValueFactory valueInterfaceFactory) {
        orb_ = orb;
        value_ = valueFactory.create(500);
        valueSub_ = valueSubFactory.create_sub(501, "ValueSub");
        custom_ = customFactory.create((short) -99, -123456, "CustomVal",
                100.997);

        TestNode left, right, ltmp, rtmp;
        ltmp = nodeFactory.create(2);
        rtmp = nodeFactory.create(1);
        left = nodeFactory.create_lr(3, ltmp, rtmp);
        ltmp = nodeFactory.create(5);
        right = nodeFactory.create_lr(6, ltmp, null);
        node_ = nodeFactory.create_lr(10, left, right);

        absInterface_ = absInterface;
        absValue_ = absValue;

        valueInterface_ = valueInterfaceFactory.create(99);

        // Java only
        valueInterfaceTie_ = new TestInterfacePOATie(valueInterface_);
        valueInterfaceTie_._this(orb);
    }

    public java.io.Serializable get_null_valuebase() {
        return null;
    }

    public void set_null_valuebase(java.io.Serializable v) {
        TEST(v == null);
    }

    public TestValueSub get_null_valuesub() {
        return null;
    }

    public void set_null_valuesub(TestValueSub v) {
        TEST(v == null);
    }

    public TestAbsValue1 get_abs_value1() {
        return value_;
    }

    public void set_abs_value1(TestAbsValue1 v) {
        TEST(v != null);
        TestValue value = (TestValue) v;
        value.ping1();
        TEST(value.count == value_.count);
    }

    public TestAbsValue2 get_abs_value2() {
        return valueSub_;
    }

    public void set_abs_value2(TestAbsValue2 v) {
        TEST(v != null);
        TestValueSub value = (TestValueSub) v;
        value.ping1();
        value.ping2();
        TEST(value.count == valueSub_.count);
        TEST(value.name.equals(valueSub_.name));
    }

    public TestValue get_value() {
        return value_;
    }

    public void set_value(TestValue v) {
        TEST(v != null);
        v.ping1();
        TEST(v.count == value_.count);
    }

    public TestValueSub get_valuesub() {
        return valueSub_;
    }

    public void set_valuesub(TestValueSub v) {
        TEST(v != null);
        v.ping1();
        v.ping2();
        TEST(v.count == valueSub_.count);
        TEST(v.name.equals(valueSub_.name));
    }

    public TestValue get_valuesub_as_value() {
        return valueSub_;
    }

    public void set_valuesub_as_value(TestValue v) {
        TEST(v != null);
        TestValueSub value = (TestValueSub) v;
        value.ping1();
        value.ping2();
        TEST(value.count == valueSub_.count);
        TEST(value.name.equals(valueSub_.name));
    }

    public void get_two_values(TestValueHolder v1, TestValueHolder v2) {
        v1.value = value_;
        v2.value = value_;
    }

    public void set_two_values(TestValue v1, TestValue v2) {
        TEST(v1 != null);
        TEST(v2 != null);
        TEST(v1 == v2);
        v1.ping1();
        v2.ping1();
        TEST(v1.count == value_.count);
    }

    public void get_two_valuesubs_as_values(TestValueHolder v1,
            TestValueHolder v2) {
        org.omg.CORBA_2_3.ORB orb_2_3 = (org.omg.CORBA_2_3.ORB) orb_;
        TestValueSubValueFactory subFactory = (TestValueSubValueFactory) orb_2_3
                .lookup_value_factory(TestValueSubHelper.id());
        TEST(subFactory != null);

        v1.value = subFactory.create_sub(999, "ValueSub");
        v2.value = subFactory.create_sub(999, "ValueSub");
    }

    public void set_two_valuesubs_as_values(TestValue v1, TestValue v2) {
        TEST(v1 != null);
        TEST(v2 != null);
        v1.ping1();
        v2.ping1();
        TEST(v1.count == v2.count);

        TestValueSub s1 = (TestValueSub) v1;
        s1.ping2();
        TestValueSub s2 = (TestValueSub) v2;
        s2.ping2();
        TEST(s1.name.equals(s2.name));
    }

    public TestCustom get_custom() {
        return custom_;
    }

    public void set_custom(TestCustom v) {
        TEST(v != null);
        v.ping1();
        TEST(v.shortVal == custom_.shortVal);
        TEST(v.longVal == custom_.longVal);
        TEST(v.doubleVal == custom_.doubleVal);
        TEST(v.stringVal.equals(custom_.stringVal));
    }

    public TestAbsValue1 get_abs_custom() {
        return custom_;
    }

    public void set_abs_custom(TestAbsValue1 v) {
        TEST(v != null);
        v.ping1();
        TestCustom c = (TestCustom) v;
        TEST(c.shortVal == custom_.shortVal);
        TEST(c.longVal == custom_.longVal);
        TEST(c.doubleVal == custom_.doubleVal);
        TEST(c.stringVal.equals(custom_.stringVal));
    }

    public void get_node(TestNodeHolder n, org.omg.CORBA.IntHolder count) {
        n.value = node_;
        count.value = node_.compute_count();
    }

    public void set_node(TestNode v) {
        TEST(v != null);
        TEST(v.compute_count() == node_.compute_count());
    }

    public String get_string_box(String value) {
        return value;
    }

    public void set_string_box(String b, String value) {
        TEST(b != null);
        TEST(b.equals(value));
    }

    public TestULongBox get_ulong_box(int value) {
        return new TestULongBox(value);
    }

    public void set_ulong_box(TestULongBox b, int value) {
        TEST(b != null);
        TEST(b.value == value);
    }

    public TestFixStruct get_fix_struct_box(TestFixStruct value) {
        return value;
    }

    public void set_fix_struct_box(TestFixStruct b, TestFixStruct value) {
        TEST(b != null);
        TEST(b.x == value.x);
        TEST(b.y == value.y);
        TEST(b.radius == value.radius);
    }

    public TestVarStruct get_var_struct_box(TestVarStruct value) {
        return value;
    }

    public void set_var_struct_box(TestVarStruct b, TestVarStruct value) {
        TEST(b != null);
        TEST(b.name.equals(value.name));
        TEST(b.email.equals(value.email));
    }

    public TestFixUnion get_fix_union_box(TestFixUnion value) {
        return value;
    }

    public void set_fix_union_box(TestFixUnion b, TestFixUnion value) {
        TEST(b != null);

        if (value.discriminator())
            TEST(b.o() == value.o());
        else
            TEST(b.d() == value.d());
    }

    public TestVarUnion get_var_union_box(TestVarUnion value) {
        return value;
    }

    public void set_var_union_box(TestVarUnion b, TestVarUnion value) {
        TEST(b != null);

        switch (value.discriminator()) {
        case 0:
            TEST(b.s().equals(value.s()));
            break;

        case 9: {
            TestFixStruct fs1 = b.fs();
            TestFixStruct fs2 = value.fs();
            TEST(fs1.x == fs2.x);
            TEST(fs1.y == fs2.y);
            TEST(fs1.radius == fs2.radius);
            break;
        }
        }
    }

    public short[] get_anon_seq_box(int length) {
        short[] result = new short[length];
        for (int i = 0; i < length; i++)
            result[i] = (short) i;
        return result;
    }

    public void set_anon_seq_box(short[] b, int length) {
        TEST(b != null);
        TEST(b.length == length);
        for (int i = 0; i < length; i++)
            TEST(b[i] == (short) i);
    }

    public String[] get_string_seq_box(String[] value) {
        return value;
    }

    public void set_string_seq_box(String[] b, String[] value) {
        TEST(b != null);
        TEST(b.length == value.length);
        for (int i = 0; i < b.length; i++)
            TEST(b[i].equals(value[i]));
    }

    public TestAbstract get_ai_interface() {
        return absInterface_;
    }

    public void set_ai_interface(TestAbstract a) {
        if (a != null) {
            a.abstract_op();
            TestAbstractSub sub = TestAbstractSubHelper.narrow(a);
            sub.sub_op();
        }
    }

    public org.omg.CORBA.Any get_ai_interface_any() {
        org.omg.CORBA.Any result = orb_.create_any();
        TestAbstractHelper.insert(result, absInterface_);

        //
        // Test local any extraction
        //
        TestAbstract ab = TestAbstractHelper.extract(result);
        ab.abstract_op();
        TestAbstractSub sub = TestAbstractSubHelper.narrow(ab);
        sub.sub_op();

        return result;
    }

    public void set_ai_interface_any(org.omg.CORBA.Any any) {
        //
        // Test remote any extraction
        //
        org.omg.CORBA.Object obj = any.extract_Object();
        TestAbstractSub sub = TestAbstractSubHelper.narrow(obj);
        sub.abstract_op();
        sub.sub_op();
    }

    public TestAbstract get_ai_value() {
        return absValue_;
    }

    public void set_ai_value(TestAbstract a) {
        if (a != null) {
            a.abstract_op();
            java.io.Serializable vb = (java.io.Serializable) a;
            TestValueAI v = (TestValueAI) a;
            v.value_op();
            TEST(v.count == 12345);
            try {
                TestAbstractSub sub = TestAbstractSubHelper.narrow(v);
                TEST(false);
            } catch (org.omg.CORBA.BAD_PARAM ex) {
                // expected
            }
        }
    }

    public org.omg.CORBA.Any get_ai_value_any() {
        org.omg.CORBA.Any result = orb_.create_any();
        TestAbstractHelper.insert(result, absValue_);

        //
        // Test local any extraction
        //
        java.io.Serializable vb = result.extract_Value();
        TestAbstract ab = (TestAbstract) vb;
        ab.abstract_op();
        TestValueAI val = (TestValueAI) vb;
        val.value_op();

        return result;
    }

    public void set_ai_value_any(org.omg.CORBA.Any a) {
        //
        // Test remote any extraction
        //
        java.io.Serializable vb = a.extract_Value();
        TestAbstract ab = (TestAbstract) vb;
        ab.abstract_op();
        TestValueAI val = (TestValueAI) vb;
        val.value_op();
    }

    public TestTruncBase get_trunc1() {
        //
        // This test addresses several issues:
        //
        // 1) truncation
        // 2) skipping chunks and nested values during truncation
        // 3) nested value with repository ID information
        //

        TestTrunc1 t1 = new TestTrunc1_impl();
        t1.cost = (float) 1.993;
        t1.boolVal = true;
        TestValue v = new TestValue_impl();
        v.count = 999;
        t1.v = v;
        t1.shortVal = (short) 12667;
        return t1;
    }

    public TestTruncBase get_trunc2() {
        //
        // This test addresses several issues:
        //
        // 1) truncation - skipping chunks, nested values
        // 2) nested values with repository ID information
        // 3) cotermination of outer and nested values
        // 4) nested truncatable values
        // 5) indirection of repository ID information
        // 6) value indirection into truncated portion of nested value
        // 7) value indirection
        //

        TestTrunc2 t2 = new TestTrunc2_impl();
        t2.cost = (float) 5.993;

        TestTrunc1 t1 = new TestTrunc1_impl();
        t1.cost = (float) 1.993;
        t1.boolVal = true;
        TestValue v = new TestValue_impl();
        v.count = 999;
        t1.v = v;
        t1.shortVal = (short) 12667;
        t2.t = t1; // issues 2, 4

        TestValue v2 = new TestValue_impl();
        v2.count = 9999;
        t2.a = v2; // issues 2, 5

        t2.v = v; // issue 6

        t2.b = t1; // issue 7

        return t2;
    }

    public org.omg.CORBA.Any get_value_any() {
        TestValue v = get_value();
        org.omg.CORBA.Any result = orb_.create_any();
        TestValueHelper.insert(result, v);
        return result;
    }

    public org.omg.CORBA.Any get_valuesub_any() {
        TestValueSub v = get_valuesub();
        org.omg.CORBA.Any result = orb_.create_any();
        TestValueSubHelper.insert(result, v);
        return result;
    }

    public org.omg.CORBA.Any get_valuesub_as_value_any() {
        //
        // Widen TestValueSub to TestValue - the any will contain
        // the TypeCode for TestValue
        //
        TestValue v = get_valuesub();
        org.omg.CORBA.Any result = orb_.create_any();
        TestValueHelper.insert(result, v);
        return result;
    }

    public org.omg.CORBA.Any get_custom_any() {
        TestCustom v = get_custom();
        org.omg.CORBA.Any result = orb_.create_any();
        TestCustomHelper.insert(result, v);
        return result;
    }

    public org.omg.CORBA.Any get_trunc1_any() {
        TestTruncBase v = get_trunc1();
        TestTrunc1 t1 = (TestTrunc1) v;
        org.omg.CORBA.Any result = orb_.create_any();
        TestTrunc1Helper.insert(result, t1);
        return result;
    }

    public org.omg.CORBA.Any get_trunc1_as_base_any() {
        //
        // Widen TestTrunc1 to TestTruncBase - the any will contain
        // the TypeCode for TestTruncBase
        //
        TestTruncBase v = get_trunc1();
        org.omg.CORBA.Any result = orb_.create_any();
        TestTruncBaseHelper.insert(result, v);
        return result;
    }

    public org.omg.CORBA.Any get_trunc2_any() {
        TestTruncBase v = get_trunc2();
        TestTrunc2 t2 = (TestTrunc2) v;
        org.omg.CORBA.Any result = orb_.create_any();
        TestTrunc2Helper.insert(result, t2);
        return result;
    }

    public org.omg.CORBA.Any get_trunc2_as_base_any() {
        //
        // Widen TestTrunc2 to TestTruncBase - the any will contain
        // the TypeCode for TestTruncBase
        //
        TestTruncBase v = get_trunc2();
        org.omg.CORBA.Any result = orb_.create_any();
        TestTruncBaseHelper.insert(result, v);
        return result;
    }

    public void remarshal_any(org.omg.CORBA.Any any) {
        // nothing to do
    }

    public void get_two_value_anys(org.omg.CORBA.AnyHolder a1,
            org.omg.CORBA.AnyHolder a2) {
        TestValue v = get_value();
        a1.value = orb_.create_any();
        TestValueHelper.insert(a1.value, v);
        a2.value = orb_.create_any();
        TestValueHelper.insert(a2.value, v);
    }

    public void set_two_value_anys(org.omg.CORBA.Any a1, org.omg.CORBA.Any a2) {
        TestValue v1 = TestValueHelper.extract(a1);
        TestValue v2 = TestValueHelper.extract(a2);
        TEST(v1 != null);
        TEST(v2 != null);
        TEST(v1 == v2);
    }

    public TestValueInterface get_value_as_value() {
        return valueInterface_;
    }

    public TestInterface get_value_as_interface() {
        return valueInterfaceTie_._this();
    }

    public void deactivate() {
        orb_.shutdown(false);
    }
}

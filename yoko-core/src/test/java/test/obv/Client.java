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

package test.obv;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;

import test.obv.TestOBVColoPackage.*;

public class Client extends test.common.TestBase {
    static void testValue(ORB orb, TestOBV to) {
        java.io.Serializable vb;
        TestValue value, v1, v2;
        TestValueHolder v1H = new TestValueHolder();
        TestValueHolder v2H = new TestValueHolder();
        TestValueSub valueSub;
        TestValueSub pValueSub;
        TestValueSub s1;
        TestValueSub s2;
        TestAbsValue1 a1;
        TestAbsValue2 a2;
        TestNode node;
        TestNodeHolder nodeH = new TestNodeHolder();

        vb = to.get_null_valuebase();
        assertTrue(vb == null);
        to.set_null_valuebase(null);

        valueSub = to.get_null_valuesub();
        assertTrue(valueSub == null);
        to.set_null_valuesub(null);

        a1 = to.get_abs_value1();
        assertTrue(a1 != null);
        a1.ping1();
        to.set_abs_value1(a1);

        a2 = to.get_abs_value2();
        assertTrue(a2 != null);
        a2.ping2();
        to.set_abs_value2(a2);

        value = to.get_value();
        assertTrue(value != null);
        value.ping1();
        assertTrue(value.count == 500);
        to.set_value(value);

        valueSub = to.get_valuesub();
        assertTrue(valueSub != null);
        valueSub.ping1();
        valueSub.ping2();
        assertTrue(valueSub.count == 501);
        assertTrue(valueSub.name.equals("ValueSub"));
        to.set_valuesub(valueSub);

        value = to.get_valuesub_as_value();
        assertTrue(value != null);
        value.ping1();
        assertTrue(value.count == 501);
        pValueSub = (TestValueSub) value;
        pValueSub.ping2();
        assertTrue(pValueSub.name.equals("ValueSub"));
        to.set_valuesub_as_value(value);

        to.get_two_values(v1H, v2H);
        assertTrue(v1H.value != null);
        assertTrue(v2H.value != null);
        assertTrue(v1H.value == v2H.value);
        v1H.value.ping1();
        assertTrue(v1H.value.count == 500);
        to.set_two_values(v1H.value, v2H.value);

        to.get_two_valuesubs_as_values(v1H, v2H);
        assertTrue(v1H.value != null);
        assertTrue(v2H.value != null);
        v1H.value.ping1();
        v2H.value.ping1();
        assertTrue(v1H.value.count == v2H.value.count);
        s1 = (TestValueSub) v1H.value;
        s2 = (TestValueSub) v2H.value;
        s1.ping2();
        s2.ping2();
        assertTrue(s1.name.equals(s2.name));
        to.set_two_valuesubs_as_values(v1H.value, v2H.value);

        IntHolder count = new IntHolder();
        to.get_node(nodeH, count);
        assertTrue(count.value == nodeH.value.compute_count());
        to.set_node(nodeH.value);
    }

    static void testCustom(ORB orb, TestOBV to) {
        TestCustom cust;
        TestCustom pCust;
        TestAbsValue1 a1;

        cust = to.get_custom();
        assertTrue(cust != null);
        cust.ping1();
        assertTrue(cust.shortVal == -99);
        assertTrue(cust.longVal == -123456);
        assertTrue(cust.stringVal.equals("CustomVal"));
        assertTrue(cust.doubleVal == 100.997);
        to.set_custom(cust);

        a1 = to.get_abs_custom();
        assertTrue(a1 != null);
        a1.ping1();
        pCust = (TestCustom) a1;
        assertTrue(pCust.shortVal == -99);
        assertTrue(pCust.longVal == -123456);
        assertTrue(pCust.stringVal.equals("CustomVal"));
        assertTrue(pCust.doubleVal == 100.997);
        to.set_abs_custom(a1);
    }

    static void testValueBox(ORB orb, TestOBV to) {
        int i;

        String sb;
        sb = to.get_string_box("hi there");
        assertTrue(sb != null);
        assertTrue(sb.equals("hi there"));
        sb = "bye now";
        to.set_string_box(sb, "bye now");

        TestULongBox ub;
        ub = to.get_ulong_box(999);
        assertTrue(ub != null);
        assertTrue(ub.value == 999);
        ub = new TestULongBox(77777);
        to.set_ulong_box(ub, 77777);

        TestFixStruct fsb;
        TestFixStruct fs = new TestFixStruct();
        fs.x = 111;
        fs.y = 222;
        fs.radius = 3.33;
        fsb = to.get_fix_struct_box(fs);
        assertTrue(fsb != null);
        assertTrue(fsb.x == fs.x);
        assertTrue(fsb.y == fs.y);
        assertTrue(fsb.radius == fs.radius);
        fsb = new TestFixStruct(fs.x, fs.y, fs.radius);
        to.set_fix_struct_box(fsb, fs);

        TestVarStruct vsb;
        TestVarStruct vs = new TestVarStruct();
        vs.name = "Joe Bob Briggs";
        vs.email = "jbb@cheese.com";
        vsb = to.get_var_struct_box(vs);
        assertTrue(vsb != null);
        assertTrue(vsb.name.equals(vs.name));
        assertTrue(vsb.email.equals(vs.email));
        vsb = new TestVarStruct(vs.name, vs.email);
        to.set_var_struct_box(vsb, vs);

        TestFixUnion fub;
        TestFixUnion fu = new TestFixUnion();
        fu.o((byte) 55);
        fub = to.get_fix_union_box(fu);
        assertTrue(fub != null);
        assertTrue(fub.o() == (byte) 55);
        fu.d(99.88);
        fub = new TestFixUnion();
        fub.d(fu.d());
        to.set_fix_union_box(fub, fu);

        TestVarUnion vub;
        TestVarUnion vu = new TestVarUnion();
        vu.s("howdy");
        vub = to.get_var_union_box(vu);
        assertTrue(vub != null);
        assertTrue(vub.s().equals("howdy"));
        vu.fs(fs);
        vub = new TestVarUnion();
        vub.fs(vu.fs());
        to.set_var_union_box(vub, vu);

        short[] asb;
        asb = to.get_anon_seq_box(10);
        assertTrue(asb != null);
        assertTrue(asb.length == 10);
        for (i = 0; i < asb.length; i++)
			assertTrue(asb[i] == (short) i);
        to.set_anon_seq_box(asb, 10);

        String[] ssb;
        String[] ss = new String[5];
        for (i = 0; i < 5; i++) {
            ss[i] = "s" + i;
        }
        ssb = to.get_string_seq_box(ss);
        assertTrue(ssb != null);
        assertTrue(ssb.length == ss.length);
        for (i = 0; i < ssb.length; i++)
			assertTrue(ssb[i].equals(ss[i]));
        to.set_string_seq_box(ssb, ss);

        TestStringBoxStruct stringbox_struct = new TestStringBoxStruct();

        //
        // Different values.
        //
        stringbox_struct.a = new String("foo");
        stringbox_struct.b = new String("bar");
        Any test_any = orb.create_any();
        TestStringBoxStructHelper.insert(test_any, stringbox_struct);

        TestStringBoxStruct ex_stringbox_struct = null;
        ex_stringbox_struct = TestStringBoxStructHelper.extract(test_any);

        assertTrue(ex_stringbox_struct.a.equals(stringbox_struct.a));
        assertTrue(ex_stringbox_struct.b.equals(stringbox_struct.b));

        //
        // Double check against constant values in case something happened
        // to the original instance.
        //
        assertTrue(ex_stringbox_struct.a.equals("foo"));
        assertTrue(ex_stringbox_struct.b.equals("bar"));

        //
        // Identical values. This tests a bug in ValueReader that
        // prevented the proper resolving of indirections within a
        // collection of multiple boxed value types that did not involve
        // recursive structures.
        //
        stringbox_struct.a = new String("foo");
        stringbox_struct.b = new String("foo");
        TestStringBoxStructHelper.insert(test_any, stringbox_struct);

        ex_stringbox_struct = TestStringBoxStructHelper.extract(test_any);

        assertTrue(ex_stringbox_struct.a.equals(stringbox_struct.a));
        assertTrue(ex_stringbox_struct.b.equals(stringbox_struct.b));

        //
        // Double check against constant values in case something happened
        // to the original instance.
        //
        assertTrue(ex_stringbox_struct.a.equals("foo"));
        assertTrue(ex_stringbox_struct.b.equals("foo"));
    }

    static void testCollocated(ORB orb, TestOBV to)
            throws org.omg.CORBA.UserException {
        //
        // Resolve Root POA
        //
        POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

        //
        // Activate the POA manager
        //
        POAManager manager = poa.the_POAManager();
        manager.activate();

        TestValue v1, vr;
        TestValueHolder v2 = new TestValueHolder(), v3 = new TestValueHolder();
        TestOBVColo_impl coloImpl = new TestOBVColo_impl();
        TestOBVColo colo = coloImpl._this(orb);

        v1 = to.get_value();
        assertTrue(v1 != null);

        //
        // Test: valuetype arguments
        //

        v1.count = 111;
        colo.set_expected_count(111);

        colo.test_value_attribute(null);
        vr = colo.test_value_attribute();
        assertTrue(vr == null);

        vr = colo.test_value_op(null, v2, v3);
        assertTrue(vr == null);
        assertTrue(v2.value == null);
        assertTrue(v3.value == null);

        colo.test_value_attribute(v1);
        assertTrue(v1.count == 111);

        v1.count = 222;
        colo.set_expected_count(222);

        v2.value = v1;
        vr = colo.test_value_op(v1, v2, v3);
        assertTrue(vr == v2.value);
        assertTrue(v3.value == v2.value);
        assertTrue(v1.count == 222);

        //
        // Test: struct arguments containing valuetypes
        //

        SV s1 = new SV(), sr;
        SVHolder s2 = new SVHolder(), s3 = new SVHolder();

        s1.str = "hi";
        s1.val = v1;
        s1.val.count = 111;
        colo.set_expected_count(111);

        colo.test_value_struct_attribute(s1);
        assertTrue(s1.val.count == 111);
        sr = colo.test_value_struct_attribute();
        assertTrue(sr.val == null);

        s1.val = null;
        s2.value = new SV(s1.str, s1.val);
        sr = colo.test_value_struct_op(s1, s2, s3);
        assertTrue(sr.val == null);
        assertTrue(s2.value.val == null);
        assertTrue(s3.value.val == null);

        s1.val = v1;
        s1.val.count = 222;
        colo.set_expected_count(222);

        s2.value = new SV(s1.str, s1.val);
        sr = colo.test_value_struct_op(s1, s2, s3);
        assertTrue(sr.val == s2.value.val);
        assertTrue(s3.value.val == s2.value.val);
        assertTrue(s1.val.count == 222);

        //
        // Test: union arguments containing valuetypes
        //

        UV u1 = new UV(), ur;
        UVHolder u2 = new UVHolder(), u3 = new UVHolder();

        u1.val(v1);
        u1.val().count = 111;
        colo.set_expected_count(111);

        colo.test_value_union_attribute(u1);
        assertTrue(u1.val().count == 111);
        ur = colo.test_value_union_attribute();
        assertTrue(ur.val() == null);

        u1.val(null);
        u2.value = new UV();
        u2.value.val(null);
        ur = colo.test_value_union_op(u1, u2, u3);
        assertTrue(ur.val() == null);
        assertTrue(u2.value.val() == null);
        assertTrue(u3.value.val() == null);

        u1.val(v1);
        u1.val().count = 222;
        colo.set_expected_count(222);

        u2.value = new UV();
        u2.value.val(u1.val());
        ur = colo.test_value_union_op(u1, u2, u3);
        assertTrue(ur.val() == u2.value.val());
        assertTrue(u3.value.val() == u2.value.val());
        assertTrue(u1.val().count == 222);

        //
        // Test: sequence arguments containing valuetypes
        //

        TestValue[] seq1, seqr;
        VSeqHolder seq2 = new VSeqHolder(), seq3 = new VSeqHolder();

        v1.count = 111;
        seq1 = new TestValue[3];
        seq1[0] = v1;
        seq1[1] = v1;
        seq1[2] = v1;
        colo.set_expected_count(111);

        colo.test_value_seq_attribute(seq1);
        assertTrue(v1.count == 111);
        seqr = colo.test_value_seq_attribute();
        for (int i = 0; i < seqr.length; i++)
			assertTrue(seqr[i] == null);

        seq1[0] = null;
        seq1[1] = null;
        seq1[2] = null;
        seq2.value = new TestValue[3];
        seqr = colo.test_value_seq_op(seq1, seq2, seq3);
        for (int i = 0; i < seqr.length; i++)
			assertTrue(seqr[i] == null);
        for (int i = 0; i < seq2.value.length; i++)
			assertTrue(seq2.value[i] == null);
        for (int i = 0; i < seq3.value.length; i++)
			assertTrue(seq3.value[i] == null);

        v1.count = 222;
        seq1[0] = v1;
        seq1[1] = v1;
        seq1[2] = v1;
        colo.set_expected_count(222);

        seq2.value = new TestValue[3];
        System.arraycopy(seq1, 0, seq2.value, 0, seq1.length);
        seqr = colo.test_value_seq_op(seq1, seq2, seq3);
        assertTrue(v1.count == 222);
        assertTrue(seqr.length == seq1.length);
        assertTrue(seq2.value.length == seq1.length);
        assertTrue(seq3.value.length == seq1.length);
        for (int i = 0; i < seq2.value.length; i++) {
            assertTrue(seq2.value[i] == seq2.value[0]);
            assertTrue(seqr[i] == seq2.value[0]);
            assertTrue(seq3.value[i] == seq2.value[0]);
        }

        //
        // Test: abstract interface arguments
        //

        TestAbstract abstractInterface = to.get_ai_interface();
        TestAbstract abstractValue = to.get_ai_value();
        TestValueAI vai = (TestValueAI) abstractValue;
        assertTrue(vai != null);

        colo.test_abstract_attribute(null);
        colo.test_abstract_op(null);

        colo.test_abstract_attribute(abstractInterface);
        colo.test_abstract_op(abstractInterface);

        vai.count = 333;
        colo.set_expected_count(333);
        colo.test_abstract_attribute(abstractValue);
        assertTrue(vai.count == 333);

        vai.count = 444;
        colo.set_expected_count(444);
        colo.test_abstract_op(abstractValue);
        assertTrue(vai.count == 444);
    }

    static void testAbstract(ORB orb, TestOBV to) {
        org.omg.CORBA.Object obj;
        TestAbstract ai;
        TestAbstractSub sub;
        java.io.Serializable vb;
        TestValueAI v;
        Any any;

        ai = to.get_ai_interface();
        assertTrue(ai != null);
        obj = (org.omg.CORBA.Object) ai;
        ai.abstract_op();
        sub = TestAbstractSubHelper.narrow(ai);
        sub.sub_op();
        to.set_ai_interface(ai);

        any = to.get_ai_interface_any();
        obj = any.extract_Object();
        assertTrue(obj != null);
        sub = TestAbstractSubHelper.narrow(obj);
        sub.abstract_op();
        sub.sub_op();
        to.set_ai_interface_any(any);

        ai = to.get_ai_value();
        vb = (java.io.Serializable) ai;
        ai.abstract_op();
        v = (TestValueAI) ai;
        assertTrue(v.count == 12345);
        to.set_ai_value(ai);
    }

    static void testTruncated(ORB orb, TestOBV to) {
        TestTruncBase truncBase;
        TestTrunc1 trunc1;
        TestTrunc2 trunc2;
        TestAbsValue1 a;
        TestValue v;
        org.omg.CORBA_2_3.ORB orb_2_3 = (org.omg.CORBA_2_3.ORB) orb;

        //
        // With factory installed, we should be able to downcast to TestTrunc1
        //
        TestTrunc1Factory_impl.install(orb);
        truncBase = to.get_trunc1();
        trunc1 = (TestTrunc1) truncBase;
        assertTrue(trunc1.cost > 1.99 && trunc1.cost < 2.0);
        assertTrue(trunc1.boolVal == true);
        a = trunc1.v;
        assertTrue(a != null);
        v = (TestValue) a;
        assertTrue(v.count == 999);
        assertTrue(trunc1.shortVal == 12667);

        //
        // With factory removed, we should not be able to downcast to
        // TestTrunc1
        //
        orb_2_3.unregister_value_factory(TestTrunc1Helper.id());
        truncBase = to.get_trunc1();
        try {
            trunc1 = (TestTrunc1) truncBase;
            assertTrue(false);
        } catch (ClassCastException ex) {
            // expected
        }
        assertTrue(truncBase.cost > 1.99 && truncBase.cost < 2.0);

        //
        // With factories installed, we should be able to downcast to
        // TestTrunc2
        //
        TestTrunc1Factory_impl.install(orb);
        TestTrunc2Factory_impl.install(orb);
        truncBase = to.get_trunc2();
        trunc2 = (TestTrunc2) truncBase;
        assertTrue(trunc2.cost > 5.99 && trunc2.cost < 6.0);
        trunc1 = (TestTrunc1) trunc2.t;
        assertTrue(trunc1.cost > 1.99 && trunc1.cost < 2.0);
        assertTrue(trunc1.boolVal == true);
        a = trunc1.v;
        assertTrue(a != null);
        v = (TestValue) a;
        assertTrue(v.count == 999);
        assertTrue(trunc1.shortVal == 12667);
        a = trunc2.a;
        assertTrue(a != null);
        v = (TestValue) a;
        assertTrue(v.count == 9999);
        assertTrue(trunc2.v == trunc1.v); // indirection
        assertTrue(trunc2.b == trunc1); // indirection

        //
        // Without a factory for TestTrunc1, some nested values of TestTrunc2
        // will be truncated
        //
        orb_2_3.unregister_value_factory(TestTrunc1Helper.id());
        truncBase = to.get_trunc2();
        trunc2 = (TestTrunc2) truncBase;
        assertTrue(trunc2.cost > 5.99 && trunc2.cost < 6.0);
        assertTrue(trunc2.t != null);
        try {
            trunc1 = (TestTrunc1) trunc2.t;
            assertTrue(false);
        } catch (ClassCastException ex) {
            // expected
        }
        assertTrue(trunc2.t.cost > 1.99 && trunc2.t.cost < 2.0);
        a = trunc2.a;
        assertTrue(a != null);
        v = (TestValue) a;
        assertTrue(v.count == 9999);
        assertTrue(trunc2.v != null); // indirection
        assertTrue(trunc2.v.count == 999); // indirection
        assertTrue(trunc2.b == trunc2.t); // indirection

        //
        // With factory removed, we should not be able to downcast to
        // TestTrunc2
        //
        TestTrunc1Factory_impl.install(orb);
        orb_2_3.unregister_value_factory(TestTrunc2Helper.id());
        truncBase = to.get_trunc2();
        try {
            trunc2 = (TestTrunc2) truncBase;
            assertTrue(false);
        } catch (ClassCastException ex) {
            // expected
        }
        assertTrue(truncBase.cost > 5.99 && truncBase.cost < 6.0);

        //
        // Leave factories in original state
        //
        orb_2_3.unregister_value_factory(TestTrunc1Helper.id());
    }

    static void testAny(ORB orb, TestOBV to) {
        TestValue v1;
        TestValue v2;
        TestValueSub sub;
        TestCustom cust;
        TestTruncBase base;
        TestTrunc1 t1;
        TestTrunc2 t2;
        TestValueAI ai;
        Any any = orb.create_any();
        Any av;
        org.omg.CORBA_2_3.ORB orb_2_3 = (org.omg.CORBA_2_3.ORB) orb;
        java.lang.Object ab;
        java.io.Serializable vb;
        TestAbstract tab;
        org.omg.CORBA.Object obj;

        //
        // Test simple valuetype
        //

        //
        // First, remove factory so that the TypeCode is used to
        // unmarshal the data. Then send the any back to the server to
        // remarshal the data (again using the TypeCode).
        //
        orb_2_3.unregister_value_factory(TestValueHelper.id());
        av = to.get_value_any();
        to.remarshal_any(av);

        //
        // We cannot extract without a factory installed
        //
        try {
            v2 = TestValueHelper.extract(av);
            assertTrue(false);
        } catch (MARSHAL ex) {
            // expected
        }

        //
        // Install the factory, remarshal again, and extract the value
        //
        TestValueFactory_impl.install(orb);
        to.remarshal_any(av); // uses factory instead of TypeCode
        v2 = TestValueHelper.extract(av);
        assertTrue(v2 != null);
        assertTrue(v2.count == 500);

        //
        // Test simple valuetype inheritance
        //

        //
        // First, remove factory so that the TypeCode is used to
        // unmarshal the data. Then send the any back to the server to
        // remarshal the data (again using the TypeCode).
        //
        orb_2_3.unregister_value_factory(TestValueSubHelper.id());
        av = to.get_valuesub_any();
        to.remarshal_any(av);

        //
        // Install the factory, remarshal again, and extract the value
        //
        TestValueSubFactory_impl.install(orb);
        to.remarshal_any(av); // uses factory instead of TypeCode
        sub = TestValueSubHelper.extract(av);
        assertTrue(sub != null);
        assertTrue(sub.count == 501);
        assertTrue(sub.name.equals("ValueSub"));

        //
        // Obtain an any whose TypeCode is TestValue, but whose value is
        // TestValueSub. This any cannot be unmarshalled unless the
        // factory for TestValueSub is present.
        //
        orb_2_3.unregister_value_factory(TestValueSubHelper.id());
        try {
            av = to.get_valuesub_as_value_any();
            assertTrue(false);
        } catch (MARSHAL ex) {
            // expected
        }
        TestValueSubFactory_impl.install(orb);
        av = to.get_valuesub_as_value_any();
        v2 = TestValueHelper.extract(av);
        assertTrue(v2 != null);
        sub = (TestValueSub) v2;
        assertTrue(sub.count == 501);
        assertTrue(sub.name.equals("ValueSub"));

        //
        // Test custom valuetype
        //

        //
        // A custom valuetype cannot be unmarshalled in an any without
        // the factory
        //
        orb_2_3.unregister_value_factory(TestCustomHelper.id());
        try {
            av = to.get_custom_any();
            assertTrue(false);
        } catch (MARSHAL ex) {
            // expected
        }
        TestCustomFactory_impl.install(orb);
        av = to.get_custom_any();
        to.remarshal_any(av);
        cust = TestCustomHelper.extract(av);
        assertTrue(cust != null);
        assertTrue(cust.shortVal == (short) -99);
        assertTrue(cust.longVal == -123456);
        assertTrue(cust.stringVal.equals("CustomVal"));
        assertTrue(cust.doubleVal == 100.997);

        //
        // Simple tests for truncatable valuetypes
        //
        // Note: Factories are not registered yet
        //
        // orb_2_3.unregister_value_factory(TestTrunc1Helper.id());
        // orb_2_3.unregister_value_factory(TestTrunc2Helper.id());
        av = to.get_trunc1_any();
        to.remarshal_any(av);
        //
        // The TestTrunc2 value returned by the server cannot be unmarshalled
        // without a factory because it uses indirection into a truncated
        // portion of another value
        //
        av = to.get_trunc2_any();
        try {
            t2 = TestTrunc2Helper.extract(av);
            assertTrue(false);
        } catch (BAD_OPERATION ex) {
            // expected
        }

        TestTrunc1Factory_impl.install(orb);
        TestTrunc2Factory_impl.install(orb);

        av = to.get_trunc1_any();
        to.remarshal_any(av);
        av = to.get_trunc2_any();
        to.remarshal_any(av);

        //
        // Test truncation
        //

        //
        // Request a TestTrunc1 value with the TestTruncBase TypeCode.
        // By removing the factories, the value will be truncated to
        // TestTruncBase when the any is unmarshalled.
        //
        orb_2_3.unregister_value_factory(TestTruncBaseHelper.id());
        orb_2_3.unregister_value_factory(TestTrunc1Helper.id());
        av = to.get_trunc1_as_base_any();
        to.remarshal_any(av);
        TestTruncBaseFactory_impl.install(orb);
        TestTrunc1Factory_impl.install(orb);
        base = TestTruncBaseHelper.extract(av);
        assertTrue(base != null);
        assertTrue(base.cost > 1.99 && base.cost < 2.0);
        try {
            // this should fail due to truncation
            t1 = (TestTrunc1) base;
            assertTrue(false);
        } catch (ClassCastException ex) {
            // expected
        }

        //
        // Things should work fine with the factories installed
        //
        av = to.get_trunc1_as_base_any();
        to.remarshal_any(av);
        base = TestTruncBaseHelper.extract(av);
        assertTrue(base != null);
        t1 = (TestTrunc1) base;
        assertTrue(t1.cost > 1.99 && t1.cost < 2.0);
        assertTrue(t1.boolVal == true);
        assertTrue(t1.shortVal == (short) 12667);

        //
        // Request a TestTrunc2 value with the TestTruncBase TypeCode.
        // By removing the factories, the value will be truncated to
        // TestTruncBase when the any is unmarshalled.
        //
        orb_2_3.unregister_value_factory(TestTruncBaseHelper.id());
        orb_2_3.unregister_value_factory(TestTrunc1Helper.id());
        orb_2_3.unregister_value_factory(TestTrunc2Helper.id());
        av = to.get_trunc2_as_base_any();
        to.remarshal_any(av);
        TestTruncBaseFactory_impl.install(orb);
        TestTrunc1Factory_impl.install(orb);
        TestTrunc2Factory_impl.install(orb);
        base = TestTruncBaseHelper.extract(av);
        assertTrue(base != null);
        assertTrue(base.cost > 5.99 && base.cost < 6.0);
        try {
            // this should fail due to truncation
            t2 = (TestTrunc2) base;
            assertTrue(false);
        } catch (ClassCastException ex) {
            // expected
        }

        //
        // Things should work fine with the factories installed
        //
        av = to.get_trunc2_as_base_any();
        to.remarshal_any(av);
        base = TestTruncBaseHelper.extract(av);
        assertTrue(base != null);
        t2 = (TestTrunc2) base;
        assertTrue(t2.cost > 5.99 && t2.cost < 6.0);

        //
        // Request a TestTrunc2 value with the TestTruncBase TypeCode.
        // By removing the factory for TestTrunc2, the value will be
        // truncated to TestTruncBase (using the factory for TestTruncBase)
        // when the any is unmarshalled.
        //
        orb_2_3.unregister_value_factory(TestTrunc2Helper.id());
        av = to.get_trunc2_as_base_any();
        to.remarshal_any(av);
        base = TestTruncBaseHelper.extract(av);
        assertTrue(base != null);
        assertTrue(base.cost > 5.99 && base.cost < 6.0);
        try {
            t2 = (TestTrunc2) base;
            assertTrue(false);
        } catch (ClassCastException ex) {
            // expected
        }

        //
        // Request a TestTrunc2 value with the TestTrunc2 TypeCode.
        // Without the factory for TestTrunc2, the value will be
        // truncated to TestTruncBase (using the factory for TestTruncBase)
        // when the any is unmarshalled. It must be possible to use
        // TestTruncBaseHelper.extract() on this any.
        //
        av = to.get_trunc2_any();
        base = TestTruncBaseHelper.extract(av);
        assertTrue(base != null);
        assertTrue(base.cost > 5.99 && base.cost < 6.0);
        try {
            t2 = (TestTrunc2) base;
            assertTrue(false);
        } catch (ClassCastException ex) {
            // expected
        }

        //
        // Leave factories in original state
        //
        orb_2_3.unregister_value_factory(TestTrunc1Helper.id());

        //
        // Request an abstract interface representing a valuetype
        //
        av = to.get_ai_value_any();
        tab = TestAbstractHelper.extract(av);
        assertTrue(tab != null);
        tab.abstract_op();
        ai = (TestValueAI) tab;
        ai.value_op();
        to.set_ai_value_any(av);

        //
        // Ensure that value sharing works across anys
        //
        AnyHolder a1 = new AnyHolder();
        AnyHolder a2 = new AnyHolder();
        to.get_two_value_anys(a1, a2);
        v1 = TestValueHelper.extract(a1.value);
        v2 = TestValueHelper.extract(a2.value);
        assertTrue(v1 != null);
        assertTrue(v2 != null);
        assertTrue(v1 == v2);
        to.set_two_value_anys(a1.value, a2.value);
    }

    static void testSupported(ORB orb, TestOBV to) {
        TestValueInterface val = to.get_value_as_value();
        val.value_op();
        assertTrue(val.get_count() == val.count);

        TestInterface i = to.get_value_as_interface();
        assertTrue(val.count == i.get_count());
    }

    public static int run(ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        org.omg.CORBA_2_3.ORB orb_2_3 = (org.omg.CORBA_2_3.ORB) orb;

        //
        // Get TestOBV
        //
        org.omg.CORBA.Object obj = orb.string_to_object("relfile:TestOBV.ref");
        if (obj == null) {
            System.err.println("cannot read IOR from TestOBV.ref");
            return 1;
        }

        TestOBV to = TestOBVHelper.narrow(obj);
        assertTrue(to != null);

        //
        // Install value factories
        //
        TestValueFactory_impl.install(orb);
        TestValueSubFactory_impl.install(orb);
        TestTruncBaseFactory_impl.install(orb);
        TestCustomFactory_impl.install(orb);
        TestNodeFactory_impl.install(orb);
        TestValueAIFactory_impl.install(orb);
        TestValueInterfaceFactory_impl.install(orb);

        //
        // Install valuebox factories
        //
        ValueBoxFactories.install(orb);

        //
        // Run tests
        //

        System.out.print("Testing valuetypes... ");
        System.out.flush();
        testValue(orb, to);
        System.out.println("Done!");

        System.out.print("Testing custom marshalling... ");
        System.out.flush();
        testCustom(orb, to);
        System.out.println("Done!");

        System.out.print("Testing value boxes... ");
        System.out.flush();
        testValueBox(orb, to);
        System.out.println("Done!");

        System.out.print("Testing collocated valuetypes... ");
        System.out.flush();
        testCollocated(orb, to);
        System.out.println("Done!");

        System.out.print("Testing abstract interfaces... ");
        System.out.flush();
        testAbstract(orb, to);
        System.out.println("Done!");

        System.out.print("Testing truncatable valuetypes... ");
        System.out.flush();
        testTruncated(orb, to);
        System.out.println("Done!");

        System.out.print("Testing valuetypes with any... ");
        System.out.flush();
        testAny(orb, to);
        System.out.println("Done!");

        System.out.print("Testing supported interfaces... ");
        System.out.flush();
        testSupported(orb, to);
        System.out.println("Done!");

        to.deactivate();

        return 0;
    }

    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

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

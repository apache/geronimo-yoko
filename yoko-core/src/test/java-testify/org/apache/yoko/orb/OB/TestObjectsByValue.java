/*
 * Copyright 2023 IBM Corporation and others.
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
package org.apache.yoko.orb.OB;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnyHolder;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;
import test.obv.TestAbsValue1;
import test.obv.TestAbsValue2;
import test.obv.TestAbstract;
import test.obv.TestAbstractHelper;
import test.obv.TestAbstractSub;
import test.obv.TestAbstractSubHelper;
import test.obv.TestAbstractSub_impl;
import test.obv.TestCustom;
import test.obv.TestCustomFactory_impl;
import test.obv.TestCustomHelper;
import test.obv.TestCustomValueFactory;
import test.obv.TestFixStruct;
import test.obv.TestFixUnion;
import test.obv.TestInterface;
import test.obv.TestNode;
import test.obv.TestNodeFactory_impl;
import test.obv.TestNodeHolder;
import test.obv.TestNodeValueFactory;
import test.obv.TestOBV;
import test.obv.TestOBVColo;
import test.obv.TestOBVColoPackage.SV;
import test.obv.TestOBVColoPackage.SVHolder;
import test.obv.TestOBVColoPackage.UV;
import test.obv.TestOBVColoPackage.UVHolder;
import test.obv.TestOBVColoPackage.VSeqHolder;
import test.obv.TestOBVColo_impl;
import test.obv.TestOBVHelper;
import test.obv.TestOBV_impl;
import test.obv.TestStringBoxStruct;
import test.obv.TestStringBoxStructHelper;
import test.obv.TestTrunc1;
import test.obv.TestTrunc1Factory_impl;
import test.obv.TestTrunc1Helper;
import test.obv.TestTrunc2;
import test.obv.TestTrunc2Factory_impl;
import test.obv.TestTrunc2Helper;
import test.obv.TestTruncBase;
import test.obv.TestTruncBaseFactory_impl;
import test.obv.TestTruncBaseHelper;
import test.obv.TestULongBox;
import test.obv.TestValue;
import test.obv.TestValueAI;
import test.obv.TestValueAIFactory_impl;
import test.obv.TestValueAIValueFactory;
import test.obv.TestValueFactory_impl;
import test.obv.TestValueHelper;
import test.obv.TestValueHolder;
import test.obv.TestValueInterface;
import test.obv.TestValueInterfaceFactory_impl;
import test.obv.TestValueInterfaceValueFactory;
import test.obv.TestValueSub;
import test.obv.TestValueSubFactory_impl;
import test.obv.TestValueSubHelper;
import test.obv.TestValueSubValueFactory;
import test.obv.TestValueValueFactory;
import test.obv.TestVarStruct;
import test.obv.TestVarUnion;
import test.obv.ValueBoxFactories;
import testify.bus.Bus;
import testify.bus.key.StringKey;
import testify.iiop.annotation.ConfigureOrb;
import testify.iiop.annotation.ConfigureServer;

import static org.junit.Assert.assertTrue;
import static testify.iiop.annotation.ConfigureServer.Separation.INTER_PROCESS;

@ConfigureServer(separation = INTER_PROCESS)
public class TestObjectsByValue {
    @ConfigureServer(serverOrb = @ConfigureOrb(args = "-OAthreaded"), clientOrb = @ConfigureOrb(args = "-ORBThreaded"))
    public static class TestObjectsByValueThreaded extends TestObjectsByValue {}
    @ConfigureServer(serverOrb = @ConfigureOrb(args = "-OAthread_per_client"), clientOrb = @ConfigureOrb(args = "-ORBThreaded"))
    public static class TestObjectsByValueThreadPerClient extends TestObjectsByValue {}
    @ConfigureServer(serverOrb = @ConfigureOrb(args = "-OAthread_per_request"), clientOrb = @ConfigureOrb(args = "-ORBThreaded"))
    public static class TestObjectsByValueThreadPerRequest extends TestObjectsByValue {}
    @ConfigureServer(serverOrb = @ConfigureOrb(args = {"-OAthread_pool", "10"}), clientOrb = @ConfigureOrb(args = "-ORBThreaded"))
    public static class TestObjectsByValueThreadPool extends TestObjectsByValue {}

    private static TestOBV stub;

    enum Ior implements StringKey {TEST_OBV}

    @ConfigureServer.BeforeServer
    public static void installValueFactoriesOnServer(ORB orb, Bus bus) {
        // Install value factories
        TestValueValueFactory valueFactory = TestValueFactory_impl.install(orb);
        TestValueSubValueFactory valueSubFactory = TestValueSubFactory_impl.install(orb);
        TestTruncBaseFactory_impl.install(orb);
        TestTrunc1Factory_impl.install(orb);
        TestTrunc2Factory_impl.install(orb);
        TestCustomValueFactory customFactory = TestCustomFactory_impl.install(orb);
        TestNodeValueFactory nodeFactory = TestNodeFactory_impl.install(orb);
        TestValueAIValueFactory valueAIFactory = TestValueAIFactory_impl.install(orb);
        TestValueInterfaceValueFactory valueInterfaceFactory = TestValueInterfaceFactory_impl.install(orb);

        // Install valuebox factories
        ValueBoxFactories.install(orb);

        // Create implementation objects
        TestAbstractSub_impl absSubImpl = new TestAbstractSub_impl();
        TestAbstract absInterface = absSubImpl._this(orb);
        TestValueAI absValue = valueAIFactory.create(12345);

        TestOBV_impl i = new TestOBV_impl(orb, valueFactory, valueSubFactory, customFactory, nodeFactory, absInterface, absValue, valueInterfaceFactory);
        TestOBV p = i._this(orb);
        String ior = orb.object_to_string(p);
        bus.put(Ior.TEST_OBV, ior);
    }

    @BeforeAll
    public static void getStubAndInstallValueFactoriesOnClient(ORB orb, Bus bus) {
        String ior = bus.get(Ior.TEST_OBV);
        stub = TestOBVHelper.narrow(orb.string_to_object(ior));
        // Install value factories
        TestValueFactory_impl.install(orb);
        TestValueSubFactory_impl.install(orb);
        TestTruncBaseFactory_impl.install(orb);
        TestCustomFactory_impl.install(orb);
        TestNodeFactory_impl.install(orb);
        TestValueAIFactory_impl.install(orb);
        TestValueInterfaceFactory_impl.install(orb);

        // Install valuebox factories
        ValueBoxFactories.install(orb);
    }

    @Test void testValue(ORB orb) {
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

        vb = stub.get_null_valuebase();
        assertTrue(vb == null);
        stub.set_null_valuebase(null);

        valueSub = stub.get_null_valuesub();
        assertTrue(valueSub == null);
        stub.set_null_valuesub(null);

        a1 = stub.get_abs_value1();
        assertTrue(a1 != null);
        a1.ping1();
        stub.set_abs_value1(a1);

        a2 = stub.get_abs_value2();
        assertTrue(a2 != null);
        a2.ping2();
        stub.set_abs_value2(a2);

        value = stub.get_value();
        assertTrue(value != null);
        value.ping1();
        assertTrue(value.count == 500);
        stub.set_value(value);

        valueSub = stub.get_valuesub();
        assertTrue(valueSub != null);
        valueSub.ping1();
        valueSub.ping2();
        assertTrue(valueSub.count == 501);
        assertTrue(valueSub.name.equals("ValueSub"));
        stub.set_valuesub(valueSub);

        value = stub.get_valuesub_as_value();
        assertTrue(value != null);
        value.ping1();
        assertTrue(value.count == 501);
        pValueSub = (TestValueSub) value;
        pValueSub.ping2();
        assertTrue(pValueSub.name.equals("ValueSub"));
        stub.set_valuesub_as_value(value);

        stub.get_two_values(v1H, v2H);
        assertTrue(v1H.value != null);
        assertTrue(v2H.value != null);
        assertTrue(v1H.value == v2H.value);
        v1H.value.ping1();
        assertTrue(v1H.value.count == 500);
        stub.set_two_values(v1H.value, v2H.value);

        stub.get_two_valuesubs_as_values(v1H, v2H);
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
        stub.set_two_valuesubs_as_values(v1H.value, v2H.value);

        IntHolder count = new IntHolder();
        stub.get_node(nodeH, count);
        assertTrue(count.value == nodeH.value.compute_count());
        stub.set_node(nodeH.value);
    }

    @Test void testCustom(ORB orb) {
        TestCustom cust;
        TestCustom pCust;
        TestAbsValue1 a1;

        cust = stub.get_custom();
        assertTrue(cust != null);
        cust.ping1();
        assertTrue(cust.shortVal == -99);
        assertTrue(cust.longVal == -123456);
        assertTrue(cust.stringVal.equals("CustomVal"));
        assertTrue(cust.doubleVal == 100.997);
        stub.set_custom(cust);

        a1 = stub.get_abs_custom();
        assertTrue(a1 != null);
        a1.ping1();
        pCust = (TestCustom) a1;
        assertTrue(pCust.shortVal == -99);
        assertTrue(pCust.longVal == -123456);
        assertTrue(pCust.stringVal.equals("CustomVal"));
        assertTrue(pCust.doubleVal == 100.997);
        stub.set_abs_custom(a1);
    }

    @Test void testValueBox(ORB orb) {
        int i;

        String sb;
        sb = stub.get_string_box("hi there");
        assertTrue(sb != null);
        assertTrue(sb.equals("hi there"));
        sb = "bye now";
        stub.set_string_box(sb, "bye now");

        TestULongBox ub;
        ub = stub.get_ulong_box(999);
        assertTrue(ub != null);
        assertTrue(ub.value == 999);
        ub = new TestULongBox(77777);
        stub.set_ulong_box(ub, 77777);

        TestFixStruct fsb;
        TestFixStruct fs = new TestFixStruct();
        fs.x = 111;
        fs.y = 222;
        fs.radius = 3.33;
        fsb = stub.get_fix_struct_box(fs);
        assertTrue(fsb != null);
        assertTrue(fsb.x == fs.x);
        assertTrue(fsb.y == fs.y);
        assertTrue(fsb.radius == fs.radius);
        fsb = new TestFixStruct(fs.x, fs.y, fs.radius);
        stub.set_fix_struct_box(fsb, fs);

        TestVarStruct vsb;
        TestVarStruct vs = new TestVarStruct();
        vs.name = "Joe Bob Briggs";
        vs.email = "jbb@cheese.com";
        vsb = stub.get_var_struct_box(vs);
        assertTrue(vsb != null);
        assertTrue(vsb.name.equals(vs.name));
        assertTrue(vsb.email.equals(vs.email));
        vsb = new TestVarStruct(vs.name, vs.email);
        stub.set_var_struct_box(vsb, vs);

        TestFixUnion fub;
        TestFixUnion fu = new TestFixUnion();
        fu.o((byte) 55);
        fub = stub.get_fix_union_box(fu);
        assertTrue(fub != null);
        assertTrue(fub.o() == (byte) 55);
        fu.d(99.88);
        fub = new TestFixUnion();
        fub.d(fu.d());
        stub.set_fix_union_box(fub, fu);

        TestVarUnion vub;
        TestVarUnion vu = new TestVarUnion();
        vu.s("howdy");
        vub = stub.get_var_union_box(vu);
        assertTrue(vub != null);
        assertTrue(vub.s().equals("howdy"));
        vu.fs(fs);
        vub = new TestVarUnion();
        vub.fs(vu.fs());
        stub.set_var_union_box(vub, vu);

        short[] asb;
        asb = stub.get_anon_seq_box(10);
        assertTrue(asb != null);
        assertTrue(asb.length == 10);
        for (i = 0; i < asb.length; i++)
            assertTrue(asb[i] == (short) i);
        stub.set_anon_seq_box(asb, 10);

        String[] ssb;
        String[] ss = new String[5];
        for (i = 0; i < 5; i++) {
            ss[i] = "s" + i;
        }
        ssb = stub.get_string_seq_box(ss);
        assertTrue(ssb != null);
        assertTrue(ssb.length == ss.length);
        for (i = 0; i < ssb.length; i++)
            assertTrue(ssb[i].equals(ss[i]));
        stub.set_string_seq_box(ssb, ss);

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

    @Test void testCollocated(ORB orb) throws org.omg.CORBA.UserException {
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

        v1 = stub.get_value();
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

        TestAbstract abstractInterface = stub.get_ai_interface();
        TestAbstract abstractValue = stub.get_ai_value();
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

    @Test void testAbstract(ORB orb) {
        org.omg.CORBA.Object obj;
        TestAbstract ai;
        TestAbstractSub sub;
        java.io.Serializable vb;
        TestValueAI v;
        Any any;

        ai = stub.get_ai_interface();
        assertTrue(ai != null);
        obj = (org.omg.CORBA.Object) ai;
        ai.abstract_op();
        sub = TestAbstractSubHelper.narrow(ai);
        sub.sub_op();
        stub.set_ai_interface(ai);

        any = stub.get_ai_interface_any();
        obj = any.extract_Object();
        assertTrue(obj != null);
        sub = TestAbstractSubHelper.narrow(obj);
        sub.abstract_op();
        sub.sub_op();
        stub.set_ai_interface_any(any);

        ai = stub.get_ai_value();
        vb = (java.io.Serializable) ai;
        ai.abstract_op();
        v = (TestValueAI) ai;
        assertTrue(v.count == 12345);
        stub.set_ai_value(ai);
    }

    @Test void testTruncated(ORB orb) {
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
        truncBase = stub.get_trunc1();
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
        truncBase = stub.get_trunc1();
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
        truncBase = stub.get_trunc2();
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
        truncBase = stub.get_trunc2();
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
        truncBase = stub.get_trunc2();
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

    @Test void testAny(ORB orb) {
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
        av = stub.get_value_any();
        stub.remarshal_any(av);

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
        stub.remarshal_any(av); // uses factory instead of TypeCode
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
        av = stub.get_valuesub_any();
        stub.remarshal_any(av);

        //
        // Install the factory, remarshal again, and extract the value
        //
        TestValueSubFactory_impl.install(orb);
        stub.remarshal_any(av); // uses factory instead of TypeCode
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
            av = stub.get_valuesub_as_value_any();
            assertTrue(false);
        } catch (MARSHAL ex) {
            // expected
        }
        TestValueSubFactory_impl.install(orb);
        av = stub.get_valuesub_as_value_any();
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
            av = stub.get_custom_any();
            assertTrue(false);
        } catch (MARSHAL ex) {
            // expected
        }
        TestCustomFactory_impl.install(orb);
        av = stub.get_custom_any();
        stub.remarshal_any(av);
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
        av = stub.get_trunc1_any();
        stub.remarshal_any(av);
        //
        // The TestTrunc2 value returned by the server cannot be unmarshalled
        // without a factory because it uses indirection into a truncated
        // portion of another value
        //
        av = stub.get_trunc2_any();
        try {
            t2 = TestTrunc2Helper.extract(av);
            assertTrue(false);
        } catch (BAD_OPERATION ex) {
            // expected
        }

        TestTrunc1Factory_impl.install(orb);
        TestTrunc2Factory_impl.install(orb);

        av = stub.get_trunc1_any();
        stub.remarshal_any(av);
        av = stub.get_trunc2_any();
        stub.remarshal_any(av);

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
        av = stub.get_trunc1_as_base_any();
        stub.remarshal_any(av);
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
        av = stub.get_trunc1_as_base_any();
        stub.remarshal_any(av);
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
        av = stub.get_trunc2_as_base_any();
        stub.remarshal_any(av);
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
        av = stub.get_trunc2_as_base_any();
        stub.remarshal_any(av);
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
        av = stub.get_trunc2_as_base_any();
        stub.remarshal_any(av);
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
        av = stub.get_trunc2_any();
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
        av = stub.get_ai_value_any();
        tab = TestAbstractHelper.extract(av);
        assertTrue(tab != null);
        tab.abstract_op();
        ai = (TestValueAI) tab;
        ai.value_op();
        stub.set_ai_value_any(av);

        //
        // Ensure that value sharing works across anys
        //
        AnyHolder a1 = new AnyHolder();
        AnyHolder a2 = new AnyHolder();
        stub.get_two_value_anys(a1, a2);
        v1 = TestValueHelper.extract(a1.value);
        v2 = TestValueHelper.extract(a2.value);
        assertTrue(v1 != null);
        assertTrue(v2 != null);
        assertTrue(v1 == v2);
        stub.set_two_value_anys(a1.value, a2.value);
    }

    @Test void testSupported(ORB orb) {
        TestValueInterface val = stub.get_value_as_value();
        val.value_op();
        assertTrue(val.get_count() == val.count);

        TestInterface i = stub.get_value_as_interface();
        assertTrue(val.count == i.get_count());
    }
}

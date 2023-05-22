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

import org.apache.yoko.orb.CORBA.ORBSingleton;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.DynamicAny.DynAnyFactoryHelper;
import test.common.TestBase;
import test.types.DynAnyTypes.TestAnySeqHelper;
import test.types.DynAnyTypes.TestBoundedString10SeqHelper;
import test.types.DynAnyTypes.TestBoundedStringHelper;
import test.types.DynAnyTypes.TestEmptyException;
import test.types.DynAnyTypes.TestEmptyExceptionHelper;
import test.types.DynAnyTypes.TestException;
import test.types.DynAnyTypes.TestExceptionHelper;
import test.types.DynAnyTypes.TestShortSeqHelper;
import test.types.DynAnyTypes.TestStringArrayHelper;
import test.types.DynAnyTypes.TestStringBoxHelper;
import test.types.DynAnyTypes.TestStruct;
import test.types.DynAnyTypes.TestStructBoxHelper;
import test.types.DynAnyTypes.TestStructHelper;
import test.types.DynAnyTypes.TestValue1Helper;
import test.types.DynAnyTypes.TestValue2Helper;
import test.types.DynAnyTypes.TestValue3Helper;
import test.types.DynAnyTypes.TestValue4Helper;
import test.types.DynAnyTypes.TestValueStructHelper;

import java.util.Properties;

import static org.junit.Assert.*;

public class TestPortableTypes extends TestBase {
    private static void testTypeCode(ORB orb,
                                     ORB singleton) {
        //
        // All of these TypeCodes will be created using the
        // singleton ORB
        //
        TypeCode[] types = { TestStruct1Helper.type(),
                TestEnumHelper.type(), OctetSeqHelper.type(),
                CharSeqHelper.type(), DoubleSeqHelper.type(),
                TestStruct1SeqHelper.type(), TestEnumSeqHelper.type(),
                BoundedStringHelper.type(), Double10SeqHelper.type(),
                Double10Seq10SeqHelper.type(), TestStruct120SeqHelper.type(),
                TestEnum30SeqHelper.type(), String40SeqHelper.type(),
                DoubleArrayHelper.type(), TestStruct2Helper.type(),
                TestStruct2SeqHelper.type(), TestFixed1Helper.type(),
                TestFixed2Helper.type(), TestStruct3Helper.type(),
                TestStruct4Helper.type(), TestUnion1Helper.type(),
                TestUnion2Helper.type(), TestUnion3Helper.type(),
                TestUnion4Helper.type(), TestUnion5Helper.type() };

        for (TypeCode type : types) {
            //
            // Force the conversion to an ORBacus TypeCode
            //
            org.apache.yoko.orb.CORBA.TypeCode tc = org.apache.yoko.orb.CORBA.TypeCode._OB_convertForeignTypeCode(type);

            //
            // Reset the repository ID so that equivalent will do
            // more than just compare IDs
            //
            tc.id_ = "";
            assertTrue(tc.equivalent(type));

            //
            // There's no point in calling equal() because the comparison
            // will stop at the repository IDs
            //
            // TEST(tc.equal(types[i]));
        }
    }

    private static void testDynAny(ORB orb,
                                   ORB singleton) throws UserException {
        boolean jdk1_2 = false; // Are we using JDK 1.2?
        if (singleton.getClass().getName().equals(
                "com.sun.CORBA.idl.ORBSingleton"))
            jdk1_2 = true;

        //
        // All of these TypeCodes will be created using the
        // singleton ORB
        //
        TypeCode[] types;
        if (jdk1_2) {
            TypeCode[] dummy = {
                    test.types.DynAnyTypes.TestEnumHelper.type(),
                    TestEmptyExceptionHelper.type(),
                    TestExceptionHelper.type(),
                    TestStructHelper.type(),
                    test.types.DynAnyTypes.TestUnion1Helper.type(),
                    test.types.DynAnyTypes.TestUnion2Helper.type(),
                    test.types.DynAnyTypes.TestUnion3Helper.type(),
                    test.types.DynAnyTypes.TestUnion4Helper.type(),
                    TestShortSeqHelper.type(),
                    TestBoundedStringHelper.type(),
                    TestBoundedString10SeqHelper.type(),
                    TestAnySeqHelper.type(),
                    TestStringArrayHelper.type(),
                    TestStructBoxHelper.type(),
                    TestStringBoxHelper.type()
            //
            // JDK 1.2 has a bug which causes a NullPointerException
            // when the following TypeCodes are created:
            //
            // test.types.DynAnyTypes.TestValue1Helper.type(),
            // test.types.DynAnyTypes.TestValue2Helper.type(),
            // test.types.DynAnyTypes.TestValue3Helper.type(),
            // test.types.DynAnyTypes.TestValue4Helper.type(),
            // test.types.DynAnyTypes.TestValueStructHelper.type()
            };
            types = dummy;
        } else // JDK 1.3
        {
            TypeCode[] dummy = {
                    test.types.DynAnyTypes.TestEnumHelper.type(),
                    TestEmptyExceptionHelper.type(),
                    TestExceptionHelper.type(),
                    TestStructHelper.type(),
                    test.types.DynAnyTypes.TestUnion1Helper.type(),
                    test.types.DynAnyTypes.TestUnion2Helper.type(),
                    test.types.DynAnyTypes.TestUnion3Helper.type(),
                    test.types.DynAnyTypes.TestUnion4Helper.type(),
                    TestShortSeqHelper.type(),
                    TestBoundedStringHelper.type(),
                    TestBoundedString10SeqHelper.type(),
                    TestAnySeqHelper.type(),
                    TestStringArrayHelper.type(),
                    TestStructBoxHelper.type(),
                    TestStringBoxHelper.type(),
                    TestValue1Helper.type(),
                    TestValue2Helper.type(),
                    TestValue3Helper.type(),
                    TestValue4Helper.type(),
                    TestValueStructHelper.type() };
            types = dummy;
        }

        DynAnyFactory factory = null;
        org.omg.CORBA.Object obj = orb
                .resolve_initial_references("DynAnyFactory");
        factory = DynAnyFactoryHelper.narrow(obj);

        for (TypeCode type : types) {
            //
            // Create and test a DynAny using a "foreign" TypeCode
            //
            DynAny da = factory.create_dyn_any_from_type_code(type);
            Any any = da.to_any();
            da.from_any(any);
            da.destroy();
        }

        //
        // TestEnum
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestEnumHelper.insert(any1,
                    test.types.DynAnyTypes.TestEnum.red);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestEmptyException
        //
        {
            Any any1 = singleton.create_any();
            TestEmptyExceptionHelper.insert(any1,
                    new TestEmptyException());
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestException
        //
        {
            Any any1 = singleton.create_any();
            TestExceptionHelper.insert(any1,
                    new TestException("hi", 0));
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestStruct
        //
        if (!jdk1_2) {
            Any any1 = singleton.create_any();
            TestStruct val = new TestStruct();
            val.shortVal = (short) -10000;
            val.ushortVal = (short) 40000;
            val.longVal = -300000;
            val.ulongVal = 500000;
            val.floatVal = (float) 1.9183;
            val.doubleVal = 7.31e29;
            val.boolVal = true;
            val.charVal = 'Y';
            val.octetVal = (byte) 155;
            val.anyVal = singleton.create_any();
            val.anyVal.insert_string("This is a string in an any");
            val.tcVal = singleton
                    .get_primitive_tc(TCKind.tk_float);
            val.objectVal = null;
            val.stringVal = "This is a string";
            val.longlongVal = -1234567890L;
            val.ulonglongVal = 9876543210L;
            val.wcharVal = 'Z';
            val.wstringVal = "This is a wstring";
            TestStructHelper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            /**
             *
             * The JDK ORB raises MARSHAL because of the wchar
             *
             * org.omg.CORBA.portable.OutputStream out =
             * orb.create_output_stream(); out.write_any(any1);
             */
        }

        //
        // test.types.TestStruct1
        //
        {
            Any any1 = singleton.create_any();
            TestStruct1 val = new TestStruct1();
            val.s = (short) -10000;
            val.l = -300000;
            val.d = 7.31e29;
            val.b = true;
            val.c = 'Y';
            val.o = (byte) 155;
            val.str = "This is a string";
            TestStruct1Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion1 (#1)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion1 val = new test.types.DynAnyTypes.TestUnion1();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion1Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion1 (#2)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion1 val = new test.types.DynAnyTypes.TestUnion1();
            val.b((float) 2.934);
            test.types.DynAnyTypes.TestUnion1Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion1 (#3)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion1 val = new test.types.DynAnyTypes.TestUnion1();
            val.c("hi");
            test.types.DynAnyTypes.TestUnion1Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion2 (#1)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion2 val = new test.types.DynAnyTypes.TestUnion2();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion2Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion2 (#2)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion2 val = new test.types.DynAnyTypes.TestUnion2();
            val.__default(false);
            test.types.DynAnyTypes.TestUnion2Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            /**
             *
             * Another JDK bug
             *
             * org.omg.CORBA.portable.OutputStream out =
             * orb.create_output_stream(); out.write_any(any1);
             */
        }

        //
        // TestUnion3 (#1)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion3 val = new test.types.DynAnyTypes.TestUnion3();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion3Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion3 (#2)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion3 val = new test.types.DynAnyTypes.TestUnion3();
            val.b(3.3933);
            test.types.DynAnyTypes.TestUnion3Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion3 (#3)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion3 val = new test.types.DynAnyTypes.TestUnion3();
            val.c('Z');
            test.types.DynAnyTypes.TestUnion3Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion4 (#1)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion4 val = new test.types.DynAnyTypes.TestUnion4();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion4Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion4 (#2)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion4 val = new test.types.DynAnyTypes.TestUnion4();
            val.a((short) 2, 1000);
            test.types.DynAnyTypes.TestUnion4Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion4 (#3)
        //
        {
            Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion4 val = new test.types.DynAnyTypes.TestUnion4();
            val.b((float) 1.0189);
            test.types.DynAnyTypes.TestUnion4Helper.insert(any1, val);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestShortSeq
        //
        {
            Any any1 = singleton.create_any();
            TestShortSeqHelper
                    .insert(any1, new short[5]);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        /*
         * These tests don't work with the JDK ORB - there is a bug in their Any
         * implementation of extract_string for bounded string TypeCodes
         *  // // TestBoundedString // { org.omg.CORBA.Any any1 =
         * singleton.create_any();
         * test.types.DynAnyTypes.TestBoundedStringHelper.insert( any1, "hi
         * there"); org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
         * org.omg.CORBA.Any any2 = da.to_any(); TEST(any2.equal(any1));
         * da.destroy();
         *
         * org.omg.CORBA.portable.OutputStream out = orb.create_output_stream();
         * out.write_any(any1); }
         *  // // TestBoundedString10Seq // { org.omg.CORBA.Any any1 =
         * singleton.create_any(); String[] seq = { "1", "2", "3" };
         * test.types.DynAnyTypes.TestBoundedString10SeqHelper.insert( any1,
         * seq); org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
         * org.omg.CORBA.Any any2 = da.to_any(); TEST(any2.equal(any1));
         * da.destroy();
         *
         * org.omg.CORBA.portable.OutputStream out = orb.create_output_stream();
         * out.write_any(any1); }
         */

        //
        // TestAnySeq
        //
        {
            Any any1 = singleton.create_any();
            Any[] seq = new Any[2];
            seq[0] = singleton.create_any();
            seq[1] = singleton.create_any();
            TestAnySeqHelper.insert(any1, seq);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestStringArray
        //
        {
            Any any1 = singleton.create_any();
            String[] seq = new String[10];
            for (int i = 0; i < seq.length; i++)
                seq[i] = "# " + i;
            TestStringArrayHelper.insert(any1, seq);
            DynAny da = factory.create_dyn_any(any1);
            Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        /**
         *
         * Can't test valuetypes inserted into Anys created by the JDK ORB
         * singleton, since an ORB singleton can't be expected to deal with
         * valuetypes properly.
         *
         */
    }

    private static int run(ORB orb, String[] args)
            throws UserException {
        ORB singleton = ORB.init();

        //
        // We will get the ORBacus ORB singleton if JDK 1.1 is being used,
        // in which case there's no need to continue with the tests
        //
        if (singleton instanceof ORBSingleton)
            return 0;

        //
        // Run tests
        //
        System.out.print("Testing TypeCode portability... ");
        System.out.flush();
        testTypeCode(orb, singleton);
        System.out.println("Done!");

        System.out.print("Testing DynAny portability... ");
        System.out.flush();
        testDynAny(orb, singleton);
        System.out.println("Done!");

        return 0;
    }

    public static void main(String[] args) throws UserException {
        //
        // Before we initialize the ORB, we ensure that there is
        // no ORBSingletonClass property defined in the system
        // property set.
        //
        Properties props = new Properties();
        props.putAll(System.getProperties());
        props.remove("org.omg.CORBA.ORBSingletonClass");

        //
        // Don't specify an ORBSingletonClass property - we want
        // to use the JDK's ORB singleton implementation
        //
        props = new Properties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");

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
            status = run(orb, args);
        } finally {
            if (orb != null) orb.destroy();
        }
    }
}

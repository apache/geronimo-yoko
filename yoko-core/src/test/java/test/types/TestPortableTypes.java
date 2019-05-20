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

import org.omg.CORBA.UserException;

import java.util.Properties;

import static org.junit.Assert.*;

public class TestPortableTypes extends test.common.TestBase {
    private static void testTypeCode(org.omg.CORBA.ORB orb,
            org.omg.CORBA.ORB singleton) {
        //
        // All of these TypeCodes will be created using the
        // singleton ORB
        //
        org.omg.CORBA.TypeCode[] types = { TestStruct1Helper.type(),
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

        for (int i = 0; i < types.length; i++) {
            //
            // Force the conversion to an ORBacus TypeCode
            //
            org.apache.yoko.orb.CORBA.TypeCode tc = org.apache.yoko.orb.CORBA.TypeCode
                    ._OB_convertForeignTypeCode(types[i]);

            //
            // Reset the repository ID so that equivalent will do
            // more than just compare IDs
            //
            tc.id_ = "";
            assertTrue(tc.equivalent(types[i]));

            //
            // There's no point in calling equal() because the comparison
            // will stop at the repository IDs
            //
            // TEST(tc.equal(types[i]));
        }
    }

    private static void testDynAny(org.omg.CORBA.ORB orb,
            org.omg.CORBA.ORB singleton) throws org.omg.CORBA.UserException {
        boolean jdk1_2 = false; // Are we using JDK 1.2?
        if (singleton.getClass().getName().equals(
                "com.sun.CORBA.idl.ORBSingleton"))
            jdk1_2 = true;

        //
        // All of these TypeCodes will be created using the
        // singleton ORB
        //
        org.omg.CORBA.TypeCode[] types;
        if (jdk1_2) {
            org.omg.CORBA.TypeCode[] dummy = {
                    test.types.DynAnyTypes.TestEnumHelper.type(),
                    test.types.DynAnyTypes.TestEmptyExceptionHelper.type(),
                    test.types.DynAnyTypes.TestExceptionHelper.type(),
                    test.types.DynAnyTypes.TestStructHelper.type(),
                    test.types.DynAnyTypes.TestUnion1Helper.type(),
                    test.types.DynAnyTypes.TestUnion2Helper.type(),
                    test.types.DynAnyTypes.TestUnion3Helper.type(),
                    test.types.DynAnyTypes.TestUnion4Helper.type(),
                    test.types.DynAnyTypes.TestShortSeqHelper.type(),
                    test.types.DynAnyTypes.TestBoundedStringHelper.type(),
                    test.types.DynAnyTypes.TestBoundedString10SeqHelper.type(),
                    test.types.DynAnyTypes.TestAnySeqHelper.type(),
                    test.types.DynAnyTypes.TestStringArrayHelper.type(),
                    test.types.DynAnyTypes.TestStructBoxHelper.type(),
                    test.types.DynAnyTypes.TestStringBoxHelper.type()
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
            org.omg.CORBA.TypeCode[] dummy = {
                    test.types.DynAnyTypes.TestEnumHelper.type(),
                    test.types.DynAnyTypes.TestEmptyExceptionHelper.type(),
                    test.types.DynAnyTypes.TestExceptionHelper.type(),
                    test.types.DynAnyTypes.TestStructHelper.type(),
                    test.types.DynAnyTypes.TestUnion1Helper.type(),
                    test.types.DynAnyTypes.TestUnion2Helper.type(),
                    test.types.DynAnyTypes.TestUnion3Helper.type(),
                    test.types.DynAnyTypes.TestUnion4Helper.type(),
                    test.types.DynAnyTypes.TestShortSeqHelper.type(),
                    test.types.DynAnyTypes.TestBoundedStringHelper.type(),
                    test.types.DynAnyTypes.TestBoundedString10SeqHelper.type(),
                    test.types.DynAnyTypes.TestAnySeqHelper.type(),
                    test.types.DynAnyTypes.TestStringArrayHelper.type(),
                    test.types.DynAnyTypes.TestStructBoxHelper.type(),
                    test.types.DynAnyTypes.TestStringBoxHelper.type(),
                    test.types.DynAnyTypes.TestValue1Helper.type(),
                    test.types.DynAnyTypes.TestValue2Helper.type(),
                    test.types.DynAnyTypes.TestValue3Helper.type(),
                    test.types.DynAnyTypes.TestValue4Helper.type(),
                    test.types.DynAnyTypes.TestValueStructHelper.type() };
            types = dummy;
        }

        org.omg.DynamicAny.DynAnyFactory factory = null;
        org.omg.CORBA.Object obj = orb
                .resolve_initial_references("DynAnyFactory");
        factory = org.omg.DynamicAny.DynAnyFactoryHelper.narrow(obj);

        for (int i = 0; i < types.length; i++) {
            //
            // Create and test a DynAny using a "foreign" TypeCode
            //
            org.omg.DynamicAny.DynAny da = factory
                    .create_dyn_any_from_type_code(types[i]);
            org.omg.CORBA.Any any = da.to_any();
            da.from_any(any);
            da.destroy();
        }

        //
        // TestEnum
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestEnumHelper.insert(any1,
                    test.types.DynAnyTypes.TestEnum.red);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestEmptyException
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestEmptyExceptionHelper.insert(any1,
                    new test.types.DynAnyTypes.TestEmptyException());
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestException
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestExceptionHelper.insert(any1,
                    new test.types.DynAnyTypes.TestException("hi", 0));
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestStruct
        //
        if (!jdk1_2) {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestStruct val = new test.types.DynAnyTypes.TestStruct();
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
                    .get_primitive_tc(org.omg.CORBA.TCKind.tk_float);
            val.objectVal = null;
            val.stringVal = "This is a string";
            val.longlongVal = -1234567890L;
            val.ulonglongVal = 9876543210L;
            val.wcharVal = 'Z';
            val.wstringVal = "This is a wstring";
            test.types.DynAnyTypes.TestStructHelper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
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
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.TestStruct1 val = new test.types.TestStruct1();
            val.s = (short) -10000;
            val.l = -300000;
            val.d = 7.31e29;
            val.b = true;
            val.c = 'Y';
            val.o = (byte) 155;
            val.str = "This is a string";
            test.types.TestStruct1Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion1 (#1)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion1 val = new test.types.DynAnyTypes.TestUnion1();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion1Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion1 (#2)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion1 val = new test.types.DynAnyTypes.TestUnion1();
            val.b((float) 2.934);
            test.types.DynAnyTypes.TestUnion1Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion1 (#3)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion1 val = new test.types.DynAnyTypes.TestUnion1();
            val.c("hi");
            test.types.DynAnyTypes.TestUnion1Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion2 (#1)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion2 val = new test.types.DynAnyTypes.TestUnion2();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion2Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion2 (#2)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion2 val = new test.types.DynAnyTypes.TestUnion2();
            val.__default(false);
            test.types.DynAnyTypes.TestUnion2Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
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
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion3 val = new test.types.DynAnyTypes.TestUnion3();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion3Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion3 (#2)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion3 val = new test.types.DynAnyTypes.TestUnion3();
            val.b(3.3933);
            test.types.DynAnyTypes.TestUnion3Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion3 (#3)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion3 val = new test.types.DynAnyTypes.TestUnion3();
            val.c('Z');
            test.types.DynAnyTypes.TestUnion3Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion4 (#1)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion4 val = new test.types.DynAnyTypes.TestUnion4();
            val.a(1000);
            test.types.DynAnyTypes.TestUnion4Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion4 (#2)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion4 val = new test.types.DynAnyTypes.TestUnion4();
            val.a((short) 2, 1000);
            test.types.DynAnyTypes.TestUnion4Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestUnion4 (#3)
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestUnion4 val = new test.types.DynAnyTypes.TestUnion4();
            val.b((float) 1.0189);
            test.types.DynAnyTypes.TestUnion4Helper.insert(any1, val);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestShortSeq
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            test.types.DynAnyTypes.TestShortSeqHelper
                    .insert(any1, new short[5]);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
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
            org.omg.CORBA.Any any1 = singleton.create_any();
            org.omg.CORBA.Any[] seq = new org.omg.CORBA.Any[2];
            seq[0] = singleton.create_any();
            seq[1] = singleton.create_any();
            test.types.DynAnyTypes.TestAnySeqHelper.insert(any1, seq);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
                    .create_output_stream();
            out.write_any(any1);
        }

        //
        // TestStringArray
        //
        {
            org.omg.CORBA.Any any1 = singleton.create_any();
            String[] seq = new String[10];
            for (int i = 0; i < seq.length; i++)
                seq[i] = "# " + i;
            test.types.DynAnyTypes.TestStringArrayHelper.insert(any1, seq);
            org.omg.DynamicAny.DynAny da = factory.create_dyn_any(any1);
            org.omg.CORBA.Any any2 = da.to_any();
            assertTrue(any2.equal(any1));
            da.destroy();

            org.omg.CORBA.portable.OutputStream out = orb
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

    public static int run(org.omg.CORBA.ORB orb, String[] args)
            throws org.omg.CORBA.UserException {
        org.omg.CORBA.ORB singleton = org.omg.CORBA.ORB.init();

        //
        // We will get the ORBacus ORB singleton if JDK 1.1 is being used,
        // in which case there's no need to continue with the tests
        //
        if (singleton instanceof org.apache.yoko.orb.CORBA.ORBSingleton)
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

    public static void main(String args[]) throws UserException {
        //
        // Before we initialize the ORB, we ensure that there is
        // no ORBSingletonClass property defined in the system
        // property set.
        //
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.remove("org.omg.CORBA.ORBSingletonClass");

        //
        // Don't specify an ORBSingletonClass property - we want
        // to use the JDK's ORB singleton implementation
        //
        props = new java.util.Properties();
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");

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
            status = run(orb, args);
        } finally {
            if (orb != null) orb.destroy();
        }
    }
}

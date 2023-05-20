/*
 * Copyright 2010 IBM Corporation and others.
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

import test.obv.TestOBVColoPackage.*;

public class TestOBVColo_impl extends TestOBVColoPOA {
    private int count_;

    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    public void set_expected_count(int n) {
        count_ = n;
    }

    public TestValue test_value_attribute() {
        return null;
    }

    public void test_value_attribute(TestValue a) {
        if (a != null) {
            TEST(a.count == count_);
            a.ping1();
            a.count = 99997;
        }
    }

    public TestValue test_value_op(TestValue v1, TestValueHolder v2,
            TestValueHolder v3) {
        if (v1 != null) {
            TEST(v1.count == count_);
            v1.ping1();
            v1.count = 99998;
        }

        TEST(v2.value == v1);

        v2.value = v1;
        v3.value = v1;

        return v1;
    }

    public SV test_value_struct_attribute() {
        return new SV("hi", null);
    }

    public void test_value_struct_attribute(SV a) {
        if (a.val != null) {
            TEST(a.val.count == count_);
            a.val.count = 99997;
        }
    }

    public SV test_value_struct_op(SV s1, SVHolder s2, SVHolder s3) {
        if (s1.val != null) {
            TEST(s1.val.count == count_);
            s1.val.count = 99998;
        }

        TEST(s2.value.val == s1.val);

        s3.value = new SV(s2.value.str, s2.value.val);

        return new SV(s2.value.str, s2.value.val);
    }

    public UV test_value_union_attribute() {
        UV result = new UV();
        result.val(null);
        return result;
    }

    public void test_value_union_attribute(UV a) {
        TEST(a.discriminator() == false);

        if (a.val() != null) {
            TEST(a.val().count == count_);
            a.val().count = 99997;
        }
    }

    public UV test_value_union_op(UV u1, UVHolder u2, UVHolder u3) {
        TEST(u1.discriminator() == false);
        TEST(u2.value.discriminator() == false);

        if (u1.val() != null) {
            TEST(u1.val().count == count_);
            u1.val().count = 99998;
        }

        TEST(u2.value.val() == u1.val());

        u3.value = new UV();
        u3.value.val(u2.value.val());

        UV result = new UV();
        result.val(u2.value.val());
        return result;
    }

    public TestValue[] test_value_seq_attribute() {
        return new TestValue[5];
    }

    public void test_value_seq_attribute(TestValue[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != null)
                TEST(a[i].count == count_);
            TEST(a[i] == a[0]);
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != null)
                a[i].count = 99997;
        }
    }

    public TestValue[] test_value_seq_op(TestValue[] s1, VSeqHolder s2,
            VSeqHolder s3) {
        for (int i = 0; i < s1.length; i++) {
            if (s1[i] != null)
                TEST(s1[i].count == count_);
            TEST(s1[i] == s1[0]);
        }
        for (int i = 0; i < s1.length; i++) {
            if (s1[i] != null)
                s1[i].count = 99998;
        }

        TEST(s2.value.length == s1.length);
        for (int i = 0; i < s2.value.length; i++)
            TEST(s2.value[i] == s1[i]);

        s3.value = new TestValue[s2.value.length];
        System.arraycopy(s2.value, 0, s3.value, 0, s2.value.length);

        TestValue[] result = new TestValue[s2.value.length];
        System.arraycopy(s2.value, 0, result, 0, s2.value.length);
        return result;
    }

    public TestAbstract test_abstract_attribute() {
        return null;
    }

    public void test_abstract_attribute(TestAbstract a) {
        if (a != null) {
            TestAbstractSub sub = null;
            try {
                sub = TestAbstractSubHelper.narrow(a);
            } catch (org.omg.CORBA.BAD_PARAM ex) {
            }
            TestValueAI v = null;
            if (a instanceof TestValueAI)
                v = (TestValueAI) a;

            if (v != null) {
                TEST(v.count == count_);
                v.value_op();
                v.count = 99996;
            }
        }
    }

    public void test_abstract_op(TestAbstract a) {
        if (a != null) {
            TestAbstractSub sub = null;
            try {
                sub = TestAbstractSubHelper.narrow(a);
            } catch (org.omg.CORBA.BAD_PARAM ex) {
            }
            TestValueAI v = null;
            if (a instanceof TestValueAI)
                v = (TestValueAI) a;

            if (v != null) {
                TEST(v.count == count_);
                v.value_op();
                v.count = 99996;
            }
        }
    }
}

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

//
// IDL:TestOBVColo:1.0
//
/***/

public interface TestOBVColoOperations
{
    //
    // IDL:TestOBVColo/set_expected_count:1.0
    //
    /***/

    void
    set_expected_count(int l);

    //
    // IDL:TestOBVColo/test_value_attribute:1.0
    //
    /***/

    TestValue
    test_value_attribute();

    void
    test_value_attribute(TestValue val);

    //
    // IDL:TestOBVColo/test_value_op:1.0
    //
    /***/

    TestValue
    test_value_op(TestValue v1,
                  TestValueHolder v2,
                  TestValueHolder v3);

    //
    // IDL:TestOBVColo/test_value_struct_attribute:1.0
    //
    /***/

    test.obv.TestOBVColoPackage.SV
    test_value_struct_attribute();

    void
    test_value_struct_attribute(test.obv.TestOBVColoPackage.SV val);

    //
    // IDL:TestOBVColo/test_value_struct_op:1.0
    //
    /***/

    test.obv.TestOBVColoPackage.SV
    test_value_struct_op(test.obv.TestOBVColoPackage.SV s1,
                         test.obv.TestOBVColoPackage.SVHolder s2,
                         test.obv.TestOBVColoPackage.SVHolder s3);

    //
    // IDL:TestOBVColo/test_value_union_attribute:1.0
    //
    /***/

    test.obv.TestOBVColoPackage.UV
    test_value_union_attribute();

    void
    test_value_union_attribute(test.obv.TestOBVColoPackage.UV val);

    //
    // IDL:TestOBVColo/test_value_union_op:1.0
    //
    /***/

    test.obv.TestOBVColoPackage.UV
    test_value_union_op(test.obv.TestOBVColoPackage.UV u1,
                        test.obv.TestOBVColoPackage.UVHolder u2,
                        test.obv.TestOBVColoPackage.UVHolder u3);

    //
    // IDL:TestOBVColo/test_value_seq_attribute:1.0
    //
    /***/

    TestValue[]
    test_value_seq_attribute();

    void
    test_value_seq_attribute(TestValue[] val);

    //
    // IDL:TestOBVColo/test_value_seq_op:1.0
    //
    /***/

    TestValue[]
    test_value_seq_op(TestValue[] s1,
                      test.obv.TestOBVColoPackage.VSeqHolder s2,
                      test.obv.TestOBVColoPackage.VSeqHolder s3);

    //
    // IDL:TestOBVColo/test_abstract_attribute:1.0
    //
    /***/

    TestAbstract
    test_abstract_attribute();

    void
    test_abstract_attribute(TestAbstract val);

    //
    // IDL:TestOBVColo/test_abstract_op:1.0
    //
    /***/

    void
    test_abstract_op(TestAbstract a);
}

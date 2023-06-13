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
// IDL:TestOBV:1.0
//
/***/

public interface TestOBVOperations
{
    //
    // IDL:TestOBV/get_null_valuebase:1.0
    //
    /***/

    java.io.Serializable
    get_null_valuebase();

    //
    // IDL:TestOBV/set_null_valuebase:1.0
    //
    /***/

    void
    set_null_valuebase(java.io.Serializable v);

    //
    // IDL:TestOBV/get_null_valuesub:1.0
    //
    /***/

    TestValueSub
    get_null_valuesub();

    //
    // IDL:TestOBV/set_null_valuesub:1.0
    //
    /***/

    void
    set_null_valuesub(TestValueSub v);

    //
    // IDL:TestOBV/get_abs_value1:1.0
    //
    /***/

    TestAbsValue1
    get_abs_value1();

    //
    // IDL:TestOBV/set_abs_value1:1.0
    //
    /***/

    void
    set_abs_value1(TestAbsValue1 v);

    //
    // IDL:TestOBV/get_abs_value2:1.0
    //
    /***/

    TestAbsValue2
    get_abs_value2();

    //
    // IDL:TestOBV/set_abs_value2:1.0
    //
    /***/

    void
    set_abs_value2(TestAbsValue2 v);

    //
    // IDL:TestOBV/get_value:1.0
    //
    /***/

    TestValue
    get_value();

    //
    // IDL:TestOBV/set_value:1.0
    //
    /***/

    void
    set_value(TestValue v);

    //
    // IDL:TestOBV/get_valuesub:1.0
    //
    /***/

    TestValueSub
    get_valuesub();

    //
    // IDL:TestOBV/set_valuesub:1.0
    //
    /***/

    void
    set_valuesub(TestValueSub v);

    //
    // IDL:TestOBV/get_valuesub_as_value:1.0
    //
    /***/

    TestValue
    get_valuesub_as_value();

    //
    // IDL:TestOBV/set_valuesub_as_value:1.0
    //
    /***/

    void
    set_valuesub_as_value(TestValue v);

    //
    // IDL:TestOBV/get_two_values:1.0
    //
    /***/

    void
    get_two_values(TestValueHolder v1,
                   TestValueHolder v2);

    //
    // IDL:TestOBV/set_two_values:1.0
    //
    /***/

    void
    set_two_values(TestValue v1,
                   TestValue v2);

    //
    // IDL:TestOBV/get_two_valuesubs_as_values:1.0
    //
    /***/

    void
    get_two_valuesubs_as_values(TestValueHolder v1,
                                TestValueHolder v2);

    //
    // IDL:TestOBV/set_two_valuesubs_as_values:1.0
    //
    /***/

    void
    set_two_valuesubs_as_values(TestValue v1,
                                TestValue v2);

    //
    // IDL:TestOBV/get_custom:1.0
    //
    /***/

    TestCustom
    get_custom();

    //
    // IDL:TestOBV/set_custom:1.0
    //
    /***/

    void
    set_custom(TestCustom v);

    //
    // IDL:TestOBV/get_abs_custom:1.0
    //
    /***/

    TestAbsValue1
    get_abs_custom();

    //
    // IDL:TestOBV/set_abs_custom:1.0
    //
    /***/

    void
    set_abs_custom(TestAbsValue1 v);

    //
    // IDL:TestOBV/get_node:1.0
    //
    /***/

    void
    get_node(TestNodeHolder v,
             org.omg.CORBA.IntHolder count);

    //
    // IDL:TestOBV/set_node:1.0
    //
    /***/

    void
    set_node(TestNode v);

    //
    // IDL:TestOBV/get_string_box:1.0
    //
    /***/

    String
    get_string_box(String value);

    //
    // IDL:TestOBV/set_string_box:1.0
    //
    /***/

    void
    set_string_box(String b,
                   String value);

    //
    // IDL:TestOBV/get_ulong_box:1.0
    //
    /***/

    TestULongBox
    get_ulong_box(int value);

    //
    // IDL:TestOBV/set_ulong_box:1.0
    //
    /***/

    void
    set_ulong_box(TestULongBox b,
                  int value);

    //
    // IDL:TestOBV/get_fix_struct_box:1.0
    //
    /***/

    TestFixStruct
    get_fix_struct_box(TestFixStruct value);

    //
    // IDL:TestOBV/set_fix_struct_box:1.0
    //
    /***/

    void
    set_fix_struct_box(TestFixStruct b,
                       TestFixStruct value);

    //
    // IDL:TestOBV/get_var_struct_box:1.0
    //
    /***/

    TestVarStruct
    get_var_struct_box(TestVarStruct value);

    //
    // IDL:TestOBV/set_var_struct_box:1.0
    //
    /***/

    void
    set_var_struct_box(TestVarStruct b,
                       TestVarStruct value);

    //
    // IDL:TestOBV/get_fix_union_box:1.0
    //
    /***/

    TestFixUnion
    get_fix_union_box(TestFixUnion value);

    //
    // IDL:TestOBV/set_fix_union_box:1.0
    //
    /***/

    void
    set_fix_union_box(TestFixUnion b,
                      TestFixUnion value);

    //
    // IDL:TestOBV/get_var_union_box:1.0
    //
    /***/

    TestVarUnion
    get_var_union_box(TestVarUnion value);

    //
    // IDL:TestOBV/set_var_union_box:1.0
    //
    /***/

    void
    set_var_union_box(TestVarUnion b,
                      TestVarUnion value);

    //
    // IDL:TestOBV/get_anon_seq_box:1.0
    //
    /***/

    short[]
    get_anon_seq_box(int length);

    //
    // IDL:TestOBV/set_anon_seq_box:1.0
    //
    /***/

    void
    set_anon_seq_box(short[] b,
                     int length);

    //
    // IDL:TestOBV/get_string_seq_box:1.0
    //
    /***/

    String[]
    get_string_seq_box(String[] value);

    //
    // IDL:TestOBV/set_string_seq_box:1.0
    //
    /***/

    void
    set_string_seq_box(String[] b,
                       String[] value);

    //
    // IDL:TestOBV/get_ai_interface:1.0
    //
    /***/

    TestAbstract
    get_ai_interface();

    //
    // IDL:TestOBV/set_ai_interface:1.0
    //
    /***/

    void
    set_ai_interface(TestAbstract a);

    //
    // IDL:TestOBV/get_ai_interface_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_ai_interface_any();

    //
    // IDL:TestOBV/set_ai_interface_any:1.0
    //
    /***/

    void
    set_ai_interface_any(org.omg.CORBA.Any a);

    //
    // IDL:TestOBV/get_ai_value:1.0
    //
    /***/

    TestAbstract
    get_ai_value();

    //
    // IDL:TestOBV/set_ai_value:1.0
    //
    /***/

    void
    set_ai_value(TestAbstract a);

    //
    // IDL:TestOBV/get_ai_value_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_ai_value_any();

    //
    // IDL:TestOBV/set_ai_value_any:1.0
    //
    /***/

    void
    set_ai_value_any(org.omg.CORBA.Any a);

    //
    // IDL:TestOBV/get_trunc1:1.0
    //
    /***/

    TestTruncBase
    get_trunc1();

    //
    // IDL:TestOBV/get_trunc2:1.0
    //
    /***/

    TestTruncBase
    get_trunc2();

    //
    // IDL:TestOBV/get_value_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_value_any();

    //
    // IDL:TestOBV/get_valuesub_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_valuesub_any();

    //
    // IDL:TestOBV/get_valuesub_as_value_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_valuesub_as_value_any();

    //
    // IDL:TestOBV/get_custom_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_custom_any();

    //
    // IDL:TestOBV/get_trunc1_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_trunc1_any();

    //
    // IDL:TestOBV/get_trunc1_as_base_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_trunc1_as_base_any();

    //
    // IDL:TestOBV/get_trunc2_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_trunc2_any();

    //
    // IDL:TestOBV/get_trunc2_as_base_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_trunc2_as_base_any();

    //
    // IDL:TestOBV/remarshal_any:1.0
    //
    /***/

    void
    remarshal_any(org.omg.CORBA.Any a);

    //
    // IDL:TestOBV/get_two_value_anys:1.0
    //
    /***/

    void
    get_two_value_anys(org.omg.CORBA.AnyHolder a1,
                       org.omg.CORBA.AnyHolder a2);

    //
    // IDL:TestOBV/set_two_value_anys:1.0
    //
    /***/

    void
    set_two_value_anys(org.omg.CORBA.Any a1,
                       org.omg.CORBA.Any a2);

    //
    // IDL:TestOBV/get_value_as_value:1.0
    //
    /***/

    TestValueInterface
    get_value_as_value();

    //
    // IDL:TestOBV/get_value_as_interface:1.0
    //
    /***/

    TestInterface
    get_value_as_interface();

    //
    // IDL:TestOBV/deactivate:1.0
    //
    /***/

    void
    deactivate();
}

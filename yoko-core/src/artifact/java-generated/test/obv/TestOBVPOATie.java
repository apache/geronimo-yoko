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
public class TestOBVPOATie extends TestOBVPOA
{
    private TestOBVOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    TestOBVPOATie(TestOBVOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    TestOBVPOATie(TestOBVOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public TestOBVOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(TestOBVOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public org.omg.PortableServer.POA
    _default_POA()
    {
        if(_ob_poa_ != null)
            return _ob_poa_;
        else
            return super._default_POA();
    }

    //
    // IDL:TestOBV/get_null_valuebase:1.0
    //
    public java.io.Serializable
    get_null_valuebase()
    {
        return _ob_delegate_.get_null_valuebase();
    }

    //
    // IDL:TestOBV/set_null_valuebase:1.0
    //
    public void
    set_null_valuebase(java.io.Serializable v)
    {
        _ob_delegate_.set_null_valuebase(v);
    }

    //
    // IDL:TestOBV/get_null_valuesub:1.0
    //
    public TestValueSub
    get_null_valuesub()
    {
        return _ob_delegate_.get_null_valuesub();
    }

    //
    // IDL:TestOBV/set_null_valuesub:1.0
    //
    public void
    set_null_valuesub(TestValueSub v)
    {
        _ob_delegate_.set_null_valuesub(v);
    }

    //
    // IDL:TestOBV/get_abs_value1:1.0
    //
    public TestAbsValue1
    get_abs_value1()
    {
        return _ob_delegate_.get_abs_value1();
    }

    //
    // IDL:TestOBV/set_abs_value1:1.0
    //
    public void
    set_abs_value1(TestAbsValue1 v)
    {
        _ob_delegate_.set_abs_value1(v);
    }

    //
    // IDL:TestOBV/get_abs_value2:1.0
    //
    public TestAbsValue2
    get_abs_value2()
    {
        return _ob_delegate_.get_abs_value2();
    }

    //
    // IDL:TestOBV/set_abs_value2:1.0
    //
    public void
    set_abs_value2(TestAbsValue2 v)
    {
        _ob_delegate_.set_abs_value2(v);
    }

    //
    // IDL:TestOBV/get_value:1.0
    //
    public TestValue
    get_value()
    {
        return _ob_delegate_.get_value();
    }

    //
    // IDL:TestOBV/set_value:1.0
    //
    public void
    set_value(TestValue v)
    {
        _ob_delegate_.set_value(v);
    }

    //
    // IDL:TestOBV/get_valuesub:1.0
    //
    public TestValueSub
    get_valuesub()
    {
        return _ob_delegate_.get_valuesub();
    }

    //
    // IDL:TestOBV/set_valuesub:1.0
    //
    public void
    set_valuesub(TestValueSub v)
    {
        _ob_delegate_.set_valuesub(v);
    }

    //
    // IDL:TestOBV/get_valuesub_as_value:1.0
    //
    public TestValue
    get_valuesub_as_value()
    {
        return _ob_delegate_.get_valuesub_as_value();
    }

    //
    // IDL:TestOBV/set_valuesub_as_value:1.0
    //
    public void
    set_valuesub_as_value(TestValue v)
    {
        _ob_delegate_.set_valuesub_as_value(v);
    }

    //
    // IDL:TestOBV/get_two_values:1.0
    //
    public void
    get_two_values(TestValueHolder v1,
                   TestValueHolder v2)
    {
        _ob_delegate_.get_two_values(v1,
                                     v2);
    }

    //
    // IDL:TestOBV/set_two_values:1.0
    //
    public void
    set_two_values(TestValue v1,
                   TestValue v2)
    {
        _ob_delegate_.set_two_values(v1,
                                     v2);
    }

    //
    // IDL:TestOBV/get_two_valuesubs_as_values:1.0
    //
    public void
    get_two_valuesubs_as_values(TestValueHolder v1,
                                TestValueHolder v2)
    {
        _ob_delegate_.get_two_valuesubs_as_values(v1,
                                                  v2);
    }

    //
    // IDL:TestOBV/set_two_valuesubs_as_values:1.0
    //
    public void
    set_two_valuesubs_as_values(TestValue v1,
                                TestValue v2)
    {
        _ob_delegate_.set_two_valuesubs_as_values(v1,
                                                  v2);
    }

    //
    // IDL:TestOBV/get_custom:1.0
    //
    public TestCustom
    get_custom()
    {
        return _ob_delegate_.get_custom();
    }

    //
    // IDL:TestOBV/set_custom:1.0
    //
    public void
    set_custom(TestCustom v)
    {
        _ob_delegate_.set_custom(v);
    }

    //
    // IDL:TestOBV/get_abs_custom:1.0
    //
    public TestAbsValue1
    get_abs_custom()
    {
        return _ob_delegate_.get_abs_custom();
    }

    //
    // IDL:TestOBV/set_abs_custom:1.0
    //
    public void
    set_abs_custom(TestAbsValue1 v)
    {
        _ob_delegate_.set_abs_custom(v);
    }

    //
    // IDL:TestOBV/get_node:1.0
    //
    public void
    get_node(TestNodeHolder v,
             org.omg.CORBA.IntHolder count)
    {
        _ob_delegate_.get_node(v,
                               count);
    }

    //
    // IDL:TestOBV/set_node:1.0
    //
    public void
    set_node(TestNode v)
    {
        _ob_delegate_.set_node(v);
    }

    //
    // IDL:TestOBV/get_string_box:1.0
    //
    public String
    get_string_box(String value)
    {
        return _ob_delegate_.get_string_box(value);
    }

    //
    // IDL:TestOBV/set_string_box:1.0
    //
    public void
    set_string_box(String b,
                   String value)
    {
        _ob_delegate_.set_string_box(b,
                                     value);
    }

    //
    // IDL:TestOBV/get_ulong_box:1.0
    //
    public TestULongBox
    get_ulong_box(int value)
    {
        return _ob_delegate_.get_ulong_box(value);
    }

    //
    // IDL:TestOBV/set_ulong_box:1.0
    //
    public void
    set_ulong_box(TestULongBox b,
                  int value)
    {
        _ob_delegate_.set_ulong_box(b,
                                    value);
    }

    //
    // IDL:TestOBV/get_fix_struct_box:1.0
    //
    public TestFixStruct
    get_fix_struct_box(TestFixStruct value)
    {
        return _ob_delegate_.get_fix_struct_box(value);
    }

    //
    // IDL:TestOBV/set_fix_struct_box:1.0
    //
    public void
    set_fix_struct_box(TestFixStruct b,
                       TestFixStruct value)
    {
        _ob_delegate_.set_fix_struct_box(b,
                                         value);
    }

    //
    // IDL:TestOBV/get_var_struct_box:1.0
    //
    public TestVarStruct
    get_var_struct_box(TestVarStruct value)
    {
        return _ob_delegate_.get_var_struct_box(value);
    }

    //
    // IDL:TestOBV/set_var_struct_box:1.0
    //
    public void
    set_var_struct_box(TestVarStruct b,
                       TestVarStruct value)
    {
        _ob_delegate_.set_var_struct_box(b,
                                         value);
    }

    //
    // IDL:TestOBV/get_fix_union_box:1.0
    //
    public TestFixUnion
    get_fix_union_box(TestFixUnion value)
    {
        return _ob_delegate_.get_fix_union_box(value);
    }

    //
    // IDL:TestOBV/set_fix_union_box:1.0
    //
    public void
    set_fix_union_box(TestFixUnion b,
                      TestFixUnion value)
    {
        _ob_delegate_.set_fix_union_box(b,
                                        value);
    }

    //
    // IDL:TestOBV/get_var_union_box:1.0
    //
    public TestVarUnion
    get_var_union_box(TestVarUnion value)
    {
        return _ob_delegate_.get_var_union_box(value);
    }

    //
    // IDL:TestOBV/set_var_union_box:1.0
    //
    public void
    set_var_union_box(TestVarUnion b,
                      TestVarUnion value)
    {
        _ob_delegate_.set_var_union_box(b,
                                        value);
    }

    //
    // IDL:TestOBV/get_anon_seq_box:1.0
    //
    public short[]
    get_anon_seq_box(int length)
    {
        return _ob_delegate_.get_anon_seq_box(length);
    }

    //
    // IDL:TestOBV/set_anon_seq_box:1.0
    //
    public void
    set_anon_seq_box(short[] b,
                     int length)
    {
        _ob_delegate_.set_anon_seq_box(b,
                                       length);
    }

    //
    // IDL:TestOBV/get_string_seq_box:1.0
    //
    public String[]
    get_string_seq_box(String[] value)
    {
        return _ob_delegate_.get_string_seq_box(value);
    }

    //
    // IDL:TestOBV/set_string_seq_box:1.0
    //
    public void
    set_string_seq_box(String[] b,
                       String[] value)
    {
        _ob_delegate_.set_string_seq_box(b,
                                         value);
    }

    //
    // IDL:TestOBV/get_ai_interface:1.0
    //
    public TestAbstract
    get_ai_interface()
    {
        return _ob_delegate_.get_ai_interface();
    }

    //
    // IDL:TestOBV/set_ai_interface:1.0
    //
    public void
    set_ai_interface(TestAbstract a)
    {
        _ob_delegate_.set_ai_interface(a);
    }

    //
    // IDL:TestOBV/get_ai_interface_any:1.0
    //
    public org.omg.CORBA.Any
    get_ai_interface_any()
    {
        return _ob_delegate_.get_ai_interface_any();
    }

    //
    // IDL:TestOBV/set_ai_interface_any:1.0
    //
    public void
    set_ai_interface_any(org.omg.CORBA.Any a)
    {
        _ob_delegate_.set_ai_interface_any(a);
    }

    //
    // IDL:TestOBV/get_ai_value:1.0
    //
    public TestAbstract
    get_ai_value()
    {
        return _ob_delegate_.get_ai_value();
    }

    //
    // IDL:TestOBV/set_ai_value:1.0
    //
    public void
    set_ai_value(TestAbstract a)
    {
        _ob_delegate_.set_ai_value(a);
    }

    //
    // IDL:TestOBV/get_ai_value_any:1.0
    //
    public org.omg.CORBA.Any
    get_ai_value_any()
    {
        return _ob_delegate_.get_ai_value_any();
    }

    //
    // IDL:TestOBV/set_ai_value_any:1.0
    //
    public void
    set_ai_value_any(org.omg.CORBA.Any a)
    {
        _ob_delegate_.set_ai_value_any(a);
    }

    //
    // IDL:TestOBV/get_trunc1:1.0
    //
    public TestTruncBase
    get_trunc1()
    {
        return _ob_delegate_.get_trunc1();
    }

    //
    // IDL:TestOBV/get_trunc2:1.0
    //
    public TestTruncBase
    get_trunc2()
    {
        return _ob_delegate_.get_trunc2();
    }

    //
    // IDL:TestOBV/get_value_any:1.0
    //
    public org.omg.CORBA.Any
    get_value_any()
    {
        return _ob_delegate_.get_value_any();
    }

    //
    // IDL:TestOBV/get_valuesub_any:1.0
    //
    public org.omg.CORBA.Any
    get_valuesub_any()
    {
        return _ob_delegate_.get_valuesub_any();
    }

    //
    // IDL:TestOBV/get_valuesub_as_value_any:1.0
    //
    public org.omg.CORBA.Any
    get_valuesub_as_value_any()
    {
        return _ob_delegate_.get_valuesub_as_value_any();
    }

    //
    // IDL:TestOBV/get_custom_any:1.0
    //
    public org.omg.CORBA.Any
    get_custom_any()
    {
        return _ob_delegate_.get_custom_any();
    }

    //
    // IDL:TestOBV/get_trunc1_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc1_any()
    {
        return _ob_delegate_.get_trunc1_any();
    }

    //
    // IDL:TestOBV/get_trunc1_as_base_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc1_as_base_any()
    {
        return _ob_delegate_.get_trunc1_as_base_any();
    }

    //
    // IDL:TestOBV/get_trunc2_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc2_any()
    {
        return _ob_delegate_.get_trunc2_any();
    }

    //
    // IDL:TestOBV/get_trunc2_as_base_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc2_as_base_any()
    {
        return _ob_delegate_.get_trunc2_as_base_any();
    }

    //
    // IDL:TestOBV/remarshal_any:1.0
    //
    public void
    remarshal_any(org.omg.CORBA.Any a)
    {
        _ob_delegate_.remarshal_any(a);
    }

    //
    // IDL:TestOBV/get_two_value_anys:1.0
    //
    public void
    get_two_value_anys(org.omg.CORBA.AnyHolder a1,
                       org.omg.CORBA.AnyHolder a2)
    {
        _ob_delegate_.get_two_value_anys(a1,
                                         a2);
    }

    //
    // IDL:TestOBV/set_two_value_anys:1.0
    //
    public void
    set_two_value_anys(org.omg.CORBA.Any a1,
                       org.omg.CORBA.Any a2)
    {
        _ob_delegate_.set_two_value_anys(a1,
                                         a2);
    }

    //
    // IDL:TestOBV/get_value_as_value:1.0
    //
    public TestValueInterface
    get_value_as_value()
    {
        return _ob_delegate_.get_value_as_value();
    }

    //
    // IDL:TestOBV/get_value_as_interface:1.0
    //
    public TestInterface
    get_value_as_interface()
    {
        return _ob_delegate_.get_value_as_interface();
    }

    //
    // IDL:TestOBV/deactivate:1.0
    //
    public void
    deactivate()
    {
        _ob_delegate_.deactivate();
    }
}

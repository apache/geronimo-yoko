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
public abstract class TestOBVPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               TestOBVOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:TestOBV:1.0",
    };

    public TestOBV
    _this()
    {
        return TestOBVHelper.narrow(super._this_object());
    }

    public TestOBV
    _this(org.omg.CORBA.ORB orb)
    {
        return TestOBVHelper.narrow(super._this_object(orb));
    }

    public String[]
    _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectId)
    {
        return _ob_ids_;
    }

    public org.omg.CORBA.portable.OutputStream
    _invoke(String opName,
            org.omg.CORBA.portable.InputStream in,
            org.omg.CORBA.portable.ResponseHandler handler)
    {
        final String[] _ob_names =
        {
            "deactivate",
            "get_abs_custom",
            "get_abs_value1",
            "get_abs_value2",
            "get_ai_interface",
            "get_ai_interface_any",
            "get_ai_value",
            "get_ai_value_any",
            "get_anon_seq_box",
            "get_custom",
            "get_custom_any",
            "get_fix_struct_box",
            "get_fix_union_box",
            "get_node",
            "get_null_valuebase",
            "get_null_valuesub",
            "get_string_box",
            "get_string_seq_box",
            "get_trunc1",
            "get_trunc1_any",
            "get_trunc1_as_base_any",
            "get_trunc2",
            "get_trunc2_any",
            "get_trunc2_as_base_any",
            "get_two_value_anys",
            "get_two_values",
            "get_two_valuesubs_as_values",
            "get_ulong_box",
            "get_value",
            "get_value_any",
            "get_value_as_interface",
            "get_value_as_value",
            "get_valuesub",
            "get_valuesub_any",
            "get_valuesub_as_value",
            "get_valuesub_as_value_any",
            "get_var_struct_box",
            "get_var_union_box",
            "remarshal_any",
            "set_abs_custom",
            "set_abs_value1",
            "set_abs_value2",
            "set_ai_interface",
            "set_ai_interface_any",
            "set_ai_value",
            "set_ai_value_any",
            "set_anon_seq_box",
            "set_custom",
            "set_fix_struct_box",
            "set_fix_union_box",
            "set_node",
            "set_null_valuebase",
            "set_null_valuesub",
            "set_string_box",
            "set_string_seq_box",
            "set_two_value_anys",
            "set_two_values",
            "set_two_valuesubs_as_values",
            "set_ulong_box",
            "set_value",
            "set_valuesub",
            "set_valuesub_as_value",
            "set_var_struct_box",
            "set_var_union_box"
        };

        int _ob_left = 0;
        int _ob_right = _ob_names.length;
        int _ob_index = -1;

        while(_ob_left < _ob_right)
        {
            int _ob_m = (_ob_left + _ob_right) / 2;
            int _ob_res = _ob_names[_ob_m].compareTo(opName);
            if(_ob_res == 0)
            {
                _ob_index = _ob_m;
                break;
            }
            else if(_ob_res > 0)
                _ob_right = _ob_m;
            else
                _ob_left = _ob_m + 1;
        }

        if(_ob_index == -1 && opName.charAt(0) == '_')
        {
            _ob_left = 0;
            _ob_right = _ob_names.length;
            String _ob_ami_op =
                opName.substring(1);

            while(_ob_left < _ob_right)
            {
                int _ob_m = (_ob_left + _ob_right) / 2;
                int _ob_res = _ob_names[_ob_m].compareTo(_ob_ami_op);
                if(_ob_res == 0)
                {
                    _ob_index = _ob_m;
                    break;
                }
                else if(_ob_res > 0)
                    _ob_right = _ob_m;
                else
                    _ob_left = _ob_m + 1;
            }
        }

        switch(_ob_index)
        {
        case 0: // deactivate
            return _OB_op_deactivate(in, handler);

        case 1: // get_abs_custom
            return _OB_op_get_abs_custom(in, handler);

        case 2: // get_abs_value1
            return _OB_op_get_abs_value1(in, handler);

        case 3: // get_abs_value2
            return _OB_op_get_abs_value2(in, handler);

        case 4: // get_ai_interface
            return _OB_op_get_ai_interface(in, handler);

        case 5: // get_ai_interface_any
            return _OB_op_get_ai_interface_any(in, handler);

        case 6: // get_ai_value
            return _OB_op_get_ai_value(in, handler);

        case 7: // get_ai_value_any
            return _OB_op_get_ai_value_any(in, handler);

        case 8: // get_anon_seq_box
            return _OB_op_get_anon_seq_box(in, handler);

        case 9: // get_custom
            return _OB_op_get_custom(in, handler);

        case 10: // get_custom_any
            return _OB_op_get_custom_any(in, handler);

        case 11: // get_fix_struct_box
            return _OB_op_get_fix_struct_box(in, handler);

        case 12: // get_fix_union_box
            return _OB_op_get_fix_union_box(in, handler);

        case 13: // get_node
            return _OB_op_get_node(in, handler);

        case 14: // get_null_valuebase
            return _OB_op_get_null_valuebase(in, handler);

        case 15: // get_null_valuesub
            return _OB_op_get_null_valuesub(in, handler);

        case 16: // get_string_box
            return _OB_op_get_string_box(in, handler);

        case 17: // get_string_seq_box
            return _OB_op_get_string_seq_box(in, handler);

        case 18: // get_trunc1
            return _OB_op_get_trunc1(in, handler);

        case 19: // get_trunc1_any
            return _OB_op_get_trunc1_any(in, handler);

        case 20: // get_trunc1_as_base_any
            return _OB_op_get_trunc1_as_base_any(in, handler);

        case 21: // get_trunc2
            return _OB_op_get_trunc2(in, handler);

        case 22: // get_trunc2_any
            return _OB_op_get_trunc2_any(in, handler);

        case 23: // get_trunc2_as_base_any
            return _OB_op_get_trunc2_as_base_any(in, handler);

        case 24: // get_two_value_anys
            return _OB_op_get_two_value_anys(in, handler);

        case 25: // get_two_values
            return _OB_op_get_two_values(in, handler);

        case 26: // get_two_valuesubs_as_values
            return _OB_op_get_two_valuesubs_as_values(in, handler);

        case 27: // get_ulong_box
            return _OB_op_get_ulong_box(in, handler);

        case 28: // get_value
            return _OB_op_get_value(in, handler);

        case 29: // get_value_any
            return _OB_op_get_value_any(in, handler);

        case 30: // get_value_as_interface
            return _OB_op_get_value_as_interface(in, handler);

        case 31: // get_value_as_value
            return _OB_op_get_value_as_value(in, handler);

        case 32: // get_valuesub
            return _OB_op_get_valuesub(in, handler);

        case 33: // get_valuesub_any
            return _OB_op_get_valuesub_any(in, handler);

        case 34: // get_valuesub_as_value
            return _OB_op_get_valuesub_as_value(in, handler);

        case 35: // get_valuesub_as_value_any
            return _OB_op_get_valuesub_as_value_any(in, handler);

        case 36: // get_var_struct_box
            return _OB_op_get_var_struct_box(in, handler);

        case 37: // get_var_union_box
            return _OB_op_get_var_union_box(in, handler);

        case 38: // remarshal_any
            return _OB_op_remarshal_any(in, handler);

        case 39: // set_abs_custom
            return _OB_op_set_abs_custom(in, handler);

        case 40: // set_abs_value1
            return _OB_op_set_abs_value1(in, handler);

        case 41: // set_abs_value2
            return _OB_op_set_abs_value2(in, handler);

        case 42: // set_ai_interface
            return _OB_op_set_ai_interface(in, handler);

        case 43: // set_ai_interface_any
            return _OB_op_set_ai_interface_any(in, handler);

        case 44: // set_ai_value
            return _OB_op_set_ai_value(in, handler);

        case 45: // set_ai_value_any
            return _OB_op_set_ai_value_any(in, handler);

        case 46: // set_anon_seq_box
            return _OB_op_set_anon_seq_box(in, handler);

        case 47: // set_custom
            return _OB_op_set_custom(in, handler);

        case 48: // set_fix_struct_box
            return _OB_op_set_fix_struct_box(in, handler);

        case 49: // set_fix_union_box
            return _OB_op_set_fix_union_box(in, handler);

        case 50: // set_node
            return _OB_op_set_node(in, handler);

        case 51: // set_null_valuebase
            return _OB_op_set_null_valuebase(in, handler);

        case 52: // set_null_valuesub
            return _OB_op_set_null_valuesub(in, handler);

        case 53: // set_string_box
            return _OB_op_set_string_box(in, handler);

        case 54: // set_string_seq_box
            return _OB_op_set_string_seq_box(in, handler);

        case 55: // set_two_value_anys
            return _OB_op_set_two_value_anys(in, handler);

        case 56: // set_two_values
            return _OB_op_set_two_values(in, handler);

        case 57: // set_two_valuesubs_as_values
            return _OB_op_set_two_valuesubs_as_values(in, handler);

        case 58: // set_ulong_box
            return _OB_op_set_ulong_box(in, handler);

        case 59: // set_value
            return _OB_op_set_value(in, handler);

        case 60: // set_valuesub
            return _OB_op_set_valuesub(in, handler);

        case 61: // set_valuesub_as_value
            return _OB_op_set_valuesub_as_value(in, handler);

        case 62: // set_var_struct_box
            return _OB_op_set_var_struct_box(in, handler);

        case 63: // set_var_union_box
            return _OB_op_set_var_union_box(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_deactivate(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        deactivate();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_abs_custom(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbsValue1 _ob_r = get_abs_custom();
        out = handler.createReply();
        TestAbsValue1Helper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_abs_value1(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbsValue1 _ob_r = get_abs_value1();
        out = handler.createReply();
        TestAbsValue1Helper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_abs_value2(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbsValue2 _ob_r = get_abs_value2();
        out = handler.createReply();
        TestAbsValue2Helper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_ai_interface(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbstract _ob_r = get_ai_interface();
        out = handler.createReply();
        TestAbstractHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_ai_interface_any(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_ai_interface_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_ai_value(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbstract _ob_r = get_ai_value();
        out = handler.createReply();
        TestAbstractHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_ai_value_any(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_ai_value_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_anon_seq_box(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        int _ob_a0 = in.read_ulong();
        short[] _ob_r = get_anon_seq_box(_ob_a0);
        out = handler.createReply();
        TestAnonSeqBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_custom(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestCustom _ob_r = get_custom();
        out = handler.createReply();
        TestCustomHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_custom_any(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_custom_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_fix_struct_box(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestFixStruct _ob_a0 = TestFixStructHelper.read(in);
        TestFixStruct _ob_r = get_fix_struct_box(_ob_a0);
        out = handler.createReply();
        TestFixStructBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_fix_union_box(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestFixUnion _ob_a0 = TestFixUnionHelper.read(in);
        TestFixUnion _ob_r = get_fix_union_box(_ob_a0);
        out = handler.createReply();
        TestFixUnionBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_node(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestNodeHolder _ob_ah0 = new TestNodeHolder();
        org.omg.CORBA.IntHolder _ob_ah1 = new org.omg.CORBA.IntHolder();
        get_node(_ob_ah0, _ob_ah1);
        out = handler.createReply();
        TestNodeHelper.write(out, _ob_ah0.value);
        out.write_ulong(_ob_ah1.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_null_valuebase(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        java.io.Serializable _ob_r = get_null_valuebase();
        out = handler.createReply();
        org.omg.CORBA.ValueBaseHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_null_valuesub(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValueSub _ob_r = get_null_valuesub();
        out = handler.createReply();
        TestValueSubHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_string_box(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_a0 = in.read_string();
        String _ob_r = get_string_box(_ob_a0);
        out = handler.createReply();
        TestStringBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_string_seq_box(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String[] _ob_a0 = TestStringSeqHelper.read(in);
        String[] _ob_r = get_string_seq_box(_ob_a0);
        out = handler.createReply();
        TestStringSeqBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_trunc1(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestTruncBase _ob_r = get_trunc1();
        out = handler.createReply();
        TestTruncBaseHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_trunc1_any(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_trunc1_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_trunc1_as_base_any(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_trunc1_as_base_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_trunc2(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestTruncBase _ob_r = get_trunc2();
        out = handler.createReply();
        TestTruncBaseHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_trunc2_any(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_trunc2_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_trunc2_as_base_any(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_trunc2_as_base_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_two_value_anys(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.AnyHolder _ob_ah0 = new org.omg.CORBA.AnyHolder();
        org.omg.CORBA.AnyHolder _ob_ah1 = new org.omg.CORBA.AnyHolder();
        get_two_value_anys(_ob_ah0, _ob_ah1);
        out = handler.createReply();
        out.write_any(_ob_ah0.value);
        out.write_any(_ob_ah1.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_two_values(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValueHolder _ob_ah0 = new TestValueHolder();
        TestValueHolder _ob_ah1 = new TestValueHolder();
        get_two_values(_ob_ah0, _ob_ah1);
        out = handler.createReply();
        TestValueHelper.write(out, _ob_ah0.value);
        TestValueHelper.write(out, _ob_ah1.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_two_valuesubs_as_values(org.omg.CORBA.portable.InputStream in,
                                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValueHolder _ob_ah0 = new TestValueHolder();
        TestValueHolder _ob_ah1 = new TestValueHolder();
        get_two_valuesubs_as_values(_ob_ah0, _ob_ah1);
        out = handler.createReply();
        TestValueHelper.write(out, _ob_ah0.value);
        TestValueHelper.write(out, _ob_ah1.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_ulong_box(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        int _ob_a0 = in.read_ulong();
        TestULongBox _ob_r = get_ulong_box(_ob_a0);
        out = handler.createReply();
        TestULongBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_value(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue _ob_r = get_value();
        out = handler.createReply();
        TestValueHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_value_any(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_value_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_value_as_interface(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestInterface _ob_r = get_value_as_interface();
        out = handler.createReply();
        TestInterfaceHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_value_as_value(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValueInterface _ob_r = get_value_as_value();
        out = handler.createReply();
        TestValueInterfaceHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_valuesub(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValueSub _ob_r = get_valuesub();
        out = handler.createReply();
        TestValueSubHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_valuesub_any(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_valuesub_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_valuesub_as_value(org.omg.CORBA.portable.InputStream in,
                                 org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue _ob_r = get_valuesub_as_value();
        out = handler.createReply();
        TestValueHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_valuesub_as_value_any(org.omg.CORBA.portable.InputStream in,
                                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_r = get_valuesub_as_value_any();
        out = handler.createReply();
        out.write_any(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_var_struct_box(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestVarStruct _ob_a0 = TestVarStructHelper.read(in);
        TestVarStruct _ob_r = get_var_struct_box(_ob_a0);
        out = handler.createReply();
        TestVarStructBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_var_union_box(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestVarUnion _ob_a0 = TestVarUnionHelper.read(in);
        TestVarUnion _ob_r = get_var_union_box(_ob_a0);
        out = handler.createReply();
        TestVarUnionBoxHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_remarshal_any(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_a0 = in.read_any();
        remarshal_any(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_abs_custom(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbsValue1 _ob_a0 = TestAbsValue1Helper.read(in);
        set_abs_custom(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_abs_value1(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbsValue1 _ob_a0 = TestAbsValue1Helper.read(in);
        set_abs_value1(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_abs_value2(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbsValue2 _ob_a0 = TestAbsValue2Helper.read(in);
        set_abs_value2(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_ai_interface(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbstract _ob_a0 = TestAbstractHelper.read(in);
        set_ai_interface(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_ai_interface_any(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_a0 = in.read_any();
        set_ai_interface_any(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_ai_value(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbstract _ob_a0 = TestAbstractHelper.read(in);
        set_ai_value(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_ai_value_any(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_a0 = in.read_any();
        set_ai_value_any(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_anon_seq_box(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        short[] _ob_a0 = TestAnonSeqBoxHelper.read(in);
        int _ob_a1 = in.read_ulong();
        set_anon_seq_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_custom(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestCustom _ob_a0 = TestCustomHelper.read(in);
        set_custom(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_fix_struct_box(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestFixStruct _ob_a0 = TestFixStructBoxHelper.read(in);
        TestFixStruct _ob_a1 = TestFixStructHelper.read(in);
        set_fix_struct_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_fix_union_box(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestFixUnion _ob_a0 = TestFixUnionBoxHelper.read(in);
        TestFixUnion _ob_a1 = TestFixUnionHelper.read(in);
        set_fix_union_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_node(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestNode _ob_a0 = TestNodeHelper.read(in);
        set_node(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_null_valuebase(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        java.io.Serializable _ob_a0 = org.omg.CORBA.ValueBaseHelper.read(in);
        set_null_valuebase(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_null_valuesub(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValueSub _ob_a0 = TestValueSubHelper.read(in);
        set_null_valuesub(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_string_box(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_a0 = TestStringBoxHelper.read(in);
        String _ob_a1 = in.read_string();
        set_string_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_string_seq_box(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String[] _ob_a0 = TestStringSeqBoxHelper.read(in);
        String[] _ob_a1 = TestStringSeqHelper.read(in);
        set_string_seq_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_two_value_anys(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Any _ob_a0 = in.read_any();
        org.omg.CORBA.Any _ob_a1 = in.read_any();
        set_two_value_anys(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_two_values(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue _ob_a0 = TestValueHelper.read(in);
        TestValue _ob_a1 = TestValueHelper.read(in);
        set_two_values(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_two_valuesubs_as_values(org.omg.CORBA.portable.InputStream in,
                                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue _ob_a0 = TestValueHelper.read(in);
        TestValue _ob_a1 = TestValueHelper.read(in);
        set_two_valuesubs_as_values(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_ulong_box(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestULongBox _ob_a0 = TestULongBoxHelper.read(in);
        int _ob_a1 = in.read_ulong();
        set_ulong_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_value(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue _ob_a0 = TestValueHelper.read(in);
        set_value(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_valuesub(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValueSub _ob_a0 = TestValueSubHelper.read(in);
        set_valuesub(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_valuesub_as_value(org.omg.CORBA.portable.InputStream in,
                                 org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue _ob_a0 = TestValueHelper.read(in);
        set_valuesub_as_value(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_var_struct_box(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestVarStruct _ob_a0 = TestVarStructBoxHelper.read(in);
        TestVarStruct _ob_a1 = TestVarStructHelper.read(in);
        set_var_struct_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_var_union_box(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestVarUnion _ob_a0 = TestVarUnionBoxHelper.read(in);
        TestVarUnion _ob_a1 = TestVarUnionHelper.read(in);
        set_var_union_box(_ob_a0, _ob_a1);
        out = handler.createReply();
        return out;
    }
}

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
public abstract class TestOBVColoPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               TestOBVColoOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:TestOBVColo:1.0",
    };

    public TestOBVColo
    _this()
    {
        return TestOBVColoHelper.narrow(super._this_object());
    }

    public TestOBVColo
    _this(org.omg.CORBA.ORB orb)
    {
        return TestOBVColoHelper.narrow(super._this_object(orb));
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
            "_get_test_abstract_attribute",
            "_get_test_value_attribute",
            "_get_test_value_seq_attribute",
            "_get_test_value_struct_attribute",
            "_get_test_value_union_attribute",
            "_set_test_abstract_attribute",
            "_set_test_value_attribute",
            "_set_test_value_seq_attribute",
            "_set_test_value_struct_attribute",
            "_set_test_value_union_attribute",
            "set_expected_count",
            "test_abstract_op",
            "test_value_op",
            "test_value_seq_op",
            "test_value_struct_op",
            "test_value_union_op"
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
        case 0: // _get_test_abstract_attribute
            return _OB_att_get_test_abstract_attribute(in, handler);

        case 1: // _get_test_value_attribute
            return _OB_att_get_test_value_attribute(in, handler);

        case 2: // _get_test_value_seq_attribute
            return _OB_att_get_test_value_seq_attribute(in, handler);

        case 3: // _get_test_value_struct_attribute
            return _OB_att_get_test_value_struct_attribute(in, handler);

        case 4: // _get_test_value_union_attribute
            return _OB_att_get_test_value_union_attribute(in, handler);

        case 5: // _set_test_abstract_attribute
            return _OB_att_set_test_abstract_attribute(in, handler);

        case 6: // _set_test_value_attribute
            return _OB_att_set_test_value_attribute(in, handler);

        case 7: // _set_test_value_seq_attribute
            return _OB_att_set_test_value_seq_attribute(in, handler);

        case 8: // _set_test_value_struct_attribute
            return _OB_att_set_test_value_struct_attribute(in, handler);

        case 9: // _set_test_value_union_attribute
            return _OB_att_set_test_value_union_attribute(in, handler);

        case 10: // set_expected_count
            return _OB_op_set_expected_count(in, handler);

        case 11: // test_abstract_op
            return _OB_op_test_abstract_op(in, handler);

        case 12: // test_value_op
            return _OB_op_test_value_op(in, handler);

        case 13: // test_value_seq_op
            return _OB_op_test_value_seq_op(in, handler);

        case 14: // test_value_struct_op
            return _OB_op_test_value_struct_op(in, handler);

        case 15: // test_value_union_op
            return _OB_op_test_value_union_op(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_test_abstract_attribute(org.omg.CORBA.portable.InputStream in,
                                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestAbstract _ob_r = test_abstract_attribute();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        TestAbstractHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_test_value_attribute(org.omg.CORBA.portable.InputStream in,
                                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestValue _ob_r = test_value_attribute();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        TestValueHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_test_value_seq_attribute(org.omg.CORBA.portable.InputStream in,
                                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestValue[] _ob_r = test_value_seq_attribute();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        test.obv.TestOBVColoPackage.VSeqHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_test_value_struct_attribute(org.omg.CORBA.portable.InputStream in,
                                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        test.obv.TestOBVColoPackage.SV _ob_r = test_value_struct_attribute();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        test.obv.TestOBVColoPackage.SVHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_test_value_union_attribute(org.omg.CORBA.portable.InputStream in,
                                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        test.obv.TestOBVColoPackage.UV _ob_r = test_value_union_attribute();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        test.obv.TestOBVColoPackage.UVHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_test_abstract_attribute(org.omg.CORBA.portable.InputStream in,
                                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestAbstract _ob_a = TestAbstractHelper.read(in);
        test_abstract_attribute(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_test_value_attribute(org.omg.CORBA.portable.InputStream in,
                                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestValue _ob_a = TestValueHelper.read(in);
        test_value_attribute(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_test_value_seq_attribute(org.omg.CORBA.portable.InputStream in,
                                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        TestValue[] _ob_a = test.obv.TestOBVColoPackage.VSeqHelper.read(in);
        test_value_seq_attribute(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_test_value_struct_attribute(org.omg.CORBA.portable.InputStream in,
                                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        test.obv.TestOBVColoPackage.SV _ob_a = test.obv.TestOBVColoPackage.SVHelper.read(in);
        test_value_struct_attribute(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_test_value_union_attribute(org.omg.CORBA.portable.InputStream in,
                                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        test.obv.TestOBVColoPackage.UV _ob_a = test.obv.TestOBVColoPackage.UVHelper.read(in);
        test_value_union_attribute(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_set_expected_count(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        int _ob_a0 = in.read_long();
        set_expected_count(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_test_abstract_op(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestAbstract _ob_a0 = TestAbstractHelper.read(in);
        test_abstract_op(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_test_value_op(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue _ob_a0 = TestValueHelper.read(in);
        TestValueHolder _ob_ah1 = new TestValueHolder();
        _ob_ah1.value = TestValueHelper.read(in);
        TestValueHolder _ob_ah2 = new TestValueHolder();
        TestValue _ob_r = test_value_op(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        TestValueHelper.write(out, _ob_r);
        TestValueHelper.write(out, _ob_ah1.value);
        TestValueHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_test_value_seq_op(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestValue[] _ob_a0 = test.obv.TestOBVColoPackage.VSeqHelper.read(in);
        test.obv.TestOBVColoPackage.VSeqHolder _ob_ah1 = new test.obv.TestOBVColoPackage.VSeqHolder();
        _ob_ah1.value = test.obv.TestOBVColoPackage.VSeqHelper.read(in);
        test.obv.TestOBVColoPackage.VSeqHolder _ob_ah2 = new test.obv.TestOBVColoPackage.VSeqHolder();
        TestValue[] _ob_r = test_value_seq_op(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        test.obv.TestOBVColoPackage.VSeqHelper.write(out, _ob_r);
        test.obv.TestOBVColoPackage.VSeqHelper.write(out, _ob_ah1.value);
        test.obv.TestOBVColoPackage.VSeqHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_test_value_struct_op(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        test.obv.TestOBVColoPackage.SV _ob_a0 = test.obv.TestOBVColoPackage.SVHelper.read(in);
        test.obv.TestOBVColoPackage.SVHolder _ob_ah1 = new test.obv.TestOBVColoPackage.SVHolder();
        _ob_ah1.value = test.obv.TestOBVColoPackage.SVHelper.read(in);
        test.obv.TestOBVColoPackage.SVHolder _ob_ah2 = new test.obv.TestOBVColoPackage.SVHolder();
        test.obv.TestOBVColoPackage.SV _ob_r = test_value_struct_op(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        test.obv.TestOBVColoPackage.SVHelper.write(out, _ob_r);
        test.obv.TestOBVColoPackage.SVHelper.write(out, _ob_ah1.value);
        test.obv.TestOBVColoPackage.SVHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_test_value_union_op(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        test.obv.TestOBVColoPackage.UV _ob_a0 = test.obv.TestOBVColoPackage.UVHelper.read(in);
        test.obv.TestOBVColoPackage.UVHolder _ob_ah1 = new test.obv.TestOBVColoPackage.UVHolder();
        _ob_ah1.value = test.obv.TestOBVColoPackage.UVHelper.read(in);
        test.obv.TestOBVColoPackage.UVHolder _ob_ah2 = new test.obv.TestOBVColoPackage.UVHolder();
        test.obv.TestOBVColoPackage.UV _ob_r = test_value_union_op(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        test.obv.TestOBVColoPackage.UVHelper.write(out, _ob_r);
        test.obv.TestOBVColoPackage.UVHelper.write(out, _ob_ah1.value);
        test.obv.TestOBVColoPackage.UVHelper.write(out, _ob_ah2.value);
        return out;
    }
}

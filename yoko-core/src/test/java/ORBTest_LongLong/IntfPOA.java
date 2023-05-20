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
package ORBTest_LongLong;

//
// IDL:ORBTest_LongLong/Intf:1.0
//
public abstract class IntfPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IntfOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_LongLong/Intf:1.0",
    };

    public Intf
    _this()
    {
        return IntfHelper.narrow(super._this_object());
    }

    public Intf
    _this(org.omg.CORBA.ORB orb)
    {
        return IntfHelper.narrow(super._this_object(orb));
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
            "_get_attrLongLong",
            "_get_attrULongLong",
            "_set_attrLongLong",
            "_set_attrULongLong",
            "opLongLong",
            "opLongLongEx",
            "opULongLong",
            "opULongLongEx"
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
        case 0: // _get_attrLongLong
            return _OB_att_get_attrLongLong(in, handler);

        case 1: // _get_attrULongLong
            return _OB_att_get_attrULongLong(in, handler);

        case 2: // _set_attrLongLong
            return _OB_att_set_attrLongLong(in, handler);

        case 3: // _set_attrULongLong
            return _OB_att_set_attrULongLong(in, handler);

        case 4: // opLongLong
            return _OB_op_opLongLong(in, handler);

        case 5: // opLongLongEx
            return _OB_op_opLongLongEx(in, handler);

        case 6: // opULongLong
            return _OB_op_opULongLong(in, handler);

        case 7: // opULongLongEx
            return _OB_op_opULongLongEx(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrLongLong(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        long _ob_r = attrLongLong();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_longlong(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrULongLong(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        long _ob_r = attrULongLong();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_ulonglong(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrLongLong(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        long _ob_a = in.read_longlong();
        attrLongLong(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrULongLong(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        long _ob_a = in.read_ulonglong();
        attrULongLong(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opLongLong(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        long _ob_a0 = in.read_longlong();
        org.omg.CORBA.LongHolder _ob_ah1 = new org.omg.CORBA.LongHolder();
        _ob_ah1.value = in.read_longlong();
        org.omg.CORBA.LongHolder _ob_ah2 = new org.omg.CORBA.LongHolder();
        long _ob_r = opLongLong(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_longlong(_ob_r);
        out.write_longlong(_ob_ah1.value);
        out.write_longlong(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opLongLongEx(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            long _ob_a0 = in.read_longlong();
            org.omg.CORBA.LongHolder _ob_ah1 = new org.omg.CORBA.LongHolder();
            _ob_ah1.value = in.read_longlong();
            org.omg.CORBA.LongHolder _ob_ah2 = new org.omg.CORBA.LongHolder();
            long _ob_r = opLongLongEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_longlong(_ob_r);
            out.write_longlong(_ob_ah1.value);
            out.write_longlong(_ob_ah2.value);
        }
        catch(ExLongLong _ob_ex)
        {
            out = handler.createExceptionReply();
            ExLongLongHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opULongLong(org.omg.CORBA.portable.InputStream in,
                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        long _ob_a0 = in.read_ulonglong();
        org.omg.CORBA.LongHolder _ob_ah1 = new org.omg.CORBA.LongHolder();
        _ob_ah1.value = in.read_ulonglong();
        org.omg.CORBA.LongHolder _ob_ah2 = new org.omg.CORBA.LongHolder();
        long _ob_r = opULongLong(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_ulonglong(_ob_r);
        out.write_ulonglong(_ob_ah1.value);
        out.write_ulonglong(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opULongLongEx(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            long _ob_a0 = in.read_ulonglong();
            org.omg.CORBA.LongHolder _ob_ah1 = new org.omg.CORBA.LongHolder();
            _ob_ah1.value = in.read_ulonglong();
            org.omg.CORBA.LongHolder _ob_ah2 = new org.omg.CORBA.LongHolder();
            long _ob_r = opULongLongEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_ulonglong(_ob_r);
            out.write_ulonglong(_ob_ah1.value);
            out.write_ulonglong(_ob_ah2.value);
        }
        catch(ExULongLong _ob_ex)
        {
            out = handler.createExceptionReply();
            ExULongLongHelper.write(out, _ob_ex);
        }
        return out;
    }
}

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
package ORBTest_WChar;

//
// IDL:ORBTest_WChar/Intf:1.0
//
public abstract class IntfPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IntfOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_WChar/Intf:1.0",
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
            "_get_attrWChar",
            "_get_attrWString",
            "_set_attrWChar",
            "_set_attrWString",
            "opWChar",
            "opWCharEx",
            "opWString",
            "opWStringEx"
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
        case 0: // _get_attrWChar
            return _OB_att_get_attrWChar(in, handler);

        case 1: // _get_attrWString
            return _OB_att_get_attrWString(in, handler);

        case 2: // _set_attrWChar
            return _OB_att_set_attrWChar(in, handler);

        case 3: // _set_attrWString
            return _OB_att_set_attrWString(in, handler);

        case 4: // opWChar
            return _OB_op_opWChar(in, handler);

        case 5: // opWCharEx
            return _OB_op_opWCharEx(in, handler);

        case 6: // opWString
            return _OB_op_opWString(in, handler);

        case 7: // opWStringEx
            return _OB_op_opWStringEx(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrWChar(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        char _ob_r = attrWChar();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_wchar(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrWString(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_r = attrWString();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_wstring(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrWChar(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        char _ob_a = in.read_wchar();
        attrWChar(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrWString(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_a = in.read_wstring();
        attrWString(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opWChar(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        char _ob_a0 = in.read_wchar();
        org.omg.CORBA.CharHolder _ob_ah1 = new org.omg.CORBA.CharHolder();
        _ob_ah1.value = in.read_wchar();
        org.omg.CORBA.CharHolder _ob_ah2 = new org.omg.CORBA.CharHolder();
        char _ob_r = opWChar(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_wchar(_ob_r);
        out.write_wchar(_ob_ah1.value);
        out.write_wchar(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opWCharEx(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            char _ob_a0 = in.read_wchar();
            org.omg.CORBA.CharHolder _ob_ah1 = new org.omg.CORBA.CharHolder();
            _ob_ah1.value = in.read_wchar();
            org.omg.CORBA.CharHolder _ob_ah2 = new org.omg.CORBA.CharHolder();
            char _ob_r = opWCharEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_wchar(_ob_r);
            out.write_wchar(_ob_ah1.value);
            out.write_wchar(_ob_ah2.value);
        }
        catch(ExWChar _ob_ex)
        {
            out = handler.createExceptionReply();
            ExWCharHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opWString(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_a0 = in.read_wstring();
        org.omg.CORBA.StringHolder _ob_ah1 = new org.omg.CORBA.StringHolder();
        _ob_ah1.value = in.read_wstring();
        org.omg.CORBA.StringHolder _ob_ah2 = new org.omg.CORBA.StringHolder();
        String _ob_r = opWString(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        out.write_wstring(_ob_r);
        out.write_wstring(_ob_ah1.value);
        out.write_wstring(_ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opWStringEx(org.omg.CORBA.portable.InputStream in,
                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String _ob_a0 = in.read_wstring();
            org.omg.CORBA.StringHolder _ob_ah1 = new org.omg.CORBA.StringHolder();
            _ob_ah1.value = in.read_wstring();
            org.omg.CORBA.StringHolder _ob_ah2 = new org.omg.CORBA.StringHolder();
            String _ob_r = opWStringEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            out.write_wstring(_ob_r);
            out.write_wstring(_ob_ah1.value);
            out.write_wstring(_ob_ah2.value);
        }
        catch(ExWString _ob_ex)
        {
            out = handler.createExceptionReply();
            ExWStringHelper.write(out, _ob_ex);
        }
        return out;
    }
}

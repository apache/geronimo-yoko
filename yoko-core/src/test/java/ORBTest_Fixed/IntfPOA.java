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
package ORBTest_Fixed;

//
// IDL:ORBTest_Fixed/Intf:1.0
//
public abstract class IntfPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IntfOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_Fixed/Intf:1.0",
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
            "_get_attrFixed",
            "_set_attrFixed",
            "opFixed",
            "opFixedEx"
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
        case 0: // _get_attrFixed
            return _OB_att_get_attrFixed(in, handler);

        case 1: // _set_attrFixed
            return _OB_att_set_attrFixed(in, handler);

        case 2: // opFixed
            return _OB_op_opFixed(in, handler);

        case 3: // opFixedEx
            return _OB_op_opFixedEx(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_attrFixed(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        java.math.BigDecimal _ob_r = attrFixed();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        TestFixedHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_attrFixed(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        java.math.BigDecimal _ob_a = TestFixedHelper.read(in);
        attrFixed(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixed(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        java.math.BigDecimal _ob_a0 = TestFixedHelper.read(in);
        org.omg.CORBA.FixedHolder _ob_ah1 = new org.omg.CORBA.FixedHolder();
        _ob_ah1.value = TestFixedHelper.read(in);
        org.omg.CORBA.FixedHolder _ob_ah2 = new org.omg.CORBA.FixedHolder();
        java.math.BigDecimal _ob_r = opFixed(_ob_a0, _ob_ah1, _ob_ah2);
        out = handler.createReply();
        TestFixedHelper.write(out, _ob_r);
        TestFixedHelper.write(out, _ob_ah1.value);
        TestFixedHelper.write(out, _ob_ah2.value);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_opFixedEx(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            java.math.BigDecimal _ob_a0 = TestFixedHelper.read(in);
            org.omg.CORBA.FixedHolder _ob_ah1 = new org.omg.CORBA.FixedHolder();
            _ob_ah1.value = TestFixedHelper.read(in);
            org.omg.CORBA.FixedHolder _ob_ah2 = new org.omg.CORBA.FixedHolder();
            java.math.BigDecimal _ob_r = opFixedEx(_ob_a0, _ob_ah1, _ob_ah2);
            out = handler.createReply();
            TestFixedHelper.write(out, _ob_r);
            TestFixedHelper.write(out, _ob_ah1.value);
            TestFixedHelper.write(out, _ob_ah2.value);
        }
        catch(ExFixed _ob_ex)
        {
            out = handler.createExceptionReply();
            ExFixedHelper.write(out, _ob_ex);
        }
        return out;
    }
}

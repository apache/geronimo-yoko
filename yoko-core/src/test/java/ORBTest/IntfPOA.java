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
package ORBTest;

//
// IDL:ORBTest/Intf:1.0
//
public abstract class IntfPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IntfOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:ORBTest/Intf:1.0",
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
            "concurrent_request_execution",
            "deactivate",
            "get_ORB_type",
            "get_test_case_list"
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
        case 0: // concurrent_request_execution
            return _OB_op_concurrent_request_execution(in, handler);

        case 1: // deactivate
            return _OB_op_deactivate(in, handler);

        case 2: // get_ORB_type
            return _OB_op_get_ORB_type(in, handler);

        case 3: // get_test_case_list
            return _OB_op_get_test_case_list(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_concurrent_request_execution(org.omg.CORBA.portable.InputStream in,
                                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        boolean _ob_r = concurrent_request_execution();
        out = handler.createReply();
        out.write_boolean(_ob_r);
        return out;
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
    _OB_op_get_ORB_type(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        ORBType _ob_r = get_ORB_type();
        out = handler.createReply();
        ORBTypeHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_test_case_list(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        TestCase[] _ob_r = get_test_case_list();
        out = handler.createReply();
        TestCaseListHelper.write(out, _ob_r);
        return out;
    }
}

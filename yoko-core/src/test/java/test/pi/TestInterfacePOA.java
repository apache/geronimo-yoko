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
package test.pi;

//
// IDL:TestInterface:1.0
//
public abstract class TestInterfacePOA
    extends org.apache.yoko.orb.PortableServer.Servant
    implements TestInterfaceOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:TestInterface:1.0",
    };

    public TestInterface
    _this()
    {
        return TestInterfaceHelper.narrow(super._this_object());
    }

    public TestInterface
    _this(org.omg.CORBA.ORB orb)
    {
        return TestInterfaceHelper.narrow(super._this_object(orb));
    }

    public String[]
    _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectId)
    {
        return _ob_ids_;
    }

    public void
    _OB_dispatch(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        final String _ob_op = _ob_up.operation();

        final String[] _ob_names =
        {
            "_get_string_attrib",
            "_get_struct_attrib",
            "_set_string_attrib",
            "_set_struct_attrib",
            "deactivate",
            "location_forward",
            "noargs",
            "noargs_oneway",
            "one_string_in",
            "one_string_inout",
            "one_string_out",
            "one_string_return",
            "one_struct_in",
            "one_struct_inout",
            "one_struct_out",
            "one_struct_return",
            "systemexception",
            "test_service_context",
            "userexception"
        };

        int _ob_left = 0;
        int _ob_right = _ob_names.length;
        int _ob_index = -1;

        while(_ob_left < _ob_right)
        {
            int _ob_m = (_ob_left + _ob_right) / 2;
            int _ob_res = _ob_names[_ob_m].compareTo(_ob_op);
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

        if(_ob_index == -1 && _ob_op.charAt(0) == '_')
        {
            _ob_left = 0;
            _ob_right = _ob_names.length;
            String _ob_ami_op =
                _ob_op.substring(1);

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
        case 0: // _get_string_attrib
            _OB_att_get_string_attrib(_ob_up);
            return;

        case 1: // _get_struct_attrib
            _OB_att_get_struct_attrib(_ob_up);
            return;

        case 2: // _set_string_attrib
            _OB_att_set_string_attrib(_ob_up);
            return;

        case 3: // _set_struct_attrib
            _OB_att_set_struct_attrib(_ob_up);
            return;

        case 4: // deactivate
            _OB_op_deactivate(_ob_up);
            return;

        case 5: // location_forward
            _OB_op_location_forward(_ob_up);
            return;

        case 6: // noargs
            _OB_op_noargs(_ob_up);
            return;

        case 7: // noargs_oneway
            _OB_op_noargs_oneway(_ob_up);
            return;

        case 8: // one_string_in
            _OB_op_one_string_in(_ob_up);
            return;

        case 9: // one_string_inout
            _OB_op_one_string_inout(_ob_up);
            return;

        case 10: // one_string_out
            _OB_op_one_string_out(_ob_up);
            return;

        case 11: // one_string_return
            _OB_op_one_string_return(_ob_up);
            return;

        case 12: // one_struct_in
            _OB_op_one_struct_in(_ob_up);
            return;

        case 13: // one_struct_inout
            _OB_op_one_struct_inout(_ob_up);
            return;

        case 14: // one_struct_out
            _OB_op_one_struct_out(_ob_up);
            return;

        case 15: // one_struct_return
            _OB_op_one_struct_return(_ob_up);
            return;

        case 16: // systemexception
            _OB_op_systemexception(_ob_up);
            return;

        case 17: // test_service_context
            _OB_op_test_service_context(_ob_up);
            return;

        case 18: // userexception
            _OB_op_userexception(_ob_up);
            return;
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private void
    _OB_att_get_string_attrib(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        org.omg.CORBA.StringHolder _ob_rh = new org.omg.CORBA.StringHolder();
        org.apache.yoko.orb.OB.ParameterDesc _ob_retDesc = new org.apache.yoko.orb.OB.ParameterDesc(_ob_rh, _orb().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), 0);
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, _ob_retDesc, null);
        _OB_postUnmarshal(_ob_up);
        _ob_rh.value = string_attrib();
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            out.write_string(_ob_rh.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_att_get_struct_attrib(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        test.pi.TestInterfacePackage.sHolder _ob_rh = new test.pi.TestInterfacePackage.sHolder();
        org.apache.yoko.orb.OB.ParameterDesc _ob_retDesc = new org.apache.yoko.orb.OB.ParameterDesc(_ob_rh, test.pi.TestInterfacePackage.sHelper.type(), 0);
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, _ob_retDesc, null);
        _OB_postUnmarshal(_ob_up);
        _ob_rh.value = struct_attrib();
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            test.pi.TestInterfacePackage.sHelper.write(out, _ob_rh.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_att_set_string_attrib(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        org.omg.CORBA.StringHolder _ob_ah = new org.omg.CORBA.StringHolder();
        org.omg.CORBA.portable.InputStream in = _OB_preUnmarshal(_ob_up);
        try
        {
            _ob_ah.value = in.read_string();
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_unmarshalEx(_ob_up, _ob_ex);
        }
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah, _orb().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), 0)
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        string_attrib(_ob_ah.value);
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_att_set_struct_attrib(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        test.pi.TestInterfacePackage.sHolder _ob_ah = new test.pi.TestInterfacePackage.sHolder();
        org.omg.CORBA.portable.InputStream in = _OB_preUnmarshal(_ob_up);
        try
        {
            _ob_ah.value = test.pi.TestInterfacePackage.sHelper.read(in);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_unmarshalEx(_ob_up, _ob_ex);
        }
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah, test.pi.TestInterfacePackage.sHelper.type(), 0)
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        struct_attrib(_ob_ah.value);
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_deactivate(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, null, null);
        _OB_postUnmarshal(_ob_up);
        deactivate();
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_location_forward(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, null, null);
        _OB_postUnmarshal(_ob_up);
        location_forward();
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_noargs(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, null, null);
        _OB_postUnmarshal(_ob_up);
        noargs();
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_noargs_oneway(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, null, null);
        _OB_postUnmarshal(_ob_up);
        noargs_oneway();
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_string_in(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        org.omg.CORBA.StringHolder _ob_ah0 = new org.omg.CORBA.StringHolder();
        org.omg.CORBA.portable.InputStream in = _OB_preUnmarshal(_ob_up);
        try
        {
            _ob_ah0.value = in.read_string();
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_unmarshalEx(_ob_up, _ob_ex);
        }
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah0, _orb().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), 0) /*in*/
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        one_string_in(_ob_ah0.value);
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_string_inout(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        org.omg.CORBA.StringHolder _ob_ah0 = new org.omg.CORBA.StringHolder();
        org.omg.CORBA.portable.InputStream in = _OB_preUnmarshal(_ob_up);
        try
        {
            _ob_ah0.value = in.read_string();
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_unmarshalEx(_ob_up, _ob_ex);
        }
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah0, _orb().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), 2) /*inout*/
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        one_string_inout(_ob_ah0);
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            out.write_string(_ob_ah0.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_string_out(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        org.omg.CORBA.StringHolder _ob_ah0 = new org.omg.CORBA.StringHolder();
        _OB_preUnmarshal(_ob_up);
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah0, _orb().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), 1) /*out*/
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        one_string_out(_ob_ah0);
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            out.write_string(_ob_ah0.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_string_return(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        org.omg.CORBA.StringHolder _ob_rh = new org.omg.CORBA.StringHolder();
        org.apache.yoko.orb.OB.ParameterDesc _ob_retDesc = new org.apache.yoko.orb.OB.ParameterDesc(_ob_rh, _orb().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), 0);
        _OB_setArgDesc(_ob_up, null, _ob_retDesc, null);
        _OB_postUnmarshal(_ob_up);
        _ob_rh.value = one_string_return();
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            out.write_string(_ob_rh.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_struct_in(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        test.pi.TestInterfacePackage.sHolder _ob_ah0 = new test.pi.TestInterfacePackage.sHolder();
        org.omg.CORBA.portable.InputStream in = _OB_preUnmarshal(_ob_up);
        try
        {
            _ob_ah0.value = test.pi.TestInterfacePackage.sHelper.read(in);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_unmarshalEx(_ob_up, _ob_ex);
        }
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah0, test.pi.TestInterfacePackage.sHelper.type(), 0) /*in*/
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        one_struct_in(_ob_ah0.value);
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_struct_inout(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        test.pi.TestInterfacePackage.sHolder _ob_ah0 = new test.pi.TestInterfacePackage.sHolder();
        org.omg.CORBA.portable.InputStream in = _OB_preUnmarshal(_ob_up);
        try
        {
            _ob_ah0.value = test.pi.TestInterfacePackage.sHelper.read(in);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_unmarshalEx(_ob_up, _ob_ex);
        }
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah0, test.pi.TestInterfacePackage.sHelper.type(), 2) /*inout*/
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        one_struct_inout(_ob_ah0);
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            test.pi.TestInterfacePackage.sHelper.write(out, _ob_ah0.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_struct_out(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        test.pi.TestInterfacePackage.sHolder _ob_ah0 = new test.pi.TestInterfacePackage.sHolder();
        _OB_preUnmarshal(_ob_up);
        org.apache.yoko.orb.OB.ParameterDesc[] _ob_desc =
        {
            new org.apache.yoko.orb.OB.ParameterDesc(_ob_ah0, test.pi.TestInterfacePackage.sHelper.type(), 1) /*out*/
        };
        _OB_setArgDesc(_ob_up, _ob_desc, null, null);
        _OB_postUnmarshal(_ob_up);
        one_struct_out(_ob_ah0);
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            test.pi.TestInterfacePackage.sHelper.write(out, _ob_ah0.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_one_struct_return(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        test.pi.TestInterfacePackage.sHolder _ob_rh = new test.pi.TestInterfacePackage.sHolder();
        org.apache.yoko.orb.OB.ParameterDesc _ob_retDesc = new org.apache.yoko.orb.OB.ParameterDesc(_ob_rh, test.pi.TestInterfacePackage.sHelper.type(), 0);
        _OB_setArgDesc(_ob_up, null, _ob_retDesc, null);
        _OB_postUnmarshal(_ob_up);
        _ob_rh.value = one_struct_return();
        _OB_postinvoke(_ob_up);
        org.omg.CORBA.portable.OutputStream out = _OB_preMarshal(_ob_up);
        try
        {
            test.pi.TestInterfacePackage.sHelper.write(out, _ob_rh.value);
        }
        catch(org.omg.CORBA.SystemException _ob_ex)
        {
            _OB_marshalEx(_ob_up, _ob_ex);
        }
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_systemexception(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, null, null);
        _OB_postUnmarshal(_ob_up);
        systemexception();
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_test_service_context(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        _OB_setArgDesc(_ob_up, null, null, null);
        _OB_postUnmarshal(_ob_up);
        test_service_context();
        _OB_postinvoke(_ob_up);
        _OB_preMarshal(_ob_up);
        _OB_postMarshal(_ob_up);
    }

    private void
    _OB_op_userexception(org.apache.yoko.orb.OB.Upcall _ob_up)
        throws org.apache.yoko.orb.OB.LocationForward
    {
        _OB_preUnmarshal(_ob_up);
        org.omg.CORBA.TypeCode[] _ob_exceptions =
        {
            test.pi.TestInterfacePackage.userHelper.type()
        };
        _OB_setArgDesc(_ob_up, null, null, _ob_exceptions);
        _OB_postUnmarshal(_ob_up);
        try
        {
            userexception();
            _OB_postinvoke(_ob_up);
            _OB_preMarshal(_ob_up);
            _OB_postMarshal(_ob_up);
        }
        catch(test.pi.TestInterfacePackage.user _ob_ex)
        {
            org.omg.CORBA.portable.OutputStream out = _OB_beginUserException(_ob_up, _ob_ex);
            if(out != null)
                test.pi.TestInterfacePackage.userHelper.write(out, _ob_ex);
        }
    }
}

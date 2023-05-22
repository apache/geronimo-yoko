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
package test.poa;

//
// IDL:POAManagerProxy:1.0
//
public abstract class POAManagerProxyPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               POAManagerProxyOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:POAManagerProxy:1.0",
    };

    public POAManagerProxy
    _this()
    {
        return POAManagerProxyHelper.narrow(super._this_object());
    }

    public POAManagerProxy
    _this(org.omg.CORBA.ORB orb)
    {
        return POAManagerProxyHelper.narrow(super._this_object(orb));
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
            "activate",
            "deactivate",
            "discard_requests",
            "get_state",
            "hold_requests"
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
        case 0: // activate
            return _OB_op_activate(in, handler);

        case 1: // deactivate
            return _OB_op_deactivate(in, handler);

        case 2: // discard_requests
            return _OB_op_discard_requests(in, handler);

        case 3: // get_state
            return _OB_op_get_state(in, handler);

        case 4: // hold_requests
            return _OB_op_hold_requests(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_activate(org.omg.CORBA.portable.InputStream in,
                    org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            activate();
            out = handler.createReply();
        }
        catch(test.poa.POAManagerProxyPackage.AdapterInactive _ob_ex)
        {
            out = handler.createExceptionReply();
            test.poa.POAManagerProxyPackage.AdapterInactiveHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_deactivate(org.omg.CORBA.portable.InputStream in,
                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            boolean _ob_a0 = in.read_boolean();
            boolean _ob_a1 = in.read_boolean();
            deactivate(_ob_a0, _ob_a1);
            out = handler.createReply();
        }
        catch(test.poa.POAManagerProxyPackage.AdapterInactive _ob_ex)
        {
            out = handler.createExceptionReply();
            test.poa.POAManagerProxyPackage.AdapterInactiveHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_discard_requests(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            boolean _ob_a0 = in.read_boolean();
            discard_requests(_ob_a0);
            out = handler.createReply();
        }
        catch(test.poa.POAManagerProxyPackage.AdapterInactive _ob_ex)
        {
            out = handler.createExceptionReply();
            test.poa.POAManagerProxyPackage.AdapterInactiveHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_state(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        test.poa.POAManagerProxyPackage.State _ob_r = get_state();
        out = handler.createReply();
        test.poa.POAManagerProxyPackage.StateHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_hold_requests(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            boolean _ob_a0 = in.read_boolean();
            hold_requests(_ob_a0);
            out = handler.createReply();
        }
        catch(test.poa.POAManagerProxyPackage.AdapterInactive _ob_ex)
        {
            out = handler.createExceptionReply();
            test.poa.POAManagerProxyPackage.AdapterInactiveHelper.write(out, _ob_ex);
        }
        return out;
    }
}

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
package test.ins.URLTest;

//
// IDL:URLTest/IIOPAddress:1.0
//
public abstract class IIOPAddressPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IIOPAddressOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:URLTest/IIOPAddress:1.0",
    };

    public IIOPAddress
    _this()
    {
        return IIOPAddressHelper.narrow(super._this_object());
    }

    public IIOPAddress
    _this(org.omg.CORBA.ORB orb)
    {
        return IIOPAddressHelper.narrow(super._this_object(orb));
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
            "destroy",
            "getCorbalocURL",
            "getHost",
            "getIIOPAddress",
            "getKey",
            "getPort",
            "getString",
            "setString"
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

        case 1: // destroy
            return _OB_op_destroy(in, handler);

        case 2: // getCorbalocURL
            return _OB_op_getCorbalocURL(in, handler);

        case 3: // getHost
            return _OB_op_getHost(in, handler);

        case 4: // getIIOPAddress
            return _OB_op_getIIOPAddress(in, handler);

        case 5: // getKey
            return _OB_op_getKey(in, handler);

        case 6: // getPort
            return _OB_op_getPort(in, handler);

        case 7: // getString
            return _OB_op_getString(in, handler);

        case 8: // setString
            return _OB_op_setString(in, handler);
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
    _OB_op_destroy(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        destroy();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_getCorbalocURL(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_r = getCorbalocURL();
        out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_getHost(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_r = getHost();
        out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_getIIOPAddress(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_r = getIIOPAddress();
        out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_getKey(org.omg.CORBA.portable.InputStream in,
                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_r = getKey();
        out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_getPort(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        short _ob_r = getPort();
        out = handler.createReply();
        out.write_ushort(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_getString(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_r = getString();
        out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_setString(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        String _ob_a0 = in.read_string();
        setString(_ob_a0);
        out = handler.createReply();
        return out;
    }
}

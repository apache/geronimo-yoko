/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.yoko.orb.IMR;

//
// IDL:orb.yoko.apache.org/IMR/ServerDomain:1.0
//
public abstract class ServerDomainPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               ServerDomainOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:orb.yoko.apache.org/IMR/ServerDomain:1.0",
        "IDL:orb.yoko.apache.org/IMR/Domain:1.0"
    };

    public ServerDomain
    _this()
    {
        return ServerDomainHelper.narrow(super._this_object());
    }

    public ServerDomain
    _this(org.omg.CORBA.ORB orb)
    {
        return ServerDomainHelper.narrow(super._this_object(orb));
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
            "create_oad_record",
            "get_oad_record",
            "get_server_factory",
            "list_oads",
            "registerServer",
            "remove_oad_record",
            "startup"
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
        case 0: // create_oad_record
            return _OB_op_create_oad_record(in, handler);

        case 1: // get_oad_record
            return _OB_op_get_oad_record(in, handler);

        case 2: // get_server_factory
            return _OB_op_get_server_factory(in, handler);

        case 3: // list_oads
            return _OB_op_list_oads(in, handler);

        case 4: // registerServer
            return _OB_op_registerServer(in, handler);

        case 5: // remove_oad_record
            return _OB_op_remove_oad_record(in, handler);

        case 6: // startup
            return _OB_op_startup(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_create_oad_record(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String _ob_a0 = in.read_string();
            create_oad_record(_ob_a0);
            out = handler.createReply();
        }
        catch(OADAlreadyExists _ob_ex)
        {
            out = handler.createExceptionReply();
            OADAlreadyExistsHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_oad_record(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String _ob_a0 = in.read_string();
            OADInfo _ob_r = get_oad_record(_ob_a0);
            out = handler.createReply();
            OADInfoHelper.write(out, _ob_r);
        }
        catch(NoSuchOAD _ob_ex)
        {
            out = handler.createExceptionReply();
            NoSuchOADHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_server_factory(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        ServerFactory _ob_r = get_server_factory();
        out = handler.createReply();
        ServerFactoryHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_list_oads(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        OADInfo[] _ob_r = list_oads();
        out = handler.createReply();
        OADInfoSeqHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_registerServer(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String _ob_a0 = in.read_string();
            String _ob_a1 = in.read_string();
            String _ob_a2 = in.read_string();
            registerServer(_ob_a0, _ob_a1, _ob_a2);
            out = handler.createReply();
        }
        catch(ServerAlreadyRegistered _ob_ex)
        {
            out = handler.createExceptionReply();
            ServerAlreadyRegisteredHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_remove_oad_record(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String _ob_a0 = in.read_string();
            remove_oad_record(_ob_a0);
            out = handler.createReply();
        }
        catch(NoSuchOAD _ob_ex)
        {
            out = handler.createExceptionReply();
            NoSuchOADHelper.write(out, _ob_ex);
        }
        catch(OADRunning _ob_ex)
        {
            out = handler.createExceptionReply();
            OADRunningHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_startup(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String _ob_a0 = in.read_string();
            String _ob_a1 = in.read_string();
            org.omg.PortableInterceptor.ObjectReferenceTemplate _ob_a2 = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read(in);
            org.apache.yoko.orb.OAD.ProcessEndpointManagerHolder _ob_ah3 = new org.apache.yoko.orb.OAD.ProcessEndpointManagerHolder();
            ActiveState _ob_r = startup(_ob_a0, _ob_a1, _ob_a2, _ob_ah3);
            out = handler.createReply();
            ActiveStateHelper.write(out, _ob_r);
            org.apache.yoko.orb.OAD.ProcessEndpointManagerHelper.write(out, _ob_ah3.value);
        }
        catch(NoSuchServer _ob_ex)
        {
            out = handler.createExceptionReply();
            NoSuchServerHelper.write(out, _ob_ex);
        }
        catch(NoSuchOAD _ob_ex)
        {
            out = handler.createExceptionReply();
            NoSuchOADHelper.write(out, _ob_ex);
        }
        catch(OADNotRunning _ob_ex)
        {
            out = handler.createExceptionReply();
            OADNotRunningHelper.write(out, _ob_ex);
        }
        return out;
    }
}

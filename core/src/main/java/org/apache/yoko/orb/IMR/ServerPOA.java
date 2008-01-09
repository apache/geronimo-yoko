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
// IDL:orb.yoko.apache.org/IMR/Server:1.0
//
public abstract class ServerPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               ServerOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:orb.yoko.apache.org/IMR/Server:1.0",
    };

    public Server
    _this()
    {
        return ServerHelper.narrow(super._this_object());
    }

    public Server
    _this(org.omg.CORBA.ORB orb)
    {
        return ServerHelper.narrow(super._this_object(orb));
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
            "_get_activatePOAs",
            "_get_args",
            "_get_directory",
            "_get_exec",
            "_get_failureTimeout",
            "_get_host",
            "_get_id",
            "_get_manual",
            "_get_maxForks",
            "_get_mode",
            "_get_name",
            "_get_status",
            "_get_timesForked",
            "_get_updateTime",
            "_get_updateTimeout",
            "_set_activatePOAs",
            "_set_args",
            "_set_directory",
            "_set_exec",
            "_set_failureTimeout",
            "_set_host",
            "_set_maxForks",
            "_set_mode",
            "_set_name",
            "_set_updateTimeout",
            "clear_error_state",
            "create_poa_record",
            "destroy",
            "get_poa_info",
            "list_poas",
            "remove_poa_record",
            "start",
            "stop"
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
        case 0: // _get_activatePOAs
            return _OB_att_get_activatePOAs(in, handler);

        case 1: // _get_args
            return _OB_att_get_args(in, handler);

        case 2: // _get_directory
            return _OB_att_get_directory(in, handler);

        case 3: // _get_exec
            return _OB_att_get_exec(in, handler);

        case 4: // _get_failureTimeout
            return _OB_att_get_failureTimeout(in, handler);

        case 5: // _get_host
            return _OB_att_get_host(in, handler);

        case 6: // _get_id
            return _OB_att_get_id(in, handler);

        case 7: // _get_manual
            return _OB_att_get_manual(in, handler);

        case 8: // _get_maxForks
            return _OB_att_get_maxForks(in, handler);

        case 9: // _get_mode
            return _OB_att_get_mode(in, handler);

        case 10: // _get_name
            return _OB_att_get_name(in, handler);

        case 11: // _get_status
            return _OB_att_get_status(in, handler);

        case 12: // _get_timesForked
            return _OB_att_get_timesForked(in, handler);

        case 13: // _get_updateTime
            return _OB_att_get_updateTime(in, handler);

        case 14: // _get_updateTimeout
            return _OB_att_get_updateTimeout(in, handler);

        case 15: // _set_activatePOAs
            return _OB_att_set_activatePOAs(in, handler);

        case 16: // _set_args
            return _OB_att_set_args(in, handler);

        case 17: // _set_directory
            return _OB_att_set_directory(in, handler);

        case 18: // _set_exec
            return _OB_att_set_exec(in, handler);

        case 19: // _set_failureTimeout
            return _OB_att_set_failureTimeout(in, handler);

        case 20: // _set_host
            return _OB_att_set_host(in, handler);

        case 21: // _set_maxForks
            return _OB_att_set_maxForks(in, handler);

        case 22: // _set_mode
            return _OB_att_set_mode(in, handler);

        case 23: // _set_name
            return _OB_att_set_name(in, handler);

        case 24: // _set_updateTimeout
            return _OB_att_set_updateTimeout(in, handler);

        case 25: // clear_error_state
            return _OB_op_clear_error_state(in, handler);

        case 26: // create_poa_record
            return _OB_op_create_poa_record(in, handler);

        case 27: // destroy
            return _OB_op_destroy(in, handler);

        case 28: // get_poa_info
            return _OB_op_get_poa_info(in, handler);

        case 29: // list_poas
            return _OB_op_list_poas(in, handler);

        case 30: // remove_poa_record
            return _OB_op_remove_poa_record(in, handler);

        case 31: // start
            return _OB_op_start(in, handler);

        case 32: // stop
            return _OB_op_stop(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_activatePOAs(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        boolean _ob_r = activatePOAs();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_boolean(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_args(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[] _ob_r = args();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        ArgListHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_directory(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_r = directory();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_exec(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_r = exec();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_failureTimeout(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_r = failureTimeout();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_long(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_host(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_r = host();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_id(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_r = id();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        ServerIDHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_manual(org.omg.CORBA.portable.InputStream in,
                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        boolean _ob_r = manual();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_boolean(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_maxForks(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        short _ob_r = maxForks();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_short(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_mode(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        ServerActivationMode _ob_r = mode();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        ServerActivationModeHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_name(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_r = name();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_string(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_status(org.omg.CORBA.portable.InputStream in,
                       org.omg.CORBA.portable.ResponseHandler handler)
    {
        ServerStatus _ob_r = status();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        ServerStatusHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_timesForked(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        short _ob_r = timesForked();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_short(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_updateTime(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_r = updateTime();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_long(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_updateTimeout(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_r = updateTimeout();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_long(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_activatePOAs(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        boolean _ob_a = in.read_boolean();
        activatePOAs(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_args(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String[] _ob_a = ArgListHelper.read(in);
        args(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_directory(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_a = in.read_string();
        directory(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_exec(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_a = in.read_string();
        exec(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_failureTimeout(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_a = in.read_long();
        failureTimeout(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_host(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_a = in.read_string();
        host(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_maxForks(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        short _ob_a = in.read_short();
        maxForks(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_mode(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        ServerActivationMode _ob_a = ServerActivationModeHelper.read(in);
        mode(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_name(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        String _ob_a = in.read_string();
        name(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_updateTimeout(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        int _ob_a = in.read_long();
        updateTimeout(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_clear_error_state(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        clear_error_state();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_create_poa_record(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String[] _ob_a0 = POANameHelper.read(in);
            create_poa_record(_ob_a0);
            out = handler.createReply();
        }
        catch(POAAlreadyRegistered _ob_ex)
        {
            out = handler.createExceptionReply();
            POAAlreadyRegisteredHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_destroy(org.omg.CORBA.portable.InputStream in,
                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            destroy();
            out = handler.createReply();
        }
        catch(ServerRunning _ob_ex)
        {
            out = handler.createExceptionReply();
            ServerRunningHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_poa_info(org.omg.CORBA.portable.InputStream in,
                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String[] _ob_a0 = POANameHelper.read(in);
            POAInfo _ob_r = get_poa_info(_ob_a0);
            out = handler.createReply();
            POAInfoHelper.write(out, _ob_r);
        }
        catch(_NoSuchPOA _ob_ex)
        {
            out = handler.createExceptionReply();
            _NoSuchPOAHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_list_poas(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        POAInfo[] _ob_r = list_poas();
        out = handler.createReply();
        POAInfoSeqHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_remove_poa_record(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            String[] _ob_a0 = POANameHelper.read(in);
            remove_poa_record(_ob_a0);
            out = handler.createReply();
        }
        catch(_NoSuchPOA _ob_ex)
        {
            out = handler.createExceptionReply();
            _NoSuchPOAHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_start(org.omg.CORBA.portable.InputStream in,
                 org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            start();
            out = handler.createReply();
        }
        catch(ServerRunning _ob_ex)
        {
            out = handler.createExceptionReply();
            ServerRunningHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_stop(org.omg.CORBA.portable.InputStream in,
                org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            stop();
            out = handler.createReply();
        }
        catch(OADNotRunning _ob_ex)
        {
            out = handler.createExceptionReply();
            OADNotRunningHelper.write(out, _ob_ex);
        }
        catch(ServerNotRunning _ob_ex)
        {
            out = handler.createExceptionReply();
            ServerNotRunningHelper.write(out, _ob_ex);
        }
        return out;
    }
}

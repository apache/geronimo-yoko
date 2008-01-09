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

package org.apache.yoko.orb.OAD;

//
// IDL:orb.yoko.apache.org/OAD/ProcessEndpoint:1.0
//
public abstract class ProcessEndpointPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               ProcessEndpointOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:orb.yoko.apache.org/OAD/ProcessEndpoint:1.0",
    };

    public ProcessEndpoint
    _this()
    {
        return ProcessEndpointHelper.narrow(super._this_object());
    }

    public ProcessEndpoint
    _this(org.omg.CORBA.ORB orb)
    {
        return ProcessEndpointHelper.narrow(super._this_object(orb));
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
            "reestablish_link",
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
        case 0: // reestablish_link
            return _OB_op_reestablish_link(in, handler);

        case 1: // stop
            return _OB_op_stop(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION(
            org.apache.yoko.orb.OB.MinorCodes
                    .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
            org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_reestablish_link(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        ProcessEndpointManager _ob_a0 = ProcessEndpointManagerHelper.read(in);
        reestablish_link(_ob_a0);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_stop(org.omg.CORBA.portable.InputStream in,
                org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        stop();
        out = handler.createReply();
        return out;
    }
}

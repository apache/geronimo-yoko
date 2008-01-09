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

package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/PersistentRequest:1.0
//
public abstract class PersistentRequestPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               PersistentRequestOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:omg.org/MessageRouting/PersistentRequest:1.0",
    };

    public PersistentRequest
    _this()
    {
        return PersistentRequestHelper.narrow(super._this_object());
    }

    public PersistentRequest
    _this(org.omg.CORBA.ORB orb)
    {
        return PersistentRequestHelper.narrow(super._this_object(orb));
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
            "_get_associated_handler",
            "_get_reply_available",
            "_set_associated_handler",
            "get_reply"
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
        case 0: // _get_associated_handler
            return _OB_att_get_associated_handler(in, handler);

        case 1: // _get_reply_available
            return _OB_att_get_reply_available(in, handler);

        case 2: // _set_associated_handler
            return _OB_att_set_associated_handler(in, handler);

        case 3: // get_reply
            return _OB_op_get_reply(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_associated_handler(org.omg.CORBA.portable.InputStream in,
                                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.Messaging.ReplyHandler _ob_r = associated_handler();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        org.omg.Messaging.ReplyHandlerHelper.write(out, _ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_get_reply_available(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        boolean _ob_r = reply_available();
        org.omg.CORBA.portable.OutputStream out = handler.createReply();
        out.write_boolean(_ob_r);
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_att_set_associated_handler(org.omg.CORBA.portable.InputStream in,
                                   org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.Messaging.ReplyHandler _ob_a = org.omg.Messaging.ReplyHandlerHelper.read(in);
        associated_handler(_ob_a);
        return handler.createReply();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_get_reply(org.omg.CORBA.portable.InputStream in,
                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            boolean _ob_a0 = in.read_boolean();
            int _ob_a1 = in.read_ulong();
            MessageBodyHolder _ob_ah2 = new MessageBodyHolder();
            org.omg.GIOP.ReplyStatusType_1_2 _ob_r = get_reply(_ob_a0, _ob_a1, _ob_ah2);
            out = handler.createReply();
            org.omg.GIOP.ReplyStatusType_1_2Helper.write(out, _ob_r);
            MessageBodyHelper.write(out, _ob_ah2.value);
        }
        catch(ReplyNotAvailable _ob_ex)
        {
            out = handler.createExceptionReply();
            ReplyNotAvailableHelper.write(out, _ob_ex);
        }
        return out;
    }
}

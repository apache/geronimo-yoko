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
// IDL:omg.org/MessageRouting/RouterAdmin:1.0
//
public abstract class RouterAdminPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               RouterAdminOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:omg.org/MessageRouting/RouterAdmin:1.0",
    };

    public RouterAdmin
    _this()
    {
        return RouterAdminHelper.narrow(super._this_object());
    }

    public RouterAdmin
    _this(org.omg.CORBA.ORB orb)
    {
        return RouterAdminHelper.narrow(super._this_object(orb));
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
            "register_destination",
            "resume_destination",
            "suspend_destination",
            "unregister_destination"
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
        case 0: // register_destination
            return _OB_op_register_destination(in, handler);

        case 1: // resume_destination
            return _OB_op_resume_destination(in, handler);

        case 2: // suspend_destination
            return _OB_op_suspend_destination(in, handler);

        case 3: // unregister_destination
            return _OB_op_unregister_destination(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_register_destination(org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        org.omg.CORBA.Object _ob_a0 = in.read_Object();
        boolean _ob_a1 = in.read_boolean();
        RetryPolicy _ob_a2 = RetryPolicyHelper.read(in);
        DecayPolicy _ob_a3 = DecayPolicyHelper.read(in);
        register_destination(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_resume_destination(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            org.omg.CORBA.Object _ob_a0 = in.read_Object();
            resume_destination(_ob_a0);
            out = handler.createReply();
        }
        catch(InvalidState _ob_ex)
        {
            out = handler.createExceptionReply();
            InvalidStateHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_suspend_destination(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            org.omg.CORBA.Object _ob_a0 = in.read_Object();
            ResumePolicy _ob_a1 = ResumePolicyHelper.read(in);
            suspend_destination(_ob_a0, _ob_a1);
            out = handler.createReply();
        }
        catch(InvalidState _ob_ex)
        {
            out = handler.createExceptionReply();
            InvalidStateHelper.write(out, _ob_ex);
        }
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_unregister_destination(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        try
        {
            org.omg.CORBA.Object _ob_a0 = in.read_Object();
            unregister_destination(_ob_a0);
            out = handler.createReply();
        }
        catch(InvalidState _ob_ex)
        {
            out = handler.createExceptionReply();
            InvalidStateHelper.write(out, _ob_ex);
        }
        return out;
    }
}

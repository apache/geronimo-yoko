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
package ORBTest_Exceptions;

//
// IDL:ORBTest_Exceptions/Intf:1.0
//
public abstract class IntfPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IntfOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_Exceptions/Intf:1.0",
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
            "op_BAD_INV_ORDER_Ex",
            "op_BAD_OPERATION_Ex",
            "op_BAD_PARAM_Ex",
            "op_BAD_TYPECODE_Ex",
            "op_COMM_FAILURE_Ex",
            "op_DATA_CONVERSION_Ex",
            "op_IMP_LIMIT_Ex",
            "op_INITIALIZE_Ex",
            "op_INTERNAL_Ex",
            "op_INV_OBJREF_Ex",
            "op_INV_POLICY_Ex",
            "op_MARSHAL_Ex",
            "op_NO_IMPLEMENT_Ex",
            "op_NO_MEMORY_Ex",
            "op_NO_PERMISSION_Ex",
            "op_NO_RESOURCES_Ex",
            "op_NO_RESPONSE_Ex",
            "op_OBJECT_NOT_EXIST_Ex",
            "op_OBJ_ADAPTER_Ex",
            "op_TRANSIENT_Ex",
            "op_UNKNOWN_Ex"
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
        case 0: // op_BAD_INV_ORDER_Ex
            return _OB_op_op_BAD_INV_ORDER_Ex(in, handler);

        case 1: // op_BAD_OPERATION_Ex
            return _OB_op_op_BAD_OPERATION_Ex(in, handler);

        case 2: // op_BAD_PARAM_Ex
            return _OB_op_op_BAD_PARAM_Ex(in, handler);

        case 3: // op_BAD_TYPECODE_Ex
            return _OB_op_op_BAD_TYPECODE_Ex(in, handler);

        case 4: // op_COMM_FAILURE_Ex
            return _OB_op_op_COMM_FAILURE_Ex(in, handler);

        case 5: // op_DATA_CONVERSION_Ex
            return _OB_op_op_DATA_CONVERSION_Ex(in, handler);

        case 6: // op_IMP_LIMIT_Ex
            return _OB_op_op_IMP_LIMIT_Ex(in, handler);

        case 7: // op_INITIALIZE_Ex
            return _OB_op_op_INITIALIZE_Ex(in, handler);

        case 8: // op_INTERNAL_Ex
            return _OB_op_op_INTERNAL_Ex(in, handler);

        case 9: // op_INV_OBJREF_Ex
            return _OB_op_op_INV_OBJREF_Ex(in, handler);

        case 10: // op_INV_POLICY_Ex
            return _OB_op_op_INV_POLICY_Ex(in, handler);

        case 11: // op_MARSHAL_Ex
            return _OB_op_op_MARSHAL_Ex(in, handler);

        case 12: // op_NO_IMPLEMENT_Ex
            return _OB_op_op_NO_IMPLEMENT_Ex(in, handler);

        case 13: // op_NO_MEMORY_Ex
            return _OB_op_op_NO_MEMORY_Ex(in, handler);

        case 14: // op_NO_PERMISSION_Ex
            return _OB_op_op_NO_PERMISSION_Ex(in, handler);

        case 15: // op_NO_RESOURCES_Ex
            return _OB_op_op_NO_RESOURCES_Ex(in, handler);

        case 16: // op_NO_RESPONSE_Ex
            return _OB_op_op_NO_RESPONSE_Ex(in, handler);

        case 17: // op_OBJECT_NOT_EXIST_Ex
            return _OB_op_op_OBJECT_NOT_EXIST_Ex(in, handler);

        case 18: // op_OBJ_ADAPTER_Ex
            return _OB_op_op_OBJ_ADAPTER_Ex(in, handler);

        case 19: // op_TRANSIENT_Ex
            return _OB_op_op_TRANSIENT_Ex(in, handler);

        case 20: // op_UNKNOWN_Ex
            return _OB_op_op_UNKNOWN_Ex(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_BAD_INV_ORDER_Ex(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_BAD_INV_ORDER_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_BAD_OPERATION_Ex(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_BAD_OPERATION_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_BAD_PARAM_Ex(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_BAD_PARAM_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_BAD_TYPECODE_Ex(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_BAD_TYPECODE_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_COMM_FAILURE_Ex(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_COMM_FAILURE_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_DATA_CONVERSION_Ex(org.omg.CORBA.portable.InputStream in,
                                 org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_DATA_CONVERSION_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_IMP_LIMIT_Ex(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_IMP_LIMIT_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INITIALIZE_Ex(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INITIALIZE_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INTERNAL_Ex(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INTERNAL_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INV_OBJREF_Ex(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INV_OBJREF_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INV_POLICY_Ex(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INV_POLICY_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_MARSHAL_Ex(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_MARSHAL_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_NO_IMPLEMENT_Ex(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_NO_IMPLEMENT_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_NO_MEMORY_Ex(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_NO_MEMORY_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_NO_PERMISSION_Ex(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_NO_PERMISSION_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_NO_RESOURCES_Ex(org.omg.CORBA.portable.InputStream in,
                              org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_NO_RESOURCES_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_NO_RESPONSE_Ex(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_NO_RESPONSE_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_OBJECT_NOT_EXIST_Ex(org.omg.CORBA.portable.InputStream in,
                                  org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_OBJECT_NOT_EXIST_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_OBJ_ADAPTER_Ex(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_OBJ_ADAPTER_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_TRANSIENT_Ex(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_TRANSIENT_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_UNKNOWN_Ex(org.omg.CORBA.portable.InputStream in,
                         org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_UNKNOWN_Ex();
        out = handler.createReply();
        return out;
    }
}

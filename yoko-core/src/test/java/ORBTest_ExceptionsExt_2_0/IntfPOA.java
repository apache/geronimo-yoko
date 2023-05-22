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
package ORBTest_ExceptionsExt_2_0;

//
// IDL:ORBTest_ExceptionsExt_2_0/Intf:1.0
//
public abstract class IntfPOA
    extends org.omg.PortableServer.Servant
    implements org.omg.CORBA.portable.InvokeHandler,
               IntfOperations
{
    static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_ExceptionsExt_2_0/Intf:1.0",
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
            "op_BAD_CONTEXT_Ex",
            "op_FREE_MEM_Ex",
            "op_INTF_REPOS_Ex",
            "op_INVALID_TRANSACTION_Ex",
            "op_INV_FLAG_Ex",
            "op_INV_IDENT_Ex",
            "op_PERSIST_STORE_Ex",
            "op_TRANSACTION_REQUIRED_Ex",
            "op_TRANSACTION_ROLLEDBACK_Ex"
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
        case 0: // op_BAD_CONTEXT_Ex
            return _OB_op_op_BAD_CONTEXT_Ex(in, handler);

        case 1: // op_FREE_MEM_Ex
            return _OB_op_op_FREE_MEM_Ex(in, handler);

        case 2: // op_INTF_REPOS_Ex
            return _OB_op_op_INTF_REPOS_Ex(in, handler);

        case 3: // op_INVALID_TRANSACTION_Ex
            return _OB_op_op_INVALID_TRANSACTION_Ex(in, handler);

        case 4: // op_INV_FLAG_Ex
            return _OB_op_op_INV_FLAG_Ex(in, handler);

        case 5: // op_INV_IDENT_Ex
            return _OB_op_op_INV_IDENT_Ex(in, handler);

        case 6: // op_PERSIST_STORE_Ex
            return _OB_op_op_PERSIST_STORE_Ex(in, handler);

        case 7: // op_TRANSACTION_REQUIRED_Ex
            return _OB_op_op_TRANSACTION_REQUIRED_Ex(in, handler);

        case 8: // op_TRANSACTION_ROLLEDBACK_Ex
            return _OB_op_op_TRANSACTION_ROLLEDBACK_Ex(in, handler);
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_BAD_CONTEXT_Ex(org.omg.CORBA.portable.InputStream in,
                             org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_BAD_CONTEXT_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_FREE_MEM_Ex(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_FREE_MEM_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INTF_REPOS_Ex(org.omg.CORBA.portable.InputStream in,
                            org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INTF_REPOS_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INVALID_TRANSACTION_Ex(org.omg.CORBA.portable.InputStream in,
                                     org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INVALID_TRANSACTION_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INV_FLAG_Ex(org.omg.CORBA.portable.InputStream in,
                          org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INV_FLAG_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_INV_IDENT_Ex(org.omg.CORBA.portable.InputStream in,
                           org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_INV_IDENT_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_PERSIST_STORE_Ex(org.omg.CORBA.portable.InputStream in,
                               org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_PERSIST_STORE_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_TRANSACTION_REQUIRED_Ex(org.omg.CORBA.portable.InputStream in,
                                      org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_TRANSACTION_REQUIRED_Ex();
        out = handler.createReply();
        return out;
    }

    private org.omg.CORBA.portable.OutputStream
    _OB_op_op_TRANSACTION_ROLLEDBACK_Ex(org.omg.CORBA.portable.InputStream in,
                                        org.omg.CORBA.portable.ResponseHandler handler)
    {
        org.omg.CORBA.portable.OutputStream out = null;
        op_TRANSACTION_ROLLEDBACK_Ex();
        out = handler.createReply();
        return out;
    }
}

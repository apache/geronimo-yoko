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
public class _IntfStub extends org.omg.CORBA.portable.ObjectImpl
                       implements Intf
{
    private static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_Exceptions/Intf:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = IntfOperations.class;

    //
    // IDL:ORBTest_Exceptions/Intf/op_UNKNOWN_Ex:1.0
    //
    public void
    op_UNKNOWN_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_UNKNOWN_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_UNKNOWN_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_UNKNOWN_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_PARAM_Ex:1.0
    //
    public void
    op_BAD_PARAM_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_BAD_PARAM_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_BAD_PARAM_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_BAD_PARAM_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_MEMORY_Ex:1.0
    //
    public void
    op_NO_MEMORY_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_NO_MEMORY_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_NO_MEMORY_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_NO_MEMORY_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_IMP_LIMIT_Ex:1.0
    //
    public void
    op_IMP_LIMIT_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_IMP_LIMIT_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_IMP_LIMIT_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_IMP_LIMIT_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_COMM_FAILURE_Ex:1.0
    //
    public void
    op_COMM_FAILURE_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_COMM_FAILURE_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_COMM_FAILURE_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_COMM_FAILURE_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INV_OBJREF_Ex:1.0
    //
    public void
    op_INV_OBJREF_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INV_OBJREF_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INV_OBJREF_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INV_OBJREF_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_PERMISSION_Ex:1.0
    //
    public void
    op_NO_PERMISSION_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_NO_PERMISSION_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_NO_PERMISSION_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_NO_PERMISSION_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INTERNAL_Ex:1.0
    //
    public void
    op_INTERNAL_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INTERNAL_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INTERNAL_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INTERNAL_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_MARSHAL_Ex:1.0
    //
    public void
    op_MARSHAL_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_MARSHAL_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_MARSHAL_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_MARSHAL_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INITIALIZE_Ex:1.0
    //
    public void
    op_INITIALIZE_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INITIALIZE_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INITIALIZE_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INITIALIZE_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_IMPLEMENT_Ex:1.0
    //
    public void
    op_NO_IMPLEMENT_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_NO_IMPLEMENT_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_NO_IMPLEMENT_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_NO_IMPLEMENT_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_TYPECODE_Ex:1.0
    //
    public void
    op_BAD_TYPECODE_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_BAD_TYPECODE_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_BAD_TYPECODE_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_BAD_TYPECODE_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_OPERATION_Ex:1.0
    //
    public void
    op_BAD_OPERATION_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_BAD_OPERATION_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_BAD_OPERATION_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_BAD_OPERATION_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_RESOURCES_Ex:1.0
    //
    public void
    op_NO_RESOURCES_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_NO_RESOURCES_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_NO_RESOURCES_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_NO_RESOURCES_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_NO_RESPONSE_Ex:1.0
    //
    public void
    op_NO_RESPONSE_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_NO_RESPONSE_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_NO_RESPONSE_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_NO_RESPONSE_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_BAD_INV_ORDER_Ex:1.0
    //
    public void
    op_BAD_INV_ORDER_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_BAD_INV_ORDER_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_BAD_INV_ORDER_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_BAD_INV_ORDER_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_TRANSIENT_Ex:1.0
    //
    public void
    op_TRANSIENT_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_TRANSIENT_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_TRANSIENT_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_TRANSIENT_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_OBJ_ADAPTER_Ex:1.0
    //
    public void
    op_OBJ_ADAPTER_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_OBJ_ADAPTER_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_OBJ_ADAPTER_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_OBJ_ADAPTER_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_DATA_CONVERSION_Ex:1.0
    //
    public void
    op_DATA_CONVERSION_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_DATA_CONVERSION_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_DATA_CONVERSION_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_DATA_CONVERSION_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_OBJECT_NOT_EXIST_Ex:1.0
    //
    public void
    op_OBJECT_NOT_EXIST_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_OBJECT_NOT_EXIST_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_OBJECT_NOT_EXIST_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_OBJECT_NOT_EXIST_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Exceptions/Intf/op_INV_POLICY_Ex:1.0
    //
    public void
    op_INV_POLICY_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INV_POLICY_Ex", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INV_POLICY_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INV_POLICY_Ex();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}

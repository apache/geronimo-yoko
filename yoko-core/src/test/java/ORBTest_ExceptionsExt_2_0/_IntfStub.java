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
public class _IntfStub extends org.omg.CORBA.portable.ObjectImpl
                       implements Intf
{
    private static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_ExceptionsExt_2_0/Intf:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = IntfOperations.class;

    //
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_PERSIST_STORE_Ex:1.0
    //
    public void
    op_PERSIST_STORE_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_PERSIST_STORE_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_PERSIST_STORE_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_PERSIST_STORE_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_FREE_MEM_Ex:1.0
    //
    public void
    op_FREE_MEM_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_FREE_MEM_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_FREE_MEM_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_FREE_MEM_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INV_IDENT_Ex:1.0
    //
    public void
    op_INV_IDENT_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INV_IDENT_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INV_IDENT_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INV_IDENT_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INV_FLAG_Ex:1.0
    //
    public void
    op_INV_FLAG_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INV_FLAG_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INV_FLAG_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INV_FLAG_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INTF_REPOS_Ex:1.0
    //
    public void
    op_INTF_REPOS_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INTF_REPOS_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INTF_REPOS_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INTF_REPOS_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_BAD_CONTEXT_Ex:1.0
    //
    public void
    op_BAD_CONTEXT_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_BAD_CONTEXT_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_BAD_CONTEXT_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_BAD_CONTEXT_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_TRANSACTION_REQUIRED_Ex:1.0
    //
    public void
    op_TRANSACTION_REQUIRED_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_TRANSACTION_REQUIRED_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_TRANSACTION_REQUIRED_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_TRANSACTION_REQUIRED_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_TRANSACTION_ROLLEDBACK_Ex:1.0
    //
    public void
    op_TRANSACTION_ROLLEDBACK_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_TRANSACTION_ROLLEDBACK_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_TRANSACTION_ROLLEDBACK_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_TRANSACTION_ROLLEDBACK_Ex();
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
    // IDL:ORBTest_ExceptionsExt_2_0/Intf/op_INVALID_TRANSACTION_Ex:1.0
    //
    public void
    op_INVALID_TRANSACTION_Ex()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("op_INVALID_TRANSACTION_Ex", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("op_INVALID_TRANSACTION_Ex", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.op_INVALID_TRANSACTION_Ex();
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

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
package ORBTest_WChar;

//
// IDL:ORBTest_WChar/Intf:1.0
//
public class _IntfStub extends org.omg.CORBA.portable.ObjectImpl
                       implements Intf
{
    private static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_WChar/Intf:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = IntfOperations.class;

    //
    // IDL:ORBTest_WChar/Intf/attrWChar:1.0
    //
    public char
    attrWChar()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrWChar", true);
                    in = _invoke(out);
                    char _ob_r = in.read_wchar();
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrWChar", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrWChar();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrWChar(char _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrWChar", true);
                    out.write_wchar(_ob_a);
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
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrWChar", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrWChar(_ob_a);
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
    // IDL:ORBTest_WChar/Intf/attrWString:1.0
    //
    public String
    attrWString()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrWString", true);
                    in = _invoke(out);
                    String _ob_r = in.read_wstring();
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrWString", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrWString();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrWString(String _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrWString", true);
                    out.write_wstring(_ob_a);
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
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrWString", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrWString(_ob_a);
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
    // IDL:ORBTest_WChar/Intf/opWChar:1.0
    //
    public char
    opWChar(char _ob_a0,
            org.omg.CORBA.CharHolder _ob_ah1,
            org.omg.CORBA.CharHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opWChar", true);
                    out.write_wchar(_ob_a0);
                    out.write_wchar(_ob_ah1.value);
                    in = _invoke(out);
                    char _ob_r = in.read_wchar();
                    _ob_ah1.value = in.read_wchar();
                    _ob_ah2.value = in.read_wchar();
                    return _ob_r;
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opWChar", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opWChar(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_WChar/Intf/opWCharEx:1.0
    //
    public char
    opWCharEx(char _ob_a0,
              org.omg.CORBA.CharHolder _ob_ah1,
              org.omg.CORBA.CharHolder _ob_ah2)
        throws ExWChar
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opWCharEx", true);
                    out.write_wchar(_ob_a0);
                    out.write_wchar(_ob_ah1.value);
                    in = _invoke(out);
                    char _ob_r = in.read_wchar();
                    _ob_ah1.value = in.read_wchar();
                    _ob_ah2.value = in.read_wchar();
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    if(_ob_id.equals(ExWCharHelper.id()))
                        throw ExWCharHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opWCharEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opWCharEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_WChar/Intf/opWString:1.0
    //
    public String
    opWString(String _ob_a0,
              org.omg.CORBA.StringHolder _ob_ah1,
              org.omg.CORBA.StringHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opWString", true);
                    out.write_wstring(_ob_a0);
                    out.write_wstring(_ob_ah1.value);
                    in = _invoke(out);
                    String _ob_r = in.read_wstring();
                    _ob_ah1.value = in.read_wstring();
                    _ob_ah2.value = in.read_wstring();
                    return _ob_r;
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opWString", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opWString(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_WChar/Intf/opWStringEx:1.0
    //
    public String
    opWStringEx(String _ob_a0,
                org.omg.CORBA.StringHolder _ob_ah1,
                org.omg.CORBA.StringHolder _ob_ah2)
        throws ExWString
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opWStringEx", true);
                    out.write_wstring(_ob_a0);
                    out.write_wstring(_ob_ah1.value);
                    in = _invoke(out);
                    String _ob_r = in.read_wstring();
                    _ob_ah1.value = in.read_wstring();
                    _ob_ah2.value = in.read_wstring();
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    if(_ob_id.equals(ExWStringHelper.id()))
                        throw ExWStringHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opWStringEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opWStringEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}

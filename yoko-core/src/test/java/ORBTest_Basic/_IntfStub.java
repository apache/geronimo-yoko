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
package ORBTest_Basic;

//
// IDL:ORBTest_Basic/Intf:1.0
//
public class _IntfStub extends org.omg.CORBA.portable.ObjectImpl
                       implements Intf
{
    private static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_Basic/Intf:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = IntfOperations.class;

    //
    // IDL:ORBTest_Basic/Intf/attrShort:1.0
    //
    public short
    attrShort()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrShort", true);
                    in = _invoke(out);
                    short _ob_r = in.read_short();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrShort", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrShort();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrShort(short _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrShort", true);
                    out.write_short(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrShort", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrShort(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrLong:1.0
    //
    public int
    attrLong()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrLong", true);
                    in = _invoke(out);
                    int _ob_r = in.read_long();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrLong", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrLong();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrLong(int _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrLong", true);
                    out.write_long(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrLong", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrLong(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrUShort:1.0
    //
    public short
    attrUShort()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrUShort", true);
                    in = _invoke(out);
                    short _ob_r = in.read_ushort();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrUShort", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrUShort();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrUShort(short _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrUShort", true);
                    out.write_ushort(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrUShort", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrUShort(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrULong:1.0
    //
    public int
    attrULong()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrULong", true);
                    in = _invoke(out);
                    int _ob_r = in.read_ulong();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrULong", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrULong();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrULong(int _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrULong", true);
                    out.write_ulong(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrULong", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrULong(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrFloat:1.0
    //
    public float
    attrFloat()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrFloat", true);
                    in = _invoke(out);
                    float _ob_r = in.read_float();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFloat", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrFloat();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrFloat(float _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrFloat", true);
                    out.write_float(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFloat", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrFloat(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrDouble:1.0
    //
    public double
    attrDouble()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrDouble", true);
                    in = _invoke(out);
                    double _ob_r = in.read_double();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrDouble", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrDouble();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrDouble(double _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrDouble", true);
                    out.write_double(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrDouble", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrDouble(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrBoolean:1.0
    //
    public boolean
    attrBoolean()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrBoolean", true);
                    in = _invoke(out);
                    boolean _ob_r = in.read_boolean();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrBoolean", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrBoolean();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrBoolean(boolean _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrBoolean", true);
                    out.write_boolean(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrBoolean", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrBoolean(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrChar:1.0
    //
    public char
    attrChar()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrChar", true);
                    in = _invoke(out);
                    char _ob_r = in.read_char();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrChar", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrChar();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrChar(char _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrChar", true);
                    out.write_char(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrChar", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrChar(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrOctet:1.0
    //
    public byte
    attrOctet()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrOctet", true);
                    in = _invoke(out);
                    byte _ob_r = in.read_octet();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrOctet", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrOctet();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrOctet(byte _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrOctet", true);
                    out.write_octet(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrOctet", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrOctet(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrString:1.0
    //
    public String
    attrString()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrString", true);
                    in = _invoke(out);
                    String _ob_r = in.read_string();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrString", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrString();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrString(String _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrString", true);
                    out.write_string(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrString", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrString(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrAny:1.0
    //
    public org.omg.CORBA.Any
    attrAny()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrAny", true);
                    in = _invoke(out);
                    org.omg.CORBA.Any _ob_r = in.read_any();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrAny", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrAny();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrAny(org.omg.CORBA.Any _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrAny", true);
                    out.write_any(_ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrAny", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrAny(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrTestEnum:1.0
    //
    public TestEnum
    attrTestEnum()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrTestEnum", true);
                    in = _invoke(out);
                    TestEnum _ob_r = TestEnumHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrTestEnum", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrTestEnum();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrTestEnum(TestEnum _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrTestEnum", true);
                    TestEnumHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrTestEnum", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrTestEnum(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrIntf:1.0
    //
    public Intf
    attrIntf()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrIntf", true);
                    in = _invoke(out);
                    Intf _ob_r = IntfHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrIntf", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrIntf();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrIntf(Intf _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrIntf", true);
                    IntfHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrIntf", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrIntf(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrFixedStruct:1.0
    //
    public FixedStruct
    attrFixedStruct()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrFixedStruct", true);
                    in = _invoke(out);
                    FixedStruct _ob_r = FixedStructHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedStruct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrFixedStruct();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrFixedStruct(FixedStruct _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrFixedStruct", true);
                    FixedStructHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedStruct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrFixedStruct(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrVariableStruct:1.0
    //
    public VariableStruct
    attrVariableStruct()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrVariableStruct", true);
                    in = _invoke(out);
                    VariableStruct _ob_r = VariableStructHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableStruct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrVariableStruct();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrVariableStruct(VariableStruct _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrVariableStruct", true);
                    VariableStructHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableStruct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrVariableStruct(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrFixedUnion:1.0
    //
    public FixedUnion
    attrFixedUnion()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrFixedUnion", true);
                    in = _invoke(out);
                    FixedUnion _ob_r = FixedUnionHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedUnion", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrFixedUnion();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrFixedUnion(FixedUnion _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrFixedUnion", true);
                    FixedUnionHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedUnion", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrFixedUnion(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrVariableUnion:1.0
    //
    public VariableUnion
    attrVariableUnion()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrVariableUnion", true);
                    in = _invoke(out);
                    VariableUnion _ob_r = VariableUnionHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableUnion", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrVariableUnion();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrVariableUnion(VariableUnion _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrVariableUnion", true);
                    VariableUnionHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableUnion", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrVariableUnion(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrStringSequence:1.0
    //
    public String[]
    attrStringSequence()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrStringSequence", true);
                    in = _invoke(out);
                    String[] _ob_r = StringSequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrStringSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrStringSequence();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrStringSequence(String[] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrStringSequence", true);
                    StringSequenceHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrStringSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrStringSequence(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrFixedArray:1.0
    //
    public short[][][]
    attrFixedArray()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrFixedArray", true);
                    in = _invoke(out);
                    short[][][] _ob_r = FixedArrayHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedArray", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrFixedArray();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrFixedArray(short[][][] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrFixedArray", true);
                    FixedArrayHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedArray", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrFixedArray(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrVariableArray:1.0
    //
    public String[][]
    attrVariableArray()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrVariableArray", true);
                    in = _invoke(out);
                    String[][] _ob_r = VariableArrayHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableArray", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrVariableArray();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrVariableArray(String[][] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrVariableArray", true);
                    VariableArrayHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableArray", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrVariableArray(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrFixedArraySequence:1.0
    //
    public short[][][][]
    attrFixedArraySequence()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrFixedArraySequence", true);
                    in = _invoke(out);
                    short[][][][] _ob_r = FixedArraySequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedArraySequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrFixedArraySequence();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrFixedArraySequence(short[][][][] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrFixedArraySequence", true);
                    FixedArraySequenceHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedArraySequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrFixedArraySequence(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrVariableArraySequence:1.0
    //
    public String[][][]
    attrVariableArraySequence()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrVariableArraySequence", true);
                    in = _invoke(out);
                    String[][][] _ob_r = VariableArraySequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableArraySequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrVariableArraySequence();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrVariableArraySequence(String[][][] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrVariableArraySequence", true);
                    VariableArraySequenceHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableArraySequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrVariableArraySequence(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrFixedArrayBoundSequence:1.0
    //
    public short[][][][]
    attrFixedArrayBoundSequence()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrFixedArrayBoundSequence", true);
                    in = _invoke(out);
                    short[][][][] _ob_r = FixedArrayBoundSequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedArrayBoundSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrFixedArrayBoundSequence();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrFixedArrayBoundSequence(short[][][][] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrFixedArrayBoundSequence", true);
                    FixedArrayBoundSequenceHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrFixedArrayBoundSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrFixedArrayBoundSequence(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/attrVariableArrayBoundSequence:1.0
    //
    public String[][][]
    attrVariableArrayBoundSequence()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_attrVariableArrayBoundSequence", true);
                    in = _invoke(out);
                    String[][][] _ob_r = VariableArrayBoundSequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableArrayBoundSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.attrVariableArrayBoundSequence();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    attrVariableArrayBoundSequence(String[][][] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_attrVariableArrayBoundSequence", true);
                    VariableArrayBoundSequenceHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("attrVariableArrayBoundSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.attrVariableArrayBoundSequence(_ob_a);
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
    // IDL:ORBTest_Basic/Intf/opVoid:1.0
    //
    public void
    opVoid()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVoid", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVoid", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.opVoid();
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
    // IDL:ORBTest_Basic/Intf/opVoidEx:1.0
    //
    public void
    opVoidEx()
        throws ExVoid
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVoidEx", true);
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

                    if(_ob_id.equals(ExVoidHelper.id()))
                        throw ExVoidHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVoidEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.opVoidEx();
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
    // IDL:ORBTest_Basic/Intf/opShort:1.0
    //
    public short
    opShort(short _ob_a0,
            org.omg.CORBA.ShortHolder _ob_ah1,
            org.omg.CORBA.ShortHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opShort", true);
                    out.write_short(_ob_a0);
                    out.write_short(_ob_ah1.value);
                    in = _invoke(out);
                    short _ob_r = in.read_short();
                    _ob_ah1.value = in.read_short();
                    _ob_ah2.value = in.read_short();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opShort", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opShort(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opShortEx:1.0
    //
    public short
    opShortEx(short _ob_a0,
              org.omg.CORBA.ShortHolder _ob_ah1,
              org.omg.CORBA.ShortHolder _ob_ah2)
        throws ExShort
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opShortEx", true);
                    out.write_short(_ob_a0);
                    out.write_short(_ob_ah1.value);
                    in = _invoke(out);
                    short _ob_r = in.read_short();
                    _ob_ah1.value = in.read_short();
                    _ob_ah2.value = in.read_short();
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

                    if(_ob_id.equals(ExShortHelper.id()))
                        throw ExShortHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opShortEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opShortEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opLong:1.0
    //
    public int
    opLong(int _ob_a0,
           org.omg.CORBA.IntHolder _ob_ah1,
           org.omg.CORBA.IntHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opLong", true);
                    out.write_long(_ob_a0);
                    out.write_long(_ob_ah1.value);
                    in = _invoke(out);
                    int _ob_r = in.read_long();
                    _ob_ah1.value = in.read_long();
                    _ob_ah2.value = in.read_long();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opLong", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opLong(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opLongEx:1.0
    //
    public int
    opLongEx(int _ob_a0,
             org.omg.CORBA.IntHolder _ob_ah1,
             org.omg.CORBA.IntHolder _ob_ah2)
        throws ExLong
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opLongEx", true);
                    out.write_long(_ob_a0);
                    out.write_long(_ob_ah1.value);
                    in = _invoke(out);
                    int _ob_r = in.read_long();
                    _ob_ah1.value = in.read_long();
                    _ob_ah2.value = in.read_long();
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

                    if(_ob_id.equals(ExLongHelper.id()))
                        throw ExLongHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opLongEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opLongEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opUShort:1.0
    //
    public short
    opUShort(short _ob_a0,
             org.omg.CORBA.ShortHolder _ob_ah1,
             org.omg.CORBA.ShortHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opUShort", true);
                    out.write_ushort(_ob_a0);
                    out.write_ushort(_ob_ah1.value);
                    in = _invoke(out);
                    short _ob_r = in.read_ushort();
                    _ob_ah1.value = in.read_ushort();
                    _ob_ah2.value = in.read_ushort();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opUShort", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opUShort(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opUShortEx:1.0
    //
    public short
    opUShortEx(short _ob_a0,
               org.omg.CORBA.ShortHolder _ob_ah1,
               org.omg.CORBA.ShortHolder _ob_ah2)
        throws ExUShort
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opUShortEx", true);
                    out.write_ushort(_ob_a0);
                    out.write_ushort(_ob_ah1.value);
                    in = _invoke(out);
                    short _ob_r = in.read_ushort();
                    _ob_ah1.value = in.read_ushort();
                    _ob_ah2.value = in.read_ushort();
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

                    if(_ob_id.equals(ExUShortHelper.id()))
                        throw ExUShortHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opUShortEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opUShortEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opULong:1.0
    //
    public int
    opULong(int _ob_a0,
            org.omg.CORBA.IntHolder _ob_ah1,
            org.omg.CORBA.IntHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opULong", true);
                    out.write_ulong(_ob_a0);
                    out.write_ulong(_ob_ah1.value);
                    in = _invoke(out);
                    int _ob_r = in.read_ulong();
                    _ob_ah1.value = in.read_ulong();
                    _ob_ah2.value = in.read_ulong();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opULong", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opULong(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opULongEx:1.0
    //
    public int
    opULongEx(int _ob_a0,
              org.omg.CORBA.IntHolder _ob_ah1,
              org.omg.CORBA.IntHolder _ob_ah2)
        throws ExULong
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opULongEx", true);
                    out.write_ulong(_ob_a0);
                    out.write_ulong(_ob_ah1.value);
                    in = _invoke(out);
                    int _ob_r = in.read_ulong();
                    _ob_ah1.value = in.read_ulong();
                    _ob_ah2.value = in.read_ulong();
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

                    if(_ob_id.equals(ExULongHelper.id()))
                        throw ExULongHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opULongEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opULongEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFloat:1.0
    //
    public float
    opFloat(float _ob_a0,
            org.omg.CORBA.FloatHolder _ob_ah1,
            org.omg.CORBA.FloatHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFloat", true);
                    out.write_float(_ob_a0);
                    out.write_float(_ob_ah1.value);
                    in = _invoke(out);
                    float _ob_r = in.read_float();
                    _ob_ah1.value = in.read_float();
                    _ob_ah2.value = in.read_float();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFloat", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFloat(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFloatEx:1.0
    //
    public float
    opFloatEx(float _ob_a0,
              org.omg.CORBA.FloatHolder _ob_ah1,
              org.omg.CORBA.FloatHolder _ob_ah2)
        throws ExFloat
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFloatEx", true);
                    out.write_float(_ob_a0);
                    out.write_float(_ob_ah1.value);
                    in = _invoke(out);
                    float _ob_r = in.read_float();
                    _ob_ah1.value = in.read_float();
                    _ob_ah2.value = in.read_float();
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

                    if(_ob_id.equals(ExFloatHelper.id()))
                        throw ExFloatHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFloatEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFloatEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opDouble:1.0
    //
    public double
    opDouble(double _ob_a0,
             org.omg.CORBA.DoubleHolder _ob_ah1,
             org.omg.CORBA.DoubleHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opDouble", true);
                    out.write_double(_ob_a0);
                    out.write_double(_ob_ah1.value);
                    in = _invoke(out);
                    double _ob_r = in.read_double();
                    _ob_ah1.value = in.read_double();
                    _ob_ah2.value = in.read_double();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opDouble", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opDouble(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opDoubleEx:1.0
    //
    public double
    opDoubleEx(double _ob_a0,
               org.omg.CORBA.DoubleHolder _ob_ah1,
               org.omg.CORBA.DoubleHolder _ob_ah2)
        throws ExDouble
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opDoubleEx", true);
                    out.write_double(_ob_a0);
                    out.write_double(_ob_ah1.value);
                    in = _invoke(out);
                    double _ob_r = in.read_double();
                    _ob_ah1.value = in.read_double();
                    _ob_ah2.value = in.read_double();
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

                    if(_ob_id.equals(ExDoubleHelper.id()))
                        throw ExDoubleHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opDoubleEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opDoubleEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opBoolean:1.0
    //
    public boolean
    opBoolean(boolean _ob_a0,
              org.omg.CORBA.BooleanHolder _ob_ah1,
              org.omg.CORBA.BooleanHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opBoolean", true);
                    out.write_boolean(_ob_a0);
                    out.write_boolean(_ob_ah1.value);
                    in = _invoke(out);
                    boolean _ob_r = in.read_boolean();
                    _ob_ah1.value = in.read_boolean();
                    _ob_ah2.value = in.read_boolean();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opBoolean", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opBoolean(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opBooleanEx:1.0
    //
    public boolean
    opBooleanEx(boolean _ob_a0,
                org.omg.CORBA.BooleanHolder _ob_ah1,
                org.omg.CORBA.BooleanHolder _ob_ah2)
        throws ExBoolean
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opBooleanEx", true);
                    out.write_boolean(_ob_a0);
                    out.write_boolean(_ob_ah1.value);
                    in = _invoke(out);
                    boolean _ob_r = in.read_boolean();
                    _ob_ah1.value = in.read_boolean();
                    _ob_ah2.value = in.read_boolean();
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

                    if(_ob_id.equals(ExBooleanHelper.id()))
                        throw ExBooleanHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opBooleanEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opBooleanEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opChar:1.0
    //
    public char
    opChar(char _ob_a0,
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
                    out = _request("opChar", true);
                    out.write_char(_ob_a0);
                    out.write_char(_ob_ah1.value);
                    in = _invoke(out);
                    char _ob_r = in.read_char();
                    _ob_ah1.value = in.read_char();
                    _ob_ah2.value = in.read_char();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opChar", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opChar(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opCharEx:1.0
    //
    public char
    opCharEx(char _ob_a0,
             org.omg.CORBA.CharHolder _ob_ah1,
             org.omg.CORBA.CharHolder _ob_ah2)
        throws ExChar
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opCharEx", true);
                    out.write_char(_ob_a0);
                    out.write_char(_ob_ah1.value);
                    in = _invoke(out);
                    char _ob_r = in.read_char();
                    _ob_ah1.value = in.read_char();
                    _ob_ah2.value = in.read_char();
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

                    if(_ob_id.equals(ExCharHelper.id()))
                        throw ExCharHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opCharEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opCharEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opOctet:1.0
    //
    public byte
    opOctet(byte _ob_a0,
            org.omg.CORBA.ByteHolder _ob_ah1,
            org.omg.CORBA.ByteHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opOctet", true);
                    out.write_octet(_ob_a0);
                    out.write_octet(_ob_ah1.value);
                    in = _invoke(out);
                    byte _ob_r = in.read_octet();
                    _ob_ah1.value = in.read_octet();
                    _ob_ah2.value = in.read_octet();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opOctet", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opOctet(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opOctetEx:1.0
    //
    public byte
    opOctetEx(byte _ob_a0,
              org.omg.CORBA.ByteHolder _ob_ah1,
              org.omg.CORBA.ByteHolder _ob_ah2)
        throws ExOctet
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opOctetEx", true);
                    out.write_octet(_ob_a0);
                    out.write_octet(_ob_ah1.value);
                    in = _invoke(out);
                    byte _ob_r = in.read_octet();
                    _ob_ah1.value = in.read_octet();
                    _ob_ah2.value = in.read_octet();
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

                    if(_ob_id.equals(ExOctetHelper.id()))
                        throw ExOctetHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opOctetEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opOctetEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opString:1.0
    //
    public String
    opString(String _ob_a0,
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
                    out = _request("opString", true);
                    out.write_string(_ob_a0);
                    out.write_string(_ob_ah1.value);
                    in = _invoke(out);
                    String _ob_r = in.read_string();
                    _ob_ah1.value = in.read_string();
                    _ob_ah2.value = in.read_string();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opString", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opString(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opStringEx:1.0
    //
    public String
    opStringEx(String _ob_a0,
               org.omg.CORBA.StringHolder _ob_ah1,
               org.omg.CORBA.StringHolder _ob_ah2)
        throws ExString
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opStringEx", true);
                    out.write_string(_ob_a0);
                    out.write_string(_ob_ah1.value);
                    in = _invoke(out);
                    String _ob_r = in.read_string();
                    _ob_ah1.value = in.read_string();
                    _ob_ah2.value = in.read_string();
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

                    if(_ob_id.equals(ExStringHelper.id()))
                        throw ExStringHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opStringEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opStringEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opAny:1.0
    //
    public org.omg.CORBA.Any
    opAny(org.omg.CORBA.Any _ob_a0,
          org.omg.CORBA.AnyHolder _ob_ah1,
          org.omg.CORBA.AnyHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opAny", true);
                    out.write_any(_ob_a0);
                    out.write_any(_ob_ah1.value);
                    in = _invoke(out);
                    org.omg.CORBA.Any _ob_r = in.read_any();
                    _ob_ah1.value = in.read_any();
                    _ob_ah2.value = in.read_any();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opAny", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opAny(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opAnyEx:1.0
    //
    public org.omg.CORBA.Any
    opAnyEx(org.omg.CORBA.Any _ob_a0,
            org.omg.CORBA.AnyHolder _ob_ah1,
            org.omg.CORBA.AnyHolder _ob_ah2)
        throws ExAny
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opAnyEx", true);
                    out.write_any(_ob_a0);
                    out.write_any(_ob_ah1.value);
                    in = _invoke(out);
                    org.omg.CORBA.Any _ob_r = in.read_any();
                    _ob_ah1.value = in.read_any();
                    _ob_ah2.value = in.read_any();
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

                    if(_ob_id.equals(ExAnyHelper.id()))
                        throw ExAnyHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opAnyEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opAnyEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opTestEnum:1.0
    //
    public TestEnum
    opTestEnum(TestEnum _ob_a0,
               TestEnumHolder _ob_ah1,
               TestEnumHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opTestEnum", true);
                    TestEnumHelper.write(out, _ob_a0);
                    TestEnumHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    TestEnum _ob_r = TestEnumHelper.read(in);
                    _ob_ah1.value = TestEnumHelper.read(in);
                    _ob_ah2.value = TestEnumHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opTestEnum", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opTestEnum(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opTestEnumEx:1.0
    //
    public TestEnum
    opTestEnumEx(TestEnum _ob_a0,
                 TestEnumHolder _ob_ah1,
                 TestEnumHolder _ob_ah2)
        throws ExTestEnum
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opTestEnumEx", true);
                    TestEnumHelper.write(out, _ob_a0);
                    TestEnumHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    TestEnum _ob_r = TestEnumHelper.read(in);
                    _ob_ah1.value = TestEnumHelper.read(in);
                    _ob_ah2.value = TestEnumHelper.read(in);
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

                    if(_ob_id.equals(ExTestEnumHelper.id()))
                        throw ExTestEnumHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opTestEnumEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opTestEnumEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opIntf:1.0
    //
    public Intf
    opIntf(Intf _ob_a0,
           IntfHolder _ob_ah1,
           IntfHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opIntf", true);
                    IntfHelper.write(out, _ob_a0);
                    IntfHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    Intf _ob_r = IntfHelper.read(in);
                    _ob_ah1.value = IntfHelper.read(in);
                    _ob_ah2.value = IntfHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opIntf", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opIntf(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opIntfEx:1.0
    //
    public Intf
    opIntfEx(Intf _ob_a0,
             IntfHolder _ob_ah1,
             IntfHolder _ob_ah2)
        throws ExIntf
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opIntfEx", true);
                    IntfHelper.write(out, _ob_a0);
                    IntfHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    Intf _ob_r = IntfHelper.read(in);
                    _ob_ah1.value = IntfHelper.read(in);
                    _ob_ah2.value = IntfHelper.read(in);
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

                    if(_ob_id.equals(ExIntfHelper.id()))
                        throw ExIntfHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opIntfEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opIntfEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedStruct:1.0
    //
    public FixedStruct
    opFixedStruct(FixedStruct _ob_a0,
                  FixedStructHolder _ob_ah1,
                  FixedStructHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedStruct", true);
                    FixedStructHelper.write(out, _ob_a0);
                    FixedStructHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    FixedStruct _ob_r = FixedStructHelper.read(in);
                    _ob_ah1.value = FixedStructHelper.read(in);
                    _ob_ah2.value = FixedStructHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedStruct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedStruct(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedStructEx:1.0
    //
    public FixedStruct
    opFixedStructEx(FixedStruct _ob_a0,
                    FixedStructHolder _ob_ah1,
                    FixedStructHolder _ob_ah2)
        throws ExFixedStruct
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedStructEx", true);
                    FixedStructHelper.write(out, _ob_a0);
                    FixedStructHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    FixedStruct _ob_r = FixedStructHelper.read(in);
                    _ob_ah1.value = FixedStructHelper.read(in);
                    _ob_ah2.value = FixedStructHelper.read(in);
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

                    if(_ob_id.equals(ExFixedStructHelper.id()))
                        throw ExFixedStructHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedStructEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedStructEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableStruct:1.0
    //
    public VariableStruct
    opVariableStruct(VariableStruct _ob_a0,
                     VariableStructHolder _ob_ah1,
                     VariableStructHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableStruct", true);
                    VariableStructHelper.write(out, _ob_a0);
                    VariableStructHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    VariableStruct _ob_r = VariableStructHelper.read(in);
                    _ob_ah1.value = VariableStructHelper.read(in);
                    _ob_ah2.value = VariableStructHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableStruct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableStruct(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableStructEx:1.0
    //
    public VariableStruct
    opVariableStructEx(VariableStruct _ob_a0,
                       VariableStructHolder _ob_ah1,
                       VariableStructHolder _ob_ah2)
        throws ExVariableStruct
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableStructEx", true);
                    VariableStructHelper.write(out, _ob_a0);
                    VariableStructHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    VariableStruct _ob_r = VariableStructHelper.read(in);
                    _ob_ah1.value = VariableStructHelper.read(in);
                    _ob_ah2.value = VariableStructHelper.read(in);
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

                    if(_ob_id.equals(ExVariableStructHelper.id()))
                        throw ExVariableStructHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableStructEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableStructEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedUnion:1.0
    //
    public FixedUnion
    opFixedUnion(FixedUnion _ob_a0,
                 FixedUnionHolder _ob_ah1,
                 FixedUnionHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedUnion", true);
                    FixedUnionHelper.write(out, _ob_a0);
                    FixedUnionHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    FixedUnion _ob_r = FixedUnionHelper.read(in);
                    _ob_ah1.value = FixedUnionHelper.read(in);
                    _ob_ah2.value = FixedUnionHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedUnion", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedUnion(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedUnionEx:1.0
    //
    public FixedUnion
    opFixedUnionEx(FixedUnion _ob_a0,
                   FixedUnionHolder _ob_ah1,
                   FixedUnionHolder _ob_ah2)
        throws ExFixedUnion
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedUnionEx", true);
                    FixedUnionHelper.write(out, _ob_a0);
                    FixedUnionHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    FixedUnion _ob_r = FixedUnionHelper.read(in);
                    _ob_ah1.value = FixedUnionHelper.read(in);
                    _ob_ah2.value = FixedUnionHelper.read(in);
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

                    if(_ob_id.equals(ExFixedUnionHelper.id()))
                        throw ExFixedUnionHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedUnionEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedUnionEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableUnion:1.0
    //
    public VariableUnion
    opVariableUnion(VariableUnion _ob_a0,
                    VariableUnionHolder _ob_ah1,
                    VariableUnionHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableUnion", true);
                    VariableUnionHelper.write(out, _ob_a0);
                    VariableUnionHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    VariableUnion _ob_r = VariableUnionHelper.read(in);
                    _ob_ah1.value = VariableUnionHelper.read(in);
                    _ob_ah2.value = VariableUnionHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableUnion", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableUnion(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableUnionEx:1.0
    //
    public VariableUnion
    opVariableUnionEx(VariableUnion _ob_a0,
                      VariableUnionHolder _ob_ah1,
                      VariableUnionHolder _ob_ah2)
        throws ExVariableUnion
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableUnionEx", true);
                    VariableUnionHelper.write(out, _ob_a0);
                    VariableUnionHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    VariableUnion _ob_r = VariableUnionHelper.read(in);
                    _ob_ah1.value = VariableUnionHelper.read(in);
                    _ob_ah2.value = VariableUnionHelper.read(in);
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

                    if(_ob_id.equals(ExVariableUnionHelper.id()))
                        throw ExVariableUnionHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableUnionEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableUnionEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opStringSequence:1.0
    //
    public String[]
    opStringSequence(String[] _ob_a0,
                     StringSequenceHolder _ob_ah1,
                     StringSequenceHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opStringSequence", true);
                    StringSequenceHelper.write(out, _ob_a0);
                    StringSequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[] _ob_r = StringSequenceHelper.read(in);
                    _ob_ah1.value = StringSequenceHelper.read(in);
                    _ob_ah2.value = StringSequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opStringSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opStringSequence(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opStringSequenceEx:1.0
    //
    public String[]
    opStringSequenceEx(String[] _ob_a0,
                       StringSequenceHolder _ob_ah1,
                       StringSequenceHolder _ob_ah2)
        throws ExStringSequence
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opStringSequenceEx", true);
                    StringSequenceHelper.write(out, _ob_a0);
                    StringSequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[] _ob_r = StringSequenceHelper.read(in);
                    _ob_ah1.value = StringSequenceHelper.read(in);
                    _ob_ah2.value = StringSequenceHelper.read(in);
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

                    if(_ob_id.equals(ExStringSequenceHelper.id()))
                        throw ExStringSequenceHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opStringSequenceEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opStringSequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArray:1.0
    //
    public short[][][]
    opFixedArray(short[][][] _ob_a0,
                 FixedArrayHolder _ob_ah1,
                 FixedArrayHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedArray", true);
                    FixedArrayHelper.write(out, _ob_a0);
                    FixedArrayHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    short[][][] _ob_r = FixedArrayHelper.read(in);
                    _ob_ah1.value = FixedArrayHelper.read(in);
                    _ob_ah2.value = FixedArrayHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedArray", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedArray(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayEx:1.0
    //
    public short[][][]
    opFixedArrayEx(short[][][] _ob_a0,
                   FixedArrayHolder _ob_ah1,
                   FixedArrayHolder _ob_ah2)
        throws ExFixedArray
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedArrayEx", true);
                    FixedArrayHelper.write(out, _ob_a0);
                    FixedArrayHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    short[][][] _ob_r = FixedArrayHelper.read(in);
                    _ob_ah1.value = FixedArrayHelper.read(in);
                    _ob_ah2.value = FixedArrayHelper.read(in);
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

                    if(_ob_id.equals(ExFixedArrayHelper.id()))
                        throw ExFixedArrayHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedArrayEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedArrayEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArray:1.0
    //
    public String[][]
    opVariableArray(String[][] _ob_a0,
                    VariableArrayHolder _ob_ah1,
                    VariableArrayHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableArray", true);
                    VariableArrayHelper.write(out, _ob_a0);
                    VariableArrayHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[][] _ob_r = VariableArrayHelper.read(in);
                    _ob_ah1.value = VariableArrayHelper.read(in);
                    _ob_ah2.value = VariableArrayHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableArray", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableArray(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayEx:1.0
    //
    public String[][]
    opVariableArrayEx(String[][] _ob_a0,
                      VariableArrayHolder _ob_ah1,
                      VariableArrayHolder _ob_ah2)
        throws ExVariableArray
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableArrayEx", true);
                    VariableArrayHelper.write(out, _ob_a0);
                    VariableArrayHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[][] _ob_r = VariableArrayHelper.read(in);
                    _ob_ah1.value = VariableArrayHelper.read(in);
                    _ob_ah2.value = VariableArrayHelper.read(in);
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

                    if(_ob_id.equals(ExVariableArrayHelper.id()))
                        throw ExVariableArrayHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableArrayEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableArrayEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArraySequence:1.0
    //
    public short[][][][]
    opFixedArraySequence(short[][][][] _ob_a0,
                         FixedArraySequenceHolder _ob_ah1,
                         FixedArraySequenceHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedArraySequence", true);
                    FixedArraySequenceHelper.write(out, _ob_a0);
                    FixedArraySequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    short[][][][] _ob_r = FixedArraySequenceHelper.read(in);
                    _ob_ah1.value = FixedArraySequenceHelper.read(in);
                    _ob_ah2.value = FixedArraySequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedArraySequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedArraySequence(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArraySequenceEx:1.0
    //
    public short[][][][]
    opFixedArraySequenceEx(short[][][][] _ob_a0,
                           FixedArraySequenceHolder _ob_ah1,
                           FixedArraySequenceHolder _ob_ah2)
        throws ExFixedArraySequence
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedArraySequenceEx", true);
                    FixedArraySequenceHelper.write(out, _ob_a0);
                    FixedArraySequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    short[][][][] _ob_r = FixedArraySequenceHelper.read(in);
                    _ob_ah1.value = FixedArraySequenceHelper.read(in);
                    _ob_ah2.value = FixedArraySequenceHelper.read(in);
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

                    if(_ob_id.equals(ExFixedArraySequenceHelper.id()))
                        throw ExFixedArraySequenceHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedArraySequenceEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedArraySequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArraySequence:1.0
    //
    public String[][][]
    opVariableArraySequence(String[][][] _ob_a0,
                            VariableArraySequenceHolder _ob_ah1,
                            VariableArraySequenceHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableArraySequence", true);
                    VariableArraySequenceHelper.write(out, _ob_a0);
                    VariableArraySequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[][][] _ob_r = VariableArraySequenceHelper.read(in);
                    _ob_ah1.value = VariableArraySequenceHelper.read(in);
                    _ob_ah2.value = VariableArraySequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableArraySequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableArraySequence(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArraySequenceEx:1.0
    //
    public String[][][]
    opVariableArraySequenceEx(String[][][] _ob_a0,
                              VariableArraySequenceHolder _ob_ah1,
                              VariableArraySequenceHolder _ob_ah2)
        throws ExVariableArraySequence
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableArraySequenceEx", true);
                    VariableArraySequenceHelper.write(out, _ob_a0);
                    VariableArraySequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[][][] _ob_r = VariableArraySequenceHelper.read(in);
                    _ob_ah1.value = VariableArraySequenceHelper.read(in);
                    _ob_ah2.value = VariableArraySequenceHelper.read(in);
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

                    if(_ob_id.equals(ExVariableArraySequenceHelper.id()))
                        throw ExVariableArraySequenceHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableArraySequenceEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableArraySequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayBoundSequence:1.0
    //
    public short[][][][]
    opFixedArrayBoundSequence(short[][][][] _ob_a0,
                              FixedArrayBoundSequenceHolder _ob_ah1,
                              FixedArrayBoundSequenceHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedArrayBoundSequence", true);
                    FixedArrayBoundSequenceHelper.write(out, _ob_a0);
                    FixedArrayBoundSequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    short[][][][] _ob_r = FixedArrayBoundSequenceHelper.read(in);
                    _ob_ah1.value = FixedArrayBoundSequenceHelper.read(in);
                    _ob_ah2.value = FixedArrayBoundSequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedArrayBoundSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedArrayBoundSequence(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayBoundSequenceEx:1.0
    //
    public short[][][][]
    opFixedArrayBoundSequenceEx(short[][][][] _ob_a0,
                                FixedArrayBoundSequenceHolder _ob_ah1,
                                FixedArrayBoundSequenceHolder _ob_ah2)
        throws ExFixedArrayBoundSequence
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opFixedArrayBoundSequenceEx", true);
                    FixedArrayBoundSequenceHelper.write(out, _ob_a0);
                    FixedArrayBoundSequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    short[][][][] _ob_r = FixedArrayBoundSequenceHelper.read(in);
                    _ob_ah1.value = FixedArrayBoundSequenceHelper.read(in);
                    _ob_ah2.value = FixedArrayBoundSequenceHelper.read(in);
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

                    if(_ob_id.equals(ExFixedArrayBoundSequenceHelper.id()))
                        throw ExFixedArrayBoundSequenceHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opFixedArrayBoundSequenceEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opFixedArrayBoundSequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayBoundSequence:1.0
    //
    public String[][][]
    opVariableArrayBoundSequence(String[][][] _ob_a0,
                                 VariableArrayBoundSequenceHolder _ob_ah1,
                                 VariableArrayBoundSequenceHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableArrayBoundSequence", true);
                    VariableArrayBoundSequenceHelper.write(out, _ob_a0);
                    VariableArrayBoundSequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[][][] _ob_r = VariableArrayBoundSequenceHelper.read(in);
                    _ob_ah1.value = VariableArrayBoundSequenceHelper.read(in);
                    _ob_ah2.value = VariableArrayBoundSequenceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableArrayBoundSequence", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableArrayBoundSequence(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayBoundSequenceEx:1.0
    //
    public String[][][]
    opVariableArrayBoundSequenceEx(String[][][] _ob_a0,
                                   VariableArrayBoundSequenceHolder _ob_ah1,
                                   VariableArrayBoundSequenceHolder _ob_ah2)
        throws ExVariableArrayBoundSequence
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opVariableArrayBoundSequenceEx", true);
                    VariableArrayBoundSequenceHelper.write(out, _ob_a0);
                    VariableArrayBoundSequenceHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    String[][][] _ob_r = VariableArrayBoundSequenceHelper.read(in);
                    _ob_ah1.value = VariableArrayBoundSequenceHelper.read(in);
                    _ob_ah2.value = VariableArrayBoundSequenceHelper.read(in);
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

                    if(_ob_id.equals(ExVariableArrayBoundSequenceHelper.id()))
                        throw ExVariableArrayBoundSequenceHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opVariableArrayBoundSequenceEx", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    return _ob_self.opVariableArrayBoundSequenceEx(_ob_a0, _ob_ah1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:ORBTest_Basic/Intf/opExRecursiveStruct:1.0
    //
    public void
    opExRecursiveStruct()
        throws ExRecursiveStruct
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opExRecursiveStruct", true);
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

                    if(_ob_id.equals(ExRecursiveStructHelper.id()))
                        throw ExRecursiveStructHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opExRecursiveStruct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                try
                {
                    _ob_self.opExRecursiveStruct();
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

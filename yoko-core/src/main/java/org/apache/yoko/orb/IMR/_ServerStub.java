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
public class _ServerStub extends org.omg.CORBA.portable.ObjectImpl
                         implements Server
{
    private static final String[] _ob_ids_ =
    {
        "IDL:orb.yoko.apache.org/IMR/Server:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = ServerOperations.class;

    //
    // IDL:orb.yoko.apache.org/IMR/Server/id:1.0
    //
    public int
    id()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_id", true);
                    in = _invoke(out);
                    int _ob_r = ServerIDHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("id", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.id();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Server/status:1.0
    //
    public ServerStatus
    status()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_status", true);
                    in = _invoke(out);
                    ServerStatus _ob_r = ServerStatusHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("status", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.status();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Server/manual:1.0
    //
    public boolean
    manual()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_manual", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("manual", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.manual();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Server/updateTime:1.0
    //
    public int
    updateTime()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_updateTime", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("updateTime", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.updateTime();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Server/timesForked:1.0
    //
    public short
    timesForked()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_timesForked", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("timesForked", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.timesForked();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Server/name:1.0
    //
    public String
    name()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_name", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("name", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.name();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    name(String _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_name", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("name", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.name(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/host:1.0
    //
    public String
    host()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_host", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("host", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.host();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    host(String _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_host", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("host", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.host(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/exec:1.0
    //
    public String
    exec()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_exec", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("exec", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.exec();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    exec(String _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_exec", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("exec", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.exec(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/args:1.0
    //
    public String[]
    args()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_args", true);
                    in = _invoke(out);
                    String[] _ob_r = ArgListHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("args", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.args();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    args(String[] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_args", true);
                    ArgListHelper.write(out, _ob_a);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("args", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.args(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/directory:1.0
    //
    public String
    directory()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_directory", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("directory", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.directory();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    directory(String _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_directory", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("directory", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.directory(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/mode:1.0
    //
    public ServerActivationMode
    mode()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_mode", true);
                    in = _invoke(out);
                    ServerActivationMode _ob_r = ServerActivationModeHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("mode", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.mode();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    mode(ServerActivationMode _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_mode", true);
                    ServerActivationModeHelper.write(out, _ob_a);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("mode", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.mode(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/updateTimeout:1.0
    //
    public int
    updateTimeout()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_updateTimeout", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("updateTimeout", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.updateTimeout();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    updateTimeout(int _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_updateTimeout", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("updateTimeout", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.updateTimeout(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/failureTimeout:1.0
    //
    public int
    failureTimeout()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_failureTimeout", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("failureTimeout", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.failureTimeout();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    failureTimeout(int _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_failureTimeout", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("failureTimeout", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.failureTimeout(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/maxForks:1.0
    //
    public short
    maxForks()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_maxForks", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("maxForks", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.maxForks();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    maxForks(short _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_maxForks", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("maxForks", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.maxForks(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/activatePOAs:1.0
    //
    public boolean
    activatePOAs()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_activatePOAs", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("activatePOAs", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.activatePOAs();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    activatePOAs(boolean _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_activatePOAs", true);
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
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("activatePOAs", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.activatePOAs(_ob_a);
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
    // IDL:orb.yoko.apache.org/IMR/Server/create_poa_record:1.0
    //
    public void
    create_poa_record(String[] _ob_a0)
        throws POAAlreadyRegistered
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_poa_record", true);
                    POANameHelper.write(out, _ob_a0);
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

                    if(_ob_id.equals(POAAlreadyRegisteredHelper.id()))
                        throw POAAlreadyRegisteredHelper.read(in);
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_poa_record", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.create_poa_record(_ob_a0);
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
    // IDL:orb.yoko.apache.org/IMR/Server/remove_poa_record:1.0
    //
    public void
    remove_poa_record(String[] _ob_a0)
        throws _NoSuchPOA
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("remove_poa_record", true);
                    POANameHelper.write(out, _ob_a0);
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

                    if(_ob_id.equals(_NoSuchPOAHelper.id()))
                        throw _NoSuchPOAHelper.read(in);
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("remove_poa_record", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.remove_poa_record(_ob_a0);
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
    // IDL:orb.yoko.apache.org/IMR/Server/get_poa_info:1.0
    //
    public POAInfo
    get_poa_info(String[] _ob_a0)
        throws _NoSuchPOA
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_poa_info", true);
                    POANameHelper.write(out, _ob_a0);
                    in = _invoke(out);
                    POAInfo _ob_r = POAInfoHelper.read(in);
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

                    if(_ob_id.equals(_NoSuchPOAHelper.id()))
                        throw _NoSuchPOAHelper.read(in);
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_poa_info", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_poa_info(_ob_a0);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Server/list_poas:1.0
    //
    public POAInfo[]
    list_poas()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("list_poas", true);
                    in = _invoke(out);
                    POAInfo[] _ob_r = POAInfoSeqHelper.read(in);
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

                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("list_poas", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.list_poas();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Server/clear_error_state:1.0
    //
    public void
    clear_error_state()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("clear_error_state", true);
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

                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("clear_error_state", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.clear_error_state();
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
    // IDL:orb.yoko.apache.org/IMR/Server/start:1.0
    //
    public void
    start()
        throws ServerRunning
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("start", true);
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

                    if(_ob_id.equals(ServerRunningHelper.id()))
                        throw ServerRunningHelper.read(in);
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("start", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.start();
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
    // IDL:orb.yoko.apache.org/IMR/Server/stop:1.0
    //
    public void
    stop()
        throws OADNotRunning,
               ServerNotRunning
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("stop", true);
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

                    if(_ob_id.equals(OADNotRunningHelper.id()))
                        throw OADNotRunningHelper.read(in);
                    if(_ob_id.equals(ServerNotRunningHelper.id()))
                        throw ServerNotRunningHelper.read(in);
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("stop", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.stop();
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
    // IDL:orb.yoko.apache.org/IMR/Server/destroy:1.0
    //
    public void
    destroy()
        throws ServerRunning
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("destroy", true);
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

                    if(_ob_id.equals(ServerRunningHelper.id()))
                        throw ServerRunningHelper.read(in);
                    throw (org.omg.CORBA.UNKNOWN)new 
                        org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id).initCause(_ob_aex);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("destroy", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerOperations _ob_self = (ServerOperations)_ob_so.servant;
                try
                {
                    _ob_self.destroy();
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

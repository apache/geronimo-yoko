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
// IDL:orb.yoko.apache.org/IMR/ServerDomain:1.0
//
public class _ServerDomainStub extends org.omg.CORBA.portable.ObjectImpl
                               implements ServerDomain
{
    private static final String[] _ob_ids_ =
    {
        "IDL:orb.yoko.apache.org/IMR/ServerDomain:1.0",
        "IDL:orb.yoko.apache.org/IMR/Domain:1.0"
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = ServerDomainOperations.class;

    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/get_server_factory:1.0
    //
    public ServerFactory
    get_server_factory()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_server_factory", true);
                    in = _invoke(out);
                    ServerFactory _ob_r = ServerFactoryHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_server_factory", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerDomainOperations _ob_self = (ServerDomainOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_server_factory();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/create_oad_record:1.0
    //
    public void
    create_oad_record(String _ob_a0)
        throws OADAlreadyExists
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_oad_record", true);
                    out.write_string(_ob_a0);
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

                    if(_ob_id.equals(OADAlreadyExistsHelper.id()))
                        throw OADAlreadyExistsHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_oad_record", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerDomainOperations _ob_self = (ServerDomainOperations)_ob_so.servant;
                try
                {
                    _ob_self.create_oad_record(_ob_a0);
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
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/remove_oad_record:1.0
    //
    public void
    remove_oad_record(String _ob_a0)
        throws NoSuchOAD,
               OADRunning
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("remove_oad_record", true);
                    out.write_string(_ob_a0);
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

                    if(_ob_id.equals(NoSuchOADHelper.id()))
                        throw NoSuchOADHelper.read(in);
                    if(_ob_id.equals(OADRunningHelper.id()))
                        throw OADRunningHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("remove_oad_record", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerDomainOperations _ob_self = (ServerDomainOperations)_ob_so.servant;
                try
                {
                    _ob_self.remove_oad_record(_ob_a0);
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
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/get_oad_record:1.0
    //
    public OADInfo
    get_oad_record(String _ob_a0)
        throws NoSuchOAD
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_oad_record", true);
                    out.write_string(_ob_a0);
                    in = _invoke(out);
                    OADInfo _ob_r = OADInfoHelper.read(in);
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

                    if(_ob_id.equals(NoSuchOADHelper.id()))
                        throw NoSuchOADHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_oad_record", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerDomainOperations _ob_self = (ServerDomainOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_oad_record(_ob_a0);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/ServerDomain/list_oads:1.0
    //
    public OADInfo[]
    list_oads()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("list_oads", true);
                    in = _invoke(out);
                    OADInfo[] _ob_r = OADInfoSeqHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("list_oads", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerDomainOperations _ob_self = (ServerDomainOperations)_ob_so.servant;
                try
                {
                    return _ob_self.list_oads();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/Domain/registerServer:1.0
    //
    public void
    registerServer(String _ob_a0,
                   String _ob_a1,
                   String _ob_a2)
        throws ServerAlreadyRegistered
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("registerServer", true);
                    out.write_string(_ob_a0);
                    out.write_string(_ob_a1);
                    out.write_string(_ob_a2);
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

                    if(_ob_id.equals(ServerAlreadyRegisteredHelper.id()))
                        throw ServerAlreadyRegisteredHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("registerServer", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerDomainOperations _ob_self = (ServerDomainOperations)_ob_so.servant;
                try
                {
                    _ob_self.registerServer(_ob_a0, _ob_a1, _ob_a2);
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
    // IDL:orb.yoko.apache.org/IMR/Domain/startup:1.0
    //
    public ActiveState
    startup(String _ob_a0,
            String _ob_a1,
            org.omg.PortableInterceptor.ObjectReferenceTemplate _ob_a2,
            org.apache.yoko.orb.OAD.ProcessEndpointManagerHolder _ob_ah3)
        throws NoSuchServer,
               NoSuchOAD,
               OADNotRunning
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("startup", true);
                    out.write_string(_ob_a0);
                    out.write_string(_ob_a1);
                    org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write(out, _ob_a2);
                    in = _invoke(out);
                    ActiveState _ob_r = ActiveStateHelper.read(in);
                    _ob_ah3.value = org.apache.yoko.orb.OAD.ProcessEndpointManagerHelper.read(in);
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

                    if(_ob_id.equals(NoSuchServerHelper.id()))
                        throw NoSuchServerHelper.read(in);
                    if(_ob_id.equals(NoSuchOADHelper.id()))
                        throw NoSuchOADHelper.read(in);
                    if(_ob_id.equals(OADNotRunningHelper.id()))
                        throw OADNotRunningHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("startup", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ServerDomainOperations _ob_self = (ServerDomainOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write(_ob_out, _ob_a2);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a2 = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read(_ob_in);
                    return _ob_self.startup(_ob_a0, _ob_a1, _ob_a2, _ob_ah3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}

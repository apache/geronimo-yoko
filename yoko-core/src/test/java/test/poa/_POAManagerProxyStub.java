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
package test.poa;

//
// IDL:POAManagerProxy:1.0
//
public class _POAManagerProxyStub extends org.omg.CORBA.portable.ObjectImpl
                                  implements POAManagerProxy
{
    private static final String[] _ob_ids_ =
    {
        "IDL:POAManagerProxy:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = POAManagerProxyOperations.class;

    //
    // IDL:POAManagerProxy/activate:1.0
    //
    public void
    activate()
        throws test.poa.POAManagerProxyPackage.AdapterInactive
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("activate", true);
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

                    if(_ob_id.equals(test.poa.POAManagerProxyPackage.AdapterInactiveHelper.id()))
                        throw test.poa.POAManagerProxyPackage.AdapterInactiveHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("activate", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                POAManagerProxyOperations _ob_self = (POAManagerProxyOperations)_ob_so.servant;
                try
                {
                    _ob_self.activate();
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
    // IDL:POAManagerProxy/hold_requests:1.0
    //
    public void
    hold_requests(boolean _ob_a0)
        throws test.poa.POAManagerProxyPackage.AdapterInactive
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("hold_requests", true);
                    out.write_boolean(_ob_a0);
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

                    if(_ob_id.equals(test.poa.POAManagerProxyPackage.AdapterInactiveHelper.id()))
                        throw test.poa.POAManagerProxyPackage.AdapterInactiveHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("hold_requests", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                POAManagerProxyOperations _ob_self = (POAManagerProxyOperations)_ob_so.servant;
                try
                {
                    _ob_self.hold_requests(_ob_a0);
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
    // IDL:POAManagerProxy/discard_requests:1.0
    //
    public void
    discard_requests(boolean _ob_a0)
        throws test.poa.POAManagerProxyPackage.AdapterInactive
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("discard_requests", true);
                    out.write_boolean(_ob_a0);
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

                    if(_ob_id.equals(test.poa.POAManagerProxyPackage.AdapterInactiveHelper.id()))
                        throw test.poa.POAManagerProxyPackage.AdapterInactiveHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("discard_requests", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                POAManagerProxyOperations _ob_self = (POAManagerProxyOperations)_ob_so.servant;
                try
                {
                    _ob_self.discard_requests(_ob_a0);
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
    // IDL:POAManagerProxy/deactivate:1.0
    //
    public void
    deactivate(boolean _ob_a0,
               boolean _ob_a1)
        throws test.poa.POAManagerProxyPackage.AdapterInactive
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("deactivate", true);
                    out.write_boolean(_ob_a0);
                    out.write_boolean(_ob_a1);
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

                    if(_ob_id.equals(test.poa.POAManagerProxyPackage.AdapterInactiveHelper.id()))
                        throw test.poa.POAManagerProxyPackage.AdapterInactiveHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("deactivate", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                POAManagerProxyOperations _ob_self = (POAManagerProxyOperations)_ob_so.servant;
                try
                {
                    _ob_self.deactivate(_ob_a0, _ob_a1);
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
    // IDL:POAManagerProxy/get_state:1.0
    //
    public test.poa.POAManagerProxyPackage.State
    get_state()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_state", true);
                    in = _invoke(out);
                    test.poa.POAManagerProxyPackage.State _ob_r = test.poa.POAManagerProxyPackage.StateHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_state", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                POAManagerProxyOperations _ob_self = (POAManagerProxyOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_state();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}

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
// IDL:orb.yoko.apache.org/IMR/ActiveState:1.0
//
public class _ActiveStateStub extends org.omg.CORBA.portable.ObjectImpl
                              implements ActiveState
{
    private static final String[] _ob_ids_ =
    {
        "IDL:orb.yoko.apache.org/IMR/ActiveState:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = ActiveStateOperations.class;

    //
    // IDL:orb.yoko.apache.org/IMR/ActiveState/set_status:1.0
    //
    public void
    set_status(String _ob_a0,
               ServerStatus _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_status", true);
                    out.write_string(_ob_a0);
                    ServerStatusHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_status", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ActiveStateOperations _ob_self = (ActiveStateOperations)_ob_so.servant;
                try
                {
                    _ob_self.set_status(_ob_a0, _ob_a1);
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
    // IDL:orb.yoko.apache.org/IMR/ActiveState/poa_create:1.0
    //
    public org.omg.PortableInterceptor.ObjectReferenceTemplate
    poa_create(POAStatus _ob_a0,
               org.omg.PortableInterceptor.ObjectReferenceTemplate _ob_a1)
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
                    out = _request("poa_create", true);
                    POAStatusHelper.write(out, _ob_a0);
                    org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write(out, _ob_a1);
                    in = _invoke(out);
                    org.omg.PortableInterceptor.ObjectReferenceTemplate _ob_r = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("poa_create", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ActiveStateOperations _ob_self = (ActiveStateOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write(_ob_out, _ob_a1);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a1 = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read(_ob_in);
                    org.omg.PortableInterceptor.ObjectReferenceTemplate _ob_r = _ob_self.poa_create(_ob_a0, _ob_a1);
                    _ob_out = _orb().create_output_stream();
                    org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.write(_ob_out, _ob_r);
                    _ob_in = _ob_out.create_input_stream();
                    _ob_r = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.read(_ob_in);
                    return _ob_r;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:orb.yoko.apache.org/IMR/ActiveState/poa_status_update:1.0
    //
    public void
    poa_status_update(String[][] _ob_a0,
                      POAStatus _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("poa_status_update", true);
                    POANameSeqHelper.write(out, _ob_a0);
                    POAStatusHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("poa_status_update", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ActiveStateOperations _ob_self = (ActiveStateOperations)_ob_so.servant;
                try
                {
                    _ob_self.poa_status_update(_ob_a0, _ob_a1);
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

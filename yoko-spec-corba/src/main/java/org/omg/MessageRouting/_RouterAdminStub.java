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

package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/RouterAdmin:1.0
//
public class _RouterAdminStub extends org.omg.CORBA.portable.ObjectImpl
                              implements RouterAdmin
{
    private static final String[] _ob_ids_ =
    {
        "IDL:omg.org/MessageRouting/RouterAdmin:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = RouterAdminOperations.class;

    //
    // IDL:omg.org/MessageRouting/RouterAdmin/register_destination:1.0
    //
    public void
    register_destination(org.omg.CORBA.Object _ob_a0,
                         boolean _ob_a1,
                         RetryPolicy _ob_a2,
                         DecayPolicy _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("register_destination", true);
                    out.write_Object(_ob_a0);
                    out.write_boolean(_ob_a1);
                    RetryPolicyHelper.write(out, _ob_a2);
                    DecayPolicyHelper.write(out, _ob_a3);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("register_destination", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                RouterAdminOperations _ob_self = (RouterAdminOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    RetryPolicyHelper.write(_ob_out, _ob_a2);
                    DecayPolicyHelper.write(_ob_out, _ob_a3);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a2 = RetryPolicyHelper.read(_ob_in);
                    _ob_a3 = DecayPolicyHelper.read(_ob_in);
                    _ob_self.register_destination(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
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
    // IDL:omg.org/MessageRouting/RouterAdmin/suspend_destination:1.0
    //
    public void
    suspend_destination(org.omg.CORBA.Object _ob_a0,
                        ResumePolicy _ob_a1)
        throws InvalidState
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("suspend_destination", true);
                    out.write_Object(_ob_a0);
                    ResumePolicyHelper.write(out, _ob_a1);
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

                    if(_ob_id.equals(InvalidStateHelper.id()))
                        throw InvalidStateHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("suspend_destination", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                RouterAdminOperations _ob_self = (RouterAdminOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    ResumePolicyHelper.write(_ob_out, _ob_a1);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a1 = ResumePolicyHelper.read(_ob_in);
                    _ob_self.suspend_destination(_ob_a0, _ob_a1);
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
    // IDL:omg.org/MessageRouting/RouterAdmin/resume_destination:1.0
    //
    public void
    resume_destination(org.omg.CORBA.Object _ob_a0)
        throws InvalidState
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("resume_destination", true);
                    out.write_Object(_ob_a0);
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

                    if(_ob_id.equals(InvalidStateHelper.id()))
                        throw InvalidStateHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("resume_destination", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                RouterAdminOperations _ob_self = (RouterAdminOperations)_ob_so.servant;
                try
                {
                    _ob_self.resume_destination(_ob_a0);
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
    // IDL:omg.org/MessageRouting/RouterAdmin/unregister_destination:1.0
    //
    public void
    unregister_destination(org.omg.CORBA.Object _ob_a0)
        throws InvalidState
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("unregister_destination", true);
                    out.write_Object(_ob_a0);
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

                    if(_ob_id.equals(InvalidStateHelper.id()))
                        throw InvalidStateHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("unregister_destination", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                RouterAdminOperations _ob_self = (RouterAdminOperations)_ob_so.servant;
                try
                {
                    _ob_self.unregister_destination(_ob_a0);
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

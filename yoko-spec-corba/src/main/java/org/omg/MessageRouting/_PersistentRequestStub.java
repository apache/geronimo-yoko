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
// IDL:omg.org/MessageRouting/PersistentRequest:1.0
//
public class _PersistentRequestStub extends org.omg.CORBA.portable.ObjectImpl
                                    implements PersistentRequest
{
    private static final String[] _ob_ids_ =
    {
        "IDL:omg.org/MessageRouting/PersistentRequest:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = PersistentRequestOperations.class;

    //
    // IDL:omg.org/MessageRouting/PersistentRequest/reply_available:1.0
    //
    public boolean
    reply_available()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_reply_available", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("reply_available", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                PersistentRequestOperations _ob_self = (PersistentRequestOperations)_ob_so.servant;
                try
                {
                    return _ob_self.reply_available();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/MessageRouting/PersistentRequest/associated_handler:1.0
    //
    public org.omg.Messaging.ReplyHandler
    associated_handler()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_associated_handler", true);
                    in = _invoke(out);
                    org.omg.Messaging.ReplyHandler _ob_r = org.omg.Messaging.ReplyHandlerHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("associated_handler", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                PersistentRequestOperations _ob_self = (PersistentRequestOperations)_ob_so.servant;
                try
                {
                    return _ob_self.associated_handler();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    associated_handler(org.omg.Messaging.ReplyHandler _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_associated_handler", true);
                    org.omg.Messaging.ReplyHandlerHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("associated_handler", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                PersistentRequestOperations _ob_self = (PersistentRequestOperations)_ob_so.servant;
                try
                {
                    _ob_self.associated_handler(_ob_a);
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
    // IDL:omg.org/MessageRouting/PersistentRequest/get_reply:1.0
    //
    public org.omg.GIOP.ReplyStatusType_1_2
    get_reply(boolean _ob_a0,
              int _ob_a1,
              MessageBodyHolder _ob_ah2)
        throws ReplyNotAvailable
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_reply", true);
                    out.write_boolean(_ob_a0);
                    out.write_ulong(_ob_a1);
                    in = _invoke(out);
                    org.omg.GIOP.ReplyStatusType_1_2 _ob_r = org.omg.GIOP.ReplyStatusType_1_2Helper.read(in);
                    _ob_ah2.value = MessageBodyHelper.read(in);
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

                    if(_ob_id.equals(ReplyNotAvailableHelper.id()))
                        throw ReplyNotAvailableHelper.read(in);
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_reply", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                PersistentRequestOperations _ob_self = (PersistentRequestOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_reply(_ob_a0, _ob_a1, _ob_ah2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}

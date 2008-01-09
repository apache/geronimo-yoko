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
// IDL:omg.org/MessageRouting/UntypedReplyHandler:1.0
//
public class _UntypedReplyHandlerStub extends org.omg.CORBA.portable.ObjectImpl
                                      implements UntypedReplyHandler
{
    private static final String[] _ob_ids_ =
    {
        "IDL:omg.org/MessageRouting/UntypedReplyHandler:1.0",
        "IDL:omg.org/Messaging/ReplyHandler:1.0"
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = UntypedReplyHandlerOperations.class;

    //
    // IDL:omg.org/MessageRouting/UntypedReplyHandler/reply:1.0
    //
    public void
    reply(String _ob_a0,
          org.omg.GIOP.ReplyStatusType_1_2 _ob_a1,
          MessageBody _ob_a2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("reply", true);
                    out.write_string(_ob_a0);
                    org.omg.GIOP.ReplyStatusType_1_2Helper.write(out, _ob_a1);
                    MessageBodyHelper.write(out, _ob_a2);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("reply", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                UntypedReplyHandlerOperations _ob_self = (UntypedReplyHandlerOperations)_ob_so.servant;
                try
                {
                    _ob_self.reply(_ob_a0, _ob_a1, _ob_a2);
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

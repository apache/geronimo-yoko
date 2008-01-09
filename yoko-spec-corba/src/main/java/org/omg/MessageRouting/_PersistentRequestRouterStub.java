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
// IDL:omg.org/MessageRouting/PersistentRequestRouter:1.0
//
public class _PersistentRequestRouterStub extends org.omg.CORBA.portable.ObjectImpl
                                          implements PersistentRequestRouter
{
    private static final String[] _ob_ids_ =
    {
        "IDL:omg.org/MessageRouting/PersistentRequestRouter:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = PersistentRequestRouterOperations.class;

    //
    // IDL:omg.org/MessageRouting/PersistentRequestRouter/create_persistent_request:1.0
    //
    public PersistentRequest
    create_persistent_request(short _ob_a0,
                              Router[] _ob_a1,
                              org.omg.CORBA.Object _ob_a2,
                              org.omg.CORBA.Policy[] _ob_a3,
                              RequestMessage _ob_a4)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_persistent_request", true);
                    out.write_ushort(_ob_a0);
                    RouterListHelper.write(out, _ob_a1);
                    out.write_Object(_ob_a2);
                    org.omg.CORBA.PolicyListHelper.write(out, _ob_a3);
                    RequestMessageHelper.write(out, _ob_a4);
                    in = _invoke(out);
                    PersistentRequest _ob_r = PersistentRequestHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_persistent_request", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                PersistentRequestRouterOperations _ob_self = (PersistentRequestRouterOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_persistent_request(_ob_a0, _ob_a1, _ob_a2, _ob_a3, _ob_a4);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}

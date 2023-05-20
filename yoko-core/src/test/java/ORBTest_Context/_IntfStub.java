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
package ORBTest_Context;

//
// IDL:ORBTest_Context/Intf:1.0
//
public class _IntfStub extends org.omg.CORBA.portable.ObjectImpl
                       implements Intf
{
    private static final String[] _ob_ids_ =
    {
        "IDL:ORBTest_Context/Intf:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = IntfOperations.class;

    //
    // IDL:ORBTest_Context/Intf/opContext:1.0
    //
    public String[]
    opContext(String _ob_a0,
              org.omg.CORBA.Context _ctx)
    {
        final String[] _ob_cs = { "A*", "C*", "X", "Z" };
        org.omg.CORBA.ContextList _ob_cl = _orb().create_context_list();
        for(int _ob_ci = 0; _ob_ci < _ob_cs.length ; _ob_ci++)
            _ob_cl.add(_ob_cs[_ob_ci]);

        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("opContext", true);
                    out.write_string(_ob_a0);
                    out.write_Context(_ctx, _ob_cl);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("opContext", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                IntfOperations _ob_self = (IntfOperations)_ob_so.servant;
                org.omg.CORBA.Context _ob_lctx = _orb().get_default_context();
                for(int _ob_ci = 0; _ob_ci < _ob_cs.length; _ob_ci++)
                {
                    try
                    {
                        org.omg.CORBA.NVList _ob_nv = _ctx.get_values("", 0, _ob_cs[_ob_ci]);
                        for(int _ob_cj = 0; _ob_cj < _ob_nv.count(); _ob_cj++)
                        {
                            org.omg.CORBA.NamedValue _ob_val = _ob_nv.item(_ob_cj);
                            _ob_lctx.set_one_value(_ob_val.name(), _ob_val.value());
                        }
                    }
                    catch(org.omg.CORBA.Bounds _ob_cx)
                    {
                    }
                    catch(org.omg.CORBA.BAD_CONTEXT _ob_cx)
                    {
                    }
                }
                try
                {
                    return _ob_self.opContext(_ob_a0, _ob_lctx);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}

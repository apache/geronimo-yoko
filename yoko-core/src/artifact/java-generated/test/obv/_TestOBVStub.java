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
package test.obv;

//
// IDL:TestOBV:1.0
//
public class _TestOBVStub extends org.omg.CORBA.portable.ObjectImpl
                          implements TestOBV
{
    private static final String[] _ob_ids_ =
    {
        "IDL:TestOBV:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = TestOBVOperations.class;

    //
    // IDL:TestOBV/get_null_valuebase:1.0
    //
    public java.io.Serializable
    get_null_valuebase()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_null_valuebase", true);
                    in = _invoke(out);
                    java.io.Serializable _ob_r = org.omg.CORBA.ValueBaseHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_null_valuebase", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    java.io.Serializable _ob_r = _ob_self.get_null_valuebase();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    org.omg.CORBA.ValueBaseHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = org.omg.CORBA.ValueBaseHelper.read(_ob_in);
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
    // IDL:TestOBV/set_null_valuebase:1.0
    //
    public void
    set_null_valuebase(java.io.Serializable _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_null_valuebase", true);
                    org.omg.CORBA.ValueBaseHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_null_valuebase", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    org.omg.CORBA.ValueBaseHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = org.omg.CORBA.ValueBaseHelper.read(_ob_in);
                    _ob_self.set_null_valuebase(_ob_a0);
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
    // IDL:TestOBV/get_null_valuesub:1.0
    //
    public TestValueSub
    get_null_valuesub()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_null_valuesub", true);
                    in = _invoke(out);
                    TestValueSub _ob_r = TestValueSubHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_null_valuesub", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestValueSub _ob_r = _ob_self.get_null_valuesub();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueSubHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestValueSubHelper.read(_ob_in);
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
    // IDL:TestOBV/set_null_valuesub:1.0
    //
    public void
    set_null_valuesub(TestValueSub _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_null_valuesub", true);
                    TestValueSubHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_null_valuesub", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueSubHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestValueSubHelper.read(_ob_in);
                    _ob_self.set_null_valuesub(_ob_a0);
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
    // IDL:TestOBV/get_abs_value1:1.0
    //
    public TestAbsValue1
    get_abs_value1()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_abs_value1", true);
                    in = _invoke(out);
                    TestAbsValue1 _ob_r = TestAbsValue1Helper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_abs_value1", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestAbsValue1 _ob_r = _ob_self.get_abs_value1();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbsValue1Helper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestAbsValue1Helper.read(_ob_in);
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
    // IDL:TestOBV/set_abs_value1:1.0
    //
    public void
    set_abs_value1(TestAbsValue1 _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_abs_value1", true);
                    TestAbsValue1Helper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_abs_value1", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbsValue1Helper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestAbsValue1Helper.read(_ob_in);
                    _ob_self.set_abs_value1(_ob_a0);
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
    // IDL:TestOBV/get_abs_value2:1.0
    //
    public TestAbsValue2
    get_abs_value2()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_abs_value2", true);
                    in = _invoke(out);
                    TestAbsValue2 _ob_r = TestAbsValue2Helper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_abs_value2", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestAbsValue2 _ob_r = _ob_self.get_abs_value2();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbsValue2Helper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestAbsValue2Helper.read(_ob_in);
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
    // IDL:TestOBV/set_abs_value2:1.0
    //
    public void
    set_abs_value2(TestAbsValue2 _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_abs_value2", true);
                    TestAbsValue2Helper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_abs_value2", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbsValue2Helper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestAbsValue2Helper.read(_ob_in);
                    _ob_self.set_abs_value2(_ob_a0);
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
    // IDL:TestOBV/get_value:1.0
    //
    public TestValue
    get_value()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_value", true);
                    in = _invoke(out);
                    TestValue _ob_r = TestValueHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestValue _ob_r = _ob_self.get_value();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestValueHelper.read(_ob_in);
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
    // IDL:TestOBV/set_value:1.0
    //
    public void
    set_value(TestValue _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_value", true);
                    TestValueHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestValueHelper.read(_ob_in);
                    _ob_self.set_value(_ob_a0);
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
    // IDL:TestOBV/get_valuesub:1.0
    //
    public TestValueSub
    get_valuesub()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_valuesub", true);
                    in = _invoke(out);
                    TestValueSub _ob_r = TestValueSubHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_valuesub", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestValueSub _ob_r = _ob_self.get_valuesub();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueSubHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestValueSubHelper.read(_ob_in);
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
    // IDL:TestOBV/set_valuesub:1.0
    //
    public void
    set_valuesub(TestValueSub _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_valuesub", true);
                    TestValueSubHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_valuesub", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueSubHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestValueSubHelper.read(_ob_in);
                    _ob_self.set_valuesub(_ob_a0);
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
    // IDL:TestOBV/get_valuesub_as_value:1.0
    //
    public TestValue
    get_valuesub_as_value()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_valuesub_as_value", true);
                    in = _invoke(out);
                    TestValue _ob_r = TestValueHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_valuesub_as_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestValue _ob_r = _ob_self.get_valuesub_as_value();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestValueHelper.read(_ob_in);
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
    // IDL:TestOBV/set_valuesub_as_value:1.0
    //
    public void
    set_valuesub_as_value(TestValue _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_valuesub_as_value", true);
                    TestValueHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_valuesub_as_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestValueHelper.read(_ob_in);
                    _ob_self.set_valuesub_as_value(_ob_a0);
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
    // IDL:TestOBV/get_two_values:1.0
    //
    public void
    get_two_values(TestValueHolder _ob_ah0,
                   TestValueHolder _ob_ah1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_two_values", true);
                    in = _invoke(out);
                    _ob_ah0.value = TestValueHelper.read(in);
                    _ob_ah1.value = TestValueHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_two_values", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.get_two_values(_ob_ah0, _ob_ah1);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_ah0.value);
                    TestValueHelper.write(_ob_out, _ob_ah1.value);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_ah0.value = TestValueHelper.read(_ob_in);
                    _ob_ah1.value = TestValueHelper.read(_ob_in);
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
    // IDL:TestOBV/set_two_values:1.0
    //
    public void
    set_two_values(TestValue _ob_a0,
                   TestValue _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_two_values", true);
                    TestValueHelper.write(out, _ob_a0);
                    TestValueHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_two_values", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_a0);
                    TestValueHelper.write(_ob_out, _ob_a1);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestValueHelper.read(_ob_in);
                    _ob_a1 = TestValueHelper.read(_ob_in);
                    _ob_self.set_two_values(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_two_valuesubs_as_values:1.0
    //
    public void
    get_two_valuesubs_as_values(TestValueHolder _ob_ah0,
                                TestValueHolder _ob_ah1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_two_valuesubs_as_values", true);
                    in = _invoke(out);
                    _ob_ah0.value = TestValueHelper.read(in);
                    _ob_ah1.value = TestValueHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_two_valuesubs_as_values", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.get_two_valuesubs_as_values(_ob_ah0, _ob_ah1);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_ah0.value);
                    TestValueHelper.write(_ob_out, _ob_ah1.value);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_ah0.value = TestValueHelper.read(_ob_in);
                    _ob_ah1.value = TestValueHelper.read(_ob_in);
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
    // IDL:TestOBV/set_two_valuesubs_as_values:1.0
    //
    public void
    set_two_valuesubs_as_values(TestValue _ob_a0,
                                TestValue _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_two_valuesubs_as_values", true);
                    TestValueHelper.write(out, _ob_a0);
                    TestValueHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_two_valuesubs_as_values", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_a0);
                    TestValueHelper.write(_ob_out, _ob_a1);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestValueHelper.read(_ob_in);
                    _ob_a1 = TestValueHelper.read(_ob_in);
                    _ob_self.set_two_valuesubs_as_values(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_custom:1.0
    //
    public TestCustom
    get_custom()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_custom", true);
                    in = _invoke(out);
                    TestCustom _ob_r = TestCustomHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_custom", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestCustom _ob_r = _ob_self.get_custom();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestCustomHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestCustomHelper.read(_ob_in);
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
    // IDL:TestOBV/set_custom:1.0
    //
    public void
    set_custom(TestCustom _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_custom", true);
                    TestCustomHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_custom", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestCustomHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestCustomHelper.read(_ob_in);
                    _ob_self.set_custom(_ob_a0);
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
    // IDL:TestOBV/get_abs_custom:1.0
    //
    public TestAbsValue1
    get_abs_custom()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_abs_custom", true);
                    in = _invoke(out);
                    TestAbsValue1 _ob_r = TestAbsValue1Helper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_abs_custom", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestAbsValue1 _ob_r = _ob_self.get_abs_custom();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbsValue1Helper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestAbsValue1Helper.read(_ob_in);
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
    // IDL:TestOBV/set_abs_custom:1.0
    //
    public void
    set_abs_custom(TestAbsValue1 _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_abs_custom", true);
                    TestAbsValue1Helper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_abs_custom", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbsValue1Helper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestAbsValue1Helper.read(_ob_in);
                    _ob_self.set_abs_custom(_ob_a0);
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
    // IDL:TestOBV/get_node:1.0
    //
    public void
    get_node(TestNodeHolder _ob_ah0,
             org.omg.CORBA.IntHolder _ob_ah1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_node", true);
                    in = _invoke(out);
                    _ob_ah0.value = TestNodeHelper.read(in);
                    _ob_ah1.value = in.read_ulong();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_node", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.get_node(_ob_ah0, _ob_ah1);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestNodeHelper.write(_ob_out, _ob_ah0.value);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_ah0.value = TestNodeHelper.read(_ob_in);
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
    // IDL:TestOBV/set_node:1.0
    //
    public void
    set_node(TestNode _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_node", true);
                    TestNodeHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_node", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestNodeHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestNodeHelper.read(_ob_in);
                    _ob_self.set_node(_ob_a0);
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
    // IDL:TestOBV/get_string_box:1.0
    //
    public String
    get_string_box(String _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_string_box", true);
                    out.write_string(_ob_a0);
                    in = _invoke(out);
                    String _ob_r = TestStringBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_string_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    String _ob_r = _ob_self.get_string_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestStringBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestStringBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_string_box:1.0
    //
    public void
    set_string_box(String _ob_a0,
                   String _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_string_box", true);
                    TestStringBoxHelper.write(out, _ob_a0);
                    out.write_string(_ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_string_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestStringBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestStringBoxHelper.read(_ob_in);
                    _ob_self.set_string_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_ulong_box:1.0
    //
    public TestULongBox
    get_ulong_box(int _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_ulong_box", true);
                    out.write_ulong(_ob_a0);
                    in = _invoke(out);
                    TestULongBox _ob_r = TestULongBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_ulong_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestULongBox _ob_r = _ob_self.get_ulong_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestULongBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestULongBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_ulong_box:1.0
    //
    public void
    set_ulong_box(TestULongBox _ob_a0,
                  int _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_ulong_box", true);
                    TestULongBoxHelper.write(out, _ob_a0);
                    out.write_ulong(_ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_ulong_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestULongBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestULongBoxHelper.read(_ob_in);
                    _ob_self.set_ulong_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_fix_struct_box:1.0
    //
    public TestFixStruct
    get_fix_struct_box(TestFixStruct _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_fix_struct_box", true);
                    TestFixStructHelper.write(out, _ob_a0);
                    in = _invoke(out);
                    TestFixStruct _ob_r = TestFixStructBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_fix_struct_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestFixStruct _ob_r = _ob_self.get_fix_struct_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestFixStructBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestFixStructBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_fix_struct_box:1.0
    //
    public void
    set_fix_struct_box(TestFixStruct _ob_a0,
                       TestFixStruct _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_fix_struct_box", true);
                    TestFixStructBoxHelper.write(out, _ob_a0);
                    TestFixStructHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_fix_struct_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestFixStructBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestFixStructBoxHelper.read(_ob_in);
                    _ob_self.set_fix_struct_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_var_struct_box:1.0
    //
    public TestVarStruct
    get_var_struct_box(TestVarStruct _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_var_struct_box", true);
                    TestVarStructHelper.write(out, _ob_a0);
                    in = _invoke(out);
                    TestVarStruct _ob_r = TestVarStructBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_var_struct_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestVarStruct _ob_r = _ob_self.get_var_struct_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestVarStructBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestVarStructBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_var_struct_box:1.0
    //
    public void
    set_var_struct_box(TestVarStruct _ob_a0,
                       TestVarStruct _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_var_struct_box", true);
                    TestVarStructBoxHelper.write(out, _ob_a0);
                    TestVarStructHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_var_struct_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestVarStructBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestVarStructBoxHelper.read(_ob_in);
                    _ob_self.set_var_struct_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_fix_union_box:1.0
    //
    public TestFixUnion
    get_fix_union_box(TestFixUnion _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_fix_union_box", true);
                    TestFixUnionHelper.write(out, _ob_a0);
                    in = _invoke(out);
                    TestFixUnion _ob_r = TestFixUnionBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_fix_union_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestFixUnion _ob_r = _ob_self.get_fix_union_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestFixUnionBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestFixUnionBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_fix_union_box:1.0
    //
    public void
    set_fix_union_box(TestFixUnion _ob_a0,
                      TestFixUnion _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_fix_union_box", true);
                    TestFixUnionBoxHelper.write(out, _ob_a0);
                    TestFixUnionHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_fix_union_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestFixUnionBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestFixUnionBoxHelper.read(_ob_in);
                    _ob_self.set_fix_union_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_var_union_box:1.0
    //
    public TestVarUnion
    get_var_union_box(TestVarUnion _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_var_union_box", true);
                    TestVarUnionHelper.write(out, _ob_a0);
                    in = _invoke(out);
                    TestVarUnion _ob_r = TestVarUnionBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_var_union_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestVarUnion _ob_r = _ob_self.get_var_union_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestVarUnionBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestVarUnionBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_var_union_box:1.0
    //
    public void
    set_var_union_box(TestVarUnion _ob_a0,
                      TestVarUnion _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_var_union_box", true);
                    TestVarUnionBoxHelper.write(out, _ob_a0);
                    TestVarUnionHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_var_union_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestVarUnionBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestVarUnionBoxHelper.read(_ob_in);
                    _ob_self.set_var_union_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_anon_seq_box:1.0
    //
    public short[]
    get_anon_seq_box(int _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_anon_seq_box", true);
                    out.write_ulong(_ob_a0);
                    in = _invoke(out);
                    short[] _ob_r = TestAnonSeqBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_anon_seq_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    short[] _ob_r = _ob_self.get_anon_seq_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAnonSeqBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestAnonSeqBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_anon_seq_box:1.0
    //
    public void
    set_anon_seq_box(short[] _ob_a0,
                     int _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_anon_seq_box", true);
                    TestAnonSeqBoxHelper.write(out, _ob_a0);
                    out.write_ulong(_ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_anon_seq_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAnonSeqBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestAnonSeqBoxHelper.read(_ob_in);
                    _ob_self.set_anon_seq_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_string_seq_box:1.0
    //
    public String[]
    get_string_seq_box(String[] _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_string_seq_box", true);
                    TestStringSeqHelper.write(out, _ob_a0);
                    in = _invoke(out);
                    String[] _ob_r = TestStringSeqBoxHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_string_seq_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    String[] _ob_r = _ob_self.get_string_seq_box(_ob_a0);
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestStringSeqBoxHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestStringSeqBoxHelper.read(_ob_in);
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
    // IDL:TestOBV/set_string_seq_box:1.0
    //
    public void
    set_string_seq_box(String[] _ob_a0,
                       String[] _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_string_seq_box", true);
                    TestStringSeqBoxHelper.write(out, _ob_a0);
                    TestStringSeqHelper.write(out, _ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_string_seq_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestStringSeqBoxHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestStringSeqBoxHelper.read(_ob_in);
                    _ob_self.set_string_seq_box(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_ai_interface:1.0
    //
    public TestAbstract
    get_ai_interface()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_ai_interface", true);
                    in = _invoke(out);
                    TestAbstract _ob_r = TestAbstractHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_ai_interface", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestAbstract _ob_r = _ob_self.get_ai_interface();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbstractHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestAbstractHelper.read(_ob_in);
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
    // IDL:TestOBV/set_ai_interface:1.0
    //
    public void
    set_ai_interface(TestAbstract _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_ai_interface", true);
                    TestAbstractHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_ai_interface", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbstractHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestAbstractHelper.read(_ob_in);
                    _ob_self.set_ai_interface(_ob_a0);
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
    // IDL:TestOBV/get_ai_interface_any:1.0
    //
    public org.omg.CORBA.Any
    get_ai_interface_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_ai_interface_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_ai_interface_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_ai_interface_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/set_ai_interface_any:1.0
    //
    public void
    set_ai_interface_any(org.omg.CORBA.Any _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_ai_interface_any", true);
                    out.write_any(_ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_ai_interface_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.set_ai_interface_any(_ob_a0);
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
    // IDL:TestOBV/get_ai_value:1.0
    //
    public TestAbstract
    get_ai_value()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_ai_value", true);
                    in = _invoke(out);
                    TestAbstract _ob_r = TestAbstractHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_ai_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestAbstract _ob_r = _ob_self.get_ai_value();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbstractHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestAbstractHelper.read(_ob_in);
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
    // IDL:TestOBV/set_ai_value:1.0
    //
    public void
    set_ai_value(TestAbstract _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_ai_value", true);
                    TestAbstractHelper.write(out, _ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_ai_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbstractHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestAbstractHelper.read(_ob_in);
                    _ob_self.set_ai_value(_ob_a0);
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
    // IDL:TestOBV/get_ai_value_any:1.0
    //
    public org.omg.CORBA.Any
    get_ai_value_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_ai_value_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_ai_value_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_ai_value_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/set_ai_value_any:1.0
    //
    public void
    set_ai_value_any(org.omg.CORBA.Any _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_ai_value_any", true);
                    out.write_any(_ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_ai_value_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.set_ai_value_any(_ob_a0);
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
    // IDL:TestOBV/get_trunc1:1.0
    //
    public TestTruncBase
    get_trunc1()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_trunc1", true);
                    in = _invoke(out);
                    TestTruncBase _ob_r = TestTruncBaseHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_trunc1", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestTruncBase _ob_r = _ob_self.get_trunc1();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestTruncBaseHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestTruncBaseHelper.read(_ob_in);
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
    // IDL:TestOBV/get_trunc2:1.0
    //
    public TestTruncBase
    get_trunc2()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_trunc2", true);
                    in = _invoke(out);
                    TestTruncBase _ob_r = TestTruncBaseHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_trunc2", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestTruncBase _ob_r = _ob_self.get_trunc2();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestTruncBaseHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestTruncBaseHelper.read(_ob_in);
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
    // IDL:TestOBV/get_value_any:1.0
    //
    public org.omg.CORBA.Any
    get_value_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_value_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_value_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_value_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/get_valuesub_any:1.0
    //
    public org.omg.CORBA.Any
    get_valuesub_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_valuesub_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_valuesub_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_valuesub_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/get_valuesub_as_value_any:1.0
    //
    public org.omg.CORBA.Any
    get_valuesub_as_value_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_valuesub_as_value_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_valuesub_as_value_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_valuesub_as_value_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/get_custom_any:1.0
    //
    public org.omg.CORBA.Any
    get_custom_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_custom_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_custom_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_custom_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/get_trunc1_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc1_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_trunc1_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_trunc1_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_trunc1_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/get_trunc1_as_base_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc1_as_base_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_trunc1_as_base_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_trunc1_as_base_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_trunc1_as_base_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/get_trunc2_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc2_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_trunc2_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_trunc2_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_trunc2_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/get_trunc2_as_base_any:1.0
    //
    public org.omg.CORBA.Any
    get_trunc2_as_base_any()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_trunc2_as_base_any", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_trunc2_as_base_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_trunc2_as_base_any();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/remarshal_any:1.0
    //
    public void
    remarshal_any(org.omg.CORBA.Any _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("remarshal_any", true);
                    out.write_any(_ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("remarshal_any", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.remarshal_any(_ob_a0);
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
    // IDL:TestOBV/get_two_value_anys:1.0
    //
    public void
    get_two_value_anys(org.omg.CORBA.AnyHolder _ob_ah0,
                       org.omg.CORBA.AnyHolder _ob_ah1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_two_value_anys", true);
                    in = _invoke(out);
                    _ob_ah0.value = in.read_any();
                    _ob_ah1.value = in.read_any();
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_two_value_anys", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.get_two_value_anys(_ob_ah0, _ob_ah1);
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
    // IDL:TestOBV/set_two_value_anys:1.0
    //
    public void
    set_two_value_anys(org.omg.CORBA.Any _ob_a0,
                       org.omg.CORBA.Any _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_two_value_anys", true);
                    out.write_any(_ob_a0);
                    out.write_any(_ob_a1);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_two_value_anys", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.set_two_value_anys(_ob_a0, _ob_a1);
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
    // IDL:TestOBV/get_value_as_value:1.0
    //
    public TestValueInterface
    get_value_as_value()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_value_as_value", true);
                    in = _invoke(out);
                    TestValueInterface _ob_r = TestValueInterfaceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_value_as_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    TestValueInterface _ob_r = _ob_self.get_value_as_value();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueInterfaceHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestValueInterfaceHelper.read(_ob_in);
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
    // IDL:TestOBV/get_value_as_interface:1.0
    //
    public TestInterface
    get_value_as_interface()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("get_value_as_interface", true);
                    in = _invoke(out);
                    TestInterface _ob_r = TestInterfaceHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("get_value_as_interface", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    return _ob_self.get_value_as_interface();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:TestOBV/deactivate:1.0
    //
    public void
    deactivate()
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("deactivate", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVOperations _ob_self = (TestOBVOperations)_ob_so.servant;
                try
                {
                    _ob_self.deactivate();
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

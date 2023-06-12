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
// IDL:TestOBVColo:1.0
//
public class _TestOBVColoStub extends org.omg.CORBA.portable.ObjectImpl
                              implements TestOBVColo
{
    private static final String[] _ob_ids_ =
    {
        "IDL:TestOBVColo:1.0",
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = TestOBVColoOperations.class;

    //
    // IDL:TestOBVColo/test_value_attribute:1.0
    //
    public TestValue
    test_value_attribute()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_test_value_attribute", true);
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
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    TestValue _ob_r = _ob_self.test_value_attribute();
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

    public void
    test_value_attribute(TestValue _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_test_value_attribute", true);
                    TestValueHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_a);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a = TestValueHelper.read(_ob_in);
                    _ob_self.test_value_attribute(_ob_a);
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
    // IDL:TestOBVColo/test_value_struct_attribute:1.0
    //
    public test.obv.TestOBVColoPackage.SV
    test_value_struct_attribute()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_test_value_struct_attribute", true);
                    in = _invoke(out);
                    test.obv.TestOBVColoPackage.SV _ob_r = test.obv.TestOBVColoPackage.SVHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_struct_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    test.obv.TestOBVColoPackage.SV _ob_r = _ob_self.test_value_struct_attribute();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.SVHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = test.obv.TestOBVColoPackage.SVHelper.read(_ob_in);
                    return _ob_r;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    test_value_struct_attribute(test.obv.TestOBVColoPackage.SV _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_test_value_struct_attribute", true);
                    test.obv.TestOBVColoPackage.SVHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_struct_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.SVHelper.write(_ob_out, _ob_a);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a = test.obv.TestOBVColoPackage.SVHelper.read(_ob_in);
                    _ob_self.test_value_struct_attribute(_ob_a);
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
    // IDL:TestOBVColo/test_value_union_attribute:1.0
    //
    public test.obv.TestOBVColoPackage.UV
    test_value_union_attribute()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_test_value_union_attribute", true);
                    in = _invoke(out);
                    test.obv.TestOBVColoPackage.UV _ob_r = test.obv.TestOBVColoPackage.UVHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_union_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    test.obv.TestOBVColoPackage.UV _ob_r = _ob_self.test_value_union_attribute();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.UVHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = test.obv.TestOBVColoPackage.UVHelper.read(_ob_in);
                    return _ob_r;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    test_value_union_attribute(test.obv.TestOBVColoPackage.UV _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_test_value_union_attribute", true);
                    test.obv.TestOBVColoPackage.UVHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_union_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.UVHelper.write(_ob_out, _ob_a);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a = test.obv.TestOBVColoPackage.UVHelper.read(_ob_in);
                    _ob_self.test_value_union_attribute(_ob_a);
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
    // IDL:TestOBVColo/test_value_seq_attribute:1.0
    //
    public TestValue[]
    test_value_seq_attribute()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_test_value_seq_attribute", true);
                    in = _invoke(out);
                    TestValue[] _ob_r = test.obv.TestOBVColoPackage.VSeqHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_seq_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    TestValue[] _ob_r = _ob_self.test_value_seq_attribute();
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.VSeqHelper.write(_ob_out, _ob_r);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_r = test.obv.TestOBVColoPackage.VSeqHelper.read(_ob_in);
                    return _ob_r;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    public void
    test_value_seq_attribute(TestValue[] _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_test_value_seq_attribute", true);
                    test.obv.TestOBVColoPackage.VSeqHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_seq_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.VSeqHelper.write(_ob_out, _ob_a);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a = test.obv.TestOBVColoPackage.VSeqHelper.read(_ob_in);
                    _ob_self.test_value_seq_attribute(_ob_a);
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
    // IDL:TestOBVColo/test_abstract_attribute:1.0
    //
    public TestAbstract
    test_abstract_attribute()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_test_abstract_attribute", true);
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
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_abstract_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    TestAbstract _ob_r = _ob_self.test_abstract_attribute();
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

    public void
    test_abstract_attribute(TestAbstract _ob_a)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_set_test_abstract_attribute", true);
                    TestAbstractHelper.write(out, _ob_a);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_abstract_attribute", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbstractHelper.write(_ob_out, _ob_a);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a = TestAbstractHelper.read(_ob_in);
                    _ob_self.test_abstract_attribute(_ob_a);
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
    // IDL:TestOBVColo/set_expected_count:1.0
    //
    public void
    set_expected_count(int _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("set_expected_count", true);
                    out.write_long(_ob_a0);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("set_expected_count", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    _ob_self.set_expected_count(_ob_a0);
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
    // IDL:TestOBVColo/test_value_op:1.0
    //
    public TestValue
    test_value_op(TestValue _ob_a0,
                  TestValueHolder _ob_ah1,
                  TestValueHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("test_value_op", true);
                    TestValueHelper.write(out, _ob_a0);
                    TestValueHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    TestValue _ob_r = TestValueHelper.read(in);
                    _ob_ah1.value = TestValueHelper.read(in);
                    _ob_ah2.value = TestValueHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_op", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_a0);
                    TestValueHelper.write(_ob_out, _ob_ah1.value);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestValueHelper.read(_ob_in);
                    _ob_ah1.value = TestValueHelper.read(_ob_in);
                    TestValue _ob_r = _ob_self.test_value_op(_ob_a0, _ob_ah1, _ob_ah2);
                    _ob_out = _orb().create_output_stream();
                    TestValueHelper.write(_ob_out, _ob_r);
                    TestValueHelper.write(_ob_out, _ob_ah1.value);
                    TestValueHelper.write(_ob_out, _ob_ah2.value);
                    _ob_in = _ob_out.create_input_stream();
                    _ob_r = TestValueHelper.read(_ob_in);
                    _ob_ah1.value = TestValueHelper.read(_ob_in);
                    _ob_ah2.value = TestValueHelper.read(_ob_in);
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
    // IDL:TestOBVColo/test_value_struct_op:1.0
    //
    public test.obv.TestOBVColoPackage.SV
    test_value_struct_op(test.obv.TestOBVColoPackage.SV _ob_a0,
                         test.obv.TestOBVColoPackage.SVHolder _ob_ah1,
                         test.obv.TestOBVColoPackage.SVHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("test_value_struct_op", true);
                    test.obv.TestOBVColoPackage.SVHelper.write(out, _ob_a0);
                    test.obv.TestOBVColoPackage.SVHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    test.obv.TestOBVColoPackage.SV _ob_r = test.obv.TestOBVColoPackage.SVHelper.read(in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.SVHelper.read(in);
                    _ob_ah2.value = test.obv.TestOBVColoPackage.SVHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_struct_op", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.SVHelper.write(_ob_out, _ob_a0);
                    test.obv.TestOBVColoPackage.SVHelper.write(_ob_out, _ob_ah1.value);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = test.obv.TestOBVColoPackage.SVHelper.read(_ob_in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.SVHelper.read(_ob_in);
                    test.obv.TestOBVColoPackage.SV _ob_r = _ob_self.test_value_struct_op(_ob_a0, _ob_ah1, _ob_ah2);
                    _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.SVHelper.write(_ob_out, _ob_r);
                    test.obv.TestOBVColoPackage.SVHelper.write(_ob_out, _ob_ah1.value);
                    test.obv.TestOBVColoPackage.SVHelper.write(_ob_out, _ob_ah2.value);
                    _ob_in = _ob_out.create_input_stream();
                    _ob_r = test.obv.TestOBVColoPackage.SVHelper.read(_ob_in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.SVHelper.read(_ob_in);
                    _ob_ah2.value = test.obv.TestOBVColoPackage.SVHelper.read(_ob_in);
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
    // IDL:TestOBVColo/test_value_union_op:1.0
    //
    public test.obv.TestOBVColoPackage.UV
    test_value_union_op(test.obv.TestOBVColoPackage.UV _ob_a0,
                        test.obv.TestOBVColoPackage.UVHolder _ob_ah1,
                        test.obv.TestOBVColoPackage.UVHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("test_value_union_op", true);
                    test.obv.TestOBVColoPackage.UVHelper.write(out, _ob_a0);
                    test.obv.TestOBVColoPackage.UVHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    test.obv.TestOBVColoPackage.UV _ob_r = test.obv.TestOBVColoPackage.UVHelper.read(in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.UVHelper.read(in);
                    _ob_ah2.value = test.obv.TestOBVColoPackage.UVHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_union_op", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.UVHelper.write(_ob_out, _ob_a0);
                    test.obv.TestOBVColoPackage.UVHelper.write(_ob_out, _ob_ah1.value);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = test.obv.TestOBVColoPackage.UVHelper.read(_ob_in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.UVHelper.read(_ob_in);
                    test.obv.TestOBVColoPackage.UV _ob_r = _ob_self.test_value_union_op(_ob_a0, _ob_ah1, _ob_ah2);
                    _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.UVHelper.write(_ob_out, _ob_r);
                    test.obv.TestOBVColoPackage.UVHelper.write(_ob_out, _ob_ah1.value);
                    test.obv.TestOBVColoPackage.UVHelper.write(_ob_out, _ob_ah2.value);
                    _ob_in = _ob_out.create_input_stream();
                    _ob_r = test.obv.TestOBVColoPackage.UVHelper.read(_ob_in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.UVHelper.read(_ob_in);
                    _ob_ah2.value = test.obv.TestOBVColoPackage.UVHelper.read(_ob_in);
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
    // IDL:TestOBVColo/test_value_seq_op:1.0
    //
    public TestValue[]
    test_value_seq_op(TestValue[] _ob_a0,
                      test.obv.TestOBVColoPackage.VSeqHolder _ob_ah1,
                      test.obv.TestOBVColoPackage.VSeqHolder _ob_ah2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("test_value_seq_op", true);
                    test.obv.TestOBVColoPackage.VSeqHelper.write(out, _ob_a0);
                    test.obv.TestOBVColoPackage.VSeqHelper.write(out, _ob_ah1.value);
                    in = _invoke(out);
                    TestValue[] _ob_r = test.obv.TestOBVColoPackage.VSeqHelper.read(in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.VSeqHelper.read(in);
                    _ob_ah2.value = test.obv.TestOBVColoPackage.VSeqHelper.read(in);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_value_seq_op", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    test.obv.TestOBVColoPackage.VSeqHelper.write(_ob_out, _ob_a0);
                    test.obv.TestOBVColoPackage.VSeqHelper.write(_ob_out, _ob_ah1.value);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = test.obv.TestOBVColoPackage.VSeqHelper.read(_ob_in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.VSeqHelper.read(_ob_in);
                    TestValue[] _ob_r = _ob_self.test_value_seq_op(_ob_a0, _ob_ah1, _ob_ah2);
                    _ob_out = _orb().create_output_stream();
                    int len0 = _ob_r.length;
                    _ob_out.write_ulong(len0);
                    for(int i0 = 0; i0 < len0; i0++)
                        TestValueHelper.write(_ob_out, _ob_r[i0]);
                    test.obv.TestOBVColoPackage.VSeqHelper.write(_ob_out, _ob_ah1.value);
                    test.obv.TestOBVColoPackage.VSeqHelper.write(_ob_out, _ob_ah2.value);
                    _ob_in = _ob_out.create_input_stream();
                    int len1 = _ob_in.read_ulong();
                    _ob_r = new TestValue[len1];
                    for(int i1 = 0; i1 < len1; i1++)
                        _ob_r[i1] = TestValueHelper.read(_ob_in);
                    _ob_ah1.value = test.obv.TestOBVColoPackage.VSeqHelper.read(_ob_in);
                    _ob_ah2.value = test.obv.TestOBVColoPackage.VSeqHelper.read(_ob_in);
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
    // IDL:TestOBVColo/test_abstract_op:1.0
    //
    public void
    test_abstract_op(TestAbstract _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("test_abstract_op", true);
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
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("test_abstract_op", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                TestOBVColoOperations _ob_self = (TestOBVColoOperations)_ob_so.servant;
                try
                {
                    org.omg.CORBA.portable.OutputStream _ob_out = _orb().create_output_stream();
                    TestAbstractHelper.write(_ob_out, _ob_a0);
                    org.omg.CORBA.portable.InputStream _ob_in = _ob_out.create_input_stream();
                    _ob_a0 = TestAbstractHelper.read(_ob_in);
                    _ob_self.test_abstract_op(_ob_a0);
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

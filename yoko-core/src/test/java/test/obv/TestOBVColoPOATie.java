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
public class TestOBVColoPOATie extends TestOBVColoPOA
{
    private TestOBVColoOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    TestOBVColoPOATie(TestOBVColoOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    TestOBVColoPOATie(TestOBVColoOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public TestOBVColoOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(TestOBVColoOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public org.omg.PortableServer.POA
    _default_POA()
    {
        if(_ob_poa_ != null)
            return _ob_poa_;
        else
            return super._default_POA();
    }

    //
    // IDL:TestOBVColo/test_value_attribute:1.0
    //
    public TestValue
    test_value_attribute()
    {
        return _ob_delegate_.test_value_attribute();
    }

    public void
    test_value_attribute(TestValue val)
    {
        _ob_delegate_.test_value_attribute(val);
    }

    //
    // IDL:TestOBVColo/test_value_struct_attribute:1.0
    //
    public test.obv.TestOBVColoPackage.SV
    test_value_struct_attribute()
    {
        return _ob_delegate_.test_value_struct_attribute();
    }

    public void
    test_value_struct_attribute(test.obv.TestOBVColoPackage.SV val)
    {
        _ob_delegate_.test_value_struct_attribute(val);
    }

    //
    // IDL:TestOBVColo/test_value_union_attribute:1.0
    //
    public test.obv.TestOBVColoPackage.UV
    test_value_union_attribute()
    {
        return _ob_delegate_.test_value_union_attribute();
    }

    public void
    test_value_union_attribute(test.obv.TestOBVColoPackage.UV val)
    {
        _ob_delegate_.test_value_union_attribute(val);
    }

    //
    // IDL:TestOBVColo/test_value_seq_attribute:1.0
    //
    public TestValue[]
    test_value_seq_attribute()
    {
        return _ob_delegate_.test_value_seq_attribute();
    }

    public void
    test_value_seq_attribute(TestValue[] val)
    {
        _ob_delegate_.test_value_seq_attribute(val);
    }

    //
    // IDL:TestOBVColo/test_abstract_attribute:1.0
    //
    public TestAbstract
    test_abstract_attribute()
    {
        return _ob_delegate_.test_abstract_attribute();
    }

    public void
    test_abstract_attribute(TestAbstract val)
    {
        _ob_delegate_.test_abstract_attribute(val);
    }

    //
    // IDL:TestOBVColo/set_expected_count:1.0
    //
    public void
    set_expected_count(int l)
    {
        _ob_delegate_.set_expected_count(l);
    }

    //
    // IDL:TestOBVColo/test_value_op:1.0
    //
    public TestValue
    test_value_op(TestValue v1,
                  TestValueHolder v2,
                  TestValueHolder v3)
    {
        return _ob_delegate_.test_value_op(v1,
                                           v2,
                                           v3);
    }

    //
    // IDL:TestOBVColo/test_value_struct_op:1.0
    //
    public test.obv.TestOBVColoPackage.SV
    test_value_struct_op(test.obv.TestOBVColoPackage.SV s1,
                         test.obv.TestOBVColoPackage.SVHolder s2,
                         test.obv.TestOBVColoPackage.SVHolder s3)
    {
        return _ob_delegate_.test_value_struct_op(s1,
                                                  s2,
                                                  s3);
    }

    //
    // IDL:TestOBVColo/test_value_union_op:1.0
    //
    public test.obv.TestOBVColoPackage.UV
    test_value_union_op(test.obv.TestOBVColoPackage.UV u1,
                        test.obv.TestOBVColoPackage.UVHolder u2,
                        test.obv.TestOBVColoPackage.UVHolder u3)
    {
        return _ob_delegate_.test_value_union_op(u1,
                                                 u2,
                                                 u3);
    }

    //
    // IDL:TestOBVColo/test_value_seq_op:1.0
    //
    public TestValue[]
    test_value_seq_op(TestValue[] s1,
                      test.obv.TestOBVColoPackage.VSeqHolder s2,
                      test.obv.TestOBVColoPackage.VSeqHolder s3)
    {
        return _ob_delegate_.test_value_seq_op(s1,
                                               s2,
                                               s3);
    }

    //
    // IDL:TestOBVColo/test_abstract_op:1.0
    //
    public void
    test_abstract_op(TestAbstract a)
    {
        _ob_delegate_.test_abstract_op(a);
    }
}

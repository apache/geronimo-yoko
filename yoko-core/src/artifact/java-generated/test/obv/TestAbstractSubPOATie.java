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
// IDL:TestAbstractSub:1.0
//
public class TestAbstractSubPOATie extends TestAbstractSubPOA
{
    private TestAbstractSubOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    TestAbstractSubPOATie(TestAbstractSubOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    TestAbstractSubPOATie(TestAbstractSubOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public TestAbstractSubOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(TestAbstractSubOperations delegate)
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
    // IDL:TestAbstractSub/sub_op:1.0
    //
    public void
    sub_op()
    {
        _ob_delegate_.sub_op();
    }

    //
    // IDL:TestAbstract/abstract_op:1.0
    //
    public void
    abstract_op()
    {
        _ob_delegate_.abstract_op();
    }
}

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
// IDL:TestInterface:1.0
//
public class TestInterfacePOATie extends TestInterfacePOA
{
    private TestInterfaceOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    TestInterfacePOATie(TestInterfaceOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    TestInterfacePOATie(TestInterfaceOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public TestInterfaceOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(TestInterfaceOperations delegate)
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
    // IDL:TestInterface/get_count:1.0
    //
    public int
    get_count()
    {
        return _ob_delegate_.get_count();
    }
}

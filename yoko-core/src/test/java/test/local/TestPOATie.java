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
package test.local;

//
// IDL:Test:1.0
//
public class TestPOATie extends TestPOA
{
    private TestOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    TestPOATie(TestOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    TestPOATie(TestOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public TestOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(TestOperations delegate)
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
    // IDL:Test/say:1.0
    //
    public void
    say(String s)
    {
        _ob_delegate_.say(s);
    }

    //
    // IDL:Test/intest:1.0
    //
    public void
    intest(Test t)
    {
        _ob_delegate_.intest(t);
    }

    //
    // IDL:Test/inany:1.0
    //
    public void
    inany(org.omg.CORBA.Any a)
    {
        _ob_delegate_.inany(a);
    }

    //
    // IDL:Test/outany:1.0
    //
    public void
    outany(org.omg.CORBA.AnyHolder a)
    {
        _ob_delegate_.outany(a);
    }

    //
    // IDL:Test/returntest:1.0
    //
    public Test
    returntest()
    {
        return _ob_delegate_.returntest();
    }

    //
    // IDL:Test/shutdown:1.0
    //
    public void
    shutdown()
    {
        _ob_delegate_.shutdown();
    }
}

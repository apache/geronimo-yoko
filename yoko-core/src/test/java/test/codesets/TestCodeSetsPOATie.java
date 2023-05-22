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
package test.codesets;

//
// IDL:TestCodeSets:1.0
//
public class TestCodeSetsPOATie extends TestCodeSetsPOA
{
    private TestCodeSetsOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    TestCodeSetsPOATie(TestCodeSetsOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    TestCodeSetsPOATie(TestCodeSetsOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public TestCodeSetsOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(TestCodeSetsOperations delegate)
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
    // IDL:TestCodeSets/testChar:1.0
    //
    public char
    testChar(char c)
    {
        return _ob_delegate_.testChar(c);
    }

    //
    // IDL:TestCodeSets/testString:1.0
    //
    public String
    testString(String s)
    {
        return _ob_delegate_.testString(s);
    }

    //
    // IDL:TestCodeSets/testWChar:1.0
    //
    public char
    testWChar(char wc)
    {
        return _ob_delegate_.testWChar(wc);
    }

    //
    // IDL:TestCodeSets/testWString:1.0
    //
    public String
    testWString(String ws)
    {
        return _ob_delegate_.testWString(ws);
    }

    //
    // IDL:TestCodeSets/deactivate:1.0
    //
    public void
    deactivate()
    {
        _ob_delegate_.deactivate();
    }
}

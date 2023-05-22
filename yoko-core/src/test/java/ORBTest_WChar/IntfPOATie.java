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
package ORBTest_WChar;

//
// IDL:ORBTest_WChar/Intf:1.0
//
public class IntfPOATie extends IntfPOA
{
    private IntfOperations _ob_delegate_;
    private org.omg.PortableServer.POA _ob_poa_;

    public
    IntfPOATie(IntfOperations delegate)
    {
        _ob_delegate_ = delegate;
    }

    public
    IntfPOATie(IntfOperations delegate, org.omg.PortableServer.POA poa)
    {
        _ob_delegate_ = delegate;
        _ob_poa_ = poa;
    }

    public IntfOperations
    _delegate()
    {
        return _ob_delegate_;
    }

    public void
    _delegate(IntfOperations delegate)
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
    // IDL:ORBTest_WChar/Intf/attrWChar:1.0
    //
    public char
    attrWChar()
    {
        return _ob_delegate_.attrWChar();
    }

    public void
    attrWChar(char val)
    {
        _ob_delegate_.attrWChar(val);
    }

    //
    // IDL:ORBTest_WChar/Intf/attrWString:1.0
    //
    public String
    attrWString()
    {
        return _ob_delegate_.attrWString();
    }

    public void
    attrWString(String val)
    {
        _ob_delegate_.attrWString(val);
    }

    //
    // IDL:ORBTest_WChar/Intf/opWChar:1.0
    //
    public char
    opWChar(char a0,
            org.omg.CORBA.CharHolder a1,
            org.omg.CORBA.CharHolder a2)
    {
        return _ob_delegate_.opWChar(a0,
                                     a1,
                                     a2);
    }

    //
    // IDL:ORBTest_WChar/Intf/opWCharEx:1.0
    //
    public char
    opWCharEx(char a0,
              org.omg.CORBA.CharHolder a1,
              org.omg.CORBA.CharHolder a2)
        throws ExWChar
    {
        return _ob_delegate_.opWCharEx(a0,
                                       a1,
                                       a2);
    }

    //
    // IDL:ORBTest_WChar/Intf/opWString:1.0
    //
    public String
    opWString(String a0,
              org.omg.CORBA.StringHolder a1,
              org.omg.CORBA.StringHolder a2)
    {
        return _ob_delegate_.opWString(a0,
                                       a1,
                                       a2);
    }

    //
    // IDL:ORBTest_WChar/Intf/opWStringEx:1.0
    //
    public String
    opWStringEx(String a0,
                org.omg.CORBA.StringHolder a1,
                org.omg.CORBA.StringHolder a2)
        throws ExWString
    {
        return _ob_delegate_.opWStringEx(a0,
                                         a1,
                                         a2);
    }
}

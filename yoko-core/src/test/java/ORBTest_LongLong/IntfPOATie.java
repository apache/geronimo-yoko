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
package ORBTest_LongLong;

//
// IDL:ORBTest_LongLong/Intf:1.0
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
    // IDL:ORBTest_LongLong/Intf/attrLongLong:1.0
    //
    public long
    attrLongLong()
    {
        return _ob_delegate_.attrLongLong();
    }

    public void
    attrLongLong(long val)
    {
        _ob_delegate_.attrLongLong(val);
    }

    //
    // IDL:ORBTest_LongLong/Intf/attrULongLong:1.0
    //
    public long
    attrULongLong()
    {
        return _ob_delegate_.attrULongLong();
    }

    public void
    attrULongLong(long val)
    {
        _ob_delegate_.attrULongLong(val);
    }

    //
    // IDL:ORBTest_LongLong/Intf/opLongLong:1.0
    //
    public long
    opLongLong(long a0,
               org.omg.CORBA.LongHolder a1,
               org.omg.CORBA.LongHolder a2)
    {
        return _ob_delegate_.opLongLong(a0,
                                        a1,
                                        a2);
    }

    //
    // IDL:ORBTest_LongLong/Intf/opLongLongEx:1.0
    //
    public long
    opLongLongEx(long a0,
                 org.omg.CORBA.LongHolder a1,
                 org.omg.CORBA.LongHolder a2)
        throws ExLongLong
    {
        return _ob_delegate_.opLongLongEx(a0,
                                          a1,
                                          a2);
    }

    //
    // IDL:ORBTest_LongLong/Intf/opULongLong:1.0
    //
    public long
    opULongLong(long a0,
                org.omg.CORBA.LongHolder a1,
                org.omg.CORBA.LongHolder a2)
    {
        return _ob_delegate_.opULongLong(a0,
                                         a1,
                                         a2);
    }

    //
    // IDL:ORBTest_LongLong/Intf/opULongLongEx:1.0
    //
    public long
    opULongLongEx(long a0,
                  org.omg.CORBA.LongHolder a1,
                  org.omg.CORBA.LongHolder a2)
        throws ExULongLong
    {
        return _ob_delegate_.opULongLongEx(a0,
                                           a1,
                                           a2);
    }
}

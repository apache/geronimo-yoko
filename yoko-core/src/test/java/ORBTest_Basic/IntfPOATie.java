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
package ORBTest_Basic;

//
// IDL:ORBTest_Basic/Intf:1.0
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
    // IDL:ORBTest_Basic/Intf/attrShort:1.0
    //
    public short
    attrShort()
    {
        return _ob_delegate_.attrShort();
    }

    public void
    attrShort(short val)
    {
        _ob_delegate_.attrShort(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrLong:1.0
    //
    public int
    attrLong()
    {
        return _ob_delegate_.attrLong();
    }

    public void
    attrLong(int val)
    {
        _ob_delegate_.attrLong(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrUShort:1.0
    //
    public short
    attrUShort()
    {
        return _ob_delegate_.attrUShort();
    }

    public void
    attrUShort(short val)
    {
        _ob_delegate_.attrUShort(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrULong:1.0
    //
    public int
    attrULong()
    {
        return _ob_delegate_.attrULong();
    }

    public void
    attrULong(int val)
    {
        _ob_delegate_.attrULong(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrFloat:1.0
    //
    public float
    attrFloat()
    {
        return _ob_delegate_.attrFloat();
    }

    public void
    attrFloat(float val)
    {
        _ob_delegate_.attrFloat(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrDouble:1.0
    //
    public double
    attrDouble()
    {
        return _ob_delegate_.attrDouble();
    }

    public void
    attrDouble(double val)
    {
        _ob_delegate_.attrDouble(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrBoolean:1.0
    //
    public boolean
    attrBoolean()
    {
        return _ob_delegate_.attrBoolean();
    }

    public void
    attrBoolean(boolean val)
    {
        _ob_delegate_.attrBoolean(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrChar:1.0
    //
    public char
    attrChar()
    {
        return _ob_delegate_.attrChar();
    }

    public void
    attrChar(char val)
    {
        _ob_delegate_.attrChar(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrOctet:1.0
    //
    public byte
    attrOctet()
    {
        return _ob_delegate_.attrOctet();
    }

    public void
    attrOctet(byte val)
    {
        _ob_delegate_.attrOctet(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrString:1.0
    //
    public String
    attrString()
    {
        return _ob_delegate_.attrString();
    }

    public void
    attrString(String val)
    {
        _ob_delegate_.attrString(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrAny:1.0
    //
    public org.omg.CORBA.Any
    attrAny()
    {
        return _ob_delegate_.attrAny();
    }

    public void
    attrAny(org.omg.CORBA.Any val)
    {
        _ob_delegate_.attrAny(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrTestEnum:1.0
    //
    public TestEnum
    attrTestEnum()
    {
        return _ob_delegate_.attrTestEnum();
    }

    public void
    attrTestEnum(TestEnum val)
    {
        _ob_delegate_.attrTestEnum(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrIntf:1.0
    //
    public Intf
    attrIntf()
    {
        return _ob_delegate_.attrIntf();
    }

    public void
    attrIntf(Intf val)
    {
        _ob_delegate_.attrIntf(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrFixedStruct:1.0
    //
    public FixedStruct
    attrFixedStruct()
    {
        return _ob_delegate_.attrFixedStruct();
    }

    public void
    attrFixedStruct(FixedStruct val)
    {
        _ob_delegate_.attrFixedStruct(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrVariableStruct:1.0
    //
    public VariableStruct
    attrVariableStruct()
    {
        return _ob_delegate_.attrVariableStruct();
    }

    public void
    attrVariableStruct(VariableStruct val)
    {
        _ob_delegate_.attrVariableStruct(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrFixedUnion:1.0
    //
    public FixedUnion
    attrFixedUnion()
    {
        return _ob_delegate_.attrFixedUnion();
    }

    public void
    attrFixedUnion(FixedUnion val)
    {
        _ob_delegate_.attrFixedUnion(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrVariableUnion:1.0
    //
    public VariableUnion
    attrVariableUnion()
    {
        return _ob_delegate_.attrVariableUnion();
    }

    public void
    attrVariableUnion(VariableUnion val)
    {
        _ob_delegate_.attrVariableUnion(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrStringSequence:1.0
    //
    public String[]
    attrStringSequence()
    {
        return _ob_delegate_.attrStringSequence();
    }

    public void
    attrStringSequence(String[] val)
    {
        _ob_delegate_.attrStringSequence(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrFixedArray:1.0
    //
    public short[][][]
    attrFixedArray()
    {
        return _ob_delegate_.attrFixedArray();
    }

    public void
    attrFixedArray(short[][][] val)
    {
        _ob_delegate_.attrFixedArray(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrVariableArray:1.0
    //
    public String[][]
    attrVariableArray()
    {
        return _ob_delegate_.attrVariableArray();
    }

    public void
    attrVariableArray(String[][] val)
    {
        _ob_delegate_.attrVariableArray(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrFixedArraySequence:1.0
    //
    public short[][][][]
    attrFixedArraySequence()
    {
        return _ob_delegate_.attrFixedArraySequence();
    }

    public void
    attrFixedArraySequence(short[][][][] val)
    {
        _ob_delegate_.attrFixedArraySequence(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrVariableArraySequence:1.0
    //
    public String[][][]
    attrVariableArraySequence()
    {
        return _ob_delegate_.attrVariableArraySequence();
    }

    public void
    attrVariableArraySequence(String[][][] val)
    {
        _ob_delegate_.attrVariableArraySequence(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrFixedArrayBoundSequence:1.0
    //
    public short[][][][]
    attrFixedArrayBoundSequence()
    {
        return _ob_delegate_.attrFixedArrayBoundSequence();
    }

    public void
    attrFixedArrayBoundSequence(short[][][][] val)
    {
        _ob_delegate_.attrFixedArrayBoundSequence(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/attrVariableArrayBoundSequence:1.0
    //
    public String[][][]
    attrVariableArrayBoundSequence()
    {
        return _ob_delegate_.attrVariableArrayBoundSequence();
    }

    public void
    attrVariableArrayBoundSequence(String[][][] val)
    {
        _ob_delegate_.attrVariableArrayBoundSequence(val);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVoid:1.0
    //
    public void
    opVoid()
    {
        _ob_delegate_.opVoid();
    }

    //
    // IDL:ORBTest_Basic/Intf/opVoidEx:1.0
    //
    public void
    opVoidEx()
        throws ExVoid
    {
        _ob_delegate_.opVoidEx();
    }

    //
    // IDL:ORBTest_Basic/Intf/opShort:1.0
    //
    public short
    opShort(short a0,
            org.omg.CORBA.ShortHolder a1,
            org.omg.CORBA.ShortHolder a2)
    {
        return _ob_delegate_.opShort(a0,
                                     a1,
                                     a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opShortEx:1.0
    //
    public short
    opShortEx(short a0,
              org.omg.CORBA.ShortHolder a1,
              org.omg.CORBA.ShortHolder a2)
        throws ExShort
    {
        return _ob_delegate_.opShortEx(a0,
                                       a1,
                                       a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opLong:1.0
    //
    public int
    opLong(int a0,
           org.omg.CORBA.IntHolder a1,
           org.omg.CORBA.IntHolder a2)
    {
        return _ob_delegate_.opLong(a0,
                                    a1,
                                    a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opLongEx:1.0
    //
    public int
    opLongEx(int a0,
             org.omg.CORBA.IntHolder a1,
             org.omg.CORBA.IntHolder a2)
        throws ExLong
    {
        return _ob_delegate_.opLongEx(a0,
                                      a1,
                                      a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opUShort:1.0
    //
    public short
    opUShort(short a0,
             org.omg.CORBA.ShortHolder a1,
             org.omg.CORBA.ShortHolder a2)
    {
        return _ob_delegate_.opUShort(a0,
                                      a1,
                                      a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opUShortEx:1.0
    //
    public short
    opUShortEx(short a0,
               org.omg.CORBA.ShortHolder a1,
               org.omg.CORBA.ShortHolder a2)
        throws ExUShort
    {
        return _ob_delegate_.opUShortEx(a0,
                                        a1,
                                        a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opULong:1.0
    //
    public int
    opULong(int a0,
            org.omg.CORBA.IntHolder a1,
            org.omg.CORBA.IntHolder a2)
    {
        return _ob_delegate_.opULong(a0,
                                     a1,
                                     a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opULongEx:1.0
    //
    public int
    opULongEx(int a0,
              org.omg.CORBA.IntHolder a1,
              org.omg.CORBA.IntHolder a2)
        throws ExULong
    {
        return _ob_delegate_.opULongEx(a0,
                                       a1,
                                       a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFloat:1.0
    //
    public float
    opFloat(float a0,
            org.omg.CORBA.FloatHolder a1,
            org.omg.CORBA.FloatHolder a2)
    {
        return _ob_delegate_.opFloat(a0,
                                     a1,
                                     a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFloatEx:1.0
    //
    public float
    opFloatEx(float a0,
              org.omg.CORBA.FloatHolder a1,
              org.omg.CORBA.FloatHolder a2)
        throws ExFloat
    {
        return _ob_delegate_.opFloatEx(a0,
                                       a1,
                                       a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opDouble:1.0
    //
    public double
    opDouble(double a0,
             org.omg.CORBA.DoubleHolder a1,
             org.omg.CORBA.DoubleHolder a2)
    {
        return _ob_delegate_.opDouble(a0,
                                      a1,
                                      a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opDoubleEx:1.0
    //
    public double
    opDoubleEx(double a0,
               org.omg.CORBA.DoubleHolder a1,
               org.omg.CORBA.DoubleHolder a2)
        throws ExDouble
    {
        return _ob_delegate_.opDoubleEx(a0,
                                        a1,
                                        a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opBoolean:1.0
    //
    public boolean
    opBoolean(boolean a0,
              org.omg.CORBA.BooleanHolder a1,
              org.omg.CORBA.BooleanHolder a2)
    {
        return _ob_delegate_.opBoolean(a0,
                                       a1,
                                       a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opBooleanEx:1.0
    //
    public boolean
    opBooleanEx(boolean a0,
                org.omg.CORBA.BooleanHolder a1,
                org.omg.CORBA.BooleanHolder a2)
        throws ExBoolean
    {
        return _ob_delegate_.opBooleanEx(a0,
                                         a1,
                                         a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opChar:1.0
    //
    public char
    opChar(char a0,
           org.omg.CORBA.CharHolder a1,
           org.omg.CORBA.CharHolder a2)
    {
        return _ob_delegate_.opChar(a0,
                                    a1,
                                    a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opCharEx:1.0
    //
    public char
    opCharEx(char a0,
             org.omg.CORBA.CharHolder a1,
             org.omg.CORBA.CharHolder a2)
        throws ExChar
    {
        return _ob_delegate_.opCharEx(a0,
                                      a1,
                                      a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opOctet:1.0
    //
    public byte
    opOctet(byte a0,
            org.omg.CORBA.ByteHolder a1,
            org.omg.CORBA.ByteHolder a2)
    {
        return _ob_delegate_.opOctet(a0,
                                     a1,
                                     a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opOctetEx:1.0
    //
    public byte
    opOctetEx(byte a0,
              org.omg.CORBA.ByteHolder a1,
              org.omg.CORBA.ByteHolder a2)
        throws ExOctet
    {
        return _ob_delegate_.opOctetEx(a0,
                                       a1,
                                       a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opString:1.0
    //
    public String
    opString(String a0,
             org.omg.CORBA.StringHolder a1,
             org.omg.CORBA.StringHolder a2)
    {
        return _ob_delegate_.opString(a0,
                                      a1,
                                      a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opStringEx:1.0
    //
    public String
    opStringEx(String a0,
               org.omg.CORBA.StringHolder a1,
               org.omg.CORBA.StringHolder a2)
        throws ExString
    {
        return _ob_delegate_.opStringEx(a0,
                                        a1,
                                        a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opAny:1.0
    //
    public org.omg.CORBA.Any
    opAny(org.omg.CORBA.Any a0,
          org.omg.CORBA.AnyHolder a1,
          org.omg.CORBA.AnyHolder a2)
    {
        return _ob_delegate_.opAny(a0,
                                   a1,
                                   a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opAnyEx:1.0
    //
    public org.omg.CORBA.Any
    opAnyEx(org.omg.CORBA.Any a0,
            org.omg.CORBA.AnyHolder a1,
            org.omg.CORBA.AnyHolder a2)
        throws ExAny
    {
        return _ob_delegate_.opAnyEx(a0,
                                     a1,
                                     a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opTestEnum:1.0
    //
    public TestEnum
    opTestEnum(TestEnum a0,
               TestEnumHolder a1,
               TestEnumHolder a2)
    {
        return _ob_delegate_.opTestEnum(a0,
                                        a1,
                                        a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opTestEnumEx:1.0
    //
    public TestEnum
    opTestEnumEx(TestEnum a0,
                 TestEnumHolder a1,
                 TestEnumHolder a2)
        throws ExTestEnum
    {
        return _ob_delegate_.opTestEnumEx(a0,
                                          a1,
                                          a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opIntf:1.0
    //
    public Intf
    opIntf(Intf a0,
           IntfHolder a1,
           IntfHolder a2)
    {
        return _ob_delegate_.opIntf(a0,
                                    a1,
                                    a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opIntfEx:1.0
    //
    public Intf
    opIntfEx(Intf a0,
             IntfHolder a1,
             IntfHolder a2)
        throws ExIntf
    {
        return _ob_delegate_.opIntfEx(a0,
                                      a1,
                                      a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedStruct:1.0
    //
    public FixedStruct
    opFixedStruct(FixedStruct a0,
                  FixedStructHolder a1,
                  FixedStructHolder a2)
    {
        return _ob_delegate_.opFixedStruct(a0,
                                           a1,
                                           a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedStructEx:1.0
    //
    public FixedStruct
    opFixedStructEx(FixedStruct a0,
                    FixedStructHolder a1,
                    FixedStructHolder a2)
        throws ExFixedStruct
    {
        return _ob_delegate_.opFixedStructEx(a0,
                                             a1,
                                             a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableStruct:1.0
    //
    public VariableStruct
    opVariableStruct(VariableStruct a0,
                     VariableStructHolder a1,
                     VariableStructHolder a2)
    {
        return _ob_delegate_.opVariableStruct(a0,
                                              a1,
                                              a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableStructEx:1.0
    //
    public VariableStruct
    opVariableStructEx(VariableStruct a0,
                       VariableStructHolder a1,
                       VariableStructHolder a2)
        throws ExVariableStruct
    {
        return _ob_delegate_.opVariableStructEx(a0,
                                                a1,
                                                a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedUnion:1.0
    //
    public FixedUnion
    opFixedUnion(FixedUnion a0,
                 FixedUnionHolder a1,
                 FixedUnionHolder a2)
    {
        return _ob_delegate_.opFixedUnion(a0,
                                          a1,
                                          a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedUnionEx:1.0
    //
    public FixedUnion
    opFixedUnionEx(FixedUnion a0,
                   FixedUnionHolder a1,
                   FixedUnionHolder a2)
        throws ExFixedUnion
    {
        return _ob_delegate_.opFixedUnionEx(a0,
                                            a1,
                                            a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableUnion:1.0
    //
    public VariableUnion
    opVariableUnion(VariableUnion a0,
                    VariableUnionHolder a1,
                    VariableUnionHolder a2)
    {
        return _ob_delegate_.opVariableUnion(a0,
                                             a1,
                                             a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableUnionEx:1.0
    //
    public VariableUnion
    opVariableUnionEx(VariableUnion a0,
                      VariableUnionHolder a1,
                      VariableUnionHolder a2)
        throws ExVariableUnion
    {
        return _ob_delegate_.opVariableUnionEx(a0,
                                               a1,
                                               a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opStringSequence:1.0
    //
    public String[]
    opStringSequence(String[] a0,
                     StringSequenceHolder a1,
                     StringSequenceHolder a2)
    {
        return _ob_delegate_.opStringSequence(a0,
                                              a1,
                                              a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opStringSequenceEx:1.0
    //
    public String[]
    opStringSequenceEx(String[] a0,
                       StringSequenceHolder a1,
                       StringSequenceHolder a2)
        throws ExStringSequence
    {
        return _ob_delegate_.opStringSequenceEx(a0,
                                                a1,
                                                a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArray:1.0
    //
    public short[][][]
    opFixedArray(short[][][] a0,
                 FixedArrayHolder a1,
                 FixedArrayHolder a2)
    {
        return _ob_delegate_.opFixedArray(a0,
                                          a1,
                                          a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayEx:1.0
    //
    public short[][][]
    opFixedArrayEx(short[][][] a0,
                   FixedArrayHolder a1,
                   FixedArrayHolder a2)
        throws ExFixedArray
    {
        return _ob_delegate_.opFixedArrayEx(a0,
                                            a1,
                                            a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArray:1.0
    //
    public String[][]
    opVariableArray(String[][] a0,
                    VariableArrayHolder a1,
                    VariableArrayHolder a2)
    {
        return _ob_delegate_.opVariableArray(a0,
                                             a1,
                                             a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayEx:1.0
    //
    public String[][]
    opVariableArrayEx(String[][] a0,
                      VariableArrayHolder a1,
                      VariableArrayHolder a2)
        throws ExVariableArray
    {
        return _ob_delegate_.opVariableArrayEx(a0,
                                               a1,
                                               a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArraySequence:1.0
    //
    public short[][][][]
    opFixedArraySequence(short[][][][] a0,
                         FixedArraySequenceHolder a1,
                         FixedArraySequenceHolder a2)
    {
        return _ob_delegate_.opFixedArraySequence(a0,
                                                  a1,
                                                  a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArraySequenceEx:1.0
    //
    public short[][][][]
    opFixedArraySequenceEx(short[][][][] a0,
                           FixedArraySequenceHolder a1,
                           FixedArraySequenceHolder a2)
        throws ExFixedArraySequence
    {
        return _ob_delegate_.opFixedArraySequenceEx(a0,
                                                    a1,
                                                    a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArraySequence:1.0
    //
    public String[][][]
    opVariableArraySequence(String[][][] a0,
                            VariableArraySequenceHolder a1,
                            VariableArraySequenceHolder a2)
    {
        return _ob_delegate_.opVariableArraySequence(a0,
                                                     a1,
                                                     a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArraySequenceEx:1.0
    //
    public String[][][]
    opVariableArraySequenceEx(String[][][] a0,
                              VariableArraySequenceHolder a1,
                              VariableArraySequenceHolder a2)
        throws ExVariableArraySequence
    {
        return _ob_delegate_.opVariableArraySequenceEx(a0,
                                                       a1,
                                                       a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayBoundSequence:1.0
    //
    public short[][][][]
    opFixedArrayBoundSequence(short[][][][] a0,
                              FixedArrayBoundSequenceHolder a1,
                              FixedArrayBoundSequenceHolder a2)
    {
        return _ob_delegate_.opFixedArrayBoundSequence(a0,
                                                       a1,
                                                       a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayBoundSequenceEx:1.0
    //
    public short[][][][]
    opFixedArrayBoundSequenceEx(short[][][][] a0,
                                FixedArrayBoundSequenceHolder a1,
                                FixedArrayBoundSequenceHolder a2)
        throws ExFixedArrayBoundSequence
    {
        return _ob_delegate_.opFixedArrayBoundSequenceEx(a0,
                                                         a1,
                                                         a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayBoundSequence:1.0
    //
    public String[][][]
    opVariableArrayBoundSequence(String[][][] a0,
                                 VariableArrayBoundSequenceHolder a1,
                                 VariableArrayBoundSequenceHolder a2)
    {
        return _ob_delegate_.opVariableArrayBoundSequence(a0,
                                                          a1,
                                                          a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayBoundSequenceEx:1.0
    //
    public String[][][]
    opVariableArrayBoundSequenceEx(String[][][] a0,
                                   VariableArrayBoundSequenceHolder a1,
                                   VariableArrayBoundSequenceHolder a2)
        throws ExVariableArrayBoundSequence
    {
        return _ob_delegate_.opVariableArrayBoundSequenceEx(a0,
                                                            a1,
                                                            a2);
    }

    //
    // IDL:ORBTest_Basic/Intf/opExRecursiveStruct:1.0
    //
    public void
    opExRecursiveStruct()
        throws ExRecursiveStruct
    {
        _ob_delegate_.opExRecursiveStruct();
    }
}

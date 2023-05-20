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
/***/

public interface IntfOperations
{
    //
    // IDL:ORBTest_Basic/Intf/opVoid:1.0
    //
    /***/

    void
    opVoid();

    //
    // IDL:ORBTest_Basic/Intf/opVoidEx:1.0
    //
    /***/

    void
    opVoidEx()
        throws ExVoid;

    //
    // IDL:ORBTest_Basic/Intf/attrShort:1.0
    //
    /***/

    short
    attrShort();

    void
    attrShort(short val);

    //
    // IDL:ORBTest_Basic/Intf/opShort:1.0
    //
    /***/

    short
    opShort(short a0,
            org.omg.CORBA.ShortHolder a1,
            org.omg.CORBA.ShortHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opShortEx:1.0
    //
    /***/

    short
    opShortEx(short a0,
              org.omg.CORBA.ShortHolder a1,
              org.omg.CORBA.ShortHolder a2)
        throws ExShort;

    //
    // IDL:ORBTest_Basic/Intf/attrLong:1.0
    //
    /***/

    int
    attrLong();

    void
    attrLong(int val);

    //
    // IDL:ORBTest_Basic/Intf/opLong:1.0
    //
    /***/

    int
    opLong(int a0,
           org.omg.CORBA.IntHolder a1,
           org.omg.CORBA.IntHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opLongEx:1.0
    //
    /***/

    int
    opLongEx(int a0,
             org.omg.CORBA.IntHolder a1,
             org.omg.CORBA.IntHolder a2)
        throws ExLong;

    //
    // IDL:ORBTest_Basic/Intf/attrUShort:1.0
    //
    /***/

    short
    attrUShort();

    void
    attrUShort(short val);

    //
    // IDL:ORBTest_Basic/Intf/opUShort:1.0
    //
    /***/

    short
    opUShort(short a0,
             org.omg.CORBA.ShortHolder a1,
             org.omg.CORBA.ShortHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opUShortEx:1.0
    //
    /***/

    short
    opUShortEx(short a0,
               org.omg.CORBA.ShortHolder a1,
               org.omg.CORBA.ShortHolder a2)
        throws ExUShort;

    //
    // IDL:ORBTest_Basic/Intf/attrULong:1.0
    //
    /***/

    int
    attrULong();

    void
    attrULong(int val);

    //
    // IDL:ORBTest_Basic/Intf/opULong:1.0
    //
    /***/

    int
    opULong(int a0,
            org.omg.CORBA.IntHolder a1,
            org.omg.CORBA.IntHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opULongEx:1.0
    //
    /***/

    int
    opULongEx(int a0,
              org.omg.CORBA.IntHolder a1,
              org.omg.CORBA.IntHolder a2)
        throws ExULong;

    //
    // IDL:ORBTest_Basic/Intf/attrFloat:1.0
    //
    /***/

    float
    attrFloat();

    void
    attrFloat(float val);

    //
    // IDL:ORBTest_Basic/Intf/opFloat:1.0
    //
    /***/

    float
    opFloat(float a0,
            org.omg.CORBA.FloatHolder a1,
            org.omg.CORBA.FloatHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opFloatEx:1.0
    //
    /***/

    float
    opFloatEx(float a0,
              org.omg.CORBA.FloatHolder a1,
              org.omg.CORBA.FloatHolder a2)
        throws ExFloat;

    //
    // IDL:ORBTest_Basic/Intf/attrDouble:1.0
    //
    /***/

    double
    attrDouble();

    void
    attrDouble(double val);

    //
    // IDL:ORBTest_Basic/Intf/opDouble:1.0
    //
    /***/

    double
    opDouble(double a0,
             org.omg.CORBA.DoubleHolder a1,
             org.omg.CORBA.DoubleHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opDoubleEx:1.0
    //
    /***/

    double
    opDoubleEx(double a0,
               org.omg.CORBA.DoubleHolder a1,
               org.omg.CORBA.DoubleHolder a2)
        throws ExDouble;

    //
    // IDL:ORBTest_Basic/Intf/attrBoolean:1.0
    //
    /***/

    boolean
    attrBoolean();

    void
    attrBoolean(boolean val);

    //
    // IDL:ORBTest_Basic/Intf/opBoolean:1.0
    //
    /***/

    boolean
    opBoolean(boolean a0,
              org.omg.CORBA.BooleanHolder a1,
              org.omg.CORBA.BooleanHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opBooleanEx:1.0
    //
    /***/

    boolean
    opBooleanEx(boolean a0,
                org.omg.CORBA.BooleanHolder a1,
                org.omg.CORBA.BooleanHolder a2)
        throws ExBoolean;

    //
    // IDL:ORBTest_Basic/Intf/attrChar:1.0
    //
    /***/

    char
    attrChar();

    void
    attrChar(char val);

    //
    // IDL:ORBTest_Basic/Intf/opChar:1.0
    //
    /***/

    char
    opChar(char a0,
           org.omg.CORBA.CharHolder a1,
           org.omg.CORBA.CharHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opCharEx:1.0
    //
    /***/

    char
    opCharEx(char a0,
             org.omg.CORBA.CharHolder a1,
             org.omg.CORBA.CharHolder a2)
        throws ExChar;

    //
    // IDL:ORBTest_Basic/Intf/attrOctet:1.0
    //
    /***/

    byte
    attrOctet();

    void
    attrOctet(byte val);

    //
    // IDL:ORBTest_Basic/Intf/opOctet:1.0
    //
    /***/

    byte
    opOctet(byte a0,
            org.omg.CORBA.ByteHolder a1,
            org.omg.CORBA.ByteHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opOctetEx:1.0
    //
    /***/

    byte
    opOctetEx(byte a0,
              org.omg.CORBA.ByteHolder a1,
              org.omg.CORBA.ByteHolder a2)
        throws ExOctet;

    //
    // IDL:ORBTest_Basic/Intf/attrString:1.0
    //
    /***/

    String
    attrString();

    void
    attrString(String val);

    //
    // IDL:ORBTest_Basic/Intf/opString:1.0
    //
    /***/

    String
    opString(String a0,
             org.omg.CORBA.StringHolder a1,
             org.omg.CORBA.StringHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opStringEx:1.0
    //
    /***/

    String
    opStringEx(String a0,
               org.omg.CORBA.StringHolder a1,
               org.omg.CORBA.StringHolder a2)
        throws ExString;

    //
    // IDL:ORBTest_Basic/Intf/attrAny:1.0
    //
    /***/

    org.omg.CORBA.Any
    attrAny();

    void
    attrAny(org.omg.CORBA.Any val);

    //
    // IDL:ORBTest_Basic/Intf/opAny:1.0
    //
    /***/

    org.omg.CORBA.Any
    opAny(org.omg.CORBA.Any a0,
          org.omg.CORBA.AnyHolder a1,
          org.omg.CORBA.AnyHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opAnyEx:1.0
    //
    /***/

    org.omg.CORBA.Any
    opAnyEx(org.omg.CORBA.Any a0,
            org.omg.CORBA.AnyHolder a1,
            org.omg.CORBA.AnyHolder a2)
        throws ExAny;

    //
    // IDL:ORBTest_Basic/Intf/attrTestEnum:1.0
    //
    /***/

    TestEnum
    attrTestEnum();

    void
    attrTestEnum(TestEnum val);

    //
    // IDL:ORBTest_Basic/Intf/opTestEnum:1.0
    //
    /***/

    TestEnum
    opTestEnum(TestEnum a0,
               TestEnumHolder a1,
               TestEnumHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opTestEnumEx:1.0
    //
    /***/

    TestEnum
    opTestEnumEx(TestEnum a0,
                 TestEnumHolder a1,
                 TestEnumHolder a2)
        throws ExTestEnum;

    //
    // IDL:ORBTest_Basic/Intf/attrIntf:1.0
    //
    /***/

    Intf
    attrIntf();

    void
    attrIntf(Intf val);

    //
    // IDL:ORBTest_Basic/Intf/opIntf:1.0
    //
    /***/

    Intf
    opIntf(Intf a0,
           IntfHolder a1,
           IntfHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opIntfEx:1.0
    //
    /***/

    Intf
    opIntfEx(Intf a0,
             IntfHolder a1,
             IntfHolder a2)
        throws ExIntf;

    //
    // IDL:ORBTest_Basic/Intf/attrFixedStruct:1.0
    //
    /***/

    FixedStruct
    attrFixedStruct();

    void
    attrFixedStruct(FixedStruct val);

    //
    // IDL:ORBTest_Basic/Intf/opFixedStruct:1.0
    //
    /***/

    FixedStruct
    opFixedStruct(FixedStruct a0,
                  FixedStructHolder a1,
                  FixedStructHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opFixedStructEx:1.0
    //
    /***/

    FixedStruct
    opFixedStructEx(FixedStruct a0,
                    FixedStructHolder a1,
                    FixedStructHolder a2)
        throws ExFixedStruct;

    //
    // IDL:ORBTest_Basic/Intf/attrVariableStruct:1.0
    //
    /***/

    VariableStruct
    attrVariableStruct();

    void
    attrVariableStruct(VariableStruct val);

    //
    // IDL:ORBTest_Basic/Intf/opVariableStruct:1.0
    //
    /***/

    VariableStruct
    opVariableStruct(VariableStruct a0,
                     VariableStructHolder a1,
                     VariableStructHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opVariableStructEx:1.0
    //
    /***/

    VariableStruct
    opVariableStructEx(VariableStruct a0,
                       VariableStructHolder a1,
                       VariableStructHolder a2)
        throws ExVariableStruct;

    //
    // IDL:ORBTest_Basic/Intf/attrFixedUnion:1.0
    //
    /***/

    FixedUnion
    attrFixedUnion();

    void
    attrFixedUnion(FixedUnion val);

    //
    // IDL:ORBTest_Basic/Intf/opFixedUnion:1.0
    //
    /***/

    FixedUnion
    opFixedUnion(FixedUnion a0,
                 FixedUnionHolder a1,
                 FixedUnionHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opFixedUnionEx:1.0
    //
    /***/

    FixedUnion
    opFixedUnionEx(FixedUnion a0,
                   FixedUnionHolder a1,
                   FixedUnionHolder a2)
        throws ExFixedUnion;

    //
    // IDL:ORBTest_Basic/Intf/attrVariableUnion:1.0
    //
    /***/

    VariableUnion
    attrVariableUnion();

    void
    attrVariableUnion(VariableUnion val);

    //
    // IDL:ORBTest_Basic/Intf/opVariableUnion:1.0
    //
    /***/

    VariableUnion
    opVariableUnion(VariableUnion a0,
                    VariableUnionHolder a1,
                    VariableUnionHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opVariableUnionEx:1.0
    //
    /***/

    VariableUnion
    opVariableUnionEx(VariableUnion a0,
                      VariableUnionHolder a1,
                      VariableUnionHolder a2)
        throws ExVariableUnion;

    //
    // IDL:ORBTest_Basic/Intf/attrStringSequence:1.0
    //
    /***/

    String[]
    attrStringSequence();

    void
    attrStringSequence(String[] val);

    //
    // IDL:ORBTest_Basic/Intf/opStringSequence:1.0
    //
    /***/

    String[]
    opStringSequence(String[] a0,
                     StringSequenceHolder a1,
                     StringSequenceHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opStringSequenceEx:1.0
    //
    /***/

    String[]
    opStringSequenceEx(String[] a0,
                       StringSequenceHolder a1,
                       StringSequenceHolder a2)
        throws ExStringSequence;

    //
    // IDL:ORBTest_Basic/Intf/attrFixedArray:1.0
    //
    /***/

    short[][][]
    attrFixedArray();

    void
    attrFixedArray(short[][][] val);

    //
    // IDL:ORBTest_Basic/Intf/opFixedArray:1.0
    //
    /***/

    short[][][]
    opFixedArray(short[][][] a0,
                 FixedArrayHolder a1,
                 FixedArrayHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayEx:1.0
    //
    /***/

    short[][][]
    opFixedArrayEx(short[][][] a0,
                   FixedArrayHolder a1,
                   FixedArrayHolder a2)
        throws ExFixedArray;

    //
    // IDL:ORBTest_Basic/Intf/attrVariableArray:1.0
    //
    /***/

    String[][]
    attrVariableArray();

    void
    attrVariableArray(String[][] val);

    //
    // IDL:ORBTest_Basic/Intf/opVariableArray:1.0
    //
    /***/

    String[][]
    opVariableArray(String[][] a0,
                    VariableArrayHolder a1,
                    VariableArrayHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayEx:1.0
    //
    /***/

    String[][]
    opVariableArrayEx(String[][] a0,
                      VariableArrayHolder a1,
                      VariableArrayHolder a2)
        throws ExVariableArray;

    //
    // IDL:ORBTest_Basic/Intf/attrFixedArraySequence:1.0
    //
    /***/

    short[][][][]
    attrFixedArraySequence();

    void
    attrFixedArraySequence(short[][][][] val);

    //
    // IDL:ORBTest_Basic/Intf/opFixedArraySequence:1.0
    //
    /***/

    short[][][][]
    opFixedArraySequence(short[][][][] a0,
                         FixedArraySequenceHolder a1,
                         FixedArraySequenceHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opFixedArraySequenceEx:1.0
    //
    /***/

    short[][][][]
    opFixedArraySequenceEx(short[][][][] a0,
                           FixedArraySequenceHolder a1,
                           FixedArraySequenceHolder a2)
        throws ExFixedArraySequence;

    //
    // IDL:ORBTest_Basic/Intf/attrVariableArraySequence:1.0
    //
    /***/

    String[][][]
    attrVariableArraySequence();

    void
    attrVariableArraySequence(String[][][] val);

    //
    // IDL:ORBTest_Basic/Intf/opVariableArraySequence:1.0
    //
    /***/

    String[][][]
    opVariableArraySequence(String[][][] a0,
                            VariableArraySequenceHolder a1,
                            VariableArraySequenceHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opVariableArraySequenceEx:1.0
    //
    /***/

    String[][][]
    opVariableArraySequenceEx(String[][][] a0,
                              VariableArraySequenceHolder a1,
                              VariableArraySequenceHolder a2)
        throws ExVariableArraySequence;

    //
    // IDL:ORBTest_Basic/Intf/attrFixedArrayBoundSequence:1.0
    //
    /***/

    short[][][][]
    attrFixedArrayBoundSequence();

    void
    attrFixedArrayBoundSequence(short[][][][] val);

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayBoundSequence:1.0
    //
    /***/

    short[][][][]
    opFixedArrayBoundSequence(short[][][][] a0,
                              FixedArrayBoundSequenceHolder a1,
                              FixedArrayBoundSequenceHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opFixedArrayBoundSequenceEx:1.0
    //
    /***/

    short[][][][]
    opFixedArrayBoundSequenceEx(short[][][][] a0,
                                FixedArrayBoundSequenceHolder a1,
                                FixedArrayBoundSequenceHolder a2)
        throws ExFixedArrayBoundSequence;

    //
    // IDL:ORBTest_Basic/Intf/attrVariableArrayBoundSequence:1.0
    //
    /***/

    String[][][]
    attrVariableArrayBoundSequence();

    void
    attrVariableArrayBoundSequence(String[][][] val);

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayBoundSequence:1.0
    //
    /***/

    String[][][]
    opVariableArrayBoundSequence(String[][][] a0,
                                 VariableArrayBoundSequenceHolder a1,
                                 VariableArrayBoundSequenceHolder a2);

    //
    // IDL:ORBTest_Basic/Intf/opVariableArrayBoundSequenceEx:1.0
    //
    /***/

    String[][][]
    opVariableArrayBoundSequenceEx(String[][][] a0,
                                   VariableArrayBoundSequenceHolder a1,
                                   VariableArrayBoundSequenceHolder a2)
        throws ExVariableArrayBoundSequence;

    //
    // IDL:ORBTest_Basic/Intf/opExRecursiveStruct:1.0
    //
    /***/

    void
    opExRecursiveStruct()
        throws ExRecursiveStruct;
}

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
package ORBTest;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import ORBTest_Basic.*;

final class TestIntfBasic_impl extends ORBTest_Basic.IntfPOA {
    private POA m_poa;

    private short m_aShort;

    private short m_aUShort;

    private int m_aLong;

    private int m_aULong;

    private float m_aFloat;

    private double m_aDouble;

    private boolean m_aBoolean;

    private char m_aChar;

    private byte m_aOctet;

    private String m_aString;

    private Any m_aAny;

    private ORBTest_Basic.TestEnum m_aTestEnum;

    private ORBTest_Basic.Intf m_aTestIntfBasic;

    private FixedStruct m_aFixedStruct;

    private VariableStruct m_aVariableStruct;

    private FixedUnion m_aFixedUnion;

    private VariableUnion m_aVariableUnion;

    private String[] m_aStringSequence;

    private short[][][] m_aFixedArray;

    private String[][] m_aVariableArray;

    private short[][][][] m_aFixedArraySequence;

    private String[][][] m_aVariableArraySequence;

    private short[][][][] m_aFixedArrayBoundSequence;

    private String[][][] m_aVariableArrayBoundSequence;

    private ORBTest_Basic.RecursiveStruct m_aRecursiveStruct;

    public TestIntfBasic_impl(POA poa) {
        m_poa = poa;
    }

    public synchronized void opVoid() {
    }

    public synchronized void opVoidEx() throws ExVoid {
        throw new ExVoid();
    }

    public synchronized short attrShort() {
        return m_aShort;
    }

    public synchronized void attrShort(short value) {
        m_aShort = value;
    }

    public synchronized short opShort(short a0, ShortHolder a1, ShortHolder a2) {
        m_aShort = (short) (a0 + a1.value);
        a1.value = a2.value = m_aShort;
        return m_aShort;
    }

    public synchronized short opShortEx(short a0, ShortHolder a1, ShortHolder a2)
            throws ExShort {
        m_aShort = (short) (a0 + a1.value);
        throw new ExShort(m_aShort);
    }

    public synchronized int attrLong() {
        return m_aLong;
    }

    public synchronized void attrLong(int value) {
        m_aLong = value;
    }

    public synchronized int opLong(int a0, IntHolder a1, IntHolder a2) {
        m_aLong = a0 + a1.value;
        a1.value = a2.value = m_aLong;
        return m_aLong;
    }

    public synchronized int opLongEx(int a0, IntHolder a1, IntHolder a2)
            throws ExLong {
        m_aLong = a0 + a1.value;
        throw new ExLong(m_aLong);
    }

    public synchronized short attrUShort() {
        return m_aUShort;
    }

    public synchronized void attrUShort(short value) {
        m_aUShort = value;
    }

    public synchronized short opUShort(short a0, ShortHolder a1, ShortHolder a2) {
        m_aUShort = (short) (a0 + a1.value);
        a1.value = a2.value = m_aUShort;
        return m_aUShort;
    }

    public synchronized short opUShortEx(short a0, ShortHolder a1,
            ShortHolder a2) throws ExUShort {
        m_aUShort = (short) (a0 + a1.value);
        throw new ExUShort(m_aUShort);
    }

    public synchronized int attrULong() {
        return m_aULong;
    }

    public synchronized void attrULong(int value) {
        m_aULong = value;
    }

    public synchronized int opULong(int a0, IntHolder a1, IntHolder a2) {
        m_aULong = a0 + a1.value;
        a1.value = a2.value = m_aULong;
        return m_aULong;
    }

    public synchronized int opULongEx(int a0, IntHolder a1, IntHolder a2)
            throws ExULong {
        m_aULong = a0 + a1.value;
        throw new ExULong(m_aULong);
    }

    public synchronized float attrFloat() {
        return m_aFloat;
    }

    public synchronized void attrFloat(float value) {
        m_aFloat = value;
    }

    public synchronized float opFloat(float a0, FloatHolder a1, FloatHolder a2) {
        m_aFloat = a0 + a1.value;
        a1.value = a2.value = m_aFloat;
        return m_aFloat;
    }

    public synchronized float opFloatEx(float a0, FloatHolder a1, FloatHolder a2)
            throws ExFloat {
        m_aFloat = a0 + a1.value;
        throw new ExFloat(m_aFloat);
    }

    public synchronized double attrDouble() {
        return m_aDouble;
    }

    public synchronized void attrDouble(double value) {
        m_aDouble = value;
    }

    public synchronized double opDouble(double a0, DoubleHolder a1,
            DoubleHolder a2) {
        m_aDouble = a0 + a1.value;
        a1.value = a2.value = m_aDouble;
        return m_aDouble;
    }

    public synchronized double opDoubleEx(double a0, DoubleHolder a1,
            DoubleHolder a2) throws ExDouble {
        m_aDouble = a0 + a1.value;
        throw new ExDouble(m_aDouble);
    }

    public synchronized boolean attrBoolean() {
        return m_aBoolean;
    }

    public synchronized void attrBoolean(boolean value) {
        m_aBoolean = value;
    }

    public synchronized boolean opBoolean(boolean a0, BooleanHolder a1,
            BooleanHolder a2) {
        m_aBoolean = a0 && a1.value;
        a1.value = a2.value = m_aBoolean;
        return m_aBoolean;
    }

    public synchronized boolean opBooleanEx(boolean a0, BooleanHolder a1,
            BooleanHolder a2) throws ExBoolean {
        m_aBoolean = a0 && a1.value;
        throw new ExBoolean(m_aBoolean);
    }

    public synchronized char attrChar() {
        return m_aChar;
    }

    public synchronized void attrChar(char value) {
        m_aChar = value;
    }

    public synchronized char opChar(char a0, CharHolder a1, CharHolder a2) {
        m_aChar = (char) (a0 + a1.value);
        a1.value = a2.value = m_aChar;
        return m_aChar;
    }

    public synchronized char opCharEx(char a0, CharHolder a1, CharHolder a2)
            throws ExChar {
        m_aChar = (char) (a0 + a1.value);
        throw new ExChar(m_aChar);
    }

    public synchronized byte attrOctet() {
        return m_aOctet;
    }

    public synchronized void attrOctet(byte value) {
        m_aOctet = value;
    }

    public synchronized byte opOctet(byte a0, ByteHolder a1, ByteHolder a2) {
        m_aOctet = (byte) (a0 + a1.value);
        a1.value = a2.value = m_aOctet;
        return m_aOctet;
    }

    public synchronized byte opOctetEx(byte a0, ByteHolder a1, ByteHolder a2)
            throws ExOctet {
        m_aOctet = (byte) (a0 + a1.value);
        throw new ExOctet(m_aOctet);
    }

    public synchronized String attrString() {
        return m_aString;
    }

    public synchronized void attrString(String value) {
        m_aString = value;
    }

    public synchronized String opString(String a0, StringHolder a1,
            StringHolder a2) {
        m_aString = a0 + a1.value;
        a1.value = a2.value = m_aString;
        return m_aString;
    }

    public synchronized String opStringEx(String a0, StringHolder a1,
            StringHolder a2) throws ExString {
        m_aString = a0 + a1.value;
        throw new ExString(m_aString);
    }

    public synchronized Any attrAny() {
        return m_aAny;
    }

    public synchronized void attrAny(Any value) {
        m_aAny = value;
    }

    public synchronized Any opAny(Any a0, AnyHolder a1, AnyHolder a2) {
        m_aAny = a0;
        a1.value = a2.value = m_aAny;
        return m_aAny;
    }

    public synchronized Any opAnyEx(Any a0, AnyHolder a1, AnyHolder a2)
            throws ExAny {
        m_aAny = a0;
        throw new ExAny(m_aAny);
    }

    public synchronized ORBTest_Basic.TestEnum attrTestEnum() {
        return m_aTestEnum;
    }

    public synchronized void attrTestEnum(ORBTest_Basic.TestEnum value) {
        m_aTestEnum = value;
    }

    public synchronized ORBTest_Basic.TestEnum opTestEnum(
            ORBTest_Basic.TestEnum a0, ORBTest_Basic.TestEnumHolder a1,
            ORBTest_Basic.TestEnumHolder a2) {
        m_aTestEnum = a0;
        a1.value = a2.value = m_aTestEnum;
        return m_aTestEnum;
    }

    public synchronized ORBTest_Basic.TestEnum opTestEnumEx(
            ORBTest_Basic.TestEnum a0, ORBTest_Basic.TestEnumHolder a1,
            ORBTest_Basic.TestEnumHolder a2) throws ExTestEnum {
        m_aTestEnum = a0;
        throw new ExTestEnum(m_aTestEnum);
    }

    public synchronized ORBTest_Basic.Intf attrIntf() {
        return m_aTestIntfBasic;
    }

    public synchronized void attrIntf(ORBTest_Basic.Intf value) {
        m_aTestIntfBasic = value;
    }

    public synchronized ORBTest_Basic.Intf opIntf(ORBTest_Basic.Intf a0,
            ORBTest_Basic.IntfHolder a1, ORBTest_Basic.IntfHolder a2) {
        m_aTestIntfBasic = a0;
        a1.value = a2.value = m_aTestIntfBasic;
        return m_aTestIntfBasic;
    }

    public synchronized ORBTest_Basic.Intf opIntfEx(ORBTest_Basic.Intf a0,
            ORBTest_Basic.IntfHolder a1, ORBTest_Basic.IntfHolder a2)
            throws ORBTest_Basic.ExIntf {
        m_aTestIntfBasic = a0;
        throw new ORBTest_Basic.ExIntf(m_aTestIntfBasic);
    }

    public synchronized FixedStruct attrFixedStruct() {
        return m_aFixedStruct;
    }

    public synchronized void attrFixedStruct(FixedStruct value) {
        m_aFixedStruct = value;
    }

    public synchronized FixedStruct opFixedStruct(FixedStruct a0,
            FixedStructHolder a1, FixedStructHolder a2) {
        m_aFixedStruct = a0;
        a1.value = a2.value = m_aFixedStruct;
        return m_aFixedStruct;
    }

    public synchronized FixedStruct opFixedStructEx(FixedStruct a0,
            FixedStructHolder a1, FixedStructHolder a2) throws ExFixedStruct {
        m_aFixedStruct = a0;
        throw new ExFixedStruct(m_aFixedStruct);
    }

    public synchronized VariableStruct attrVariableStruct() {
        return m_aVariableStruct;
    }

    public synchronized void attrVariableStruct(VariableStruct value) {
        m_aVariableStruct = value;
    }

    public synchronized VariableStruct opVariableStruct(VariableStruct a0,
            VariableStructHolder a1, VariableStructHolder a2) {
        m_aVariableStruct = a0;
        a1.value = a2.value = m_aVariableStruct;
        return m_aVariableStruct;
    }

    public synchronized VariableStruct opVariableStructEx(VariableStruct a0,
            VariableStructHolder a1, VariableStructHolder a2)
            throws ExVariableStruct {
        m_aVariableStruct = a0;
        throw new ExVariableStruct(m_aVariableStruct);
    }

    public synchronized FixedUnion attrFixedUnion() {
        return m_aFixedUnion;
    }

    public synchronized void attrFixedUnion(FixedUnion value) {
        m_aFixedUnion = value;
    }

    public synchronized FixedUnion opFixedUnion(FixedUnion a0,
            FixedUnionHolder a1, FixedUnionHolder a2) {
        m_aFixedUnion = a0;
        a1.value = a2.value = m_aFixedUnion;
        return m_aFixedUnion;
    }

    public synchronized FixedUnion opFixedUnionEx(FixedUnion a0,
            FixedUnionHolder a1, FixedUnionHolder a2) throws ExFixedUnion {
        m_aFixedUnion = a0;
        throw new ExFixedUnion(m_aFixedUnion);
    }

    public synchronized VariableUnion attrVariableUnion() {
        return m_aVariableUnion;
    }

    public synchronized void attrVariableUnion(VariableUnion value) {
        m_aVariableUnion = value;
    }

    public synchronized VariableUnion opVariableUnion(VariableUnion a0,
            VariableUnionHolder a1, VariableUnionHolder a2) {
        m_aVariableUnion = a0;
        a1.value = a2.value = m_aVariableUnion;
        return m_aVariableUnion;
    }

    public synchronized VariableUnion opVariableUnionEx(VariableUnion a0,
            VariableUnionHolder a1, VariableUnionHolder a2)
            throws ExVariableUnion {
        m_aVariableUnion = a0;
        throw new ExVariableUnion(m_aVariableUnion);
    }

    public synchronized String[] attrStringSequence() {
        return m_aStringSequence;
    }

    public synchronized void attrStringSequence(String[] value) {
        m_aStringSequence = value;
    }

    public synchronized String[] opStringSequence(String[] a0,
            StringSequenceHolder a1, StringSequenceHolder a2) {
        m_aStringSequence = new String[a0.length + a1.value.length];
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aStringSequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aStringSequence[idx++] = a1.value[i];
        }
        a1.value = a2.value = m_aStringSequence;
        return m_aStringSequence;
    }

    public synchronized String[] opStringSequenceEx(String[] a0,
            StringSequenceHolder a1, StringSequenceHolder a2)
            throws ExStringSequence {
        m_aStringSequence = new String[a0.length + a1.value.length];
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aStringSequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aStringSequence[idx++] = a1.value[i];
        }
        throw new ExStringSequence(m_aStringSequence);
    }

    public synchronized short[][][] attrFixedArray() {
        return m_aFixedArray;
    }

    public synchronized void attrFixedArray(short[][][] value) {
        m_aFixedArray = value;
    }

    public synchronized short[][][] opFixedArray(short[][][] a0,
            FixedArrayHolder a1, FixedArrayHolder a2) {
        m_aFixedArray = a0;
        a1.value = a2.value = m_aFixedArray;
        return m_aFixedArray;
    }

    public synchronized short[][][] opFixedArrayEx(short[][][] a0,
            FixedArrayHolder a1, FixedArrayHolder a2) throws ExFixedArray {
        m_aFixedArray = a0;
        throw new ExFixedArray(m_aFixedArray);
    }

    public synchronized String[][] attrVariableArray() {
        return m_aVariableArray;
    }

    public synchronized void attrVariableArray(String[][] value) {
        m_aVariableArray = value;
    }

    public synchronized String[][] opVariableArray(String[][] a0,
            VariableArrayHolder a1, VariableArrayHolder a2) {
        m_aVariableArray = a0;
        a1.value = a2.value = m_aVariableArray;
        return m_aVariableArray;
    }

    public synchronized String[][] opVariableArrayEx(String[][] a0,
            VariableArrayHolder a1, VariableArrayHolder a2)
            throws ExVariableArray {
        m_aVariableArray = a0;
        throw new ExVariableArray(m_aVariableArray);
    }

    public synchronized short[][][][] attrFixedArraySequence() {
        return m_aFixedArraySequence;
    }

    public synchronized void attrFixedArraySequence(short[][][][] value) {
        m_aFixedArraySequence = value;
    }

    public synchronized short[][][][] opFixedArraySequence(short[][][][] a0,
            FixedArraySequenceHolder a1, FixedArraySequenceHolder a2) {
        m_aFixedArraySequence = new short[a0.length + a1.value.length][][][];
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aFixedArraySequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aFixedArraySequence[idx++] = a1.value[i];
        }
        a1.value = a2.value = m_aFixedArraySequence;
        return m_aFixedArraySequence;
    }

    public synchronized short[][][][] opFixedArraySequenceEx(short[][][][] a0,
            FixedArraySequenceHolder a1, FixedArraySequenceHolder a2)
            throws ExFixedArraySequence {
        m_aFixedArraySequence = new short[a0.length + a1.value.length][][][];
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aFixedArraySequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aFixedArraySequence[idx++] = a1.value[i];
        }
        throw new ExFixedArraySequence(m_aFixedArraySequence);
    }

    public synchronized String[][][] attrVariableArraySequence() {
        return m_aVariableArraySequence;
    }

    public synchronized void attrVariableArraySequence(String[][][] value) {
        m_aVariableArraySequence = value;
    }

    public synchronized String[][][] opVariableArraySequence(String[][][] a0,
            VariableArraySequenceHolder a1, VariableArraySequenceHolder a2) {
        m_aVariableArraySequence = new String[a0.length + a1.value.length][][];

        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aVariableArraySequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aVariableArraySequence[idx++] = a1.value[i];
        }
        a1.value = a2.value = m_aVariableArraySequence;
        return m_aVariableArraySequence;
    }

    public synchronized String[][][] opVariableArraySequenceEx(String[][][] a0,
            VariableArraySequenceHolder a1, VariableArraySequenceHolder a2)
            throws ExVariableArraySequence {
        m_aVariableArraySequence = new String[a0.length + a1.value.length][][];
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aVariableArraySequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aVariableArraySequence[idx++] = a1.value[i];
        }
        throw new ExVariableArraySequence(m_aVariableArraySequence);
    }

    public synchronized short[][][][] attrFixedArrayBoundSequence() {
        return m_aFixedArrayBoundSequence;
    }

    public synchronized void attrFixedArrayBoundSequence(short[][][][] value) {
        m_aFixedArrayBoundSequence = value;
    }

    public synchronized short[][][][] opFixedArrayBoundSequence(
            short[][][][] a0, FixedArrayBoundSequenceHolder a1,
            FixedArrayBoundSequenceHolder a2) {
        m_aFixedArrayBoundSequence = (new short[a0.length + a1.value.length][][][]);
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aFixedArrayBoundSequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aFixedArrayBoundSequence[idx++] = a1.value[i];
        }
        a1.value = a2.value = m_aFixedArrayBoundSequence;
        return m_aFixedArrayBoundSequence;
    }

    public synchronized short[][][][] opFixedArrayBoundSequenceEx(
            short[][][][] a0, FixedArrayBoundSequenceHolder a1,
            FixedArrayBoundSequenceHolder a2) throws ExFixedArrayBoundSequence {
        m_aFixedArrayBoundSequence = (new short[a0.length + a1.value.length][][][]);
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aFixedArrayBoundSequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aFixedArrayBoundSequence[idx++] = a1.value[i];
        }
        throw new ExFixedArrayBoundSequence(m_aFixedArrayBoundSequence);
    }

    public synchronized String[][][] attrVariableArrayBoundSequence() {
        return m_aVariableArrayBoundSequence;
    }

    public synchronized void attrVariableArrayBoundSequence(String[][][] value) {
        m_aVariableArrayBoundSequence = value;
    }

    public synchronized String[][][] opVariableArrayBoundSequence(
            String[][][] a0, VariableArrayBoundSequenceHolder a1,
            VariableArrayBoundSequenceHolder a2) {
        m_aVariableArrayBoundSequence = (new String[a0.length + a1.value.length][][]);
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aVariableArrayBoundSequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aVariableArrayBoundSequence[idx++] = a1.value[i];
        }
        a1.value = a2.value = m_aVariableArrayBoundSequence;
        return m_aVariableArrayBoundSequence;
    }

    public synchronized String[][][] opVariableArrayBoundSequenceEx(
            String[][][] a0, VariableArrayBoundSequenceHolder a1,
            VariableArrayBoundSequenceHolder a2)
            throws ExVariableArrayBoundSequence {
        m_aVariableArrayBoundSequence = (new String[a0.length + a1.value.length][][]);
        int idx = 0;
        for (int i = 0; i < a0.length; i++) {
            m_aVariableArrayBoundSequence[idx++] = a0[i];
        }
        for (int i = 0; i < a1.value.length; i++) {
            m_aVariableArrayBoundSequence[idx++] = a1.value[i];
        }
        throw new ExVariableArrayBoundSequence(m_aVariableArraySequence);
    }

    public synchronized void opExRecursiveStruct()
            throws ORBTest_Basic.ExRecursiveStruct {
        m_aRecursiveStruct = new ORBTest_Basic.RecursiveStruct();

        m_aRecursiveStruct.s = "test";
        m_aRecursiveStruct.i = 2;

        m_aRecursiveStruct.rs = new ORBTest_Basic.RecursiveStruct[1];
        m_aRecursiveStruct.rs[0] = new ORBTest_Basic.RecursiveStruct();
        m_aRecursiveStruct.rs[0].s = "ORBTest_Basic_RecursiveStruct";
        m_aRecursiveStruct.rs[0].i = 111;
        m_aRecursiveStruct.rs[0].rs = new ORBTest_Basic.RecursiveStruct[0];

        throw new ORBTest_Basic.ExRecursiveStruct((short) 1, m_aRecursiveStruct);
    }

    public org.omg.PortableServer.POA _default_POA() {
        return m_poa;
    }
}

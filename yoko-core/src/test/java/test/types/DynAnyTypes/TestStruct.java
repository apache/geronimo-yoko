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
package test.types.DynAnyTypes;

//
// IDL:test/types/DynAnyTypes/TestStruct:1.0
//
/***/

final public class TestStruct implements org.omg.CORBA.portable.IDLEntity
{
    private static final String _ob_id = "IDL:test/types/DynAnyTypes/TestStruct:1.0";

    public
    TestStruct()
    {
    }

    public
    TestStruct(short shortVal,
               short ushortVal,
               int longVal,
               int ulongVal,
               float floatVal,
               double doubleVal,
               boolean boolVal,
               char charVal,
               byte octetVal,
               org.omg.CORBA.Any anyVal,
               org.omg.CORBA.TypeCode tcVal,
               org.omg.CORBA.Object objectVal,
               String stringVal,
               long longlongVal,
               long ulonglongVal,
               char wcharVal,
               String wstringVal)
    {
        this.shortVal = shortVal;
        this.ushortVal = ushortVal;
        this.longVal = longVal;
        this.ulongVal = ulongVal;
        this.floatVal = floatVal;
        this.doubleVal = doubleVal;
        this.boolVal = boolVal;
        this.charVal = charVal;
        this.octetVal = octetVal;
        this.anyVal = anyVal;
        this.tcVal = tcVal;
        this.objectVal = objectVal;
        this.stringVal = stringVal;
        this.longlongVal = longlongVal;
        this.ulonglongVal = ulonglongVal;
        this.wcharVal = wcharVal;
        this.wstringVal = wstringVal;
    }

    public short shortVal;
    public short ushortVal;
    public int longVal;
    public int ulongVal;
    public float floatVal;
    public double doubleVal;
    public boolean boolVal;
    public char charVal;
    public byte octetVal;
    public org.omg.CORBA.Any anyVal;
    public org.omg.CORBA.TypeCode tcVal;
    public org.omg.CORBA.Object objectVal;
    public String stringVal;
    public long longlongVal;
    public long ulonglongVal;
    public char wcharVal;
    public String wstringVal;
}

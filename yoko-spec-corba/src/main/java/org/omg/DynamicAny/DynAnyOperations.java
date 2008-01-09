/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.omg.DynamicAny;

//
// IDL:omg.org/DynamicAny/DynAny:1.0
//
/***/

public interface DynAnyOperations
{
    //
    // IDL:omg.org/DynamicAny/DynAny/type:1.0
    //
    /***/

    org.omg.CORBA.TypeCode
    type();

    //
    // IDL:omg.org/DynamicAny/DynAny/assign:1.0
    //
    /***/

    void
    assign(DynAny dyn_any)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

    //
    // IDL:omg.org/DynamicAny/DynAny/from_any:1.0
    //
    /***/

    void
    from_any(org.omg.CORBA.Any value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/to_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    to_any();

    //
    // IDL:omg.org/DynamicAny/DynAny/equal:1.0
    //
    /***/

    boolean
    equal(DynAny dyn_any);

    //
    // IDL:omg.org/DynamicAny/DynAny/destroy:1.0
    //
    /***/

    void
    destroy();

    //
    // IDL:omg.org/DynamicAny/DynAny/copy:1.0
    //
    /***/

    DynAny
    copy();

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_boolean:1.0
    //
    /***/

    void
    insert_boolean(boolean value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_octet:1.0
    //
    /***/

    void
    insert_octet(byte value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_char:1.0
    //
    /***/

    void
    insert_char(char value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_short:1.0
    //
    /***/

    void
    insert_short(short value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_ushort:1.0
    //
    /***/

    void
    insert_ushort(short value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_long:1.0
    //
    /***/

    void
    insert_long(int value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_ulong:1.0
    //
    /***/

    void
    insert_ulong(int value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_float:1.0
    //
    /***/

    void
    insert_float(float value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_double:1.0
    //
    /***/

    void
    insert_double(double value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_string:1.0
    //
    /***/

    void
    insert_string(String value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_reference:1.0
    //
    /***/

    void
    insert_reference(org.omg.CORBA.Object value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_typecode:1.0
    //
    /***/

    void
    insert_typecode(org.omg.CORBA.TypeCode value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_longlong:1.0
    //
    /***/

    void
    insert_longlong(long value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_ulonglong:1.0
    //
    /***/

    void
    insert_ulonglong(long value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_wchar:1.0
    //
    /***/

    void
    insert_wchar(char value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_wstring:1.0
    //
    /***/

    void
    insert_wstring(String value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_any:1.0
    //
    /***/

    void
    insert_any(org.omg.CORBA.Any value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_dyn_any:1.0
    //
    /***/

    void
    insert_dyn_any(DynAny value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_val:1.0
    //
    /***/

    void
    insert_val(java.io.Serializable value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_abstract:1.0
    //
    /***/

    void
    insert_abstract(java.lang.Object value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_boolean_seq:1.0
    //
    /***/

    void
    insert_boolean_seq(boolean[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_octet_seq:1.0
    //
    /***/

    void
    insert_octet_seq(byte[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_char_seq:1.0
    //
    /***/

    void
    insert_char_seq(char[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_wchar_seq:1.0
    //
    /***/

    void
    insert_wchar_seq(char[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_short_seq:1.0
    //
    /***/

    void
    insert_short_seq(short[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_ushort_seq:1.0
    //
    /***/

    void
    insert_ushort_seq(short[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_long_seq:1.0
    //
    /***/

    void
    insert_long_seq(int[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_ulong_seq:1.0
    //
    /***/

    void
    insert_ulong_seq(int[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_longlong_seq:1.0
    //
    /***/

    void
    insert_longlong_seq(long[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_ulonglong_seq:1.0
    //
    /***/

    void
    insert_ulonglong_seq(long[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_float_seq:1.0
    //
    /***/

    void
    insert_float_seq(float[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/insert_double_seq:1.0
    //
    /***/

    void
    insert_double_seq(double[] value)
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_boolean:1.0
    //
    /***/

    boolean
    get_boolean()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_octet:1.0
    //
    /***/

    byte
    get_octet()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_char:1.0
    //
    /***/

    char
    get_char()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_short:1.0
    //
    /***/

    short
    get_short()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_ushort:1.0
    //
    /***/

    short
    get_ushort()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_long:1.0
    //
    /***/

    int
    get_long()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_ulong:1.0
    //
    /***/

    int
    get_ulong()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_float:1.0
    //
    /***/

    float
    get_float()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_double:1.0
    //
    /***/

    double
    get_double()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_string:1.0
    //
    /***/

    String
    get_string()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_reference:1.0
    //
    /***/

    org.omg.CORBA.Object
    get_reference()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_typecode:1.0
    //
    /***/

    org.omg.CORBA.TypeCode
    get_typecode()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_longlong:1.0
    //
    /***/

    long
    get_longlong()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_ulonglong:1.0
    //
    /***/

    long
    get_ulonglong()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_wchar:1.0
    //
    /***/

    char
    get_wchar()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_wstring:1.0
    //
    /***/

    String
    get_wstring()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    get_any()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_dyn_any:1.0
    //
    /***/

    DynAny
    get_dyn_any()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_val:1.0
    //
    /***/

    java.io.Serializable
    get_val()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_abstract:1.0
    //
    /***/

    java.lang.Object
    get_abstract()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_boolean_seq:1.0
    //
    /***/

    boolean[]
    get_boolean_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_octet_seq:1.0
    //
    /***/

    byte[]
    get_octet_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_char_seq:1.0
    //
    /***/

    char[]
    get_char_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_wchar_seq:1.0
    //
    /***/

    char[]
    get_wchar_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_short_seq:1.0
    //
    /***/

    short[]
    get_short_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_ushort_seq:1.0
    //
    /***/

    short[]
    get_ushort_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_long_seq:1.0
    //
    /***/

    int[]
    get_long_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_ulong_seq:1.0
    //
    /***/

    int[]
    get_ulong_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_longlong_seq:1.0
    //
    /***/

    long[]
    get_longlong_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_ulonglong_seq:1.0
    //
    /***/

    long[]
    get_ulonglong_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_float_seq:1.0
    //
    /***/

    float[]
    get_float_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/get_double_seq:1.0
    //
    /***/

    double[]
    get_double_seq()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch,
               org.omg.DynamicAny.DynAnyPackage.InvalidValue;

    //
    // IDL:omg.org/DynamicAny/DynAny/seek:1.0
    //
    /***/

    boolean
    seek(int index);

    //
    // IDL:omg.org/DynamicAny/DynAny/rewind:1.0
    //
    /***/

    void
    rewind();

    //
    // IDL:omg.org/DynamicAny/DynAny/next:1.0
    //
    /***/

    boolean
    next();

    //
    // IDL:omg.org/DynamicAny/DynAny/component_count:1.0
    //
    /***/

    int
    component_count();

    //
    // IDL:omg.org/DynamicAny/DynAny/current_component:1.0
    //
    /***/

    DynAny
    current_component()
        throws org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
}

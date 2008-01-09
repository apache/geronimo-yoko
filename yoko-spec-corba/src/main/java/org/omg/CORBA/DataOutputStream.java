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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/DataOutputStream:1.0
//
/***/

public interface DataOutputStream extends org.omg.CORBA.portable.ValueBase
{
    //
    // IDL:omg.org/CORBA/DataOutputStream/write_any:1.0
    //
    /***/

    void
    write_any(org.omg.CORBA.Any value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_boolean:1.0
    //
    /***/

    void
    write_boolean(boolean value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_char:1.0
    //
    /***/

    void
    write_char(char value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_wchar:1.0
    //
    /***/

    void
    write_wchar(char value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_octet:1.0
    //
    /***/

    void
    write_octet(byte value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_short:1.0
    //
    /***/

    void
    write_short(short value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_ushort:1.0
    //
    /***/

    void
    write_ushort(short value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_long:1.0
    //
    /***/

    void
    write_long(int value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_ulong:1.0
    //
    /***/

    void
    write_ulong(int value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_longlong:1.0
    //
    /***/

    void
    write_longlong(long value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_ulonglong:1.0
    //
    /***/

    void
    write_ulonglong(long value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_float:1.0
    //
    /***/

    void
    write_float(float value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_double:1.0
    //
    /***/

    void
    write_double(double value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_string:1.0
    //
    /***/

    void
    write_string(String value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_wstring:1.0
    //
    /***/

    void
    write_wstring(String value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_Object:1.0
    //
    /***/

    void
    write_Object(org.omg.CORBA.Object value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_Abstract:1.0
    //
    /***/

    void
    write_Abstract(java.lang.Object value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_Value:1.0
    //
    /***/

    void
    write_Value(java.io.Serializable value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_TypeCode:1.0
    //
    /***/

    void
    write_TypeCode(org.omg.CORBA.TypeCode value);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_any_array:1.0
    //
    /***/

    void
    write_any_array(org.omg.CORBA.Any[] seq,
                    int offset,
                    int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_boolean_array:1.0
    //
    /***/

    void
    write_boolean_array(boolean[] seq,
                        int offset,
                        int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_char_array:1.0
    //
    /***/

    void
    write_char_array(char[] seq,
                     int offset,
                     int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_wchar_array:1.0
    //
    /***/

    void
    write_wchar_array(char[] seq,
                      int offset,
                      int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_octet_array:1.0
    //
    /***/

    void
    write_octet_array(byte[] seq,
                      int offset,
                      int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_short_array:1.0
    //
    /***/

    void
    write_short_array(short[] seq,
                      int offset,
                      int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_ushort_array:1.0
    //
    /***/

    void
    write_ushort_array(short[] seq,
                       int offset,
                       int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_long_array:1.0
    //
    /***/

    void
    write_long_array(int[] seq,
                     int offset,
                     int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_ulong_array:1.0
    //
    /***/

    void
    write_ulong_array(int[] seq,
                      int offset,
                      int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_ulonglong_array:1.0
    //
    /***/

    void
    write_ulonglong_array(long[] seq,
                          int offset,
                          int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_longlong_array:1.0
    //
    /***/

    void
    write_longlong_array(long[] seq,
                         int offset,
                         int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_float_array:1.0
    //
    /***/

    void
    write_float_array(float[] seq,
                      int offset,
                      int length);

    //
    // IDL:omg.org/CORBA/DataOutputStream/write_double_array:1.0
    //
    /***/

    void
    write_double_array(double[] seq,
                       int offset,
                       int length);
}

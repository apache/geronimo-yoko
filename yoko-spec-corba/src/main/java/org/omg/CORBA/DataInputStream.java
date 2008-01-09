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
// IDL:omg.org/CORBA/DataInputStream:1.0
//
/***/

public interface DataInputStream extends org.omg.CORBA.portable.ValueBase
{
    //
    // IDL:omg.org/CORBA/DataInputStream/read_any:1.0
    //
    /***/

    org.omg.CORBA.Any
    read_any();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_boolean:1.0
    //
    /***/

    boolean
    read_boolean();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_char:1.0
    //
    /***/

    char
    read_char();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_wchar:1.0
    //
    /***/

    char
    read_wchar();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_octet:1.0
    //
    /***/

    byte
    read_octet();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_short:1.0
    //
    /***/

    short
    read_short();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_ushort:1.0
    //
    /***/

    short
    read_ushort();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_long:1.0
    //
    /***/

    int
    read_long();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_ulong:1.0
    //
    /***/

    int
    read_ulong();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_longlong:1.0
    //
    /***/

    long
    read_longlong();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_ulonglong:1.0
    //
    /***/

    long
    read_ulonglong();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_float:1.0
    //
    /***/

    float
    read_float();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_double:1.0
    //
    /***/

    double
    read_double();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_string:1.0
    //
    /***/

    String
    read_string();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_wstring:1.0
    //
    /***/

    String
    read_wstring();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_Object:1.0
    //
    /***/

    org.omg.CORBA.Object
    read_Object();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_Abstract:1.0
    //
    /***/

    java.lang.Object
    read_Abstract();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_Value:1.0
    //
    /***/

    java.io.Serializable
    read_Value();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_TypeCode:1.0
    //
    /***/

    org.omg.CORBA.TypeCode
    read_TypeCode();

    //
    // IDL:omg.org/CORBA/DataInputStream/read_any_array:1.0
    //
    /***/

    void
    read_any_array(AnySeqHolder seq,
                   int offset,
                   int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_boolean_array:1.0
    //
    /***/

    void
    read_boolean_array(BooleanSeqHolder seq,
                       int offset,
                       int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_char_array:1.0
    //
    /***/

    void
    read_char_array(CharSeqHolder seq,
                    int offset,
                    int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_wchar_array:1.0
    //
    /***/

    void
    read_wchar_array(WCharSeqHolder seq,
                     int offset,
                     int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_octet_array:1.0
    //
    /***/

    void
    read_octet_array(OctetSeqHolder seq,
                     int offset,
                     int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_short_array:1.0
    //
    /***/

    void
    read_short_array(ShortSeqHolder seq,
                     int offset,
                     int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_ushort_array:1.0
    //
    /***/

    void
    read_ushort_array(UShortSeqHolder seq,
                      int offset,
                      int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_long_array:1.0
    //
    /***/

    void
    read_long_array(LongSeqHolder seq,
                    int offset,
                    int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_ulong_array:1.0
    //
    /***/

    void
    read_ulong_array(ULongSeqHolder seq,
                     int offset,
                     int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_ulonglong_array:1.0
    //
    /***/

    void
    read_ulonglong_array(ULongLongSeqHolder seq,
                         int offset,
                         int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_longlong_array:1.0
    //
    /***/

    void
    read_longlong_array(LongLongSeqHolder seq,
                        int offset,
                        int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_float_array:1.0
    //
    /***/

    void
    read_float_array(FloatSeqHolder seq,
                     int offset,
                     int length);

    //
    // IDL:omg.org/CORBA/DataInputStream/read_double_array:1.0
    //
    /***/

    void
    read_double_array(DoubleSeqHolder seq,
                      int offset,
                      int length);
}

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

package org.apache.yoko.orb.CORBA;

final public class DataOutputStream implements org.omg.CORBA.DataOutputStream {
    private OutputStream out_;

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------

    public DataOutputStream(OutputStream out) {
        out_ = out;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String[] _truncatable_ids() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void write_any(org.omg.CORBA.Any value) {
        out_.write_any(value);
    }

    public void write_boolean(boolean value) {
        out_.write_boolean(value);
    }

    public void write_char(char value) {
        out_.write_char(value);
    }

    public void write_wchar(char value) {
        out_.write_wchar(value);
    }

    public void write_octet(byte value) {
        out_.write_octet(value);
    }

    public void write_short(short value) {
        out_.write_short(value);
    }

    public void write_ushort(short value) {
        out_.write_ushort(value);
    }

    public void write_long(int value) {
        out_.write_long(value);
    }

    public void write_ulong(int value) {
        out_.write_ulong(value);
    }

    public void write_longlong(long value) {
        out_.write_longlong(value);
    }

    public void write_ulonglong(long value) {
        out_.write_ulonglong(value);
    }

    public void write_float(float value) {
        out_.write_float(value);
    }

    public void write_double(double value) {
        out_.write_double(value);
    }

    public void write_string(String value) {
        out_.write_string(value);
    }

    public void write_wstring(String value) {
        out_.write_wstring(value);
    }

    public void write_Object(org.omg.CORBA.Object value) {
        out_.write_Object(value);
    }

    public void write_Abstract(java.lang.Object value) {
        out_.write_abstract_interface(value);
    }

    public void write_Value(java.io.Serializable value) {
        out_.write_value(value);
    }

    public void write_TypeCode(org.omg.CORBA.TypeCode value) {
        out_.write_TypeCode(value);
    }

    public void write_any_array(org.omg.CORBA.Any[] seq, int offset, int length) {
        for (int i = offset; i < offset + length; i++)
            out_.write_any(seq[i]);
    }

    public void write_boolean_array(boolean[] seq, int offset, int length) {
        out_.write_boolean_array(seq, offset, length);
    }

    public void write_char_array(char[] seq, int offset, int length) {
        out_.write_char_array(seq, offset, length);
    }

    public void write_wchar_array(char[] seq, int offset, int length) {
        out_.write_wchar_array(seq, offset, length);
    }

    public void write_octet_array(byte[] seq, int offset, int length) {
        out_.write_octet_array(seq, offset, length);
    }

    public void write_short_array(short[] seq, int offset, int length) {
        out_.write_short_array(seq, offset, length);
    }

    public void write_ushort_array(short[] seq, int offset, int length) {
        out_.write_ushort_array(seq, offset, length);
    }

    public void write_long_array(int[] seq, int offset, int length) {
        out_.write_long_array(seq, offset, length);
    }

    public void write_ulong_array(int[] seq, int offset, int length) {
        out_.write_ulong_array(seq, offset, length);
    }

    public void write_ulonglong_array(long[] seq, int offset, int length) {
        out_.write_ulonglong_array(seq, offset, length);
    }

    public void write_longlong_array(long[] seq, int offset, int length) {
        out_.write_longlong_array(seq, offset, length);
    }

    public void write_float_array(float[] seq, int offset, int length) {
        out_.write_float_array(seq, offset, length);
    }

    public void write_double_array(double[] seq, int offset, int length) {
        out_.write_double_array(seq, offset, length);
    }
}

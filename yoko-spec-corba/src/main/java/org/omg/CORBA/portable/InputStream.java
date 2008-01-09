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

package org.omg.CORBA.portable;

public abstract class InputStream extends java.io.InputStream {
    public int read() throws java.io.IOException {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.ORB orb() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public abstract boolean read_boolean();

    public abstract char read_char();

    public abstract char read_wchar();

    public abstract byte read_octet();

    public abstract short read_short();

    public abstract short read_ushort();

    public abstract int read_long();

    public abstract int read_ulong();

    public abstract long read_longlong();

    public abstract long read_ulonglong();

    public abstract float read_float();

    public abstract double read_double();

    public abstract String read_string();

    public abstract String read_wstring();

    public abstract void read_boolean_array(boolean[] value, int offset,
            int length);

    public abstract void read_char_array(char[] value, int offset, int length);

    public abstract void read_wchar_array(char[] value, int offset, int length);

    public abstract void read_octet_array(byte[] value, int offset, int length);

    public abstract void read_short_array(short[] value, int offset, int length);

    public abstract void read_ushort_array(short[] value, int offset, int length);

    public abstract void read_long_array(int[] value, int offset, int length);

    public abstract void read_ulong_array(int[] value, int offset, int length);

    public abstract void read_longlong_array(long[] value, int offset,
            int length);

    public abstract void read_ulonglong_array(long[] value, int offset,
            int length);

    public abstract void read_float_array(float[] value, int offset, int length);

    public abstract void read_double_array(double[] value, int offset,
            int length);

    public abstract org.omg.CORBA.Object read_Object();

    public org.omg.CORBA.Object read_Object(java.lang.Class clz) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public abstract org.omg.CORBA.TypeCode read_TypeCode();

    public abstract org.omg.CORBA.Any read_any();

    public org.omg.CORBA.Context read_Context() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    // Note: Don't use @deprecated here
    /**
     * Deprecated by CORBA 2.2.
     */
    public org.omg.CORBA.Principal read_Principal() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.math.BigDecimal read_fixed() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }
}

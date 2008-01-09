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

public abstract class Any implements org.omg.CORBA.portable.IDLEntity {
    public abstract boolean equal(org.omg.CORBA.Any a);

    public abstract org.omg.CORBA.TypeCode type();

    public abstract void type(org.omg.CORBA.TypeCode t);

    public abstract void read_value(org.omg.CORBA.portable.InputStream is,
            org.omg.CORBA.TypeCode t) throws org.omg.CORBA.MARSHAL;

    public abstract void write_value(org.omg.CORBA.portable.OutputStream os);

    public abstract org.omg.CORBA.portable.OutputStream create_output_stream();

    public abstract org.omg.CORBA.portable.InputStream create_input_stream();

    public abstract short extract_short() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_short(short s);

    public abstract int extract_long() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_long(int i);

    public abstract long extract_longlong() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_longlong(long l);

    public abstract short extract_ushort() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_ushort(short s);

    public abstract int extract_ulong() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_ulong(int i);

    public abstract long extract_ulonglong() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_ulonglong(long l);

    public abstract float extract_float() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_float(float f);

    public abstract double extract_double() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_double(double d);

    public abstract boolean extract_boolean()
            throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_boolean(boolean b);

    public abstract char extract_char() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_char(char c)
            throws org.omg.CORBA.DATA_CONVERSION;

    public abstract char extract_wchar() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_wchar(char c)
            throws org.omg.CORBA.DATA_CONVERSION;

    public abstract byte extract_octet() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_octet(byte b);

    public abstract org.omg.CORBA.Any extract_any()
            throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_any(org.omg.CORBA.Any a);

    public abstract org.omg.CORBA.Object extract_Object()
            throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_Object(org.omg.CORBA.Object o);

    public abstract void insert_Object(org.omg.CORBA.Object o,
            org.omg.CORBA.TypeCode t) throws org.omg.CORBA.BAD_PARAM;

    public abstract String extract_string() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_string(String s)
            throws org.omg.CORBA.DATA_CONVERSION, org.omg.CORBA.MARSHAL;

    public abstract String extract_wstring() throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_wstring(String s) throws org.omg.CORBA.MARSHAL;

    public abstract org.omg.CORBA.TypeCode extract_TypeCode()
            throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_TypeCode(org.omg.CORBA.TypeCode t);

    // Note: Don't use @deprecated here
    /**
     * Deprecated by CORBA 2.2.
     */
    public org.omg.CORBA.Principal extract_Principal()
            throws org.omg.CORBA.BAD_OPERATION {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    // Note: Don't use @deprecated here
    /**
     * Deprecated by CORBA 2.2.
     */
    public void insert_Principal(org.omg.CORBA.Principal p) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.portable.Streamable extract_Streamable()
            throws org.omg.CORBA.BAD_INV_ORDER {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void insert_Streamable(org.omg.CORBA.portable.Streamable s) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public java.math.BigDecimal extract_fixed() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void insert_fixed(java.math.BigDecimal value) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void insert_fixed(java.math.BigDecimal value,
            org.omg.CORBA.TypeCode type) throws org.omg.CORBA.BAD_INV_ORDER {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public abstract java.io.Serializable extract_Value()
            throws org.omg.CORBA.BAD_OPERATION;

    public abstract void insert_Value(java.io.Serializable v);

    public abstract void insert_Value(java.io.Serializable v,
            org.omg.CORBA.TypeCode t) throws org.omg.CORBA.MARSHAL;
}

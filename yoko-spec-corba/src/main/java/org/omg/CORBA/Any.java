/*
 * Copyright 2022 IBM Corporation and others.
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
package org.omg.CORBA;

import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("unused")
public abstract class Any implements IDLEntity {
    public abstract boolean equal(Any a);
    public abstract TypeCode type();
    public abstract void type(TypeCode t);
    public abstract void read_value(InputStream is, TypeCode t) throws MARSHAL;
    public abstract void write_value(OutputStream os);
    public abstract OutputStream create_output_stream();
    public abstract InputStream create_input_stream();
    public abstract short extract_short() throws BAD_OPERATION;
    public abstract void insert_short(short s);
    public abstract int extract_long() throws BAD_OPERATION;
    public abstract void insert_long(int i);
    public abstract long extract_longlong() throws BAD_OPERATION;
    public abstract void insert_longlong(long l);
    public abstract short extract_ushort() throws BAD_OPERATION;
    public abstract void insert_ushort(short s);
    public abstract int extract_ulong() throws BAD_OPERATION;
    public abstract void insert_ulong(int i);
    public abstract long extract_ulonglong() throws BAD_OPERATION;
    public abstract void insert_ulonglong(long l);
    public abstract float extract_float() throws BAD_OPERATION;
    public abstract void insert_float(float f);
    public abstract double extract_double() throws BAD_OPERATION;
    public abstract void insert_double(double d);
    public abstract boolean extract_boolean() throws BAD_OPERATION;
    public abstract void insert_boolean(boolean b);
    public abstract char extract_char() throws BAD_OPERATION;
    public abstract void insert_char(char c) throws DATA_CONVERSION;
    public abstract char extract_wchar() throws BAD_OPERATION;
    public abstract void insert_wchar(char c) throws DATA_CONVERSION;
    public abstract byte extract_octet() throws BAD_OPERATION;
    public abstract void insert_octet(byte b);
    public abstract Any extract_any() throws BAD_OPERATION;
    public abstract void insert_any(Any a);
    public abstract org.omg.CORBA.Object extract_Object() throws BAD_OPERATION;
    public abstract void insert_Object(org.omg.CORBA.Object o);
    public abstract void insert_Object(org.omg.CORBA.Object o, TypeCode t) throws BAD_PARAM;
    public abstract String extract_string() throws BAD_OPERATION;
    public abstract void insert_string(String s) throws DATA_CONVERSION, MARSHAL;
    public abstract String extract_wstring() throws BAD_OPERATION;
    public abstract void insert_wstring(String s) throws MARSHAL;
    public abstract TypeCode extract_TypeCode() throws BAD_OPERATION;
    public abstract void insert_TypeCode(TypeCode t);

    // Note: Don't use @deprecated here
    /**
     * Deprecated by CORBA 2.2.
     */
    public Principal extract_Principal() throws BAD_OPERATION { throw new NO_IMPLEMENT(); }

    // Note: Don't use @deprecated here
    /**
     * Deprecated by CORBA 2.2.
     */
    public void insert_Principal(Principal p) {
        throw new NO_IMPLEMENT();
    }
    public Streamable extract_Streamable() throws BAD_INV_ORDER { throw new NO_IMPLEMENT(); }
    public void insert_Streamable(Streamable s) {
        throw new NO_IMPLEMENT();
    }
    public BigDecimal extract_fixed() {
        throw new NO_IMPLEMENT();
    }
    public void insert_fixed(BigDecimal value) {
        throw new NO_IMPLEMENT();
    }
    public void insert_fixed(BigDecimal value, TypeCode type) throws BAD_INV_ORDER { throw new NO_IMPLEMENT(); }
    public abstract Serializable extract_Value() throws BAD_OPERATION;
    public abstract void insert_Value(Serializable v);
    public abstract void insert_Value(Serializable v, TypeCode t) throws MARSHAL;
}

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

import org.omg.CORBA.portable.IDLEntity;

import static java.util.Objects.hash;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

//
// We cannot generate this class from IDL because we need hacks to
// support org.omg.CORBA_2_4.tk_local_interface
//
@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class TCKind implements IDLEntity {
    private static final TCKind[] values = new TCKind[34];

    private final int value;

    public final static int _tk_null = 0;
    public final static int _tk_void = 1;
    public final static int _tk_short = 2;
    public final static int _tk_long = 3;
    public final static int _tk_ushort = 4;
    public final static int _tk_ulong = 5;
    public final static int _tk_float = 6;
    public final static int _tk_double = 7;
    public final static int _tk_boolean = 8;
    public final static int _tk_char = 9;
    public final static int _tk_octet = 10;
    public final static int _tk_any = 11;
    public final static int _tk_TypeCode = 12;
    public final static int _tk_Principal = 13;
    public final static int _tk_objref = 14;
    public final static int _tk_struct = 15;
    public final static int _tk_union = 16;
    public final static int _tk_enum = 17;
    public final static int _tk_string = 18;
    public final static int _tk_sequence = 19;
    public final static int _tk_array = 20;
    public final static int _tk_alias = 21;
    public final static int _tk_except = 22;
    public final static int _tk_longlong = 23;
    public final static int _tk_ulonglong = 24;
    public final static int _tk_longdouble = 25;
    public final static int _tk_wchar = 26;
    public final static int _tk_wstring = 27;
    public final static int _tk_fixed = 28;
    public final static int _tk_value = 29;
    public final static int _tk_value_box = 30;
    public final static int _tk_native = 31;
    public final static int _tk_abstract_interface = 32;

    public final static TCKind tk_null = new TCKind(_tk_null);
    public final static TCKind tk_void = new TCKind(_tk_void);
    public final static TCKind tk_short = new TCKind(_tk_short);
    public final static TCKind tk_long = new TCKind(_tk_long);
    public final static TCKind tk_ushort = new TCKind(_tk_ushort);
    public final static TCKind tk_ulong = new TCKind(_tk_ulong);
    public final static TCKind tk_float = new TCKind(_tk_float);
    public final static TCKind tk_double = new TCKind(_tk_double);
    public final static TCKind tk_boolean = new TCKind(_tk_boolean);
    public final static TCKind tk_char = new TCKind(_tk_char);
    public final static TCKind tk_octet = new TCKind(_tk_octet);
    public final static TCKind tk_any = new TCKind(_tk_any);
    public final static TCKind tk_TypeCode = new TCKind(_tk_TypeCode);
    public final static TCKind tk_Principal = new TCKind(_tk_Principal);
    public final static TCKind tk_objref = new TCKind(_tk_objref);
    public final static TCKind tk_struct = new TCKind(_tk_struct);
    public final static TCKind tk_union = new TCKind(_tk_union);
    public final static TCKind tk_enum = new TCKind(_tk_enum);
    public final static TCKind tk_string = new TCKind(_tk_string);
    public final static TCKind tk_sequence = new TCKind(_tk_sequence);
    public final static TCKind tk_array = new TCKind(_tk_array);
    public final static TCKind tk_alias = new TCKind(_tk_alias);
    public final static TCKind tk_except = new TCKind(_tk_except);
    public final static TCKind tk_longlong = new TCKind(_tk_longlong);
    public final static TCKind tk_ulonglong = new TCKind(_tk_ulonglong);
    public final static TCKind tk_longdouble = new TCKind(_tk_longdouble);
    public final static TCKind tk_wchar = new TCKind(_tk_wchar);
    public final static TCKind tk_wstring = new TCKind(_tk_wstring);
    public final static TCKind tk_fixed = new TCKind(_tk_fixed);
    public final static TCKind tk_value = new TCKind(_tk_value);
    public final static TCKind tk_value_box = new TCKind(_tk_value_box);
    public final static TCKind tk_native = new TCKind(_tk_native);
    public final static TCKind tk_abstract_interface = new TCKind(_tk_abstract_interface);

    protected TCKind(int value) {
        this.value = value;
        values[value] = this;
    }

    public int value() {
        return value;
    }

    public static TCKind from_int(int value) {
        try {
            return values[value];
        } catch (IndexOutOfBoundsException e) {
            throw new BAD_PARAM("Value (" + value + ") out of range: ", 25, COMPLETED_NO);
        }

    }

    private java.lang.Object readResolve() {
        return from_int(value());
    }

    @Override
    public String toString() {
        switch (this.value) {
        case _tk_null: return "tk_null";
        case _tk_void: return "tk_void";
        case _tk_short: return "tk_short";
        case _tk_long: return "tk_long";
        case _tk_ushort: return "tk_ushort";
        case _tk_ulong: return "tk_ulong";
        case _tk_float: return "tk_float";
        case _tk_double: return "tk_double";
        case _tk_boolean: return "tk_boolean";
        case _tk_char: return "tk_char";
        case _tk_octet: return "tk_octet";
        case _tk_any: return "tk_any";
        case _tk_TypeCode: return "tk_TypeCode";
        case _tk_Principal: return "tk_Principal";
        case _tk_objref: return "tk_objref";
        case _tk_struct: return "tk_struct";
        case _tk_union: return "tk_union";
        case _tk_enum: return "tk_enum";
        case _tk_string: return "tk_string";
        case _tk_sequence: return "tk_sequence";
        case _tk_array: return "tk_array";
        case _tk_alias: return "tk_alias";
        case _tk_except: return "tk_except";
        case _tk_longlong: return "tk_longlong";
        case _tk_ulonglong: return "tk_ulonglong";
        case _tk_longdouble: return "tk_longdouble";
        case _tk_wchar: return "tk_wchar";
        case _tk_wstring: return "tk_wstring";
        case _tk_fixed: return "tk_fixed";
        case _tk_value: return "tk_value";
        case _tk_value_box: return "tk_value_box";
        case _tk_native: return "tk_native";
        case _tk_abstract_interface: return "tk_abstract_interface";
        default: return "TCKind(" + value + ")";
        }
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;
        return ((TCKind)o).value() == value;
    }

    @Override
    public int hashCode() {
        return hash(value);
    }
}

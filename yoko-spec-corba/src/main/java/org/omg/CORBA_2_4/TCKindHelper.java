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
package org.omg.CORBA_2_4;

final public class TCKindHelper {
    public static void insert(org.omg.CORBA.Any any, org.omg.CORBA.TCKind val) {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static org.omg.CORBA.TCKind extract(org.omg.CORBA.Any any) {
        if (any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode type() {
        if (typeCode_ == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            String[] members = new String[34];
            members[0] = "tk_null";
            members[1] = "tk_void";
            members[2] = "tk_short";
            members[3] = "tk_long";
            members[4] = "tk_ushort";
            members[5] = "tk_ulong";
            members[6] = "tk_float";
            members[7] = "tk_double";
            members[8] = "tk_boolean";
            members[9] = "tk_char";
            members[10] = "tk_octet";
            members[11] = "tk_any";
            members[12] = "tk_TypeCode";
            members[13] = "tk_Principal";
            members[14] = "tk_objref";
            members[15] = "tk_struct";
            members[16] = "tk_union";
            members[17] = "tk_enum";
            members[18] = "tk_string";
            members[19] = "tk_sequence";
            members[20] = "tk_array";
            members[21] = "tk_alias";
            members[22] = "tk_except";
            members[23] = "tk_longlong";
            members[24] = "tk_ulonglong";
            members[25] = "tk_longdouble";
            members[26] = "tk_wchar";
            members[27] = "tk_wstring";
            members[28] = "tk_fixed";
            members[29] = "tk_value";
            members[30] = "tk_value_box";
            members[31] = "tk_native";
            members[32] = "tk_abstract_interface";
            members[33] = "tk_local_interface";
            typeCode_ = orb.create_enum_tc(id(), "TCKind", members);
        }

        return typeCode_;
    }

    public static String id() {
        return "IDL:omg.org/CORBA/TCKind:1.0";
    }

    public static org.omg.CORBA.TCKind read(
            org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA.TCKind _ob_v;
        _ob_v = TCKind.from_int(in.read_ulong());
        return _ob_v;
    }

    public static void write(org.omg.CORBA.portable.OutputStream out,
            org.omg.CORBA.TCKind val) {
        out.write_ulong(val.value());
    }
}

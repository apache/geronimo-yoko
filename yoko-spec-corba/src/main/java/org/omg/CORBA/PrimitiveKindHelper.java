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
// IDL:omg.org/CORBA/PrimitiveKind:1.0
//
final public class PrimitiveKindHelper
{
    public static void
    insert(org.omg.CORBA.Any any, PrimitiveKind val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static PrimitiveKind
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            String[] members = new String[22];
            members[0] = "pk_null";
            members[1] = "pk_void";
            members[2] = "pk_short";
            members[3] = "pk_long";
            members[4] = "pk_ushort";
            members[5] = "pk_ulong";
            members[6] = "pk_float";
            members[7] = "pk_double";
            members[8] = "pk_boolean";
            members[9] = "pk_char";
            members[10] = "pk_octet";
            members[11] = "pk_any";
            members[12] = "pk_TypeCode";
            members[13] = "pk_Principal";
            members[14] = "pk_string";
            members[15] = "pk_objref";
            members[16] = "pk_longlong";
            members[17] = "pk_ulonglong";
            members[18] = "pk_longdouble";
            members[19] = "pk_wchar";
            members[20] = "pk_wstring";
            members[21] = "pk_value_base";
            typeCode_ = orb.create_enum_tc(id(), "PrimitiveKind", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/PrimitiveKind:1.0";
    }

    public static PrimitiveKind
    read(org.omg.CORBA.portable.InputStream in)
    {
        PrimitiveKind _ob_v;
        _ob_v = PrimitiveKind.from_int(in.read_ulong());
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, PrimitiveKind val)
    {
        out.write_ulong(val.value());
    }
}

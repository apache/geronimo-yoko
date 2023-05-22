/*
 * Copyright 2010 IBM Corporation and others.
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
package ORBTest_Basic;

//
// IDL:ORBTest_Basic/RecursiveStruct:1.0
//
final public class RecursiveStructHelper
{
    public static void
    insert(org.omg.CORBA.Any any, RecursiveStruct val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static RecursiveStruct
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[3];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "s";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "i";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "rs";
            org.omg.CORBA.TypeCode content0;
            content0 = orb.create_recursive_tc(id());
            members[2].type = orb.create_sequence_tc(0, content0);

            typeCode_ = orb.create_struct_tc(id(), "RecursiveStruct", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:ORBTest_Basic/RecursiveStruct:1.0";
    }

    public static RecursiveStruct
    read(org.omg.CORBA.portable.InputStream in)
    {
        RecursiveStruct _ob_v = new RecursiveStruct();
        _ob_v.s = in.read_string();
        _ob_v.i = in.read_short();
        int len0 = in.read_ulong();
        _ob_v.rs = new RecursiveStruct[len0];
        for(int i0 = 0; i0 < len0; i0++)
            _ob_v.rs[i0] = RecursiveStructHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, RecursiveStruct val)
    {
        out.write_string(val.s);
        out.write_short(val.i);
        int len0 = val.rs.length;
        out.write_ulong(len0);
        for(int i0 = 0; i0 < len0; i0++)
            RecursiveStructHelper.write(out, val.rs[i0]);
    }
}

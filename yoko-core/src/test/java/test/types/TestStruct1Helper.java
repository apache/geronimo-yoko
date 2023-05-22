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
package test.types;

//
// IDL:TestStruct1:1.0
//
final public class TestStruct1Helper
{
    public static void
    insert(org.omg.CORBA.Any any, TestStruct1 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestStruct1
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[7];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "s";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "l";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "d";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_double);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "b";
            members[3].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "c";
            members[4].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "o";
            members[5].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            members[6] = new org.omg.CORBA.StructMember();
            members[6].name = "str";
            members[6].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            typeCode_ = orb.create_struct_tc(id(), "TestStruct1", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestStruct1:1.0";
    }

    public static TestStruct1
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestStruct1 _ob_v = new TestStruct1();
        _ob_v.s = in.read_short();
        _ob_v.l = in.read_long();
        _ob_v.d = in.read_double();
        _ob_v.b = in.read_boolean();
        _ob_v.c = in.read_char();
        _ob_v.o = in.read_octet();
        _ob_v.str = in.read_string();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestStruct1 val)
    {
        out.write_short(val.s);
        out.write_long(val.l);
        out.write_double(val.d);
        out.write_boolean(val.b);
        out.write_char(val.c);
        out.write_octet(val.o);
        out.write_string(val.str);
    }
}

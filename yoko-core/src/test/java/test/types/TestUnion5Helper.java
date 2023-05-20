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
// IDL:TestUnion5:1.0
//
final public class TestUnion5Helper
{
    public static void
    insert(org.omg.CORBA.Any any, TestUnion5 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestUnion5
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
            org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[4];

            members[0] = new org.omg.CORBA.UnionMember();
            members[0].name = "f";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);
            members[0].label = orb.create_any();
            members[0].label.insert_octet((byte)0);

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "a";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_any);
            members[1].label = orb.create_any();
            members[1].label.insert_longlong(-42L);

            members[2] = new org.omg.CORBA.UnionMember();
            members[2].name = "c";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);
            members[2].label = orb.create_any();
            members[2].label.insert_longlong(100000L);

            members[3] = new org.omg.CORBA.UnionMember();
            members[3].name = "c";
            members[3].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);
            members[3].label = orb.create_any();
            members[3].label.insert_longlong(50000000L);

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_longlong);
            typeCode_ = orb.create_union_tc(id(), "TestUnion5", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestUnion5:1.0";
    }

    public static TestUnion5
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestUnion5 _ob_v = new TestUnion5();
        long _ob_d;
        _ob_d = in.read_longlong();

        if(_ob_d == -42L)
        {
            org.omg.CORBA.Any _ob_m;
            _ob_m = in.read_any();
            _ob_v.a(_ob_m);
        }
        else if(_ob_d == 100000L ||
                _ob_d == 50000000L)
        {
            char _ob_m;
            _ob_m = in.read_char();
            _ob_v.c(_ob_d, _ob_m);
        }
        else 
        {
            String _ob_m;
            _ob_m = in.read_string();
            _ob_v.f(_ob_d, _ob_m);
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestUnion5 val)
    {
        long _ob_d = val.discriminator();
        out.write_longlong(_ob_d);

        if(_ob_d == -42L)
        {
            org.omg.CORBA.Any _ob_m = val.a();
            out.write_any(_ob_m);
        }
        else if(_ob_d == 100000L ||
                _ob_d == 50000000L)
        {
            char _ob_m = val.c();
            out.write_char(_ob_m);
        }
        else 
        {
            String _ob_m = val.f();
            out.write_string(_ob_m);
        }
    }
}

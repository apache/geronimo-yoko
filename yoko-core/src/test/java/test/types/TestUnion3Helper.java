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
// IDL:TestUnion3:1.0
//
final public class TestUnion3Helper
{
    public static void
    insert(org.omg.CORBA.Any any, TestUnion3 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestUnion3
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
            org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[5];

            members[0] = new org.omg.CORBA.UnionMember();
            members[0].name = "c";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);
            members[0].label = orb.create_any();
            members[0].label.insert_char('a');

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "c";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);
            members[1].label = orb.create_any();
            members[1].label.insert_char('b');

            members[2] = new org.omg.CORBA.UnionMember();
            members[2].name = "a";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_any);
            members[2].label = orb.create_any();
            members[2].label.insert_char('c');

            members[3] = new org.omg.CORBA.UnionMember();
            members[3].name = "ar";
            members[3].type = orb.create_array_tc(10, orb.create_array_tc(20, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string)));
            members[3].label = orb.create_any();
            members[3].label.insert_char('d');

            members[4] = new org.omg.CORBA.UnionMember();
            members[4].name = "s";
            members[4].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);
            members[4].label = orb.create_any();
            members[4].label.insert_char('x');

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);
            typeCode_ = orb.create_union_tc(id(), "TestUnion3", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestUnion3:1.0";
    }

    public static TestUnion3
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestUnion3 _ob_v = new TestUnion3();
        char _ob_d;
        _ob_d = in.read_char();

        switch(_ob_d)
        {
        case 'a':
        case 'b':
        {
            char _ob_m;
            _ob_m = in.read_char();
            _ob_v.c(_ob_d, _ob_m);
            break;
        }

        case 'c':
        {
            org.omg.CORBA.Any _ob_m;
            _ob_m = in.read_any();
            _ob_v.a(_ob_m);
            break;
        }

        case 'd':
        {
            String[][] _ob_m;
            int len0 = 10;
            _ob_m = new String[len0][];
            for(int i0 = 0; i0 < len0; i0++)
            {
                int len1 = 20;
                _ob_m[i0] = new String[len1];
                for(int i1 = 0; i1 < len1; i1++)
                    _ob_m[i0][i1] = in.read_string();
            }
            _ob_v.ar(_ob_m);
            break;
        }

        case 'x':
        {
            String _ob_m;
            _ob_m = in.read_string();
            _ob_v.s(_ob_m);
            break;
        }

        default:
            _ob_v.__default(_ob_d);
            break;
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestUnion3 val)
    {
        char _ob_d = val.discriminator();
        out.write_char(_ob_d);

        switch(_ob_d)
        {
        case 'a':
        case 'b':
        {
            char _ob_m = val.c();
            out.write_char(_ob_m);
            break;
        }

        case 'c':
        {
            org.omg.CORBA.Any _ob_m = val.a();
            out.write_any(_ob_m);
            break;
        }

        case 'd':
        {
            String[][] _ob_m = val.ar();
            int len0 = _ob_m.length;
            if(len0 != 10)
                 throw new org.omg.CORBA.MARSHAL();
            for(int i0 = 0; i0 < len0; i0++)
            {
                int len1 = _ob_m[i0].length;
                if(len1 != 20)
                     throw new org.omg.CORBA.MARSHAL();
                for(int i1 = 0; i1 < len1; i1++)
                    out.write_string(_ob_m[i0][i1]);
            }
            break;
        }

        case 'x':
        {
            String _ob_m = val.s();
            out.write_string(_ob_m);
            break;
        }

        default:
            break;
        }
    }
}

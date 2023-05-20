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
package test.types.DynAnyTypes;

//
// IDL:test/types/DynAnyTypes/TestUnion3:1.0
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
            org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[3];

            members[0] = new org.omg.CORBA.UnionMember();
            members[0].name = "a";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
            members[0].label = orb.create_any();
            TestEnumHelper.insert(members[0].label, TestEnum.from_int(0));

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "b";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_double);
            members[1].label = orb.create_any();
            TestEnumHelper.insert(members[1].label, TestEnum.from_int(1));

            members[2] = new org.omg.CORBA.UnionMember();
            members[2].name = "c";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);
            members[2].label = orb.create_any();
            TestEnumHelper.insert(members[2].label, TestEnum.from_int(2));

            org.omg.CORBA.TypeCode discType = TestEnumHelper.type();
            typeCode_ = orb.create_union_tc(id(), "TestUnion3", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:test/types/DynAnyTypes/TestUnion3:1.0";
    }

    public static TestUnion3
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestUnion3 _ob_v = new TestUnion3();
        TestEnum _ob_d;
        _ob_d = TestEnumHelper.read(in);

        switch(_ob_d.value())
        {
        case TestEnum._red:
        {
            int _ob_m;
            _ob_m = in.read_long();
            _ob_v.a(_ob_m);
            break;
        }

        case TestEnum._green:
        {
            double _ob_m;
            _ob_m = in.read_double();
            _ob_v.b(_ob_m);
            break;
        }

        case TestEnum._blue:
        {
            char _ob_m;
            _ob_m = in.read_char();
            _ob_v.c(_ob_m);
            break;
        }
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestUnion3 val)
    {
        TestEnum _ob_d = val.discriminator();
        TestEnumHelper.write(out, _ob_d);

        switch(_ob_d.value())
        {
        case TestEnum._red:
        {
            int _ob_m = val.a();
            out.write_long(_ob_m);
            break;
        }

        case TestEnum._green:
        {
            double _ob_m = val.b();
            out.write_double(_ob_m);
            break;
        }

        case TestEnum._blue:
        {
            char _ob_m = val.c();
            out.write_char(_ob_m);
            break;
        }
        }
    }
}

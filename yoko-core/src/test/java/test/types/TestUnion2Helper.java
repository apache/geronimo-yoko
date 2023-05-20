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
// IDL:TestUnion2:1.0
//
final public class TestUnion2Helper
{
    public static void
    insert(org.omg.CORBA.Any any, TestUnion2 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestUnion2
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
            members[0].name = "seq";
            members[0].type = orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long));
            members[0].label = orb.create_any();
            TestEnumHelper.insert(members[0].label, TestEnum.from_int(0));

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "seq";
            members[1].type = orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long));
            members[1].label = orb.create_any();
            TestEnumHelper.insert(members[1].label, TestEnum.from_int(1));

            members[2] = new org.omg.CORBA.UnionMember();
            members[2].name = "un";
            members[2].type = TestUnion1Helper.type();
            members[2].label = orb.create_any();
            TestEnumHelper.insert(members[2].label, TestEnum.from_int(2));

            org.omg.CORBA.TypeCode discType = TestEnumHelper.type();
            typeCode_ = orb.create_union_tc(id(), "TestUnion2", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestUnion2:1.0";
    }

    public static TestUnion2
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestUnion2 _ob_v = new TestUnion2();
        TestEnum _ob_d;
        _ob_d = TestEnumHelper.read(in);

        switch(_ob_d.value())
        {
        case TestEnum._A:
        case TestEnum._B:
        {
            int[] _ob_m;
            int len0 = in.read_ulong();
            _ob_m = new int[len0];
            in.read_long_array(_ob_m, 0, len0);
            _ob_v.seq(_ob_d, _ob_m);
            break;
        }

        case TestEnum._C:
        {
            TestUnion1 _ob_m;
            _ob_m = TestUnion1Helper.read(in);
            _ob_v.un(_ob_m);
            break;
        }
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestUnion2 val)
    {
        TestEnum _ob_d = val.discriminator();
        TestEnumHelper.write(out, _ob_d);

        switch(_ob_d.value())
        {
        case TestEnum._A:
        case TestEnum._B:
        {
            int[] _ob_m = val.seq();
            int len0 = _ob_m.length;
            out.write_ulong(len0);
            out.write_long_array(_ob_m, 0, len0);
            break;
        }

        case TestEnum._C:
        {
            TestUnion1 _ob_m = val.un();
            TestUnion1Helper.write(out, _ob_m);
            break;
        }
        }
    }
}

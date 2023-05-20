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
// IDL:TestUnion1:1.0
//
final public class TestUnion1Helper
{
    public static void
    insert(org.omg.CORBA.Any any, TestUnion1 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestUnion1
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
            org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[10];

            members[0] = new org.omg.CORBA.UnionMember();
            members[0].name = "l";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
            members[0].label = orb.create_any();
            members[0].label.insert_long((int)(-1L));

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "a";
            members[1].type = DoubleArrayHelper.type();
            members[1].label = orb.create_any();
            members[1].label.insert_long((int)(-2L));

            members[2] = new org.omg.CORBA.UnionMember();
            members[2].name = "s";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);
            members[2].label = orb.create_any();
            members[2].label.insert_long((int)(-3L));

            members[3] = new org.omg.CORBA.UnionMember();
            members[3].name = "str";
            members[3].type = TestStruct2Helper.type();
            members[3].label = orb.create_any();
            members[3].label.insert_long((int)(0L));

            members[4] = new org.omg.CORBA.UnionMember();
            members[4].name = "str";
            members[4].type = TestStruct2Helper.type();
            members[4].label = orb.create_any();
            members[4].label.insert_long((int)(1L));

            members[5] = new org.omg.CORBA.UnionMember();
            members[5].name = "str";
            members[5].type = TestStruct2Helper.type();
            members[5].label = orb.create_any();
            members[5].label.insert_long((int)(2L));

            members[6] = new org.omg.CORBA.UnionMember();
            members[6].name = "str";
            members[6].type = TestStruct2Helper.type();
            members[6].label = orb.create_any();
            members[6].label.insert_long((int)(3L));

            members[7] = new org.omg.CORBA.UnionMember();
            members[7].name = "str";
            members[7].type = TestStruct2Helper.type();
            members[7].label = orb.create_any();
            members[7].label.insert_long((int)(-4L));

            members[8] = new org.omg.CORBA.UnionMember();
            members[8].name = "str";
            members[8].type = TestStruct2Helper.type();
            members[8].label = orb.create_any();
            members[8].label.insert_long((int)(-5L));

            members[9] = new org.omg.CORBA.UnionMember();
            members[9].name = "tc";
            members[9].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_TypeCode);
            members[9].label = orb.create_any();
            members[9].label.insert_octet((byte)0);

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
            typeCode_ = orb.create_union_tc(id(), "TestUnion1", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestUnion1:1.0";
    }

    public static TestUnion1
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestUnion1 _ob_v = new TestUnion1();
        int _ob_d;
        _ob_d = in.read_long();

        switch(_ob_d)
        {
        case -1:
        {
            int _ob_m;
            _ob_m = in.read_long();
            _ob_v.l(_ob_m);
            break;
        }

        case -2:
        {
            double[][][] _ob_m;
            _ob_m = DoubleArrayHelper.read(in);
            _ob_v.a(_ob_m);
            break;
        }

        case -3:
        {
            String _ob_m;
            _ob_m = in.read_string();
            _ob_v.s(_ob_m);
            break;
        }

        case 0:
        case 1:
        case 2:
        case 3:
        case -4:
        case -5:
        {
            TestStruct2 _ob_m;
            _ob_m = TestStruct2Helper.read(in);
            _ob_v.str(_ob_d, _ob_m);
            break;
        }

        default:
        {
            org.omg.CORBA.TypeCode _ob_m;
            _ob_m = in.read_TypeCode();
            _ob_v.tc(_ob_d, _ob_m);
            break;
        }
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestUnion1 val)
    {
        int _ob_d = val.discriminator();
        out.write_long(_ob_d);

        switch(_ob_d)
        {
        case -1:
        {
            int _ob_m = val.l();
            out.write_long(_ob_m);
            break;
        }

        case -2:
        {
            double[][][] _ob_m = val.a();
            DoubleArrayHelper.write(out, _ob_m);
            break;
        }

        case -3:
        {
            String _ob_m = val.s();
            out.write_string(_ob_m);
            break;
        }

        case 0:
        case 1:
        case 2:
        case 3:
        case -4:
        case -5:
        {
            TestStruct2 _ob_m = val.str();
            TestStruct2Helper.write(out, _ob_m);
            break;
        }

        default:
        {
            org.omg.CORBA.TypeCode _ob_m = val.tc();
            out.write_TypeCode(_ob_m);
            break;
        }
        }
    }
}

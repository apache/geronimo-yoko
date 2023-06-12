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
package test.obv;

//
// IDL:TestVarUnion:1.0
//
final public class TestVarUnionHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TestVarUnion val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestVarUnion
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
            org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[2];

            members[0] = new org.omg.CORBA.UnionMember();
            members[0].name = "s";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);
            members[0].label = orb.create_any();
            members[0].label.insert_long((int)(0L));

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "fs";
            members[1].type = TestFixStructHelper.type();
            members[1].label = orb.create_any();
            members[1].label.insert_long((int)(9L));

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
            typeCode_ = orb.create_union_tc(id(), "TestVarUnion", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestVarUnion:1.0";
    }

    public static TestVarUnion
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestVarUnion _ob_v = new TestVarUnion();
        int _ob_d;
        _ob_d = in.read_long();

        switch(_ob_d)
        {
        case 0:
        {
            String _ob_m;
            _ob_m = in.read_string();
            _ob_v.s(_ob_m);
            break;
        }

        case 9:
        {
            TestFixStruct _ob_m;
            _ob_m = TestFixStructHelper.read(in);
            _ob_v.fs(_ob_m);
            break;
        }

        default:
            _ob_v.__default(_ob_d);
            break;
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestVarUnion val)
    {
        int _ob_d = val.discriminator();
        out.write_long(_ob_d);

        switch(_ob_d)
        {
        case 0:
        {
            String _ob_m = val.s();
            out.write_string(_ob_m);
            break;
        }

        case 9:
        {
            TestFixStruct _ob_m = val.fs();
            TestFixStructHelper.write(out, _ob_m);
            break;
        }

        default:
            break;
        }
    }
}

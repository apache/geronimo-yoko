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
// IDL:test/types/DynAnyTypes/TestUnion2:1.0
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
            org.omg.CORBA.UnionMember[] members = new org.omg.CORBA.UnionMember[1];

            members[0] = new org.omg.CORBA.UnionMember();
            members[0].name = "a";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
            members[0].label = orb.create_any();
            members[0].label.insert_boolean(true);

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);
            typeCode_ = orb.create_union_tc(id(), "TestUnion2", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:test/types/DynAnyTypes/TestUnion2:1.0";
    }

    public static TestUnion2
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestUnion2 _ob_v = new TestUnion2();
        boolean _ob_d;
        _ob_d = in.read_boolean();

        switch(_ob_d ? 1 : 0)
        {
        case 1:
        {
            int _ob_m;
            _ob_m = in.read_long();
            _ob_v.a(_ob_m);
            break;
        }

        default:
            _ob_v.__default(_ob_d);
            break;
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestUnion2 val)
    {
        boolean _ob_d = val.discriminator();
        out.write_boolean(_ob_d);

        switch(_ob_d ? 1 : 0)
        {
        case 1:
        {
            int _ob_m = val.a();
            out.write_long(_ob_m);
            break;
        }

        default:
            break;
        }
    }
}

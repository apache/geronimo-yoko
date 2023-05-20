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
// IDL:ORBTest_Basic/FixedUnion:1.0
//
final public class FixedUnionHelper
{
    public static void
    insert(org.omg.CORBA.Any any, FixedUnion val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static FixedUnion
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
            members[0].name = "s";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
            members[0].label = orb.create_any();
            members[0].label.insert_ushort((short)(0L));

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "l";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);
            members[1].label = orb.create_any();
            members[1].label.insert_ushort((short)(1L));

            members[2] = new org.omg.CORBA.UnionMember();
            members[2].name = "st";
            members[2].type = FixedStructHelper.type();
            members[2].label = orb.create_any();
            members[2].label.insert_ushort((short)(3L));

            members[3] = new org.omg.CORBA.UnionMember();
            members[3].name = "b";
            members[3].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);
            members[3].label = orb.create_any();
            members[3].label.insert_octet((byte)0);

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ushort);
            typeCode_ = orb.create_union_tc(id(), "FixedUnion", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:ORBTest_Basic/FixedUnion:1.0";
    }

    public static FixedUnion
    read(org.omg.CORBA.portable.InputStream in)
    {
        FixedUnion _ob_v = new FixedUnion();
        short _ob_d;
        _ob_d = in.read_ushort();

        switch(_ob_d)
        {
        case (short)0:
        {
            short _ob_m;
            _ob_m = in.read_short();
            _ob_v.s(_ob_m);
            break;
        }

        case (short)1:
        {
            int _ob_m;
            _ob_m = in.read_long();
            _ob_v.l(_ob_m);
            break;
        }

        case (short)3:
        {
            FixedStruct _ob_m;
            _ob_m = FixedStructHelper.read(in);
            _ob_v.st(_ob_m);
            break;
        }

        default:
        {
            boolean _ob_m;
            _ob_m = in.read_boolean();
            _ob_v.b(_ob_d, _ob_m);
            break;
        }
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, FixedUnion val)
    {
        short _ob_d = val.discriminator();
        out.write_ushort(_ob_d);

        switch(_ob_d)
        {
        case (short)0:
        {
            short _ob_m = val.s();
            out.write_short(_ob_m);
            break;
        }

        case (short)1:
        {
            int _ob_m = val.l();
            out.write_long(_ob_m);
            break;
        }

        case (short)3:
        {
            FixedStruct _ob_m = val.st();
            FixedStructHelper.write(out, _ob_m);
            break;
        }

        default:
        {
            boolean _ob_m = val.b();
            out.write_boolean(_ob_m);
            break;
        }
        }
    }
}

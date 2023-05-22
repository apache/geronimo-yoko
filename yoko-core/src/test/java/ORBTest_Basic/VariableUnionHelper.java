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
// IDL:ORBTest_Basic/VariableUnion:1.0
//
final public class VariableUnionHelper
{
    public static void
    insert(org.omg.CORBA.Any any, VariableUnion val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static VariableUnion
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
            members[0].name = "ti";
            members[0].type = IntfHelper.type();
            members[0].label = orb.create_any();
            members[0].label.insert_boolean(false);

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "st";
            members[1].type = VariableStructHelper.type();
            members[1].label = orb.create_any();
            members[1].label.insert_boolean(true);

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);
            typeCode_ = orb.create_union_tc(id(), "VariableUnion", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:ORBTest_Basic/VariableUnion:1.0";
    }

    public static VariableUnion
    read(org.omg.CORBA.portable.InputStream in)
    {
        VariableUnion _ob_v = new VariableUnion();
        boolean _ob_d;
        _ob_d = in.read_boolean();

        switch(_ob_d ? 1 : 0)
        {
        case 0:
        {
            Intf _ob_m;
            _ob_m = IntfHelper.read(in);
            _ob_v.ti(_ob_m);
            break;
        }

        case 1:
        {
            VariableStruct _ob_m;
            _ob_m = VariableStructHelper.read(in);
            _ob_v.st(_ob_m);
            break;
        }
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, VariableUnion val)
    {
        boolean _ob_d = val.discriminator();
        out.write_boolean(_ob_d);

        switch(_ob_d ? 1 : 0)
        {
        case 0:
        {
            Intf _ob_m = val.ti();
            IntfHelper.write(out, _ob_m);
            break;
        }

        case 1:
        {
            VariableStruct _ob_m = val.st();
            VariableStructHelper.write(out, _ob_m);
            break;
        }
        }
    }
}

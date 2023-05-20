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
package test.obv.TestOBVColoPackage;

//
// IDL:TestOBVColo/UV:1.0
//
final public class UVHelper
{
    public static void
    insert(org.omg.CORBA.Any any, UV val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static UV
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
            members[0].name = "str";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);
            members[0].label = orb.create_any();
            members[0].label.insert_boolean(true);

            members[1] = new org.omg.CORBA.UnionMember();
            members[1].name = "val";
            members[1].type = test.obv.TestValueHelper.type();
            members[1].label = orb.create_any();
            members[1].label.insert_boolean(false);

            org.omg.CORBA.TypeCode discType = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);
            typeCode_ = orb.create_union_tc(id(), "UV", discType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestOBVColo/UV:1.0";
    }

    public static UV
    read(org.omg.CORBA.portable.InputStream in)
    {
        UV _ob_v = new UV();
        boolean _ob_d;
        _ob_d = in.read_boolean();

        switch(_ob_d ? 1 : 0)
        {
        case 1:
        {
            String _ob_m;
            _ob_m = in.read_string();
            _ob_v.str(_ob_m);
            break;
        }

        case 0:
        {
            test.obv.TestValue _ob_m;
            _ob_m = test.obv.TestValueHelper.read(in);
            _ob_v.val(_ob_m);
            break;
        }
        }

        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, UV val)
    {
        boolean _ob_d = val.discriminator();
        out.write_boolean(_ob_d);

        switch(_ob_d ? 1 : 0)
        {
        case 1:
        {
            String _ob_m = val.str();
            out.write_string(_ob_m);
            break;
        }

        case 0:
        {
            test.obv.TestValue _ob_m = val.val();
            test.obv.TestValueHelper.write(out, _ob_m);
            break;
        }
        }
    }
}

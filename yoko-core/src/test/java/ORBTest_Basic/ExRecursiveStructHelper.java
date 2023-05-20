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
// IDL:ORBTest_Basic/ExRecursiveStruct:1.0
//
final public class ExRecursiveStructHelper
{
    public static void
    insert(org.omg.CORBA.Any any, ExRecursiveStruct val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static ExRecursiveStruct
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "us";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ushort);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "rs";
            members[1].type = RecursiveStructHelper.type();

            typeCode_ = orb.create_exception_tc(id(), "ExRecursiveStruct", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:ORBTest_Basic/ExRecursiveStruct:1.0";
    }

    public static ExRecursiveStruct
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!id().equals(in.read_string()))
            throw new org.omg.CORBA.MARSHAL();

        ExRecursiveStruct _ob_v = new ExRecursiveStruct();
        _ob_v.us = in.read_ushort();
        _ob_v.rs = RecursiveStructHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, ExRecursiveStruct val)
    {
        out.write_string(id());
        out.write_ushort(val.us);
        RecursiveStructHelper.write(out, val.rs);
    }
}

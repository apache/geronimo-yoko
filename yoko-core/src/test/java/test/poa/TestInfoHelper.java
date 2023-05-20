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
package test.poa;

//
// IDL:TestInfo:1.0
//
final public class TestInfoHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TestInfo val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestInfo
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
            members[0].name = "obj";
            members[0].type = TestHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "except_id";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            typeCode_ = orb.create_struct_tc(id(), "TestInfo", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestInfo:1.0";
    }

    public static TestInfo
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestInfo _ob_v = new TestInfo();
        _ob_v.obj = TestHelper.read(in);
        _ob_v.except_id = in.read_string();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestInfo val)
    {
        TestHelper.write(out, val.obj);
        out.write_string(val.except_id);
    }
}

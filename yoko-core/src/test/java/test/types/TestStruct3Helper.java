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
// IDL:TestStruct3:1.0
//
final public class TestStruct3Helper
{
    public static void
    insert(org.omg.CORBA.Any any, TestStruct3 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestStruct3
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
            members[0].name = "l";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "seq";
            org.omg.CORBA.TypeCode content0;
            content0 = orb.create_recursive_tc(id());
            members[1].type = orb.create_sequence_tc(0, content0);

            typeCode_ = orb.create_struct_tc(id(), "TestStruct3", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestStruct3:1.0";
    }

    public static TestStruct3
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestStruct3 _ob_v = new TestStruct3();
        _ob_v.l = in.read_long();
        int len0 = in.read_ulong();
        _ob_v.seq = new TestStruct3[len0];
        for(int i0 = 0; i0 < len0; i0++)
            _ob_v.seq[i0] = TestStruct3Helper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestStruct3 val)
    {
        out.write_long(val.l);
        int len0 = val.seq.length;
        out.write_ulong(len0);
        for(int i0 = 0; i0 < len0; i0++)
            TestStruct3Helper.write(out, val.seq[i0]);
    }
}

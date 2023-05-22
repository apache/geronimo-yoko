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
// IDL:TestStruct2:1.0
//
final public class TestStruct2Helper
{
    public static void
    insert(org.omg.CORBA.Any any, TestStruct2 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestStruct2
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[4];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "s";
            members[0].type = TestStruct1Helper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "a";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_any);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "da";
            members[2].type = DoubleArrayHelper.type();

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "sa";
            members[3].type = orb.create_array_tc(100, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string));

            typeCode_ = orb.create_struct_tc(id(), "TestStruct2", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestStruct2:1.0";
    }

    public static TestStruct2
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestStruct2 _ob_v = new TestStruct2();
        _ob_v.s = TestStruct1Helper.read(in);
        _ob_v.a = in.read_any();
        _ob_v.da = DoubleArrayHelper.read(in);
        int len0 = 100;
        _ob_v.sa = new String[len0];
        for(int i0 = 0; i0 < len0; i0++)
            _ob_v.sa[i0] = in.read_string();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestStruct2 val)
    {
        TestStruct1Helper.write(out, val.s);
        out.write_any(val.a);
        DoubleArrayHelper.write(out, val.da);
        int len0 = val.sa.length;
        if(len0 != 100)
             throw new org.omg.CORBA.MARSHAL();
        for(int i0 = 0; i0 < len0; i0++)
            out.write_string(val.sa[i0]);
    }
}

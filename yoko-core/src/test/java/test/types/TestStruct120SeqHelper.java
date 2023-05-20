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
// IDL:TestStruct120Seq:1.0
//
final public class TestStruct120SeqHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TestStruct1[] val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestStruct1[]
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
            typeCode_ = orb.create_alias_tc(id(), "TestStruct120Seq", orb.create_sequence_tc(20, TestStruct1Helper.type()));
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestStruct120Seq:1.0";
    }

    public static TestStruct1[]
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestStruct1[] _ob_v;
        int len0 = in.read_ulong();
        _ob_v = new TestStruct1[len0];
        for(int i0 = 0; i0 < len0; i0++)
            _ob_v[i0] = TestStruct1Helper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestStruct1[] val)
    {
        int len0 = val.length;
        if(len0 > 20)
             throw new org.omg.CORBA.MARSHAL();
        out.write_ulong(len0);
        for(int i0 = 0; i0 < len0; i0++)
            TestStruct1Helper.write(out, val[i0]);
    }
}

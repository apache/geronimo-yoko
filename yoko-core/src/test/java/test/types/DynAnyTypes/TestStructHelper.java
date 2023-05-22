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
// IDL:test/types/DynAnyTypes/TestStruct:1.0
//
final public class TestStructHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TestStruct val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TestStruct
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[17];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "shortVal";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "ushortVal";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ushort);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "longVal";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_long);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "ulongVal";
            members[3].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "floatVal";
            members[4].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_float);

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "doubleVal";
            members[5].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_double);

            members[6] = new org.omg.CORBA.StructMember();
            members[6].name = "boolVal";
            members[6].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[7] = new org.omg.CORBA.StructMember();
            members[7].name = "charVal";
            members[7].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char);

            members[8] = new org.omg.CORBA.StructMember();
            members[8].name = "octetVal";
            members[8].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            members[9] = new org.omg.CORBA.StructMember();
            members[9].name = "anyVal";
            members[9].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_any);

            members[10] = new org.omg.CORBA.StructMember();
            members[10].name = "tcVal";
            members[10].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_TypeCode);

            members[11] = new org.omg.CORBA.StructMember();
            members[11].name = "objectVal";
            members[11].type = orb.create_interface_tc("IDL:omg.org/CORBA/Object:1.0", "Object");

            members[12] = new org.omg.CORBA.StructMember();
            members[12].name = "stringVal";
            members[12].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[13] = new org.omg.CORBA.StructMember();
            members[13].name = "longlongVal";
            members[13].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_longlong);

            members[14] = new org.omg.CORBA.StructMember();
            members[14].name = "ulonglongVal";
            members[14].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulonglong);

            members[15] = new org.omg.CORBA.StructMember();
            members[15].name = "wcharVal";
            members[15].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_wchar);

            members[16] = new org.omg.CORBA.StructMember();
            members[16].name = "wstringVal";
            members[16].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_wstring);

            typeCode_ = orb.create_struct_tc(id(), "TestStruct", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:test/types/DynAnyTypes/TestStruct:1.0";
    }

    public static TestStruct
    read(org.omg.CORBA.portable.InputStream in)
    {
        TestStruct _ob_v = new TestStruct();
        _ob_v.shortVal = in.read_short();
        _ob_v.ushortVal = in.read_ushort();
        _ob_v.longVal = in.read_long();
        _ob_v.ulongVal = in.read_ulong();
        _ob_v.floatVal = in.read_float();
        _ob_v.doubleVal = in.read_double();
        _ob_v.boolVal = in.read_boolean();
        _ob_v.charVal = in.read_char();
        _ob_v.octetVal = in.read_octet();
        _ob_v.anyVal = in.read_any();
        _ob_v.tcVal = in.read_TypeCode();
        _ob_v.objectVal = in.read_Object();
        _ob_v.stringVal = in.read_string();
        _ob_v.longlongVal = in.read_longlong();
        _ob_v.ulonglongVal = in.read_ulonglong();
        _ob_v.wcharVal = in.read_wchar();
        _ob_v.wstringVal = in.read_wstring();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TestStruct val)
    {
        out.write_short(val.shortVal);
        out.write_ushort(val.ushortVal);
        out.write_long(val.longVal);
        out.write_ulong(val.ulongVal);
        out.write_float(val.floatVal);
        out.write_double(val.doubleVal);
        out.write_boolean(val.boolVal);
        out.write_char(val.charVal);
        out.write_octet(val.octetVal);
        out.write_any(val.anyVal);
        out.write_TypeCode(val.tcVal);
        out.write_Object(val.objectVal);
        out.write_string(val.stringVal);
        out.write_longlong(val.longlongVal);
        out.write_ulonglong(val.ulonglongVal);
        out.write_wchar(val.wcharVal);
        out.write_wstring(val.wstringVal);
    }
}

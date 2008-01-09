/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.omg.GIOP;

//
// IDL:omg.org/GIOP/RequestHeader_1_0:1.0
//
final public class RequestHeader_1_0Helper
{
    public static void
    insert(org.omg.CORBA.Any any, RequestHeader_1_0 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static RequestHeader_1_0
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[6];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "service_context";
            members[0].type = org.omg.IOP.ServiceContextListHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "request_id";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "response_expected";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "object_key";
            members[3].type = orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet));

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "operation";
            members[4].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "requesting_principal";
            members[5].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_Principal);

            typeCode_ = orb.create_struct_tc(id(), "RequestHeader_1_0", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/GIOP/RequestHeader_1_0:1.0";
    }

    public static RequestHeader_1_0
    read(org.omg.CORBA.portable.InputStream in)
    {
        RequestHeader_1_0 _ob_v = new RequestHeader_1_0();
        _ob_v.service_context = org.omg.IOP.ServiceContextListHelper.read(in);
        _ob_v.request_id = in.read_ulong();
        _ob_v.response_expected = in.read_boolean();
        int len0 = in.read_ulong();
        _ob_v.object_key = new byte[len0];
        in.read_octet_array(_ob_v.object_key, 0, len0);
        _ob_v.operation = in.read_string();
        _ob_v.requesting_principal = in.read_Principal();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, RequestHeader_1_0 val)
    {
        org.omg.IOP.ServiceContextListHelper.write(out, val.service_context);
        out.write_ulong(val.request_id);
        out.write_boolean(val.response_expected);
        int len0 = val.object_key.length;
        out.write_ulong(len0);
        out.write_octet_array(val.object_key, 0, len0);
        out.write_string(val.operation);
        out.write_Principal(val.requesting_principal);
    }
}

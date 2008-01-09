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
// IDL:omg.org/GIOP/RequestHeader_1_2:1.0
//
final public class RequestHeader_1_2Helper
{
    public static void
    insert(org.omg.CORBA.Any any, RequestHeader_1_2 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static RequestHeader_1_2
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
            members[0].name = "request_id";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "response_flags";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "reserved";
            members[2].type = orb.create_array_tc(3, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet));

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "target";
            members[3].type = TargetAddressHelper.type();

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "operation";
            members[4].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "service_context";
            members[5].type = org.omg.IOP.ServiceContextListHelper.type();

            typeCode_ = orb.create_struct_tc(id(), "RequestHeader_1_2", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/GIOP/RequestHeader_1_2:1.0";
    }

    public static RequestHeader_1_2
    read(org.omg.CORBA.portable.InputStream in)
    {
        RequestHeader_1_2 _ob_v = new RequestHeader_1_2();
        _ob_v.request_id = in.read_ulong();
        _ob_v.response_flags = in.read_octet();
        int len0 = 3;
        _ob_v.reserved = new byte[len0];
        in.read_octet_array(_ob_v.reserved, 0, len0);
        _ob_v.target = TargetAddressHelper.read(in);
        _ob_v.operation = in.read_string();
        _ob_v.service_context = org.omg.IOP.ServiceContextListHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, RequestHeader_1_2 val)
    {
        out.write_ulong(val.request_id);
        out.write_octet(val.response_flags);
        int len0 = val.reserved.length;
        if(len0 != 3)
             throw new org.omg.CORBA.MARSHAL();
        out.write_octet_array(val.reserved, 0, len0);
        TargetAddressHelper.write(out, val.target);
        out.write_string(val.operation);
        org.omg.IOP.ServiceContextListHelper.write(out, val.service_context);
    }
}

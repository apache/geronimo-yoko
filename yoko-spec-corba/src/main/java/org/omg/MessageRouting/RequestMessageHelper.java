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

package org.omg.MessageRouting;

//
// IDL:omg.org/MessageRouting/RequestMessage:1.0
//
final public class RequestMessageHelper
{
    public static void
    insert(org.omg.CORBA.Any any, RequestMessage val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static RequestMessage
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[7];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "giop_version";
            members[0].type = org.omg.GIOP.VersionHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "service_contexts";
            members[1].type = org.omg.IOP.ServiceContextListHelper.type();

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "response_flags";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "reserved";
            members[3].type = orb.create_array_tc(3, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet));

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "object_key";
            members[4].type = orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet));

            members[5] = new org.omg.CORBA.StructMember();
            members[5].name = "operation";
            members[5].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[6] = new org.omg.CORBA.StructMember();
            members[6].name = "body";
            members[6].type = MessageBodyHelper.type();

            typeCode_ = orb.create_struct_tc(id(), "RequestMessage", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/MessageRouting/RequestMessage:1.0";
    }

    public static RequestMessage
    read(org.omg.CORBA.portable.InputStream in)
    {
        RequestMessage _ob_v = new RequestMessage();
        _ob_v.giop_version = org.omg.GIOP.VersionHelper.read(in);
        _ob_v.service_contexts = org.omg.IOP.ServiceContextListHelper.read(in);
        _ob_v.response_flags = in.read_octet();
        int len0 = 3;
        _ob_v.reserved = new byte[len0];
        in.read_octet_array(_ob_v.reserved, 0, len0);
        int len1 = in.read_ulong();
        _ob_v.object_key = new byte[len1];
        in.read_octet_array(_ob_v.object_key, 0, len1);
        _ob_v.operation = in.read_string();
        _ob_v.body = MessageBodyHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, RequestMessage val)
    {
        org.omg.GIOP.VersionHelper.write(out, val.giop_version);
        org.omg.IOP.ServiceContextListHelper.write(out, val.service_contexts);
        out.write_octet(val.response_flags);
        int len0 = val.reserved.length;
        if(len0 != 3)
             throw new org.omg.CORBA.MARSHAL();
        out.write_octet_array(val.reserved, 0, len0);
        int len1 = val.object_key.length;
        out.write_ulong(len1);
        out.write_octet_array(val.object_key, 0, len1);
        out.write_string(val.operation);
        MessageBodyHelper.write(out, val.body);
    }
}

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
// IDL:omg.org/GIOP/LocateReplyHeader_1_2:1.0
//
final public class LocateReplyHeader_1_2Helper
{
    public static void
    insert(org.omg.CORBA.Any any, LocateReplyHeader_1_2 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static LocateReplyHeader_1_2
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
            members[0].name = "request_id";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "locate_status";
            members[1].type = LocateStatusType_1_2Helper.type();

            typeCode_ = orb.create_struct_tc(id(), "LocateReplyHeader_1_2", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/GIOP/LocateReplyHeader_1_2:1.0";
    }

    public static LocateReplyHeader_1_2
    read(org.omg.CORBA.portable.InputStream in)
    {
        LocateReplyHeader_1_2 _ob_v = new LocateReplyHeader_1_2();
        _ob_v.request_id = in.read_ulong();
        _ob_v.locate_status = LocateStatusType_1_2Helper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, LocateReplyHeader_1_2 val)
    {
        out.write_ulong(val.request_id);
        LocateStatusType_1_2Helper.write(out, val.locate_status);
    }
}

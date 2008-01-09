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
// IDL:omg.org/GIOP/MessageHeader_1_1:1.0
//
final public class MessageHeader_1_1Helper
{
    public static void
    insert(org.omg.CORBA.Any any, MessageHeader_1_1 val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static MessageHeader_1_1
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[5];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "magic";
            members[0].type = orb.create_array_tc(4, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_char));

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "GIOP_version";
            members[1].type = VersionHelper.type();

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "flags";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            members[3] = new org.omg.CORBA.StructMember();
            members[3].name = "message_type";
            members[3].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            members[4] = new org.omg.CORBA.StructMember();
            members[4].name = "message_size";
            members[4].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);

            typeCode_ = orb.create_struct_tc(id(), "MessageHeader_1_1", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/GIOP/MessageHeader_1_1:1.0";
    }

    public static MessageHeader_1_1
    read(org.omg.CORBA.portable.InputStream in)
    {
        MessageHeader_1_1 _ob_v = new MessageHeader_1_1();
        int len0 = 4;
        _ob_v.magic = new char[len0];
        in.read_char_array(_ob_v.magic, 0, len0);
        _ob_v.GIOP_version = VersionHelper.read(in);
        _ob_v.flags = in.read_octet();
        _ob_v.message_type = in.read_octet();
        _ob_v.message_size = in.read_ulong();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, MessageHeader_1_1 val)
    {
        int len0 = val.magic.length;
        if(len0 != 4)
             throw new org.omg.CORBA.MARSHAL();
        out.write_char_array(val.magic, 0, len0);
        VersionHelper.write(out, val.GIOP_version);
        out.write_octet(val.flags);
        out.write_octet(val.message_type);
        out.write_ulong(val.message_size);
    }
}

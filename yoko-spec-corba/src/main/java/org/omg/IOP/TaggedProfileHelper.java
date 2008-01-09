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

package org.omg.IOP;

//
// IDL:omg.org/IOP/TaggedProfile:1.0
//
public class TaggedProfileHelper
{
    public static void
    insert(org.omg.CORBA.Any any, TaggedProfile val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TaggedProfile
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
            members[0].name = "tag";
            members[0].type = ProfileIdHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "profile_data";
            members[1].type = orb.create_sequence_tc(0, orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet));

            typeCode_ = orb.create_struct_tc(id(), "TaggedProfile", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/IOP/TaggedProfile:1.0";
    }

    public static TaggedProfile
    read(org.omg.CORBA.portable.InputStream in)
    {
        TaggedProfile _ob_v = new TaggedProfile();
        _ob_v.tag = ProfileIdHelper.read(in);
        int len0 = in.read_ulong();
        _ob_v.profile_data = new byte[len0];
        in.read_octet_array(_ob_v.profile_data, 0, len0);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, TaggedProfile val)
    {
        ProfileIdHelper.write(out, val.tag);
        int len0 = val.profile_data.length;
        out.write_ulong(len0);
        out.write_octet_array(val.profile_data, 0, len0);
    }
}

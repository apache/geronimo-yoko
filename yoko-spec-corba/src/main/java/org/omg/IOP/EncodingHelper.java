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
// IDL:omg.org/IOP/Encoding:1.0
//
public class EncodingHelper
{
    public static void
    insert(org.omg.CORBA.Any any, Encoding val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static Encoding
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
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[3];

            members[0] = new org.omg.CORBA.StructMember();
            members[0].name = "format";
            members[0].type = EncodingFormatHelper.type();

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "major_version";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "minor_version";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_octet);

            typeCode_ = orb.create_struct_tc(id(), "Encoding", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/IOP/Encoding:1.0";
    }

    public static Encoding
    read(org.omg.CORBA.portable.InputStream in)
    {
        Encoding _ob_v = new Encoding();
        _ob_v.format = EncodingFormatHelper.read(in);
        _ob_v.major_version = in.read_octet();
        _ob_v.minor_version = in.read_octet();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, Encoding val)
    {
        EncodingFormatHelper.write(out, val.format);
        out.write_octet(val.major_version);
        out.write_octet(val.minor_version);
    }
}

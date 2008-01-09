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
// IDL:omg.org/GIOP/SystemExceptionReplyBody:1.0
//
final public class SystemExceptionReplyBodyHelper
{
    public static void
    insert(org.omg.CORBA.Any any, SystemExceptionReplyBody val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static SystemExceptionReplyBody
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
            members[0].name = "exception_id";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_string);

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "minor_code_value";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);

            members[2] = new org.omg.CORBA.StructMember();
            members[2].name = "completion_status";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);

            typeCode_ = orb.create_struct_tc(id(), "SystemExceptionReplyBody", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/GIOP/SystemExceptionReplyBody:1.0";
    }

    public static SystemExceptionReplyBody
    read(org.omg.CORBA.portable.InputStream in)
    {
        SystemExceptionReplyBody _ob_v = new SystemExceptionReplyBody();
        _ob_v.exception_id = in.read_string();
        _ob_v.minor_code_value = in.read_ulong();
        _ob_v.completion_status = in.read_ulong();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, SystemExceptionReplyBody val)
    {
        out.write_string(val.exception_id);
        out.write_ulong(val.minor_code_value);
        out.write_ulong(val.completion_status);
    }
}

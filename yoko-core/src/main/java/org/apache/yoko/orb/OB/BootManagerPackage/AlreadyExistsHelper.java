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

package org.apache.yoko.orb.OB.BootManagerPackage;

//
// IDL:orb.yoko.apache.org/OB/BootManager/AlreadyExists:1.0
//
final public class AlreadyExistsHelper
{
    public static void
    insert(org.omg.CORBA.Any any, AlreadyExists val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static AlreadyExists
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return read(any.create_input_stream());
        else

            throw new org.omg.CORBA.BAD_OPERATION(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeBadOperation(org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch),
                org.apache.yoko.orb.OB.MinorCodes.MinorTypeMismatch, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[0];

            typeCode_ = orb.create_exception_tc(id(), "AlreadyExists", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:orb.yoko.apache.org/OB/BootManager/AlreadyExists:1.0";
    }

    public static AlreadyExists
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!id().equals(in.read_string())) {
            throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                    .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadIDMismatch), 
                org.apache.yoko.orb.OB.MinorCodes.MinorReadIDMismatch, 
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        AlreadyExists _ob_v = new AlreadyExists();
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, AlreadyExists val)
    {
        out.write_string(id());
    }
}

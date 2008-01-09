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

package org.apache.yoko.orb.OBPortableServer;

//
// IDL:orb.yoko.apache.org/OBPortableServer/SynchronizationPolicyValue:1.0
//
final public class SynchronizationPolicyValueHelper
{
    public static void
    insert(org.omg.CORBA.Any any, SynchronizationPolicyValue val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static SynchronizationPolicyValue
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
            String[] members = new String[3];
            members[0] = "NO_SYNCHRONIZATION";
            members[1] = "SYNCHRONIZE_ON_POA";
            members[2] = "SYNCHRONIZE_ON_ORB";
            typeCode_ = orb.create_enum_tc(id(), "SynchronizationPolicyValue", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:orb.yoko.apache.org/OBPortableServer/SynchronizationPolicyValue:1.0";
    }

    public static SynchronizationPolicyValue
    read(org.omg.CORBA.portable.InputStream in)
    {
        SynchronizationPolicyValue _ob_v;
        _ob_v = SynchronizationPolicyValue.from_int(in.read_ulong());
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, SynchronizationPolicyValue val)
    {
        out.write_ulong(val.value());
    }
}

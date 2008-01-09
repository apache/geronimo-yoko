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
// IDL:omg.org/MessageRouting/UnlimitedPing:1.0
//
final public class UnlimitedPingHelper
{
    public static void
    insert(org.omg.CORBA.Any any, UnlimitedPing val)
    {
        any.insert_Value(val, type());
    }

    public static UnlimitedPing
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            java.io.Serializable _ob_v = any.extract_Value();
            if(_ob_v == null || _ob_v instanceof UnlimitedPing)
                return (UnlimitedPing)_ob_v;
        }

        throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode
    type()
    {
        if(typeCode_ == null)
        {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            org.omg.CORBA.ValueMember[] members = new org.omg.CORBA.ValueMember[3];

            members[0] = new org.omg.CORBA.ValueMember();
            members[0].name = "max_backoffs";
            members[0].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_short);
            members[0].access = org.omg.CORBA.PUBLIC_MEMBER.value;

            members[1] = new org.omg.CORBA.ValueMember();
            members[1].name = "backoff_factor";
            members[1].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_float);
            members[1].access = org.omg.CORBA.PUBLIC_MEMBER.value;

            members[2] = new org.omg.CORBA.ValueMember();
            members[2].name = "base_interval_seconds";
            members[2].type = orb.get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong);
            members[2].access = org.omg.CORBA.PUBLIC_MEMBER.value;

            org.omg.CORBA.TypeCode baseType = RetryPolicyHelper.type();

            typeCode_ = orb.create_value_tc(id(), "UnlimitedPing", org.omg.CORBA.VM_NONE.value, baseType, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/MessageRouting/UnlimitedPing:1.0";
    }

    public static UnlimitedPing
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        return (UnlimitedPing)((org.omg.CORBA_2_3.portable.InputStream)in).read_value(id());
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, UnlimitedPing val)
    {
        if(!(out instanceof org.omg.CORBA_2_3.portable.OutputStream))
            throw new org.omg.CORBA.BAD_PARAM();
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_value(val, id());
    }
}

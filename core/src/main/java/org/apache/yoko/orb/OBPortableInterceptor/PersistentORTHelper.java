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

package org.apache.yoko.orb.OBPortableInterceptor;

//
// IDL:orb.yoko.apache.org/OBPortableInterceptor/PersistentORT:1.0
//
final public class PersistentORTHelper
{
    public static void
    insert(org.omg.CORBA.Any any, PersistentORT val)
    {
        any.insert_Value(val, type());
    }

    public static PersistentORT
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            java.io.Serializable _ob_v = any.extract_Value();
            if(_ob_v == null || _ob_v instanceof PersistentORT)
                return (PersistentORT)_ob_v;
        }


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
            org.omg.CORBA.ValueMember[] members = new org.omg.CORBA.ValueMember[4];

            members[0] = new org.omg.CORBA.ValueMember();
            members[0].name = "the_server_id";
            members[0].type = org.omg.PortableInterceptor.ServerIdHelper.type();
            members[0].access = org.omg.CORBA.PRIVATE_MEMBER.value;

            members[1] = new org.omg.CORBA.ValueMember();
            members[1].name = "the_orb_id";
            members[1].type = org.omg.PortableInterceptor.ORBIdHelper.type();
            members[1].access = org.omg.CORBA.PRIVATE_MEMBER.value;

            members[2] = new org.omg.CORBA.ValueMember();
            members[2].name = "the_adapter_name";
            members[2].type = org.omg.PortableInterceptor.AdapterNameHelper.type();
            members[2].access = org.omg.CORBA.PRIVATE_MEMBER.value;

            members[3] = new org.omg.CORBA.ValueMember();
            members[3].name = "the_ior_template";
            members[3].type = org.omg.IOP.IORHelper.type();
            members[3].access = org.omg.CORBA.PRIVATE_MEMBER.value;

            typeCode_ = orb.create_value_tc(id(), "PersistentORT", org.omg.CORBA.VM_NONE.value, null, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:orb.yoko.apache.org/OBPortableInterceptor/PersistentORT:1.0";
    }

    public static PersistentORT
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream)) {
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType), 
                org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType, 
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return (PersistentORT)((org.omg.CORBA_2_3.portable.InputStream)in).read_value(id());
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, PersistentORT val)
    {
        if(!(out instanceof org.omg.CORBA_2_3.portable.OutputStream)) {
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType), 
                org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType, 
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        ((org.omg.CORBA_2_3.portable.OutputStream)out).write_value(val, id());
    }
}

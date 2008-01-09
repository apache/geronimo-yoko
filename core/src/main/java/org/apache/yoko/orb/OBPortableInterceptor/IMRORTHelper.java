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
// IDL:orb.yoko.apache.org/OBPortableInterceptor/IMRORT:1.0
//
final public class IMRORTHelper
{
    public static void
    insert(org.omg.CORBA.Any any, IMRORT val)
    {
        any.insert_Value(val, type());
    }

    public static IMRORT
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
        {
            java.io.Serializable _ob_v = any.extract_Value();
            if(_ob_v == null || _ob_v instanceof IMRORT)
                return (IMRORT)_ob_v;
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
            org.omg.CORBA.ValueMember[] members = new org.omg.CORBA.ValueMember[3];

            members[0] = new org.omg.CORBA.ValueMember();
            members[0].name = "the_server_id";
            members[0].type = org.omg.PortableInterceptor.ServerIdHelper.type();
            members[0].access = org.omg.CORBA.PRIVATE_MEMBER.value;

            members[1] = new org.omg.CORBA.ValueMember();
            members[1].name = "the_adapter_name";
            members[1].type = org.omg.PortableInterceptor.AdapterNameHelper.type();
            members[1].access = org.omg.CORBA.PRIVATE_MEMBER.value;

            members[2] = new org.omg.CORBA.ValueMember();
            members[2].name = "the_real_template";
            members[2].type = org.omg.PortableInterceptor.ObjectReferenceTemplateHelper.type();
            members[2].access = org.omg.CORBA.PRIVATE_MEMBER.value;

            typeCode_ = orb.create_value_tc(id(), "IMRORT", org.omg.CORBA.VM_NONE.value, null, members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:orb.yoko.apache.org/OBPortableInterceptor/IMRORT:1.0";
    }

    public static IMRORT
    read(org.omg.CORBA.portable.InputStream in)
    {
        if(!(in instanceof org.omg.CORBA_2_3.portable.InputStream)) {
            throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
                .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType), 
                org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType, 
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return (IMRORT)((org.omg.CORBA_2_3.portable.InputStream)in).read_value(id());
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, IMRORT val)
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

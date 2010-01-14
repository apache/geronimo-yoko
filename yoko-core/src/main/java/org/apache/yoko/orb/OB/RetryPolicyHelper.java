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

package org.apache.yoko.orb.OB;

//
// IDL:orb.yoko.apache.org/OB/RetryPolicy:1.0
//
final public class RetryPolicyHelper
{
    public static void
    insert(org.omg.CORBA.Any any, RetryPolicy val)
    {
        any.insert_Object(val, type());
    }

    public static RetryPolicy
    extract(org.omg.CORBA.Any any)
    {
        if(any.type().equivalent(type()))
            return narrow(any.extract_Object());


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
            typeCode_ = ((org.omg.CORBA_2_4.ORB)orb).create_local_interface_tc(id(), "RetryPolicy");
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:orb.yoko.apache.org/OB/RetryPolicy:1.0";
    }

    public static RetryPolicy
    read(org.omg.CORBA.portable.InputStream in)
    {
        throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorReadUnsupported),
                org.apache.yoko.orb.OB.MinorCodes.MinorReadUnsupported,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, RetryPolicy val)
    {
        throw new org.omg.CORBA.MARSHAL(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeMarshal(org.apache.yoko.orb.OB.MinorCodes.MinorWriteUnsupported),
                org.apache.yoko.orb.OB.MinorCodes.MinorWriteUnsupported,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public static RetryPolicy
    narrow(org.omg.CORBA.Object val)
    {
        try
        {
            return (RetryPolicy)val;
        }
        catch(ClassCastException ex)
        {
        }

        throw new org.omg.CORBA.BAD_PARAM(org.apache.yoko.orb.OB.MinorCodes
            .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType), 
            org.apache.yoko.orb.OB.MinorCodes.MinorIncompatibleObjectType, 
            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }
}

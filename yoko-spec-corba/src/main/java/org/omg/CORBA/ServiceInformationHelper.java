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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/ServiceInformation:1.0
//
public abstract class ServiceInformationHelper
{
    public static void
    insert(org.omg.CORBA.Any any, ServiceInformation val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static ServiceInformation
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
            members[0].name = "service_options";
            members[0].type = orb.create_sequence_tc(0, ServiceOptionHelper.type());

            members[1] = new org.omg.CORBA.StructMember();
            members[1].name = "service_details";
            members[1].type = orb.create_sequence_tc(0, ServiceDetailHelper.type());

            typeCode_ = orb.create_struct_tc(id(), "ServiceInformation", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:omg.org/CORBA/ServiceInformation:1.0";
    }

    public static ServiceInformation
    read(org.omg.CORBA.portable.InputStream in)
    {
        ServiceInformation _ob_v = new ServiceInformation();
        int len0 = in.read_ulong();
        _ob_v.service_options = new int[len0];
        in.read_ulong_array(_ob_v.service_options, 0, len0);
        int len1 = in.read_ulong();
        _ob_v.service_details = new ServiceDetail[len1];
        for(int i1 = 0; i1 < len1; i1++)
            _ob_v.service_details[i1] = ServiceDetailHelper.read(in);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, ServiceInformation val)
    {
        int len0 = val.service_options.length;
        out.write_ulong(len0);
        out.write_ulong_array(val.service_options, 0, len0);
        int len1 = val.service_details.length;
        out.write_ulong(len1);
        for(int i1 = 0; i1 < len1; i1++)
            ServiceDetailHelper.write(out, val.service_details[i1]);
    }
}

/*
 * Copyright 2010 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package test.types;

//
// IDL:Measurement:1.0
//
final public class MeasurementHelper
{
    public static void
    insert(org.omg.CORBA.Any any, Measurement val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static Measurement
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
            String[] members = new String[3];
            members[0] = "FEET";
            members[1] = "METERS";
            members[2] = "FURLONGS";
            typeCode_ = orb.create_enum_tc(id(), "Measurement", members);
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:Measurement:1.0";
    }

    public static Measurement
    read(org.omg.CORBA.portable.InputStream in)
    {
        Measurement _ob_v;
        _ob_v = Measurement.from_int(in.read_ulong());
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, Measurement val)
    {
        out.write_ulong(val.value());
    }
}

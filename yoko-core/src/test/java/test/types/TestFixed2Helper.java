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
// IDL:TestFixed2:1.0
//
final public class TestFixed2Helper
{
    public static void
    insert(org.omg.CORBA.Any any, java.math.BigDecimal val)
    {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static java.math.BigDecimal
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
            typeCode_ = orb.create_alias_tc(id(), "TestFixed2", orb.create_fixed_tc((short)24, (short)8));
        }

        return typeCode_;
    }

    public static String
    id()
    {
        return "IDL:TestFixed2:1.0";
    }

    public static java.math.BigDecimal
    read(org.omg.CORBA.portable.InputStream in)
    {
        java.math.BigDecimal _ob_v;
        _ob_v = in.read_fixed().movePointLeft(8);
        return _ob_v;
    }

    public static void
    write(org.omg.CORBA.portable.OutputStream out, java.math.BigDecimal val)
    {
        out.write_fixed(val.movePointRight(8));
    }
}

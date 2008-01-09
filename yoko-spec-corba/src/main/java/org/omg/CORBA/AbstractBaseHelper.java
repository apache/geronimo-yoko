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

abstract public class AbstractBaseHelper {
    public static void insert(org.omg.CORBA.Any a, java.lang.Object v) {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream();
        write(out, v);
        a.read_value(out.create_input_stream(), type());
    }

    public static java.lang.Object extract(org.omg.CORBA.Any a) {
        if (a.type().equivalent(type()))
            return read(a.create_input_stream());
        else
            throw new org.omg.CORBA.BAD_OPERATION();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode type() {
        if (typeCode_ == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            typeCode_ = orb.create_native_tc(id(), "AbstractBase");
        }

        return typeCode_;
    }

    public static String id() {
        return "IDL:omg.org/CORBA/AbstractBase:1.0";
    }

    public static java.lang.Object read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA_2_3.portable.InputStream in_2_3 = (org.omg.CORBA_2_3.portable.InputStream) in;
        return in_2_3.read_abstract_interface();
    }

    public static void write(org.omg.CORBA.portable.OutputStream out,
            java.lang.Object value) {
        org.omg.CORBA_2_3.portable.OutputStream out_2_3 = (org.omg.CORBA_2_3.portable.OutputStream) out;
        out_2_3.write_abstract_interface(value);
    }
}

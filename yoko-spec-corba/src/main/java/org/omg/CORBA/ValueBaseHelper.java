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

abstract public class ValueBaseHelper {
    public static void insert(org.omg.CORBA.Any a, java.io.Serializable v) {
        a.insert_Value(v, type());
    }

    public static java.io.Serializable extract(org.omg.CORBA.Any a) {
        return a.extract_Value();
    }

    private static org.omg.CORBA.TypeCode typeCode_;

    public static org.omg.CORBA.TypeCode type() {
        if (typeCode_ == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            org.omg.CORBA.ValueMember[] members = new org.omg.CORBA.ValueMember[0];

            typeCode_ = orb.create_value_tc(id(), "ValueBase",
                    org.omg.CORBA.VM_ABSTRACT.value, null, members);
        }

        return typeCode_;
    }

    public static String id() {
        return "IDL:omg.org/CORBA/ValueBase:1.0";
    }

    public static java.io.Serializable read(
            org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA_2_3.portable.InputStream in_2_3 = (org.omg.CORBA_2_3.portable.InputStream) in;
        return in_2_3.read_value(id());
    }

    public static void write(org.omg.CORBA.portable.OutputStream out,
            java.io.Serializable value) {
        org.omg.CORBA_2_3.portable.OutputStream out_2_3 = (org.omg.CORBA_2_3.portable.OutputStream) out;
        out_2_3.write_value(value, id());
    }
}

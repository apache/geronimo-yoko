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

abstract public class ObjectHelper {
    public static void insert(org.omg.CORBA.Any any, org.omg.CORBA.Object obj) {
        any.insert_Object(obj);
    }

    public static org.omg.CORBA.Object extract(Any any) {
        return any.extract_Object();
    }

    public static org.omg.CORBA.TypeCode type() {
        return org.omg.CORBA.ORB.init().get_primitive_tc(TCKind.tk_objref);
    }

    public static String id() {
        return "IDL:omg.org/CORBA/Object:1.0";
    }

    public static org.omg.CORBA.Object read(
            org.omg.CORBA.portable.InputStream in) {
        return in.read_Object();
    }

    public static void write(org.omg.CORBA.portable.OutputStream out,
            org.omg.CORBA.Object val) {
        out.write_Object(val);
    }
}

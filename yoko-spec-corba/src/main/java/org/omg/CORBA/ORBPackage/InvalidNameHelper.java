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

package org.omg.CORBA.ORBPackage;

final public class InvalidNameHelper {
    public static void insert(org.omg.CORBA.Any any,
            org.omg.CORBA.ORBPackage.InvalidName value) {
        throw new org.omg.CORBA.MARSHAL();
    }

    public static org.omg.CORBA.ORBPackage.InvalidName extract(
            org.omg.CORBA.Any any) {
        throw new org.omg.CORBA.MARSHAL();
    }

    public static org.omg.CORBA.TypeCode type() {
        throw new org.omg.CORBA.BAD_OPERATION();
    }

    public static java.lang.String id() {
        return "IDL:omg.org/CORBA/ORB/InvalidName:1.0";
    }

    public static org.omg.CORBA.ORBPackage.InvalidName read(
            org.omg.CORBA.portable.InputStream input) {
        throw new org.omg.CORBA.MARSHAL();
    }

    public static void write(org.omg.CORBA.portable.OutputStream output,
            org.omg.CORBA.ORBPackage.InvalidName value) {
        throw new org.omg.CORBA.MARSHAL();
    }
}

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

final public class TRANSACTION_UNAVAILABLEHelper {
    public static void insert(Any any, TRANSACTION_UNAVAILABLE val) {
        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        write(out, val);
        any.read_value(out.create_input_stream(), type());
    }

    public static TRANSACTION_UNAVAILABLE extract(Any any) {
        if (any.type().equivalent(type()))
            return read(any.create_input_stream());
        else
            throw new BAD_OPERATION();
    }

    private static TypeCode typeCode_;

    public static TypeCode type() {
        if (typeCode_ == null) {
            ORB orb = ORB.init();
            StructMember[] members = new StructMember[2];
            members[0] = new StructMember();
            members[0].name = "minor";
            members[0].type = orb.get_primitive_tc(TCKind.tk_ulong);
            members[1] = new StructMember();
            members[1].name = "completed";
            members[1].type = CompletionStatusHelper.type();

            typeCode_ = orb.create_exception_tc(id(),
                    "TRANSACTION_UNAVAILABLE", members);
        }

        return typeCode_;
    }

    public static String id() {
        return "IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0";
    }

    public static TRANSACTION_UNAVAILABLE read(
            org.omg.CORBA.portable.InputStream in) {
        if (!id().equals(in.read_string()))
            throw new MARSHAL();

        TRANSACTION_UNAVAILABLE val = new TRANSACTION_UNAVAILABLE();
        val.minor = in.read_ulong();
        val.completed = CompletionStatus.from_int(in.read_ulong());
        return val;
    }

    public static void write(org.omg.CORBA.portable.OutputStream out,
            TRANSACTION_UNAVAILABLE val) {
        out.write_string(id());
        out.write_ulong(val.minor);
        out.write_ulong(val.completed.value());
    }
}

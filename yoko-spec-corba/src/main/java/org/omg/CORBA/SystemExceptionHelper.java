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

final public class SystemExceptionHelper {
    final static private int _BAD_CONTEXT = 0;

    final static private int _BAD_INV_ORDER = 1;

    final static private int _BAD_OPERATION = 2;

    final static private int _BAD_PARAM = 3;

    final static private int _BAD_QOS = 4;

    final static private int _BAD_TYPECODE = 5;

    final static private int _CODESET_INCOMPATIBLE = 6;

    final static private int _COMM_FAILURE = 7;

    final static private int _DATA_CONVERSION = 8;

    final static private int _FREE_MEM = 9;

    final static private int _IMP_LIMIT = 10;

    final static private int _INITIALIZE = 11;

    final static private int _INTERNAL = 12;

    final static private int _INTF_REPOS = 13;

    final static private int _INVALID_TRANSACTION = 14;

    final static private int _INV_FLAG = 15;

    final static private int _INV_IDENT = 16;

    final static private int _INV_OBJREF = 17;

    final static private int _INV_POLICY = 18;

    final static private int _MARSHAL = 19;

    final static private int _NO_IMPLEMENT = 20;

    final static private int _NO_MEMORY = 21;

    final static private int _NO_PERMISSION = 22;

    final static private int _NO_RESOURCES = 23;

    final static private int _NO_RESPONSE = 24;

    final static private int _OBJECT_NOT_EXIST = 25;

    final static private int _OBJ_ADAPTER = 26;

    final static private int _PERSIST_STORE = 27;

    final static private int _REBIND = 28;

    final static private int _TIMEOUT = 29;

    final static private int _TRANSACTION_MODE = 30;

    final static private int _TRANSACTION_REQUIRED = 31;

    final static private int _TRANSACTION_ROLLEDBACK = 32;

    final static private int _TRANSACTION_UNAVAILABLE = 33;

    final static private int _TRANSIENT = 34;

    final static private int _UNKNOWN = 35;

    private static String[] classes_ = { "org.omg.CORBA.BAD_CONTEXT",
            "org.omg.CORBA.BAD_INV_ORDER", "org.omg.CORBA.BAD_OPERATION",
            "org.omg.CORBA.BAD_PARAM", "org.omg.CORBA.BAD_QOS",
            "org.omg.CORBA.BAD_TYPECODE", "org.omg.CORBA.CODESET_INCOMPATIBLE",
            "org.omg.CORBA.COMM_FAILURE", "org.omg.CORBA.DATA_CONVERSION",
            "org.omg.CORBA.FREE_MEM", "org.omg.CORBA.IMP_LIMIT",
            "org.omg.CORBA.INITIALIZE", "org.omg.CORBA.INTERNAL",
            "org.omg.CORBA.INTF_REPOS", "org.omg.CORBA.INVALID_TRANSACTION",
            "org.omg.CORBA.INV_FLAG", "org.omg.CORBA.INV_IDENT",
            "org.omg.CORBA.INV_OBJREF", "org.omg.CORBA.INV_POLICY",
            "org.omg.CORBA.MARSHAL", "org.omg.CORBA.NO_IMPLEMENT",
            "org.omg.CORBA.NO_MEMORY", "org.omg.CORBA.NO_PERMISSION",
            "org.omg.CORBA.NO_RESOURCES", "org.omg.CORBA.NO_RESPONSE",
            "org.omg.CORBA.OBJECT_NOT_EXIST", "org.omg.CORBA.OBJ_ADAPTER",
            "org.omg.CORBA.PERSIST_STORE", "org.omg.CORBA.REBIND",
            "org.omg.CORBA.TIMEOUT", "org.omg.CORBA.TRANSACTION_MODE",
            "org.omg.CORBA.TRANSACTION_REQUIRED",
            "org.omg.CORBA.TRANSACTION_ROLLEDBACK",
            "org.omg.CORBA.TRANSACTION_UNAVAILABLE", "org.omg.CORBA.TRANSIENT",
            "org.omg.CORBA.UNKNOWN" };

    private static String[] names_ = { "BAD_CONTEXT", "BAD_INV_ORDER",
            "BAD_OPERATION", "BAD_PARAM", "BAD_QOS", "BAD_TYPECODE",
            "CODESET_INCOMPATIBLE", "COMM_FAILURE", "DATA_CONVERSION",
            "FREE_MEM", "IMP_LIMIT", "INITIALIZE", "INTERNAL", "INTF_REPOS",
            "INVALID_TRANSACTION", "INV_FLAG", "INV_IDENT", "INV_OBJREF",
            "INV_POLICY", "MARSHAL", "NO_IMPLEMENT", "NO_MEMORY",
            "NO_PERMISSION", "NO_RESOURCES", "NO_RESPONSE", "OBJECT_NOT_EXIST",
            "OBJ_ADAPTER", "PERSIST_STORE", "REBIND", "TIMEOUT",
            "TRANSACTION_MODE", "TRANSACTION_REQUIRED",
            "TRANSACTION_ROLLEDBACK", "TRANSACTION_UNAVAILABLE", "TRANSIENT",
            "UNKNOWN" };

    private static String[] ids_ = { "IDL:omg.org/CORBA/BAD_CONTEXT:1.0",
            "IDL:omg.org/CORBA/BAD_INV_ORDER:1.0",
            "IDL:omg.org/CORBA/BAD_OPERATION:1.0",
            "IDL:omg.org/CORBA/BAD_PARAM:1.0", "IDL:omg.org/CORBA/BAD_QOS:1.0",
            "IDL:omg.org/CORBA/BAD_TYPECODE:1.0",
            "IDL:omg.org/CORBA/CODESET_INCOMPATIBLE:1.0",
            "IDL:omg.org/CORBA/COMM_FAILURE:1.0",
            "IDL:omg.org/CORBA/DATA_CONVERSION:1.0",
            "IDL:omg.org/CORBA/FREE_MEM:1.0",
            "IDL:omg.org/CORBA/IMP_LIMIT:1.0",
            "IDL:omg.org/CORBA/INITIALIZE:1.0",
            "IDL:omg.org/CORBA/INTERNAL:1.0",
            "IDL:omg.org/CORBA/INTF_REPOS:1.0",
            "IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0",
            "IDL:omg.org/CORBA/INV_FLAG:1.0",
            "IDL:omg.org/CORBA/INV_IDENT:1.0",
            "IDL:omg.org/CORBA/INV_OBJREF:1.0",
            "IDL:omg.org/CORBA/INV_POLICY:1.0",
            "IDL:omg.org/CORBA/MARSHAL:1.0",
            "IDL:omg.org/CORBA/NO_IMPLEMENT:1.0",
            "IDL:omg.org/CORBA/NO_MEMORY:1.0",
            "IDL:omg.org/CORBA/NO_PERMISSION:1.0",
            "IDL:omg.org/CORBA/NO_RESOURCES:1.0",
            "IDL:omg.org/CORBA/NO_RESPONSE:1.0",
            "IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0",
            "IDL:omg.org/CORBA/OBJ_ADAPTER:1.0",
            "IDL:omg.org/CORBA/PERSIST_STORE:1.0",
            "IDL:omg.org/CORBA/REBIND:1.0", "IDL:omg.org/CORBA/TIMEOUT:1.0",
            "IDL:omg.org/CORBA/TRANSACTION_MODE:1.0",
            "IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0",
            "IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0",
            "IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0",
            "IDL:omg.org/CORBA/TRANSIENT:1.0", "IDL:omg.org/CORBA/UNKNOWN:1.0" };

    private static int binarySearch(String[] arr, String value) {
        int left = 0;
        int right = arr.length;
        int index = -1;

        while (left < right) {
            int m = (left + right) / 2;
            int res = arr[m].compareTo(value);
            if (res == 0) {
                index = m;
                break;
            } else if (res > 0)
                right = m;
            else
                left = m + 1;
        }

        return index;
    }

    private static TypeCode createTypeCode(String id, String name) {
        ORB orb = ORB.init();
        StructMember[] members = new StructMember[2];
        members[0] = new StructMember();
        members[0].name = "minor";
        members[0].type = orb.get_primitive_tc(TCKind.tk_ulong);
        members[1] = new StructMember();
        members[1].name = "completed";
        members[1].type = CompletionStatusHelper.type();
        return orb.create_exception_tc(id, name, members);
    }

    private static void writeImpl(org.omg.CORBA.portable.OutputStream out,
            SystemException val, String id) {
        out.write_string(id);
        out.write_ulong(val.minor);
        out.write_ulong(val.completed.value());
    }

    public static void insert(Any any, SystemException val) {
        String className = val.getClass().getName();
        int index = binarySearch(classes_, className);

        String id;
        if (index == -1)
            id = ids_[_UNKNOWN];
        else
            id = ids_[index];

        org.omg.CORBA.portable.OutputStream out = any.create_output_stream();
        writeImpl(out, val, id);
        any.read_value(out.create_input_stream(), createTypeCode(id,
                names_[index]));
    }

    public static SystemException extract(Any any) {
        try {
            TypeCode tc = any.type();
            String id = tc.id();
            if (tc.kind() == TCKind.tk_except
                    && (id.length() == 0 || binarySearch(ids_, id) != -1)) {
                return read(any.create_input_stream());
            }
        } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
        }

        throw new BAD_OPERATION();
    }

    private static TypeCode typeCode_;

    public static TypeCode type() {
        if (typeCode_ == null)
            typeCode_ = createTypeCode(id(), "SystemException");

        return typeCode_;
    }

    public static String id() {
        return "IDL:omg.org/CORBA/SystemException:1.0";
    }

    public static SystemException read(org.omg.CORBA.portable.InputStream in) {
        String id = in.read_string();
        int minor = in.read_ulong();
        org.omg.CORBA.CompletionStatus status = org.omg.CORBA.CompletionStatus
                .from_int(in.read_ulong());

        int n = binarySearch(ids_, id);
        switch (n) {
        case _BAD_CONTEXT:
            return new BAD_CONTEXT(minor, status);
        case _BAD_INV_ORDER:
            return new BAD_INV_ORDER(minor, status);
        case _BAD_OPERATION:
            return new BAD_OPERATION(minor, status);
        case _BAD_PARAM:
            return new BAD_PARAM(minor, status);
        case _BAD_QOS:
            return new BAD_QOS(minor, status);
        case _BAD_TYPECODE:
            return new BAD_TYPECODE(minor, status);
        case _CODESET_INCOMPATIBLE:
            return new CODESET_INCOMPATIBLE(minor, status);
        case _COMM_FAILURE:
            return new COMM_FAILURE(minor, status);
        case _DATA_CONVERSION:
            return new DATA_CONVERSION(minor, status);
        case _FREE_MEM:
            return new FREE_MEM(minor, status);
        case _IMP_LIMIT:
            return new IMP_LIMIT(minor, status);
        case _INITIALIZE:
            return new INITIALIZE(minor, status);
        case _INTERNAL:
            return new INTERNAL(minor, status);
        case _INTF_REPOS:
            return new INTF_REPOS(minor, status);
        case _INVALID_TRANSACTION:
            return new INVALID_TRANSACTION(minor, status);
        case _INV_FLAG:
            return new INV_FLAG(minor, status);
        case _INV_IDENT:
            return new INV_IDENT(minor, status);
        case _INV_OBJREF:
            return new INV_OBJREF(minor, status);
        case _INV_POLICY:
            return new INV_POLICY(minor, status);
        case _MARSHAL:
            return new MARSHAL(minor, status);
        case _NO_IMPLEMENT:
            return new NO_IMPLEMENT(minor, status);
        case _NO_MEMORY:
            return new NO_MEMORY(minor, status);
        case _NO_PERMISSION:
            return new NO_PERMISSION(minor, status);
        case _NO_RESOURCES:
            return new NO_RESOURCES(minor, status);
        case _NO_RESPONSE:
            return new NO_RESPONSE(minor, status);
        case _OBJECT_NOT_EXIST:
            return new OBJECT_NOT_EXIST(minor, status);
        case _OBJ_ADAPTER:
            return new OBJ_ADAPTER(minor, status);
        case _PERSIST_STORE:
            return new PERSIST_STORE(minor, status);
        case _REBIND:
            return new REBIND(minor, status);
        case _TIMEOUT:
            return new TIMEOUT(minor, status);
        case _TRANSACTION_MODE:
            return new TRANSACTION_MODE(minor, status);
        case _TRANSACTION_REQUIRED:
            return new TRANSACTION_REQUIRED(minor, status);
        case _TRANSACTION_ROLLEDBACK:
            return new TRANSACTION_ROLLEDBACK(minor, status);
        case _TRANSACTION_UNAVAILABLE:
            return new TRANSACTION_UNAVAILABLE(minor, status);
        case _TRANSIENT:
            return new TRANSIENT(minor, status);
        case _UNKNOWN:
        default:
            return new UNKNOWN(minor, status);
        }
    }

    public static void write(org.omg.CORBA.portable.OutputStream out,
            SystemException val) {
        String className = val.getClass().getName();
        int index = binarySearch(classes_, className);

        String id;
        if (index == -1)
            id = ids_[_UNKNOWN];
        else
            id = ids_[index];

        writeImpl(out, val, id);
    }
}

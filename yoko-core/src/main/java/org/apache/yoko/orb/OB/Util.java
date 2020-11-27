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

import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.osgi.ProviderLocator;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_CONTEXT;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_QOS;
import org.omg.CORBA.BAD_TYPECODE;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.FREE_MEM;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INTF_REPOS;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.INV_FLAG;
import org.omg.CORBA.INV_IDENT;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NO_MEMORY;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.NO_RESOURCES;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.PERSIST_STORE;
import org.omg.CORBA.REBIND;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TIMEOUT;
import org.omg.CORBA.TRANSACTION_MODE;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.TRANSACTION_UNAVAILABLE;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.SendingContextRunTime;
import org.omg.IOP.ServiceContext;
import org.omg.SendingContext.CodeBase;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

public final class Util {
    static final Logger logger = Logger.getLogger(Util.class.getName());
    // Print octets to stream
    public static void printOctets(PrintStream out, byte[] oct, int offset, int length) {
        final int inc = 8;

        for (int i = offset; i < offset + length; i += inc) {
            for (int j = i; j - i < inc; j++) {
                if (j < offset + length) {
                    int n = (int) oct[j];
                    if (n < 0)
                        n += 256;
                    String s;
                    if (n < 10)
                        s = "  " + n;
                    else if (n < 100)
                        s = " " + n;
                    else
                        s = "" + n;
                    out.print(s + " ");
                } else
                    out.print("    ");
            }

            out.print('"');

            for (int j = i; j < offset + length && j - i < inc; j++) {
                if (oct[j] >= (byte) 32 && oct[j] < (byte) 127)
                    out.print((char) oct[j]);
                else
                    out.print('.');
            }

            out.println('"');
        }
    }

    // Copy a system exception
    public static SystemException copySystemException(SystemException ex) {
        SystemException result;
        try {
            Class c = ex.getClass();
            Class[] paramTypes = { String.class };
            Constructor constr = c.getConstructor(paramTypes);
            Object[] initArgs = { ex.getMessage() };
            result = (SystemException) constr.newInstance(initArgs);
            result.minor = ex.minor;
            result.completed = ex.completed;
            result.initCause(ex);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException | InstantiationException e) {
            throw Assert.fail(ex);
        }
        return result;
    }

    // Unmarshal a system exception
    public static SystemException unmarshalSystemException(InputStream in) {
        String id = in.read_string();
        int minor = in.read_ulong();
        CompletionStatus status = CompletionStatus.from_int(in.read_ulong());

        switch (id) {
        case "IDL:omg.org/CORBA/BAD_PARAM:1.0": {
            String reason = MinorCodes.describeBadParam(minor);
            return new BAD_PARAM(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/NO_MEMORY:1.0": {
            String reason = MinorCodes.describeNoMemory(minor);
            return new NO_MEMORY(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/IMP_LIMIT:1.0": {
            String reason = MinorCodes.describeImpLimit(minor);
            return new IMP_LIMIT(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/COMM_FAILURE:1.0": {
            String reason = MinorCodes.describeCommFailure(minor);
            return new COMM_FAILURE(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/INV_OBJREF:1.0":
            return new INV_OBJREF(minor, status);
        case "IDL:omg.org/CORBA/NO_PERMISSION:1.0":
            return new NO_PERMISSION(minor, status);
        case "IDL:omg.org/CORBA/INTERNAL:1.0":
            return new INTERNAL(minor, status);
        case "IDL:omg.org/CORBA/MARSHAL:1.0": {
            String reason = MinorCodes.describeMarshal(minor);
            return new MARSHAL(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/INITIALIZE:1.0": {
            String reason = MinorCodes.describeInitialize(minor);
            return new INITIALIZE(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/NO_IMPLEMENT:1.0": {
            String reason = MinorCodes.describeNoImplement(minor);
            return new NO_IMPLEMENT(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/BAD_TYPECODE:1.0":
            return new BAD_TYPECODE(minor, status);
        case "IDL:omg.org/CORBA/BAD_OPERATION:1.0":
            return new BAD_OPERATION(minor, status);
        case "IDL:omg.org/CORBA/NO_RESOURCES:1.0": {
            String reason = MinorCodes.describeNoResources(minor);
            return new NO_RESOURCES(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/NO_RESPONSE:1.0":
            return new NO_RESPONSE(minor, status);
        case "IDL:omg.org/CORBA/PERSIST_STORE:1.0":
            return new PERSIST_STORE(minor, status);
        case "IDL:omg.org/CORBA/BAD_INV_ORDER:1.0": {
            String reason = MinorCodes.describeBadInvOrder(minor);
            return new BAD_INV_ORDER(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/TRANSIENT:1.0": {
            return Transients.create(minor, status);
        }
        case "IDL:omg.org/CORBA/FREE_MEM:1.0":
            return new FREE_MEM(minor, status);
        case "IDL:omg.org/CORBA/INV_IDENT:1.0":
            return new INV_IDENT(minor, status);
        case "IDL:omg.org/CORBA/INV_FLAG:1.0":
            return new INV_FLAG(minor, status);
        case "IDL:omg.org/CORBA/INTF_REPOS:1.0": {
            String reason = MinorCodes.describeIntfRepos(minor);
            return new INTF_REPOS(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/BAD_CONTEXT:1.0":
            return new BAD_CONTEXT(minor, status);
        case "IDL:omg.org/CORBA/OBJ_ADAPTER:1.0":
            return new OBJ_ADAPTER(minor, status);
        case "IDL:omg.org/CORBA/DATA_CONVERSION:1.0":
            return new DATA_CONVERSION(minor, status);
        case "IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0": {
            String reason = MinorCodes.describeObjectNotExist(minor);
            return new OBJECT_NOT_EXIST(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0":
            return new TRANSACTION_REQUIRED(minor, status);
        case "IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0":
            return new TRANSACTION_ROLLEDBACK(minor, status);
        case "IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0":
            return new INVALID_TRANSACTION(minor, status);
        case "IDL:omg.org/CORBA/INV_POLICY:1.0": {
            String reason = MinorCodes.describeInvPolicy(minor);
            return new INV_POLICY(reason, minor, status);
        }
        case "IDL:omg.org/CORBA/CODESET_INCOMPATIBLE:1.0":
            return new CODESET_INCOMPATIBLE(minor, status);
        case "IDL:omg.org/CORBA/REBIND:1.0":
            return new REBIND(minor, status);
        case "IDL:omg.org/CORBA/TIMEOUT:1.0":
            return new TIMEOUT(minor, status);
        case "IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0":
            return new TRANSACTION_UNAVAILABLE(minor, status);
        case "IDL:omg.org/CORBA/TRANSACTION_MODE:1.0":
            return new TRANSACTION_MODE(minor, status);
        case "IDL:omg.org/CORBA/BAD_QOS:1.0":
            return new BAD_QOS(minor, status);
        }

        // Unknown exception
        String reason = MinorCodes.describeUnknown(minor);
        return new UNKNOWN(reason, minor, status);
    }

    // Marshal a system exception
    public static void marshalSystemException(OutputStream out, SystemException ex) {
        out.write_string(getExceptionId(ex));
        out.write_ulong(ex.minor);
        out.write_ulong(ex.completed.value());
    }

    private static final String[] sysExClassNames_ = {"org.omg.CORBA.BAD_CONTEXT",
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

    private static final String[] sysExIds_ = { "IDL:omg.org/CORBA/BAD_CONTEXT:1.0",
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

    // Determine if the repository ID represents a system exception
    public static boolean isSystemException(String id) {
        return (binarySearch(sysExIds_, id) != -1);
    }

    // Determine the repository ID of an exception
    public static String getExceptionId(Exception ex) {
        if (ex instanceof SystemException) {
            String className = ex.getClass().getName();
            int index = binarySearch(sysExClassNames_, className);

            if (index == -1)
                return "IDL:omg.org/CORBA/UNKNOWN:1.0";
            else
                return sysExIds_[index];
        } else if (ex instanceof UserException) {
            Class exClass = ex.getClass();
            String className = exClass.getName();
            String id = null;
            try {

                Class c = ProviderLocator.loadClass(className + "Helper", exClass, null);
                Method m = c.getMethod("id");
                id = (String) m.invoke(null, new Object[0]);
            } catch (ClassNotFoundException | SecurityException ignored) {
            } catch (NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
                throw Assert.fail(ex);
            }

            //
            // TODO: Is this correct?
            //
            if (id == null)
                return "IDL:omg.org/CORBA/UserException:1.0";
            else
                return id;
        } else {
            throw Assert.fail(ex);
        }
    }

    public static Any insertException(Any any, Exception ex) {
        // Find the helper class for the exception and use it to insert the exception into the any
        try {
            Class exClass = ex.getClass();
            String helper = exClass.getName() + "Helper";
            // get the appropriate class for the loading.
            Class c = ProviderLocator.loadClass(helper, exClass, Thread.currentThread().getContextClassLoader());
            final Class[] paramTypes = { Any.class, exClass };
            Method m = c.getMethod("insert", paramTypes);
            final Object[] args = { any, ex };
            m.invoke(null, args);
        } catch (ClassNotFoundException | SecurityException ignored) {
        } catch (NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            throw Assert.fail(ex);
        }
        return any;
    }

    static ClassLoader getContextClassLoader () {
        if (System.getSecurityManager() == null) return Thread.currentThread().getContextClassLoader ();
        return AccessController.doPrivileged(
                new PrivilegedAction<ClassLoader>() {
                    public ClassLoader run() {
                        return Thread.currentThread ().getContextClassLoader ();
                    }
                });
    }

    public static CodeBase getSendingContextRuntime(ORBInstance orbInstance_, ServiceContexts contexts) {
        ServiceContext serviceContext = contexts.get(SendingContextRunTime.value);
        return serviceContext == null ? null : new CodeBaseProxy(orbInstance_, serviceContext);
    }
}

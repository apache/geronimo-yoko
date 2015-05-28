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

import org.apache.yoko.util.osgi.ProviderLocator;
import org.omg.IOP.ServiceContext;
import org.omg.SendingContext.CodeBase;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

public final class Util {
    static final Logger logger = Logger.getLogger(Util.class.getName());
    //
    // Print octets to stream
    //
    public static void printOctets(java.io.PrintStream out, byte[] oct,
            int offset, int length) {
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

    //
    // Copy a system exception
    //
    public static org.omg.CORBA.SystemException copySystemException(
            org.omg.CORBA.SystemException ex) {
        org.omg.CORBA.SystemException result = null;

        try {
            Class c = ex.getClass();
            Class[] paramTypes = { String.class };
            java.lang.reflect.Constructor constr = c.getConstructor(paramTypes);
            Object[] initArgs = { ex.getMessage() };
            result = (org.omg.CORBA.SystemException) constr
                    .newInstance(initArgs);
            result.minor = ex.minor;
            result.completed = ex.completed;
        } catch (NoSuchMethodException e) {
            Assert._OB_assert(ex);
        } catch (InstantiationException e) {
            Assert._OB_assert(ex);
        } catch (IllegalAccessException e) {
            Assert._OB_assert(ex);
        } catch (IllegalArgumentException e) {
            Assert._OB_assert(ex);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Assert._OB_assert(ex);
        }

        return result;
    }

    //
    // Unmarshal a system exception
    //
    public static org.omg.CORBA.SystemException unmarshalSystemException(
            org.omg.CORBA.portable.InputStream in) {
        String id = in.read_string();
        int minor = in.read_ulong();
        org.omg.CORBA.CompletionStatus status = org.omg.CORBA.CompletionStatus
                .from_int(in.read_ulong());

        if (id.equals("IDL:omg.org/CORBA/BAD_PARAM:1.0")) {
            String reason = MinorCodes.describeBadParam(minor);
            return new org.omg.CORBA.BAD_PARAM(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/NO_MEMORY:1.0")) {
            String reason = MinorCodes.describeNoMemory(minor);
            return new org.omg.CORBA.NO_MEMORY(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/IMP_LIMIT:1.0")) {
            String reason = MinorCodes.describeImpLimit(minor);
            return new org.omg.CORBA.IMP_LIMIT(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/COMM_FAILURE:1.0")) {
            String reason = MinorCodes.describeCommFailure(minor);
            return new org.omg.CORBA.COMM_FAILURE(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INV_OBJREF:1.0")) {
            return new org.omg.CORBA.INV_OBJREF(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/NO_PERMISSION:1.0")) {
            return new org.omg.CORBA.NO_PERMISSION(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INTERNAL:1.0")) {
            return new org.omg.CORBA.INTERNAL(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/MARSHAL:1.0")) {
            String reason = MinorCodes.describeMarshal(minor);
            return new org.omg.CORBA.MARSHAL(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INITIALIZE:1.0")) {
            String reason = MinorCodes.describeInitialize(minor);
            return new org.omg.CORBA.INITIALIZE(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/NO_IMPLEMENT:1.0")) {
            String reason = MinorCodes.describeNoImplement(minor);
            return new org.omg.CORBA.NO_IMPLEMENT(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/BAD_TYPECODE:1.0")) {
            return new org.omg.CORBA.BAD_TYPECODE(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/BAD_OPERATION:1.0")) {
            return new org.omg.CORBA.BAD_OPERATION(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/NO_RESOURCES:1.0")) {
            String reason = MinorCodes.describeNoResources(minor);
            return new org.omg.CORBA.NO_RESOURCES(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/NO_RESPONSE:1.0")) {
            return new org.omg.CORBA.NO_RESPONSE(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/PERSIST_STORE:1.0")) {
            return new org.omg.CORBA.PERSIST_STORE(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/BAD_INV_ORDER:1.0")) {
            String reason = MinorCodes.describeBadInvOrder(minor);
            return new org.omg.CORBA.BAD_INV_ORDER(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/TRANSIENT:1.0")) {
            String reason = MinorCodes.describeTransient(minor);
            return new org.omg.CORBA.TRANSIENT(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/FREE_MEM:1.0")) {
            return new org.omg.CORBA.FREE_MEM(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INV_IDENT:1.0")) {
            return new org.omg.CORBA.INV_IDENT(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INV_FLAG:1.0")) {
            return new org.omg.CORBA.INV_FLAG(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INTF_REPOS:1.0")) {
            String reason = MinorCodes.describeIntfRepos(minor);
            return new org.omg.CORBA.INTF_REPOS(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/BAD_CONTEXT:1.0")) {
            return new org.omg.CORBA.BAD_CONTEXT(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/OBJ_ADAPTER:1.0")) {
            return new org.omg.CORBA.OBJ_ADAPTER(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/DATA_CONVERSION:1.0")) {
            return new org.omg.CORBA.DATA_CONVERSION(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/OBJECT_NOT_EXIST:1.0")) {
            String reason = MinorCodes.describeObjectNotExist(minor);
            return new org.omg.CORBA.OBJECT_NOT_EXIST(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/TRANSACTION_REQUIRED:1.0")) {
            return new org.omg.CORBA.TRANSACTION_REQUIRED(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/TRANSACTION_ROLLEDBACK:1.0")) {
            return new org.omg.CORBA.TRANSACTION_ROLLEDBACK(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INVALID_TRANSACTION:1.0")) {
            return new org.omg.CORBA.INVALID_TRANSACTION(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/INV_POLICY:1.0")) {
            String reason = MinorCodes.describeInvPolicy(minor);
            return new org.omg.CORBA.INV_POLICY(reason, minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/CODESET_INCOMPATIBLE:1.0")) {
            return new org.omg.CORBA.CODESET_INCOMPATIBLE(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/REBIND:1.0")) {
            return new org.omg.CORBA.REBIND(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/TIMEOUT:1.0")) {
            return new org.omg.CORBA.TIMEOUT(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/TRANSACTION_UNAVAILABLE:1.0")) {
            return new org.omg.CORBA.TRANSACTION_UNAVAILABLE(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/TRANSACTION_MODE:1.0")) {
            return new org.omg.CORBA.TRANSACTION_MODE(minor, status);
        } else if (id.equals("IDL:omg.org/CORBA/BAD_QOS:1.0")) {
            return new org.omg.CORBA.BAD_QOS(minor, status);
        }

        //
        // Unknown exception
        //
        String reason = MinorCodes.describeUnknown(minor);
        return new org.omg.CORBA.UNKNOWN(reason, minor, status);
    }

    //
    // Marshal a system exception
    //
    public static void marshalSystemException(
            org.omg.CORBA.portable.OutputStream out,
            org.omg.CORBA.SystemException ex) {
        out.write_string(getExceptionId(ex));
        out.write_ulong(ex.minor);
        out.write_ulong(ex.completed.value());
    }

    private static String[] sysExClassNames_ = { "org.omg.CORBA.BAD_CONTEXT",
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

    private static String[] sysExIds_ = { "IDL:omg.org/CORBA/BAD_CONTEXT:1.0",
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

    //
    // Determine if the repository ID represents a system exception
    //
    public static boolean isSystemException(String id) {
        return (binarySearch(sysExIds_, id) != -1);
    }

    //
    // Determine the repository ID of an exception
    //
    public static String getExceptionId(Exception ex) {
        if (ex instanceof org.omg.CORBA.SystemException) {
            String className = ex.getClass().getName();
            int index = binarySearch(sysExClassNames_, className);

            if (index == -1)
                return "IDL:omg.org/CORBA/UNKNOWN:1.0";
            else
                return sysExIds_[index];
        } else if (ex instanceof org.omg.CORBA.UserException) {
            Class exClass = ex.getClass();
            String className = exClass.getName();
            String id = null;
            try {

                Class c = ProviderLocator.loadClass(className + "Helper", exClass, null);
                java.lang.reflect.Method m = c.getMethod("id", new Class[0]);
                id = (String) m.invoke(null, new Object[0]);
            } catch (ClassNotFoundException e) {
            } catch (NoSuchMethodException e) {
                Assert._OB_assert(ex);
            } catch (IllegalAccessException e) {
                Assert._OB_assert(ex);
            } catch (IllegalArgumentException e) {
                Assert._OB_assert(ex);
            } catch (java.lang.reflect.InvocationTargetException e) {
                Assert._OB_assert(ex);
            } catch (SecurityException e) {
            }

            //
            // TODO: Is this correct?
            //
            if (id == null)
                return "IDL:omg.org/CORBA/UserException:1.0";
            else
                return id;
        } else {
            Assert._OB_assert(ex);
            return null; // needed by compiler
        }
    }

    public static void insertException(org.omg.CORBA.Any any,
            java.lang.Exception ex) {
        //
        // Find the helper class for the exception and use it to insert
        // the exception into the any
        //

        try {
            Class exClass = ex.getClass();
            String helper = exClass.getName() + "Helper";
            // get the appropriate class for the loading.
        	Class c = ProviderLocator.loadClass(helper, exClass, Thread.currentThread().getContextClassLoader());
            final Class[] paramTypes = { org.omg.CORBA.Any.class, exClass };
            java.lang.reflect.Method m = c.getMethod("insert", paramTypes);
            final java.lang.Object[] args = { any, ex };
            m.invoke(null, args);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
            Assert._OB_assert(ex);
        } catch (IllegalAccessException e) {
            Assert._OB_assert(ex);
        } catch (IllegalArgumentException e) {
            Assert._OB_assert(ex);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Assert._OB_assert(ex);
        } catch (SecurityException e) {
        }
    }

    static ClassLoader getContextClassLoader ()
    {
        if (System.getSecurityManager() == null) {
                return Thread.currentThread ().getContextClassLoader ();
        } else {
        return (ClassLoader)
        AccessController.doPrivileged(
        new PrivilegedAction() {
                public Object run() {
                                return Thread.currentThread ().getContextClassLoader ();
                }
        });
        }
    }

	public static CodeBase getSendingContextRuntime(ORBInstance orbInstance_, ServiceContext[] scl) {
        for(int i = 0 ; i < scl.length ; i++)
        {
            if(scl[i].context_id == org.omg.IOP.SendingContextRunTime.value)
                {
                    return new CodeBaseProxy(orbInstance_, scl[i]);
                }
        }

        return null;
	}
}

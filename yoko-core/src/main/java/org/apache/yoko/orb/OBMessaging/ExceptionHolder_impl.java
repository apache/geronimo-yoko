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

package org.apache.yoko.orb.OBMessaging;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OCI.Buffer;
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
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.FREE_MEM;
import org.omg.CORBA.IMP_LIMIT;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.INTF_REPOS;
import org.omg.CORBA.INVALID_TRANSACTION;
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
import org.omg.CORBA.SystemExceptionHelper;
import org.omg.CORBA.TIMEOUT;
import org.omg.CORBA.TRANSACTION_MODE;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.TRANSACTION_UNAVAILABLE;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.ValueBase;
import org.omg.Messaging._ExceptionHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.apache.yoko.orb.OCI.GiopVersion.GIOP1_2;

public class ExceptionHolder_impl extends _ExceptionHolder {
    //
    // The user exception proxy
    //
    private UserExceptionRaiseProxy raiseProxy_;

    //
    // Raise the exception
    //
    public void raise_exception() throws Exception
    // throws org.omg.CORBA.UserException
    {
        if (is_system_exception) {
            InputStream in = _OB_inputStream();
            SystemException sysEx = SystemExceptionHelper
                    .read(in);

            try {
                if (sysEx.getClass().getName().equals("org.omg.CORBA.UNKNOWN")) {
                    UNKNOWN ex = (UNKNOWN) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_PARAM")) {
                    BAD_PARAM ex = (BAD_PARAM) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_MEMORY")) {
                    NO_MEMORY ex = (NO_MEMORY) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.IMP_LIMIT")) {
                    IMP_LIMIT ex = (IMP_LIMIT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.COMM_FAILURE")) {
                    COMM_FAILURE ex = (COMM_FAILURE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INV_OBJREF")) {
                    INV_OBJREF ex = (INV_OBJREF) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_PERMISSION")) {
                    NO_PERMISSION ex = (NO_PERMISSION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INTERNAL")) {
                    INTERNAL ex = (INTERNAL) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.MARSHAL")) {
                    MARSHAL ex = (MARSHAL) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INITIALIZE")) {
                    INITIALIZE ex = (INITIALIZE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_IMPLEMENT")) {
                    NO_IMPLEMENT ex = (NO_IMPLEMENT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_TYPECODE")) {
                    BAD_TYPECODE ex = (BAD_TYPECODE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_OPERATION")) {
                    BAD_OPERATION ex = (BAD_OPERATION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_RESOURCES")) {
                    NO_RESOURCES ex = (NO_RESOURCES) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_RESPONSE")) {
                    NO_RESPONSE ex = (NO_RESPONSE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.PERSIST_STORE")) {
                    PERSIST_STORE ex = (PERSIST_STORE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_INV_ORDER")) {
                    BAD_INV_ORDER ex = (BAD_INV_ORDER) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSIENT")) {
                    TRANSIENT ex = (TRANSIENT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.FREE_MEM")) {
                    FREE_MEM ex = (FREE_MEM) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INV_IDENT")) {
                    INV_IDENT ex = (INV_IDENT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INTF_REPOS")) {
                    INTF_REPOS ex = (INTF_REPOS) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_CONTEXT")) {
                    BAD_CONTEXT ex = (BAD_CONTEXT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.OBJ_ADAPTER")) {
                    OBJ_ADAPTER ex = (OBJ_ADAPTER) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.DATA_CONVERSION")) {
                    DATA_CONVERSION ex = (DATA_CONVERSION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.OBJECT_NOT_EXIST")) {
                    OBJECT_NOT_EXIST ex = (OBJECT_NOT_EXIST) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_REQUIRED")) {
                    TRANSACTION_REQUIRED ex = (TRANSACTION_REQUIRED) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_ROLLEDBACK")) {
                    TRANSACTION_ROLLEDBACK ex = (TRANSACTION_ROLLEDBACK) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INVALID_TRANSACTION")) {
                    INVALID_TRANSACTION ex = (INVALID_TRANSACTION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INV_POLICY")) {
                    INV_POLICY ex = (INV_POLICY) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.CODESET_INCOMPATIBLE")) {
                    CODESET_INCOMPATIBLE ex = (CODESET_INCOMPATIBLE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.REBIND")) {
                    REBIND ex = (REBIND) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TIMEOUT")) {
                    TIMEOUT ex = (TIMEOUT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_UNAVAILABLE")) {
                    TRANSACTION_UNAVAILABLE ex = (TRANSACTION_UNAVAILABLE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_MODE")) {
                    TRANSACTION_MODE ex = (TRANSACTION_MODE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_QOS")) {
                    BAD_QOS ex = (BAD_QOS) sysEx;
                    throw ex;
                }
            } catch (SystemException ex) {
                throw ex;
            }
        }

        if (raiseProxy_ != null)
            raiseProxy_.raise(this);

        throw new UNKNOWN();
    }

    //
    // Raise the exception with a list of possible exceptions
    //
    public void raise_exception_with_list(TypeCode[] exc_list)
            throws Exception {
        try {
            this.raise_exception();
        } catch (Exception ex) {
            //
            // This should work for all our exception types (System and
            // User)
            //
            Any any = new org.apache.yoko.orb.CORBA.Any();

            Class exClass = ex.getClass();
            String className = exClass.getName();
            try {
                //
                // Get the helper class and the insert method with
                // appropriate parameter types
                //
                // get the appropriate class for the loading.
                ClassLoader loader = exClass.getClassLoader();
                Class c = ProviderLocator.loadClass(className + "Helper", exClass);
                Class[] paramTypes = new Class[2];
                paramTypes[0] = Any.class;
                paramTypes[1] = exClass;
                Method m = c.getMethod("insert", paramTypes);

                //
                // Build up the parameter list
                //
                Object[] parameters = new Object[2];
                parameters[0] = any;
                parameters[1] = ex;

                //
                // No object is needed since this is a static method
                // call
                //
                m.invoke(null, parameters);
            } catch (ClassNotFoundException e) {
                //
                // REVISIT:
                // This just means that we probably caught a non-CORBA
                // exception. What do we want to do with it?
                //
                return;
            } catch (NoSuchMethodException e) {
                Assert._OB_assert(ex);
            } catch (IllegalAccessException e) {
                Assert._OB_assert(ex);
            } catch (IllegalArgumentException e) {
                Assert._OB_assert(ex);
            } catch (InvocationTargetException e) {
                Assert._OB_assert(ex);
            } catch (SecurityException e) {
                return;
            }

            //
            // Check against typecodes
            //
            for (int i = 0; i < exc_list.length; ++i) {
                if (any.type().equal(exc_list[i]))
                    throw ex;
            }
        }
    }

    //
    // from ValueBase
    //
    public ValueBase _copy_value()
            throws SystemException {
        ExceptionHolder_impl copy = new ExceptionHolder_impl();

        //
        // Copy data members
        //
        copy.is_system_exception = is_system_exception;
        copy.byte_order = byte_order;
        if (marshaled_exception != null) {
            copy.marshaled_exception = new byte[marshaled_exception.length];
            System.arraycopy(marshaled_exception, 0, copy.marshaled_exception,
                    0, marshaled_exception.length);
        }

        //
        // Copy the raiser class
        //
        if (raiseProxy_ != null) {
            copy.raiseProxy_ = raiseProxy_;
        }

        return copy;
    }

    //
    // Copy the contents of the stream into the internal octet sequence
    //
    public void _OB_extractSequence(Buffer buf) {
        Assert._OB_assert(buf != null);
        marshaled_exception = buf.copyRemainingBytes();
    }

    //
    // Obtain an input stream from the marshalled exception sequence. This
    // is used for unmarshalling the exception.
    //
    public InputStream _OB_inputStream() {
        Buffer buf = new Buffer(marshaled_exception);
        return new InputStream(buf, 0, false, null, GIOP1_2);
    }

    //
    // Register the class used to raise any exceptions. This class will
    // take ownership of that raiser object.
    //
    public void _OB_register_raise_proxy(UserExceptionRaiseProxy proxy) {
        Assert._OB_assert(proxy != null);
        Assert._OB_assert(raiseProxy_ == null);
        raiseProxy_ = proxy;
    }

    //
    // ExceptionHolder_impl constructors
    //
    public ExceptionHolder_impl() {
        byte_order = false;
        is_system_exception = false;
        marshaled_exception = null;
        raiseProxy_ = null;
    }

    public ExceptionHolder_impl(boolean border) {
        byte_order = border;
        is_system_exception = false;
        marshaled_exception = null;
        raiseProxy_ = null;
    }

    public ExceptionHolder_impl(boolean border, boolean sys_except) {
        byte_order = border;
        is_system_exception = sys_except;
        marshaled_exception = null;
        raiseProxy_ = null;
    }
}

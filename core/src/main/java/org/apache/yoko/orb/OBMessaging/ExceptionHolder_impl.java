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
import org.omg.CORBA.Any;

public class ExceptionHolder_impl extends org.omg.Messaging._ExceptionHolder {
    //
    // The user exception proxy
    //
    private org.apache.yoko.orb.OBMessaging.UserExceptionRaiseProxy raiseProxy_;

    //
    // Raise the exception
    //
    public void raise_exception() throws java.lang.Exception
    // throws org.omg.CORBA.UserException
    {
        if (is_system_exception) {
            org.apache.yoko.orb.CORBA.InputStream in = _OB_inputStream();
            org.omg.CORBA.SystemException sysEx = org.omg.CORBA.SystemExceptionHelper
                    .read(in);

            try {
                if (sysEx.getClass().getName().equals("org.omg.CORBA.UNKNOWN")) {
                    org.omg.CORBA.UNKNOWN ex = (org.omg.CORBA.UNKNOWN) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_PARAM")) {
                    org.omg.CORBA.BAD_PARAM ex = (org.omg.CORBA.BAD_PARAM) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_MEMORY")) {
                    org.omg.CORBA.NO_MEMORY ex = (org.omg.CORBA.NO_MEMORY) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.IMP_LIMIT")) {
                    org.omg.CORBA.IMP_LIMIT ex = (org.omg.CORBA.IMP_LIMIT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.COMM_FAILURE")) {
                    org.omg.CORBA.COMM_FAILURE ex = (org.omg.CORBA.COMM_FAILURE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INV_OBJREF")) {
                    org.omg.CORBA.INV_OBJREF ex = (org.omg.CORBA.INV_OBJREF) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_PERMISSION")) {
                    org.omg.CORBA.NO_PERMISSION ex = (org.omg.CORBA.NO_PERMISSION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INTERNAL")) {
                    org.omg.CORBA.INTERNAL ex = (org.omg.CORBA.INTERNAL) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.MARSHAL")) {
                    org.omg.CORBA.MARSHAL ex = (org.omg.CORBA.MARSHAL) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INITIALIZE")) {
                    org.omg.CORBA.INITIALIZE ex = (org.omg.CORBA.INITIALIZE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_IMPLEMENT")) {
                    org.omg.CORBA.NO_IMPLEMENT ex = (org.omg.CORBA.NO_IMPLEMENT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_TYPECODE")) {
                    org.omg.CORBA.BAD_TYPECODE ex = (org.omg.CORBA.BAD_TYPECODE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_OPERATION")) {
                    org.omg.CORBA.BAD_OPERATION ex = (org.omg.CORBA.BAD_OPERATION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_RESOURCES")) {
                    org.omg.CORBA.NO_RESOURCES ex = (org.omg.CORBA.NO_RESOURCES) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.NO_RESPONSE")) {
                    org.omg.CORBA.NO_RESPONSE ex = (org.omg.CORBA.NO_RESPONSE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.PERSIST_STORE")) {
                    org.omg.CORBA.PERSIST_STORE ex = (org.omg.CORBA.PERSIST_STORE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_INV_ORDER")) {
                    org.omg.CORBA.BAD_INV_ORDER ex = (org.omg.CORBA.BAD_INV_ORDER) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSIENT")) {
                    org.omg.CORBA.TRANSIENT ex = (org.omg.CORBA.TRANSIENT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.FREE_MEM")) {
                    org.omg.CORBA.FREE_MEM ex = (org.omg.CORBA.FREE_MEM) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INV_IDENT")) {
                    org.omg.CORBA.INV_IDENT ex = (org.omg.CORBA.INV_IDENT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INTF_REPOS")) {
                    org.omg.CORBA.INTF_REPOS ex = (org.omg.CORBA.INTF_REPOS) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_CONTEXT")) {
                    org.omg.CORBA.BAD_CONTEXT ex = (org.omg.CORBA.BAD_CONTEXT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.OBJ_ADAPTER")) {
                    org.omg.CORBA.OBJ_ADAPTER ex = (org.omg.CORBA.OBJ_ADAPTER) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.DATA_CONVERSION")) {
                    org.omg.CORBA.DATA_CONVERSION ex = (org.omg.CORBA.DATA_CONVERSION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.OBJECT_NOT_EXIST")) {
                    org.omg.CORBA.OBJECT_NOT_EXIST ex = (org.omg.CORBA.OBJECT_NOT_EXIST) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_REQUIRED")) {
                    org.omg.CORBA.TRANSACTION_REQUIRED ex = (org.omg.CORBA.TRANSACTION_REQUIRED) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_ROLLEDBACK")) {
                    org.omg.CORBA.TRANSACTION_ROLLEDBACK ex = (org.omg.CORBA.TRANSACTION_ROLLEDBACK) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INVALID_TRANSACTION")) {
                    org.omg.CORBA.INVALID_TRANSACTION ex = (org.omg.CORBA.INVALID_TRANSACTION) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.INV_POLICY")) {
                    org.omg.CORBA.INV_POLICY ex = (org.omg.CORBA.INV_POLICY) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.CODESET_INCOMPATIBLE")) {
                    org.omg.CORBA.CODESET_INCOMPATIBLE ex = (org.omg.CORBA.CODESET_INCOMPATIBLE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.REBIND")) {
                    org.omg.CORBA.REBIND ex = (org.omg.CORBA.REBIND) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TIMEOUT")) {
                    org.omg.CORBA.TIMEOUT ex = (org.omg.CORBA.TIMEOUT) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_UNAVAILABLE")) {
                    org.omg.CORBA.TRANSACTION_UNAVAILABLE ex = (org.omg.CORBA.TRANSACTION_UNAVAILABLE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.TRANSACTION_MODE")) {
                    org.omg.CORBA.TRANSACTION_MODE ex = (org.omg.CORBA.TRANSACTION_MODE) sysEx;
                    throw ex;
                } else if (sysEx.getClass().getName().equals(
                        "org.omg.CORBA.BAD_QOS")) {
                    org.omg.CORBA.BAD_QOS ex = (org.omg.CORBA.BAD_QOS) sysEx;
                    throw ex;
                }
            } catch (org.omg.CORBA.SystemException ex) {
                throw ex;
            }
        }

        if (raiseProxy_ != null)
            raiseProxy_.raise(this);

        throw new org.omg.CORBA.UNKNOWN();
    }

    //
    // Raise the exception with a list of possible exceptions
    //
    public void raise_exception_with_list(org.omg.CORBA.TypeCode[] exc_list)
            throws java.lang.Exception {
        try {
            this.raise_exception();
        } catch (java.lang.Exception ex) {
            //
            // This should work for all our exception types (System and
            // User)
            //
            org.omg.CORBA.Any any = new org.apache.yoko.orb.CORBA.Any();

            Class exClass = ex.getClass();
            String className = exClass.getName();
            try {
                //
                // Get the helper class and the insert method with
                // appropriate parameter types
                //
                // get the appropriate class for the loading.
                ClassLoader loader = exClass.getClassLoader();
                Class c = loader.loadClass(className + "Helper");
                Class[] paramTypes = new Class[2];
                paramTypes[0] = org.omg.CORBA.Any.class;
                paramTypes[1] = exClass;
                java.lang.reflect.Method m = c.getMethod("insert", paramTypes);

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
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (IllegalAccessException e) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (IllegalArgumentException e) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (java.lang.reflect.InvocationTargetException e) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
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
    public org.omg.CORBA.portable.ValueBase _copy_value()
            throws org.omg.CORBA.SystemException {
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
    public void _OB_extractSequence(org.apache.yoko.orb.OCI.Buffer buf) {
        org.apache.yoko.orb.OB.Assert._OB_assert(buf != null);

        marshaled_exception = new byte[buf.rest_length()];
        System.arraycopy(buf.data_, buf.pos(), marshaled_exception, 0, buf
                .rest_length());
    }

    //
    // Obtain an input stream from the marshalled exception sequence. This
    // is used for unmarshalling the exception.
    //
    public org.apache.yoko.orb.CORBA.InputStream _OB_inputStream() {
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                marshaled_exception, marshaled_exception.length);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf, 0, false, null, 258);

        return in;
    }

    //
    // Register the class used to raise any exceptions. This class will
    // take ownership of that raiser object.
    //
    public void _OB_register_raise_proxy(
            org.apache.yoko.orb.OBMessaging.UserExceptionRaiseProxy proxy) {
        org.apache.yoko.orb.OB.Assert._OB_assert(proxy != null);
        org.apache.yoko.orb.OB.Assert._OB_assert(raiseProxy_ == null);

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

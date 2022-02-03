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
import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.util.Assert;
import org.omg.CORBA.Any;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.SystemExceptionHelper;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.Messaging._ExceptionHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.security.AccessController.doPrivileged;
import static org.apache.yoko.orb.OCI.GiopVersion.GIOP1_2;
import static org.apache.yoko.util.PrivilegedActions.GET_CONTEXT_CLASS_LOADER;

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
            SystemException sysEx = SystemExceptionHelper.read(in);

            switch (sysEx.getClass().getName()) {
            case "org.omg.CORBA.UNKNOWN":
            case "org.omg.CORBA.BAD_PARAM":
            case "org.omg.CORBA.NO_MEMORY":
            case "org.omg.CORBA.IMP_LIMIT":
            case "org.omg.CORBA.COMM_FAILURE":
            case "org.omg.CORBA.INV_OBJREF":
            case "org.omg.CORBA.BAD_QOS":
            case "org.omg.CORBA.TRANSACTION_MODE":
            case "org.omg.CORBA.TRANSACTION_UNAVAILABLE":
            case "org.omg.CORBA.TIMEOUT":
            case "org.omg.CORBA.REBIND":
            case "org.omg.CORBA.CODESET_INCOMPATIBLE":
            case "org.omg.CORBA.INV_POLICY":
            case "org.omg.CORBA.INVALID_TRANSACTION":
            case "org.omg.CORBA.TRANSACTION_ROLLEDBACK":
            case "org.omg.CORBA.TRANSACTION_REQUIRED":
            case "org.omg.CORBA.OBJECT_NOT_EXIST":
            case "org.omg.CORBA.DATA_CONVERSION":
            case "org.omg.CORBA.OBJ_ADAPTER":
            case "org.omg.CORBA.BAD_CONTEXT":
            case "org.omg.CORBA.INTF_REPOS":
            case "org.omg.CORBA.INV_IDENT":
            case "org.omg.CORBA.FREE_MEM":
            case "org.omg.CORBA.TRANSIENT":
            case "org.omg.CORBA.BAD_INV_ORDER":
            case "org.omg.CORBA.PERSIST_STORE":
            case "org.omg.CORBA.NO_RESPONSE":
            case "org.omg.CORBA.NO_RESOURCES":
            case "org.omg.CORBA.BAD_OPERATION":
            case "org.omg.CORBA.BAD_TYPECODE":
            case "org.omg.CORBA.NO_IMPLEMENT":
            case "org.omg.CORBA.INITIALIZE":
            case "org.omg.CORBA.MARSHAL":
            case "org.omg.CORBA.INTERNAL":
            case "org.omg.CORBA.NO_PERMISSION":
                throw sysEx;
            }
        }

        if (null == raiseProxy_) throw new UNKNOWN();
        raiseProxy_.raise(this);
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

            Class<?> exClass = ex.getClass();
            String className = exClass.getName();
            try {
                // Get the helper class and the insert method with
                // appropriate parameter types
                Class<?> c = ProviderLocator.loadClass(className + "Helper", exClass, doPrivileged(GET_CONTEXT_CLASS_LOADER));
                Method m = c.getMethod("insert", Any.class, exClass);
                m.invoke(null, any, ex);
            } catch (ClassNotFoundException | SecurityException e) {
                //
                // REVISIT:
                // This just means that we probably caught a non-CORBA
                // exception. What do we want to do with it?
                //
                return;
            } catch (NoSuchMethodException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
                throw Assert.fail(ex);
            }

            final TypeCode anyType = any.type();
            for (TypeCode typeCode : exc_list) {
                if (anyType.equal(typeCode)) throw ex;
            }
        }
    }

    //
    // Obtain an input stream from the marshalled exception sequence. This
    // is used for unmarshalling the exception.
    //
    public InputStream _OB_inputStream() {
        return new InputStream(marshaled_exception, false, null, GIOP1_2);
    }

    //
    // Register the class used to raise any exceptions. This class will
    // take ownership of that raiser object.
    //
    public void _OB_register_raise_proxy(UserExceptionRaiseProxy proxy) {
        Assert.ensure(proxy != null);
        Assert.ensure(raiseProxy_ == null);
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

}

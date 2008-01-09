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

public class UserExceptionRaiseProxy {
    public void raise(org.omg.Messaging._ExceptionHolder execptHolder)
            throws org.omg.CORBA.UserException {
    }

    public void raise_with_list(
            org.omg.Messaging._ExceptionHolder exceptHolder,
            org.omg.CORBA.TypeCode[] exceptList)
            throws org.omg.CORBA.UserException {
        try {
            raise(exceptHolder);
        } catch (org.omg.CORBA.UserException ex) {
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
                // exception. For now, we'll just throw this again.
                //
                // throw e;
            } catch (NoSuchMethodException e) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (IllegalAccessException e) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (IllegalArgumentException e) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (java.lang.reflect.InvocationTargetException e) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            } catch (SecurityException e) {
                //
                // REVISIT:
                // What do we do here?
                //
            }

            for (int i = 0; i < exceptList.length; ++i) {
                if (any.type().equal(exceptList[i]))
                    throw ex;
            }
        }
    }

    public void register_as_proxy_with(
            org.omg.Messaging._ExceptionHolder exceptHolder) {

        org.apache.yoko.orb.OBMessaging.ExceptionHolder_impl exImpl = (org.apache.yoko.orb.OBMessaging.ExceptionHolder_impl) exceptHolder;

        exImpl._OB_register_raise_proxy(this);
        //
        // TODO: try/catch block???
        //
        /*
         * org.apache.yoko.orb.OBMessaging.ExceptionHolder_impl exImpl =
         * (org.apache.yoko.orb.OBMessaging.ExceptionHolder_impl)exceptHolder;
         *
         * exImpl._OB_register_raise_proxy(this);
         */
    }
}

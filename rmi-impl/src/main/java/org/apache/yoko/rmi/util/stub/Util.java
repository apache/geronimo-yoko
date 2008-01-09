/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.util.stub;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;

class Util {

    static String getPackageName(Class clazz) {
        String class_name = clazz.getName();
        int idx = class_name.lastIndexOf('.');
        if (idx == -1) {
            return null;
        } else {
            return class_name.substring(0, idx);
        }
    }

    static String getClassName(Class clazz) {
        String class_name = clazz.getName();
        int idx = class_name.lastIndexOf('.');
        if (idx == -1) {
            return class_name;
        } else {
            return class_name.substring(idx + 1);
        }
    }

    static private java.lang.reflect.Method defineClassMethod;
    static {
        try {
            // get the method object
            defineClassMethod = (SecureClassLoader.class).getDeclaredMethod(
                    "defineClass", new Class[] { String.class, byte[].class,
                            Integer.TYPE, Integer.TYPE, CodeSource.class });

        } catch (Error ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new Error("unexpected exception: " + ex.getMessage(), ex);
        }
    }

    static Class defineClass(final ClassLoader loader, String className,
            byte[] data, int off, int len) {

        final Object[] args = new Object[5];
        try {
            args[0] = className;
            args[1] = data;
            args[2] = new Integer(off);
            args[3] = new Integer(len);
            args[4] = new CodeSource(new URL("file:stub"), new Certificate[0]);
        } catch (java.net.MalformedURLException ex) {
            throw new Error(ex.getMessage(), ex);
        }
        return (Class) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {

                ClassLoader the_loader = (loader == null ? (SecureClassLoader) Thread
                        .currentThread().getContextClassLoader()
                        : (SecureClassLoader) loader);

                // make it accessible
                defineClassMethod.setAccessible(true);

                try {
                    return defineClassMethod.invoke(the_loader, args);
                } catch (IllegalAccessException ex) {
                    throw new Error("internal error", ex);
                } catch (IllegalArgumentException ex) {
                    throw new Error("internal error", ex);
                } catch (InvocationTargetException ex) {
                    Throwable th = ex.getTargetException();

                    if (th instanceof Error) {
                        throw (Error) th;
                    } else if (th instanceof RuntimeException) {
                        throw (RuntimeException) th;
                    } else {
                        throw new Error("unexpected exception: " + ex.getMessage(), ex);
                    }
                }
            }
        });
    }

    static String methodFieldName(int i) {
        return "__method$" + i;
    }

    static String handlerFieldName() {
        return "__handler";
    }

    static String initializerFieldName() {
        return "__initializer";
    }

    static String handlerDataFieldName() {
        return "__data";
    }

    static String getSuperMethodName(String name) {
        return "__super_" + name + "$"
                + Integer.toHexString(name.hashCode() & 0xffff);
    }

}

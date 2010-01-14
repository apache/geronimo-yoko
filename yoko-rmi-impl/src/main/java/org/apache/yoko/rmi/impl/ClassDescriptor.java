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

package org.apache.yoko.rmi.impl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

public class ClassDescriptor extends ValueDescriptor {
    static Logger logger = Logger.getLogger(ClassDescriptor.class.getName());

    ClassDescriptor(TypeRepository repository) {
        super(javax.rmi.CORBA.ClassDesc.class, repository);
    }

    java.lang.reflect.Field repid_field;

    java.lang.reflect.Field codebase_field;

    String _repid_arr;

    public void init() {
        super.init();

        Class clz = javax.rmi.CORBA.ClassDesc.class;
        try {
            repid_field = clz.getDeclaredField("repid");
            repid_field.setAccessible(true);
            codebase_field = clz.getDeclaredField("codebase");
            codebase_field.setAccessible(true);
        } catch (java.lang.NoSuchFieldException ex) {
            throw new org.omg.CORBA.MARSHAL("no such field: " + ex);
        }

        ValueDescriptor class_desc = new ValueDescriptor(Class.class,
                getTypeRepository());
        class_desc.init();
        _repid_arr = class_desc.getRepositoryID();
    }

    public String getRepositoryIDForArray() {
        return _repid_arr;
    }

    Object copyObject(Object orig, CopyState state) {
        state.put(orig, orig);
        return orig;
    }

    /** Read an instance of this value from a CDR stream */
    public java.io.Serializable readResolve(final java.io.Serializable value) {
        final javax.rmi.CORBA.ClassDesc desc = (javax.rmi.CORBA.ClassDesc) value;

        java.io.Serializable result = (java.io.Serializable) AccessController
                .doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        String className = "<unknown>";
                        try {
                            String repid = (String) repid_field.get(desc);
                            String codebase = (String) codebase_field.get(desc);

                            int beg = repid.indexOf(':');
                            int end = repid.indexOf(':', beg + 1);

                            className = repid.substring(beg + 1, end);
                            ClassLoader loader = Thread.currentThread()
                                    .getContextClassLoader();

                            return javax.rmi.CORBA.Util.loadClass(className,
                                    codebase, loader);
                        } catch (java.lang.ClassNotFoundException ex) {
                            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(
                                    "cannot load class " + className).initCause(ex);
                        } catch (java.lang.IllegalAccessException ex) {
                            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(
                                    "no such field: " + ex).initCause(ex);
                        }
                    }
                });

        logger.fine("readResolve " + value + " => " + result);

        return result;
    }

    /** Write an instance of this value to a CDR stream */
    public java.io.Serializable writeReplace(final java.io.Serializable value) {
        final Class type = (Class) value;

        final javax.rmi.CORBA.ClassDesc desc = new javax.rmi.CORBA.ClassDesc();

        return (java.io.Serializable) AccessController
                .doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        try {

                            javax.rmi.CORBA.ValueHandler handler = javax.rmi.CORBA.Util
                                    .createValueHandler();
                            String repId = handler.getRMIRepositoryID(type);
                            repid_field.set(desc, repId);

                            String codebase = javax.rmi.CORBA.Util
                                    .getCodebase(type);
                            codebase_field.set(desc, codebase);

                            return desc;

                        } catch (java.lang.IllegalAccessException ex) {
                            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(
                                    "no such field: " + ex).initCause(ex);
                        }
                    }
                });
    }

}

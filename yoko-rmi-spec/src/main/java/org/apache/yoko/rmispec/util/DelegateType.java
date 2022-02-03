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
package org.apache.yoko.rmispec.util;

import org.omg.CORBA.INITIALIZE;

import java.lang.reflect.Constructor;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;

import static java.security.AccessController.doPrivileged;
import static java.util.Collections.synchronizedSet;
import static org.apache.yoko.rmispec.util.UtilLoader.loadServiceClass;

public enum DelegateType {
    // Default class names used to avoid circular dependency between rmi-spec and rmi-impl
    UTIL("javax.rmi.CORBA.UtilClass","org.apache.yoko.rmi.impl.UtilImpl"),
    STUB("javax.rmi.CORBA.StubClass", "org.apache.yoko.rmi.impl.StubImpl"),
    PRO("javax.rmi.CORBA.PortableRemoteObjectClass", "org.apache.yoko.rmi.impl.PortableRemoteObjectImpl"),
    STUB_INIT("org.apache.yoko.rmi.RMIStubInitializerClass", "org.apache.yoko.rmi.impl.RMIStubInitializer");

    private static final Set<Object> delegateClassLoaders = synchronizedSet(new HashSet<>());

    public static boolean isDelegateClassLoader(ClassLoader cl) {
        return delegateClassLoaders.contains(cl);
    }

    private final String key;
    private final String defaultClassName;

    DelegateType(String key, String defaultClassName) {
        this.key = key;
        this.defaultClassName = defaultClassName;
    }

    public <U> PrivilegedExceptionAction<Constructor<? extends U>> getConstructorAction() {
        return () -> {
            final String delegateName = System.getProperty(key, defaultClassName);
            try {
                final Class<? extends U> delegateClass = loadServiceClass(delegateName, key);
                final Constructor<? extends U> constructor = delegateClass.getConstructor();
                // add class only after successful retrieval of constructor
                final ClassLoader classLoader = doPriv(delegateClass::getClassLoader);
                if (null != classLoader) delegateClassLoaders.add(classLoader);
                return constructor;
            } catch(Exception e) {
                throw (INITIALIZE) new INITIALIZE("Can not create delegate: '" + delegateName + "' for key: '" + key + "'").initCause(e);
            }
        };
    }

    @SuppressWarnings("SpellCheckingInspection")
    private static <T> T doPriv(PrivilegedAction<T> action) { return doPrivileged(action); }
}

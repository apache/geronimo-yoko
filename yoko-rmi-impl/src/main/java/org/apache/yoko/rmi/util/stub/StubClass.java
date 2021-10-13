/*
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

import org.apache.yoko.rmi.impl.MethodDescriptor;
import org.apache.yoko.rmi.impl.RMIStub;
import org.apache.yoko.rmi.impl.RMIStubInitializer;
import org.apache.yoko.rmi.impl.StubHandler;
import org.omg.CORBA.INITIALIZE;

import java.lang.reflect.Method;
import java.security.PrivilegedActionException;
import java.security.SecureClassLoader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.security.AccessController.doPrivileged;
import static java.util.Optional.ofNullable;
import static org.apache.yoko.logging.VerboseLogging.wrapped;
import static org.apache.yoko.rmi.util.stub.StubClass.InitializerHolder.RMI_STUB_INITIALIZER;
import static org.apache.yoko.rmi.util.stub.StubClass.StubInvokeMethodHolder.STUB_INVOKE_METHOD;
import static org.apache.yoko.rmi.util.stub.Util.getPackageName;
import static org.apache.yoko.rmispec.util.UtilLoader.loadServiceClass;
import static org.apache.yoko.util.Exceptions.as;
import static org.apache.yoko.util.PrivilegedActions.action;
import static org.apache.yoko.util.PrivilegedActions.getDeclaredMethod;
import static org.apache.yoko.util.PrivilegedActions.getNoArgInstance;
import static org.apache.yoko.util.PrivilegedActions.getSysProp;

public final class StubClass {
    static final Logger LOGGER = Logger.getLogger(StubClass.class.getName());
    static final AtomicInteger counter = new AtomicInteger();

    private static MethodRef[] getMethodRefs(Class<?> type) {
        return doPrivileged(action(() -> BCELClassBuilder.collectMethods(RMIStub.class, type)));
    }

    private static ClassLoader chooseLoader(ClassLoader loader, Class<?> type) {
        Set<ClassLoader> loaders = new HashSet<>();

        loaders.add(getClassLoader(Stub.class));
        loaders.add(getClassLoader(STUB_INVOKE_METHOD.getDeclaringClass()));

        ofNullable(loader).ifPresent(loaders::add);
        Optional.of(RMIStub.class).map(StubClass::getClassLoader).ifPresent(loaders::add);
        Optional.of(type).map(StubClass::getClassLoader).ifPresent(loaders::add);

        if (loaders.isEmpty()) return null;
        if (loaders.size() == 1) return loaders.iterator().next();

        return doPrivileged(action(() -> new MetaLoader(loaders)));
    }

    private static ClassLoader getClassLoader(Class<?> c) {
        return doPrivileged(action(c::getClassLoader));
    }

    public static <S extends Stub> Class<S> make(Class<?> type, MethodDescriptor[] descriptors, MethodRef[] methods, ClassLoader loader) {
        loader = chooseLoader(loader, type);
        methods = ofNullable(methods).orElseGet(() -> getMethodRefs(type));
        final MethodRef handler = new MethodRef(STUB_INVOKE_METHOD);
        String className = stubClassName(getPackageName(type));
        return BCELClassBuilder.make(loader, RMIStub.class, type, methods, descriptors, handler, className, RMI_STUB_INITIALIZER);
    }

    static String stubClassName(String packageName) {
        if (packageName == null) packageName = "org.apache.yoko.rmi.util.stub.gen";

        return packageName + ".Stub$$" + counter.getAndIncrement();
    }

    enum StubInvokeMethodHolder {
        ;
        static final Method STUB_INVOKE_METHOD;
        static {
            try {
                STUB_INVOKE_METHOD = doPrivileged(getDeclaredMethod(StubHandler.class, "invoke", RMIStub.class, MethodDescriptor.class, Object[].class));
            } catch (PrivilegedActionException ex) {
                //noinspection Convert2MethodRef
                throw wrapped(LOGGER, ex, "cannot initialize: \n" + ex.getMessage(), e -> new Error(e));
            }
        }
    }

    enum InitializerHolder {
        ;
        static final StubInitializer RMI_STUB_INITIALIZER = findRmiStubInitializer();

        private static final String rmiStubInitializerKey ="org.apache.yoko.rmi.RMIStubInitializerClass";

        private static StubInitializer findRmiStubInitializer() {
            String factory = doPrivileged(getSysProp(rmiStubInitializerKey, RMIStubInitializer.class.getName()));
            try {
                final Class<? extends StubInitializer> type = loadServiceClass(factory, rmiStubInitializerKey);
                return doPrivileged(getNoArgInstance(type));
            } catch (Exception e) {
                throw as(INITIALIZE::new, e,"Can not create RMIStubInitializer: " + factory);
            }
        }
    }

    static class MetaLoader extends SecureClassLoader {
        private final Iterable<ClassLoader> loaders;

        MetaLoader(Iterable<ClassLoader> loaders) {
            assert loaders.iterator().hasNext();
            this.loaders = loaders;
        }

        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            ClassNotFoundException mainException = null;
            for (ClassLoader loader: loaders) {
                try {
                    return (null == loader) ? loadSystemClass(name) : loader.loadClass(name);
                } catch (ClassNotFoundException e) {
                    if (null == mainException) mainException = new ClassNotFoundException(name);
                    mainException.addSuppressed(e);
                }
            }
            assert mainException != null;
            throw mainException;
        }

        private Class<?> loadSystemClass(String name) throws ClassNotFoundException {
            // Since this class loader implicitly invokes its no-args super constructor,
            // super.loadClass() will query the system class loader
            return super.loadClass(name);
        }
    }
}

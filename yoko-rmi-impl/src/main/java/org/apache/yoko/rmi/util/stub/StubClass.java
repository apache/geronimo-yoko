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
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class StubClass {
    static final Logger logger = Logger.getLogger(StubClass.class.getName());

    /**
     * Return the stub handler for the given stub.
     */
    public static Object getStubHandler(final Stub stub) {
        return stub.____getTriforkStubHandler();
    }

    /**
     * Construct a StubClass.
     * 
     * @param loader
     *            designates the classloader to use for constructing the
     *            stubclass. If <code>null</code>, the current context class
     *            loader will be used.
     * @param super_class
     *            is the base class for the new stub. If <code>null</code>
     *            java.lang.Object will be used.
     * @param interfaces
     *            designates the interfaces the stub class should implement.
     *            This value may be <code>null</code> if no additional
     *            interfaces should be included.
     * @param methods
     *            is the list of methods to implement in the stub class. If this
     *            parameter is <code>null</code>, stub methods will be
     *            generated for all abstract methods declared but not
     *            implemented.
     * @param data
     *            describes the data to be passed along as the second argument
     *            to a handler method.
     * @param handler_method
     *            is the method used for delegating the call handled by a stub
     *            method. If <code>null</code>, then
     *            <code>com.trifork.StubHandler.invoke()</code> is used. This
     *            method must have a signature `public Object <i>Handler</i>.<i>name</i>(Object,
     *            <i>Data</i>, Object)'. The constructor to the resulting stub
     *            class takes as it's argument, a <i>Handler</i> object.
     * @param package_name
     *            is the name of the package into which the stub class is
     *            defined. If <code>null</code>, then the package of the
     *            <i>super_class</i> is used; unless it lives in a sealed
     *            package, in which case the package anme is undefined.
     */
    public static Class make(final ClassLoader loader, final Class super_class,
            final Class[] interfaces, final MethodRef[] methods,
            final Object[] data, final Method handler_method,
            final String package_name) {
        return (Class) java.security.AccessController
                .doPrivileged(new java.security.PrivilegedAction() {
                    public Object run() {

                        try {
                            Class superClass = super_class;
                            if (superClass == null)
                                superClass = java.lang.Object.class;

                            Class[] theInterfaces = interfaces;
                            if (theInterfaces == null)
                                theInterfaces = new java.lang.Class[0];

                            ClassLoader theLoader = chooseLoader(loader,
                                    superClass, theInterfaces, handler_method);

                            MethodRef[] theMethods = methods;
                            if (theMethods == null)
                                theMethods = BCELClassBuilder.collectMethods(
                                        superClass, theInterfaces);

                            String className = BCELClassBuilder.className(
                                    package_name, superClass, theInterfaces);
                            return BCELClassBuilder.make(theLoader, superClass,
                                    theInterfaces, theMethods, data,
                                    new MethodRef(handler_method), className);

                            /*
                             * return StubClassBuilder.make (loader,
                             * super_class, interfaces, methods, data,
                             * handler_method, package_name);
                             */
                        } catch (IllegalAccessException ex) {
                            throw new Error("illegal access", ex);
                        } catch (InstantiationException ex) {
                            throw new Error("illegal access", ex);
                        }

                    }
                });
    }

    public static MethodRef[] getAbstractMethodRefs(final Class base,
            final Class[] interfaces) {
        return (MethodRef[]) java.security.AccessController
                .doPrivileged(new java.security.PrivilegedAction() {
                    public Object run() {
                        return BCELClassBuilder.getAbstractMethods(base,
                                interfaces);
                    }
                });
    }

    public static Class make(final ClassLoader loader, final Class super_class,
            final Class[] interfaces, final MethodRef[] methods,
            final Method[] superMethods, final Object[] data,
            final Method handler_method, final String package_name,
            final StubInitializer initializer) {
        return make(loader, super_class, interfaces, methods, superMethods,
                data, handler_method, package_name, (String) null, initializer);
    }

    /**
     * Construct a stub for which it's handler is determined by using a
     * StubInitializer. Using this, the resulting class has a no-arg
     * constructor.
     * <p>
     * 
     * @param superMethods
     *            is an array of methods defined in the super_class, for which
     *            super.method trampolines should be generated. Upon return, the
     *            array has filled in java.lang.reflect.Method objects for the
     *            generated trampolines.
     */
    public static Class make(final ClassLoader loader, final Class super_class,
            final Class[] interfaces, final MethodRef[] methods,
            final Method[] superMethods, final Object[] data,
            final Method handler_method, final String package_name,
            final String class_name, final StubInitializer initializer) {
        return (Class) java.security.AccessController
                .doPrivileged(new java.security.PrivilegedAction() {
                    public Object run() {

                        try {
                            Class superClass = super_class;
                            if (superClass == null)
                                superClass = java.lang.Object.class;

                            Class[] theInterfaces = interfaces;
                            if (theInterfaces == null)
                                theInterfaces = new java.lang.Class[0];

                            ClassLoader theLoader = chooseLoader(loader,
                                    superClass, theInterfaces, handler_method);

                            MethodRef[] theMethods = methods;
                            if (theMethods == null)
                                theMethods = BCELClassBuilder.collectMethods(
                                        superClass, theInterfaces);

                            MethodRef[] superMethodRefs;
                            if (superMethods == null) {
                                superMethodRefs = new MethodRef[0];
                            } else {
                                superMethodRefs = new MethodRef[superMethods.length];
                                for (int i = 0; i < superMethods.length; i++)
                                    superMethodRefs[i] = new MethodRef(
                                            superMethods[i]);
                            }

                            Object[] theData = data;
                            if (theData == null)
                                theData = theMethods;

                            MethodRef handlerMethodRef = new MethodRef(
                                    handler_method);

                            String className = class_name;
                            if (className == null)
                                className = BCELClassBuilder
                                        .className(package_name, superClass,
                                                theInterfaces);

                            Class result = BCELClassBuilder.make(theLoader,
                                    superClass, theInterfaces, theMethods,
                                    superMethodRefs, theData, handlerMethodRef,
                                    className, initializer);

                            if (superMethods != null) {
                                try {
                                    for (int i = 0; i < superMethods.length; i++) {
                                        java.lang.reflect.Method m = superMethods[i];

                                        superMethods[i] = result
                                                .getDeclaredMethod(Util
                                                        .getSuperMethodName(m
                                                                .getName()), m
                                                        .getParameterTypes());
                                    }

                                } catch (NoSuchMethodException ex) {
                                    throw new Error("internal error!", ex);
                                }
                            }

                            return result;

                        } catch (IllegalAccessException ex) {
                            throw new Error("illegal access", ex);
                        } catch (InstantiationException ex) {
                            throw new Error("illegal access", ex);
                        }

                    }
                });
    }

    /**
     * 
     */
    public static Stub createInstance(final Class clazz, final Object handler) {
        return (Stub) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    java.lang.reflect.Constructor[] con = clazz
                            .getConstructors();

                    // create an instance of it
                    return con[0].newInstance(new Object[] { handler });
                } catch (InstantiationException ex) {
                    logger.log(Level.WARNING, "", ex);
                } catch (InvocationTargetException ex) {
                    logger.log(Level.WARNING, "", ex);
                } catch (IllegalAccessException ex) {
                    logger.log(Level.WARNING, "", ex);
                }

                return null;
            }
        });
    }

    public static Stub createInstance(final Class clazz) {
        return (Stub) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException ex) {
                    logger.log(Level.WARNING, "", ex);
                } catch (IllegalAccessException ex) {
                    logger.log(Level.WARNING, "", ex);
                }

                return null;
            }
        });
    }

    private static ClassLoader chooseLoader(ClassLoader loader,
            Class superClass, Class[] interfaces, Method handler) {
        java.util.Set loaders = new java.util.HashSet();

        loaders.add(Stub.class.getClassLoader());

        loaders.add(handler.getDeclaringClass().getClassLoader());

        if (loader != null) {
            loaders.add(loader);
        }

        if (superClass != null && superClass.getClassLoader() != null) {
            loaders.add(superClass.getClassLoader());
        }

        for (int i = 0; i < interfaces.length; i++) {
            if (interfaces[i].getClassLoader() != null) {
                loaders.add(interfaces[i].getClassLoader());
            }
        }

        if (loaders.size() == 0) {
            return superClass.getClassLoader();
        }

        ClassLoader first = (ClassLoader) loaders.iterator().next();

        if (loaders.size() == 1) {
            return (ClassLoader) first;
        }

        loaders.remove(first);
        ClassLoader[] rest = new ClassLoader[loaders.size()];
        loaders.toArray(rest);

        return new SetClassLoader(rest, first);
    }

    static class SetClassLoader extends java.security.SecureClassLoader {
        ClassLoader[] rest;

        SetClassLoader(ClassLoader[] loaders, ClassLoader parent) {
            super(parent);
            rest = loaders;
        }

        protected Class loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            try {

                //
                // will try parent
                //
                return super.loadClass(name, resolve);
            } catch (ClassNotFoundException ex) {
                // ignore //
            }

            for (int i = 0; i < rest.length; i++) {
                try {
                    return rest[i].loadClass(name);
                } catch (ClassNotFoundException ex) {
                    // ignore //
                }
            }

            throw new ClassNotFoundException(name);
        }
    }
}

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

package org.apache.yoko.rmispec.util;

import org.apache.yoko.osgi.ProviderLocator;

import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;
import static java.security.AccessController.doPrivileged;
import static java.util.function.Predicate.isEqual;

public class UtilLoader {
    static final Logger logger = Logger.getLogger(UtilLoader.class.getName());

    // Note: this field must be declared before the static intializer that calls Util.loadClass
    // since that method will call loadClass0 which uses this field... if it is below the static
    // initializer the _secman field will be null
    private static final SecMan SEC_MAN = doPriv(SecMan::new);

    private static final ClassLoader MY_LOADER = UtilLoader.class.getClassLoader();
    private static final Function<Class, ClassLoader> GET_LOADER = type -> doPrivileged((PrivilegedAction<ClassLoader>) type::getClassLoader);

    public static <T> Class<T> loadServiceClass(String delegateName, String delegateKey) throws ClassNotFoundException {
        try {
            Class<T> cls = ProviderLocator.getServiceClass(delegateKey, null, null);
            if (cls != null) return cls;
        } catch (ClassNotFoundException ignored){
        }
        return loadClass0(delegateName, null);
    }

    public static <T> Class<T> loadClass(String name, ClassLoader loader) throws ClassNotFoundException {
        try {
            return ProviderLocator.loadClass(name, null, loader);
        } catch (ClassNotFoundException ignored) {
        }
        return loadClass0(name, loader);
    }

    private static <T> Class<T> loadClass0(String name, ClassLoader loader) throws ClassNotFoundException {
        // try loading using our loader, just in case we really were loaded
        // using the same classloader the delegate is in.
        Stream<ClassLoader> loadersFromStack = Stream.of(SEC_MAN.getClassContext())
                .skip(1)
                .map(GET_LOADER)
                .filter(isEqual(MY_LOADER).negate())
                .filter(Objects::nonNull)
                .peek(ldr -> logger.fine("Trying stack loader: " + ldr));

        Stream<ClassLoader> loaderOfThisClass = Stream.of(MY_LOADER)
                .filter(Objects::nonNull)
                .peek(ldr -> logger.fine("Trying util loader: " + ldr));

        Stream<ClassLoader> loaderFromContext = Stream.of(loader)
                .map(l -> l == null ? doPriv(currentThread()::getContextClassLoader) : l)
                .filter(Objects::nonNull)
                .peek(ldr -> logger.fine("Trying supplied/context loader: " + ldr));

        Function<ClassLoader, Class<T>> loadClass = ldr -> {
            try {
                return (Class<T>) ldr.loadClass(name);
            } catch (ClassNotFoundException e) {
                logger.log(Level.FINER, "Loader says " + e.getMessage(), e);
                return null;
            }
        };

        Optional<Class<T>> foundClass = Stream.of(loadersFromStack, loaderOfThisClass, loaderFromContext)
                .flatMap(s -> s)
                .map(loadClass)
                .filter(Objects::nonNull)
                .findFirst();

        if (foundClass.isPresent()) return foundClass.get();
        throw new ClassNotFoundException(name);
    }

    private static <T> T doPriv(PrivilegedAction<T> action) { return doPrivileged(action); }

    static class SecMan extends java.rmi.RMISecurityManager {
        public Class[] getClassContext() {
            return super.getClassContext();
        }
    }
}

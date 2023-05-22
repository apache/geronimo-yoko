/*
 * Copyright 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.rmi.api;

import org.apache.yoko.osgi.ProviderLocator;
import org.omg.CORBA.INTERNAL;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.util.Optional;
import java.util.function.Function;

import static java.security.AccessController.doPrivileged;
import static org.apache.yoko.util.PrivilegedActions.GET_CONTEXT_CLASS_LOADER;
import static org.apache.yoko.util.PrivilegedActions.getSysProp;

public class PortableRemoteObjectExt {
    private static final class DelegateHolder {
        private static final PortableRemoteObjectExtDelegate DELEGATE;

        public static final String DELEGATE_KEY = "org.apache.yoko.rmi.PortableRemoteObjectExtClass";
        @SuppressWarnings("SpellCheckingInspection")
        private static final Function<Class<PortableRemoteObjectExtDelegate>, Constructor<PortableRemoteObjectExtDelegate>> DOPRIV_GET_CONSTRUCTOR = c -> doPrivileged(getNoArgsConstructor(c));

        static {
            final ClassLoader contextCl = doPrivileged(GET_CONTEXT_CLASS_LOADER);
            DELEGATE = ProviderLocator.getService(DELEGATE_KEY, PortableRemoteObjectExt.class, contextCl, DOPRIV_GET_CONSTRUCTOR)
                    .orElseGet(() -> {
                        try {
                            return Optional.of(doPrivileged(getSysProp(DELEGATE_KEY, "org.apache.yoko.rmi.impl.PortableRemoteObjectExtImpl")))
                                    .map(name -> {
                                        try {
                                            return ProviderLocator.<PortableRemoteObjectExtDelegate>loadClass(name, PortableRemoteObjectExt.class, contextCl);
                                        } catch (ClassNotFoundException e) {
                                            throw new RuntimeException("internal problem: " + e.getMessage(), e);
                                        }
                                    })
                                    .map(DOPRIV_GET_CONSTRUCTOR)
                                    .orElseThrow(INTERNAL::new)
                                    .newInstance();
                        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException("internal problem: " + e.getMessage(), e);
                        }
                    });
        }
    }

    private static <T> PrivilegedAction<Constructor<T>> getNoArgsConstructor(Class<T> clz) {
        return () -> {
            try {
                return clz.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new Error(e);
            }
        };
    }

    /** Return the currently active state for this thread */
    public static PortableRemoteObjectState getState() {
        return DelegateHolder.DELEGATE.getCurrentState();
    }
}

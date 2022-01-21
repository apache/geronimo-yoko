/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.yoko.osgi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.security.AccessController.doPrivileged;

public enum ProviderLocator {;
    static private ProviderRegistry registry;

    public static void setRegistry(ProviderRegistry registry) {
        ProviderLocator.registry = registry;
    }

    /**
     * Locate a class by its provider id indicator. .
     *
     * @param providerId The provider id (generally, a fully qualified class name).
     *
     * @return The Class corresponding to this provider id.  Returns null
     *         if this is not registered or the indicated class can't be
     *         loaded.
     */
    static public Class<?> locate(String providerId) {
        return getRegistry()
                .map(r -> r.locate(providerId))
                .orElse(null);
    }

    /**
     * Standardized utility method for performing class lookups
     * with support for OSGi registry lookups.
     *
     * Note: this method is <em>unprivileged</em>: the onus is on the caller to sanitize input and assert privilege
     */
    static public <T> Class<T> loadClass(String className, Class<?> contextClass, ClassLoader loader) throws ClassNotFoundException {
        // First check the registered service providers for this class
        Class<T> cls = generify(locate(className));
        if (null != cls) return cls;

        // Load from the explicit class loader if there is one
        if (null != loader) {
            try {
                return generify(Class.forName(className, false, loader));
            } catch (ClassNotFoundException e) {
                if (null == contextClass) throw e;
            }
        }

        // Load from either the context class loader, if provided,
        // or the system class loader (i.e. null)
        loader = null == contextClass ? null : doPriv(contextClass::getClassLoader);
        return generify(Class.forName(className, false, loader));
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> generify(Class<?> c) { return (Class<T>)c; }

    @SuppressWarnings("SpellCheckingInspection")
    private static <T> T doPriv(PrivilegedAction<T> action) { return doPrivileged(action); }


    /**
     * Get a single service instance that matches an interface
     * definition.
     *
     * @param iface  The name of the required interface.
     * @param contextClass
     *               The class requesting the lookup (used for class resolution).
     * @param loader A class loader to use for searching for service definitions
     *               and loading classes.
     *
     * @return The service instance, or null if no matching services
     *         can be found.
     */
    static public <T> Optional<T> getService(String iface, Class<?> contextClass, ClassLoader loader, Function<Class<T>,Constructor<T>> privilegedGetConstructor) {
        // if we are working in an OSGi environment, then process the service
        // registry first.  Ideally, we would do this last, but because of boot delegation
        // issues with some API implementations, we must try the OSGi version first

        return getRegistry()
                // get the service, if it exists.
                .map(r -> r.<T>getService(iface))
                .map(Optional::of)
                // try for a classpath locatable instance next.
                .orElseGet(() -> ProviderLocator.<T>locateServiceClass(iface, contextClass, loader)
                                .map(privilegedGetConstructor)
                                .map(ProviderLocator::newInstance));
    }

    private static <T> T newInstance(Constructor<T> constructor) {
        try {
            return constructor.newInstance();
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new Error(e);
        }
    }

    public static <T> PrivilegedAction<Constructor<T>> getNoArgsConstructor(Class<T> clz) {
        return () -> {
            try {
                return clz.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new Error(e);
            }
        };
    }

    /**
     * Locate a service class that matches an interface
     * definition.
     *
     * @param iface  The name of the required interface.
     * @param contextClass
     *               The class requesting the lookup (used for class resolution).
     * @param loader A class loader to use for searching for service definitions
     *               and loading classes.
     *
     * @return An Optional containing The located class, if found.
     */
    static public <T> Optional<Class<T>> getServiceClass(String iface, Class<?> contextClass, ClassLoader loader) {
        // if we are working in an OSGi environment, then process the service
        // registry first.  Ideally, we would do this last, but because of boot delegation
        // issues with some API implementations, we must try the OSGi version first
        return getRegistry()
                // get the service class, if it is known to the registry.
                .map(r -> r.<T>getServiceClass(iface))
                .map(Optional::of)
                // try for a classpath locatable instance
                .orElseGet(() -> locateServiceClass(iface, contextClass, loader));
    }

    /**
     * Test whether a class loader is associated with a provided service.
     */
    public static boolean isServiceClassLoader(ClassLoader loader) {
        return getRegistry()
                .map(r -> r.isServiceClassLoader(loader))
                .orElse(false);
    }


    /**
     * Locate a classpath-define service mapping.
     *
     * @param iface  The required interface name.
     * @param loader The ClassLoader instance to use to locate the service.
     *
     * @return The mapped class name, if one is found.  Returns null if the
     *         mapping is not located.
     */
    static Optional<String> locateServiceClassName(String iface, ClassLoader loader) {
        if (loader != null) {
            try {
                // we only look at resources that match the file name, using the specified loader
                for (URL provider : Collections.list(loader.getResources("META-INF/services/" + iface))) {
                    for (String providerName: parseServiceDefinition(provider)) {
                        return Optional.of(providerName);
                    }
                }
            } catch (IOException ignored) {}
        }
        // not found
        return Optional.empty();
    }


    /**
     * Locate the first class for a META-INF/services definition
     * of a given interface class.  The first matching provider is
     * returned.
     *
     * @param iface  The interface class name used for the match.
     * @param loader The classloader for locating resources.
     *
     * @return An Optional containing The mapped provider class, if found.
     */
    static private <T> Optional<Class<T>> locateServiceClass(String iface, Class<?> contextClass, ClassLoader loader) {
        Optional<String> name = locateServiceClassName(iface, loader);
        final ClassLoader cl = name
                .map(n -> loader)
                .orElse(null == contextClass ? loader : doPriv(contextClass::getClassLoader));
        return name
                .map(Optional::of)
                .orElseGet(() -> locateServiceClassName(iface, cl))
                .map(n -> {
                    try {
                        return loadClass(n, contextClass, cl);
                    } catch (ClassNotFoundException e) {
                        return null;
                    }
                });
    }

    /**
     * Parse a definition file and return the names of all included implementation classes
     * contained within the file.
     *
     * @param u      The URL of the file
     *
     * @return A list of all matching classes.  Returns an empty list
     *         if no matches are found.
     */
    static private List<String> parseServiceDefinition(URL u) {
        final String url = u.toString();
        List<String> classes = new ArrayList<>();
        // ignore directories
        if (url.endsWith("/")) {
            return classes;
        }
        // the identifier used for the provider is the last item in the URL.
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), UTF_8));
            // the file can be multiple lines long, with comments.  A single file can define multiple providers
            // for a single key, so we might need to create multiple entries.  If the file does not contain any
            // definition lines, then as a default, we use the providerId as an implementation class also.
            String line = br.readLine();
            while (line != null) {
                // we allow comments on these lines, and a line can be all comment
                int comment = line.indexOf('#');
                if (comment != -1) {
                    line = line.substring(0, comment);
                }
                line = line.trim();
                // if there is nothing left on the line after stripping white space and comments, skip this
                if (line.length() > 0) {
                    // add this to our list
                    classes.add(line);
                }
                // keep reading until the end.
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            // ignore errors and handle as default
        }
        return classes;
    }


    private static Optional<ProviderRegistry> getRegistry() {
        return Optional.ofNullable(registry);
    }
}

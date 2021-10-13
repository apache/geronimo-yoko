/**
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
        ProviderRegistry registry = getRegistry();
        // if no registry service available, this is a failure
        if (registry == null) {
            return null;
        }
        // get the service, if it exists.  NB, if there is a service object,
        // then the extender and the interface class are available, so this cast should be
        // safe now.

        // the rest of the work is done by the registry
        return registry.locate(providerId);
    }

    /**
     * Standardized utility method for performing class lookups
     * with support for OSGi registry lookups.
     *
     * Note: this method is <em>unprivileged</em>: the onus is on the caller to sanitize input and assert privilege
     */
    static public <T> Class<T> loadClass(String className, Class<?> contextClass, ClassLoader loader) throws ClassNotFoundException {
        // First check the registered service providers for this class
        Class cls = locate(className);
        if (cls != null) {
            return cls;
        }

        // Load from the explicit class loader if there is one
        if (loader != null) {
            try {
                return (Class<T>) Class.forName(className, false, loader);
            } catch (ClassNotFoundException e) {
                if (contextClass == null)
                    throw e;
            }
        }

        // Load from either the context class loader, if provided,
        // or the system class loader (i.e. null)
        loader = contextClass == null ? null : contextClass.getClassLoader();
        return (Class<T>) Class.forName(className, false, loader);

    }


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
     * @exception Exception Thrown for any classloading or exceptions thrown
     *                      trying to instantiate a service instance.
     */
    static public Object getService(String iface, Class<?> contextClass, ClassLoader loader) throws Exception {
        // if we are working in an OSGi environment, then process the service
        // registry first.  Ideally, we would do this last, but because of boot delegation
        // issues with some API implementations, we must try the OSGi version first
        Object registry = getRegistry();
        if (registry != null) {
            // get the service, if it exists.  NB, if there is a service object,
            // then the extender and the interface class are available, so this cast should be
            // safe now.
            // the rest of the work is done by the registry
            Object service = ((ProviderRegistry)registry).getService(iface);
            if (service != null) {
                return service;
            }
        }

        // try for a classpath locatable instance next.  If we find an appropriate class mapping,
        // create an instance and return it.
        Class<?> cls = locateServiceClass(iface, contextClass, loader);
        if (cls != null) {
            // TODO: this should have a doPrivileged() around it
            // BUT because locateServiceClass tries to load any service described in one loader
            // from another loader as well, a doPrivileged() block here would expose a HUGE security hole
            return cls.getConstructor().newInstance();
        }
        // a provider was not found
        return null;
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
     * @return The located class, or null if no matching services
     *         can be found.
     * @exception Exception Thrown for any classloading exceptions thrown
     *                      trying to load the class.
     */
    static public <T> Class<T> getServiceClass(String iface, Class<?> contextClass, ClassLoader loader) throws ClassNotFoundException {
        // if we are working in an OSGi environment, then process the service
        // registry first.  Ideally, we would do this last, but because of boot delegation
        // issues with some API implementations, we must try the OSGi version first
        Object registry = getRegistry();
        if (registry != null) {
            // get the service, if it exists.  NB, if there is a service object,
            // then the extender and the interface class are available, so this cast should be
            // safe now.

            // If we've located stuff in the registry, then return it
            Class<T> cls = ((ProviderRegistry)registry).getServiceClass(iface);
            if (cls != null) {
                return cls;
            }
        }

        // try for a classpath locatable instance first.  If we find an appropriate class mapping,
        // create an instance and return it.
        return locateServiceClass(iface, contextClass, loader);
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
    static private String locateServiceClassName(String iface, ClassLoader loader) {
        if (loader != null) {
            try {
                // we only look at resources that match the file name, using the specified loader
                String service = "META-INF/services/" + iface;
                Enumeration<URL> providers = loader.getResources(service);

                while (providers.hasMoreElements()) {
                    List<String>providerNames = parseServiceDefinition(providers.nextElement());
                    // if there is something defined here, return the first entry
                    if (!providerNames.isEmpty()) {
                        return providerNames.get(0);
                    }
                }
            } catch (IOException e) {
            }
        }
        // not found
        return null;
    }


    /**
     * Locate the first class for a META-INF/services definition
     * of a given interface class.  The first matching provider is
     * returned.
     *
     * @param iface  The interface class name used for the match.
     * @param loader The classloader for locating resources.
     *
     * @return The mapped provider class, if found.  Returns null if
     *         no mapping is located.
     */
    static private <T> Class<T> locateServiceClass(String iface, Class<?> contextClass, ClassLoader loader) throws ClassNotFoundException {
        // search first with the loader class path
        String name = locateServiceClassName(iface, loader);
        if (name == null && contextClass != null) {
            // then with the context class, if there is one
            loader = contextClass.getClassLoader();
            name = locateServiceClassName(iface, loader);
        }

        return name == null ? null : loadClass(name, contextClass, loader);
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
        List<String> classes = new ArrayList<String>();
        // ignore directories
        if (url.endsWith("/")) {
            return classes;
        }
        // the identifier used for the provider is the last item in the URL.
        final String providerId = url.substring(url.lastIndexOf("/") + 1);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(u.openStream(), "UTF-8"));
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


    /**
     * Retrieve the registry from the tracker if it is available,
     * all without causing the interface class to load.
     *
     * @return The registry service instance, or null if it is not
     *         available for any reason.
     */
    private static ProviderRegistry getRegistry() {
        return registry;
    }
}

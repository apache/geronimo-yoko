/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.yoko.osgi.locator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.osgi.ProviderRegistry;

/**
 * The implementation of the provider registry used to store
 * the bundle registrations.
 */
public class ProviderRegistryImpl implements ProviderRegistry, Register {

    private static final Logger log = Logger.getLogger(ProviderRegistryImpl.class.getName());
    // our mapping between a provider id and the implementation information.  There
    // might be a one-to-many relationship between the ids and implementing classes.
    private SPIRegistry providers = new SPIRegistry();
    // our mapping between an interface name and a META-INF/services SPI implementation.  There
    // might be a one-to-many relationship between the ids and implementing classes.
    private SPIRegistry serviceProviders = new SPIRegistry();

    public void start() {
        ProviderLocator.setRegistry(this);
    }

    public void stop() {
        ProviderLocator.setRegistry(null);
    }

    /**
     * Register an individual provivider item by its provider identifier.
     *
     * @param provider The loader used to resolve the provider class.
     */
    public void registerProvider(BundleProviderLoader provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "registering provider " + provider);
        providers.register(provider);
    }

    /**
     * Removed a provider registration for a named provider id.
     *
     * @param provider The provider registration instance
     */
    public void unregisterProvider(BundleProviderLoader provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "unregistering provider " + provider);
        providers.unregister(provider);
    }


    /**
     * Register an individual provivider item by its provider identifier.
     *
     * @param provider The loader used to resolve the provider class.
     */
    public void registerService(BundleProviderLoader provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "registering service " + provider);
        serviceProviders.register(provider);
    }

    /**
     * Removed a provider registration for a named provider id.
     *
     * @param provider The provider registration instance
     */
    public void unregisterService(BundleProviderLoader provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "unregistering service " + provider);
        serviceProviders.unregister(provider);
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
    public Class<?> locate(String providerId) {
        // see if we have a registered match for this...getting just the first instance
        BundleProviderLoader loader = providers.getLoader(providerId);
        if (loader != null) {
            try {
                // try to load this.  We always return null
                return loader.loadClass();
            } catch (Exception e) {
                e.printStackTrace();
                // just swallow this and return null.  The exception has already
                // been logged.
            }
        }
        // no match to return
        return null;
    }

    /**
     * Locate all class files that match a given provider id.
     *
     * @param providerId The target provider identifier.
     *
     * @return A List containing the class objects corresponding to the
     *         provider identifier.  Returns an empty list if no
     *         matching classes can be located.
     */
    public List<Class<?>> locateAll(String providerId) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        List<BundleProviderLoader> l = providers.getLoaders(providerId);
        // this returns null if nothing is found.
        if (l != null) {
            for (BundleProviderLoader c : l) {
                try {
                    classes.add(c.loadClass());
                } catch (Exception e) {
                    // just swallow this and proceed to the next.  The exception has
                    // already been logged.
                }
            }
        }
        return classes;
    }

    /**
     * Locate and instantiate an instance of a service provider
     * defined in the META-INF/services directory of tracked bundles.
     *
     * @param providerId The name of the target interface class.
     *
     * @return The service instance.  Returns null if no service defintions
     *         can be located.
     * @exception Exception Any classloading or other exceptions thrown during
     *                      the process of creating this service instance.
     */
    public Object getService(String providerId) throws Exception {
        // see if we have a registered match for this...getting just the first instance
        BundleProviderLoader loader = serviceProviders.getLoader(providerId);
        if (loader != null) {
            // try to load this and create an instance.  Any/all exceptions get
            // thrown here
            return loader.createInstance();
        }
        // no match to return
        return null;
    }

    /**
     * Locate all services that match a given provider id and create instances.
     *
     * @param providerId The target provider identifier.
     *
     * @return A List containing the instances corresponding to the
     *         provider identifier.  Returns an empty list if no
     *         matching classes can be located or created
     */
    public List<Object> getServices(String providerId) {
        List<Object> instances = new ArrayList<Object>();
        List<BundleProviderLoader> l = serviceProviders.getLoaders(providerId);
        // this returns null for nothing found
        if (l != null) {
            for (BundleProviderLoader c : l) {
                try {
                    instances.add(c.createInstance());
                } catch (Exception e) {
                    // just swallow this and proceed to the next.  The exception has
                    // already been logged.
                }
            }
        }
        return instances;
    }

    /**
     * Locate all services that match a given provider id and return the implementation
     * classes
     *
     * @param providerId The target provider identifier.
     *
     * @return A List containing the classes corresponding to the
     *         provider identifier.  Returns an empty list if no
     *         matching classes can be located.
     */
    public List<Class<?>> getServiceClasses(String providerId) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        List<BundleProviderLoader> l = serviceProviders.getLoaders(providerId);
        // this returns null for nothing found
        if (l != null) {
            for (BundleProviderLoader c : l) {
                try {
                    classes.add(c.loadClass());
                } catch (Exception e) {
                    e.printStackTrace();
                    // just swallow this and proceed to the next.  The exception has
                    // already been logged.
                }
            }
        }
        return classes;
    }

    /**
     * Locate and return the class for a service provider
     * defined in the META-INF/services directory of tracked bundles.
     *
     * @param providerId The name of the target interface class.
     *
     * @return The provider class.   Returns null if no service defintions
     *         can be located.
     * @exception ClassNotFoundException Any classloading or other exceptions thrown during
     *                      the process of loading this service provider class.
     */
    public Class<?> getServiceClass(String providerId) throws ClassNotFoundException {
        // see if we have a registered match for this...getting just the first instance
        BundleProviderLoader loader = serviceProviders.getLoader(providerId);
        if (loader != null) {
            // try to load this and create an instance.  Any/all exceptions get
            // thrown here
            return loader.loadClass();
        }
        // no match to return
        return null;
    }

    /**
     * Holder class for information about a given collection of
     * id to provider mappings.  Used for both the providers and
     * the services.
     */
    private class SPIRegistry {
        private Map<String, List<BundleProviderLoader>> registry;


        /**
         * Register an individual provivider item by its provider identifier.
         *
         * @param provider The loader used to resolve the provider class.
         */
        public synchronized void register(BundleProviderLoader provider) {
            // if this is the first registration, create the mapping table
            if (registry == null) {
                registry = new HashMap<String, List<BundleProviderLoader>>();
            }

            String providerId = provider.id();

            // the providers are stored as a list...we use the first one registered
            // when asked to locate.
            List<BundleProviderLoader> l = registry.get(providerId);
            if (l ==  null) {
                l = new ArrayList<BundleProviderLoader>(2);
                registry.put(providerId, l);
            }
            l.add(provider);
            Collections.sort(l);
        }

        /**
         * Remove a provider registration for a named provider id.
         *
         * @param provider The provider registration instance
         */
        public synchronized void unregister(BundleProviderLoader provider) {
            if (registry != null) {
                // this is stored as a list.  Just remove using the registration information
                // This may move a different provider to the front of the list.
                List<BundleProviderLoader> l = registry.get(provider.id());
                if (l != null) {
                    l.remove(provider);
                }
            }
        }

        private synchronized BundleProviderLoader getLoader(String id) {
            // synchronize on the registry instance
            if (registry != null) {
                if (log.isLoggable(Level.FINE))
                    log.fine("registry: " + registry);
                // return the first match, if any
                List<BundleProviderLoader> list = registry.get(id);
                if (list != null && !list.isEmpty()) {
                    return list.get(0);
                }
            }
            // no match here
            return null;
        }

        private synchronized List<BundleProviderLoader> getLoaders(String id) {
            if (registry != null) {
                // if we have matches, return a copy of what we currently have
                // to create a safe local copy.
                List<BundleProviderLoader> list = registry.get(id);
                if (list != null && !list.isEmpty()) {
                    return new ArrayList<BundleProviderLoader>(list);
                }
            }
            // no match here
            return null;
        }
    }


}

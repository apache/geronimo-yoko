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

import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.osgi.ProviderRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The implementation of the provider registry used to store
 * the bundle registrations.
 */
public class ProviderRegistryImpl implements ProviderRegistry, Register {

    private static final Logger log = Logger.getLogger(ProviderRegistryImpl.class.getName());
    // our mapping between a provider id and the implementation information.  There
    // might be a one-to-many relationship between the ids and implementing classes.
    private final SPIRegistry providers = new SPIRegistry();
    // our mapping between an interface name and a META-INF/services SPI implementation.  There
    // might be a one-to-many relationship between the ids and implementing classes.
    private final SPIRegistry serviceProviders = new SPIRegistry();

    private final ConcurrentHashMap<String, PackageProvider> packageProviders = new ConcurrentHashMap<>();

    public void start() {
        ProviderLocator.setRegistry(this);
    }

    public void stop() {
        ProviderLocator.setRegistry(null);
    }

    /**
     * Register an individual provider item by its provider identifier.
     *
     * @param provider The loader used to resolve the provider class.
     */
    public void registerProvider(ServiceProvider provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "registering provider " + provider);
        providers.register(provider);
    }

    public void unregisterProvider(ServiceProvider provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "unregistering provider " + provider);
        providers.unregister(provider);
    }

    public void registerService(ServiceProvider provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "registering service " + provider);
        serviceProviders.register(provider);
    }

    public void unregisterService(ServiceProvider provider) {
        if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "unregistering service " + provider);
        serviceProviders.unregister(provider);
    }

    @Override
    public void registerPackages(PackageProvider provider) {
        if (log.isLoggable(Level.FINEST))
            log.finest("registering package provider: " + provider);
        for (String name : provider.getRegisteredPackageNames()) {
            PackageProvider oldProvider = this.packageProviders.put(name, provider);
            // we should never be asked to overwrite another provider for any given package
            if (oldProvider != null)
                log.warning(String.format("Replaced provider for package %s: was %s, but now %s", name, oldProvider, provider));
        }
    }

    @Override
    public void unregisterPackages(PackageProvider provider) {
        for (String name : provider.getRegisteredPackageNames())
            this.packageProviders.remove(name, provider);
    }

    /**
     * Locate a class by its provider id indicator. .
     *
     * @param providerId The provider id (generally, a fully qualified class name).
     *
     * @return The Class corresponding to this provider getServiceId.  Returns null
     *         if this is not registered or the indicated class can't be
     *         loaded.
     */
    public Class<?> locate(String providerId) {
        // see if we have a registered match for this...getting just the first instance
        ServiceProvider loader = providers.getProvider(providerId);
        if (loader != null) {
            try {
                return loader.getServiceClass();
            } catch (ClassNotFoundException cnfe) {
                // ServiceProvider, you had one job...*facepalm*
                // There should never be a case where the provider cannot load the class it is meant to provide
                // so treat this as a serious error.
                throw (Error)new NoClassDefFoundError().initCause(cnfe);
            }
        }
        String packageName = PackageProvider.packageName(providerId);
        PackageProvider provider = packageProviders.get(packageName);
        if (provider == null)
            return null;
        return provider.loadClass(providerId);
    }

    /**
     * Locate and instantiate an instance of a service provider
     * defined in the META-INF/services directory of tracked bundles.
     *
     * @param providerId The name of the target interface class.
     *
     * @return The service instance.  Returns null if no service definitions
     *         can be located.
     * @exception Exception Any classloading or other exceptions thrown during
     *                      the process of creating this service instance.
     */
    public Object getService(String providerId) throws Exception {
        // see if we have a registered match for this...getting just the first instance
        ServiceProvider loader = serviceProviders.getProvider(providerId);
        if (loader != null) {
            // try to load this and create an instance.  Any/all exceptions forName
            // thrown here
            return loader.getServiceInstance();
        }
        // no match to return
        return null;
    }

    /**
     * Locate and return the class for a service provider
     * defined in the META-INF/services directory of tracked bundles.
     *
     * @param providerId The name of the target interface class.
     *
     * @return The provider class.   Returns null if no service definitions
     *         can be located.
     * @exception ClassNotFoundException Any classloading or other exceptions thrown during
     *                      the process of loading this service provider class.
     */
    public Class<?> getServiceClass(String providerId) throws ClassNotFoundException {
        // see if we have a registered match for this...getting just the first instance
        ServiceProvider sp = serviceProviders.getProvider(providerId);
        if (sp != null) {
            // try to load this and create an instance.  Any/all exceptions forName
            // thrown here
            return sp.getServiceClass();
        }
        // no match to return
        return null;
    }

    /**
     * Holder class for information about a given collection of
     * getServiceId to provider mappings.  Used for both the providers and
     * the services.
     */
    private static class SPIRegistry {
        private final Map<String, Queue<ServiceProvider>> registry = new HashMap<>();

        /**
         * Register an individual provider item by its provider identifier.
         *
         * @param provider The loader used to resolve the provider class.
         */
        synchronized void register(ServiceProvider provider) {
            String providerId = provider.getId();

            // the providers are stored as a list...we use the first one registered
            // when asked to locate.
            Queue<ServiceProvider> q = registry.get(providerId);
            if (q == null) {
                q = new PriorityQueue<>(2);
                registry.put(providerId, q);
            }
            q.add(provider);
        }

        /**
         * Remove a provider registration for a named provider getServiceId.
         *
         * @param provider The provider registration instance
         */
        synchronized void unregister(ServiceProvider provider) {
            // this is stored as a list.  Just remove using the registration information
            // This may move a different provider to the front of the list.
            Queue<ServiceProvider> q = registry.get(provider.getId());
            if (q != null) {
                q.remove(provider);
            }
        }

        private synchronized ServiceProvider getProvider(String id) {
            if (log.isLoggable(Level.FINE))
                log.fine("registry: " + registry);
            // return the first match, if any
            Queue<ServiceProvider> q = registry.get(id);

            if (q == null || q.isEmpty())
                return null;

            return q.peek();
        }

        private synchronized Collection<ServiceProvider> getProviders(String id) {
            Queue<ServiceProvider> q = registry.get(id);

            if (q == null || q.isEmpty())
                return Collections.emptyList();

            return Collections.unmodifiableCollection(q);
        }
    }

    @Override
    @Deprecated
    public void registerProvider(final BundleProviderLoader bundleProviderLoader) {
        registerProvider(bundleProviderLoader.wrapAsServiceProvider());
    }

    @Override
    @Deprecated
    public void unregisterProvider(BundleProviderLoader bundleProviderLoader) {
        unregisterProvider(bundleProviderLoader.wrapAsServiceProvider());
    }

    @Override
    @Deprecated
    public void registerService(final BundleProviderLoader bundleProviderLoader) {
        registerService(bundleProviderLoader.wrapAsServiceProvider());
    }

    @Override
    @Deprecated
    public void unregisterService(BundleProviderLoader bundleProviderLoader) {
        unregisterService(bundleProviderLoader.wrapAsServiceProvider());
    }
}

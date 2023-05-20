/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.osgi.locator;

import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.osgi.ProviderRegistry;

import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.security.AccessController.doPrivileged;
import static java.util.Collections.newSetFromMap;
import static java.util.Collections.synchronizedSet;

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

    /** Store the known classloaders weakly to eliminate them from enquiries when stack-walking */
    private final Set<ClassLoader> knownLoaders = synchronizedSet(newSetFromMap(new WeakHashMap<>()));

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
    public <T> Class<T> locate(String providerId) {
        return this.<T>loadFromServiceProvider(providerId)
                .map(Optional::of)
                .orElseGet(() -> loadFromPackageProvider(providerId))
                .map(this::recordClass)
                .orElse(null);
    }

    private <T> Optional<Class<T>> loadFromServiceProvider(String providerId) {
        return Optional.of(providerId)
                .map(providers::getProvider)
                .map(l -> {
                    try {
                        return l.getServiceClass();
                    } catch (ClassNotFoundException cnfe) {
                        // ServiceProvider, you had one job...*facepalm*
                        // Should never happen so treat as a serious error.
                        throw (Error) new NoClassDefFoundError().initCause(cnfe);
                    }});
    }

    private <T> Optional<Class<T>> loadFromPackageProvider(String providerId) {
        return Optional.of(providerId)
                .map(PackageProvider::packageName)
                .map(packageProviders::get)
                .map(provider -> provider.loadClass(providerId));
    }

    /**
     * Locate and instantiate an instance of a service provider
     * defined in the META-INF/services directory of tracked bundles.
     *
     * @param providerId The name of the target interface class.
     *
     * @return The service instance.  Returns null if no service definitions
     *         can be located.
     */
    public <T> T getService(String providerId) {
        // see if we have a registered match for this...getting just the first instance
        ServiceProvider loader = serviceProviders.getProvider(providerId);
        if (loader != null) {
            // try to load this and create an instance.  Any/all exceptions forName
            // thrown here
            try {
                return recordInstance(loader.getServiceInstance());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error trying to load service of type " + loader.getClassName(), e);
            }
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
     */
    public <T> Class<T> getServiceClass(String providerId) {
        // see if we have a registered match for this...getting just the first instance
        ServiceProvider sp = serviceProviders.getProvider(providerId);
        if (sp != null) {
            // try to load this and create an instance.  Any/all exceptions forName
            // thrown here
            try {
                return recordClass(sp.getServiceClass());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Error locating service class: " + sp.getClassName(), e);
            }
        }
        // no match to return
        return null;
    }

    private <T> T recordInstance(T t) {
        Optional.ofNullable(t)
                .map(Object::getClass)
                .map(this::recordClass);
        return t;
    }

    private <T> Class<T> recordClass(Class<T> cls) {
        Optional.ofNullable(cls)
                .map(c -> doPriv(c::getClassLoader))
                .map(this::recordLoader);
        return cls;
    }

    private ClassLoader recordLoader(ClassLoader loader) {
        Optional.ofNullable(loader).map(knownLoaders::add);
        return loader;
    }


    @Override
    public boolean isServiceClassLoader(ClassLoader loader) {
        return knownLoaders.contains(loader);
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

    private static <T> T doPriv(PrivilegedAction<T> action) { return doPrivileged(action); }
}

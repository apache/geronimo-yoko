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
package org.apache.yoko.osgi;

/**
 * The implementation of the factory registry used to store
 * the bundle registrations.
 */
public interface ProviderRegistry {
    /**
     * Locate a class by its factory id indicator. .
     *
     * @param factoryId The factory id (generally, a fully qualified class name).
     *
     * @return The Class corresponding to this factory id.  Returns null
     *         if this is not registered or the indicated class can't be
     *         loaded.
     */
    <T> Class<T> locate(String factoryId);
    
    /**
     * Locate and instantiate an instance of a service provider
     * defined in the META-INF/services directory of tracked bundles.
     *
     * @param providerId The name of the target interface class.
     *
     * @return The service instance. Returns null if no service definitions
     *         can be located.
     */
    <T> T getService(String providerId);


    /**
     * Locate and return the class for a service provider
     * defined in the META-INF/services directory of tracked bundles.
     *
     * @param providerId The name of the target interface class.
     *
     * @return The provider class. Returns null if no service definitions
     *         can be located.
     */
    <T> Class<T> getServiceClass(String providerId);

    /**
     * Test whether a given class loader is associated with a provided service.
     */
    boolean isServiceClassLoader(ClassLoader loader);
}

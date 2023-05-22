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

import org.osgi.framework.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import static java.security.AccessController.doPrivileged;

/**
 * Holder class for located services information.
 */
@Deprecated
public class BundleProviderLoader implements Comparable<BundleProviderLoader> {
    // the class name for this provider
    private final String providerId;
    // the mapped class name of the provider.
    private final String providerClass;
    // the hosting bundle.
    private final Bundle bundle;

    private final int priority;

    /**
     * Create a loader for this registered provider.
     *
     * @param providerId The provider ID
     * @param providerClass The mapped class name of the provider.
     * @param bundle    The hosting bundle.
     * @param priority
     */
    public BundleProviderLoader(String providerId, String providerClass, Bundle bundle, int priority) {
        this.providerId = providerId;
        this.providerClass = providerClass;
        this.bundle = bundle;
        this.priority = priority;
    }

    /**
     * Load a provider class.
     *
     * @return The provider class from the target bundle.
     * @exception Exception
     */
    public Class<?> loadClass() throws ClassNotFoundException {
        try {
//                log(LogService.LOG_DEBUG, "loading class for: " + this);
            return bundle.loadClass(providerClass);
        } catch (ClassNotFoundException e) {
//                log(LogService.LOG_DEBUG, "exception caught while loading " + this, e);
            throw e;
        }
    }


    public String id() {
        return providerId;
    }

    @Override
    public String toString() {
        return "Provider interface=" + providerId + " , provider class=" + providerClass + ", bundle=" + bundle;
    }

    @Override
    public int hashCode() {
        return providerId.hashCode() + providerClass.hashCode() + (int)bundle.getBundleId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BundleProviderLoader) {
            return providerId.equals(((BundleProviderLoader)obj).providerId) &&
                   providerClass.equals(((BundleProviderLoader)obj).providerClass) &&
                   bundle.getBundleId() == ((BundleProviderLoader)obj).bundle.getBundleId();
        } else {
            return false;
        }
    }

    public int compareTo(BundleProviderLoader other) {
        return other.priority - priority;
    }

    ServiceProvider wrapAsServiceProvider() {
        return new ServiceProvider(
                new LocalFactory() {
                    @Override
                    public Class<?> forName(String clsName) throws ClassNotFoundException {
                        return BundleProviderLoader.this.loadClass();
                    }

                    @Override
                    public Object newInstance(Class cls) throws InstantiationException, IllegalAccessException {
                        try {
                            return doPrivEx(cls::getConstructor).newInstance();
                        } catch (PrivilegedActionException | InvocationTargetException e) {
                            throw (InstantiationException)(new InstantiationException().initCause(e));
                        }
                    }
                },
                providerId,
                providerClass,
                priority);
    }

    private static <T> T doPriv(PrivilegedAction<T> action) { return doPrivileged(action); }
    private static <T> T doPrivEx(PrivilegedExceptionAction<T> action) throws PrivilegedActionException { return doPrivileged(action); }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.yoko.osgi.locator.activator;

import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.PackageProvider;
import org.apache.yoko.osgi.locator.Register;
import org.apache.yoko.osgi.locator.ServiceProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBundleActivator implements BundleActivator {
    public static final class Info {
        final String id;
        final String className;
        final int priority;

        public Info(String id, String className, int priority) {
            this.id = id;
            this.className = className;
            this.priority = priority;
        }

        public Info(String id, String className) {
            this(id, className, ServiceProvider.DEFAULT_PRIORITY);
        }

        public Info(String id, Class<?> implClass, int priority) {
            this(id, implClass.getName(), priority);
        }

        public Info(String id, Class<?> implClass) {
            this(id, implClass.getName());
        }

        public <T, U extends T> Info(Class<T> idClass, Class<U> implClass, int priority) {
            this(idClass.getName(), implClass.getName(), priority);
        }

        public <T, U extends T> Info(Class<T> idClass, Class<U> implClass) {
            this(idClass.getName(), implClass.getName());
        }

        public <T> Info(Class<T> svcClass, int priority) {
            this(svcClass, svcClass, priority);
        }

        public <T> Info(Class<T> svcClass) {
            this(svcClass, svcClass);
        }

    }
    private static final Info[] NO_INFO = {};

    private final LocalFactory localFactory;
    private final Info[] providerInfo;
    private final Info[] serviceInfo;
    private final String[] providedPackages;
    private ServiceTracker<Register, Register> tracker;
    private BundleContext context;
    private boolean registered;
    private final List<ServiceProvider> providerLoaders = new ArrayList<>();
    private final List<ServiceProvider> serviceLoaders = new ArrayList<>();
    private PackageProvider packageProvider;

    protected AbstractBundleActivator(LocalFactory localFactory, Info[] providerInfo, Info[] serviceInfo, String...providedPackages) {
        this.localFactory = localFactory;
        this.providerInfo = providerInfo;
        this.serviceInfo = serviceInfo;
        this.providedPackages = providedPackages;
    }

    protected AbstractBundleActivator(LocalFactory localFactory, String...providedPackages) {
        this(localFactory, NO_INFO, NO_INFO, providedPackages);
    }

    public void start(final BundleContext context) throws Exception {
        this.context = context;
        tracker = new ServiceTracker<>(context, Register.class, new ServiceTrackerCustomizer<Register, Register>() {

            public Register addingService(ServiceReference<Register> reference) {
                Register register = context.getService(reference);
                register(register);
                return register;
            }

            public void modifiedService(ServiceReference<Register> reference, Register service) {}

            public void removedService(ServiceReference<Register> reference, Register service) {}

        });
        tracker.open();
        Register register = tracker.getService();
        if (register != null) {
            register(register);
        }
    }

    private synchronized void register(Register register) {
        if (!registered) {
            registered = true;
            Bundle bundle = context.getBundle();
            for (Info info: providerInfo) {
                ServiceProvider sp = new ServiceProvider(localFactory, info.id, info.className, info.priority);
                providerLoaders.add(sp);
                register.registerProvider(sp);
            }
            for (Info info: serviceInfo) {
                ServiceProvider sp = new ServiceProvider(localFactory, info.id, info.className, info.priority);
                serviceLoaders.add(sp);
                register.registerService(sp);
            }
            if (providedPackages.length > 0) {
                packageProvider = new PackageProvider(localFactory, providedPackages);
                register.registerPackages(packageProvider);
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        Register register = tracker.getService();
        tracker.close();
        synchronized (this) {
            if (register != null && registered) {
                for (ServiceProvider cp: providerLoaders) {
                    register.unregisterProvider(cp);
                }
                for (ServiceProvider cp: serviceLoaders) {
                    register.unregisterService(cp);
                }
                if (packageProvider != null)
                    register.unregisterPackages(packageProvider);
            }
        }
    }
}

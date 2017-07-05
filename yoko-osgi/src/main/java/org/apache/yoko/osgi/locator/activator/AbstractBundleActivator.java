package org.apache.yoko.osgi.locator.activator;

import org.apache.yoko.osgi.locator.BundleProviderLoader;
import org.apache.yoko.osgi.locator.PackageProvider;
import org.apache.yoko.osgi.locator.Register;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBundleActivator implements BundleActivator {
    public static class Info {
        final String id;
        final String className;
        final int priority;

        public Info(String id, String className, int priority) {
            super();
            this.id = id;
            this.className = className;
            this.priority = priority;
        }

    }
    private static final Info[] NO_INFO = {};

    private final Info[] providerInfo;
    private final Info[] serviceInfo;
    private final String[] providedPackages;
    private ServiceTracker<Register, Register> tracker;
    private BundleContext context;
    private boolean registered;
    private final List<BundleProviderLoader> providerLoaders = new ArrayList<>();
    private final List<BundleProviderLoader> serviceLoaders = new ArrayList<>();
    private PackageProvider packageProvider;

    protected AbstractBundleActivator(Info[] providerInfo, Info[] serviceInfo, String...providedPackages) {
        this.providerInfo = providerInfo;
        this.serviceInfo = serviceInfo;
        this.providedPackages = providedPackages;
    }

    protected AbstractBundleActivator(String...providedPackages) {
        this(NO_INFO, NO_INFO, providedPackages);
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
            for (Info classInfo: providerInfo) {
                BundleProviderLoader loader = new BundleProviderLoader(classInfo.id, classInfo.className, bundle, classInfo.priority);
                providerLoaders.add(loader);
                register.registerProvider(loader);
            }
            for (Info classInfo: serviceInfo) {
                BundleProviderLoader loader = new BundleProviderLoader(classInfo.id, classInfo.className, bundle, classInfo.priority);
                serviceLoaders.add(loader);
                register.registerService(loader);
            }
            if (providedPackages.length > 0) {
                packageProvider = new PackageProvider(bundle, providedPackages);
                register.registerPackages(packageProvider);
            }
        }
    }

    public void stop(BundleContext context) throws Exception {
        Register register = tracker.getService();
        tracker.close();
        synchronized (this) {
            if (register != null && registered) {
                for (BundleProviderLoader loader: providerLoaders) {
                    register.unregisterProvider(loader);
                }
                for (BundleProviderLoader loader: serviceLoaders) {
                    register.unregisterService(loader);
                }
                if (packageProvider != null)
                    register.unregisterPackages(packageProvider);
            }
        }
    }
}

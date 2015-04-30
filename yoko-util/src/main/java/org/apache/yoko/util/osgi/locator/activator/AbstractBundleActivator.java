package org.apache.yoko.util.osgi.locator.activator;

import java.util.ArrayList;
import java.util.List;

import org.apache.yoko.util.osgi.locator.BundleProviderLoader;
import org.apache.yoko.util.osgi.locator.Register;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

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
	private final Info[] providerInfo;
    private final Info[] serviceInfo;
	private ServiceTracker<Register, Register> tracker;
	private BundleContext context;
	private boolean registered;
	private final List<BundleProviderLoader> providerLoaders = new ArrayList<BundleProviderLoader>();
	private final List<BundleProviderLoader> serviceLoaders = new ArrayList<BundleProviderLoader>();
	
	public AbstractBundleActivator(Info[] providerInfo, Info[] serviceInfo) {
		this.providerInfo = providerInfo;
		this.serviceInfo = serviceInfo;
	}

	public void start(final BundleContext context) throws Exception {
		this.context = context;
		tracker = new ServiceTracker<Register, Register>(context, Register.class, new ServiceTrackerCustomizer<Register, Register>() {

			public Register addingService(ServiceReference<Register> reference) {
				Register register = context.getService(reference);
				register(register);
				return register;
			}

			public void modifiedService(ServiceReference<Register> reference,
					Register service) {
				// TODO Auto-generated method stub
				
			}

			public void removedService(ServiceReference<Register> reference,
					Register service) {
				// TODO Auto-generated method stub
				
			}
			
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
			}
		}

	}

}

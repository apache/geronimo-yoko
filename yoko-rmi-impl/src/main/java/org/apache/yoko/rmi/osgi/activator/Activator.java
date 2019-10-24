package org.apache.yoko.rmi.osgi.activator;

import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.ProviderRegistryImpl;
import org.apache.yoko.osgi.locator.Register;
import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class Activator extends AbstractBundleActivator {
    private enum MyLocalFactory implements LocalFactory {
        INSTANCE;
        @Override
        public Class<?> forName(String clsName) throws ClassNotFoundException {
            return Class.forName(clsName);
        }

        @Override
        public Object newInstance(Class cls) throws InstantiationException, IllegalAccessException {
            return cls.newInstance();
        }
    }

    private ServiceRegistration<Register> sr;
    private ProviderRegistryImpl register;
    
    public Activator() {
        super(MyLocalFactory.INSTANCE,
                new Info[] {
                        new Info(org.omg.stub.java.rmi._Remote_Stub.class),
                        new Info(javax.rmi.CORBA.Stub.class)
                },
                new Info[] {
                        new Info("javax.rmi.CORBA.PortableRemoteObjectClass",           org.apache.yoko.rmi.impl.PortableRemoteObjectImpl.class),
                        new Info("javax.rmi.CORBA.UtilClass",                           org.apache.yoko.rmi.impl.UtilImpl.class),
                        new Info("javax.rmi.CORBA.StubClass",                           org.apache.yoko.rmi.impl.StubImpl.class),
                        new Info("org.apache.yoko.rmi.PortableRemoteObjectExtClass",    org.apache.yoko.rmi.impl.PortableRemoteObjectExtImpl.class),
                        new Info("org.apache.yoko.rmi.RMIStubInitializerClass",         org.apache.yoko.rmi.impl.RMIStubInitializer.class)
                });
    }

    @Override
    public void start(BundleContext context) throws Exception {
        register = new ProviderRegistryImpl();
        register.start();
        sr = context.registerService(Register.class, register, null);
        super.start(context);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        sr.unregister();
        register.stop();
    }
}

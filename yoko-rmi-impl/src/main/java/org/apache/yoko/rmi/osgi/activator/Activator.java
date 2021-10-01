package org.apache.yoko.rmi.osgi.activator;

import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.ProviderRegistryImpl;
import org.apache.yoko.osgi.locator.Register;
import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;
import org.apache.yoko.rmi.impl.PortableRemoteObjectExtImpl;
import org.apache.yoko.rmi.impl.PortableRemoteObjectImpl;
import org.apache.yoko.rmi.impl.RMIStubInitializer;
import org.apache.yoko.rmi.impl.StubImpl;
import org.apache.yoko.rmi.impl.UtilImpl;
import org.omg.stub.java.rmi._Remote_Stub;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import javax.rmi.CORBA.Stub;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableMap;

public final class Activator extends AbstractBundleActivator {
    private static final Map<Class<?>, Supplier<?>> CONSTRUCTOR_MAP = unmodifiableMap(new HashMap<Class<?>, Supplier<?>>() {{
        put(_Remote_Stub.class, _Remote_Stub::new);
        put(PortableRemoteObjectImpl.class, PortableRemoteObjectImpl::new);
        put(UtilImpl.class, UtilImpl::new);
        put(StubImpl.class, StubImpl::new);
        put(PortableRemoteObjectExtImpl.class, PortableRemoteObjectExtImpl::new);
        put(RMIStubInitializer.class, RMIStubInitializer::new);
    }});

    private enum MyLocalFactory implements LocalFactory {
        INSTANCE;
        @Override
        public Class<?> forName(String clsName) throws ClassNotFoundException {
            switch (clsName) {
            case "org.omg.stub.java.rmi._Remote_Stub": return _Remote_Stub.class;
            case "javax.rmi.CORBA.Stub": return Stub.class;
            case "org.apache.yoko.rmi.impl.PortableRemoteObjectImpl": return PortableRemoteObjectImpl.class;
            case "org.apache.yoko.rmi.impl.UtilImpl": return UtilImpl.class;
            case "org.apache.yoko.rmi.impl.StubImpl": return StubImpl.class;
            case "org.apache.yoko.rmi.impl.PortableRemoteObjectExtImpl": return PortableRemoteObjectExtImpl.class;
            case "org.apache.yoko.rmi.impl.RMIStubInitializer": return RMIStubInitializer.class;
            }
            throw new ClassNotFoundException(clsName);
        }

        @Override
        public Object newInstance(Class cls) throws IllegalAccessException {
            return Optional
                    .ofNullable(CONSTRUCTOR_MAP.get(cls))
                    .map(Supplier::get)
                    .orElseThrow(() -> new IllegalAccessException("Cannot instantiate " + cls.getName()));
        }
    }

    private ServiceRegistration<Register> sr;
    private ProviderRegistryImpl register;

    public Activator() {
        super(MyLocalFactory.INSTANCE,
                new Info[] {
                        new Info(_Remote_Stub.class),
                        new Info(Stub.class)
                },
                new Info[] {
                        new Info("javax.rmi.CORBA.PortableRemoteObjectClass",           PortableRemoteObjectImpl.class),
                        new Info("javax.rmi.CORBA.UtilClass",                           UtilImpl.class),
                        new Info("javax.rmi.CORBA.StubClass",                           StubImpl.class),
                        new Info("org.apache.yoko.rmi.PortableRemoteObjectExtClass",    PortableRemoteObjectExtImpl.class),
                        new Info("org.apache.yoko.rmi.RMIStubInitializerClass",         RMIStubInitializer.class)
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

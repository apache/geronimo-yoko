package org.apache.yoko.rmi.osgi.activator;

import javax.rmi.CORBA.Stub;

import org.apache.yoko.osgi.locator.ProviderRegistryImpl;
import org.apache.yoko.osgi.locator.Register;
import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;
import org.omg.stub.java.rmi._Remote_Stub;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator extends AbstractBundleActivator {

    private ServiceRegistration<Register> sr;
    private ProviderRegistryImpl register;
    
    public Activator() {
        super(new Info[] {
                   new Info(_Remote_Stub.class.getName(), _Remote_Stub.class.getName(), 1),
                   new Info(Stub.class.getName(), Stub.class.getName(), 1)
               }, 
               new Info[] {
                   new Info("javax.rmi.CORBA.PortableRemoteObjectClass", "org.apache.yoko.rmi.impl.PortableRemoteObjectImpl", 1),
                   new Info("javax.rmi.CORBA.UtilClass", "org.apache.yoko.rmi.impl.UtilImpl", 1),
                   new Info("org.apache.yoko.rmi.PortableRemoteObjectExtClass", "org.apache.yoko.rmi.impl.PortableRemoteObjectExtImpl", 1),
                   new Info("org.apache.yoko.rmi.RMIStubInitializerClass", "org.apache.yoko.rmi.impl.RMIStubInitializer", 1),
                   new Info("javax.rmi.CORBA.StubClass", "org.apache.yoko.rmi.impl.StubImpl", 1)
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

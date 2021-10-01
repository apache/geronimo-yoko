package org.apache.yoko.rmispec.util;

import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;

public final class Activator extends AbstractBundleActivator {
    private enum MyLocalFactory implements LocalFactory {
        INSTANCE;
        @Override
        public Class<?> forName(String clsName) throws ClassNotFoundException {
            return Class.forName(clsName);
        }

        @Override
        public Object newInstance(Class cls) throws IllegalAccessException {
            // no Info objects are passed to the activator's parent constructor
            // so no service instances can be requested
            throw new IllegalAccessException("Cannot instantiate class " + cls);
        }
    }

    public Activator() {
        super(MyLocalFactory.INSTANCE,
                "javax.rmi",
                "javax.rmi.CORBA",
                "org.omg.stub.java.rmi");
    }
}

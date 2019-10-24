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
        public Object newInstance(Class cls) throws InstantiationException, IllegalAccessException {
            return cls.newInstance();
        }
    }

    public Activator() {
        super(MyLocalFactory.INSTANCE,
                "javax.rmi",
                "javax.rmi.CORBA",
                "org.omg.stub.java.rmi");
    }
}

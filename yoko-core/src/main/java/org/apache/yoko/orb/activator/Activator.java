package org.apache.yoko.orb.activator;

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
                new Info[] {
                    new Info(org.apache.yoko.orb.CORBA.ORB.class),
                    new Info(org.apache.yoko.orb.CORBA.ORBSingleton.class)
                },
                new Info[] {
                    new Info("org.omg.CORBA.ORBClass", org.apache.yoko.orb.CORBA.ORB.class),
                    new Info("org.omg.CORBA.ORBSingletonClass", org.apache.yoko.orb.CORBA.ORBSingleton.class)
                }
        );
    }
}

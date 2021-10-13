package org.apache.yoko.orb.activator;

import org.apache.yoko.orb.CORBA.ORB;
import org.apache.yoko.orb.CORBA.ORBSingleton;
import org.apache.yoko.osgi.locator.LocalFactory;
import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;

public final class Activator extends AbstractBundleActivator {
    private enum MyLocalFactory implements LocalFactory {
        INSTANCE;
        @Override
        public Class<?> forName(String clsName) throws ClassNotFoundException {
            switch (clsName) {
            case "org.apache.yoko.orb.CORBA.ORB": return ORB.class;
            case "org.apache.yoko.orb.CORBA.ORBSingleton": return ORBSingleton.class;
            }
            throw new ClassNotFoundException(clsName);
         }

        @Override
        public Object newInstance(Class cls) throws IllegalAccessException {
            if (cls == ORB.class) return new ORB();
            if (cls == ORBSingleton.class) return new ORBSingleton();
            throw new IllegalAccessException("Cannot instantiate class " + cls.getName());
        }
    }

    public Activator() {
        super(MyLocalFactory.INSTANCE,
                new Info[] {
                    new Info(ORB.class),
                    new Info(ORBSingleton.class)
                },
                new Info[] {
                    new Info("org.omg.CORBA.ORBClass", ORB.class),
                    new Info("org.omg.CORBA.ORBSingletonClass", ORBSingleton.class)
                }
        );
    }
}

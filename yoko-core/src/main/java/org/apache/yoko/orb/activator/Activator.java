package org.apache.yoko.orb.activator;

import org.apache.yoko.util.osgi.locator.activator.AbstractBundleActivator;

public class Activator extends AbstractBundleActivator {

    public Activator() {
        super(new Info[] {new Info("org.apache.yoko.orb.CORBA.ORB", "org.apache.yoko.orb.CORBA.ORB", 1),
                new Info("org.apache.yoko.orb.CORBA.ORBSingleton", "org.apache.yoko.orb.CORBA.ORBSingleton", 1)
        },
                new Info[] {new Info("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB", 1),
                new Info("org.omg.CORBA.ORBSingletonClass", "org.apache.yoko.orb.CORBA.ORBSingleton", 1)
        });
    }

}

package org.apache.yoko.rmispec.util;

import org.apache.yoko.osgi.locator.activator.AbstractBundleActivator;

public class Activator extends AbstractBundleActivator {

    public Activator() {
        super(
                "javax.rmi",
                "javax.rmi.CORBA",
                "org.omg.stub.java.rmi");
    }

}

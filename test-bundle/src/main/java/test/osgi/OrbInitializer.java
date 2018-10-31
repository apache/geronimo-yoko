package test.osgi;

import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Properties;

public class OrbInitializer implements BundleActivator {
    @Override
    public void start(BundleContext bundleContext) {
        System.out.println("======== Activating test-bundle with ORBInitializer.start() ========");
        System.out.println("org.omg.CORBA.ORB.class = " + ORB.class);
        System.out.println("org.omg.CORBA.ORB.class.getClassLoader() = " + ORB.class.getClassLoader());
        {
            System.out.println();
            System.out.println(" ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻ ");
            System.out.println();
            System.out.println("Initialize the ORBSingleton");
            final ORB singletonOrb = ORB.init();
            System.out.println("Got an orb --- yay! orb = " + singletonOrb);
            try {
                singletonOrb.destroy();
                assert false;
            } catch (NO_IMPLEMENT expected) {}
        }
        {
            System.out.println();
            System.out.println(" ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻ ");
            System.out.println();
            System.out.println("Initialize an ORB with no properties");
            final ORB noPropsOrb = ORB.init((String[]) null, null);
            System.out.println("Got an orb --- yay! orb = " + noPropsOrb);
        }
        {
            System.out.println();
            System.out.println(" ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻ ");
            System.out.println();
            System.out.println("Initialize the ORBSingleton");
            final ORB singletonOrb = ORB.init();
            System.out.println("Got an orb --- yay! orb = " + singletonOrb);
            try {
                singletonOrb.destroy();
                assert false;
            } catch (NO_IMPLEMENT expected) {}
        }
        {
            System.out.println();
            System.out.println(" ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻   ʕノ•ᴥ•ʔノ ︵ ┻━┻ ");
            System.out.println();
            System.out.println("Initialize an ORB with an ORB class property");
            final ORB singlePropOrb = ORB.init((String[]) null, new Properties() {{
                put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
            }});
            System.out.println("Got an orb --- yay! orb = " + singlePropOrb);
            System.out.println("======== Activated test-bundle with ORBInitializer.start() ========");
        }
    }

    @Override
    public void stop(BundleContext bundleContext) {}
}

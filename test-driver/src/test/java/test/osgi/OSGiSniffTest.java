package test.osgi;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES;

public class OSGiSniffTest {
    private enum Util {
        ;

        private static final Map<Integer, String> BUNDLE_STATES = new HashMap<Integer, String>() {{
            put(Bundle.UNINSTALLED, "UNINSTALLED");
            put(Bundle.ACTIVE, "ACTIVE");
            put(Bundle.STARTING, "STARTING");
            put(Bundle.STOPPING, "STOPPING");
            put(Bundle.INSTALLED, "INSTALLED");
            put(Bundle.RESOLVED, "RESOLVED");
        }};

        private static String describe(Bundle b) {
            return String.format("%s@%02d[%s]", b.getSymbolicName(), b.getBundleId(), BUNDLE_STATES.get(b.getState()));
        }
    }
    private final SortedSet<Bundle> bundles = new TreeSet<>();
    private Framework osgiFramework;
    private BundleContext bundleContext;

    @Test
    public void testBundles()throws Exception {
        installBundles();
        startBundles();
    }

    private void startBundles() throws BundleException {
        for (Bundle bundle : bundles) {
            bundle.start();
            System.out.println("Started bundle " + Util.describe(bundle));
        }
    }

    private void installBundles() throws IOException {
        Path dir = Paths.get("../tmp/gatherBundles").toRealPath();
        System.out.println(dir.toAbsolutePath());
        DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*");
        for (Path file : files) {
            try {
                System.out.println(file);
                String url = file.toUri().toURL().toString();
                System.out.println(url);
                Bundle bundle = bundleContext.installBundle(url);
                System.out.println("Installed bundle " + Util.describe(bundle));
                bundles.add(bundle);
            } catch (Exception e) {
                System.out.println("Install of bundle failed with exception: " + e);
            }
        }
    }

    @Before
    public void startOSGiFramework() throws BundleException {
        // start the Util framework
        ServiceLoader<FrameworkFactory> ffs = ServiceLoader.load(FrameworkFactory.class);
        FrameworkFactory ff = ffs.iterator().next();
        Map<String, String> config = new HashMap<>();
        // add some params to config ...
        config.put(FRAMEWORK_SYSTEMPACKAGES, ""+
                "javax.security.auth," +
                "javax.security.auth.login," +
                "javax.security.auth.x500");
        config.put("org.eclipse.update.reconcile", "false");
        osgiFramework = ff.newFramework(config);
        osgiFramework.start();

        bundleContext = osgiFramework.getBundleContext();


        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getBundleId() == 00) continue; // leave the system bundle alone
            System.out.println(Util.describe(bundle));
            bundle.stop();
        }

        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getBundleId() == 00) continue; // leave the system bundle alone
            System.out.println(Util.describe(bundle));
            bundle.uninstall();
            System.out.println(Util.describe(bundle));
        }
    }

}

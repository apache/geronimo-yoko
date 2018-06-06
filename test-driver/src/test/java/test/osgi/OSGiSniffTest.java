package test.osgi;

import org.junit.Before;
import org.junit.BeforeClass;
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
import java.util.Set;
import java.util.TreeSet;

import static org.osgi.framework.Constants.FRAMEWORK_SYSTEMPACKAGES;

public class OSGiSniffTest {

    private static Set<Path> testBundlePaths;
    private static Set<Path> yokoBundlePaths;

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
    private Framework osgiFramework;
    private BundleContext bundleContext;

    @BeforeClass
    public static void findBundlePaths() throws IOException {
        Path dir = Paths.get("../tmp/gatherBundles").toRealPath();
        System.out.println(dir.toAbsolutePath());
        testBundlePaths = new TreeSet<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*test*")) {
            System.out.println("Finding test bundles:");
            for (Path file : files) {
                System.out.println("\t" + file);
                testBundlePaths.add(file);
            }
        }
        yokoBundlePaths = new TreeSet<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(dir, "*")) {
            System.out.println("Finding impl:");
            for (Path file : files) {
                if (testBundlePaths.contains(file))
                    continue;
                System.out.println("\t" + file);
                yokoBundlePaths.add(file);
            }
        }
    }

    @Before
    public void startYoko() throws Exception {
        startOSGiFramework();
        startBundles(yokoBundlePaths);
    }

    @Test
    public void testBundles() throws Throwable {
        try {
            startBundles(testBundlePaths);
        } catch (BundleException be) {
            throw be.getCause();
        }
    }

    private void startOSGiFramework() throws Exception {
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

    private void startBundles(Set<Path> bundlePaths) throws BundleException {
        Set<Bundle> bundles = new TreeSet<>();
        // first install all the bundles
        for (Path file : bundlePaths) {
            try {
                System.out.println(file);
                String url = file.toUri().toURL().toString();
                System.out.println(url);
                Bundle bundle1 = bundleContext.installBundle(url);
                System.out.println("Installed bundle " + Util.describe(bundle1));
                bundles.add(bundle1);
            } catch (Exception e) {
                System.out.println("Install of bundle failed with exception: " + e);
            }
        }
        // then start them
        for (Bundle bundle : bundles) {
            bundle.start();
            System.out.println("Started bundle " + Util.describe(bundle));
        }
    }
}

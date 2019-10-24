package org.apache.yoko.osgi.locator;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class PackageProviderTest {
    public static final LocalFactory CLASS_FINDER = new LocalFactory() {
        @Override
        public Class<?> forName(String className) throws ClassNotFoundException {
            return Class.forName(className);
        }

        @Override
        public Object newInstance(Class cls) throws InstantiationException, IllegalAccessException {
            return cls.newInstance();
        }
    };

    @Test
    public void testUnknownPackage() throws Exception {
        PackageProvider provider = new PackageProvider(CLASS_FINDER);
        assertThat("Provider should deny knowledge of package",
                provider.fromUnregisteredPackage("org.omg.CosNaming.NamingContext"),
                is(true));
    }

    @Test
    public void testKnownPackage() {
        PackageProvider provider = new PackageProvider(CLASS_FINDER,"org.omg.CosNaming");
        assertThat("Provider should admit knowledge of package",
                provider.fromUnregisteredPackage("org.omg.CosNaming.NotARealClass"),
                is(false));
    }

    @Test
    public void testLoadClassComplainsAboutUnknownPackage() throws Exception {
        PackageProvider provider = new PackageProvider(CLASS_FINDER);
        assertThat( "Provider should not load classes from packages it does not know about",
                provider.loadClass("org.omg.CosNaming.NamingContext"),
                is(nullValue()));
    }

    @Test
    public void testLoadClassFailsOnPackageMismatch() throws Exception {
        PackageProvider provider = new PackageProvider(CLASS_FINDER,"org.omg.CosNaming");
        assertThat("getImplClass should try to load class and fail",
                provider.loadClass("org.omg.CosNaming.NotARealClass"),
                is(nullValue()));
    }
}

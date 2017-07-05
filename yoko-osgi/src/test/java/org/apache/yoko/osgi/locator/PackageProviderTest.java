package org.apache.yoko.osgi.locator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.Bundle;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PackageProviderTest {
    Bundle myBundle;

    @Before
    public void createMockBundle() throws Exception {
        myBundle = mock(Bundle.class);
        // teach this mock to load classes
        when(myBundle.loadClass("")).thenAnswer(
                new Answer<Class<?>>() {
                    public Class<?> answer(InvocationOnMock invocation) throws Throwable {
                        String name = invocation.getArgument(0);
                        return Class.forName(name);
                    }
                }
        );
    }

    @Test
    public void testUnknownPackage() throws Exception {
        PackageProvider provider = new PackageProvider(myBundle);
        assertThat("Provider should deny knowledge of package",
                provider.fromUnregisteredPackage("org.omg.CosNaming.NamingContext"),
                is(true));
    }

    @Test
    public void testKnownPackage() {
        PackageProvider provider = new PackageProvider(myBundle, "org.omg.CosNaming");
        assertThat("Provider should admit knowledge of package",
                provider.fromUnregisteredPackage("org.omg.CosNaming.NotARealClass"),
                is(false));
    }

    @Test
    public void testLoadClassComplainsAboutUnknownPackage() throws Exception {
        PackageProvider provider = new PackageProvider(myBundle);
        assertThat( "Provider should not load classes from packages it does not know about",
                provider.loadClass("org.omg.CosNaming.NamingContext"),
                is(nullValue()));
    }

    @Test
    public void testLoadClassFailsOnPackageMismatch() throws Exception {
        PackageProvider provider = new PackageProvider(myBundle, "org.omg.CosNaming");
        assertThat("loadClass should try to load class and fail",
                provider.loadClass("org.omg.CosNaming.NotARealClass"),
                is(nullValue()));
    }
}

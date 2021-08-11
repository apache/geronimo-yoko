package acme;

import org.apache.yoko.util.AssertionFailed;
import testify.io.EasyCloseable;
import testify.util.Throw;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;

/**
 * A simplified mechanism (with a lot of debug) for loading classes from the specialised test class paths.
 * Ensure that the build.gradle for this project sets up the class paths appropriately.
 */
public enum Loader {
    V1,
    V2;
    final URLClassLoader loader;

    Loader() {
        this.loader = getLoader("acme.loader." + name().toLowerCase() + ".path");
    }

    private static URLClassLoader getLoader(String classpathProp) {
        // NOTE:
        // Directory URLs for URLClassLoader need a trailing slash.
        // Paths.get(path).toURI().toURL() sets this up nicely.
        String classpath = System.getProperty(classpathProp);
        assertThat(classpath, not(nullValue()));
        assertThat(classpath, not(emptyString()));
        URL[] urls = Stream.of(classpath.split(File.pathSeparator))
                .sequential()
                .map(Paths::get)
                .map(Path::toUri)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException("Invalid url: " + uri, e);
                    }
                })
                .collect(Collectors.toList())
                .toArray(new URL[]{});
        ClassLoader parentLoader = Loader.class.getClassLoader();
        return new URLClassLoader(urls, parentLoader);
    }

    public <T> Class<? extends T> loadClass(String className) {
        try {
            return (Class<? extends T>) loader.loadClass(className);
        } catch (ClassNotFoundException cnfe) {
            String resourceName = className.replace('.', '/') + ".class";
            URL url = loader.findResource(resourceName);
            StringBuilder sb = new StringBuilder();
            sb.append("\n### Could not load class " + className);
            sb.append("\n### URLs:");
            Stream.of(loader.getURLs()).map(URL::toString).map("\n\t### "::concat).forEach(sb::append);
            sb.append("\n### loader.findResource(").append(resourceName).append(") returns ").append(url);
            throw new AssertionFailed("Caught " + cnfe + sb, cnfe);
        }
    }

    public <T> T deserializeFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais) {
                    @Override
                    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                        return Class.forName(desc.getName(), false, loader);
                    }
                };
        ) {
            return (T)ois.readObject();
        }
    }

    public <T> Constructor<T> getConstructor(String className, Class<?>...paramTypes) {
        return (Constructor<T>) Throw.invokeWithImpunity(loadClass(className)::getConstructor, paramTypes);
    }

    public EasyCloseable setAsThreadContextClassLoader() {
        final Thread t = Thread.currentThread();
        final ClassLoader l = t.getContextClassLoader();
        t.setContextClassLoader(this.loader);
        return () -> t.setContextClassLoader(l);
    }
}

package org.apache.yoko.osgi.locator;

public interface LocalFactory {
    /** Implement using {@link Class#forName(String)} <em>within the target bundle</em> to avoid security issues. */
    Class<?> forName(String clsName) throws ClassNotFoundException;

    /** Implement using {@link Class#newInstance()} <em>within the target bundle</em> to avoid security issues. */
    Object newInstance(Class cls) throws InstantiationException, IllegalAccessException;
}

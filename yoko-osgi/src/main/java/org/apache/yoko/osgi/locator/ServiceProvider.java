package org.apache.yoko.osgi.locator;

import java.util.Objects;

public final class ServiceProvider implements Comparable<ServiceProvider> {
    public final static int DEFAULT_PRIORITY = 1;
    private final LocalFactory localFactory;
    private final String id;
    private final String className;
    private final int priority;

    public ServiceProvider(LocalFactory localFactory, String id, String className, int priority) {
        this.localFactory = localFactory;
        this.id = id;
        this.className = className;
        this.priority = priority;
    }

    private ServiceProvider(LocalFactory localFactory, String id, String className) {
        this(localFactory, id, className, ServiceProvider.DEFAULT_PRIORITY);
    }

    @SuppressWarnings("unused")
    public ServiceProvider(LocalFactory localFactory, String id, Class<?> implClass, int priority) {
        this(localFactory, id, implClass.getName(), priority);
    }

    private <T, U extends T> ServiceProvider(LocalFactory localFactory, Class<T> idClass, Class<U> implClass) {
        this(localFactory, idClass.getName(), implClass.getName());
    }

    @SuppressWarnings("unused")
    public <T> ServiceProvider(LocalFactory localFactory, Class<T> svcClass) {
        this(localFactory, svcClass, svcClass);
    }

    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    /** A higher value takes priority over a lower value */
    public int getPriority() {
        return priority;
    }

    public <T> Class<T> getServiceClass() throws ClassNotFoundException {
        return (Class<T>) localFactory.forName(className);
    }

    public <T> T getServiceInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return (T) localFactory.newInstance(getServiceClass());
    }

    @Override
    public boolean equals(Object theOther) {
        if (this.getClass() != theOther.getClass())
            return false;
        ServiceProvider that = (ServiceProvider) theOther;
        return Objects.equals(this.localFactory.getClass(), that.localFactory.getClass())
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.className, that.className)
                && Objects.equals(this.priority, that.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, className, priority);
    }

    @Override
    public int compareTo(ServiceProvider that) {
        return that.priority - this.priority;
    }

    @Override
    public String toString() {
        return String.format("Service id=%s class=%s priority=%d", id, className, priority);
    }
}

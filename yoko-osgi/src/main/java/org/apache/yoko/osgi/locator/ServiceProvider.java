package org.apache.yoko.osgi.locator;

import java.util.Objects;

public final class ServiceProvider implements Comparable<ServiceProvider> {
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

    public final String getId() {
        return id;
    }

    public final String getClassName() {
        return className;
    }

    /** A higher value takes priority over a lower value */
    public final int getPriority() {
        return priority;
    }

    public final Class<?> getServiceClass() throws ClassNotFoundException {
        return localFactory.forName(className);
    }

    public final Object getServiceInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return localFactory.newInstance(getServiceClass());
    }

    @Override
    public final boolean equals(Object theOther) {
        if (this.getClass() != theOther.getClass())
            return false;
        ServiceProvider that = (ServiceProvider) theOther;
        return Objects.equals(this.localFactory.getClass(), that.localFactory.getClass())
                && Objects.equals(this.id, that.id)
                && Objects.equals(this.className, that.className)
                && Objects.equals(this.priority, that.priority);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id, className, priority);
    }

    @Override
    public final int compareTo(ServiceProvider that) {
        return that.priority - this.priority;
    }

    @Override
    public final String toString() {
        return String.format("Service id=%s class=%s priority=%d", id, className, priority);
    }
}

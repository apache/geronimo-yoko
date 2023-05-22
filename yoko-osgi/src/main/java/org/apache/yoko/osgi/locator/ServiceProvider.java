/*
 * Copyright 2021 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
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

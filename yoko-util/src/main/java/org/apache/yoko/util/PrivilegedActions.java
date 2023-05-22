/*
 * Copyright 2022 IBM Corporation and others.
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
package org.apache.yoko.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static java.lang.Thread.currentThread;

public enum PrivilegedActions {
    ;
    public static final PrivilegedAction<Properties> GET_SYSPROPS = System::getProperties;
    public static final PrivilegedAction<Map<Object, Object>> GET_SYSPROPS_OR_EMPTY_MAP = () -> {
        try {
            return System.getProperties();
        } catch (SecurityException swallowed) {
            return Collections.emptyMap();
        }
    };

    public static final PrivilegedAction<ClassLoader> GET_CONTEXT_CLASS_LOADER = () -> currentThread().getContextClassLoader();

    public static PrivilegedAction<String> getSysProp(final String key) { return () -> System.getProperty(key); }

    public static PrivilegedAction<String> getSysProp(final String key, final String defaultValue) { return () -> System.getProperty(key, defaultValue); }

    public static PrivilegedAction<ClassLoader> getClassLoader(final Class<?> clz) { return clz::getClassLoader; }

    public static <T> PrivilegedExceptionAction<Constructor<T>> getNoArgConstructor(Class<T> clz) { return clz::getDeclaredConstructor; }

    public static PrivilegedExceptionAction<Method> getMethod(Class<?> type, String name, Class<?>...parameterTypes) {
        return () -> type.getMethod(name, parameterTypes);
    }

    public static PrivilegedExceptionAction<Method> getDeclaredMethod(Class<?> type, String name, Class<?>...parameterTypes) {
        return () -> type.getDeclaredMethod(name, parameterTypes);
    }

    public static <T> PrivilegedAction<T> action(PrivilegedAction<T> action) { return action; }
}

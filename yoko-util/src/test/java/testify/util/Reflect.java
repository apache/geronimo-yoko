/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.util;

import org.junit.platform.commons.support.ModifierSupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.ModifierSupport.isPublic;

public enum Reflect {
    ;

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Set<Class<?>> allowedParamTypes) {
        return (Constructor<T>) Stream.of(clazz.getConstructors())
        .filter(ModifierSupport::isPublic)
        .filter(ctor -> allowedParamTypes.containsAll(asList(ctor.getParameterTypes())))
        .findAny()
        .orElseThrow(() -> (Error)fail(clazz.getName() + " has no public constructors with only the allowed parameters:" + allowedParamTypes));
    }

    public static String getMatchingTypeName(Class<?> type, String pattern) {
        String fqname = type.getName();
        String name = type.getSimpleName();
        String prefix = fqname.substring(0, fqname.length() - name.length());
        return prefix + pattern.replace("*", name);
    }

    public static Class<?> getMatchingType(Class<?> type, String pattern) {
        try {
            String name = getMatchingTypeName(type, pattern);
            return Class.forName(name);
        }  catch (ClassNotFoundException e) {
            throw (Error) new NoClassDefFoundError(e.getMessage()).initCause(e);
        }
    }

    public static Constructor<?> getMatchingConstructor(String expectedTypeName, Class<?>... allowedConstructorParameterTypes) {
        Class<?> expectedType;
        try {
            expectedType = Class.forName(expectedTypeName);
            assertTrue(isPublic(expectedType), () -> expectedType.getName() + " must be public.");
            return getConstructor(expectedType, Sets.of(allowedConstructorParameterTypes));
        } catch (ClassNotFoundException e) {
            throw (Error) new NoClassDefFoundError(e.getMessage()).initCause(e);
        }
    }

    public static void setStaticField(Field f, Object object) {
        try {
            f.set(null, object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newMatchingInstance(Class<?> type, String pattern) {
        return newMatchingInstance(type, pattern, emptyMap());
    }

    public static <T> T newMatchingInstance(Class<?> type, String pattern, Map<Class<?>, Object> paramMap) {
        Class<T> implClass = (Class<T>) getMatchingType(type, pattern);
        Constructor<T> ctor = getConstructor(implClass, paramMap.keySet());
        try {
            return ctor.newInstance(Stream.of(ctor.getParameterTypes())
                    .map(paramMap::get)
                    .collect(toList())
                    .toArray(new Object[0]));
        } catch (InstantiationException|IllegalAccessException| InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

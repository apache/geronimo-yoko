/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package testify.util;

import org.junit.platform.commons.support.ModifierSupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.fail;

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
        return newInstance((Class<T>) getMatchingType(type, pattern), paramMap);
    }

    public static <T> T newInstance(Class<T> implClass, Map<Class<?>, Object> paramMap) {
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

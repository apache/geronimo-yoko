/*
 * Copyright 2023 IBM Corporation and others.
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
package testify.bus.key;

import testify.bus.Bus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Transmit a collection of <code>T</code> elements using a {@link TypeKey}
 * to transmit the elements as strings and a {@link StringListKey}
 * to transmit the collection as a string.
 * <br>
 * <em>N.B. the runtime collection type must have a public, no-args constructor</em>
 *
 * @param <T> the element type of the collection
 */
public interface CollectionKey<C extends Collection<T>, T> extends TypeKey<C> {
    testify.bus.Key<T> getElementKey();

    @Override
    default String stringify(C c) {
        Class<?> cType = c.getClass();
        String cTypeName = cType.getName();
        getConstructor(cType); // check there is a suitable constructor
        Stream<String> typeStream = Stream.of(cTypeName);
        Stream<String> elemStream = c.stream().map(getElementKey()::stringify);
        List<String> stringList = Stream.concat(typeStream, elemStream).collect(toList());
        return StringListKey.toString(stringList);
    }

    static <C> Constructor<C> getConstructor(Class<C> cType) {
        try {
            Constructor<C> ctor = cType.getConstructor();
            if (!Modifier.isPublic(ctor.getModifiers()))
                throw new Error("Supplied collection of type " + cType.getName() + " has a non-public no-args constructor");
            return ctor;
        } catch (NoSuchMethodException e) {
            throw new Error("Supplied collection of type " + cType.getName() + " does not have a no-args constructor", e);
        }
    }

    @Override
    default C unstringify(String s, Supplier<Bus> busSupplier) {
        List<String> strings = StringListKey.toList(s);
        final String cTypeName = strings.get(0);
        final Class<C> cType;
        try {
            Class<?> cls = Class.forName(cTypeName);
            if (!Collection.class.isAssignableFrom(cls)) throw new Error("Received class " + cTypeName + " is not a collection type");
            cType = (Class<C>)cls;
        } catch (ClassNotFoundException e) {
            throw new Error("Could not locate received collection class " + cTypeName, e);
        }
        final Constructor<C> ctor = getConstructor(cType);
        final C result;
        try {
            result = ctor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new Error("Could not create an instance of " + cTypeName, e);
        }
        strings.stream()
                .skip(1) // ignore the type name
                .map(str -> getElementKey().unstringify(str, busSupplier))
                .forEach(result::add);
        return result;
    }
}

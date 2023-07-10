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
package testify.io;

import org.opentest4j.AssertionFailedError;
import testify.io.Stringifiable.Unstringify;
import testify.util.function.RawOptional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isStatic;
import static org.junit.jupiter.api.Assertions.assertTrue;

public enum Stringifier {
    ;

    public interface SerializableConsumer<T> extends Consumer<T>, Serializable {}
    public interface SerializableSupplier<T> extends Supplier<T>, Serializable {}

    public static String stringify(Object payload) {
        return payload == null ? "null" : payload instanceof Stringifiable ?
                payload.getClass().getName() + " " + ((Stringifiable) payload).stringify() :
                "serial " + Base64.getEncoder().encodeToString(writeObject(payload));
    }

    public static <T> T unstringify(String string) {
        String discriminator = new Scanner(string).next();
        if ("null".equals(discriminator)) return null;
        String payload = string.substring(discriminator.length() + 1);
        if (discriminator.equals("serial")) return readSerializable(Base64.getDecoder().decode(payload));
        return readStringifiable(discriminator, payload);
    }

    private static <T> Function<String, T> findUnstringifier(Class<T> clazz) {
        Stream<Function<String, T>> functions = Stream.of(clazz.getDeclaredMethods())
                .filter(m -> m.getAnnotation(Unstringify.class) != null)
                .map(m -> s -> invoke(m, s));


        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(String.class);
            if (constructor.getAnnotation(Unstringify.class) != null) {
                functions = Stream.concat(functions, Stream.of(s -> invoke(constructor, s)));
            }
        } catch (NoSuchMethodException ignored) {
        }

        return functions.reduce((m,n) -> { throw new Error(clazz.getName() + " has multiple @Unstringify annotations"); })
                .orElseThrow(() -> new Error(clazz.getName() + " has no @Unstringify annotations"));
    }

    private static <T> T invoke(Method m, String s)  {
        assertTrue(isStatic(m.getModifiers()), () -> "Unstringify method MUST be static: " + m);
        assertTrue(1 == m.getParameterCount(), () -> "Unstringify method MUST take one parameter: " + m);
        assertTrue(String.class == m.getParameterTypes()[0], () -> "Unstringify method MUST take a string parameter: " + m);
        Class<?> targetClass = m.getDeclaringClass();
        assertTrue(targetClass == m.getReturnType(), () -> "Unstringify method MUST return an instance of " + targetClass.getName() + ": " + m);
        m.setAccessible(true);
        try {
            return (T) m.invoke(null, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T invoke(Constructor<T> c, String s)  {
        c.setAccessible(true);
        try {
            return c.newInstance(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] writeObject(Object payload) {
        final byte[] bytes;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOut)) {
            out.writeObject(payload);
            out.flush();
            bytes = byteOut.toByteArray();
        } catch (IOException e) {
            throw new IOError(e);
        }
        return bytes;
    }

    @SuppressWarnings("unchecked")
    private static <T> T readStringifiable(String cname, String payload) {
            return RawOptional.of(cname)
                    .map(Class::forName)
                    .map(Stringifier::findUnstringifier)
                    .map(f -> (T) f.apply(payload)).get();
    }

    @SuppressWarnings("unchecked")
    private static <T> T readSerializable(byte[] bytes) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T)in.readObject();
        } catch (RuntimeException|Error e) {
            throw e;
        } catch (Throwable e) {
            throw (Error)new AssertionFailedError("Unexpected exception:" + e).initCause(e);
        }
    }
}

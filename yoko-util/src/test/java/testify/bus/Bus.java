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
package testify.bus;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.EnumSet.range;

public interface Bus extends SimpleBus {
    String user();
    default boolean isGlobal() { return GLOBAL_USER.equals(user()); }
    default boolean isLocal() { return !isGlobal(); }

    @Override
    Bus put(String key, String value);
    @Override
    Bus onMsg(String key, Consumer<String> action);

    <K extends Enum<K> & TypeRef<?>> boolean hasKey(K key);
    <K extends Enum<K> & TypeRef<T>, T> T get(K key);
    <K extends Enum<K> & TypeRef<T>, T> T peek(K key);
    <K extends Enum<K> & TypeRef<? super T>, T> Bus put(K key, T value);
    <K extends Enum<K> & TypeRef<T>, T> Bus put(K key);
    <K extends Enum<K> & TypeRef<T>, T> Bus onMsg(K key, Consumer<T> action);
    <K extends Enum<K> & TypeRef<K>> void onMsg(K key, Runnable action);

    String isLoggingEnabled(LogLevel level);

    Bus enableLogging(String... patterns);
    Bus enableLogging(LogLevel level, String... patterns);

    Bus enableLogging(LogLevel level, String pattern);
    Bus enableLogging(Set<LogLevel> levels, String... patterns);

    Bus log(Supplier<String> message);
    Bus log(String message);
    Bus log(LogLevel level, String message);
    Bus log(LogLevel level, Supplier<String> message);

    Bus logToSysOut(LogLevel level);
    Bus logToSysErr(LogLevel level);
    Bus logToSysOut(Set<LogLevel> levels);
    Bus logToSysErr(Set<LogLevel> levels);

    Bus onLog(Consumer<String> action);
    Bus onLog(LogLevel level, Consumer<String> action);
    Bus onLog(Set<LogLevel> levels, Consumer<String> action);

    public static enum LogLevel implements StringRef {
        DEBUG, INFO, DEFAULT, WARN, ERROR;
        Set<LogBus.LogLevel> andHigher() { return range(this, ERROR); }
        boolean includes(LogLevel level) { return andHigher().contains(level); }
    }

    interface TypeRef<T> {
        Class<? extends Enum> getDeclaringClass();
        String name();
        @SuppressWarnings("unchecked")
        default T unstringify(String s) { return (T) SerialUtil.unstringify(s); }
        default String stringify(T t) { return SerialUtil.stringify(t); }
        default String fullName() { return getDeclaringClass().getTypeName() + '.' + name(); }
    }

    interface StringRef extends TypeRef {
        default String stringify(String s) { return s; }
        default String unstringify(String s) { return s; }
    }

    interface VoidRef extends TypeRef {
        default String stringify(Void v) { return ""; }
        default Void unstringify(String s) { return null; }
    }
}


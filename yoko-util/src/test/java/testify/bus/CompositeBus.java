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

import testify.streams.BiStream;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

interface CompositeBus extends Bus {
    abstract SimpleBus simpleBus();
    abstract UserBus userBus();
    abstract EventBus eventBus();
    abstract LogBus logBus();
    Bus global();

    @Override
    default String user() { return userBus().user(); }
    @Override
    default Bus put(String key, String value) { userBus().put(key, value); return this; }
    @Override
    default boolean hasKey(String key) { return userBus().hasKey(key); }
    @Override
    default String peek(String key) { return userBus().peek(key); }
    @Override
    default String get(String key) { return userBus().get(key); }
    @Override
    default Bus onMsg(String key, Consumer<String> action) { userBus().onMsg(key, action); return this; }
    @Override
    default BiStream<String, String> biStream() { return userBus().biStream(); }
    @Override
    default Bus forUser(String user) { return simpleBus().forUser(user); }

    @Override
    default <K extends Enum<K> & TypeRef<?>> boolean hasKey(K key) { return eventBus().hasKey(key); }
    @Override
    default <K extends Enum<K> & TypeRef<T>, T> T get(K key) { return eventBus().get(key); }
    @Override
    default <K extends Enum<K> & TypeRef<T>, T> T peek(K key) { return eventBus().peek(key); }
    @Override
    default <K extends Enum<K> & TypeRef<? super T>, T> Bus put(K key, T value) { eventBus().put(key, value); return this; }
    @Override
    default <K extends Enum<K> & TypeRef<T>, T> Bus put(K key) { eventBus().put(key); return this; }
    @Override
    default <K extends Enum<K> & TypeRef<T>, T> Bus onMsg(K key, Consumer<T> action) { eventBus().onMsg(key, action); return this; }
    @Override
    default <K extends Enum<K> & TypeRef<K>> Bus onMsg(K key, Runnable action) { eventBus().onMsg(key, action); return this; }

    @Override
    default String isLoggingEnabled(LogLevel level) { return logBus().isLoggingEnabled(level); }
    @Override
    default Bus enableLogging(String... patterns) { logBus().enableLogging(patterns); return this; }
    @Override
    default Bus enableLogging(LogLevel level, String... patterns) { logBus().enableLogging(level, patterns); return this; }
    @Override
    default Bus enableLogging(LogLevel level, String pattern) { logBus().enableLogging(level, pattern); return this; }
    @Override
    default Bus enableLogging(Set<LogLevel> levels, String... patterns) { logBus().enableLogging(levels, patterns); return this; }
    @Override
    default Bus log(Supplier<String> message) { logBus().log(message); return this; }
    @Override
    default Bus log(String message) { logBus().log(message); return this; }
    @Override
    default Bus log(LogLevel level, String message) { logBus().log(level, message); return this; }
    @Override
    default Bus log(LogLevel level, Supplier<String> message) { logBus().log(level, message); return this; }
    @Override
    default Bus logToSysOut(LogLevel level) { logBus().logToSysOut(level); return this; }
    @Override
    default Bus logToSysErr(LogLevel level) { logBus().logToSysErr(level); return this; }
    @Override
    default Bus logToSysOut(Set<LogLevel> levels) { logBus().logToSysOut(levels); return this; }
    @Override
    default Bus logToSysErr(Set<LogLevel> levels) { logBus().logToSysErr(levels); return this; }
    @Override
    default Bus onLog(Consumer<String> action) { logBus().onLog(action); return this; }
    @Override
    default Bus onLog(LogLevel level, Consumer<String> action) { logBus().onLog(level, action); return this; }
    @Override
    default Bus onLog(Set<LogLevel> levels, Consumer<String> action) { logBus().onLog(levels, action); return this; }

    static CompositeBus createGlobal(LogBus globalLogBus) {
        EventBus globalEventBus = globalLogBus.eventBus();
        UserBus globalUserBus = globalEventBus.userBus();
        SimpleBus simpleBus = globalUserBus.simpleBus();
        return new CompositeBus() {
            public SimpleBus simpleBus() { return simpleBus; }
            public UserBus userBus() { return globalUserBus; }
            public EventBus eventBus() { return globalEventBus; }
            public LogBus logBus() { return globalLogBus; }
            public Bus global() { return this; }
        };
    }

    static CompositeBus create(LogBus logBus, Bus globalBus) {
        EventBus eventBus = logBus.eventBus();
        UserBus userBus = eventBus.userBus();
        SimpleBus simpleBus = userBus.simpleBus();
        return new CompositeBus() {
            public SimpleBus simpleBus() { return simpleBus; }
            public UserBus userBus() { return userBus; }
            public EventBus eventBus() { return eventBus; }
            public LogBus logBus() { return logBus; }
            public Bus global() { return globalBus; }
        };
    }
}

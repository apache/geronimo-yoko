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
package testify.bus;

import testify.streams.BiStream;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static testify.util.ObjectUtil.getNextObjectLabel;

class BusImpl implements Bus {
    private final String label = getNextObjectLabel(Bus.class);
    private final SimpleBus simpleBus;
    private final UserBus userBus;
    private final EventBus eventBus;
    private final LogBus logBus;

    BusImpl(LogBusImpl logBus) {
        this.logBus = logBus;
        this.eventBus = logBus.eventBus;
        this.userBus = logBus.eventBus.userBus;
        this.simpleBus = logBus.eventBus.userBus.simpleBus;
    }

    @Override
    public String user() { return userBus.user(); }
    @Override
    public Bus put(String key, String value) { userBus.put(key, value); return this; }
    @Override
    public boolean hasKey(String key) { return userBus.hasKey(key); }
    @Override
    public String peek(String key) { return userBus.peek(key); }
    @Override
    public String get(String key) { return userBus.get(key); }
    @Override
    public Bus onMsg(String key, Consumer<String> action) { userBus.onMsg(key, action); return this; }
    @Override
    public BiStream<String, String> biStream() { return userBus.biStream(); }
    @Override
    public Bus forUser(String user) { return simpleBus.forUser(user); }

    @Override
    public <K extends Enum<K> & Key<?>> boolean hasKey(K key) { return eventBus.hasKey(key); }
    @Override
    public <K extends Enum<K> & Key<T>, T> T get(K key) { return eventBus.get(key); }
    @Override
    public <K extends Enum<K> & Key<T>, T> T peek(K key) { return eventBus.peek(key); }
    @Override
    public <K extends Enum<K> & Key<? super T>, T> Bus put(K key, T value) { eventBus.put(key, value); return this; }
    @Override
    public <K extends Enum<K> & Key<T>, T> Bus put(K key) { eventBus.put(key); return this; }
    @Override
    public <K extends Enum<K> & Key<T>, T> Bus onMsg(K key, Consumer<T> action) { eventBus.onMsg(key, action); return this; }
    @Override
    public <K extends Enum<K> & Key<K>> Bus onMsg(K key, Runnable action) { eventBus.onMsg(key, action); return this; }

    @Override
    public String isLoggingEnabled(TestLogLevel level) { return logBus.isLoggingEnabled(level); }
    @Override
    public Bus enableLogging(String... patterns) { logBus.enableLogging(patterns); return this; }
    @Override
    public Bus enableLogging(TestLogLevel level, String... patterns) { logBus.enableLogging(level, patterns); return this; }
    @Override
    public Bus log(Supplier<String> message) { logBus.log(message); return this; }
    @Override
    public Bus log(String message) { logBus.log(message); return this; }
    @Override
    public Bus log(TestLogLevel level, String message) { logBus.log(level, message); return this; }
    @Override
    public Bus log(TestLogLevel level, Supplier<String> message) { logBus.log(level, message); return this; }
    @Override
    public Bus logToSysOut(TestLogLevel level) { logBus.logToSysOut(level); return this; }
    @Override
    public Bus logToSysErr(TestLogLevel level) { logBus.logToSysErr(level); return this; }
    @Override
    public Bus logToSysOut(Set<TestLogLevel> levels) { logBus.logToSysOut(levels); return this; }
    @Override
    public Bus logToSysErr(Set<TestLogLevel> levels) { logBus.logToSysErr(levels); return this; }
    @Override
    public Bus onLog(Consumer<String> action) { logBus.onLog(action); return this; }
    @Override
    public Bus onLog(TestLogLevel level, Consumer<String> action) { logBus.onLog(level, action); return this; }
    @Override
    public Bus onLog(Set<TestLogLevel> levels, Consumer<String> action) { logBus.onLog(levels, action); return this; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        BusImpl that = (BusImpl) other;
        return Objects.equals(this.userBus, that.userBus);
    }

    @Override
    public int hashCode() { return Objects.hash(simpleBus); }

    @Override
    public String toString() { return String.format("%s[%s]", label, user()); }
}

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

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

interface BusWrapper extends Bus {
    LogBus bus();

    String transform(String key);

    String untransform(String key);

    @Override
    default boolean hasKey(String key) { return bus().hasKey(transform(key)); }

    @Override
    default String peek(String key) { return bus().peek(transform(key)); }

    @Override
    default String get(String key) { return bus().get(transform(key)); }

    @Override
    default void onMsg(String key, Consumer<String> action) { bus().onMsg(transform(key), action);}

    @Override
    default Bus forUser(String user) { return bus().forUser(user); }

    @Override
    default void forEach(BiConsumer<String, String> action) { bus().forEach(action); }

    @Override
    default BiStream<String, String> biStream() {
        return bus().biStream().mapKeys(this::untransform).filterKeys(Objects::nonNull);
    }

    @Override
    default void put(String key, String value) { bus().put(transform(key), value); }

    @Override
    default void log(LogLevel level, String message) { Bus.super.log(level, transform(message)); }

    @Override
    default String isLoggingEnabled(String user, LogLevel level) {
        return bus().isLoggingEnabled(user, level);
    }

    @Override
    String isLoggingEnabled(LogLevel level);

    @Override
    void enableLogging(LogLevel level, String pattern);

    default void enableLogging(String user, LogLevel level, String pattern) {
        bus().enableLogging(user, level, pattern);
    }
}

class TestBusWrapper implements BusWrapper {

    @Override
    public LogBus bus() { return null; }

    @Override
    public String transform(String key) { return null; }

    @Override
    public String untransform(String key) { return null; }

    @Override
    public String isLoggingEnabled(LogLevel level) {
        return null;
    }

    @Override
    public void enableLogging(LogLevel level, String pattern) {

    }

}
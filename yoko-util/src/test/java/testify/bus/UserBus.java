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
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

interface UserBus {
    String DELIMITER = "::";
    String GLOBAL_USER = "global";

    String user();
    SimpleBus simpleBus();
    UserBus global();

    default Bus forUser(String user) { return simpleBus().forUser(user()); }

    default String transform(String key) {  return key == null ? null : (user() + DELIMITER + validate(key)); }

    default String untransform(String key) { return key.startsWith(user() + DELIMITER) ? key.substring((user() + DELIMITER).length()) : null; }

    default void put(String key, String value) {
        simpleBus().put(transform(key), value);
        if (global() != this) global().put(key, value);
    }

    default boolean hasKey(String key) { return simpleBus().hasKey(transform(key)); }

    default String peek(String key) { return simpleBus().peek(transform(key)); }

    default String get(String key) { return simpleBus().get(transform(key)); }

    default void onMsg(String key, Consumer<String> action) { simpleBus().onMsg(transform(key), action); }

    default BiStream<String, String> biStream() {
        return simpleBus().biStream().mapKeys(this::untransform).filterKeys(Objects::nonNull);
    }

    static String validate(String name) {
        if (requireNonNull(name).contains(DELIMITER))
            throw new Error("Names may not contain '" + DELIMITER + "' (name was '" + name + "')");
        return name;
    }

    static UserBus createGlobal(SimpleBus simpleBus) {
        return new UserBus() {
            public String user() { return GLOBAL_USER; }
            public SimpleBus simpleBus() { return simpleBus; }
            public UserBus global() { return this; }
            public String toString() { return "Global UserBus"; }
        };
    }

    static UserBus create(String user, SimpleBus simpleBus, UserBus globalUserBus) {
        return new UserBus() {
            public String user() { return user; }
            public SimpleBus simpleBus() { return simpleBus; }
            public UserBus global() { return globalUserBus; }
            public String toString() { return String.format("UserBus[%s]", user); }
        };
    }
}

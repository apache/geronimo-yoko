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
import static testify.util.ObjectUtil.getNextObjectLabel;

class UserBusImpl implements UserBus {
    private static final String DELIMITER = "::";

    final String label = getNextObjectLabel(UserBus.class);
    final String user;
    final SimpleBus simpleBus;

    /** Create a local UserBus */
    UserBusImpl(String user, SimpleBus simpleBus){
        this.user = user;
        this.simpleBus = simpleBus;
    }

    private static String validate(String name) {
        if (requireNonNull(name).contains(UserBusImpl.DELIMITER))
            throw new Error("Names may not contain '" + UserBusImpl.DELIMITER + "' (name was '" + name + "')");
        return name;
    }

    @Override
    public String user() { return user; }

    @Override
    public Bus forUser(String user) { return simpleBus.forUser(this.user); }

    private String transform(String key) { return key == null ? null : (user + DELIMITER + validate(key)); }

    private String untransform(String key) { return key.startsWith(user + DELIMITER) ? key.substring((user + DELIMITER).length()) : null; }

    @Override
    public Bus put(String key, String value) {
        simpleBus.put(transform(key), value);
        return null;
    }

    @Override
    public boolean hasKey(String key) { return simpleBus.hasKey(transform(key)); }

    @Override
    public String peek(String key) { return simpleBus.peek(transform(key)); }

    @Override
    public String get(String key) { return simpleBus.get(transform(key)); }

    @Override
    public Bus onMsg(String key, Consumer<String> action) { simpleBus.onMsg(transform(key), action); return null; }

    @Override
    public BiStream<String, String> biStream() {
        return simpleBus.biStream().mapKeys(this::untransform).filterKeys(Objects::nonNull);
    }

    @Override
    public String toString() { return String.format("%s[%s]", label, user); }
}

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
package test.util.parts;

import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

final class UserBusImpl implements UserBus {
    private static final String DELIMITER = "::";

    private final Bus bus;
    private final String user;

    public UserBusImpl(String user, Bus bus) {
        this.user = validate(user);
        this.bus = bus;
    }

    @Override
    public UserBus forUser(String user) { return bus.forUser(user); }

    @Override
    public void put(String key, String value) {
        bus.put(qualify(user, key), value);
        bus.put(key, value); // also use the unqualified key for lookup convenience
    }

    @Override
    public String get(String key) { return bus.get(validate(key)); }

    @Override
    public String getOwn(String key) { return get(user, key); }

    @Override
    public String get(String user, String key){ return bus.get(qualify(user, key)); }

    @Override
    public void forEach(BiConsumer<String, String> action) { bus.forEach(action); }

    private static String qualify(String user, String key) { return user + DELIMITER + validate(key); }

    private static String qualify(String user, Enum<?> event) {return qualify(qualify(user, event.getDeclaringClass().getName()), event.name());}

    private static String validate(String name) {
        if (requireNonNull(name).contains(DELIMITER))
            throw new Error("Names may not contain '" + DELIMITER + "' (name was '" + name + "')");
        return name;
    }
}

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

import testify.bus.Bus.TypeRef;

import java.util.function.Consumer;

// Provide event functionality. This interface should remain package-private.
interface EventBus {
    UserBus userBus();
    EventBus global();
    default <K extends Enum<K> & TypeRef<?>> boolean hasKey(K key) { return userBus().hasKey(key.fullName()); }
    default <K extends Enum<K> & TypeRef<T>, T> T get(K key) { return key.unstringify(userBus().get(key.fullName())); }
    default <K extends Enum<K> & TypeRef<T>, T> T peek(K key) { return key.unstringify(userBus().peek(key.fullName())); }
    default <K extends Enum<K> & TypeRef<? super T>, T> void put(K key, T value) { userBus().put(key.fullName(), key.stringify(value)); }
    default <K extends Enum<K> & TypeRef<T>, T> void put(K key) { put(key, null); }
    default <K extends Enum<K> & TypeRef<T>, T> void onMsg(K key, Consumer<T> action) { userBus().onMsg(key.fullName(), s -> action.accept(key.unstringify(s))); }
    default <K extends Enum<K> & TypeRef<K>> void onMsg(K key, Runnable action) { onMsg(key, s -> action.run()); }

    static EventBus createGlobal(UserBus globalUserBus) {
        return new EventBus() {
            public UserBus userBus() { return globalUserBus; }
            public EventBus global() { return this; }
        };
    }

    static EventBus create(UserBus userBus, EventBus globalEventBus) {
        return new EventBus() {
          public UserBus userBus() { return userBus; }
          public EventBus global() { return globalEventBus; }
        };
    }
}

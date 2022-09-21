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

import java.util.function.Consumer;

import static testify.util.ObjectUtil.getNextObjectLabel;

// Provide event functionality. This interface should remain package-private.
class EventBusImpl implements EventBus {
    final String label = getNextObjectLabel(EventBus.class);
    final UserBusImpl userBus;

    EventBusImpl(UserBusImpl userBus) {
        this.userBus = userBus;
    }

    @Override
    public <K extends Enum<K> & TypeSpec<?>> boolean hasKey(K key) { return userBus.hasKey(key.fullName()); }
    @Override
    public <K extends Enum<K> & TypeSpec<T>, T> T get(K key) { return key.unstringify(userBus.get(key.fullName())); }
    @Override
    public <K extends Enum<K> & TypeSpec<T>, T> T peek(K key) { return key.unstringify(userBus.peek(key.fullName())); }
    @Override
    public <K extends Enum<K> & TypeSpec<? super T>, T> Bus put(K key, T value) { userBus.put(key.fullName(), key.stringify(value)); return null; }
    @Override
    public <K extends Enum<K> & TypeSpec<T>, T> Bus put(K key) { put(key, null); return null; }
    @Override
    public <K extends Enum<K> & TypeSpec<T>, T> Bus onMsg(K key, Consumer<T> action) { userBus.onMsg(key.fullName(), s -> action.accept(key.unstringify(s))); return null; }
    @Override
    public <K extends Enum<K> & TypeSpec<K>> Bus onMsg(K key, Runnable action) { onMsg(key, s -> action.run()); return null; }

    @Override
    public String toString() { return String.format("%s[%s]", label, userBus.user); }
}

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

import java.util.Objects;
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
    public <K extends Enum<K> & Key<?>> boolean hasKey(K key) { return userBus.hasKey(key.fullName()); }
    @Override
    public <K extends Enum<K> & Key<T>, T> T get(K key) { return key.unstringify(userBus.get(key.fullName()), userBus::getTheBus); }
    @Override
    public <K extends Enum<K> & Key<T>, T> T peek(K key) { return key.unstringify(userBus.peek(key.fullName()), userBus::getTheBus); }
    @Override
    public <K extends Enum<K> & Key<? super T>, T> Bus put(K key, T value) { userBus.put(key.fullName(), key.stringify(value)); return null; }
    @Override
    public <K extends Enum<K> & Key<T>, T> Bus put(K key) { put(key, null); return null; }
    @Override
    public <K extends Enum<K> & Key<T>, T> Bus onMsg(K key, Consumer<T> action) { userBus.onMsg(key.fullName(), s -> action.accept(key.unstringify(s, userBus::getTheBus))); return null; }
    @Override
    public <K extends Enum<K> & Key<K>> Bus onMsg(K key, Runnable action) { onMsg(key, s -> action.run()); return null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventBusImpl eventBus = (EventBusImpl) o;
        return Objects.equals(userBus, eventBus.userBus);
    }

    @Override
    public int hashCode() { return Objects.hash(userBus); }

    @Override
    public String toString() { return String.format("%s[%s]", label, userBus.user); }
}

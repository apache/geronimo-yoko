/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.rmi.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.synchronizedMap;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableSet;

@SuppressWarnings({"SuspiciousMethodCalls", "unchecked"})
public final class NoDeleteSynchronizedMap<K,V> implements Map<K,V> {
    private final Map<K,V> global = synchronizedMap(new HashMap<K, V>());

    private final ThreadLocal<Map<K,V>> threadLocal = new ThreadLocal<Map<K,V>>() {
        public Map<K,V> initialValue() {
            return new HashMap<>();
        }
    };

    public boolean containsKey(Object key) {
        Map<K,V> local = threadLocal.get();
        if (local.containsKey(key)) return true;

        if (global.containsKey(key)) {
            local.put((K)key, global.get(key));
            return true;
        } else {
            return false;
        }
    }

    public boolean containsValue(Object val) {
        return threadLocal.get().containsValue(val) || global.containsValue(val);
    }

    public V get(Object key) {
        final Map<K, V> local = threadLocal.get();
        V val = local.get(key);
        if (val != null || local.containsKey(key)) return val;

        val = global.get(key);
        if (val != null) local.put((K) key, val);
        return val;
    }

    // disable the delete operations
    public V remove(Object key) { throw new UnsupportedOperationException(); }
    public void clear() { throw new UnsupportedOperationException(); }

    // disable removal while delegating to global's collection methods
    public Set<K> keySet() { return unmodifiableSet(global.keySet()); }
    public Collection<V> values() { return unmodifiableCollection(global.values()); }
    public Set<Entry<K,V>> entrySet() { return unmodifiableSet(global.entrySet()); }

    // put requires special handling to ensure we never overwrite
    public V put(K key, V val) {
        synchronized(global) {
            if (global.containsKey(key)) throw new IllegalStateException("Cannot overwrite existing entry for " + key);
            return global.put(key, val);
        }
    }

    // putAll also needs special handlign to prevent overwrites
    public void putAll(Map<? extends K, ? extends V> other) {
        // WARNING: avoid calling out to other from within the synchronized block.
        // If other is synchronized, this would try to obtain two object monitors in sequence,
        // and we cannot ensure a canonical ordering on these monitors,
        // so we would risk ...DEADLOCK — dun-dun-duh!
        synchronized(global) {
            Map<K, V> intersection = new HashMap<>(other);
            intersection.keySet().retainAll(global.keySet());
            switch (intersection.size()) {
            case 0:
                global.putAll(other);
                return;
            case 1:
                throw new IllegalStateException("Cannot overwrite existing entry for " + intersection.keySet().iterator().next());
            default:
                throw new IllegalStateException("Cannot overwrite existing entries: " + intersection.keySet());
            }
        }
    }

    // delegate the rest of the API to global
    public int size() { return global.size(); }
    public boolean isEmpty() { return global.isEmpty(); }
    public int hashCode() { return global.hashCode(); }
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object other) { return other == null || other.equals(global); }
}

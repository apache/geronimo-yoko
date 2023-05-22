/*
 * Copyright 2020 IBM Corporation and others.
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
package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.Cache;
import org.apache.yoko.util.Factory;
import org.apache.yoko.util.Fifa;
import org.apache.yoko.util.KeyedFactory;
import org.apache.yoko.util.Reference;

import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe map that counts references and facilitates clean-up of unused entries.
 * References are acquired when any of the getter methods are called:
 * <ul>
 *     <li>{@link #get(K)}</li>
 *     <li>{@link #getOrCreate(K, Factory<V>)}</li>
 *     <li>{@link #getOrCreate(K, KeyedFactory<K,V>)}</li>
 * </ul>
 * Each of these methods returns a reference object that must be
 * released when no longer required by calling {@link Reference#close()}.
 *
 * <p>
 *
 * When a value is removed implicitly as a result of a cache clean-up operation,
 * the {@link Cleaner} provided at construction time is invoked. This provides a way
 * of registering a call-back object to handle the cleaning up of resources when they
 * are discarded from the cache.
 *
 * No clean-up happens when a value is explicitly removed by a call to either of the remove methods:
 * <ul>
 *     <li>{@link #remove(Reference)}</li>
 *     <li>{@link #remove(K, V)}</li>
 * </ul>
 *
 * @param <K> the key type, compared by equality
 * @param <V> the value type, compared by identity
 */
public class ReferenceCountedCache<K, V> implements Cache<K,V> {

    private final ConcurrentMap<K, CountedEntry<K, V>> map = new ConcurrentHashMap<>();
    private final Fifa<CountedEntry<K, V>> idleEntries = new ConcurrentFifo<>();
    private volatile int threshold;
    private volatile int sweep;
    private final Cleaner<V> cleaner;
    private final ReferenceQueue<Reference<V>> gcQueue;

    /**
     * Create a new cache
     * @param cleaner   the object to use to clean entries
     * @param threshold the number of values above which to start cleaning up
     * @param sweep     the number of unused values to clear up
     */
    public ReferenceCountedCache(Cleaner<V> cleaner, int threshold, int sweep) {
        this.threshold = threshold;
        this.sweep = sweep;
        this.cleaner = cleaner;
        gcQueue = new ReferenceQueue<>();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public int idleCount() {
        return idleEntries.size();
    }

    @Override
    public Reference<V> get(K key) {
        CountedEntry<K, V> entry = map.get(key);
        if (entry == null) return null;
        return track(entry.obtain());
    }

    @Override
    public Reference<V> getOrCreate(K key, KeyedFactory<K, V> valueFactory) {
        CountedEntry<K,V>.ValueReference result;
        do {
            CountedEntry<K, V> entry = map.get(key);
            if (entry == null) {
                // try putting a new entry in the map
                CountedEntry<K, V> newEntry = new CountedEntry<>(key, idleEntries);
                entry = map.putIfAbsent(key, newEntry);
                if (entry == null) {
                    // this thread won the race to create the new entry
                    V value = null;
                    try {
                        value = valueFactory.create(key);
                        return track(newEntry.setValue(value));
                    } finally {
                        if (value == null) {
                            // create() threw an exception, so clean up
                            // and make sure no-one else tries to use this entry
                            newEntry.abort();
                            map.remove(key, newEntry);
                        }
                    }
                }
            }
            result = entry.obtain();
        } while (result == null); // the entry was cleared - try again
        return track(result);
    }

    /**
     * This method is the identity transformation.
     * Sub-classes should override this method to add any tracking behaviour.
     */
    protected CountedEntry<K,V>.ValueReference track(CountedEntry<K,V>.ValueReference ref) {return ref;}

    @Override
    public final Reference<V> getOrCreate(K key, final Factory<V> factory) {
        return getOrCreate(key, new KeyedFactory<K, V>() {
            @Override
            public V create(K key) {
                return factory.create();
            }
        });
    }

    @Override
    public boolean remove(Reference<V> ref) {return remove(((CountedEntry<K,V>.ValueReference) ref).invalidateAndGetEntry());}

    @Override
    public boolean remove(K key, V value) {
        if (key == null) return false;
        final CountedEntry<K, V> entry = map.get(key);
        if (entry == null) return false;
        try (Reference ref = entry.obtain();) {
            if (ref == null) return false;
            if (ref.get() != value) return false;
            return remove(ref);
        }
    }

    private boolean remove(CountedEntry<K, V> entry) { return entry != null && map.remove(entry.key, entry); }

    @Override
    public int clean() {
        if (size() <= threshold) return 0;
        int removed = 0;
        while (removed < sweep) {
            CountedEntry<K, V> e = idleEntries.peek();
            if (e == null) break;
            V clearedValue = e.clear();
            if (clearedValue == null) continue;
            if (!!!map.remove(e.key, e))
                throw new IllegalStateException("Entry already removed");
            cleaner.clean(clearedValue);
            removed++;
        }
        return removed;
    }

    @Override
    public Map<K, V> snapshot() {
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<K,CountedEntry<K, V>> entry : map.entrySet()) {
            try (Reference<V> ref = entry.getValue().obtain()){
                result.put(entry.getKey(), ref.get());
            } catch (NullPointerException ignored) {}
        }
        return result;
    }
}

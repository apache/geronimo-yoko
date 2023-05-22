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
package org.apache.yoko.util;

import java.util.Map;

public interface Cache<K, V> {
    /** Get the number of cached values */
    int size();

    /** Get the number of cached values not currently in use */
    int idleCount();

    /**
     * Retrieve the value for the given key.
     * The caller must ensure the returned reference is closed
     */
    Reference<V> get(K key);

    /**
     * Retrieve or compute the value for the given key.
     * The caller must ensure that the returned reference is closed.
     * @return an auto-closeable reference to the cached value
     */
    Reference<V> getOrCreate(K key, KeyedFactory<K, V> keyedFactory);

    /**
     * Retrieve or compute the value for the given key.
     * The caller must ensure that the returned reference is closed.
     * @return an auto-closeable reference to the cached value
     */
    Reference<V> getOrCreate(K key, Factory<V> factory);

    /**
     * Uncache an item. No cleanup will be performed.
     *
     * @return <code>true</code> if the item was removed
     */
    boolean remove(Reference<V> ref);

    /**
     * Uncache an item. No cleanup will be performed.
     * Removes the entry for the specified key only
     * if it is currently mapped to the specified value.
     *
     * @return <code>true</code> if the item was removed
     */
    boolean remove(K key, V value);

    /**
     * Remove some idle entries.
     * @return the number of entries removed
     */
    int clean();

    Map<K, V> snapshot();

    interface Cleaner<V> {void clean(V value);}
}

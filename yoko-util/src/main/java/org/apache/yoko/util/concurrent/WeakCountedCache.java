/*
 * Copyright 2015 IBM Corporation and others.
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

import org.apache.yoko.util.KeyedFactory;

public class WeakCountedCache<K, V> extends ReferenceCountedCache<K, V> {
    private final WeakConcurrentFifo<CountedEntry<K,V>.ValueReference> referenceTracker
            = new WeakConcurrentFifo<>(new CleanupFactory());

    /**
     * Create a new cache
     *
     * @param cleaner   the object to use to clean entries
     * @param threshold the number of values above which to start cleaning up
     * @param sweep     the number of unused values to clear up
     */
    public WeakCountedCache(Cleaner<V> cleaner, int threshold, int sweep) {
        super(cleaner, threshold, sweep);
    }

    @Override
    protected CountedEntry<K,V>.ValueReference track(CountedEntry<K,V>.ValueReference ref) {
        if (ref != null)
            referenceTracker.put(ref);
        return ref;
    }

    private final class CleanupFactory implements KeyedFactory<CountedEntry<K,V>.ValueReference, Runnable> {
        public Runnable create(CountedEntry<K, V>.ValueReference key) {
            // Do NOT keep the key around, since this must only be held weakly.
            // Instead, keep the closer task so we can clean up after the key is collected.
            // Subsequent calls to close do nothing, so it's safe to run this multiple times.
            return key.getCloserTask();
        }
    }
}

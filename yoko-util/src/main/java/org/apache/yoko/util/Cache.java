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
     * @throws IllegalStateException if valueRef has already been closed
     */
    void remove(Reference<V> ref);

    /**
     * Remove some idle entries.
     * @return the number of entries removed
     */
    int clean();

    Map<K, V> snapshot();

    interface Cleaner<V> {void clean(V value);}
}

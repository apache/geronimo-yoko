package org.apache.yoko.util;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public abstract class UnmodifiableEnumMap<K extends Enum<K>, V> extends EnumMap<K, V> {
    private static final long serialVersionUID = 1L;

    public UnmodifiableEnumMap(Class<K> keyType) {
        super(keyType);
        // initialise all values up front to avoid races later
        for(K key : keyType.getEnumConstants())
            super.put(key, computeValueFor(key));
    }

    protected abstract V computeValueFor(K key);

    @Override
    public final V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Set<K> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    @Override
    public final Collection<V> values() {
        return Collections.unmodifiableCollection(super.values());
    }
}

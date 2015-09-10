package org.apache.yoko.util;

public interface KeyedFactory<K, V> {
    V create(K key);
}

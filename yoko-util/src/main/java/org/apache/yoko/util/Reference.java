package org.apache.yoko.util;

public interface Reference<T> extends AutoCloseable {
    /** Get the referent, which is guaranteed to be non-null */
    T get();
    /** Finish using the reference */
    @Override
    void close();
}

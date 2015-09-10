package org.apache.yoko.util;

/** A first-in, first-accessed holder of stuff */
public interface Fifa<T> extends Sequential<T> {
    T peek();
}

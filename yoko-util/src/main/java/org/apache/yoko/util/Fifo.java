package org.apache.yoko.util;

/** A first-in, first-out holder of stuff */
public interface Fifo<T> extends Sequential<T> {
    Object remove();
}

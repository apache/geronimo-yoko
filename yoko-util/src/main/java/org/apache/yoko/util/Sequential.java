package org.apache.yoko.util;

/** A holder of stuff */
public interface Sequential<T> {
    int size();

    Place<T> put(T elem);

    interface Place<T> {
        /**
         * Relinquish this place in the sequence.
         *
         * @return the element if it is successfully removed from the sequence<br>
         * or <code>null</code> if the element has already been removed by another operation
         */
        T relinquish();
    }
}

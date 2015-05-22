package org.apache.yoko.rmi.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakKey<T> extends WeakReference<T> implements Key<T> {
    private final int hash;

    public WeakKey(T r, ReferenceQueue<T> q) {
        super(r, q);
        hash = r.hashCode();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!!!(o instanceof Key)) return false;
        final Object otherKey = ((Key<?>)o).get();
        if (null == otherKey) return false;
        return otherKey.equals(get());
    }
}

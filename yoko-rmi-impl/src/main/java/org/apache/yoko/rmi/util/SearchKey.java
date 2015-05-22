package org.apache.yoko.rmi.util;

public class SearchKey<T> implements Key<T> {
    private final T value;
    private final int hash;

    public SearchKey(T value) {
        this.value = value;
        hash = value.hashCode();
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!!!(o instanceof Key)) return false;
        return value.equals(((Key)o).get());
    }
}

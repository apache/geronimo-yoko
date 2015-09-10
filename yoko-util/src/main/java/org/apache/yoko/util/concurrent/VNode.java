package org.apache.yoko.util.concurrent;

public interface VNode<T> extends PNode<T>, NNode<T> {
    T get();

    void insertAfter(PNode<T> pnode);

    void delete();

}

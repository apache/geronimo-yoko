package org.apache.yoko.util.concurrent;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

final class WeakNode<T> extends WeakReference<T> implements VNode<T> {
    final Runnable cleanup;
    private PNode<T> prev;
    private NNode<T> next;

    WeakNode(T value, ReferenceQueue<T> q, Runnable cleanup) {
        super(value, q);
        this.cleanup = cleanup;
    }
    public PNode<T> prev() {return prev;}
    public NNode<T> next() {return next;}
    public void prev(PNode<T> pnode) {prev = pnode;}
    public void next(NNode<T> nnode) {next = nnode;}

    public void insertAfter(PNode<T> pnode) {
        NNode<T> nnode = pnode.next();
        this.next = nnode;
        this.prev = pnode;
        nnode.prev(this);
        pnode.next(this);
    }

    public void delete() {
        this.prev.next(this.next);
        this.next.prev(this.prev);
        this.prev = null;
        this.next = null;
    }
}

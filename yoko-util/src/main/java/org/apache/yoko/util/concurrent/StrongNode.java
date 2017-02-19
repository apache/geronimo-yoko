package org.apache.yoko.util.concurrent;

final class StrongNode<T> implements VNode<T> {
    private final T value;
    private PNode<T> prev;
    private NNode<T> next;
    
    StrongNode(T value) {this.value = value;}
    public PNode<T> prev() {return prev;}
    public NNode<T> next() {return next;}
    public void prev(PNode<T> pnode) {prev = pnode;}
    public void next(NNode<T> nnode) {next = nnode;}
    public T get() {return value;}

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
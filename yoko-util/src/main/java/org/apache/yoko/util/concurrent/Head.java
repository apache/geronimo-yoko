package org.apache.yoko.util.concurrent;

final class Head<T> implements PNode<T> {
    private NNode<T> next;
    public NNode<T> next() {return next;}
    public void next(NNode<T> nnode) {next = nnode;}
}

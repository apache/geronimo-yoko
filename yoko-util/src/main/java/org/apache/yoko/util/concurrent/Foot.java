package org.apache.yoko.util.concurrent;

import static java.util.Objects.requireNonNull;

final class Foot<T> implements NNode<T> {
    private PNode<T> prev;
    
    public PNode<T> prev() {return prev;}
    public void prev(PNode<T> pnode) {prev = pnode;}
    
    Foot(Head<T> head) {
        this.prev = requireNonNull(head);
        head.next(this);
    }
}
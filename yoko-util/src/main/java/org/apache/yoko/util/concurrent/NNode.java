package org.apache.yoko.util.concurrent;

interface NNode<T> {
    PNode<T> prev();
    
    void prev(PNode<T> pnode);
}

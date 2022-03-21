package org.apache.yoko.util.concurrent;

interface PNode<T> {
    NNode<T> next();
    
    void next(NNode<T> nnode);
}

package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.KeyedFactory;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import static java.util.Objects.requireNonNull;

public class WeakConcurrentFifo<T> extends ConcurrentFifo<T> {
    private final ReferenceQueue<T> refQueue = new ReferenceQueue<>();
    private final KeyedFactory<? super T, Runnable> cleanupFactory;

    WeakConcurrentFifo(KeyedFactory<? super T, Runnable> cleanupFactory) {
        this.cleanupFactory = requireNonNull(cleanupFactory);
    }

    @Override
    public int size() {
        cleanup();
        return super.size();
    }

    @Override
    public T peek() {
        return getNextUncollectedValue(false);
    }

    @Override
    public Place<T> put(T elem) {
        cleanup();
        return super.put(elem);
    }

    @Override
    public T remove() {
        return getNextUncollectedValue(true);
    }

    @Override
    protected VNode<T> createNode(T elem) {
        return new WeakNode<>(elem, refQueue, cleanupFactory.create(elem));
    }

    private void cleanup() {
        peek();
    }

    private void processQ() {
        while(processStaleRef(refQueue.poll()));
    }

    /** Returns false only if the ref passed in was null. */
    private boolean processStaleRef(Reference<? extends T> ref) {
        if (ref == null) return false;
        WeakNode<T> wn = (WeakNode<T>) ref;
        RESPIN: do {
            PNode<T> prev = wn.prev();
            if (prev == null) return true; // this node is already removed
            synchronized (prev) {
                if (wn.prev() != prev) continue RESPIN; // something changed!
                synchronized (wn) {
                    wn.delete();
                    size.decrementAndGet();
                    wn.cleanup.run();
                    return true;
                }
            }
        } while (true);
    }

    private T getNextUncollectedValue(boolean removeNode) {
        SKIP_NULL_REFS: do {
            processQ();
            synchronized (head) {
                NNode<T> next = head.next();
                if (next == foot) return null;
                synchronized (next) {
                    WeakNode<T> curr = (WeakNode<T>) next;
                    T elem = curr.get();
                    if (elem == null) {
                        // this weak node's referent has been collected
                        curr.enqueue();
                        continue SKIP_NULL_REFS;
                    }
                    if (removeNode) {
                        curr.delete();
                        size.decrementAndGet();
                    }
                    return elem;
                }
            }
        } while (true);
    }
}

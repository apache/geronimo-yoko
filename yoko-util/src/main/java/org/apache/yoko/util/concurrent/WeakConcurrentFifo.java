package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.KeyedFactory;
import org.apache.yoko.util.Sequential;

import java.lang.ref.ReferenceQueue;

public class WeakConcurrentFifo<T> extends ConcurrentFifo<T> {
    private final ReferenceQueue<T> refQueue = new ReferenceQueue<>();
    private final KeyedFactory<? super T, Runnable> cleanupFactory;

    WeakConcurrentFifo() {
        this(NoOpRunnableFactory.INSTANCE);
    }

    WeakConcurrentFifo(KeyedFactory<? super T, Runnable> cleanupFactory) {
        this.cleanupFactory = cleanupFactory;
    }

    @Override
    public int size() {
        cleanup();
        return super.size();
    }

    @Override
    public T peek() {
        cleanup();
        return super.peek();
    }

    @Override
    public Place<T> put(T elem) {
        cleanup();
        return super.put(elem);
    }

    @Override
    public T remove() {
        cleanup();
        return super.remove();
    }

    @Override
    protected VNode<T> createNode(T elem) {
        return new WeakNode<>(elem, refQueue, cleanupFactory.create(elem));
    }

    private void cleanup() {
        do {
            @SuppressWarnings("unchecked")
            WeakNode<T> wn = (WeakNode<T>) refQueue.poll();
            if (wn == null) return;
            cleanup(wn);
        } while (true);
    }

    private void cleanup(WeakNode<T> wn) {
        RESPIN: do {
            PNode<T> prev = wn.prev();
            if (prev == null) return; // this node is already removed
            synchronized (prev) {
                if (wn.prev() != prev) continue RESPIN; // something changed!
                synchronized (wn) {
                    wn.delete();
                    size.decrementAndGet();
                    wn.cleanup.run();
                    return;
                }
            }
        } while (true);
    }
}

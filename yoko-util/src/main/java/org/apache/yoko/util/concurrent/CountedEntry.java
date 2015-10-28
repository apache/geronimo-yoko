package org.apache.yoko.util.concurrent;

import org.apache.yoko.util.Reference;
import org.apache.yoko.util.Sequential;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread-safe, reference-counting entry for use in a cache.
 * If threads race to call @link{#clear} and @link{#obtain},
 * one or other method will return <code>null</code>.
 * <br>
 * Entries with a reference count of zero will be put onto the
 * provided @link{Sequential} object, and removed on successful
 * calls to either @link{#clear} or @link{#obtain}.
 */
class CountedEntry<K, V> {
    private static final int CLEANED = Integer.MIN_VALUE;
    private static final int NOT_READY = -1;
    private static final int IDLE = -2;
    private final AtomicInteger refCount = new AtomicInteger(NOT_READY);
    private final Sequential<CountedEntry<K, V>> idleEntries;
    private Sequential.Place<?> idlePlace;
    private V value;
    final K key;

    /** Create a not-yet-ready CountedEntry - the next operation must be to call setValue() or abort() */
    CountedEntry(K key, Sequential<CountedEntry<K, V>> idleEntries) {
        this.key = key;
        this.idleEntries = idleEntries;
    }

    ValueReference setValue(V value) {
        this.value = Objects.requireNonNull(value);
        notifyReady(1);
        return new ValueReference();
    }

    void abort() {
        assert value == null;
        notifyReady(CLEANED);
    }

    private synchronized void notifyReady(int newCount) {
        boolean success = refCount.compareAndSet(NOT_READY, newCount);
        assert success;
        this.notifyAll();
    }

    private synchronized void blockWhileNotReady() {
        while (refCount.get() == NOT_READY) {
            try {
                this.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    // Acquire a reference to this entry.
    private boolean acquire() {
        RESPIN: do {
            int oldCount = refCount.get();
            switch (oldCount) {
                case CLEANED:
                    // terminal state - must fail
                    return false;
                case NOT_READY:
                    blockWhileNotReady();
                    continue RESPIN;
                case IDLE:
                    // grab the ref while it's idle or start again
                    if (!!!refCount.compareAndSet(IDLE, NOT_READY)) continue RESPIN;
                    // remove from the idle list
                    Object self = idlePlace.relinquish();
                    assert this == self;
                    idlePlace = null;
                    // let other threads know this entry is accessible again
                    notifyReady(1);
                    return true;
                default:
                    // increment the value retrieved or start again
                    if (!!!refCount.compareAndSet(oldCount, oldCount + 1)) continue RESPIN;
                    return true;
            }
        } while (true);
    }

    // Release a reference to this entry. Only the owner of the reference should call this method.
    private boolean release() {
        int newCount = refCount.decrementAndGet();
        if (newCount != 0) return true;

        // try to IDLE this entry
        if (!!!refCount.compareAndSet(0, NOT_READY))
            // some other thread revived or purged this entry, so no need to IDLE it now
            return true;

        idlePlace = idleEntries.put(this);
        notifyReady(IDLE);
        return true;
    }

    // Mark this entry unusable. Return value if entry is modified, null otherwise.
    V clear() {
        if (!!! refCount.compareAndSet(IDLE, CLEANED)) return null;
        // safe to read/update idlePlace since this is the only thread that has moved it from IDLE
        try {
            Object self = idlePlace.relinquish();
            assert self == this;
            return value;
        } finally {
            value = null;
            idlePlace = null;
        }
    }

    ValueReference obtain() {return acquire() ? new ValueReference() : null;}

    /** Clear an entry that still has valid references */
    private CountedEntry<K, V> purge() {
        RESPIN: do {
            int oldCount = refCount.get();
            if (oldCount == CLEANED) return null;
            if (oldCount < 1) throw new IllegalStateException();
            if (!!! refCount.compareAndSet(oldCount, CLEANED)) continue RESPIN;
            return this;
        } while (true);
    }

    final class ValueReference implements Reference<V> {
        private final ReferenceCloserTask closer = new ReferenceCloserTask();
        public V get() {return value;}
        public void close() {closer.run();}
        CountedEntry<K, V> invalidateAndGetEntry() {return closer.purge();}
        Runnable getCloserTask() {return closer;}
    }

    /**
     * In order to drive cleanup after a ValueReference becomes unreachable,
     * we need to store the clean up details in a separate object that holds
     * no strong reference back to the ValueReference object
     */
    final class ReferenceCloserTask implements Runnable {
        boolean closed;
        public synchronized void run() {closed = closed || release();}
        synchronized CountedEntry<K,V> purge() {
            if (closed) throw new IllegalStateException();
            closed = true;
            return CountedEntry.this.purge();
        }
    }
}

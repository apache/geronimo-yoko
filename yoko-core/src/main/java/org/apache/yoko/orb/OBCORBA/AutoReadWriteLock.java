package org.apache.yoko.orb.OBCORBA;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class AutoLock implements AutoCloseable{
    final AtomicReference<Lock> lockRef;
    
    AutoLock(Lock lock) {
        lockRef = new AtomicReference<>(lock);
        lock.lock();
    }
    
    @Override
    public void close() {
        Lock lock = lockRef.getAndSet(null);
        if (lock != null) lock.unlock();
    }
}

class AutoReadWriteLock {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public AutoLock getReadLock() {
        return new AutoLock(lock.readLock());
    }

    public AutoLock getWriteLock() {
        return new AutoLock(lock.writeLock());
    }
}
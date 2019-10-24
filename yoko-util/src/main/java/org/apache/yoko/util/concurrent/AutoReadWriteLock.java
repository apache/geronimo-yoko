package org.apache.yoko.util.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutoReadWriteLock {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    public AutoLock getReadLock() {
        return new AutoLock(lock.readLock());
    }

    public AutoLock getWriteLock() {
        return new AutoLock(lock.writeLock(), lock.readLock(), lock.getReadHoldCount());
    }
}

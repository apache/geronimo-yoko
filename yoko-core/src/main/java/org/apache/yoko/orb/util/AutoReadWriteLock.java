package org.apache.yoko.orb.util;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AutoReadWriteLock {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public AutoLock getReadLock() {
        return new AutoLock(lock.readLock());
    }

    public AutoLock getWriteLock() {
        return new AutoLock(lock.writeLock());
    }
}
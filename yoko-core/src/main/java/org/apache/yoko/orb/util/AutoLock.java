package org.apache.yoko.orb.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class AutoLock implements AutoCloseable{
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

    public Condition newCondition() {
        return lockRef.get().newCondition();
    }
}
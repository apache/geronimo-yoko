package org.apache.yoko.orb.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class AutoLock implements AutoCloseable {
    final AtomicReference<Lock> lockRef;
    final Lock downgradeLock;
    final int downgradeHeld;

    AutoLock(Lock lock) {
        this(lock, null, 0);
    }

    AutoLock(Lock lock, Lock downgradeLock, int downgradeHeld) {
        lockRef = new AtomicReference<>(lock);
        this.downgradeLock = downgradeLock;
        this.downgradeHeld = downgradeHeld;
        for (int i = downgradeHeld; i > 0; i--) {
            downgradeLock.unlock();
        }
        lock.lock();
    }

    @Override
    public void close() {
        Lock lock = lockRef.getAndSet(null);
        if (lock == null)
            return;
        for (int i = 0; i < downgradeHeld; i++) {
            downgradeLock.lock();
        }
        lock.unlock();
    }

    public Condition newCondition() {
        return lockRef.get().newCondition();
    }

    public boolean downgrade() {
        if (downgradeLock == null)
            return false;
        Lock oldLock = lockRef.getAndSet(downgradeLock);
        if (oldLock == downgradeLock)
            return false;
        downgradeLock.lock();
        oldLock.unlock();
        return true;
    }
}

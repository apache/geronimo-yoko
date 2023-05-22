/*
 * Copyright 2017 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.util.concurrent;

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

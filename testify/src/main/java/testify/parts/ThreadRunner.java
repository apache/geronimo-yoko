/*
 * Copyright 2023 IBM Corporation and others.
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
package testify.parts;

import testify.bus.InterProcessBus;

import java.util.concurrent.TimeUnit;

import static testify.io.Serializer.stringify;
import static testify.io.Serializer.unstringify;

enum ThreadRunner implements Runner<Thread> {
    SINGLETON
    ;
    public Thread fork(InterProcessBus centralBus, NamedPart part) {
        NamedPart remotePart = unstringify(stringify(part));
        Thread thread = new Thread(() -> remotePart.run(centralBus.forUser(remotePart.name)), remotePart.name);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
    public boolean join(Thread thread, long timeout, TimeUnit unit) throws InterruptedException {
        thread.join(unit.toMillis(timeout));
        return !thread.isAlive();
    }
    public boolean stop(Thread thread, long timeout, TimeUnit unit) throws InterruptedException {
        thread.interrupt();
        thread.join(unit.toMillis(timeout));
        return !thread.isAlive();
    }
}

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package testify.parts;

import testify.bus.InterProcessBus;

import java.util.concurrent.TimeUnit;

enum ThreadRunner implements Runner<Thread> {
    SINGLETON
    ;
    public Thread fork(InterProcessBus centralBus, NamedPart part) {
        Thread thread = new Thread(() -> part.run(centralBus.forUser(part.name)));
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

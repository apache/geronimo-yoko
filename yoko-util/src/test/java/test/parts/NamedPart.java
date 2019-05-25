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
package test.parts;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

final class NamedPart implements TestPart {
    private enum Event{STARTED, ENDED}
    private static final ConcurrentMap<String, AtomicInteger> uids = new ConcurrentHashMap<>();
    final String name;
    private final TestPart part;
    private final String uid;

    NamedPart(String name, TestPart part) {
        this.name = name;
        this.part = part;
        int instance = uids.computeIfAbsent(name, s -> new AtomicInteger()).incrementAndGet();
        this.uid = name + '#' + instance;
    }

    private String resultKey() { return uid + ".result"; }

    public void run(UserBus bus) {
        try {
            bus.forUser(uid).put(Event.STARTED);
            part.run(bus);
            // normal completion — test passed
            bus.forUser(uid).put(Event.ENDED, null);
        } catch (Throwable e) {
            System.err.printf("Test part '%s' failed with exception: %s%n", name, e);
            e.printStackTrace();
            bus.forUser(uid).put(Event.ENDED, e);
        } finally {
            System.out.flush();
            System.err.flush();
        }
    }

    public void waitForStart(UserBus bus) { bus.forUser(uid).getOwn(Event.STARTED); }

    public void waitForEnd(UserBus bus) { bus.forUser(uid).getOwn(Event.ENDED); }
}

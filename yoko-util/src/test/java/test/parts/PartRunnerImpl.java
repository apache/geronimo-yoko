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

import junit.framework.AssertionFailedError;
import test.util.BiStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

abstract class PartRunnerImpl<J> implements PartRunner {
    private final Map<J, NamedPart> jobs = new HashMap<>();
    final InterProcessBus centralBus = InterProcessBus.createMaster();
    private final UserBus bus = centralBus.forUser("master");

    @Override
    public PartRunner fork(String partName, TestPart part) {
        try {
            final NamedPart namedPart = new NamedPart(partName, part);
            J job = fork(namedPart);
            namedPart.waitForStart(bus);
            jobs.put(job, namedPart);
            return this;
        } catch (Throwable throwable) {
            throw fatalError(throwable);
        }
    }

    @Override
    public PartRunner inline(TestPart part) {
        try {
            part.run(bus);
            return this;
        } catch (Throwable throwable) {
            throw fatalError(throwable);
        }
    }

    private final Error fatalError(Throwable t) {
        try {
            try {
                throw t;
            } catch (RuntimeException | Error runtimeExceptionOrError) {
                throw runtimeExceptionOrError;
            } catch (Throwable e) {
                t = new AssertionFailedError("Unexpected exception: " + t).initCause(t);
                throw (Error) t;
            }
        } finally {
            join(); // clean up anything already launched
        }
    }

    @Override
    public void join() {
        // wait for the ended events on the bus
        jobs.values().forEach(p -> p.waitForEnd(bus));
        // wait for the job mechanisms to complete
        jobs.forEach(this::waitForJob);
    }

    private void waitForJob(J job, NamedPart part) {
        try {
            if (join(job, 5, SECONDS)) return;
            System.err.printf("The test part '%s' did not complete. Trying to force it to stop.%n", part.name);
            if (stop(job, 5, SECONDS)) return;
            System.err.printf("The test part '%s' STILL did not complete. There's nothing more to try.%n", part.name);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    abstract J fork(NamedPart part);
    abstract boolean join(J job, long timeout, TimeUnit unit) throws InterruptedException;
    abstract boolean stop(J job, long timeout, TimeUnit unit) throws InterruptedException;

    private enum Test {
        ;
        @SuppressWarnings("CodeBlock2Expr")
        public static void main(String[] args) throws Exception{
            for (PartRunner runner: asList(new ThreadRunner(), new ProcessRunner())) {
                runner.fork("part1", bus -> {
                    bus.put("a", "Hello");
                    bus.get("b");
                }).inline(bus -> {
                    bus.get("a");
                    bus.put("b", "Hello");
                }).join();
            }
            for (PartRunner runner: asList(new ThreadRunner(), new ProcessRunner())) {
                runner.inline(bus -> {
                    System.out.printf("======Testing with %s======%n", runner);
                }).fork("part1", bus -> {
                    bus.put("a", "foo");
                }).fork("part2", bus -> {
                    bus.put("b", "bar");
                    System.out.printf("a == %s%nb == %s%nc == %s%n",
                            bus.get("a"),
                            bus.get("b"),
                            bus.get("c"));
                }).fork("part3", bus -> {
                    bus.put("c", "baz");
                    System.out.printf("part1.a == %s%npart1.b == %s%npart1.c == %s%n",
                            bus.get("part1", "a"),
                            bus.get("part2", "b"),
                            bus.get("part3", "c"));
                }).inline(bus -> {
                    bus.get("a");
                    bus.get("b");
                    bus.get("c");
                    Thread.sleep(200);
                    System.out.println();
                    System.out.println();
                    System.out.println();
                }).join();
            }
        }
    }
}



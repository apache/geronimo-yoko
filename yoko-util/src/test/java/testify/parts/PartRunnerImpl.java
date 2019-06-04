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

import junit.framework.AssertionFailedError;
import testify.bus.Bus;
import testify.bus.InterProcessBus;
import testify.bus.LogBus.LogLevel;
import testify.io.EasyCloseable;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

abstract class PartRunnerImpl<J> implements PartRunner {
    private final Map<J, NamedPart> jobs = new HashMap<>();
    final InterProcessBus centralBus = InterProcessBus.createMaster();
    private final Bus bus = centralBus.global();
    private final Queue<EasyCloseable> endActions = new ConcurrentLinkedQueue<>();

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
    public PartRunner onStop(String partName, Consumer<Bus> endAction) {
        endActions.add(() -> endAction.accept(bus.forUser(partName)));
        return this;
    }

    @Override
    public PartRunner here(TestPart part) {
        try {
            part.run(bus);
            return this;
        } catch (Throwable throwable) {
            throw fatalError(throwable);
        }
    }

    @Override
    public PartRunner here(String partName, TestPart part) {
        NamedPart namedPart = new NamedPart(partName, part);
        namedPart.run(bus);
        return this;
    }

    private Error fatalError(Throwable t) {
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

    // recursively ensure close
    private void runCloseHooks() {
        if (endActions.isEmpty()) return;
        try (EasyCloseable hook = endActions.poll()) {
            runCloseHooks();
        }
    }

    @Override
    public void join() {
        // close down the main bus
        try (EasyCloseable close = centralBus) {
            runCloseHooks();
            // wait for the ended events on the bus
            jobs.values().forEach(p -> p.waitForEnd(bus));
            // wait for the job mechanisms to complete
            jobs.forEach(this::waitForJob);
        }
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
                    bus.global().get("b");
                }).here(bus -> {
                    bus.global().get("a");
                    bus.put("b", "Hello");
                }).join();
            }
            for (PartRunner runner: asList(new ThreadRunner(), new ProcessRunner())) {
                runner.debug(LogLevel.INFO, ".*", "part4").here(bus -> {
                    System.out.printf("======Testing with %s======%n", runner);
                }).fork("part1", bus -> {
                    bus.put("a", "foo");
                }).fork("part2", bus -> {
                    bus.put("b", "bar");
                    System.out.printf("a == %s%nb == %s%nc == %s%n",
                            bus.global().get("a"),
                            bus.global().get("b"),
                            bus.global().get("c"));
                }).fork("part3", bus -> {
                    bus.put("c", "baz");
                    System.out.printf("part1.a == %s%npart1.b == %s%npart1.c == %s%n",
                            bus.forUser("part1").get("a"),
                            bus.forUser("part2").get("b"),
                            bus.forUser("part3").get("c"));
                }).here("part4", bus -> {
                    bus.global().get("a");
                    bus.global().get("b");
                    bus.global().get("c");
                    Thread.sleep(200);
                    System.out.println();
                    System.out.println();
                    System.out.println(bus.forUser("part1"));
                    System.out.println();
                    System.out.println();
                    System.out.println(bus.forUser("part2"));
                    System.out.println();
                    System.out.println();
                    System.out.println(bus.forUser("part3"));
                    System.out.println();
                    System.out.println();
                    System.out.println(bus.forUser("part4"));
                    System.out.println();
                    System.out.println();
                }).here(System.out::println).join();
            }
            System.out.println("#########################");
        }
    }
}



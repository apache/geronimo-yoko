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

import java.util.Deque;
import java.util.EnumSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.EnumSet.complementOf;
import static java.util.concurrent.TimeUnit.SECONDS;
import static testify.bus.LogBus.LogLevel.WARN;

class PartRunnerImpl implements PartRunner {
    public static final EnumSet<LogLevel> URGENT_LEVELS = EnumSet.of(LogLevel.ERROR, WARN);
    final InterProcessBus centralBus = InterProcessBus.createMaster();
    private final Bus bus = centralBus.global()
            .logToSysErr(URGENT_LEVELS)
            .logToSysOut(complementOf(URGENT_LEVELS))
//            .enableLogging(LogLevel.ERROR, ".*")
//            .enableLogging(WARN, ".*")
            ;

    private final Queue<EasyCloseable> preJoinActions = new ConcurrentLinkedQueue<>();
    private final Deque<EasyCloseable> joinActions = new ConcurrentLinkedDeque<>();
    private final Deque<EasyCloseable> postJoinActions = new ConcurrentLinkedDeque<>();
    private boolean useProcesses = false;

    @Override
    public Bus bus() {
        return centralBus.global();
    }

    @Override
    public Bus bus(String partName) { return centralBus.forUser(partName); }

    @Override
    public PartRunner useProcesses(boolean useProcesses) { this.useProcesses = useProcesses; return this; }

    @Override
    public PartRunner fork(String partName, TestPart part) {
        final Runner<?> runner = useProcesses ? ProcessRunner.SINGLETON : ThreadRunner.SINGLETON;
        return fork(runner, partName, part);
    }

    private <J> PartRunner fork(Runner<J> runner, String partName, TestPart part) {
        try {
            final NamedPart namedPart = new NamedPart(partName, part);
            J job = runner.fork(centralBus, namedPart);
            namedPart.waitForStart(bus);
            registerForJoin(runner, job, partName);
            return this;
        } catch (Throwable throwable) {
            throw fatalError(throwable);
        }
    }

    @Override
    public PartRunner endWith(String partName, Consumer<Bus> endAction) {
        preJoinActions.add(() -> endAction.accept(bus.forUser(partName)));
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
        namedPart.run(bus.forUser(partName));
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
    private static void close(Queue<EasyCloseable> closeables) {
        if (closeables.isEmpty()) return;
        try (EasyCloseable hook = closeables.poll()) {}
        finally { close(closeables); }
    }

    @Override
    public void join() {
        // close down the main bus
        try (EasyCloseable close = centralBus) {
            centralBus.log("Running pre-join actions: " + preJoinActions);
            close(preJoinActions);
            centralBus.log("Running join actions: " + joinActions);
            close(joinActions);
            centralBus.log("Running post-join actions: " + postJoinActions);
            close(postJoinActions);
            centralBus.log("Completed all join actions.");
        }
    }

    private <J> void registerForJoin(Runner<J> runner, J job, String name) {
        joinActions.addFirst(() -> {
            try {
                if (runner.join(job, 5, SECONDS)) return;
                centralBus.log(LogLevel.ERROR, "The test part '" + name + "' did not complete. Trying to force it to stop.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        postJoinActions.addFirst(() -> {
            try {
                if (runner.stop(job, 5, SECONDS)) return;
                centralBus.log(LogLevel.ERROR, "The test part '" + name + "' did not complete when forced. Giving up.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private enum Test {
        ;
        @SuppressWarnings("CodeBlock2Expr")
        public static void main(String[] args) throws Exception{
            for (PartRunner runner: asList(new PartRunnerImpl(), new PartRunnerImpl().useProcesses(true))) {
                runner.fork("part1", bus -> {
                    bus.put("a", "Hello");
                    bus.global().get("b");
                }).here(bus -> {
                    bus.global().get("a");
                    bus.put("b", "Hello");
                }).join();
            }
            for (PartRunner runner: asList(new PartRunnerImpl(), new PartRunnerImpl().useProcesses(true))) {
                runner.enableLogging(LogLevel.INFO, ".*NamedPart", "part4").here(bus -> {
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



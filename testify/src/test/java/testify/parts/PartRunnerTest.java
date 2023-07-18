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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import testify.annotation.RetriedTest;
import testify.bus.Bus;
import testify.bus.key.VoidKey;
import testify.util.function.RawOptional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testify.parts.PartRunner.State.COMPLETED;
import static testify.parts.PartRunner.State.CONFIGURING;
import static testify.parts.PartRunner.State.IN_USE;
import static testify.parts.PartRunnerTest.SyncPoint.JOIN;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PartRunnerTest {
    enum SyncPoint implements VoidKey {JOIN}
    public static final Part PRINT_PART_NAME = bus -> RawOptional.of(bus).peek(b -> System.out.printf("Running part \"%s\"%n", b.user())).ifPresent(JOIN::await);

    final PartRunner runner = new PartRunnerImpl();

    @RetriedTest(maxRuns = 2)
    void testPartRunnerJoinWithForkedThreadsAndProcesses() {
        runner.bus("P0").put("K0", "A");
        runner.useNewThreadWhenForking().fork("P1", b -> b.put("K1", "B"));
        runner.useNewJVMWhenForking().fork("P2", b -> b.put("K2", "C"));
        runner.useNewThreadWhenForking().fork("P3", b -> b.put("K3", "D"));
        runner.useNewJVMWhenForking().fork("P4", b -> b.put("K4", "E"));
        runner.useNewThreadWhenForking().fork("P5", b -> b.put("K5", "F"));
        runner.useNewJVMWhenForking().fork("P6", b -> b.put("K6", "G"));
        // Once join has been called, every part should have completed executing
        assertThat(runner.bus("P0").get("K0"), is("A"));
        assertThat(runner.bus("P1").get("K1"), is("B"));
        assertThat(runner.bus("P2").get("K2"), is("C"));
        assertThat(runner.bus("P3").get("K3"), is("D"));
        assertThat(runner.bus("P4").get("K4"), is("E"));
        assertThat(runner.bus("P5").get("K5"), is("F"));
        assertThat(runner.bus("P6").get("K6"), is("G"));
        runner.join();
    }

    @Test
    void testBusNames() {
        // any part can access any bus by name - create a "main" bus here in the parent thread
        Bus mainBus = runner.bus("main");
        // put the wrong value into the main bus
        mainBus.put("part name", "main");
        // fork a 'part' on a new thread, and record the user name from the bus it gets given
        runner.fork("new thread", bus -> bus.forUser("main").put("part name", bus.user()));
        // wait for the part to complete
        runner.join();
        // check that the part was able to access the main bus, and had the correct bus name
        assertThat(mainBus.get("part name"), is("new thread"));
    }

    @RetriedTest(maxRuns = 10)
    void testJvmStartupHookDoesNotRunOnNewThread() {
        Bus mainBus = runner.bus("main");
        mainBus.put("part name", "main");
        // add a JVM startup hook to change the value in the main bus
        runner.addJVMStartupHook(bus -> bus.forUser("main").put("part name", bus.user()));
        // fork a part on a new thread
        runner.fork("new thread", bus -> System.out.println("Running part " + bus.user()));
        // wait for the part to run
        runner.join();
        // check that the value is unmodified (because the hook should not have run)
        assertThat(mainBus.get("part name"), is("main"));
    }

    @Test
    void testStateTransitions() {
        // - == === CONFIGURING === == -
        assertThat(runner.getState(), is(CONFIGURING));
        runner.addJVMStartupHook(bus -> System.out.println("startup hook"));
        assertThat(runner.getState(), is(CONFIGURING));
        runner.useNewJVMWhenForking();
        assertThat(runner.getState(), is(CONFIGURING));
        runner.useNewThreadWhenForking();
        assertThat(runner.getState(), is(CONFIGURING));

        // fork a part on a new thread
        // the state should automatically change to IN_USE
        runner.fork("Thread 1", PRINT_PART_NAME).endWith(JOIN::announce);

        // - == === IN_USE === == -
        assertThat(runner.getState(), is(IN_USE));
        assertThrows(IllegalStateException.class, () -> runner.addJVMStartupHook(bus -> {}));
        runner.fork("Thread 2", PRINT_PART_NAME).endWith(JOIN::announce);
        assertThat(runner.getState(), is(IN_USE));
        assertThrows(IllegalStateException.class, () -> runner.addJVMStartupHook(bus -> {}));
        runner.useNewJVMWhenForking();
        assertThat(runner.getState(), is(IN_USE));
        runner.fork("JVM1", PRINT_PART_NAME).endWith(JOIN::announce);
        assertThat(runner.getState(), is(IN_USE));
        assertThrows(IllegalStateException.class, () -> runner.addJVMStartupHook(bus -> {}));

        // join all the parts (wait for them to complete)
        // the state should automatically change to COMPLETED
        runner.join();

        // - == === COMPLETED === == -
        assertThat(runner.getState(), is(COMPLETED));
        assertThrows(IllegalStateException.class, () -> runner.addJVMStartupHook(bus -> {}));
        assertThrows(IllegalStateException.class, () -> runner.useNewThreadWhenForking());
        assertThrows(IllegalStateException.class, () -> runner.useNewJVMWhenForking());
        assertThrows(IllegalStateException.class, () -> runner.fork("JVM2", PRINT_PART_NAME));
    }

    @Test
    void testJvmStartupHookRunsOnNewJvm() {
        // add a JVM startup hook to change the value in the main bus
        runner.addJVMStartupHook(bus -> bus.forUser("main").put("part name", bus.user()));
        // fork a part on a new JVM
        runner.useNewJVMWhenForking();
        runner.fork("new jvm", PRINT_PART_NAME).endWith(JOIN::announce);
        // check that the value arrives
        assertThat(runner.bus("main").get("part name"), is("new jvm"));
        // wait for the part to run
        runner.join();
    }
}

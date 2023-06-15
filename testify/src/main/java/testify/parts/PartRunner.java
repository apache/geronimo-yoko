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

import testify.bus.Bus;
import testify.bus.TestLogLevel;

import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("UnusedReturnValue")
public interface PartRunner {
    static PartRunner create() { return new PartRunnerImpl(); }

    /**
     * Enable a range of log levels for the specified pattern and partnames.
     * @param level the most detailed log level to include
     * @param pattern a regular expression to match the classes to trace
     * @return this object for call chaining
     */
    PartRunner enableTestLogging(TestLogLevel level, String pattern);

    /**
     * Get the bus specific to the named part.
     */
    Bus bus(String partName);

    default Bus bus(Enum<?> member) { return bus(member.toString()); }

    PartRunner useNewJVMWhenForking(String...jvmArgs);
    PartRunner useNewThreadWhenForking();

    ForkedPart fork(String partName, TestPart part);
    default ForkedPart fork(Enum<?> partName, TestPart part) { return fork(partName.toString(), part); }

    default ForkedPart forkMain(Class<?> mainClass, String...args) { return fork(mainClass.getName(), wrapMain(mainClass, args)); }

    static TestPart wrapMain(Class<?> mainClass, String[] args) {
        return bus -> {
            try {
                mainClass.getMethod("main", String[].class).invoke(null, new Object[]{args});
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        };
    }

    void join();
}

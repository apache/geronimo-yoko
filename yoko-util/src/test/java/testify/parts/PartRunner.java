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

import testify.bus.Bus;
import testify.bus.LogLevel;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static testify.bus.LogLevel.DEFAULT;

public interface PartRunner {
    static PartRunner create() { return new PartRunnerImpl(); }
    /**
     * Enable logging.
     * @param level the most detailed level of logging to enable
     * @param pattern a regular expression to match the classes to debug
     * @param partNames if empty, all parts will be debugged, otherwise only the parts with the specified names will have debugging enabled
     * @return this object for call chaining
     */
    default PartRunner enableLogging0(LogLevel level, String pattern, String...partNames) {
        // define how to enable logging for a bus
        // if no part names were supplied, enable logging globally
        if (partNames.length == 0) bus().enableLogging(level, pattern);
        // otherwise, enable logging for each supplied part name
        else Stream.of(partNames).map(this::bus).forEach(bus -> bus.enableLogging(level, pattern));
        return this;
    }

    /**
     * Enable {@link LogLevel#DEFAULT} level logging.
     * @param pattern a regular expression to match the classes to debug
     * @param partNames if empty, all parts will be logged, otherwise only the parts with the specified names will have logging enabled
     * @return this object for call chaining
     */
    default PartRunner enableLogging(String pattern, String...partNames) { return enableLogging(DEFAULT, pattern, partNames); }

    /**
     * Enable a range of log levels for the specified classes and partnames.
     * @param level the most detailed log level to enable
     * @param classesToTrace the actual classes to trace, or empty to trace all classes
     * @param partNames if empty, all parts will be logged, otherwise only the parts with the specified names will have logging enabled
     * @return this object for call chaining
     */
    default PartRunner enableLogging(LogLevel level, Class<?>[] classesToTrace, String...partNames) {
        level.andHigher().forEach(lvl -> {
            for (Class<?> cls: classesToTrace) enableLogging0(lvl, cls.getName(), partNames);
        });
        return this;
    }

    /**
     * Enable a range of log levels for the specified pattern and partnames.
     * @param level the most detailed log level to include
     * @param pattern a regular expression to match the classes to trace
     * @param partNames if empty, all parts will be logged, otherwise only the parts with the specified names will have logging enabled
     * @return this object for call chaining
     */
    default PartRunner enableLogging(LogLevel level, String pattern, String...partNames) {
        if (partNames.length == 0) bus().enableLogging(level, pattern);
        else Stream.of(partNames).map(this::bus).forEach(bus -> bus.enableLogging(level, pattern));
        return this;
    }

    /**
     * Get the global bus.
     */
    Bus bus();

    /**
     * Get the bus specific to the named part.
     */
    Bus bus(String partName);

    PartRunner useProcesses(boolean useProcesses);

    PartRunner fork(String partName, TestPart part);

    default PartRunner fork(String partName, TestPart part, Consumer<Bus> endAction) {
        return fork(partName, part).endWith(partName, endAction);
    }

    default PartRunner forkMain(Class<?> mainClass, String...args) { return fork(mainClass.getName(), wrapMain(mainClass, args)); }

    static TestPart wrapMain(Class<?> mainClass, String[] args) {
        return bus -> {
            try {
                // invoke static void main(String[]) on the provided class
                mainClass.getMethod("main", String[].class).invoke(null, new Object[]{args});
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        };
    }

    PartRunner endWith(String partName, Consumer<Bus> endAction);
    PartRunner here(TestPart part);
    PartRunner here(String partName, TestPart part);
    default PartRunner runMain(Class<?> mainClass, String...args) { return here(mainClass.getName(), wrapMain(mainClass, args)); }
    void join();
}

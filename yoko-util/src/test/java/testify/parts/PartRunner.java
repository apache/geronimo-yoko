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
import testify.bus.LogBus.LogLevel;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface PartRunner {
    static PartRunner create() { return new PartRunnerImpl(); }
    /**
     * Enable logging.
     * @param level the level of logging to enable
     * @param pattern a regular expression to match the classes to debug
     * @param partNames if empty, all parts will be debugged, otherwise only the parts with the specified names will have debugging enabled
     * @return this object for call chaining
     */
    default PartRunner enableLogging(LogLevel level, String pattern, String...partNames) {
        // define how to enable logging for a bus
        // if no part names were supplied, enable logging globally
        if (partNames.length == 0) bus().enableLogging(level, pattern).sendToErr(level);
        // otherwise, enable logging for each supplied part name
        else Stream.of(partNames).map(this::bus).forEach(bus -> bus.enableLogging(level, pattern).sendToErr(level));
        return this;
    }

    /**
     * Enable {@link LogLevel#DEFAULT} level logging.
     * @param pattern a regular expression to match the classes to debug
     * @param partNames if empty, all parts will be debugged, otherwise only the parts with the specified names will have debugging enabled
     * @return this object for call chaining
     */
    default PartRunner enableLogging(String pattern, String...partNames) { return enableLogging(LogLevel.DEFAULT, pattern, partNames); }

    /**
     * Enable {@link LogLevel#DEFAULT} level logging.
     * @param pattern a regular expression to match the classes to debug
     * @param partNames if empty, all parts will be debugged, otherwise only the parts with the specified names will have debugging enabled
     * @return this object for call chaining
     */
    default PartRunner enableLogging(Class<?> classToTrace, String...partNames) { return enableLogging(LogLevel.DEFAULT, Pattern.quote(classToTrace.getName()), partNames); }

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

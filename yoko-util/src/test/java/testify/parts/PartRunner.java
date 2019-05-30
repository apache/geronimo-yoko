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
import java.util.stream.Stream;

public interface PartRunner {
    default PartRunner debug(LogLevel level, String pattern, String...partNames) {
        // define how to enable logging for a bus
        final TestPart enableLogging = bus -> {
            bus.enableLogging(level, pattern);
            bus.sendToErr(level);
        };
        // if no part names were supplied, enable logging globally
        if (partNames.length == 0) return here(enableLogging);
        // otherwise, enable logging for each supplied part name
        Stream.of(partNames).forEach(partName -> here(partName, bus -> {
            bus.enableLogging(level, pattern);
            bus.sendToErr(level);
        }));
        return this;
    }

    default PartRunner debug(String pattern, String...partNames) { return debug(LogLevel.DEFAULT, pattern, partNames); }

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

    PartRunner onStop(String partName, Consumer<Bus> endAction);
    PartRunner here(TestPart part);
    PartRunner here(String partName, TestPart part);
    default PartRunner runMain(Class<?> mainClass, String...args) { return here(mainClass.getName(), wrapMain(mainClass, args)); }
    void join();
}

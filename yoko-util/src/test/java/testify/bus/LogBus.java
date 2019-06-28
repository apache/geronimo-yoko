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
package testify.bus;

import testify.bus.Bus.LogLevel;
import testify.bus.Bus.StringRef;
import testify.util.Stack;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static testify.bus.Bus.LogLevel.DEFAULT;
import static testify.bus.LogBus.LogDestination.SYS_ERR;
import static testify.bus.LogBus.LogDestination.SYS_OUT;

// Provide logging functionality. This interface should remain package-private.
interface LogBus {
    enum LogSpec implements StringRef {SPEC}

    enum LogDestination implements Consumer<String> {
        SYS_OUT(System.out::println),
        SYS_ERR(System.err::println)
        ;
        private final Consumer<String> consumer;
        LogDestination(Consumer<String> consumer) {this.consumer = consumer;}
        public void accept(String s) { consumer.accept(s); }
    }

    Package MY_PKG = LogBus.class.getPackage();

    EventBus eventBus();
    LogBus global();
    Set<String> loggingShortcuts();

    default void enableLogging(LogLevel level, String pattern) {
        // add this new pattern to the existing spec if any
        String spec = eventBus().peek(LogSpec.SPEC);
        if (spec == null) spec = "";
        else spec += ":";
        spec += pattern + '=' + level;
        // now put the new spec on the bus so everyone can see it
        eventBus().put(LogSpec.SPEC, spec);
    }

    default void enableLogging(String... patterns) { enableLogging(DEFAULT, patterns); }
    default void enableLogging(LogLevel level, String...patterns) {
        if (patterns.length == 0) enableLogging(level, ".*");
        else Stream.of(patterns).forEach(p -> enableLogging(level, p));
    }
    default void enableLogging(Set<LogLevel> levels, String... patterns) { levels.forEach(l -> enableLogging(l, patterns)); }

    default void log(Supplier<String> message) { log(DEFAULT, message);}
    default void log(String message) { log(DEFAULT, message); }
    default void log(LogLevel level, String message) { log(level, () -> message); }

    default void log(LogLevel level, Supplier<String> message) {
        final String context = isLoggingEnabled(level);
        if (context != null) eventBus().put(level, "[" + context + "] " + message.get());
    }

    default void logToSysOut(LogLevel level) { onLog(level, SYS_OUT); }
    default void logToSysErr(LogLevel level) { onLog(level, SYS_ERR); }
    default void logToSysOut(Set<LogLevel> levels) { onLog(levels, SYS_OUT); }
    default void logToSysErr(Set<LogLevel> levels) { onLog(levels, SYS_ERR); }

    default void onLog(Consumer<String> action) { onLog(DEFAULT, action); }
    default void onLog(LogLevel level, Consumer<String> action) { eventBus().onMsg(level, action); }
    default void onLog(Set<LogLevel> levels, Consumer<String> action) { levels.forEach(l -> onLog(l, action)); }

    default String isLoggingEnabled(LogLevel level) {
        String spec = eventBus().peek(LogSpec.SPEC);
        if (spec == null) spec = eventBus().global().peek(LogSpec.SPEC);
        if (spec == null) return null;

        Class<?> caller = Stack.getCallingClassOutsidePackage(MY_PKG);
        String context = caller.getName();
        String shortcut = eventBus().userBus().user() + level + context;

        if (loggingShortcuts().contains(shortcut)) return Stack.getCallingFrame(caller);

        Supplier<String> answer = () -> {
            loggingShortcuts().add(shortcut);
            return Stack.getCallingFrame(caller);
        };

        // an empty string for the trace spec means enable all DEFAULT level logging
        if (spec.isEmpty() && DEFAULT.includes(level)) return answer.get();

        for (String specpart : spec.split(":")) {
            // we can specify just the log level
            if (specpart.equals(level.name())) return answer.get();
        }

        for (String specpart : spec.split(":")) {
            String[] subparts = specpart.split("=", 2);
            String pattern = subparts[0] + ".*";
            if (!caller.getName().matches(pattern)) continue;
            if (subparts.length == 1) {
                if (DEFAULT.includes(level)) return answer.get();
            }
            String levelSpec = subparts[1];
            try {
                LogLevel specifiedLevel = Bus.LogLevel.valueOf(levelSpec);
                if (specifiedLevel.includes(level)) return answer.get();
            } catch (Throwable t) {
                System.err.println("Unknown level '" + levelSpec + "' in logging specification '" + spec + "'");
            }
        }
        return null;
    }

    static LogBus createGlobal(EventBus eventBus, Set<String> shortcuts) {
        return new LogBus() {
            public EventBus eventBus() { return eventBus; }
            public LogBus global() { return this; }
            public Set<String> loggingShortcuts() { return shortcuts; }
        };
    }

    static LogBus create(EventBus eventBus, LogBus globalLogBus, Set<String> shortcuts) {
        return new LogBus() {
            public EventBus eventBus() { return eventBus; }
            public LogBus global() { return globalLogBus; }
            public Set<String> loggingShortcuts() { return shortcuts; }
        };
    }
}

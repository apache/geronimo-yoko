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

import testify.util.Stack;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static testify.bus.Bus.LogLevel.DEFAULT;
import static testify.bus.LogBus.LogDestination.SYS_ERR;
import static testify.bus.LogBus.LogDestination.SYS_OUT;

abstract class LogBus extends EventBus {
    private static final Package MY_PKG = LogBus.class.getPackage();

    @Override
    public Bus enableLogging(LogLevel level, String pattern) {
        // add this new pattern to the existing spec if any
        String spec = this.<LogSpec, String>peek(LogSpec.SPEC);
        if (spec == null) spec = "";
        else spec += ":";
        spec += pattern + '=' + level;
        // now put the new spec on the bus so everyone can see it
        put(LogSpec.SPEC, spec);
        return this;
    }

    @Override
    public Bus enableLogging(String... patterns) { return enableLogging(DEFAULT, patterns); }
    @Override
    public Bus enableLogging(LogLevel level, String... patterns) {
        if (patterns.length == 0) return enableLogging(level, ".*");
        Stream.of(patterns).forEach(p -> enableLogging(level, p));
        return this;
    }
    @Override
    public final Bus enableLogging(Set<LogLevel> levels, String... patterns) { levels.forEach(l -> enableLogging(l, patterns)); return this; }

    @Override
    public final Bus log(Supplier<String> message) { return log(DEFAULT, message);}
    @Override
    public final Bus log(String message) { return log(DEFAULT, message); }
    @Override
    public final Bus log(LogLevel level, String message) { return log(level, () -> message); }

    @Override
    public Bus log(LogLevel level, Supplier<String> message) {
        final String context = isLoggingEnabled(level);
        if (context != null) put(level, "[" + context + "] " + message.get());
        return this;
    }

    public static enum LogDestination implements Consumer<String> {
        SYS_OUT(System.out::println),
        SYS_ERR(System.err::println)
        ;
        private final Consumer<String> consumer;
        LogDestination(Consumer<String> consumer) {this.consumer = consumer;}
        public void accept(String s) { consumer.accept(s); }
    }

    @Override
    public final Bus logToSysOut(LogLevel level) { return onLog(level, SYS_OUT); }
    @Override
    public final Bus logToSysErr(LogLevel level) { return onLog(level, SYS_ERR); }
    @Override
    public final Bus logToSysOut(Set<LogLevel> levels) { return onLog(levels, SYS_OUT); }
    @Override
    public final Bus logToSysErr(Set<LogLevel> levels) { return onLog(levels, SYS_ERR); }

    @Override
    public final Bus onLog(Consumer<String> action) { return onLog(DEFAULT, action); }
    @Override
    public final Bus onLog(LogLevel level, Consumer<String> action) { return onMsg(level, action);}
    @Override
    public final Bus onLog(Set<LogLevel> levels, Consumer<String> action) { levels.forEach(l -> onMsg(l, action)); return this; }


    private final Set<String> loggingShortcuts = new ConcurrentSkipListSet<>();

    @Override
    public String isLoggingEnabled(LogLevel level) {
        String spec = this.<LogSpec, String>peek(LogSpec.SPEC);
        if (isLocal() && spec == null) spec = global().<LogSpec, String>peek(LogSpec.SPEC);
        if (spec == null) return null;

        Class<?> caller = Stack.getCallingClassOutsidePackage(MY_PKG);
        String context = caller.getName();
        String shortcut = user() + level + context;

        if (loggingShortcuts.contains(shortcut)) return Stack.getCallingFrame(caller);

        Supplier<String> answer = () -> {
            loggingShortcuts.add(shortcut);
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

    enum LogSpec implements StringRef {SPEC}
}

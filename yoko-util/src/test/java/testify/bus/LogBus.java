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

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.EnumSet.range;

public interface LogBus extends EventBus {
    enum LogSpec implements StringRef {SPEC}
    enum LogLevel implements StringRef {
        ERROR, WARN, DEFAULT, INFO, DEBUG;
        Set<LogLevel> includedLevels() { return range(ERROR, this); }
        boolean includes(LogLevel level) { return includedLevels().contains(level); }
    }

    default String isLoggingEnabled(LogLevel level) { return global().isLoggingEnabled(level); }
    String isLoggingEnabled(String user, LogLevel level);

    default void enableLogging() { enableLogging(""); }
    default void enableLogging(String pattern) { enableLogging(LogLevel.DEFAULT, pattern); }
    default void enableLogging(LogLevel level, String pattern) {
        global().enableLogging(level, pattern);
    }
    void enableLogging(String user, LogLevel level, String pattern);


    default void log(Supplier<String> message) { log(LogLevel.DEFAULT, message);}
    default void log(String message) { log(LogLevel.DEFAULT, message); }
    default void log(LogLevel level, String message) { log(level, () -> message); }
    default void log(LogLevel level, Supplier<String> message) {
        final String context = isLoggingEnabled(level);
        if (context == null) return;
        put(level, "[" + context + "]" + message.get());
    }
    default void sendToErr() { onLog(System.err::println); }
    default void sendToErr(LogLevel level) { onLog(level, System.err::println); }
    default void onLog(Consumer<String> action) { onLog(LogLevel.DEFAULT, action); }

    default void onLog(LogLevel level, Consumer<String> action) {
        level.includedLevels().forEach(l -> onMsg(l, action));
    }

    Bus forUser(String user);
    default Bus global() { return forUser(QualifiedBus.GLOBAL_USER); }
}


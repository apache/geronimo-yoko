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
import java.util.stream.Stream;

import static java.util.EnumSet.range;
import static testify.bus.QualifiedBus.GLOBAL_USER;

public interface LogBus<B extends LogBus<B>> extends EventBus<B> {
    enum LogSpec implements StringRef {SPEC}
    enum LogLevel implements StringRef {
        DEBUG, INFO, DEFAULT, WARN, ERROR;
        Set<LogLevel> andHigher() { return range(this, ERROR); }
        boolean includes(LogLevel level) { return andHigher().contains(level); }
    }

    String isLoggingEnabled(String user, LogLevel level);
    B enableLogging(String user, LogLevel level, String pattern);
    B forUser(String user);

    default String isLoggingEnabled(LogLevel level) { return global().isLoggingEnabled(level); }
    default B enableLogging(String...patterns) { return enableLogging(LogLevel.DEFAULT, patterns); }
    default B enableLogging(LogLevel level, String...patterns) {
        if (patterns.length == 0) return enableLogging(level, ".*");
        Stream.of(patterns).forEach(p -> enableLogging(level, p));
        return self();
    }
    default B enableLogging(LogLevel level, String pattern) { return global().enableLogging(level, pattern); }
    default B enableLogging(Set<LogLevel> levels, String...patterns) { levels.forEach( l -> enableLogging(l, patterns)); return self(); }
    default B enableLogging(String user, Set<LogLevel> levels, String...patterns) { levels.forEach( l -> enableLogging(user, l, patterns)); return self(); }
    default B enableLogging(String user, LogLevel level, String...patterns) { forUser(user).enableLogging(level, patterns); return self(); }

    default B log(Supplier<String> message) { return log(LogLevel.DEFAULT, message);}
    default B log(String message) { return log(LogLevel.DEFAULT, message); }
    default B log(LogLevel level, String message) { return log(level, () -> message); }

    default B log(LogLevel level, Supplier<String> message) {
        final String context = isLoggingEnabled(level);
        if (context != null) put(level, "[" + context + "] " + message.get());
        return self();
    }

    default B logToSysOut(LogLevel level) { return onLog(level, System.out::println); }
    default B logToSysErr(LogLevel level) { return onLog(level, System.err::println); }
    default B logToSysOut(Set<LogLevel> levels) { return onLog(levels, System.out::println); }
    default B logToSysErr(Set<LogLevel> levels) { return onLog(levels, System.err::println); }

    default B onLog(Consumer<String> action) { return onLog(LogLevel.DEFAULT, action); }
    default B onLog(LogLevel level, Consumer<String> action) { return onMsg(level, action);}
    default B onLog(Set<LogLevel> levels, Consumer<String> action) { levels.forEach(l -> onMsg(l, action)); return self(); }

    default B global() { return forUser(GLOBAL_USER); }
}

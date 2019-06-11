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

public interface LogBus<B extends LogBus<B>> extends EventBus<B> {
    enum LogSpec implements StringRef {SPEC}
    enum LogLevel implements StringRef {
        ERROR, WARN, DEFAULT, INFO, DEBUG;
        Set<LogLevel> includedLevels() { return range(ERROR, this); }
        boolean includes(LogLevel level) { return includedLevels().contains(level); }
    }

    default String isLoggingEnabled(LogLevel level) { return global().isLoggingEnabled(level); }
    String isLoggingEnabled(String user, LogLevel level);

    default B enableLogging() { return enableLogging(""); }
    default B enableLogging(String pattern) { return enableLogging(LogLevel.DEFAULT, pattern); }
    default B enableLogging(LogLevel level, String pattern) {
        return global().enableLogging(level, pattern);
    }
    B enableLogging(String user, LogLevel level, String pattern);


    default B log(Supplier<String> message) { return log(LogLevel.DEFAULT, message);}
    default B log(String message) { return log(LogLevel.DEFAULT, message); }
    default B log(LogLevel level, String message) { return log(level, () -> message); }

    @SuppressWarnings("unchecked")
    default B log(LogLevel level, Supplier<String> message) {
        final String context = isLoggingEnabled(level);
        if (context != null) put(level, "[" + context + "] " + message.get());
        return (B)this;
    }
    default B logToSysOut(LogLevel level) { return onLog(level, System.out::println); }
    default B logToSysErr(LogLevel level) { return onLog(level, System.err::println); }
    default B onLog(Consumer<String> action) { return onLog(LogLevel.DEFAULT, action); }

    default B onLog(LogLevel level, Consumer<String> action) { return onMsg(level, action);}

    B forUser(String user);
    default B global() { return forUser(QualifiedBus.GLOBAL_USER); }
}


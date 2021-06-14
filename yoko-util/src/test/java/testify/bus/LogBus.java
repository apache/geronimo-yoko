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

// Provide logging functionality. This interface should remain package-private.
interface LogBus {
    String isLoggingEnabled(LogLevel level);
    Bus enableLogging(String... patterns);
    Bus enableLogging(LogLevel level, String... patterns);
    Bus log(Supplier<String> message);
    Bus log(String message);
    Bus log(LogLevel level, String message);
    Bus log(LogLevel level, Supplier<String> message);
    Bus logToSysOut(LogLevel level);
    Bus logToSysErr(LogLevel level);
    Bus logToSysOut(Set<LogLevel> levels);
    Bus logToSysErr(Set<LogLevel> levels);
    Bus onLog(Consumer<String> action);
    Bus onLog(LogLevel level, Consumer<String> action);
    Bus onLog(Set<LogLevel> levels, Consumer<String> action);
}

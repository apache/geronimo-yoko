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
package testify.bus;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Provide logging functionality. This interface should remain package-private.
interface LogBus {
    String isLoggingEnabled(TestLogLevel level);
    Bus enableLogging(String... patterns);
    Bus enableLogging(TestLogLevel level, String... patterns);
    Bus log(Supplier<String> message);
    Bus log(String message);
    Bus log(TestLogLevel level, String message);
    Bus log(TestLogLevel level, Supplier<String> message);
    Bus logToSysOut(TestLogLevel level);
    Bus logToSysErr(TestLogLevel level);
    Bus logToSysOut(Set<TestLogLevel> levels);
    Bus logToSysErr(Set<TestLogLevel> levels);
    Bus onLog(Consumer<String> action);
    Bus onLog(TestLogLevel level, Consumer<String> action);
    Bus onLog(Set<TestLogLevel> levels, Consumer<String> action);
}

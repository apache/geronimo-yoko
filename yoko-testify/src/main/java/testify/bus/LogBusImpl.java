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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static testify.bus.LogLevel.DEFAULT;
import static testify.util.ObjectUtil.getNextObjectLabel;

// Although some of the methods here are fluent in design
// (i.e. they return a Bus object suitable for method chaining)
// the internal implementations are expected to return null.
// The fluency is an affordance purely for the code calling
// objects accessible outside the package.
@SuppressWarnings("UnusedReturnValue")
class LogBusImpl implements LogBus {
    private final static DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final static DateTimeFormatter TIMER_FORMAT = DateTimeFormatter.ofPattern("mm:ss.SSS");
    private final static LocalDateTime EPOCH = LocalDateTime.ofInstant(Instant.EPOCH, ZoneId.ofOffset("", ZoneOffset.UTC));

    final String label = getNextObjectLabel(LogBus.class);
    final EventBusImpl eventBus;
    private final Set<String> shortcuts;
    private final ZonedDateTime startTime = ZonedDateTime.now();
    private final Consumer<String> SYS_OUT = s -> System.out.println(convertLog(s));
    private final Consumer<String> SYS_ERR = s -> System.err.println(convertLog(s));

    LogBusImpl(EventBusImpl eventBus, Set<String> shortcuts) {
        this.eventBus = eventBus;
        this.shortcuts = shortcuts;
    }

    private static final String[] DEFAULT_PATTERNS = {".*"};

    enum LogSpec implements StringSpec {SPEC}

    private static final Package MY_PKG = LogBusImpl.class.getPackage();

    @Override
    public Bus enableLogging(String... patterns) { enableLogging(DEFAULT, patterns); return null; }
    @Override
    public Bus enableLogging(LogLevel level, String... patterns) {
        String newSpec = Stream.of(patterns.length == 0 ? DEFAULT_PATTERNS : patterns)
                .filter(this::validateLoggingPattern)
                .map(s -> s + "=" + level)
                .collect(Collectors.joining(":"));
        // add this new pattern to the existing spec if any
        String spec = Optional.ofNullable(eventBus.peek(LogSpec.SPEC))
                .map(s -> s + ":" + newSpec)
                .orElse(newSpec);
        eventBus.put(LogSpec.SPEC, spec);
        System.out.println("### new log spec is " + spec);
        System.out.println();
        System.out.flush();
        return null;
    }

    private boolean validateLoggingPattern(String s) {
        if (!!! s.contains("=")) return true;
        System.err.println("### ignoring logging pattern " + s + " because it contains the '=' character");
        return false;
    }

    @Override
    public Bus log(Supplier<String> message) { log(DEFAULT, message); return null; }
    @Override
    public Bus log(String message) { log(DEFAULT, message); return null; }
    @Override
    public Bus log(LogLevel level, String message) { log(level, () -> message); return null; }

    @Override
    public Bus log(LogLevel level, Supplier<String> message) {
        final String context = isLoggingEnabled(level);
        if (context != null) eventBus.put(level, String.format("[%s][%s]%s", timestamp(), context, message.get()));
        return null;
    }

    private static String timestamp() { return ZonedDateTime.now().format(TIMESTAMP_FORMAT); }

    private static ZonedDateTime toDate(String timestamp) { return TIMESTAMP_FORMAT.parse(timestamp, ZonedDateTime::from); }

    private static String elapsedTime(Temporal start, Temporal end) { return EPOCH.plus(Duration.between(start, end)).format(TIMER_FORMAT); }

    private String elapsedTime(String timestamp) { return elapsedTime(startTime, toDate(timestamp)); }

    private String convertLog(String log) {
        int i = 1;
        int j = log.indexOf(']');
        String timestamp = log.substring(i, j);
        String remainder = log.substring(j + 1);
        return '[' + elapsedTime(timestamp) + ']' + remainder;
    }

    @Override
    public Bus logToSysOut(LogLevel level) { onLog(level, SYS_OUT); return null; }
    @Override
    public Bus logToSysErr(LogLevel level) { onLog(level, SYS_ERR); return null; }
    @Override
    public Bus logToSysOut(Set<LogLevel> levels) { onLog(levels, SYS_OUT); return null; }
    @Override
    public Bus logToSysErr(Set<LogLevel> levels) { onLog(levels, SYS_ERR); return null; }

    @Override
    public Bus onLog(Consumer<String> action) { onLog(DEFAULT, action); return null; }
    @Override
    public Bus onLog(LogLevel level, Consumer<String> action) { eventBus.onMsg(level, action); return null; }
    @Override
    public Bus onLog(Set<LogLevel> levels, Consumer<String> action) { levels.forEach(l -> onLog(l, action)); return null; }

    @Override
    public String isLoggingEnabled(LogLevel level) {
        String spec = eventBus.peek(LogSpec.SPEC);
        if (spec == null) return null;

        Class<?> caller = Stack.getCallingClassOutsidePackage(MY_PKG);
        String context = caller.getName();
        String shortcut = eventBus.userBus.user + level + context;

        if (shortcuts.contains(shortcut)) return Stack.getCallingFrame(caller);

        Supplier<String> answer = () -> {
            shortcuts.add(shortcut);
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
                LogLevel specifiedLevel = LogLevel.valueOf(levelSpec);
                if (specifiedLevel.includes(level)) return answer.get();
            } catch (Throwable t) {
                System.err.println("Unknown level '" + levelSpec + "' in logging specification '" + spec + "'");
            }
        }
        return null;
    }

    @Override
    public String toString() { return String.format("%s[%s]", label, eventBus.userBus.user); }
}

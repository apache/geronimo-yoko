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
package testify.annotation.logging;

import testify.bus.Bus;
import testify.parts.PartRunner;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.Math.max;
import static testify.annotation.logging.LogRecorder.StringMessage.HELLO;
import static testify.annotation.logging.LogRecorder.requestLogRecords;
import static testify.annotation.logging.LogRecorder.requestThreadTable;
import static testify.annotation.logging.LogRecorder.sendPushSettings;

/**
 * Responsible for starting logging, capturing logs, and formatting them.
 */
public class LogPublisher {
    private final LogRecorder localRecorder;
    private final Bus initialBus;
    private final Set<Bus> dedicatedBuses = new HashSet<>();
    private final Deque<List<LogSetting>> settingsStack = new ArrayDeque<>();
    private final PartRunner runner;
    private int partNameLength = 6;
    private volatile PrintWriter out = new PrintWriter(System.out);

    private boolean testWentWrong;

    static LogPublisher create(PartRunner runner) {
        LogPublisher publisher = new LogPublisher(runner);
        publisher.initialize();
        return publisher;
    }

    private LogPublisher(PartRunner runner) {
        this.runner = runner;
        this.localRecorder = new LogRecorder("parent", runner, System.currentTimeMillis());
        this.initialBus = localRecorder.initialBus;
    }

    private void initialize() {
        // start listening for incoming messages from LogRecorders
        initialBus.onMsg(HELLO, this::addRecorder);
        localRecorder.initialize();
    }

    // called whenever a LogRecorder sends a HELLO
    private synchronized void addRecorder(String processName) {
        Objects.requireNonNull(processName);
        this.partNameLength = max(partNameLength, processName.length());
        Bus dedicatedBus = runner.bus(LogRecorder.getDedicatedBusName(processName));
        dedicatedBuses.add(dedicatedBus);
        // send existing settings to
        settingsStack.forEach(list -> sendPushSettings(list, dedicatedBus));
    }

    // Allow output to be redirected, purely to test this class
    void setOut(PrintWriter newOut) {
        System.out.println("### redirecting output from " + this.out + " to " + newOut);
        this.out = newOut;
    }

    void pushSettings(List<LogSetting> settings) {
        settingsStack.push(settings);
        if (settings.isEmpty()) return; // no need to send empty settings
        dedicatedBuses.forEach(bus -> sendPushSettings(settings, bus));
    }
    void popSettings() {
        List<?> popped = settingsStack.pop();
        if (popped.isEmpty()) return; // must not pop empty settings because they were never pushed
        dedicatedBuses.forEach(LogRecorder::sendPopSettings);
    }

    void somethingWentWrong(Throwable throwable) { this.testWentWrong = true; }

    void flushLogs(String displayName) {
        // if there were no log settings, do nothing at all
        if (settingsStack.stream().allMatch(List::isEmpty)) return;

        out.printf(">>>FLUSHING LOGS [%s] <<<%n", displayName);

        // PRINT THREAD TABLE
        long count = dedicatedBuses.stream()
                .map(bus -> requestThreadTable(bus, partNameLength))
                .flatMap(List::stream)
                .peek(out::println)
                .count();

        // IF NO THREADS, QUIT NOW
        if (0L == count) {
            out.printf("No logs recorded.%n");
            return;
        }

        char flag = testWentWrong ? '\u274C' : '\u2714'; // cross or tick character
        testWentWrong = false;

        out.printf("%c%1$c%1$cBEGIN LOG REPLAY [%s] %1$c%1$c%1$c%n", flag, displayName);

        dedicatedBuses.stream()
                .map(bus -> requestLogRecords(bus, partNameLength))
                .flatMap(List::stream)
                .sorted()
                .forEach(out::println);

        out.printf("%c%1$c%1$cEND LOG REPLAY [%s] %1$c%1$c%1$c%n", flag, displayName);
        out.flush();
    }

    public void close() { dedicatedBuses.forEach(LogRecorder::close); }
}

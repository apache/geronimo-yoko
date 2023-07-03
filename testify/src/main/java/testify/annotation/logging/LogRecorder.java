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
import testify.bus.IntSpec;
import testify.bus.ListSpec;
import testify.bus.StringListSpec;
import testify.bus.StringSpec;
import testify.bus.TypeSpec;
import testify.parts.PartRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static testify.annotation.logging.LogRecorder.IntMessage.REQUEST_ID;
import static testify.annotation.logging.LogRecorder.SettingsMessage.PUSH_SETTINGS;
import static testify.annotation.logging.LogRecorder.SimpleMessage.CLOSE;
import static testify.annotation.logging.LogRecorder.SimpleMessage.POP_SETTINGS;
import static testify.annotation.logging.LogRecorder.StringMessage.HELLO;
import static testify.annotation.logging.LogRecorder.StringMessage.REQUEST_LOG_RECORDS;
import static testify.annotation.logging.LogRecorder.StringMessage.REQUEST_THREAD_TABLE;
import static testify.annotation.logging.LogRecorder.StringsMessage.REPLY_LOG_RECORDS;
import static testify.annotation.logging.LogRecorder.StringsMessage.REPLY_THREAD_TABLE;
import static testify.util.Queues.drain;
import static testify.util.Queues.drainInOrder;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class LogRecorder {
    enum StringMessage implements StringSpec { HELLO, REQUEST_THREAD_TABLE, REQUEST_LOG_RECORDS }
    enum IntMessage implements IntSpec {REQUEST_ID}
    enum SettingsMessage implements ListSpec<LogSetting> {
        PUSH_SETTINGS;
        public TypeSpec<LogSetting> getElementTypeSpec() { return LogSetting.Spec.SEND_SETTING; }
    }
    enum SimpleMessage implements TypeSpec<SimpleMessage> {POP_SETTINGS, CLOSE}
    enum StringsMessage implements StringListSpec {REPLY_THREAD_TABLE, REPLY_LOG_RECORDS };

    public static final String INITIAL_BUS_NAME = LogRecorder.class.getName();
    private static final Logger ROOT_LOGGER = Logger.getLogger("");
    private static final String RECEIPT_FORMAT = "REQUEST %d RECEIVED";

    final Bus initialBus;
    final long startTime;
    private final Bus dedicatedBus;
    private final String processName;
    private final Deque<List<LogSetting>> settingsStack = new ArrayDeque<>();
    private final Queue<Thread> newThreads = new ConcurrentLinkedQueue<>();
    /** The full list of per-thread logging journals to be merged chronologically before printing. */
    private final Queue<Journal> journals = new ConcurrentLinkedQueue<>();
    /** Create a new journal for each thread to avoid forcing synchronization. */
    private final ThreadLocal<Journal> journalsByThread = ThreadLocal.withInitial(() -> {
        newThreads.add(Thread.currentThread());
        Journal result = new Journal();
        journals.add(result);
        return result;
    });
    private final CodeNaming<Long> threadNames = new CodeNaming<>();
    private final Handler handler = new Handler() {
        public void publish(LogRecord record) {
            journalsByThread.get().add(record);
        }
        public void flush() {}
        public void close() {
            ROOT_LOGGER.removeHandler(this);
        }
    };



    LogRecorder(String processName, PartRunner runner, long startTime) { this(processName, runner.bus(INITIAL_BUS_NAME), runner.bus(getDedicatedBusName(processName)), startTime); }
    LogRecorder(String processName, Bus bus, long startTime) { this(processName, bus.forUser(INITIAL_BUS_NAME), bus.forUser(getDedicatedBusName(processName)), startTime); }
    private LogRecorder(String processName, Bus initialBus, Bus dedicatedBus, long startTime) {
        this.initialBus = initialBus;
        this.processName = processName;
        this.dedicatedBus = dedicatedBus;
        this.startTime = startTime;
        ROOT_LOGGER.addHandler(handler);
        handler.setLevel(Level.ALL);
    }

    static String getDedicatedBusName(String processName) { return INITIAL_BUS_NAME + "#" + processName; }

    void initialize() {
        // prepare to receive and handle requests
        dedicatedBus.onMsg(PUSH_SETTINGS, this::receivePushSettings);
        dedicatedBus.onMsg(POP_SETTINGS, this::receivePopSettings);
        dedicatedBus.onMsg(REQUEST_THREAD_TABLE, this::replyThreadTable);
        dedicatedBus.onMsg(REQUEST_LOG_RECORDS, this::replyLogRecords);
        dedicatedBus.onMsg(CLOSE, bus -> Logger.getLogger("").removeHandler(handler));
        // make myself known to the publisher
        initialBus.put(HELLO, processName);
    }

    private static int getNextRequestId(Bus dedicatedBus) {
        synchronized (dedicatedBus) {
            final int requestId;
            requestId = Optional.ofNullable(dedicatedBus.peek(REQUEST_ID)).map(i -> i + 1).orElse(1);
            dedicatedBus.put(REQUEST_ID, requestId);
            return requestId;
        }
    }

    static synchronized void sendPushSettings(List<LogSetting> settings, Bus dedicatedBus) {
        // must only be called within the parent process
        final int requestId = getNextRequestId(dedicatedBus);
        dedicatedBus.put(PUSH_SETTINGS, settings);
        dedicatedBus.get(String.format(RECEIPT_FORMAT, requestId));
    }

    private void receivePushSettings(List<LogSetting> settings) {
        settings.forEach(LogSetting::apply);
        settingsStack.push(settings);
        int requestId = dedicatedBus.get(REQUEST_ID);
        dedicatedBus.put(String.format(RECEIPT_FORMAT, requestId), "settings applied");
    }

    static synchronized void sendPopSettings(Bus dedicatedBus) {
        // must only be called within the parent process
        final int requestId = getNextRequestId(dedicatedBus);
        dedicatedBus.put(POP_SETTINGS);
        dedicatedBus.get(String.format(RECEIPT_FORMAT, requestId));
    }

    private void receivePopSettings() {
        settingsStack.pop().forEach(LogSetting::undo);
        int requestId = dedicatedBus.get(REQUEST_ID);
        dedicatedBus.put(String.format(RECEIPT_FORMAT, requestId), "settings popped");
    }

    static synchronized List<String> requestThreadTable(Bus dedicatedBus, int partNameLength) {
        // must only be called within the parent process
        final String threadFormat = "THREAD KEY:  %" + partNameLength + "s %8s  id=%08x  state=%-13s  %s";
        final int requestId = getNextRequestId(dedicatedBus);
        dedicatedBus.put(REQUEST_THREAD_TABLE, threadFormat);
        // wait for completion of remote processing
        dedicatedBus.get(String.format(RECEIPT_FORMAT, requestId));
        // and then retrieve the result
        return dedicatedBus.get(REPLY_THREAD_TABLE);
    }

    private void replyThreadTable(String threadFormat) {
        Function<Thread, String> formatter = t ->
                String.format(threadFormat,
                        this.processName,
                        threadNames.get(t.getId()),
                        t.getId(),
                        t.getState(),
                        t.getName());
        List<String> threadTable = drain(newThreads).map(formatter).collect(toList());
        int requestId = dedicatedBus.get(REQUEST_ID);
        dedicatedBus.put(REPLY_THREAD_TABLE, threadTable);
        dedicatedBus.put(String.format(RECEIPT_FORMAT, requestId), "returned thread table");
    }

    static synchronized List<String> requestLogRecords(Bus dedicatedBus, int partNameLength) {
        // must only be called within the parent process
        final String logFormat = "LOG: %02d.%03d  %" + partNameLength + "s %8s  [%s]  %s";
        final int requestId;
        requestId = getNextRequestId(dedicatedBus);
        dedicatedBus.put(REQUEST_LOG_RECORDS, logFormat);
        // wait for completion of remote processing
        dedicatedBus.get(String.format(RECEIPT_FORMAT, requestId));
        // and then retrieve the result
        return dedicatedBus.get(REPLY_LOG_RECORDS);
    }

    private void replyLogRecords(String logFormat) {
        Function<LogRecord, String> formatter = rec -> format(rec, logFormat);
        List<String> logRecords = drainInOrder(journals).map(formatter).collect(toList());;
        int requestId = dedicatedBus.get(REQUEST_ID);
        dedicatedBus.put(REPLY_LOG_RECORDS, logRecords);
        dedicatedBus.put(String.format(RECEIPT_FORMAT, requestId), "returned log records");
    }

    private String format(LogRecord rec, String logFormat) {
        // format: ss.mmm  _____tid  [logger]  message
        long millis = rec.getMillis() - startTime;
        String result = String.format(logFormat,
                millis / 1000,
                millis % 1000,
                this.processName,
                threadNames.get((long) rec.getThreadID()),
                rec.getLoggerName(),
                rec.getMessage());
        Throwable throwable = rec.getThrown();
        if (null != throwable) result = String.format("%s%n%s", result, formatThrowable(throwable));
        return result;
    }

    private static String formatThrowable(Throwable t) {
        try (StringWriter sw = new StringWriter()) {
            try (PrintWriter out = new PrintWriter(sw)) {
                out.printf("Exception was %s%n", t);
                t.printStackTrace(out);
                return sw.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void close(Bus dedicatedBus) { dedicatedBus.put(CLOSE); }

}
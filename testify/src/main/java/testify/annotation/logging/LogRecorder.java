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
import testify.bus.TypeSpec;
import testify.bus.key.CollectionSpec;
import testify.bus.key.IntSpec;
import testify.bus.key.ListSpec;
import testify.bus.key.StringListSpec;
import testify.bus.key.StringSpec;
import testify.bus.key.VoidSpec;
import testify.io.Stringifiable;
import testify.parts.ProcessRunner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static testify.annotation.logging.LogRecorder.IntMessage.REQUEST_ID;
import static testify.annotation.logging.LogRecorder.LogFormatter.Spec.REQUEST_LOG_RECORDS;
import static testify.annotation.logging.LogRecorder.SettingsMessage.PUSH_SETTINGS;
import static testify.annotation.logging.LogRecorder.SettingsStackMessage.INITIALIZE_SETTINGS_STACK;
import static testify.annotation.logging.LogRecorder.SimpleMessage.CLOSE;
import static testify.annotation.logging.LogRecorder.SimpleMessage.POP_SETTINGS;
import static testify.annotation.logging.LogRecorder.StringMessage.HELLO;
import static testify.annotation.logging.LogRecorder.StringsMessage.REPLY_LOG_RECORDS;
import static testify.annotation.logging.LogRecorder.StringsMessage.REPLY_THREAD_TABLE;
import static testify.annotation.logging.LogRecorder.SyncPoint.READY_FOR_CLOSE;
import static testify.annotation.logging.LogRecorder.ThreadFormatter.Spec.REQUEST_THREAD_TABLE;
import static testify.streams.Collectors.forbidCombining;
import static testify.streams.Streams.stream;
import static testify.util.Queues.drain;
import static testify.util.Queues.drainInOrder;

@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class  LogRecorder {
    enum StringMessage implements StringSpec {HELLO}
    enum IntMessage implements IntSpec {REQUEST_ID}
    enum SettingsMessage implements ListSpec<LogSetting> {
        PUSH_SETTINGS;
        public TypeSpec<LogSetting> getElementTypeSpec() { return LogSetting.Spec.SEND_SETTING; }
    }
    enum SettingsStackMessage implements CollectionSpec<Deque<List<LogSetting>>, List<LogSetting>> {
        INITIALIZE_SETTINGS_STACK;
        @Override
        public TypeSpec<List<LogSetting>> getElementTypeSpec() { return PUSH_SETTINGS; }
    }
    enum SimpleMessage implements TypeSpec<SimpleMessage> {POP_SETTINGS, CLOSE}
    enum SyncPoint implements VoidSpec {READY_FOR_CLOSE}
    enum StringsMessage implements StringListSpec {REPLY_THREAD_TABLE, REPLY_LOG_RECORDS };

    public static final String INITIAL_BUS_NAME = LogRecorder.class.getName();
    private static final Logger ROOT_LOGGER = Logger.getLogger("");
    private static final String RECEIPT_FORMAT = "REQUEST %d RECEIVED";

    final Bus initialBus;
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
    private final Handler handler = new Handler() {
        public void publish(LogRecord record) {
            journalsByThread.get().add(record);
        }
        public void flush() {}
        public void close() {
            ROOT_LOGGER.removeHandler(this);
        }
    };
    private final KeepAlive keepAlive = new KeepAlive();
    private CodeNaming<Long> threadNames;

    static LogRecorder create(Bus bus) {
        String processName = bus.user();
        Bus iBus = bus.forUser(INITIAL_BUS_NAME);
        Bus dBus = bus.forUser(getDedicatedBusName(processName));
        return new LogRecorder(processName, iBus, dBus).initialize();
    }

    private LogRecorder(String processName, Bus initialBus, Bus dedicatedBus) {
        this.initialBus = initialBus;
        this.processName = processName;
        this.dedicatedBus = dedicatedBus;
        ROOT_LOGGER.addHandler(handler);
        handler.setLevel(Level.ALL);
    }

    // To test this class, it is useful to be able to retrieve some internals
    synchronized Deque<List<LogSetting>> copySettingsStack() {
        return settingsStack.stream()
                .map(ArrayList::new) // copy each list
                .collect(ArrayDeque::new, Deque::add, forbidCombining()); // collect into new deque
    }

    static String getDedicatedBusName(String processName) { return INITIAL_BUS_NAME + "#" + processName; }

    // This is the only case where a lock is obtained for the recorder BEFORE the publisher.
    // It is acceptable in this case because the recorder is not yet known to the publisher.
    // Once initialization is complete, locks must always be acquired in publisher-then-recorder order
    synchronized LogRecorder initialize() {
        // prepare to receive and handle requests
        dedicatedBus.onMsg(PUSH_SETTINGS, this::receivePushSettings);
        dedicatedBus.onMsg(POP_SETTINGS, this::receivePopSettings);
        dedicatedBus.onMsg(REQUEST_THREAD_TABLE, this::replyThreadTable);
        dedicatedBus.onMsg(REQUEST_LOG_RECORDS, this::replyLogRecords);
        if (ProcessRunner.isChildProcess()) keepAlive.begin();
        dedicatedBus.onMsg(CLOSE, this::close);
        // make myself known to the publisher
        dispatchRequestAndWaitForReply(initialBus, HELLO, processName);
        // wait for the settings
        Deque<List<LogSetting>> stack = dedicatedBus.get(INITIALIZE_SETTINGS_STACK);
        settingsStack.addAll(stack);
        stream(stack.descendingIterator()).flatMap(List::stream).forEach(LogSetting::apply);
        dispatchReply("initial settings applied");
        return this;
    }

    static void sendInitialSettings(Bus dedicatedBus, Deque<List<LogSetting>> stack) {
        dispatchRequestAndWaitForReply(dedicatedBus, INITIALIZE_SETTINGS_STACK, stack);
    }

    private static<K extends Enum<K>&TypeSpec<T>, T> void dispatchRequestAndWaitForReply(Bus bus, K requestType) {
        int id = getNextRequestId(bus);
        bus.put(requestType);
        bus.get(String.format(RECEIPT_FORMAT, id));
    }

    private static<K extends Enum<K>&TypeSpec<T>, T> void dispatchRequestAndWaitForReply(Bus bus, K requestType, T requestPayload) {
        final int id = getNextRequestId(bus);
        bus.put(requestType, requestPayload);
        bus.get(String.format(RECEIPT_FORMAT, id));
    }

    private void dispatchReply(String s) {
        dispatchReply(dedicatedBus, s);
    }

    static void dispatchReply(Bus bus, String s) {
        int requestId = bus.get(REQUEST_ID);
        bus.put(String.format(RECEIPT_FORMAT, requestId), s);
    }

    private static int getNextRequestId(Bus dedicatedBus) {
        synchronized (dedicatedBus) {
            final int requestId = Optional.ofNullable(dedicatedBus.peek(REQUEST_ID)).map(i -> i + 1).orElse(1);
            dedicatedBus.put(REQUEST_ID, requestId);
            return requestId;
        }
    }

    static void sendPushSettings(Bus dedicatedBus, List<LogSetting> settings) {
        dispatchRequestAndWaitForReply(dedicatedBus, PUSH_SETTINGS, settings);
    }

    private synchronized void receivePushSettings(List<LogSetting> settings) {
        settings.forEach(LogSetting::apply);
        settingsStack.push(settings);
        dispatchReply("settings applied");
    }

    static void sendPopSettings(Bus dedicatedBus) { dispatchRequestAndWaitForReply(dedicatedBus, POP_SETTINGS); }

    private synchronized void receivePopSettings() {
        List<LogSetting> popped = settingsStack.pop();
        // If multiple settings are for the same logger, they must be undone in reverse order
        Collections.reverse(popped);
        popped.forEach(LogSetting::undo);
        dispatchReply("settings popped");
    }

    /** Retrieve the aggregated thread table for all processes */
    static List<String> requestThreadTable(Deque<Bus> dedicatedBuses, int partNameWidth) {
        ThreadFormatter formatter = new ThreadFormatter(partNameWidth);
        return dedicatedBuses.stream()
                .map(bus -> requestThreadTable(bus, formatter))
                .peek(formatter::updateThreadCount)
                .flatMap(List::stream)
                .collect(toList());
    }

    private static List<String> requestThreadTable(Bus dedicatedBus, ThreadFormatter formatter) {
        dispatchRequestAndWaitForReply(dedicatedBus, REQUEST_THREAD_TABLE, formatter);
        // retrieve the result
        return dedicatedBus.get(REPLY_THREAD_TABLE);
    }

    private synchronized void replyThreadTable(ThreadFormatter formatter) {
        // Keep (replace) the local version with the one passed in from the bus.
        // This will be used when the log records are collected.
        this.threadNames = formatter.threadNames;
        formatter.recorder = this;
        List<String> threadTable = drain(newThreads).map(formatter).collect(toList());
        dedicatedBus.put(REPLY_THREAD_TABLE, threadTable);
        dispatchReply("returned thread table");
    }

    static class ThreadFormatter implements Function<Thread, String>, Stringifiable {
        enum Spec implements TypeSpec<ThreadFormatter> {REQUEST_THREAD_TABLE}

        final int partNameWidth;
        int threadCount;
        transient final CodeNaming<Long> threadNames;
        transient final String format;
        transient LogRecorder recorder;

        // constructor for requests
        ThreadFormatter(int partNameWidth) {
            this.partNameWidth = partNameWidth;
            this.threadCount = 0;
            this.format = null;
            this.threadNames = null;
        }

        public String stringify() { return partNameWidth + " " + threadCount; }

        // constructor called from unstringify()
        private ThreadFormatter(String s) {
            Scanner scan = new Scanner(s);
            this.partNameWidth = scan.nextInt();
            this.threadCount = scan.nextInt();
            this.format = "THREAD KEY:  %" + partNameWidth + "s %8s  id=%08x  state=%-13s  %s";
            this.threadNames = new CodeNaming<>(threadCount);
        }

        void updateThreadCount(List<String> subTable) {
            threadCount += subTable.size();
        }

        @Override
        public String apply(Thread t) {
            return String.format(format, recorder.processName, threadNames.get(t.getId()), t.getId(), t.getState(), t.getName());
        }
    }

    static synchronized List<String> requestLogRecords(Bus dedicatedBus, long startTime, int partNameWidth) {
        // must only be called within the parent process
        final LogFormatter formatter = new LogFormatter(startTime, partNameWidth);
        dispatchRequestAndWaitForReply(dedicatedBus, REQUEST_LOG_RECORDS, formatter);
        // and then retrieve the result
        return dedicatedBus.get(REPLY_LOG_RECORDS);
    }

    static class LogFormatter implements Function<LogRecord, String>, Stringifiable {
        enum Spec implements TypeSpec<LogFormatter> {REQUEST_LOG_RECORDS}
        final long startTime;
        final int partNameWidth;
        transient final String format;
        transient LogRecorder recorder;

        LogFormatter(long startTime, int partNameWidth) {
            this.startTime = startTime;
            this.partNameWidth = partNameWidth;
            this.format = null;
        }

        public String stringify() { return startTime + " " + partNameWidth; }

        LogFormatter(String s) {
            Scanner scan = new Scanner(s);
            this.startTime = scan.nextLong();
            this.partNameWidth = scan.nextInt();
            this.format = "LOG: %02d.%03d  %" + partNameWidth + "s %8s  [%s] %7s: %s";
        }

        @Override
        public String apply(LogRecord rec) {
            long millis = rec.getMillis() - startTime;
            String result = String.format(format,
                    millis / 1000,
                    millis % 1000,
                    recorder.processName,
                    recorder.threadNames.get((long) rec.getThreadID()),
                    rec.getLoggerName(),
                    rec.getLevel(),
                    rec.getMessage());
            Throwable throwable = rec.getThrown();
            if (null != throwable) result = String.format("%s%n%s", result, formatThrowable(throwable));
            return result;
        }

        @Override
        public String toString() {
            return "LogFormatter { startTime=" + startTime + ", format=" + format + "}";
        }
    }

    private synchronized void replyLogRecords(LogFormatter formatter) {
        formatter.recorder = this;
        List<String> logRecords = drainInOrder(journals).map(formatter).collect(toList());
        dedicatedBus.put(REPLY_LOG_RECORDS, logRecords);
        dispatchReply("returned log records");
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

    // idempotent
    static void close(Bus dedicatedBus) { dispatchRequestAndWaitForReply(dedicatedBus, CLOSE); }

    //idempotent
    private synchronized void close() {
        ROOT_LOGGER.removeHandler(handler);
        while (!settingsStack.isEmpty()) receivePopSettings(); // undo all the remaining settings
        dispatchReply(processName + " recorder closed");
        keepAlive.end(); // allow the keep-alive thread to die
    }

    private class KeepAlive extends Thread {
        CountDownLatch closeLatch = new CountDownLatch(1);
        KeepAlive() { super("testify-keep-alive-" + processName); }
        public void run() {
            dedicatedBus.put(READY_FOR_CLOSE);
            try { closeLatch.await(); } catch (InterruptedException e) { throw new Error(e); }
        }
        void begin() { start(); dedicatedBus.get(READY_FOR_CLOSE); }
        void end() { closeLatch.countDown(); }
    }
}

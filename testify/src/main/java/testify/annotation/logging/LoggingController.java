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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.util.stream.Collectors.toList;
import static testify.util.Queues.drain;
import static testify.util.Queues.drainInOrder;

/**
 * Responsible for starting logging, capturing logs, and formatting them.
 */
@Logging
public class LoggingController {
    private final String processName = "<junit>";
    private final Handler handler = new Handler();
    private volatile PrintWriter out = new PrintWriter(System.out);
    private final Deque<List<LogSetting>> settingsStack = new ArrayDeque<>();
    private final long epoch = System.currentTimeMillis();
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

    private boolean badStuffHappened;

    // Allow output to be redirected, purely to test this class
    void setOut(PrintWriter newOut) {
        System.out.println("### redirecting output from " + this.out + " to " + newOut);
        this.out = newOut;
    }

    void registerLogHandler() { Logger.getLogger("").addHandler(handler); }
    void deregisterLogHandler() { Logger.getLogger("").removeHandler(handler); }
    void pushSettings(List<LogSetting> settings) { settingsStack.push(settings);}
    void popSettings() { settingsStack.pop().forEach(LogSetting::undo);}

    void somethingWentWrong(Throwable throwable) { this.badStuffHappened = true; }

    void flushLogs(String displayName) {
        // if there were no log settings, do nothing at all
        if (settingsStack.stream().allMatch(List::isEmpty)) return;

        out.printf(">>>FLUSHING LOGS [%s] <<<%n", displayName);

        // if there happen to be no logs yet, say so and return
        if (journals.stream().allMatch(Journal::isEmpty)) {
            out.printf("No logs recorded.%n");
            return;
        }

        char flag = badStuffHappened ? '\u274C' : '\u2714'; // cross or tick character
        badStuffHappened = false;
        // PRINT THREAD KEY
        List<String> tableOfThreads = drain(newThreads).map(this::describe).collect(toList());
        tableOfThreads.forEach(out::println);
        out.printf("%c%1$c%1$cBEGIN LOG REPLAY [%s] %1$c%1$c%1$c%n", flag, displayName);
        // PRINT LOGS
        List<String> logs = drainInOrder(journals).map(this::format).collect(toList());
        logs.forEach(out::println);
        out.printf("%c%1$c%1$cEND LOG REPLAY [%s] %1$c%1$c%1$c%n", flag, displayName);
        out.flush();
    }

    private String describe(Thread t) {
        return String.format("THREAD KEY:  %8s %8s  id=%08x  state=%-13s  %s", this.processName, threadNames.get(t.getId()), t.getId(), t.getState(), t.getName());
    }

    private String format(LogRecord rec) {
        // format: ss.mmm  _____tid  [logger]  message
        long millis = rec.getMillis() - epoch;
        String result = String.format("LOG: %02d.%03d  %8s %8s  [%s]  %s",
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

    private class Handler extends java.util.logging.Handler {
        public void publish(LogRecord record) { journalsByThread.get().add(record); }
        public void flush() {}
        public void close() throws SecurityException {}
    }

    static class LogSetting {
        private final Logger logger;
        private final Level oldLevel;

        LogSetting(Logging annotation) {
            System.out.println("### applying log setting: " + annotation);
            this.logger = Logger.getLogger(annotation.value());
            this.oldLevel = logger.getLevel();
            logger.setLevel(annotation.level().level);
        }

        void undo() {
            logger.setLevel(oldLevel); // this might be a no-op but that's ok
        }
    }
}

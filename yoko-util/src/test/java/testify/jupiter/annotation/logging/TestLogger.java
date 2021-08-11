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
package testify.jupiter.annotation.logging;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;
import static testify.util.Queues.drain;
import static testify.util.Queues.drainInOrder;

/**
 * Log each test and conditionally print out the log messages
 * as required by the annotation for that test.
 */
@Logging
public class TestLogger {
    private final Handler handler = new Handler();
    private final PrintWriter out = new PrintWriter(System.out);
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

    void registerLogHandler() { Logger.getLogger("").addHandler(handler); }
    void deregisterLogHandler() { Logger.getLogger("").removeHandler(handler); }

    public void before(ExtensionContext ctx) {
        List<LogSetting> settings = findRepeatableAnnotations(ctx.getElement(), Logging.class)
                        .stream()
                        .map(LogSetting::new)
                        .collect(Collectors.toList());
        settingsStack.push(settings);
        flushLogs("BEFORE: " + ctx.getDisplayName());
    }

    public void after(ExtensionContext ctx) {
        flushLogs("AFTER: " + ctx.getDisplayName());
        settingsStack.pop().forEach(LogSetting::undo);
    }

    public void somethingWentWrong(Throwable throwable) {
        this.badStuffHappened = true;
    }

    private void flushLogs(String displayName) {
        // if there were no log settings, do nothing at all
        if (settingsStack.stream().allMatch(List::isEmpty)) return;

        out.printf(">>>FLUSHING LOGS [%s] <<<%n", displayName);

        // if there happen to be no logs yet, say so and return
        if (journals.stream().allMatch(Journal::isEmpty)) {
            out.printf("No logs recorded.%n");
            return;
        }

        char flag = badStuffHappened ? '\u274C' : '\u2714';
        badStuffHappened = false;
        // PRINT THREAD KEY
        drain(newThreads).forEach(this::introduceThread);
        out.printf("%c%1$c%1$cBEGIN LOG REPLAY [%s] %1$c%1$c%1$c%n", flag, displayName);
        // PRINT LOGS
        drainInOrder(journals).forEachOrdered(this::printLog);
        out.printf("%c%1$c%1$cEND LOG REPLAY [%s] %1$c%1$c%1$c%n", flag, displayName);
        out.flush();
    }

    private void introduceThread(Thread t) {
        out.printf("THREAD KEY:  %8s  id=%08x  state=%-13s  %s%n", threadNames.get(t.getId()), t.getId(), t.getState(), t.getName());
    }

    private void printLog(LogRecord rec) {
        // format: ss.mmm  _____tid  [logger]  message
        long millis = rec.getMillis() - epoch;
        out.printf("LOG:  %02d.%03d  %8s  [%s]  %s%n",
                millis / 1000,
                millis % 1000,
                threadNames.get((long) rec.getThreadID()),
                rec.getLoggerName(),
                rec.getMessage());
        Optional.ofNullable(rec.getThrown()).ifPresent(this::printThrowable);
    }

    private void printThrowable(Throwable t) {
        out.printf("Exception was %s%n", t);
        t.printStackTrace(out);
    }

    private class Handler extends java.util.logging.Handler {
        public void publish(LogRecord record) { journalsByThread.get().add(record); }
        public void flush() {}
        public void close() throws SecurityException {}
    }

    private class LogSetting {
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

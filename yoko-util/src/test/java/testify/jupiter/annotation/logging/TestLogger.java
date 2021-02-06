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

import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static testify.jupiter.annotation.logging.CodeNameGenerator.toCodeNameMap;
import static testify.util.Queues.drainInOrder;

/**
 * Log each test and conditionally print out the log messages
 * as required by the annotation for that test.
 */
class TestLogger implements CloseableResource {
    private Map<Long, String> codeNames;

    private final PrintWriter out;
    private final List<LogSetting> settings;
    private long epoch;
    private Queue<Thread> threads;
    private Queue<Journal> journals;
    private ThreadLocal<Journal> journalsByThread;
    private char resultPrefix;

    TestLogger(List<Logging> annotations) {
        this.out = new PrintWriter(System.out);
        this.settings = unmodifiableList(annotations.stream()
                .map(this::asSetting)
                .collect(toList()));
        beforeTestExecution();
    }

    private LogSetting asSetting(Logging annotation) {
        return new LogSetting(annotation, this.getHandler(annotation));
    }

    void beforeTestExecution() {
        epoch = System.currentTimeMillis();
        threads = new ConcurrentLinkedQueue<>();
        journals = new ConcurrentLinkedQueue<>();
        journalsByThread = ThreadLocal.withInitial(() -> {
            threads.add(Thread.currentThread());
            Journal result = new Journal();
            journals.add(result);
            return result;
        });
    }

    void afterTestExecution(boolean hasTestFailed) {
        resultPrefix = hasTestFailed ? '\u274C' : '\u2714';
        // Casting
        this.codeNames = threads.stream().map(Thread::getId).collect(toCodeNameMap());
        // Opening credits
        threads.forEach(this::introduceThread);
        // Main feature
        drainInOrder(journals).forEachOrdered(this::printLog);
    }

    private void introduceThread(Thread t) {
        out.printf("%c          %8s  id=%08x  state=%-13s  %s%n", resultPrefix, codeNames.get(t.getId()), t.getId(), t.getState(), t.getName());
    }

    private void printLog(LogRecord rec) {
        // format: ss.mmm  _____tid  [logger]  message
        long millis = rec.getMillis() - epoch;
        out.printf("%c  %02d.%03d  %8s  [%s]  %s%n",
                resultPrefix,
                millis / 1000,
                millis % 1000,
                codeNames.get((long) rec.getThreadID()),
                rec.getLoggerName(),
                rec.getMessage());
        out.flush();
    }

    /** Get a handler with the logging level set for the specified annotation */
    public Handler getHandler(Logging annotation) {
        return new Handler() {
            { setLevel(annotation.level().level); }
            public void publish(LogRecord record) { journalsByThread.get().add(record); }
            public void flush() {}
            public void close() throws SecurityException {}
        };
    }

    public void close() {
        settings.forEach(LogSetting::undo);
    }
}

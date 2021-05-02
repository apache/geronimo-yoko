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
import testify.jupiter.annotation.logging.Logging.Suppression;
import testify.util.SerialUtil.SerializableConsumer;
import testify.util.SerialUtil.SerializableSupplier;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;
import static testify.jupiter.annotation.logging.CodeNameGenerator.toCodeNameMap;
import static testify.util.Queues.drainInOrder;

/**
 * Log each test and conditionally print out the log messages
 * as required by the annotation for that test.
 */
public class TestLogger {
    private Map<Long, String> codeNames;

    private final PrintWriter out = new PrintWriter(System.out);
    private final List<LogSetting> settings;
    private final long epoch = System.currentTimeMillis();
    private final Queue<Thread> threads = new ConcurrentLinkedQueue<>();
    private final Queue<Journal> journals = new ConcurrentLinkedQueue<>();
    private final ThreadLocal<Journal> journalsByThread = ThreadLocal.withInitial(() -> {
        threads.add(Thread.currentThread());
        Journal result = new Journal();
        journals.add(result);
        return result;
    });;

    TestLogger(List<Logging> annotations) {
        this.settings = unmodifiableList(annotations.stream()
                .map(this::asSetting)
                .collect(toList()));
    }

    private LogSetting asSetting(Logging annotation) {
        return new LogSetting(annotation, this.getHandler(annotation));
    }

    /** Get a serializable action to create a test logger if one is configured for the test) */
    public static Supplier<Optional<TestLogger>> getLogStarter(ExtensionContext ctx) {
        return Optional.of(findRepeatableAnnotations(ctx.getTestMethod(), Logging.class))
                .filter(l -> l.size() > 0)
                .map(annotations -> (SerializableSupplier<Optional<TestLogger>>)() -> Optional.of(new TestLogger(annotations)))
                .orElse(Optional::empty);
    }

    /** Get a logging action to run after a test has executed */
    public static Consumer<TestLogger> getLogFinisher(ExtensionContext ctx) {
        boolean hasTestFailed = ctx.getExecutionException().isPresent();
        boolean loggingNeeded = Suppression.forContext(ctx).isRequired(hasTestFailed);
        return (SerializableConsumer<TestLogger>) logger -> logger.finishLogging(loggingNeeded, hasTestFailed);
    }

    private void finishLogging(boolean loggingNeeded, boolean hasTestFailed) {
        if (!loggingNeeded) return;
        char flag = hasTestFailed ? '\u274C' : '\u2714';
        // BEGIN LOG
        out.printf("%1$c%1$c%1$cBEGIN TEST LOGGING REPLAY%1$c%1$c%1$c%n", flag);
        // PRINT THREAD KEY
        this.codeNames = threads.stream()
                .sorted((t1,t2) -> t1.getName().compareTo(t2.getName())) // sort in name order
                .map(Thread::getId)
                .collect(toCodeNameMap());
        threads.forEach(this::introduceThread);
        // PRINT LOGS
        drainInOrder(journals).forEachOrdered(this::printLog);
        // END LOG
        out.printf("%1$c%1$c%1$cEND TEST LOGGING REPLAY%1$c%1$c%1$c%n", flag);
        out.flush();
            settings.forEach(LogSetting::undo);
    }

    private void introduceThread(Thread t) {
        out.printf("THREAD KEY:  %8s  id=%08x  state=%-13s  %s%n", codeNames.get(t.getId()), t.getId(), t.getState(), t.getName());
    }

    private void printLog(LogRecord rec) {
        // format: ss.mmm  _____tid  [logger]  message
        long millis = rec.getMillis() - epoch;
        out.printf("LOG:  %02d.%03d  %8s  [%s]  %s%n",
                millis / 1000,
                millis % 1000,
                codeNames.get((long) rec.getThreadID()),
                rec.getLoggerName(),
                rec.getMessage());
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
}

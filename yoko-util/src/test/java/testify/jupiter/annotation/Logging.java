/*
 * =============================================================================
 * Copyright (c) 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.jupiter.annotation;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import testify.jupiter.annotation.impl.Steward;

import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedList;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * When used on a class containing Junit 5 (Jupiter) tests,
 * this annotation will enable logging for the duration of the test.
 */
@ExtendWith(LoggingExtension.class)
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    String value() default ""; // if unspecified, apply to the root logger
    LoggingLevel level() default LoggingLevel.ALL;
    boolean logOnSuccess() default false;
    enum LoggingLevel {
        OFF(Level.OFF),
        SEVERE(Level.SEVERE),
        WARNING(Level.WARNING),
        INFO(Level.INFO),
        CONFIG(Level.CONFIG),
        FINE(Level.FINE),
        FINER(Level.FINER),
        FINEST(Level.FINEST),
        ALL(Level.ALL);
        final Level level;
        LoggingLevel(Level level) { this.level = level; }
    }
}

/**
 * The steward handles enabling and disabling logging. It is stored using the Jupiter
 */
class LoggingSteward extends Steward<Logging> {
    final BufferedHandler bufferedHandler;
    final Logger logger;
    final Level oldLevel;

    LoggingSteward(AnnotatedElement elem) {
        super(Logging.class, elem);
        final String component = annotation.value();
        final Level newLevel = annotation.level().level;
        // set up the handlers
        bufferedHandler = new BufferedHandler(new PrintWriter(System.out));
        bufferedHandler.setLevel(newLevel);
        // find the logger
        logger = Logger.getLogger(component);
        oldLevel = logger.getLevel();
        // upgrade the logging if needed
        if (oldLevel == null || newLevel.intValue() < oldLevel.intValue()) logger.setLevel(newLevel);
        logger.addHandler(bufferedHandler);
    }

    void reset() { bufferedHandler.reset(); }

    void logResult(boolean hasTestFailed) {
        bufferedHandler.setResult(hasTestFailed);
        if (hasTestFailed || annotation.logOnSuccess()) bufferedHandler.logTest();
    }

    @Override
    public void close() {
        logger.setLevel(oldLevel); // this might be a no-op but that's ok
        logger.removeHandler(bufferedHandler);
    }

    static Optional<LoggingSteward> getSteward(ExtensionContext ctx) { return Steward.getOrCreate(ctx, LoggingSteward.class, LoggingSteward::new); }
}

class LoggingExtension implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    @Override
    public void beforeAll(ExtensionContext ctx) throws Exception {
        LoggingSteward.getSteward(ctx);
    }

    @Override
    public void beforeTestExecution(ExtensionContext ctx) throws Exception {
        LoggingSteward.getSteward(ctx).ifPresent(LoggingSteward::reset);
    }

    @Override
    public void afterTestExecution(ExtensionContext ctx) throws Exception {
        LoggingSteward.getSteward(ctx).ifPresent(steward -> steward.logResult(ctx.getExecutionException().isPresent()));
    }
}

class BufferedHandler extends Handler {
    static final class ThreadJournal extends LinkedList<LogRecord> implements Comparable<ThreadJournal> {
        final Thread thread = Thread.currentThread();

        @Override
        public int compareTo(ThreadJournal that) {
            final LogRecord thisRec = this.peek();
            final LogRecord thatRec = that.peek();
            if (null == thisRec) return (null == thatRec) ? 0 : 1;
            if (null == thatRec) return -1;
            return Long.signum(thisRec.getMillis() - thatRec.getMillis());
        }
    }

    final PrintWriter out;
    long epoch;
    Queue<ThreadJournal> buffers;
    ThreadLocal<ThreadJournal> buffer;
    char prefix;

    BufferedHandler(PrintWriter out) {
        this.out = out;
        reset();
    }

    public void reset() {
        epoch = System.currentTimeMillis();
        buffers = new ConcurrentLinkedQueue<>();
        buffer = ThreadLocal.withInitial(() -> {
            ThreadJournal result = new ThreadJournal();
            buffers.add(result);
            return result;
        });
    }

    void setResult(boolean hasTestFailed) {
        prefix = hasTestFailed ? '\u274C' : '\u2714';
    }

    void logTest() {
        // Run the opening credits
        buffers.stream().map(j -> j.thread).forEach(this::introduceThread);
        // and now for the main feature
        PriorityQueue<ThreadJournal> pq = new PriorityQueue<>(buffers);
        for (ThreadJournal nextJournal = pq.poll(); nextJournal != null; nextJournal = pq.poll()) {
            if (nextJournal.isEmpty()) continue;
            // the nextThread queue is no longer in the priority queue
            // so we can safely poll it, which can change its priority
            printLog(nextJournal.poll());
            if (nextJournal.isEmpty()) continue;
            // now the queue has a different priority
            // we can reinsert it into the priority queue
            pq.add(nextJournal);
        }
    }

    private void introduceThread(Thread t) {
        out.printf("%c %08x: [%13s] %s%n", prefix, t.getId(), t.getState(), t.getName());
    }

    private void printLog(LogRecord rec) {
        // format: ss.mmm  _____tid  [logger]  message
        out.printf("%c %02d.%03d  %08x  [%s]  %s%n",
                prefix,
                rec.getMillis() /1000,
                rec.getMillis() %1000,
                rec.getThreadID(),
                rec.getLoggerName(),
                rec.getMessage());
        out.flush();
    }

    @Override
    public void publish(LogRecord record) { buffer.get().add(record); }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}

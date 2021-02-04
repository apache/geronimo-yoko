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

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import testify.jupiter.annotation.impl.Steward;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.*;

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
    final Handler destinationHandler;
    final BufferedHandler bufferedHandler;
    final Logger logger;
    final Level oldLevel;

    LoggingSteward(AnnotatedElement elem) {
        super(Logging.class, elem);
        final String component = annotation.value();
        final Level newLevel = annotation.level().level;
        // set up the handlers
        destinationHandler = new ConsoleHandler();
        destinationHandler.setLevel(newLevel);
        bufferedHandler = new BufferedHandler();
        bufferedHandler.setLevel(newLevel);
        // find the logger
        logger = Logger.getLogger(component);
        oldLevel = logger.getLevel();
        // upgrade the logging if needed
        if (oldLevel == null || newLevel.intValue() < oldLevel.intValue()) logger.setLevel(newLevel);
        logger.addHandler(bufferedHandler);
    }

    boolean logOnSuccess() { return annotation.logOnSuccess(); }

    void reset() { bufferedHandler.reset(); }

    void flush() { bufferedHandler.flushTo(destinationHandler); }

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
        LoggingSteward.getSteward(ctx)
                .ifPresent(LoggingSteward::reset);
    }

    @Override
    public void afterTestExecution(ExtensionContext ctx) throws Exception {
        LoggingSteward.getSteward(ctx)
                .filter(steward -> ctx.getExecutionException()
                        .map(e -> true)
                        .orElseGet(steward::logOnSuccess))
                .ifPresent(LoggingSteward::flush);
    }
}

class BufferedHandler extends Handler {
    ConcurrentMap<Long, Queue<LogRecord>> buffers;
    ThreadLocal<Queue<LogRecord>> buffer;

    BufferedHandler() {
        reset();
    }

    private Queue<LogRecord> newQueueForThread(Long threadId) { return new LinkedList<>(); }
    private Queue<LogRecord> queueForThread() { return buffers.computeIfAbsent(Thread.currentThread().getId(), this::newQueueForThread); }

    public void reset() {
        buffers = new ConcurrentHashMap<>();
        buffer = ThreadLocal.withInitial(this::queueForThread);
    }

    public void flushTo(Handler handler) {
        PriorityQueue<Queue<LogRecord>> pq = new PriorityQueue<>( (l1, l2) -> {
            if (l1.isEmpty()) return l2.isEmpty() ? 0 : 1;
            if (l2.isEmpty()) return -1;
            return Long.signum(l1.peek().getMillis() - l2.peek().getMillis());
        });

        pq.addAll(buffers.values());

        for (Queue<LogRecord> nextThread = pq.poll(); nextThread != null; nextThread = pq.poll()) {
            if (nextThread.isEmpty()) continue;
            // the nextThread queue is no longer in the priority queue
            // so we can safely poll it, which can change its priority
            handler.publish(nextThread.poll());
            if (nextThread.isEmpty()) continue;
            // now the queue has a different priority
            // we can reinsert it into the priority queue
            // and we will see it again when it reaches the front of the queue again
            pq.add(nextThread);
        }
    }

    @Override
    public void publish(LogRecord record) { buffer.get().add(record); }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}

}

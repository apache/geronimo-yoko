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

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import testify.jupiter.annotation.impl.Steward;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
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
    final ConsoleHandler handler;
    final Logger logger;
    final Level oldLevel;

    LoggingSteward(AnnotatedElement elem) {
        super(Logging.class, elem);
        final String component = annotation.value();
        final Level newLevel = annotation.level().level;
        // set up the handler
        handler = new ConsoleHandler();
        handler.setLevel(newLevel);
        // find the logger
        logger = Logger.getLogger(component);
        oldLevel = logger.getLevel();
        // upgrade the logging if needed
        if (oldLevel == null || newLevel.intValue() < oldLevel.intValue()) logger.setLevel(newLevel);
        logger.addHandler(handler);
    }

    @Override
    public void close() {
        logger.setLevel(oldLevel); // this might be a no-op but that's ok
        logger.removeHandler(handler);
    }

    static void enableLogging(ExtensionContext ctx) { Steward.getOrCreate(ctx, LoggingSteward.class, LoggingSteward::new); }
}

class LoggingExtension implements BeforeAllCallback, BeforeEachCallback {
    @Override
    public void beforeAll(ExtensionContext ctx) throws Exception {
        LoggingSteward.enableLogging(ctx);
    }

    @Override
    public void beforeEach(ExtensionContext ctx) throws Exception {
        LoggingSteward.enableLogging(ctx);
    }
}

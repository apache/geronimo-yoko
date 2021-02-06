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

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Level;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * When used on a class containing Junit 5 (Jupiter) tests,
 * this annotation will enable logging for the duration of the test.
 */
@ExtendWith(LoggingExtension.class)
@Target({TYPE, METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Logging.Container.class)
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

    @Target({TYPE, METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Container {
        Logging[] value();
    }
}


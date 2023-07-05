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
package testify.annotation;

import org.junit.jupiter.api.extension.ExtendWith;
import testify.annotation.logging.LoggingExtension;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.logging.Level;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * On a class containing Junit 5 (Jupiter) tests,
 * this annotation will enable logging for all tests in that class.
 *
 * On a test method, it will add log settings for that test alone.
 * These settings will be processed after the class log settings, if any.
 */
@ExtendWith(LoggingExtension.class)
@Target({TYPE, METHOD, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Logging.Container.class)
public @interface Logging {
    String value() default ""; // if unspecified, apply to the root logger
    LoggingLevel level() default LoggingLevel.ALL;
    /** Use an enum here because {@link Level} is a class and can't be used as a return type for an annotation method */
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
        public final Level level;
        LoggingLevel(Level level) { this.level = level; }
    }

    @Target({TYPE, METHOD, ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface Container {
        Logging[] value();
    }
}


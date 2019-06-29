/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.jupiter;

import testify.bus.Bus;
import testify.bus.Bus.LogLevel;
import testify.parts.PartRunner;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.AnnotatedElement;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

@Target({ANNOTATION_TYPE, TYPE})
@Retention(RUNTIME)
public @interface Tracing {
    /** Specify which classes to trace */
    Class<?>[] classes() default {};
    /** Specify a regular expression to match trace sources */
    String value() default "";
    LogLevel level() default Bus.LogLevel.DEFAULT;
    LogLevel maxLevel() default Bus.LogLevel.ERROR;
    boolean disabled() default false;
}

enum TracingSteward {
    ;
    static void addTraceSettings(PartRunner runner, AnnotatedElement elem) {
        findAnnotation(elem, Tracing.class).ifPresent(trc -> TracingSteward.addTraceSettings(runner, trc));
    }
    static void addTraceSettings(PartRunner runner, Tracing config, String...parts) {
        if (config.disabled()) return;
        if (config.value().isEmpty() || config.classes().length > 0)
            runner.enableLogging(config.level(), config.maxLevel(), config.classes(), parts);
        if (config.value().length() > 0)
            runner.enableLogging(config.level(), config.maxLevel(), config.value(), parts);
    }
}
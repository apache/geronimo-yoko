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
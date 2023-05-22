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
package testify.annotation.impl;

import org.junit.platform.commons.support.AnnotationSupport;
import testify.annotation.Tracing;
import testify.parts.PartRunner;

import java.lang.reflect.AnnotatedElement;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public enum TracingSteward {
    ;
    public static void addTraceSettings(PartRunner runner, AnnotatedElement elem) {
        AnnotationSupport.findAnnotation(elem, Tracing.class).ifPresent(trc -> TracingSteward.addTraceSettings(runner, trc));
    }
    private static void addTraceSettings(PartRunner runner, Tracing config) {
        if (config.value().isEmpty()) return;
        runner.enableLogging(config.level(), config.value());
    }
}

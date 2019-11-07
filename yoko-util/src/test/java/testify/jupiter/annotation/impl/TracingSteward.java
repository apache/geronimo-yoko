/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.jupiter.annotation.impl;

import testify.jupiter.annotation.Tracing;
import testify.parts.PartRunner;

import java.lang.reflect.AnnotatedElement;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public enum TracingSteward {
    ;
    public static void addTraceSettings(PartRunner runner, AnnotatedElement elem) {
        findAnnotation(elem, Tracing.class).ifPresent(trc -> TracingSteward.addTraceSettings(runner, trc));
    }
    private static void addTraceSettings(PartRunner runner, Tracing config, String... parts) {
        if (config.value().isEmpty()) return;
        runner.enableLogging(config.level(), config.value(), parts);
    }
}

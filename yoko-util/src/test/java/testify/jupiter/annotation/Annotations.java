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
package testify.jupiter.annotation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;

public enum Annotations {
    ;
    private static <A extends Annotation> List<A> findOneOrManyAnnotations(final AnnotatedElement e, final Class<?> testClass, final Class<A> annotationType) {
        List<A> result = listAnnotations(e, annotationType);
        if (result.isEmpty() && e != testClass) result = listAnnotations(testClass, annotationType);
        return result.isEmpty() ? null : result;
    }

    private static <A extends Annotation> List<A> listAnnotations(AnnotatedElement e, Class<A> annotationType) {
        return findAnnotation(e, annotationType)
                .map(Collections::singletonList)
                .orElse(findRepeatableAnnotations(e, annotationType));
    }

    public static <A extends Annotation, T extends CloseableResource>
    Optional<T> getRepeatableAnnotationHandler(
            ExtensionContext ctx,
            Class<A> annotationType,
            Class<T> handlerType,
            Function<List<A>, T> factory) {
        final ExtensionContext.Store store = getTestContextStore(ctx, handlerType, "repeatable annotation");
        final Class<?> testClass = ctx.getRequiredTestClass();
        return ctx.getElement()
                .map(e -> findOneOrManyAnnotations(e, testClass, annotationType))
                .map(annos -> store.getOrComputeIfAbsent(testClass, k -> factory.apply(annos), handlerType));
    }

    private static <A extends Annotation> A findSingleAnnotation(final AnnotatedElement e, final Class<?> testClass, final Class<A> annotationType) {
        A result = findAnnotation(e, annotationType).orElse(null);
        if (null == result && e != testClass) result = findAnnotation(testClass, annotationType).orElse(null);
        return result;
    }

    public static <A extends Annotation, T extends CloseableResource>
    Optional<T> getSingleAnnotationHandler(
            ExtensionContext ctx,
            Class<A> annotationType,
            Class<T> handlerType,
            Function<A, T> factory) {
        final ExtensionContext.Store store = getTestContextStore(ctx, handlerType, "single annotation");
        final Class<?> testClass = ctx.getRequiredTestClass();
        return ctx.getElement()
                .map(e -> findSingleAnnotation(e, testClass, annotationType)) // find the annotation on the annotated element
                .map(annotation -> store.getOrComputeIfAbsent(testClass, k -> factory.apply(annotation), handlerType));
    }

    /** get a test-context-specific store to cache the handler for the lifetime of the context */
    private static ExtensionContext.Store getTestContextStore(ExtensionContext ctx, Object...nameSpaceParts) {
        return ctx.getStore(ExtensionContext.Namespace.create(nameSpaceParts));
    }
}

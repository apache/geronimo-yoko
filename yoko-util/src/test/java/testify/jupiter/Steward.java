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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.function.Function;

class Steward<A extends Annotation> implements CloseableResource {
    private final Class<A> annotationClass;
    private final String missingAnnotationSuffix;

    protected Steward(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
        this.missingAnnotationSuffix =  String.format(" does not use the @%s annotation", annotationClass.getSimpleName());
    }

    protected <B extends Annotation> Steward(Class<A> annotationClass, Class<B> contentClass) {
        this.annotationClass = annotationClass;
        validateRepeatableRelationship(annotationClass, contentClass);
        this.missingAnnotationSuffix = String.format(" does not use multiple @%s annotations", contentClass.getSimpleName());
    }

    private static <A extends Annotation, B extends Annotation> void validateRepeatableRelationship(Class<A> annotationClass, Class<B> contentClass) {
        Repeatable repeatable = contentClass.getAnnotation(Repeatable.class);
        if (repeatable != null && repeatable.value() == annotationClass) return;
        throw new Error(contentClass + " does not declare @Repeatable(" + annotationClass.getSimpleName() + ")");
    }

    static <S extends Steward> S getInstanceForContext(ExtensionContext ctx, Class<S> type, Function<Class<?>, S> constructor) {
        return ctx.getStore(Namespace.create(type)).getOrComputeIfAbsent(ctx.getRequiredTestClass(), constructor, type);
    }

    private final Optional<A> findAnnotation(AnnotatedElement elem){
        return AnnotationSupport.findAnnotation(elem, annotationClass);
    }

    final A getAnnotation(AnnotatedElement elem) {
        return findAnnotation(elem).orElseThrow(() -> new Error(elem + missingAnnotationSuffix));
    }

    /**
     * Child classes that have any clean up work to do should override this method.
     */
    @Override
    public void close() { /* do nothing */ }
}

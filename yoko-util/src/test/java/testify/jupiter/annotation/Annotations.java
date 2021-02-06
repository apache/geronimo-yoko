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
import static testify.util.Predicates.not;

public enum Annotations {
    ;
    public static <A extends Annotation> List<A> findOneOrManyAnnotations(AnnotatedElement e, Class<A> annotationType) {
        return findAnnotation(e, annotationType)
                .map(Collections::singletonList)
                .orElse(findRepeatableAnnotations(e, annotationType));
    }

    public static <A extends Annotation, T extends CloseableResource>
    Optional<T> getAnnotationHandler(
            ExtensionContext ctx,
            Class<A> annotationType,
            Class<T> handlerType,
            Function<List<A>, T> factory) {
        final ExtensionContext.Store store = ctx.getStore(ExtensionContext.Namespace.create(handlerType));
        final Object key = ctx.getRequiredTestClass();
        return ctx.getElement()
                .map(e -> findOneOrManyAnnotations(e, annotationType))
                .filter(not(List::isEmpty))
                .map(annos -> store.getOrComputeIfAbsent(key, k -> factory.apply(annos), handlerType));
    }
}

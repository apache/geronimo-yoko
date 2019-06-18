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
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.function.Function;

interface Steward<A extends Annotation> extends CloseableResource {
    Class<A> annoType();

    static <T extends Steward> T getInstance(ExtensionContext ctx, Class<T> type, Function<Class<?>, T> constructor) {
        return ctx.getStore(Namespace.create(type)).getOrComputeIfAbsent(ctx.getRequiredTestClass(), constructor, type);
    }

    default Optional<A> findAnnotation(AnnotatedElement elem){
        return AnnotationSupport.findAnnotation(elem, annoType());
    }

    default A getAnnotation(AnnotatedElement elem) {
        return findAnnotation(elem).orElseThrow(() -> new IllegalStateException(String.format("%s needs to use the @%s annotation", elem, annoType().getSimpleName())));
    }

    @Override
    default void close() throws Throwable { /* do nothing */ }
}
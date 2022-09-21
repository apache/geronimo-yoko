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
package testify.jupiter.annotation.impl;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ExtensionContext.Store.CloseableResource;
import testify.jupiter.annotation.Summoner;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

/**
 * This class provides the mechanism to create, store, and retrieve
 * an instance of an object for managing some orthogonal aspect of a
 * test or test case.
 *
 * Since this is to be used with an annotation, the annotated member
 * will not provide a location for the annotation to store its artifacts.
 * Instead, the Jupiter extension context store for a type-specific namespace
 * is retrieved, and the test class is used as the key.
 *
 * So, for a given extension context, artifact type, and annotated test class,
 * there will exist at most one such artifact. This artifact will be retrieved
 * and used by the implementing child class to allow state to be propagated from
 * one method invocation to another during the handling of the specified annotation
 * <p>
 * <em>Use of this as a base class renders child logic difficult to follow.
 * This base class is deprecated in favour of the {@link Summoner} interface,
 * which supports both single and repeatable annotations.</em>
 */
@Deprecated
public class Steward<A extends Annotation> implements CloseableResource {
    protected final A annotation;

    protected Steward(Class<A> annotationClass, AnnotatedElement elem) {
        this.annotation = AnnotationButler.forClass(annotationClass).recruit().getAnnotation(elem);
    }

    protected static <S extends Steward<?>> S getInstanceForContext(ExtensionContext ctx, Class<S> type, Function<Class<?>, S> constructor) {
        final Store store = ctx.getStore(Namespace.create(type));
        final Class<?> testClass = ctx.getRequiredTestClass();
        return store.getOrComputeIfAbsent(testClass, constructor, type);
    }

    /** Child classes that have any clean up work to do should override this method. */
    @Override
    public void close() { /* do nothing */ }
}

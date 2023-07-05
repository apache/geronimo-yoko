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

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

/**
 * Summon the steward for an annotation or list of annotations.
 * @param <A> the annotation type
 *           (Note: the restrictions are introduced by the static factory methods on the interface)
 * @param <S> the steward type
 */
public interface Summoner<A extends Annotation, S> {
    @FunctionalInterface
    interface ContextualFactory<A,S> extends BiFunction<A,ExtensionContext,S> {
        default Function<A,S> curry(ExtensionContext c) { return a -> apply(a, c); }
    }
    @FunctionalInterface
    interface SimpleFactory<A, S> extends Function<A,S> {
        default ContextualFactory<A,S> ignoringContext() { return (a, c) -> apply(a); }
    }
    interface StewardSummoner<A, S> {
        /** Retrieve or create the steward for a given annotation or list of annotations */
        S requireSteward(A a);
        /** Retrieve the steward for a given annotation or list of annotations */
        S summonSteward(A a);
        /** Retrieve or create the steward for the known ExtensionContext if annotations are available */
        Optional<S> requestSteward();
        /** Re-summon the steward for the known ExtensionContext if annotations are available */
        Optional<S> recallSteward();
    }

    StewardSummoner<A, S> forContext(ExtensionContext context);

    static <A extends Annotation, S> Summoner<A, S> forAnnotation(Class<A> annotationType, Class<S> stewardType, SimpleFactory<A, S> factory) {
        return new SummonerImpl<>(annotationType, stewardType, factory, null, SummonerImpl::lookupAnnotation);
    }

    static <A extends Annotation, S> Summoner<A, S> forAnnotation(Class<A> annotationType, Class<S> stewardType, ContextualFactory<A, S> factory) {
        return new SummonerImpl<>(annotationType, stewardType, factory, null, SummonerImpl::lookupAnnotation);
    }
}

/**
 * This class implements both Summoner and StewardSummoner. The separate interfaces only exist to
 * restrict the order of invocation. The {@link Summoner#forContext(ExtensionContext)} method must be called
 * before the {@link StewardSummoner#requestSteward()} or {@link StewardSummoner#requireSteward(Object)} methods.
 *
 * @param <A> the annotation type
 * @param <S> the steward type
 */
class SummonerImpl<A extends Annotation, S> implements Summoner<A, S>, Summoner.StewardSummoner<A, S> {
    @FunctionalInterface
    interface AnnotationFinder<A extends Annotation,S> extends Function<SummonerImpl<A,S>, Optional<A>> {}
    private final Class<A> annotationType;
    private final Class<S> stewardType;
    private final ContextualFactory<A, S> factory;
    private final AnnotationFinder<A,S> annotationFinder;
    private final ExtensionContext context;
    private final Class<?> testClass;
    private final ExtensionContext.Store store;

    SummonerImpl(Class<A> annotationType, Class<S> stewardType, ContextualFactory<A,S> factory, ExtensionContext context, AnnotationFinder<A,S> annotationFinder) {
        this.annotationType = annotationType;
        this.stewardType = stewardType;
        this.factory = factory;
        this.annotationFinder = annotationFinder;
        this.context = context;
        this.testClass = context == null ? null : context.getRequiredTestClass();
        this.store = context == null ? null : context.getStore(ExtensionContext.Namespace.create(stewardType, "test annotation steward namespace"));
    }

    SummonerImpl(Class<A> annotationType, Class<S> stewardType, SimpleFactory<A, S> factory, ExtensionContext context, AnnotationFinder<A,S> annotationFinder) {
        this(annotationType, stewardType, factory.ignoringContext(), context, annotationFinder);
    }

    public StewardSummoner<A, S> forContext(ExtensionContext context) { return new SummonerImpl<>(annotationType, stewardType, factory, context, annotationFinder); }

    public final S requireSteward(A annotation) { return store.getOrComputeIfAbsent(annotation, factory.curry(context), stewardType); }
    public final S summonSteward(A annotation) { return store.get(annotation, stewardType); }
    public final Optional<S> requestSteward() { return annotationFinder.apply(this).map(this::requireSteward); }
    public final Optional<S> recallSteward() { return annotationFinder.apply(this).map(this::summonSteward); }

    Optional<A> lookupAnnotation() {
        final Optional<AnnotatedElement> element = context.getElement();
        Optional<A> result = element.flatMap(e -> findAnnotation(e, annotationType));
        if (result.isPresent() || element.filter(testClass::equals).isPresent()) return result;
        return findAnnotation(testClass, annotationType);
    }
}

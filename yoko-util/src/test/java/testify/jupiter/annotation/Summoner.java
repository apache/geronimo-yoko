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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;

/**
 * Summon the steward for an annotation or list of annotations.
 * @param <P> either the annotation type or a List of the annotation type
 *           (Note: the restrictions are introduced by the static factory methods on the interface)
 * @param <S> the steward type
 */
public interface Summoner<P, S> {
    @FunctionalInterface
    interface ContextualFactory<P,S> extends BiFunction<P,ExtensionContext,S> {
        default Function<P,S> curry(ExtensionContext c) { return p -> apply(p, c); }
    }
    @FunctionalInterface
    interface SimpleFactory<P, S> extends Function<P,S> {
        default ContextualFactory<P,S> ignoringContext() { return (p, c) -> apply(p); }
    }
    interface StewardSummoner<P, S> {
        /** Retrieve or create the steward for a given annotation or list of annotations */
        S requireSteward(P p);
        /** Retrieve the steward for a given annotation or list of annotations */
        S summonSteward(P p);
        /** Retrieve or create the steward for the known ExtensionContext if annotations are available */
        Optional<S> requestSteward();
        /** Re-summon the steward for the known ExtensionContext if annotations are available */
        Optional<S> recallSteward();
    }

    StewardSummoner<P, S> forContext(ExtensionContext context);

    static <A extends Annotation, S> Summoner<A, S> forAnnotation(Class<A> annotationType, Class<S> stewardType, SimpleFactory<A, S> factory) {
        return new SummonerImpl<>(annotationType, stewardType, factory, null, SummonerImpl::lookupAnnotation);
    }

    static <A extends Annotation, S> Summoner<A, S> forAnnotation(Class<A> annotationType, Class<S> stewardType, ContextualFactory<A, S> factory) {
        return new SummonerImpl<>(annotationType, stewardType, factory, null, SummonerImpl::lookupAnnotation);
    }

    static <A extends Annotation, S> Summoner<List<A>, S> forRepeatableAnnotation(Class<A> annotationType, Class<S> stewardType, SimpleFactory<List<A>, S> factory) {
        return new SummonerImpl<>(annotationType, stewardType, factory, null, SummonerImpl::lookupAnnotations);
    }
}

/**
 * This class implements both Summoner and StewardSummoner. The separate interfaces only exist to
 * restrict the order of invocation. The {@link Summoner#forContext(ExtensionContext)} method must be called
 * before the {@link StewardSummoner#requestSteward()} or {@link StewardSummoner#requireSteward(Object)} methods.
 *
 * @param <A> the annotation type
 * @param <P> the parameter type (either A or List of A)
 * @param <S> the steward type
 */
class SummonerImpl<A extends Annotation, P, S> implements Summoner<P, S>, Summoner.StewardSummoner<P, S> {
    @FunctionalInterface
    interface AnnotationFinder<A extends Annotation,P,S> extends Function<SummonerImpl<A,P,S>, Optional<P>> {}
    private final Class<A> annotationType;
    private final Class<S> stewardType;
    private final ContextualFactory<P, S> factory;
    private final AnnotationFinder<A,P,S> annotationFinder;
    private final ExtensionContext context;
    private final Class<?> testClass;
    private final ExtensionContext.Store store;

    SummonerImpl(Class<A> annotationType, Class<S> stewardType, ContextualFactory<P,S> factory, ExtensionContext context, AnnotationFinder<A,P,S> annotationFinder) {
        this.annotationType = annotationType;
        this.stewardType = stewardType;
        this.factory = factory;
        this.annotationFinder = annotationFinder;
        this.context = context;
        this.testClass = context == null ? null : context.getRequiredTestClass();
        this.store = context == null ? null : context.getStore(ExtensionContext.Namespace.create(stewardType, "test annotation steward namespace"));
    }

    SummonerImpl(Class<A> annotationType, Class<S> stewardType, SimpleFactory<P, S> factory, ExtensionContext context, AnnotationFinder<A,P,S> annotationFinder) {
        this(annotationType, stewardType, factory.ignoringContext(), context, annotationFinder);
    }

    public SummonerImpl<A, P, S> forContext(ExtensionContext context) { return new SummonerImpl<>(annotationType, stewardType, factory, context, annotationFinder); }

    public final S requireSteward(P parameter) { return store.getOrComputeIfAbsent(parameter, factory.curry(context), stewardType); }
    public final S summonSteward(P parameter) { return store.get(parameter, stewardType); }
    public final Optional<S> requestSteward() { return annotationFinder.apply(this).map(this::requireSteward); }
    public final Optional<S> recallSteward() { return annotationFinder.apply(this).map(this::summonSteward); }

    Optional<A> lookupAnnotation() {
        final Optional<AnnotatedElement> element = context.getElement();
        Optional<A> result = element.flatMap(e -> findAnnotation(e, annotationType));
        if (result.isPresent() || element.filter(testClass::equals).isPresent()) return result;
        return findAnnotation(testClass, annotationType);
    }

    Optional<List<A>> lookupAnnotations() {
        final Optional<AnnotatedElement> element = context.getElement();
        Optional<List<A>> result = element.map(e -> findRepeatableAnnotations(e, annotationType)).filter(l -> l.size() > 0);
        if (result.isPresent() || element.filter(testClass::equals).isPresent()) return result;
        return Optional.of(findRepeatableAnnotations(testClass, annotationType)).filter(l -> l.size() > 0);
    }
}

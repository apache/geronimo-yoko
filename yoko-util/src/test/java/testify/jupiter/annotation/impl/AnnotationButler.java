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

import org.hamcrest.Matcher;
import org.junit.platform.commons.support.HierarchyTraversalMode;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedFields;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedMethods;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.ModifierSupport.isFinal;
import static org.junit.platform.commons.support.ModifierSupport.isPublic;
import static org.junit.platform.commons.support.ModifierSupport.isStatic;

public class AnnotationButler<A extends Annotation> implements Serializable {
    public static <A extends Annotation> Spec<A> forClass(Class<A> annoType) {
        return new Spec<>(annoType);
    }

    public static class Spec<A extends Annotation> {
        private final Class<A> annoType;
        private final String annoName;
        private Predicate<A> filters = a -> true;
        private Consumer<Member> assertions = m -> {};
        private Spec(Class<A> annoType) {
            this.annoType = annoType;
            this.annoName =  "@" + annoType.getSimpleName();
        }
        public Spec<A> assertPublic() {
            assertions = assertions.andThen(member -> assertTrue(isPublic(member), () -> ""
                    + "The " + annoName + " annotation must be used on only public members."
                    + " It has been used on the non-public member: " + member));
            return this;
        }
        public Spec<A> assertStatic() {
            assertions = assertions.andThen(member -> assertTrue(isStatic(member), () -> ""
                    + "The " + annoName + " annotation must be used on only static members."
                    + " It has been used on the non-static member: " + member));
            return this;
        }
        public Spec<A> assertFinal() {
            assertions = assertions.andThen(member -> assertTrue(isFinal(member), () -> ""
                    + "The " + annoName + " annotation must be used on only final members."
                    + " It has been used on the non-final member: " + member));
            return this;
        }
        public <TA extends Annotation> Spec<A> requireTestAnnotation(Class<TA> testAnnoType) {
            assertions = assertions.andThen(
                    member -> assertTrue(findAnnotation(member.getDeclaringClass(), testAnnoType)
                                    .isPresent(),
                            () -> "The " + annoName
                                    + " annotation may only be used within tests that have the @"
                                    + testAnnoType.getSimpleName() + " annotation."
                                    + " It has been used on the invalid member: " + member));
            return this;
        }
        public <TA extends Annotation, X> Spec<A> requireTestAnnotation(Class<TA> testAnnoType, String description, Function<TA, X> mapper, Matcher<X> matcher) {
            // TODO: consider using the actual known test class instead of member.getDeclaringClass()
            requireTestAnnotation(testAnnoType);
            assertions = assertions.andThen(
                    member -> assertThat(
                            "The " + annoName
                                    + " annotation on member " + member + " has a matching @"
                                    + testAnnoType.getSimpleName() + " annotation on its declaring class but "
                                    + description,
                            findAnnotation(member.getDeclaringClass(), testAnnoType)
                                    .map(mapper)
                                    .orElse(null),
                            matcher));
            return this;
        }
        public Spec<A> assertParameterTypes(Set<Class<?>> types) {
            assertions = assertions.andThen(member -> {
                if (!!!(member instanceof Method)) return;
                Method method = (Method)member;
                for (Class<?> paramType: method.getParameterTypes()) {
                    if (types.contains(paramType)) continue;
                    fail(annoName + " does not support parameters of type " + paramType.getSimpleName() + " on method " + method);
                }
            });
            return this;
        }
        public Spec<A> assertFieldTypes(Class<?>... allowedTypes) {
            assertions = assertions.andThen(member -> {
                if (!!!(member instanceof Field)) return;
                Field field = (Field)member;
                Class<?> fieldType = field.getType();
                if (Stream.of(allowedTypes).anyMatch(allowed -> allowed.isAssignableFrom(fieldType))) return;
                fail(annoName + " does not support the declared type " + fieldType.getSimpleName() + " of field " + field);
            });
            return this;
        }

        public Spec<A> filter(Predicate<A> filter) { filters = filters.and(filter); return this; }
        public AnnotationButler<A> recruit() { return new AnnotationButler<>(annoType, filters, assertions); }
    }

    private final Class<A> annoType;
    private final Consumer<Member> assertions;
    private final Predicate<A> filters;

    private AnnotationButler(Class<A> annoType, Predicate<A> filters, Consumer<Member> assertions) {
        this.annoType = annoType;
        this.assertions = assertions;
        this.filters = filters;
    }

    public List<Method> findMethods(Class<?> clazz) {
        return findAnnotatedMethods(clazz, annoType, HierarchyTraversalMode.TOP_DOWN)
                .stream()
                .filter(this::filter)
                .peek(this.assertions)
                .collect(toList());
    }

    public List<Field> findFields(Class<?> clazz) {
        return findAnnotatedFields(clazz, annoType)
                .stream()
                .filter(this::filter)
                .peek(this.assertions)
                .collect(toList());
    }

    public <V> Map<Field, V> findFieldsAsMap(Class<?> clazz) {
        return findAnnotatedFields(clazz, annoType)
                .stream()
                .sequential()
                .filter(this::filter)
                .peek(this.assertions)
                .collect(HashMap::new, (m, f) -> m.put(f, null), HashMap::putAll);
    }

    private boolean filter(AnnotatedElement elem) {
        return findAnnotation(elem, annoType)
                .map(filters::test)
                .orElseThrow(Error::new); // the annotation MUST be present
    }

    protected final A getAnnotation(AnnotatedElement elem) {
        return findAnnotation(elem, annoType)
                .orElseThrow(() -> new NoSuchElementException(elem + String.format(" does not use the @%s annotation", annoType.getSimpleName())));
    }
}

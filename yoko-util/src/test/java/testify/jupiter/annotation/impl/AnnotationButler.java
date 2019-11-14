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

import org.junit.platform.commons.support.HierarchyTraversalMode;
import org.opentest4j.AssertionFailedError;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedFields;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedMethods;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;
import static org.junit.platform.commons.support.ModifierSupport.isPublic;
import static org.junit.platform.commons.support.ModifierSupport.isStatic;
import static testify.util.Reflect.getMatchingConstructor;
import static testify.util.Reflect.getMatchingTypeName;

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
        private void assertStatic(Member member) {
            assertTrue(isStatic(member), () -> ""
                    + "The " + annoName + " annotation must be used on only static members."
                    + " It has been used on the non-static member: " + member);
        }
        public void assertPublic(Member member) {
            assertTrue(isPublic(member), () -> ""
                    + "The " + annoName + " annotation must be used on only public members."
                    + " It has been used on the non-public member: " + member);
        }
        public Spec<A> assertParameterTypes(Class<?>... paramTypes) {
            Set<Class<?>> allowedParamTypes = new HashSet<>(asList(paramTypes));
            assertions = assertions.andThen(member -> {
                if (!!!(member instanceof Method)) return;
                Method method = (Method)member;
                for (Class<?> paramType: method.getParameterTypes()) {
                    if (allowedParamTypes.contains(paramType)) continue;
                    fail(annoName + " does not support parameters of type " + paramType.getSimpleName() + " on method " + method);
                }
            });
            return this;
        }
        public Spec<A> assertFieldTypes(Class<?>... fieldTypes) {
            Set<Class<?>> allowedFieldTypes = new HashSet<>(asList(fieldTypes));
            assertions = assertions.andThen(member -> {
                if (!!!(member instanceof Field)) return;
                Field field = (Field)member;
                Class<?> fieldType = field.getType();
                if (allowedFieldTypes.contains(fieldType)) return;
                for (Class<?> c = fieldType; c != null; c = c.getSuperclass()) {
                    if (allowedFieldTypes.contains(c)) return;
                    for (Class<?> iface: c.getInterfaces())
                        if (allowedFieldTypes.contains(iface)) return;
                }
                fail(annoName + " does not support the declared type " + fieldType.getSimpleName() + " of field " + field);
            });
            return this;
        }

        public Spec<A> assertFieldHasMatchingConcreteType(String pattern, Class<?>... allowedConstructorParameterTypes) {
            assertions = assertions.andThen(member -> {
                if (!!!(member instanceof Field)) return;
                Field field = (Field)member;
                String expectedTypeName = getMatchingTypeName(field.getType(), pattern);
                try {
                    getMatchingConstructor(expectedTypeName, allowedConstructorParameterTypes);
                } catch (AssertionFailedError assertionFailedError) {
                    throw (Error) fail("Could not process annotation " + annoName + " on field " + field, assertionFailedError);
                }
            });
            return this;
        }

        public Spec<A> filter(Predicate<A> filter) { filters = filters.and(filter); return this; }
        public AnnotationButler<A> recruit() { return new AnnotationButler(annoType, filters, assertions); }
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


    private boolean filter(AnnotatedElement elem) {
        return findAnnotation(elem, annoType)
                .map(filters::test)
                .orElseThrow(Error::new); // the annotation MUST be present
    }

    public boolean hasAnnotatedMethods(Class<?> clazz) {
        return !!! findMethods(clazz).isEmpty();
    }

    protected final A getAnnotation(AnnotatedElement elem) {
        return findAnnotation(elem, annoType)
                .orElseThrow(() -> new Error(elem + String.format(" does not use the @%s annotation", annoType.getSimpleName())));
    }
}

/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package testify.jupiter.params.provider;

import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.AnnotationConsumer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "5.0")
@ArgumentsSource(ClassSource.Provider.class)
public @interface ClassSource {
    Class[] value();
    class Provider implements ArgumentsProvider, AnnotationConsumer<ClassSource> {
        private static final String ANNO_NAME = '@' + ClassSource.class.getSimpleName();
        Class<?>[] classes;
        @Override
        public void accept(ClassSource classSource) {
            this.classes = classSource.value();
        }

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            final Method method = context.getRequiredTestMethod();
            final Class<?>[] parameterTypes = method.getParameterTypes();
            // Check parameters can all be classes
            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type.isAssignableFrom(Class.class)) continue;
                String msg = String.format("The %s annotation cannot be used on method %s because parameter %d is of type %s and cannot accept a value of type class",
                        ANNO_NAME, method.getName(), i+1, type.getSimpleName());
                throw new IllegalArgumentException(msg);
            }
            int paramCount = parameterTypes.length;
            if (classes.length == 0) throw new IllegalArgumentException("The " + ANNO_NAME + " annotation on method " + method.getName() + " does not provide any values.");
            if (paramCount == 0) throw new IllegalArgumentException("Method " + method.getName() + " uses annotation " + ANNO_NAME + " but does not have any parameters.");
            if (classes.length % paramCount > 0) {
                String msg = String.format("The %s annotation cannot be used on method %s. " +
                                "The method has %d parameter(s) so the annotation needs to provide %d more value(s).",
                        ANNO_NAME, method.getName(), paramCount, paramCount - classes.length % paramCount);
                throw new IllegalArgumentException(msg);
            }
            final Builder<Class<?>[]> builder = Stream.builder();
            for (int i = 0; i < classes.length; i += paramCount) {
                builder.add(Arrays.copyOfRange(classes, i, i + paramCount));
            }
            return builder.build().map(classes -> Arguments.of(classes));
        }
    }
}

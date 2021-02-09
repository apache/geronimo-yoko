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

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.Preconditions;
import testify.streams.Streams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

/**
 * This test can be run multiple times, until at least one failure occurs.
 * In other words: if at first you do succeed, try until you fail.
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(RetriedTest.ContextProvider.class)
@TestTemplate
public @interface RetriedTest {
    int maxRuns();
    int maxFailures() default 1;
    int maxSuccesses() default Integer.MAX_VALUE;

    class ContextProvider implements TestTemplateInvocationContextProvider {
        @Override
        public boolean supportsTestTemplate(ExtensionContext context) {
            return AnnotationSupport.isAnnotated(context.getTestMethod(), RetriedTest.class);
        }

        @Override
        public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
            return AnnotationSupport
                    .findAnnotation(context.getTestMethod(), RetriedTest.class)
                    .map(Context::stream)
                    .orElse(Stream.empty());
        }
    }

    /**
     * This class holds the mutable state for the collection of test runs.
     * It implements Spliterator purely to make it easy to treat as a stream.
     */
    class Context implements TestTemplateInvocationContext, AfterTestExecutionCallback {
        private int successes;
        private int failures;

        static Stream<TestTemplateInvocationContext> stream(RetriedTest annotation) {
            Preconditions.condition(annotation.maxRuns() > 0, "The maximum number of runs must be greater than zero." );
            Preconditions.condition(annotation.maxFailures() > 0, "The maximum allowed failures must be greater than zero." );
            Preconditions.condition(annotation.maxSuccesses() > 0, "The maximum allowed successes must be greater than zero." );
            Context ctx = new Context();
            return Streams.stream(action -> {
                if (annotation.maxFailures() == ctx.failures) return false;
                if (annotation.maxSuccesses() == ctx.successes) return false;
                if (annotation.maxRuns() == ctx.failures + ctx.successes) return false;
                action.accept(ctx);
                return true;
            });
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return singletonList(this);
        }

        @Override
        public void afterTestExecution(ExtensionContext context) throws Exception {
            if (context.getExecutionException().isPresent()) failures++;
            else successes++;
        }
    }
}






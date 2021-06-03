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
package testify.expect;

import org.junit.jupiter.api.function.Executable;

import java.util.function.UnaryOperator;

import static testify.expect.ExceptionExpectation.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Expect an exception with an optional causal chain when executing an action.
 */
@FunctionalInterface
public interface ExceptionExpectation {
    /**
     * @return an assertion on a {@link Throwable} that returns the throwable (or some evaluation of it) for subsequent assertions
     */
    UnaryOperator<Throwable> assertion();

    /**
     * This terminal operation calls the supplied action and tests for the expected exceptions.
     * @param action
     */
    default void when(Executable action) {
        try {
            action.execute();
            assertion().apply(null);
        } catch (Throwable originalException) {
            assertion().apply(originalException);
        }
    }

    /**
     * Expect a causal exception.
     * @param expectedExceptionType the exact type of exception to expect
     */
    default ExceptionExpectation causedBy(Class<? extends Throwable> expectedExceptionType) {
        return () -> originalException -> {
            Throwable t = assertion().apply(originalException).getCause();
            assertThat(t, instanceOf(expectedExceptionType));
            assertThat(t.getClass(), equalTo(expectedExceptionType));
            return t;
        };
    }

    /**
     * Expect no further causal chain beyond that already specified.
     */
    default ExceptionExpectation noCause() {
        return () -> originalException -> {
            Throwable t = assertion().apply(originalException);
            assertThat(t.getCause(), nullValue());
            return t;
        };
    }

    /**
     * Expect one further, final cause in the chain.
     * @param expectedExceptionType the exact type of exception to expect
     */
    default ExceptionExpectation rootCause(Class<? extends Throwable> expectedExceptionType) {
        return causedBy(expectedExceptionType).noCause();
    }

    /**
     * Expect an initial exception to be thrown.
     * @param expectedExceptionType the exact type of exception to expect
     */
    static ExceptionExpectation expect(Class<? extends Throwable> expectedExceptionType) {
        return () -> t -> {
            assertThat(t, instanceOf(expectedExceptionType)); // weaker condition that will filter out nulls and incompatible exceptions
            assertThat(t.getClass(), equalTo(expectedExceptionType)); // stricter condition that will catch child exceptions
            return t;
        };
    }

    /**
     * Expect an initial exception to be thrown, without any causal chain.
     * @param expectedExceptionType the exact type of exception to expect
     */
    static ExceptionExpectation expectOnly(Class<? extends Throwable> expectedExceptionType) {
        return expect(expectedExceptionType).noCause();
    }
}

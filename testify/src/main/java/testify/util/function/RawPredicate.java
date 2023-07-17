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
package testify.util.function;

import org.opentest4j.AssertionFailedError;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Predicate;

/** Overrides {@link Predicate} to allow exceptions */
@FunctionalInterface
public interface RawPredicate<T> extends Predicate<T>, Serializable {
    boolean testRaw(T t) throws Exception;

    @Override
    default boolean test(T t) {
        try {
            return testRaw(t);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionFailedError("", e);
        }

    }

    default RawPredicate<T> negate() { return t -> !this.testRaw(t); }

    default RawPredicate<T> and(RawPredicate<? super T> that) {
        return t -> this.testRaw(t) && that.testRaw(t);
    }

    default RawPredicate<T> or(RawPredicate<? super T> that) {
        return t -> this.testRaw(t) || that.testRaw(t);
    }

    static <T> RawPredicate<T> isEqual(Object targetRef) {
        return t -> Objects.equals(targetRef, t);
    }
}

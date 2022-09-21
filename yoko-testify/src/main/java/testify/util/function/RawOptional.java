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
package testify.util.function;

import org.opentest4j.AssertionFailedError;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This is an alternative to the {@link java.util.Optional} monad that allows exceptions.
 * Methods are supplied to match the methods on {@link java.util.Optional}.
 * <p>
 *     Methods are provided to match the methods on {@link java.util.Optional}.
 *     If a non-terminal method throws a checked exception, this is stored and the {@link RawOptional}
 *     is treated as empty. When a terminal method is invoked
 *     &mdash; i.e. a method that returns T or void &mdash;
 *     any stored exception is wrapped in an Error and re-thrown,
 *     (or added as suppressed to a requested exception).
 * </p>
 * <p>
 *     Additional "raw" methods are provided that declare and throw checked exceptions
 *     so that the original exception may be propagated directly if so desired.
 * </p>
 */
public final class RawOptional<T> {
    private static final RawOptional<?> EMPTY = new RawOptional<>();

    public static <T> RawOptional<T> empty() {
        return (RawOptional<T>) EMPTY;
    }

    public static <T> RawOptional<T> of(T elem) {
        return new RawOptional<>(Objects.requireNonNull(elem));
    }

    public static <T> RawOptional<T> ofNullable(T elem) {
        return elem == null ? empty() : of(elem);
    }

    public static <T> RawOptional<T> from(RawSupplier<T> supplier) { return from0(supplier, RawOptional::of); }

    public static <T> RawOptional<T> fromNullable(RawSupplier<T> supplier) { return from0(supplier, RawOptional::ofNullable); }

    private static <T> RawOptional<T> from0(RawSupplier<T> supplier, RawFunction<T, RawOptional<T>> fun) {
        try {
            return fun.apply(supplier.getRaw());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new RawOptional<>(e);
        }
    }

    private final T elem;
    private final Exception exception;

    private RawOptional() {
        this.elem = null;
        this.exception = null;
    }

    private RawOptional(Exception exception) {
        this.elem = null;
        this.exception = Objects.requireNonNull(exception);
    }

    private RawOptional(T elem) {
        this.elem = Objects.requireNonNull(elem);
        this.exception = null;
    }

    public boolean isEmpty() {
        return elem == null;
    }

    public boolean isPresent() {
        return elem != null;
    }

    public boolean hasException() {
        return exception != null;
    }

    public T getRaw() throws Exception {
        if (hasException()) throw exception;
        if (isEmpty()) throw new NoSuchElementException();
        return elem;
    }

    public T get() {
        if (hasException()) throw new AssertionFailedError("", exception);
        if (isEmpty()) throw new NoSuchElementException();
        return elem;
    }

    public RawOptional<T> filter(RawPredicate<? super T> predicate) {
        try {
            return isEmpty() || predicate.testRaw(elem) ? this : empty();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new RawOptional<>(e);
        }
    }

    public <U> RawOptional<U> map(RawFunction<? super T, ? extends U> mapper) {
        try {
            return isEmpty() ? (RawOptional<U>) this : ofNullable(mapper.applyRaw(elem));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new RawOptional<>(e);
        }
    }

    public <U> RawOptional<U> flatMap(RawFunction<? super T, RawOptional<U>> mapper) {
        try {
            return isEmpty() ? (RawOptional<U>) this : ofNullable(mapper.applyRaw(elem).getRaw());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new RawOptional<>(e);
        }
    }

    public void ifPresentRaw(RawConsumer<? super T> consumer) throws Exception {
        if (hasException()) throw exception;
        if (isPresent()) consumer.accept(elem);
    }

    public void ifPresent(RawConsumer<? super T> consumer) {
        if (hasException()) throw new AssertionFailedError("", exception);
        if (isPresent()) consumer.accept(elem);
    }

    public T orElseRaw(T other) throws Exception {
        if (hasException()) throw exception;
        return isPresent() ? elem : other;
    }

    public T orElse(T other) {
        if (hasException()) throw new AssertionFailedError("", exception);
        return isPresent() ? elem : other;
    }

    public T orElseGetRaw(RawSupplier<? extends T> supplier) throws Exception {
        if (hasException()) throw exception;
        return isPresent() ? elem : supplier.getRaw();
    }

    public T orElseGet(RawSupplier<? extends T> supplier) {
        if (hasException()) throw new AssertionFailedError("", exception);
        return isPresent() ? elem : supplier.get();
    }

    public <X extends Throwable> T orElseThrowRaw(RawSupplier<? extends X> exceptionSupplier) throws Exception, X {
        if (hasException()) throw exception;
        if (isPresent()) return elem;
        throw exceptionSupplier.get();
    }

    public <X extends Throwable> T orElseThrow(RawSupplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) return elem;
        X x = exceptionSupplier.get();
        if (hasException()) x.addSuppressed(exception);
        throw x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawOptional<?> optional = (RawOptional<?>) o;
        return Objects.equals(elem, optional.elem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elem);
    }
}

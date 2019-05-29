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
package testify.bus;

import test.util.BiStream;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Enable multiple threads to communicate asynchronously.
 */
class BusImpl implements RawBus {
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();
    private final ConcurrentMap<String, Object> properties = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Queue<Consumer<String>>> callbacks = new ConcurrentHashMap<>();
    private volatile Throwable originalError = null;

    @Override
    public void put(String key, String value) {
        // store the reference
        Object previous = properties.put(key, requireNonNull(value));
        // notify any waiting threads
        if (previous instanceof CountDownLatch) ((CountDownLatch) previous).countDown();
        // kick off callbacks on (potentially) separate threads
        Optional.ofNullable(callbacks.get(key))
                .map(Queue::stream).orElse(Stream.empty())
                .forEach(action -> EXECUTOR.execute(() -> action.accept(value)));
    }

    @Override
    public String get(String key) {
        try {
            Object obj = properties.computeIfAbsent(key, s -> new CountDownLatch(1));
            // if it wasn't there, block until it arrives
            if (obj instanceof CountDownLatch) ((CountDownLatch) obj).await(10, SECONDS);
            // rethrow any stored error
            reThrowErrorIfPresent();
            // it's there now, so return it
            final String value = (String) properties.get(key);
            return value;
        } catch (InterruptedException e) {
            storeError(new InterruptedException("Interrupted while waiting for key: " + key).initCause(e));
        } catch (ClassCastException e) {
            storeError(new TimeoutException("Timed out waiting for key: " + key));
        }
        throw reThrowErrorIfPresent(); // there must be an error by now
    }

    @Override
    public void onMsg(String key, Consumer<String> action) {
        // register the callback
        callbacks.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(action);
    }

    Error reThrowErrorIfPresent() {
        if (originalError == null) return null;
        throw new IllegalStateException(originalError);
    }

    void storeError(Throwable t) {
        // only do this for the first error
        if (originalError != null) return;
        // log the error
        System.err.println("RawBus error: " + t);
        System.err.println("RawBus state: " + this);
        // must set the error before waking any threads
        originalError = t;
        // wake any waiting threads — they will receive an error
        BiStream.of(properties).narrowValues(CountDownLatch.class).values().forEach(CountDownLatch::countDown);
    }

    @Override
    public void forEach(BiConsumer<String, String> action) {
        BiStream.of(properties).narrowValues(String.class).forEach(action);
    }

    private <T> T collect(Supplier<T> supplier, Function<T,BiConsumer<String, String>> accumulator) {
        T t = supplier.get();
        this.forEach(accumulator.apply(t));
        return t;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        this.forEach((k, v) -> sb.append("\n\t").append(k).append(" -> ").append(v));
        sb.append("}");
        return sb.toString();
    }
}



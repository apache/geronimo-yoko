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

import testify.io.EasyCloseable;
import testify.streams.BiStream;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Enable multiple threads to communicate asynchronously.
 */
class BusImpl implements SimpleBus, EasyCloseable {
    protected final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ConcurrentMap<String, Object> properties = new ConcurrentSkipListMap<>();
    private final ConcurrentMap<String, Queue<Consumer<String>>> callbacks = new ConcurrentHashMap<>();
    private volatile Throwable originalError = null;
    private final Map<String, Bus> userBusMap = new ConcurrentHashMap<>();

    @Override
    public Bus forUser(String user) {
        return userBusMap.computeIfAbsent(user, u -> new UserBus(this, u));
    }

    @Override
    public BusImpl put(String key, String value) {
        // store the reference
        Object previous = properties.put(key, requireNonNull(value));
        // notify any waiting threads
        if (previous instanceof CountDownLatch) ((CountDownLatch) previous).countDown();
        // kick off callbacks on (potentially) separate threads
        Optional.ofNullable(callbacks.get(key))
                .map(Queue::stream).orElse(Stream.empty())
                .forEach(action -> threadPool.execute(() -> action.accept(value)));
        return this;
    }

    @Override
    public boolean hasKey(String key) {
        return Optional.ofNullable(properties.get(key))
                .map(Object::getClass)
                .filter(String.class::equals)
                .isPresent();
    }

    @Override
    public String peek(String key) {
        try { return (String) properties.get(key); }
        catch (ClassCastException e) { return null; }
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
            return (String) properties.get(key);
        } catch (InterruptedException e) {
            storeError(new InterruptedException("Interrupted while waiting for key: " + key).initCause(e));
        } catch (ClassCastException e) {
            storeError(new TimeoutException("Timed out waiting for key: " + key));
        }
        throw reThrowErrorIfPresent(); // there must be an error by now
    }

    @Override
    public BusImpl onMsg(String key, Consumer<String> action) {
        // register the callback
        callbacks.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(action);
        return this;
    }

    Error reThrowErrorIfPresent() {
        if (originalError == null) return null;
        throw new IllegalStateException(originalError);
    }

    void storeError(Throwable t) {
        // only do this for the first error
        if (originalError != null) return;
        // log the error
        System.err.println("Bus error: " + t);
        System.err.println("Bus state: " + this);
        // must set the error before waking any threads
        originalError = t;
        // wake any waiting threads — they will receive an error
        BiStream.of(properties).narrowValues(CountDownLatch.class).values().forEach(CountDownLatch::countDown);
    }

    @Override
    public BiStream<String, String> biStream() {
        return BiStream.of(properties).narrowValues(String.class);
    }

    private <T> T collect(Supplier<T> supplier, Function<T,BiConsumer<String, String>> accumulator) {
        T t = supplier.get();
        biStream().forEach(accumulator.apply(t));
        return t;
    }

    @Override
    public void easyClose() throws Exception {
        threadPool.shutdown();
        threadPool.awaitTermination(200, MILLISECONDS);
        threadPool.shutdownNow();
        if (threadPool.isTerminated()) return;
        throw new Error("Unable to shut down thread pool: " + threadPool.shutdownNow());
    }

    @Override
    public String toString() {
        return format(this.biStream());
    }

    static String format(BiStream<String, String> bis) {
        StringBuilder sb = new StringBuilder("{");
        bis.forEach((k, v) -> sb.append("\n\t").append(k).append(" -> ").append(v));
        if (sb.length() == 1) return "{}";
        sb.append("\n}");
        return sb.toString();
    }

}



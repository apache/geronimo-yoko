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

import org.junit.jupiter.api.Assertions;
import testify.io.EasyCloseable;
import testify.streams.BiStream;
import testify.util.ObjectUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Enable multiple threads to communicate asynchronously.
 */
class SimpleBusImpl implements SimpleBus, EasyCloseable {
    private final String label = ObjectUtil.getNextObjectLabel(SimpleBusImpl.class);
    private final AtomicInteger threadCount = new AtomicInteger();
    private final ExecutorService threadPool = Executors.newCachedThreadPool(this::createThread);

    private Thread createThread(Runnable r) {
        return new Thread(r, label + ".thread#" + threadCount.incrementAndGet());
    }

    private final ConcurrentMap<String, Object> properties = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Queue<Consumer<String>>> callbacks = new ConcurrentHashMap<>();
    private volatile Throwable originalError = null;
    private final Map<String, Bus> userBusMap = new ConcurrentHashMap<>();
    private final Set<String> loggingShortcuts = new ConcurrentSkipListSet<>();

    @Override
    public Bus forUser(String user) { return userBusMap.computeIfAbsent(user, this::newBus); }

    private Bus newBus(String user) {
        return new BusImpl(newLogBus(user));
    }

    private LogBusImpl newLogBus(String user) {
        return new LogBusImpl(newEventBus(user), loggingShortcuts);
    }

    private EventBusImpl newEventBus(String user) {
        return new EventBusImpl(newUserBus(user));
    }

    private UserBusImpl newUserBus(String user) {
        return new UserBusImpl(user, this);
    }

    @Override
    public SimpleBusImpl put(String key, String value) {
        // store the reference
        Object previous = properties.put(key, requireNonNull(value));
        // notify any waiting threads
        if (previous instanceof CountDownLatch) ((CountDownLatch) previous).countDown();
        // kick off callbacks on (potentially) separate threads
        Optional.ofNullable(callbacks.get(key))
                .map(Queue::stream)
                .orElse(Stream.empty())
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
            if (obj instanceof CountDownLatch) ((CountDownLatch) obj).await(20, SECONDS);
            // rethrow any stored error
            //noinspection ThrowableNotThrown
            reThrowErrorIfPresent();
            // it's there now, so return it
            return (String) properties.get(key);
        } catch (InterruptedException e) {
            storeError(new InterruptedException("Interrupted while waiting for key: " + key).initCause(e));
        } catch (ClassCastException e) {
            storeError(new TimeoutException("Timed out waiting for key: " + key));
        }
        throw requireNonNull(reThrowErrorIfPresent()); // there must be an error by now
    }

    @Override
    public SimpleBusImpl onMsg(String key, Consumer<String> action) {
        // register the callback
        callbacks.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).add(action);
        return this;
    }

    @SuppressWarnings("SameReturnValue")
    private Error reThrowErrorIfPresent() {
        if (originalError == null) return null;
        throw new IllegalStateException(originalError);
    }

    void storeError(Throwable t) {
        // only do this for the first error
        if (originalError != null) return;
        // log the error
        System.err.println("Bus error: " + t);
        System.err.println("Bus state: " + Buses.dump(this));
        // must set the error before waking any threads
        originalError = t;
        // wake any waiting threads — they will receive an error
        BiStream.of(properties).narrowValues(CountDownLatch.class).values().forEach(CountDownLatch::countDown);
    }

    @Override
    public BiStream<String, String> biStream() {
        return BiStream.of(properties).narrowValues(String.class);
    }

    @Override
    public void easyClose() throws Exception {
        threadPool.shutdown();
        threadPool.awaitTermination(200, MILLISECONDS);
        List<?> list = threadPool.shutdownNow();
        if (threadPool.isTerminated()) return;
        throw new Error("Unable to shut down thread pool: " + threadPool.shutdownNow());
    }

    @Override
    public String toString() { return label; }
}

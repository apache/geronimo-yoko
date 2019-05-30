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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
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
import static testify.bus.LogBus.LogLevel.DEFAULT;

/**
 * Enable multiple threads to communicate asynchronously.
 */
class BusImpl implements LogBus, EasyCloseable {
    protected final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ConcurrentMap<String, Object> properties = new ConcurrentSkipListMap<>();
    private final ConcurrentMap<String, Queue<Consumer<String>>> callbacks = new ConcurrentHashMap<>();
    private final Set<String> loggingShortcuts = new ConcurrentSkipListSet<>();
    private final Map<String, Bus> userBusMap = new ConcurrentHashMap<>();
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
                .forEach(action -> threadPool.execute(() -> action.accept(value)));
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

    public BiStream biStream() {
        return BiStream.of(properties).narrowValues(String.class);
    }

    private <T> T collect(Supplier<T> supplier, Function<T,BiConsumer<String, String>> accumulator) {
        T t = supplier.get();
        this.forEach(accumulator.apply(t));
        return t;
    }

    @Override
    public String isLoggingEnabled(String user, LogLevel level) {
        String spec = forUser(user).peek(LogSpec.SPEC);
        if (spec == null) spec = global().peek(LogSpec.SPEC);
        if (spec == null) return null;

        Class<?> caller = StackUtil.getCallingClass();
        String context = caller.getName();
        String shortcut = user + level + context;
        Supplier<String> returnValue = () -> {
            loggingShortcuts.add(shortcut);
            return context;
        };

        if (loggingShortcuts.contains(shortcut)) return context;

        // an empty string for the trace spec means enable all DEFAULT level logging
        if (spec.isEmpty() && DEFAULT.includes(level)) return returnValue.get();

        for (String specpart : spec.split(":")) {
            // we can specify just the log level
            if (specpart.equals(level.name())) return returnValue.get();
        };

        for (String specpart : spec.split(":")) {
            String[] subparts = specpart.split("=", 2);
            String pattern = subparts[0] + ".*";
            if (!caller.getName().matches(pattern)) continue;
            if (subparts.length == 1) {
                if (DEFAULT.includes(level)) return returnValue.get();
            }
            String levelSpec = subparts[1];
            try {
                LogLevel specifiedLevel = LogLevel.valueOf(levelSpec);
                if (specifiedLevel.includes(level)) return returnValue.get();
            } catch (Throwable t) {
                System.err.println("Unknown level '" + levelSpec + "' in logging specification '" + spec + "'");
            }
        }
        return null;
    }

    @Override
    public void enableLogging(String user, LogLevel level, String pattern) {
        Bus bus = forUser(user);
        // add this new pattern to the existing spec if any
        String spec = bus.peek(LogSpec.SPEC);
        if (spec == null) spec = "";
        else spec += ":";
        spec += pattern + '=' + level;
        // now put the new spec on the bus so everyone can see it
        bus.put(LogSpec.SPEC, spec);
    }

    @Override
    public Bus forUser(String user) {
        return userBusMap.computeIfAbsent(user, UserBus::new);
    }

    @Override
    public void easyClose() throws Exception {
        threadPool.shutdown();
        threadPool.awaitTermination(200, MILLISECONDS);
        threadPool.shutdownNow();
        if (threadPool.isTerminated()) return;
        throw new Error("Unable to shut down thread pool: " + threadPool.shutdownNow());
    }

    private class UserBus implements QualifiedBus {
        final String user;
        private UserBus(String user) {this.user = user;}
        public String user() { return user; }
        public LogBus bus() { return BusImpl.this; }
        @Override
        public String toString() { return String.format("UserBus[%s]%s", user(), format(biStream())); }
    }

    @Override
    public String toString() {
        return format(this.biStream());
    }

    public static String format(BiStream<String, String> bis) {
        StringBuilder sb = new StringBuilder("{");
        bis.forEach((k, v) -> sb.append("\n\t").append(k).append(" -> ").append(v));
        sb.append("}");
        return sb.toString();
    }

    enum StackUtil {
        ;
        private static class Callers extends SecurityManager {
            private static final Callers INSTANCE = new Callers();

            private static Class<?>[] get() {
                return INSTANCE.getClassContext();
            }
        }

        private static final Package MY_PKG = StackUtil.class.getPackage();

        static Class<?> getCallingClass() {
            final Class<?>[] stack = Callers.get();
            for (Class<?> c: stack) {
                if (MY_PKG.equals(c.getPackage())) continue;
                return c;
            }
            throw new Error("Could not find a caller in the stack: " + Arrays.toString(stack));
        }

    }
}



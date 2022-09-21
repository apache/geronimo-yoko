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
package testify.streams;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
public interface BiStream<K, V> {
    boolean tryAdvance(BiConsumer<K, V> action);

    default void forEach(BiConsumer<K, V> action) {
        while (tryAdvance(action)) continue;
    }

    default BiStream<K, V> filterKeys(Function<K, Boolean> filter) {
        return action -> this.tryAdvance((k, v) -> { if (filter.apply(k)) action.accept(k, v); });
    }

    default <K2> BiStream<K2, V> mapKeys(Function<K, K2> mapper) {
        return action -> this.tryAdvance((k, v) -> action.accept(mapper.apply(k), v));
    }

    default <K2 extends K> BiStream<K2, V> narrowKeys(Class<K2> keyClass) {
        return this.filterKeys(keyClass::isInstance).mapKeys(keyClass::cast);
    }

    default BiStream<K, V> filterValues(Function<V, Boolean> filter) {
        return action -> this.tryAdvance((k, v) -> { if (filter.apply(v)) action.accept(k, v); });
    }

    default <V2> BiStream<K, V2> mapValues(Function<V, V2> mapper) {
        return action -> this.tryAdvance((k, v) -> action.accept(k, mapper.apply(v)));
    }

    default <V2> BiStream<K, V2> mapValues(BiFunction<K,V,V2> mapper) {
        return action -> this.tryAdvance((k, v) -> action.accept(k, mapper.apply(k, v)));
    }

    default <V2 extends V> BiStream<K, V2> narrowValues(Class<V2> valueClass) {
        return this.filterValues(valueClass::isInstance).mapValues(valueClass::cast);
    }

    default BiStream<K, V> filter(BiFunction<K, V, Boolean> filter) {
        return action -> this.tryAdvance((k, v) -> { if (filter.apply(k, v)) action.accept(k, v); });
    }

    default <K2, V2> BiStream<K2, V2> map(Function<K, K2> keyMapper, Function<V, V2> valueMapper) {
        return this.mapKeys(keyMapper).mapValues(valueMapper);
    }

    default <T> Stream<T> map(BiFunction<K, V, T> mapper) {
        return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return BiStream.this.tryAdvance((k, v) -> action.accept(mapper.apply(k, v)));
            }
        }, false);
    }

    default <K2 extends K, V2 extends V> BiStream<K2, V2> narrow(Class<K2> keyClass, Class<V2> valueClass) {
        return this.narrowKeys(keyClass).narrowValues(valueClass);
    }

    default Stream<K> keys() { return map((k, v) -> k); }
    default Stream<V> values() { return map((k, v) -> v); }

    default <R> R collect(Supplier<R> supplier, Function<R, BiConsumer<K, V>> accumulator) {
        R result = supplier.get();
        forEach(accumulator.apply(result));
        return result;
    }

    default <R> R collect(BiCollector<K,V,R> collector) { return collect(collector.supplier(), collector.accumulator()); }

    static <K,V> BiStream<K,V> of(Map<K,V> map) {
        Spliterator<Entry<K,V>> split = map.entrySet().spliterator();
        return action -> split.tryAdvance(e -> action.accept(e.getKey(), e.getValue()));
    }

    static <K,V> BiStream<K,V> of(Function<V,K> keyFunction, V[] values) { return of(keyFunction, Stream.of(values)); }
    static <K,V> BiStream<K,V> of(Function<V,K> keyFunction, Stream<V> values) {
        Spliterator<V> spliterator = values.spliterator();
        return action -> spliterator.tryAdvance(v -> action.accept(keyFunction.apply(v), v));
    }

    static <K extends Enum<K>,V> BiStream<K,V> of(Class<K> enumClass, Function<K,V> valueFunction) { return of(enumClass.getEnumConstants(), valueFunction); }
    static <K,V> BiStream<K,V> of(K[] keys, Function<K,V> valueFunction) { return of(Stream.of(keys), valueFunction); }
    static <K,V> BiStream<K,V> of(Stream<K> keys, Function<K,V> valueFunction) {
        Spliterator<K> spliterator = keys.spliterator();
        return action -> spliterator.tryAdvance(k -> action.accept(k, valueFunction.apply(k)));
    }

    interface BiCollector<K,V,R> {
        Supplier<R> supplier();
        Function<R, BiConsumer<K, V>> accumulator();
        static <K,V,R> BiCollector<K,V,R> of(Supplier<R> supplier, Function<R, BiConsumer<K, V>> accumulator) {
            return new BiCollector<K,V,R>() {
                public Supplier<R> supplier() { return supplier; }
                public Function<R,BiConsumer<K,V>> accumulator() { return accumulator; }
            };
        }
        static <K,V,M extends Map<K,V>> BiCollector<K,V,M> toMap(Supplier<M> supplier) { return BiCollector.of(supplier, m -> m::put); }
        static <K,V> BiCollector<K,V,HashMap<K,V>> toHashMap() { return BiCollector.toMap(HashMap::new); }
        static <K extends Enum<K>,V> BiCollector<K,V, EnumMap<K,V>> toEnumMap(Class<K> enumClass) { return BiCollector.toMap(() -> new EnumMap<>(enumClass)); }
    }
}

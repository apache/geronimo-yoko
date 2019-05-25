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
package test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface BiStream<K, V> {
    void forEach(BiConsumer<K, V> action);

    default BiStream<K, V> filterKeys(Function<K, Boolean> filter) {
        return action -> this.forEach((k, v) -> { if (filter.apply(k)) action.accept(k, v); });
    }

    default <K2> BiStream<K2, V> mapKeys(Function<K, K2> mapper) {
        return action -> this.forEach((k, v) -> action.accept(mapper.apply(k), v));
    }

    default <K2 extends K> BiStream<K2, V> narrowKeys(Class<K2> keyClass) {
        return this.filterKeys(keyClass::isInstance).mapKeys(keyClass::cast);
    }

    default BiStream<K, V> filterValues(Function<V, Boolean> filter) {
        return action -> this.forEach((k, v) -> { if (filter.apply(v)) action.accept(k, v); });
    }

    default <V2> BiStream<K, V2> mapValues(Function<V, V2> mapper) {
        return action -> this.forEach((k, v) -> action.accept(k, mapper.apply(v)));
    }

    default <V2 extends V> BiStream<K, V2> narrowValues(Class<V2> valueClass) {
        return this.filterValues(valueClass::isInstance).mapValues(valueClass::cast);
    }

    default BiStream<K, V> filter(BiFunction<K, V, Boolean> filter) {
        return action -> this.forEach((k, v) -> { if (filter.apply(k, v)) action.accept(k, v); });
    }

    default <K2, V2> BiStream<K2, V2> map(Function<K, K2> keyMapper, Function<V, V2> valueMapper) {
        return this.mapKeys(keyMapper).mapValues(valueMapper);
    }

    default <T> Stream<T> map(BiFunction<K, V, T> mapper) {
        final List<T> tmpList = new ArrayList<>();
        this.forEach((k, v) -> tmpList.add(mapper.apply(k, v)));
        return tmpList.stream();
    }

    default <K2 extends K, V2 extends V> BiStream<K2, V2> narrow(Class<K2> keyClass, Class<V2> valueClass) {
        return this.narrowKeys(keyClass).narrowValues(valueClass);
    }

    default Stream<K> keys() { return map((k, v) -> k); }
    default Stream<V> values() { return map((k, v) -> v); }

    static <K, V> BiStream<K, V> of(Map<K, V> map) { return map::forEach; }
}


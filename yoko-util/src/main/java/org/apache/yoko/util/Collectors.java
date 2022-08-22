/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
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
package org.apache.yoko.util;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;

public enum Collectors {
    ;
    public static <E extends Enum<E>> Collector<E, ?, Set<E>> toUnmodifiableEnumSet(Class<E> enumClass) {
        return new Collector<E, EnumSet<E>, Set<E>>() {
            public Supplier<EnumSet<E>> supplier() { return () -> EnumSet.noneOf(enumClass); }
            public BiConsumer<EnumSet<E>, E> accumulator() { return EnumSet::add; }
            public BinaryOperator<EnumSet<E>> combiner() { return (a, b) -> {a.addAll(b); return a;}; }
            public Function<EnumSet<E>, Set<E>> finisher() { return Collections::unmodifiableSet; }
            public Set<Characteristics> characteristics() { return EnumSet.of(UNORDERED); }
        };
    }
    
    public static <T> Collector<T, ?, BitSet> toBitSet(Function<T, Integer> intMapper) {
        return new Collector<T, BitSet, BitSet>() {
            public Supplier<BitSet> supplier() { return BitSet::new; }
            public BiConsumer<BitSet, T> accumulator() { return (bs, t) -> bs.set(intMapper.apply(t)); }
            public BinaryOperator<BitSet> combiner() { return (a, b) -> {a.and(b); return a;}; }
            public Function<BitSet, BitSet> finisher() { return bs -> bs; }
            public Set<Characteristics> characteristics() { return EnumSet.of(UNORDERED, IDENTITY_FINISH); }
        };
    }
}

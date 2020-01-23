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

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static org.junit.jupiter.api.Assertions.assertNull;

public enum Collectors {;
    /**
     * Assert that a stream contains no more than one item and return an optional containing the item if present.
     * @param assertionText the message to use if the condition is violated
     *                      (may contain printf-style placeholders for inserting the clashing items into the message)
     */
    public static <T> Collector<T, ?, Optional<T>> requireNoMoreThanOne(String assertionText) {
        return new AtMostOneCollector<>(assertionText);
    }

    private static class AtMostOneCollector<T> implements Collector<T, AtMostOneCollector<T>, Optional<T>> {
        final String assertionText;
        T elem;

        private AtMostOneCollector(String assertionText) {this.assertionText = assertionText;}

        void accumulate(T t){
            assertNull(elem, () -> String.format(assertionText, elem, t));
            elem = t;
        }

        AtMostOneCollector combine(AtMostOneCollector<T> that) {
            if (that.elem != null) accumulate(that.elem);
            return this;
        }

        public Supplier<AtMostOneCollector<T>> supplier() { return () -> new AtMostOneCollector<>(assertionText); }
        public BiConsumer<AtMostOneCollector<T>, T> accumulator() { return AtMostOneCollector::accumulate; }
        public BinaryOperator<AtMostOneCollector<T>> combiner() { return AtMostOneCollector::combine; }
        public Function<AtMostOneCollector<T>, Optional<T>> finisher() { return c -> Optional.ofNullable(c.elem); }
        public Set<Characteristics> characteristics() { return EnumSet.of(Characteristics.UNORDERED); }
    }
}

/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
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

import static org.junit.jupiter.api.Assertions.assertFalse;

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
        Optional<T> elem = Optional.empty();

        private AtMostOneCollector(String assertionText) {this.assertionText = assertionText;}

        void accumulate(T t){
            assertFalse(elem.isPresent(), () -> String.format(assertionText, elem.get(), t));
            elem = Optional.ofNullable(t);
        }

        AtMostOneCollector combine(AtMostOneCollector<T> acc) {
            acc.elem.ifPresent(this::accumulate);
            return this;
        }

        public Supplier<AtMostOneCollector<T>> supplier() { return () -> new AtMostOneCollector(assertionText); }
        public BiConsumer<AtMostOneCollector<T>, T> accumulator() { return AtMostOneCollector::accumulate; }
        public BinaryOperator<AtMostOneCollector<T>> combiner() { return AtMostOneCollector::combine; }
        public Function<AtMostOneCollector<T>, Optional<T>> finisher() { return c -> c.elem; }
        public Set<Characteristics> characteristics() { return EnumSet.of(Characteristics.UNORDERED); }
    }
}

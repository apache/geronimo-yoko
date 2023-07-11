/*
 * Copyright 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package testify.streams;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public enum Streams {
    ;
    @FunctionalInterface
    public interface Streamer<T> {
        boolean tryAdvance(Consumer<? super T> action);
    }

    /** Define the behaviour of a spliterator given only the logic for tryAdvance. */
    @FunctionalInterface
    private interface Streamer0<T> extends Spliterator<T> {
        boolean tryAdvance(Consumer<? super T> action);
        default Spliterator<T> trySplit() { return null; } // don't support parallelization
        default long estimateSize() { return Long.MAX_VALUE; } // unknown size
        default int characteristics() { return 0; } // can't really guarantee anything else here
    }

    public static <T> Stream<T> stream(Streamer<T> streamer) {
        // N.B. implicitly casting a method reference (or lambda) to a functional interface is a way of adding a mixin.
        // The calling code cannot override the default methods on the private interface, Streamer0.
        Streamer0<T> spliterator = streamer::tryAdvance;
        return StreamSupport.stream(spliterator, false);
    }

    public static <T> Stream<T> stream(Iterator<T> iter) {
        return stream(action -> Optional.ofNullable(iter)
                .filter(Iterator::hasNext)
                .map(i -> {action.accept(i.next()); return true;})
                .orElse(false));
    }
}

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
        // N.B. pay special attention to the cast below.
        // It is casting a *method reference* to a functional interface,
        // which is Java 8's way of adding a mixin.
        return StreamSupport.stream((Streamer0<T>) streamer::tryAdvance, false);
    }
}

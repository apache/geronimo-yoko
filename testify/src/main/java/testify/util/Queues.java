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
package testify.util;

import testify.streams.Streams;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Stream;

public enum Queues {
    ;

    public static <T> Stream<T> drain(Queue<T> q) {
        return Streams.stream( action -> {
            T t = q.poll();
            if (t == null) return false;
            action.accept(t);
            return true;
        });
    }

    /** Drain the supplied queues in their natural ordering, assuming the ordering may change after each poll operation. */
    public static <T, Q extends Queue<T> & Comparable<Q>> Stream<T> drainInOrder(Collection<Q> queues) {
        return drainInOrder(new PriorityQueue<>(queues), null);
    }

    /** Drain the supplied queues in the ordering specified, assuming the ordering may change after each poll operation. */
    public static <T, Q extends Queue<T>> Stream<T> drainInOrder(Collection<Q> queues, Comparator<Q> ordering) {
        PriorityQueue<Q> pq = new PriorityQueue<>(ordering);
        pq.addAll(queues);
        return Streams.stream(action -> {
            Q q;
            T t;
            do {
                q = pq.poll();
                if (q == null) return false;
                t = q.poll();
            } while (t == null);
            action.accept(t);
            if (!q.isEmpty()) pq.add(q);
            return true;
        });
    }
}

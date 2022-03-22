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
package org.apache.yoko.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.synchronizedList;

public enum CollectionExtras {
    ;

    public static <T> Iterable<T> removeInReverse(final List<T> list) {
        final ListIterator<T> listIterator = list.listIterator(list.size());
        return () -> new Iterator<T>() {
            public boolean hasNext() { return listIterator.hasPrevious(); }
            public T next() {
                final T result = listIterator.previous();
                listIterator.remove();
                return result;
            }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    public static <T> List<T> newSynchronizedList() {
        return synchronizedList(new ArrayList<T>());
    }

    public static <T> Iterable<T> allOf(final Iterable<? extends T>... iterables) {
        return () -> new Iterator<T>() {
            final Iterator<Iterable<? extends T>> metaIterator = asList(iterables).iterator();
            Iterator<? extends T> iterator = emptyIterator();
            public boolean hasNext() {
                for (;;) {
                    if (iterator.hasNext()) return true;
                    if (!metaIterator.hasNext()) return false;
                    iterator = metaIterator.next().iterator();
                }
            }
            public T next() { return iterator.next(); }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    public static <U, T extends U> Iterable<T> filterByType(final Iterable<U> iterable, final Class<T> type) {
        return () -> new Iterator<T>() {
            Iterator<U> iterator = iterable.iterator();
            T next = null;
            public boolean hasNext() {
                if (null != next) return true;
                while (iterator.hasNext()) {
                    U elem = iterator.next();
                    if (!type.isInstance(elem)) continue;
                    next = type.cast(elem);
                    return true;
                }
                return false;
            }
            public T next() {
                try {
                    return next;
                } finally {
                    next = null;
                }
            }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }

    public static <E extends Enum<E>> Set<E> readOnlyEnumSet(E first, E...rest) {
        return Collections.unmodifiableSet(EnumSet.of(first, rest));
    }
}

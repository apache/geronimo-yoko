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
package testify.bus.key;

import testify.bus.Bus;
import testify.bus.Key;

import java.util.List;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * Transmit a list of <code>T</code> elements using a {@link TypeKey}
 * to transmit the elements as strings and a {@link StringListKey}
 * to transmit the list as a string.
 * @param <T> the element type of the list
 */
public interface ListKey<T> extends TypeKey<List<T>> {
    Key<T> getElementKey();

    @Override
    default String stringify(List<T> list) {
        return StringListKey.toString(list
                .stream()
                .map(getElementKey()::stringify)
                .collect(toList()));
    }

    @Override
    default List<T> unstringify(String s, Supplier<Bus> busSupplier) {
        return StringListKey.toList(s)
                .stream()
                .map(str -> getElementKey().unstringify(str, busSupplier))
                .collect(toList());
    }
}

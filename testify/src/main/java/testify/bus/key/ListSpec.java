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

import testify.bus.TypeSpec;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Transmit a list of <code>T</code> elements using a {@link TypeSpec}
 * to transmit the elements as strings and a {@link StringListSpec}
 * to transmit the list as a string.
 * @param <T> the element type of the list
 */
public interface ListSpec<T> extends TypeSpec<List<T>> {
    TypeSpec<T> getElementTypeSpec();

    @Override
    default String stringify(List<T> list) {
        return StringListSpec.toString(list
                .stream()
                .map(getElementTypeSpec()::stringify)
                .collect(toList()));
    }

    @Override
    default List<T> unstringify(String s) {
        return StringListSpec.toList(s)
                .stream()
                .map(getElementTypeSpec()::unstringify)
                .collect(toList());
    }
}

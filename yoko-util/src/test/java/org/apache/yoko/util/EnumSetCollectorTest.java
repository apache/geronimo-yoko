/*
 * Copyright 2022 IBM Corporation and others.
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
package org.apache.yoko.util;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.stream.Stream;

import static java.util.Collections.EMPTY_SET;
import static org.apache.yoko.util.Collectors.toUnmodifiableEnumSet;
import static org.apache.yoko.util.EnumSetCollectorTest.TestEnum.A;
import static org.apache.yoko.util.EnumSetCollectorTest.TestEnum.C;
import static org.apache.yoko.util.EnumSetCollectorTest.TestEnum.D;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EnumSetCollectorTest {
    enum TestEnum {A, B, C, D}
    @Test
    void testEmptyEnumSet() {
        assertThat(Stream.empty()
                .map(TestEnum.class::cast)
                .collect(toUnmodifiableEnumSet(TestEnum.class)), is(EMPTY_SET));
    }

    @Test
    void testSingleEnumSet() {
        assertThat(Stream.of(A).collect(toUnmodifiableEnumSet(TestEnum.class)), is(EnumSet.of(A)));
    }

    @Test
    void testMultipleEnumSet() {
        assertThat(Stream.of(C, D, A).collect(toUnmodifiableEnumSet(TestEnum.class)), is(EnumSet.of(A, C, D)));
    }

    @Test
    void testFullEnumSet() {
        assertThat(Stream.of(TestEnum.values()).collect(toUnmodifiableEnumSet(TestEnum.class)), is(EnumSet.allOf(TestEnum.class)));
    }
}

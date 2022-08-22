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

import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.yoko.util.Collectors.toBitSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class BitSetCollectorTest {
    @Test
    void testNoBitsSet() {
        assertThat(Stream.empty().collect(toBitSet(i -> {throw new Error();})), is(new BitSet()));
    }

    @Test
    void testSingleBitSet() {
        BitSet expected = new BitSet();
        BitSet actual = Stream.of(23)
                .peek(expected::set)
                .collect(toBitSet(Integer::intValue));
        assertThat(actual, is(expected));
    }

    @Test
    void testMultipleBitsSet() {
        BitSet expected = new BitSet();
        BitSet actual = Stream.of(2, 3, 5, 7, 11, 13, 17, 19, 23)
                .peek(expected::set)
                .collect(toBitSet(Integer::intValue));
        assertThat(actual, is(expected));
    }

    @Test
    void testAllBitsSet() {
        BitSet expected = new BitSet();
        BitSet actual = IntStream.range(0,65)
                .peek(expected::set)
                .mapToObj(Integer::toString)
                .collect(toBitSet(Integer::parseInt));
        assertThat(actual, is(expected));
    }
}

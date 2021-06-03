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
package org.apache.yoko.io;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AlignmentBoundaryTest {
    @Test
    public void testNoByteBoundary() {
        AlignmentBoundary boundary = AlignmentBoundary.NO_BOUNDARY;
        for (int i = 0; i < 100; i++) {
            assertThat(boundary.gap(i), is(0));
            assertThat(boundary.newIndex(i), is(i));
        }
    }

    @Test
    public void testTwoByteBoundary() {
        check(AlignmentBoundary.TWO_BYTE_BOUNDARY, 2);
    }

    @Test
    public void testFourByteBoundary() {
        check(AlignmentBoundary.FOUR_BYTE_BOUNDARY, 4);
    }

    @Test
    public void testEightByteBoundary() {
        check(AlignmentBoundary.EIGHT_BYTE_BOUNDARY, 8);
    }

    private void check(AlignmentBoundary boundary, int width) {
        for (int i = 0; i < 100; i++) {
            int expectedGap = width - (i % width);
            if (expectedGap == width) expectedGap = 0;
            assertThat(boundary.gap(i), is(expectedGap));
            assertThat(boundary.newIndex(i), is(i + expectedGap));
        }
    }
}

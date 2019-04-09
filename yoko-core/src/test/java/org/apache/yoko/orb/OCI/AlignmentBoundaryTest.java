/*
 * =============================================================================
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * =============================================================================
 */
package org.apache.yoko.orb.OCI;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

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

    @Test
    public void testBoundaryConflation() {
        for (AlignmentBoundary a: AlignmentBoundary.values()) for (AlignmentBoundary b: AlignmentBoundary.values()) {
            AlignmentBoundary expected = AlignmentBoundary.values()[Math.max(a.ordinal(), b.ordinal())];
            assertThat(a.and(b), is(expected));
        }
    }
}

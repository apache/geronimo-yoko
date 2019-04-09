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

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Enumerate the alignment boundaries in use.
 * Each member implements its methods efficiently.
 *
 * "The boundary lines have fallen for me in pleasant places; surely I have a delightful inheritance."
 */
public enum AlignmentBoundary {
    NO_BOUNDARY {
        int gap(int index) { return 0; }
        int newIndex(int index) { return index; }
        public AlignmentBoundary and(AlignmentBoundary that) { return requireNonNull(that); }
    }, TWO_BYTE_BOUNDARY {
        int gap(int index) { return index & 1; }
        int newIndex(int index) { return (index + 1) & ~1; }
        public AlignmentBoundary and(AlignmentBoundary that) { return requireNonNull(that) == NO_BOUNDARY ? this : that; }
    }, FOUR_BYTE_BOUNDARY {
        int gap(int index) { return -index & 3; }
        int newIndex(int index) { return (index + 3) & ~3; }
        public AlignmentBoundary and(AlignmentBoundary that) { return requireNonNull(that) == EIGHT_BYTE_BOUNDARY ? that : this; }
    }, EIGHT_BYTE_BOUNDARY {
        int gap(int index) { return -index & 7; }
        int newIndex(int index) { return (index + 7) & ~7; }
        public AlignmentBoundary and(AlignmentBoundary that) { return this; }
    };

    /**
     * Calculate the number of bytes between the supplied index and the next alignment boundary.
     */
    abstract int gap(int index);

    /**
     * Calculate the index of the next alignment boundary.
     */
    abstract int newIndex(int index);

    /**
     * Returns the minimum alignment boundary that satisfies both <code>this</code> alignment boundary
     * and <code>that</code> alignment boundary (i.e. the larger of the two).
     * @param that the other alignment boundary --- must not be <code>null</code>
     */
    public abstract AlignmentBoundary and(AlignmentBoundary that);
}

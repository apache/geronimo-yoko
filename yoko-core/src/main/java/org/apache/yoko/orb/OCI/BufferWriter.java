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

public interface BufferWriter {
    void padAlign(AlignmentBoundary boundary);

    /**
     * Write some padding bytes.
     * @param n the number of padding bytes to write, from 0 to 7
     *
     */
    void pad(int n);

    /**
     * Ensure there is space to write from the current position, after aligning on a boundary
     * @param size the number of bytes to be written
     * @param align the size of boundary to align on (in bytes)
     * @return <code>true</code> iff more space had to be allocated
     */
    boolean ensureAvailable(int size, AlignmentBoundary boundary);

    /**
     * Ensure there is space to write from the current position.
     * @param size the number of bytes to be written
     * @return <code>true</code> iff more space had to be allocated
     */
    boolean ensureAvailable(int size);

    void writeByte(int i);

    void writeByte(byte b);

    void writeChar(char value);

    void writeShort(short value);

    void writeInt(int value);

    void writeLong(long value);
}

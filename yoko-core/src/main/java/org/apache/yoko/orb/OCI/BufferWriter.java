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

import org.apache.yoko.orb.OCI.Buffer.AlignmentBoundary;

public interface BufferWriter {
    void padAlign(AlignmentBoundary boundary);

    /**
     * Write some padding bytes.
     * @param n the number of padding bytes to write, from 0 to 7
     *
     */
    void pad(int n);

    void writeByte(int i);

    void writeByte(byte b);

    void writeChar(char value);

    void writeShort(short value);

    void writeInt(int value);

    void writeLong(long value);
}

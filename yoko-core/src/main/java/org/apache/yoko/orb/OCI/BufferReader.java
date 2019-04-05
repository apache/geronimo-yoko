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

public interface BufferReader {
    void align(AlignmentBoundary boundary);

    void skipBytes(int n);

    void skipToEnd();

    void rewindToStart();

    byte peekByte();

    byte readByte();

    char readByteAsChar();

    void readBytes(byte[] value, int offset, int length);

    char peekChar();

    char readChar();

    char readChar_LE();

    String dumpPosition();

    String dumpRemainingData();

    String dumpAllData();

    String dumpAllDataWithPosition();
}

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
package org.apache.yoko.orb.OCI;

import org.omg.CORBA.portable.InputStream;

import java.util.logging.Logger;

public interface BufferWriter {
    void trim();

    void padAlign(AlignmentBoundary boundary);

    /**
     * Write some padding bytes.
     * @param n the number of padding bytes to write, from 0 to 7
     *
     */
    void pad(int n);

    /**
     * Ensure there is space to write from the current position,
     * taking an alignment boundary into account. The writer will be
     * aligned on the provided boundary when this method returns.
     * @param size the number of bytes to be written
     * @param align the size of boundary to align on (in bytes)
     * @return <code>true</code> iff an existing buffer had to be resized
     */
    boolean ensureAvailable(int size, AlignmentBoundary boundary);

    /**
     * Ensure there is space to write from the current position.
     * @param size the number of bytes to be written
     * @return <code>true</code> iff an existing buffer had to be resized
     */
    boolean ensureAvailable(int size);

    void writeBytes(byte[] bytes);

    void writeBytes(byte[] bytes, int offset, int length);

    void readFrom(InputStream source);

    void writeByte(int i);

    void writeByte(byte b);

    void writeChar(char value);

    void writeShort(short value);

    void writeInt(int value);

    void writeLong(long value);

    /**
     * Write the available bytes from <code>reader</code>.
     * @param reader
     */
    void writeBytes(BufferReader reader);

    /**
     * Leaves a 4 byte space to write a length. When {@link SimplyCloseable#close()} is called,
     * the number of intervening bytes is written as a length to the remembered location.
     * @param logger the logger to use to log the operations - must not be null
     */
    SimplyCloseable recordLength(Logger logger);
}

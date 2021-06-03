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

import org.apache.yoko.util.HexConverter;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import static org.apache.yoko.util.Hex.formatHexPara;

public final class ReadBuffer extends Buffer<ReadBuffer> {
    ReadBuffer(Core core) { super(core); }

    public byte peekByte() { return core.data[position];    }

    public byte readByte() { return core.data[position++]; }

    public char readByteAsChar() { return (char) core.data[position++]; }

    public void readBytes(byte[] buffer, int offset, int length) {
        if (available() < length) throw new IndexOutOfBoundsException();
        System.arraycopy(core.data, position, buffer, offset, length);
        position += length;
    }

    public void readBytes(WriteBuffer buffer) {
        buffer.writeBytes(core.data, position, available());
    }

    public byte[] copyRemainingBytes() {
        return copyOf(core.data, available());
    }

    public char peekChar() {
        return (char)((core.data[position] << 8) | (core.data[position + 1] & 0xff));
    }

    public char readChar() {
        return (char) ((core.data[position++] << 8) | (core.data[position++] & 0xff));
    }

    public char readChar_LE() {
        return (char) ((core.data[position++] & 0xff) | (core.data[position++] << 8));
    }

    public int readInt() {
        return (core.data[position++] << 24)
                | ((core.data[position++] << 16) & 0xff0000)
                | ((core.data[position++] << 8) & 0xff00)
                | (core.data[position++] & 0xff);
    }

    public int readInt_LE() {
        return (core.data[position++] & 0xff)
                | ((core.data[position++] << 8) & 0xff00)
                | ((core.data[position++] << 16) & 0xff0000)
                | (core.data[position++] << 24);
    }

    public String remainingBytesToAscii() {
        return HexConverter.octetsToAscii(core.data, available());
    }

    public String dumpRemainingData() {
        StringBuilder dump = new StringBuilder();
        dump.append(String.format("Core pos=0x%x Core len=0x%x Remaining core data=%n%n", position, core.length));
        return formatHexPara(core.data, position, available(), dump).toString();
    }

    public String dumpAllDataWithPosition() {
        StringBuilder sb = new StringBuilder();
        formatHexPara(core.data, 0, position, sb);
        sb.append(String.format("------------------ pos = 0x%08X -------------------%n", position));
        return formatHexPara(core.data, position, available(), sb).toString();
    }

    public ReadBuffer writeTo(OutputStream out) throws IOException {
        try {
            out.write(core.data, position, available());
            out.flush();
            position = core.length;
            return this;
        } catch (InterruptedIOException ex) {
            position += ex.bytesTransferred;
            throw ex;
        }
    }

    public ReadBuffer rewindToStart() {
        position = 0;
        return this;
    }

    public ReadBuffer align(AlignmentBoundary boundary) {
        position = boundary.newIndex(position);
        return this;
    }

    public ReadBuffer skipBytes(int n) {
        if (position + n > core.length) throw new IndexOutOfBoundsException();
        position = position + n;
        return this;
    }

    public ReadBuffer newReadBuffer() { return clone(); }
}

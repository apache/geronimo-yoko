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

import org.apache.yoko.orb.OB.IORUtil;
import org.apache.yoko.util.HexConverter;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_MEMORY;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorAllocationFailure;
import static org.apache.yoko.orb.OB.MinorCodes.describeNoMemory;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;

final class Buffer {
    private transient byte[] data; // The octet buffer
    private int length; // The requested size of the buffer

    BufferReader readFromStart() {
        return new Reader();
    }

    BufferWriter writeFromStart() {
        return new Writer();
    }

    /**
     * Extend the current buffer.
     * @param extra the number of additional bytes required beyond the end of the buffer.
     * @return <code>true</code> iff an existing buffer was insufficient
     */
    private boolean addLength(int extra) {
        _OB_assert(extra >= 0);
        length += extra;

        // the existing buffer might be big enough
        if (length <= data.length) {
            return false;
        }
        // ok, we need a bigger buffer
        data = copyOf(data, computeNewBufferSize(length));
        return true;
    }

    private int computeNewBufferSize(int len) {
        // use an allocation threshold of 4 megabytes
        final int MAX_OVERALLOC = 4 * 1024 * 1024;
        // double the existing capacity, unless over a threshold
        final int minAlloc = data.length + min(data.length, MAX_OVERALLOC);
        // allow more if requested length is greater
        return max(len, minAlloc);
    }

    /**
     * Create a Buffer with initial length zero.
     */
    public Buffer() {
        // since we expect a write operation to follow, allocate a small buffer up front
        this(newBytes(16), 0);
    }
    public Buffer(byte[] data) {
        this(data, data.length);
    }

    /**
     * Create a Buffer with <code>len</code> bytes available for writing.
     */
    public Buffer(int len) {
        this(newBytes(len), len);
    }
    private Buffer(byte[] data, int len) {
        this.data = data;
        this.length = len;
    }

    private static byte[] copyOf(byte[] data, int length) {
        try {
            return Arrays.copyOf(data, length);
        } catch (OutOfMemoryError oom) {
            throw new NO_MEMORY(describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
        }
    }

    private static byte[] newBytes(int len) {
        try {
            // allocate only multiples of 16 so we can pad without checking
            return new byte[(len + 0xFF) & ~0xFF];
        } catch (OutOfMemoryError oom) {
            throw new NO_MEMORY(describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
        }
    }

    @Override
    public String toString() {
        return readFromStart().dumpAllDataWithPosition();
    }

    enum Padding {;
        static final byte PAD_BYTE = (byte) 0xBD;
        /**
         * The base 2 log of the width of our padding array.
         * The array size must be a power of 2, so that bitwise
         * operations can be used in place of arithmetic operations.
         */
        private static final int PADDING_POWER = 5;
        private static final byte[] PADDING = new byte[1<<PADDING_POWER];
        static { Arrays.fill(PADDING, PAD_BYTE); }

        static void pad(Writer w, int n) {
            // write as many full copies of PADDING as required
            for (int i = n >> PADDING_POWER; i > 0; i--) w.writeBytes(PADDING);
            // write any remaining bytes of PADDING
            w.writeBytes(PADDING, 0, n & ((1 << PADDING_POWER) - 1));
        }
    }

    @SuppressWarnings("unchecked")
    private class Facet<T extends BufferFacet> implements BufferFacet<T> {
        int position = 0;
        @Override
        final public int getPosition() { return position; }
        @Override
        public T setPosition(int p) { position = p; return (T)this; }
        @Override
        public T rewind(int n) { position -= n; return (T)this;}
        @Override
        final public int available() { return length - position; }
        @Override
        final public int length() { return length; }
        @Override
        final public boolean isComplete() { return position >= length; }
        @Override
        public T clone() {
            try {
                return (T)super.clone();
            } catch (CloneNotSupportedException e) {
                throw new INTERNAL(e.getMessage());
            }
        }

        byte[] data() { return data; }

        @Override
        public boolean dataEquals(BufferFacet<T> other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            final Facet<T> that = (Facet<T>) other;
            if (this.length() != that.length()) return false;
            final byte[] thisdata = data;
            final byte[] thatdata = that.data();
            if (thisdata == thatdata) return true;
            for (int i = 0; i < length(); i++) if(thisdata[i] != thatdata[i]) return false;
            return true;
        }

        @Override
        public String dumpPosition() {
            return String.format("position=0x%x", position);
        }

        @Override
        public String dumpAllData() {
            StringBuilder dump = new StringBuilder();
            dump.append(String.format("Buffer len=0x%x All buffer data=%n%n", length));
            IORUtil.dump_octets(data, 0, length(), dump);
            return dump.toString();
        }
    }

    private final class Reader extends Facet<BufferReader> implements BufferReader {
        @Override
        public void align(AlignmentBoundary boundary) {
            position = boundary.newIndex(position);
        }

        @Override
        public BufferReader skipBytes(int n) {
            if (position + n > length) throw new IndexOutOfBoundsException();
            position = position + n;
            return this;
        }

        @Override
        public BufferReader skipToEnd() {
            position = length;
            return this;
        }

        @Override
        public BufferReader rewindToStart() {
            position = 0;
            return this;
        }

        @Override
        public byte peekByte() {
            return data[position];
        }

        @Override
        public byte readByte() {
            return data[position++];
        }

        @Override
        public char readByteAsChar() {
            return (char) data[position++];
        }

        @Override
        public void readBytes(byte[] value, int offset, int length) {
            if (available() < length) throw new IndexOutOfBoundsException();

            System.arraycopy(data, position, value, offset, length);
            position += length;
        }

        @Override
        public char peekChar() {
            return (char)((data[position] << 8) | (data[position + 1] & 0xff));
        }

        @Override
        public char readChar() {
            return (char) ((data[position++] << 8) | (data[position++] & 0xff));
        }

        @Override
        public char readChar_LE() {
            return (char) ((data[position++] & 0xff) | (data[position++] << 8));
        }

        @Override
        public String dumpRemainingData() {
            StringBuilder dump = new StringBuilder();
            dump.append(String.format("Buffer pos=0x%x Buffer len=0x%x Remaining buffer data=%n%n", position, length));

            IORUtil.dump_octets(data, position, available(), dump);
            return dump.toString();
        }

        @Override
        public String dumpAllDataWithPosition() {
            StringBuilder sb = new StringBuilder();
            IORUtil.dump_octets(data, 0, position, sb);
            sb.append(String.format("------------------ pos = 0x%08X -------------------%n", position));
            IORUtil.dump_octets(data, position, available(), sb);
            return sb.toString();
        }

        @Override
        public byte[] copyRemainingBytes() {
            return copyOf(data, available());
        }

        @Override
        public String remainingBytesToAscii() {
            return HexConverter.octetsToAscii(data, available());
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            try {
                out.write(data, position, available());
                out.flush();
                position = length;
            } catch (InterruptedIOException ex) {
                position += ex.bytesTransferred;
                throw ex;
            }
        }
    }

    @SuppressWarnings({"PointlessBitwiseExpression", "OctalInteger"})
    private final class Writer extends Facet<BufferWriter> implements BufferWriter {
        @Override
        public BufferWriter trim() {
            length = position;
            return this;
        }

        @Override
        public BufferReader readFromStart() {
            return new Reader();
        }

        @Override
        public void padAlign(AlignmentBoundary boundary) {
            padGap(boundary.gap(position));
        }

        private void padGap(int gap) {
            switch (gap) {
            case 7: writeByte(Padding.PAD_BYTE);
            case 6: writeByte(Padding.PAD_BYTE);
            case 5: writeByte(Padding.PAD_BYTE);
            case 4: writeByte(Padding.PAD_BYTE);
            case 3: writeByte(Padding.PAD_BYTE);
            case 2: writeByte(Padding.PAD_BYTE);
            case 1: writeByte(Padding.PAD_BYTE);
            case 0: break;
            default: Padding.pad(this, gap);
            }
        }

        @Override
        public BufferWriter padAll() {
            Padding.pad(this, length);
            return this;
        }

        @Override
        public boolean ensureAvailable(int size, AlignmentBoundary boundary) {
            final int gap = boundary.gap(position);
            try { return ensureAvailable(gap + size); }
            finally { padGap(gap); }
        }

        @Override
        public boolean ensureAvailable(int size) {
            final int shortfall = size - available();
            return shortfall > 0 && addLength(shortfall);
        }

        @Override
        public void writeBytes(byte[] bytes) {
            writeBytes(bytes, 0, bytes.length);
        }

        @Override
        public void writeBytes(byte[] bytes, int offset, int length) {
            System.arraycopy(bytes, 0, data, position, length);
            position += length;
        }

        @Override
        public void readFrom(org.omg.CORBA.portable.InputStream source) {
            final int length = available();
            source.read_octet_array(data, position, length);
            position += length;
        }

        @Override
        public void writeByte(int i) {
            data[position++] = (byte)i;
        }

        @Override
        public void writeByte(byte b) {
            data[position++] = b;
        }

        @Override
        public void writeChar(char value) {
            data[position++] = (byte) (value >> 010);
            data[position++] = (byte) (value >> 000);
        }

        @Override
        public void writeShort(short value) {
            data[position++] = (byte) (value >> 010);
            data[position++] = (byte) (value >> 000);
        }

        @Override
        public void writeInt(int value) {
            data[position++] = (byte) (value >> 030);
            data[position++] = (byte) (value >> 020);
            data[position++] = (byte) (value >> 010);
            data[position++] = (byte) (value >> 000);
        }

        @Override
        public void writeLong(long value) {
            data[position++] = (byte) (value >> 070);
            data[position++] = (byte) (value >> 060);
            data[position++] = (byte) (value >> 050);
            data[position++] = (byte) (value >> 040);
            data[position++] = (byte) (value >> 030);
            data[position++] = (byte) (value >> 020);
            data[position++] = (byte) (value >> 010);
            data[position++] = (byte) (value >> 000);
        }

        @Override
        public void writeBytes(BufferReader reader) {
            ensureAvailable(reader.available());
            Reader rdr = (Reader) reader;
            writeBytes(rdr.data(), rdr.getPosition(), rdr.available());
        }

        @Override
        public boolean readFrom(InputStream in) throws IOException {
            try {
                int result = in.read(data, position, available());
                if (result <= 0) return false;
                position += result;
                return true;
            } catch (InterruptedIOException ex) {
                position += ex.bytesTransferred;
                throw ex;
            }
        }

        @Override
        public SimplyCloseable recordLength(Logger logger) {
            return new LengthWriter(logger);
        }

        @SuppressWarnings("PointlessBitwiseExpression")
        private final class LengthWriter implements SimplyCloseable {
            final Logger logger;
            final int index;

            private LengthWriter(Logger logger) {
                this.logger = logger;
                this.index = position;
                this.logger.finest("Writing a gap value for a length at offset " + index);
                Padding.pad(Writer.this, 4);
            }

            public void close() {
                writeLength();
            }

            private void writeLength() {
                final int length = position - (index + 4);
                data[index + 0] = (byte) (length >> 030);
                data[index + 1] = (byte) (length >> 020);
                data[index + 2] = (byte) (length >> 010);
                data[index + 3] = (byte) (length >> 000);
                logger.finest("Wrote a length value of " + length + " at offset " + index);
            }
        }
    }
}

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

public final class Buffer {
    public final BufferReader reader = this.new Reader();
    public final BufferWriter writer = new Writer();

    public byte[] data_; // The octet buffer

    private int len_; // The requested size of the buffer

    public int pos_; // The position counter

    public byte[] data() {
        return data_;
    }

    public int length() {
        return len_;
    }

    public int available() {
        return len_ - pos_;
    }

    public void advance(int delta) {
        pos_ += delta;
    }

    public boolean is_full() {
        return pos_ >= len_;
    }

    public void realloc(int len) {
        if (data_ == null) {
            len_ = len;
            data_ = newBytes(len);
            pos_ = 0;
        }
        else {
            _OB_assert(len >= len_);
            if (len <= data_.length)
                len_ = len;
            else {
                byte[] newData = newBytes(computeNewBufferSize(len));
                System.arraycopy(data_, 0, newData, 0, len_);
                data_ = newData;
                len_ = len;
            }
        }
    }

    private int computeNewBufferSize(int len) {
        // use an allocation threshold of 4 megabytes
        final int MAX_OVERALLOC = 4 * 1024 * 1024;
        // double the existing capacity, unless over a threshold
        final int minAlloc = data_.length + min(data_.length, MAX_OVERALLOC);
        // allow more if requested length is greater
        return max(len, minAlloc);
    }

    public void data(byte[] data, int len) {
        this.data_ = data;
        this.len_ = len;
        this.pos_ = 0;
    }

    public void consume(Buffer buf) {
        this.data_ = buf.data_;
        this.len_ = buf.len_;
        this.pos_ = buf.pos_;
        buf.data_ = null;
        buf.len_ = 0;
        buf.pos_ = 0;
    }

    public Buffer() {}

    private Buffer(byte[] data, int len) {
        this.data_ = data;
        this.len_ = len;
        this.pos_ = 0;
    }

    public Buffer(byte[] data) {
        this(data, data.length);
    }

    public Buffer(Buffer that) {
        this(copyBytes(that.data_));
    }

    public Buffer(int len) {
        this(newBytes(len), len);
    }

    private static byte[] copyBytes(byte[] data) {
        try {
            return Arrays.copyOf(data, data.length);
        } catch (OutOfMemoryError oome) {
            throw new NO_MEMORY(describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
        }
    }

    private static byte[] newBytes(int len) {
        try {
            return new byte[len];
        } catch (OutOfMemoryError oome) {
            throw new NO_MEMORY(describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
        }
    }

    @Override
    public String toString() {
        return reader.dumpAllDataWithPosition();
    }

    public boolean readFrom(InputStream in) throws IOException {
        try {
            int result = in.read(data(), pos_, available());
            if (result <= 0) return false;
            advance(result);
            return true;
        } catch (InterruptedIOException ex) {
            advance(ex.bytesTransferred);
            throw ex;
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        try {
            out.write(data(), pos_, available());
            out.flush();
            pos_ = length();
        } catch (InterruptedIOException ex) {
            advance(ex.bytesTransferred);
            throw ex;
        }
    }

    public byte[] copyRemainingBytes() {
        byte[] bytes = new byte[available()];
        System.arraycopy(data_, pos_, bytes, 0, available());
        return bytes;
    }

    public void appendRemainingDataFrom(Buffer b) {
        realloc(length() + b.available());
        System.arraycopy(b.data(), b.pos_, data(), length(), b.available());
    }

    private final class Reader implements BufferReader {
        @Override
        public void align2() {
            pos_ += pos_ & 0x1;
        }

        @Override
        public void align4() {
            pos_ = (pos_ + 3) & ~3;
        }

        @Override
        public void align8() {
            pos_ = (pos_ + 7) & ~7;
        }

        @Override
        public void align(int n) {
            switch (n) {
            case 8: align8(); break;
            case 4: align4(); break;
            case 2: align2(); break;
            default: throw new UnsupportedOperationException("Aligning to " + n + " byte boundary is not supported");
            }
        }

        @Override
        public void skipBytes(int n) {
            if (pos_ + n > len_) throw new IndexOutOfBoundsException();
            pos_ = pos_ + n;
        }

        @Override
        public void skipToEnd() {
            pos_ = len_;
        }

        @Override
        public void rewindToStart() {
            pos_ = 0;
        }

        @Override
        public byte peekByte() {
            return data_[pos_];
        }

        @Override
        public byte readByte() {
            return data_[pos_++];
        }

        @Override
        public char readByteAsChar() {
            return (char) data_[pos_++];
        }

        @Override
        public void readBytes(byte[] value, int offset, int length) {
            if (available() < length) throw new IndexOutOfBoundsException();

            System.arraycopy(data_, pos_, value, offset, length);
            pos_ += length;
        }

        @Override
        public char peekChar() {
            return (char)((data_[pos_] << 8) | (data_[pos_ + 1] & 0xff));
        }

        @Override
        public char readChar() {
            return (char) ((data_[pos_++] << 8) | (data_[pos_++] & 0xff));
        }

        @Override
        public char readChar_LE() {
            return (char) ((data_[pos_++] & 0xff) | (data_[pos_++] << 8));
        }

        /**
         * Return the cursor position in the buffer as a formatted string suitable for logging.
         */
        @Override
        public String dumpPosition() {
            return String.format("position=0x%x", pos_);
        }

        /**
         * Return the unread data in the buffer as a formatted string suitable for logging.
         */
        @Override
        public String dumpRemainingData() {
            StringBuilder dump = new StringBuilder();
            dump.append(String.format("Buffer pos=0x%x Buffer len=0x%x Remaining buffer data=%n%n", pos_, len_));

            IORUtil.dump_octets(data_, pos_, available(), dump);
            return dump.toString();
        }

        /**
         * Return all the data in the buffer as a formatted string suitable for logging.
         */
        @Override
        public String dumpAllData() {
            StringBuilder dump = new StringBuilder();
            dump.append(String.format("Buffer len=0x%x All buffer data=%n%n", len_));
            IORUtil.dump_octets(data_, 0, length(), dump);
            return dump.toString();
        }

        /**
         * Return all the data in the buffer, with the position marked, as a formatted string suitable for logging.
         */
        @Override
        public String dumpAllDataWithPosition() {
            StringBuilder sb = new StringBuilder();
            IORUtil.dump_octets(data_, 0, pos_, sb);
            sb.append(String.format("------------------ pos = 0x%08X -------------------%n", pos_));
            IORUtil.dump_octets(data_, pos_, len_ - pos_, sb);
            return sb.toString();
        }
    }

    private static byte[] arrayOf(int n, byte b) {
        byte[] result = new byte[n];
        Arrays.fill(result, b);
        return result;
    }

    /**
     * The base 2 log of the width of our padding array. This allows us to use bit shifting and masking
     * instead of division and remainder operations
     */
    private static final int PADDING_POWER = 5;
    public static final byte PAD_BYTE = (byte) 0xBD;
    private static final int PADDING_WIDTH = 1 << PADDING_POWER;
    private static final byte[] PADDING = arrayOf(1 << PADDING_POWER, PAD_BYTE);

    private final class Writer implements BufferWriter {
        @Override
        public void padAlign2() {
            if ((pos_ & 1) == 1) writeByte(PAD_BYTE);
        }

        @Override
        public void padAlign4() {
            int remainder = pos_ & 3;
            if (remainder == 0) return;
            writeBytes(PADDING, 0, 4 - remainder);
        }

        @Override
        public void padAlign8() {
            int remainder = pos_ & 7;
            if (remainder == 0) return;
            writeBytes(PADDING, 0, 8 - remainder);
        }

        @Override
        public void pad(int n) {
            // write as many full copies of PADDING as required
            for (int i = n >> PADDING_POWER; i > 0; i--) writeBytes(PADDING);
            // write any remaining bytes of PADDING
            writeBytes(PADDING, 0, n & ((1 << PADDING_POWER) - 1));
        }

        private void writeBytes(byte[] bytes) {
            writeBytes(bytes, 0, bytes.length);
        }

        private void writeBytes(byte[] bytes, int offset, int length) {
            System.arraycopy(bytes, 0, data_, pos_, length);
            pos_ += length;
        }

        @Override
        public void writeByte(int i) {
            data_[pos_++] = (byte)i;
        }

        @Override
        public void writeByte(byte b) {
            data_[pos_++] = b;
        }

        @Override
        public void writeChar(char value) {
            data_[pos_++] = (byte) (value >> 010);
            data_[pos_++] = (byte) (value >> 000);
        }

        @Override
        public void writeShort(short value) {
            data_[pos_++] = (byte) (value >> 010);
            data_[pos_++] = (byte) (value >> 000);
        }

        @Override
        public void writeInt(int value) {
            data_[pos_++] = (byte) (value >> 030);
            data_[pos_++] = (byte) (value >> 020);
            data_[pos_++] = (byte) (value >> 010);
            data_[pos_++] = (byte) (value >> 000);
        }

        @Override
        public void writeLong(long value) {
            data_[pos_++] = (byte) (value >> 070);
            data_[pos_++] = (byte) (value >> 060);
            data_[pos_++] = (byte) (value >> 050);
            data_[pos_++] = (byte) (value >> 040);
            data_[pos_++] = (byte) (value >> 030);
            data_[pos_++] = (byte) (value >> 020);
            data_[pos_++] = (byte) (value >> 010);
            data_[pos_++] = (byte) (value >> 000);
        }
    }

    public final class LengthWriter implements AutoCloseable {
        final Logger logger;
        final int index;

        public LengthWriter(Logger logger) {
            this.logger = logger;
            this.index = pos_;
            this.logger.finest("Writing a gap value for a length at offset " + index);
            writer.pad(4);
        }

        public void close() {
            writeLength();
        }

        private void writeLength() {
            final int length = pos_ - (index + 4);
            data_[index +0] = (byte) (length >>> 030);
            data_[index +1] = (byte) (length >>> 020);
            data_[index +2] = (byte) (length >>> 010);
            data_[index +3] = (byte) (length >>> 000);
            logger.finest("Wrote a length value of " + length + " at offset " + index);
        }
    }
}

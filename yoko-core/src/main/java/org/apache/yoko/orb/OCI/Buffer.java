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
import java.util.logging.Logger;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorAllocationFailure;
import static org.apache.yoko.orb.OB.MinorCodes.describeNoMemory;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;

public final class Buffer {
    private int max_; // The maximum size of the buffer

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

    private int pos() {
        return pos_;
    }

    public void pos(int pos) {
        pos_ = pos;
    }

    public void advance(int delta) {
        pos_ += delta;
    }

    public boolean is_full() {
        return pos_ >= len_;
    }

    /**
     * Return the data in the buffer as a formatted string suitable for
     * logging.
     *
     * @return The string value of the data.
     */
    public String dumpRemainingData() {
        StringBuilder dump = new StringBuilder();
        dump.append(String.format("Buffer pos=0x%x Buffer len=0x%x Remaining buffer data=%n%n", pos_, len_));

        IORUtil.dump_octets(data_, pos_, available(), dump);
        return dump.toString();
    }

    public String dumpAllData() {
        StringBuilder dump = new StringBuilder();
        dump.append(String.format("Buffer len=0x%x All buffer data=%n%n", len_));
        IORUtil.dump_octets(data_, 0, length(), dump);
        return dump.toString();
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    private void alloc(int len) {
        max_ = len;
        len_ = len;
        try {
            data_ = new byte[max_];
        } catch (OutOfMemoryError ex) {
            throw new NO_MEMORY(describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
        }
        pos_ = 0;
    }

    public void realloc(int len) {
        if (data_ == null)
            alloc(len);
        else {
            _OB_assert(len >= len_);
            if (len <= max_)
                len_ = len;
            else {
                final int MAX_OVERALLOC = 4 * 1024 * 1024; // 4 megabytes!
                // we will look for double the existing capacity, or a smaller increment if it is over a threshold
                final int minAlloc = min(max_ * 2, max_ + MAX_OVERALLOC);
                // we might need more if the new length is greater than this minimum new allocation
                final int newMax = max(len, minAlloc);
                byte[] newData = null;
                try {
                    newData = new byte[newMax];
                } catch (OutOfMemoryError ex) {
                    throw new NO_MEMORY(describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
                }
                System.arraycopy(data_, 0, newData, 0, len_);
                data_ = newData;
                len_ = len;
                max_ = newMax;
            }
        }
    }

    public void data(byte[] data, int len) {
        data_ = data;
        len_ = len;
        max_ = len;
        pos_ = 0;
    }

    public void consume(Buffer buf) {
        data_ = buf.data_;
        len_ = buf.len_;
        max_ = buf.max_;
        pos_ = buf.pos_;
        buf.data_ = null;
        buf.len_ = 0;
        buf.max_ = 0;
        buf.pos_ = 0;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Buffer() {}

    public Buffer(byte[] data) {
        data_ = data;
        len_ = data.length;
        max_ = data.length;
        pos_ = 0;
    }

    public Buffer(int len) {
        alloc(len);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        IORUtil.dump_octets(data_, 0, pos_, sb);
        sb.append(String.format("------------------ pos = 0x%08X -------------------%n", pos_));
        IORUtil.dump_octets(data_, pos_, len_ - pos_, sb);
        return sb.toString();
    }

    public boolean readFrom(InputStream in) throws IOException {
        try {
            int result = in.read(data(), pos(), available());
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
            out.write(data(), pos(), available());
            out.flush();
            pos(length());
        } catch (InterruptedIOException ex) {
            advance(ex.bytesTransferred);
            throw ex;
        }
    }

    public byte[] copyRemainingBytes() {
        byte[] bytes = new byte[available()];
        System.arraycopy(data_, pos(), bytes, 0, available());
        return bytes;
    }

    public void appendRemainingDataFrom(Buffer b) {
        realloc(length() + b.available());
        System.arraycopy(b.data(), b.pos(), data(), length(), b.available());
    }

    public void align8() {
        // Skip cursor to next 8-byte boundary unless already at end of the buffer
        if (available() > 0) pos((pos() + 7) & ~7);
    }

    public void skipToEnd() {
        pos(length());
    }

    public void rewindToStart() {
        pos(0);
    }

    public byte readByte() {
        return data_[pos_++];
    }

    public void writeByte(int i) {
        data_[pos_++] = (byte)i;
    }

    public void writeByte(byte b) {
        data_[pos_++] = b;
    }

    public void writeChar(char value) {
        data_[pos_++] = (byte) (value >> 010);
        data_[pos_++] = (byte) (value >> 000);
    }

    public void writeShort(short value) {
        data_[pos_++] = (byte) (value >> 010);
        data_[pos_++] = (byte) (value >> 000);
    }

    public void writeInt(int value) {
        data_[pos_++] = (byte) (value >> 030);
        data_[pos_++] = (byte) (value >> 020);
        data_[pos_++] = (byte) (value >> 010);
        data_[pos_++] = (byte) (value >> 000);
    }

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

    private static final byte PAD_BYTE = (byte)0xbd;

    /**
     * Write some padding bytes.
     * @param n the number of padding bytes to write, from 0 to 7
     */
    public void pad(int n) {
        assert 0 <= n;
        assert n < 8;
        switch (n) {
        case 7: writeByte(PAD_BYTE);
        case 6: writeByte(PAD_BYTE);
        case 5: writeByte(PAD_BYTE);
        case 4: writeByte(PAD_BYTE);
        case 3: writeByte(PAD_BYTE);
        case 2: writeByte(PAD_BYTE);
        case 1: writeByte(PAD_BYTE);
        case 0: return;
        }
        throw new AssertionError(n + " must be between 0 and 7 inclusive");
    }

    public class LengthWriter implements AutoCloseable {
        final Logger logger;
        final int index;

        public LengthWriter(Logger logger) {
            this.logger = logger;
            this.index = pos();
            this.logger.finest("Writing a gap value for a length at offset " + index);
            pad(4);
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

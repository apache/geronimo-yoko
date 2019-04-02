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

    public int pos() {
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
    public String dumpData()
    {
        StringBuilder dump = new StringBuilder();
        dump.append(String.format("Buffer pos=0x%x Buffer len=0x%x Remaining buffer data=%n%n", pos_, len_));

        IORUtil.dump_octets(data_, pos_, available(), dump);
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
        int pos = pos_, len = len_;
        IORUtil.dump_octets(data_, 0, pos, sb);
        sb.append(String.format("------------------ pos = 0x%08X -------------------%n", pos));
        IORUtil.dump_octets(data_, pos, len_ - pos, sb);
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

    public void writeInto(OutputStream out) throws IOException {
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
}

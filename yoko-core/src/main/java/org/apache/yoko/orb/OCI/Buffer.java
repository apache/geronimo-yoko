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

public final class Buffer {
    private int max_; // The maximum size of the buffer

    public byte[] data_; // The octet buffer

    public int len_; // The requested size of the buffer

    public int pos_; // The position counter

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public byte[] data() {
        return data_;
    }

    public int length() {
        return len_;
    }

    public int rest_length() {
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
        StringBuffer dump = new StringBuffer(); 
        dump.append("Buffer pos="); 
        dump.append(pos_); 
        dump.append(" Buffer len="); 
        dump.append(len_); 
        dump.append(" Remaining buffer data=\n\n"); 
        
        dump.append(org.apache.yoko.orb.OB.IORUtil.dump_octets(data_, pos_, rest_length())); 
        return dump.toString(); 
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    public void alloc(int len) {
        max_ = len;
        len_ = len;
        try {
            data_ = new byte[max_];
        } catch (OutOfMemoryError ex) {
            throw new org.omg.CORBA.NO_MEMORY(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeNoMemory(org.apache.yoko.orb.OB.MinorCodes.MinorAllocationFailure),
                    org.apache.yoko.orb.OB.MinorCodes.MinorAllocationFailure,
                    org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        }
        pos_ = 0;
    }

    public void realloc(int len) {
        if (data_ == null)
            alloc(len);
        else {
            org.apache.yoko.orb.OB.Assert._OB_assert(len >= len_);
            if (len <= max_)
                len_ = len;
            else {
                int newMax = len > 2 * max_ ? len : 2 * max_;
                byte[] newData = null;
                try {
                    newData = new byte[newMax];
                } catch (OutOfMemoryError ex) {
                    throw new org.omg.CORBA.NO_MEMORY(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeNoMemory(org.apache.yoko.orb.OB.MinorCodes.MinorAllocationFailure),
                            org.apache.yoko.orb.OB.MinorCodes.MinorAllocationFailure,
                            org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
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

    public Buffer() {
    }

    public Buffer(byte[] data, int len) {
        data_ = data;
        len_ = len;
        max_ = len;
        pos_ = 0;
    }

    public Buffer(int len) {
        alloc(len);
    }
}

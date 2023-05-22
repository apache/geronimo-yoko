/*
 * Copyright 2021 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.io;

import org.apache.yoko.util.Assert;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_MEMORY;

import java.util.Arrays;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.apache.yoko.util.Exceptions.as;
import static org.apache.yoko.util.Hex.formatHexPara;
import static org.apache.yoko.util.MinorCodes.MinorAllocationFailure;
import static org.apache.yoko.util.MinorCodes.describeNoMemory;
import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;

/**
 * A growable buffer for holding data as a byte array.
 * This class serves as a factory for creating buffers for reading or writing.
 * @param <T> the concrete child class
 */
@SuppressWarnings("unchecked")
public abstract class Buffer<T extends Buffer> implements Cloneable {
    public static ReadBuffer createReadBuffer(byte[] data) { return new ReadBuffer(new Core(data)); }
    public static WriteBuffer createWriteBuffer() { return new WriteBuffer(new Core()); }
    public static WriteBuffer createWriteBuffer(int initialBufferSize) { return new WriteBuffer(new Core(initialBufferSize)); }

    /**
     * Holds the actual buffer data. Objects of this type are shared between potentially many read or write buffers.
     * This class and its members have package visibility so that the child classes can access them, but note that the
     * constructors are private. Instance objects should always be held in private fields to preserve encapsulation.
     */
    static final class Core {
        byte[] data; // The octet core
        int length; // The requested size of the core

        /**
         * Create a Core with initial length zero.
         */
        private Core() {
            // since we expect a write operation to follow, allocate a small core up front
            this(newBytes(16), 0);
        }

        private Core(byte[] data) {
            this(data, data.length);
        }

        /**
         * Create a Core with <code>len</code> bytes available for writing.
         */
        private Core(int len) {
            this(newBytes(len), len);
        }

        private Core(byte[] data, int len) {
            this.data = data;
            this.length = len;
        }

        /**
         * Extend the current core.
         * @param extra the number of additional bytes required beyond the end of the core.
         * @return <code>true</code> iff an existing core was insufficient
         */
        boolean growBy(int extra) {
            Assert.ensure(extra >= 0);
            length += extra;

            // the existing core might be big enough
            if (length <= data.length) {
                return false;
            }
            // ok, we need a bigger core
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

        boolean dataEquals(Core that) {
            if (this == that) return true;
            if (that == null) return false;
            if (this.length != that.length) return false;
            for (int i = 0; i < length; i++) if (this.data[i] != that.data[i]) return false;
            return true;
        }

        StringBuilder dumpTo(StringBuilder dump) {
            return formatHexPara(data, 0, length, dump);
        }

        @Override
        public String toString() {
            return new ReadBuffer(this).dumpAllDataWithPosition();
        }
    }

    final Core core;
    int position = 0;

    Buffer(Core core) { this.core = core; }

    public final boolean isComplete() { return position >= length(); }
    public final int getPosition() { return position; }
    public final int available() { return length() - position; }
    public final int length() { return core.length; }

    public final T clone() {
        try {
            return (T)super.clone();
        } catch (CloneNotSupportedException e) {
            throw as(INTERNAL::new, e, e.getMessage());
        }
    }

    public final boolean dataEquals(T that) {
        return this == that || that != null && this.core.dataEquals(that.core);
    }

    public final String dumpPosition() { return String.format("position=0x%x", position); }
    public final String dumpAllData() { return dumpAllData(new StringBuilder()).toString(); }
    public final StringBuilder dumpAllData(StringBuilder dump) { return core.dumpTo(dump); }

    public final T setPosition(int p) { position = p; return (T)this; }
    public final T rewind(int n) { position -= n; return (T)this;}
    public abstract ReadBuffer newReadBuffer();

    static byte[] copyOf(byte[] data, int length) {
        try {
            return Arrays.copyOf(data, length);
        } catch (OutOfMemoryError oom) {
            throw as(NO_MEMORY::new, oom, describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
        }
    }

    private static byte[] newBytes(int len) {
        try {
            // allocate only multiples of 16 so we can pad without checking
            return new byte[(len + 0xFF) & ~0xFF];
        } catch (OutOfMemoryError oom) {
            throw as(NO_MEMORY::new, oom, describeNoMemory(MinorAllocationFailure), MinorAllocationFailure, COMPLETED_MAYBE);
        }
    }
}

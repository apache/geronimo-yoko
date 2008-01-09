/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.util;

public class ByteBuffer {
    byte[] contents;

    int pos = 0;

    public ByteBuffer(int size) {
        contents = new byte[size];
    }

    public ByteBuffer() {
        this(48);
    }

    public void append(byte b) {
        ensure(1);

        contents[pos++] = b;
    }

    public void append(char c) {
        ensure(1);

        contents[pos++] = (byte) c;
    }

    static final char[] integerData = new char[] { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    public void append(int v, int radix) {

        if (v > radix) {
            append(v / radix, radix);
        }

        append(integerData[v % radix]);
    }

    public void append(int v) {
        append(v, 10);
    }

    public void append(String value) {
        int len = value.length();
        ensure(len);

        for (int i = 0; i < len; i++) {
            contents[pos++] = (byte) value.charAt(i);
        }
    }

    public void append(byte[] b) {
        append(b, 0, b.length);
    }

    public void append(ByteString b) {
        append(b.getData(), b.getOffset(), b.length());
    }

    public void append(byte[] b, int off, int len) {
        if (b.length == 0 || len == 0)
            return;
        ensure(len);
        System.arraycopy(b, off, contents, pos, len);
        pos += len;
    }

    public byte[] toByteArray() {
        byte[] barr = new byte[pos];
        System.arraycopy(contents, 0, barr, 0, pos);
        return barr;
    }

    public ByteString toByteString() {
        return new ByteString(contents, 0, pos);
    }

    public String toString() {
        return toByteString().toString();
    }

    private void ensure(int size) {
        if (pos + size > contents.length) {
            grow(pos + size);
        }
    }

    private void grow(int minimumSize) {
        int newSize = minimumSize > (contents.length * 2) ? minimumSize
                : (contents.length * 2);

        byte[] barr = new byte[newSize];
        System.arraycopy(contents, 0, barr, 0, contents.length);
        contents = barr;
    }

    public void writeTo(java.io.OutputStream out) throws java.io.IOException {
        writeFully(out, contents, 0, pos);
    }

    private void writeFully(java.io.OutputStream os, byte[] data, int off,
            int len) throws java.io.IOException {
        while (len > 0) {
            int bytes = len;
            try {
                os.write(data, off, len);
            } catch (java.io.InterruptedIOException ex) {
                bytes = ex.bytesTransferred;
            }

            off += bytes;
            len -= bytes;
        }
    }

    public byte[] getContents() {
        return contents;
    }

}

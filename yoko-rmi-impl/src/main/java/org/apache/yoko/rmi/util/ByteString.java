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

public class ByteString implements Comparable, Cloneable {
    private byte[] data;

    private final int pos;

    private final int len;

    private String string;

    private int hash;

    public ByteString(byte[] data, int pos, int len) {
        this.data = data;
        this.pos = pos;
        this.len = len;

        if (len < 0 || pos + len > data.length || pos < 0) {
            throw new IndexOutOfBoundsException("byte[" + data.length + "], "
                    + pos + ", " + len);
        }
    }

    public ByteString(byte[] data) {
        this(data, 0, data.length);
    }

    public ByteString(String value) {
        pos = 0;
        len = value.length();
        data = new byte[len];

        for (int i = 0; i < len; i++) {
            data[i] = (byte) value.charAt(i);
        }
    }

    public char charAt(int i) {
        return (char) byteAt(i);
    }

    public byte byteAt(int i) {
        if (i < 0 || i >= len)
            throw new IndexOutOfBoundsException("" + i);

        return data[pos + i];
    }

    public int length() {
        return len;
    }

    public Object clone() {
        ByteString result = new ByteString(data, pos, len);
        result.string = string;
        result.hash = hash;

        return result;
    }

    public int compareTo(Object o) {
        ByteString other = (ByteString) o;

        int min = (len < other.len) ? len : other.len;

        for (int i = 0; i < min; i++) {
            byte me = data[pos + i];
            byte him = other.data[other.pos + i];

            if (me < him)
                return -1;
            else if (me > him)
                return +1;
        }

        return (len - other.len);
    }

    public ByteString substring(int index) {
        if (index < 0 || index > len)
            throw new IndexOutOfBoundsException("" + index);
        return new ByteString(data, pos + index, len - index);
    }

    public ByteString substring(int index, int endindex) {
        if (index < 0 || endindex > len)
            throw new IndexOutOfBoundsException("" + index);

        ByteString result = new ByteString(data, pos + index, endindex - index);

        // System.out.println ("substring "+this+" ("+index+","+endindex+") =>
        // "+result);
        return result;
    }

    public int indexOf(char ch) {
        return indexOf(ch, 0);
    }

    public int indexOf(char ch, int index) {
        return indexOf((byte) ch, index);
    }

    public int indexOf(byte by, int index) {
        int start = pos + index;
        int end = start + len;

        if (index < 0 || index >= len)
            return -1;

        for (int i = start; i < end; i++) {
            if (data[i] == by)
                return (i - pos);
        }

        return -1;
    }

    public int lastIndexOf(char ch) {
        return lastIndexOf(ch, len - 1);
    }

    public int lastIndexOf(char ch, int index) {
        return lastIndexOf((byte) ch, index);
    }

    public int lastIndexOf(byte by, int index) {
        int start = pos + index;
        int end = pos;

        if (start >= pos + len)
            return -1;

        for (int i = start; i >= end; i--) {
            if (data[i] == by) {
                return (i - pos);
            }
        }

        return -1;
    }

    public boolean startsWith(ByteString prefix) {
        if (prefix.len > len)
            return false;

        for (int i = 0; i < prefix.len; i++) {
            if (byteAt(i) != prefix.byteAt(i))
                return false;
        }

        return true;
    }

    public boolean startsWithIgnoreCase(ByteString prefix) {
        if (prefix.len > len)
            return false;

        for (int i = 0; i < prefix.len; i++) {
            byte b1 = byteAt(i);
            byte b2 = prefix.byteAt(i);

            if (b1 == b2)
                continue;

            if (toLowerCase(b1) != toLowerCase(b2))
                return false;
        }

        return true;
    }

    public boolean startsWithIgnoreCase(String prefix) {
        if (prefix.length() > len)
            return false;

        for (int i = 0; i < prefix.length(); i++) {
            if (toLowerCase(byteAt(i)) != toLowerCase((byte) prefix.charAt(i)))
                return false;
        }

        return true;
    }

    public boolean endsWith(ByteString postfix) {
        if (postfix.len > len)
            return false;

        for (int i = 0; i < postfix.len; i++) {
            if (byteAt(i) != postfix.byteAt(i))
                return false;
        }

        return true;
    }

    public boolean startsWith(String prefix) {
        if (prefix.length() > len)
            return false;

        for (int i = 0; i < prefix.length(); i++) {
            if (charAt(i) != prefix.charAt(i))
                return false;
        }

        return true;
    }

    public boolean endsWith(String postfix) {
        if (postfix.length() > len)
            return false;

        for (int i = 0; i < postfix.length(); i++) {
            if (charAt(i) != postfix.charAt(i))
                return false;
        }

        return true;
    }

    private static byte toUpperCase(byte ch) {

        if (ch >= (byte) 'a' && ch <= (byte) 'z') {
            return (byte) ((int) ch - (int) 'a' + (int) 'A');

        } else if (ch >= (byte) 0xe0 && ch <= (byte) 0xfe && ch != (byte) 0xf7) {
            return (byte) ((int) ch - 0xe0 + 0xc0);

        } else {
            return ch;
        }

    }

    private static byte toLowerCase(byte ch) {

        if (ch >= (byte) 'A' && ch <= (byte) 'Z') {
            return (byte) ((int) ch - (int) 'A' + (int) 'a');

        } else if (ch >= (byte) 0xc0 && ch <= (byte) 0xde && ch != (byte) 0xd7) {
            return (byte) ((int) ch - 0xc0 + 0xe0);

        } else {
            return ch;
        }

    }

    public ByteString toUpperCase(boolean overwrite) {

        if (overwrite == false) {
            return toUpperCase();
        }

        for (int i = 0; i < len; i++) {
            data[pos + i] = toUpperCase(data[pos + i]);
        }

        return this;
    }

    public ByteString toUpperCase() {
        boolean didChange = false;
        byte[] up = null;

        for (int i = 0; i < len; i++) {
            byte ch = data[pos + i];

            byte newCh = toUpperCase(ch);

            if (ch != newCh) {

                if (didChange == false) {
                    up = new byte[len];
                    didChange = true;
                    for (int j = 0; j < i; j++) {
                        up[j] = data[pos + j];
                    }
                }

            }

            if (didChange)
                up[i] = newCh;

        }

        if (didChange) {
            return new ByteString(up, 0, len);
        } else {
            return this;
        }
    }

    public int hashCode() {
        int h = hash;
        if (h == 0) {
            int off = pos;
            byte val[] = data;

            for (int i = 0; i < len; i++)
                h = 31 * h + val[off++];
            hash = h;
        }
        return h;
    }

    public int lowerCaseHashCode() {
        int h = 0;

        int off = pos;
        byte val[] = data;

        for (int i = 0; i < len; i++)
            h = 31 * h + toLowerCase(val[off++]);
        hash = h;

        return h;
    }

    public boolean equals(ByteString bs) {
        if (bs == null)
            return false;

        if (len != bs.len)
            return false;

        for (int i = 0; i < len; i++) {
            if (data[pos + i] != bs.data[bs.pos + i])
                return false;
        }

        return true;
    }

    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (!(other instanceof ByteString)) {
            if (other instanceof String) {
                return toString().equals(other);
            } else {
                return false;
            }
        }

        return equals((ByteString) other);
    }

    public boolean equalsIgnoreCase(Object other) {
        if (other instanceof ByteString)
            return equalsIgnoreCase((ByteString) other);

        if (this == other)
            return true;

        if (other == null)
            return false;

        if (other instanceof String) {
            return equalsIgnoreCase((String) other);
        } else {
            return false;
        }
    }

    public boolean equalsIgnoreCase(ByteString bs) {
        if (bs == null)
            return false;

        if (len != bs.len)
            return false;

        for (int i = 0; i < len; i++) {
            if (toLowerCase(data[pos + i]) != toLowerCase(bs.data[bs.pos + i]))
                return false;
        }

        return true;
    }

    public boolean equalsIgnoreCase(String s) {
        if (len != s.length())
            return false;

        for (int i = 0; i < len; i++) {
            char c1 = (char) toLowerCase(byteAt(i));
            char c2 = s.charAt(i);

            if (c1 == c2)
                continue;

            if (c1 != Character.toLowerCase(c2))
                return false;
        }

        return true;
    }

    public static boolean isSpace(byte value) {
        return value == ' ' || value == '\t' || value == '\n' || value == '\r';
    }

    public ByteString trim() {
        int newStart = pos;
        int newEnd = pos + len;

        while (newStart < newEnd && isSpace(data[newStart]))
            newStart++;

        while (newStart < newEnd && isSpace(data[newEnd - 1]))
            newEnd--;

        int newLen = (newEnd - newStart);
        if (newLen == len)
            return this;
        else
            return new ByteString(data, newStart, newLen);
    }

    public String toString() {
        if (string == null) {
            char[] chars = new char[len];

            for (int i = 0; i < len; i++) {
                chars[i] = (char) data[pos + i];
            }

            string = new String(chars);
        }

        return string;
    }

    public ByteString[] split(char c) {
        java.util.ArrayList list = new java.util.ArrayList();
        int start = 0;
        for (int pos = 0; pos < len; pos++) {
            if (charAt(pos) == c) {
                list.add(substring(start, pos));
                start = pos + 1;
            }
        }

        if (start == 0) {
            return new ByteString[] { this };

        } else {
            list.add(substring(start, len));

            ByteString[] bsa = new ByteString[list.size()];
            list.toArray(bsa);
            return bsa;
        }
    }

    public void copyInto(byte[] data, int off) {
        for (int i = 0; i < len; i++) {
            data[i + off] = byteAt(i);
        }
    }

    byte[] getData() {
        return data;
    }

    protected void setData(byte[] data) {
        this.data = data;
    }

    int getOffset() {
        return pos;
    }

    public int parseInt(int start) {
        int off = this.pos + start;
        int pos = off;
        int value = 0;

        // skip blanks...
        while (data[pos] == (byte) ' ' || data[pos] == (byte) '\t')
            pos += 1;

        while (pos < off + len) {
            byte ch = data[pos++];
            if (ch >= (byte) '0' && ch <= (byte) '9') {
                int digit = (int) ch - (int) '0';

                value = (value * 10) + digit;

            } else {
                break;
            }
        }

        return value;

    }

}

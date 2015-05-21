/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.yoko.rmi.impl;


import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ValueInputStream;

import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.util.Map;

public class CorbaObjectReader extends ObjectReaderBase {
    final org.omg.CORBA_2_3.portable.InputStream in;

    private final Map<Integer, Object> offsetMap;

    CorbaObjectReader(InputStream in, Map<Integer, Object> offsetMap, Serializable obj) throws IOException {
        super(obj);
        this.in = (org.omg.CORBA_2_3.portable.InputStream) in;
        this.offsetMap = offsetMap;
    }

    public void readFully(byte[] arr, int off, int val) throws IOException {
        in.read_octet_array(arr, off, val);
    }

    public int skipBytes(int len) throws IOException {
        final byte[] data = new byte[len];
        readFully(data, 0, len);
        return len;
    }

    public boolean readBoolean() throws IOException {
        return in.read_boolean();
    }

    public byte readByte() throws IOException {
        return in.read_octet();
    }

    public int readUnsignedByte() throws IOException {
        final int val = in.read_octet();
        return val & 0xff;
    }

    public short readShort() throws IOException {
        return in.read_short();
    }

    public int readUnsignedShort() throws IOException {
        final int val = in.read_short();
        return val & 0xffff;
    }

    public char readChar() throws IOException {
        return in.read_wchar();
    }

    public int readInt() throws IOException {
        return in.read_long();
    }

    public long readLong() throws IOException {
        return in.read_longlong();
    }

    public float readFloat() throws IOException {
        return in.read_float();
    }

    public double readDouble() throws IOException {
        return in.read_double();
    }

    @Deprecated
    public String readLine() throws IOException {
        final StringBuilder buf = new StringBuilder();

        char ch;

        try {
            ch = (char) readUnsignedByte();
        } catch (MARSHAL ex) {
            return null;
        }

        do {
            buf.append(ch);

            try {
                ch = (char) readUnsignedByte();
            } catch (MARSHAL ex) {
                // reached EOF
                return buf.toString();
            }

            if (ch == '\n') {
                return buf.toString();
            }

            if (ch == '\r') {
                final char ch2;
                try {
                    ch2 = (char) readUnsignedByte();
                } catch (MARSHAL ex) {
                    // reached EOF
                    return buf.toString();
                }

                if (ch2 == '\n') {
                    return buf.toString();
                } else {
                    ch = ch2;
                }
            }
        } while (true);
    }

    public String readUTF() throws IOException {
        return in.read_wstring();
    }

    public Object readAbstractObject() throws IndirectionException {
        try {
            return in.read_abstract_interface();
        } catch (IndirectionException ex) {
            return offsetMap.get(ex.offset);
        }
    }

    public Object readAny() throws IndirectionException {
        try {
            return Util.readAny(in);
        } catch (IndirectionException ex) {
            return offsetMap.get(ex.offset);
        }
    }

    public Object readValueObject() throws IndirectionException {
        try {
            return in.read_value();
        } catch (IndirectionException ex) {
            return offsetMap.get(ex.offset);
        }
    }

    public Object readValueObject(Class<?> clz)
            throws IndirectionException {
        try {
            return in.read_value(clz);
        } catch (IndirectionException ex) {
            return offsetMap.get(ex.offset);
        }
    }

    public org.omg.CORBA.Object readCorbaObject(Class<?> type) {
        return in.read_Object();
    }

    public Remote readRemoteObject(Class<?> type) {
        final org.omg.CORBA.Object objref = in.read_Object();
        return (Remote) PortableRemoteObject.narrow(objref, type);
    }

    public int read() throws IOException {
        return readUnsignedByte();
    }

    public int read(byte[] arr) throws IOException {
        return read(arr, 0, arr.length);
    }

    public int read(byte[] arr, int off, int len) throws IOException {
        readFully(arr, off, len);
        return len;
    }

    public long skip(long len) throws IOException {
        skipBytes((int) len);
        return len;
    }

    public int available() throws IOException {
        return in.available();
    }

    @Override
    protected void _startValue() {
        ((ValueInputStream)in).start_value();
    }

    @Override
    protected void _endValue() {
        ((ValueInputStream)in).end_value();
    }
}

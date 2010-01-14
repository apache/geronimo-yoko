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

package org.apache.yoko.rmi.impl;


public class CorbaObjectReader extends ObjectReader {
    final org.omg.CORBA_2_3.portable.InputStream in;

    final java.util.Map offsetMap;

    CorbaObjectReader(org.omg.CORBA.portable.InputStream in,
            java.util.Map offsetMap, java.io.Serializable obj)
            throws java.io.IOException {
        super(obj);

        this.in = (org.omg.CORBA_2_3.portable.InputStream) in;
        this.offsetMap = offsetMap;
    }

    public void readFully(byte[] arr, int off, int val)
            throws java.io.IOException {
        in.read_octet_array(arr, off, val);
    }

    public int skipBytes(int len) throws java.io.IOException {
        byte[] data = new byte[len];
        readFully(data, 0, len);
        return len;
    }

    public boolean readBoolean() throws java.io.IOException {
        return in.read_boolean();
    }

    public byte readByte() throws java.io.IOException {
        return in.read_octet();
    }

    public int readUnsignedByte() throws java.io.IOException {
        int val = in.read_octet();
        return val & 0xff;
    }

    public short readShort() throws java.io.IOException {
        return in.read_short();
    }

    public int readUnsignedShort() throws java.io.IOException {
        int val = in.read_short();
        return val & 0xffff;
    }

    public char readChar() throws java.io.IOException {
        return in.read_wchar();
    }

    public int readInt() throws java.io.IOException {
        return in.read_long();
    }

    public long readLong() throws java.io.IOException {
        return in.read_longlong();
    }

    public float readFloat() throws java.io.IOException {
        return in.read_float();
    }

    public double readDouble() throws java.io.IOException {
        return in.read_double();
    }

    /** @deprecated */
    public java.lang.String readLine() throws java.io.IOException {
        StringBuffer buf = new StringBuffer();

        char ch;

        try {
            ch = (char) readUnsignedByte();
        } catch (org.omg.CORBA.MARSHAL ex) {
            return null;
        }

        do {
            buf.append(ch);

            try {
                ch = (char) readUnsignedByte();
            } catch (org.omg.CORBA.MARSHAL ex) {
                // reached EOF
                return buf.toString();
            }

            if (ch == '\n')
                return buf.toString();

            if (ch == '\r') {
                char ch2;
                try {
                    ch2 = (char) readUnsignedByte();
                } catch (org.omg.CORBA.MARSHAL ex) {
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

    public java.lang.String readUTF() throws java.io.IOException {
        return in.read_wstring();
    }

    public Object readAbstractObject()
            throws org.omg.CORBA.portable.IndirectionException {
        try {
            return in.read_abstract_interface();
        } catch (org.omg.CORBA.portable.IndirectionException ex) {
            return offsetMap.get(new Integer(ex.offset));
        }
    }

    public Object readAny() throws org.omg.CORBA.portable.IndirectionException {
        try {
            return javax.rmi.CORBA.Util.readAny(in);
        } catch (org.omg.CORBA.portable.IndirectionException ex) {
            return offsetMap.get(new Integer(ex.offset));
        }
    }

    public Object readValueObject()
            throws org.omg.CORBA.portable.IndirectionException {
        try {
            return in.read_value();
        } catch (org.omg.CORBA.portable.IndirectionException ex) {
            return offsetMap.get(new Integer(ex.offset));
        }
    }

    public Object readValueObject(Class clz)
            throws org.omg.CORBA.portable.IndirectionException {
        try {
            return in.read_value(clz);
        } catch (org.omg.CORBA.portable.IndirectionException ex) {
            return offsetMap.get(new Integer(ex.offset));
        }
    }
    
    public org.omg.CORBA.Object readCorbaObject(Class type) {
	org.omg.CORBA.Object objref = in.read_Object();
	//objref = (org.omg.CORBA.Object) PortableRemoteObject.narrow(objref, type);
	return objref;
    }

    public java.rmi.Remote readRemoteObject(Class type) {
        org.omg.CORBA.Object objref = in.read_Object();
        return (java.rmi.Remote) javax.rmi.PortableRemoteObject.narrow(objref,
                type);
    }

    public int read() throws java.io.IOException {
        return readUnsignedByte();
    }

    public int read(byte[] arr) throws java.io.IOException {
        return read(arr, 0, arr.length);
    }

    public int read(byte[] arr, int off, int len) throws java.io.IOException {
        readFully(arr, off, len);
        return len;
    }

    public long skip(long len) throws java.io.IOException {
        skipBytes((int) len);
        return len;
    }

    public int available() throws java.io.IOException {
        return in.available();
    }

}

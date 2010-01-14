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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.logging.Logger;

import org.omg.CORBA.MARSHAL;

public final class CopyState {

    static Logger logger = Logger.getLogger(CopyState.class.getName());

    private IdentityHashMap copied;

    private IdentityHashMap recursionResolverMap;

    private TypeRepository rep;

    private static Object recursionCheck = new Object();

    public CopyState(TypeRepository rep) {
        this.rep = rep;
        this.copied = new IdentityHashMap();
        this.recursionResolverMap = new IdentityHashMap();
    }

    public void registerRecursion(CopyRecursionResolver resolver) {
        Object key = resolver.orig;
        CopyRecursionResolver orig = (CopyRecursionResolver) recursionResolverMap
                .get(key);
        resolver.next = orig;
        recursionResolverMap.put(key, resolver);

        logger.fine("registering recursion resolver " + resolver + " for "
                + key.getClass() + "@" + System.identityHashCode(key));

    }

    public void put(Object orig, Object copy) {
        Object old = copied.put(orig, copy);
        if (old == recursionCheck) {
            for (CopyRecursionResolver resolver = (CopyRecursionResolver) recursionResolverMap
                    .get(orig); resolver != null; resolver = resolver.next) {

                logger
                        .fine("invoking " + resolver + " for "
                                + orig.getClass() + "@"
                                + System.identityHashCode(orig) + " ===> "
                                + copy.getClass() + "@"
                                + System.identityHashCode(copy));

                resolver.resolve(copy);
            }
        }
    }

    private String spaces(int c) {
        StringBuffer sb = new StringBuffer();
        while (c-- != 0) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private int idx;

    public Object copy(Object orig) throws CopyRecursionException {
        if (orig == null)
            return orig;

        Object copy = copied.get(orig);
        if (copy != null) {
            if (copy == recursionCheck) {
                logger.fine("throwign CopyRecursion for " + orig.getClass()
                        + "@" + System.identityHashCode(orig));

                throw new CopyRecursionException(this, orig);
            }
            return copy;
        }

        Class origClass = orig.getClass();

        logger.fine("[" + hashCode() + "]" + spaces(idx++)
                + "copying instance of " + origClass);

        TypeDescriptor desc = rep.getDescriptor(origClass);

        copied.put(orig, recursionCheck);
        copy = desc.copyObject(orig, this);
        copied.put(orig, copy);

        logger.fine(spaces(--idx) + "=> " + copy);

        return copy;
    }

    public ObjectWriter createObjectWriter(final java.io.Serializable obj) {
        try {
            return (ObjectWriter) java.security.AccessController
                    .doPrivileged(new java.security.PrivilegedExceptionAction() {
                        public Object run() throws IOException {
                            return new Writer(obj);
                        }
                    });

        } catch (java.security.PrivilegedActionException e) {
            throw (InternalError)new InternalError(e.getMessage()).initCause(e);
        }
    }

    class Writer extends ObjectWriter {
        List contents = new ArrayList();

        private void enqueue(Object o) {
            contents.add(o);
        }

        Writer(java.io.Serializable obj) throws java.io.IOException {
            super(obj);
        }

        public ObjectReader getObjectReader(Object val) {
            try {
                // register mapping from old value to new
                put(object, val);
                return new Reader((Serializable) val, contents);
            } catch (IOException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
            }

        }

        public void write(int val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Byte((byte) val));
        }

        public void write(byte[] val) throws java.io.IOException {
            write(val, 0, val.length);
        }

        public void write(byte[] arr, int off, int len)
                throws java.io.IOException {
            if (arr == null || arr.length == 0) {
                beforeWriteData();
                enqueue(null);
            } else if (off == 0 && len == arr.length) {
                beforeWriteData();
                enqueue(arr);
            } else {
                byte[] data = new byte[len];
                System.arraycopy(arr, off, data, 0, len);
                beforeWriteData();
                enqueue(data);
            }
        }

        public void writeBoolean(boolean val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Boolean(val));
        }

        public void writeByte(int val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Byte((byte) val));
        }

        public void writeShort(int val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Short((short) val));
        }

        public void writeChar(int val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Character((char) val));
        }

        public void writeInt(int val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Integer(val));
        }

        public void writeLong(long val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Long(val));
        }

        public void writeFloat(float val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Float(val));
        }

        public void writeDouble(double val) throws java.io.IOException {
            beforeWriteData();
            enqueue(new Double(val));
        }

        public void writeBytes(java.lang.String val) throws java.io.IOException {
            beforeWriteData();
            byte[] data = val.getBytes();
            enqueue(data);
        }

        public void writeChars(java.lang.String val) throws java.io.IOException {
            beforeWriteData();
            char[] data = val.toCharArray();
            enqueue(data);
        }

        public void writeUTF(java.lang.String val) throws java.io.IOException {
            beforeWriteData();
            enqueue(val);
        }

        public void writeObjectOverride(final Object obj) {
            Object copy;

            try {
                copy = copy(obj);
            } catch (CopyRecursionException rec) {
                copy = recursionCheck;

                // save position of newly added element
                final int idx = contents.size();

                registerRecursion(new CopyRecursionResolver(obj) {
                    public void resolve(Object newValue) {
                        contents.set(idx, newValue);
                    }
                });
            }

            enqueue(copy);
        }

        public void writeValueObject(Object obj) {
            writeObjectOverride(obj);
        }
        
        public void writeCorbaObject(Object obj) {
            writeObjectOverride(obj);
        }

        public void writeRemoteObject(Object obj) {
            writeObjectOverride(obj);
        }

        public void writeAny(Object obj) {
            writeObjectOverride(obj);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.yoko.rmi.impl.ObjectWriter#_startValue(java.lang.String)
         */
        protected void _startValue(String rep_id) throws IOException {
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.yoko.rmi.impl.ObjectWriter#_endValue()
         */
        protected void _endValue() throws IOException {
        }

        protected void _nullValue() throws IOException {
        }

    }

    public class Reader extends ObjectReader {

        int cpos;

        List contents;

        private Object dequeue() {
            Object result = contents.get(cpos++);

            if (result == recursionCheck) {
                throw new IllegalStateException("recursion was not resolved?");
            } else {
                return result;
            }
        }

        private void undequeue(Object o) {
            contents.set(--cpos, o);
        }

        Reader(java.io.Serializable obj, List data) throws IOException {
            super(obj);
            contents = data;
            cpos = 0;
        }

        public Object readAbstractObject() {
            return dequeue();
        }

        public Object readAny() {
            return dequeue();
        }

        public Object readValueObject() {
            return dequeue();
        }

        public Object readValueObject(Class clz) {
            return dequeue();
        }
        
        public org.omg.CORBA.Object readCorbaObject(Class type) {
            return (org.omg.CORBA.Object) dequeue();
        }

        public java.rmi.Remote readRemoteObject(Class type) {
            return (java.rmi.Remote) dequeue();
        }

        public void readFully(byte[] arr, int off, int val)
                throws java.io.IOException {
            while (val > 0) {
                int bytes = read(arr, off, val);
                off += bytes;
                val -= bytes;
            }
        }

        public int read(byte[] arr, int off, int len)
                throws java.io.IOException {
            Object obj = dequeue();

            if (obj instanceof byte[]) {
                byte[] data = (byte[]) obj;
                if (data.length <= len) {
                    System.arraycopy(data, 0, arr, off, data.length);
                    return data.length;
                } else {
                    // copy only portion of the data
                    System.arraycopy(data, 0, arr, off, len);
                    byte[] newdata = new byte[data.length - len];
                    System.arraycopy(data, len, newdata, 0, data.length - len);
                    undequeue(newdata);
                    return len;
                }
            } else if (obj instanceof Byte) {
                Byte b = (Byte) obj;
                arr[off] = b.byteValue();
                return 1;
            } else {
                throw new IOException("stream contents is not byte[], it is: "
                        + obj.getClass().getName());
            }
        }

        public int skipBytes(int len) throws java.io.IOException {
            byte[] data = new byte[len];
            readFully(data);
            return len;
        }

        public boolean readBoolean() throws java.io.IOException {
            try {
                return ((Boolean) dequeue()).booleanValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        public byte readByte() throws java.io.IOException {
            try {
                return ((Byte) dequeue()).byteValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        public short readShort() throws java.io.IOException {
            try {
                return ((Short) dequeue()).shortValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        public char readChar() throws java.io.IOException {
            try {
                return ((Character) dequeue()).charValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        public int readInt() throws java.io.IOException {
            try {
                return ((Integer) dequeue()).intValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        public long readLong() throws java.io.IOException {
            try {
                return ((Long) dequeue()).longValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        public float readFloat() throws java.io.IOException {
            try {
                return ((Float) dequeue()).floatValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        public double readDouble() throws java.io.IOException {
            try {
                return ((Double) dequeue()).doubleValue();
            } catch (ClassCastException ex) {
                IOException iox = new IOException(ex.getMessage());
                iox.initCause(ex);
                throw iox;
            }
        }

        /** @deprecated */
        public java.lang.String readLine() throws java.io.IOException {
            throw new InternalError("cannot use readline");
        }

        public java.lang.String readUTF() throws java.io.IOException {
            return (String) dequeue();
        }

    }

}

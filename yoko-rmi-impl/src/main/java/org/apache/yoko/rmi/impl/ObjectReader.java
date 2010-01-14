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

import java.io.Externalizable;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.NotActiveException;
import java.io.ObjectInputValidation;
import java.io.ObjectStreamClass;
import java.util.Map;

import org.apache.yoko.rmi.util.PriorityQueue;


public abstract class ObjectReader extends java.io.ObjectInputStream {
    int recursionDepth = 0;

    final java.io.Serializable object;

    PriorityQueue callbacks;

    ObjectReader(java.io.Serializable obj) throws java.io.IOException {
        super();
        this.object = obj;
    }

    private ValueDescriptor desc;

    private byte streamFormatVersion;

    final void setCurrentValueDescriptor(ValueDescriptor desc) {
        this.desc = desc;
    }

    public final void defaultReadObject() throws java.io.IOException,
            ClassNotFoundException, java.io.NotActiveException {
        if (desc == null) {
            throw new java.io.NotActiveException();
        }
        desc.defaultReadValue(this, object);
    }

    protected final java.lang.Object readObjectOverride()
            throws java.lang.ClassNotFoundException, java.io.IOException {
        try {
            enterRecursion();
            return readAbstractObject();
        } finally {
            exitRecursion();
        }
    }

    protected void enterRecursion() {
        recursionDepth += 1;
    }

    protected void exitRecursion() throws InvalidObjectException {
        recursionDepth -= 1;

        if (recursionDepth == 0) {
            if (callbacks != null) {
                while (callbacks.size() != 0) {
                    Validation val = (Validation) callbacks.dequeue();
                    val.validate();
                }
            }
        }
    }

    private class Validation implements Comparable {
        ObjectInputValidation validator;

        int pri;

        Validation(ObjectInputValidation val, int pri) {
            this.validator = val;
            this.pri = pri;
        }

        public int compareTo(Object other) {
            Validation o = (Validation) other;

            return pri - o.pri;
        }

        void validate() throws InvalidObjectException {
            validator.validateObject();
        }
    }

    public synchronized void registerValidation(ObjectInputValidation obj,
            int prio) throws NotActiveException, InvalidObjectException {
        if (recursionDepth == 0) {
            throw new NotActiveException("readObject not Active");
        }

        if (obj == null) {
            throw new InvalidObjectException(
                    "Null is not a valid callback object");
        }

        Validation val = new Validation(obj, prio);

        if (callbacks == null) {
            callbacks = new PriorityQueue();
        }

        callbacks.enqueue(val);
    }

    public abstract Object readAbstractObject()
            throws org.omg.CORBA.portable.IndirectionException;

    public abstract Object readAny()
            throws org.omg.CORBA.portable.IndirectionException;

    public abstract Object readValueObject()
            throws org.omg.CORBA.portable.IndirectionException;

    public abstract Object readValueObject(Class clz)
            throws org.omg.CORBA.portable.IndirectionException;

    public abstract org.omg.CORBA.Object readCorbaObject(Class type);
    
    public abstract java.rmi.Remote readRemoteObject(Class type);
    
    public void close() throws java.io.IOException {
        // skip //
    }

    public java.io.ObjectInputStream.GetField readFields()
            throws java.io.IOException {
        if (desc == null) {
            throw new java.io.NotActiveException();
        }

        Map fieldMap = desc.readFields(this);

        return new GetFieldImpl(fieldMap);
    }

    public void readFully(byte[] arr) throws java.io.IOException {
        readFully(arr, 0, arr.length);
    }

    public int readUnsignedByte() throws java.io.IOException {
        return ((int) readByte()) & 0xff;
    }

    public int readUnsignedShort() throws java.io.IOException {
        int val = readShort();
        return val & 0xffff;
    }

    static class GetFieldImpl extends GetField {

        Map fieldMap;

        GetFieldImpl(java.util.Map map) {
            fieldMap = map;
        }

        /**
         * @see java.io.ObjectInputStream.GetField#defaulted(String)
         */
        public boolean defaulted(String name) throws IOException {
            return !fieldMap.containsKey(name);
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, boolean)
         */
        public boolean get(String name, boolean val) throws IOException {
            Boolean value = (Boolean) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.booleanValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, byte)
         */
        public byte get(String name, byte val) throws IOException {
            Byte value = (Byte) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.byteValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, char)
         */
        public char get(String name, char val) throws IOException {
            Character value = (Character) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.charValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, double)
         */
        public double get(String name, double val) throws IOException {
            Double value = (Double) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.doubleValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, float)
         */
        public float get(String name, float val) throws IOException {
            Float value = (Float) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.floatValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, int)
         */
        public int get(String name, int val) throws IOException {
            Integer value = (Integer) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.intValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, long)
         */
        public long get(String name, long val) throws IOException {
            Long value = (Long) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.longValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, Object)
         */
        public Object get(String name, Object val) throws IOException {
            Object value = (Object) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value;
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#get(String, short)
         */
        public short get(String name, short val) throws IOException {
            Short value = (Short) fieldMap.get(name);
            if (defaulted(name)) {
                return val;
            } else {
                return value.shortValue();
            }
        }

        /**
         * @see java.io.ObjectInputStream.GetField#getObjectStreamClass()
         */
        public ObjectStreamClass getObjectStreamClass() {
            return null;
        }

    }

    /**
     * @param ext
     */
    void readExternal(Externalizable ext) throws IOException,
            ClassNotFoundException {
        byte old = streamFormatVersion;
        try {
            streamFormatVersion = readByte();
            ext.readExternal(this);
        } finally {
            streamFormatVersion = old;
        }
    }

}

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
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

abstract class ObjectWriter extends java.io.ObjectOutputStream {
    protected final java.io.Serializable object;

    private ValueDescriptor _desc;

    private PutFieldImpl _putFields;

    private WriteObjectState state = NOT_IN_WRITE_OBJECT;

    private byte streamFormatVersion = 1;

    static class WriteObjectState {
        void beforeWriteObject(ObjectWriter writer) throws IOException {
        }

        void afterWriteObject(ObjectWriter writer) throws IOException {
        }

        void beforeDefaultWriteObject(ObjectWriter writer) throws IOException {
        }

        void afterDefaultWriteObject(ObjectWriter writer) throws IOException {
        }

        void beforeWriteData(ObjectWriter writer) throws IOException {
        }
    }

    static WriteObjectState NOT_IN_WRITE_OBJECT = new WriteObjectState() {
        void beforeWriteObject(ObjectWriter writer) throws IOException {
            writer.state = IN_WRITE_OBJECT;
        }

        void afterWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void beforeDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void afterDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }
    };

    static WriteObjectState IN_WRITE_OBJECT = new WriteObjectState() {
        void beforeWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("already in writeObject");
        }

        void afterWriteObject(ObjectWriter writer) throws IOException {

            writer.state = NOT_IN_WRITE_OBJECT;

            // there is no custom (nor default) state written
            writer.writeBoolean(false);

            // If we're in stream format verison 2, we must
            // put the "null" marker to say that there isn't
            // any optional data
            if (writer.getStreamFormatVersion() == 2) {
                writer._nullValue();
            }

        }

        void beforeDefaultWriteObject(ObjectWriter writer) throws IOException {
            writer.state = IN_DEFAULT_WRITE_OBJECT;
            writer.writeBoolean(true);
        }

        void afterDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.yoko.rmi.impl.ObjectWriter.WriteObjectState#writeData(org.apache.yoko.rmi.impl.ObjectWriter)
         */
        void beforeWriteData(ObjectWriter writer) throws IOException {
            writer.state = WROTE_CUSTOM_DATA;

            // writeDefaultObject was not invoked
            writer.writeBoolean(false);

            if (writer.getStreamFormatVersion() == 2) {
                writer._startValue(writer._desc.getRepositoryID());
            }

        }
    };

    static WriteObjectState IN_DEFAULT_WRITE_OBJECT = new WriteObjectState() {

        void beforeWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void afterWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void beforeDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void afterDefaultWriteObject(ObjectWriter writer) throws IOException {
            writer.state = WROTE_DEFAULT_DATA;
        }

        void beforeWriteData(ObjectWriter writer) throws IOException {
            // do nothing
        }
    };

    static WriteObjectState WROTE_DEFAULT_DATA = new WriteObjectState() {

        void afterWriteObject(ObjectWriter writer) throws IOException {

            // the boolean identifying that default data was written has already
            // been emitted in IN_WRITE_OBJECT.beforeWriteDefaultObject

            writer.state = NOT_IN_WRITE_OBJECT;

            // write a null-marker to identify that there is no custom
            // state being marshalled...
            if (writer.getStreamFormatVersion() == 2) {
                writer._nullValue();
            }

        }

        void beforeDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void afterDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void beforeWriteData(ObjectWriter writer) throws IOException {

            // the boolean identifying that default data was written has already
            // been emitted in IN_WRITE_OBJECT.beforeWriteDefaultObject

            writer.state = WROTE_CUSTOM_DATA;

            if (writer.getStreamFormatVersion() == 2) {
                writer._startValue(writer._desc.getRepositoryID());
            }

        }
    };

    static WriteObjectState WROTE_CUSTOM_DATA = new WriteObjectState() {
        /*
         * (non-Javadoc)
         * 
         * @see org.apache.yoko.rmi.impl.ObjectWriter.WriteObjectState#afterWriteObject(org.apache.yoko.rmi.impl.ObjectWriter)
         */
        void afterWriteObject(ObjectWriter writer) throws IOException {
            if (writer.getStreamFormatVersion() == 2) {
                writer._endValue();
            }

            writer.state = NOT_IN_WRITE_OBJECT;
        }

        void beforeDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException("");
        }

        void afterDefaultWriteObject(ObjectWriter writer) {
            throw new IllegalStateException("");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.apache.yoko.rmi.impl.ObjectWriter.WriteObjectState#writeData(org.apache.yoko.rmi.impl.ObjectWriter)
         */
        void beforeWriteData(ObjectWriter writer) throws IOException {
            // do nothing
        }
    };

    protected void beforeWriteData() throws IOException {
        state.beforeWriteData(this);
    }

    ObjectWriter(java.io.Serializable obj) throws java.io.IOException {
        object = obj;
    }

    /**
     * @return
     */
    protected byte getStreamFormatVersion() {
        return streamFormatVersion;
    }

    abstract ObjectReader getObjectReader(Object newObject);

    void setCurrentValueDescriptor(ValueDescriptor desc) {
        _desc = desc;
        _putFields = null;
    }

    public final void defaultWriteObject() throws java.io.IOException,
            java.io.NotActiveException {
        if (_desc == null) {
            throw new java.io.NotActiveException();
        }

        state.beforeDefaultWriteObject(this);
        try {
            _desc.defaultWriteValue(this, object);
        } finally {
            state.afterDefaultWriteObject(this);
        }
    }

    public java.io.ObjectOutputStream.PutField putFields()
            throws java.io.IOException {
        if (_desc == null) {
            throw new java.io.NotActiveException();
        }

        _putFields = new PutFieldImpl(_desc);

        return _putFields;
    }

    public void writeFields() throws IOException {
        if (_putFields == null) {
            throw new java.io.NotActiveException("no current PutFields");
        }

        if (_putFields.slice != _desc) {
            throw new java.io.NotActiveException(
                    "PutField cannot survive writeObject invocation");
        }

        state.beforeDefaultWriteObject(this);
        try {
            _putFields.write(this);
        } finally {
            state.afterDefaultWriteObject(this);
        }
    }

    public void close() { /* skip */
    }

    public void flush() { /* skip */
    }

    public abstract void writeValueObject(Object obj) throws IOException;

    public abstract void writeRemoteObject(Object obj) throws IOException;
    
    public abstract void writeCorbaObject(Object obj) throws IOException;

    public abstract void writeAny(Object obj) throws IOException;
    
    static class PutFieldImpl extends PutField {
        ValueDescriptor slice;

        java.util.Map valueMap = new HashMap();

        PutFieldImpl(ValueDescriptor slice) {
            this.slice = slice;
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, boolean)
         */
        public void put(String name, boolean val) {
            valueMap.put(name, new Boolean(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, byte)
         */
        public void put(String name, byte val) {
            valueMap.put(name, new Byte(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, char)
         */
        public void put(String name, char val) {
            valueMap.put(name, new Character(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, double)
         */
        public void put(String name, double val) {
            valueMap.put(name, new Double(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, float)
         */
        public void put(String name, float val) {
            valueMap.put(name, new Float(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, int)
         */
        public void put(String name, int val) {
            valueMap.put(name, new Integer(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, long)
         */
        public void put(String name, long val) {
            valueMap.put(name, new Long(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, Object)
         */
        public void put(String name, Object val) {
            valueMap.put(name, val);
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#put(String, short)
         */
        public void put(String name, short val) {
            valueMap.put(name, new Short(val));
        }

        /**
         * @see java.io.ObjectOutputStream.PutField#write(ObjectOutput)
         * @deprecated
         */
        public void write(ObjectOutput out) throws IOException {
            slice.writeFields((ObjectWriter) out, valueMap);
        }

    }

    /**
     * @param descriptor
     * @param val
     * @param _write_object_method
     */
    void invokeWriteObject(ValueDescriptor descriptor, Serializable val,
            Method _write_object_method) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, IOException {
        ValueDescriptor desc = _desc;
        WriteObjectState old_state = state;
        state = NOT_IN_WRITE_OBJECT;
        try {
            setCurrentValueDescriptor(descriptor);
            writeByte(getStreamFormatVersion());
            state.beforeWriteObject(this);
            _write_object_method.invoke(val, new Object[] { this });
            state.afterWriteObject(this);
        } finally {
            state = old_state;
            setCurrentValueDescriptor(desc);
        }
    }

    protected abstract void _startValue(String rep_id) throws IOException;

    protected abstract void _endValue() throws IOException;

    protected abstract void _nullValue() throws IOException;

    /**
     * @param externalizable
     */
    void invokeWriteExternal(Externalizable externalizable) throws IOException {
        writeByte(getStreamFormatVersion());
        externalizable.writeExternal(this);
    }
}

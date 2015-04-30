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
import java.io.NotActiveException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.yoko.util.cmsf.CmsfThreadLocal;

abstract class ObjectWriter extends ObjectOutputStream {
    protected final Serializable object;

    private ValueDescriptor _desc;

    private PutFieldImpl _putFields;

    private WriteObjectState state = WriteObjectState.NOT_IN_WRITE_OBJECT;

    private byte streamFormatVersion = 1;

    private enum WriteObjectState {
        NOT_IN_WRITE_OBJECT {
            void beforeWriteObject(ObjectWriter writer) {
                writer.state = IN_WRITE_OBJECT;
            }
        },
        IN_WRITE_OBJECT {
            void afterWriteObject(ObjectWriter writer) throws IOException {
                writer.state = NOT_IN_WRITE_OBJECT;

                // there is no custom (nor default) state written
                writer.writeBoolean(false);

                // If we're in stream format version 2, we must
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

            void beforeWriteData(ObjectWriter writer) throws IOException {
                writer.state = WROTE_CUSTOM_DATA;

                // writeDefaultObject was not invoked
                writer.writeBoolean(false);

                if (writer.getStreamFormatVersion() == 2) {
                    writer._startValue(writer._desc.getCustomRepositoryID());
                }
            }
        },
        IN_DEFAULT_WRITE_OBJECT {
            void afterDefaultWriteObject(ObjectWriter writer) {
                writer.state = WROTE_DEFAULT_DATA;
            }
        },
        WROTE_DEFAULT_DATA {
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

            void beforeWriteData(ObjectWriter writer) throws IOException {
                // the boolean identifying that default data was written has already
                // been emitted in IN_WRITE_OBJECT.beforeWriteDefaultObject
                writer.state = WROTE_CUSTOM_DATA;

                if (writer.getStreamFormatVersion() == 2) {
                    writer._startValue(writer._desc.getCustomRepositoryID());
                }
            }
        },
        WROTE_CUSTOM_DATA {
            void afterWriteObject(ObjectWriter writer) throws IOException {
                if (writer.getStreamFormatVersion() == 2) {
                    writer._endValue();
                }

                writer.state = NOT_IN_WRITE_OBJECT;
            }
        }
        ;

        void beforeWriteObject(ObjectWriter writer) {
            throw new IllegalStateException();
        }

        void afterWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException();
        }

        void beforeDefaultWriteObject(ObjectWriter writer) throws IOException {
            throw new IllegalStateException();
        }

        void afterDefaultWriteObject(ObjectWriter writer) {
            throw new IllegalStateException();
        }

        void beforeWriteData(ObjectWriter writer) throws IOException {
        }
    }

    protected void beforeWriteData() throws IOException {
        state.beforeWriteData(this);
    }

    ObjectWriter(Serializable obj) throws IOException {
        object = obj;
        streamFormatVersion = CmsfThreadLocal.get();
    }

    private byte getStreamFormatVersion() {
        return streamFormatVersion;
    }

    abstract ObjectReader getObjectReader(Object newObject);

    private void setCurrentValueDescriptor(ValueDescriptor desc) {
        _desc = desc;
        _putFields = null;
    }

    public final void defaultWriteObject() throws IOException {
        if (_desc == null) {
            throw new NotActiveException();
        }

        state.beforeDefaultWriteObject(this);
        try {
            _desc.defaultWriteValue(this, object);
        } finally {
            state.afterDefaultWriteObject(this);
        }
    }

    public ObjectOutputStream.PutField putFields()
            throws IOException {
        if (_desc == null) {
            throw new NotActiveException();
        }

        _putFields = new PutFieldImpl(_desc);

        return _putFields;
    }

    public void writeFields() throws IOException {
        if (_putFields == null) {
            throw new NotActiveException("no current PutFields");
        }

        if (_putFields.slice != _desc) {
            throw new NotActiveException(
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
        final ValueDescriptor slice;

        final Map<String, Object> valueMap = new HashMap<>();

        PutFieldImpl(ValueDescriptor slice) {
            this.slice = slice;
        }

        @Override
        public void put(String name, boolean val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, byte val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, char val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, double val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, float val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, int val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, long val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, Object val) {
            valueMap.put(name, val);
        }

        @Override
        public void put(String name, short val) {
            valueMap.put(name, val);
        }

        @Override
        public void write(ObjectOutput out) throws IOException {
            slice.writeFields((ObjectWriter) out, valueMap);
        }
    }

    void invokeWriteObject(ValueDescriptor descriptor, Serializable val,
                           Method _write_object_method) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, IOException {
        final ValueDescriptor desc = _desc;
        final WriteObjectState old_state = state;
        state = WriteObjectState.NOT_IN_WRITE_OBJECT;
        try {
            setCurrentValueDescriptor(descriptor);
            writeByte(getStreamFormatVersion());
            state.beforeWriteObject(this);
            _write_object_method.invoke(val, this);
            state.afterWriteObject(this);
        } finally {
            state = old_state;
            setCurrentValueDescriptor(desc);
        }
    }

    protected abstract void _startValue(String rep_id) throws IOException;

    protected abstract void _endValue() throws IOException;

    protected abstract void _nullValue() throws IOException;

    void invokeWriteExternal(Externalizable externalizable) throws IOException {
        writeByte(getStreamFormatVersion());
        externalizable.writeExternal(this);
    }
}

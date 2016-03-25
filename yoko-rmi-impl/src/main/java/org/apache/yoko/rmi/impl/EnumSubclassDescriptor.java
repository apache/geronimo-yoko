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
import java.util.Map;

import org.apache.yoko.rmi.util.StringUtil;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;

class EnumSubclassDescriptor extends ValueDescriptor {
    @SuppressWarnings("rawtypes")
    private final Class enumType;

    EnumSubclassDescriptor(Class<?> type, TypeRepository repository) {
        super(type, repository);
        enumType = type;
    }

    static Class<?> getEnumType(Class<?> type) {
        if (!!!Enum.class.isAssignableFrom(type)) throw new IllegalArgumentException(type.getName() + " is not an Enum");
        while (!!!type.isEnum()) type = type.getSuperclass();
        return type;
    }

    @Override
    public final void init() {
        super.init();
        // Avoid doing anything that would cause the calculated classHash to change
    }

    @Override
    protected final long getSerialVersionUID() {
        return 0L;
    }

    @Override
    protected final boolean isEnum() {
        return true;
    }

    @Override
    final public Serializable readValue(InputStream in, Map<Integer, Object> offsetMap, Integer offset) {
        try {
            // Shortcut to reading in just the 'name' field of java.lang.Enum
            String name = null;
            try {
                name = (String) ((org.omg.CORBA_2_3.portable.InputStream) in).read_value(String.class);
            } catch (org.omg.CORBA.MARSHAL e) {
                // Problem probably due to ordinal field data being sent
                // This should be resolved by the 'if (name == null) {' block below, so this
                // exception can be safely discarded.
            }
            if (name == null) {
                // ordinal field may have been sent, causing the read of the name field to fail
                // If this is the case, the input stream cursor will now be at the start of where the
                // name field is located (the 4 bytes of the ordinal having now been read in)
                name = (String) ((org.omg.CORBA_2_3.portable.InputStream) in).read_value(String.class);
            }

            @SuppressWarnings("unchecked")
            final Enum<?> value = (Enum<?>) Enum.valueOf(enumType, name);
            offsetMap.put(offset, value);
            return value;
        } catch (IndirectionException ex) {
            return (Serializable) offsetMap.get(ex.offset);
        }
    }

    @Override
    protected final void writeValue(ObjectWriter writer, Serializable val) throws IOException {
        // Don't write out any fields in the Enum subclass
        _super_descriptor.writeValue(writer, val);
    }

    @Override
    public final boolean isChunked() {
        // Always do chunking for subclasses of Enum - like it's custom marshalled
        return true;
    }

    @Override
    public final Serializable writeReplace(Serializable val) {
        // Never allow the honoring of writeReplace on an Enum subclass
        return val;
    }
}

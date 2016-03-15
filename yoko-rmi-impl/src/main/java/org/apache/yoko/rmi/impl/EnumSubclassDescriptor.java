package org.apache.yoko.rmi.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;

public final class EnumSubclassDescriptor extends ValueDescriptor {
    @SuppressWarnings("rawtypes")
    private final Class enumType;

    EnumSubclassDescriptor(Class<?> type, TypeRepository repository) {
        super(type, repository);
        enumType = getEnumType(type);
    }

    private static Class<?> getEnumType(Class<?> type) {
        while (!!!type.isEnum()) {
            type = type.getSuperclass();
        }
        return type;
    }

    @Override
    long getSerialVersionUID() {
        return 0L;
    }

    @Override
    public Serializable readValue(InputStream in, Map<Integer, Object> offsetMap, Integer offset) {
        try {
            // Shortcut to reading in just the fields of java.lang.Enum - ordinal and name
            in.read_long(); // read in and ignore Enum ordinal
            final String name = (String) ((org.omg.CORBA_2_3.portable.InputStream) in).read_value(String.class);
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

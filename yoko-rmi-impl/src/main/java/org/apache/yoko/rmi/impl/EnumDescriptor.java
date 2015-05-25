package org.apache.yoko.rmi.impl;

import java.io.Serializable;
import java.util.Map;

import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;

public final class EnumDescriptor extends ValueDescriptor {
    @SuppressWarnings("rawtypes")
    private final Class enumType;

    EnumDescriptor(Class<?> type, TypeRepository repository) {
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
}

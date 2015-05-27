package org.apache.yoko.rmi.impl;

import java.lang.reflect.Field;

import javax.rmi.CORBA.ClassDesc;

import org.omg.CORBA.MARSHAL;

public abstract class ClassBaseDescriptor extends ValueDescriptor {

    ClassBaseDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    @Override
    public void init() {
        super.init();

        Class<?> clz = ClassDesc.class;
        try {
            final Field repid_field = clz.getDeclaredField("repid");
            repid_field.setAccessible(true);
            final Field codebase_field = clz.getDeclaredField("codebase");
            codebase_field.setAccessible(true);
            init(repid_field, codebase_field);
        } catch (NoSuchFieldException ex) {
            throw new MARSHAL("no such field: " + ex);
        }
    }

    abstract void init(Field repid_field, Field codebase_field);
}

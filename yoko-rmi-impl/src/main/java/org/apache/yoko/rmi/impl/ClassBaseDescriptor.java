package org.apache.yoko.rmi.impl;

import org.omg.CORBA.MARSHAL;

import javax.rmi.CORBA.ClassDesc;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

abstract class ClassBaseDescriptor extends ValueDescriptor {

    ClassBaseDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    private volatile Field repidField = null;
    private Field genRepIdField() {
        return findField("repid");
    }
    final Field getRepidField() {
        if (null == repidField) repidField = genRepIdField();
        return repidField;
    }

    private volatile Field cobebaseField = null;
    private Field genCodebaseField() {
        return findField("codebase");
    }
    final Field getCobebaseField() {
        if (null == cobebaseField) cobebaseField = genCodebaseField();
        return cobebaseField;
    }

    private Field findField(final String fieldName) {
        return AccessController.doPrivileged(new PrivilegedAction<Field>() {
            public Field run() {
                try {
                    Field f = ClassDesc.class.getDeclaredField(fieldName);
                    f.setAccessible(true);
                    return f;
                } catch (NoSuchFieldException e) {
                    throw (MARSHAL)new MARSHAL("no such field: " + e).initCause(e);
                }
            }
        });
    }
}

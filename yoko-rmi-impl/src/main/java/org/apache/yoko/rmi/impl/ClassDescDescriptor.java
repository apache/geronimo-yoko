package org.apache.yoko.rmi.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.ClassDesc;
import javax.rmi.CORBA.Util;

import org.omg.CORBA.MARSHAL;

public class ClassDescDescriptor extends ClassBaseDescriptor {
    private static final Logger logger = Logger.getLogger(ClassDescDescriptor.class.getName());

    private Field repid_field;
    private Field codebase_field;

    ClassDescDescriptor(TypeRepository repository) {
        super(ClassDesc.class, repository);
    }

    @Override
    void init(Field repid_field, Field codebase_field) {
        this.repid_field = repid_field;
        this.codebase_field = codebase_field;
    }

    /** Read an instance of this value from a CDR stream */
    @Override
    public Serializable readResolve(final Serializable value) {
        final ClassDesc desc = (ClassDesc) value;

        Class<?> result = AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
            public Class<?> run() {
                String className = "<unknown>";
                try {
                    String repid = (String) repid_field.get(desc);
                    String codebase = (String) codebase_field.get(desc);

                    TypeDescriptor typeDesc = repository.getDescriptor(repid);
                    if (null != typeDesc) {
                        Class<?> type = typeDesc.getJavaClass();
                        if (null != type) return type;
                    }

                    int beg = repid.indexOf(':');
                    int end = repid.indexOf(':', beg + 1);

                    className = repid.substring(beg + 1, end);
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();

                    return Util.loadClass(className, codebase, loader);
                } catch (ClassNotFoundException ex) {
                    throw (MARSHAL)new MARSHAL("cannot load class " + className).initCause(ex);
                } catch (IllegalAccessException ex) {
                    throw (MARSHAL)new MARSHAL("no such field: " + ex).initCause(ex);
                }
            }
        });

        if (logger.isLoggable(Level.FINE))
            logger.fine(String.format("readResolve %s => %s", value, result));

        return result;
    }

}

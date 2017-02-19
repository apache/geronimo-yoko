package org.apache.yoko.rmi.impl;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.ClassDesc;
import javax.rmi.CORBA.Util;

import org.apache.yoko.util.cmsf.RepIds;
import org.omg.CORBA.MARSHAL;

class ClassDescDescriptor extends ClassBaseDescriptor {
    private static final Logger logger = Logger.getLogger(ClassDescDescriptor.class.getName());

    ClassDescDescriptor(TypeRepository repository) {
        super(ClassDesc.class, repository);
    }

    /** Read an instance of this value from a CDR stream */
    @Override
    public Serializable readResolve(final Serializable value) {
        final ClassDesc desc = (ClassDesc) value;

        Class<?> result = AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
            public Class<?> run() {
                String className = "<unknown>";
                try {
                    String repid = (String) getRepidField().get(desc);
                    String codebase = (String) getCobebaseField().get(desc);

                    Class<?> result = RepIds.query(repid).codebase(codebase).toClass();
                    if (null != result) return result;

                    throw new MARSHAL(String.format("Cannot load class \"%s\"", className));
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

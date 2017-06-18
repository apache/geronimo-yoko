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

import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

import javax.rmi.CORBA.Util;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

class IDLEntityDescriptor extends ValueDescriptor {
    private final boolean isCorba;
    private final Class helperType;

    IDLEntityDescriptor(Class type, TypeRepository repository) {
        super(type, repository);

        isCorba = org.omg.CORBA.Object.class.isAssignableFrom(type);
        try {
            final String helperName = type.getName() + "Helper";
            helperType = Util.loadClass(helperName, null, type.getClassLoader());
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("cannot load IDL Helper class for "
                    + type, ex);
        }
    }

    @Override
    protected final String genIDLName() {
        return "org_omg_boxedIDL_" + super.genIDLName();
    }

    private volatile Method readMethod = null;
    private Method getReadMethod() {
        if (null == readMethod) readMethod = genHelperMethod("read");
        return readMethod;
    }

    private volatile Method writeMethod = null;
    private Method getWriteMethod() {
        if (null == writeMethod) writeMethod = genHelperMethod("write");
        return writeMethod;
    }

    private volatile Method typeMethod = null;
    private Method getTypeMethod() {
        if (null == typeMethod) typeMethod = genHelperMethod("type");
        return typeMethod;
    }

    private Method genHelperMethod(final String name) {
        return AccessController.doPrivileged(new PrivilegedAction<Method>() {
            @Override
            public Method run() {
                for (Method m: helperType.getDeclaredMethods()) {
                    if (m.getName().equals(name)) return m;
                }
                throw new RuntimeException("Unable to find " + name + " method for " + helperType.getName());
            }
        });
    }

    /** Read an instance of this value from a CDR stream */
    @Override
    public Object read(InputStream in) {
        org.omg.CORBA_2_3.portable.InputStream _in = (org.omg.CORBA_2_3.portable.InputStream) in;
        
        // there are two ways we need to deal with IDLEntity classes.  Ones that also implement 
        // the CORBA Object interface are actual corba objects, and must be handled that way. 
        // Other IDLEntity classes are just transmitted by value. 
        if (isCorba) {
            return _in.read_Object(type);
        } else {
            // we directly call read_value() on the stream here, with the explicitly specified
            // repository ID.  The input stream will handle validating the value tag for us, and eventually
            // will call our readValue() method to deserialize the object.
            return _in.read_value(getRepositoryID());
        }
    }

    @Override
    public Serializable readValue(final InputStream in, final Map<Integer, Serializable> offsetMap, final Integer offset) {
        try {
            Serializable value = (Serializable) getReadMethod().invoke(null, new Object[]{in});
            offsetMap.put(offset, value);
            return value;
        } catch (InvocationTargetException ex) {
            throw (MARSHAL)new MARSHAL(""+ex.getCause()).initCause(ex.getCause());
        } catch (IllegalAccessException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    /** Write an instance of this value to a CDR stream */
    @Override
    public void write(OutputStream out, Object val) {
        org.omg.CORBA_2_3.portable.OutputStream _out = (org.omg.CORBA_2_3.portable.OutputStream) out;

        
        // there are two ways we need to deal with IDLEntity classes.  Ones that also implement 
        // the CORBA Object interface are actual corba objects, and must be handled that way. 
        // Other IDLEntity classes are just transmitted by value. 
        if (val instanceof ObjectImpl) {
            _out.write_Object((org.omg.CORBA.Object)val); 
        } else {
            // we directly call write_value() on the stream here, with the explicitly specified
            // repository ID.  the output stream will handle writing the value tag for us, and eventually
            // will call our writeValue() method to serialize the object.
            _out.write_value((Serializable)val, getRepositoryID());
        }
    }

    @Override
    public void writeValue(OutputStream out, Serializable val) {
        try {
            getWriteMethod().invoke(null, new Object[] { out, val });
        } catch (InvocationTargetException ex) {
            throw (MARSHAL)new MARSHAL(""+ ex.getCause()).initCause(ex.getCause());
        } catch (IllegalAccessException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    @Override
    protected TypeCode genTypeCode() {
        try {
            return (TypeCode) getTypeMethod().invoke(null, new Object[0]);
        } catch (InvocationTargetException ex) {
            throw (MARSHAL)new MARSHAL(""+ex.getCause()).initCause(ex.getCause());
        } catch (IllegalAccessException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }
    }
}

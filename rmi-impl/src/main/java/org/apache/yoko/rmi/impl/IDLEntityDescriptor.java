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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.omg.CORBA.MARSHAL;

public class IDLEntityDescriptor extends ValueDescriptor {
    Method _read_method;

    Method _write_method;

    Method _type_method;

    boolean isAbstract = false;

    boolean isCorba = false; 

    IDLEntityDescriptor(Class type, TypeRepository repository) {
        super(type, repository);

        if (org.omg.CORBA.Object.class.isAssignableFrom(type)) {
            isCorba = true; 
        }
    }

    public String getIDLName() {
        return "org_omg_boxedIDL_" + super.getIDLName();
    }

    public void initIDL() {
        super.init();

        try {
            final Class type = getJavaClass();
            final String helperName = type.getName() + "Helper";
            final Class helperClass = javax.rmi.CORBA.Util.loadClass(
                    helperName, null, type.getClassLoader());

            java.security.AccessController
                    .doPrivileged(new java.security.PrivilegedAction() {
                        public Object run() {

                            try {
                                Method _id_method = null;
                                Method[] methods = helperClass
                                        .getDeclaredMethods();
                                for (int i = 0; i < methods.length; i++) {
                                    String name = methods[i].getName();

                                    if (name.equals("id"))
                                        _id_method = methods[i];

                                    else if (name.equals("read"))
                                        _read_method = methods[i];

                                    else if (name.equals("write"))
                                        _write_method = methods[i];

                                    else if (name.equals("type"))
                                        _type_method = methods[i];
                                }

                                // _repid = (String)
                                _id_method.invoke(null, new Object[0]);

                            } catch (InvocationTargetException ex) {
                                throw new RuntimeException(
                                        "cannot initialize: " + ex, ex);

                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(
                                        "cannot initialize: " + ex, ex);
                            }

                            return null;
                        }
                    });

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("cannot load IDL Helper class for "
                    + getJavaClass(), ex);
        }
    }

    /*
     * public String getRepositoryID () { return _repid; }
     */

    /** Read an instance of this value from a CDR stream */
    public Object read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA_2_3.portable.InputStream _in = (org.omg.CORBA_2_3.portable.InputStream) in;
        
        // there are two ways we need to deal with IDLEntity classes.  Ones that also implement 
        // the CORBA Object interface are actual corba objects, and must be handled that way. 
        // Other IDLEntity classes are just transmitted by value. 
        if (isCorba) {
            return _in.read_Object(getJavaClass()); 
        }
        else {

            // we directly call read_value() on the stream here, with the explicitly specified
            // repository ID.  The input stream will handle validating the value tag for us, and eventually
            // will call our readValue() method to deserialize the object.
            return _in.read_value(getRepositoryID());
        }
    }

    public java.io.Serializable readValue(
            final org.omg.CORBA.portable.InputStream in,
            final java.util.Map offsetMap, final java.lang.Integer offset) {
        final java.io.Serializable value = (java.io.Serializable) readValue(in);

        offsetMap.put(offset, value);

        return value;
    }

    public Object readValue(org.omg.CORBA.portable.InputStream in) {
        if (isAbstract) {
            throw new MARSHAL("IDL Entity " + getJavaClass().getName()
                    + " is abstract");
        }

        try {
            return _read_method.invoke(null, new Object[] { in });
        } catch (InvocationTargetException ex) {
            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
        } catch (IllegalAccessException ex) {
            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    /** Write an instance of this value to a CDR stream */
    public void write(org.omg.CORBA.portable.OutputStream out, Object val) {
        org.omg.CORBA_2_3.portable.OutputStream _out = (org.omg.CORBA_2_3.portable.OutputStream) out;

        
        // there are two ways we need to deal with IDLEntity classes.  Ones that also implement 
        // the CORBA Object interface are actual corba objects, and must be handled that way. 
        // Other IDLEntity classes are just transmitted by value. 
        if (val instanceof org.omg.CORBA.portable.ObjectImpl) {
            _out.write_Object((org.omg.CORBA.Object)val); 
        }
        else {
            // we directly call write_value() on the stream here, with the explicitly specified
            // repository ID.  the output stream will handle writing the value tag for us, and eventually
            // will call our writeValue() method to serialize the object.
            _out.write_value((java.io.Serializable)val, getRepositoryID());
        }

    }

    public void writeValue(org.omg.CORBA.portable.OutputStream out, java.io.Serializable val) {
        if (isAbstract) {
            throw new MARSHAL("IDL Entity " + getJavaClass().getName()
                    + " is abstract");
        }

        try {
            _write_method.invoke(null, new Object[] { out, val });
        } catch (InvocationTargetException ex) {
            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
        } catch (IllegalAccessException ex) {
            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    org.omg.CORBA.TypeCode getTypeCode() {
        if (_type_code == null) {

            try {
                _type_code = (org.omg.CORBA.TypeCode) _type_method.invoke(null,
                        new Object[0]);
            } catch (InvocationTargetException ex) {
                throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
            } catch (IllegalAccessException ex) {
                throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
            }
        }

        return _type_code;
    }

}

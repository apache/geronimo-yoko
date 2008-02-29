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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;

import sun.reflect.ReflectionFactory;

public class ValueDescriptor extends TypeDescriptor {
    static final Logger logger = Logger.getLogger(ValueDescriptor.class
            .getName());

    protected boolean _is_externalizable;

    protected boolean _is_serializable;

    protected Method _write_replace_method;

    protected Method _read_resolve_method;

    protected Constructor _constructor;

    protected Method _write_object_method;

    protected Method _read_object_method;

    protected Field _serial_version_uid_field;

    protected ValueDescriptor _super_descriptor;

    protected FieldDescriptor[] _fields;

    protected ObjectDeserializer _object_deserializer;

    protected boolean _is_immutable_value;

    protected boolean _is_rmi_stub;

    private static Set _immutable_value_classes;

    private static Object[] NO_ARGS = new Object[0];

    static {
        _immutable_value_classes = new HashSet();
        _immutable_value_classes.add(Integer.class);
        _immutable_value_classes.add(Character.class);
        _immutable_value_classes.add(Boolean.class);
        _immutable_value_classes.add(Byte.class);
        _immutable_value_classes.add(Long.class);
        _immutable_value_classes.add(Float.class);
        _immutable_value_classes.add(Double.class);
        _immutable_value_classes.add(Short.class);
    }

    protected long _hash_code;

    ValueDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    public String getRepositoryID() {
        if (_repid == null) {
            StringBuffer buf = new StringBuffer("RMI:");
            buf.append(getJavaClass().getName());
            buf.append(":");

            String hashCode = Long.toHexString(_hash_code).toUpperCase();
            for (int i = 0; (i + hashCode.length()) != 16; i++)
                buf.append('0');
            buf.append(hashCode);

            long serialVersionUID = getSerialVersionUID();

            buf.append(":");
            String serialID = Long.toHexString(serialVersionUID).toUpperCase();
            for (int i = 0; (i + serialID.length()) != 16; i++)
                buf.append('0');
            buf.append(serialID);

            _repid = buf.toString();
        }

        return _repid;
    }

    long getSerialVersionUID() {
        if (_serial_version_uid_field != null) {

            try {
                return _serial_version_uid_field.getLong(null);
            } catch (IllegalAccessException ex) {
                // skip //
            }
        }
        java.io.ObjectStreamClass serialForm = java.io.ObjectStreamClass.lookup(getJavaClass());
       
        return serialForm != null ? serialForm.getSerialVersionUID() : 0L;
    }

    public void init() {
        try {
            init0();

            if (_fields == null) {
                throw new RuntimeException("fields==null after init!");
            }

        } catch (RuntimeException ex) {
            logger.log(Level.FINE, "runtime error in ValueDescriptor.init " + ex.getMessage(), ex);
        } catch (Error ex) {
            logger.log(Level.FINE, "runtime error in ValueDescriptor.init " + ex.getMessage(), ex);
        }
    }

    public void init0() {
        final Class type = getJavaClass();
        final Class superClass = type.getSuperclass();

        _is_rmi_stub = RMIStub.class.isAssignableFrom(type);
        _is_externalizable = java.io.Externalizable.class
                .isAssignableFrom(type);
        _is_serializable = java.io.Serializable.class.isAssignableFrom(type);

        _is_immutable_value = _immutable_value_classes.contains(type);

        if (superClass != null && superClass != java.lang.Object.class) {
            TypeDescriptor superDesc = getTypeRepository().getDescriptor(
                    superClass);

            if (superDesc instanceof ValueDescriptor) {
                _super_descriptor = (ValueDescriptor) superDesc;
            }

        }

        java.security.AccessController
                .doPrivileged(new java.security.PrivilegedAction() {
                    public Object run() {

                        for (Class curr = type; curr != null; curr = curr
                                .getSuperclass()) {

                            //
                            // get writeReplace, if any
                            //
                            try {
                                _write_replace_method = curr.getDeclaredMethod(
                                        "writeReplace", new Class[0]);
                                _write_replace_method.setAccessible(true);

                                break;
                            } catch (NoSuchMethodException ex) {

                            }
                        }

                        //
                        // Get readResolve, if present
                        //
                        try {
                            _read_resolve_method = type.getDeclaredMethod(
                                    "readResolve", new Class[0]);
                            _read_resolve_method.setAccessible(true);

                        } catch (NoSuchMethodException ex) {
                            // skip //
                        }

                        // 
                        // get readObject
                        //
                        try {
                            _read_object_method = type
                                    .getDeclaredMethod(
                                            "readObject",
                                            new Class[] { java.io.ObjectInputStream.class });
                            _read_object_method.setAccessible(true);
                        } catch (NoSuchMethodException ex) {
                            // skip //
                        }

                        // 
                        // get readObject
                        //
                        try {
                            _write_object_method = type
                                    .getDeclaredMethod(
                                            "writeObject",
                                            new Class[] { java.io.ObjectOutputStream.class });
                            _write_object_method.setAccessible(true);
                        } catch (NoSuchMethodException ex) {
                            // skip //
                        }

                        // 
                        // validate readObject
                        //
                        if (_write_object_method == null
                                || !Modifier.isPrivate(_write_object_method
                                        .getModifiers())
                                || Modifier.isStatic(_write_object_method
                                        .getModifiers())
                                || _write_object_method.getDeclaringClass() != getJavaClass()) {

                            _write_object_method = null;

                        }

                        // 
                        // validate writeObject
                        //
                        if (_read_object_method == null
                                || !Modifier.isPrivate(_read_object_method
                                        .getModifiers())
                                || Modifier.isStatic(_read_object_method
                                        .getModifiers())) {

                            _read_object_method = null;
                        }

                        // 
                        // get serialVersionUID field
                        //
                        try {
                            _serial_version_uid_field = type
                                    .getDeclaredField("serialVersionUID");
                            if (Modifier.isStatic(_serial_version_uid_field
                                    .getModifiers())) {
                                _serial_version_uid_field.setAccessible(true);
                            } else {
                                _serial_version_uid_field = null;
                            }
                        } catch (NoSuchFieldException ex) {
                            // skip //
                        }

                        // 
                        // get serialPersistentFields field
                        //
                        ObjectStreamField[] serial_persistent_fields = null;
                        try {
                            Field _serial_persistent_fields_field = type
                                    .getDeclaredField("serialPersistentFields");
                            _serial_persistent_fields_field.setAccessible(true);

                            serial_persistent_fields = (ObjectStreamField[]) _serial_persistent_fields_field
                                    .get(null);

                        } catch (IllegalAccessException ex) {
                            // skip //
                        } catch (NoSuchFieldException ex) {
                            // skip //
                        }

                        if (_is_externalizable) {
                            //
                            // Get the default constructor
                            //
                            try {
                                _constructor = type
                                        .getDeclaredConstructor(new Class[0]);
                                _constructor.setAccessible(true);

                            } catch (NoSuchMethodException ex) {
                                logger.log(Level.WARNING, "Class " + type.getName()
                                        + " is not properly externalizable.  "
                                        + "It has not default constructor.", ex);
                            }

                        } else if (_is_serializable && !type.isInterface()) {

                            Class initClass = type;

                            while (initClass != null
                                    && java.io.Serializable.class
                                            .isAssignableFrom(initClass)) {
                                initClass = initClass.getSuperclass();
                            }

                            if (initClass == null) {
                                logger
                                        .warning("Class "
                                                + type.getName()
                                                + " is not properly serializable.  "
                                                + "It has no non-serializable super-class");
                            } else {
                                try {
                                    Constructor init_cons = initClass
                                            .getDeclaredConstructor(new Class[0]);

                                    if (Modifier.isPublic(init_cons
                                            .getModifiers())
                                            || Modifier.isProtected(init_cons
                                                    .getModifiers())) {
                                        // do nothing - it's accessible

                                    } else if (!samePackage(type, initClass)) {
                                        logger
                                                .warning("Class "
                                                        + type.getName()
                                                        + " is not properly serializable.  "
                                                        + "The default constructor of its first "
                                                        + "non-serializable super-class ("
                                                        + initClass.getName()
                                                        + ") is not accessible.");
                                    }

                                    _constructor = ReflectionFactory
                                            .getReflectionFactory()
                                            .newConstructorForSerialization(
                                                    type, init_cons);

                                    if (_constructor == null) {
                                        logger
                                                .warning("Unable to get constructor for serialization for class "
                                                        + getJavaName());
                                    } else {
                                        _constructor.setAccessible(true);
                                    }

                                } catch (NoSuchMethodException ex) {
                                    logger.log(Level.WARNING, 
                                                     "Class "
                                                    + type.getName()
                                                    + " is not properly serializable.  "
                                                    + "First non-serializable super-class ("
                                                    + initClass.getName()
                                                    + ") has not default constructor.", ex);
                                }
                            }
                        }

                        if (serial_persistent_fields == null) {

                            //
                            // Get relevant field definitions
                            //

                            Field[] ff = type.getDeclaredFields();

                            if (ff == null || ff.length == 0) {
                                _fields = new FieldDescriptor[0];

                            } else {
                                java.util.List flist = new java.util.ArrayList();

                                for (int i = 0; i < ff.length; i++) {
                                    Field f = ff[i];
                                    int mod = f.getModifiers();
                                    if (Modifier.isStatic(mod)
                                            || Modifier.isTransient(mod)) {
                                        continue;
                                    }

                                    f.setAccessible(true);
                                    FieldDescriptor fd = FieldDescriptor.get(f);
                                    fd.setTypeRepository(getTypeRepository());
                                    flist.add(fd);
                                }

                                _fields = new FieldDescriptor[flist.size()];
                                _fields = (FieldDescriptor[]) flist
                                        .toArray(_fields);

                                //
                                // sort the fields
                                //
                                java.util.Arrays.sort(_fields);
                            }

                        } else {
                            _fields = new FieldDescriptor[serial_persistent_fields.length];

                            for (int i = 0; i < serial_persistent_fields.length; i++) {
                                ObjectStreamField f = serial_persistent_fields[i];

                                FieldDescriptor fd = null;

                                try {
                                    java.lang.reflect.Field rf = type
                                            .getField(f.getName());
                                    rf.setAccessible(true);

                                    if (rf.getType() == f.getType()) {
                                        fd = FieldDescriptor.get(rf);
                                    }
                                } catch (SecurityException ex) {
                                } catch (NoSuchFieldException ex) {
                                }

                                if (fd == null) {
                                    fd = FieldDescriptor.get(type, f);
                                }

                                fd.setTypeRepository(getTypeRepository());
                                _fields[i] = fd;
                            }

                            //
                            // sort the fields (this is also the case for serial
                            // persistent
                            // fields, because they have to map to some foreign
                            // IDL).
                            //
                            java.util.Arrays.sort(_fields);
                        }

                        //
                        // Compute the structural hasn
                        //
                        _hash_code = computeHashCode();

                        // 
                        // Setup the default deserializer
                        //
                        _object_deserializer = new ObjectDeserializer(
                                ValueDescriptor.this);

                        return null;
                    }

                });
    }

    private boolean samePackage(Class type, Class initClass) {
        String pkg1 = getPackageName(type);
        String pkg2 = getPackageName(initClass);

        return pkg1.equals(pkg2);
    }

    private String getPackageName(Class type) {
        String name = type.getName();
        int idx = name.lastIndexOf('.');
        if (idx == -1) {
            return "";
        } else {
            return name.substring(0, idx);
        }
    }

    /** Read an instance of this value from a CDR stream */
    public Object read(org.omg.CORBA.portable.InputStream in) {
        org.omg.CORBA_2_3.portable.InputStream _in = (org.omg.CORBA_2_3.portable.InputStream) in;

        return _in.read_value();
    }

    /** Write an instance of this value to a CDR stream */
    public void write(org.omg.CORBA.portable.OutputStream out, Object value) {
        org.omg.CORBA_2_3.portable.OutputStream _out = (org.omg.CORBA_2_3.portable.OutputStream) out;

        _out.write_value((java.io.Serializable) value);
    }

    public boolean isCustomMarshalled() {
        if (_is_externalizable)
            return true;

        if (_write_object_method != null)
            return true;

        return false;
    }

    public java.io.Serializable writeReplace(java.io.Serializable val) {
        if (_write_replace_method != null) {
            try {
                return (java.io.Serializable) _write_replace_method.invoke(val,
                        NO_ARGS);

            } catch (java.lang.IllegalAccessException ex) {
                throw (MARSHAL)new MARSHAL("cannot call " + _write_replace_method).initCause(ex);

            } catch (IllegalArgumentException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InvocationTargetException ex) {
                throw new org.omg.CORBA.portable.UnknownException(ex
                        .getTargetException());
            }

        }

        return val;
    }

    public java.io.Serializable readResolve(java.io.Serializable val) {
        if (_read_resolve_method != null) {
            try {
                return (java.io.Serializable) _read_resolve_method.invoke(val,
                        NO_ARGS);

            } catch (java.lang.IllegalAccessException ex) {
                throw (MARSHAL)new MARSHAL("cannot call " + _read_resolve_method).initCause(ex);

            } catch (IllegalArgumentException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InvocationTargetException ex) {
                throw new org.omg.CORBA.portable.UnknownException(ex
                        .getTargetException());
            }

        }

        return val;
    }

    public void writeValue(final org.omg.CORBA.portable.OutputStream out,
            final java.io.Serializable value) {
        try {

            ObjectWriter writer = (ObjectWriter) java.security.AccessController
                    .doPrivileged(new java.security.PrivilegedAction() {
                        public Object run() {
                            try {
                                return new CorbaObjectWriter(out, value);
                            } catch (java.io.IOException ex) {
                                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
                            }
                        }
                    });

            writeValue(writer, value);

        } catch (java.io.IOException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    protected void defaultWriteValue(ObjectWriter writer,
            java.io.Serializable val) throws java.io.IOException {
        logger.finer("writing fields for " + getJavaClass());
        FieldDescriptor[] fields = _fields;

        if (fields == null) {
            return;
        }

        for (int i = 0; i < fields.length; i++) {
            logger.finer("writing field " + _fields[i].getJavaName());

            fields[i].write(writer, val);
        }
    }

    protected void writeValue(ObjectWriter writer, java.io.Serializable val)
            throws java.io.IOException {

        if (_is_externalizable) {
            writer.invokeWriteExternal((java.io.Externalizable) val);
            return;
        }

        if (_super_descriptor != null) {
            _super_descriptor.writeValue(writer, val);
        }

        if (_write_object_method != null) {

            try {
                writer.invokeWriteObject(this, val, _write_object_method);

            } catch (IllegalAccessException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (IllegalArgumentException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InvocationTargetException ex) {

                throw new org.omg.CORBA.portable.UnknownException(ex
                        .getTargetException());
            }

        } else {
            defaultWriteValue(writer, val);
        }

    }

    public java.io.Serializable createBlankInstance() {
        if (_constructor != null) {

            try {
                return (java.io.Serializable) _constructor
                        .newInstance(new Object[0]);

            } catch (java.lang.IllegalAccessException ex) {
                throw (MARSHAL)new MARSHAL("cannot call " + _constructor).initCause(ex);

            } catch (IllegalArgumentException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InstantiationException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InvocationTargetException ex) {
                throw new org.omg.CORBA.portable.UnknownException(ex
                        .getTargetException());

            } catch (NullPointerException ex) {
                logger.log(Level.WARNING, 
                    "unable to create instance of " + getJavaClass().getName(), ex);
                logger.warning("constructor => " + _constructor);

                throw ex;
            }

        } else {
            return null;
        }
    }

    public java.io.Serializable readValue(
            final org.omg.CORBA.portable.InputStream in,
            final java.util.Map offsetMap, final java.lang.Integer offset) {
        final java.io.Serializable value = createBlankInstance();

        offsetMap.put(offset, value);

        try {
            ObjectReader reader = (ObjectReader) java.security.AccessController
                    .doPrivileged(new java.security.PrivilegedAction() {
                        public Object run() {
                            try {
                                return new CorbaObjectReader(in, offsetMap,
                                        value);
                            } catch (java.io.IOException ex) {
                                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
                            }
                        }
                    });

            readValue(reader, value);

            return readResolve(value);

        } catch (java.io.IOException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }

    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        if (val == null) {
            pw.print("null");
        }

        Integer old = (Integer) recurse.get(val);
        if (old != null) {
            pw.print("^" + old);
        } else {
            Integer key = new Integer(System.identityHashCode(val));
            recurse.put(val, key);

            pw.println(getJavaClass().getName() + "@"
                    + Integer.toHexString(key.intValue()) + "[");

            printFields(pw, recurse, val);

            pw.println("]");
        }
    }

    void printFields(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        pw.print("(" + getClass().getName() + ")");

        if (_super_descriptor != null) {
            _super_descriptor.printFields(pw, recurse, val);
        }

        if (_fields == null)
            return;

        for (int i = 0; i < _fields.length; i++) {
            if (i != 0) {
                pw.print("; ");
            }

            _fields[i].print(pw, recurse, val);
        }

    }

    protected void defaultReadValue(ObjectReader reader,
            java.io.Serializable value) throws java.io.IOException {
        // System.out.println ("defaultReadValue "+getJavaClass());

        if (_fields == null) {
            // System.out.println ("fields == null for "+getJavaClass ());
            return;
        }

        logger.fine("reading fields for " + getJavaClass().getName());

        for (int i = 0; i < _fields.length; i++) {

            logger.fine("reading field " + _fields[i].getJavaName() + " of type " + _fields[i].getType().getName() + " using " + _fields[i].getClass().getName());

            try {
                _fields[i].read(reader, value);
            } catch (org.omg.CORBA.MARSHAL ex) {
                if (ex.getMessage() == null) {
                    org.omg.CORBA.MARSHAL exx = new org.omg.CORBA.MARSHAL(ex
                            .getMessage()
                            + ", while reading "
                            + getJavaName()
                            + "."
                            + _fields[i].getJavaName(), ex.minor, ex.completed);
                    exx.initCause(ex);
                    throw exx;
                } else {
                    throw ex;
                }
            }
        }
    }

    java.util.Map readFields(ObjectReader reader) throws java.io.IOException {
        if (_fields == null || _fields.length == 0) {
            return Collections.EMPTY_MAP;
        }

        logger.finer("reading fields for " + getJavaClass().getName());

        java.util.Map map = new HashMap();

        for (int i = 0; i < _fields.length; i++) {

            logger.finer("reading field " + _fields[i].getJavaName());

            _fields[i].readFieldIntoMap(reader, map);
        }

        return map;
    }

    void writeFields(ObjectWriter writer, java.util.Map fieldMap)
            throws java.io.IOException {
        if (_fields == null || _fields.length == 0) {
            return;
        }

        logger.finer("writing fields for " + getJavaClass().getName());

        for (int i = 0; i < _fields.length; i++) {

            logger.finer("writing field " + _fields[i].getJavaName());

            _fields[i].writeFieldFromMap(writer, fieldMap);
        }

    }

    /**
     * This methods reads the fields of a single class slice.
     */
    protected void readValue(ObjectReader reader, java.io.Serializable value)
            throws java.io.IOException {
        if (_is_externalizable) {
            java.io.Externalizable ext = (java.io.Externalizable) value;

            try {
                reader.readExternal(ext);
            } catch (ClassNotFoundException e) {
                IOException ex = new IOException("cannot instantiate class");
                ex.initCause(e);
                throw ex;
            }
            return;
        }

        if (_super_descriptor != null) {
            _super_descriptor.readValue(reader, value);
        }

        // System.out.println ("readValue "+getJavaClass());

        if (_write_object_method != null) {

            // read custom marshalling value header
            byte streamFormatVersion = reader.readByte();
            boolean writeDefaultStateCalled = reader.readBoolean();
        }

        if (_read_object_method != null) {

            // System.out.println ("readValue "+getJavaClass()+" calling
            // readObject");

            try {
                reader.setCurrentValueDescriptor(this);
                _read_object_method.invoke(value, new Object[] { reader });
                reader.setCurrentValueDescriptor(null);

            } catch (IllegalAccessException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
            } catch (IllegalArgumentException ex) {
                throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
            } catch (InvocationTargetException ex) {
                throw new org.omg.CORBA.portable.UnknownException(ex
                        .getTargetException());
            }

        } else {
            defaultReadValue(reader, value);
        }

    }

    protected long computeHashCode() {
        Class type = getJavaClass();
        long hash = 0L;

        if (_is_externalizable) {
            return 1L;

        } else if (!java.io.Serializable.class.isAssignableFrom(type)) {
            return 0;

        } else
            try {
                ByteArrayOutputStream barr = new ByteArrayOutputStream(512);
                MessageDigest md = MessageDigest.getInstance("SHA");
                DigestOutputStream digestout = new DigestOutputStream(barr, md);
                DataOutputStream out = new DataOutputStream(digestout);

                Class superType = type.getSuperclass();
                if (superType != null) {
                    TypeDescriptor desc = getTypeRepository().getDescriptor(
                            superType);
                    out.writeLong(desc.getHashCode());
                }

                if (_write_object_method == null)
                    out.writeInt(1);
                else
                    out.writeInt(2);

                FieldDescriptor[] fds = new FieldDescriptor[_fields.length];
                for (int i = 0; i < _fields.length; i++) {
                    fds[i] = _fields[i];
                }

                if (fds.length > 1)
                    java.util.Arrays.sort(fds, compareByName);

                for (int i = 0; i < fds.length; i++) {
                    FieldDescriptor f = fds[i];
                    out.writeUTF(f.getJavaName());
                    out.writeUTF(makeSignature(f.getType()));
                }

                /*
                 * Field[] fields = type.getDeclaredFields ();
                 * 
                 * if (fields.length > 1) java.util.Arrays.sort (fields,
                 * compareByName);
                 * 
                 * for(int i = 0; i < fields.length; i++) { Field f = fields[i];
                 * int mod = f.getModifiers (); if (!Modifier.isTransient(mod) &&
                 * !Modifier.isStatic (mod)) { out.writeUTF(f.getName());
                 * out.writeUTF( makeSignature (f.getType ())); } }
                 */

                out.flush();

                byte data[] = md.digest();
                int end = Math.min(8, data.length);
                for (int j = 0; j < end; j++) {
                    hash += (long) (data[j] & 0xff) << j * 8;
                }
            } catch (java.lang.Exception ex) {
                throw new RuntimeException("cannot compute RMI hash code", ex);
            }

        return hash;
    }

    private static java.util.Comparator compareByName = new java.util.Comparator() {
        public int compare(Object f1, Object f2) {
            String n1 = ((FieldDescriptor) f1).getJavaName();
            String n2 = ((FieldDescriptor) f2).getJavaName();
            return n1.compareTo(n2);
        }
    };

    long getHashCode() {
        return _hash_code;
    }

    protected org.omg.CORBA.ValueMember[] _value_members = null;

    org.omg.CORBA.ValueMember[] getValueMembers() {
        if (_value_members == null) {
            _value_members = new org.omg.CORBA.ValueMember[_fields.length];
            for (int i = 0; i < _fields.length; i++) {
                _value_members[i] = _fields[i]
                        .getValueMember(getTypeRepository());
            }
        }

        return _value_members;
    }

    org.omg.CORBA.TypeCode getTypeCode() {
        if (_type_code != null)
            return _type_code;

        org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
        _type_code = orb.create_recursive_tc(getRepositoryID());

        org.omg.CORBA.TypeCode _base = (_super_descriptor == null ? null
                : _super_descriptor.getTypeCode());

        _type_code = orb.create_value_tc(getRepositoryID(), getJavaClass()
                .getName(), org.omg.CORBA.VM_NONE.value, _base,
                getValueMembers());

        return _type_code;
    }

    org.omg.CORBA.ValueDefPackage.FullValueDescription getFullValueDescription() {
        return new org.omg.CORBA.ValueDefPackage.FullValueDescription(
                getJavaClass().getName(), getRepositoryID(), false, // is_abstract
                isCustomMarshalled(), "", "1.0",
                new org.omg.CORBA.OperationDescription[0],
                new org.omg.CORBA.AttributeDescription[0], getValueMembers(),
                new org.omg.CORBA.Initializer[0], new String[0], new String[0],
                false, // is_truncatable
                (_super_descriptor == null ? "" : _super_descriptor
                        .getRepositoryID()), getTypeCode());
    }

    class ObjectDeserializer {

        ObjectDeserializer super_descriptor;

        String repository_id;

        FieldDescriptor[] fields;

        ValueDescriptor localDescriptor() {
            return ValueDescriptor.this;
        }

        ObjectDeserializer(ValueDescriptor desc) {
            fields = desc._fields;
            repository_id = desc.getRepositoryID();

            if (desc._super_descriptor != null) {
                super_descriptor = desc._super_descriptor._object_deserializer;
            }
        }

        ObjectDeserializer(FullValueDescription desc,
                org.omg.SendingContext.RunTime runtime) throws IOException {
            Class myClass = ValueDescriptor.this.getJavaClass();
            ValueMember[] members = desc.members;
            fields = new FieldDescriptor[members.length];
            for (int i = 0; i < members.length; i++) {
                Class type = getClassFromTypeCode(members[i].type);
                fields[i] = FieldDescriptor.get(myClass, type, members[i].name,
                        null);
            }

            if (!"".equals(desc.base_value)) {
                Class clz = ValueHandlerImpl
                        .getClassFromRepositoryID(desc.base_value);
                TypeDescriptor tdesc = getTypeRepository().getDescriptor(clz);

                if (tdesc != null && tdesc instanceof ValueDescriptor) {
                    super_descriptor = ((ValueDescriptor) tdesc)
                            .getObjectDeserializer(desc.base_value, runtime);
                }
            }
        }
    }

    ObjectDeserializer getObjectDeserializer(String repositoryID,
            org.omg.SendingContext.RunTime runtime) throws java.io.IOException {
        if (repositoryID.equals(getRepositoryID())) {
            return _object_deserializer;
        }

        CodeBase codebase = CodeBaseHelper.narrow(runtime);
        if (codebase == null) {
            throw new IOException("cannot narrow RunTime -> CodeBase");
        }

        FullValueDescription desc = codebase.meta(repositoryID);

        return new ObjectDeserializer(desc, codebase);
    }

    static Class getClassFromTypeCode(TypeCode tc) {
        return null;
    }

    public boolean copyWithinState() {
        return !(_is_immutable_value | _is_rmi_stub);
    }

    Object copyObject(Object orig, CopyState state) {

        if (_is_immutable_value || _is_rmi_stub) {
            return orig;
        }

        java.io.Serializable oorig = (java.io.Serializable) orig;

        logger.finer("copying " + orig);

        oorig = writeReplace(oorig);

        ValueDescriptor wdesc;
        if (oorig != orig) {
            wdesc = (ValueDescriptor) getTypeRepository().getDescriptor(
                    oorig.getClass());

            logger.finer("writeReplace -> " + getJavaClass().getName());

        } else {
            wdesc = this;
        }

        return wdesc.copyObject2(oorig, state);
    }

    /**
     * this is called after write-replace on the type descriptor of the correct
     * type for writing
     */
    Serializable copyObject2(Serializable oorig, CopyState state) {

        // create instance of copied object, and register
        Serializable copy = createBlankInstance();
        state.put(oorig, copy);

        // write original object
        ObjectWriter writer = writeObject(oorig, state);

        // read into copy
        return readObject(writer, copy);
    }

    ObjectWriter writeObject(java.io.Serializable oorig, CopyState state) {

        ObjectWriter writer = null;

        try {

            writer = state.createObjectWriter(oorig);
            writeValue(writer, oorig);

            return writer;

        } catch (java.io.IOException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    java.io.Serializable readObject(ObjectWriter writer,
            java.io.Serializable copy) {

        ObjectReader reader = writer.getObjectReader(copy);

        try {
            readValue(reader, copy);
        } catch (java.io.IOException ex) {
            MARSHAL m = new MARSHAL(ex.getMessage() + " reading instance of "
                    + getJavaClass().getName());
            m.initCause(ex);
            throw m;
        }

        return readResolve(copy);
    }

    void writeMarshalValue(java.io.PrintWriter pw, String outName,
            String paramName) {
        pw.print(outName);
        pw.print('.');
        pw.print("write_value");

        // this ValueDescriptor could represent an Abstract Value,
        // in which case we need to cast the first argument.
        // We'll just always do that, because most of the time
        // HotSpot will remove this cast anyway.

        pw.print("((java.io.Serializable)");

        pw.print(paramName);
        pw.print(',');
        MethodDescriptor.writeJavaType(pw, getJavaClass());
        pw.print(".class)");
    }

    void writeUnmarshalValue(java.io.PrintWriter pw, String inName) {
        pw.print(inName);
        pw.print('.');
        pw.print("read_value");
        pw.print('(');
        MethodDescriptor.writeJavaType(pw, getJavaClass());
        pw.print(".class)");
    }

    void addDependencies(java.util.Set classes) {
        Class c = getJavaClass();

        if (c == java.lang.Object.class || classes.contains(c))
            return;

        classes.add(c);

        if (c.getSuperclass() != null) {
            TypeDescriptor desc = getTypeRepository().getDescriptor(
                    c.getSuperclass());
            desc.addDependencies(classes);
        }

        Class[] ifaces = c.getInterfaces();
        for (int i = 0; i < ifaces.length; i++) {
            TypeDescriptor desc = getTypeRepository().getDescriptor(ifaces[i]);
            desc.addDependencies(classes);
        }

        if (_fields != null) {
            for (int i = 0; i < _fields.length; i++) {
                if (_fields[i].isPrimitive())
                    continue;

                TypeDescriptor desc = getTypeRepository().getDescriptor(
                        _fields[i].type);
                desc.addDependencies(classes);
            }
        }
    }
}

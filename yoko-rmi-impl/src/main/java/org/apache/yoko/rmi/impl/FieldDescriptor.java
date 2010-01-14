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
import java.io.ObjectStreamField;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.omg.CORBA.ValueMember;

public abstract class FieldDescriptor extends ModelElement implements
        Comparable {
    static Logger logger = Logger.getLogger(FieldDescriptor.class.getName());

    org.apache.yoko.rmi.util.corba.Field field;

    Class type;

    Class declaringClass;

    boolean isFinal;

    ValueMember valuemember;

    boolean isPublic;

    protected FieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        this.type = type;
        setJavaName(name);
        setIDLName(name);
        declaringClass = owner;

        if (f != null) {
            isPublic = (Modifier.isPublic(f.getModifiers()));
            this.field = new org.apache.yoko.rmi.util.corba.Field(f);
            isFinal = Modifier.isFinal(f.getModifiers());
        } else {
            isPublic = false;
            this.field = null;
            isFinal = false;
        }
    }

    ValueMember getValueMember(TypeRepository rep) {
        if (valuemember == null) {
            TypeDescriptor desc = rep.getDescriptor(type);
            TypeDescriptor owner = rep.getDescriptor(declaringClass);

            valuemember = new ValueMember(getIDLName(), desc.getRepositoryID(),
                    owner.getRepositoryID(), "1.0", desc.getTypeCode(), null,
                    (short) (isPublic ? 1 : 0));
        }

        return valuemember;
    }

    public Class getType() {
        return type;
    }

    /**
     * ordering of fields
     */
    public int compareTo(Object other) {
        FieldDescriptor desc = (FieldDescriptor) other;

        //
        // Primitive fields precede non-primitive fields
        //
        if (this.isPrimitive() && !desc.isPrimitive())
            return -1;

        else if (!this.isPrimitive() && desc.isPrimitive())
            return 1;

        //
        // fields of the same kind are ordered lexicographically
        //	
        return getJavaName().compareTo(desc.getJavaName());
    }

    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    abstract void read(ObjectReader reader, Object obj)
            throws java.io.IOException;

    abstract void write(ObjectWriter writer, Object obj)
            throws java.io.IOException;

    abstract void readFieldIntoMap(ObjectReader reader, java.util.Map map)
            throws java.io.IOException;

    abstract void writeFieldFromMap(ObjectWriter writer, java.util.Map map)
            throws java.io.IOException;

    abstract void copyState(Object orig, Object copy, CopyState state);

    static FieldDescriptor get(java.lang.reflect.Field f) {
        return get(f.getDeclaringClass(), f.getType(), f.getName(), f);
    }

    static FieldDescriptor get(Class declaringClass, ObjectStreamField field) {
        Field f = null;
        try {
            f = declaringClass.getDeclaredField(field.getName());
        } catch (Exception ex) {
            logger.log(Level.FINER, "Cannot get java field \"" + field.getName()
                    + "\" for class \"" + declaringClass.getName() + "\""
                    + "\n" + ex, ex);
        }
        return get(declaringClass, field.getType(), field.getName(), f);
    }

    static FieldDescriptor get(Class owner, Class type, String name,
            java.lang.reflect.Field f) {

        if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
                return new BooleanFieldDescriptor(owner, type, name, f);
            } else if (type == Byte.TYPE) {
                return new ByteFieldDescriptor(owner, type, name, f);
            } else if (type == Short.TYPE) {
                return new ShortFieldDescriptor(owner, type, name, f);
            } else if (type == Character.TYPE) {
                return new CharFieldDescriptor(owner, type, name, f);
            } else if (type == Integer.TYPE) {
                return new IntFieldDescriptor(owner, type, name, f);
            } else if (type == Long.TYPE) {
                return new LongFieldDescriptor(owner, type, name, f);
            } else if (type == Float.TYPE) {
                return new FloatFieldDescriptor(owner, type, name, f);
            } else if (type == Double.TYPE) {
                return new DoubleFieldDescriptor(owner, type, name, f);
            } else {
                throw new RuntimeException("unknown field type " + type);
            }

        } else {
            if(org.omg.CORBA.Object.class.isAssignableFrom(type)) {
        	return new CorbaObjectFieldDescriptor(owner, type, name, f);
            }
            if (java.lang.Object.class.equals(type)
                    || java.io.Externalizable.class.equals(type)
                    || java.io.Serializable.class.equals(type)) {
                return new AnyFieldDescriptor(owner, type, name, f);

            } else if (java.rmi.Remote.class.isAssignableFrom(type) 
        	    || java.rmi.Remote.class.equals(type))
            {
                return new RemoteFieldDescriptor(owner, type, name, f);

            } else if (String.class.equals(type)) {
                return new StringFieldDescriptor(owner, type, name, f);

            } else if (java.io.Serializable.class.isAssignableFrom(type)) {
                return new ValueFieldDescriptor(owner, type, name, f);
            } else if (type.isInterface() && type.getMethods().length > 0) {
                // interface classes that define methods 
                return new ValueFieldDescriptor(owner, type, name, f);
            } else {
                return new ObjectFieldDescriptor(owner, type, name, f);
            }
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        pw.print(getJavaName());
        pw.print("=");
        try {
            Object obj = field.get(val);
            if (obj == null) {
                pw.print("null");
            } else {
                TypeDescriptor desc = getTypeRepository().getDescriptor(
                        obj.getClass());
                desc.print(pw, recurse, obj);
            }
        } catch (IllegalAccessException ex) {
            /*
             * } catch (RuntimeException ex) { System.err.println
             * ("SystemException in FieldDescriptor "+field); System.err.println
             * ("value = "+val); ex.printStackTrace ();
             */
        }
    }
}

class RemoteFieldDescriptor extends FieldDescriptor {
    Class interfaceType;

    RemoteFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);

        if (type.isInterface()) {
            interfaceType = type;
        } else {
            Class t = type;

            loop: while (!Object.class.equals(t)) {
                Class[] ifs = t.getInterfaces();
                for (int i = 0; i < ifs.length; i++) {
                    if (java.rmi.Remote.class.isAssignableFrom(ifs[i])) {
                        interfaceType = ifs[i];
                        break loop;
                    }
                }
                t = t.getSuperclass();
            }

            if (interfaceType == null) {
                throw new RuntimeException("cannot find " + "remote interface "
                        + "for " + type);
            }
        }
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            Object value = reader.readRemoteObject(interfaceType);
            field.set(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeRemoteObject((java.rmi.Remote) field.get(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(final Object orig, final Object copy, CopyState state) {
        try {
            field.set(copy, state.copy(field.get(orig)));
        } catch (CopyRecursionException e) {
            state.registerRecursion(new CopyRecursionResolver(orig) {
                public void resolve(Object value) {
                    try {
                        field.set(copy, value);
                    } catch (IllegalAccessException ex) {
                        throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
                    }
                }
            });
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        java.rmi.Remote value = (java.rmi.Remote) reader
                .readRemoteObject(interfaceType);
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        java.rmi.Remote value = (java.rmi.Remote) map.get(getJavaName());
        writer.writeRemoteObject(value);
    }

}

class AnyFieldDescriptor extends FieldDescriptor {
    static final Logger logger = Logger.getLogger(AnyFieldDescriptor.class
            .getName());

    boolean narrowValue;

    AnyFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
        narrowValue = java.rmi.Remote.class.isAssignableFrom(type);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        try {
            Object val = reader.readAny();
            if (narrowValue && val != null && !type.isInstance(val)) {
                try {
                    val = javax.rmi.PortableRemoteObject.narrow(val, this.type);
                } catch (SecurityException ex) {
                    logger.finer("Narrow failed" + "\n" + ex);
                    throw ex;
                }
            } else if (val != null && !type.isInstance(val)) {
                throw new org.omg.CORBA.MARSHAL("value is instance of "
                        + val.getClass().getName() + " -- should be: "
                        + type.getName());
            }

            field.set(obj, val);
        } catch (IllegalAccessException ex) {
            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        try {
            writer.writeAny(field.get(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(final Object orig, final Object copy, CopyState state) {
        try {
            field.set(copy, state.copy(field.get(orig)));
        } catch (CopyRecursionException e) {
            state.registerRecursion(new CopyRecursionResolver(orig) {
                public void resolve(Object value) {
                    try {
                        field.set(copy, value);
                    } catch (IllegalAccessException ex) {
                        throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
                    }
                }
            });
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        org.omg.CORBA.Any value = (org.omg.CORBA.Any) reader.readAny();
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        org.omg.CORBA.Any value = (org.omg.CORBA.Any) map.get(getJavaName());
        writer.writeAny(value);
    }

}

class ValueFieldDescriptor extends FieldDescriptor {
    ValueFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        try {
            field.set(obj, reader.readValueObject(getType()));
        } catch (IllegalAccessException ex) {
            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {

        try {
            writer.writeValueObject(field.get(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(final Object orig, final Object copy, CopyState state) {
        try {
            field.set(copy, state.copy(field.get(orig)));
        } catch (CopyRecursionException e) {
            state.registerRecursion(new CopyRecursionResolver(orig) {
                public void resolve(Object value) {
                    try {
                        field.set(copy, value);
                    } catch (IllegalAccessException ex) {
                        throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
                    }
                }
            });
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        java.io.Serializable value = (java.io.Serializable) reader
                .readValueObject();
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        java.io.Serializable value = (java.io.Serializable) map
                .get(getJavaName());
        writer.writeValueObject(value);
    }

}

class StringFieldDescriptor extends FieldDescriptor {
    StringFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        try {
            String value = (String) reader.readValueObject();
            field.set(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        try {
            writer.writeValueObject(field.get(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.set(copy, field.get(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        String value = (String) reader.readValueObject();
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        String value = (String) map.get(getJavaName());
        writer.writeValueObject(value);
    }

}

class ObjectFieldDescriptor extends FieldDescriptor {
    ObjectFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        try {
            field.set(obj, reader.readAbstractObject());

        } catch (IllegalAccessException ex) {
            throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(ex.getMessage()).initCause(ex);
        }

    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        try {
            writer.writeObject(field.get(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(final Object orig, final Object copy, CopyState state) {
        try {
            field.set(copy, state.copy(field.get(orig)));
        } catch (CopyRecursionException e) {
            state.registerRecursion(new CopyRecursionResolver(orig) {
                public void resolve(Object value) {
                    try {
                        field.set(copy, value);
                    } catch (IllegalAccessException ex) {
                        throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
                    }
                }
            });
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Object value = (Object) reader.readAbstractObject();
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Object value = (Object) map.get(getJavaName());
        writer.writeObject(value);
    }

}

class BooleanFieldDescriptor extends FieldDescriptor {
    BooleanFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            boolean value = reader.readBoolean();
            field.setBoolean(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeBoolean(field.getBoolean(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setBoolean(copy, field.getBoolean(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            pw.print(field.getBoolean(val));
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Boolean value = new Boolean(reader.readBoolean());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Boolean value = (Boolean) map.get(getJavaName());
        if (value == null) {
            writer.writeBoolean(false);
        } else {
            writer.writeBoolean(value.booleanValue());
        }
    }

}

class ByteFieldDescriptor extends FieldDescriptor {
    ByteFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            byte value = reader.readByte();
            field.setByte(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeByte(field.getByte(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setByte(copy, field.getByte(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            pw.print(field.getByte(val));
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Byte value = new Byte(reader.readByte());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Byte value = (Byte) map.get(getJavaName());
        if (value == null) {
            writer.writeByte(0);
        } else {
            writer.writeByte(value.byteValue());
        }
    }

}

class ShortFieldDescriptor extends FieldDescriptor {
    ShortFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            short value = reader.readShort();
            field.setShort(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeShort(field.getShort(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setShort(copy, field.getShort(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            pw.print(field.getShort(val));
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Short value = new Short(reader.readShort());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Short value = (Short) map.get(getJavaName());
        if (value == null) {
            writer.writeShort(0);
        } else {
            writer.writeShort(value.shortValue());
        }
    }

}

class CharFieldDescriptor extends FieldDescriptor {
    CharFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            char value = reader.readChar();
            field.setChar(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeChar(field.getChar(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setChar(copy, field.getChar(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            char ch = field.getChar(val);
            pw.print(ch);
            pw.print('(');
            pw.print(Integer.toHexString(0xffff & ((int) ch)));
            pw.print(')');
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Character value = new Character(reader.readChar());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Character value = (Character) map.get(getJavaName());
        if (value == null) {
            writer.writeChar(0);
        } else {
            writer.writeChar(value.charValue());
        }
    }

}

class IntFieldDescriptor extends FieldDescriptor {
    IntFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            int value = reader.readInt();
            logger.finest("Read int field value " + value); 
            field.setInt(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeInt(field.getInt(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setInt(copy, field.getInt(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            pw.print(field.getInt(val));
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Integer value = new Integer(reader.readInt());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Integer value = (Integer) map.get(getJavaName());
        if (value == null) {
            writer.writeInt(0);
        } else {
            writer.writeInt(value.intValue());
        }
    }

}

class LongFieldDescriptor extends FieldDescriptor {
    LongFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            long value = reader.readLong();
            logger.finest("Read long field value " + value); 
            field.setLong(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeLong(field.getLong(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setLong(copy, field.getLong(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            pw.print(field.getLong(val));
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Long value = new Long(reader.readLong());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Long value = (Long) map.get(getJavaName());
        if (value == null) {
            writer.writeLong(0);
        } else {
            writer.writeLong(value.longValue());
        }
    }

}

class FloatFieldDescriptor extends FieldDescriptor {
    FloatFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            float value = reader.readFloat();
            field.setFloat(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeFloat(field.getFloat(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setFloat(copy, field.getFloat(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            pw.print(field.getFloat(val));
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Float value = new Float(reader.readFloat());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Float value = (Float) map.get(getJavaName());
        if (value == null) {
            writer.writeFloat(0.0F);
        } else {
            writer.writeFloat(value.floatValue());
        }
    }

}

class DoubleFieldDescriptor extends FieldDescriptor {
    DoubleFieldDescriptor(Class owner, Class type, String name,
            java.lang.reflect.Field f) {
        super(owner, type, name, f);
    }

    public void read(ObjectReader reader, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            double value = reader.readDouble();
            field.setDouble(obj, value);
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    public void write(ObjectWriter writer, Object obj)
            throws java.io.IOException {
        if (field == null) {
            throw new IOException(
                    "cannot read/write using serialPersistentFields");
        }
        try {
            writer.writeDouble(field.getDouble(obj));
        } catch (IllegalAccessException ex) {
            throw (IOException)new IOException(ex.getMessage()).initCause(ex);
        }
    }

    void copyState(Object orig, Object copy, CopyState state) {
        try {
            field.setDouble(copy, field.getDouble(orig));
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void print(java.io.PrintWriter pw, java.util.Map recurse, Object val) {
        try {
            pw.print(getJavaName());
            pw.print("=");
            pw.print(field.getDouble(val));
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#readField(ObjectReader, Map)
     */
    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
        Double value = new Double(reader.readDouble());
        map.put(getJavaName(), value);
    }

    /**
     * @see org.apache.yoko.rmi.impl.FieldDescriptor#writeField(ObjectWriter, Map)
     */
    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        Double value = (Double) map.get(getJavaName());
        if (value == null) {
            writer.writeDouble(0.0D);
        } else {
            writer.writeDouble(value.doubleValue());
        }
    }

}

class CorbaObjectFieldDescriptor extends FieldDescriptor {

    protected CorbaObjectFieldDescriptor(Class owner, Class type, String name, Field f) {
	super(owner, type, name, f);
    }

    void copyState(final Object orig, final Object copy, CopyState state) {
        try {
            field.set(copy, state.copy(field.get(orig)));
        } catch (CopyRecursionException e) {
            state.registerRecursion(new CopyRecursionResolver(orig) {
                public void resolve(Object value) {
                    try {
                        field.set(copy, value);
                    } catch (IllegalAccessException ex) {
                        throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
                    }
                }
            });
        } catch (IllegalAccessException ex) {
            throw (InternalError)new InternalError(ex.getMessage()).initCause(ex);
        }
    }

    void read(ObjectReader reader, Object obj) throws IOException {
        Object value = reader.readCorbaObject(null);
        try {
	    field.set(obj, value);
	} catch (IllegalAccessException e) {
	    throw (IOException)new IOException(e.getMessage()).initCause(e);
	}
    }

    void readFieldIntoMap(ObjectReader reader, Map map) throws IOException {
	Object value = reader.readCorbaObject(null);
	map.put(getJavaName(), value);
	
    }

    void write(ObjectWriter writer, Object obj) throws IOException {
	try {
	    writer.writeCorbaObject(field.get(obj));
	}
	catch(IllegalAccessException e) {
	    throw (IOException)new IOException(e.getMessage()).initCause(e);
	}
    }

    void writeFieldFromMap(ObjectWriter writer, Map map) throws IOException {
        org.omg.CORBA.Object value = (org.omg.CORBA.Object) map.get(getJavaName());
        writer.writeCorbaObject(value);
	
    }
    
}

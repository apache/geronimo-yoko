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

import java.io.Externalizable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

abstract class ArrayDescriptor extends ValueDescriptor {
    final Class elementType;
    final Class basicType;
    private final int order;

    protected ArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, rep);
        logger.fine("Creating an array descriptor for type " + type.getName() + " holding elements of " + elemType.getName());
        this.elementType = elemType;

        int order = 1;
        Class basicType = elemType;
        while (basicType.isArray()) {
            basicType = basicType.getComponentType();
            order++;
        }
        this.basicType = basicType;
        this.order = order;
    }

    @Override
    protected String genRepId() {
        if (elementType.isPrimitive() || elementType == Object.class)
            return String.format("RMI:%s:%016X", type.getName(), 0);

        TypeDescriptor desc = repo.getDescriptor(elementType);
        String elemRep = desc.getRepositoryID();
        String hash = elemRep.substring(elemRep.indexOf(':', 4));
        return String.format("RMI:%s:%s", type.getName(), hash);
    }

    // repository ID for the contained elements
    private volatile String _elementRepid = null;
    private final String genElemRepId() {
        if (elementType.isPrimitive() || elementType == Object.class) {
            // use the descriptor type past the array type marker
            return String.format("RMI:%s:%016X", type.getName().substring(1), 0);
        }
        return repo.getDescriptor(elementType).getRepositoryID();
    }

    public String getElementRepositoryID() {
        if (_elementRepid == null) _elementRepid = genElemRepId();
        return _elementRepid;
    }

    @Override
    protected final String genIDLName() {
        StringBuffer sb = new StringBuffer("org_omg_boxedRMI_");

        TypeDescriptor desc = repo.getDescriptor(basicType);
        
        // The logic that looks for the last "_" fails when this is a 
        // long_long primitive type.  The primitive types have a "" package 
        // name, so check those first.  If it's not one of the primitives, 
        // then we can safely split using the last index position.
        String pkgName = desc.getPackageName(); 
        if (pkgName.length() == 0) {
            sb.append("seq");
            sb.append(order);
            sb.append('_');
            sb.append(desc.getTypeName());
        }
        else {
            String elemName = desc.getIDLName();

            int idx = elemName.lastIndexOf('_');

            pkgName = elemName.substring(0, idx + 1);
            String elmName = elemName.substring(idx + 1);

            sb.append(pkgName);

            sb.append("seq");
            sb.append(order);
            sb.append('_');

            sb.append(elmName);
        }

        return sb.toString();
    }

    static ArrayDescriptor get(final Class type, TypeRepository rep) {
        logger.fine("retrieving an array descriptor for class " + type.getName());
        if (!type.isArray()) {
            throw new IllegalArgumentException("type is not an array");
        }

        Class elemType = type.getComponentType();

        if (elemType.isPrimitive()) {
            if (elemType == Boolean.TYPE) {
                return new BooleanArrayDescriptor(type, elemType, rep);
            } else if (elemType == Byte.TYPE) {
                return new ByteArrayDescriptor(type, elemType, rep);
            } else if (elemType == Character.TYPE) {
                return new CharArrayDescriptor(type, elemType, rep);
            } else if (elemType == Short.TYPE) {
                return new ShortArrayDescriptor(type, elemType, rep);
            } else if (elemType == Integer.TYPE) {
                return new IntArrayDescriptor(type, elemType, rep);
            } else if (elemType == Long.TYPE) {
                return new LongArrayDescriptor(type, elemType, rep);
            } else if (elemType == Float.TYPE) {
                return new FloatArrayDescriptor(type, elemType, rep);
            } else if (elemType == Double.TYPE) {
                return new DoubleArrayDescriptor(type, elemType, rep);
            } else {
                throw new RuntimeException("unknown array type " + type);
            }
        }
        if (Serializable.class.equals(elemType) ||
                Externalizable.class.equals(elemType) || Object.class.equals(elemType)) {
            return new ObjectArrayDescriptor(type, elemType, rep);
        } else if (Serializable.class.isAssignableFrom(elemType)) {
            return new ValueArrayDescriptor(type, elemType, rep);
        } else if (Remote.class.isAssignableFrom(elemType)) {
            return new RemoteArrayDescriptor(type, elemType, rep);
        } else {
            return new AbstractObjectArrayDescriptor(type, elemType, rep);
        }
    }

    /**
     * Read an instance of this value from a CDR stream. Overridden to provide a
     * specific type
     */
    @Override
    public Object read(InputStream in) {
        org.omg.CORBA_2_3.portable.InputStream _in = (org.omg.CORBA_2_3.portable.InputStream) in;
        logger.fine("Reading an array value with repository id " + getRepositoryID() + " java class is " + type);

        // if we have a resolved class, read using that, otherwise fall back on the
        // repository id.
        return ((null == type) ? _in.read_value(getRepositoryID()) : _in.read_value(type));
    }

    /** Write an instance of this value to a CDR stream */
    @Override
    public void write(OutputStream out, Object value) {
        org.omg.CORBA_2_3.portable.OutputStream _out = (org.omg.CORBA_2_3.portable.OutputStream) out;

        _out.write_value((Serializable)value, getRepositoryID());
    }

    @Override
    protected final ValueMember[] genValueMembers() {
        final ValueMember[] members = new ValueMember[1];
        final TypeDescriptor elemDesc = repo.getDescriptor(elementType);
        final String elemRepID = elemDesc.getRepositoryID();

        final ORB orb = ORB.init();
        TypeCode memberTC = orb.create_sequence_tc(0, elemDesc.getTypeCode());

        members[0] = new ValueMember("", // member has no name!
                    elemRepID, this.getRepositoryID(), "1.0", memberTC, null,
                    (short) 1);

        return members;
    }

    @Override
    void addDependencies(Set classes) {
        repo.getDescriptor(basicType).addDependencies(classes);
    }

    final CorbaObjectReader makeCorbaObjectReader(final InputStream in, final Map offsetMap, final Serializable obj)
            throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<CorbaObjectReader>() {
                public CorbaObjectReader run() throws IOException {
                    return new CorbaObjectReader(in, offsetMap, obj);
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException)e.getException();
        }
    }
}

class ObjectArrayDescriptor extends ArrayDescriptor {
    static Logger logger = Logger.getLogger(ArrayDescriptor.class.getName());

    ObjectArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public void writeValue(OutputStream out, Serializable value) {
        // System.out.println ("ObjectArrayDescriptor::writeValue
        // "+getRepositoryID ());

        Object[] arr = (Object[]) value;
        out.write_long(arr.length);

        logger.finer("writing " + type.getName() + " size="
                + arr.length);

        for (int i = 0; i < arr.length; i++) {
            javax.rmi.CORBA.Util.writeAny(out, arr[i]); 
        }
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        try {
            ObjectReader reader = makeCorbaObjectReader(in, offsetMap, null);

            int length = reader.readInt();
            Object[] arr = (Object[]) Array.newInstance(elementType, length);

            offsetMap.put(key, arr);

            logger.fine("reading " + type.getName() + " size="
                    + arr.length);

            for (int i = 0; i < length; i++) {
                try {
                    arr[i] = reader.readAny();
                    if (arr[i] != null) {
                        logger.finer("Array item " + i + " is of type " + arr[i].getClass().getName()); 
                    }
                    else {
                        logger.finer("Array item " + i + " is null"); 
                    }
                } catch (IndirectionException ex) {
                    arr[i] = offsetMap.get(new Integer(ex.offset));
                    // reader.addValueBox (ex.offset, new ArrayBox (i, arr));
                }
            }
            return arr;

        } catch (IOException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }

    }

    @Override
    Object copyObject(Object value, CopyState state) {
        final Object[] orig = (Object[]) value;
        final Object[] result = new Object[orig.length];
        state.put(value, result);

        for (int i = 0; i < orig.length; i++) {
            try {
                result[i] = state.copy(orig[i]);
            } catch (CopyRecursionException e) {
                final int idx = i;

                state.registerRecursion(new CopyRecursionResolver(orig[i]) {
                    public void resolve(Object value) {
                        result[idx] = value;
                    }
                });
            }
        }

        return result;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        Object[] arr = (Object[]) val;
        TypeDescriptor desc = repo.getDescriptor(elementType);
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            desc.print(pw, recurse, arr[i]);
        }
    }

}

class RemoteArrayDescriptor extends ArrayDescriptor {
    RemoteArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        Object[] arr = (Object[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            javax.rmi.CORBA.Util.writeRemoteObject(out, arr[i]);
        }
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        try {
            ObjectReader reader = makeCorbaObjectReader(in, offsetMap, null);

            int length = reader.readInt();
            Object[] arr = (Object[]) Array.newInstance(elementType, length);
            offsetMap.put(key, arr);

            for (int i = 0; i < length; i++) {
                try {
                    arr[i] = reader.readRemoteObject(elementType);
                } catch (IndirectionException ex) {
                    arr[i] = offsetMap.get(new Integer(ex.offset));
                    // reader.addValueBox (ex.offset, new ArrayBox (i, arr));
                }
            }

            return arr;

        } catch (IOException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }

    }

    @Override
    Object copyObject(Object value, CopyState state) {
        final Object[] orig = (Object[]) value;
        final Object[] result = (Object[]) Array.newInstance(elementType,
                orig.length);

        state.put(value, result);
        for (int i = 0; i < orig.length; i++) {
            try {
                result[i] = state.copy(orig[i]);
            } catch (CopyRecursionException e) {
                final int idx = i;

                state.registerRecursion(new CopyRecursionResolver(orig[i]) {
                    public void resolve(Object value) {
                        result[idx] = value;
                    }
                });
            }
        }

        return result;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        Object[] arr = (Object[]) val;
        TypeDescriptor desc = repo.getDescriptor(elementType);
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            desc.print(pw, recurse, arr[i]);
        }
    }
}

class ValueArrayDescriptor extends ArrayDescriptor {

    ValueArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        Object[] arr = (Object[]) value;
        out.write_long(arr.length);
        Serializable[] sarr = (Serializable[]) arr;
        org.omg.CORBA_2_3.portable.OutputStream _out = (org.omg.CORBA_2_3.portable.OutputStream) out;
        for (int i = 0; i < sarr.length; i++) {
            _out.write_value(sarr[i], getElementRepositoryID());
        }
    }

    @Override
    public Serializable readValue(InputStream in, Map<Integer, Serializable> offsetMap, Integer key) {
        final int length = in.read_long();
        Object[] arr = (Object[]) Array.newInstance(elementType, length);
        offsetMap.put(key, arr);

        final org.omg.CORBA_2_3.portable.InputStream _in = (org.omg.CORBA_2_3.portable.InputStream) in;
        for (int i = 0; i < length; i++) {
            try {
                arr[i] = _in.read_value(elementType);
            } catch (IndirectionException ex) {
                arr[i] = offsetMap.get(new Integer(ex.offset));
            }
        }

        return arr;
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        Object[] orig = (Object[]) value;
        final Object[] result = (Object[]) Array.newInstance(value.getClass()
                .getComponentType(), orig.length);

        state.put(value, result);
        for (int i = 0; i < orig.length; i++) {
            try {
                result[i] = state.copy(orig[i]);
            } catch (CopyRecursionException e) {
                final int idx = i;

                state.registerRecursion(new CopyRecursionResolver(orig[i]) {
                    public void resolve(Object value) {
                        result[idx] = value;
                    }
                });
            }
        }

        return result;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        Object[] arr = (Object[]) val;
        TypeDescriptor desc = repo.getDescriptor(elementType);
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            desc.print(pw, recurse, arr[i]);
        }
    }

}

class AbstractObjectArrayDescriptor extends ArrayDescriptor {
    AbstractObjectArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {

        Object[] arr = (Object[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            javax.rmi.CORBA.Util.writeAbstractObject(out, arr[i]);
        }
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        try {
            ObjectReader reader = makeCorbaObjectReader(in, offsetMap, null);

            int length = reader.readInt();
            Object[] arr = (Object[]) Array.newInstance(elementType, length);

            offsetMap.put(key, arr);

            for (int i = 0; i < length; i++) {
                try {
                    arr[i] = reader.readAbstractObject();
                } catch (IndirectionException ex) {
                    arr[i] = offsetMap.get(new Integer(ex.offset));
                    // reader.addValueBox (ex.offset, new ArrayBox (i, arr));
                }
            }

            return arr;

        } catch (IOException ex) {
            throw (MARSHAL)new MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        final Object[] orig = (Object[]) value;
        final Object[] result = (Object[]) Array.newInstance(elementType,
                orig.length);

        state.put(value, result);
        for (int i = 0; i < orig.length; i++) {
            try {
                result[i] = state.copy(orig[i]);
            } catch (CopyRecursionException e) {
                final int idx = i;

                state.registerRecursion(new CopyRecursionResolver(orig[i]) {
                    public void resolve(Object value) {
                        result[idx] = value;
                    }
                });
            }
        }

        return result;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        Object[] arr = (Object[]) val;
        TypeDescriptor desc = repo.getDescriptor(elementType);
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            desc.print(pw, recurse, arr[i]);
        }
    }

}

class BooleanArrayDescriptor extends ArrayDescriptor {
    BooleanArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        boolean[] arr = new boolean[in.read_long()];
        offsetMap.put(key, arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.read_boolean();
        }
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        boolean[] arr = (boolean[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            out.write_boolean(arr[i]);
        }
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((boolean[]) value).length == 0)
            return value;

        Object copy = ((boolean[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        boolean[] arr = (boolean[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }

}

class ByteArrayDescriptor extends ArrayDescriptor {
    ByteArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        byte[] arr = new byte[in.read_long()];
        offsetMap.put(key, arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.read_octet();
        }
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        byte[] arr = (byte[]) value;
        out.write_long(arr.length);

        out.write_octet_array(arr, 0, arr.length);
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((byte[]) value).length == 0)
            return value;

        Object copy = ((byte[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        byte[] arr = (byte[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }
}

class CharArrayDescriptor extends ArrayDescriptor {
    CharArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        int len = in.read_long();
        char[] arr = new char[len];
        offsetMap.put(key, arr);
        in.read_wchar_array(arr, 0, len);
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        char[] arr = (char[]) value;
        out.write_long(arr.length);
        out.write_wchar_array(arr, 0, arr.length);
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((char[]) value).length == 0)
            return value;

        Object copy = ((char[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        char[] arr = (char[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }
}

class ShortArrayDescriptor extends ArrayDescriptor {
    ShortArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        short[] arr = new short[in.read_long()];
        offsetMap.put(key, arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.read_short();
        }
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        short[] arr = (short[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            out.write_short(arr[i]);
        }
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((short[]) value).length == 0)
            return value;

        Object copy = ((short[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        short[] arr = (short[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }
}

class IntArrayDescriptor extends ArrayDescriptor {
    IntArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        int[] arr = new int[in.read_long()];
        offsetMap.put(key, arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.read_long();
        }
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        int[] arr = (int[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            out.write_long(arr[i]);
        }
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((int[]) value).length == 0)
            return value;

        Object copy = ((int[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        int[] arr = (int[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }
}

class LongArrayDescriptor extends ArrayDescriptor {
    LongArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        long[] arr = new long[in.read_long()];
        offsetMap.put(key, arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.read_longlong();
        }
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        long[] arr = (long[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            out.write_longlong(arr[i]);
        }
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((long[]) value).length == 0)
            return value;

        Object copy = ((long[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        long[] arr = (long[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }

}

class FloatArrayDescriptor extends ArrayDescriptor {
    FloatArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        float[] arr = new float[in.read_long()];
        offsetMap.put(key, arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.read_float();
        }
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        float[] arr = (float[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            out.write_float(arr[i]);
        }
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((float[]) value).length == 0)
            return value;

        Object copy = ((float[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        float[] arr = (float[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }
}

class DoubleArrayDescriptor extends ArrayDescriptor {
    DoubleArrayDescriptor(Class type, Class elemType, TypeRepository rep) {
        super(type, elemType, rep);
    }

    @Override
    public Serializable readValue(
            InputStream in, Map<Integer, Serializable> offsetMap,
            Integer key) {
        double[] arr = new double[in.read_long()];
        offsetMap.put(key, arr);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = in.read_double();
        }
        return arr;
    }

    @Override
    public void writeValue(OutputStream out,
            Serializable value) {
        double[] arr = (double[]) value;
        out.write_long(arr.length);
        for (int i = 0; i < arr.length; i++) {
            out.write_double(arr[i]);
        }
    }

    @Override
    Object copyObject(Object value, CopyState state) {
        if (((double[]) value).length == 0)
            return value;

        Object copy = ((double[]) value).clone();
        state.put(value, copy);
        return copy;
    }

    @Override
    void printFields(PrintWriter pw, Map recurse, Object val) {
        double[] arr = (double[]) val;
        pw.print("length=" + arr.length + "; ");
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                pw.print(", ");
            }
            pw.print(arr[i]);
        }
    }
}

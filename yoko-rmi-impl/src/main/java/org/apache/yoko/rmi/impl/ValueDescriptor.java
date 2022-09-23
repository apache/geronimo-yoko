/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.yoko.rmi.impl;

import org.apache.yoko.rmi.util.StringUtil;
import org.omg.CORBA.AttributeDescription;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.Initializer;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.OperationDescription;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.VM_NONE;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;
import org.omg.SendingContext.RunTime;
import sun.reflect.ReflectionFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.ObjectStreamField;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;
import static org.apache.yoko.util.Exceptions.as;

class ValueDescriptor extends TypeDescriptor {
    static final Logger logger = Logger.getLogger(ValueDescriptor.class.getName());

    private boolean _is_externalizable;

    private boolean _is_serializable;

    private Method _write_replace_method;

    private Method _read_resolve_method;

    private Constructor _constructor;

    private Method _write_object_method;

    private Method _read_object_method;

    private Field _serial_version_uid_field;

    protected ValueDescriptor _super_descriptor;

    protected FieldDescriptor[] _fields;

    private ObjectDeserializer _object_deserializer;

    private boolean _is_immutable_value;

    private boolean _is_rmi_stub;

    private String _custom_repid;

    private static final Set<? extends Class<? extends Serializable>> _immutable_value_classes = unmodifiableSet(new HashSet<>(asList(Integer.class,
            Character.class, Boolean.class, Byte.class, Long.class, Float.class, Double.class, Short.class)));

    private long _hash_code;

    ValueDescriptor(Class type, TypeRepository repository) {
        super(type, repository);
    }

    protected boolean isEnum() { return false; }

    @Override
    protected final RemoteInterfaceDescriptor genRemoteInterface() {
        if (!!!java.rmi.Remote.class.isAssignableFrom(type)) return super.genRemoteInterface();
        return RemoteDescriptor.genMostSpecificRemoteInterface(type, repo);
    }

    @Override
    protected String genRepId() {
        return String.format("RMI:%s:%016X:%016X", StringUtil.convertToValidIDLNames(type.getName()),
                _hash_code, getSerialVersionUID());
    }

    private String genCustomRepId() {
        return String.format("RMI:org.omg.custom.%s", getRepositoryID().substring(4));
    }

    public final String getCustomRepositoryID() {
        if (_custom_repid == null) _custom_repid = genCustomRepId();
        return _custom_repid;
    }

    protected long getSerialVersionUID() {
        if (_serial_version_uid_field != null) {

            try {
                return _serial_version_uid_field.getLong(null);
            } catch (IllegalAccessException ex) {
                // skip //
            }
        }
        ObjectStreamClass serialForm = ObjectStreamClass.lookup(type);

        return (serialForm != null) ? serialForm.getSerialVersionUID() : 0L;
    }

    public void init() {
        try {
            init0();
            super.init();

            if (_fields == null) {
                throw new RuntimeException("fields==null after init!");
            }

        } catch (INTERNAL internal) {
            throw internal;
        } catch (RuntimeException | Error ex) {
            throw as(INTERNAL::new, ex);
        }
    }

    private void init0() {
        final Class<?> superClass = type.getSuperclass();

        _is_rmi_stub = RMIStub.class.isAssignableFrom(type);
        _is_externalizable = Externalizable.class.isAssignableFrom(type);
        _is_serializable = Serializable.class.isAssignableFrom(type);

        _is_immutable_value = _immutable_value_classes.contains(type);

        if ((superClass != null) && (superClass != Object.class)) {
            TypeDescriptor superDesc = repo.getDescriptor(superClass);

            if (superDesc instanceof ValueDescriptor) {
                _super_descriptor = (ValueDescriptor) superDesc;
            }

        }

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {

                for (Class<?> curr = type; curr != null; curr = curr.getSuperclass()) {
                    try {
                        _write_replace_method = curr.getDeclaredMethod("writeReplace");
                        _write_replace_method.setAccessible(true);

                        break;
                    } catch (NoSuchMethodException ignored) {
                    }
                }

                try {
                    _read_resolve_method = type.getDeclaredMethod("readResolve");
                    _read_resolve_method.setAccessible(true);

                } catch (NoSuchMethodException ignored) {
                }

                try {
                    _read_object_method = type.getDeclaredMethod("readObject", ObjectInputStream.class);
                    _read_object_method.setAccessible(true);
                } catch (NoSuchMethodException ignored) {
                }

                try {
                    _write_object_method = type.getDeclaredMethod("writeObject", ObjectOutputStream.class);
                    _write_object_method.setAccessible(true);
                } catch (NoSuchMethodException ignored) {
                }

                if ((_write_object_method == null) || !Modifier.isPrivate(_write_object_method.getModifiers())
                        || Modifier.isStatic(_write_object_method.getModifiers()) || (_write_object_method.getDeclaringClass() != type)) {

                    _write_object_method = null;

                }

                if ((_read_object_method == null) || !Modifier.isPrivate(_read_object_method.getModifiers())
                        || Modifier.isStatic(_read_object_method.getModifiers())) {

                    _read_object_method = null;
                }

                try {
                    _serial_version_uid_field = type.getDeclaredField("serialVersionUID");
                    if (Modifier.isStatic(_serial_version_uid_field.getModifiers())) {
                        _serial_version_uid_field.setAccessible(true);
                    } else {
                        _serial_version_uid_field = null;
                    }
                } catch (NoSuchFieldException ex) {
                }

                ObjectStreamField[] serial_persistent_fields = null;
                try {
                    Field _serial_persistent_fields_field = type.getDeclaredField("serialPersistentFields");
                    _serial_persistent_fields_field.setAccessible(true);

                    serial_persistent_fields = (ObjectStreamField[]) _serial_persistent_fields_field.get(null);

                } catch (IllegalAccessException | NoSuchFieldException ex) {
                }

                if (_is_externalizable) {
                    try {
                        _constructor = type.getDeclaredConstructor();
                        _constructor.setAccessible(true);

                    } catch (NoSuchMethodException ex) {
                        logger.log(Level.WARNING, "Class " + type.getName() + " is not properly externalizable.  "
                                + "It has not default constructor.", ex);
                    }

                } else if (_is_serializable && !type.isInterface()) {

                    Class<?> initClass = type;

                    while ((initClass != null) && Serializable.class.isAssignableFrom(initClass)) {
                        initClass = initClass.getSuperclass();
                    }

                    if (initClass == null) {
                        logger.warning("Class " + type.getName() + " is not properly serializable.  " + "It has no non-serializable super-class");
                    } else {
                        try {
                            Constructor init_cons = initClass.getDeclaredConstructor();

                            if (Modifier.isPublic(init_cons.getModifiers()) || Modifier.isProtected(init_cons.getModifiers())) {
                                // do nothing - it's accessible

                            } else if (!samePackage(type, initClass)) {
                                logger.warning("Class " + type.getName() + " is not properly serializable.  "
                                        + "The default constructor of its first " + "non-serializable super-class (" + initClass.getName()
                                        + ") is not accessible.");
                            }

                            _constructor = ReflectionFactory.getReflectionFactory().newConstructorForSerialization(type, init_cons);

                            if (_constructor == null) {
                                logger.warning("Unable to get constructor for serialization for class " + java_name);
                            } else {
                                _constructor.setAccessible(true);
                            }

                        } catch (NoSuchMethodException ex) {
                            logger.log(Level.WARNING, "Class " + type.getName() + " is not properly serializable.  "
                                    + "First non-serializable super-class (" + initClass.getName() + ") has no default constructor.", ex);
                        }
                    }
                }

                if (serial_persistent_fields == null) {
                    Field[] ff = type.getDeclaredFields();

                    if ((!Serializable.class.isAssignableFrom(type)) || (ff == null) || (ff.length == 0)) {
                        _fields = new FieldDescriptor[0];

                    } else {
                        List<FieldDescriptor> flist = new ArrayList<>();

                        for (Field f : ff) {
                            int mod = f.getModifiers();
                            if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                                continue;
                            }

                            f.setAccessible(true);
                            FieldDescriptor fd = FieldDescriptor.get(f, repo);
                            flist.add(fd);
                        }

                        _fields = new FieldDescriptor[flist.size()];
                        _fields = flist.toArray(_fields);

                        Arrays.sort(_fields);
                    }
                } else {
                    _fields = new FieldDescriptor[serial_persistent_fields.length];

                    for (int i = 0; i < serial_persistent_fields.length; i++) {
                        ObjectStreamField f = serial_persistent_fields[i];

                        FieldDescriptor fd = null;

                        try {
                            Field rf = type.getField(f.getName());
                            rf.setAccessible(true);

                            if (rf.getType() == f.getType()) {
                                fd = FieldDescriptor.get(rf,repo);
                            }
                        } catch (SecurityException | NoSuchFieldException ex) {
                        }

                        if (fd == null) {
                            fd = FieldDescriptor.getForSerialPersistentField(type, f, repo);
                        }
                        _fields[i] = fd;
                    }
                    Arrays.sort(_fields);
                }

                _hash_code = computeHashCode();

                _object_deserializer = new ObjectDeserializer(ValueDescriptor.this);

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
        return (idx == -1) ? "" : name.substring(0, idx);
    }

    /** Read an instance of this value from a CDR stream */
    public Object read(org.omg.CORBA.portable.InputStream in) {
        return ((org.omg.CORBA_2_3.portable.InputStream) in).read_value();
    }

    /** Write an instance of this value to a CDR stream */
    public void write(OutputStream out, Object value) {
        ((org.omg.CORBA_2_3.portable.OutputStream) out).write_value((Serializable) value);
    }

    public boolean isCustomMarshalled() {
        return (_is_externalizable || (_write_object_method != null));
    }

    public boolean isChunked() {
        if (isCustomMarshalled()) return true;
        return (_super_descriptor != null) && _super_descriptor.isChunked();
    }

    public Serializable writeReplace(Serializable val) {
        if (_write_replace_method != null) {
            try {
                return (Serializable) _write_replace_method.invoke(val);

            } catch (IllegalAccessException ex) {
                throw (MARSHAL) new MARSHAL("cannot call " + _write_replace_method).initCause(ex);

            } catch (IllegalArgumentException ex) {
                throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InvocationTargetException ex) {
                throw (UnknownException) new UnknownException(ex.getTargetException()).initCause(ex.getTargetException());
            }

        }

        return val;
    }

    public Serializable readResolve(Serializable val) {
        if (_read_resolve_method != null) {
            try {
                return (Serializable) _read_resolve_method.invoke(val);

            } catch (IllegalAccessException ex) {
                throw (MARSHAL) new MARSHAL("cannot call " + _read_resolve_method).initCause(ex);

            } catch (IllegalArgumentException ex) {
                throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InvocationTargetException ex) {
                throw (UnknownException) new UnknownException(ex.getTargetException()).initCause(ex.getTargetException());
            }

        }

        return val;
    }

    public void writeValue(final OutputStream out, final Serializable value) {
        try {

            ObjectWriter writer = (ObjectWriter) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    try {
                        return new CorbaObjectWriter(out, value);
                    } catch (IOException ex) {
                        throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);
                    }
                }
            });

            writeValue(writer, value);

        } catch (IOException ex) {
            throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);
        }
    }

    protected void defaultWriteValue(ObjectWriter writer, Serializable val) throws IOException {
        logger.finer("writing fields for " + type);
        FieldDescriptor[] fields = _fields;

        if (fields == null) {
            return;
        }

        for (int i = 0; i < fields.length; i++) {
            logger.finer("writing field " + _fields[i].java_name);

            fields[i].write(writer, val);
        }
    }

    protected void writeValue(ObjectWriter writer, Serializable val) throws IOException {

        if (_is_externalizable) {
            writer.invokeWriteExternal((Externalizable) val);
            return;
        }

        if (_super_descriptor != null) {
            _super_descriptor.writeValue(writer, val);
        }

        if (_write_object_method != null) {

            try {
                writer.invokeWriteObject(this, val, _write_object_method);
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);
            } catch (InvocationTargetException ex) {
                throw (UnknownException) new UnknownException(ex.getTargetException()).initCause(ex.getTargetException());
            }

        } else {
            defaultWriteValue(writer, val);
        }

    }

    private Serializable createBlankInstance() {
        if (_constructor != null) {

            try {
                return (Serializable) _constructor.newInstance();

            } catch (IllegalAccessException ex) {
                throw (MARSHAL) new MARSHAL("cannot call " + _constructor).initCause(ex);

            } catch (IllegalArgumentException | InstantiationException ex) {
                throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);

            } catch (InvocationTargetException ex) {
                throw (UnknownException) new UnknownException(ex.getTargetException()).initCause(ex.getTargetException());

            } catch (NullPointerException ex) {
                logger.log(Level.WARNING, "unable to create instance of " + type.getName(), ex);
                logger.warning("constructor => " + _constructor);

                throw ex;
            }

        } else {
            return null;
        }
    }

    public Serializable readValue(final InputStream in, final Map<Integer, Serializable> offsetMap, final Integer offset) {
        final Serializable value = createBlankInstance();

        offsetMap.put(offset, value);

        try {
            ObjectReader reader = (ObjectReader) AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    try {
                        return new CorbaObjectReader(in, offsetMap, value);
                    } catch (IOException ex) {
                        throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);
                    }
                }
            });

            readValue(reader, value);

            final Serializable resolved = readResolve(value);
            if (value != resolved) {
                offsetMap.put(offset, resolved);
            }
            return resolved;

        } catch (IOException ex) {
            throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);
        }

    }

    void print(PrintWriter pw, Map<Object, Integer> recurse, Object val) {
        if (val == null) {
            pw.print("null");
        }

        Integer old = recurse.get(val);
        if (old != null) {
            pw.print("^" + old);
        } else {
            int key = System.identityHashCode(val);
            recurse.put(val, key);

            pw.println(type.getName() + "@" + Integer.toHexString(key) + "[");

            printFields(pw, recurse, val);

            pw.println("]");
        }
    }

    void printFields(PrintWriter pw, Map recurse, Object val) {
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

    protected void defaultReadValue(ObjectReader reader, Serializable value) throws IOException {
        if (null == _fields) return;

        logger.fine("reading fields for " + type.getName());

        for (FieldDescriptor _field : _fields) {
            if (null == _field) continue;
            logger.fine("reading field " + _field.java_name + " of type " + _field.getType().getName() + " using " + _field.getClass().getName());

            try {
                _field.read(reader, value);
            } catch (MARSHAL ex) {
                if (ex.getMessage() != null)
                    throw ex;

                String msg = String.format("%s, while reading %s.%s", ex, java_name, _field.java_name);
                throw (MARSHAL) new MARSHAL(msg, ex.minor, ex.completed).initCause(ex);
            }
        }
    }

    Map readFields(ObjectReader reader) throws IOException {
        if ((_fields == null) || (_fields.length == 0)) {
            return Collections.EMPTY_MAP;
        }

        logger.finer("reading fields for " + type.getName());

        Map map = new HashMap();

        for (FieldDescriptor _field : _fields) {

            logger.finer("reading field " + _field.java_name);

            _field.readFieldIntoMap(reader, map);
        }

        return map;
    }

    void writeFields(ObjectWriter writer, Map fieldMap) throws IOException {
        if ((_fields == null) || (_fields.length == 0)) {
            return;
        }

        logger.finer("writing fields for " + type.getName());

        for (FieldDescriptor _field : _fields) {

            logger.finer("writing field " + _field.java_name);

            _field.writeFieldFromMap(writer, fieldMap);
        }

    }

    /**
     * This methods reads the fields of a single class slice.
     */
    protected void readValue(ObjectReader reader, Serializable value) throws IOException {
        if (_is_externalizable) {
            try {
                reader.readExternal((Externalizable) value);
            } catch (ClassNotFoundException e) {
                throw new IOException("cannot instantiate class", e);
            }
            return;
        }

        if (_super_descriptor != null) {
            _super_descriptor.readValue(reader, value);
        }

        // check whether the class (not its ancestors) does any custom marshalling
        if (_write_object_method != null) {
            // read custom marshalling value header
            byte cmsfVersion = reader.readByte(); // custom marshal stream format version
            boolean dwoCalled = reader.readBoolean(); // was defaultWriteObject() called?
            logger.log(Level.FINE, "Reading value in streamFormatVersion=" + cmsfVersion + " defaultWriteObject=" + dwoCalled);

            if (cmsfVersion == 2) {
                // use a wrapped reader to open the secondary custom valuetype
                ObjectReader wrapper = CustomMarshaledObjectReader.wrap(reader);
                readSerializable(_read_object_method == null ? reader : wrapper, value);
                // invoke close to skip to the end of the secondary custom valuetype
                wrapper.close();
                return;
            }
        }

        readSerializable(reader, value);

    }

    private void readSerializable(ObjectReader reader, Serializable value) throws IOException {
        if (_read_object_method != null) {
            try {
                reader.setCurrentValueDescriptor(this);
                _read_object_method.invoke(value, reader);
                reader.setCurrentValueDescriptor(null);

            } catch (IllegalAccessException | IllegalArgumentException ex) {
                throw (MARSHAL) new MARSHAL(ex.getMessage()).initCause(ex);
            } catch (InvocationTargetException ex) {
                throw (UnknownException) new UnknownException(ex.getTargetException()).initCause(ex.getTargetException());
            }

        } else {
            defaultReadValue(reader, value);
        }
    }

    protected long computeHashCode() {
        Class type = this.type;

        if (_is_externalizable) {
            return 1L;
        }

        if (!Serializable.class.isAssignableFrom(type)) {
            return 0;
        }

        long hash = 0L;
        try {
            ByteArrayOutputStream barr = new ByteArrayOutputStream(512);
            MessageDigest md = MessageDigest.getInstance("SHA");
            DigestOutputStream digestout = new DigestOutputStream(barr, md);
            DataOutputStream out = new DataOutputStream(digestout);

            Class superType = type.getSuperclass();
            if (superType != null) {
                TypeDescriptor desc = repo.getDescriptor(superType);
                out.writeLong(desc.getHashCode());
            }

            if (_write_object_method == null)
                out.writeInt(1);
            else
                out.writeInt(2);

            FieldDescriptor[] fds = new FieldDescriptor[_fields.length];
            System.arraycopy(_fields, 0, fds, 0, _fields.length);

            if (fds.length > 1)
                Arrays.sort(fds, compareByName);

            for (FieldDescriptor f : fds) {
                out.writeUTF(f.java_name);
                out.writeUTF(makeSignature(f.getType()));
            }

            /*
             * Field[] fields = type.getDeclaredFields (); if (fields.length >
             * 1) java.util.Arrays.sort (fields, compareByName); for(int i = 0;
             * i < fields.length; i++) { Field f = fields[i]; int mod =
             * f.getModifiers (); if (!Modifier.isTransient(mod) &&
             * !Modifier.isStatic (mod)) { out.writeUTF(f.getName());
             * out.writeUTF( makeSignature (f.getType ())); } }
             */

            out.flush();

            byte[] data = md.digest();
            int end = Math.min(8, data.length);
            for (int j = 0; j < end; j++) {
                hash += (long) (data[j] & 0xff) << (j * 8);
            }
        } catch (Exception ex) {
            throw new RuntimeException("cannot compute RMI hash code", ex);
        }

        return hash;
    }

    private static final Comparator compareByName = new Comparator() {
        public int compare(Object f1, Object f2) {
            String n1 = ((FieldDescriptor) f1).java_name;
            String n2 = ((FieldDescriptor) f2).java_name;
            return n1.compareTo(n2);
        }
    };

    long getHashCode() {
        return _hash_code;
    }

    private volatile ValueMember[] valueMembers = null;
    protected ValueMember[] genValueMembers() {
        final ValueMember[] members = new ValueMember[_fields.length];
        for (int i = 0; i < _fields.length; i++) {
            members[i] = _fields[i].getValueMember(repo);
        }

        return members;
    }
    final ValueMember[] getValueMembers() {
        getTypeCode(); // ensure recursion through typecode
        if (null == valueMembers) {
            synchronized (repo) {
                if (null == valueMembers) valueMembers = genValueMembers();
            }
        }
        return valueMembers;
    }

    @Override
    protected TypeCode genTypeCode() {
        ORB orb = ORB.init();
        setTypeCode(orb.create_recursive_tc(getRepositoryID()));

        TypeCode _base = ((_super_descriptor == null) ? null : _super_descriptor.getTypeCode());

        TypeCode tc;
        if (type.isArray()) {
            TypeDescriptor desc = repo.getDescriptor(type.getComponentType());
            tc = desc.getTypeCode();
            tc = orb.create_sequence_tc(0, tc);
            tc = orb.create_value_box_tc(getRepositoryID(), "Sequence", tc);
        } else {
            tc = orb.create_value_tc(getRepositoryID(), type.getSimpleName(), VM_NONE.value, _base, getValueMembers());
        }

        return tc;
    }

    private static final OperationDescription[] ZERO_OPERATIONS = {};
    private static final AttributeDescription[] ZERO_ATTRIBUTES = {};
    private static final Initializer[] ZERO_INITIALIZERS = {};
    private static final String[] ZERO_STRINGS = {};
    
    FullValueDescription getFullValueDescription() {
        FullValueDescription fvd = new FullValueDescription();
        fvd.name = type.getName();
        fvd.id = getRepositoryID();
        fvd.is_abstract = false;
        fvd.is_custom = isCustomMarshalled();
        fvd.defined_in = "";
        fvd.version = "1.0";
        fvd.operations = ZERO_OPERATIONS;
        fvd.attributes = ZERO_ATTRIBUTES;
        fvd.members = getValueMembers();
        fvd.initializers = ZERO_INITIALIZERS;
        fvd.supported_interfaces = ZERO_STRINGS;
        fvd.abstract_base_values = ZERO_STRINGS;
        fvd.is_truncatable = false;
        fvd.base_value = ((_super_descriptor == null) ? "" : _super_descriptor.getRepositoryID());
        fvd.type = getTypeCode();
        return fvd;
    }

    class ObjectDeserializer {

        ObjectDeserializer super_descriptor;

        String repository_id;

        final FieldDescriptor[] fields;

        ObjectDeserializer(ValueDescriptor desc) {
            fields = desc._fields;
            repository_id = desc.getRepositoryID();

            if (desc._super_descriptor != null) {
                super_descriptor = desc._super_descriptor._object_deserializer;
            }
        }

        ObjectDeserializer(FullValueDescription desc, RunTime runtime) throws IOException {
            Class myClass = type;
            ValueMember[] members = desc.members;
            fields = new FieldDescriptor[members.length];
            for (int i = 0; i < members.length; i++) {
                Class type = getClassFromTypeCode(members[i].type);
                fields[i] = FieldDescriptor.get(myClass, type, members[i].name, null, repo);
            }

            if (!"".equals(desc.base_value)) {
                Class clz = ValueHandlerImpl.getClassFromRepositoryID(desc.base_value);
                TypeDescriptor tdesc = repo.getDescriptor(clz);

                if ((tdesc instanceof ValueDescriptor)) {
                    super_descriptor = ((ValueDescriptor) tdesc).getObjectDeserializer(desc.base_value, runtime);
                }
            }
        }
    }

    private ObjectDeserializer getObjectDeserializer(String repositoryID, RunTime runtime) throws IOException {
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

    private static Class getClassFromTypeCode(TypeCode tc) {
        return null;
    }

    public boolean copyWithinState() {
        return !(_is_immutable_value | _is_rmi_stub);
    }

    Object copyObject(Object orig, CopyState state) {

        if (_is_immutable_value || _is_rmi_stub) {
            return orig;
        }

        Serializable oorig = (Serializable) orig;

        logger.finer("copying " + orig);

        oorig = writeReplace(oorig);

        ValueDescriptor wdesc;
        if (oorig == orig) {
            wdesc = this;
        } else {
            wdesc = (ValueDescriptor) repo.getDescriptor(oorig.getClass());

            logger.finer("writeReplace -> " + type.getName());
        }

        return wdesc.copyObject2(oorig, state);
    }

    /**
     * this is called after write-replace on the type descriptor of the correct
     * type for writing
     */
    private Serializable copyObject2(Serializable oorig, CopyState state) {

        // create instance of copied object, and register
        Serializable copy = createBlankInstance();
        state.put(oorig, copy);

        // write original object
        ObjectWriter writer = writeObject(oorig, state);

        // read into copy
        return readObject(writer, copy);
    }

    private ObjectWriter writeObject(Serializable oorig, CopyState state) {
        try {
            ObjectWriter writer = state.createObjectWriter(oorig);
            writeValue(writer, oorig);
            return writer;
        } catch (IOException ex) {
            String msg = String.format("%s writing %s", ex, type.getName());
            throw (MARSHAL) new MARSHAL(msg).initCause(ex);
        }
    }

    private Serializable readObject(ObjectWriter writer, Serializable copy) {
        try {
            ObjectReader reader = writer.getObjectReader(copy);
            readValue(reader, copy);
            return readResolve(copy);
        } catch (IOException ex) {
            String msg = String.format("%s reading instance of %s", ex, type.getName());
            throw (MARSHAL) new MARSHAL(msg).initCause(ex);
        }
    }

    void writeMarshalValue(PrintWriter pw, String outName, String paramName) {
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
        MethodDescriptor.writeJavaType(pw, type);
        pw.print(".class)");
    }

    void writeUnmarshalValue(PrintWriter pw, String inName) {
        pw.print(inName);
        pw.print('.');
        pw.print("read_value");
        pw.print('(');
        MethodDescriptor.writeJavaType(pw, type);
        pw.print(".class)");
    }

    @Override
    void addDependencies(Set<Class<?>> classes) {
        Class c = type;

        if ((c == Object.class) || classes.contains(c))
            return;

        classes.add(c);

        if (c.getSuperclass() != null) {
            TypeDescriptor desc = repo.getDescriptor(c.getSuperclass());
            desc.addDependencies(classes);
        }

        Class[] ifaces = c.getInterfaces();
        for (Class iface : ifaces) {
            TypeDescriptor desc = repo.getDescriptor(iface);
            desc.addDependencies(classes);
        }

        if (_fields != null) {
            for (FieldDescriptor _field : _fields) {
                if (_field.isPrimitive())
                    continue;

                TypeDescriptor desc = repo.getDescriptor(_field.type);
                desc.addDependencies(classes);
            }
        }
    }
}

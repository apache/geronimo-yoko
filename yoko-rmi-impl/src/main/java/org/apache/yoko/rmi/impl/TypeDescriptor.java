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

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

import java.io.PrintWriter;
import java.rmi.Remote;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

abstract class TypeDescriptor extends ModelElement {
    static Logger logger = Logger.getLogger(TypeDescriptor.class.getName());

    final Class type;

    private volatile String _repid = null;

    private volatile String packageName = null;    // the package name qualifier (if any)
    protected String genPackageName() {
        int idx = java_name.lastIndexOf('.');
        return ((idx < 0) ? "" : java_name.substring(0, idx));
    }
    public final String getPackageName() {
        if (null == packageName) packageName = genPackageName();
        return packageName;
    }

    private volatile String typeName = null;       // the simple type name (minus package, if any)
    protected String genTypeName() {
        int idx = java_name.lastIndexOf('.');
        return ((idx < 0) ? java_name : java_name.substring(idx + 1));
    }
    public final String getTypeName() {
        if (null == typeName) typeName = genTypeName();
        return typeName;
    }

    private volatile FullKey key = null;
    private FullKey genKey() {
        return new FullKey(getRepositoryID(), type);
    }
    public final FullKey getKey() {
        if (null == key) key = genKey();
        return key;
    }

    public static class SimpleKey {
        private final String repid;

        public SimpleKey(String repid) {
            this.repid = repid;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((repid == null) ? 0 : repid.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof SimpleKey)) return false;
            return Objects.equals(repid, ((SimpleKey)obj).repid);
        }
    }

    public static final class FullKey extends SimpleKey {
        private final Class<?> localType;

        public FullKey(String repid, Class<?> localType) {
            super(repid);
            this.localType = localType;
        }

        @Override
        public int hashCode() {
            // must just be the same as SimpleKey's hashCode
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof SimpleKey)) return false;
            if (obj instanceof FullKey &&
                    !!!Objects.equals(localType, ((FullKey)obj).localType)) return false;
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return String.format("%s{class=\"%s\",repId=\"%s\"}",
                this.getClass().getName(), type,
                getRepositoryID());
    }

    protected TypeDescriptor(Class type, TypeRepository repository) {
        super(repository, type.getName());
        this.type = type;
    }

    @Override
    protected String genIDLName() {
        return java_name.replace('.', '_');
    }

    protected String genRepId() {
        return String.format("RMI:%s:%016X", type.getName(), 0);
    }
    public final String getRepositoryID() {
        if (_repid == null) _repid = genRepId();
        return _repid;
    }

    private volatile RemoteInterfaceDescriptor remoteInterface = null;
    protected RemoteInterfaceDescriptor genRemoteInterface() {
        throw new UnsupportedOperationException("class " + type + " does not implement " + Remote.class.getName());
    }
    final RemoteInterfaceDescriptor getRemoteInterface() {
        if (null == remoteInterface) remoteInterface = genRemoteInterface();
        return remoteInterface;
    }



    /** Read an instance of this value from a CDR stream */
    public abstract Object read(InputStream in);

    /** Write an instance of this value to a CDR stream */
    public abstract void write(OutputStream out, Object val);

    public boolean isCustomMarshalled() {
        return false;
    }

    String makeSignature(Class type) {
        if (type.isPrimitive()) {

            if (type == Boolean.TYPE) {
                return "Z";
            } else if (type == Byte.TYPE) {
                return "B";
            } else if (type == Short.TYPE) {
                return "S";
            } else if (type == Character.TYPE) {
                return "C";
            } else if (type == Integer.TYPE) {
                return "I";
            } else if (type == Long.TYPE) {
                return "J";
            } else if (type == Float.TYPE) {
                return "F";
            } else if (type == Double.TYPE) {
                return "D";
            } else if (type == Void.TYPE) {
                return "V";
            } else
                throw new RuntimeException("unknown primitive class" + type);

        } else if (type.isArray()) {
            int i = 0;
            Class elem = type;
            for (; elem.isArray(); elem = elem.getComponentType())
                i += 1;

            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < i; j++)
                sb.append('[');

            sb.append(makeSignature(elem));

            return sb.toString();
        } else {
            return "L" + (type.getName()).replace('.', '/') + ";";
        }
    }

    long getHashCode() {
        return 0L;
    }

    @Override
    protected void init() {
        typeCode = genTypeCode();
    }

    private volatile TypeCode typeCode = null;
    protected abstract TypeCode genTypeCode();
    final TypeCode getTypeCode() {
        // typeCode should have already been set from within init(), so this is just defensive
        if (null == typeCode) typeCode = genTypeCode();
        return typeCode;
    }
    protected final void setTypeCode(TypeCode tc) {
        typeCode = tc;
    }

    Object copyObject(Object value, CopyState state) {
        throw new InternalError("cannot copy " + value.getClass().getName());
    }

    void writeMarshalValue(PrintWriter pw, String outName,
            String paramName) {
        pw.print(outName);
        pw.print('.');
        pw.print("write_");
        pw.print(getIDLName());
        pw.print('(');
        pw.print(paramName);
        pw.print(')');
    }

    void writeUnmarshalValue(PrintWriter pw, String inName) {
        pw.print(inName);
        pw.print('.');
        pw.print("read_");
        pw.print(getIDLName());
        pw.print('(');
        pw.print(')');
    }

    void addDependencies(Set<Class<?>> classes) {
        return;
    }

    boolean copyInStub() {
        return true;
    }

    void print(PrintWriter pw, Map<Object,Integer> recurse, Object val) {
        if (val == null) {
            pw.print("null");
        }

        Integer old = (Integer) recurse.get(val);
        if (old != null) {
            pw.print("^" + old);
        } else {
            Integer key = new Integer(System.identityHashCode(val));
            pw.println(type.getName() + "@"
                    + Integer.toHexString(key.intValue()));
        }
    }

    /**
     * Method copyBetweenStates.
     * 
     * @return boolean
     */
    public boolean copyBetweenStates() {
        return true;
    }

    /**
     * Method copyWithinState.
     * 
     * @return boolean
     */
    public boolean copyWithinState() {
        return true;
    }

}

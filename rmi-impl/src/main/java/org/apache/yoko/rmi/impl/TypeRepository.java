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

import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.yoko.rmi.util.ByteBuffer;
import org.apache.yoko.rmi.util.ByteString;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;
import org.omg.SendingContext.RunTime;


public class TypeRepository {
    static final Logger logger = Logger.getLogger(TypeRepository.class
            .getName());

    org.omg.CORBA.ORB orb;

    java.util.Map classMap = new java.util.HashMap();

    java.util.Map repidMap = new java.util.HashMap();

    public TypeRepository(org.omg.CORBA.ORB orb) {
        this.orb = orb;
        init();
    }

    org.omg.CORBA.ORB getORB() {
        return orb;
    }

    void init() {
        TypeDescriptor desc;

        desc = new AnyDescriptor(java.lang.Object.class, this);
        synchronized (desc) {
            classMap.put(java.lang.Object.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }

        desc = new AnyDescriptor(java.lang.Object.class, this);
        synchronized (desc) {
            classMap.put(java.lang.Object.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }

        desc = new StringDescriptor(this);
        synchronized (desc) {
            classMap.put(String.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }

        desc = new ClassDescriptor(this);
        synchronized (desc) {
            classMap.put(Class.class, desc);
            classMap.put(javax.rmi.CORBA.ClassDesc.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }

        desc = new DateValueDescriptor(this);
        synchronized (desc) {
            classMap.put(java.util.Date.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }
        desc = new AnyDescriptor(java.io.Externalizable.class, this);
        synchronized (desc) {
            classMap.put(java.io.Externalizable.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }

        desc = new AnyDescriptor(java.io.Serializable.class, this);
        synchronized (desc) {
            classMap.put(java.io.Serializable.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }
        desc = new AnyDescriptor(java.rmi.Remote.class, this);
        synchronized (desc) {
            classMap.put(java.rmi.Remote.class, desc);
            desc.init();
            repidMap.put(desc.getRepositoryID(), desc);
        }
    }

    public String getRepositoryID(Class type) {
        return getDescriptor(type).getRepositoryID();
    }

    public RemoteInterfaceDescriptor getRemoteDescriptor(Class type) {
        TypeDescriptor td = getDescriptor(type);
        RemoteInterfaceDescriptor result = td.getRemoteInterface();

        if (result != null) {
            return result;
        }

        RemoteDescriptor desc;

        if (java.rmi.Remote.class.isAssignableFrom(type)) {
            if (type.isInterface()) {
                desc = new RemoteInterfaceDescriptor(type, this);
            } else {
                desc = new RemoteClassDescriptor(type, this);
            }

            desc.init();
        } else {
            throw new IllegalArgumentException("class " + type.toString()
                    + " does not implement" + " java.rmi.Remote");
        }

        result = desc.getRemoteInterface();
        td.setRemoteInterface(result);

        return result;
    }

    public TypeDescriptor getDescriptor(Class type) {
        logger.fine("Requesting type descriptor for class " + type.getName()); 
        TypeDescriptor desc = (TypeDescriptor) classMap.get(type);

        if (desc != null) {
            return desc.getSelf();
        }

        if (org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(type)
                && isIDLEntity(type)) {
            IDLEntityDescriptor idlDesc = new IDLEntityDescriptor(type, this);
            desc = idlDesc;
            synchronized (desc) {
                classMap.put(type, desc);
                idlDesc.initIDL();
            }
        } else if (java.lang.Throwable.class.isAssignableFrom(type)) {
            desc = new ExceptionDescriptor(type, this);
            synchronized (desc) {
                classMap.put(type, desc);
                desc.init();
                repidMap.put(desc.getRepositoryID(), desc);
            }

        } else if (type.isArray()) {
            desc = ArrayDescriptor.get(type, this);
            synchronized (desc) {
                classMap.put(type, desc);
                desc.init();
                repidMap.put(desc.getRepositoryID(), desc);
            }
        } else if (!type.isInterface()
                && java.io.Serializable.class.isAssignableFrom(type)) {
            desc = new ValueDescriptor(type, this);
            synchronized (desc) {
                classMap.put(type, desc);
                desc.init();
                repidMap.put(desc.getRepositoryID(), desc);
            }
        } else if (java.rmi.Remote.class.isAssignableFrom(type)) {
            if (type.isInterface()) {
                desc = new RemoteInterfaceDescriptor(type, this);
            } else {
                desc = new RemoteClassDescriptor(type, this);
            }

            synchronized (desc) {
                classMap.put(type, desc);
                desc.init();
                repidMap.put(desc.getRepositoryID(), desc);
            }
        } else if (type.isPrimitive()) {
            desc = getSimpleDescriptor(type);
            synchronized (desc) {
                classMap.put(type, desc);
                repidMap.put(desc.getRepositoryID(), desc);
            }

        } else if (Object.class.isAssignableFrom(type)) {
            if (isAbstractInterface(type)) {

                logger.finer("encoding " + type + " as abstract interface");
                desc = new AbstractObjectDescriptor(type, this);

            } else {

                logger.finer("encoding " + type + " as a abstract value");
                desc = new ValueDescriptor(type, this);
            }

            synchronized (desc) {
                classMap.put(type, desc);
                desc.init();
                repidMap.put(desc.getRepositoryID(), desc);
            }

        } else {
            throw new RuntimeException("cannot handle class " + type.getName());
        }
        
        logger.fine("Class " + type.getName() + " resolves to " + desc.getClass().getName()); 
        return desc;
    }

    /**
     * @param type
     * @return
     */
    private boolean isIDLEntity(Class type) {
        Class[] supers = type.getInterfaces();

        for (int i = 0; supers != null && i < supers.length; i++) {
            if (supers[i].equals(IDLEntity.class)) {
                return true;
            }
        }

        return false;
    }

    SimpleDescriptor getSimpleDescriptor(Class type) {
        if (type == Boolean.TYPE) {
            return boolean_descriptor;
        } else if (type == Byte.TYPE) {
            return byte_descriptor;
        } else if (type == Short.TYPE) {
            return short_descriptor;
        } else if (type == Character.TYPE) {
            return char_descriptor;
        } else if (type == Integer.TYPE) {
            return int_descriptor;
        } else if (type == Long.TYPE) {
            return long_descriptor;
        } else if (type == Float.TYPE) {
            return float_descriptor;
        } else if (type == Double.TYPE) {
            return double_descriptor;
        } else if (type == Void.TYPE) {
            return void_descriptor;
        } else if (!type.isPrimitive()) {
            throw new IllegalArgumentException("Cannot resolve simple descriptor for primitive types");
        } else {
            throw new RuntimeException("internal error: " + type);
        }
    }

    SimpleDescriptor boolean_descriptor = new BooleanDescriptor(this);

    SimpleDescriptor byte_descriptor = new ByteDescriptor(this);

    SimpleDescriptor char_descriptor = new CharDescriptor(this);

    SimpleDescriptor short_descriptor = new ShortDescriptor(this);

    SimpleDescriptor int_descriptor = new IntegerDescriptor(this);

    SimpleDescriptor long_descriptor = new LongDescriptor(this);

    SimpleDescriptor float_descriptor = new FloatDescriptor(this);

    SimpleDescriptor double_descriptor = new DoubleDescriptor(this);

    SimpleDescriptor void_descriptor = new VoidDescriptor(this);

    static final java.lang.Class REMOTE_EXCEPTION = java.rmi.RemoteException.class;

    boolean isAbstractInterface(Class type) {
        if (!type.isInterface())
            return false;

        Class[] interfaces = type.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (!isAbstractInterface(interfaces[i]))
                return false;
        }

        java.lang.reflect.Method[] methods = type.getDeclaredMethods();
        for (int j = 0; j < methods.length; j++) {
            if (!isRemoteMethod(methods[j]))
                return false;
        }

        return true;
    }

    boolean isRemoteMethod(java.lang.reflect.Method m) {
        Class[] ex = m.getExceptionTypes();

        for (int i = 0; i < ex.length; i++) {
            if (ex[i].isAssignableFrom(REMOTE_EXCEPTION))
                return true;
        }

        return false;
    }

    /**
     * @param repid
     * @return ValueDescriptor
     */
    public ValueDescriptor getDescriptor(Class clz, String repid,
            RunTime runtime) throws ClassNotFoundException {
        // ValueDescriptor desc = null;
        ValueDescriptor clzdesc = null;

        if (clz != null) {
            logger.fine("Requesting type descriptor for class " + clz.getName() + " with repid " + repid); 
            ValueDescriptor desc = (ValueDescriptor)classMap.get(clz);
            if (desc != null) {
                return desc;
            }
            // special handling for array value types. 
            if (clz.isArray()) {
                desc = ArrayDescriptor.get(clz, this);
                synchronized (desc) {
                    classMap.put(clz, desc);
                    desc.init();
                    repidMap.put(desc.getRepositoryID(), desc);
                }
                return desc;
            }
            clzdesc = (ValueDescriptor) getDescriptor(clz);
            String localID = clzdesc.getRepositoryID();

            if (repid.equals(localID)) {
                return clzdesc;
            }

            // we have a mismatch.  We'll accept this if the class name and the
            // serial version id are the same (ignoring the hash portion of the id); 
            String localClassName = localID.substring(0, localID.indexOf(':'));
            String remoteClassName = repid.substring(0, repid.indexOf(':'));
            
            String localSUID = localID.substring(localID.lastIndexOf(':'));
            String remoteSUID = repid.substring(repid.lastIndexOf(':'));

            // compare the CORBA hash codes, and allow this to work
            if (localClassName.equals(remoteClassName) && localSUID.equals(remoteSUID)) {
                logger.fine("mismatching repository ids accepted because of matching name and SUID.  local: " + clzdesc.getRepositoryID() + "; remote: " + repid);
                return clzdesc; 
            }

            logger.fine("mismatching repository ids. local: "
                    + clzdesc.getRepositoryID() + "; remote: " + repid);
        }

        logger.fine("Requesting type descriptor for repid " + repid); 
        if (repid != null) {
            clzdesc = (ValueDescriptor) repidMap.get(repid);
            if (clzdesc != null) {
                return clzdesc;
            }
        }

        CodeBase codebase = CodeBaseHelper.narrow(runtime);
        if (codebase == null) {
            throw new MARSHAL("cannot locate RunTime CodeBase");
        }

        FullValueDescription fvd = codebase.meta(repid);

        ValueDescriptor super_desc = null;
        if (!"".equals(fvd.base_value)) {
            super_desc = getDescriptor(clz.getSuperclass(), fvd.base_value,
                    codebase);
        }

        clzdesc = new FVDValueDescriptor(fvd, clz, this, repid, super_desc);
        repidMap.put(repid, clzdesc);

        return clzdesc;
    }

    public static String idToClass(String repid) {
        // debug
        logger.finer("idToClass " + repid);

        if (repid.startsWith("IDL:")) {

            ByteString id = new ByteString(repid);

            try {
                int end = id.lastIndexOf(':');
                ByteString s = end < 0 ? id.substring(4) : id.substring(4, end);

                ByteBuffer bb = new ByteBuffer();

                //
                // reverse order of dot-separated name components up
                // till the first slash.
                //
                int firstSlash = s.indexOf('/');
                if (firstSlash > 0) {
                    ByteString prefix = s.substring(0, firstSlash);
                    ByteString[] elems = prefix.split('.');

                    for (int i = elems.length - 1; i >= 0; i--) {
                        bb.append(fixName(elems[i]));
                        bb.append('.');
                    }

                    s = s.substring(firstSlash + 1);
                }

                //
                // Append slash-separated name components ...
                //
                ByteString[] elems = s.split('/');
                for (int i = 0; i < elems.length; i++) {
                    bb.append(fixName(elems[i]));
                    if (i != elems.length - 1)
                        bb.append('.');
                }

                String result = bb.toString();

                logger.finer("idToClassName " + repid + " => " + result);

                return result;
            } catch (IndexOutOfBoundsException ex) {
                logger.log(Level.FINE, "idToClass " + ex.getMessage(), ex);
                return null;
            }

        } else if (repid.startsWith("RMI:")) {
            int end = repid.indexOf(':', 4);
            return end < 0 ? repid.substring(4) : repid.substring(4, end);
        }

        return null;
    }

    static String fixName(String name) {
        return (new ByteString(name)).toString();
    }

    static ByteString fixName(ByteString name) {
        if (keyWords.contains(name)) {
            ByteBuffer buf = new ByteBuffer();
            buf.append('_');
            buf.append(name);
            return buf.toByteString();
        }

        ByteString result = name;
        ByteString current = name;

        boolean match = true;
        while (match) {

            int len = current.length();
            match = false;

            for (int i = 0; i < reservedPostfixes.length; i++) {
                if (current.endsWith(reservedPostfixes[i])) {
                    ByteBuffer buf = new ByteBuffer();
                    buf.append('_');
                    buf.append(result);
                    result = buf.toByteString();

                    int resultLen = reservedPostfixes[i].length();
                    if (len > resultLen)
                        current = current.substring(0, len - resultLen);
                    else
                        current = new ByteString("");

                    match = true;
                    break;
                }
            }

        }

        return name;
    }

    static final java.util.Set keyWords = new java.util.HashSet();

    static final ByteString[] reservedPostfixes = new ByteString[] {
            new ByteString("Helper"), new ByteString("Holder"),
            new ByteString("Operations"), new ByteString("POA"),
            new ByteString("POATie"), new ByteString("Package"),
            new ByteString("ValueFactory") };

    static {
        String[] words = { "abstract", "boolean", "break", "byte", "case",
                "catch", "char", "class", "clone", "const", "continue",
                "default", "do", "double", "else", "equals", "extends",
                "false", "final", "finalize", "finally", "float", "for",
                "getClass", "goto", "hashCode", "if", "implements", "import",
                "instanceof", "int", "interface", "long", "native", "new",
                "notify", "notifyAll", "null", "package", "private",
                "protected", "public", "return", "short", "static", "super",
                "switch", "synchronized", "this", "throw", "throws",
                "toString", "transient", "true", "try", "void", "volatile",
                "wait", "while" };

        for (int i = 0; i < words.length; i++) {
            keyWords.add(new ByteString(words[i]));
        }
    }

}

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
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.ClassDesc;

import org.apache.yoko.rmi.util.ByteBuffer;
import org.apache.yoko.rmi.util.ByteString;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;
import org.omg.SendingContext.RunTime;
import org.apache.yoko.rmi.util.SearchKey;
import org.apache.yoko.rmi.util.WeakKey;

public class TypeRepository {
    static final Logger logger = Logger.getLogger(TypeRepository.class.getName());

    org.omg.CORBA.ORB orb;

    private static final class TypeDescriptorCache {
        private final ConcurrentMap<WeakKey<String>, WeakReference<TypeDescriptor>> map = new ConcurrentHashMap<>();
        private final ReferenceQueue<String> staleKeys = new ReferenceQueue<>();

        public TypeDescriptor get(String repId) {
            cleanStaleKeys();
            WeakReference<TypeDescriptor> ref = map.get(new SearchKey<String>(repId));
            return (null == ref) ? null : ref.get();
        }

        public void put(TypeDescriptor typeDesc) {
            cleanStaleKeys();
            final WeakReference<TypeDescriptor> value = new WeakReference<>(typeDesc);
            map.putIfAbsent(new WeakKey<String>(typeDesc.getRepositoryID(), staleKeys), value);
        }

        private void cleanStaleKeys() {
            for (Reference<? extends String> staleKey = staleKeys.poll(); staleKey != null; staleKey = staleKeys.poll()) {
                map.remove(staleKey);
            }
        }
    }

    private static final class LocalDescriptors extends ClassValue<TypeDescriptor> {
        private static final class Raw extends ClassValue<TypeDescriptor> {
            private static final List<Class<?>> staticAnyTypes =
                    Collections.unmodifiableList(
                            Arrays.asList(Object.class, Externalizable.class, Serializable.class, Remote.class));

            private final TypeRepository repo;

            Raw(TypeRepository repo) {
                this.repo = repo;
            }

            @Override
            protected TypeDescriptor computeValue(Class<?> type) {
                if (type.isPrimitive()) {
                    return primitiveDescriptor(type);
                } else if (type == String.class) {
                    return new StringDescriptor(repo);
                } else if (type == Class.class) {
                    return new ClassDescriptor(repo);
                } else if (type == ClassDesc.class) {
                    return new ClassDescDescriptor(repo);
                } else if (type == java.util.Date.class) {
                    return new DateValueDescriptor(repo);
                } else if (staticAnyTypes.contains(type)) {
                    return new AnyDescriptor(type, repo);
                } else if ((IDLEntity.class.isAssignableFrom(type)) && isIDLEntity(type)) {
                    return new IDLEntityDescriptor(type, repo);
                } else if (Throwable.class.isAssignableFrom(type)) {
                    return new ExceptionDescriptor(type, repo);
                } else if (Enum.class.isAssignableFrom(type) && (Enum.class != type)) {
                    return new EnumDescriptor(type, repo);
                } else if (type.isArray()) {
                    return ArrayDescriptor.get(type, repo);
                } else if (!type.isInterface()
                        && Serializable.class.isAssignableFrom(type)) {
                    return new ValueDescriptor(type, repo);
                } else if (Remote.class.isAssignableFrom(type)) {
                    if (type.isInterface()) {
                        return new RemoteInterfaceDescriptor(type, repo);
                    } else {
                        return new RemoteClassDescriptor(type, repo);
                    }
                } else if (Object.class.isAssignableFrom(type)) {
                    if (isAbstractInterface(type)) {
                        logger.finer("encoding " + type + " as abstract interface");
                        return new AbstractObjectDescriptor(type, repo);
                    } else {
                        logger.finer("encoding " + type + " as a abstract value");
                        return new ValueDescriptor(type, repo);
                    }
                } else {
                    throw new RuntimeException("cannot handle class " + type.getName());
                }
            }

            private TypeDescriptor primitiveDescriptor(Class<?> type) {
                if (type == Boolean.TYPE) {
                    return new BooleanDescriptor(repo);
                } else if (type == Byte.TYPE) {
                    return new ByteDescriptor(repo);
                } else if (type == Short.TYPE) {
                    return new ShortDescriptor(repo);
                } else if (type == Character.TYPE) {
                    return new CharDescriptor(repo);
                } else if (type == Integer.TYPE) {
                    return new IntegerDescriptor(repo);
                } else if (type == Long.TYPE) {
                    return new LongDescriptor(repo);
                } else if (type == Float.TYPE) {
                    return new FloatDescriptor(repo);
                } else if (type == Double.TYPE) {
                    return new DoubleDescriptor(repo);
                } else if (type == Void.TYPE) {
                    return new VoidDescriptor(repo);
                } else {
                    throw new RuntimeException("internal error: " + type);
                }
            }

            private static boolean isIDLEntity(Class<?> type) {
                for (Class<?> intf : type.getInterfaces()) {
                    if (intf.equals(IDLEntity.class))
                        return true;
                }
                return false;
            }

            private static boolean isAbstractInterface(Class<?> type) {
                if (!type.isInterface())
                    return false;

                for (Class<?> intf : type.getInterfaces()) {
                    if (!isAbstractInterface(intf))
                        return false;
                }

                for (Method method : type.getDeclaredMethods()) {
                    if (!isRemoteMethod(method))
                        return false;
                }

                return true;
            }

            private static boolean isRemoteMethod(java.lang.reflect.Method m) {
                for (Class<?> exceptionType : m.getExceptionTypes()) {
                    if (exceptionType.isAssignableFrom(RemoteException.class))
                        return true;
                }

                return false;
            }

        }

        private final Raw rawValues;
        private final TypeDescriptorCache repIdDescriptors;

        LocalDescriptors(TypeRepository repo, TypeDescriptorCache repIdDescriptors) {
            rawValues = new Raw(repo);
            this.repIdDescriptors = repIdDescriptors;
        }
        @Override
        protected TypeDescriptor computeValue(Class<?> type) {
            final TypeDescriptor desc = rawValues.get(type);
            desc.init();
            repIdDescriptors.put(desc);
            return desc;
        }

    }

    private static final class FvdRepIdDescriptorMaps extends ClassValue<ConcurrentMap<String,ValueDescriptor>> {

        @Override
        protected ConcurrentMap<String,ValueDescriptor> computeValue(
                Class<?> type) {
            return new ConcurrentHashMap<String,ValueDescriptor>(1);
        }
    }

    private final TypeDescriptorCache repIdDescriptors;
    private final LocalDescriptors localDescriptors;
    private final FvdRepIdDescriptorMaps fvdDescMaps = new FvdRepIdDescriptorMaps();
    private final ConcurrentMap<String,ValueDescriptor> noTypeDescMap = new ConcurrentHashMap<String,ValueDescriptor>();

    private static final Set<Class<?>> initTypes;

    static {
        initTypes = createClassSet(Object.class, String.class, ClassDesc.class, Date.class,
                Externalizable.class, Serializable.class, Remote.class);
    }

    private static Set<Class<?>> createClassSet(Class<?>...types) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(types)));
    }

    public TypeRepository(org.omg.CORBA.ORB orb) {
        this.orb = orb;
        repIdDescriptors = new TypeDescriptorCache();
        localDescriptors = new LocalDescriptors(this, repIdDescriptors);

        for (Class<?> type: initTypes) {
            localDescriptors.get(type);
        }
    }

    org.omg.CORBA.ORB getORB() {
        return orb;
    }

    public String getRepositoryID(Class<?> type) {
        return getDescriptor(type).getRepositoryID();
    }

    public RemoteInterfaceDescriptor getRemoteDescriptor(Class<?> type) {
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

    public TypeDescriptor getDescriptor(Class<?> type) {
        if (logger.isLoggable(Level.FINE))
            logger.fine(String.format("Requesting type descriptor for class \"%s\"", type.getName()));
        final TypeDescriptor desc = localDescriptors.get(type);
        if (logger.isLoggable(Level.FINE))
            logger.fine(String.format("Class \"%s\" resolves to %s", type.getName(), desc));
        return desc;
    }

    public TypeDescriptor getDescriptor(String repId) {
        if (logger.isLoggable(Level.FINE))
            logger.fine(String.format("Requesting type descriptor for repId \"%s\"", repId));
        final TypeDescriptor desc = repIdDescriptors.get(repId);
        if (logger.isLoggable(Level.FINE))
            logger.fine(String.format("RepId \"%s\" resolves to %s", repId, desc));
        return desc;
    }

    /**
     * @param clz (local) class we are interested in
     * @param repid  repository id from GIOP input for the remote class
     * @param runtime way to look up the complete remote descriptor
     * @return ValueDescriptor
     * @throws ClassNotFoundException  something might go wrong.
     */
    public ValueDescriptor getDescriptor(Class<?> clz, String repid,
            RunTime runtime) throws ClassNotFoundException {
        if (repid == null) {
            return (ValueDescriptor) getDescriptor(clz);
        }

        ValueDescriptor clzdesc = (ValueDescriptor) repIdDescriptors.get(repid);
        if (clzdesc != null) {
            return clzdesc;
        }

        if (clz != null) {
            logger.fine("Requesting type descriptor for class " + clz.getName() + " with repid " + repid); 
            // special handling for array value types.
            if (clz.isArray()) {
                //TODO don't we need to look up the FVD for the array element?
                return (ValueDescriptor) localDescriptors.get(clz);
            }
            clzdesc = (ValueDescriptor) getDescriptor(clz);
            String localID = clzdesc.getRepositoryID();

            if (repid.equals(localID)) {
                return clzdesc;
            }
            //One might think that java serialization compatibility (same SerialVersionUID) would mean corba
            //serialization compatibility.  However, one implementation might have a writeObject method and the
            //other implementation not.  This is recorded only in the isCustomMarshall of the source value
            //descriptor, so we have to fetch it to find out.  A custom marshall value has a couple extra bytes
            // and padding and these can't be reliably identified without this remote info.  cf YOKO-434.
        }

        logger.fine("Requesting type descriptor for repid " + repid); 
        CodeBase codebase = CodeBaseHelper.narrow(runtime);
        if (codebase == null) {
            throw new MARSHAL("cannot locate RunTime CodeBase");
        }

        FullValueDescription fvd = codebase.meta(repid);

        ValueDescriptor super_desc = null;
        if (!"".equals(fvd.base_value)) {
            super_desc = getDescriptor(clz == null? null: clz.getSuperclass(), fvd.base_value,
                    codebase);
        }

        ValueDescriptor newDesc = new FVDValueDescriptor(fvd, clz, this, repid, super_desc);
        ConcurrentMap<String, ValueDescriptor> remoteDescMap = (clz == null) ? noTypeDescMap : fvdDescMaps.get(clz);
        clzdesc = remoteDescMap.putIfAbsent(newDesc.getRepositoryID(), newDesc);
        if (clzdesc == null) {
            clzdesc = newDesc;
            repIdDescriptors.put(clzdesc);
        }

        return clzdesc;
    }
}

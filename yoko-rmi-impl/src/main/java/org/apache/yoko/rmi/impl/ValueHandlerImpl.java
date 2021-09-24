/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
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

import org.apache.yoko.rmi.util.SerialFilterHelper;
import org.apache.yoko.util.PrivilegedActions;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.SendingContext.CodeBaseHelper;
import org.omg.SendingContext.RunTime;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.security.AccessController.doPrivileged;
import static org.apache.yoko.util.PrivilegedActions.GET_CONTEXT_CLASS_LOADER;

public class ValueHandlerImpl implements ValueHandler {
    private static final Logger logger = Logger.getLogger(ValueHandlerImpl.class.getName());
    private static final Pattern COLON = Pattern.compile(Pattern.quote(":"));

    private final Map<InputStream, Map<Integer, Serializable>> streamMap = new HashMap<>();
    private final TypeRepository repo;
    private RunTimeCodeBaseImpl codeBase;
    private int depth = 0;

    private ValueHandlerImpl() {
        this.repo = TypeRepository.get();
    }

    private enum HandlerHolder {
        ;
        static final ValueHandlerImpl value = new ValueHandlerImpl();
    }

    public static ValueHandlerImpl get() {
        return HandlerHolder.value;
    }

    private ValueDescriptor desc(Class clz) {
        return (ValueDescriptor) repo.getDescriptor(clz);
    }

    private ValueDescriptor desc(String repId) {
        return (ValueDescriptor) repo.getDescriptor(repId);
    }

    private ValueDescriptor desc(Class clz, String repid, RunTime runtime) {
        try {
            return repo.getDescriptor(clz, repid, runtime);
        } catch (ClassNotFoundException ex) {
            MARSHAL m = new MARSHAL("class not found " + ex.getMessage());
            m.initCause(ex);
            throw m;
        }
    }

    public void writeValue(OutputStream out, Serializable val) {
        desc(val.getClass()).writeValue(out, val);
    }

    public Serializable readValue(InputStream in, int offset, Class clz, String repid, RunTime codebase) {
        try {
            depth++;
            SerialFilterHelper.checkInput(clz, depth, in);
            return readValue0(in, offset, clz, repid, codebase);
        } catch (Error | RuntimeException ex) {
            logger.log(Level.FINE, "Exception reading value of type " + repid, ex); 
            throw ex;
        } finally {
            depth--;
        }
    }

    private Serializable readValue0(InputStream in, int offset, Class clz, String repid, RunTime codebase) {
        Serializable obj = null;
        ValueDescriptor desc = repid == null ? desc(clz) : desc(clz, repid, codebase);
        Integer key = offset;
        boolean remove = false;
        Map<Integer, Serializable> offsetMap = null;
        try {
            if (in instanceof InputStreamWithOffsets) {
                offsetMap = ((InputStreamWithOffsets)in).getOffsetMap();
            } else synchronized (streamMap) {
                offsetMap = streamMap.get(in);
                if (offsetMap == null) {
                    offsetMap = new HashMap<>();
                    streamMap.put(in, offsetMap);
                    remove = true;
                }
            }
            obj = desc.readValue(in, offsetMap, key);
            /*
             * // lazy initialization of recursive fields... for (ValueBox box =
             * (ValueBox) offsetMap.get (key); box != null; box = box.next) {
             * box.set (obj); }
             */
        } finally {
            if (remove) {
                synchronized (streamMap) {
                    streamMap.remove(in);
                }
            } 
        }
        return obj;
    }

    public String getRMIRepositoryID(Class clz) {
        return repo.getDescriptor(clz).getRepositoryID();
    }

    @Override
    public boolean isCustomMarshaled(Class clz) {
        return desc(clz).isChunked();
    }

    public synchronized RunTime getRunTimeCodeBase() {
        logger.finer("getRunTimeCodeBase");

        if (codeBase == null) {
            codeBase = new RunTimeCodeBaseImpl(this);
        }

        POA poa = RMIState.current().getPOA();

        try {
            org.omg.CORBA.Object ref = poa.servant_to_reference(codeBase);
            return CodeBaseHelper.narrow(ref);
        } catch (ServantNotActive ex) {
            // ignore //
        } catch (WrongPolicy ex) {
            throw (INTERNAL)new INTERNAL("should not happen").initCause(ex);
        }

        try {
            byte[] id = poa.activate_object(codeBase);
            org.omg.CORBA.Object ref = poa.id_to_reference(id);
            return CodeBaseHelper.narrow(ref);
        } catch (ServantAlreadyActive | ObjectNotActive | WrongPolicy ex) {
            throw (INTERNAL)new INTERNAL("should not happen").initCause(ex);
        }
    }

    public Serializable writeReplace(Serializable val) {
        if (val instanceof RMIStub) {
            RMIStub stub = (RMIStub) val;
            Class type = stub._descriptor.type;
            RMIState state = RMIState.current();
            Stub result = state.getStaticStub(stub._get_codebase(), type);
            if (null == result) return new RMIPersistentStub(stub, type);
            result._set_delegate(stub._get_delegate());
            logger.finer("replacing with stub " + result.getClass().getName());
            return result;
        } else {
            ValueDescriptor desc = desc(val.getClass());
            Serializable result = desc.writeReplace(val);
            if (result != val) logger.finer("replacing with value of type " + val.getClass().getName() + " with " + result.getClass().getName());
            return result; 
        }
    }

    static Class getClassFromRepositoryID(String id) {
        if (logger.isLoggable(Level.FINER)) logger.finer("getClassFromRepositoryID => " + id);
        try {
            final String[] parts = COLON.split(id, 3);
            switch (parts[0]) {
            case "RMI": // fall through
            case "IDL":
                final String className = parts[1];
                if (logger.isLoggable(Level.FINER)) logger.finer("getClassFromRepositoryID =>> " + className);
                ClassLoader loader = doPrivileged(GET_CONTEXT_CLASS_LOADER);
                return loader.loadClass(className);
            default:
                if (logger.isLoggable(Level.FINER)) logger.finer("getClassFromRepositoryID =>> " + null);
                return null;
            }
        } catch (Throwable ex) {
            logger.log(Level.FINE, "error resolving class from id", ex); 
            return null;
        }
    }

    String getImplementation(String id) {
        try {
            final String result;
            Class clz = getClassFromRepositoryID(id);
            if (clz == null) {
                result = "";
            } else {
                result = Util.getCodebase(clz);
                if (result == null) {
                    if (logger.isLoggable(Level.FINE)) logger.fine("failed to find implementation " + id);
                    return "";
                }
            }
            if (logger.isLoggable(Level.FINER)) logger.finer("getImplementation " + id + " => " + result);
            return result;
        } catch (RuntimeException ex) {
            logger.log(Level.FINE, "error implementation class from id", ex); 
            throw ex;
        }
    }

    String[] getImplementations(String[] ids) {
        if (ids == null) return new String[0];
        String[] result = new String[ids.length];
        for (int i = 0; i < ids.length; i++) result[i] = getImplementation(ids[i]);
        return result;
    }

    FullValueDescription meta(String repId) {
        if (logger.isLoggable(Level.FINER)) logger.finer(String.format("meta \"%s\"", repId));
        try {
            ValueDescriptor desc = desc(repId);
            if (null == desc) {
                Class clz = getClassFromRepositoryID(repId);
                if (clz == null) {
                    logger.warning("class not found: " + repId);
                    throw new MARSHAL(0x4f4d0001, CompletionStatus.COMPLETED_MAYBE);
                }
                desc = desc(clz);
            }
            return desc.getFullValueDescription();
        } catch (Throwable ex) {
            logger.log(Level.WARNING, "exception in meta", ex);
            throw (OBJECT_NOT_EXIST)new OBJECT_NOT_EXIST().initCause(ex);
        }
    }

    String[] getBases(String id) {
        try {
            Class clz = getClassFromRepositoryID(id);
            if (clz == null) return new String[0];

            Class[] ifaces = clz.getInterfaces();
            Class superClz = clz.getSuperclass();

            ArrayList supers = new ArrayList();

            if (superClz != Object.class) addIfRMIClass(supers, superClz);


            for (Class iface : ifaces) addIfRMIClass(supers, iface);


            String[] result = new String[supers.size()];
            for (int i = 0; i < supers.size(); i++) result[i] = ((TypeDescriptor) supers.get(i)).getRepositoryID();


            if (logger.isLoggable(Level.FINER)) logger.finer("getBases " + id + " => " + Arrays.toString(result));

            return result;
        } catch (Throwable ex) {
            logger.log(Level.WARNING, "exception in CodeBase::bases", ex);
            return new String[0];
        }
    }

    private void addIfRMIClass(List list, Class clz) {
        TypeDescriptor desc = repo.getDescriptor(clz);

        if (desc instanceof RemoteDescriptor) list.add(desc);
        else if (desc instanceof ValueDescriptor) list.add(desc);
    }
}

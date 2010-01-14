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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.ValueHandler;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.ValueDefPackage.FullValueDescription;
import org.omg.SendingContext.RunTime;

public class ValueHandlerImpl implements ValueHandler {
    static final Logger logger = Logger.getLogger(ValueHandlerImpl.class
            .getName());

    TypeRepository repository;

    RunTimeCodeBaseImpl codeBase;

    private TypeRepository getRepository() {
        return RMIState.current().getTypeRepository(); // repository;
    }

    ValueHandlerImpl(TypeRepository rep) {
        this.repository = rep;
    }

    private ValueDescriptor desc(Class clz) {
        return (ValueDescriptor) getRepository().getDescriptor(clz);
    }

    private ValueDescriptor desc(Class clz, String repid, RunTime runtime) {
        try {
            return (ValueDescriptor) getRepository().getDescriptor(clz, repid,
                    runtime);
        } catch (ClassNotFoundException ex) {
            MARSHAL m = new MARSHAL("class not found " + ex.getMessage());
            m.initCause(ex);
            throw m;
        }
    }

    public void writeValue(org.omg.CORBA.portable.OutputStream out,
            java.io.Serializable val) {
        desc(val.getClass()).writeValue(out, val);
    }

    java.util.Map streamMap = new java.util.HashMap();

    public java.io.Serializable readValue(
            org.omg.CORBA.portable.InputStream in, int offset,
            java.lang.Class clz, java.lang.String repid,
            org.omg.SendingContext.RunTime codebase) {
        try {
            return readValue0(in, offset, clz, repid, codebase);
        } catch (Error ex) {
            logger.log(Level.FINE, "Exception reading value of type " + repid, ex); 
            throw ex;
        } catch (RuntimeException ex) {
            logger.log(Level.FINE, "Exception reading value of type " + repid, ex); 
            throw ex;
        }
    }

    public java.io.Serializable readValue0(
            org.omg.CORBA.portable.InputStream in, int offset,
            java.lang.Class clz, java.lang.String repid,
            org.omg.SendingContext.RunTime codebase) {
        java.io.Serializable obj = null;
        ValueDescriptor desc = repid == null ? desc(clz) : desc(clz, repid,
                codebase);

        Integer key = new Integer(offset);
        boolean remove = false;
        java.util.Map offsetMap = null;
        try {
            synchronized (streamMap) {
                offsetMap = (java.util.Map) streamMap.get(in);
                if (offsetMap == null) {
                    offsetMap = new java.util.HashMap();
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

    public java.lang.String getRMIRepositoryID(java.lang.Class clz) {
        return getRepository().getDescriptor(clz).getRepositoryID();
    }

    public boolean isCustomMarshaled(java.lang.Class clz) {
        return desc(clz).isCustomMarshalled();
    }

    public synchronized org.omg.SendingContext.RunTime getRunTimeCodeBase() {
        logger.finer("getRunTimeCodeBase");

        if (codeBase == null) {
            codeBase = new RunTimeCodeBaseImpl(this);
        }

        org.omg.CORBA.ORB orb = RMIState.current().getORB();

        org.omg.PortableServer.POA poa = RMIState.current().getPOA();

        try {
            org.omg.CORBA.Object ref = poa.servant_to_reference(codeBase);
            return org.omg.SendingContext.CodeBaseHelper.narrow(ref);
        } catch (org.omg.PortableServer.POAPackage.ServantNotActive ex) {
            // ignore //
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            throw (org.omg.CORBA.INTERNAL)new org.omg.CORBA.INTERNAL("should not happen").initCause(ex);
        }

        try {
            byte[] id = poa.activate_object(codeBase);
            org.omg.CORBA.Object ref = poa.id_to_reference(id);
            return org.omg.SendingContext.CodeBaseHelper.narrow(ref);
        } catch (org.omg.PortableServer.POAPackage.ServantAlreadyActive ex) {
            throw (org.omg.CORBA.INTERNAL)new org.omg.CORBA.INTERNAL("should not happen").initCause(ex);
        } catch (org.omg.PortableServer.POAPackage.ObjectNotActive ex) {
            throw (org.omg.CORBA.INTERNAL)new org.omg.CORBA.INTERNAL("should not happen").initCause(ex);
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            throw (org.omg.CORBA.INTERNAL)new org.omg.CORBA.INTERNAL("should not happen").initCause(ex);
        }
    }

    public java.io.Serializable writeReplace(java.io.Serializable val) {
        if (val instanceof RMIStub) {

            RMIStub stub = (RMIStub) val;
            Class type = stub._descriptor.getJavaClass();

            RMIState state = RMIState.current();
            Stub result = state.getStaticStub(stub._get_codebase(), type);
            if (result != null) {
                result._set_delegate(stub._get_delegate());

                logger.finer("replacing with stub " + result.getClass().getName());

                return result;
            }

            return new org.apache.yoko.rmi.impl.RMIPersistentStub(stub, type);

        } else {
            ValueDescriptor desc = desc(val.getClass());
            java.io.Serializable result = desc.writeReplace(val);
            
            if (result != val) {
                logger.finer("replacing with value of type " + val.getClass().getName() + " with " + result.getClass().getName());
            }
            return result; 
        }
    }

    static Class getClassFromRepositoryID(String id) {
        String className = null;

        if (logger.isLoggable(Level.FINER)) {
            logger.finer("getClassFromRepositoryID => " + id);
        }

        try {
            if (id.startsWith("RMI:")) {
                className = id.substring(4, id.indexOf(':', 4));
            } else if (id.startsWith("IDL:")) {
                className = id.substring(4, id.indexOf(':', 4));
            } else {
                if (logger.isLoggable(Level.FINER)) {
                    logger.finer("getClassFromRepositoryID =>> " + null);
                }
                return null;
            }

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("getClassFromRepositoryID =>> " + className);
            }

            ClassLoader loader = RMIState.current().getClassLoader();
            return loader.loadClass(className);
        } catch (Throwable ex) {
            logger.log(Level.FINE, "error resolving class from id", ex); 
            return null;
        }
    }

    String getImplementation(String id) {
        try {
            String result = "";

            Class clz = getClassFromRepositoryID(id);
            if (clz != null) {
                result = javax.rmi.CORBA.Util.getCodebase(clz);
                if (result == null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("failed to find implementation " + id);
                    }
                    return "";
                }
            }

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("getImplementation " + id + " => " + result);
            }

            return result;
        } catch (RuntimeException ex) {
            logger.log(Level.FINE, "error implementation class from id", ex); 
            throw ex;
        }
    }

    String[] getImplementations(String[] ids) {

        if (ids == null)
            return new String[0];

        String[] result = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            result[i] = getImplementation(ids[i]);
        }

        return result;
    }

    FullValueDescription meta(String id) {
        try {
            // System.out.println ("ValueHandlerImpl::meta "+id);

            Class clz = getClassFromRepositoryID(id);

            if (clz == null) {
                logger.warning("class not found: " + id);
                throw new org.omg.CORBA.MARSHAL(0x4f4d0001,
                        CompletionStatus.COMPLETED_MAYBE);
            }

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("meta " + id);
            }

            ValueDescriptor desc = (ValueDescriptor) getRepository()
                    .getDescriptor(clz);

            return desc.getFullValueDescription();
        } catch (Throwable ex) {
            logger.log(Level.WARNING, "exception in meta", ex);

            throw (org.omg.CORBA.OBJECT_NOT_EXIST)new org.omg.CORBA.OBJECT_NOT_EXIST()
                .initCause(ex);
        }
    }

    String[] getBases(String id) {

        try {
            Class clz = getClassFromRepositoryID(id);
            if (clz == null)
                return new String[0];

            Class[] ifaces = clz.getInterfaces();
            Class superClz = clz.getSuperclass();

            java.util.ArrayList supers = new java.util.ArrayList();

            if (superClz != Object.class) {
                addIfRMIClass(supers, superClz);
            }

            for (int i = 0; i < ifaces.length; i++) {
                addIfRMIClass(supers, ifaces[i]);
            }

            String[] result = new String[supers.size()];
            for (int i = 0; i < supers.size(); i++) {
                result[i] = ((TypeDescriptor) supers.get(i)).getRepositoryID();
            }

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("getBases " + id + " => " + result);
            }

            return result;

        } catch (Throwable ex) {
            logger.log(Level.WARNING, "exception in CodeBase::bases", ex);
            return new String[0];
        }
    }

    private void addIfRMIClass(java.util.List list, Class clz) {
        TypeDescriptor desc = getRepository().getDescriptor(clz);

        if (desc instanceof RemoteDescriptor)
            list.add(desc);
        else if (desc instanceof ValueDescriptor)
            list.add(desc);
    }
}

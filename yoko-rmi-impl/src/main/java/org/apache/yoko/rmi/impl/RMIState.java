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
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.ValueHandler;

import org.apache.yoko.rmi.api.PortableRemoteObjectExt;
import org.apache.yoko.rmi.api.PortableRemoteObjectState;
import org.apache.yoko.rmi.util.NodeleteSynchronizedMap;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.Policy;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;


public class RMIState implements PortableRemoteObjectState {
    static final Logger logger = Logger.getLogger(RMIState.class.getName());

    private boolean isShutdown;

    final private org.omg.CORBA.ORB _orb;

    private String _name;

    private TypeRepository _typerepository;
    
    private POA poa;
    
    POA getPOA() {
	return poa;
    }

    TypeRepository getTypeRepository() {
        if (_typerepository == null)
            _typerepository = new TypeRepository(_orb);

        return _typerepository;
    }

    RMIState(org.omg.CORBA.ORB orb, String name) {
        if (orb == null) {
            throw new NullPointerException("ORB is null");
        }
        
        try {
            POA rootPoa = (POA) orb.resolve_initial_references("RootPOA");
	    poa = rootPoa.create_POA(name, null, new Policy[0]);
	    poa.the_POAManager().activate();
	} catch (AdapterAlreadyExists e) {
        logger.log(Level.WARNING, "Adapter already exists", e);
	} catch (InvalidPolicy e) {
        logger.log(Level.WARNING, "Invalid policy", e);
	} catch (InvalidName e) {
        logger.log(Level.WARNING, "Invalid name", e);
	} catch (AdapterInactive e) {
        logger.log(Level.WARNING, "Adapter inactive", e);
	}

        _orb = orb;
        _name = name;
    }

    void checkShutDown() {
        if (isShutdown) {
            BAD_INV_ORDER ex = new BAD_INV_ORDER(
                    "RMIState has already been shut down");
            logger.fine("RMIState has already been shut down " + ex);
            throw ex;
        }
    }

    public void shutdown() {
        logger.finer("RMIState shutdown requested; name = " + _name);

        checkShutDown();

        isShutdown = true;
    }

    public org.omg.CORBA.ORB getORB() {
        return _orb;
    }


    org.omg.CORBA.portable.Delegate createDelegate(RMIServant servant) {
        checkShutDown();

        byte[] id = servant._id;
        RemoteDescriptor desc = servant._descriptor;

        String repid = desc.getRepositoryID();
        
        org.omg.CORBA.portable.ObjectImpl ref;
        org.omg.PortableServer.POA poa;

        try {
            poa = getPOA();
            ref = (org.omg.CORBA.portable.ObjectImpl) poa
                    .create_reference_with_id(id, repid);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            throw (InternalError)new InternalError("wrong policy: " + ex.getMessage()).initCause(ex);
        }

        return ref._get_delegate();
    }

    ValueHandler valueHandler;

    public ValueHandler createValueHandler() {
        checkShutDown();

        if (valueHandler == null) {
            valueHandler = new ValueHandlerImpl(getTypeRepository());
        }

        return valueHandler;
    }

    static RMIState current() {
        return (RMIState) PortableRemoteObjectExt.getState();
    }

    public ClassLoader getClassLoader() {
        ClassLoader loader  = Thread.currentThread().getContextClassLoader();
        return loader;
    }

    //
    // data for use in PortableRemoteObjectImpl
    //

    java.util.Map stub_map = new NodeleteSynchronizedMap() {
        public java.util.Map initialValue() {
            return new HashMap();
        }
    };

    //
    // data for use in UtilImpl
    //
    java.util.Map tie_map = java.util.Collections
            .synchronizedMap(new IdentityHashMap());

    private java.util.Map static_stub_map = new NodeleteSynchronizedMap() {
        public java.util.Map initialValue() {
            return new HashMap();
        }
    };

    private URL _codebase;

    //
    //
    //
    public void setCodeBase(URL codebase) {
        _codebase = codebase;
    }

    public URL getCodeBase() {
        return _codebase;
    }

    void clearState() {
        _typerepository = null;
        valueHandler = null;
        stub_map = null;
        tie_map = null;

        static_stub_map = null;
    }

    static class StaticStubEntry {
        java.lang.reflect.Constructor stub_constructor;
    }

    /**
     * Method getStaticStub.
     * 
     * @param codebase
     * @param type
     * @return Stub
     */
    public Stub getStaticStub1(String codebase, Class type) {
        return null;
    }

    public Stub getStaticStub(String codebase, Class type) {

        StaticStubEntry ent = (StaticStubEntry) static_stub_map.get(type);
        if (ent == null) {
            ent = new StaticStubEntry();

            java.lang.reflect.Constructor cons = findConstructor(codebase,
                    getNewStubClassName(type));

            if (cons != null
                    && !javax.rmi.CORBA.Stub.class.isAssignableFrom(cons
                            .getDeclaringClass())) {
                logger.fine("class " + cons.getDeclaringClass()
                        + " is not a javax.rmi.CORBA.Stub");
                cons = null;
            }

            if (cons == null) {
                cons = findConstructor(codebase, getOldStubClassName(type));
            }

            if (cons != null
                    && !javax.rmi.CORBA.Stub.class.isAssignableFrom(cons
                            .getDeclaringClass())) {
                logger.fine("class " + cons.getDeclaringClass()
                        + " is not a javax.rmi.CORBA.Stub");
                cons = null;
            }

            ent.stub_constructor = cons;

            static_stub_map.put(type, ent);
        }

        if (ent.stub_constructor == null) {
            return null;
        }

        try {
            return (Stub) ent.stub_constructor
                    .newInstance(PortableRemoteObjectImpl.NO_ARG);
        } catch (ClassCastException ex) {
            logger.log(Level.FINE, "loaded class "
                    + ent.stub_constructor.getDeclaringClass()
                    + " is not a proper stub", ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.FINE, "cannot instantiate stub class for " + type + " :: "
                    + ex.getMessage(), ex);
        } catch (InstantiationException ex) {
            logger.log(Level.FINE, "cannot instantiate stub class for " + type + " :: "
                    + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            logger.log(Level.FINE, "cannot instantiate stub class for " + type + " :: "
                    + ex.getMessage(), ex);
        }

        return null;
    }

    private java.lang.reflect.Constructor findConstructor(String codebase,
            String stubName) {
        try {
            Class stubClass = javax.rmi.CORBA.Util.loadClass(stubName,
                    codebase, getClassLoader());
            return stubClass.getConstructor(new Class[0]);

        } catch (NoSuchMethodException ex) {
            logger.log(Level.WARNING, "stub class " + stubName
                    + " has no default constructor", ex);

        } catch (ClassNotFoundException ex) {
            logger.log(Level.FINE, "failed to load remote class " + stubName + " from "
                    + codebase, ex);
            // ignore //
        }

        return null;
    }

    String getNewStubClassName(Class c) {

        String cname = c.getName();

        String pkgname = null;
        int idx = cname.lastIndexOf('.');

        if (idx == -1) {
            pkgname = "org.omg.stub";
        } else {
            pkgname = "org.omg.stub." + cname.substring(0, idx);
        }

        String cplain = cname.substring(idx + 1);

        return pkgname + "." + "_" + cplain + "_Stub";
    }

    String getOldStubClassName(Class c) {

        String cname = c.getName();

        //String pkgname = null;
        int idx = cname.lastIndexOf('.');

        if (idx == -1) {
            return "_" + cname + "_Stub";
        } else {
            return cname.substring(0, idx + 1) + "_" + cname.substring(idx + 1)
                    + "_Stub";
        }
    }

    public void exportObject(Remote remote) throws RemoteException {
        //PortableRemoteObjectExt.pushState(this);
        try {
            javax.rmi.PortableRemoteObject.exportObject(remote);
        } finally {
            //PortableRemoteObjectExt.popState();
        }
    }

    public void unexportObject(Remote remote) throws RemoteException {
        //PortableRemoteObjectExt.pushState(this);
        try {
            javax.rmi.PortableRemoteObject.unexportObject(remote);
        } finally {
            //PortableRemoteObjectExt.popState();
        }
    }

    public String getName() {
        return _name;
    }
}

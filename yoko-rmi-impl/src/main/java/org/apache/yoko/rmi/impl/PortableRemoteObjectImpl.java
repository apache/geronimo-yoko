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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;

import org.apache.yoko.rmi.util.ClientUtil;
import org.apache.yoko.rmi.util.GetSystemPropertyAction;
import org.apache.yoko.rmi.util.stub.MethodRef;
import org.apache.yoko.rmi.util.stub.StubClass;
import org.apache.yoko.rmi.util.stub.StubInitializer;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.portable.ObjectImpl;


public class PortableRemoteObjectImpl implements PortableRemoteObjectDelegate {
    static final Logger logger = Logger
            .getLogger(PortableRemoteObjectImpl.class.getName());

    static {
        // Initialize the stub handler factory when first loaded to ensure we have 
        // class loading visibility to the factory. 
        getRMIStubInitializer(); 
    }

    private static TypeRepository getTypeRepository() {
        return RMIState.current().getTypeRepository();
    }

    static org.omg.CORBA.ORB getORB() {
        return RMIState.current().getORB();
    }

    static org.omg.PortableServer.POA getPOA() {
        return RMIState.current().getPOA();
    }

    static ClassLoader getClassLoader() {
        return RMIState.current().getClassLoader();
    }

    public void connect(Remote target, Remote source)
            throws java.rmi.RemoteException {
        if (!(source instanceof javax.rmi.CORBA.Stub))
            source = toStub(source);

        ObjectImpl obj;
        if (target instanceof ObjectImpl) {
            obj = (ObjectImpl) target;

        } else {

            try {
                exportObject(target);
            } catch (java.rmi.RemoteException ex) {
                // ignore "already exported test" //
            }

            try {
                obj = (ObjectImpl) toStub(target);
            } catch (java.rmi.NoSuchObjectException ex) {
                throw (java.rmi.RemoteException)new 
                    java.rmi.RemoteException("cannot convert to stub!").initCause(ex);
            }
        }

        try {
            ((javax.rmi.CORBA.Stub) source).connect(((ObjectImpl) obj)._orb());
        } catch (org.omg.CORBA.BAD_OPERATION bad_operation) {
            throw (RemoteException)new RemoteException(bad_operation.getMessage())
                .initCause(bad_operation);
        }
    }

    public Object narrow(Object narrowFrom, Class narrowTo)
            throws ClassCastException {
        if (narrowFrom == null)
            return null;

        if (narrowTo.isInstance(narrowFrom))
            return narrowFrom;

        logger.finer("narrow " + narrowFrom.getClass().getName() + " => " + narrowTo.getName());

        if (narrowFrom instanceof org.omg.CORBA.portable.ObjectImpl
                && narrowTo.isInterface()
                && java.rmi.Remote.class.isAssignableFrom(narrowTo)) {
            org.omg.CORBA.portable.ObjectImpl object = (org.omg.CORBA.portable.ObjectImpl) narrowFrom;

            String id = getTypeRepository().getDescriptor(narrowTo)
                    .getRepositoryID();

            //
            // actually call _is_a to verify runtime type
            //
            /*
             * if (! object._is_a (id)) { throw new ClassCastException (id); }
             */
            //
            // Get the codebase for this object, if possible...
            //
            String codebase = null;
            if (narrowFrom instanceof org.omg.CORBA_2_3.portable.ObjectImpl) {
                org.omg.CORBA_2_3.portable.ObjectImpl object_2_3 = (org.omg.CORBA_2_3.portable.ObjectImpl) narrowFrom;

                try {
                    codebase = object_2_3._get_codebase();
                } catch (org.omg.CORBA.BAD_OPERATION ex) {
                    codebase = null;
                }
            }

            if (false) {
                //
                // use most specific class possible, so as to allow
                // introspection
                // of the available methods...
                //
                try {

                    //
                    // write object reference and read just the repository id
                    //
                    org.omg.CORBA.portable.OutputStream out = RMIState
                            .current().getORB().create_output_stream();
                    out.write_Object(object);
                    org.omg.CORBA.portable.InputStream in = out
                            .create_input_stream();
                    String object_id = in.read_string();

                    if (!object_id.equals(id) && object_id.startsWith("RMI:")) {

                        String name = object_id.substring(4, object_id.indexOf(
                                ':', 4));

                        String baseName = RemoteDescriptor
                                .classNameFromStub(name);
                        if (baseName == null)
                            baseName = name;

                        //
                        // Try loading the found type...
                        //
                        Class newNarrowTo = javax.rmi.CORBA.Util.loadClass(
                                baseName, codebase, Thread.currentThread()
                                        .getContextClassLoader());

                        //
                        // if the new narrow to is more specific, use it!
                        //
                        if (narrowTo.isAssignableFrom(newNarrowTo)) {
                            narrowTo = newNarrowTo;

                            logger.finer("NARROW " + object_id + " TO "
                                    + narrowTo.getName());
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    // ignore ..
                } catch (org.omg.CORBA.SystemException ex) {
                    // ignore
                }
            }

            RMIState state = null;
            if (narrowFrom instanceof RMIServant) {
                state = ((RMIServant) narrowFrom).getRMIState();
            } else {
                state = RMIState.current();
            }

            javax.rmi.CORBA.Stub stub;
            try {
                stub = createStub(state, codebase, narrowTo);
            } catch (ClassNotFoundException ex) {
                throw (ClassCastException)new ClassCastException(narrowTo.getName()).initCause(ex);
            }

            org.omg.CORBA.portable.Delegate delegate;
            try {
                // let the stub adopt narrowFrom's identity
                delegate = object._get_delegate();

            } catch (org.omg.CORBA.BAD_OPERATION ex) {
                // ignore
                delegate = null;
            }

            stub._set_delegate(delegate);

            return stub;
        }

        throw new ClassCastException(narrowTo.getName());
    }

    static java.rmi.Remote narrow1(RMIState state,
            org.omg.CORBA.portable.ObjectImpl object, Class narrowTo)
            throws ClassCastException {
        javax.rmi.CORBA.Stub stub;

        try {
            stub = createStub(state, null, narrowTo);
        } catch (ClassNotFoundException ex) {
            throw (ClassCastException)new ClassCastException(narrowTo.getName()).initCause(ex);
        }

        org.omg.CORBA.portable.Delegate delegate;
        try {
            // let the stub adopt narrowFrom's identity
            delegate = object._get_delegate();

        } catch (org.omg.CORBA.BAD_OPERATION ex) {
            // ignore
            delegate = null;
        }

        stub._set_delegate(delegate);

        return (java.rmi.Remote) stub;
    }

    static private javax.rmi.CORBA.Stub createStub(RMIState state,
            String codebase, Class type) throws ClassNotFoundException {
        if (java.rmi.Remote.class == type) {
            return new RMIRemoteStub();
        }

        if (ClientUtil.isRunningAsClientContainer()) {
            javax.rmi.CORBA.Stub stub = state.getStaticStub(codebase, type);
            if (stub != null) {
                return stub;
            }
        }

        return createRMIStub(state, type);
    }

    static Object[] NO_ARG = new Object[0];

    static javax.rmi.CORBA.Stub createRMIStub(RMIState state, Class type)
            throws ClassNotFoundException {
        if (!type.isInterface()) {
            throw new RuntimeException("non-interfaces not supported");
        }
        
        logger.fine("Creating RMI stub for class " + type.getName()); 

        Constructor cons = getRMIStubClassConstructor(state, type);

        try {
            javax.rmi.CORBA.Stub result = (javax.rmi.CORBA.Stub) cons.newInstance(NO_ARG);
            return result;
        } catch (InstantiationException ex) {
            throw new RuntimeException(
                    "internal problem: cannot instantiate stub", ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(
                    "internal problem: cannot instantiate stub", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(
                    "internal problem: cannot instantiate stub", ex);
        }
    }

    private static java.lang.reflect.Method stub_write_replace;

    static {
        try {
            stub_write_replace = RMIStub.class.getDeclaredMethod(
                    "writeReplace", new Class[0]);

        } catch (Throwable ex) {
            logger.log(Level.WARNING, "cannot initialize: \n" + ex.getMessage(), ex);
            throw new Error("cannot initialize", ex);
        }
    }

    static synchronized Class getRMIStubClass(RMIState state, Class type)
            throws ClassNotFoundException {
        return getRMIStubClassConstructor(state, type).getDeclaringClass();
    }

    static Constructor getRMIStubClassConstructor(RMIState state, Class type)
            throws ClassNotFoundException {
        logger.fine("Requesting stub constructor of class " + type.getName()); 
        Constructor cons = (Constructor) state.stub_map.get(type);

        if (cons != null) {
            logger.fine("Returning cached constructor of class " + cons.getDeclaringClass().getName()); 
            return cons;
        }

        TypeRepository repository = state.getTypeRepository();
        RemoteDescriptor desc = (RemoteDescriptor) repository.getRemoteDescriptor(type);

        MethodDescriptor[] mdesc = desc.getMethods();
        MethodDescriptor[] descriptors = new MethodDescriptor[mdesc.length + 1];
        for (int i = 0; i < mdesc.length; i++) {
            descriptors[i] = mdesc[i];
        }
        
        logger.finer("TYPE ----> " + type);
        logger.finer("LOADER --> " + UtilImpl.getClassLoader(type));
        logger.finer("CONTEXT -> " + getClassLoader());

        MethodRef[] methods = new MethodRef[descriptors.length];

        for (int i = 0; i < mdesc.length; i++) {
            Method m = descriptors[i].getReflectedMethod();
            logger.finer("Method ----> " + m);
            methods[i] = new MethodRef(m);
        }
        methods[mdesc.length] = new MethodRef(stub_write_replace);


        Class clazz = null;

        try {
            /* Construct class! */
            clazz = StubClass.make(/* the class loader to use */
            UtilImpl.getClassLoader(type),

            /* the bean developer's bean class */
            RMIStub.class,

            /* interfaces */
            new Class[] { type },

            /* the methods */
            methods,

            /* contains only ejbCreate */
            null,

            /* our data objects */
            descriptors,

            /* the handler method */
            getPOAStubInvokeMethod(),

            /* package name (use superclass') */
            getPackageName(type),

            /* provider of handlers */
            getRMIStubInitializer());
        } catch (java.lang.NoClassDefFoundError ex) {
            /* Construct class! */
            clazz = StubClass.make(/* the class loader to use */
            getClassLoader(),

            /* the bean developer's bean class */
            RMIStub.class,

            /* interfaces */
            new Class[] { type },

            /* the methods */
            methods,

            /* contains only ejbCreate */
            null,

            /* our data objects */
            descriptors,

            /* the handler method */
            getPOAStubInvokeMethod(),

            /* package name (use superclass') */
            getPackageName(type),

            /* provider of handlers */
            getRMIStubInitializer());

        }

        if (clazz != null) {
            try {
                cons = clazz.getConstructor(new Class[0]);
                state.stub_map.put(type, cons);
            } catch (NoSuchMethodException e) {
                logger.log(Level.FINER, "constructed stub has no default constructor", e);
            }
        }

        return cons;
    }

    static String getPackageName(Class clazz) {
        String class_name = clazz.getName();
        int idx = class_name.lastIndexOf('.');
        if (idx == -1) {
            return null;
        } else {
            return class_name.substring(0, idx);
        }
    }

    private static Method poa_stub_invoke_method;

    static Method getPOAStubInvokeMethod() {
        if (poa_stub_invoke_method == null) {

            // NYI: PrivilegedAction
            try {
                // get the interface method used to invoke the stub handler
                poa_stub_invoke_method = (StubHandler.class)
                        .getDeclaredMethod("invoke", new Class[] {
                                RMIStub.class, MethodDescriptor.class,
                                Object[].class });
            } catch (NoSuchMethodException ex) {
                throw new Error("cannot find RMI Stub handler invoke method", ex);
            }

        }

        return poa_stub_invoke_method;

    }
    

    /*
     * private ClassLoader last_loader = null; private Class last_remote_type =
     * null; private String last_codebase = null; private Class last_stubclass =
     * null;
     *
     * static javax.rmi.CORBA.Stub createStaticStub (RMIState state, String
     * codebase, Class type) { String key = (codebase == null ? "null" :
     * codebase) + "#" + type.getName();
     *
     * if (state.negative_stub_set.contains (key)) return null;
     *
     * ClassLoader loader = getClassLoader ();
     *
     * synchronized (this) { if (type == last_remote_type && codebase ==
     * last_codebase && last_loader == loader) { try { return
     * (javax.rmi.CORBA.Stub)last_stubclass.newInstance (); } catch
     * (InstantiationException ex) { // ignore } catch (IllegalAccessException
     * ex) { // ignore } } }
     *
     * String stubName = RemoteDescriptor.stubClassName (type);
     *
     * try { Class stub_class = javax.rmi.CORBA.Util.loadClass (stubName,
     * codebase, loader);
     *
     * last_loader = loader; last_remote_type = type; last_codebase = codebase;
     * last_stubclass = stub_class;
     *
     * return (javax.rmi.CORBA.Stub)stub_class.newInstance (); } catch
     * (ClassNotFoundException ex) {
     *  } catch (InstantiationException ex) { // ignore } catch
     * (IllegalAccessException ex) { // ignore } catch (ClassCastException ex) { //
     * ignore }
     *
     * state.negative_stub_set.add (key);
     *
     * return null; }
     */

    public java.rmi.Remote toStub(java.rmi.Remote value)
            throws java.rmi.NoSuchObjectException {
        if (value instanceof javax.rmi.CORBA.Stub)
            return value;

        javax.rmi.CORBA.Tie tie = javax.rmi.CORBA.Util.getTie(value);
        if (tie == null) {

            // Throwable trace =
            // org.apache.yoko.rmi.api.PortableRemoteObjectExt.getStateTrace ();

            // log.info("Instance of "+value.getClass()+" is not exported in
            // "+RMIState.current().getName()+" tie="+tie+";
            // "+(trace==null?"trace is null":""), trace);

            throw new java.rmi.NoSuchObjectException("object not exported");
        }

        RMIServant servant = (RMIServant) tie;

        try {
            org.omg.PortableServer.POA poa = servant.getRMIState().getPOA();
            org.omg.CORBA.Object ref = poa.servant_to_reference(servant);
            return (java.rmi.Remote) narrow(ref, servant.getJavaClass());
        } catch (org.omg.PortableServer.POAPackage.ServantNotActive ex) {
            throw new RuntimeException("internal error: " + ex.getMessage(), ex);
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            throw new RuntimeException("internal error: " + ex.getMessage(), ex);
        }
    }

    public void exportObject(Remote obj) throws java.rmi.RemoteException {
        RMIState state = RMIState.current();

        try {
            state.checkShutDown();
        } catch (BAD_INV_ORDER ex) {
            throw new RemoteException("RMIState is deactivated", ex);
        }

        Tie tie = javax.rmi.CORBA.Util.getTie(obj);

        if (tie != null)
            throw new java.rmi.RemoteException("object already exported");

        RMIServant servant = new RMIServant(state);
        javax.rmi.CORBA.Util.registerTarget(servant, obj);

        logger.finer("exporting instance of " + obj.getClass().getName()
                + " in " + state.getName());

        try {
            servant._id = state.getPOA().activate_object(servant);
        } catch (org.omg.PortableServer.POAPackage.ServantAlreadyActive ex) {
            throw new java.rmi.RemoteException("internal error: " + ex.getMessage(), ex);
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            throw new java.rmi.RemoteException("internal error: " + ex.getMessage(), ex);
        }
    }

    public void unexportObject(Remote obj)
            throws java.rmi.NoSuchObjectException {
        javax.rmi.CORBA.Util.unexportObject(obj);
    }
    
    // the factory object used for creating stub initializers
    static private StubInitializer initializer = null;
    // the default stub handler, which is ours without overrides. 
    private static final String defaultInitializer = "org.apache.yoko.rmi.impl.RMIStubInitializer"; 
    
    /**
     * Get the RMI stub handler initializer to use for RMI invocation 
     * stubs.  The Class in question must implement the StubInitializer method. 
     * 
     * @return The class used to create StubHandler instances. 
     */
    private static StubInitializer getRMIStubInitializer() {
        if (initializer == null) {
            String factory = (String)AccessController.doPrivileged(new GetSystemPropertyAction("org.apache.yoko.rmi.RMIStubInitializerClass", defaultInitializer));
            try {
                initializer  = (StubInitializer)(Util.loadClass(factory, null, null).newInstance());
            } catch (Exception e) {
                throw (org.omg.CORBA.INITIALIZE)new org.omg.CORBA.INITIALIZE(
                    "Can not create RMIStubInitializer: " + factory).initCause(e);
            }
        }
        return initializer; 
    }
}

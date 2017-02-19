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
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;

import org.apache.yoko.rmi.util.ClientUtil;
import org.apache.yoko.rmi.util.GetSystemPropertyAction;
import org.apache.yoko.rmi.util.stub.MethodRef;
import org.apache.yoko.rmi.util.stub.StubClass;
import org.apache.yoko.rmi.util.stub.StubInitializer;
import org.apache.yoko.rmispec.util.UtilLoader;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.ObjectImpl;


public class PortableRemoteObjectImpl implements PortableRemoteObjectDelegate {
    static final Logger LOGGER = Logger
            .getLogger(PortableRemoteObjectImpl.class.getName());

    static {
        // Initialize the stub handler factory when first loaded to ensure we have
        // class loading visibility to the factory.
        getRMIStubInitializer();
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
            } catch (RemoteException ex) {
                // ignore "already exported test" //
            }

            try {
                obj = (ObjectImpl) toStub(target);
            } catch (java.rmi.NoSuchObjectException ex) {
                throw (RemoteException)new RemoteException("cannot convert to stub!").initCause(ex);
            }
        }

        try {
            ((javax.rmi.CORBA.Stub) source).connect(((ObjectImpl) obj)._orb());
        } catch (org.omg.CORBA.BAD_OPERATION bad_operation) {
            throw (RemoteException)new RemoteException(bad_operation.getMessage())
                .initCause(bad_operation);
        }
    }

    private Object narrowRMI(ObjectImpl narrowFrom, Class<?> narrowTo) {
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine(String.format("RMI narrowing %s => %s", narrowFrom.getClass().getName(), narrowTo.getName()));
        ObjectImpl object = narrowFrom;

        final String codebase = getCodebase(narrowFrom);

        RMIState state = RMIState.current();

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

    private String getCodebase(ObjectImpl narrowFrom) {
        String codebase;
        if (narrowFrom instanceof org.omg.CORBA_2_3.portable.ObjectImpl) {
            org.omg.CORBA_2_3.portable.ObjectImpl object_2_3 = (org.omg.CORBA_2_3.portable.ObjectImpl) narrowFrom;

            try {
                codebase = object_2_3._get_codebase();
            } catch (org.omg.CORBA.BAD_OPERATION ex) {
                codebase = null;
            }
        } else {
            codebase = null;
        }
        return codebase;
    }

    private Object narrowIDL(ObjectImpl narrowFrom, Class<?> narrowTo) {
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine(String.format("IDL narrowing %s => %s", narrowFrom.getClass().getName(), narrowTo.getName()));
        final ClassLoader idlClassLoader = UtilImpl.getClassLoader(narrowTo);
        final String codebase = getCodebase(narrowFrom);
        final String helperClassName = narrowTo.getName() + "Helper";

        try {
            final Class<?> helperClass = Util.loadClass(helperClassName, codebase, idlClassLoader);
            final Method helperNarrow = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
                @Override
                public Method run() throws Exception {
                    return helperClass.getMethod("narrow", org.omg.CORBA.Object.class);
                }
            });
            return helperNarrow.invoke(null, narrowFrom);
        } catch (PrivilegedActionException e) {
            throw (ClassCastException)new ClassCastException(narrowTo.getName()).initCause(e.getCause());
        } catch (Exception e) {
            throw (ClassCastException)new ClassCastException(narrowTo.getName()).initCause(e);
        }
    }

    public Object narrow(Object narrowFrom, @SuppressWarnings("rawtypes") Class narrowTo)
            throws ClassCastException {
        if (narrowFrom == null)
            return null;

        if (narrowTo.isInstance(narrowFrom))
            return narrowFrom;

        final String fromClassName = narrowFrom.getClass().getName();
        final String toClassName = narrowTo.getName();
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.finer(String.format("narrow %s => %s", fromClassName, toClassName));

        if (!(narrowFrom instanceof org.omg.CORBA.portable.ObjectImpl))
            throw new ClassCastException(String.format(
                    "object to narrow (runtime type %s) is not an instance of %s",
                    fromClassName, ObjectImpl.class.getName()));
        if (!narrowTo.isInterface())
            throw new ClassCastException(String.format("%s is not an interface", toClassName));

        final boolean isRemote = Remote.class.isAssignableFrom(narrowTo);
        final boolean isIDLEntity = IDLEntity.class.isAssignableFrom(narrowTo);

        if (isRemote && isIDLEntity)
            throw new ClassCastException(String.format(
                    "%s invalidly extends both %s and %s",
                    toClassName, Remote.class.getName(), IDLEntity.class.getName()));
        if (isRemote)
            return narrowRMI((ObjectImpl) narrowFrom, narrowTo);
        if (isIDLEntity)
            return narrowIDL((ObjectImpl) narrowFrom, narrowTo);

        throw new ClassCastException(String.format(
                    "%s extends neither %s nor %s",
                    toClassName, Remote.class.getName(), IDLEntity.class.getName()));
    }

    static java.rmi.Remote narrow1(RMIState state, ObjectImpl object, Class<?> narrowTo) throws ClassCastException {
        Stub stub;

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

    static private Stub createStub(RMIState state, String codebase, Class<?> type) throws ClassNotFoundException {
        if (Remote.class == type) {
            return new RMIRemoteStub();
        }

        if (ClientUtil.isRunningAsClientContainer()) {
            Stub stub = state.getStaticStub(codebase, type);
            if (stub != null) {
                return stub;
            }
        }

        return createRMIStub(state, type);
    }

    static Object[] NO_ARG = new Object[0];

    static Stub createRMIStub(RMIState state, Class<?> type) throws ClassNotFoundException {
        if (!type.isInterface()) {
            throw new RuntimeException("non-interfaces not supported");
        }

        LOGGER.fine("Creating RMI stub for class " + type.getName());

        Constructor<? extends Stub> cons = getRMIStubClassConstructor(state, type);

        try {
            Stub result = cons.newInstance(NO_ARG);
            return result;
        } catch (InstantiationException ex) {
            throw new RuntimeException(
                    "internal problem: cannot instantiate stub", ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(
                    "internal problem: cannot instantiate stub", ex.getCause());
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
            LOGGER.log(Level.WARNING, "cannot initialize: \n" + ex.getMessage(), ex);
            throw new Error("cannot initialize", ex);
        }
    }

    static synchronized Class<?> getRMIStubClass(RMIState state, Class<?> type) throws ClassNotFoundException {
        return getRMIStubClassConstructor(state, type).getDeclaringClass();
    }

    static Constructor<? extends Stub> getRMIStubClassConstructor(RMIState state, Class<?> type) throws ClassNotFoundException {
        LOGGER.fine("Requesting stub constructor of class " + type.getName());
        @SuppressWarnings("unchecked")
        Constructor<? extends Stub> cons = (Constructor<? extends Stub>) state.stub_map.get(type);

        if (cons != null) {
            LOGGER.fine("Returning cached constructor of class " + cons.getDeclaringClass().getName());
            return cons;
        }

        TypeRepository repository = state.repo;
        RemoteDescriptor desc = (RemoteDescriptor) repository.getRemoteInterface(type);

        MethodDescriptor[] mdesc = desc.getMethods();
        MethodDescriptor[] descriptors = new MethodDescriptor[mdesc.length + 1];
        for (int i = 0; i < mdesc.length; i++) {
            descriptors[i] = mdesc[i];
        }

        LOGGER.finer("TYPE ----> " + type);
        LOGGER.finer("LOADER --> " + UtilImpl.getClassLoader(type));
        LOGGER.finer("CONTEXT -> " + getClassLoader());

        MethodRef[] methods = new MethodRef[descriptors.length];

        for (int i = 0; i < mdesc.length; i++) {
            Method m = descriptors[i].getReflectedMethod();
            LOGGER.finer("Method ----> " + m);
            methods[i] = new MethodRef(m);
        }
        methods[mdesc.length] = new MethodRef(stub_write_replace);


        Class<?> clazz = null;

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
                cons = (Constructor<? extends Stub>) clazz.getConstructor();
                state.stub_map.put(type, cons);
            } catch (NoSuchMethodException e) {
                LOGGER.log(Level.FINER, "constructed stub has no default constructor", e);
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

    public java.rmi.Remote toStub(java.rmi.Remote value)
            throws java.rmi.NoSuchObjectException {
        if (value instanceof javax.rmi.CORBA.Stub)
            return value;

        javax.rmi.CORBA.Tie tie = javax.rmi.CORBA.Util.getTie(value);
        if (tie == null) {
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

        LOGGER.finer("exporting instance of " + obj.getClass().getName()
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
                initializer = (StubInitializer)(UtilLoader.loadServiceClass(factory, "org.apache.yoko.rmi.RMIStubInitializerClass").newInstance());
            } catch (Exception e) {
                throw (org.omg.CORBA.INITIALIZE)new org.omg.CORBA.INITIALIZE(
                    "Can not create RMIStubInitializer: " + factory).initCause(e);
            }
        }
        return initializer;
    }
}

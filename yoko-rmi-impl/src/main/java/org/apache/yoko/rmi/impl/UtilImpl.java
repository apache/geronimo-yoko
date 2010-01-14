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
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.UtilDelegate;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.PortableRemoteObject;

import org.apache.yoko.rmi.util.GetSystemPropertyAction;
import org.omg.CORBA.Any;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.UnknownException;

public class UtilImpl implements UtilDelegate {
    static final Logger logger = Logger.getLogger(UtilImpl.class.getName());

    // Note: this field must be declared before the static intializer that calls Util.loadClass
    // since that method will call loadClass0 which uses this field... if it is below the static
    // initializer the _secman field will be null
    private static final SecMan _secman = getSecMan();

    static final Class JAVAX_TRANSACTION_USERTRANSACTION_CLASS;

    static {
        Class userTransactionClass;
        try {
            userTransactionClass = Util.loadClass("javax.transaction.userTransaction", null, null);
        }
        catch (ClassNotFoundException e) {
            logger.log(Level.FINE, "error loading transaction class", e);
            userTransactionClass = null;
        }
        JAVAX_TRANSACTION_USERTRANSACTION_CLASS = userTransactionClass;
    }

    /**
     * Translate a CORBA SystemException to the corresponding RemoteException
     */
    public RemoteException mapSystemException(
            final org.omg.CORBA.SystemException theException) {

        org.omg.CORBA.SystemException ex = theException;

        if (ex instanceof UnknownException) {
            Throwable orig = ((UnknownException) ex).originalEx;
            if (orig instanceof Error) {
                return new ServerError("Error occurred in server thread",
                        (Error) orig);
            } else if (orig instanceof RemoteException) {
                return new ServerException(
                        "RemoteException occurred in server thread",
                        (Exception) orig);
            } else if (orig instanceof RuntimeException) {
                throw (RuntimeException) orig;
            }
        }

        Class exclass = ex.getClass();
        String name = exclass.getName();

        Class exclz = (Class) CORBA_TO_RMI_MAP.get(name);

        if (exclz == null) {
            exclz = RemoteException.class;

            Class exc = ex.getClass();

            for (int i = 0; i < CORBA_TO_RMI_EXCEPTION.length; i += 2) {
                if (CORBA_TO_RMI_EXCEPTION[i].isAssignableFrom(exc)) {
                    exclz = CORBA_TO_RMI_EXCEPTION[i + 1];
                    break;
                }
            }
        }

        RemoteException rex = null;

        // construct the exception message according to �1.4.8

        StringBuffer buf = new StringBuffer("CORBA ");

        final String prefix = "org.omg.CORBA";
        if (name.startsWith(prefix)) {
            buf.append(name.substring(prefix.length() + 1));
        } else {
            buf.append(name);
        }

        buf.append(" ");
        buf.append(ex.minor);

        switch (ex.completed.value()) {
            case org.omg.CORBA.CompletionStatus._COMPLETED_YES:
                buf.append(" Yes");
                break;

            case org.omg.CORBA.CompletionStatus._COMPLETED_NO:
                buf.append(" No");
                break;

            case org.omg.CORBA.CompletionStatus._COMPLETED_MAYBE:
                buf.append(" Maybe");
                break;
        }

        String exceptionMessage = buf.toString();

        try {

            rex = (RemoteException) newInstance(exclz,
                    new Class[]{String.class},
                    new Object[]{exceptionMessage});

            rex.detail = ex;

        } catch (RuntimeException ex2) {

            rex = new RemoteException(exceptionMessage, ex2);
        }

        return rex;
    }

    static org.omg.CORBA.SystemException mapRemoteException(RemoteException rex) {
        if (rex.detail != null
                && rex.detail instanceof org.omg.CORBA.SystemException)
            return (org.omg.CORBA.SystemException) rex.detail;

        if (rex.detail != null && rex.detail instanceof RemoteException)
            rex = (RemoteException) rex.detail;

        if (rex instanceof java.rmi.NoSuchObjectException) {
            throw new org.omg.CORBA.INV_OBJREF(rex.getMessage());

        } else if (rex instanceof java.rmi.AccessException) {
            throw new org.omg.CORBA.NO_PERMISSION(rex.getMessage());

        } else if (rex instanceof java.rmi.MarshalException) {
            throw new org.omg.CORBA.MARSHAL(rex.getMessage());

        } else if (rex instanceof javax.transaction.TransactionRequiredException) {
            throw new org.omg.CORBA.TRANSACTION_REQUIRED(rex.getMessage());

        } else if (rex instanceof javax.transaction.TransactionRolledbackException) {
            throw new org.omg.CORBA.TRANSACTION_ROLLEDBACK(rex.getMessage());

        } else if (rex instanceof javax.transaction.InvalidTransactionException) {
            throw new org.omg.CORBA.INVALID_TRANSACTION(rex.getMessage());

            /*
             * } else if (rex.detail != null) { throw new
             * org.omg.CORBA.portable.UnknownException (rex.detail);
             */

        } else {
            throw new org.omg.CORBA.portable.UnknownException(rex);

        }

    }

    /**
     * Generic function for reflective instantiation
     */
    private Object newInstance(final Class cls, final Class[] arg_types,
            final Object[] args) {
        return java.security.AccessController
                .doPrivileged(new java.security.PrivilegedAction() {
                    public Object run() {
                        try {
                            java.lang.reflect.Constructor cons = cls
                                    .getConstructor(arg_types);

                            return cons.newInstance(args);

                        } catch (NoSuchMethodException ex) {
                            return new RuntimeException("cannot instantiate "
                                    + cls + ": " + ex.getMessage(), ex);

                        } catch (InstantiationException ex) {
                            return new RuntimeException("cannot instantiate "
                                    + cls + ": " + ex.getMessage(), ex);

                        } catch (IllegalAccessException ex) {
                            return new RuntimeException("cannot instantiate "
                                    + cls + ": " + ex.getMessage(), ex);

                        } catch (IllegalArgumentException ex) {
                            return new RuntimeException("cannot instantiate "
                                    + cls + ": " + ex.getMessage(), ex);

                        } catch (InvocationTargetException ex) {
                            return new RuntimeException("cannot instantiate "
                                    + cls + ": " + ex.getMessage(), ex);

                        }
                    }
                });
    }

    /**
     * Write an org.omg.CORBA.Any containing the given object.
     * <p/>
     * The object is not converted or translated, simply written. Thus, it must
     * be either an IDL-generated entity, a Serializable value or an
     * org.omg.CORBA.Object. Specifically, a Remote objects and Servants cannot
     * be written, but their corresponding Stubs can.
     *
     * @param out The stream to which the value should be written
     * @param obj The object/value to write
     * @throws org.omg.CORBA.MARSHAL if the value cannot be written
     */
    public void writeAny(org.omg.CORBA.portable.OutputStream out, Object obj)
            throws org.omg.CORBA.SystemException {
        //
        // In this implementation of RMI/IIOP we do not use type codes
        // beyond the implementation of this method, and it's
        // counterpart readAny.
        //

        org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
        Any any = orb.create_any();

        if (obj == null) {
            // JDK 1.3.1-1 doesn't like NULL typecodes, so
            // we write it as a null Value.

            any.insert_Value(null);

        } else if (obj instanceof String) {
            any.insert_Value((String) obj);

        } else if (obj instanceof org.omg.CORBA.Object) {
            any.insert_Object((org.omg.CORBA.Object) obj);

        } else if (obj instanceof Any) {
            any.insert_any((Any) obj);

        } else if (obj instanceof IDLEntity) {
            any.insert_Value((Serializable) obj);

        } else if (obj instanceof java.rmi.Remote) {
            Remote ro = (Remote) obj;
            org.omg.CORBA.Object corba_obj;
            try {
                corba_obj = (org.omg.CORBA.Object) PortableRemoteObject
                        .toStub(ro);
            } catch (java.rmi.NoSuchObjectException ex) {
                throw (org.omg.CORBA.MARSHAL)new org.omg.CORBA.MARSHAL(
                    "object not exported " + ro).initCause(ex);
            }

            any.insert_Object((org.omg.CORBA.Object) corba_obj);

        } else if (obj instanceof Serializable || obj instanceof Externalizable) {

            any.insert_Value((Serializable) obj);

        } else {
            throw new MARSHAL("cannot write as " + obj.getClass()
                    + " to an Any");
        }

        out.write_any(any);
    }

    public Object readAny(org.omg.CORBA.portable.InputStream in)
            throws org.omg.CORBA.SystemException {
        Any any = in.read_any();
        TypeCode typecode = any.type();

        switch (typecode.kind().value()) {

            case TCKind._tk_null:
            case TCKind._tk_void:
                return null;

            case TCKind._tk_value:
            case TCKind._tk_value_box:
                return any.extract_Value();

            case TCKind._tk_abstract_interface:
                org.omg.CORBA_2_3.portable.InputStream in23 = (org.omg.CORBA_2_3.portable.InputStream) any
                        .create_input_stream();
                return in23.read_abstract_interface();

            case TCKind._tk_string:
                return any.extract_string();

            case TCKind._tk_objref:
                org.omg.CORBA.Object ref = any.extract_Object();
                return ref;

            case TCKind._tk_any:
                return any.extract_any();

            default:
                String id = "<unknown>";
                try {
                    id = typecode.id();
                } catch (org.omg.CORBA.TypeCodePackage.BadKind ex) {
                }

                throw new MARSHAL("cannot extract " + id + " ("
                        + typecode.kind().value() + ") value from Any");
        }
    }

    /**
     * Write a remote object. It must already be exported.
     * <p/>
     * This method accepts values of org.omg.CORBA.Object (including stubs), and
     * instances of java.rmi.Remote for objects that have already been exported.
     */
    public void writeRemoteObject(org.omg.CORBA.portable.OutputStream out,
            Object obj) throws org.omg.CORBA.SystemException {
        org.omg.CORBA.Object objref = null;

        if (obj == null) {
            out.write_Object(null);
            return;

        } else if (obj instanceof org.omg.CORBA.Object) {
            objref = (org.omg.CORBA.Object) obj;

        } else if (obj instanceof Remote) {
            try {
                objref = (javax.rmi.CORBA.Stub) PortableRemoteObject
                        .toStub((java.rmi.Remote) obj);

            } catch (java.rmi.NoSuchObjectException ex) {
            }

            if (objref == null) {

                try {
                    PortableRemoteObject.exportObject((java.rmi.Remote) obj);
                    objref = (javax.rmi.CORBA.Stub) PortableRemoteObject
                            .toStub((java.rmi.Remote) obj);
                } catch (java.rmi.RemoteException ex) {
                    throw (MARSHAL)new MARSHAL("cannot convert Remote to Object").initCause(ex);
                }
            }

        } else {
            throw new MARSHAL(
                    "object is neither Remote nor org.omg.CORBA.Object: "
                            + obj.getClass().getName());
        }

        out.write_Object(objref);
    }

    public void writeAbstractObject(org.omg.CORBA.portable.OutputStream out,
            Object obj) {
        logger.finer("writeAbstractObject.1 " + " out=" + out);

        if (obj instanceof org.omg.CORBA.Object || obj instanceof Serializable) {

            // skip //

        } else if (obj instanceof Remote) {
            org.omg.CORBA.Object objref = null;

            try {
                objref = (org.omg.CORBA.Object) PortableRemoteObject
                        .toStub((Remote) obj);

            } catch (java.rmi.NoSuchObjectException ex) {
            }

            if (objref == null) {
                try {
                    PortableRemoteObject.exportObject((Remote) obj);
                    objref = (org.omg.CORBA.Object) PortableRemoteObject
                            .toStub((Remote) obj);
                } catch (RemoteException ex) {
                    throw (MARSHAL)new MARSHAL("unable to export object").initCause(ex);
                }
            }
            obj = objref; 
        }

        org.omg.CORBA_2_3.portable.OutputStream out_ = (org.omg.CORBA_2_3.portable.OutputStream) out;

        logger.finer("writeAbstractObject.2 " + " out=" + out);

        out_.write_abstract_interface(obj);
    }

    protected java.util.Map tie_map() {
        return RMIState.current().tie_map;
    }

    public void registerTarget(Tie tie, Remote obj) {
        if (obj == null)
            throw new IllegalArgumentException("remote object is null");

        tie.setTarget(obj);
        tie_map().put(obj, tie);

        // log.info("exported instance of "+obj.getClass()+" in
        // "+RMIState.current().getName());
    }

    public Tie getTie(Remote obj) {
        if (obj == null)
            return null;

        return (Tie) tie_map().get(obj);
    }

    public ValueHandler createValueHandler() {
        return RMIState.current().createValueHandler();
        // return new ValueHandlerImpl (null);
    }

    public String getCodebase(Class clz) {
        if (clz == null)
            return null;

        if (clz.isArray())
            return getCodebase(clz.getComponentType());

        if (clz.isPrimitive())
            return null;

        ClassLoader theLoader = clz.getClassLoader();

        // ignore system classes
        if (theLoader == null)
            return null;

        // ignore J2SE base class loader
        if (theLoader == (Object.class).getClassLoader())
            return null;

        // ignore standard extensions
        if (JAVAX_TRANSACTION_USERTRANSACTION_CLASS != null &&
                theLoader == JAVAX_TRANSACTION_USERTRANSACTION_CLASS.getClassLoader())
            return null;

        RMIState state = RMIState.current();
        ClassLoader stateLoader = state.getClassLoader();

        try {
            // is the class loaded with the stateLoader?
            if (clz.equals(stateLoader.loadClass(clz.getName()))) {
                java.net.URL codebaseURL = state.getCodeBase();

                if (codebaseURL != null) {
                    logger.finer("class " + clz.getName() + " => "
                            + codebaseURL);

                    // System.out.println ("class "+clz.getName()+" =>
                    // "+codebaseURL);
                    return codebaseURL.toString();
                }
            }
        } catch (ClassNotFoundException ex) {
            // ignore
        }

        return (String) AccessController.doPrivileged(new GetSystemPropertyAction("java.rmi.server.codebase"));
    }

    static class SecMan extends java.rmi.RMISecurityManager {
        public Class[] getClassContext() {
            return super.getClassContext();
        }
    }

    private static SecMan getSecMan() {
        try {
            return (SecMan) AccessController
                    .doPrivileged(new java.security.PrivilegedExceptionAction() {
                        public Object run() {
                            return new SecMan();
                        }
                    });
        } catch (PrivilegedActionException e) {
            throw new RuntimeException(e);
        }

    }

    public Class loadClass(String name, String codebase, ClassLoader loader)
            throws ClassNotFoundException {
        try {
            return loadClass0(name, codebase, loader);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.FINER, "cannot load from " + codebase + " " + 
                ex.getMessage(), ex);
            throw ex;
        }
    }

    static public Class loadClass0(String name, String codebase, ClassLoader loader)
            throws ClassNotFoundException {
        Class result = null;

        if (loader != null) {
            try {
                result = loader.loadClass(name);
            } catch (ClassNotFoundException ex) {
                // skip //
            }

            if (result != null)
                return result;
        }

        ClassLoader stackLoader = null;
        ClassLoader thisLoader = Util.class.getClassLoader(); 
        Class[] stack = _secman.getClassContext();
        for (int i = 1; i < stack.length; i++) {
            ClassLoader testLoader = stack[i].getClassLoader();
            if (testLoader != null && testLoader != thisLoader)
            {
                stackLoader = thisLoader; 
                break; 
            }
        }

        if (stackLoader != null) {
            try {
                result = stackLoader.loadClass(name);
            } catch (ClassNotFoundException ex) {
                // skip //
            }

            if (result != null) {
                return result;
            }
        }

        // try loading using our loader, just in case we really were loaded
        // using the same classloader the delegate is in.
        if (thisLoader != null) {
            try {
                result = thisLoader.loadClass(name);
            } catch (ClassNotFoundException ex) {
                // skip //
            }

            if (result != null) {
                return result;
            }
        }

        if (codebase != null && !"".equals(codebase)
                && !Boolean.getBoolean("java.rmi.server.useCodeBaseOnly")) {
            try {
                logger.finer("trying RMIClassLoader");

                URLClassLoader url_loader = new URLClassLoader(
                        new URL[]{new URL(codebase)}, loader);

                result = url_loader.loadClass(name);

                // log.info("SUCESSFUL class download "+name+" from "+codebase,
                // new Throwable("TRACE"));

            } catch (ClassNotFoundException ex) {
                logger.log(Level.FINER, "RMIClassLoader says " + ex.getMessage(), ex);

                // log.info("FAILED class download "+name+" from "+codebase,
                // ex);

                // skip //
            } catch (MalformedURLException ex) {
                logger.log(Level.FINER, "RMIClassLoader says " + ex.getMessage(), ex);

                logger.finer("FAILED class download " + name + " from "
                        + codebase + " " + ex.getMessage());

                // skip //
            } catch (RuntimeException ex) {

                logger.log(Level.FINER, "FAILED class download " + name + " from "
                        + codebase + " " + ex.getMessage(), ex);

            }

            if (result != null) {
                return result;
            }

        } else {

            codebase = (String) AccessController.doPrivileged(new GetSystemPropertyAction("java.rmi.server.codebase"));

            if (codebase != null) {
                try {
                    result = java.rmi.server.RMIClassLoader.loadClass(codebase,
                            name);
                } catch (ClassNotFoundException ex) {
                    // skip //
                } catch (MalformedURLException ex) {
                    // skip //
                }

                if (result != null) {
                    return result;
                }
            }
        }

        if (loader == null) {
            loader = getContextClassLoader();
        }

        try {
            result = loader.loadClass(name);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.FINER, "LocalLoader says " + ex.getMessage(), ex);
        }

        if (result != null) {
            return result;
        }

        throw new ClassNotFoundException(name);
    }

    public boolean isLocal(Stub stub) throws RemoteException {
        try {
            if (stub instanceof RMIStub) {
                return true;
            } else {
                return stub._is_local();
            }
        } catch (org.omg.CORBA.SystemException ex) {
            throw mapSystemException(ex);
        }
    }

    public RemoteException wrapException(Throwable ex) {
        if (ex instanceof Error) {
            return new java.rmi.ServerError(ex.getMessage(), (Error) ex);

        } else if (ex instanceof RemoteException) {
            return new java.rmi.ServerException(ex.getMessage(), (Exception) ex);

        } else if (ex instanceof org.omg.CORBA.portable.UnknownException) {
            org.omg.CORBA.portable.UnknownException uex = (org.omg.CORBA.portable.UnknownException) ex;

            return new java.rmi.ServerError(
                    ex.getMessage(),
                    (uex.originalEx instanceof Error ? (Error) uex.originalEx
                            : new Error("[OTHER EXCEPTION] " + ex.getMessage())));

        } else if (ex instanceof org.omg.CORBA.SystemException) {
            return mapSystemException((org.omg.CORBA.SystemException) ex);

        } else if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;

        } else {
            return new java.rmi.RemoteException(ex.getMessage(), ex);
        }
    }

    static ClassLoader getContextClassLoader() {
        return (ClassLoader) AccessController
                .doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
    }

    static ClassLoader getClassLoader(final Class clz) {
        if (System.getSecurityManager() == null) {
            return clz.getClassLoader();
        } else {
            return (ClassLoader) AccessController
                    .doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            return clz.getClassLoader();
                        }
                    });
        }
    }

    static Object copyRMIStub(RMIStub stub) throws RemoteException {
        ClassLoader loader = getContextClassLoader();

        if (getClassLoader(stub._descriptor.getJavaClass()) == loader) {
            return stub;
        }

        RemoteDescriptor desc = stub._descriptor;

        Class targetClass;

        try {
            targetClass = Util.loadClass(desc.getJavaClass().getName(), stub
                    ._get_codebase(), loader);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.FINER, "copyRMIStub exception (current loader is: " + loader
                    + ") " + ex.getMessage(), ex);
            throw new RemoteException("Class not found", ex);
        }

        return PortableRemoteObjectImpl.narrow1(RMIState.current(), stub,
                targetClass);
    }

    static boolean copy_with_corba = false;

    /**
     * Copy a single object, maintaining internal reference integrity.
     * <p/>
     * This is done by writing and reading the object to/from a temporary
     * stream. As such, this should be called after the receiving context's
     * class-loaders etc. have been set up.
     */
    public Object copyObject(Object obj, org.omg.CORBA.ORB orb)
            throws RemoteException {
        if (obj == null)
            return null;

        if (orb == null)
            throw new NullPointerException();

        if (obj instanceof String || obj instanceof Number)
            return obj;

        if (obj instanceof RMIStub) {
            return copyRMIStub((RMIStub) obj);
        }

        /*
         * try { org.omg.CORBA_2_3.portable.OutputStream out =
         * (org.omg.CORBA_2_3.portable.OutputStream) orb.create_output_stream
         * ();
         *
         * out.write_value ((java.io.Serializable) obj);
         *
         * org.omg.CORBA_2_3.portable.InputStream in =
         * (org.omg.CORBA_2_3.portable.InputStream) out.create_input_stream ();
         *
         * return in.read_value ();
         *  } catch (org.omg.CORBA.SystemException ex) { throw
         * mapSystemException (ex); }
         */
        try {
            TypeRepository rep = RMIState.current().getTypeRepository();
            CopyState state = new CopyState(rep);
            return state.copy(obj);
        } catch (CopyRecursionException ex) {
            throw new MarshalException("unable to resolve recursion", ex);
        } catch (org.omg.CORBA.SystemException ex) {
            throw mapSystemException(ex);
        }

    }

    static final Object READ_SERIALIZABLE_KEY = new Object();

    /**
     * Copy an array of objects, maintaining internal reference integrity.
     * <p/>
     * This is done by writing and reading the object array to/from a temporary
     * stream. As such, this should be called after the receiving context's
     * class-loaders etc. have been set up.
     */
    public Object[] copyObjects(Object[] objs, org.omg.CORBA.ORB orb)
            throws RemoteException {

        if (objs == null || orb == null)
            throw new NullPointerException();

        if (objs.length == 0)
            return objs;

        try {

            TypeRepository rep = RMIState.current().getTypeRepository();
            CopyState state = new CopyState(rep);
            try {
                return (Object[]) state.copy(objs);
            } catch (CopyRecursionException ex) {
                throw new MarshalException("unable to resolve recursion", ex);
            }

            /*
             * int length = objs.length;
             *
             * for (int i = 0; i < length; i++) {
             *
             * Object val = objs[i];
             *
             * if (val == null || val instanceof String || val instanceof
             * Number) { // do nothing, just leave the object in place //
             *  } else if (val instanceof RMIStub) { objs[i] =
             * copyRMIStub((RMIStub)val);
             *  } else { if (orb == null) { orb = RMIState.current().getORB(); }
             *
             * if (out == null) { out =
             * (org.omg.CORBA_2_3.portable.OutputStream) orb
             * .create_output_stream(); }
             *
             * out.write_value((java.io.Serializable) val); objs[i] =
             * READ_SERIALIZABLE_KEY; }
             *  }
             *
             * if (out != null) {
             *
             * org.omg.CORBA_2_3.portable.InputStream in =
             * (org.omg.CORBA_2_3.portable.InputStream) out
             * .create_input_stream();
             *
             * for (int i = 0; i < length; i++) { if (objs[i] ==
             * READ_SERIALIZABLE_KEY) { objs[i] = in.read_value(); } } }
             *
             * return objs;
             */

        } catch (org.omg.CORBA.SystemException ex) {
            throw mapSystemException(ex);
        }
    }

    public void unexportObject(Remote obj)
            throws java.rmi.NoSuchObjectException {
        if (obj == null)
            return;

        java.util.Map tie_map = tie_map();

        if (tie_map == null)
            return;

        Tie tie = (Tie) tie_map.remove(obj);

        if (tie == null) {
            logger.fine("unexporting unknown instance of "
                    + obj.getClass().getName() + " from "
                    + RMIState.current().getName());
            return;
        } else {
            logger.finer("unexporting instance of " + obj.getClass().getName()
                    + " from " + RMIState.current().getName());
        }

        tie.deactivate();
    }

    static final Class[] RMI_TO_CORBA_EXCEPTION;

//  We want to avoid a dependency on JTA, so we add these classes only if JTA is available.
    static {
	Class[] rmiToCorba;
	try {
	    rmiToCorba = new Class[] {
		    Util.loadClass("javax.transaction.HeuresticMixedException", null, null),
		    org.omg.CosTransactions.HeuristicMixed.class,

		    Util.loadClass("javax.transaction.HeuristicRollbackException", null, null),
		    org.omg.CosTransactions.HeuristicRollback.class,

	            Util.loadClass("javax.transaction.HeuristicCommitException", null, null),
	            org.omg.CosTransactions.HeuristicCommit.class,

	            Util.loadClass("javax.transaction.NotSupportedException", null, null),
	            org.omg.CosTransactions.SubtransactionsUnavailable.class,

	            Util.loadClass("javax.transaction.InvalidTransactionException", null, null),
	            org.omg.CORBA.INVALID_TRANSACTION.class,

	            Util.loadClass("javax.transaction.TransactionRequiredException", null, null),
	            org.omg.CORBA.TRANSACTION_REQUIRED.class,

	            Util.loadClass("javax.transaction.TransactionRolledbackException", null, null),
	            org.omg.CORBA.TRANSACTION_ROLLEDBACK.class,

	            Util.loadClass("javax.transaction.RollbackException", null, null),
	            org.omg.CORBA.TRANSACTION_ROLLEDBACK.class
	    };

	}
	catch(ClassNotFoundException e) {
	    rmiToCorba = new Class[0];
	}
	RMI_TO_CORBA_EXCEPTION = rmiToCorba;
    }

    static final Class[] CORBA_TO_RMI_EXCEPTION = {
            org.omg.CORBA.BAD_PARAM.class, java.rmi.MarshalException.class,

            org.omg.CORBA.COMM_FAILURE.class, java.rmi.MarshalException.class,

            org.omg.CORBA.INV_OBJREF.class,
            java.rmi.NoSuchObjectException.class,

            org.omg.CORBA.MARSHAL.class, java.rmi.MarshalException.class,

            org.omg.CORBA.NO_IMPLEMENT.class,
            java.rmi.NoSuchObjectException.class,

            org.omg.CORBA.NO_PERMISSION.class, java.rmi.AccessException.class,

            org.omg.CORBA.OBJECT_NOT_EXIST.class,
            java.rmi.NoSuchObjectException.class,

            org.omg.CORBA.TRANSACTION_REQUIRED.class,
            javax.transaction.TransactionRequiredException.class,

            org.omg.CORBA.TRANSACTION_ROLLEDBACK.class,
            javax.transaction.TransactionRolledbackException.class,

            org.omg.CORBA.INVALID_TRANSACTION.class,
            javax.transaction.InvalidTransactionException.class};

    static final java.util.Map CORBA_TO_RMI_MAP = new java.util.HashMap();

    static {
        for (int i = 0; i < CORBA_TO_RMI_EXCEPTION.length; i += 2) {
            Class corba = CORBA_TO_RMI_EXCEPTION[i];
            Class rmi = CORBA_TO_RMI_EXCEPTION[i + 1];

            CORBA_TO_RMI_MAP.put(corba.getName(), rmi);
        }
    }
}

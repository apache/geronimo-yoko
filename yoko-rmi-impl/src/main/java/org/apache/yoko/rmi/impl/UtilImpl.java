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

import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.rmi.util.GetSystemPropertyAction;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CORBA.portable.UnknownException;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.UtilDelegate;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.PortableRemoteObject;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static org.apache.yoko.logging.VerboseLogging.CLASS_LOG;

public class UtilImpl implements UtilDelegate {
    private static final Logger logger = Logger.getLogger(UtilImpl.class.getName());

    // Note: this field must be declared before the static intializer that calls Util.loadClass
    // since that method will call loadClass0 which uses this field... if it is below the static
    // initializer the _secman field will be null
    private static final SecMan _secman = getSecMan();

    private static final ClassLoader BEST_GUESS_AT_EXTENSION_CLASS_LOADER;
    static {
        ClassLoader candidateLoader = getClassLoader(UtilImpl.class);

        if (candidateLoader == null) {
            // looks like this class was loaded by the boot class loader
            // so it is safe to try loading stuff by reflection without
            // worrying about whether we have imported the packages into the OSGi bundle
            candidateLoader = findFirstNonNullLoader(
                    "sun.net.spi.nameservice.dns.DNSNameService",
                    "javax.transaction.UserTransaction");
        }

        // We will try to find the extension class
        // loader by ascending the loader hierarchy
        // starting from whatever loader we found.
        for (ClassLoader l = candidateLoader; l != null; l = l.getParent()) {
            candidateLoader = l;
        }

        BEST_GUESS_AT_EXTENSION_CLASS_LOADER = candidateLoader;
    }

    private static ClassLoader findFirstNonNullLoader(String...classNames) {
        for (String className : classNames) {
            try {
                final Class<?> c = Class.forName(className);
                ClassLoader cl = getClassLoader(c);
                if (cl != null) return cl;
            } catch (Exception|NoClassDefFoundError swallowed) {
            }
        }
        return null;
    }

    /**
     * Translate a CORBA SystemException to the corresponding RemoteException
     */
    public RemoteException mapSystemException(final SystemException theException) {

        SystemException ex = theException;

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

        Class<? extends SystemException> exclass = ex.getClass();
        String name = exclass.getName();

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

        return createRemoteException(ex, exceptionMessage);
    }

    private RemoteException createRemoteException(SystemException sysEx, String s) {
        RemoteException result;
        try {
            throw sysEx;
        } catch (BAD_PARAM|COMM_FAILURE|MARSHAL e) {
            result = new MarshalException(s);
        } catch (INV_OBJREF|NO_IMPLEMENT|OBJECT_NOT_EXIST e) {
            result = new NoSuchObjectException(s);
        } catch(NO_PERMISSION e) {
            result = new AccessException(s);
        } catch (TRANSACTION_REQUIRED e) {
            result = createRemoteException("javax.transaction.TransactionRequiredException", s);
        } catch (TRANSACTION_ROLLEDBACK e) {
            result = createRemoteException("javax.transaction.TransactionRolledbackException", s);
        } catch (INVALID_TRANSACTION e) {
            result = createRemoteException("javax.transaction.InvalidTransactionException", s);
        } catch (SystemException catchAll) {
            result = new RemoteException(s);
        }
        result.detail = sysEx;
        return result;
    }

    private static RemoteException createRemoteException(String className, String s) {
        RemoteException result;
        try {
            @SuppressWarnings("unchecked")
            Class<? extends RemoteException> clazz =  Util.loadClass(className, null, null);
            Constructor<? extends RemoteException> ctor = clazz.getConstructor(String.class);
            result = ctor.newInstance(s);
        } catch (Throwable t) {
            result = new RemoteException(s);
            result.addSuppressed(t);
        }
        return result;
    }

    static SystemException mapRemoteException(RemoteException rex) {
        if (rex.detail instanceof org.omg.CORBA.SystemException)
            return (org.omg.CORBA.SystemException) rex.detail;

        if (rex.detail instanceof RemoteException)
            rex = (RemoteException) rex.detail;

        SystemException sysEx;

        if (rex instanceof java.rmi.NoSuchObjectException) {
            sysEx = new org.omg.CORBA.INV_OBJREF(rex.getMessage());
        } else if (rex instanceof java.rmi.AccessException) {
            sysEx = new org.omg.CORBA.NO_PERMISSION(rex.getMessage());
        } else if (rex instanceof java.rmi.MarshalException) {
            sysEx = new org.omg.CORBA.MARSHAL(rex.getMessage());
        } else {
            sysEx = createSystemException(rex);
        }
        sysEx.initCause(rex);
        throw sysEx;
    }

    private static SystemException createSystemException(RemoteException rex) {
        return createSystemException(rex, rex.getClass());
    }

    /**
     * utility method to check for JTA exception types without linking to the JTA classes directly
     */
    private static SystemException createSystemException(RemoteException rex, Class<?> fromClass) {
        // Recurse up the parent chain,
        // until we reach a known JTA type.
        switch(fromClass.getName()) {
            // Of course, we place some known elephants in Cairo.
            case "java.lang.Object":
            case "java.lang.Throwable":
            case "java.lang.Exception":
            case "java.lang.RuntimeException":
            case "java.lang.Error":
            case "java.io.IOException":
            case "java.rmi.RemoteException":
                return new UnknownException(rex);
            case "javax.transaction.InvalidTransactionException":
                return new INVALID_TRANSACTION(rex.getMessage());
            case "javax.transaction.TransactionRolledbackException":
                return new TRANSACTION_ROLLEDBACK(rex.getMessage());
            case "javax.transaction.TransactionRequiredException":
                return new TRANSACTION_REQUIRED(rex.getMessage());
        }
        return createSystemException(rex, fromClass.getSuperclass());
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
                throw (org.omg.CORBA.MARSHAL) new org.omg.CORBA.MARSHAL(
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
                    throw (MARSHAL) new MARSHAL("cannot convert Remote to Object").initCause(ex);
                }
            }

        } else {
            throw new MARSHAL(
                    "object is neither Remote nor org.omg.CORBA.Object: "
                            + obj.getClass().getName());
        }

        out.write_Object(objref);
    }

    public void writeAbstractObject(org.omg.CORBA.portable.OutputStream out, Object obj) {
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
                    throw (MARSHAL) new MARSHAL("unable to export object").initCause(ex);
                }
            }
            obj = objref;
        }

        org.omg.CORBA_2_3.portable.OutputStream out_ = (org.omg.CORBA_2_3.portable.OutputStream) out;

        logger.finer("writeAbstractObject.2 " + " out=" + out);

        out_.write_abstract_interface(obj);
    }

    @SuppressWarnings("unchecked")
    protected java.util.Map<Remote, Tie> tie_map() {
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

        return tie_map().get(obj);
    }

    public ValueHandler createValueHandler() {
        return ValueHandlerImpl.get();
    }

    public String getCodebase(@SuppressWarnings("rawtypes") Class clz) {
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
        if (theLoader == BEST_GUESS_AT_EXTENSION_CLASS_LOADER)
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

        return AccessController.doPrivileged(new GetSystemPropertyAction("java.rmi.server.codebase"));
    }

    static class SecMan extends java.rmi.RMISecurityManager {
        @SuppressWarnings("rawtypes")
        public Class[] getClassContext() {
            return super.getClassContext();
        }
    }

    private static SecMan getSecMan() {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<SecMan>() {
                public SecMan run() {
                    return new SecMan();
                }
            });
        } catch (PrivilegedActionException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("rawtypes")
    public Class loadClass(String name, String codebase, ClassLoader loader) throws ClassNotFoundException {
        if (CLASS_LOG.isLoggable(FINEST)) CLASS_LOG.finer(String.format("loadClass(\"%s\", \"%s\", %s)", name, codebase, loader));
        return Arrays.stream(ClassLoadStrategy.values())
                .sequential()
                .map(strategy -> strategy.getAction(loader))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(action -> action.tryToLoad(name))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new ClassNotFoundException(name));
    }

    @FunctionalInterface
    interface ClassLoadAction {
        Class<?> loadClass(String name) throws ClassNotFoundException;
        default Class<?> tryToLoad(String name) {
            try {
                Class<?> clazz = loadClass(name);
                if (clazz != null) CLASS_LOG.fine(() -> "found " + name);
                return clazz;
            } catch (ClassNotFoundException ignored) {
                return null;
            }
        }
    }

    enum ClassLoadStrategy {
        PROVIDER_LOADER(l -> Optional.of(n -> ProviderLocator.loadClass(n, null, l))),
        STACK_LOADER(l -> Optional.ofNullable(getStackLoader()).map(sl -> sl::loadClass)),
        THIS_LOADER(l -> Optional.ofNullable(l).map(gl -> gl::loadClass)),
        /*
          Deliberately removed two risky steps from the original algorithm that loaded code remotely:
          1. Try to load the class from the provided codebase (if present) using a URLClassLoader
          2. Try to load the class from the java.rmi.server.codebase system property value (if present) using the RMIClassLoader
         */
        GIVEN_LOADER(l -> Optional.ofNullable(l).map(gl -> gl::loadClass)),
        CONTEXT_LOADER(l -> Optional.ofNullable(null == l ? getContextClassLoader() : null).map(ccl -> ccl::loadClass));
        private final Function<ClassLoader, Optional<ClassLoadAction>> fun;
        ClassLoadStrategy(Function<ClassLoader, Optional<ClassLoadAction>> fun) { this.fun = fun; }
        final Optional<ClassLoadAction> getAction(ClassLoader givenLoader) {
            Optional<ClassLoadAction> optionalAction = fun.apply(givenLoader);
            CLASS_LOG.finest(() -> optionalAction.map(a -> "searching " + name() + "...").orElse("skipping " + name()));
            return optionalAction;
        }
    }

    private static ClassLoader getStackLoader() {
        // walk down the stack looking for the first class loader that is NOT
        //  - the system class loader (null)
        //  - the loader that loaded Util.class
        final ClassLoader thisLoader = UtilImpl.class.getClassLoader();
        CLASS_LOG.finest(() -> "Looking for stack loader other than loader of UtilImpl: " + thisLoader);
        for (Class<?> candidateContextClass : _secman.getClassContext()) {
            final ClassLoader candidateLoader = candidateContextClass.getClassLoader();
            if (candidateLoader == null) {
                CLASS_LOG.finest(() -> "Ignoring system class " + candidateContextClass.getName());
            } else if (thisLoader == candidateLoader) {
                CLASS_LOG.finest(() -> "Ignoring yoko class " + candidateContextClass.getName());
            } else {
                CLASS_LOG.finer(() -> "Using " + candidateContextClass.getName() + "'s loader: " + candidateLoader);
                return candidateLoader;
            }
        }
        return null;
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
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }

    static ClassLoader getClassLoader(final Class<?> clz) {
        if (System.getSecurityManager() == null) {
            return clz.getClassLoader();
        } else {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return clz.getClassLoader();
                }
            });
        }
    }

    static Object copyRMIStub(RMIStub stub) throws RemoteException {
        ClassLoader loader = getContextClassLoader();

        if (getClassLoader(stub._descriptor.type) == loader) {
            return stub;
        }

        RemoteDescriptor desc = stub._descriptor;

        Class<?> targetClass;

        try {
            targetClass = Util.loadClass(desc.type.getName(), stub
                    ._get_codebase(), loader);
        } catch (ClassNotFoundException ex) {
            logger.log(FINER, "copyRMIStub exception (current loader is: " + loader
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
            TypeRepository rep = RMIState.current().repo;
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

            TypeRepository rep = RMIState.current().repo;
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

        java.util.Map<Remote, Tie> tie_map = tie_map();

        if (tie_map == null)
            return;

        Tie tie = tie_map.remove(obj);

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
}

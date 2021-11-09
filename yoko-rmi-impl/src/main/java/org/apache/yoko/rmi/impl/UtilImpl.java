/*
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
import org.apache.yoko.rmispec.util.DelegateType;
import org.apache.yoko.util.Exceptions;
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
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.UnknownException;
import org.omg.stub.java.rmi._Remote_Stub;

import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.UtilDelegate;
import javax.rmi.CORBA.ValueHandler;
import javax.rmi.PortableRemoteObject;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.security.AccessController.doPrivileged;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static org.apache.yoko.logging.VerboseLogging.CLASS_LOG;
import static org.apache.yoko.util.Predicates.not;
import static org.apache.yoko.util.PrivilegedActions.GET_CONTEXT_CLASS_LOADER;
import static org.apache.yoko.util.PrivilegedActions.action;

public class UtilImpl implements UtilDelegate {
    private static final Logger logger = Logger.getLogger(UtilImpl.class.getName());

    private static final Supplier<Stream<Class<?>>> STACK_CONTEXT_SUPPLIER = doPrivileged(action(StackContextSupplier::new));

    /**
     * Translate a CORBA SystemException to the corresponding RemoteException
     */
    public RemoteException mapSystemException(final SystemException ex) {
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

        StringBuilder buf = new StringBuilder("CORBA ");

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

    private enum TransactionExceptions {
        REQUIRED("jakarta.transaction.TransactionRequiredException", "javax.transaction.TransactionRequiredException"),
        ROLLED_BACK("jakarta.transaction.TransactionRolledbackException", "javax.transaction.TransactionRolledbackException"),
        INVALID("jakarta.transaction.InvalidTransactionException", "javax.transaction.InvalidTransactionException");

        final List<String> remoteExceptionClassNames;

        TransactionExceptions(String...classNames) {
            this.remoteExceptionClassNames = Arrays.asList(classNames);
        }

        RemoteException create(String message) {
            return remoteExceptionClassNames
                    .stream()
                    .map(this::loadClass)
                    .filter(Objects::nonNull)
                    .map(this::getSingleStringConstructor)
                    .filter(Objects::nonNull)
                    .map(cons -> invokeConstructor(cons, message))
                    .filter(Objects::nonNull)
                    .map(RemoteException.class::cast)
                    .findFirst()
                    .orElseGet(() -> new RemoteException(message));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        Class<? extends RemoteException> loadClass(String name) {
            try {
                Class clazz = Util.loadClass(name, null, null);
                if (RemoteException.class.isAssignableFrom(clazz)) {
                    return (Class<? extends RemoteException>)clazz;
                }
            } catch (ClassNotFoundException ignored) {}
            return null;
        }

        <T> Constructor<T> getSingleStringConstructor(Class<T> clazz) {
            try {
                return clazz.getConstructor(String.class);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }

        <T> T invokeConstructor(Constructor<T> ctor, String arg) {
            try {
                return ctor.newInstance(arg);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                return null;
            }
        }
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
            result = TransactionExceptions.REQUIRED.create(s);
        } catch (TRANSACTION_ROLLEDBACK e) {
            result = TransactionExceptions.ROLLED_BACK.create(s);
        } catch (INVALID_TRANSACTION e) {
            result = TransactionExceptions.INVALID.create(s);
        } catch (SystemException e) { // catch-all
            result = new RemoteException(s);
        }
        result.detail = sysEx;
        return result;
    }

    static SystemException mapRemoteException(RemoteException rex) {
        if (rex.detail instanceof SystemException)
            return (SystemException) rex.detail;

        if (rex.detail instanceof RemoteException)
            rex = (RemoteException) rex.detail;

        SystemException sysEx;

        if (rex instanceof NoSuchObjectException) {
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
            case "jakarta.transaction.InvalidTransactionException":
                return new INVALID_TRANSACTION(rex.getMessage());
            case "javax.transaction.TransactionRolledbackException":
            case "jakarta.transaction.TransactionRolledbackException":
                return new TRANSACTION_ROLLEDBACK(rex.getMessage());
            case "javax.transaction.TransactionRequiredException":
            case "jakarta.transaction.TransactionRequiredException":
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
    public void writeAny(OutputStream out, Object obj)
            throws SystemException {
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
            } catch (NoSuchObjectException ex) {
                throw (org.omg.CORBA.MARSHAL) new org.omg.CORBA.MARSHAL(
                        "object not exported " + ro).initCause(ex);
            }

            any.insert_Object(corba_obj);

        } else if (obj instanceof Serializable) {
            any.insert_Value((Serializable) obj);
        } else {
            throw new MARSHAL("cannot write as " + obj.getClass() + " to an Any");
        }

        out.write_any(any);
    }

    public Object readAny(org.omg.CORBA.portable.InputStream in)
            throws SystemException {
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
                return any.extract_Object();

            case TCKind._tk_any:
                return any.extract_any();

            default:
                String id = "<unknown>";
                try {
                    id = typecode.id();
                } catch (org.omg.CORBA.TypeCodePackage.BadKind ignored) {
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
    public void writeRemoteObject(OutputStream out, Object obj) throws SystemException {
        org.omg.CORBA.Object objref = null;

        if (obj == null) {
            out.write_Object(null);
            return;

        } else if (obj instanceof org.omg.CORBA.Object) {
            objref = (org.omg.CORBA.Object) obj;

        } else if (obj instanceof Remote) {
            try {
                objref = (javax.rmi.CORBA.Stub) PortableRemoteObject.toStub((java.rmi.Remote) obj);

            } catch (NoSuchObjectException ignored) {}

            if (null == objref) {
                try {
                    PortableRemoteObject.exportObject((java.rmi.Remote) obj);
                    objref = (javax.rmi.CORBA.Stub) PortableRemoteObject.toStub((java.rmi.Remote) obj);
                } catch (java.rmi.RemoteException ex) {
                    throw (MARSHAL) new MARSHAL("cannot convert Remote to Object").initCause(ex);
                }
            }
        } else {
            throw new MARSHAL("object is neither Remote nor org.omg.CORBA.Object: " + obj.getClass().getName());
        }

        out.write_Object(objref);
    }

    public void writeAbstractObject(OutputStream out, Object obj) {
        logger.finer("writeAbstractObject.1 " + " out=" + out);

        org.omg.CORBA_2_3.portable.OutputStream out_ = (org.omg.CORBA_2_3.portable.OutputStream) out;

        logger.finer("writeAbstractObject.2 " + " out=" + out);

        out_.write_abstract_interface(convertObject(obj));
    }

    private Object convertObject(Object obj) {
        if (obj instanceof org.omg.CORBA.Object) return obj;
        if (obj instanceof Serializable) return obj;
        if (!(obj instanceof Remote)) return obj;

        Remote remote = (Remote)obj;
        try {
            Object objectRef = PortableRemoteObject.toStub(remote);
            if (null != objectRef) return objectRef;
        } catch (NoSuchObjectException ignored) {}

        try {
            PortableRemoteObject.exportObject(remote);
            return PortableRemoteObject.toStub(remote);
        } catch (RemoteException ex) {
            throw Exceptions.as(MARSHAL::new, ex,"unable to export object");
        }
    }

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

    @SuppressWarnings("rawtypes")
    public String getCodebase(Class clz) {
        // Specifically disable the ability to specify a remote codebase using "java.rmi.server.codebase"
        // system property, because it's an *EVIL* vector for security holes
        return null;
    }

    @SuppressWarnings("deprecation")
    private static class StackContextSupplier extends java.rmi.RMISecurityManager implements Supplier<Stream<Class<?>>> {
        @SuppressWarnings("RedundantCast")
        @Override
        public Stream<Class<?>> get() { return Arrays.stream((Class<?>[])getClassContext()).sequential().skip(1); }
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
        /** Give providers a change to supply classes */
        PROVIDER_LOADER(l -> Optional.of(n -> ProviderLocator.loadClass(n, null, l))),
        /** Ignoring Yoko, API, delegate, and provider classes, try the first non-null loader on the stack */
        STACK_LOADER(l -> Optional.ofNullable(getStackLoader()).map(sl -> sl::loadClass)),
        THIS_LOADER(l -> Optional.ofNullable(l).map(gl -> gl::loadClass)),
        /*
          Deliberately removed two risky steps from the original algorithm that loaded code remotely:
          1. Try to load the class from the provided codebase (if present) using a URLClassLoader
          2. Try to load the class from the java.rmi.server.codebase system property value (if present) using the RMIClassLoader
         */
        GIVEN_LOADER(l -> Optional.ofNullable(l).map(gl -> gl::loadClass)),
        CONTEXT_LOADER(l -> Optional.ofNullable(null == l ? doPrivileged(GET_CONTEXT_CLASS_LOADER) : null).map(ccl -> ccl::loadClass));
        private final Function<ClassLoader, Optional<ClassLoadAction>> fun;
        ClassLoadStrategy(Function<ClassLoader, Optional<ClassLoadAction>> fun) { this.fun = fun; }
        final Optional<ClassLoadAction> getAction(ClassLoader givenLoader) {
            Optional<ClassLoadAction> optionalAction = fun.apply(givenLoader);
            CLASS_LOG.finest(() -> optionalAction.map(a -> "searching " + name() + "...").orElse("skipping " + name()));
            return optionalAction;
        }
    }

    private static boolean isYokoImplClass(Class<?> c) { return c.getName().startsWith("org.apache.yoko."); }
    private static boolean isJavaxRmiClass(Class<?> c) { return c.getName().startsWith("javax.rmi."); }

    private static boolean isOmgClass(Class<?> c) {
        if (_Remote_Stub.class == c) return true;
        final String name = c.getName();
        return (name.startsWith("org.omg.") && !name.startsWith("org.omg.stub."));
    }

    private static ClassLoader getStackLoader() {
        // walk down the stack looking for the first class loader that is NOT
        //  - the system class loader (null)
        //  - the loader(s) for Yoko implementation classes
        //  - the loader(s) for OMG classes
        //  - the loader(s) for javax.rmi.* classes
        //  - the loader(s) that loaded any ProviderRegistry-provided classes or services
        //  - the loader(s) that loaded any delegate class
        CLASS_LOG.finest(() -> "Looking for stack loader other than those used by Yoko");
        return STACK_CONTEXT_SUPPLIER.get()
                .peek((c) -> CLASS_LOG.finest(() -> "Considering class: " + c.getName()))
                .filter(not(UtilImpl::isYokoImplClass))
                .filter(not(UtilImpl::isOmgClass))
                .filter(not(UtilImpl::isJavaxRmiClass))
                .peek((c) -> CLASS_LOG.finest(() -> "Considering classloader for class: " + c.getName()))
                .map((c) -> doPrivileged(action(c::getClassLoader)))
                .filter(Objects::nonNull)
                .filter(not(ProviderLocator::isServiceClassLoader))
                .filter(not(DelegateType::isDelegateClassLoader))
                .peek((l) -> CLASS_LOG.finer(() -> "Using loader " + l))
                .findFirst()
                .orElse(null);
    }

    public boolean isLocal(Stub stub) throws RemoteException {
        try {
            return (stub instanceof RMIStub) || stub._is_local();
        } catch (SystemException ex) {
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

        } else if (ex instanceof SystemException) {
            return mapSystemException((SystemException) ex);

        } else if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;

        } else {
            return new java.rmi.RemoteException(ex.getMessage(), ex);
        }
    }

    static Object copyRMIStub(RMIStub stub) throws RemoteException {
        ClassLoader loader = doPrivileged(GET_CONTEXT_CLASS_LOADER);

        if (doPrivileged(action(stub._descriptor.type::getClassLoader)) == loader) {
            return stub;
        }

        RemoteDescriptor desc = stub._descriptor;

        Class<?> targetClass;

        try {
            targetClass = Util.loadClass(desc.type.getName(), stub._get_codebase(), loader);
        } catch (ClassNotFoundException ex) {
            logger.log(FINER, "copyRMIStub exception (current loader is: " + loader + ") " + ex.getMessage(), ex);
            throw new RemoteException("Class not found", ex);
        }

        return PortableRemoteObjectImpl.narrow1(RMIState.current(), stub, targetClass);
    }

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
        } catch (SystemException ex) {
            throw mapSystemException(ex);
        }

    }

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

        } catch (SystemException ex) {
            throw mapSystemException(ex);
        }
    }

    public void unexportObject(Remote obj)
            throws NoSuchObjectException {
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

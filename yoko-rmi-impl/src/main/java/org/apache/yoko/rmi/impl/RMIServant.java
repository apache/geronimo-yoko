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

import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.security.AccessController.doPrivileged;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static org.apache.yoko.logging.VerboseLogging.REQ_IN_LOG;

public class RMIServant extends org.omg.PortableServer.Servant implements javax.rmi.CORBA.Tie, InvocationHandler {
    RMIState _state;
    RemoteDescriptor _descriptor;
    byte[] _id;
    private InvokeHandler proxyInvokeHandler;

    Class getJavaClass() {
        return _descriptor.type;
    }

    RMIState getRMIState() {
        return _state;
    }

    public RMIServant(RMIState state) {
        _state = state;
    }

    private java.rmi.Remote _target = null;

    /** this implements the sole missing method in javax.rmi.CORBA.Tie */
    public String[] _all_interfaces(final org.omg.PortableServer.POA poa,
            final byte[] objectId) {
        return _descriptor.all_interfaces();
    }

    static String debug_name(Method m) {
        return m.getDeclaringClass().getName() + "." + m.getName();
    }

    /**
      * this implements the sole missing method in
      * org.omg.CORBA.portable.InvokeHandler
     */
    public OutputStream _invoke(String opName, InputStream _input, ResponseHandler response) throws SystemException {
        return this.proxyInvokeHandler._invoke(opName, _input, response);
    }

    private OutputStream _invoke0(String opName, InputStream _input, ResponseHandler response) throws SystemException {

        MethodDescriptor method = _descriptor.getMethod(opName);

        if (method == null) {
            _descriptor.debugMethodMap();
            throw new org.omg.CORBA.BAD_OPERATION(opName);
        }

        java.lang.reflect.Method m = method.getReflectedMethod();

        if (REQ_IN_LOG.isLoggable(FINEST)) REQ_IN_LOG.finest(debug_name(m) + ": invoking on " + _id);

        try {
            Object[] args = method.readArguments(_input);

            Object result = invoke_method(m, args);

            OutputStream _out = response.createReply();

            method.writeResult(_out, result);
            if (REQ_IN_LOG.isLoggable(FINEST)) REQ_IN_LOG.finest(debug_name(m) + ": returning normally");

            return _out;
        } catch (SystemException ex) {
            REQ_IN_LOG.throwing(RMIServant.class.getName(), "_invoke", ex);
            REQ_IN_LOG.warning(ex.getMessage());
            throw ex;

        } catch (java.lang.reflect.UndeclaredThrowableException ex) {
            REQ_IN_LOG.throwing(RMIServant.class.getName(), "_invoke", ex.getUndeclaredThrowable());
            throw new org.omg.CORBA.portable.UnknownException(ex
                    .getUndeclaredThrowable());
        } catch (RuntimeException ex) {
            if (REQ_IN_LOG.isLoggable(FINER)) REQ_IN_LOG.log(FINER, debug_name(m) + ": RuntimeException " + ex.getMessage(), ex);
            return method.writeException(response, ex);
        } catch (java.rmi.RemoteException ex) {
            if (REQ_IN_LOG.isLoggable(FINER)) REQ_IN_LOG.log(FINER, debug_name(m) + ": RemoteException " + ex.getMessage(), ex);
            // return method.writeException (response, ex);
            throw UtilImpl.mapRemoteException(ex);
        } catch (Throwable ex) {
            if (REQ_IN_LOG.isLoggable(FINER)) REQ_IN_LOG.log(FINER, debug_name(m) + ": Throwable " + ex.getMessage(), ex);
            return method.writeException(response, ex);
        } finally {
            // PortableRemoteObjectExt.popState();

        }
    }

    /* package */
    Object invoke_method(java.lang.reflect.Method m, Object[] args) throws Throwable {

        if (_target != null) {
            try {
                if (REQ_IN_LOG.isLoggable(FINE)) REQ_IN_LOG.fine("invoking method " + m + " on target " + _target);
                if (REQ_IN_LOG.isLoggable(FINER)) REQ_IN_LOG.finer(" with args: " + Arrays.asList(args));
                if (REQ_IN_LOG.isLoggable(FINEST)) REQ_IN_LOG.finest(" of arg types: " + Stream.of(args).map(o -> o == null ? null : o.getClass()).collect(Collectors.toList()));
                return m.invoke(_target, args);
            } catch (java.lang.reflect.InvocationTargetException ex) {
                REQ_IN_LOG.log(FINER, "Error invoking local method", ex.getCause());
                throw ex.getTargetException();
            }
        } else {
            throw new OBJECT_NOT_EXIST();
        }
    }

    public org.omg.CORBA.ORB orb() {
        return _orb();
    }

    public void orb(org.omg.CORBA.ORB orb) {
        try {
            POA _poa = POAHelper.narrow(orb
                    .resolve_initial_references("RootPOA"));

            _poa.activate_object(this);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            throw new RuntimeException("ORB must have POA support", ex);
        } catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
            throw new RuntimeException("wrong policy: " + ex.getMessage(), ex);
        } catch (org.omg.PortableServer.POAPackage.ServantAlreadyActive ex) {
            throw new RuntimeException("already active: " + ex.getMessage(), ex);
        }
    }

    public void deactivate() {
        if (_get_delegate() == null)
            throw new RuntimeException("object not active");

        try {
            org.omg.PortableServer.POA poa = _state.getPOA();
            byte[] id = poa.servant_to_id(this);
            poa.deactivate_object(id);
            _set_delegate(null);
        } catch (Throwable ex) {
            REQ_IN_LOG.throwing("", "deactivate", ex);
            throw new RuntimeException("cannot deactivate: " + ex.getMessage(), ex);
        }
    }

    public java.rmi.Remote getTarget() {
        return _target;
    }

    public synchronized void setTarget(java.rmi.Remote target) {
        if (target == null) {
            throw new IllegalArgumentException();
        }

        _descriptor = _state.repo.getRemoteInterface(target.getClass()).getRemoteInterface();

        if (_descriptor == null) {
            throw new RuntimeException("remote classes not supported");
        }

        _target = target;
        ClassLoader targetLoader = doPrivileged((PrivilegedAction<ClassLoader>) () -> _target.getClass().getClassLoader());
        if (targetLoader != null && targetLoader != this.getClass().getClassLoader())
            proxyInvokeHandler = (InvokeHandler) newProxyInstance(targetLoader, new Class<?>[]{InvokeHandler.class}, this);
        else
            proxyInvokeHandler = this::_invoke0;
    }

    Delegate getDelegate() {
        return _state.createDelegate(this);
    }

    public org.omg.CORBA.Object thisObject() {
        return _this_object();
    }

    private static final Method INVOKE_HANDLER_METHOD = InvokeHandler.class.getMethods()[0];

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        assert INVOKE_HANDLER_METHOD.equals(method);
        return this._invoke0((String) args[0], (InputStream) args[1], (ResponseHandler) args[2]);
    }
}

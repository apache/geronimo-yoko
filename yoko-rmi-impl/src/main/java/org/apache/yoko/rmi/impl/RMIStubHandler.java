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

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA.portable.UnknownException;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static javax.rmi.CORBA.Util.mapSystemException;
import static org.apache.yoko.util.Streams.concatStreams;

/**
 * This class is the InvocationHandler for instances of POAStub. When a client
 * calls a remote method, this is translated to a call to the invoke() method in
 * this class.
 */
public class RMIStubHandler implements StubHandler, Serializable {
    static final Logger logger = Logger.getLogger(RMIStubHandler.class.getName());

    protected RMIStubHandler() {

    }

    static final RMIStubHandler instance = new RMIStubHandler();

    public Object stubWriteReplace(RMIStub stub) {
        return new RMIPersistentStub(stub, stub._descriptor.type);
    }


    public Object invoke(RMIStub stub, MethodDescriptor method, Object[] args) throws Throwable {
        // special-case for writeReplace
        if (null == method) return stubWriteReplace(stub);

        final String method_name = method.getIDLName();

        logger.finer("invoking " + method_name);

        return stub._is_local() ? invokeLocal(stub, method, args, method_name) : invokeRemote(stub, method, args, method_name);
    }

    private Object invokeRemote(RMIStub stub, MethodDescriptor method, Object[] args, String method_name) throws Throwable {
        for (;;) {
            InputStream in = null;
            try {
                final OutputStream out = stub._request(method_name, method.responseExpected());
                method.writeArguments(out, args);
                in = stub._invoke(out);
                return method.readResult(in);
            } catch (RemarshalException retry) { // go round the loop
            } catch (ApplicationException ex) {
                try {
                    method.readException(ex.getInputStream());
                } catch (Throwable exx) {
                    logger.log(Level.FINE, "rmi1::" + method_name + " " + exx.getMessage(), exx);
                    throw addLocalTrace(method, exx);
                }
            } catch (UnknownException ex) {
                logger.log(Level.FINER, "rmi2::" + method_name + " " + ex.getMessage(), ex);
                logger.log(Level.FINER, "rmi2::" + method_name + " " + ex.originalEx.getMessage(), ex.originalEx);

                throw addLocalTrace(method, ex.originalEx);
            } catch (SystemException ex) {
                RemoteException exx = mapSystemException(ex);
                logger.log(Level.FINER, "rmi3::" + method_name + " " + exx.getMessage(), exx);
                throw exx;
            } catch (Throwable ex) {
                logger.log(Level.FINER, "rmi4::" + method_name + " " + ex.getMessage(), ex);
                throw ex;
            } finally {
                stub._releaseReply(in);
            }
        }
    }

    private Object invokeLocal(RMIStub stub, MethodDescriptor method, Object[] args, String method_name) throws Throwable {
        final ServantObject so = stub._servant_preinvoke(method_name, RMIServant.class);

        if (!(so.servant instanceof RMIServant)) return invokeRemote(stub, method, args, method_name);

        final RMIServant servant = (RMIServant)so.servant;
        final RMIState target_state = servant.getRMIState();
        final ORB orb = target_state.getORB();
        final RMIState currentState = RMIState.current();
        final boolean same_state = (currentState == target_state);

        try {
            final Method m = method.getReflectedMethod();
            final Object return_value = servant.invoke_method(m, method.copyArguments(args, same_state, orb));
            return method.copyResult(return_value, same_state, orb);
        } catch (SystemException ex) {
            throw mapSystemException(ex);
        } finally {
            stub._servant_postinvoke(so);
        }
    }

    private static Throwable addLocalTrace(MethodDescriptor desc, Throwable ex) {
        final Method m = desc.getReflectedMethod();
        final StackTraceElement separatorElement =
                new StackTraceElement(m.getDeclaringClass().getName(), m.getName(), "--- RMI/IIOP INVOCATION ---", -2000);
        final Throwable lex = new Throwable("Client-Side RMI Trace");
        final StackTraceElement[] combinedTrace =
                concatStreams(Arrays.stream(ex.getStackTrace()),
                        Stream.of(separatorElement),
                        Arrays.stream(lex.getStackTrace()).sequential().skip(1)
                ).toArray(StackTraceElement[]::new);
        ex.setStackTrace(combinedTrace);
        return ex;
    }
}

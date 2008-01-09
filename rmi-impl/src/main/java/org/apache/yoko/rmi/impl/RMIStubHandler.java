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

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.omg.CORBA.ORB;

/**
 * This class is the InvocationHandler for instances of POAStub. When a client
 * calls a remote method, this is translated to a call to the invoke() method in
 * this class.
 */
public class RMIStubHandler implements StubHandler, java.io.Serializable {
    static final Logger logger = Logger.getLogger(RMIStubHandler.class
        .getName());

    protected RMIStubHandler() {

    }

    static final RMIStubHandler instance = new RMIStubHandler();

    public Object stubWriteReplace(RMIStub stub) {
    Class type = stub._descriptor.getJavaClass();
    return new org.apache.yoko.rmi.impl.RMIPersistentStub(stub, type);
    }
    
    public Object invoke(RMIStub stub, MethodDescriptor method, Object[] args)
    throws Throwable {

        // special-case for writeReplace
        if (method == null) {
            return stubWriteReplace(stub);
        }

        final String method_name = method.getIDLName();
        boolean stream_arguments = false;

        logger.finer("invoking " + method_name);

        while (true) {
            boolean is_local = stub._is_local();

            if (!is_local || stream_arguments) {

                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;

                try {
                    out = stub._request(method_name, method.responseExpected());

                    // write arguments
                    method.writeArguments(out, args);

                    // invoke method
                    in = stub._invoke(out);

                    Object result = method.readResult(in);

                    return result;

                } catch (org.omg.CORBA.portable.ApplicationException ex) {
                    try {
                        method.readException(ex.getInputStream());

                    } catch (Throwable exx) {
                        logger.log(Level.FINE, "rmi1::" + method_name + " " + exx.getMessage(), exx);

                        addLocalTrace(method, exx);

                        throw exx;
                    }

                } catch (org.omg.CORBA.portable.UnknownException ex) {
                    logger.log(Level.FINER, "rmi2::" + method_name + " " + ex.getMessage(), ex);
                    logger.log(Level.FINER, "rmi2::" + method_name + " " + 
                               ex.originalEx.getMessage(), ex.originalEx);

                    addLocalTrace(method, ex.originalEx);

                    throw ex.originalEx;

                } catch (org.omg.CORBA.portable.RemarshalException _exception) {
                    continue;

                } catch (org.omg.CORBA.SystemException ex) {
                    java.rmi.RemoteException exx = javax.rmi.CORBA.Util
                                                   .mapSystemException(ex);

                    logger.log(Level.FINER, "rmi3::" + method_name + " " + exx.getMessage(), exx);

                    throw exx;

                } catch (Throwable ex) {
                    logger.log(Level.FINER, "rmi4::" + method_name + " " + ex.getMessage(), ex);

                    throw ex;

                } finally {
                    stub._releaseReply(in);
                }

            } else {
                org.omg.CORBA.portable.ServantObject so;

                so = stub._servant_preinvoke(method_name, RMIServant.class);

                RMIServant servant;

                try {
                    servant = (RMIServant) so.servant;
                } catch (ClassCastException ex) {
                    stream_arguments = true;
                    continue;
                } catch (NullPointerException ex) {
                    stream_arguments = true;
                    continue;
                }

                final RMIState target_state = servant.getRMIState();
                final ORB orb = target_state.getORB();

                Object return_value = null;
                boolean same_state;

                RMIState currentState = RMIState.current();
                same_state = (currentState == target_state);

                Object[] copied_args = method.copyArguments(args,
                                                            same_state, orb);

                try {
                    java.lang.reflect.Method m = method
                                                 .getReflectedMethod();

                    return_value = servant
                                   .invoke_method(m, copied_args);
                } catch (org.omg.CORBA.SystemException ex) {
                    throw javax.rmi.CORBA.Util.mapSystemException(ex);

                } finally {
                    stub._servant_postinvoke(so);
                }

                return method.copyResult(return_value, same_state, orb);
            }
        }
    }

    private static Throwable addLocalTrace(MethodDescriptor desc, Throwable ex) {
        try {
            throw new Throwable("Client-Side RMI Trace");

        } catch (Throwable lex) {

            StackTraceElement[] remoteTrace = ex.getStackTrace();
            StackTraceElement[] localTrace = lex.getStackTrace();

            StackTraceElement[] fullTrace = new StackTraceElement[localTrace.length
                                                                  + remoteTrace.length];

            for (int i = 0; i < remoteTrace.length; i++) {
                fullTrace[i] = remoteTrace[i];
            }

            java.lang.reflect.Method m = desc.getReflectedMethod();
            resetTraceInfo(m.getDeclaringClass().getName(), m.getName(),
                           localTrace[0]);

            for (int i = 0; i < localTrace.length; i++) {
                fullTrace[remoteTrace.length + i] = localTrace[i];
            }

            ex.setStackTrace(fullTrace);
            return ex;
        }
    }

    static Field classNameField;

    static Field methodNameField;

    static Field fileNameField;

    static Field lineNumberField;

    static {
    AccessController.doPrivileged(new PrivilegedAction() {
        /**
                 * @see java.security.PrivilegedAction#run()
                 */
        public Object run() {
        try {
            classNameField = StackTraceElement.class
                .getDeclaredField("declaringClass");
            classNameField.setAccessible(true);

            methodNameField = StackTraceElement.class
                .getDeclaredField("methodName");
            methodNameField.setAccessible(true);

            fileNameField = StackTraceElement.class
                .getDeclaredField("fileName");
            fileNameField.setAccessible(true);

            lineNumberField = StackTraceElement.class
                .getDeclaredField("lineNumber");
            lineNumberField.setAccessible(true);

        } catch (Exception ex) {
            // ignore
        }
        return null;
        }

    });
    }

    /**
         * Method resetTraceInfo.
         * 
         * @param stackTraceElement
         */
    private static void resetTraceInfo(String className, String methodName,
                                       StackTraceElement ste) {
        try {
            classNameField.set(ste, className);
            methodNameField.set(ste, methodName);
            fileNameField.set(ste, "--- RMI/IIOP INVOCATION ---");
            lineNumberField.set(ste, new Integer(-2000));
        } catch (IllegalAccessException e) {
        } catch (NullPointerException e) {
        }
    }

}

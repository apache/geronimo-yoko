/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.orb.CORBA;

//
// ObjectImpl is the base class for proprietary stubs with full
// interceptor support
//
abstract public class ObjectImpl extends org.omg.CORBA_2_4.portable.ObjectImpl {
    public org.apache.yoko.orb.OB.DowncallStub _OB_getDowncallStub()
            throws org.apache.yoko.orb.OB.LocationForward,
            org.apache.yoko.orb.OB.FailureException {
        Delegate delegate = (Delegate) _get_delegate();
        return delegate._OB_getDowncallStub(this);
    }

    public void _OB_handleException(Exception ex, RetryInfo info) {
        Delegate delegate = (Delegate) _get_delegate();
        delegate._OB_handleException(this, ex, info, false);
    }

    public org.apache.yoko.orb.OB.GIOPOutgoingMessage _OB_ami_router_preMarshal(
            String operation, boolean responseExpected, OutputStreamHolder out,
            org.apache.yoko.orb.OCI.ProfileInfoHolder info) {
        Delegate delegate = (Delegate) _get_delegate();

        try {
            org.apache.yoko.orb.OB.DowncallStub downStub = delegate
                    ._OB_getDowncallStub(this);
            org.apache.yoko.orb.OB.GIOPOutgoingMessage message = downStub
                    .AMIRouterPreMarshal(operation, responseExpected, out, info);

            return message;
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
        }

        return null;
    }

    public void _OB_ami_router_postMarshal(
            org.apache.yoko.orb.OB.GIOPOutgoingMessage message,
            OutputStreamHolder out) {
        Delegate delegate = (Delegate) _get_delegate();

        try {
            org.apache.yoko.orb.OB.DowncallStub downStub = delegate
                    ._OB_getDowncallStub(this);
            downStub.AMIRouterPostMarshal(message, out);
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
        }
    }

    // public org.apache.yoko.orb.CORBA.OutputStream
    public org.apache.yoko.orb.OB.CodeConverters _OB_setup_ami_poll_request(
            org.omg.IOP.ServiceContextListHolder sclHolder,
            org.apache.yoko.orb.CORBA.OutputStreamHolder out) {
        Delegate delegate = (Delegate) _get_delegate();

        try {
            org.apache.yoko.orb.OB.DowncallStub downStub = delegate
                    ._OB_getDowncallStub(this);
            org.apache.yoko.orb.OB.CodeConverters cc = downStub
                    .setupPollingRequest(sclHolder, out);

            return cc;
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
        }

        return null;
    }

    public org.omg.CORBA.Object _OB_get_ami_poll_target() {
        //
        // This is needed since we don't have access to the IOR information
        // from the DowncallStub like we do in C++ (MarshalStub)
        //
        Delegate delegate = (Delegate) _get_delegate();
        try {
            org.apache.yoko.orb.OB.DowncallStub downStub = delegate
                    ._OB_getDowncallStub(this);
            return downStub.getAMIPollTarget();
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
        }

        return null;
    }

    public org.apache.yoko.orb.OB.ORBInstance _OB_get_ami_poll_ORBInstance() {
        //
        // We need to be able to retrieve the ORB instance to use with a
        // persistent poller in case we want to use pollable sets
        //
        Delegate delegate = (Delegate) _get_delegate();
        try {
            org.apache.yoko.orb.OB.DowncallStub downStub = delegate
                    ._OB_getDowncallStub(this);
            return downStub._OB_getORBInstance();
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
        }

        return null;
    }

    public org.omg.MessageRouting.PersistentRequest _OB_ami_poll_request(
            org.omg.CORBA.portable.OutputStream out, String operation,
            org.omg.IOP.ServiceContext[] scl)
            throws org.omg.CORBA.portable.RemarshalException {
        Delegate delegate = (Delegate) _get_delegate();

        try {
            org.apache.yoko.orb.OB.DowncallStub downStub = delegate
                    ._OB_getDowncallStub(this);
            org.omg.MessageRouting.PersistentRequest req = downStub
                    .ami_poll_request(out, operation, scl);
            return req;
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
        }

        //
        // Something happened other than what we expected. Remarshal so
        // the stub can try again.
        //
        throw new org.omg.CORBA.portable.RemarshalException();
    }

    public boolean _OB_ami_callback_request(
            org.omg.CORBA.portable.OutputStream out,
            org.omg.Messaging.ReplyHandler reply,
            org.apache.yoko.orb.OCI.ProfileInfo info)
            throws org.omg.CORBA.portable.RemarshalException {
        Delegate delegate = (Delegate) _get_delegate();

        boolean success = false;
        try {
            org.apache.yoko.orb.OB.DowncallStub downStub = delegate
                    ._OB_getDowncallStub(this);
            success = downStub.ami_callback_request(out, reply, info);
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
        } catch (org.apache.yoko.orb.OB.FailureException ex) {
        }

        return success;
    }

    public org.omg.Messaging._ExceptionHolder _OB_repackageException(
            java.lang.Exception ex, boolean system_exception) {
        //
        // Create a stream for writing our exception
        //
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                32);
        org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                buf, null, 258);

        //
        // Put the exception into the output stream
        //
        Class exClass = ex.getClass();
        String className = exClass.getName();
        try {
            //
            // Get the appropriate helper class for the exception and
            // retrieve the write method from the helper
            //

            // get the appropriate class for the loading.
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
                loader = this.getClass().getClassLoader();
            }

            Class c = loader.loadClass(className + "Helper");
            Class[] paramTypes = new Class[2];
            paramTypes[0] = loader.loadClass("org.omg.CORBA.portable.OutputStream");
            paramTypes[1] = loader.loadClass(className);
            java.lang.reflect.Method m = c.getMethod("write", paramTypes);

            //
            // Build up the parameter list
            //
            Object[] parameters = new Object[2];
            parameters[0] = out;
            parameters[1] = ex;

            //
            // No object is needed as the first parameter because the
            // method is a static method
            //
            m.invoke(null, parameters);
        } catch (ClassNotFoundException e) {
            //
            // REVISIT:
            // This just means that we probably caught a non-CORBA
            // exception. For now, just return null.
            //
            return null;
        } catch (NoSuchMethodException e) {
            org.apache.yoko.orb.OB.Assert._OB_assert(e);
        } catch (IllegalAccessException e) {
            org.apache.yoko.orb.OB.Assert._OB_assert(e);
        } catch (IllegalArgumentException e) {
            org.apache.yoko.orb.OB.Assert._OB_assert(e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            org.apache.yoko.orb.OB.Assert._OB_assert(e);
        } catch (SecurityException e) {
            //
            // REVISIT:
            // What to do in this case?
            //
            return null;
        }

        //
        // Now create the exception holder
        //
        org.apache.yoko.orb.OBMessaging.ExceptionHolder_impl exHolder = new org.apache.yoko.orb.OBMessaging.ExceptionHolder_impl(
                false, system_exception);
        out._OB_pos(0);
        exHolder._OB_extractSequence(out._OB_buffer());
        return exHolder;
    }
}

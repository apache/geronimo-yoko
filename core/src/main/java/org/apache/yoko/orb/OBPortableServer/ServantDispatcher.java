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

package org.apache.yoko.orb.OBPortableServer;

//
// There are several ways a request can be dispatched in Java:
//
// 1) Portable stream-based skeleton
// 2) DSI
// 3) Proprietary skeleton
// 
// We cannot simply invoke _OB_dispatch() on the servant, because
// org.omg.PortableServer.Servant is standardized.
//
// To support portable skeletons, this class also implements the
// standard ResponseHandler interface.
//
final class ServantDispatcher implements org.omg.CORBA.portable.ResponseHandler {
    //
    // The Upcall
    //
    protected org.apache.yoko.orb.OB.Upcall upcall_;

    //
    // The servant
    //
    protected org.omg.PortableServer.Servant servant_;

    //
    // Used to bypass a portable skeleton
    //
    private class Abort extends RuntimeException {
    }

    ServantDispatcher(org.apache.yoko.orb.OB.Upcall upcall,
            org.omg.PortableServer.Servant servant) {
        upcall_ = upcall;
        servant_ = servant;
    }

    private boolean dispatchBase()
            throws org.apache.yoko.orb.OB.LocationForward {
        String _ob_op = upcall_.operation();

        //
        // Optimization. All operations that we dispatch start with an '_'
        // character.
        //
        if (_ob_op.charAt(0) != '_')
            return false;

        final String[] _ob_names = { "_interface", "_is_a", "_non_existent" };

        int _ob_left = 0;
        int _ob_right = _ob_names.length;
        int _ob_index = -1;

        while (_ob_left < _ob_right) {
            int _ob_m = (_ob_left + _ob_right) / 2;
            int _ob_res = _ob_names[_ob_m].compareTo(_ob_op);
            if (_ob_res == 0) {
                _ob_index = _ob_m;
                break;
            } else if (_ob_res > 0)
                _ob_right = _ob_m;
            else
                _ob_left = _ob_m + 1;
        }

        switch (_ob_index) {
        case 0: // _interface
        {
            upcall_.preUnmarshal();
            upcall_.postUnmarshal();
            org.omg.CORBA.InterfaceDef def = servant_._get_interface();
            upcall_.postinvoke();
            org.apache.yoko.orb.CORBA.OutputStream out = upcall_.preMarshal();
            try {
                out.write_Object(def);
            } catch (org.omg.CORBA.SystemException ex) {
                upcall_.marshalEx(ex);
            }
            upcall_.postMarshal();
            return true;
        }

        case 1: // _is_a
        {
            org.apache.yoko.orb.CORBA.InputStream in = upcall_.preUnmarshal();
            String id = null;
            try {
                id = in.read_string();
            } catch (org.omg.CORBA.SystemException ex) {
                upcall_.unmarshalEx(ex);
            }
            upcall_.postUnmarshal();
            boolean b = servant_._is_a(id);
            upcall_.postinvoke();
            org.apache.yoko.orb.CORBA.OutputStream out = upcall_.preMarshal();
            try {
                out.write_boolean(b);
            } catch (org.omg.CORBA.SystemException ex) {
                upcall_.marshalEx(ex);
            }
            upcall_.postMarshal();
            return true;
        }

        case 2: // _non_existent
        {
            upcall_.preUnmarshal();
            upcall_.postUnmarshal();
            boolean b = servant_._non_existent();
            upcall_.postinvoke();
            org.apache.yoko.orb.CORBA.OutputStream out = upcall_.preMarshal();
            try {
                out.write_boolean(b);
            } catch (org.omg.CORBA.SystemException ex) {
                upcall_.marshalEx(ex);
            }
            upcall_.postMarshal();
            return true;
        }
        }

        return false;
    }

    void dispatch() throws org.apache.yoko.orb.OB.LocationForward {
        //
        // Handle common operations
        //
        if (dispatchBase())
            return;

        //
        // Case 1: Servant is org.apache.yoko.orb.PortableServer.Servant, i.e.,
        // a proprietary skeleton with full interceptor support
        //
        if (servant_ instanceof org.apache.yoko.orb.PortableServer.Servant) {
            org.apache.yoko.orb.PortableServer.Servant s = (org.apache.yoko.orb.PortableServer.Servant) servant_;
            s._OB_dispatch(upcall_);
        }
        //
        // Case 2: Servant is a org.omg.CORBA.portable.InvokeHandler,
        // i.e., a portable stream-based skeleton. For a normal reply,
        // the skeleton will call back to createReply(). If a user
        // exception occurred, the skeleton will call back to
        // createExceptionReply(). SystemExceptions are raised
        // directly.
        //
        else if (servant_ instanceof org.omg.CORBA.portable.InvokeHandler) {
            try {
                org.omg.CORBA.portable.InvokeHandler inv = (org.omg.CORBA.portable.InvokeHandler) servant_;

                //
                // Prepare to unmarshal
                //
                org.omg.CORBA.portable.InputStream in = upcall_.preUnmarshal();

                //
                // Call postUnmarshal now. There may be interceptors that
                // need to be called before dispatching to the servant.
                // When using a portable skeleton, the interceptors cannot
                // obtain parameter information.
                //
                upcall_.postUnmarshal();

                //
                // Invoke the portable skeleton
                //
                org.omg.CORBA.portable.OutputStream out = inv._invoke(upcall_
                        .operation(), in, this);

                //
                // The OutputStream returned by _invoke() should be
                // the Upcall's OutputStream
                //
                org.apache.yoko.orb.OB.Assert._OB_assert(out == upcall_
                        .output());

                //
                // Finish up
                //
                if (!upcall_.userException())
                    upcall_.postMarshal();
            } catch (Abort ex) {
                //
                // Abort is raised by createExceptionReply()
                //
            } catch (org.apache.yoko.orb.OB.RuntimeLocationForward ex) {
                //
                // RuntimeLocationForward is raised by createReply() and
                // createExceptionReply() to bypass the portable
                // skeleton and report a location-forward
                //
                throw new org.apache.yoko.orb.OB.LocationForward(ex.ior,
                        ex.perm);
            }
        }
        //
        // Case 3: DSI
        //
        else if (servant_ instanceof org.omg.PortableServer.DynamicImplementation) {
            org.omg.PortableServer.DynamicImplementation impl = (org.omg.PortableServer.DynamicImplementation) servant_;
            org.apache.yoko.orb.CORBA.ServerRequest request = new org.apache.yoko.orb.CORBA.ServerRequest(
                    impl, upcall_);

            try {
                impl.invoke(request);
                request._OB_finishUnmarshal();
                request._OB_postinvoke();
                request._OB_doMarshal();
            } catch (org.omg.CORBA.SystemException ex) {
                request._OB_finishUnmarshal();
                throw ex;
            }
        } else
            org.apache.yoko.orb.OB.Assert._OB_assert(false);
    }

    // ----------------------------------------------------------------------
    // ResponseHandler standard method implementations
    // ----------------------------------------------------------------------

    //
    // Called by a portable skeleton for a normal reply
    //
    public org.omg.CORBA.portable.OutputStream createReply() {
        try {
            upcall_.postinvoke();
            return upcall_.preMarshal();
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
            //
            // We need to raise an exception in order to abort the
            // current execution context and return control to
            // DispatchStrategy_impl. We do this by raising a
            // RuntimeException containing the location forward
            // parameters.
            //
            // Note that the user can interfere with this process
            // if they trap RuntimeException.
            //
            throw new org.apache.yoko.orb.OB.RuntimeLocationForward(ex.ior,
                    ex.perm);
        }
    }

    //
    // Called by a portable skeleton for a user exception
    //
    public org.omg.CORBA.portable.OutputStream createExceptionReply() {
        org.omg.CORBA.portable.OutputStream out = upcall_
                .beginUserException(null);

        //
        // If the return value of beginUserException is null, then
        // we cannot let the skeleton attempt to marshal. So we'll
        // raise the Abort exception to bypass the portable skeleton.
        //
        // Note that the user can interfere with this process
        // if they trap RuntimeException.
        //
        if (out == null)
            throw new Abort();
        else
            return out;
    }
}

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

package org.apache.yoko.orb.PortableInterceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestInfo_impl extends org.omg.CORBA.LocalObject implements
        org.omg.PortableInterceptor.RequestInfo {
// the real logger backing instance.  We use the interface class as the locator
static final Logger logger = Logger.getLogger(RequestInfo_impl.class.getName());
    //
    // The ORB (Java only)
    //
    protected org.omg.CORBA.ORB orb_;

    //
    // The Request ID
    //
    protected int id_;

    //
    // The operation name
    //
    protected String op_;

    //
    // Is this method oneway?
    //
    protected boolean responseExpected_;

    //
    // ORBacus proprietary status flags
    //
    protected final static short NO_REPLY_SC = -2;

    protected final static short NO_REPLY = -1;

    //
    // The reply status
    //
    protected short status_;

    //
    // The forward reference (if status_ == LOCATION_FORWARD[_PERM]
    //
    protected org.omg.IOP.IOR forwardReference_;

    //
    // The ORB instance
    //
    protected org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    //
    // The policies
    //
    protected org.omg.CORBA.Policy[] policies_;

    //
    // The argument strategy
    //
    protected ArgumentStrategy argStrategy_;

    //
    // The Request and Reply service context lists
    //
    protected java.util.Vector requestSCL_;

    protected java.util.Vector replySCL_;

    //
    // ReceivedException (status_ == [SYSTEM|USER]_EXCEPTION)
    //
    protected Exception receivedException_;

    protected String receivedId_;

    //
    // Slot data for the request
    //
    protected org.omg.CORBA.Any[] slots_;

    //
    // A pointer to the PortableInterceptor::Current implementation
    //
    protected Current_impl current_;

    //
    // Does the slot data need to be popped in the current
    // implementation?
    //
    protected boolean popCurrent_;

    // ------------------------------------------------------------------
    // Private member implementations
    // ------------------------------------------------------------------

    private org.omg.IOP.ServiceContext copyServiceContext(
            org.omg.IOP.ServiceContext sc) {
        org.omg.IOP.ServiceContext result = new org.omg.IOP.ServiceContext();
        result.context_id = sc.context_id;
        result.context_data = new byte[sc.context_data.length];
        System.arraycopy(sc.context_data, 0, result.context_data, 0,
                sc.context_data.length);
        return result;
    }

    // ------------------------------------------------------------------
    // Protected member implementations
    // ------------------------------------------------------------------

    protected org.omg.IOP.ServiceContext getServiceContext(java.util.Vector l, int id) {
        for (int i = 0; i < l.size(); i++) {
            org.omg.IOP.ServiceContext sc = (org.omg.IOP.ServiceContext) l.elementAt(i);
            if (sc.context_id == id) {
                return copyServiceContext(sc);
            }
        }

        throw new org.omg.CORBA.BAD_PARAM(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidServiceContextId)
                        + ": " + id,
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidServiceContextId,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    protected void addServiceContext(java.util.Vector l, org.omg.IOP.ServiceContext sc, boolean addReplace) {
        //
        // It would be possible to use a hashtable internally for this
        // instead of a sequence. However, the additional overhead isn't
        // worth the effort.
        //
        for (int i = 0; i < l.size(); i++) {
            org.omg.IOP.ServiceContext c = (org.omg.IOP.ServiceContext) l.elementAt(i);
            if (c.context_id == sc.context_id) {
                if (!addReplace) {
                    throw new org.omg.CORBA.BAD_INV_ORDER(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorServiceContextExists)
                                    + ": " + sc.context_id,
                            org.apache.yoko.orb.OB.MinorCodes.MinorServiceContextExists,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                }
                l.setElementAt(copyServiceContext(sc), i);
                return;
            }
        }
        l.addElement(copyServiceContext(sc));
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java mapping
    // ------------------------------------------------------------------

    //
    // The ID uniquely identifies an active request/reply sequence.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public int request_id() {
        return id_;
    }

    //
    // The operation being invoked.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public String operation() {
        return op_;
    }

    //
    // A Dynamic::ParameterList containing the arguments on the operation
    // being invoked.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: no receive_other: no
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: no send_other: no
    //
    // TODO: verify server side against final document
    //
    public org.omg.Dynamic.Parameter[] arguments() {
        return argStrategy_.arguments();
    }

    //
    // A Dynamic::ExceptionList containing the exceptions that this invocation
    // may raise.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: no send_other: no
    //
    // TODO: verify server side against final document
    //
    public org.omg.CORBA.TypeCode[] exceptions() {
        return argStrategy_.exceptions();
    }

    //
    // A Dynamic::ContextList describing the contexts that may be passed
    // on this invocation.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: no send_other: no
    //
    // TODO: verify server side against final document
    //
    public String[] contexts() {
        throw new org.omg.CORBA.NO_IMPLEMENT(); // TODO: Implement
    }

    //
    // A Dynamic::Context describing the contexts being send on the
    // request.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: no send_other: no
    //
    // TODO: verify server side against final document
    //
    public String[] operation_context() {
        throw new org.omg.CORBA.NO_IMPLEMENT(); // TODO: Implement
    }

    //
    // The result of the method invocation. tk_void if the result type is
    // void
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: yes
    // receive_exception: no receive_other: no
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: no
    // send_reply: yes send_exception: no send_other: no
    //
    public org.omg.CORBA.Any result() {
        return argStrategy_.result();
    }

    //
    // Indicates whether there is a response expected for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public boolean response_expected() {
        return responseExpected_;
    }

    //
    // Indicates whether there is a response expected for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public short sync_scope() {
        throw new org.omg.CORBA.NO_IMPLEMENT(); // TODO: Implement
    }

    //
    // Describes the state of the result of the request.
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: no
    // send_reply: yes send_exception: yes send_other: yes
    //
    public short reply_status() {
        //
        // This cannot be called in send_poll, send_request or
        // receive_request_service_context, receive_request
        //
        if (status_ < 0) {
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return status_;
    }

    //
    // Contains the result of a location forward.
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: no
    // receive_exception: no receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: no
    // send_reply: no send_exception: no send_other: yes
    //
    public org.omg.CORBA.Object forward_reference() {
        //
        // This can only be called if the status is location forward
        // or location forward perm
        //
        if (status_ != org.omg.PortableInterceptor.LOCATION_FORWARD.value) {
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(forwardReference_ != null);
        return orbInstance_.getObjectFactory().createObject(forwardReference_);
    }

    //
    // Get the data from the slot table with the provided id.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public org.omg.CORBA.Any get_slot(int id)
            throws org.omg.PortableInterceptor.InvalidSlot {
        if (id >= slots_.length) {
            throw new org.omg.PortableInterceptor.InvalidSlot();
        }
        
        logger.fine("getting slot " + id + " for operation " + op_); 

        org.omg.CORBA.Any result = orb_.create_any();
        if (slots_[id] != null) {
            result.read_value(slots_[id].create_input_stream(), slots_[id].type());
        }
        return result;
    }

    //
    // Return a copy of the service context with the given id.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public org.omg.IOP.ServiceContext get_request_service_context(int id) {
        return getServiceContext(requestSCL_, id);
    }

    //
    // Return a copy of the service context with the given id.
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: no
    // send_reply: yes send_exception: yes send_other: yes
    //
    public org.omg.IOP.ServiceContext get_reply_service_context(int id) {
        if (status_ < 0) {
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return getServiceContext(replySCL_, id);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    //
    // No argument information available
    //
    protected RequestInfo_impl(org.omg.CORBA.ORB orb, int id, String op,
            boolean responseExpected, java.util.Vector requestSCL,
            java.util.Vector replySCL,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.Policy[] policies, Current_impl current) {
        orb_ = orb; // Java only

        id_ = id;
        op_ = op;
        responseExpected_ = responseExpected;
        orbInstance_ = orbInstance;
        policies_ = policies;
        requestSCL_ = requestSCL;
        replySCL_ = replySCL;
        current_ = current;
    }

    public void _OB_setReplyStatus(short status) {
        status_ = status;
    }

    public void _OB_setForwardReference(org.omg.IOP.IOR ior) {
        org.apache.yoko.orb.OB.Assert
                ._OB_assert(status_ == org.omg.PortableInterceptor.LOCATION_FORWARD.value);
        forwardReference_ = ior;
    }

    public void _OB_setReceivedException(Exception ex, String id) {
        //
        // id may be null
        //
        // TODO:
        // org.apache.yoko.orb.OB.Assert._OB_assert(receivedException_ == null);
        receivedException_ = ex;
        receivedId_ = id;
    }
}

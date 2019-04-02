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

import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.MinorCodes;
import org.apache.yoko.orb.OB.ORBInstance;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.IOR;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.RequestInfo;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestInfo_impl extends LocalObject implements
        RequestInfo {
// the real logger backing instance.  We use the interface class as the locator
static final Logger logger = Logger.getLogger(RequestInfo_impl.class.getName());
    //
    // The ORB (Java only)
    //
    protected ORB orb_;

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
    protected IOR forwardReference_;

    //
    // The ORB instance
    //
    protected ORBInstance orbInstance_;

    //
    // The policies
    //
    protected Policy[] policies_;

    //
    // The argument strategy
    //
    protected ArgumentStrategy argStrategy_;

    //
    // The Request and Reply service context lists
    //
    protected Vector requestSCL_;

    protected Vector replySCL_;

    //
    // ReceivedException (status_ == [SYSTEM|USER]_EXCEPTION)
    //
    protected Exception receivedException_;

    protected String receivedId_;

    //
    // Slot data for the request
    //
    protected Any[] slots_;

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

    private ServiceContext copyServiceContext(
            ServiceContext sc) {
        ServiceContext result = new ServiceContext();
        result.context_id = sc.context_id;
        result.context_data = new byte[sc.context_data.length];
        System.arraycopy(sc.context_data, 0, result.context_data, 0,
                sc.context_data.length);
        return result;
    }

    // ------------------------------------------------------------------
    // Protected member implementations
    // ------------------------------------------------------------------

    protected ServiceContext getServiceContext(Vector l, int id) {
        for (int i = 0; i < l.size(); i++) {
            ServiceContext sc = (ServiceContext) l.elementAt(i);
            if (sc.context_id == id) {
                return copyServiceContext(sc);
            }
        }

        throw new BAD_PARAM(
                MinorCodes
                        .describeBadParam(MinorCodes.MinorInvalidServiceContextId)
                        + ": " + id,
                MinorCodes.MinorInvalidServiceContextId,
                CompletionStatus.COMPLETED_NO);
    }

    protected void addServiceContext(Vector l, ServiceContext sc, boolean addReplace) {
        //
        // It would be possible to use a hashtable internally for this
        // instead of a sequence. However, the additional overhead isn't
        // worth the effort.
        //
        for (int i = 0; i < l.size(); i++) {
            ServiceContext c = (ServiceContext) l.elementAt(i);
            if (c.context_id == sc.context_id) {
                if (!addReplace) {
                    throw new BAD_INV_ORDER(
                            MinorCodes
                                    .describeBadInvOrder(MinorCodes.MinorServiceContextExists)
                                    + ": " + sc.context_id,
                            MinorCodes.MinorServiceContextExists,
                            CompletionStatus.COMPLETED_NO);
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
    public Parameter[] arguments() {
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
    public TypeCode[] exceptions() {
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
        throw new NO_IMPLEMENT(); // TODO: Implement
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
        throw new NO_IMPLEMENT(); // TODO: Implement
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
    public Any result() {
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
        throw new NO_IMPLEMENT(); // TODO: Implement
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
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);
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
        if (status_ != LOCATION_FORWARD.value) {
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);
        }

        Assert._OB_assert(forwardReference_ != null);
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
    public Any get_slot(int id)
            throws InvalidSlot {
        if (id >= slots_.length) {
            throw new InvalidSlot();
        }
        
        logger.fine("getting slot " + id + " for operation " + op_); 

        Any result = orb_.create_any();
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
    public ServiceContext get_request_service_context(int id) {
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
    public ServiceContext get_reply_service_context(int id) {
        if (status_ < 0) {
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);
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
    protected RequestInfo_impl(ORB orb, int id, String op,
                               boolean responseExpected, Vector requestSCL,
                               Vector replySCL,
                               ORBInstance orbInstance,
                               Policy[] policies, Current_impl current) {
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

    public void _OB_setForwardReference(IOR ior) {
        Assert
                ._OB_assert(status_ == LOCATION_FORWARD.value);
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

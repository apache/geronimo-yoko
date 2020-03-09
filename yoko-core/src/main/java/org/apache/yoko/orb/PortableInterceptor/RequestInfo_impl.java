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

import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.Downcall;
import org.apache.yoko.orb.OB.ORBInstance;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
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

import java.util.logging.Logger;

import static org.apache.yoko.orb.OB.Assert.ensure;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidServiceContextId;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadInvOrder;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadParam;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public class RequestInfo_impl extends LocalObject implements RequestInfo {
    static final Logger logger = Logger.getLogger(RequestInfo_impl.class.getName());
    private final int id;
    private final boolean requestIsOneWay;
    protected final static short NO_REPLY_SC = -2;
    protected final static short NO_REPLY = -1;
    protected final ORB orb;
    protected final ORBInstance orbInstance;
    protected final String operationName;
    protected final Policy[] policies;
    protected final ServiceContexts requestContexts;
    protected final ServiceContexts replyContexts;
    protected final Current_impl piCurrent;

    protected short replyStatus;
    protected IOR forwardReference; // only when status_ == LOCATION_FORWARD[_PERM]
    protected ArgumentStrategy argStrategy;
    protected Exception receivedException; // only when status_ == [SYSTEM|USER]_EXCEPTION)
    protected String receivedId;
    protected Any[] requestSlotData;
    protected boolean currentNeedsPopping;

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
    public int request_id() {
        return id;
    }

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
    public String operation() {
        return operationName;
    }

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
    public Parameter[] arguments() {
        return argStrategy.arguments();
    }

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
    public TypeCode[] exceptions() {
        return argStrategy.exceptions();
    }

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
    public String[] contexts() {
        throw new NO_IMPLEMENT(); // TODO: Implement
    }

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
    public String[] operation_context() {
        throw new NO_IMPLEMENT(); // TODO: Implement
    }

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
    public Any result() {
        return argStrategy.result();
    }

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
    public boolean response_expected() {
        return !requestIsOneWay;
    }

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
        if (replyStatus < 0) {
            throw newBadInvOrder();
        }
        return replyStatus;
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
        // This can only be called if the status is location forward or location forward perm
        if (replyStatus != LOCATION_FORWARD.value) throw newBadInvOrder();

        Assert.ensure(forwardReference != null);
        return orbInstance.getObjectFactory().createObject(forwardReference);
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
        if (id >= requestSlotData.length) {
            throw new InvalidSlot();
        }
        
        logger.fine("getting slot " + id + " for operation " + operationName);

        Any result = orb.create_any();
        if (requestSlotData[id] != null) {
            result.read_value(requestSlotData[id].create_input_stream(), requestSlotData[id].type());
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
        ServiceContext ctx = requestContexts.get(id);
        if (ctx == null) throw newBadParam(id);
        return ctx;
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
        if (replyStatus < 0) throw newBadInvOrder();
        ServiceContext result = replyContexts.get(id);
        if (result == null) throw newBadParam(id);
        return result;
    }

    private BAD_INV_ORDER newBadInvOrder() {
        final int code = MinorInvalidPICall;
        return new BAD_INV_ORDER(describeBadInvOrder(code), code, COMPLETED_NO);
    }

    private BAD_PARAM newBadParam(int id) {
        final int code = MinorInvalidServiceContextId;
        return new BAD_PARAM(describeBadParam(code) + ": " + id, code, COMPLETED_NO);
    }

    public RequestInfo_impl(ORB orb, ORBInstance orbInstance, Current_impl current, Downcall dc) {
        this(orb, dc.requestId(), dc.operation(), dc.responseExpected(), dc.requestContexts, dc.replyContexts, orbInstance, dc.policies().value, current);

    }

    protected RequestInfo_impl(ORB orb, int id, String op,
                               boolean responseExpected,
                               ServiceContexts requestContexts, ServiceContexts replyContexts,
                               ORBInstance orbInstance,
                               Policy[] policies, Current_impl current) {
        this.orb = orb;
        this.id = id;
        this.operationName = op;
        this.requestIsOneWay = !responseExpected;
        this.orbInstance = orbInstance;
        this.policies = policies;
        this.requestContexts = requestContexts;
        this.replyContexts = replyContexts;
        this.piCurrent = current;
    }

    public void _OB_setReplyStatus(short status) {
        replyStatus = status;
    }

    public void _OB_setForwardReference(IOR ior) {
        Assert.ensure(replyStatus == LOCATION_FORWARD.value);
        forwardReference = ior;
    }

    public void _OB_setReceivedException(Exception ex, String id) {
        receivedException = ex;
        receivedId = id;
    }

    /**
     * If a set of slots was provided to the Current implementation then the slots have to be popped
     */
    void popCurrent() {
        if (currentNeedsPopping) {
            currentNeedsPopping = false;
            piCurrent._OB_popSlotData();
        }
    }
}

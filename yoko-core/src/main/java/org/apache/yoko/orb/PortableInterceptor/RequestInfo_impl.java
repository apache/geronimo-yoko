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

import java.util.Vector;
import java.util.logging.Logger;

import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidServiceContextId;
import static org.apache.yoko.orb.OB.MinorCodes.MinorServiceContextExists;
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
    protected final Vector requestSCL_;
    protected final Vector replySCL_;
    protected final Current_impl piCurrent;

    protected short replyStatus;
    protected IOR forwardReference; // only when status_ == LOCATION_FORWARD[_PERM]
    protected ArgumentStrategy argStrategy;
    protected Exception receivedException; // only when status_ == [SYSTEM|USER]_EXCEPTION)
    protected String receivedId;
    protected Any[] requestSlotData;
    protected boolean currentNeedsPopping;

    private ServiceContext copyServiceContext(ServiceContext sc) {
        ServiceContext result = new ServiceContext();
        result.context_id = sc.context_id;
        result.context_data = new byte[sc.context_data.length];
        System.arraycopy(sc.context_data, 0, result.context_data, 0, sc.context_data.length);
        return result;
    }

    private ServiceContext getServiceContext(Vector l, int id) {
        for (int i = 0; i < l.size(); i++) {
            ServiceContext sc = (ServiceContext) l.elementAt(i);
            if (sc.context_id == id) {
                return copyServiceContext(sc);
            }
        }

        throw new BAD_PARAM(describeBadParam(MinorInvalidServiceContextId) + ": " + id, MinorInvalidServiceContextId, COMPLETED_NO);
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
                if (addReplace) {
                    l.setElementAt(copyServiceContext(sc), i);
                    return;
                }
                throw new BAD_INV_ORDER(describeBadInvOrder(MinorServiceContextExists) + ": " + sc.context_id, MinorServiceContextExists, COMPLETED_NO);
            }
        }
        l.addElement(copyServiceContext(sc));
    }
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
            throw new BAD_INV_ORDER(
                    describeBadInvOrder(MinorInvalidPICall),
                    MinorInvalidPICall,
                    COMPLETED_NO);
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
        //
        // This can only be called if the status is location forward
        // or location forward perm
        //
        if (replyStatus != LOCATION_FORWARD.value) {
            throw new BAD_INV_ORDER(
                    describeBadInvOrder(MinorInvalidPICall),
                    MinorInvalidPICall,
                    COMPLETED_NO);
        }

        _OB_assert(forwardReference != null);
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
        if (replyStatus < 0) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        return getServiceContext(replySCL_, id);
    }

    public RequestInfo_impl(ORB orb, ORBInstance orbInstance, Current_impl current, Downcall dc) {
        this(orb, dc.requestId(), dc.operation(), dc.responseExpected(), dc.requestSCL_, dc.replySCL_, orbInstance, dc.policies().value, current);
    }

    protected RequestInfo_impl(ORB orb, int id, String op,
                               boolean responseExpected, Vector requestSCL,
                               Vector replySCL,
                               ORBInstance orbInstance,
                               Policy[] policies, Current_impl current) {
        this.orb = orb;
        this.id = id;
        this.operationName = op;
        this.requestIsOneWay = !responseExpected;
        this.orbInstance = orbInstance;
        this.policies = policies;
        this.requestSCL_ = requestSCL;
        this.replySCL_ = replySCL;
        this.piCurrent = current;
    }

    public void _OB_setReplyStatus(short status) {
        replyStatus = status;
    }

    public void _OB_setForwardReference(IOR ior) {
        _OB_assert(replyStatus == LOCATION_FORWARD.value);
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

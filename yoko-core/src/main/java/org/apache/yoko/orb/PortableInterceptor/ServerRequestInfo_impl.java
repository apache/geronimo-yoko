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

import org.apache.yoko.orb.CORBA.Delegate;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OB.LocationForward;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.ParameterDesc;
import org.apache.yoko.orb.OB.Util;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.util.cmsf.CmsfThreadLocal;
import org.apache.yoko.util.cmsf.CmsfThreadLocal.CmsfOverride;
import org.apache.yoko.util.yasf.YasfThreadLocal;
import org.apache.yoko.util.yasf.YasfThreadLocal.YasfOverride;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.TRANSPORT_RETRY;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

import java.util.Arrays;
import java.util.List;

import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall;
import static org.apache.yoko.orb.OB.MinorCodes.MinorNoPolicyFactory;
import static org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadInvOrder;
import static org.apache.yoko.orb.OB.MinorCodes.describeInvPolicy;
import static org.apache.yoko.orb.OB.MinorCodes.describeUnknown;
import static org.apache.yoko.util.CollectionExtras.newSynchronizedList;
import static org.apache.yoko.util.CollectionExtras.removeInReverse;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;
import static org.omg.CORBA.CompletionStatus.COMPLETED_YES;

final public class ServerRequestInfo_impl extends RequestInfo_impl implements ServerRequestInfoExt {
    private final List<ServerRequestInterceptor> interceptors = newSynchronizedList();
    private final byte[] adapterId;
    private final byte[] objectId;
    private final ObjectReferenceTemplate adapterTemplate;
    private final TransportInfo transportInfo;
    private Servant servant;
    private POA poa;

    // Return the raised exception.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: no
    // send_reply: no send_exception: yes send_other: no
    public Any sending_exception() {
        // If status is not SYSTEM_EXCEPTION or USER_EXCEPTION then this is a BAD_INV_ORDER exception
        if (replyStatus != SYSTEM_EXCEPTION.value && replyStatus != USER_EXCEPTION.value)
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);

        // UnknownUserException? Extract the contained UserException.
        if (receivedException instanceof UnknownUserException) return ((UnknownUserException) receivedException).except;

        final Any exceptionAny = orb.create_any();

        // If the application code hasn't provided the exception then we insert an UNKNOWN
        Exception exceptionToInsert = receivedException == null
                ? new UNKNOWN(describeUnknown(MinorUnknownUserException) + ": exception unavailable", MinorUnknownUserException, COMPLETED_YES)
                : receivedException;

        return Util.insertException(exceptionAny, exceptionToInsert);
    }

    // Return the object-id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public byte[] object_id() {
        if (replyStatus == NO_REPLY_SC) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        return Arrays.copyOf(objectId, objectId.length);
    }

    // Return the adapter_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public byte[] adapter_id() {
        if (replyStatus == NO_REPLY_SC) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        return Arrays.copyOf(adapterId, adapterId.length);
    }

    // Return the adapter_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: no send_exception: no send_other: no
    public String target_most_derived_interface() {
        if (replyStatus == NO_REPLY_SC || servant == null) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        _OB_assert(poa != null);
        return servant._all_interfaces(poa, objectId)[0];
    }

    // Return the server_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public String server_id() {
        if (replyStatus == NO_REPLY_SC) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        return adapterTemplate.server_id();
    }

    // Return the orb_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public String orb_id() {
        if (replyStatus == NO_REPLY_SC) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        return adapterTemplate.orb_id();
    }

    // Return the adapter_name of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public String[] adapter_name() {
        if (replyStatus == NO_REPLY_SC) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        return adapterTemplate.adapter_name();
    }

    // Retrieve the policy with the provided type in effect for this
    // object-adapter.
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public Policy get_server_policy(int type) {
        for (Policy policy : policies) {
            if (policy.policy_type() == type) return policy;
        }

        // if the target policy was not in the current policy list, check to see
        // if the type has even been registered.  If it is valid, return null
        // to indicate we ain't got one.
        if (orbInstance.getPolicyFactoryManager().isPolicyRegistered(type)) {
            return null;
        }
        throw new INV_POLICY(describeInvPolicy(MinorNoPolicyFactory) + ": " + type, MinorNoPolicyFactory, COMPLETED_NO);
    }

    // Set the slot in the slot table for the request thread-level data.
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public void set_slot(int id, Any data) throws InvalidSlot {
        if (id >= requestSlotData.length) {
            throw new InvalidSlot();
        }
        logger.fine("setting slot " + id + " for operation " + operationName);
        requestSlotData[id] = new org.apache.yoko.orb.CORBA.Any(data);
    }

    // Determine if the servant has the given repository id.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: no send_exception: no send_other: no
    public boolean target_is_a(String id) {
        if (replyStatus == NO_REPLY_SC || servant == null) {
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);
        }
        return servant._is_a(id);
    }

    // Add a service context for the reply.
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    public void add_reply_service_context(ServiceContext sc, boolean addReplace) {
        replyContexts.mutable().add(sc, addReplace);
    }

    public ServerRequestInfo_impl(
            ORB orb,
            int id,
            String op,
            boolean responseExpected,
            Policy[] policies,
            byte[] adapterId,
            byte[] objectId,
            ObjectReferenceTemplate adapterTemplate,
            ServiceContexts requestContexts,
            ServiceContexts replyContexts,
            ORBInstance orbInstance, Current_impl current,
            TransportInfo transportInfo) {
        super(orb, id, op, responseExpected, requestContexts, replyContexts, orbInstance, policies, current);

        this.adapterId = adapterId;
        this.objectId = objectId;
        this.adapterTemplate = adapterTemplate;
        servant = null;
        poa = null;
        this.transportInfo = transportInfo;

        // TODO: Dump NO_REPLY_SC
        replyStatus = NO_REPLY_SC;

        // Start of with a Null strategy. It's not necessary to deal with
        // arguments() and exceptions() being called during
        // receive_request_service_contexts since this cannot occur.
        argStrategy = new ArgumentStrategyNull(this.orb);

        // On the server side the slots_ are initialized with the
        // correct number of entries
        requestSlotData = piCurrent._OB_newSlotTable();
    }

    public void _OB_requestServiceContext(List<ServerRequestInterceptor> interceptors) throws LocationForward {
        // Arguments, result and exceptions not available
        argStrategy.setResultAvail(false);
        argStrategy.setArgsAvail(false);
        argStrategy.setExceptAvail(false);

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            for (ServerRequestInterceptor i: interceptors) {
                i.receive_request_service_contexts(this);
                this.interceptors.add(i);
            }
            currentNeedsPopping = true;
            piCurrent._OB_pushSlotData(requestSlotData);
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_request() throws LocationForward {
        // Arguments, exceptions are now available. Result isn't.
        argStrategy.setArgsAvail(true);
        argStrategy.setExceptAvail(true);
        replyStatus = NO_REPLY;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            for (ServerRequestInterceptor sri: interceptors)
                sri.receive_request(this);
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_sendReply() {
        _OB_assert(replyStatus == SUCCESSFUL.value);
        // The result is available
        argStrategy.setResultAvail(true);
        // The servant is no longer available
        servant = null;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            for (ServerRequestInterceptor i: removeInReverse(interceptors)) {
                i.send_reply(this);
            }
            popCurrent();
        }
    }

    public void _OB_sendException() throws LocationForward {
        // Arguments, result not available
        argStrategy.setResultAvail(false);
        argStrategy.setArgsAvail(false);
        // The servant is no longer available
        servant = null;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            _OB_assert(replyStatus == SYSTEM_EXCEPTION.value || replyStatus == USER_EXCEPTION.value);

            for (ServerRequestInterceptor i: removeInReverse(interceptors)) {
                i.send_exception(this);
            }

            popCurrent();
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_sendOther() throws LocationForward {
        // Arguments, result not available
        argStrategy.setResultAvail(false);
        argStrategy.setArgsAvail(false);
        // The servant is no longer available
        servant = null;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            _OB_assert(replyStatus == LOCATION_FORWARD.value || replyStatus == TRANSPORT_RETRY.value);

            for (ServerRequestInterceptor i: removeInReverse(interceptors)) {
                i.send_other(this);
            }

            popCurrent();
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_servant(Servant servant, POA poa) {
        this.servant = servant;
        this.poa = poa;
    }

    // Called when we are entering or leaving a thread, to allow the Current to be managed properly
    public void _OB_contextSwitch() {
        if (currentNeedsPopping) {
            logger.fine("Popping the PICurrent because of a context switch"); 
            currentNeedsPopping = false;
            piCurrent._OB_popSlotData();
        } else {
            logger.fine("Pushing the PICurrent because of a context switch"); 
            currentNeedsPopping = true;
            piCurrent._OB_pushSlotData(requestSlotData);
        }
    }

    public void _OB_parameterDesc(ParameterDesc[] argDesc, ParameterDesc retDesc, TypeCode[] exceptionTC) {
        argStrategy = new ArgumentStrategySII(orb, argDesc, retDesc, exceptionTC);
    }

    public void _OB_arguments(NVList args) {
        argStrategy = new ArgumentStrategyDII(orb, args);
        // Exceptions are never available
        argStrategy.setExceptNeverAvail();
    }

    public void _OB_result(Any value) {
        // Result is now available. Update the argument strategy.
        _OB_assert(argStrategy != null);
        argStrategy.setResult(value);
    }

    /**
     * Retrieve the TransportInfo object associated with this server
     * request.  The TransportInfo object contains information about
     * the connection used for the request.
     *
     * @return The TransportInfo object created by the OCI layer.
     */
    public TransportInfo getTransportInfo() {
        return transportInfo;
    }
}

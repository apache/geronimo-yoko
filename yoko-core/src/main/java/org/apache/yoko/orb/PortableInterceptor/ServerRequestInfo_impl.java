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
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.LocationForward;
import org.apache.yoko.orb.OB.MinorCodes;
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
import org.omg.CORBA.CompletionStatus;
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

import java.util.Enumeration;
import java.util.Vector;

final public class ServerRequestInfo_impl extends RequestInfo_impl implements
        ServerRequestInfoExt {
    //
    // Sequence of ServerRequestInterceptors to call on reply
    //
    Vector interceptors_ = new Vector();

    //
    // The adapter ID
    //
    private byte[] adapterId_;

    //
    // The object-id
    //
    private byte[] objectId_;

    //
    // The servant
    //
    private Servant servant_;

    //
    // The POA
    //
    private POA poa_;

    //
    // The adapter ORT
    //
    private ObjectReferenceTemplate adapterTemplate_;

    //
    // The adapter name
    //
    private String[] adapterName_;

    //
    // The information about the transport servicing this request
    //
    private TransportInfo transportInfo_;

    // ------------------------------------------------------------------
    // Standard IDL to Java mapping
    // ------------------------------------------------------------------

    //
    // Return the raised exception.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: no
    // send_reply: no send_exception: yes send_other: no
    //
    public Any sending_exception() {
        //
        // If status is not SYSTEM_EXCEPTION or USER_EXCEPTION then this
        // is a BAD_INV_ORDER exception
        //
        if (status_ != SYSTEM_EXCEPTION.value
                && status_ != USER_EXCEPTION.value)
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);

        //
        // If the application code hasn't provided the exception
        // then we insert an UNKNOWN
        //
        // (Java only)
        //
        if (receivedException_ == null) {
            Any any = orb_.create_any();
            Util
                    .insertException(
                            any,
                            new UNKNOWN(
                                    MinorCodes
                                            .describeUnknown(MinorCodes.MinorUnknownUserException)
                                            + ": exception unavailable",
                                    MinorCodes.MinorUnknownUserException,
                                    CompletionStatus.COMPLETED_YES));
            return any;
        }

        //
        // UnknownUserException? Extract the contained UserException.
        //
        UnknownUserException unk = null;
        try {
            unk = (UnknownUserException) receivedException_;
        } catch (ClassCastException ex) {
        }
        if (unk != null)
            return unk.except;

        Any any = orb_.create_any();
        Util.insertException(any, receivedException_);

        return any;
    }

    //
    // Return the object-id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public byte[] object_id() {
        if (status_ == NO_REPLY_SC)
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);

        byte[] data = new byte[objectId_.length];
        System.arraycopy(objectId_, 0, data, 0, objectId_.length);

        return data;
    }

    //
    // Return the adapter_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public byte[] adapter_id() {
        if (status_ == NO_REPLY_SC)
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);

        byte[] result = new byte[adapterId_.length];
        System.arraycopy(adapterId_, 0, result, 0, adapterId_.length);
        return result;
    }

    //
    // Return the adapter_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: no send_exception: no send_other: no
    //
    public String target_most_derived_interface() {
        if (status_ == NO_REPLY_SC || servant_ == null)
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);

        Assert._OB_assert(poa_ != null);
        return servant_._all_interfaces(poa_, objectId_)[0];
    }

    //
    // Return the server_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public String server_id() {
        if (status_ == NO_REPLY_SC)
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);

        return adapterTemplate_.server_id();
    }

    //
    // Return the orb_id of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public String orb_id() {
        if (status_ == NO_REPLY_SC)
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);

        return adapterTemplate_.orb_id();
    }

    //
    // Return the adapter_name of the destination of this request.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public String[] adapter_name() {
        if (status_ == NO_REPLY_SC)
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);

        return adapterTemplate_.adapter_name();
    }

    //
    // Retrieve the policy with the provided type in effect for this
    // object-adapter.
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public Policy get_server_policy(int type) {
        for (int i = 0; i < policies_.length; i++) {
            if (policies_[i].policy_type() == type) {
                return policies_[i];
            }
        }

        // if the target policy was not in the current policy list, check to see
        // if the type has even been registered.  If it is valid, return null
        // to indicate we ain't got one.
        if (orbInstance_.getPolicyFactoryManager().isPolicyRegistered(type)) {
            return null;
        }

        throw new INV_POLICY(
                MinorCodes
                        .describeInvPolicy(MinorCodes.MinorNoPolicyFactory)
                        + ": " + type,
                MinorCodes.MinorNoPolicyFactory,
                CompletionStatus.COMPLETED_NO);
    }

    //
    // Set the slot in the slot table for the request thread-level data.
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public void set_slot(int id, Any data)
            throws InvalidSlot {
        if (id >= slots_.length) {
            throw new InvalidSlot();
        }
        logger.fine("setting slot " + id + " for operation " + op_); 
        slots_[id] = new org.apache.yoko.orb.CORBA.Any(data);
    }

    //
    // Determine if the servant has the given repository id.
    //
    // Server side:
    //
    // receive_request_service_contexts: no receive_request: yes
    // send_reply: no send_exception: no send_other: no
    //
    public boolean target_is_a(String id) {
        if (status_ == NO_REPLY_SC || servant_ == null) {
            throw new BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorInvalidPICall),
                    MinorCodes.MinorInvalidPICall,
                    CompletionStatus.COMPLETED_NO);
        }

        return servant_._is_a(id);
    }

    //
    // Add a service context for the reply.
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public void add_reply_service_context(ServiceContext sc,
                                          boolean addReplace) {
        addServiceContext(replySCL_, sc, addReplace);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ServerRequestInfo_impl(
            ORB orb,
            int id,
            String op,
            boolean responseExpected,
            Policy[] policies,
            byte[] adapterId,
            byte[] objectId,
            ObjectReferenceTemplate adapterTemplate,
            Vector request, Vector reply,
            ORBInstance orbInstance, Current_impl current,
            TransportInfo transportInfo) {
        super(orb, id, op, responseExpected, request, reply, orbInstance,
                policies, current);

        adapterId_ = adapterId;
        objectId_ = objectId;
        adapterTemplate_ = adapterTemplate;
        servant_ = null;
        poa_ = null;
        transportInfo_ = transportInfo;

        //
        // TODO: Dump NO_REPLY_SC
        //
        status_ = NO_REPLY_SC;

        //
        // Start of with a Null strategy. It's not necessary to deal with
        // arguments() and exceptions() being called during
        // receive_request_service_contexts since this cannot occur.
        //
        argStrategy_ = new ArgumentStrategyNull(orb_);

        //
        // On the server side the slots_ are initialized with the
        // correct number of entries
        //
        slots_ = current_._OB_newSlotTable();
    }

    public void _OB_requestServiceContext(Vector interceptors)
            throws LocationForward {
        //
        // Arguments, result and exceptions not available
        //
        argStrategy_.setResultAvail(false);
        argStrategy_.setArgsAvail(false);
        argStrategy_.setExceptAvail(false);

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            Enumeration e = interceptors.elements();
            while (e.hasMoreElements()) {
                ServerRequestInterceptor i = (ServerRequestInterceptor) e
                        .nextElement();

                i.receive_request_service_contexts(this);
                interceptors_.addElement(i);
            }

            popCurrent_ = true;
            current_._OB_pushSlotData(slots_);
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_request() throws LocationForward {
        //
        // Arguments, exceptions are now available. Result isn't.
        //
        argStrategy_.setArgsAvail(true);
        argStrategy_.setExceptAvail(true);

        status_ = NO_REPLY;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            Enumeration e = interceptors_.elements();
            while (e.hasMoreElements()) {
                ((ServerRequestInterceptor) (e
                        .nextElement())).receive_request(this);
            }
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_sendReply() {
        Assert
                ._OB_assert(status_ == SUCCESSFUL.value);
        //
        // The result is available
        //
        argStrategy_.setResultAvail(true);

        //
        // The servant is no longer available
        //
        servant_ = null;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            int curr = interceptors_.size() - 1;
            while (!interceptors_.isEmpty()) {
                ServerRequestInterceptor i = (ServerRequestInterceptor) interceptors_
                        .elementAt(curr);
                interceptors_.removeElementAt(curr);
                --curr;

                i.send_reply(this);
            }
        }

        //
        // If a set of slots was provided to the Current implementation
        // then the slots have to be popped
        //
        if (popCurrent_) {
            popCurrent_ = false;
            current_._OB_popSlotData();
        }
    }

    public void _OB_sendException()
            throws LocationForward {
        //
        // Arguments, result not available
        //
        argStrategy_.setResultAvail(false);
        argStrategy_.setArgsAvail(false);

        //
        // The servant is no longer available
        //
        servant_ = null;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            Assert
                    ._OB_assert(status_ == SYSTEM_EXCEPTION.value
                            || status_ == USER_EXCEPTION.value);

            int curr = interceptors_.size() - 1;
            while (!interceptors_.isEmpty()) {
                ServerRequestInterceptor i = (ServerRequestInterceptor) interceptors_
                        .elementAt(curr);
                interceptors_.removeElementAt(curr);
                --curr;

                i.send_exception(this);
            }

            //
            // If a set of slots was provided to the Current implementation
            // then the slots have to be popped
            //
            if (popCurrent_) {
                popCurrent_ = false;
                current_._OB_popSlotData();
            }
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_sendOther() throws LocationForward {
        //
        // Arguments, result not available
        //
        argStrategy_.setResultAvail(false);
        argStrategy_.setArgsAvail(false);

        //
        // The servant is no longer available
        //
        servant_ = null;

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            Assert
                    ._OB_assert(status_ == LOCATION_FORWARD.value
                            || status_ == TRANSPORT_RETRY.value);

            int curr = interceptors_.size() - 1;
            while (!interceptors_.isEmpty()) {
                ServerRequestInterceptor i = (ServerRequestInterceptor) interceptors_
                        .elementAt(curr);
                interceptors_.removeElementAt(curr);
                --curr;

                i.send_other(this);
            }

            //
            // If a set of slots was provided to the Current implementation
            // then the slots have to be popped
            //
            if (popCurrent_) {
                popCurrent_ = false;
                current_._OB_popSlotData();
            }
        } catch (ForwardRequest ex) {
            Delegate p = (Delegate) (((ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_servant(Servant servant,
                            POA poa) {
        servant_ = servant;
        poa_ = poa;
    }

    //
    // Called when we are entering or leaving a thread, to allow
    // the Current to be managed properly
    //
    public void _OB_contextSwitch() {
        if (popCurrent_) {
            logger.fine("Popping the PICurrent because of a context switch"); 
            popCurrent_ = false;
            current_._OB_popSlotData();
        } else {
            logger.fine("Pushing the PICurrent because of a context switch"); 
            popCurrent_ = true;
            current_._OB_pushSlotData(slots_);
        }
    }

    public void _OB_parameterDesc(
            ParameterDesc[] argDesc,
            ParameterDesc retDesc,
            TypeCode[] exceptionTC) {
        //
        // Update the argument strategy
        //
        argStrategy_ = new ArgumentStrategySII(orb_, argDesc, retDesc,
                exceptionTC);
    }

    public void _OB_arguments(NVList args) {
        //
        // Update the argument strategy
        //
        argStrategy_ = new ArgumentStrategyDII(orb_, args);

        //
        // Exceptions are never available
        //
        argStrategy_.setExceptNeverAvail();
    }

    public void _OB_result(Any value) {
        //
        // Result is now available. Update the argument strategy.
        //
        Assert._OB_assert(argStrategy_ != null);
        argStrategy_.setResult(value);
    }


    /**
     * Retrieve the TransportInfo object associated with this server
     * request.  The TransportInfo object contains information about
     * the connection used for the request.
     *
     * @return The TransportInfo object created by the OCI layer.
     */
    public TransportInfo getTransportInfo() {
        return transportInfo_;
    }
}

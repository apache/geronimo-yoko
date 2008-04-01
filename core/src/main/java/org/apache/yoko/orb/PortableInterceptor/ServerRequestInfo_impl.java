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

final public class ServerRequestInfo_impl extends RequestInfo_impl implements
        ServerRequestInfoExt {
    //
    // Sequence of ServerRequestInterceptors to call on reply
    //
    java.util.Vector interceptors_ = new java.util.Vector();

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
    private org.omg.PortableServer.Servant servant_;

    //
    // The POA
    //
    private org.omg.PortableServer.POA poa_;

    //
    // The adapter ORT
    //
    private org.omg.PortableInterceptor.ObjectReferenceTemplate adapterTemplate_;

    //
    // The adapter name
    //
    private String[] adapterName_;

    //
    // The information about the transport servicing this request
    //
    private org.apache.yoko.orb.OCI.TransportInfo transportInfo_;

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
    public org.omg.CORBA.Any sending_exception() {
        //
        // If status is not SYSTEM_EXCEPTION or USER_EXCEPTION then this
        // is a BAD_INV_ORDER exception
        //
        if (status_ != org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value
                && status_ != org.omg.PortableInterceptor.USER_EXCEPTION.value)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        //
        // If the application code hasn't provided the exception
        // then we insert an UNKNOWN
        //
        // (Java only)
        //
        if (receivedException_ == null) {
            org.omg.CORBA.Any any = orb_.create_any();
            org.apache.yoko.orb.OB.Util
                    .insertException(
                            any,
                            new org.omg.CORBA.UNKNOWN(
                                    org.apache.yoko.orb.OB.MinorCodes
                                            .describeUnknown(org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException)
                                            + ": exception unavailable",
                                    org.apache.yoko.orb.OB.MinorCodes.MinorUnknownUserException,
                                    org.omg.CORBA.CompletionStatus.COMPLETED_YES));
            return any;
        }

        //
        // UnknownUserException? Extract the contained UserException.
        //
        org.omg.CORBA.UnknownUserException unk = null;
        try {
            unk = (org.omg.CORBA.UnknownUserException) receivedException_;
        } catch (ClassCastException ex) {
        }
        if (unk != null)
            return unk.except;

        org.omg.CORBA.Any any = orb_.create_any();
        org.apache.yoko.orb.OB.Util.insertException(any, receivedException_);

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
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

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
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

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
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        org.apache.yoko.orb.OB.Assert._OB_assert(poa_ != null);
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
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

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
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

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
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

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
    public org.omg.CORBA.Policy get_server_policy(int type) {
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

        throw new org.omg.CORBA.INV_POLICY(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeInvPolicy(org.apache.yoko.orb.OB.MinorCodes.MinorNoPolicyFactory)
                        + ": " + type,
                org.apache.yoko.orb.OB.MinorCodes.MinorNoPolicyFactory,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    //
    // Set the slot in the slot table for the request thread-level data.
    //
    // Server side:
    //
    // receive_request_service_contexts: yes receive_request: yes
    // send_reply: yes send_exception: yes send_other: yes
    //
    public void set_slot(int id, org.omg.CORBA.Any data)
            throws org.omg.PortableInterceptor.InvalidSlot {
        if (id >= slots_.length) {
            throw new org.omg.PortableInterceptor.InvalidSlot();
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
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
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
    public void add_reply_service_context(org.omg.IOP.ServiceContext sc,
            boolean addReplace) {
        addServiceContext(replySCL_, sc, addReplace);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ServerRequestInfo_impl(
            org.omg.CORBA.ORB orb,
            int id,
            String op,
            boolean responseExpected,
            org.omg.CORBA.Policy[] policies,
            byte[] adapterId,
            byte[] objectId,
            org.omg.PortableInterceptor.ObjectReferenceTemplate adapterTemplate,
            java.util.Vector request, java.util.Vector reply,
            org.apache.yoko.orb.OB.ORBInstance orbInstance, Current_impl current,
            org.apache.yoko.orb.OCI.TransportInfo transportInfo) {
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

    public void _OB_requestServiceContext(java.util.Vector interceptors)
            throws org.apache.yoko.orb.OB.LocationForward {
        //
        // Arguments, result and exceptions not available
        //
        argStrategy_.setResultAvail(false);
        argStrategy_.setArgsAvail(false);
        argStrategy_.setExceptAvail(false);

        try {
            java.util.Enumeration e = interceptors.elements();
            while (e.hasMoreElements()) {
                org.omg.PortableInterceptor.ServerRequestInterceptor i = (org.omg.PortableInterceptor.ServerRequestInterceptor) e
                        .nextElement();

                i.receive_request_service_contexts(this);
                interceptors_.addElement(i);
            }

            popCurrent_ = true;
            current_._OB_pushSlotData(slots_);
        } catch (org.omg.PortableInterceptor.ForwardRequest ex) {
            org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new org.apache.yoko.orb.OB.LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_request() throws org.apache.yoko.orb.OB.LocationForward {
        //
        // Arguments, exceptions are now available. Result isn't.
        //
        argStrategy_.setArgsAvail(true);
        argStrategy_.setExceptAvail(true);

        status_ = NO_REPLY;

        try {
            java.util.Enumeration e = interceptors_.elements();
            while (e.hasMoreElements()) {
                ((org.omg.PortableInterceptor.ServerRequestInterceptor) (e
                        .nextElement())).receive_request(this);
            }
        } catch (org.omg.PortableInterceptor.ForwardRequest ex) {
            org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new org.apache.yoko.orb.OB.LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_sendReply() {
        org.apache.yoko.orb.OB.Assert
                ._OB_assert(status_ == org.omg.PortableInterceptor.SUCCESSFUL.value);
        //
        // The result is available
        //
        argStrategy_.setResultAvail(true);

        //
        // The servant is no longer available
        //
        servant_ = null;

        int curr = interceptors_.size() - 1;
        while (!interceptors_.isEmpty()) {
            org.omg.PortableInterceptor.ServerRequestInterceptor i = (org.omg.PortableInterceptor.ServerRequestInterceptor) interceptors_
                    .elementAt(curr);
            interceptors_.removeElementAt(curr);
            --curr;

            i.send_reply(this);
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
            throws org.apache.yoko.orb.OB.LocationForward {
        //
        // Arguments, result not available
        //
        argStrategy_.setResultAvail(false);
        argStrategy_.setArgsAvail(false);

        //
        // The servant is no longer available
        //
        servant_ = null;

        try {
            org.apache.yoko.orb.OB.Assert
                    ._OB_assert(status_ == org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value
                            || status_ == org.omg.PortableInterceptor.USER_EXCEPTION.value);

            int curr = interceptors_.size() - 1;
            while (!interceptors_.isEmpty()) {
                org.omg.PortableInterceptor.ServerRequestInterceptor i = (org.omg.PortableInterceptor.ServerRequestInterceptor) interceptors_
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
        } catch (org.omg.PortableInterceptor.ForwardRequest ex) {
            org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new org.apache.yoko.orb.OB.LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_sendOther() throws org.apache.yoko.orb.OB.LocationForward {
        //
        // Arguments, result not available
        //
        argStrategy_.setResultAvail(false);
        argStrategy_.setArgsAvail(false);

        //
        // The servant is no longer available
        //
        servant_ = null;

        try {
            org.apache.yoko.orb.OB.Assert
                    ._OB_assert(status_ == org.omg.PortableInterceptor.LOCATION_FORWARD.value
                            || status_ == org.omg.PortableInterceptor.TRANSPORT_RETRY.value);

            int curr = interceptors_.size() - 1;
            while (!interceptors_.isEmpty()) {
                org.omg.PortableInterceptor.ServerRequestInterceptor i = (org.omg.PortableInterceptor.ServerRequestInterceptor) interceptors_
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
        } catch (org.omg.PortableInterceptor.ForwardRequest ex) {
            org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) ex.forward)
                    ._get_delegate());
            throw new org.apache.yoko.orb.OB.LocationForward(p._OB_IOR(), false);
        }
    }

    public void _OB_servant(org.omg.PortableServer.Servant servant,
            org.omg.PortableServer.POA poa) {
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
            org.apache.yoko.orb.OB.ParameterDesc[] argDesc,
            org.apache.yoko.orb.OB.ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) {
        //
        // Update the argument strategy
        //
        argStrategy_ = new ArgumentStrategySII(orb_, argDesc, retDesc,
                exceptionTC);
    }

    public void _OB_arguments(org.omg.CORBA.NVList args) {
        //
        // Update the argument strategy
        //
        argStrategy_ = new ArgumentStrategyDII(orb_, args);

        //
        // Exceptions are never available
        //
        argStrategy_.setExceptNeverAvail();
    }

    public void _OB_result(org.omg.CORBA.Any value) {
        //
        // Result is now available. Update the argument strategy.
        //
        org.apache.yoko.orb.OB.Assert._OB_assert(argStrategy_ != null);
        argStrategy_.setResult(value);
    }


    /**
     * Retrieve the TransportInfo object associated with this server
     * request.  The TransportInfo object contains information about
     * the connection used for the request.
     *
     * @return The TransportInfo object created by the OCI layer.
     */
    public org.apache.yoko.orb.OCI.TransportInfo getTransportInfo() {
        return transportInfo_;
    }
}

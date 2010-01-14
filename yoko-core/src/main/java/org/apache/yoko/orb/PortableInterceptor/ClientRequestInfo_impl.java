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

final public class ClientRequestInfo_impl extends RequestInfo_impl implements
        org.omg.PortableInterceptor.ClientRequestInfo {
    //
    // Sequence of ClientRequestInterceptors to call on reply
    //
    private java.util.Vector interceptors_ = new java.util.Vector();

    //
    // The effective IOR
    //
    private org.omg.IOP.IOR IOR_;

    //
    // The original IOR
    //
    private org.omg.IOP.IOR origIOR_;

    //
    // The ProfileInfo
    //
    private org.apache.yoko.orb.OCI.ProfileInfo profileInfo_;

    //
    // Slot data for the request PICurrent
    //
    protected org.omg.CORBA.Any[] currentSlots_;

    // ------------------------------------------------------------------
    // Standard IDL to Java mapping
    // ------------------------------------------------------------------

    //
    // Returns the target object on which the current request was invoked.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public org.omg.CORBA.Object target() {
        org.apache.yoko.orb.OB.ObjectFactory factory = orbInstance_
                .getObjectFactory();
        return factory.createObject(origIOR_);
    }

    //
    // Returns the actual target object on which the current request was
    // invoked.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public org.omg.CORBA.Object effective_target() {
        org.apache.yoko.orb.OB.ObjectFactory factory = orbInstance_
                .getObjectFactory();
        return factory.createObject(IOR_);
    }

    //
    // Returns the profile that will be used to send this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public org.omg.IOP.TaggedProfile effective_profile() {
        for (int i = 0; i < IOR_.profiles.length; i++)
            if (IOR_.profiles[i].tag == profileInfo_.id) {
                org.omg.IOP.TaggedProfile result = new org.omg.IOP.TaggedProfile();
                result.tag = IOR_.profiles[i].tag;
                result.profile_data = new byte[IOR_.profiles[i].profile_data.length];
                System.arraycopy(IOR_.profiles[i].profile_data, 0,
                        result.profile_data, 0,
                        IOR_.profiles[i].profile_data.length);
                return result;
            }

        //
        // This shouldn't happen
        //
        org.apache.yoko.orb.OB.Assert._OB_assert(false);
        return null;
    }

    //
    // If the result of the invocation is an exception this is the result.
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: no
    // receive_exception: yes receive_other: no
    //
    public org.omg.CORBA.Any received_exception() {
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
    // If the result of the invocation is an exception this the
    // respository id of the exception
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: no
    // receive_exception: yes receive_other: no
    //
    public String received_exception_id() {
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

        if (receivedId_ == null)
            receivedId_ = org.apache.yoko.orb.OB.Util
                    .getExceptionId(receivedException_);

        return receivedId_;
    }

    //
    // Return the TaggedComponent with the given ID in the effective
    // profile.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public org.omg.IOP.TaggedComponent get_effective_component(int id) {
        for (int i = 0; i < profileInfo_.components.length; i++)
            if (profileInfo_.components[i].tag == id) {
                org.omg.IOP.TaggedComponent result = new org.omg.IOP.TaggedComponent();
                result.tag = profileInfo_.components[i].tag;
                result.component_data = new byte[profileInfo_.components[i].component_data.length];
                System.arraycopy(profileInfo_.components[i].component_data, 0,
                        result.component_data, 0,
                        profileInfo_.components[i].component_data.length);
                return result;
            }

        throw new org.omg.CORBA.BAD_PARAM(
                org.apache.yoko.orb.OB.MinorCodes
                        .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidComponentId)
                        + ": " + id,
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidComponentId,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    //
    // Return all TaggedComponents with the given ID in all profiles.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public org.omg.IOP.TaggedComponent[] get_effective_components(int id) {
        throw new org.omg.CORBA.NO_IMPLEMENT(); // TODO: implement this
    }

    //
    // Returns the policy of the given type in effect for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public org.omg.CORBA.Policy get_request_policy(int type) {
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
                        .describeInvPolicy(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPolicyType),
                org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPolicyType,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    //
    // Add a service context for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: no
    // receive_exception: no receive_other: no
    //
    public void add_request_service_context(org.omg.IOP.ServiceContext sc,
            boolean addReplace) {
        //
        // This isn't valid in any call other than send_request (note that
        // send_poll isn't currently implemented)
        //
        if (status_ >= 0)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall),
                    org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        addServiceContext(requestSCL_, sc, addReplace);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    //
    // No arguments
    //
    public ClientRequestInfo_impl(org.omg.CORBA.ORB orb, int id, String op,
            boolean responseExpected, org.omg.IOP.IOR IOR,
            org.omg.IOP.IOR origIOR,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.omg.CORBA.Policy[] policies, java.util.Vector requestSCL,
            java.util.Vector replySCL,
            org.apache.yoko.orb.OB.ORBInstance orbInstance, Current_impl current) {
        super(orb, id, op, responseExpected, requestSCL, replySCL, orbInstance,
                policies, current);

        IOR_ = IOR;
        origIOR_ = origIOR;
        profileInfo_ = profileInfo;

        argStrategy_ = new ArgumentStrategyNull(orb);
        status_ = NO_REPLY;
        currentSlots_ = current_._OB_newSlotTable();
    }

    //
    // DII style arguments
    //
    public ClientRequestInfo_impl(org.omg.CORBA.ORB orb, int id, String op,
            boolean responseExpected, org.omg.IOP.IOR IOR,
            org.omg.IOP.IOR origIOR,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.omg.CORBA.Policy[] policies, java.util.Vector requestSCL,
            java.util.Vector replySCL,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            Current_impl current, org.omg.CORBA.NVList args,
            org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList exceptions) {
        super(orb, id, op, responseExpected, requestSCL, replySCL, orbInstance,
                policies, current);

        IOR_ = IOR;
        origIOR_ = origIOR;
        profileInfo_ = profileInfo;

        argStrategy_ = new ArgumentStrategyDII(orb, args, result, exceptions);
        status_ = NO_REPLY;
        currentSlots_ = current_._OB_newSlotTable();
    }

    //
    // SII style arguments
    //
    public ClientRequestInfo_impl(org.omg.CORBA.ORB orb, int id, String op,
            boolean responseExpected, org.omg.IOP.IOR IOR,
            org.omg.IOP.IOR origIOR,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.omg.CORBA.Policy[] policies, java.util.Vector requestSCL,
            java.util.Vector replySCL,
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            Current_impl current,
            org.apache.yoko.orb.OB.ParameterDesc[] argDesc,
            org.apache.yoko.orb.OB.ParameterDesc retDesc,
            org.omg.CORBA.TypeCode[] exceptionTC) {
        super(orb, id, op, responseExpected, requestSCL, replySCL, orbInstance,
                policies, current);

        IOR_ = IOR;
        origIOR_ = origIOR;
        profileInfo_ = profileInfo;

        argStrategy_ = new ArgumentStrategySII(orb, argDesc, retDesc,
                exceptionTC);
        status_ = NO_REPLY;
        currentSlots_ = current_._OB_newSlotTable();
    }

    public void _OB_request(java.util.Vector interceptors)
            throws org.apache.yoko.orb.OB.LocationForward {
        //
        // The PICurrent needs a new set of slot data
        //
        slots_ = current_._OB_currentSlotData();
        popCurrent_ = true;
        current_._OB_pushSlotData(currentSlots_);

        //
        // The result not available, arguments and exceptions are
        // available
        //
        argStrategy_.setResultAvail(false);
        argStrategy_.setArgsAvail(true);
        argStrategy_.setExceptAvail(true);

        java.util.Enumeration e = interceptors.elements();
        while (e.hasMoreElements()) {
            org.omg.PortableInterceptor.ClientRequestInterceptor interceptor = (org.omg.PortableInterceptor.ClientRequestInterceptor) e
                    .nextElement();
            try {
                interceptor.send_request(this);
                interceptors_.addElement(interceptor);
            } catch (org.omg.CORBA.SystemException ex) {
                status_ = org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value;
                receivedException_ = ex;
                _OB_reply();
            } catch (org.omg.PortableInterceptor.ForwardRequest ex) {
                status_ = org.omg.PortableInterceptor.LOCATION_FORWARD.value;
                org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) ex.forward)
                        ._get_delegate());
                forwardReference_ = p._OB_IOR();
                _OB_reply();
            }
        }

        if (popCurrent_) {
            popCurrent_ = false;
            current_._OB_popSlotData();
        }
    }

    public void _OB_reply() throws org.apache.yoko.orb.OB.LocationForward {
        if (!popCurrent_) {
            popCurrent_ = true;
            current_._OB_pushSlotData(currentSlots_);
        }

        int curr = interceptors_.size() - 1;
        while (!interceptors_.isEmpty()) {
            try {
                org.omg.PortableInterceptor.ClientRequestInterceptor i = (org.omg.PortableInterceptor.ClientRequestInterceptor) interceptors_
                        .elementAt(curr);

                if (status_ == org.omg.PortableInterceptor.SUCCESSFUL.value) {
                    //
                    // The result, arguments are available
                    //
                    argStrategy_.setResultAvail(true);

                    i.receive_reply(this);
                } else if (status_ == org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value
                        || status_ == org.omg.PortableInterceptor.USER_EXCEPTION.value) {
                    //
                    // The result, arguments not available
                    //
                    argStrategy_.setResultAvail(false);
                    argStrategy_.setArgsAvail(false);

                    i.receive_exception(this);
                } else {
                    //
                    // The result, arguments not available
                    //
                    argStrategy_.setResultAvail(false);
                    argStrategy_.setArgsAvail(false);

                    i.receive_other(this);
                }
            } catch (org.omg.CORBA.SystemException ex) {
                status_ = org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value;
                receivedException_ = ex;
                receivedId_ = null;
            } catch (org.omg.PortableInterceptor.ForwardRequest ex) {
                status_ = org.omg.PortableInterceptor.LOCATION_FORWARD.value;
                org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) ex.forward)
                        ._get_delegate());
                forwardReference_ = p._OB_IOR();
            }
            interceptors_.removeElementAt(curr);
            --curr;
        }

        //
        // If a set of slots was provided to the
        // PortableInterceptor::Current implementation then the slots have
        // to be popped
        //
        if (popCurrent_) {
            popCurrent_ = false;
            current_._OB_popSlotData();
        }

        //
        // Raise the appropriate exception, if necessary. Can't use a
        // switch statement -- the values are not integer constants
        //
        if (status_ == org.omg.PortableInterceptor.SYSTEM_EXCEPTION.value)
            throw (org.omg.CORBA.SystemException) receivedException_;
        if (status_ == org.omg.PortableInterceptor.LOCATION_FORWARD.value)
            throw new org.apache.yoko.orb.OB.LocationForward(forwardReference_,
                    false);
    }
}

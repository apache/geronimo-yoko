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
import org.apache.yoko.orb.OB.ObjectFactory;
import org.apache.yoko.orb.OB.ParameterDesc;
import org.apache.yoko.orb.OB.Util;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.util.cmsf.CmsfThreadLocal;
import org.apache.yoko.util.cmsf.CmsfThreadLocal.CmsfOverride;
import org.apache.yoko.util.yasf.YasfThreadLocal;
import org.apache.yoko.util.yasf.YasfThreadLocal.YasfOverride;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.IOP.IOR;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;
import org.omg.PortableInterceptor.ClientRequestInfo;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.omg.PortableInterceptor.SUCCESSFUL;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import java.util.Enumeration;
import java.util.Vector;

import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidComponentId;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPolicyType;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadInvOrder;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadParam;
import static org.apache.yoko.orb.OB.MinorCodes.describeInvPolicy;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final public class ClientRequestInfo_impl extends RequestInfo_impl implements ClientRequestInfo {
    //
    // Sequence of ClientRequestInterceptors to call on reply
    //
    private Vector interceptors_ = new Vector();

    //
    // The effective IOR
    //
    private IOR IOR_;

    //
    // The original IOR
    //
    private IOR origIOR_;

    private ProfileInfo profileInfo_;

    //
    // Slot data for the request PICurrent
    //
    protected Any[] currentSlots_;

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
        ObjectFactory factory = orbInstance_.getObjectFactory();
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
        ObjectFactory factory = orbInstance_.getObjectFactory();
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
    public TaggedProfile effective_profile() {
        for (int i = 0; i < IOR_.profiles.length; i++) {
            if (IOR_.profiles[i].tag == profileInfo_.id) {
                TaggedProfile result = new TaggedProfile();
                result.tag = IOR_.profiles[i].tag;
                result.profile_data = new byte[IOR_.profiles[i].profile_data.length];
                System.arraycopy(IOR_.profiles[i].profile_data, 0, result.profile_data, 0, IOR_.profiles[i].profile_data.length);
                return result;
            }
        }
        //
        // This shouldn't happen
        //
        Assert._OB_assert(false);
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
    public Any received_exception() {
        //
        // If status is not SYSTEM_EXCEPTION or USER_EXCEPTION then this
        // is a BAD_INV_ORDER exception
        //
        if (status_ != SYSTEM_EXCEPTION.value && status_ != USER_EXCEPTION.value)
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);

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
        if (status_ != SYSTEM_EXCEPTION.value && status_ != USER_EXCEPTION.value)
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);

        if (receivedId_ == null)
            receivedId_ = Util.getExceptionId(receivedException_);

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
    public TaggedComponent get_effective_component(int id) {
        for (int i = 0; i < profileInfo_.components.length; i++)
            if (profileInfo_.components[i].tag == id) {
                TaggedComponent result = new TaggedComponent();
                result.tag = profileInfo_.components[i].tag;
                result.component_data = new byte[profileInfo_.components[i].component_data.length];
                System.arraycopy(profileInfo_.components[i].component_data, 0,
                        result.component_data, 0,
                        profileInfo_.components[i].component_data.length);
                return result;
            }

        throw new BAD_PARAM(describeBadParam(MinorInvalidComponentId) + ": " + id, MinorInvalidComponentId, COMPLETED_NO);
    }

    //
    // Return all TaggedComponents with the given ID in all profiles.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public TaggedComponent[] get_effective_components(int id) {
        throw new NO_IMPLEMENT(); // TODO: implement this
    }

    //
    // Returns the policy of the given type in effect for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    //
    public Policy get_request_policy(int type) {
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

        throw new INV_POLICY(describeInvPolicy(MinorInvalidPolicyType), MinorInvalidPolicyType, COMPLETED_NO);
    }

    //
    // Add a service context for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: no
    // receive_exception: no receive_other: no
    //
    public void add_request_service_context(ServiceContext sc, boolean addReplace) {
        //
        // This isn't valid in any call other than send_request (note that
        // send_poll isn't currently implemented)
        //
        if (status_ >= 0)
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);

        addServiceContext(requestSCL_, sc, addReplace);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    //
    // No arguments
    //
    public ClientRequestInfo_impl(ORB orb, int id, String op,
                                  boolean responseExpected, IOR IOR,
                                  IOR origIOR,
                                  ProfileInfo profileInfo,
                                  Policy[] policies,
                                  Vector requestSCL,
                                  Vector replySCL,
                                  ORBInstance orbInstance, Current_impl current) {
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
    public ClientRequestInfo_impl(ORB orb, int id, String op,
                                  boolean responseExpected, IOR IOR,
                                  IOR origIOR,
                                  ProfileInfo profileInfo,
                                  Policy[] policies,
                                  Vector requestSCL,
                                  Vector replySCL,
                                  ORBInstance orbInstance,
                                  Current_impl current, NVList args,
                                  NamedValue result,
                                  ExceptionList exceptions) {
        super(orb, id, op, responseExpected, requestSCL, replySCL, orbInstance, policies, current);

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
    public ClientRequestInfo_impl(ORB orb, int id, String op,
                                  boolean responseExpected, IOR IOR,
                                  IOR origIOR,
                                  ProfileInfo profileInfo,
                                  Policy[] policies, Vector requestSCL,
                                  Vector replySCL,
                                  ORBInstance orbInstance,
                                  Current_impl current,
                                  ParameterDesc[] argDesc,
                                  ParameterDesc retDesc,
                                  TypeCode[] exceptionTC) {
        super(orb, id, op, responseExpected, requestSCL, replySCL, orbInstance, policies, current);

        IOR_ = IOR;
        origIOR_ = origIOR;
        profileInfo_ = profileInfo;

        argStrategy_ = new ArgumentStrategySII(orb, argDesc, retDesc, exceptionTC);
        status_ = NO_REPLY;
        currentSlots_ = current_._OB_newSlotTable();
    }

    public void _OB_request(Vector interceptors) throws LocationForward {
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

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            Enumeration e = interceptors.elements();
            while (e.hasMoreElements()) {
                ClientRequestInterceptor interceptor = (ClientRequestInterceptor) e.nextElement();
                try {
                    interceptor.send_request(this);
                    interceptors_.addElement(interceptor);
                } catch (SystemException ex) {
                    status_ = SYSTEM_EXCEPTION.value;
                    receivedException_ = ex;
                    _OB_reply();
                } catch (ForwardRequest ex) {
                    status_ = LOCATION_FORWARD.value;
                    Delegate p = (Delegate) (((ObjectImpl) ex.forward)
                            ._get_delegate());
                    forwardReference_ = p._OB_IOR();
                    _OB_reply();
                }
            }
        }

        if (popCurrent_) {
            popCurrent_ = false;
            current_._OB_popSlotData();
        }
    }

    public void _OB_reply() throws LocationForward {
        if (!popCurrent_) {
            popCurrent_ = true;
            current_._OB_pushSlotData(currentSlots_);
        }

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            int curr = interceptors_.size() - 1;
            while (!interceptors_.isEmpty()) {
                try {
                    ClientRequestInterceptor i = (ClientRequestInterceptor) interceptors_.elementAt(curr);

                    if (status_ == SUCCESSFUL.value) {
                        //
                        // The result, arguments are available
                        //
                        argStrategy_.setResultAvail(true);

                        i.receive_reply(this);
                    } else if (status_ == SYSTEM_EXCEPTION.value
                            || status_ == USER_EXCEPTION.value) {
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
                } catch (SystemException ex) {
                    status_ = SYSTEM_EXCEPTION.value;
                    receivedException_ = ex;
                    receivedId_ = null;
                } catch (ForwardRequest ex) {
                    status_ = LOCATION_FORWARD.value;
                    Delegate p = (Delegate) (((ObjectImpl) ex.forward)
                            ._get_delegate());
                    forwardReference_ = p._OB_IOR();
                }
                interceptors_.removeElementAt(curr);
                --curr;
            }
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
        if (status_ == SYSTEM_EXCEPTION.value)
            throw (SystemException) receivedException_;
        if (status_ == LOCATION_FORWARD.value)
            throw new LocationForward(forwardReference_,
                    false);
    }
}

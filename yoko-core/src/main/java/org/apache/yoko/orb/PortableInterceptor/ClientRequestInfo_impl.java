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
import org.apache.yoko.orb.OB.LocationForward;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.ObjectFactory;
import org.apache.yoko.orb.OB.PIDowncall;
import org.apache.yoko.orb.OB.Util;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.util.CollectionExtras;
import org.apache.yoko.util.cmsf.CmsfThreadLocal;
import org.apache.yoko.util.cmsf.CmsfThreadLocal.CmsfOverride;
import org.apache.yoko.util.yasf.YasfThreadLocal;
import org.apache.yoko.util.yasf.YasfThreadLocal.YasfOverride;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
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

import java.util.List;

import static org.apache.yoko.orb.OB.Assert._OB_assert;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidComponentId;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPICall;
import static org.apache.yoko.orb.OB.MinorCodes.MinorInvalidPolicyType;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadInvOrder;
import static org.apache.yoko.orb.OB.MinorCodes.describeBadParam;
import static org.apache.yoko.orb.OB.MinorCodes.describeInvPolicy;
import static org.apache.yoko.util.CollectionExtras.newSynchronizedList;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final public class ClientRequestInfo_impl extends RequestInfo_impl implements ClientRequestInfo {
    private final List<ClientRequestInterceptor> interceptors = newSynchronizedList();
    private final IOR effectiveIor;
    private final IOR originalIor;
    private final ProfileInfo profileInfo;
    /**
     * <em>This is really complicated! </em>
     * <br>
     * These are the new thread-context slots for the PI Current for this request's interception points.
     * <br>
     * <strong>
     *     The spec actually says <strong>each</strong> interception point should have its own
     *     context. That would mean we do not need this field as we never need to retrieve or reuse
     *     the thread-scope context for an interception point.
     *     TODO: try fixing this?
     * </strong> (See CORBA 3.0.3 21.4.4.6 paragraph 3)
     */
    private final Any[] newThreadScopePICurrentSlotData;

    // Returns the target object on which the current request was invoked.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    public org.omg.CORBA.Object target() {
        ObjectFactory factory = orbInstance.getObjectFactory();
        return factory.createObject(originalIor);
    }

    // Returns the actual target object on which the current request was
    // invoked.
    //
    // Client side:
    //
    // send_request: yes send_poll: yes receive_reply: yes
    // receive_exception: yes receive_other: yes
    public org.omg.CORBA.Object effective_target() {
        ObjectFactory factory = orbInstance.getObjectFactory();
        return factory.createObject(effectiveIor);
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
        for (final TaggedProfile profile : effectiveIor.profiles) {
            if (profile.tag == profileInfo.id) {
                TaggedProfile result = new TaggedProfile();
                result.tag = profile.tag;
                result.profile_data = new byte[profile.profile_data.length];
                System.arraycopy(profile.profile_data, 0, result.profile_data, 0, profile.profile_data.length);
                return result;
            }
        }
        throw _OB_assert("There should have been a tagged profile matching the profile info.");
    }

    // If the result of the invocation is an exception this is the result.
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: no
    // receive_exception: yes receive_other: no
    public Any received_exception() {
        //
        // If status is not SYSTEM_EXCEPTION or USER_EXCEPTION then this
        // is a BAD_INV_ORDER exception
        //
        if (replyStatus != SYSTEM_EXCEPTION.value && replyStatus != USER_EXCEPTION.value)
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);

        //
        // UnknownUserException? Extract the contained UserException.
        //
        UnknownUserException unk = null;
        try {
            unk = (UnknownUserException) receivedException;
        } catch (ClassCastException ignored) {
        }
        if (unk != null)
            return unk.except;

        Any any = orb.create_any();
        Util.insertException(any, receivedException);

        return any;
    }

    // If the result of the invocation is an exception this the
    // repository id of the exception
    //
    // Client side:
    //
    // send_request: no send_poll: no receive_reply: no
    // receive_exception: yes receive_other: no
    public String received_exception_id() {
        //
        // If status is not SYSTEM_EXCEPTION or USER_EXCEPTION then this
        // is a BAD_INV_ORDER exception
        //
        if (replyStatus != SYSTEM_EXCEPTION.value && replyStatus != USER_EXCEPTION.value)
            throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);

        if (receivedId == null)
            receivedId = Util.getExceptionId(receivedException);

        return receivedId;
    }

    // Return the TaggedComponent with the given ID in the effective
    // profile.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    public TaggedComponent get_effective_component(int id) {
        for (int i = 0; i < profileInfo.components.length; i++)
            if (profileInfo.components[i].tag == id) {
                TaggedComponent result = new TaggedComponent();
                result.tag = profileInfo.components[i].tag;
                result.component_data = new byte[profileInfo.components[i].component_data.length];
                System.arraycopy(profileInfo.components[i].component_data, 0,
                        result.component_data, 0,
                        profileInfo.components[i].component_data.length);
                return result;
            }

        throw new BAD_PARAM(describeBadParam(MinorInvalidComponentId) + ": " + id, MinorInvalidComponentId, COMPLETED_NO);
    }

    // Return all TaggedComponents with the given ID in all profiles.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    public TaggedComponent[] get_effective_components(int id) {
        throw new NO_IMPLEMENT(); // TODO: implement this
    }

    // Returns the policy of the given type in effect for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: yes
    // receive_exception: yes receive_other: yes
    public Policy get_request_policy(int type) {
        for (Policy policy : policies) {
            if (policy.policy_type() == type)return policy;
        }

        // if the target policy was not in the current policy list, check to see
        // if the type has even been registered.  If it is valid, return null
        // to indicate we ain't got one.
        if (orbInstance.getPolicyFactoryManager().isPolicyRegistered(type)) {
            return null;
        }

        throw new INV_POLICY(describeInvPolicy(MinorInvalidPolicyType), MinorInvalidPolicyType, COMPLETED_NO);
    }

    // Add a service context for this request.
    //
    // Client side:
    //
    // send_request: yes send_poll: no receive_reply: no
    // receive_exception: no receive_other: no
    public void add_request_service_context(ServiceContext sc, boolean addReplace) {
        // This isn't valid in any call other than send_request (note that send_poll isn't currently implemented)
        if (replyStatus >= 0) throw new BAD_INV_ORDER(describeBadInvOrder(MinorInvalidPICall), MinorInvalidPICall, COMPLETED_NO);

        requestContexts.mutable().add(sc, addReplace);
    }

    public ClientRequestInfo_impl(ORB orb, ORBInstance orbInstance, Current_impl current, PIDowncall dc) {
        super(orb, orbInstance, current, dc);
        this.effectiveIor = dc.effectiveIor;
        this.originalIor = dc.originalIor;
        this.profileInfo = dc.profileInfo();
        this.newThreadScopePICurrentSlotData = piCurrent._OB_newSlotTable();
        this.replyStatus = NO_REPLY;
        this.argStrategy = dc.createArgumentStrategy(orb);
    }


    public void _OB_request(List<ClientRequestInterceptor> interceptors) throws LocationForward {
        // The PICurrent needs a new set of slot data
        requestSlotData = piCurrent._OB_currentSlotData();
        currentNeedsPopping = true;
        piCurrent._OB_pushSlotData(newThreadScopePICurrentSlotData);

        // result not available, arguments and exceptions available
        argStrategy.setResultAvail(false);
        argStrategy.setArgsAvail(true);
        argStrategy.setExceptAvail(true);

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            for(ClientRequestInterceptor interceptor: interceptors) {
                try {
                    interceptor.send_request(this);
                    this.interceptors.add(interceptor);
                } catch (SystemException ex) {
                    replyStatus = SYSTEM_EXCEPTION.value;
                    receivedException = ex;
                    _OB_reply();
                } catch (ForwardRequest ex) {
                    replyStatus = LOCATION_FORWARD.value;
                    Delegate p = (Delegate) (((ObjectImpl) ex.forward)._get_delegate());
                    forwardReference = p._OB_IOR();
                    _OB_reply();
                }
            }
        }

        popCurrent();
    }

    public void _OB_reply() throws LocationForward {
        if (!currentNeedsPopping) {
            currentNeedsPopping = true;
            piCurrent._OB_pushSlotData(newThreadScopePICurrentSlotData);
        }

        try (CmsfOverride cmsfo = CmsfThreadLocal.override();
             YasfOverride yasfo = YasfThreadLocal.override()) {
            for (ClientRequestInterceptor i: CollectionExtras.removeInReverse(interceptors)) {
                try {
                    switch (replyStatus) {
                    case SUCCESSFUL.value:
                        // result, arguments are available
                        argStrategy.setResultAvail(true);
                        i.receive_reply(this);
                        break;
                    case SYSTEM_EXCEPTION.value:
                    case USER_EXCEPTION.value:
                        // result, arguments not available
                        argStrategy.setResultAvail(false);
                        argStrategy.setArgsAvail(false);
                        i.receive_exception(this);
                        break;
                    default:
                        // result, arguments not available
                        argStrategy.setResultAvail(false);
                        argStrategy.setArgsAvail(false);
                        i.receive_other(this);
                        break;
                    }
                } catch (SystemException ex) {
                    replyStatus = SYSTEM_EXCEPTION.value;
                    receivedException = ex;
                    receivedId = null;
                } catch (ForwardRequest ex) {
                    replyStatus = LOCATION_FORWARD.value;
                    Delegate p = (Delegate) (((ObjectImpl) ex.forward)._get_delegate());
                    forwardReference = p._OB_IOR();
                }
            }
        }

        popCurrent();

        // Raise the appropriate exception, if necessary.
        switch (replyStatus) {
        case SYSTEM_EXCEPTION.value: throw (SystemException)receivedException;
        case LOCATION_FORWARD.value: throw new LocationForward(forwardReference, false);
        }
    }
}

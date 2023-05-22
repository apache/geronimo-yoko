/*
 * Copyright 2019 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.Messaging.MaxHopsPolicy_impl;
import org.apache.yoko.orb.Messaging.QueueOrderPolicy_impl;
import org.apache.yoko.orb.Messaging.RebindPolicy_impl;
import org.apache.yoko.orb.Messaging.ReplyEndTimePolicy_impl;
import org.apache.yoko.orb.Messaging.ReplyPriorityPolicy_impl;
import org.apache.yoko.orb.Messaging.ReplyStartTimePolicy_impl;
import org.apache.yoko.orb.Messaging.RequestEndTimePolicy_impl;
import org.apache.yoko.orb.Messaging.RequestPriorityPolicy_impl;
import org.apache.yoko.orb.Messaging.RequestStartTimePolicy_impl;
import org.apache.yoko.orb.Messaging.RoutingPolicy_impl;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSIENT;
import org.omg.IOP.TaggedComponent;
import org.omg.MessageRouting.PersistentRequestRouter;
import org.omg.MessageRouting.PersistentRequestRouterHelper;
import org.omg.MessageRouting.Router;
import org.omg.MessageRouting.RouterHelper;
import org.omg.MessageRouting.RouterListHelper;
import org.omg.MessageRouting.RouterListHolder;
import org.omg.MessageRouting.TAG_MESSAGE_ROUTERS;
import org.omg.Messaging.MAX_HOPS_POLICY_TYPE;
import org.omg.Messaging.MaxHopsPolicy;
import org.omg.Messaging.MaxHopsPolicyHelper;
import org.omg.Messaging.OrderingHelper;
import org.omg.Messaging.PolicyValue;
import org.omg.Messaging.PolicyValueSeqHolder;
import org.omg.Messaging.PriorityRange;
import org.omg.Messaging.PriorityRangeHelper;
import org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE;
import org.omg.Messaging.QueueOrderPolicy;
import org.omg.Messaging.QueueOrderPolicyHelper;
import org.omg.Messaging.REBIND_POLICY_TYPE;
import org.omg.Messaging.RELATIVE_REQ_TIMEOUT_POLICY_TYPE;
import org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE;
import org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE;
import org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE;
import org.omg.Messaging.REPLY_START_TIME_POLICY_TYPE;
import org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE;
import org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE;
import org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE;
import org.omg.Messaging.ROUTING_POLICY_TYPE;
import org.omg.Messaging.RebindModeHelper;
import org.omg.Messaging.RebindPolicy;
import org.omg.Messaging.RebindPolicyHelper;
import org.omg.Messaging.RelativeRequestTimeoutPolicy;
import org.omg.Messaging.RelativeRequestTimeoutPolicyHelper;
import org.omg.Messaging.RelativeRoundtripTimeoutPolicy;
import org.omg.Messaging.RelativeRoundtripTimeoutPolicyHelper;
import org.omg.Messaging.ReplyPriorityPolicy;
import org.omg.Messaging.ReplyPriorityPolicyHelper;
import org.omg.Messaging.ReplyStartTimePolicy;
import org.omg.Messaging.ReplyStartTimePolicyHelper;
import org.omg.Messaging.RequestEndTimePolicy;
import org.omg.Messaging.RequestEndTimePolicyHelper;
import org.omg.Messaging.RequestPriorityPolicy;
import org.omg.Messaging.RequestPriorityPolicyHelper;
import org.omg.Messaging.RequestStartTimePolicy;
import org.omg.Messaging.RequestStartTimePolicyHelper;
import org.omg.Messaging.RoutingPolicy;
import org.omg.Messaging.RoutingPolicyHelper;
import org.omg.Messaging.RoutingTypeRange;
import org.omg.Messaging.RoutingTypeRangeHelper;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.TimeBase.UtcT;
import org.omg.TimeBase.UtcTHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.util.Arrays.copyOf;
import static java.util.Arrays.sort;

final public class MessageRoutingUtil {
    public static void getRouterListFromConfig(ORBInstance orbInstance, RouterListHolder routerList) {
        Properties properties = orbInstance.getProperties();

        List<String> amiPropKeys = new ArrayList<>();
        // Get all of the AMI router keys.
        for (Object obj : properties.keySet()) {
            String key = (String) obj;
            if (key.startsWith("yoko.ami.router.")) amiPropKeys.add(key);
        }
        // Sort the keys - not an efficient sort but ok as lists are small
        String[] routerKeys = (String[]) amiPropKeys.toArray(new String[0]);
        sort(routerKeys);

        for (String routerKey : routerKeys) {
            String value = properties.getProperty(routerKey);
            // Strip any quotes that might be around the key value
            if (value.startsWith("\"")) value = value.substring(1, value.length() - 1);
            // Create the object depending on how the router address was
            // given in the config file. Either we can use the reference
            // as specified or use the data to create a corbaloc
            ORB orb = orbInstance.getORB();
            org.omg.CORBA.Object obj;
            try {
                if (value.startsWith("corbaloc:")
                        || value.startsWith("corbaname:")
                        || value.startsWith("IOR:")
                        || value.startsWith("relfile:")
                        || value.startsWith("file:")) {
                    obj = orb.string_to_object(value);
                } else {
                    // REVISIT: For now, we expect this to be in a
                    // <host>:<port> format that we can use to create a
                    // corbaloc address
                    String address = "corbaloc::" + value + "/AMIRouter";

                    obj = orb.string_to_object(address);
                }
            } catch (BAD_PARAM ex) {
                continue;
            }
            // NOTE: we don't want to do a _narrow here because the router may
            // not be active and we would get an exception. Do an
            // _uncheck_narrow so that we can create the object to store in
            // the IOR without having to be worried about the router being
            // active.
            Router router = RouterHelper.unchecked_narrow(obj);
            // Add the new router to the supplied list of routers
            final int oldLen = routerList.value.length + 1;
            routerList.value = copyOf(routerList.value, oldLen);
            routerList.value[oldLen] = router;
        }
    }

    public static PersistentRequestRouter getPersistentRouterFromConfig(ORBInstance orbInstance) throws SystemException {
        Properties properties = orbInstance.getProperties();
        String key = "yoko.ami.persistent_router";
        String value = properties.getProperty(key);
        // Strip any quotes that might be around the key value
        if (value.startsWith("\""))
            value = value.substring(1, value.length() - 1);

        // Create the object depending on how the router address was
        // given in the config file. Either we can use the reference
        // as specified or use the data to create a corbaloc
        ORB orb = orbInstance.getORB();
        org.omg.CORBA.Object obj;

        if (value.startsWith("corbaloc:") || value.startsWith("corbaname:")
                || value.startsWith("IOR:") || value.startsWith("relfile:")
                || value.startsWith("file:")) {
            obj = orb.string_to_object(value);
        } else {
            // TODO: For now, we expect this to be in a
            // <host>:<port> format that we can use to create a
            // corbaloc address
            String address = "corbaloc::" + value + "/AMIPersistentRouter";

            obj = orb.string_to_object(address);
        }
        // The persistent request router must be active in order to even
        // attempt polling requests so we can safely assume that we can
        // perform a narrow. Otherwise, we do want to cause an exception
        // to occur.
        PersistentRequestRouter router;
        try {
            router = PersistentRequestRouterHelper
                    .narrow(obj);
        } catch (SystemException ex) {
            throw (TRANSIENT)new TRANSIENT(
                    "PersistentRequestRouter not available").initCause(ex);
        }

        return router;
    }

    public static void getRouterListFromComponents(ORBInstance orbInstance, ProfileInfo info, RouterListHolder routerList) {
        // 1.0 profiles do not have tagged components
        if (info.major == 1 && info.minor == 0) return;

        for (final TaggedComponent component : info.components) {
            if (component.tag == TAG_MESSAGE_ROUTERS.value) {
                InputStream in = new InputStream(component.component_data);
                in._OB_readEndian();
                // Needed to create Objects
                in._OB_ORBInstance(orbInstance);
                // Read the router list from the components
                Router[] routers = RouterListHelper.read(in);
                // Add the new routers to the supplied list of routers
                int routerLen = routers.length;
                int currentLen = routerList.value.length;
                Router[] newList = new Router[routerLen + currentLen];
                System.arraycopy(routerList.value, 0, newList, 0, currentLen);
                System.arraycopy(routers, 0, newList, currentLen, routerLen);
                routerList.value = newList;
            }
        }
    }

    private static PolicyValue createMessagingPolicyValue(Policy policy) {
        PolicyValue value = new PolicyValue();
        // Create a PolicyValue based on the type of policy that was passed
        // to this method
        switch (policy.policy_type()) {

        case REBIND_POLICY_TYPE.value: {
            RebindPolicy p = RebindPolicyHelper.narrow(policy);

            short mode = p.rebind_mode();

            value.ptype = REBIND_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                RebindModeHelper.write(out, mode);
                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }

        case REQUEST_PRIORITY_POLICY_TYPE.value: {
            RequestPriorityPolicy p = RequestPriorityPolicyHelper.narrow(policy);

            PriorityRange range = p.priority_range();

            value.ptype = REQUEST_PRIORITY_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                PriorityRangeHelper.write(out, range);
                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case REPLY_PRIORITY_POLICY_TYPE.value: {
            ReplyPriorityPolicy p = ReplyPriorityPolicyHelper.narrow(policy);

            PriorityRange range = p.priority_range();

            value.ptype = REPLY_PRIORITY_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                PriorityRangeHelper.write(out, range);
                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case REQUEST_START_TIME_POLICY_TYPE.value: {
            RequestStartTimePolicy p = RequestStartTimePolicyHelper.narrow(policy);

            UtcT time = p.start_time();

            value.ptype = REQUEST_START_TIME_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                UtcTHelper.write(out, time);
                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case REQUEST_END_TIME_POLICY_TYPE.value: {
            RequestEndTimePolicy p = RequestEndTimePolicyHelper.narrow(policy);

            UtcT time = p.end_time();

            value.ptype = REQUEST_END_TIME_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                UtcTHelper.write(out, time);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case REPLY_START_TIME_POLICY_TYPE.value: {
            ReplyStartTimePolicy p = ReplyStartTimePolicyHelper.narrow(policy);

            UtcT time = p.start_time();

            value.ptype = REPLY_START_TIME_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                UtcTHelper.write(out, time);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case REPLY_END_TIME_POLICY_TYPE.value: {
            RequestEndTimePolicy p = RequestEndTimePolicyHelper.narrow(policy);

            UtcT time = p.end_time();

            value.ptype = REPLY_END_TIME_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                UtcTHelper.write(out, time);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case RELATIVE_REQ_TIMEOUT_POLICY_TYPE.value: {
            RelativeRequestTimeoutPolicy p = RelativeRequestTimeoutPolicyHelper.narrow(policy);

            long time = p.relative_expiry();
            UtcT timeout = TimeHelper.add(TimeHelper.utcNow(0), time);

            value.ptype = REQUEST_END_TIME_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                UtcTHelper.write(out, timeout);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case RELATIVE_RT_TIMEOUT_POLICY_TYPE.value: {
            RelativeRoundtripTimeoutPolicy p = RelativeRoundtripTimeoutPolicyHelper.narrow(policy);

            long time = p.relative_expiry();
            UtcT timeout = TimeHelper.add(TimeHelper.utcNow(0), time);

            value.ptype = REPLY_END_TIME_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                UtcTHelper.write(out, timeout);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case ROUTING_POLICY_TYPE.value: {
            RoutingPolicy p = RoutingPolicyHelper.narrow(policy);

            RoutingTypeRange range = p.routing_range();

            value.ptype = ROUTING_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                RoutingTypeRangeHelper.write(out, range);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case MAX_HOPS_POLICY_TYPE.value: {
            MaxHopsPolicy p = MaxHopsPolicyHelper.narrow(policy);

            short hops = p.max_hops();

            value.ptype = MAX_HOPS_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                out.write_ushort(hops);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        case QUEUE_ORDER_POLICY_TYPE.value: {
            QueueOrderPolicy p = QueueOrderPolicyHelper.narrow(policy);

            short order = p.allowed_orders();

            value.ptype = QUEUE_ORDER_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                OrderingHelper.write(out, order);

                value.pvalue = out.copyWrittenBytes();
            }
            break;
        }
        default: {
            throw new INV_POLICY();
        }

        } // End of switch

        return value;
    }

    public static void getComponentPolicyValues(IORInfo info, PolicyValueSeqHolder policies) {
        // Retrieve the value for the REQUEST_PRIORITY_POLICY_TYPE
        try {
            Policy p = info.get_effective_policy(REQUEST_PRIORITY_POLICY_TYPE.value);

            PolicyValue value = createMessagingPolicyValue(p);
            // Add the new policy to the supplied list of policies
            int len = policies.value.length;
            PolicyValue[] newSeq = new PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (INV_POLICY ignored) {

        }
        // Retrieve the value for the REPLY_PRIORITY_POLICY_TYPE
        try {
            Policy p = info.get_effective_policy(REPLY_PRIORITY_POLICY_TYPE.value);

            PolicyValue value = createMessagingPolicyValue(p);
            // Add the new policy to the supplied list of policies
            int len = policies.value.length;
            PolicyValue[] newSeq = new PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (INV_POLICY ignored) {

        }
        // Retrieve the value for the MAX_HOPS_POLICY_TYPE
        try {
            Policy p = info.get_effective_policy(MAX_HOPS_POLICY_TYPE.value);

            PolicyValue value = createMessagingPolicyValue(p);
            // Add the new policy to the supplied list of policies
            int len = policies.value.length;
            PolicyValue[] newSeq = new PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (INV_POLICY ignored) {

        }
        // Retrieve the value for the QUEUE_ORDER_POLICY_TYPE
        try {
            Policy p = info.get_effective_policy(QUEUE_ORDER_POLICY_TYPE.value);

            PolicyValue value = createMessagingPolicyValue(p);
            // Add the new policy to the supplied list of policies
            int len = policies.value.length;
            PolicyValue[] newSeq = new PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (INV_POLICY ignored) {

        }
    }

    public static void getInvocationPolicyValues(RefCountPolicyList policyList, PolicyValueSeqHolder policies) {
        // TODO: We should revisit this. The spec is not very clear on
        // whether we should propogate policies that have no default values
        // and that have not been overriden by the client. For now, we
        // assume that only default or overriden policyes will be
        // propogated in the INVOCATION_POLICIES service context.
        //
        // A set of flags that will indicate how to handle specific
        // policies
        boolean rebind = false;
        boolean queueOrder = false;
        boolean useRelativeRequest = false;
        boolean useRelativeRoundtrip = false;
        // There is a special case we need to watch out for. The two
        // relative timeouts are actually a different way to specify the
        // request and reply end times. We don't want them to overwrite
        // each other so we wil pick the earliest values if both times
        // are supplied (e.g. RelativeRequest and RequestEnd or
        // RelativeRoundtrip and ReplyEnd)
        if (policyList.relativeRequestTimeout != 0) {
            useRelativeRequest = true;
            if (TimeHelper.notEqual(
                    policyList.requestEndTime,
                    TimeHelper.utcMin())) {
                // Compare the times and pick the earliest
                UtcT timeout = TimeHelper.utcMin();
                if (TimeHelper.greaterThan(timeout,
                        policyList.requestEndTime))
                    useRelativeRequest = false;
            }
        }
        if (policyList.relativeRoundTripTimeout != 0) {
            useRelativeRoundtrip = true;
            if (TimeHelper.notEqual(
                    policyList.replyEndTime, TimeHelper.utcMin())) {
                // Compare the times and pick the earliest
                UtcT timeout = TimeHelper.utcMin();
                if (TimeHelper.greaterThan(timeout,
                        policyList.replyEndTime))
                    useRelativeRequest = false;
            }
        }
        // Go through all of the policies in the policy list and create the
        // appropriate PolicyValue
        for (final Policy policy : policyList.value) {
            try {
                int type = policy.policy_type();

                if ((type == RELATIVE_REQ_TIMEOUT_POLICY_TYPE.value && !useRelativeRequest) || (type == REQUEST_END_TIME_POLICY_TYPE.value && useRelativeRequest)) {
                    // This is to handle cases when both policies are set
                    continue;
                }
                if ((type == RELATIVE_RT_TIMEOUT_POLICY_TYPE.value && !useRelativeRoundtrip) || (type == REPLY_END_TIME_POLICY_TYPE.value && useRelativeRoundtrip)) {
                    // This is to handle cases when both policies are set
                    continue;
                }

                PolicyValue val = createMessagingPolicyValue(policy);
                // We also need to watch out for rebind and queue order.
                // If they have been specified by the client, we don't need
                // to use their defaults. Flag this so we know about it
                // later
                if (val.ptype == REBIND_POLICY_TYPE.value) rebind = true;
                if (val.ptype == QUEUE_ORDER_POLICY_TYPE.value) queueOrder = true;
                // Add the policy to the supplied list of policies
                int oldLen = policies.value.length;
                policies.value = copyOf(policies.value, oldLen + 1);
                // TODO: we seem to have missed policies.value[oldLen] = val;
            } catch (INV_POLICY ignored) {

            }
        }
        if (!rebind) {
            // We need to use the default value for the rebind policy
            PolicyValue val = new PolicyValue();

            val.ptype = REBIND_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                RebindModeHelper.write(out, policyList.rebindMode);
                val.pvalue = out.copyWrittenBytes();
            }
            // Add the rebind policy to the list of supplied policies
            int len = policies.value.length;
            PolicyValue[] newSeq = new PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = val;
            policies.value = newSeq;
        }
        if (!queueOrder) {
            // We need to use the default value for the queue order policy
            PolicyValue val = new PolicyValue();

            val.ptype = QUEUE_ORDER_POLICY_TYPE.value;

            try (OutputStream out = new OutputStream()) {
                out._OB_writeEndian();
                OrderingHelper.write(out, policyList.queueOrder);
                val.pvalue = out.copyWrittenBytes();
            }
            // Add the queue order policy to the list of supplied policies
            int len = policies.value.length;
            PolicyValue[] newSeq = new PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = val;
            policies.value = newSeq;
        }
    }

    public static Policy[] getPolicyListFromPolicyValues(PolicyValue[] policies) {
        List<Policy> list = new ArrayList<>();
        // Loop through the PolicyValueSeq and create necessary policies
        for (final PolicyValue policyValue : policies) {
            try {
                list.add(getPolicyFromPolicyValue(policyValue));
            } catch (INV_POLICY ignored) {
            }
        }
        return list.toArray(new Policy[0]);
    }

    private static Policy getPolicyFromPolicyValue(PolicyValue policyValue) {
        // Create the appropriate policy depending on the policy value we were given
        switch (policyValue.ptype) {
        case REBIND_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            short mode = RebindModeHelper.read(in);

            return new RebindPolicy_impl(mode);
        }
        case REQUEST_PRIORITY_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            PriorityRange range = PriorityRangeHelper.read(in);

            return new RequestPriorityPolicy_impl(range);
        }
        case REPLY_PRIORITY_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            PriorityRange range = PriorityRangeHelper.read(in);

            return new ReplyPriorityPolicy_impl(range);
        }
        case REQUEST_START_TIME_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            UtcT time = UtcTHelper.read(in);

            return new RequestStartTimePolicy_impl(time);
        }
        case REQUEST_END_TIME_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            UtcT time = UtcTHelper.read(in);

            return new RequestEndTimePolicy_impl(time);
        }
        case REPLY_START_TIME_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            UtcT time = UtcTHelper.read(in);

            return new ReplyStartTimePolicy_impl(time);
        }
        case REPLY_END_TIME_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            UtcT time = UtcTHelper.read(in);

            return new ReplyEndTimePolicy_impl(time);
        }
        case ROUTING_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            RoutingTypeRange range = RoutingTypeRangeHelper.read(in);

            return new RoutingPolicy_impl(range);
        }
        case MAX_HOPS_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            short hops = in.read_ushort();

            return new MaxHopsPolicy_impl(hops);
        }
        case QUEUE_ORDER_POLICY_TYPE.value: {
            InputStream in = new InputStream(policyValue.pvalue);
            in._OB_readEndian();
            short order = OrderingHelper.read(in);

            return new QueueOrderPolicy_impl(order);
        }
        default: {
            throw new INV_POLICY();
        }

        } // End of switch

        // return org.omg.CORBA.Policy._nil();
    }
}

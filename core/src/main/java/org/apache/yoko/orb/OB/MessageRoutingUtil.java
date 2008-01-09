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

package org.apache.yoko.orb.OB;

final public class MessageRoutingUtil {
    public static void getRouterListFromConfig(
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.MessageRouting.RouterListHolder routerList) {
        java.util.Properties properties = orbInstance.getProperties();

        java.util.Enumeration keys = properties.keys();
        java.util.Vector amiPropKeys = new java.util.Vector();

        //
        // Get all of the AMI router keys.
        //
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (!key.startsWith("yoko.ami.router."))
                continue;

            amiPropKeys.add(key);
        }

        //
        // Sort the keys
        //
        // NOTE: As with the C++ implementation, this is not the most
        // efficient way to sort the list. However, our lists should be
        // small enough that this sorting method is OK.
        //
        String[] routerKeys = (String[]) amiPropKeys.toArray(new String[0]);
        for (int i = 0; i < routerKeys.length; ++i) {
            int min = i;
            for (int j = i + 1; j < routerKeys.length; ++j) {
                if (routerKeys[min].compareTo(routerKeys[j]) > 0)
                    min = j;
            }
            String tmp = routerKeys[i];
            routerKeys[i] = routerKeys[min];
            routerKeys[min] = tmp;
        }

        for (int i = 0; i < routerKeys.length; ++i) {
            String value = properties.getProperty(routerKeys[i]);

            //
            // Strip any quotes that might be around the key value
            //
            if (value.startsWith("\""))
                value = value.substring(1, value.length() - 1);

            //
            // Create the object depending on how the router address was
            // given in the config file. Either we can use the reference
            // as specified or use the data to create a corbaloc
            //
            org.omg.CORBA.ORB orb = orbInstance.getORB();
            org.omg.CORBA.Object obj;
            try {
                if (value.startsWith("corbaloc:")
                        || value.startsWith("corbaname:")
                        || value.startsWith("IOR:")
                        || value.startsWith("relfile:")
                        || value.startsWith("file:")) {
                    obj = orb.string_to_object(value);
                } else {
                    //
                    // REVISIT: For now, we expect this to be in a
                    // <host>:<port> format that we can use to create a
                    // corbaloc address
                    //
                    String address = "corbaloc::" + value + "/AMIRouter";

                    obj = orb.string_to_object(address);
                }
            } catch (org.omg.CORBA.BAD_PARAM ex) {
                continue;
            }

            //
            // NOTE: we don't want to do a _narrow here because the router may
            // not be active and we would get an exception. Do an
            // _uncheck_narrow so that we can create the object to store in
            // the IOR without having to be worried about the router being
            // active.
            //
            org.omg.MessageRouting.Router router = org.omg.MessageRouting.RouterHelper
                    .unchecked_narrow(obj);

            //
            // Add the new router to the supplied list of routers
            //
            int len = routerList.value.length;
            org.omg.MessageRouting.Router[] newList = new org.omg.MessageRouting.Router[len + 1];
            System.arraycopy(routerList.value, 0, newList, 0, len);
            newList[len] = router;
            routerList.value = newList;
        }
    }

    public static org.omg.MessageRouting.PersistentRequestRouter getPersistentRouterFromConfig(
            org.apache.yoko.orb.OB.ORBInstance orbInstance)
            throws org.omg.CORBA.SystemException {
        java.util.Properties properties = orbInstance.getProperties();
        String key = "yoko.ami.persistent_router";
        String value = properties.getProperty(key);

        //
        // Strip any quotes that might be around the key value
        //
        if (value.startsWith("\""))
            value = value.substring(1, value.length() - 1);

        if (key == null)
            throw new org.omg.CORBA.BAD_PARAM("No persistent router specified");

        //
        // Create the object depending on how the router address was
        // given in the config file. Either we can use the reference
        // as specified or use the data to create a corbaloc
        //
        org.omg.CORBA.ORB orb = orbInstance.getORB();
        org.omg.CORBA.Object obj;

        if (value.startsWith("corbaloc:") || value.startsWith("corbaname:")
                || value.startsWith("IOR:") || value.startsWith("relfile:")
                || value.startsWith("file:")) {
            obj = orb.string_to_object(value);
        } else {
            //
            // TODO: For now, we expect this to be in a
            // <host>:<port> format that we can use to create a
            // corbaloc address
            //
            String address = "corbaloc::" + value + "/AMIPersistentRouter";

            obj = orb.string_to_object(address);
        }

        //
        // The persistent request router must be active in order to even
        // attempt polling requests so we can safely assume that we can
        // perform a narrow. Otherwise, we do want to cause an exception
        // to occur.
        //
        org.omg.MessageRouting.PersistentRequestRouter router;
        try {
            router = org.omg.MessageRouting.PersistentRequestRouterHelper
                    .narrow(obj);
        } catch (org.omg.CORBA.SystemException ex) {
            throw (org.omg.CORBA.TRANSIENT)new org.omg.CORBA.TRANSIENT(
                    "PersistentRequestRouter not available").initCause(ex);
        }

        return router;
    }

    public static void getRouterListFromComponents(
            org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.ProfileInfo info,
            org.omg.MessageRouting.RouterListHolder routerList) {
        if (info.major == 1 && info.minor == 0) {
            //
            // 1.0 profiles do not have tagged components
            // 
            return;
        }

        for (int i = 0; i < info.components.length; ++i) {
            if (info.components[i].tag == org.omg.MessageRouting.TAG_MESSAGE_ROUTERS.value) {
                byte[] data = info.components[i].component_data;
                org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                        data, data.length);
                org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                        buf);
                in._OB_readEndian();

                //
                // Needed to create Objects
                //
                in._OB_ORBInstance(orbInstance);

                //
                // Read the router list from the components
                //
                org.omg.MessageRouting.Router[] routers = org.omg.MessageRouting.RouterListHelper
                        .read(in);

                //
                // Add the new routers to the supplied list of routers
                //	    
                int routerLen = routers.length;
                int currentLen = routerList.value.length;
                org.omg.MessageRouting.Router[] newList = new org.omg.MessageRouting.Router[routerLen
                        + currentLen];
                System.arraycopy(routerList.value, 0, newList, 0, currentLen);
                System.arraycopy(routers, 0, newList, currentLen, routerLen);
                routerList.value = newList;
            }
        }
    }

    public static org.omg.Messaging.PolicyValue createMessagingPolicyValue(
            org.omg.CORBA.Policy policy) {
        org.omg.Messaging.PolicyValue value = new org.omg.Messaging.PolicyValue();

        //
        // Create a PolicyValue based on the type of policy that was passed
        // to this method
        //
        switch (policy.policy_type()) {

        case org.omg.Messaging.REBIND_POLICY_TYPE.value: {
            org.omg.Messaging.RebindPolicy p = org.omg.Messaging.RebindPolicyHelper
                    .narrow(policy);

            short mode = p.rebind_mode();

            value.ptype = org.omg.Messaging.REBIND_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.Messaging.RebindModeHelper.write(out, mode);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }

        case org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE.value: {
            org.omg.Messaging.RequestPriorityPolicy p = org.omg.Messaging.RequestPriorityPolicyHelper
                    .narrow(policy);

            org.omg.Messaging.PriorityRange range = p.priority_range();

            value.ptype = org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.Messaging.PriorityRangeHelper.write(out, range);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE.value: {
            org.omg.Messaging.ReplyPriorityPolicy p = org.omg.Messaging.ReplyPriorityPolicyHelper
                    .narrow(policy);

            org.omg.Messaging.PriorityRange range = p.priority_range();

            value.ptype = org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.Messaging.PriorityRangeHelper.write(out, range);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE.value: {
            org.omg.Messaging.RequestStartTimePolicy p = org.omg.Messaging.RequestStartTimePolicyHelper
                    .narrow(policy);

            org.omg.TimeBase.UtcT time = p.start_time();

            value.ptype = org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.TimeBase.UtcTHelper.write(out, time);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE.value: {
            org.omg.Messaging.RequestEndTimePolicy p = org.omg.Messaging.RequestEndTimePolicyHelper
                    .narrow(policy);

            org.omg.TimeBase.UtcT time = p.end_time();

            value.ptype = org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.TimeBase.UtcTHelper.write(out, time);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.REPLY_START_TIME_POLICY_TYPE.value: {
            org.omg.Messaging.ReplyStartTimePolicy p = org.omg.Messaging.ReplyStartTimePolicyHelper
                    .narrow(policy);

            org.omg.TimeBase.UtcT time = p.start_time();

            value.ptype = org.omg.Messaging.REPLY_START_TIME_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.TimeBase.UtcTHelper.write(out, time);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE.value: {
            org.omg.Messaging.RequestEndTimePolicy p = org.omg.Messaging.RequestEndTimePolicyHelper
                    .narrow(policy);

            org.omg.TimeBase.UtcT time = p.end_time();

            value.ptype = org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.TimeBase.UtcTHelper.write(out, time);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.RELATIVE_REQ_TIMEOUT_POLICY_TYPE.value: {
            org.omg.Messaging.RelativeRequestTimeoutPolicy p = org.omg.Messaging.RelativeRequestTimeoutPolicyHelper
                    .narrow(policy);

            long time = p.relative_expiry();
            org.omg.TimeBase.UtcT timeout = org.apache.yoko.orb.OB.TimeHelper
                    .add(org.apache.yoko.orb.OB.TimeHelper.utcNow(0), time);

            value.ptype = org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.TimeBase.UtcTHelper.write(out, timeout);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE.value: {
            org.omg.Messaging.RelativeRoundtripTimeoutPolicy p = org.omg.Messaging.RelativeRoundtripTimeoutPolicyHelper
                    .narrow(policy);

            long time = p.relative_expiry();
            org.omg.TimeBase.UtcT timeout = org.apache.yoko.orb.OB.TimeHelper
                    .add(org.apache.yoko.orb.OB.TimeHelper.utcNow(0), time);

            value.ptype = org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.TimeBase.UtcTHelper.write(out, timeout);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.ROUTING_POLICY_TYPE.value: {
            org.omg.Messaging.RoutingPolicy p = org.omg.Messaging.RoutingPolicyHelper
                    .narrow(policy);

            org.omg.Messaging.RoutingTypeRange range = p.routing_range();

            value.ptype = org.omg.Messaging.ROUTING_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.Messaging.RoutingTypeRangeHelper.write(out, range);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.MAX_HOPS_POLICY_TYPE.value: {
            org.omg.Messaging.MaxHopsPolicy p = org.omg.Messaging.MaxHopsPolicyHelper
                    .narrow(policy);

            short hops = p.max_hops();

            value.ptype = org.omg.Messaging.MAX_HOPS_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            out.write_ushort(hops);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        case org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value: {
            org.omg.Messaging.QueueOrderPolicy p = org.omg.Messaging.QueueOrderPolicyHelper
                    .narrow(policy);

            short order = p.allowed_orders();

            value.ptype = org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.Messaging.OrderingHelper.write(out, order);

            value.pvalue = new byte[buf.length()];
            System.arraycopy(buf.data(), 0, value.pvalue, 0, buf.length());
            break;
        }
        default: {
            throw new org.omg.CORBA.INV_POLICY();
        }

        } // End of switch

        return value;
    }

    public static void getComponentPolicyValues(
            org.omg.PortableInterceptor.IORInfo info,
            org.omg.Messaging.PolicyValueSeqHolder policies) {
        //
        // Retrieve the value for the REQUEST_PRIORITY_POLICY_TYPE
        //
        try {
            org.omg.CORBA.Policy p = info
                    .get_effective_policy(org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE.value);

            org.omg.Messaging.PolicyValue value = createMessagingPolicyValue(p);

            //
            // Add the new policy to the supplied list of policies
            //
            int len = policies.value.length;
            org.omg.Messaging.PolicyValue[] newSeq = new org.omg.Messaging.PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (org.omg.CORBA.INV_POLICY ex) {

        }

        //
        // Retrieve the value for the REPLY_PRIORITY_POLICY_TYPE
        //
        try {
            org.omg.CORBA.Policy p = info
                    .get_effective_policy(org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE.value);

            org.omg.Messaging.PolicyValue value = createMessagingPolicyValue(p);

            //
            // Add the new policy to the supplied list of policies
            //
            int len = policies.value.length;
            org.omg.Messaging.PolicyValue[] newSeq = new org.omg.Messaging.PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (org.omg.CORBA.INV_POLICY ex) {

        }

        //
        // Retrieve the value for the MAX_HOPS_POLICY_TYPE
        //
        try {
            org.omg.CORBA.Policy p = info
                    .get_effective_policy(org.omg.Messaging.MAX_HOPS_POLICY_TYPE.value);

            org.omg.Messaging.PolicyValue value = createMessagingPolicyValue(p);

            //
            // Add the new policy to the supplied list of policies
            //
            int len = policies.value.length;
            org.omg.Messaging.PolicyValue[] newSeq = new org.omg.Messaging.PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (org.omg.CORBA.INV_POLICY ex) {

        }

        //
        // Retrieve the value for the QUEUE_ORDER_POLICY_TYPE
        //
        try {
            org.omg.CORBA.Policy p = info
                    .get_effective_policy(org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value);

            org.omg.Messaging.PolicyValue value = createMessagingPolicyValue(p);

            //
            // Add the new policy to the supplied list of policies
            //
            int len = policies.value.length;
            org.omg.Messaging.PolicyValue[] newSeq = new org.omg.Messaging.PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = value;
            policies.value = newSeq;
        } catch (org.omg.CORBA.INV_POLICY ex) {

        }
    }

    public static void getInvocationPolicyValues(
            org.apache.yoko.orb.OB.RefCountPolicyList policyList,
            org.omg.Messaging.PolicyValueSeqHolder policies) {
        //
        // TODO: We should revisit this. The spec is not very clear on
        // whether we should propogate policies that have no default values
        // and that have not been overriden by the client. For now, we
        // assume that only default or overriden policyes will be
        // propogated in the INVOCATION_POLICIES service context.
        //

        //
        // A set of flags that will indicate how to handle specific
        // policies
        // 
        boolean rebind = false;
        boolean queueOrder = false;
        boolean useRelativeRequest = false;
        boolean useRelativeRoundtrip = false;

        //
        // There is a special case we need to watch out for. The two
        // relative timeouts are actually a different way to specify the
        // request and reply end times. We don't want them to overwrite
        // each other so we wil pick the earliest values if both times
        // are supplied (e.g. RelativeRequest and RequestEnd or
        // RelativeRoundtrip and ReplyEnd)
        //
        if (policyList.relativeRequestTimeout != 0) {
            useRelativeRequest = true;
            if (org.apache.yoko.orb.OB.TimeHelper.notEqual(
                    policyList.requestEndTime,
                    org.apache.yoko.orb.OB.TimeHelper.utcMin())) {
                //
                // Compare the times and pick the earliest
                //
                org.omg.TimeBase.UtcT timeout = org.apache.yoko.orb.OB.TimeHelper
                        .utcMin();
                if (org.apache.yoko.orb.OB.TimeHelper.greaterThan(timeout,
                        policyList.requestEndTime))
                    useRelativeRequest = false;
            }
        }
        if (policyList.relativeRoundTripTimeout != 0) {
            useRelativeRoundtrip = true;
            if (org.apache.yoko.orb.OB.TimeHelper.notEqual(
                    policyList.replyEndTime, org.apache.yoko.orb.OB.TimeHelper
                            .utcMin())) {
                //
                // Compare the times and pick the earliest
                //
                org.omg.TimeBase.UtcT timeout = org.apache.yoko.orb.OB.TimeHelper
                        .utcMin();
                if (org.apache.yoko.orb.OB.TimeHelper.greaterThan(timeout,
                        policyList.replyEndTime))
                    useRelativeRequest = false;
            }
        }

        //
        // Go through all of the policies in the policy list and create the
        // appropriate PolicyValue
        //
        int numPolicies = policyList.value.length;
        for (int i = 0; i < numPolicies; ++i) {
            try {
                int type = policyList.value[i].policy_type();

                if ((type == org.omg.Messaging.RELATIVE_REQ_TIMEOUT_POLICY_TYPE.value && useRelativeRequest == false)
                        || (type == org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE.value && useRelativeRequest == true)) {
                    //
                    // This is to handle cases when both policies are set
                    //
                    continue;
                }
                if ((type == org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE.value && useRelativeRoundtrip == false)
                        || (type == org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE.value && useRelativeRoundtrip == true)) {
                    //
                    // This is to handle cases when both policies are set
                    //
                    continue;
                }

                org.omg.Messaging.PolicyValue val = createMessagingPolicyValue(policyList.value[i]);

                //
                // We also need to watch out for rebind and queue order.
                // If they have been specified by the client, we don't need
                // to use their defaults. Flag this so we know about it
                // later
                //
                if (val.ptype == org.omg.Messaging.REBIND_POLICY_TYPE.value)
                    rebind = true;
                if (val.ptype == org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value)
                    queueOrder = true;

                //
                // Add the policy to the supplied list of policies
                //
                int len = policies.value.length;
                org.omg.Messaging.PolicyValue[] newSeq = new org.omg.Messaging.PolicyValue[len + 1];
                System.arraycopy(policies.value, 0, newSeq, 0, len);
                policies.value = newSeq;
            } catch (org.omg.CORBA.INV_POLICY ex) {

            }
        }
        if (rebind == false) {
            //
            // We need to use the default value for the rebind policy
            //
            org.omg.Messaging.PolicyValue val = new org.omg.Messaging.PolicyValue();

            val.ptype = org.omg.Messaging.REBIND_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.Messaging.RebindModeHelper
                    .write(out, policyList.rebindMode);
            val.pvalue = new byte[out._OB_pos()];
            System.arraycopy(buf.data(), 0, val.pvalue, 0, buf.length());

            //
            // Add the rebind policy to the list of supplied policies
            //
            int len = policies.value.length;
            org.omg.Messaging.PolicyValue[] newSeq = new org.omg.Messaging.PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = val;
            policies.value = newSeq;
        }
        if (queueOrder == false) {
            //
            // We need to use the default value for the queue order policy
            //
            org.omg.Messaging.PolicyValue val = new org.omg.Messaging.PolicyValue();

            val.ptype = org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value;

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            out._OB_writeEndian();
            org.omg.Messaging.OrderingHelper.write(out, policyList.queueOrder);
            val.pvalue = new byte[out._OB_pos()];
            System.arraycopy(buf.data(), 0, val.pvalue, 0, buf.length());

            //
            // Add the queue order policy to the list of supplied policies
            //
            int len = policies.value.length;
            org.omg.Messaging.PolicyValue[] newSeq = new org.omg.Messaging.PolicyValue[len + 1];
            System.arraycopy(policies.value, 0, newSeq, 0, len);
            newSeq[len] = val;
            policies.value = newSeq;
        }
    }

    public static org.omg.CORBA.Policy[] getPolicyListFromPolicyValues(
            org.omg.Messaging.PolicyValue[] policies) {
        java.util.Vector list = new java.util.Vector(0);

        //
        // Loop through the PolicyValueSeq and create necessary policies
        //
        int policiesLength = policies.length;
        for (int i = 0; i < policiesLength; ++i) {
            org.omg.CORBA.Policy policy;
            try {
                policy = getPolicyFromPolicyValue(policies[i]);
            } catch (org.omg.CORBA.INV_POLICY ex) {
                continue;
            }

            //
            // Add the new policy to the list
            //
            list.addElement(policy);
        }

        org.omg.CORBA.Policy[] policyList = new org.omg.CORBA.Policy[list
                .size()];
        for (int i = 0; i < list.size(); ++i)
            policyList[i] = (org.omg.CORBA.Policy) list.elementAt(i);

        return policyList;
    }

    public static org.omg.CORBA.Policy getPolicyFromPolicyValue(
            org.omg.Messaging.PolicyValue policyValue) {
        //
        // Create the appropriate policy depending on the policy value we
        // were given
        //
        switch (policyValue.ptype) {
        case org.omg.Messaging.REBIND_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            short mode = org.omg.Messaging.RebindModeHelper.read(in);

            return new org.apache.yoko.orb.Messaging.RebindPolicy_impl(mode);
        }
        case org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            org.omg.Messaging.PriorityRange range = org.omg.Messaging.PriorityRangeHelper
                    .read(in);

            return new org.apache.yoko.orb.Messaging.RequestPriorityPolicy_impl(
                    range);
        }
        case org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            org.omg.Messaging.PriorityRange range = org.omg.Messaging.PriorityRangeHelper
                    .read(in);

            return new org.apache.yoko.orb.Messaging.ReplyPriorityPolicy_impl(
                    range);
        }
        case org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            org.omg.TimeBase.UtcT time = org.omg.TimeBase.UtcTHelper.read(in);

            return new org.apache.yoko.orb.Messaging.RequestStartTimePolicy_impl(
                    time);
        }
        case org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            org.omg.TimeBase.UtcT time = org.omg.TimeBase.UtcTHelper.read(in);

            return new org.apache.yoko.orb.Messaging.RequestEndTimePolicy_impl(
                    time);
        }
        case org.omg.Messaging.REPLY_START_TIME_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            org.omg.TimeBase.UtcT time = org.omg.TimeBase.UtcTHelper.read(in);

            return new org.apache.yoko.orb.Messaging.ReplyStartTimePolicy_impl(
                    time);
        }
        case org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            org.omg.TimeBase.UtcT time = org.omg.TimeBase.UtcTHelper.read(in);

            return new org.apache.yoko.orb.Messaging.ReplyEndTimePolicy_impl(
                    time);
        }
        case org.omg.Messaging.ROUTING_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            org.omg.Messaging.RoutingTypeRange range = org.omg.Messaging.RoutingTypeRangeHelper
                    .read(in);

            return new org.apache.yoko.orb.Messaging.RoutingPolicy_impl(range);
        }
        case org.omg.Messaging.MAX_HOPS_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            short hops = in.read_ushort();

            return new org.apache.yoko.orb.Messaging.MaxHopsPolicy_impl(hops);
        }
        case org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value: {
            byte[] data = policyValue.pvalue;
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    data, data.length);
            org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                    buf);
            in._OB_readEndian();
            short order = org.omg.Messaging.OrderingHelper.read(in);

            return new org.apache.yoko.orb.Messaging.QueueOrderPolicy_impl(
                    order);
        }
        default: {
            throw new org.omg.CORBA.INV_POLICY();
        }

        } // End of switch

        // return org.omg.CORBA.Policy._nil();
    }
}

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

import org.apache.yoko.orb.OB.CONNECT_TIMEOUT_POLICY_ID;
import org.apache.yoko.orb.OB.ConnectTimeoutPolicy;
import org.apache.yoko.orb.OB.INTERCEPTOR_POLICY_ID;
import org.apache.yoko.orb.OB.InterceptorPolicy;
import org.apache.yoko.orb.OB.LOCATE_REQUEST_POLICY_ID;
import org.apache.yoko.orb.OB.LOCATION_TRANSPARENCY_POLICY_ID;
import org.apache.yoko.orb.OB.LOCATION_TRANSPARENCY_RELAXED;
import org.apache.yoko.orb.OB.LocateRequestPolicy;
import org.apache.yoko.orb.OB.LocationTransparencyPolicy;
import org.apache.yoko.orb.OB.REQUEST_TIMEOUT_POLICY_ID;
import org.apache.yoko.orb.OB.RETRY_POLICY_ID;
import org.apache.yoko.orb.OB.RETRY_STRICT;
import org.apache.yoko.orb.OB.RequestTimeoutPolicy;
import org.apache.yoko.orb.OB.RetryAttributes;
import org.apache.yoko.orb.OB.RetryPolicy;
import org.apache.yoko.orb.OB.TIMEOUT_POLICY_ID;
import org.apache.yoko.orb.OB.TimeoutPolicy;

final public class RefCountPolicyList {
    //
    // The immutable PolicyList
    //
    public org.omg.CORBA.Policy[] value;

    //
    // The immutable value of the retry policy
    //
    public RetryAttributes retry;

    //
    // The immutable value of the connect timeout policy
    //
    public int connectTimeout;

    //
    // The immutable value of the request timeout policy
    //
    public int requestTimeout;

    //
    // The immutable value of the request start time policy
    //
    public org.omg.TimeBase.UtcT requestStartTime;

    //
    // The immutable value of the request end time policy
    //
    public org.omg.TimeBase.UtcT requestEndTime;

    //
    // The immutable value of the reply start time policy
    //
    public org.omg.TimeBase.UtcT replyStartTime;

    //
    // The immutable value of the reply end time policy
    //
    public org.omg.TimeBase.UtcT replyEndTime;

    //
    // The immutable value of the relative request timeout policy
    //
    public long relativeRequestTimeout;

    //
    // The immutable value of the relative round trip timeout policy
    //
    public long relativeRoundTripTimeout;

    //
    // The immutable value of the rebind mode policy
    //
    public short rebindMode;

    //
    // The immutable value of the sync scope policy
    //
    public short syncScope;

    //
    // The immutable value of the location transparency policy
    //
    public short locationTransparency;

    //
    // the immutable value of the bidir policy
    //
    public short biDirMode;

    //
    // The immutable value of the InterceptorPolicy, or true if there
    // is no such policy
    //
    public boolean interceptor;

    //
    // The immutable value of the LocateRequestPolicy, or false if there
    // is no such policy
    //
    public boolean locateRequest;

    //
    // the immutable value of the request priority policy
    //
    public org.omg.Messaging.PriorityRange requestPriority;

    //
    // the immutable value of the reply priority policy
    //
    public org.omg.Messaging.PriorityRange replyPriority;

    //
    // the immutable value of the routing policy
    //
    public org.omg.Messaging.RoutingTypeRange routingRange;

    //
    // the immutable value of the max hops policy
    //
    public short maxHops;

    //
    // the immutable value of the queue order policy
    //
    public short queueOrder;

    // ----------------------------------------------------------------------
    // RefCountPolicyList private and protected members
    // ----------------------------------------------------------------------

    private static RetryAttributes getRetry(org.omg.CORBA.Policy[] policies) {
        RetryAttributes attributes = new RetryAttributes();
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == RETRY_POLICY_ID.value) {
                RetryPolicy policy = (RetryPolicy) policies[i];

                attributes.mode = policy.retry_mode();
                attributes.interval = policy.retry_interval();
                attributes.max = policy.retry_max();
                attributes.remote = policy.retry_remote();

                return attributes;
            }
        }
        attributes.mode = RETRY_STRICT.value;
        attributes.interval = 0;
        attributes.max = 1;
        attributes.remote = false;

        return attributes;
    }

    private static int getConnectTimeout(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == CONNECT_TIMEOUT_POLICY_ID.value) {
                ConnectTimeoutPolicy policy = (ConnectTimeoutPolicy) policies[i];
                return policy.value();
            }
        }

        //
        // Fall back to TimeoutPolicy
        //
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == TIMEOUT_POLICY_ID.value) {
                TimeoutPolicy policy = (TimeoutPolicy) policies[i];
                return policy.value();
            }
        }

        return -1;
    }

    //
    // TODO: This needs to be replaced with the new messaging timeout
    // policies below.
    //
    private static int getRequestTimeout(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == REQUEST_TIMEOUT_POLICY_ID.value) {
                RequestTimeoutPolicy policy = (RequestTimeoutPolicy) policies[i];
                return policy.value();
            }
        }

        //
        // Fall back to TimeoutPolicy
        //
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == TIMEOUT_POLICY_ID.value) {
                TimeoutPolicy policy = (TimeoutPolicy) policies[i];
                return policy.value();
            }
        }

        return -1;
    }

    private org.omg.TimeBase.UtcT getRequestStartTime(
            org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE.value) {
                org.omg.Messaging.RequestStartTimePolicy policy = (org.omg.Messaging.RequestStartTimePolicy) policies[i];
                return policy.start_time();
            }
        }

        return org.apache.yoko.orb.OB.TimeHelper.utcMin();
    }

    private org.omg.TimeBase.UtcT getRequestEndTime(
            org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE.value) {
                org.omg.Messaging.RequestEndTimePolicy policy = (org.omg.Messaging.RequestEndTimePolicy) policies[i];
                return policy.end_time();
            }
        }

        return org.apache.yoko.orb.OB.TimeHelper.utcMin();
    }

    private org.omg.TimeBase.UtcT getReplyStartTime(
            org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.REPLY_START_TIME_POLICY_TYPE.value) {
                org.omg.Messaging.ReplyStartTimePolicy policy = (org.omg.Messaging.ReplyStartTimePolicy) policies[i];
                return policy.start_time();
            }
        }

        return org.apache.yoko.orb.OB.TimeHelper.utcMin();
    }

    private org.omg.TimeBase.UtcT getReplyEndTime(
            org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE.value) {
                org.omg.Messaging.ReplyEndTimePolicy policy = (org.omg.Messaging.ReplyEndTimePolicy) policies[i];
                return policy.end_time();
            }
        }

        return org.apache.yoko.orb.OB.TimeHelper.utcMin();
    }

    private long getRelativeRequestTimeout(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.RELATIVE_REQ_TIMEOUT_POLICY_TYPE.value) {
                org.omg.Messaging.RelativeRequestTimeoutPolicy policy = (org.omg.Messaging.RelativeRequestTimeoutPolicy) policies[i];
                return policy.relative_expiry();
            }
        }

        return 0;
    }

    private long getRelativeRoundTripTimeout(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE.value) {
                org.omg.Messaging.RelativeRoundtripTimeoutPolicy policy = (org.omg.Messaging.RelativeRoundtripTimeoutPolicy) policies[i];
                return policy.relative_expiry();
            }
        }

        return 0;
    }

    private static short getRebindMode(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.REBIND_POLICY_TYPE.value) {
                org.omg.Messaging.RebindPolicy policy = (org.omg.Messaging.RebindPolicy) policies[i];
                return policy.rebind_mode();
            }
        }

        return org.omg.Messaging.TRANSPARENT.value;
    }

    private static short getSyncScope(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.SYNC_SCOPE_POLICY_TYPE.value) {
                org.omg.Messaging.SyncScopePolicy policy = (org.omg.Messaging.SyncScopePolicy) policies[i];
                return policy.synchronization();
            }
        }

        return org.omg.Messaging.SYNC_NONE.value;
    }

    private static short getLocationTransparency(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == LOCATION_TRANSPARENCY_POLICY_ID.value) {
                LocationTransparencyPolicy policy = (LocationTransparencyPolicy) policies[i];
                return policy.value();
            }
        }

        return LOCATION_TRANSPARENCY_RELAXED.value;
    }

    private static short getBiDirMode(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE.value) {
                org.omg.BiDirPolicy.BidirectionalPolicy policy = (org.omg.BiDirPolicy.BidirectionalPolicy) policies[i];

                return policy.value();
            }
        }

        return org.omg.BiDirPolicy.NORMAL.value;
    }

    private static boolean getInterceptor(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == INTERCEPTOR_POLICY_ID.value) {
                InterceptorPolicy policy = (InterceptorPolicy) policies[i];
                return policy.value();
            }
        }

        return true;
    }

    private static boolean getLocateRequest(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == LOCATE_REQUEST_POLICY_ID.value) {
                LocateRequestPolicy policy = (LocateRequestPolicy) policies[i];
                return policy.value();
            }
        }

        return false;
    }

    public static org.omg.Messaging.PriorityRange getRequestPriority(
            org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE.value) {
                org.omg.Messaging.RequestPriorityPolicy policy = (org.omg.Messaging.RequestPriorityPolicy) policies[i];
                return policy.priority_range();
            }
        }

        org.omg.Messaging.PriorityRange range = new org.omg.Messaging.PriorityRange();
        range.min = 0;
        range.max = 0;

        return range;
    }

    public static org.omg.Messaging.PriorityRange getReplyPriority(
            org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE.value) {
                org.omg.Messaging.ReplyPriorityPolicy policy = (org.omg.Messaging.ReplyPriorityPolicy) policies[i];
                return policy.priority_range();
            }
        }

        org.omg.Messaging.PriorityRange range = new org.omg.Messaging.PriorityRange();
        range.min = 0;
        range.max = 0;

        return range;
    }

    public static org.omg.Messaging.RoutingTypeRange getRoutingRange(
            org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.ROUTING_POLICY_TYPE.value) {
                org.omg.Messaging.RoutingPolicy policy = (org.omg.Messaging.RoutingPolicy) policies[i];
                return policy.routing_range();
            }
        }

        org.omg.Messaging.RoutingTypeRange range = new org.omg.Messaging.RoutingTypeRange();
        range.min = org.omg.Messaging.ROUTE_NONE.value;
        range.max = org.omg.Messaging.ROUTE_NONE.value;

        return range;
    }

    public static short getMaxHops(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.MAX_HOPS_POLICY_TYPE.value) {
                org.omg.Messaging.MaxHopsPolicy policy = (org.omg.Messaging.MaxHopsPolicy) policies[i];
                return policy.max_hops();
            }
        }

        return Short.MAX_VALUE;
    }

    public static short getQueueOrder(org.omg.CORBA.Policy[] policies) {
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value) {
                org.omg.Messaging.QueueOrderPolicy policy = (org.omg.Messaging.QueueOrderPolicy) policies[i];
                return policy.allowed_orders();
            }
        }

        return org.omg.Messaging.ORDER_TEMPORAL.value;
    }

    // ----------------------------------------------------------------------
    // RefCountPolicyList public members
    // ----------------------------------------------------------------------

    public RefCountPolicyList(org.omg.CORBA.Policy[] v) {
        value = v;
        retry = getRetry(v);
        connectTimeout = getConnectTimeout(v);
        requestTimeout = getRequestTimeout(v);
        requestStartTime = getRequestStartTime(v);
        requestEndTime = getRequestEndTime(v);
        replyStartTime = getReplyStartTime(v);
        replyEndTime = getReplyEndTime(v);
        relativeRequestTimeout = getRelativeRequestTimeout(v);
        relativeRoundTripTimeout = getRelativeRoundTripTimeout(v);
        rebindMode = getRebindMode(v);
        syncScope = getSyncScope(v);
        locationTransparency = getLocationTransparency(v);
        biDirMode = getBiDirMode(v);
        interceptor = getInterceptor(v);
        locateRequest = getLocateRequest(v);
        requestPriority = getRequestPriority(v);
        replyPriority = getReplyPriority(v);
        routingRange = getRoutingRange(v);
        maxHops = getMaxHops(v);
        queueOrder = getQueueOrder(v);
    }
}

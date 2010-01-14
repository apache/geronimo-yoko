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

package org.apache.yoko.orb.CORBA;

final public class ORBPolicyFactory_impl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.PolicyFactory {
    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public org.omg.CORBA.Policy create_policy(int type, org.omg.CORBA.Any any)
            throws org.omg.CORBA.PolicyError {
        try {
            switch (type) {
            case org.apache.yoko.orb.OB.CONNECTION_REUSE_POLICY_ID.value: {
                boolean b = any.extract_boolean();
                return new org.apache.yoko.orb.OB.ConnectionReusePolicy_impl(b);
            }
            case org.apache.yoko.orb.OB.ZERO_PORT_POLICY_ID.value: {
                boolean b = any.extract_boolean();
                return new org.apache.yoko.orb.OB.ZeroPortPolicy_impl(b);
            }

            case org.apache.yoko.orb.OB.PROTOCOL_POLICY_ID.value: {
                String[] seq = org.apache.yoko.orb.OCI.PluginIdSeqHelper
                        .extract(any);
                return new org.apache.yoko.orb.OB.ProtocolPolicy_impl(seq);
            }

            case org.apache.yoko.orb.OB.RETRY_POLICY_ID.value: {
                try {
                    short v = any.extract_short();
                    if (v > org.apache.yoko.orb.OB.RETRY_ALWAYS.value)
                        throw new org.omg.CORBA.PolicyError(
                                org.omg.CORBA.BAD_POLICY_VALUE.value);
                    return new org.apache.yoko.orb.OB.RetryPolicy_impl(v, 0, 1,
                            false);
                } catch (org.omg.CORBA.BAD_OPERATION ex) {
                }
                org.apache.yoko.orb.OB.RetryAttributes attr = org.apache.yoko.orb.OB.RetryAttributesHelper
                        .extract(any);
                return new org.apache.yoko.orb.OB.RetryPolicy_impl(attr.mode,
                        attr.interval, attr.max, attr.remote);
            }

            case org.apache.yoko.orb.OB.TIMEOUT_POLICY_ID.value: {
                int t = any.extract_ulong();
                return new org.apache.yoko.orb.OB.TimeoutPolicy_impl(t);
            }

            case org.apache.yoko.orb.OB.LOCATION_TRANSPARENCY_POLICY_ID.value: {
                short v = any.extract_short();
                return new org.apache.yoko.orb.OB.LocationTransparencyPolicy_impl(
                        v);
            }

            case org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE.value: {
                org.omg.TimeBase.UtcT v = org.omg.TimeBase.UtcTHelper
                        .extract(any);
                return new org.apache.yoko.orb.Messaging.RequestStartTimePolicy_impl(
                        v);
            }

            case org.omg.Messaging.REQUEST_END_TIME_POLICY_TYPE.value: {
                org.omg.TimeBase.UtcT v = org.omg.TimeBase.UtcTHelper
                        .extract(any);
                return new org.apache.yoko.orb.Messaging.RequestEndTimePolicy_impl(
                        v);
            }

            case org.omg.Messaging.REPLY_START_TIME_POLICY_TYPE.value: {
                org.omg.TimeBase.UtcT v = org.omg.TimeBase.UtcTHelper
                        .extract(any);
                return new org.apache.yoko.orb.Messaging.ReplyStartTimePolicy_impl(
                        v);
            }

            case org.omg.Messaging.REPLY_END_TIME_POLICY_TYPE.value: {
                org.omg.TimeBase.UtcT v = org.omg.TimeBase.UtcTHelper
                        .extract(any);
                return new org.apache.yoko.orb.Messaging.ReplyEndTimePolicy_impl(
                        v);
            }

            case org.omg.Messaging.RELATIVE_REQ_TIMEOUT_POLICY_TYPE.value: {
                long v = any.extract_long();
                return new org.apache.yoko.orb.Messaging.RelativeRequestTimeoutPolicy_impl(
                        v);
            }

            case org.omg.Messaging.RELATIVE_RT_TIMEOUT_POLICY_TYPE.value: {
                long v = any.extract_long();
                return new org.apache.yoko.orb.Messaging.RelativeRoundtripTimeoutPolicy_impl(
                        v);
            }

            case org.omg.Messaging.REBIND_POLICY_TYPE.value: {
                short v = any.extract_short();
                return new org.apache.yoko.orb.Messaging.RebindPolicy_impl(v);
            }

            case org.omg.Messaging.SYNC_SCOPE_POLICY_TYPE.value: {
                short v = any.extract_short();
                return new org.apache.yoko.orb.Messaging.SyncScopePolicy_impl(v);
            }

            case org.apache.yoko.orb.OB.INTERCEPTOR_POLICY_ID.value: {
                boolean v = any.extract_boolean();
                return new org.apache.yoko.orb.OB.InterceptorPolicy_impl(v);
            }

            case org.apache.yoko.orb.OB.CONNECT_TIMEOUT_POLICY_ID.value: {
                int t = any.extract_ulong();
                return new org.apache.yoko.orb.OB.ConnectTimeoutPolicy_impl(t);
            }

            case org.apache.yoko.orb.OB.REQUEST_TIMEOUT_POLICY_ID.value: {
                int t = any.extract_ulong();
                return new org.apache.yoko.orb.OB.RequestTimeoutPolicy_impl(t);
            }

            case org.apache.yoko.orb.OB.LOCATE_REQUEST_POLICY_ID.value: {
                boolean b = any.extract_boolean();
                return new org.apache.yoko.orb.OB.LocateRequestPolicy_impl(b);
            }

            case org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE.value: {
                short v = org.omg.BiDirPolicy.BidirectionalPolicyValueHelper
                        .extract(any);
                return new org.apache.yoko.orb.BiDirPolicy.BidirectionalPolicy_impl(
                        v);
            }
            case org.omg.Messaging.REQUEST_PRIORITY_POLICY_TYPE.value: {
                org.omg.Messaging.PriorityRange v = org.omg.Messaging.PriorityRangeHelper
                        .extract(any);
                if (v.min > v.max)
                    throw new org.omg.CORBA.PolicyError(
                            org.omg.CORBA.BAD_POLICY_VALUE.value);
                return new org.apache.yoko.orb.Messaging.RequestPriorityPolicy_impl(
                        v);
            }
            case org.omg.Messaging.REPLY_PRIORITY_POLICY_TYPE.value: {
                org.omg.Messaging.PriorityRange v = org.omg.Messaging.PriorityRangeHelper
                        .extract(any);
                if (v.min > v.max)
                    throw new org.omg.CORBA.PolicyError(
                            org.omg.CORBA.BAD_POLICY_VALUE.value);
                return new org.apache.yoko.orb.Messaging.ReplyPriorityPolicy_impl(
                        v);
            }
            case org.omg.Messaging.ROUTING_POLICY_TYPE.value: {
                org.omg.Messaging.RoutingTypeRange v = org.omg.Messaging.RoutingTypeRangeHelper
                        .extract(any);
                if (v.min > v.max)
                    throw new org.omg.CORBA.PolicyError(
                            org.omg.CORBA.BAD_POLICY_VALUE.value);
                return new org.apache.yoko.orb.Messaging.RoutingPolicy_impl(v);
            }
            case org.omg.Messaging.MAX_HOPS_POLICY_TYPE.value: {
                short v = any.extract_ushort();
                return new org.apache.yoko.orb.Messaging.MaxHopsPolicy_impl(v);
            }
            case org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE.value: {
                short v = org.omg.Messaging.OrderingHelper.extract(any);
                if (v < org.omg.Messaging.ORDER_ANY.value
                        || v > org.omg.Messaging.ORDER_DEADLINE.value)
                    throw new org.omg.CORBA.PolicyError(
                            org.omg.CORBA.BAD_POLICY_VALUE.value);
                return new org.apache.yoko.orb.Messaging.QueueOrderPolicy_impl(
                        v);
            }

            } // end of switch
        } catch (org.omg.CORBA.BAD_OPERATION ex) {
            //
            // Any extraction failure
            //
            throw new org.omg.CORBA.PolicyError(
                    org.omg.CORBA.BAD_POLICY_TYPE.value);
        }

        throw new org.omg.CORBA.PolicyError(org.omg.CORBA.BAD_POLICY.value);
    }
}

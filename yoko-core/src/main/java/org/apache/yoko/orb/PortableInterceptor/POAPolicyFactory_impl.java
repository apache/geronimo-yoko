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

public final class POAPolicyFactory_impl extends org.omg.CORBA.LocalObject
        implements org.omg.PortableInterceptor.PolicyFactory {
    public org.omg.CORBA.Policy create_policy(int type, org.omg.CORBA.Any val)
            throws org.omg.CORBA.PolicyError {
        try {
            if (type == org.omg.PortableServer.THREAD_POLICY_ID.value) {
                org.omg.PortableServer.ThreadPolicyValue v = org.omg.PortableServer.ThreadPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.PortableServer.ThreadPolicy_impl(
                        v);
            }

            if (type == org.omg.PortableServer.LIFESPAN_POLICY_ID.value) {
                org.omg.PortableServer.LifespanPolicyValue v = org.omg.PortableServer.LifespanPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.PortableServer.LifespanPolicy_impl(
                        v);
            }

            if (type == org.omg.PortableServer.ID_UNIQUENESS_POLICY_ID.value) {
                org.omg.PortableServer.IdUniquenessPolicyValue v = org.omg.PortableServer.IdUniquenessPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.PortableServer.IdUniquenessPolicy_impl(
                        v);
            }

            if (type == org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID.value) {
                org.omg.PortableServer.IdAssignmentPolicyValue v = org.omg.PortableServer.IdAssignmentPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.PortableServer.IdAssignmentPolicy_impl(
                        v);
            }

            if (type == org.omg.PortableServer.IMPLICIT_ACTIVATION_POLICY_ID.value) {
                org.omg.PortableServer.ImplicitActivationPolicyValue v = org.omg.PortableServer.ImplicitActivationPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.PortableServer.ImplicitActivationPolicy_impl(
                        v);
            }

            if (type == org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID.value) {
                org.omg.PortableServer.ServantRetentionPolicyValue v = org.omg.PortableServer.ServantRetentionPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.PortableServer.ServantRetentionPolicy_impl(
                        v);
            }

            if (type == org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID.value) {
                org.omg.PortableServer.RequestProcessingPolicyValue v = org.omg.PortableServer.RequestProcessingPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.PortableServer.RequestProcessingPolicy_impl(
                        v);
            }

            if (type == org.apache.yoko.orb.OBPortableServer.SYNCHRONIZATION_POLICY_ID.value) {
                org.apache.yoko.orb.OBPortableServer.SynchronizationPolicyValue v = org.apache.yoko.orb.OBPortableServer.SynchronizationPolicyValueHelper
                        .extract(val);
                return new org.apache.yoko.orb.OBPortableServer.SynchronizationPolicy_impl(
                        v);
            }

            if (type == org.apache.yoko.orb.OBPortableServer.INTERCEPTOR_CALL_POLICY_ID.value) {
                boolean v = val.extract_boolean();
                return new org.apache.yoko.orb.OBPortableServer.InterceptorCallPolicy_impl(
                        v);
            }

            /*
             * We can't implement this yet since locality constrained objects
             * cannot be placed in CORBA::Any yet
             * 
             * if(type ==
             * org.apache.yoko.orb.PortableServer.OB_DISPATCH_STRATEGY_POLICY_ID.value) {
             * org.apache.yoko.orb.PortableServer.OBDispatchStrategy v =
             * org.apache.yoko.orb.PortableServer.OBDispatchStrategyHelper.
             * extract(val); return new OBDispatchStrategyPolicy_impl(v); }
             */

            throw new org.omg.CORBA.PolicyError(org.omg.CORBA.BAD_POLICY.value);
        } catch (org.omg.CORBA.BAD_OPERATION ex) {
            //
            // Any extraction failed
            //
            throw new org.omg.CORBA.PolicyError(
                    org.omg.CORBA.BAD_POLICY_TYPE.value);
        }
    }
}

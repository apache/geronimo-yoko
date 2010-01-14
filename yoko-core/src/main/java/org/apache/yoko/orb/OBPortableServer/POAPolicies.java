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

//
// This class represents the set of POA policies defined on the
// current POA. This class is immutable, and needs no mutex
// protection.
//

package org.apache.yoko.orb.OBPortableServer;

import org.apache.yoko.orb.OBPortableServer.DISPATCH_STRATEGY_POLICY_ID;
import org.apache.yoko.orb.OBPortableServer.DispatchStrategyPolicy;
import org.apache.yoko.orb.OBPortableServer.DispatchStrategyPolicyHelper;
import org.apache.yoko.orb.OBPortableServer.INTERCEPTOR_CALL_POLICY_ID;
import org.apache.yoko.orb.OBPortableServer.InterceptorCallPolicy;
import org.apache.yoko.orb.OBPortableServer.InterceptorCallPolicyHelper;
import org.apache.yoko.orb.OBPortableServer.SYNCHRONIZATION_POLICY_ID;
import org.apache.yoko.orb.OBPortableServer.SynchronizationPolicy;
import org.apache.yoko.orb.OBPortableServer.SynchronizationPolicyHelper;
import org.apache.yoko.orb.OBPortableServer.SynchronizationPolicyValue;
import org.apache.yoko.orb.OB.ZERO_PORT_POLICY_ID;
import org.apache.yoko.orb.OB.ZeroPortPolicy;
import org.apache.yoko.orb.OB.ZeroPortPolicyHelper;
import org.apache.yoko.orb.OB.ZeroPortPolicyValue;

final public class POAPolicies {
    private boolean interceptorCallPolicyValue_;

    private SynchronizationPolicyValue synchronizationPolicyValue_;

    private org.apache.yoko.orb.OB.DispatchStrategy dispatchStrategyPolicyValue_;
    
    private boolean zeroPortPolicyValue_;

    private org.omg.PortableServer.LifespanPolicyValue lifespanPolicyValue_;

    private org.omg.PortableServer.IdUniquenessPolicyValue idUniquenessPolicyValue_;

    private org.omg.PortableServer.IdAssignmentPolicyValue idAssignmentPolicyValue_;

    private org.omg.PortableServer.ImplicitActivationPolicyValue implicitActivationPolicyValue_;

    private org.omg.PortableServer.ServantRetentionPolicyValue servantRetentionPolicyValue_;

    private org.omg.PortableServer.RequestProcessingPolicyValue requestProcessingPolicyValue_;

    private short bidirPolicyValue_;

    POAPolicies(org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.CORBA.Policy[] policies) {
        //
        // Set the default policy values
        //
        interceptorCallPolicyValue_ = true;
        synchronizationPolicyValue_ = SynchronizationPolicyValue.NO_SYNCHRONIZATION;
        zeroPortPolicyValue_ = false;
        lifespanPolicyValue_ = org.omg.PortableServer.LifespanPolicyValue.TRANSIENT;
        idUniquenessPolicyValue_ = org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID;
        idAssignmentPolicyValue_ = org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID;
        implicitActivationPolicyValue_ = org.omg.PortableServer.ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION;
        servantRetentionPolicyValue_ = org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN;
        requestProcessingPolicyValue_ = org.omg.PortableServer.RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY;
        dispatchStrategyPolicyValue_ = null;
        bidirPolicyValue_ = org.omg.BiDirPolicy.BOTH.value;

        if (policies != null) {
            for (int i = 0; i < policies.length; ++i) {
                int policyType = policies[i].policy_type();
                if (policyType == org.omg.PortableServer.THREAD_POLICY_ID.value) {
                    org.omg.PortableServer.ThreadPolicy policy = org.omg.PortableServer.ThreadPolicyHelper
                            .narrow(policies[i]);
                    synchronizationPolicyValue_ = (policy.value() == org.omg.PortableServer.ThreadPolicyValue.ORB_CTRL_MODEL) ? SynchronizationPolicyValue.NO_SYNCHRONIZATION
                            : SynchronizationPolicyValue.SYNCHRONIZE_ON_ORB;
                } else if (policyType == org.omg.PortableServer.LIFESPAN_POLICY_ID.value) {
                    org.omg.PortableServer.LifespanPolicy policy = org.omg.PortableServer.LifespanPolicyHelper
                            .narrow(policies[i]);
                    lifespanPolicyValue_ = policy.value();
                } else if (policyType == org.omg.PortableServer.ID_UNIQUENESS_POLICY_ID.value) {
                    org.omg.PortableServer.IdUniquenessPolicy policy = org.omg.PortableServer.IdUniquenessPolicyHelper
                            .narrow(policies[i]);
                    idUniquenessPolicyValue_ = policy.value();
                } else if (policyType == org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID.value) {
                    org.omg.PortableServer.IdAssignmentPolicy policy = org.omg.PortableServer.IdAssignmentPolicyHelper
                            .narrow(policies[i]);
                    idAssignmentPolicyValue_ = policy.value();
                } else if (policyType == org.omg.PortableServer.IMPLICIT_ACTIVATION_POLICY_ID.value) {
                    org.omg.PortableServer.ImplicitActivationPolicy policy = org.omg.PortableServer.ImplicitActivationPolicyHelper
                            .narrow(policies[i]);
                    implicitActivationPolicyValue_ = policy.value();
                } else if (policyType == org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID.value) {
                    org.omg.PortableServer.ServantRetentionPolicy policy = org.omg.PortableServer.ServantRetentionPolicyHelper
                            .narrow(policies[i]);
                    servantRetentionPolicyValue_ = policy.value();
                } else if (policyType == org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID.value) {
                    org.omg.PortableServer.RequestProcessingPolicy policy = org.omg.PortableServer.RequestProcessingPolicyHelper
                            .narrow(policies[i]);
                    requestProcessingPolicyValue_ = policy.value();
                } else if (policyType == org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE.value) {
                    org.omg.BiDirPolicy.BidirectionalPolicy p = org.omg.BiDirPolicy.BidirectionalPolicyHelper
                            .narrow(policies[i]);
                    bidirPolicyValue_ = p.value();
                }

                //
                // Yoko proprietary policies
                //
                else if (policyType == SYNCHRONIZATION_POLICY_ID.value) {
                    SynchronizationPolicy policy = SynchronizationPolicyHelper
                            .narrow(policies[i]);
                    synchronizationPolicyValue_ = policy.value();
                } else if (policyType == DISPATCH_STRATEGY_POLICY_ID.value) {
                    DispatchStrategyPolicy policy = DispatchStrategyPolicyHelper
                            .narrow(policies[i]);
                    dispatchStrategyPolicyValue_ = policy.value();
                } else if (policyType == INTERCEPTOR_CALL_POLICY_ID.value) {
                    InterceptorCallPolicy policy = InterceptorCallPolicyHelper
                            .narrow(policies[i]);
                    interceptorCallPolicyValue_ = policy.value();
                } else if (policyType == ZERO_PORT_POLICY_ID.value) {
                    ZeroPortPolicy policy = ZeroPortPolicyHelper
                            .narrow(policies[i]);
                    zeroPortPolicyValue_ = policy.value();
                }

                //
                // Otherwise the policy is not a ORB defined POA policy
                //
            }
        }

        if (dispatchStrategyPolicyValue_ == null) {
            org.apache.yoko.orb.OB.DispatchStrategyFactory dsf = orbInstance
                    .getDispatchStrategyFactory();
            dispatchStrategyPolicyValue_ = dsf
                    .create_default_dispatch_strategy();
        }
    }

    public boolean interceptorCallPolicy() {
        return interceptorCallPolicyValue_;
    }

    public boolean zeroPortPolicy() {
        return zeroPortPolicyValue_;
    }

    public SynchronizationPolicyValue synchronizationPolicy() {
        // TODO: Fix this
        // if(OBORB_impl.server_conc_model() ==
        // ORBORB_impl.ServerConcModelThreaded)
        // return org.apache.yoko.orb.OB.SYNCHRONIZE_ON_ORB;

        return synchronizationPolicyValue_;
    }

    public org.apache.yoko.orb.OB.DispatchStrategy dispatchStrategyPolicy() {
        return dispatchStrategyPolicyValue_;
    }

    public org.omg.PortableServer.LifespanPolicyValue lifespanPolicy() {
        return lifespanPolicyValue_;
    }

    public org.omg.PortableServer.IdUniquenessPolicyValue idUniquenessPolicy() {
        return idUniquenessPolicyValue_;
    }

    public org.omg.PortableServer.IdAssignmentPolicyValue idAssignmentPolicy() {
        return idAssignmentPolicyValue_;
    }

    public org.omg.PortableServer.ImplicitActivationPolicyValue implicitActivationPolicy() {
        return implicitActivationPolicyValue_;
    }

    public org.omg.PortableServer.ServantRetentionPolicyValue servantRetentionPolicy() {
        return servantRetentionPolicyValue_;
    }

    public org.omg.PortableServer.RequestProcessingPolicyValue requestProcessingPolicy() {
        return requestProcessingPolicyValue_;
    }

    public short bidirPolicy() {
        return bidirPolicyValue_;
    }

    public org.omg.CORBA.Policy[] recreate() {
        //
        // TODO:
        //
        // No ThreadPolicy policy appended. The problem is that some
        // values of SyncPolicy don't map. I guess the real solution
        // to this is to only create those policies that were
        // provided. Also, providing both Sync policy and ThreadPolicy
        // should be invalid.
        //
        org.omg.CORBA.Policy[] pl = new org.omg.CORBA.Policy[10];
        int i = 0;
        pl[i++] = new org.apache.yoko.orb.PortableServer.LifespanPolicy_impl(
                lifespanPolicyValue_);
        pl[i++] = new org.apache.yoko.orb.PortableServer.IdUniquenessPolicy_impl(
                idUniquenessPolicyValue_);
        pl[i++] = new org.apache.yoko.orb.PortableServer.IdAssignmentPolicy_impl(
                idAssignmentPolicyValue_);
        pl[i++] = new org.apache.yoko.orb.PortableServer.ImplicitActivationPolicy_impl(
                implicitActivationPolicyValue_);
        pl[i++] = new org.apache.yoko.orb.PortableServer.ServantRetentionPolicy_impl(
                servantRetentionPolicyValue_);
        pl[i++] = new org.apache.yoko.orb.PortableServer.RequestProcessingPolicy_impl(
                requestProcessingPolicyValue_);
        pl[i++] = new SynchronizationPolicy_impl(synchronizationPolicyValue_);
        pl[i++] = new DispatchStrategyPolicy_impl(dispatchStrategyPolicyValue_);
        pl[i] = new InterceptorCallPolicy_impl(interceptorCallPolicyValue_);

        return pl;
    }
}

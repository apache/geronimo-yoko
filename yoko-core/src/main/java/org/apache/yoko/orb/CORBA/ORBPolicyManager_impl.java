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

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyManager;
import org.omg.CORBA.SetOverrideType;

import static org.apache.yoko.orb.OB.MinorCodes.MinorDuplicatePolicyType;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final public class ORBPolicyManager_impl extends LocalObject implements PolicyManager {
    private final PolicyMap policies;

    public ORBPolicyManager_impl(PolicyMap policies) {
        this.policies = new PolicyMap(policies);
    }

    public synchronized Policy[] get_policy_overrides(int[] ts) {
        // if an empty array was passed in, return EVERYTHING
        if (ts.length == 0) return policies.getAllPolicies();
        // otherwise, return only the policies matching the requested types
        return policies.getSomePolicies(ts);
    }

    public synchronized void set_policy_overrides(Policy[] newPolicyArr, SetOverrideType set_add) {
        PolicyMap newPolicies = new PolicyMap(newPolicyArr);
        // throw an error if there were dupes
        if (newPolicies.size() < newPolicyArr.length) throw new BAD_PARAM(MinorDuplicatePolicyType, COMPLETED_NO);
        if (set_add == SetOverrideType.SET_OVERRIDE) policies.clear();
        policies.putAll(newPolicies);
    }
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import static java.util.Collections.synchronizedList;
import static org.apache.yoko.orb.OB.MinorCodes.MinorDuplicatePolicyType;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

final public class ORBPolicyManager_impl extends LocalObject implements PolicyManager {
    // owned by OBORB_impl - do not use Policy[]
    private List<Policy> policies_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized Policy[] get_policy_overrides(int[] ts) {
        // if an empty array was passed in, return EVERYTHING
        if (ts.length == 0) return policies_.toArray(new Policy[0]);

        // otherwise, return only the policies matching the requested types
        List<Policy> list = new ArrayList<>();
        for (int t : ts) for (Policy p : policies_) if (p.policy_type() == t) list.add(p);
        return list.toArray(new Policy[0]);
    }

    public synchronized void set_policy_overrides(Policy[] newPolicies, SetOverrideType set_add) {
        // check for duplicates
        Set<Integer> checklist = new HashSet<Integer>();
        for (Policy p: newPolicies) {
            if (!checklist.add(p.policy_type())) throw new BAD_PARAM(MinorDuplicatePolicyType, COMPLETED_NO);
        }

        if ((set_add == SetOverrideType.SET_OVERRIDE) || newPolicies.length == 0) {
            List<Policy> v = synchronizedList(new ArrayList<Policy>());
            Collections.addAll(v, newPolicies);
            // TODO: check whether we need to update anything in OBORB_impl
            policies_ = v;
            return;
        }

        List<Policy> appendList = new ArrayList<>();
        for (final Policy newPolicy : newPolicies) {
            if (override(newPolicy)) continue;
            appendList.add(newPolicy);
        }
        policies_.addAll(appendList);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ORBPolicyManager_impl(List<Policy> policies) {
        policies_ = policies;
    }

    private boolean override(Policy newPolicy) {
        // override an existing policy if the type matches
        final ListIterator<Policy> iterator = policies_.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().policy_type() != newPolicy.policy_type()) continue;
            iterator.set(newPolicy);
            return true;
        }
        return false;
    }
}

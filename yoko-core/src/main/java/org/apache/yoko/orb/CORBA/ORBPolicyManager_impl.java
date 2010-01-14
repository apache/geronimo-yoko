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

final public class ORBPolicyManager_impl extends org.omg.CORBA.LocalObject
        implements org.omg.CORBA.PolicyManager {
    //
    // Vector is owned by OBORB_impl - do not use Policy[]
    //
    private java.util.Vector policies_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized org.omg.CORBA.Policy[] get_policy_overrides(int[] ts) {
        if (ts.length == 0) {
            org.omg.CORBA.Policy[] result = new org.omg.CORBA.Policy[policies_
                    .size()];
            policies_.copyInto(result);
            return result;
        }

        java.util.Vector v = new java.util.Vector();
        for (int i = 0; i < ts.length; ++i) {
            java.util.Enumeration e = policies_.elements();
            while (e.hasMoreElements()) {
                org.omg.CORBA.Policy p = (org.omg.CORBA.Policy) e.nextElement();
                if (p.policy_type() == ts[i])
                    v.add(p);
            }
        }
        org.omg.CORBA.Policy[] result = new org.omg.CORBA.Policy[v.size()];
        v.copyInto(result);
        return result;
    }

    public synchronized void set_policy_overrides(
            org.omg.CORBA.Policy[] policies,
            org.omg.CORBA.SetOverrideType set_add)
            throws org.omg.CORBA.InvalidPolicies {
        for (int i = 0; i < policies.length; ++i) {
            for (int j = i + 1; j < policies.length; ++j) {
                if (policies[i].policy_type() == policies[j].policy_type())
                    throw new org.omg.CORBA.BAD_PARAM(
                            org.apache.yoko.orb.OB.MinorCodes.MinorDuplicatePolicyType,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }
        }

        if ((set_add == org.omg.CORBA.SetOverrideType.SET_OVERRIDE)
                || policies.length == 0) {
            java.util.Vector v = new java.util.Vector();
            for (int i = 0; i < policies.length; ++i) {
                v.add(policies[i]);
            }
            policies_ = v;
            return;
        }

        java.util.Vector appendList = new java.util.Vector();
        for (int i = 0; i < policies.length; ++i) {
            boolean override = false;
            java.util.Enumeration e = policies_.elements();
            int j = 0;
            while (e.hasMoreElements() && !override) {
                org.omg.CORBA.Policy p = (org.omg.CORBA.Policy) e.nextElement();
                if (p.policy_type() == policies[i].policy_type()) {
                    override = true;
                    policies_.setElementAt(policies[i], j);
                    break;
                }
                ++j;
            }
            if (!override) {
                appendList.add(policies[i]);
            }
        }
        policies_.addAll(appendList);
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ORBPolicyManager_impl(java.util.Vector policies) {
        policies_ = policies;
    }
}

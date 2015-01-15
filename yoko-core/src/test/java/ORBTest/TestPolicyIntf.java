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

package ORBTest;

import static org.junit.Assert.assertTrue;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyManager;
import org.omg.CORBA.PolicyManagerHelper;
import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.InvalidPolicies;
import org.apache.yoko.orb.OB.RetryPolicy;
import org.apache.yoko.orb.OB.RetryPolicyHelper;
import org.apache.yoko.orb.OB.TimeoutPolicy;
import org.apache.yoko.orb.OB.TimeoutPolicyHelper;
import org.apache.yoko.orb.OB.MinorCodes;

import ORBTest_Basic.*;

public class TestPolicyIntf extends test.common.TestBase {
    static void run(ORB orb) {
        PolicyManager pm = null;
        try {
            pm = PolicyManagerHelper.narrow(orb
                    .resolve_initial_references("ORBPolicyManager"));
        } catch (InvalidName ex) {
            assertTrue(false);
        }

        assertTrue(pm != null);

        int[] policyTypes = new int[0];
        Policy[] origPolicies = pm.get_policy_overrides(policyTypes);

        {
            try {
                Policy[] pl = new Policy[1];
                Any any = orb.create_any();
                any.insert_short(org.apache.yoko.orb.OB.RETRY_ALWAYS.value);
                pl[0] = orb.create_policy(
                        org.apache.yoko.orb.OB.RETRY_POLICY_ID.value, any);
                pm.set_policy_overrides(pl, SetOverrideType.ADD_OVERRIDE);

            } catch (PolicyError ex) {
                assertTrue(false);
            } catch (InvalidPolicies ex) {
                assertTrue(false);
            }

            policyTypes = new int[1];
            policyTypes[0] = org.apache.yoko.orb.OB.RETRY_POLICY_ID.value;
            Policy[] policy = pm.get_policy_overrides(policyTypes);
            assertTrue(policy.length == 1
			&& policy[0].policy_type() == org.apache.yoko.orb.OB.RETRY_POLICY_ID.value);

            RetryPolicy p = RetryPolicyHelper.narrow(policy[0]);
            assertTrue(p.retry_mode() == org.apache.yoko.orb.OB.RETRY_ALWAYS.value);
        }

        {
            try {
                Policy[] pl = new Policy[2];
                Any any = orb.create_any();
                any.insert_short(org.apache.yoko.orb.OB.RETRY_STRICT.value);
                pl[0] = orb.create_policy(
                        org.apache.yoko.orb.OB.RETRY_POLICY_ID.value, any);
                any = orb.create_any();
                any.insert_ulong(3000);
                pl[1] = orb.create_policy(
                        org.apache.yoko.orb.OB.TIMEOUT_POLICY_ID.value, any);

                pm.set_policy_overrides(pl, SetOverrideType.ADD_OVERRIDE);

            } catch (PolicyError ex) {
                assertTrue(false);
            } catch (InvalidPolicies ex) {
                assertTrue(false);
            }

            policyTypes = new int[2];
            policyTypes[0] = org.apache.yoko.orb.OB.RETRY_POLICY_ID.value;
            policyTypes[1] = org.apache.yoko.orb.OB.TIMEOUT_POLICY_ID.value;

            Policy[] policies = pm.get_policy_overrides(policyTypes);
            assertTrue(policies.length == 2);

            for (int i = 0; i < policies.length; ++i) {
                switch (policies[i].policy_type()) {
                case org.apache.yoko.orb.OB.RETRY_POLICY_ID.value: {
                    try {
                        RetryPolicy policy = RetryPolicyHelper
                                .narrow(policies[i]);
                        assertTrue(policy != null);
                        assertTrue(policy.retry_mode() == org.apache.yoko.orb.OB.RETRY_STRICT.value);
                    } catch (BAD_PARAM ex) {
                        assertTrue(false);
                    }
                    break;
                }
                case org.apache.yoko.orb.OB.TIMEOUT_POLICY_ID.value: {
                    try {
                        TimeoutPolicy policy = TimeoutPolicyHelper
                                .narrow(policies[i]);
                        assertTrue(policy != null);
                        assertTrue(policy.value() == 3000);
                    } catch (BAD_PARAM ex) {
                        assertTrue(false);
                    }
                    break;
                }
                default:
                    assertTrue(("org.omg.CORBA.PolicyManager.get_policy_overrides()"
					+ " returned policies with unexpected types") == null);
                }
            }
        }

        {
            try {
                Any any = orb.create_any();
                any.insert_short(org.apache.yoko.orb.OB.RETRY_ALWAYS.value);
                Policy[] pl = new Policy[2];
                pl[0] = orb.create_policy(
                        org.apache.yoko.orb.OB.RETRY_POLICY_ID.value, any);
                pl[1] = orb.create_policy(
                        org.apache.yoko.orb.OB.RETRY_POLICY_ID.value, any);
                pm.set_policy_overrides(pl, SetOverrideType.ADD_OVERRIDE);
                assertTrue(("org.omg.CORBA.PolicyManager.set_policy_overrides() "
				+ "BAD_PARAM(30, COMPLETED_NO) expected") == null);
            } catch (PolicyError ex) {
                assertTrue(false);
            } catch (InvalidPolicies ex) {
                assertTrue(false);
            } catch (BAD_PARAM ex) {
                assertTrue(ex.minor == MinorCodes.MinorDuplicatePolicyType);
            }

            //
            // Reset original policies.
            //

            try {
                pm.set_policy_overrides(origPolicies,
                        SetOverrideType.SET_OVERRIDE);

            } catch (InvalidPolicies ex) {
                assertTrue(false);
            }
            policyTypes = new int[0];
            Policy[] current = pm.get_policy_overrides(policyTypes);
            assertTrue(current.length == origPolicies.length);
            for (int i = 0; i < current.length; ++i) {
                boolean matched = false;
                for (int j = 0; j < origPolicies.length; ++j) {
                    if (current[i].policy_type() == origPolicies[j]
                            .policy_type()) {
                        matched = true;
                        break;
                    }
                }
                assertTrue(matched);
            }
        }

    }
}

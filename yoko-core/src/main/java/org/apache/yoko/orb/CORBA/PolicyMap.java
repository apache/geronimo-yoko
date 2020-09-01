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

import org.omg.CORBA.Policy;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class PolicyMap extends TreeMap<Integer, Policy> {
    private static final Policy[] ZERO_POLICIES_ARRAY = new Policy[0];

    public PolicyMap(PolicyMap m) { super(m); }
    public PolicyMap(Policy...policies) { for (Policy p: policies) add(p); }

    public void add(Policy p) { put(p.policy_type(), p); }

    public Policy[] getAllPolicies() { return values().toArray(ZERO_POLICIES_ARRAY); }

    public Policy[] getSomePolicies(int[] types) {
        List<Policy> list = new ArrayList<>();
        for (int type: types) if (containsKey(type)) list.add(get(type));
        return list.toArray(ZERO_POLICIES_ARRAY);
    }
}

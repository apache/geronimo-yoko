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

import org.apache.yoko.util.Assert;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.BAD_POLICY;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.PortableInterceptor.PolicyFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public final class PolicyFactoryManager {
    private boolean destroy;

    /** PolicyFactory objects registered from the PIManager */
    Map<Integer, PolicyFactory> policyFactoryTableExternal = new HashMap<>();

    /** PolicyFactory objects registered internally (for ORB policies, etc) */
    Map<Integer, PolicyFactory> policyFactoryTableInternal = new HashMap<>();

    void destroy() {
        Assert.ensure(!destroy);
        destroy = true;
        policyFactoryTableInternal.clear();
        policyFactoryTableExternal.clear();
    }

    // ----------------------------------------------------------------------
    // PolicyFactoryManager public member implementations
    // ----------------------------------------------------------------------

    public void registerPolicyFactory(int type, PolicyFactory factory, boolean internal) {
        Map<Integer, PolicyFactory> table = internal ? policyFactoryTableInternal : policyFactoryTableExternal;
        if (table.containsKey(type)) throw new BAD_PARAM();
        table.put(type, factory);
    }

    public Policy createPolicy(int type, Any any) throws PolicyError {
        PolicyFactory factory = Stream.of(policyFactoryTableInternal, policyFactoryTableExternal)
                .map(m -> m.get(type))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new PolicyError(BAD_POLICY.value));
        return factory.create_policy(type, any);
    }

    public boolean isPolicyRegistered(int type) {
        return policyFactoryTableInternal.containsKey(type) || policyFactoryTableExternal.containsKey(type);
    }
}

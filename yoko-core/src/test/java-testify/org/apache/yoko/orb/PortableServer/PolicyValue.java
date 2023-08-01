/*
 * Copyright 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.PortableServer;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

import java.util.function.Function;
import java.util.stream.Stream;

public enum PolicyValue {
    UNIQUE_ID(poa -> poa.create_id_uniqueness_policy(IdUniquenessPolicyValue.UNIQUE_ID)),
    MULTIPLE_ID(poa -> poa.create_id_uniqueness_policy(IdUniquenessPolicyValue.MULTIPLE_ID)),
    RETAIN(poa -> poa.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN)),
    NON_RETAIN(poa -> poa.create_servant_retention_policy(ServantRetentionPolicyValue.NON_RETAIN)),
    NO_IMPLICIT_ACTIVATION(poa -> poa.create_implicit_activation_policy(ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION)),
    IMPLICIT_ACTIVATION(poa -> poa.create_implicit_activation_policy(ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION)),
    USER_ID(poa -> poa.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID)),
    SYSTEM_ID(poa -> poa.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID)),
    PERSISTENT(poa -> poa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT)),
    USE_DEFAULT_SERVANT(poa -> poa.create_request_processing_policy(RequestProcessingPolicyValue.USE_DEFAULT_SERVANT)),
    USE_SERVANT_MANAGER(poa -> poa.create_request_processing_policy(RequestProcessingPolicyValue.USE_SERVANT_MANAGER)),
    USE_ACTIVE_OBJECT_MAP_ONLY(poa -> poa.create_request_processing_policy(RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY));

    final Function<POA, Policy> factory;

    PolicyValue(Function<POA, Policy> factory) {
        this.factory = factory;
    }

    static POA create_POA(String id, POA parentPoa, POAManager poaMgr, PolicyValue...policies) throws InvalidPolicy, AdapterAlreadyExists {
        Policy[] policyList = Stream.of(policies)
                .map(policy -> policy.factory.apply(parentPoa))
                .toArray(Policy[]::new);
        return parentPoa.create_POA(id, poaMgr, policyList);
    }
}

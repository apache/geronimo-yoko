package org.apache.yoko.orb.PortableServer;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

import java.util.function.Function;
import java.util.stream.Stream;

public enum Policies {
    UNIQUE_ID(poa -> poa.create_id_uniqueness_policy(IdUniquenessPolicyValue.UNIQUE_ID)),
    RETAIN(poa -> poa.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN)),
    NO_IMPLICIT_ACTIVATION(poa -> poa.create_implicit_activation_policy(ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION)),
    IMPLICIT_ACTIVATION(poa -> poa.create_implicit_activation_policy(ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION)),
    USER_ID(poa -> poa.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID)),
    SYSTEM_ID(poa -> poa.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID)),
    MULTIPLE_ID(poa -> poa.create_id_uniqueness_policy(IdUniquenessPolicyValue.MULTIPLE_ID)),

    PERSISTENT(poa -> poa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT)),
    USE_DEFAULT_SERVANT(poa -> poa.create_request_processing_policy(RequestProcessingPolicyValue.USE_DEFAULT_SERVANT));
    final Function<POA, Policy> factory;

    Policies(Function<POA, Policy> factory) {
        this.factory = factory;
    }
    static Policy[] of(POA poa, Policies... policies) {
        return Stream.of(policies)
                .map(policy -> policy.factory.apply(poa))
                .toArray(Policy[]::new);
    }
}

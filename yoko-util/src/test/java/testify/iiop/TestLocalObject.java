package testify.iiop;

import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.InterfaceDef;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SetOverrideType;

import static testify.iiop.TestLocalObject.Internals.LOCAL_OBJECT;

/**
 * CORBA's LocalObject implementation is actually stateless and could have been a default interface.
 * This is the equivalent default interface for test purposes.
 */
public interface TestLocalObject extends org.omg.CORBA.Object {
    enum Internals {
        ;
        static LocalObject LOCAL_OBJECT = new LocalObject();
    }
    default boolean _is_a(String identifier) { return LOCAL_OBJECT._is_a(identifier); }
    default boolean _is_equivalent(org.omg.CORBA.Object that) { return this.equals(that); }
    default boolean _non_existent() { return LOCAL_OBJECT._non_existent(); }
    default int _hash(int maximum) { return hashCode() % (maximum + 1); }
    default org.omg.CORBA.Object _duplicate() { return LOCAL_OBJECT._duplicate(); }
    default void _release() { LOCAL_OBJECT._release(); }
    default InterfaceDef _get_interface() { return LOCAL_OBJECT._get_interface(); }
    default org.omg.CORBA.Object _get_interface_def() { return LOCAL_OBJECT._get_interface_def(); }
    default Request _request(String s) { return LOCAL_OBJECT._request(s); }
    default Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result) { return LOCAL_OBJECT._create_request(ctx, operation, arg_list, result); }
    default Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) { return LOCAL_OBJECT._create_request(ctx, operation, arg_list, result, exclist, ctxlist); }
    default Policy _get_policy(int policy_type) { return LOCAL_OBJECT._get_policy(policy_type); }
    default org.omg.CORBA.Object _set_policy_override(Policy[] policies, SetOverrideType set_add) { return LOCAL_OBJECT._set_policy_override(policies, set_add); }
    default DomainManager[] _get_domain_managers() { return LOCAL_OBJECT._get_domain_managers(); }
}

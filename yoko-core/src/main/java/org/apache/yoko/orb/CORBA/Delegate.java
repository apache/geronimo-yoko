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

import org.apache.yoko.util.Assert;
import org.apache.yoko.orb.OB.ClientManager;
import org.apache.yoko.orb.OB.CoreTraceLevels;
import org.apache.yoko.orb.OB.DowncallStub;
import org.apache.yoko.orb.OB.FailureException;
import org.apache.yoko.orb.OB.LocationForward;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.RETRY_NEVER;
import org.apache.yoko.orb.OB.RETRY_STRICT;
import org.apache.yoko.orb.OB.RefCountPolicyList;
import org.apache.yoko.orb.OBPortableServer.DirectServant;
import org.apache.yoko.orb.OBPortableServer.POAManagerFactory;
import org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl;
import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.util.Factory;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.DomainManager;
import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.InterfaceDef;
import org.omg.CORBA.InterfaceDefHelper;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyListHolder;
import org.omg.CORBA.REBIND;
import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHelper;
import org.omg.Messaging.NO_RECONNECT;
import org.omg.PortableServer.Servant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static org.apache.yoko.util.MinorCodes.MinorDuplicatePolicyType;
import static org.apache.yoko.util.MinorCodes.MinorNoPolicy;
import static org.apache.yoko.util.MinorCodes.describeBadParam;
import static org.apache.yoko.util.MinorCodes.describeInvPolicy;
import static org.apache.yoko.logging.VerboseLogging.RETRY_LOG;
import static org.apache.yoko.logging.VerboseLogging.logged;
import static org.apache.yoko.logging.VerboseLogging.wrapped;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public final class Delegate extends org.omg.CORBA_2_4.portable.Delegate {
    private static final Logger logger = Logger.getLogger(Delegate.class.getName());
    private static final Policy[] EMPTY_POLICY_ARRAY = new Policy[0];
    private final ORBInstance orbInstance;
    private IOR ior;
    private IOR origIor;
    private final RefCountPolicyList policyList;
    private DowncallStub downcallStub_;

    // The servant for use in collocated invocations
    private DirectServant directServant;
    private final Object directServantMutex = new Object(){};

    // If false, the object is "remote", in the sense that its request
    // must be marshalled. If true, we need to check if the object is local.
    private boolean checkLocal = true;

    private static class ThreadSpecificRetryInfo extends ThreadLocal<RetryInfo> {
        protected RetryInfo initialValue() { return new RetryInfo(); }
        /**
         * Retrieve a new RetryInfo UNLESS one has explicitly been set for the thread.
         * After this has been called, there should be nothing stored for this thread.
         */
        public RetryInfo get() { try { return super.get(); } finally {  remove(); } }
    }

    // Thread-specific storage for tracking retries by portable stubs

    private final ThreadSpecificRetryInfo threadSpecificRetryInfo = new ThreadSpecificRetryInfo();
    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    // Check whether it's safe to retry
    private synchronized void checkRetry(int retry, SystemException ex, boolean remote) {
        // We remove the downcall stub, whether we retry or not
        downcallStub_ = null;

        // Reset to the original IOR
        ior = origIor;

        // Reset cached state
        directServant = null;
        checkLocal = true;

        // Get the core trace levels
        CoreTraceLevels coreTraceLevels = orbInstance.getCoreTraceLevels();

        // We only retry upon COMM_FAILURE, TRANSIENT, and NO_RESPONSE
        try {
            throw ex;
        } catch (COMM_FAILURE | TRANSIENT | NO_RESPONSE ignored) {
        } catch (SystemException e) {
            logger.log(FINE, "System exception during operation", e);
            throw logged(RETRY_LOG, e, "Caught a non-retryable exception");
        }

        // TODO: Check the Rebind Policy - raise REBIND if the policy
        // is set to NO_RECONNECT, but only if this object reference
        // was previously bound
        //
        //if (policyList.rebindMode == org.omg.Messaging.NO_RECONNECT.value && !ignoreRebind) {
        //    logException("retry forbidden by NO_RECONNECT policy - caught exception", ex);
        //    throw (REBIND)new org.omg.CORBA.REBIND().initCause(ex);
        //}

        // Check policy to see if we should retry on remote exceptions
        if (!policyList.retry.remote && remote) {
            throw logged(RETRY_LOG, ex, "Caught a non-local exception");
        }

        // Only try maximum number of times. Zero indicates infinite retry.
        if (policyList.retry.max != 0 && retry > policyList.retry.max) {
            throw logged(RETRY_LOG, ex, "Exceeded retry limit");
        }

        // We can't retry if RETRY_NEVER is set
        if (policyList.retry.mode == RETRY_NEVER.value) {
            throw logged(RETRY_LOG, ex, "Honor RETRY_NEVER policy");
        }

        // We can't retry if RETRY_STRICT is set and the completion
        // status is not COMPLETED_NO
        if (policyList.retry.mode == RETRY_STRICT.value && ex.completed != COMPLETED_NO) {
            throw logged(RETRY_LOG, ex, "Honor RETRY_STRICT policy");
        }

        // If a retry interval has been set then wait the specified amount of time
        if (policyList.retry.interval != 0) {
            logged(RETRY_LOG, ex, "Delay retry for " + policyList.retry.interval + "ms");
            try {
                Thread.sleep(policyList.retry.interval);
            } catch (InterruptedException ignored) {
            }
        }

        logged(RETRY_LOG, ex, "Allow retry");
    }

    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        // CollocatedServant must be explicitly destroyed in order to make it eligible for garbage collection
        if (directServant != null) {
            directServant.destroy();
        }
        super.finalize();
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    public InterfaceDef get_interface(org.omg.CORBA.Object self) {
        while (true) {
            if (!is_local(self)) {
                org.omg.CORBA.portable.OutputStream out;
                org.omg.CORBA.portable.InputStream in = null;
                try {
                    out = request(self, "_interface", true);
                    in = invoke(self, out);
                    return InterfaceDefHelper.read(in);
                } catch (ApplicationException ex) {
                    throw Assert.fail(ex);
                } catch (RemarshalException ex) {
                    // do nothing - continue loop
                } finally {
                    releaseReply(self, in);
                }
            } else {
                ServantObject so = servant_preinvoke(
                        self, "_interface", null);
                if (so == null)
                    continue;
                try {
                    Servant servant = (Servant) so.servant;
                    return servant._get_interface();
                } finally {
                    servant_postinvoke(self, so);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public org.omg.CORBA.Object get_interface_def(org.omg.CORBA.Object self) {
        return get_interface(self);
    }

    public org.omg.CORBA.Object duplicate(org.omg.CORBA.Object self) {
        return self;
    }

    public void release(org.omg.CORBA.Object self) {
    }

    public boolean is_a(org.omg.CORBA.Object self, String repository_id) {
        // Check IDL:omg.org/CORBA/Object:1.0
        if (repository_id.equals("IDL:omg.org/CORBA/Object:1.0")) return true;

        // Check all other ids
        org.omg.CORBA.portable.ObjectImpl o = (org.omg.CORBA.portable.ObjectImpl) self;

        for (String id : o._ids()) if (repository_id.equals(id)) return true;

        // Check the type_id in the IOR and the original IOR
        synchronized (this) {
            if (repository_id.equals(ior.type_id) || repository_id.equals(origIor.type_id)) return true;
        }

        // TODO: Some kind of is-a cache should be consulted here

        // Check implementation object
        while (true) {
            if (!is_local(self)) {
                org.omg.CORBA.portable.OutputStream out;
                org.omg.CORBA.portable.InputStream in = null;
                try {
                    out = request(self, "_is_a", true);
                    out.write_string(repository_id);
                    in = invoke(self, out);
                    return in.read_boolean();
                } catch (ApplicationException ex) {
                    throw Assert.fail(ex);
                } catch (RemarshalException ex) {
                    // do nothing - continue loop
                } finally {
                    releaseReply(self, in);
                }
            } else {
                ServantObject so = servant_preinvoke(self, "_is_a", null);
                if (so == null) {
                    continue;
                }
                try {
                    Servant servant = (Servant) so.servant;
                    return servant._is_a(repository_id);
                } finally {
                    servant_postinvoke(self, so);
                }
            }
        }
    }

    public boolean non_existent(org.omg.CORBA.Object self) {
        while (true) {
            if (!is_local(self)) {
                org.omg.CORBA.portable.OutputStream out;
                org.omg.CORBA.portable.InputStream in = null;
                try {
                    out = request(self, "_non_existent", true);
                    in = invoke(self, out);
                    return in.read_boolean();
                } catch (ApplicationException ex) {
                    throw Assert.fail(ex);
                } catch (RemarshalException ex) {
                    // do nothing - continue loop
                } finally {
                    releaseReply(self, in);
                }
            } else {
                ServantObject so = servant_preinvoke(self, "_non_existent", null);
                if (so == null) {
                    continue;
                }
                try {
                    Servant servant = (Servant) so.servant;
                    return servant._non_existent();
                } finally {
                    servant_postinvoke(self, so);
                }
            }
        }
    }

    public boolean is_equivalent(org.omg.CORBA.Object self, org.omg.CORBA.Object rhs) {
        if (rhs == null) return false;
        if (self == rhs) return true;

        if (self instanceof LocalObject || rhs instanceof LocalObject) {
            return false;
        }

        // Direct delegate reference comparison
        Delegate p = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) rhs)._get_delegate();
        if (p == this) {
            return true;
        }

        // Ask the client manager
        synchronized (this) {
            ClientManager clientManager = orbInstance.getClientManager();
            return clientManager.equivalent(origIor, p._OB_origIOR());
        }
    }

    public int hash(org.omg.CORBA.Object self, int maximum) {
        synchronized (this) {
            ClientManager clientManager = orbInstance.getClientManager();
            return clientManager.hash(origIor, maximum);
        }
    }

    public org.omg.CORBA.Request create_request(org.omg.CORBA.Object self,
            org.omg.CORBA.Context ctx, String operation,
            org.omg.CORBA.NVList arg_list, org.omg.CORBA.NamedValue result) {
        Request request = new Request(self, operation, arg_list, result);
        request.ctx(ctx);
        return request;
    }

    public org.omg.CORBA.Request create_request(org.omg.CORBA.Object self,
            org.omg.CORBA.Context ctx, String operation,
            org.omg.CORBA.NVList arg_list, org.omg.CORBA.NamedValue result,
            org.omg.CORBA.ExceptionList excepts,
            org.omg.CORBA.ContextList contexts) {
        Request request = new Request(self, operation, arg_list, result, excepts, contexts);
        request.ctx(ctx);

        return request;
    }

    public org.omg.CORBA.Request request(org.omg.CORBA.Object self, String operation) {
        return new Request(self, operation);
    }

    public org.omg.CORBA.portable.OutputStream request(org.omg.CORBA.Object self, String operation, boolean responseExpected) {
        // Here's how retry works with portable stubs:
        //
        // 1) request() calls getRetry(), which removes any existing
        // entry from TSS or returns a new RetryInfo if none was found
        //
        // 2) request() loops until a request can be setup by the
        // DowncallStub (i.e., a connection is successfully
        // established). There is no need to update TSS in this
        // loop.
        //
        // 3) Upon success, the current RetryInfo is stored by the
        // OutputStream. The invoke() method will retrieve this
        // from the stream. We don't want to use TSS between
        // retry() and invoke(), since a marshalling exception
        // could occur in the stub and potentially cause a leak.
        //
        // Continued in invoke()...
        RetryInfo info = threadSpecificRetryInfo.get();
        while (true) {
            try {
                DowncallStub downcallStub = _OB_getDowncallStub();
                OutputStream out = downcallStub.setupRequest(self, operation, responseExpected);
                out._OB_delegateContext(info);
                return out;
            } catch (Exception ex) {
                _OB_handleException(ex, info, false);
            }
        }
    }

    public org.omg.CORBA.portable.InputStream invoke(org.omg.CORBA.Object self, org.omg.CORBA.portable.OutputStream out) throws ApplicationException, RemarshalException {
        // Continuing the discussion of retry from request() above...
        //
        // 4) invoke() retrieves the current RetryInfo from the
        // OutputStream. At this point, there should be no
        // entry in TSS for this thread.
        //
        // 5) If a retry is necessary, we must set the current
        // RetryInfo in TSS so that the next call to request()
        // can retrieve it.
        OutputStream outImpl = (OutputStream) out;
        RetryInfo info = (RetryInfo) outImpl._OB_delegateContext();
        try {
            DowncallStub downcallStub = _OB_getDowncallStub();
            return downcallStub.invoke(self, outImpl);
        } catch (ApplicationException ex) {
            logger.log(FINE, "Received ApplicationException for request", ex);
            throw ex;
        } catch (RemarshalException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.log(FINE, "Received unexpected exception for request", ex);
            _OB_handleException(ex, info, false);
            threadSpecificRetryInfo.set(info);
            // If we reach this point, then we need to reinvoke
            throw (RemarshalException)new RemarshalException().initCause(ex);
        }
    }

    public void releaseReply(org.omg.CORBA.Object self, org.omg.CORBA.portable.InputStream in) { }

    public Policy get_policy(org.omg.CORBA.Object self, int policy_type) {
        Policy policy = _OB_getPolicy(policy_type);
        if (policy == null) throw new INV_POLICY(describeInvPolicy(MinorNoPolicy), MinorNoPolicy, COMPLETED_NO);
        return policy;
    }

    public DomainManager[] get_domain_managers(
            org.omg.CORBA.Object self) {
        throw new NO_IMPLEMENT();
    }

    public org.omg.CORBA.Object set_policy_override(org.omg.CORBA.Object self, Policy[] np, SetOverrideType set_add) {
        // Check for duplicate policy type
        final Set<Integer> policyTypes = new HashSet<>();
        for (Policy policy: np) {
            if (policyTypes.add(policy.policy_type())) continue;
            throw new BAD_PARAM(describeBadParam(MinorDuplicatePolicyType), MinorDuplicatePolicyType, COMPLETED_NO);
        }

        // Compute the new policy list
        final Policy[] newPolicies;
        if (set_add == SetOverrideType.SET_OVERRIDE) {
            newPolicies = Arrays.copyOf(np, np.length);
        } else { // ADD_OVERRIDE
            // use a linked hash map to preserve initial insertion order
            final Map<Integer,Policy> policiesByType = new LinkedHashMap<>();
            // first add all the existing policies
            for (Policy p: policyList.value) policiesByType.put(p.policy_type(), p);
            // now overwrite/add the new policies
            for (Policy p : np) policiesByType.put(p.policy_type(), p);
            // copy the policies that survived into a new array
            newPolicies = policiesByType.values().toArray(EMPTY_POLICY_ARRAY);
        }

        final Delegate p = new Delegate(orbInstance, ior, origIor, newPolicies);
        // Create new object, set the delegate and return
        final StubForObject obj = new StubForObject();
        obj._set_delegate(p);
        return obj;
    }

    public org.omg.CORBA.ORB orb(org.omg.CORBA.Object self) {
        return orbInstance.getORB();
    }

    public boolean is_local(org.omg.CORBA.Object self) {
        if (checkLocal) {
            synchronized (directServantMutex) {
                if (directServant != null && !directServant.deactivated()) {
                    return true;
                }

                POAManagerFactory pmFactory = orbInstance.getPOAManagerFactory();
                POAManagerFactory_impl factory = (POAManagerFactory_impl) pmFactory;
                while (true) {
                    try {
                        directServant = factory._OB_getDirectServant(ior, policyList);
                        break;
                    } catch (LocationForward ex) {
                        synchronized (this) {
                            //
                            // Change the IOR
                            //
                            ior = ex.ior;
                            if (ex.perm) {
                                origIor = ex.ior;
                            }

                            //
                            // Clear the downcall stub
                            //
                            downcallStub_ = null;
                        }
                    }
                }

                // If the servant is collocated, then we remove the entry for this thread from the retry TSS
                if (directServant != null) {
                    // We can only make collocated calls on a servant if
                    // the servant class was loaded by the same class
                    // loader (which may not be the case in application
                    // servers, for example). The only solution is to
                    // consider the servant to be "remote" and marshal
                    // the request.
                    if (directServant.servant.getClass().getClassLoader() == self.getClass().getClassLoader()) {
                        threadSpecificRetryInfo.remove();
                        if (!directServant.locate_request()) {
                            throw new OBJECT_NOT_EXIST();
                        }
                        return true;
                    }
                }

                //
                // Collocated invocations are not possible on this object
                //
                checkLocal = false;
            }
        }
        return false;
    }

    public ServantObject servant_preinvoke(org.omg.CORBA.Object self, String operation, Class expectedType) {
        DirectServant ds;
        synchronized (directServantMutex) {
            if (directServant == null) return null;
            if (directServant.deactivated()) {
                directServant = null;
                return null;
            }
            ds = directServant;
        }

        final ServantObject result = ds.preinvoke(operation);
        if (expectedType == null) return result;
        if (result == null) return null;
        if (expectedType.isInstance(result.servant)) return result;
        throw new BAD_PARAM("Servant class " + result.servant.getClass().getName() + " does not match expected type " + expectedType.getName());
    }

    public void servant_postinvoke(org.omg.CORBA.Object self, ServantObject servant) {
        DirectServant directServant = (DirectServant) servant;
        directServant.postinvoke();
    }

    public String get_codebase(org.omg.CORBA.Object self) {
        // TODO: implement
        return null;
    }

    // ------------------------------------------------------------------
    // Operations from org.omg.CORBA_2_4.Delegate
    // ------------------------------------------------------------------

    public Policy[] get_policy_overrides(org.omg.CORBA.Object self, int[] types) {
        // If no types were supplied we need to return a list of all policies.
        final Policy[] policies = policyList.value;
        if (types.length == 0) return Arrays.copyOf(policies, policies.length);

        List<Policy> list = new ArrayList<>();

        for (Policy policy : policies) {
            for (int type : types) {
                if (policy.policy_type() == type) {
                    list.add(policy);
                    break; // optimisation!
                }
            }
        }

        return list.toArray(EMPTY_POLICY_ARRAY);
    }

    public Policy get_client_policy(org.omg.CORBA.Object self, int type) {
        // TODO: Implement
        return get_policy(self, type);
    }

    public boolean validate_connection(org.omg.CORBA.Object self, PolicyListHolder policies) {
        // TODO: Validate the policies
        RetryInfo info = new RetryInfo();
        while (true) {
            try {
                DowncallStub downcallStub = _OB_getDowncallStub();
                return downcallStub.locate_request();
            } catch (Exception ex) {
                _OB_handleException(ex, info, true);
            }
        }
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Delegate(ORBInstance orbInstance, IOR ior, IOR origIor, Policy...policies) {
        this.orbInstance = orbInstance;
        this.ior = ior;
        this.origIor = origIor;
        this.policyList = new RefCountPolicyList(policies);
    }

    public ORBInstance _OB_ORBInstance() {
        return orbInstance;
    }

    public synchronized IOR _OB_IOR() {
        return ior;
    }

    public synchronized IOR _OB_origIOR() {
        return origIor;
    }

    public synchronized void _OB_marshalOrigIOR(org.omg.CORBA.portable.OutputStream out) {
        IORHelper.write(out, origIor);
    }

    private Policy _OB_getPolicy(int policy_type) {
        for (Policy policy : policyList.value) {
            if (policy.policy_type() == policy_type) return policy;
        }
        return null;
    }

    void _OB_handleException(Exception ex, RetryInfo info, boolean ignoreRebind) {
        try {
            throw ex;
        } catch (LocationForward e) {
            handleLocationForward(e, info, ignoreRebind);
        } catch (FailureException e) {
            handleFailure(e, info);
        } catch (TRANSIENT e) {
            handleTRANSIENT(e, info);
        } catch (SystemException e) {
            logger.log(FINE, "Received SystemException", e);
            throw e;
        } catch (RuntimeException e) {
            logger.log(FINE, "Received RuntimeException", e);
            throw e;
        } catch (Exception shouldNeverHappen) {
            throw Assert.fail(shouldNeverHappen);
        }
    }

    private void handleTRANSIENT(TRANSIENT e, RetryInfo info) {
        info.incrementRetryCount();
        // If it's not safe to retry, throw the exception
        checkRetry(info.getRetry(), e, false);
    }

    private void handleFailure(FailureException e, RetryInfo info) {
        if (e.incrementRetry) info.incrementRetryCount();
        // If it's not safe to retry, throw the exception
        checkRetry(info.getRetry(), e.exception, false);
    }

    private synchronized void handleLocationForward(LocationForward e, RetryInfo info, boolean ignoreRebind) {

        // Check the Rebind Policy
        //
        // TODO: NO_REBIND should raise exception as well if LocationForward changes client effective QoS policies
        if (policyList.rebindMode == NO_RECONNECT.value && !ignoreRebind) {
            throw wrapped(RETRY_LOG, e, "Honouring NO_RECONNECT policy", new Factory<REBIND>(){ public REBIND create() {
                return new REBIND();
            }});
        }

        // Check for a potential infinite forwarding loop.
        // The maximum is currently hard-coded to 10. If
        // this is changed, also change the exception
        // description for the minor code.
        info.incrementHopCount();
        if (info.getHop() > 10) {
            throw wrapped(RETRY_LOG, e, "Exceeded location forward hop count", Transients.LOCATION_FORWARD_TOO_MANY_HOPS);
        }

        // Change the IOR
        ior = e.ior;
        if (e.perm) origIor = e.ior;

        // We need to re-get the downcall stub
        downcallStub_ = null;

        // The object may have changed from remote to local
        checkLocal = true;

        logged(RETRY_LOG, e, "Retrying");
    }

    public synchronized DowncallStub _OB_getDowncallStub() throws LocationForward, FailureException {
        if (downcallStub_ == null) {
            downcallStub_ = new DowncallStub(orbInstance, ior, origIor, policyList);
            if (!downcallStub_.locate_request()) {
                throw new OBJECT_NOT_EXIST();
            }
        }
        return downcallStub_;
    }
}

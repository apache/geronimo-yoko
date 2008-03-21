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
 
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA.INV_POLICY;
import org.omg.CORBA.LocalObject;

//
// Delegate is equivalent to OBCORBA::Object in C++
//
public final class Delegate extends org.omg.CORBA_2_4.portable.Delegate {
    static final Logger logger = Logger.getLogger(Delegate.class.getName());
    //
    // The ORBInstance object
    //
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    //
    // The IOR and the original IOR
    //
    private org.omg.IOP.IOR IOR_;

    private org.omg.IOP.IOR origIOR_;

    //
    // The list of policies
    //
    private org.apache.yoko.orb.OB.RefCountPolicyList policies_;

    //
    // The DowncallStub
    //
    private org.apache.yoko.orb.OB.DowncallStub downcallStub_;

    //
    // The servant for use in collocated invocations (Java only)
    //
    private org.apache.yoko.orb.OBPortableServer.DirectServant directServant_;

    private java.lang.Object directServantMutex_ = new java.lang.Object();

    //
    // If false, the object is "remote", in the sense that its request
    // must be marshalled. If true, we need to check if the object
    // is local.
    //
    private boolean checkLocal_ = true;

    //
    // Thread-specific storage for tracking retries by portable stubs
    //
    private java.util.Hashtable retryTSS_;

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    //
    // Get (and remove) retry information for current thread from TSS
    //
    private synchronized RetryInfo getRetry() {
        if (retryTSS_ != null) {
            Thread t = Thread.currentThread();
            RetryInfo info = (RetryInfo) retryTSS_.remove(t);
            if (info != null) {
                return info;
            }
        }
        return new RetryInfo();
    }

    //
    // Set retry information for current thread in TSS
    //
    private synchronized void setRetry(RetryInfo info) {
        if (retryTSS_ == null) {
            retryTSS_ = new java.util.Hashtable(7);
        }

        Thread t = Thread.currentThread();
        retryTSS_.put(t, info);
    }

    //
    // Check whether it's safe to retry
    //
    private synchronized void checkRetry(int retry,
            org.omg.CORBA.SystemException ex, boolean remote) {
        //
        // We remove the downcall stub, whether we retry or not
        //
        downcallStub_ = null;

        //
        // Reset to the original IOR
        //
        IOR_ = origIOR_;

        //
        // Reset cached state
        //
        directServant_ = null;
        checkLocal_ = true;

        //
        // Get the core trace levels
        //
        org.apache.yoko.orb.OB.CoreTraceLevels coreTraceLevels = orbInstance_
                .getCoreTraceLevels();

        //
        // We only retry upon COMM_FAILURE, TRANSIENT, and NO_RESPONSE
        //
        try {
            throw ex;
        } catch (org.omg.CORBA.COMM_FAILURE e) {
        } catch (org.omg.CORBA.TRANSIENT e) {
        } catch (org.omg.CORBA.NO_RESPONSE e) {
        } catch (org.omg.CORBA.SystemException e) {
            logger.log(java.util.logging.Level.FINE, "System exception during operation", ex); 
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "retry only upon COMM_FAILURE, TRANSIENT "
                        + "and NO_RESPONSE exceptions";
                String exMsg = ex.getMessage();
                if (exMsg != null) {
                    msg += "\n" + exMsg;
                }
                logger.fine("retry: " + msg);
            }
            throw ex;
        }

        //
        // TODO: Check the Rebind Policy - raise REBIND if the policy
        // is set to NO_RECONNECT, but only if this object reference
        // was previously bound
        //
        // if(policies_.rebindMode == org.omg.Messaging.NO_RECONNECT.value &&
        // !ignoreRebind)
        // {
        // if(coreTraceLevels.traceRetry() > 0)
        // {
        // String msg = "can't try again because NO_RECONNECT is set";
        // String exMsg = ex.getMessage();
        // if(exMsg != null)
        // msg += "\n" + exMsg;
        // Logger logger = orbInstance_.getLogger();
        // logger.trace("retry", msg);
        // }
        // throw new org.omg.CORBA.REBIND();
        // }

        //
        // Check policy to see if we should retry on remote exceptions
        //
        if (!policies_.retry.remote && remote) {
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "retry only upon locally raised exceptions\n";
                String exMsg = ex.getMessage();
                if (exMsg != null) {
                    msg += "\n" + exMsg;
                }
                logger.fine("retry " + msg);
            }
            throw ex;
        }

        //
        // Only try maximum number of times. Zero indicates infinite retry.
        //
        if (policies_.retry.max != 0 && retry > policies_.retry.max) {
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "can't try again, because I "
                        + "tried maximum times already";
                String exMsg = ex.getMessage();
                if (exMsg != null) {
                    msg += "\n" + exMsg;
                }
                logger.fine("retry " + msg);
            }
            throw ex;
        }

        //
        // We can't retry if RETRY_NEVER is set
        //
        if (policies_.retry.mode == org.apache.yoko.orb.OB.RETRY_NEVER.value) {
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "can't try again because the "
                        + "RETRY_NEVER policy is set";
                String exMsg = ex.getMessage();
                if (exMsg != null) {
                    msg += "\n" + exMsg;
                }
                logger.fine("retry " + msg);
            }
            throw ex;
        }

        //
        // We can't retry if RETRY_STRICT is set and the completion
        // status is not COMPLETED_NO
        //
        if (policies_.retry.mode == org.apache.yoko.orb.OB.RETRY_STRICT.value
                && ex.completed != org.omg.CORBA.CompletionStatus.COMPLETED_NO) {
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "can't try again, because the "
                        + "RETRY_STRICT policy is set\n"
                        + "and completion status is not " + "COMPLETED_NO";
                String exMsg = ex.getMessage();
                if (exMsg != null) {
                    msg += "\n" + exMsg;
                }
                logger.fine("retry " + msg);
            }
            throw ex;
        }

        //
        // If a retry interval has been set then wait the specified
        // amount of time
        //
        if (policies_.retry.interval != 0) {
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "next attempt in " + policies_.retry.interval
                        + " milliseconds";
                logger.fine("retry " + msg);
            }
            try {
                Thread.sleep(policies_.retry.interval);
            } catch (java.lang.InterruptedException e) {
            }
        }
    }

    protected void finalize() throws Throwable {
        //
        // DirectServant must be explicitly destroyed in order to
        // make it eligible for garbage collection
        //
        if (directServant_ != null) {
            directServant_.destroy();
        }
        super.finalize();
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    /**
     * @deprecated Deprecated by CORBA 2.3.
     */
    public org.omg.CORBA.InterfaceDef get_interface(org.omg.CORBA.Object self) {
        while (true) {
            if (!is_local(self)) {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try {
                    out = request(self, "_interface", true);
                    in = invoke(self, out);
                    org.omg.CORBA.InterfaceDef def = org.omg.CORBA.InterfaceDefHelper.read(in);
                    return def;
                } catch (org.omg.CORBA.portable.ApplicationException ex) {
                    org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                } catch (org.omg.CORBA.portable.RemarshalException ex) {
                    // do nothing - continue loop
                } finally {
                    releaseReply(self, in);
                }
            } else {
                org.omg.CORBA.portable.ServantObject so = servant_preinvoke(
                        self, "_interface", null);
                if (so == null)
                    continue;
                try {
                    org.omg.PortableServer.Servant servant = (org.omg.PortableServer.Servant) so.servant;
                    return servant._get_interface();
                } finally {
                    servant_postinvoke(self, so);
                }
            }
        }
    }

    public org.omg.CORBA.Object get_interface_def(org.omg.CORBA.Object self) {
        return get_interface(self);
    }

    public org.omg.CORBA.Object duplicate(org.omg.CORBA.Object self) {
        return self;
    }

    public void release(org.omg.CORBA.Object self) {
    }

    public boolean is_a(org.omg.CORBA.Object self, String repository_id) {
        //
        // Check IDL:omg.org/CORBA/Object:1.0
        //
        if (repository_id.equals("IDL:omg.org/CORBA/Object:1.0")) {
            return true;
        }

        //
        // Check all other ids
        //
        org.omg.CORBA.portable.ObjectImpl o = (org.omg.CORBA.portable.ObjectImpl) self;

        String[] ids = o._ids();
        for (int i = 0; i < ids.length; i++) {
            if (repository_id.equals(ids[i])) {
                return true;
            }
        }

        //
        // Check the type_id in the IOR and the original IOR
        //
        synchronized (this) {
            if (repository_id.equals(IOR_.type_id) || repository_id.equals(origIOR_.type_id)) {
                return true;
            }
        }

        //
        // TODO: Some kind of is-a cache should be consulted here
        //

        //
        // Check implementation object
        //
        while (true) {
            if (!is_local(self)) {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try {
                    out = request(self, "_is_a", true);
                    out.write_string(repository_id);
                    in = invoke(self, out);
                    return in.read_boolean();
                } catch (org.omg.CORBA.portable.ApplicationException ex) {
                    org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                } catch (org.omg.CORBA.portable.RemarshalException ex) {
                    // do nothing - continue loop
                } finally {
                    releaseReply(self, in);
                }
            } else {
                org.omg.CORBA.portable.ServantObject so = servant_preinvoke(
                        self, "_is_a", null);
                if (so == null) {
                    continue;
                }
                try {
                    org.omg.PortableServer.Servant servant = (org.omg.PortableServer.Servant) so.servant;
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
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try {
                    out = request(self, "_non_existent", true);
                    in = invoke(self, out);
                    return in.read_boolean();
                } catch (org.omg.CORBA.portable.ApplicationException ex) {
                    org.apache.yoko.orb.OB.Assert._OB_assert(ex);
                } catch (org.omg.CORBA.portable.RemarshalException ex) {
                    // do nothing - continue loop
                } finally {
                    releaseReply(self, in);
                }
            } else {
                org.omg.CORBA.portable.ServantObject so = servant_preinvoke(self, "_non_existent", null);
                if (so == null) {
                    continue;
                }
                try {
                    org.omg.PortableServer.Servant servant = (org.omg.PortableServer.Servant) so.servant;
                    return servant._non_existent();
                } finally {
                    servant_postinvoke(self, so);
                }
            }
        }
    }

    public boolean is_equivalent(org.omg.CORBA.Object self,
            org.omg.CORBA.Object rhs) {
        //
        // Check for nil reference
        //
        if (rhs == null) {
            return false;
        }

        //
        // Direct object reference comparison
        //
        if (self == rhs) {
            return true;
        }

        //
        // Locality-constrained objects are never equivalent if the
        // reference comparisons fail
        //
        if (self instanceof org.omg.CORBA.LocalObject || rhs instanceof org.omg.CORBA.LocalObject) {
            return false;
        }

        //
        // Direct delegate reference comparison
        //
        Delegate p = (Delegate) ((org.omg.CORBA.portable.ObjectImpl) rhs)._get_delegate();
        if (p == this) {
            return true;
        }

        //
        // Ask the client manager
        //
        synchronized (this) {
            org.apache.yoko.orb.OB.ClientManager clientManager = orbInstance_.getClientManager();
            return clientManager.equivalent(origIOR_, p._OB_origIOR());
        }
    }

    public int hash(org.omg.CORBA.Object self, int maximum) {
        //
        // Ask the client manager
        //
        synchronized (this) {
            org.apache.yoko.orb.OB.ClientManager clientManager = orbInstance_.getClientManager();
            return clientManager.hash(origIOR_, maximum);
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

    public org.omg.CORBA.Request request(org.omg.CORBA.Object self,
            String operation) {
        return new Request(self, operation);
    }

    public org.omg.CORBA.portable.OutputStream request(
            org.omg.CORBA.Object self, String operation,
            boolean responseExpected) {
        //
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
        //
        RetryInfo info = getRetry();
        while (true) {
            try {
                org.apache.yoko.orb.OB.DowncallStub downcallStub = _OB_getDowncallStub(self);
                org.apache.yoko.orb.CORBA.OutputStream out = downcallStub
                        .setupRequest(self, operation, responseExpected);
                out._OB_delegateContext(info);
                return out;
            } catch (Exception ex) {
                _OB_handleException(self, ex, info, false, false);
            }
        }
    }

    public org.omg.CORBA.portable.InputStream invoke(org.omg.CORBA.Object self,
            org.omg.CORBA.portable.OutputStream out)
            throws org.omg.CORBA.portable.ApplicationException,
            org.omg.CORBA.portable.RemarshalException {
        //
        // Continuing the discussion of retry from request() above...
        //
        // 4) invoke() retrieves the current RetryInfo from the
        // OutputStream. At this point, there should be no
        // entry in TSS for this thread.
        //
        // 5) If a retry is necessary, we must set the current
        // RetryInfo in TSS so that the next call to request()
        // can retrieve it.
        //
        org.apache.yoko.orb.CORBA.OutputStream obout = (org.apache.yoko.orb.CORBA.OutputStream) out;
        RetryInfo info = (RetryInfo) obout._OB_delegateContext();
        try {
            org.apache.yoko.orb.OB.DowncallStub downcallStub = _OB_getDowncallStub(self);
            org.omg.CORBA.portable.InputStream in = downcallStub.invoke(self, obout);
            return in;
        } catch (org.omg.CORBA.portable.ApplicationException ex) {
            logger.log(java.util.logging.Level.FINE, "Received ApplicationException for request", ex); 
            throw ex;
        } catch (org.omg.CORBA.portable.RemarshalException ex) {
            // fall through
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.FINE, "Received unexpected exception for request", ex); 
            _OB_handleException(self, ex, info, false, true);
        }

        //
        // If we reach this point, then we need to reinvoke
        //
        throw new org.omg.CORBA.portable.RemarshalException();
    }

    public void releaseReply(org.omg.CORBA.Object self,
            org.omg.CORBA.portable.InputStream in) {
    }

    public org.omg.CORBA.Policy get_policy(org.omg.CORBA.Object self,
            int policy_type) {
        org.omg.CORBA.Policy policy = _OB_getPolicy(policy_type);
        if (policy == null) {
            throw new org.omg.CORBA.INV_POLICY(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeInvPolicy(org.apache.yoko.orb.OB.MinorCodes.MinorNoPolicy),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoPolicy,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        return policy;
    }

    public org.omg.CORBA.DomainManager[] get_domain_managers(
            org.omg.CORBA.Object self) {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public org.omg.CORBA.Object set_policy_override(org.omg.CORBA.Object self,
            org.omg.CORBA.Policy[] np, org.omg.CORBA.SetOverrideType set_add) {
        //
        // Check for duplicate policy type
        //
        if (np.length > 1) {
            for (int i = 0; i < np.length - 1; i++) {
                for (int j = i + 1; j < np.length; j++) {
                    if (np[i].policy_type() == np[j].policy_type()) {
                        throw new org.omg.CORBA.BAD_PARAM(
                                org.apache.yoko.orb.OB.MinorCodes
                                        .describeBadParam(org.apache.yoko.orb.OB.MinorCodes.MinorDuplicatePolicyType),
                                org.apache.yoko.orb.OB.MinorCodes.MinorDuplicatePolicyType,
                                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                    }
                }
            }
        }

        //
        // Create the new policy list
        //
        org.omg.CORBA.Policy[] newPolicies;

        if (set_add == org.omg.CORBA.SetOverrideType.SET_OVERRIDE) {
            newPolicies = new org.omg.CORBA.Policy[np.length];
            System.arraycopy(np, 0, newPolicies, 0, np.length);
        } else // ADD_OVERRIDE
        {
            java.util.Vector v = new java.util.Vector();
            for (int i = 0; i < policies_.value.length; i++) {
                v.addElement(policies_.value[i]);
            }

            for (int i = 0; i < np.length; i++) {
                int len = v.size();
                int j;

                for (j = 0; j < len; j++) {
                    org.omg.CORBA.Policy policy = (org.omg.CORBA.Policy) v.elementAt(j);
                    if (policy.policy_type() == np[i].policy_type()) {
                        break;
                    }
                }

                if (j < len) {
                    v.setElementAt(np[i], j);
                }
                else {
                    v.addElement(np[i]);
                }
            }

            newPolicies = new org.omg.CORBA.Policy[v.size()];
            v.copyInto(newPolicies);
        }

        //
        // Create and initialize a new delegate
        //
        org.apache.yoko.orb.OB.RefCountPolicyList policyList = new org.apache.yoko.orb.OB.RefCountPolicyList(
                newPolicies);
        Delegate p = new Delegate(orbInstance_, IOR_, origIOR_, policyList);

        //
        // Create new object, set the delegate and return
        //
        StubForObject obj = new StubForObject();
        obj._set_delegate(p);

        return obj;
    }

    public org.omg.CORBA.ORB orb(org.omg.CORBA.Object self) {
        return orbInstance_.getORB();
    }

    public boolean is_local(org.omg.CORBA.Object self) {
        if (checkLocal_) {
            synchronized (directServantMutex_) {
                if (directServant_ != null && !directServant_.deactivated()) {
                    return true;
                }

                org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactory = orbInstance_
                        .getPOAManagerFactory();
                org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl factory = (org.apache.yoko.orb.OBPortableServer.POAManagerFactory_impl) pmFactory;
                while (true) {
                    try {
                        directServant_ = factory._OB_getDirectServant(IOR_, policies_);
                        break;
                    } catch (org.apache.yoko.orb.OB.LocationForward ex) {
                        synchronized (this) {
                            //
                            // Change the IOR
                            //
                            IOR_ = ex.ior;
                            if (ex.perm) {
                                origIOR_ = ex.ior;
                            }

                            //
                            // Clear the downcall stub
                            //
                            downcallStub_ = null;
                        }
                    }
                }

                //
                // If the servant is collocated, then we remove the entry
                // for this thread from the retry TSS
                //
                if (directServant_ != null) {
                    //
                    // We can only make collocated calls on a servant if
                    // the servant class was loaded by the same class
                    // loader (which may not be the case in application
                    // servers, for example). The only solution is to
                    // consider the servant to be "remote" and marshal
                    // the request.
                    //
                    if (directServant_.servant.getClass().getClassLoader() == self
                            .getClass().getClassLoader()) {
                        getRetry();
                        if (!directServant_.locate_request()) {
                            throw new org.omg.CORBA.OBJECT_NOT_EXIST();
                        }
                        return true;
                    }
                }

                //
                // Collocated invocations are not possible on this object
                //
                checkLocal_ = false;
            }
        }
        return false;
    }

    public org.omg.CORBA.portable.ServantObject servant_preinvoke(
            org.omg.CORBA.Object self, String operation, Class expectedType)  
  {  
        org.omg.CORBA.portable.ServantObject result = null;

        org.apache.yoko.orb.OBPortableServer.DirectServant ds = directServant_;
        if (ds != null) {
            if (!ds.deactivated()) {
            	result = ds.preinvoke(operation);

                if (result != null && expectedType != null
                        && !expectedType.isInstance(result.servant)) {
                    throw new org.omg.CORBA.BAD_PARAM("Servant class "
                            + result.servant.getClass().getName()
                            + " does not match expected type "
                            + expectedType.getName());
                }
            } else {
                synchronized (directServantMutex_) {
                    directServant_ = null;
                }
            }
        }

        return result;
    }

    public void servant_postinvoke(org.omg.CORBA.Object self,
            org.omg.CORBA.portable.ServantObject servant) {
        org.apache.yoko.orb.OBPortableServer.DirectServant directServant = (org.apache.yoko.orb.OBPortableServer.DirectServant) servant;
        directServant.postinvoke();
    }

    public String get_codebase(org.omg.CORBA.Object self) {
        // TODO: implement
        return null;
    }

    // ------------------------------------------------------------------
    // Operations from org.omg.CORBA_2_4.Delegate
    // ------------------------------------------------------------------

    public org.omg.CORBA.Policy[] get_policy_overrides(
            org.omg.CORBA.Object self, int[] types) {
        // 
        // If no types we supplied we need to return a list of
        // all policies.
        //
        if (types.length == 0) {
            org.omg.CORBA.Policy[] all = new org.omg.CORBA.Policy[policies_.value.length];
            System.arraycopy(policies_.value, 0, all, 0, policies_.value.length);

            return all;
        }

        java.util.Vector policies = new java.util.Vector();

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < policies_.value.length; j++) {
                if (policies_.value[j].policy_type() == types[i]) {
                    policies.addElement(policies_.value[j]);
                }
            }
        }

        org.omg.CORBA.Policy[] result = new org.omg.CORBA.Policy[policies.size()];
        policies.copyInto(result);
        return result;
    }

    public org.omg.CORBA.Policy get_client_policy(org.omg.CORBA.Object self, int type) {
        //
        // TODO: Implement
        //
        return get_policy(self, type);
    }

    public boolean validate_connection(org.omg.CORBA.Object self,
            org.omg.CORBA.PolicyListHolder policies) {
        //
        // TODO: Validate the policies
        //
        RetryInfo info = new RetryInfo();
        while (true) {
            try {
                org.apache.yoko.orb.OB.DowncallStub downcallStub = _OB_getDowncallStub(null);
                return downcallStub.locate_request();
            } catch (Exception ex) {
                _OB_handleException(null, ex, info, true, false);
            }
        }
    }

    // ------------------------------------------------------------------
    // Additional Yoko specific functions
    // ------------------------------------------------------------------

    public org.apache.yoko.orb.OCI.ConnectorInfo get_oci_connector_info() {
        if (is_local(null))
            return null;

        RetryInfo info = new RetryInfo();
        while (true) {
            try {
                org.apache.yoko.orb.OB.DowncallStub downcallStub = _OB_getDowncallStub(null);
                return downcallStub.get_oci_connector_info();
            } catch (Exception ex) {
                _OB_handleException(null, ex, info, false, false);
            }
        }
    }

    public org.apache.yoko.orb.OCI.TransportInfo get_oci_transport_info() {
        if (is_local(null)) {
            return null;
        }

        RetryInfo info = new RetryInfo();
        while (true) {
            try {
                org.apache.yoko.orb.OB.DowncallStub downcallStub = _OB_getDowncallStub(null);
                return downcallStub.get_oci_transport_info();
            } catch (Exception ex) {
                _OB_handleException(null, ex, info, false, false);
            }
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Delegate(org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.omg.IOP.IOR ior, org.omg.IOP.IOR origIOR,
            org.apache.yoko.orb.OB.RefCountPolicyList policies) {
        //
        // Save the ORBInstance object
        //
        orbInstance_ = orbInstance;

        //
        // Save the IOR
        //
        IOR_ = ior;
        origIOR_ = origIOR;

        //
        // Save the policies
        //
        policies_ = policies;
    }

    public org.apache.yoko.orb.OB.ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }

    public synchronized org.omg.IOP.IOR _OB_IOR() {
        return IOR_;
    }

    public synchronized org.omg.IOP.IOR _OB_origIOR() {
        return origIOR_;
    }

    public synchronized void _OB_marshalOrigIOR(
            org.omg.CORBA.portable.OutputStream out) {
        org.omg.IOP.IORHelper.write(out, origIOR_);
    }

    public org.apache.yoko.orb.OB.RefCountPolicyList _OB_policies() {
        return policies_;
    }

    public org.omg.CORBA.Policy _OB_getPolicy(int policy_type) {
        for (int i = 0; i < policies_.value.length; i++) {
            if (policies_.value[i].policy_type() == policy_type) {
                return policies_.value[i];
            }
        }
        return null;
    }

    void _OB_handleException(org.omg.CORBA.Object self, Exception ex,
            RetryInfo info, boolean ignoreRebind) {
        _OB_handleException(self, ex, info, ignoreRebind, false);
    }

    void _OB_handleException(org.omg.CORBA.Object self, Exception ex,
            RetryInfo info, boolean ignoreRebind, boolean useTSS) {
        
        try {
            throw ex;
        } catch (org.apache.yoko.orb.OB.LocationForward e) {
            synchronized (this) {
                org.apache.yoko.orb.OB.CoreTraceLevels coreTraceLevels = orbInstance_
                        .getCoreTraceLevels();

                //
                // Check the Rebind Policy
                //
                // TODO: NO_REBIND should raise exception as well if
                // LocationForward changes client effective QoS policies
                //
                if (policies_.rebindMode == org.omg.Messaging.NO_RECONNECT.value
                        && !ignoreRebind) {
                    if (coreTraceLevels.traceRetry() > 0) {
                        logger.fine("retry: can't try again, because "
                                + "NO_RECONNECT prevents a transparent "
                                + "location forward");
                    }
                    throw new org.omg.CORBA.REBIND();
                }

                //
                // Check for a potential infinite forwarding loop.
                // The maximum is currently hard-coded to 10. If
                // this is changed, also change the exception
                // description for the minor code.
                //
                info.hop++;
                if (info.hop > 10) {
                    if (coreTraceLevels.traceRetry() > 0) {
                        logger.fine("retry: location forward hop count exceeded");
                    }

                    throw new org.omg.CORBA.TRANSIENT(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorLocationForwardHopCountExceeded),
                            org.apache.yoko.orb.OB.MinorCodes.MinorLocationForwardHopCountExceeded,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                }

                //
                //
                // Change the IOR
                //
                IOR_ = e.ior;
                if (e.perm) {
                    origIOR_ = e.ior;
                }

                //
                // We need to reget the downcall stub
                //
                downcallStub_ = null;

                //
                // The object may have changed from remote to local
                //
                checkLocal_ = true;

                if (coreTraceLevels.traceRetry() > 0) {
                    logger.fine("retry:  trying again because of location forward");
                }
            }

            if (useTSS) {
                setRetry(info);
            }
        } catch (org.apache.yoko.orb.OB.FailureException e) {
            info.retry++;

            //
            // If it's not safe to retry, throw the exception
            //
            checkRetry(info.retry, e.exception, false);

            org.apache.yoko.orb.OB.CoreTraceLevels coreTraceLevels = orbInstance_
                    .getCoreTraceLevels();
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "trying again (" + info.retry
                        + ") because of failure";
                String exMsg = e.exception.getMessage();
                if (exMsg != null) {
                    msg += "\n" + exMsg;
                }
                logger.fine("retry: " + msg);
            }

            if (useTSS) {
                setRetry(info);
            }
        } catch (org.omg.CORBA.TRANSIENT e) {
            info.retry++;

            //
            // If it's not safe to retry, throw the exception
            //
            checkRetry(info.retry, e, true);

            org.apache.yoko.orb.OB.CoreTraceLevels coreTraceLevels = orbInstance_
                    .getCoreTraceLevels();
            if (coreTraceLevels.traceRetry() > 0) {
                String msg = "trying again (" + info.retry
                        + ") because server sent a TRANSIENT " + "exception";
                String exMsg = e.getMessage();
                if (exMsg != null) {
                    msg += "\n" + exMsg;
                }
                logger.fine("retry: " + msg);
            }

            if (useTSS) {
                setRetry(info);
            }
        } catch (org.omg.CORBA.SystemException e) {
            logger.log(java.util.logging.Level.FINE, "Received SystemException", e); 
            throw e;
        } catch (org.omg.CORBA.UserException e) {
            org.apache.yoko.orb.OB.Assert._OB_assert(e);     // should never
                                                                // happen
        } catch (java.lang.RuntimeException e) {
            logger.log(java.util.logging.Level.FINE, "Received RuntimeException", e); 
            throw e;
        } catch (java.lang.Exception e) {
            org.apache.yoko.orb.OB.Assert._OB_assert(e);     // should never
                                                                // happen
        }
    }

    public synchronized org.apache.yoko.orb.OB.DowncallStub _OB_getDowncallStub(
            org.omg.CORBA.Object self)
            throws org.apache.yoko.orb.OB.LocationForward,
            org.apache.yoko.orb.OB.FailureException {
        if (downcallStub_ == null) {
            downcallStub_ = new org.apache.yoko.orb.OB.DowncallStub(orbInstance_, IOR_, origIOR_, policies_);
            if (!downcallStub_.locate_request()) {
                throw new org.omg.CORBA.OBJECT_NOT_EXIST();
            }
        }
        return downcallStub_;
    }

    public synchronized void _OB_closeConnection(boolean terminate) {
        if (downcallStub_ == null) {
            return;
        }
        downcallStub_._OB_closeConnection(terminate);
        downcallStub_ = null;
    }
}

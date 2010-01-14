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

package org.apache.yoko.orb.OBPortableServer;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OBPortableServer.COMMUNICATIONS_CONCURRENCY_POLICY_ID;
import org.apache.yoko.orb.OBPortableServer.COMMUNICATIONS_CONCURRENCY_POLICY_REACTIVE;
import org.apache.yoko.orb.OBPortableServer.COMMUNICATIONS_CONCURRENCY_POLICY_THREADED;
import org.apache.yoko.orb.OBPortableServer.CommunicationsConcurrencyPolicy;
import org.apache.yoko.orb.OBPortableServer.CommunicationsConcurrencyPolicyHelper;
import org.apache.yoko.orb.OBPortableServer.GIOPVersionPolicy;
import org.apache.yoko.orb.OBPortableServer.GIOPVersionPolicyHelper;
import org.apache.yoko.orb.OBPortableServer.GIOP_VERSION_POLICY_1_0;
import org.apache.yoko.orb.OBPortableServer.GIOP_VERSION_POLICY_1_1;
import org.apache.yoko.orb.OBPortableServer.GIOP_VERSION_POLICY_1_2;
import org.apache.yoko.orb.OBPortableServer.GIOP_VERSION_POLICY_ID;
import org.apache.yoko.orb.OBPortableServer.POAManager;
import org.omg.PortableServer.POAManagerPackage.*;

final public class POAManager_impl extends org.omg.CORBA.LocalObject implements POAManager {
    static final Logger logger = Logger.getLogger(POAManager_impl.class.getName());
    
    //
    // The ORBInstance
    //
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    //
    // Set of associated POAs
    //
    //
    // Set of connected POAs
    //
    private java.util.Hashtable poas_;

    //
    // The POAManager state
    //
    private org.omg.PortableServer.POAManagerPackage.State state_;

    //
    // The OCI acceptors
    //
    private org.apache.yoko.orb.OCI.Acceptor[] acceptors_;

    //
    // The server manager
    //
    private org.apache.yoko.orb.OB.ServerManager serverManager_;

    //
    // The POAManager id
    //
    private String id_;

    //
    // The POAManager Adapter Manager id
    //
    String adapterManagerId_;

    //
    // The OAInterface
    //
    private org.apache.yoko.orb.OB.OAInterface oaInterface_;

    //
    // The GIOP version
    //
    private org.omg.GIOP.Version version_ = new org.omg.GIOP.Version();

    //
    // The boot manager implementation
    //
    private org.apache.yoko.orb.OB.BootManager_impl bootManagerImpl_;

    //
    // The POALocator
    //
    private POALocator poaLocator_;

    //
    // The Server Id
    //
    private String serverId_;

    // ------------------------------------------------------------------
    // POAManager_impl private and protected member implementations
    // ------------------------------------------------------------------

    //
    // If we're in the context of a method invocation returns true,
    // false otherwise.
    //
    private boolean isInORBUpcall() {
        //
        // Find out whether we're inside a method invocation
        //
        boolean inInvocation = false;
        try {
            org.apache.yoko.orb.OB.InitialServiceManager initialServiceManager = orbInstance_
                    .getInitialServiceManager();
            org.omg.CORBA.Object o = initialServiceManager
                    .resolveInitialReferences("POACurrent");
            org.apache.yoko.orb.PortableServer.Current_impl current = (org.apache.yoko.orb.PortableServer.Current_impl) o;

            inInvocation = current._OB_inUpcall();
            if (inInvocation) {
                //
                // Check whether or not the request is dispatched in this
                // POAManager's ORB or another ORB.
                //
                try {
                    POA_impl p = (POA_impl) current.get_POA();
                    inInvocation = (p._OB_ORBInstance() == orbInstance_);
                } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
                }
            }
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
        } catch (ClassCastException ex) {
            // Cast to Current_impl failed - ignore
        }

        return inInvocation;
    }

    //
    // Wait for all pending requests to complete
    //
    private void waitPendingRequests() {
        //
        // Wait for all pending requests from all POAs to complete
        //
        java.util.Enumeration keys = poas_.keys();
        while (keys.hasMoreElements()) {
            POA_impl poaImpl = (POA_impl) poas_.get(keys.nextElement());
            poaImpl._OB_waitPendingRequests();
        }
    }

    //
    // Etherealize each of the servants associated with each POA
    //
    private void etherealizePOAs() {
        try {
            org.apache.yoko.orb.OB.InitialServiceManager initialServiceManager = orbInstance_
                    .getInitialServiceManager();
            org.omg.CORBA.Object o = initialServiceManager
                    .resolveInitialReferences("RootPOA");
            org.apache.yoko.orb.OBPortableServer.POA_impl rootPOA = (org.apache.yoko.orb.OBPortableServer.POA_impl) o;

            //
            // Etherealize recursively from the RootPOA and only POAs
            // associated to this POAManager.
            //
            rootPOA._OB_etherealize(this);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
        }
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public synchronized void activate() throws AdapterInactive {
        logger.fine("Activating POAManager " + id_ + " current state is " + state_); 
        //
        // If the POA manager is in inactive state then raise the
        // AdapterInactive exception
        //
        if (state_ == State.INACTIVE) {
            throw new AdapterInactive();
        }

        if (state_ == State.ACTIVE) {
            logger.fine("POAManager already active, returning"); 
            return;
        }

        //
        // Switch to the active state.
        //
        state_ = State.ACTIVE;

        //
        // Notify all of a state transition
        //
        notifyAll();

        //
        // Activate the server manager
        //
        serverManager_.activate();

        //
        // Tell the OAInterface to accept requests
        //
        oaInterface_.activate();

        //
        // Call the state change interceptor
        //
        org.apache.yoko.orb.OB.PIManager piManager = orbInstance_
                .getPIManager();
        piManager.adapterManagerStateChange(adapterManagerId_,
                _OB_getAdapterState());
    }

    public void hold_requests(boolean waitCompletion) throws AdapterInactive {
        synchronized (this) {
            //
            // If the POA manager is in inactive state then raise the
            // AdapterInactive exception
            //
            if (state_ == State.INACTIVE)
                throw new AdapterInactive();

            if (state_ == State.HOLDING)
                return;

            if (waitCompletion && isInORBUpcall()) {
                throw new org.omg.CORBA.BAD_INV_ORDER("Invocation in progress",
                        0, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            //
            // Switch to the holding state.
            //
            state_ = State.HOLDING;

            //
            // Notify all of a state transition
            //
            notifyAll();

            //
            // Ask the server manager to hold
            //
            serverManager_.hold();

            //
            // Tell the OAInterface to accept requests
            //
            oaInterface_.activate();

            //
            // Call the state change interceptor
            //
            org.apache.yoko.orb.OB.PIManager piManager = orbInstance_
                    .getPIManager();
            piManager.adapterManagerStateChange(adapterManagerId_,
                    _OB_getAdapterState());
        }

        //
        // Wait for all pending requests to complete, if asked
        //
        if (waitCompletion)
            waitPendingRequests();
    }

    public void discard_requests(boolean waitCompletion) throws AdapterInactive {
        synchronized (this) {
            //
            // If the POA manager is in inactive state then raise the
            // AdapterInactive exception
            //
            if (state_ == State.INACTIVE)
                throw new AdapterInactive();

            if (state_ == State.DISCARDING)
                return;

            if (waitCompletion && isInORBUpcall()) {
                throw new org.omg.CORBA.BAD_INV_ORDER("Invocation in progress",
                        0, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            //
            // Switch to the discarding state.
            //
            state_ = State.DISCARDING;

            //
            // Notify all of a state transition
            //
            notifyAll();

            //
            // Tell the OAInterface to discard requests
            //
            oaInterface_.discard();

            //
            // Activate the server manager
            //
            serverManager_.activate();

            //
            // Call the state change interceptor
            //
            org.apache.yoko.orb.OB.PIManager piManager = orbInstance_
                    .getPIManager();
            piManager.adapterManagerStateChange(adapterManagerId_,
                    _OB_getAdapterState());
        }

        //
        // Wait for all pending requests to complete, if asked
        //
        if (waitCompletion)
            waitPendingRequests();
    }

    public void deactivate(boolean etherealize, boolean waitCompletion)
            throws AdapterInactive {
        synchronized (this) {
            if (state_ == State.INACTIVE)
                return;

            if (waitCompletion && isInORBUpcall()) {
                throw new org.omg.CORBA.BAD_INV_ORDER("Invocation in progress",
                        0, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            //
            // Destroy the server manager
            //
            serverManager_.destroy();

            //
            // Clear the acceptor sequence
            //
            acceptors_ = null;

            //
            // Set the state to INACTIVE *after* the serverManager_ has
            // been destroyed, to avoid a race condition.
            //
            state_ = State.INACTIVE;

            //
            // Notify all of a state transition
            //
            notifyAll();

            //
            // Call the state change interceptor
            //
            org.apache.yoko.orb.OB.PIManager piManager = orbInstance_
                    .getPIManager();
            piManager.adapterManagerStateChange(adapterManagerId_,
                    _OB_getAdapterState());
        }

        //
        // Wait for all pending requests to complete, if asked
        //
        if (waitCompletion)
            waitPendingRequests();

        //
        // Etherealize each of the servants associated with each POA
        //
        if (etherealize) {
            etherealizePOAs();
        }
    }

    //
    // We'll remove the synchronization at present since this is a
    // simple state variable
    //
    public/* synchronized */State get_state() {
        return state_;
    }

    //
    // Mapping for OBPortableServer::POAManager
    //

    public String get_id() {
        return id_;
    }

    public synchronized org.apache.yoko.orb.OCI.Acceptor[] get_acceptors()
            throws AdapterInactive {
        if (state_ == State.INACTIVE)
            throw new AdapterInactive();

        org.apache.yoko.orb.OCI.Acceptor[] result = new org.apache.yoko.orb.OCI.Acceptor[acceptors_.length];
        System.arraycopy(acceptors_, 0, result, 0, acceptors_.length);
        return result;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    POAManager_impl(org.apache.yoko.orb.OB.ORBInstance orbInstance,
            POALocator poaLocator, String id, String adapterManagerId,
            org.apache.yoko.orb.OCI.Acceptor[] acceptors,
            org.omg.CORBA.Policy[] policies) throws org.omg.CORBA.PolicyError {
        orbInstance_ = orbInstance;
        poas_ = new java.util.Hashtable(63);
        state_ = State.HOLDING;
        acceptors_ = acceptors;
        id_ = id;
        adapterManagerId_ = adapterManagerId;
        poaLocator_ = poaLocator;

        //
        // Set the server id
        //
        serverId_ = orbInstance_.getServerId();
        if (serverId_.length() == 0)
            serverId_ = "_RootPOA";

        //
        // Create the OAInterface
        //
        oaInterface_ = new POAOAInterface_impl(this, orbInstance_);

        //
        // Construct the root name of the property key for this POAManager
        // instance
        //
        String rootKey = "yoko.orb.poamanager." + id_ + ".";
        int rootKeyLen = rootKey.length();
        
        //
        // Get the ORB properties
        //
        java.util.Properties properties = orbInstance.getProperties();

        //
        // The set of properties that the POAManager supports
        //
        final String props[] = { "conc_model", "endpoint", "version" };
        int numProps = props.length;

        //
        // If policies are provided, they will take precedence
        // over the configuration properties.
        //
        CommunicationsConcurrencyPolicy commsPolicy = null;
        GIOPVersionPolicy giopPolicy = null;

        int nPolicies = policies.length;
        if (nPolicies != 0) {
            for (int i = 0; i < nPolicies; ++i) {
                int policyType = policies[i].policy_type();
                if (policyType == COMMUNICATIONS_CONCURRENCY_POLICY_ID.value) {
                    commsPolicy = CommunicationsConcurrencyPolicyHelper
                            .narrow(policies[i]);
                } else if (policyType == GIOP_VERSION_POLICY_ID.value) {
                    giopPolicy = GIOPVersionPolicyHelper.narrow(policies[i]);
                } else {
                    throw new org.omg.CORBA.PolicyError(
                            org.omg.CORBA.BAD_POLICY_TYPE.value);
                }
            }

        }

        //
        // Check over the POAManager properties and find out whether an
        // unknown property is present
        //
        java.util.Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();

            //
            // Ignore yoko.orb.oa.thread_pool
            //
            if (key.equals("yoko.orb.oa.thread_pool"))
                continue;

            //
            // Remove the property prefix
            //
            String prop;
            if (key.startsWith(rootKey))
                prop = key.substring(rootKeyLen);
            else if (key.startsWith("yoko.orb.oa."))
                prop = key.substring("yoko.orb.oa.".length());
            else
                continue;

            //
            // Check for a match among the supported properties
            //
            int i;
            for (i = 0; i < numProps; i++)
                if (prop.equals(props[i]))
                    break;

            //
            // Was the property present?
            //
            if (i >= numProps) {
                String err = key + ": unknown property";
                logger.warning(err);
            }
        }

        //
        // Configuration parameters
        //
        version_.major = (byte) 1;
        version_.minor = (byte) 2;
        int concModel = org.apache.yoko.orb.OB.Server.Threaded;

        //
        // Check for comms policy:
        //
        boolean commsPolicyPresent;
        if (commsPolicy == null) {
            commsPolicyPresent = false;
        } else {
            commsPolicyPresent = true;
            short value = commsPolicy.value();
            if (value == COMMUNICATIONS_CONCURRENCY_POLICY_REACTIVE.value) {
                concModel = org.apache.yoko.orb.OB.Server.Blocking;
            } else if (value == COMMUNICATIONS_CONCURRENCY_POLICY_THREADED.value) {
                concModel = org.apache.yoko.orb.OB.Server.Threaded;
            } else {
                throw new org.omg.CORBA.PolicyError(
                        org.omg.CORBA.BAD_POLICY_VALUE.value);
            }
        }

        //
        // Check for giop policy:
        //
        boolean giopPolicyPresent;
        if (giopPolicy == null) {
            giopPolicyPresent = false;
        } else {
            giopPolicyPresent = true;
            short value = giopPolicy.value();
            if (value == GIOP_VERSION_POLICY_1_0.value) {
                version_.major = (byte) 1;
                version_.minor = (byte) 0;
            } else if (value == GIOP_VERSION_POLICY_1_1.value) {
                version_.major = (byte) 1;
                version_.minor = (byte) 1;
            } else if (value == GIOP_VERSION_POLICY_1_2.value) {
                version_.major = (byte) 1;
                version_.minor = (byte) 2;
            } else {
                throw new org.omg.CORBA.PolicyError(
                        org.omg.CORBA.BAD_POLICY_VALUE.value);
            }
        }

        //
        // Parse the individual properties, if necessary.
        // This is necessary as long as not all of the policies have
        // been specified.
        //

        if (!commsPolicyPresent || !giopPolicyPresent) {
            for (int i = 0; i < numProps; i++) {
                String key = props[i];

                //
                // First check the specific POAManager key
                //
                String fullkey = rootKey + key;
                String value = properties.getProperty(fullkey);

                //
                // If the specific POAManager key doesn't have a value then
                // check the default value "yoko.orb.oa.*"
                //
                if (value == null) {
                    fullkey = "yoko.orb.oa." + key;
                    value = properties.getProperty(fullkey);
                }

                //
                // No value
                //
                if (value == null)
                    continue;

                if (key.equals("conc_model") && !commsPolicyPresent) {
                    if (value.equals("threaded"))
                        concModel = org.apache.yoko.orb.OB.Server.Threaded;
                    //
                    // Technically the only valid values for
                    // yoko.orb.poamanager.*.conc_model are "reactive" and
                    // "threaded" since this is the communications conc model.
                    // However, we'll also accept the following values since
                    // we might be parsing "yoko.orb.oa.conc_model" (which
                    // represents the default value for both the comm conc
                    // model *and* the method dispatch model).
                    //
                    else if (value.equals("thread_per_client"))
                        concModel = org.apache.yoko.orb.OB.Server.Threaded;
                    else if (value.equals("thread_per_request"))
                        concModel = org.apache.yoko.orb.OB.Server.Threaded;
                    else if (value.equals("thread_pool"))
                        concModel = org.apache.yoko.orb.OB.Server.Threaded;
                    else {
                        String err = fullkey + ": unknown value";
                        logger.warning(err);
                    }
                } else if (key.equals("version") && !giopPolicyPresent) {
                    if (value.equals("1.0")) {
                        version_.major = (byte) 1;
                        version_.minor = (byte) 0;
                    } else if (value.equals("1.1")) {
                        version_.major = (byte) 1;
                        version_.minor = (byte) 1;
                    } else if (value.equals("1.2")) {
                        version_.major = (byte) 1;
                        version_.minor = (byte) 2;
                    } else {
                        String err = fullkey
                                + ": expected `1.0', `1.1' or `1.2'";
                        logger.severe(err);
                        throw new org.omg.CORBA.INITIALIZE(err);
                    }
                }
            }
        }

        //
        // Create the server manager
        //
        serverManager_ = new org.apache.yoko.orb.OB.ServerManager(orbInstance_,
                acceptors_, oaInterface_, concModel);

        //
        // Get the boot manager implementation
        //
        bootManagerImpl_ = (org.apache.yoko.orb.OB.BootManager_impl) orbInstance
                .getBootManager();
    }

    //
    // Register a POA with this POAManager
    //
    synchronized void _OB_addPOA(org.omg.PortableServer.POA poa, String[] id) {
        POANameHasher idkey = new POANameHasher(id);
        
        logger.fine("Adding new poa with id " + idkey); 
        org.apache.yoko.orb.OB.Assert._OB_assert(!poas_.containsKey(idkey));
        poas_.put(idkey, poa);

        poaLocator_.add(poa, id);
    }

    //
    // Un-register a POA with this POAManager
    //
    synchronized void _OB_removePOA(String[] id) {
        POANameHasher idkey = new POANameHasher(id);
        logger.fine("Removing poa with id " + idkey); 
        org.apache.yoko.orb.OB.Assert._OB_assert(poas_.containsKey(idkey));
        poas_.remove(idkey);

        poaLocator_.remove(id);
    }

    DirectServant _OB_getDirectServant(byte[] key,
            org.apache.yoko.orb.OB.RefCountPolicyList policies)
            throws org.apache.yoko.orb.OB.LocationForward, AdapterInactive {
        
        synchronized (this) {
            if (state_ == State.INACTIVE)
                throw new AdapterInactive();
        }
        
        org.apache.yoko.orb.OB.ObjectKeyData data = new org.apache.yoko.orb.OB.ObjectKeyData();
        
        if (org.apache.yoko.orb.OB.ObjectKey.ParseObjectKey(key, data)) {
            org.omg.PortableServer.POA poa;
            synchronized (this) {
                poa = _OB_locatePOA(data);
            }

            if (poa != null) {
                POA_impl poaImpl = (POA_impl) poa;
                return poaImpl._OB_getDirectServant(data.oid, policies);
            }
        }

        //
        // Check to see if the BootManager knows of a reference
        // for the ObjectKey. If so, forward the request.
        //
        synchronized (this) {
            org.omg.IOP.IOR ior = bootManagerImpl_._OB_locate(key);
            if (ior != null)
                throw new org.apache.yoko.orb.OB.LocationForward(ior, false);
        }

        //
        // In this case, there is no POA for a local servant. This is
        // an OBJECT_NOT_EXIST exception.
        //
        throw new org.omg.CORBA.OBJECT_NOT_EXIST("No POA for local servant");
    }

    org.omg.PortableServer.POA _OB_locatePOA(
            org.apache.yoko.orb.OB.ObjectKeyData data)
            throws org.apache.yoko.orb.OB.LocationForward {
        //
        // If the GIOP engine sends a request while the POAManager is in
        // INACTIVE state, then something is wrong.
        //
        org.apache.yoko.orb.OB.Assert._OB_assert(get_state() != State.INACTIVE);
        logger.fine("Searching for direct servant with key " + data); 

        org.omg.PortableServer.POA poa = null;
        if (data.serverId.equals(serverId_)) {
            POANameHasher key = new POANameHasher(data.poaId); 
            logger.fine("Searching for direct servant with poa key " + key); 
            poa = (org.omg.PortableServer.POA) poas_.get(key); 
            if (poa == null) {
                //
                // The POA isn't contained in our local POA table. Ask the
                // POALocator to locate the POA.
                //
                poa = poaLocator_.locate(data);

                //
                // If the POA is connected to some other POAManager (and
                // hence some other end-point) then location forward
                //
                if (poa != null) {
                    logger.fine("Attempting to obtain a local reference to an object activated on a differnt POA"); 
                    org.omg.PortableServer.POAManager manager = poa.the_POAManager();
                    if (manager != this) {
                        Object obj = poa.create_reference_with_id(data.oid, "");

                        org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) obj)
                                ._get_delegate());
                        org.omg.IOP.IOR ior = p._OB_IOR();
                        throw new org.apache.yoko.orb.OB.LocationForward(ior, false);
                    }
                }
            }
        }

        //
        // If the POA doesn't match the ObjectKeyData then this POA
        // isn't present.
        //
        if (poa != null) {
            POA_impl poaImpl = (POA_impl) poa;
            if (!poaImpl._OB_poaMatches(data, false)) {
                logger.fine("POA located but object key data doesn't match"); 
                poa = null;
            }
        }

        return poa;
    }

    public org.apache.yoko.orb.OB.CollocatedServer _OB_getCollocatedServer() {
        return serverManager_.getCollocatedServer();
    }

    public synchronized void _OB_validateState() {
        while (true) {
            //
            // If POAManager::activate() has been called then we're done
            //
            if (state_ == State.ACTIVE)
                break;

            //
            // If the POAManager is INACTIVE or DISCARDING then throw a
            // TRANSIENT exception
            //
            if (state_ == State.INACTIVE || state_ == State.DISCARDING)
                throw new org.omg.CORBA.TRANSIENT(
                        "POAManager is inactive or discarding requests", 0,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            //
            // Wait for a state transition
            //
            try {
                wait();
            } catch (InterruptedException ex) {
            }
        }
    }

    public org.omg.GIOP.Version _OB_getGIOPVersion() {
        return new org.omg.GIOP.Version(version_.major, version_.minor);
    }

    public String _OB_getAdapterManagerId() {
        return adapterManagerId_;
    }

    public short _OB_getAdapterState() {
        //
        // We'll remove the synchronization at present since this is a
        // simple state variable
        //

        switch (state_.value()) {
        case State._INACTIVE:
            return org.omg.PortableInterceptor.INACTIVE.value;

        case State._ACTIVE:
            return org.omg.PortableInterceptor.ACTIVE.value;

        case State._HOLDING:
            return org.omg.PortableInterceptor.HOLDING.value;

        case State._DISCARDING:
            return org.omg.PortableInterceptor.DISCARDING.value;
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(false);
        return org.omg.PortableInterceptor.NON_EXISTENT.value;
    }

    public org.apache.yoko.orb.OCI.Acceptor[] _OB_getAcceptors() {
        org.apache.yoko.orb.OCI.Acceptor[] result = new org.apache.yoko.orb.OCI.Acceptor[acceptors_.length];
        System.arraycopy(acceptors_, 0, result, 0, acceptors_.length);
        return result;
    }

    public org.apache.yoko.orb.OB.ServerManager _OB_getServerManager() {
        return serverManager_;
    }

    public org.apache.yoko.orb.OB.OAInterface _OB_getOAInterface() {
        return oaInterface_;
    }
}

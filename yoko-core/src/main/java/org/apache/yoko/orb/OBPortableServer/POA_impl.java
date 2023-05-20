/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.orb.OBPortableServer;

import org.apache.yoko.orb.CORBA.Delegate;
import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OB.DispatchRequest_impl;
import org.apache.yoko.orb.OB.DispatchStrategy;
import org.apache.yoko.orb.OB.InitialServiceManager;
import org.apache.yoko.orb.OB.LocationForward;
import org.apache.yoko.orb.OB.MessageRoutingUtil;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.ObjectKey;
import org.apache.yoko.orb.OB.ObjectKeyData;
import org.apache.yoko.orb.OB.PIManager;
import org.apache.yoko.orb.OB.PIUpcall;
import org.apache.yoko.orb.OB.PolicyFactoryManager;
import org.apache.yoko.orb.OB.RecursiveMutex;
import org.apache.yoko.orb.OB.RefCountPolicyList;
import org.apache.yoko.orb.OB.UnknownExceptionInfo;
import org.apache.yoko.orb.OB.UnknownExceptionInfo_impl;
import org.apache.yoko.orb.OB.UnknownExceptionStrategy;
import org.apache.yoko.orb.OB.Upcall;
import org.apache.yoko.orb.OB.UpcallReturn;
import org.apache.yoko.orb.OBMessageRouting.DecayPolicy_impl;
import org.apache.yoko.orb.OBMessageRouting.ImmediateSuspendPolicy_impl;
import org.apache.yoko.orb.OBPortableInterceptor.PersistentORT_impl;
import org.apache.yoko.orb.OBPortableInterceptor.TransientORT_impl;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.OciCurrentImpl;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.orb.PortableInterceptor.IORInfo_impl;
import org.apache.yoko.orb.PortableInterceptor.POAPolicyFactory_impl;
import org.apache.yoko.orb.PortableServer.IdAssignmentPolicy_impl;
import org.apache.yoko.orb.PortableServer.IdUniquenessPolicy_impl;
import org.apache.yoko.orb.PortableServer.ImplicitActivationPolicy_impl;
import org.apache.yoko.orb.PortableServer.LifespanPolicy_impl;
import org.apache.yoko.orb.PortableServer.PoaCurrentImpl;
import org.apache.yoko.orb.PortableServer.RequestProcessingPolicy_impl;
import org.apache.yoko.orb.PortableServer.ServantRetentionPolicy_impl;
import org.apache.yoko.orb.PortableServer.ThreadPolicy_impl;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.omg.BiDirPolicy.BOTH;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.ORB;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHolder;
import org.omg.IOP.TaggedProfile;
import org.omg.MessageRouting.InvalidState;
import org.omg.MessageRouting.Router;
import org.omg.MessageRouting.RouterAdmin;
import org.omg.MessageRouting.RouterListHolder;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.NON_EXISTENT;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableServer.AdapterActivator;
import org.omg.PortableServer.CurrentPackage.NoContext;
import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;
import org.omg.PortableServer.ID_UNIQUENESS_POLICY_ID;
import org.omg.PortableServer.IMPLICIT_ACTIVATION_POLICY_ID;
import org.omg.PortableServer.IdAssignmentPolicy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicy;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicy;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.omg.PortableServer.LifespanPolicy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POAManagerFactoryPackage.ManagerAlreadyExists;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.AdapterNonExistent;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.ObjectAlreadyActive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;
import org.omg.PortableServer.RequestProcessingPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.ServantLocatorPackage.CookieHolder;
import org.omg.PortableServer.ServantManager;
import org.omg.PortableServer.ServantRetentionPolicy;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.omg.PortableServer.ThreadPolicy;
import org.omg.PortableServer.ThreadPolicyValue;

import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.stream.Stream.concat;
import static org.apache.yoko.util.MinorCodes.MinorSystemExceptionInUnknownAdapter;
import static org.apache.yoko.util.MinorCodes.describeObjAdapter;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;
import static org.omg.PortableServer.IdAssignmentPolicyValue.SYSTEM_ID;
import static org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID;
import static org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID;
import static org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION;
import static org.omg.PortableServer.LifespanPolicyValue.TRANSIENT;
import static org.omg.PortableServer.RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY;
import static org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT;
import static org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN;
import static org.omg.PortableServer.ThreadPolicyValue.ORB_CTRL_MODEL;

final public class POA_impl extends LocalObject implements POA {
    static final Logger logger = Logger.getLogger(POA_impl.class.getName());
    private org.omg.CORBA.ORB orb_;
    private final ORBInstance orbInstance_;
    private final String name_;
    private final byte[] adapterId_;
    final String serverId_;
    private final String[] poaId_;
    private final POA_impl parent_;
    private POAManager manager_;
    private final ServantLocationStrategy servantLocationStrategy_;
    private final POAControl poaControl_;
    private final AdapterActivatorHolder adapterActivator_;
    private final ConcurrentHashMap<String, POA_impl> children_;
    private final POAPolicies policies_;
    private final IdGenerationStrategy idGenerationStrategy_;
    private final int poaCreateTime_;
    /** If this POA has the single thread policy set then this mutex is used ensure this policy is met */
    private final RecursiveMutex poaSyncMutex_ = new RecursiveMutex();
    private final DispatchStrategy dispatchStrategy_;
    private final PoaCurrentImpl poaCurrent_;
    private final OciCurrentImpl ociCurrent_;
    /** This is the set of policies that are not POA policies and are registered with a valid PolicyFactory */
    private final Policy[] rawPolicies_;
    private ObjectReferenceTemplate adapterTemplate_;
    private ObjectReferenceFactory ort_ = null;
    private IORInfo iorInfo_;
    /** The child ORTs required for adapterStateChange call when POA is destroyed. */
    private final Vector<ObjectReferenceTemplate> childTemplates_ = new Vector<>();
    /** Should POA call AdapterStateChange when destroyed */
    boolean callAdapterStateChange_ = true;

    /** Return the appropriate object reference template. */
    private ObjectReferenceFactory ort() {
        if (ort_ == null) {
            ort_ = current_factory();
            if (ort_ == null) ort_ = adapterTemplate_;
        }
        return ort_;
    }

    private boolean findPolicyIndex(Policy[] p, int type, IntHolder policyIndexHolder) {
        for (policyIndexHolder.value = 0; policyIndexHolder.value < p.length; policyIndexHolder.value++)
            if (p[policyIndexHolder.value].policy_type() == type) return true;
        return false;
    }

    private boolean validatePolicies(POAPolicies policies, Policy[] policyList, IntHolder i) {
        if (policies.servantRetentionPolicy() == ServantRetentionPolicyValue.NON_RETAIN
                && policies.requestProcessingPolicy() != RequestProcessingPolicyValue.USE_SERVANT_MANAGER
                && policies.requestProcessingPolicy() != USE_DEFAULT_SERVANT) {
            boolean ok = findPolicyIndex(policyList, SERVANT_RETENTION_POLICY_ID.value, i);
            Assert.ensure(ok);
            return false;
        }

        if (USE_ACTIVE_OBJECT_MAP_ONLY == policies.requestProcessingPolicy()
                && RETAIN != policies.servantRetentionPolicy()) {
            // One of the two must be present
            boolean ok = findPolicyIndex(policyList, SERVANT_RETENTION_POLICY_ID.value, i)
                    || findPolicyIndex(policyList, REQUEST_PROCESSING_POLICY_ID.value, i);
            Assert.ensure(ok);
            return false;
        }

        if (USE_DEFAULT_SERVANT == policies.requestProcessingPolicy()
                && MULTIPLE_ID != policies.idUniquenessPolicy()) {
            // Since USE_DEFAULT_SERVANT, this must be present
            boolean ok = findPolicyIndex(policyList,
                    REQUEST_PROCESSING_POLICY_ID.value,
                    i);
            Assert.ensure(ok);
            return false;
        }

        // IMPLICIT_ACTIVATION requires SYSTEM_ID and RETAIN
        if (policies.implicitActivationPolicy() == IMPLICIT_ACTIVATION
                && policies.servantRetentionPolicy() != RETAIN
                && policies.idAssignmentPolicy() != SYSTEM_ID) {
            // Since IMPLICIT_ACTIVATION , this must be present
            boolean ok = findPolicyIndex(policyList, IMPLICIT_ACTIVATION_POLICY_ID.value, i);
            Assert.ensure(ok);
            return false;
        }
        return true;
    }

    private void handleUnknownException(Upcall upcall, Exception ex) {
        UnknownExceptionStrategy strategy = orbInstance_.getUnknownExceptionStrategy();
        UnknownExceptionInfo info = new UnknownExceptionInfo_impl(upcall.operation(), upcall.responseExpected(), upcall.transportInfo(), (RuntimeException) ex);
        try {
            strategy.unknown_exception(info);
            // Return CORBA::UNKNOWN if the strategy doesn't raise an exception
            upcall.setSystemException(new UNKNOWN());
        } catch (SystemException sysEx) {
            upcall.setSystemException(sysEx);
        } catch (RuntimeException rex) {
            upcall.setSystemException(new UNKNOWN());
        }
    }

    private void completeDestroy() {
        logger.fine("Completing destroy of POA " + name_ + " using POAControl " + poaControl_);
        if (poaControl_.waitPendingRequests()) {
            if (parent_ != null) {
                synchronized (parent_.children_) {
                    Assert.ensure(parent_.children_.containsKey(name_));
                    parent_.children_.remove(name_);
                }
            }
            if (manager_ != null) {
                POAManager_impl m = (POAManager_impl) manager_;
                m._OB_removePOA(poaId_);
                manager_ = null;
            }
            if (callAdapterStateChange_) {
                ObjectReferenceTemplate[] orts = childTemplates_.stream().toArray(ObjectReferenceTemplate[]::new);
                orbInstance_.getPIManager().adapterStateChange(orts, NON_EXISTENT.value);
            }
            servantLocationStrategy_.destroy(this, poaControl_.etherealize());
            adapterActivator_.destroy();
            orb_ = null;
            poaControl_.markDestroyCompleted();
            logger.fine("POA " + name_ + " is in a destroyed state");
            iorInfo_ = null;
        }
    }

    private POA_impl(org.omg.CORBA.ORB orb,
                     ORBInstance orbInstance,
                     String serverId,
                     String name,
                     POA_impl parent,
                     POAManager manager,
                     PoaCurrentImpl poaCurrent,
                     OciCurrentImpl ociCurrent,
                     POAPolicies policies,
                     Policy[] rawPolicies) {
        orb_ = orb;
        orbInstance_ = orbInstance;
        serverId_ = serverId;
        name_ = name;
        parent_ = parent;
        manager_ = manager;
        policies_ = policies;
        poaCurrent_ = poaCurrent;
        ociCurrent_ = ociCurrent;
        rawPolicies_ = rawPolicies;

        logger.fine("Creating POA " + name + " using manager " + manager.get_id());

        poaId_ = null == parent ? new String[0] : concat(stream(parent.poaId_), Stream.of(name)).toArray(String[]::new);
        poaCreateTime_ = (int) (System.currentTimeMillis() / 1000);

        // TODO: It would be nicer if this didn't use CreateObjectKey
        byte[] oid = new byte[0];
        ObjectKeyData data = new ObjectKeyData(serverId_, poaId_, oid, policies_.lifespanPolicy() == LifespanPolicyValue.PERSISTENT, poaCreateTime_);
        adapterId_ = ObjectKey.CreateObjectKey(data);

        // Setup the object-reference templates for this POA. This
        // involves running the IORInterceptor to establish the IOR
        // components, and after calling the components_established
        // interception point.
        POAManager_impl m = (POAManager_impl) manager_;

        Acceptor[] acceptors = m._OB_getAcceptors();

        IORInfo_impl iorInfoImpl = new IORInfo_impl(orbInstance_, acceptors, rawPolicies_, policies_, m._OB_getAdapterManagerId(), m._OB_getAdapterState());
        iorInfo_ = iorInfoImpl;

        orbInstance_.getPIManager().establishComponents(iorInfo_);

        IOR iorTemplate = new IOR("", new TaggedProfile[0]);

        IORHolder iorH = new IORHolder(iorTemplate);
        iorInfoImpl._OB_addComponents(iorH, m._OB_getGIOPVersion());

        // Create the primary, secondary and current object-reference template.
        String orbId = orbInstance_.getOrbId();
        String serverId1 = orbInstance_.getServerId();
        logger.fine("POA " + name_ + " activated on ORB " + orbId + " and server " + serverId1);
        adapterTemplate_ = policies_.lifespanPolicy() == LifespanPolicyValue.PERSISTENT ?
                new PersistentORT_impl(orbInstance_, serverId1, orbId, poaId_, iorTemplate) :
                new TransientORT_impl(orbInstance_, serverId1, orbId, poaId_, poaCreateTime_, iorTemplate);
        iorInfoImpl._OB_adapterTemplate(adapterTemplate_);
        orbInstance_.getPIManager().componentsEstablished(iorInfo_);
        idGenerationStrategy_ = IdGenerationStrategyFactory.createIdGenerationStrategy(policies_);
        adapterActivator_ = new AdapterActivatorHolder();
        poaControl_ = new POAControl();
        servantLocationStrategy_ = ServantLocationStrategyFactory.createServantLocationStrategy(policies_, orbInstance_);
        children_ = new ConcurrentHashMap<>(31);
        dispatchStrategy_ = policies_.dispatchStrategyPolicy();
        m._OB_addPOA(this, poaId_);
    }

    //
    // Constructor for the root POA
    //
    public POA_impl(org.omg.CORBA.ORB orb, ORBInstance orbInstance, String name, POAManager manager) {
        this(orb, orbInstance, name, name, null, manager, new PoaCurrentImpl(), new OciCurrentImpl(), getDefaultPolicies(orbInstance), getDefaultRawPolicies());
        InitialServiceManager ism = orbInstance_.getInitialServiceManager();
        try {
            ism.addInitialReference("POACurrent", poaCurrent_);
            ism.addInitialReference("OCICurrent", ociCurrent_);
        } catch (InvalidName ex) {
            throw Assert.fail(ex);
        }
    }

    private static POAPolicies getDefaultPolicies(ORBInstance orbInstance) {
        Policy[] policies = { new ImplicitActivationPolicy_impl(IMPLICIT_ACTIVATION) };
        return new POAPolicies(orbInstance, policies);
    }

    private static Policy[] getDefaultRawPolicies() {
        return new Policy[]{
                new ThreadPolicy_impl(ORB_CTRL_MODEL),
                new LifespanPolicy_impl(TRANSIENT),
                new IdUniquenessPolicy_impl(UNIQUE_ID),
                new IdAssignmentPolicy_impl(SYSTEM_ID),
                new ServantRetentionPolicy_impl(RETAIN),
                new RequestProcessingPolicy_impl(USE_ACTIVE_OBJECT_MAP_ONLY),
                new ImplicitActivationPolicy_impl(IMPLICIT_ACTIVATION)
        };
    }

    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        Assert.ensure(poaControl_.getDestroyed());

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // Standard IDL to Java mapping
    // ----------------------------------------------------------------------

    public org.omg.PortableServer.POA create_POA(String adapter,
            org.omg.PortableServer.POAManager manager,
            Policy[] rawPolicies)
            throws AdapterAlreadyExists,
            InvalidPolicy {
        Assert.ensure(adapter != null);

        //
        // Has the POA been destroyed?
        //
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }
        
        logger.fine("POA " + name_ + " creating new POA with name " + adapter); 

        //
        // Are the requested policies valid?
        //
        POAPolicies policies = new POAPolicies(orbInstance_, rawPolicies);
        IntHolder idx = new IntHolder();
        if (!validatePolicies(policies, rawPolicies, idx))
            throw new InvalidPolicy(
                    (short) idx.value);

        POA_impl child;

        //
        // It's not possible to a POA that already exists. If the POA is
        // being destroyed an AdapterAlreadyExists exception is still
        // thrown -- the application has to be capable of dealing with this
        // (by calling create_POA again)
        //
        synchronized (children_) {
            if (children_.containsKey(adapter)) {
                throw new AdapterAlreadyExists();
            }

            // If necessary create a new POAManager for this POA
            POAManager obmanager;
            if (manager == null) {
                try {
                    InitialServiceManager ism = orbInstance_.getInitialServiceManager();
                    POAManagerFactory factory = POAManagerFactoryHelper.narrow(ism.resolveInitialReferences("POAManagerFactory"));
                    Policy[] emptyPl = new Policy[0];
                    obmanager = (POAManager) factory.create_POAManager(adapter, emptyPl);
                } catch (InvalidName | ManagerAlreadyExists | PolicyError ex) {
                    throw Assert.fail(ex);
                }
            } else {
                try {
                    obmanager = POAManagerHelper.narrow(manager);
                } catch (BAD_PARAM ex) {
                    throw Assert.fail(ex);
                }
            }

            //
            // Create the new POA
            //
            try {
                child = new POA_impl(orb_, orbInstance_, serverId_, adapter, this, obmanager, poaCurrent_, ociCurrent_, policies, rawPolicies);
            } catch (SystemException ex) {
                // If the creation of the POA fails and a new POAManager
                // was created the deactivate the POAManager
                if (manager == null) {
                    Assert.ensure(obmanager != null);
                    try {
                        obmanager.deactivate(true, true);
                    } catch (AdapterInactive ignored) {
                    }
                }
                throw ex;
            }

            //
            // Add this to the child list
            //
            children_.put(adapter, child);
        }

        return child;
    }

    public org.omg.PortableServer.POA find_POA(String adapter, boolean activate) throws AdapterNonExistent {
        Objects.requireNonNull(adapter);
        if (poaControl_.getDestroyed()) throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        logger.fine("POA " + name_ + " finding POA with name " + adapter);

        org.omg.PortableServer.POA poa = children_.get(adapter);
        if (null != poa) return poa;
        if (!activate) throw new AdapterNonExistent();

        // If the child isn't registered, and we've been asked to activate the POA and we have an AdapterActivator
        // and the POAManager's state is active then we activate the POA (see below requirement)
        //
        // From 11-18: In addition, when a POA manager is in the holding state, the adapter activators registered with
        // the associated POAs will not get called.


        // From the spec:
        //
        // Creating a POA using a POA manager that is in the active state can lead to race conditions if the POA
        // supports preexisting objects, because the new POA may receive a request before its adapter activator,
        // servant manager, or default servant have been initialized. These problems do not occur if the POA
        // is created by an adapter activator registered with a parent of the new POA, because requests are
        // queued until the adapter activator returns. To avoid these problems when a POA must be explicitly
        // initialized, the application can initialize the POA by invoking find_POA with a TRUE activate parameter.
        // TODO: This requirement is not met
        try {
            Optional.of(adapterActivator_)
                    .map(AdapterActivatorHolder::getAdapterActivator)
                    .ifPresent(aa -> aa.unknown_adapter(this, adapter));
        } catch (SystemException ex) {
            // 11.3.3.2: If unknown_adapter raises a system exception, the ORB will report an OBJ_ADAPTER with minor code 1.
            throw new OBJ_ADAPTER(describeObjAdapter(MinorSystemExceptionInUnknownAdapter), MinorSystemExceptionInUnknownAdapter, COMPLETED_NO);
        }

        return Optional.of(adapter)
                .map(children_::get)
                .orElseThrow(AdapterNonExistent::new);
    }

    public void destroy(boolean etherealize, boolean waitForCompletion) {
        _OB_destroy(etherealize, waitForCompletion, null);
    }

    // ----------------------------------------------------------------------
    // Factories for policy objects
    // ----------------------------------------------------------------------

    //
    // These methods don't bother to check to see if the POA has
    // already been destroyed
    //

    public ThreadPolicy create_thread_policy(
            ThreadPolicyValue value) {
        return new ThreadPolicy_impl(value);
    }

    public LifespanPolicy create_lifespan_policy(
            LifespanPolicyValue value) {
        return new LifespanPolicy_impl(value);
    }

    public IdUniquenessPolicy create_id_uniqueness_policy(
            IdUniquenessPolicyValue value) {
        return new IdUniquenessPolicy_impl(
                value);
    }

    public IdAssignmentPolicy create_id_assignment_policy(
            IdAssignmentPolicyValue value) {
        return new IdAssignmentPolicy_impl(
                value);
    }

    public ImplicitActivationPolicy create_implicit_activation_policy(
            ImplicitActivationPolicyValue value) {
        return new ImplicitActivationPolicy_impl(
                value);
    }

    public ServantRetentionPolicy create_servant_retention_policy(
            ServantRetentionPolicyValue value) {
        return new ServantRetentionPolicy_impl(
                value);
    }

    public RequestProcessingPolicy create_request_processing_policy(
            RequestProcessingPolicyValue value) {
        return new RequestProcessingPolicy_impl(
                value);
    }

    public SynchronizationPolicy create_synchronization_policy(
            SynchronizationPolicyValue value) {
        return new SynchronizationPolicy_impl(value);
    }

    public DispatchStrategyPolicy create_dispatch_strategy_policy(
            DispatchStrategy value) {
        return new DispatchStrategyPolicy_impl(value);
    }

    // ----------------------------------------------------------------------
    // POA attributes
    // ----------------------------------------------------------------------

    public String the_name() {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        return name_;
    }

    public org.omg.PortableServer.POA the_parent() {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        return parent_;
    }

    public org.omg.PortableServer.POA[] the_children() {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        // Since its possible that the children list changes while
        // this method call is in progress its necessary to check the
        // return value of Hashtable::get.
        return children_.values().stream()
                .filter(Objects::nonNull)
                .toArray(org.omg.PortableServer.POA[]::new);
    }

    public org.omg.PortableServer.POAManager the_POAManager() {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        return manager_;
    }

    public org.omg.PortableServer.POAManagerFactory the_POAManagerFactory() {
        return orbInstance_.getPOAManagerFactory();
    }

    public AdapterActivator the_activator() {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        return adapterActivator_.getAdapterActivator();
    }

    public void the_activator(AdapterActivator activator) {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }
        
        adapterActivator_.setAdapterActivator(activator);
    }

    public Policy[] the_policies() {
        return policies_.recreate();
    }

    public DispatchStrategy the_dispatch_strategy() {
        return policies_.dispatchStrategyPolicy();
    }

    public ObjectReferenceTemplate adapter_template() {
        return adapterTemplate_;
    }

    public ObjectReferenceFactory current_factory() {
        if (iorInfo_ != null) {
            try {
                return iorInfo_
                        .current_factory();
            } catch (ClassCastException e) {
                return iorInfo_.current_factory();
            }
        }
        return null;
    }

    public org.omg.CORBA.ORB the_ORB() {
        return orb_;
    }

    // ----------------------------------------------------------------------
    // Servant manager registration
    // ----------------------------------------------------------------------

    public ServantManager get_servant_manager()
            throws WrongPolicy {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        ServantManagerStrategy servantManagerStrategy = servantLocationStrategy_
                .getServantManagerStrategy();

        if (servantManagerStrategy == null) {
            throw new WrongPolicy();
        }

        return servantManagerStrategy.getServantManager();
    }

    public void set_servant_manager(ServantManager mgr) throws WrongPolicy {
        if (poaControl_.getDestroyed()) throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        Optional.of(servantLocationStrategy_)
                .map(ServantLocationStrategy::getServantManagerStrategy)
                .orElseThrow(WrongPolicy::new)
                .setServantManager(mgr);
    }

    public Servant get_servant() throws NoServant, WrongPolicy {
        if (poaControl_.getDestroyed()) throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");

        DefaultServantHolder holder = Optional.of(servantLocationStrategy_)
                .map(ServantLocationStrategy::getDefaultServantHolder)
                .orElseThrow(WrongPolicy::new);

        return Optional.of(holder)
                .map(DefaultServantHolder::getDefaultServant)
                .orElseThrow(NoServant::new);
    }

    public void set_servant(Servant servant) throws WrongPolicy {
        Assert.ensure(servant != null);

        if (poaControl_.getDestroyed()) throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");

        Optional.of(servantLocationStrategy_)
                .map(ServantLocationStrategy::getDefaultServantHolder)
                .orElseThrow(WrongPolicy::new)
                .setDefaultServant(servant);

        ((ORB) orbInstance_.getORB()).set_delegate(servant);
    }

    public byte[] activate_object(Servant servant) throws ServantAlreadyActive, WrongPolicy {
        Assert.ensure(servant != null);
        if (poaControl_.getDestroyed()) throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        byte[] oid = idGenerationStrategy_.createId();
        try {
            servantLocationStrategy_.activate(oid, servant);
        } catch (ObjectAlreadyActive ex) {
            throw Assert.fail(ex); // Should not occur
        }
        return oid;
    }

    public void notifyRouters(boolean activate, byte[] oid) {
        //
        // Get the list of routers for this target
        //
        RouterListHolder configRouterList = new RouterListHolder();
        configRouterList.value = new Router[0];
        MessageRoutingUtil.getRouterListFromConfig(orbInstance_, configRouterList);

        Router[] value = configRouterList.value;
        for (Router curRouter : value) {
            try {
                RouterAdmin routerAdmin = curRouter.admin();
                if (null == routerAdmin) continue;
                // The object itself must be registered with the router
                // admin. The 'oid' parameter can be used to create a
                // reference for this object.
                final org.omg.CORBA.Object dest = id_to_reference(oid);
                if (activate) {
                    routerAdmin.register_destination(dest, false,
                            new ImmediateSuspendPolicy_impl(),
                            new DecayPolicy_impl(0));
                } else {
                    routerAdmin.unregister_destination(dest);
                }
            } catch (ObjectNotActive | WrongPolicy ex) {
                return;
            } catch (InvalidState | SystemException ignored) {
            }
        }
    }

    public void activate_object_with_id(byte[] oid, Servant servant) throws ServantAlreadyActive, ObjectAlreadyActive, WrongPolicy {
        Assert.ensure(servant != null);

        if (poaControl_.getDestroyed()) throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");


        //
        // Validate that the ObjectId is valid
        //
        if (!idGenerationStrategy_.isValid(oid))
            throw new BAD_PARAM(
                    MinorCodes
                            .describeBadParam(MinorCodes.MinorInvalidObjectId)
                            + ": POA has SYSTEM_ID policy but the object ID was not "
                            + "generated by this POA",
                    MinorCodes.MinorInvalidObjectId,
                    COMPLETED_NO);

        servantLocationStrategy_.activate(oid, servant);

        //
        // Notify associated routers of activation
        //
        notifyRouters(true, oid);
    }

    public void deactivate_object(byte[] oid) throws ObjectNotActive, WrongPolicy {
        if (poaControl_.getDestroyed()) throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");


        servantLocationStrategy_.deactivate(this, oid);

        //
        // Notify associated routers of activation
        //
        notifyRouters(false, oid);
    }

    public org.omg.CORBA.Object create_reference(String intf)
            throws WrongPolicy {
        Assert.ensure(intf != null);

        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        byte[] oid = idGenerationStrategy_.createId();
        return ort().make_object(intf, oid);
    }

    public org.omg.CORBA.Object create_reference_with_id(byte[] oid, String intf) {
        Assert.ensure(intf != null);

        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }
        //
        // Validate that the ObjectId is valid
        //
        if (!idGenerationStrategy_.isValid(oid))
            throw new BAD_PARAM(
                    MinorCodes
                            .describeBadParam(MinorCodes.MinorInvalidObjectId)
                            + ": POA has SYSTEM_ID policy but the object ID was not "
                            + "generated by this POA",
                    MinorCodes.MinorInvalidObjectId,
                    COMPLETED_NO);

        return ort().make_object(intf, oid);
    }

    public byte[] servant_to_id(Servant servant)
            throws ServantNotActive,
            WrongPolicy {
        Assert.ensure(servant != null);

        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        //
        // Requires USE_DEFAULT_SERVANT policy or RETAIN policy and
        // either the UNIQUE_ID policy or the IMPLICIT_ACTIVATION (w/
        // SYSTEM_ID) policies.
        //
        if (policies_.requestProcessingPolicy() != USE_DEFAULT_SERVANT
                && (policies_.servantRetentionPolicy() != RETAIN || (policies_
                        .idUniquenessPolicy() != UNIQUE_ID && policies_
                        .implicitActivationPolicy() != IMPLICIT_ACTIVATION)))
            throw new WrongPolicy();

        byte[] oid = servantLocationStrategy_.servantToId(servant, poaCurrent_);

        if (oid == null) {
            //
            // If the POA doesn't have the IMPLICIT_ACTIVATION
            // (w/ SYSTEM_ID) then a ServantNotActive exception
            //
            if (policies_.implicitActivationPolicy() != IMPLICIT_ACTIVATION)
                throw new ServantNotActive();

            try {
                oid = activate_object(servant);
            } catch (ServantAlreadyActive ex) {
                throw Assert.fail(ex); // Should
                                                                    // not occur
            }
        }

        return oid;
    }

    public org.omg.CORBA.Object servant_to_reference(
            Servant servant)
            throws ServantNotActive,
            WrongPolicy {
        Assert.ensure(servant != null);

        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        //
        // If the operation was invoked in the context of executing
        // request on the specified servant, the reference associated
        // with the current invocation is returned.
        //
        if (poaCurrent_._OB_inUpcall()
                && poaCurrent_._OB_getServant() == servant) {
            try {
                byte[] oid = poaCurrent_.get_object_id();
                assert servant != null;
                String intf = servant._all_interfaces(this, oid)[0];
                return ort().make_object(intf, oid);
            } catch (NoContext ex) {
                throw Assert.fail(ex);
            }
        }

        byte[] oid = servant_to_id(servant);
        assert servant != null;
        String intf = servant._all_interfaces(this, oid)[0];
        return create_reference_with_id(oid, intf);
    }

    public Servant reference_to_servant(
            org.omg.CORBA.Object reference)
            throws ObjectNotActive,
            WrongAdapter,
            WrongPolicy {
        Assert.ensure(reference != null);

        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        //
        // Requires the RETAIN policy or the USE_DEFAULT_SERVANT
        // policy.
        //
        if (policies_.servantRetentionPolicy() != RETAIN
                && policies_.requestProcessingPolicy() != USE_DEFAULT_SERVANT)
            throw new WrongPolicy();

        byte[] oid = reference_to_id(reference);

        Servant servant = servantLocationStrategy_
                .idToServant(oid, true);

        if (servant == null)
            throw new ObjectNotActive();

        return servant;
    }

    public byte[] reference_to_id(org.omg.CORBA.Object reference)
            throws WrongAdapter {
        Assert.ensure(reference != null);

        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        assert reference != null;
        Delegate d = (Delegate) ((ObjectImpl) reference)
                ._get_delegate();
        IOR ior = d._OB_IOR();

        //
        // Extract the object key from the IOR of the object reference.
        //
        POAManager_impl m = (POAManager_impl) manager_;
        Acceptor[] acceptors = m._OB_getAcceptors();

        boolean local = false;
        ProfileInfo[] profileInfos = null;

        for (int i = 0; i < acceptors.length && !local; i++) {
            profileInfos = acceptors[i].get_local_profiles(ior);
            if (profileInfos.length > 0)
                local = true;
        }

        //
        // Is the key local?
        //
        if (local) {
            //
            // Verify the poaIds are the same, have the persistency
            // values and the same create time if transient
            //
            ObjectKeyData keyData = new ObjectKeyData();
            if (ObjectKey.ParseObjectKey(
                    profileInfos[0].key, keyData)
                    && _OB_poaMatches(keyData, true)) {
                return keyData.oid;
            }
        }

        throw new WrongAdapter();
    }

    public Servant id_to_servant(byte[] oid)
            throws ObjectNotActive,
            WrongPolicy {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        //
        // Requires the RETAIN policy or the USE_DEFAULT_SERVANT policy.
        //
        if (policies_.servantRetentionPolicy() != RETAIN
                && policies_.requestProcessingPolicy() != USE_DEFAULT_SERVANT)
            throw new WrongPolicy();

        Servant servant = servantLocationStrategy_
                .idToServant(oid, true);

        if (servant == null)
            throw new ObjectNotActive();

        return servant;
    }

    public org.omg.CORBA.Object id_to_reference(byte[] oid)
            throws ObjectNotActive,
            WrongPolicy {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        // Requires the RETAIN policy
        //
        if (policies_.servantRetentionPolicy() != RETAIN)
            throw new WrongPolicy();

        Servant servant = servantLocationStrategy_
                .idToServant(oid, false);
        if (servant == null)
            throw new ObjectNotActive();

        String intf = servant._all_interfaces(this, oid)[0];
        return ort().make_object(intf, oid);
    }

    public byte[] id() {
        if (poaControl_.getDestroyed()) {
            throw new OBJECT_NOT_EXIST("POA " + name_ + " has been destroyed");
        }

        return adapterId_;
    }

    // ----------------------------------------------------------------------
    // Yoko specific functions
    // ----------------------------------------------------------------------

    public void _OB_preinvoke(String op, byte[] oid,
            Servant servant, Object cookie,
            TransportInfo info) {
        //
        // preinvoke the servant location strategy
        //
        servantLocationStrategy_.preinvoke(oid);

        //
        // Setup the OCI::Current context
        //
        ociCurrent_._OB_preinvoke(info);

        //
        // Setup the PortableServer::Current context
        //
        poaCurrent_._OB_preinvoke(this, servant, op, oid, cookie);

        //
        // Depending on the synchronization policy we have to lock a
        // variety of mutexes.
        //
        // TODO: DUMP THIS IF POSSIBLE
        //
        switch (policies_.synchronizationPolicy().value()) {
        case SynchronizationPolicyValue._NO_SYNCHRONIZATION:
            break;

        case SynchronizationPolicyValue._SYNCHRONIZE_ON_POA:
            poaSyncMutex_.lock();
            break;

        case SynchronizationPolicyValue._SYNCHRONIZE_ON_ORB:
            orbInstance_.getORBSyncMutex().lock();
            break;
        }
    }

    public void _OB_postinvoke() {
        byte[] oid = poaCurrent_._OB_getObjectId();
        String op = poaCurrent_._OB_getOp();
        Servant servant = poaCurrent_._OB_getServant();
        Object cookie = poaCurrent_._OB_getCookie();

        //
        // Depending on the synchronization policy we have to unlock a
        // variety of mutexes. Note that we have to do this before
        // calling postinvoke else we might operation on a destroyed
        // servant.
        //
        switch (policies_.synchronizationPolicy().value()) {
        case SynchronizationPolicyValue._NO_SYNCHRONIZATION:
            break;

        case SynchronizationPolicyValue._SYNCHRONIZE_ON_POA:
            poaSyncMutex_.unlock();
            break;

        case SynchronizationPolicyValue._SYNCHRONIZE_ON_ORB:
            orbInstance_.getORBSyncMutex().unlock();
            break;
        }

        //
        // Clean up the PortableServant::Current
        //
        poaCurrent_._OB_postinvoke();

        //
        // Clean up the OCI:Current
        //
        ociCurrent_._OB_postinvoke();

        //
        // postinvoke the servant location strategy
        //
        servantLocationStrategy_.postinvoke(oid, this, op, cookie, servant);
    }

    void _OB_locateServant(byte[] oid)
            throws LocationForward {
        CookieHolder cookieHolder = new CookieHolder();
        String op = "_locate";
        Servant servant = servantLocationStrategy_
                .locate(oid, this, op, cookieHolder);

        if (servant == null)
            throw new OBJECT_NOT_EXIST(
                    MinorCodes
                            .describeObjectNotExist(MinorCodes.MinorCannotDispatch),
                    MinorCodes.MinorCannotDispatch,
                    COMPLETED_NO);

        servantLocationStrategy_.preinvoke(oid);
        servantLocationStrategy_.postinvoke(oid, this, op, cookieHolder.value,
                servant);
    }

    //
    // Create the upcall object for a method invocation
    //
    Upcall _OB_createUpcall(byte[] oid,
                            UpcallReturn upcallReturn,
                            ProfileInfo profileInfo,
                            TransportInfo transportInfo, int requestId,
                            String op, InputStream in,
                            ServiceContexts requestContexts) throws LocationForward {
        // Increment the outstanding request count
        if (!poaControl_.incrementRequestCount()) return null;

        final Upcall upcall;

            //
            // Create the upcall object
            //
            if (policies_.interceptorCallPolicy()) {
                PIManager piManager = orbInstance_.getPIManager();

                if (piManager.haveServerInterceptors()) {
                    PIUpcall piUpcall = new PIUpcall(orbInstance_, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts, piManager);
                    upcall = piUpcall;

                    //
                    // Call the receive_request_service_contexts
                    // interception point
                    //
                    boolean failed = true;
                    try {
                        piUpcall.receiveRequestServiceContexts(rawPolicies_, adapterId_, oid, adapterTemplate_);
                        failed = false;
                    } finally {
                        if (failed) _OB_decrementRequestCount();
                    }
                    piUpcall.contextSwitch();
                } else {
                    upcall = new Upcall(orbInstance_, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts);
                }
            } else {
                upcall = new Upcall(orbInstance_, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts);
            }

            try {
                DispatchRequest_impl dispatchRequestImpl = new DispatchRequest_impl(this, oid, upcall);
                upcall.setDispatchInfo(dispatchRequestImpl, dispatchStrategy_);
            } catch (SystemException ex) {
                upcall.setSystemException(ex);
                _OB_decrementRequestCount();
            }

        //
        // If this POA has a BidirPolicy set to BOTH and we have
        // received some listening points in the service context
        // (implying the client has the BidirPolicy as well),
        // then we must map these in the transportInfo structure
        //
        _OB_handleBidirContext(transportInfo, requestContexts);


        return upcall;
    }

    //
    // Dispatch a method invocation
    //
    public void _OB_dispatch(byte[] oid, Upcall upcall) {
        String op = upcall.operation();

        //
        // Notify the PIUpcall about a potential change in thread
        // context. This has to be done before the servant is located
        // since PICurrent information has to be available in servant
        // locators.
        //
        upcall.contextSwitch();

        try {
            //
            // Locating a servant can throw LocationForward and
            // SystemException errors
            //
            CookieHolder cookieHolder = new CookieHolder();
            Servant servant = servantLocationStrategy_
                    .locate(oid, this, op, cookieHolder);

            //
            // If there is a servant then dispatch the request
            //
            if (servant != null) {
                TransportInfo transportInfo = upcall
                        .transportInfo();

                //
                // Do any pre invocation stuff
                //
                _OB_preinvoke(op, oid, servant, cookieHolder.value,
                        transportInfo);

                //
                // The Upcall may call _OB_postinvoke
                //
                upcall.setServantAndPOA(servant, this);

                //
                // Dispatch the request
                //
                try {
                    ServantDispatcher dispatcher = new ServantDispatcher(
                            upcall, servant);
                    dispatcher.dispatch();
                } finally {
                    //
                    // If postinvoke() hasn't been called, then do it now,
                    // since it may raise an exception that must be returned
                    // to the client
                    //
                    if (!upcall.postinvokeCalled()) {
                        _OB_postinvoke(); // May raise SystemException
                    }
                }
            } else if (op.equals("_non_existent") || op.equals("_not_existent")) {
                upcall.preUnmarshal();
                upcall.postUnmarshal();
                upcall.postinvoke();
                OutputStream out = upcall.preMarshal();
                out.write_boolean(true);
                upcall.postMarshal();
            } else {
                upcall
                        .setSystemException(new OBJECT_NOT_EXIST(
                                MinorCodes
                                        .describeObjectNotExist(MinorCodes.MinorCannotDispatch),
                                MinorCodes.MinorCannotDispatch,
                                COMPLETED_NO));
            }
        } catch (LocationForward ex) {
            upcall.setLocationForward(ex.ior, ex.perm);
        }
        /*
         * This can't happen in Java catch(org.omg.CORBA.UserException ex) {
         * upcall.setUserException(ex); }
         */
        catch (SystemException ex) {
            upcall.setSystemException(ex);
        } catch (Exception ex) {
            handleUnknownException(upcall, ex);
        }

        //
        // Java only
        //
        // If the skeleton has marshalled a user exception, it is now
        // safe to call endUserException
        //
        if (upcall.userException())
            upcall.endUserException();

        //
        // Decrement the outstanding request count
        //
        _OB_decrementRequestCount();
    }

    DirectServant _OB_getDirectServant(byte[] oid,
            RefCountPolicyList policies)
            throws LocationForward {
        return servantLocationStrategy_.createDirectStubImpl(this, oid,
                policies);
    }

    public PoaCurrentImpl _OB_POACurrent() {
        return poaCurrent_;
    }

    public void _OB_removeDirectServant(byte[] oid, DirectServant directServant) {
        servantLocationStrategy_.removeDirectStubImpl(oid, directServant);
    }

    //
    // Determine if this POA matches the provided object key
    //
    boolean _OB_poaMatches(ObjectKeyData data,
                           boolean full) {
        if (full) {
            //
            // Check server id
            //
            if (!data.serverId.equals(serverId_))
                return false;

            //
            // If the length is incorrect, return false
            //
            if (data.poaId.length != poaId_.length)
                return false;

            //
            // Check each name
            //
            for (int i = 0; i < data.poaId.length; ++i)
                if (!data.poaId[i].equals(poaId_[i]))
                    return false;
        }

        //
        // Is the POA persistent? If so then the ObjectKeyData must be
        // persistent.
        //
        if (policies_.lifespanPolicy() == LifespanPolicyValue.PERSISTENT)
            return data.persistent;

        //
        // Otherwise this POA is transient. The ObjectKeyData must be
        // transient, and the POA create times must be the same.
        //
        if (data.persistent)
            return false;
        return data.createTime == poaCreateTime_;
    }

    //
    // Increment the outstanding number of requests
    //
    public boolean _OB_incrementRequestCount() {
        return poaControl_.incrementRequestCount();
    }

    //
    // Decrement the outstanding number of requests
    //
    public void _OB_decrementRequestCount() {
        if (poaControl_.decrementRequestCount()) {
            completeDestroy();
        }
    }

    //
    // Wait for all pending requests to terminate
    //
    public void _OB_waitPendingRequests() {
        poaControl_.waitPendingRequests();
    }

    //
    // Get the ORBInstance object
    //
    public ORBInstance _OB_ORBInstance() {
        return orbInstance_;
    }

    //
    // Add the policy factory for the POA Policies
    //
    public void _OB_addPolicyFactory() {
        PolicyFactoryManager pfm = orbInstance_
                .getPolicyFactoryManager();

        PolicyFactory factory = new POAPolicyFactory_impl();
        pfm.registerPolicyFactory(
                THREAD_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(
                LIFESPAN_POLICY_ID.value, factory, true);
        pfm.registerPolicyFactory(
                ID_UNIQUENESS_POLICY_ID.value, factory,
                true);
        pfm.registerPolicyFactory(
                ID_ASSIGNMENT_POLICY_ID.value, factory,
                true);
        pfm.registerPolicyFactory(
                IMPLICIT_ACTIVATION_POLICY_ID.value,
                factory, true);
        pfm.registerPolicyFactory(
                SERVANT_RETENTION_POLICY_ID.value,
                factory, true);
        pfm.registerPolicyFactory(
                REQUEST_PROCESSING_POLICY_ID.value,
                factory, true);
        pfm.registerPolicyFactory(SYNCHRONIZATION_POLICY_ID.value, factory,
                true);
        pfm.registerPolicyFactory(DISPATCH_STRATEGY_POLICY_ID.value, factory,
                true);
        pfm.registerPolicyFactory(INTERCEPTOR_CALL_POLICY_ID.value, factory,
                true);
    }

    public void _OB_validateManagerState() {
        POAManager_impl m = (POAManager_impl) manager_;
        m._OB_validateState();
    }

    public void _OB_etherealize(POAManager_impl manager) {
        //
        // Recursively etherealize children's POA
        //
        Enumeration e = children_.elements();
        while (e.hasMoreElements()) {
            POA_impl child = (POA_impl) e
                    .nextElement();
            child._OB_etherealize(manager);
        }

        //
        // Eterealize our servants if the POA is assocatied with the
        // POAManager passed in argument.
        //
        if (manager == manager_) {
            servantLocationStrategy_.etherealize(this);
        }
    }

    public void _OB_destroy(boolean etherealize, boolean waitForCompletion, List<ObjectReferenceTemplate> templates) {
        logger.fine("Destroying POA " + name_); 
        if (poaControl_.getDestroyed()) return;

        //
        // If waitForCompletion is TRUE and the current thread is in
        // an invocation context dispatched from some POA belonging to
        // the same ORB as this POA, the BAD_INV_ORDER exception is
        // raised and POA destruction does not occur.
        //
        if (waitForCompletion && poaCurrent_._OB_inUpcall()) {
            try {
                POA_impl p = (POA_impl) poaCurrent_.get_POA();
                if (p._OB_ORBInstance() == orbInstance_) throw new BAD_INV_ORDER("Invocation in progress");
            } catch (NoContext ignored) {
            }
        }

        //
        // Add our primary template to the template sequence to return to
        // our parent. If templates is null then destroy() was called directly
        // on this POA.
        //
        if (templates != null) {
            templates.add(adapter_template());
            callAdapterStateChange_ = false;
        }

        //
        // Mark the POA as destroy pending. Only one thread will
        // continue. All others will wait (if waitForCompletion is
        // true) until the POA destruction is complete.
        //
        if (poaControl_.markDestroyPending(etherealize, waitForCompletion)) {
            // Recursively destroy all children

            Enumeration e = children_.elements();

            final List<ObjectReferenceTemplate> orts = null == templates ? childTemplates_ : templates;

            children_.values().stream()
                    .filter(Objects::nonNull)
                    .forEach(p -> p._OB_destroy(etherealize, waitForCompletion, orts));

            //
            // If waitForCompletion is FALSE, the destroy operation
            // destroys the POA and its children but waits neither for
            // active requests to complete nor for etherealization to
            // occur. If destroy is called multiple times before
            // destruction is complete (because there are active
            // requests), the etherealize_objects parameter applies
            // only to the first call of destroy. Subsequent calls
            // with conflicting etherealize_objects settings use the
            // value of etherealize_objects from the first call. The
            // waitForCompletion parameter is handled as defined above
            // for each individual call (some callers may choose to
            // block, while others may not).
            //
            // We only return if there are outstanding requests. In
            // this case when the last outstanding request is
            // completed then destruction of the the POA will
            // complete.
            //
            if (!waitForCompletion && poaControl_.hasPendingRequests()) return;

            completeDestroy();
        }
    }

    private void _OB_handleBidirContext(TransportInfo transportInfo, ServiceContexts contexts) {
        if (policies_.bidirPolicy() != BOTH.value) return;
        if (transportInfo == null) return;
        transportInfo.handle_service_contexts(contexts);
    }
}

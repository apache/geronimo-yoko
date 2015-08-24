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

import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;
import org.omg.PortableServer.POAManagerPackage.State;

public final class ClientManager {
    static final Logger logger = Logger.getLogger(ClientManager.class.getName());
    private boolean destroy_; // True if destroy() was called

    //
    // The ORB Instance
    //
    private ORBInstance orbInstance_;

    //
    // All clients
    //
    private Vector<Client> allClients_ = new Vector<>();

    //
    // All reusable clients
    //
    private Vector<Client> reusableClients_ = new Vector<>();

    //
    // The concurrency model with which new Clients are created
    //
    private int concModel_;

    // ----------------------------------------------------------------------
    // ClientManager private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert._OB_assert(destroy_);
        Assert._OB_assert(allClients_.isEmpty());
        Assert._OB_assert(reusableClients_.isEmpty());

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // ClientManager package member implementations
    // ----------------------------------------------------------------------

    synchronized void destroy() {
        //
        // Don't destroy twice
        //
        if (destroy_) {
            return;
        }

        //
        // Set the destroy flag
        //
        destroy_ = true;

        //
        // Destroy all clients
        //
        for (Client c : allClients_) c.destroy(false);

        //
        // Reset internal data
        //
        orbInstance_ = null;
        allClients_.removeAllElements();
        reusableClients_.removeAllElements();
    }

    // ----------------------------------------------------------------------
    // ClientManager public member implementations
    // ----------------------------------------------------------------------

    public ClientManager(int concModel) {
        destroy_ = false;
        concModel_ = concModel;
    }

    public void setORBInstance(ORBInstance instance) {
        orbInstance_ = instance;
    }

    //
    // Get a list of ClientProfilePairs for an IOR and a list of policies
    //
    public synchronized Vector<ClientProfilePair> getClientProfilePairs(IOR ior, Policy[] policies) {
        Assert._OB_assert(ior.type_id != null);

        //
        // Can't create a Client for a nil object
        //
        if (ior.type_id.length() == 0 && ior.profiles.length == 0) {
            throw new org.omg.CORBA.INV_OBJREF("Object reference is nil");
        }

        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }


        //
        // Find out whether private clients are requested
        //
        boolean privateClients = false;
        for (Policy pol : policies) {
            if (pol.policy_type() == CONNECTION_REUSE_POLICY_ID.value) {
                privateClients = !!!(ConnectionReusePolicyHelper.narrow(pol).value());
                break;
            }
        }

        //
        // Get the protocol policy, if any
        //
        ProtocolPolicy protocolPolicy = null;
        for (Policy pol : policies) {
            if (pol.policy_type() == PROTOCOL_POLICY_ID.value) {
                protocolPolicy = ProtocolPolicyHelper.narrow(pol);
                break;
            }
        }

        //
        // check whether the BiDir policy is enabled
        //
        boolean enableBidir = false;
        for (Policy pol : policies) {
            if (pol.policy_type() == org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE.value) {
                org.omg.BiDirPolicy.BidirectionalPolicy p = org.omg.BiDirPolicy.BidirectionalPolicyHelper
                        .narrow(pol);
                if (p.value() == org.omg.BiDirPolicy.BOTH.value) {
                    enableBidir = true;
                }
            }
        }

        Vector<ClientProfilePair> pairs = new Vector<>();

        //
        // First try to create CollocatedClients
        //
        org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactory = orbInstance_.getPOAManagerFactory();
        for (org.omg.PortableServer.POAManager mgr : pmFactory.list()) {
            try {
                boolean local = false;
                for (org.apache.yoko.orb.OCI.Acceptor acceptor : ((org.apache.yoko.orb.OBPortableServer.POAManager)mgr).get_acceptors()) {
                    org.apache.yoko.orb.OCI.ProfileInfo[] localProfileInfos = acceptor.get_local_profiles(ior);
                    if (localProfileInfos.length > 0) {
                        local = true;
                    }
                }

                // we can get into hang situations if we return a collocated server for an
                // inactive POA.  This can happen with the RootPOA, which is generally not activated.
                if (local && mgr.get_state() == State.ACTIVE) {
                    //
                    // Retrieve the CollocatedServer from the POAManager
                    //
                    org.apache.yoko.orb.OBPortableServer.POAManager_impl manager = (org.apache.yoko.orb.OBPortableServer.POAManager_impl) mgr;
                    CollocatedServer collocatedServer = manager._OB_getCollocatedServer();

                    //
                    // Create a new CollocatedClient and add the new client to
                    // both the list of all clients, and the list of clients
                    // that is returned
                    //
                    CodeConverters conv = new CodeConverters();
                    Client client = new CollocatedClient(collocatedServer, concModel_, conv);
                    allClients_.addElement(client);

                    for (org.apache.yoko.orb.OCI.ProfileInfo profileInfo : client.getUsableProfiles(ior, policies)) {
                        ClientProfilePair pair = new ClientProfilePair();
                        pair.client = client;
                        pair.profile = profileInfo;
                        pairs.addElement(pair);
                    }

                    //
                    // TODO: Introduce reusable CollocatedClients?
                    //
                }
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ignored) {
            }
        }

        //
        // If no private clients are requested, add all existing reusable
        // clients which are usable for the given IOR and policies
        //
        if (!privateClients) {
            for (Client reusableClient : reusableClients_) {

                //
                // Skip any client whose protocol is not present in the
                // protocol list
                //
                if (protocolPolicy != null) {
                    org.apache.yoko.orb.OCI.ConnectorInfo info = reusableClient.connectorInfo();
                    if (info != null && !protocolPolicy.contains(info.id())) {
                        continue;
                    }
                }

                for (org.apache.yoko.orb.OCI.ProfileInfo profileInfo : reusableClient.getUsableProfiles(ior, policies)) {
                    ClientProfilePair pair = new ClientProfilePair();
                    pair.client = reusableClient;
                    pair.profile = profileInfo;
                    pairs.addElement(pair);
                }
            }
        }

        //
        // Finally, create new GIOPClients for all connectors we can get
        //
        org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry = orbInstance_.getConFactoryRegistry();
        for (org.apache.yoko.orb.OCI.ConFactory factory : conFactoryRegistry.get_factories()) {
            for (org.apache.yoko.orb.OCI.Connector connector : factory.create_connectors(ior, policies)) {
                //
                // Skip any connector whose protocol is not present in the
                // protocol list
                //
                if (protocolPolicy != null && !protocolPolicy.contains(connector.id())) {
                    continue;
                }

                //
                // Get all usable profiles
                //
                org.apache.yoko.orb.OCI.ProfileInfo[] profileInfos = connector.get_usable_profiles(ior, policies);
                Assert._OB_assert(profileInfos.length != 0);

                //
                // Create a new GIOPClient for each usable profile, and set
                // the concurrency model and code converters. Filter out
                // clients that are equivalent to other clients we already
                // have.
                //
                for (org.apache.yoko.orb.OCI.ProfileInfo profileInfo: profileInfos) {
                    CodeConverters conv = CodeSetUtil.getCodeConverters(orbInstance_, profileInfo);

                    Client newClient = new GIOPClient(orbInstance_, connector, concModel_, conv, enableBidir);

                    if (!pairs.isEmpty()) {
                        boolean matched = false;

                        for (ClientProfilePair pair : pairs) {
                            if (pair.client.matches(newClient)) {
                                matched = true;
                                break;
                            }
                        }

                        if (matched) {
                            newClient.destroy(false);
                            continue;
                        }
                    }

                    //
                    // Add the new client to the list of all clients
                    //
                    allClients_.addElement(newClient);

                    //
                    // Add client/profile pairs
                    //
                    for (org.apache.yoko.orb.OCI.ProfileInfo clientProfileInfo : newClient.getUsableProfiles(ior, policies)) {
                        ClientProfilePair pair = new ClientProfilePair();
                        pair.client = newClient;
                        pair.profile = clientProfileInfo;
                        pairs.addElement(pair);
                    }

                    //
                    // If no private clients have been requested, also add the
                    // client to the list of existing reusable clients
                    //
                    if (!privateClients) {
                        reusableClients_.addElement(newClient);
                    }
                }
            }
        }

        //
        // If there is a protocol policy, then the client/profile pairs
        // have already been filtered. Now we need to sort the pairs in
        // the order specified by the policy. Note that clients which
        // do not have a ConnectorInfo are assumed to be local, and will
        // be ordered before the other clients.
        //
        if (!pairs.isEmpty() && protocolPolicy != null) {
            String[] protocols = protocolPolicy.value();

            Vector<ClientProfilePair> newPairs = new Vector<>();

            //
            // First, add any pairs whose clients do not have ConnectorInfo
            //
            for (ClientProfilePair pair : pairs) {
                if (pair.client.connectorInfo() == null) {
                    newPairs.addElement(pair);
                }
            }

            //
            // Next, add the pairs in the order specified by the policy
            //
            for (String protocol : protocols) {
                for (ClientProfilePair pair : pairs) {
                    ConnectorInfo info = pair.client.connectorInfo();
                    if (info != null && protocol.equals(info.id())) {
                        newPairs.addElement(pair);
                    }
                }
            }

            pairs = newPairs;
        }

        //
        // If we still don't have any client/profile pairs, throw a
        // TRANSIENT exception
        //
        if (pairs.isEmpty()) {
            throw new org.omg.CORBA.TRANSIENT(org.apache.yoko.orb.OB.MinorCodes
                    .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorNoUsableProfileInIOR)
                    + "Unable to create client",
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoUsableProfileInIOR,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        //
        // Increment the usage count on all clients
        //
        for (ClientProfilePair pair : pairs) {
            pair.client.incUsage();
        }
        return pairs;
    }

    public synchronized void releaseClient(Client client, boolean terminate) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            return;
        }

        //
        // TODO:
        //
        // We need to examine the entire shutdown sequence again since the
        // GIOPClientWorkers and what-not get destroyed after the ORB has
        // been marked as destroyed
        //
        // if(destroy_)
        // throw new org.omg.CORBA.INITIALIZE(
        // org.apache.yoko.orb.OB.MinorCodes.describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
        // org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
        // org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        boolean inUse = client.decUsage();

        if (!inUse) {
            reusableClients_.remove(client);

            if (allClients_.remove(client)) {
                client.destroy(terminate);
            } else {
                Assert._OB_assert("Release called on unknown client");
            }
        }
    }

    public boolean equivalent(org.omg.IOP.IOR ior1, org.omg.IOP.IOR ior2) {
        org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry = orbInstance_.getConFactoryRegistry();

        for (org.apache.yoko.orb.OCI.ConFactory factory : conFactoryRegistry.get_factories()) {
            if (!!!factory.equivalent(ior1, ior2)) {
                return false;
            }
        }
        return true;
    }

    public int hash(org.omg.IOP.IOR ior, int maximum) {
        return Arrays.hashCode(orbInstance_.getConFactoryRegistry().get_factories());
    }
}

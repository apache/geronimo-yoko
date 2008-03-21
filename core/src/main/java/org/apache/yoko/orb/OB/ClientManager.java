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
 
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OB.CONNECTION_REUSE_POLICY_ID;
import org.apache.yoko.orb.OB.ConnectionReusePolicy;
import org.apache.yoko.orb.OB.ConnectionReusePolicyHelper;
import org.apache.yoko.orb.OB.PROTOCOL_POLICY_ID;
import org.apache.yoko.orb.OB.ProtocolPolicy;
import org.apache.yoko.orb.OB.ProtocolPolicyHelper;
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
    private java.util.Vector allClients_ = new java.util.Vector();

    //
    // All reusable clients
    //
    private java.util.Vector reusableClients_ = new java.util.Vector();

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
        java.util.Enumeration e = allClients_.elements();
        while (e.hasMoreElements()) {
            Client client = (Client) e.nextElement();
            client.destroy(false);
        }

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
    public synchronized java.util.Vector getClientProfilePairs(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies) {
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
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == CONNECTION_REUSE_POLICY_ID.value) {
                ConnectionReusePolicy p = ConnectionReusePolicyHelper.narrow(policies[i]);
                if (p.value() == false) {
                    privateClients = true;
                }
                break;
            }
        }

        //
        // Get the protocol policy, if any
        //
        ProtocolPolicy protocolPolicy = null;
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == PROTOCOL_POLICY_ID.value) {
                protocolPolicy = ProtocolPolicyHelper.narrow(policies[i]);
                break;
            }
        }

        //
        // check whether the BiDir policy is enabled
        //
        boolean enableBidir = false;
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE.value) {
                org.omg.BiDirPolicy.BidirectionalPolicy p = org.omg.BiDirPolicy.BidirectionalPolicyHelper
                        .narrow(policies[i]);
                if (p.value() == org.omg.BiDirPolicy.BOTH.value) {
                    enableBidir = true;
                }
            }
        }

        java.util.Vector pairs = new java.util.Vector();

        //
        // First try to create CollocatedClients
        //
        org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactory = orbInstance_.getPOAManagerFactory();
        org.omg.PortableServer.POAManager[] managers = pmFactory.list();

        for (int i = 0; i < managers.length; i++) {
            try {
                org.apache.yoko.orb.OCI.Acceptor[] acceptors = ((org.apache.yoko.orb.OBPortableServer.POAManager) managers[i])
                        .get_acceptors();

                boolean local = false;
                for (int j = 0; j < acceptors.length && !local; j++) {
                    org.apache.yoko.orb.OCI.ProfileInfo[] localProfileInfos = acceptors[j].get_local_profiles(ior);
                    if (localProfileInfos.length > 0) {
                        local = true;
                    }
                }

                // we can get into hang situations if we return a collocated server for an 
                // inactive POA.  This can happen with the RootPOA, which is generally not activated. 
                if (local && managers[i].get_state() == State.ACTIVE) {
                    //
                    // Retrieve the CollocatedServer from the POAManager
                    //
                    org.apache.yoko.orb.OBPortableServer.POAManager_impl manager = (org.apache.yoko.orb.OBPortableServer.POAManager_impl) managers[i];
                    CollocatedServer collocatedServer = manager._OB_getCollocatedServer();

                    //
                    // Create a new CollocatedClient and add the new client to
                    // both the list of all clients, and the list of clients
                    // that is returned
                    //
                    CodeConverters conv = new CodeConverters();
                    Client client = new CollocatedClient(collocatedServer, concModel_, conv);
                    allClients_.addElement(client);

                    org.apache.yoko.orb.OCI.ProfileInfo[] profileInfos = client.getUsableProfiles(ior, policies);
                    for (int j = 0; j < profileInfos.length; j++) {
                        ClientProfilePair pair = new ClientProfilePair();
                        pair.client = client;
                        pair.profile = profileInfos[j];
                        pairs.addElement(pair);
                    }

                    //
                    // TODO: Introduce reusable CollocatedClients?
                    //
                }
            } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
                // Ignore
            }
        }

        //
        // If no private clients are requested, add all existing reusable
        // clients which are usable for the given IOR and policies
        //
        if (!privateClients) {
            for (int i = 0; i < reusableClients_.size(); i++) {
                Client reusableClient = (Client) reusableClients_.elementAt(i);

                //
                // Skip any client whose protocol is not present in the
                // protocol list
                //
                if (protocolPolicy != null) {
                    org.apache.yoko.orb.OCI.ConnectorInfo info = reusableClient.connectorInfo();
                    if (info != null) {
                        if (!protocolPolicy.contains(info.id())) {
                            continue;
                        }
                    }
                }

                org.apache.yoko.orb.OCI.ProfileInfo[] profileInfos = reusableClient
                        .getUsableProfiles(ior, policies);
                for (int j = 0; j < profileInfos.length; j++) {
                    ClientProfilePair pair = new ClientProfilePair();
                    pair.client = reusableClient;
                    pair.profile = profileInfos[j];
                    pairs.addElement(pair);
                }
            }
        }

        //
        // Finally, create new GIOPClients for all connectors we can get
        //
        org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry = orbInstance_
                .getConFactoryRegistry();
        org.apache.yoko.orb.OCI.ConFactory[] factories = conFactoryRegistry
                .get_factories();
        for (int i = 0; i < factories.length; i++) {
            org.apache.yoko.orb.OCI.Connector[] connectors = factories[i]
                    .create_connectors(ior, policies);
            for (int j = 0; j < connectors.length; j++) {
                //
                // Skip any connector whose protocol is not present in the
                // protocol list
                //
                if (protocolPolicy != null)
                {
                    if (!protocolPolicy.contains(connectors[j].id())) {
                        continue;
                    }
                }

                //
                // Get all usable profiles
                //
                org.apache.yoko.orb.OCI.ProfileInfo[] profileInfos = connectors[j].get_usable_profiles(ior, policies);
                Assert._OB_assert(profileInfos.length >= 1);

                //
                // Create a new GIOPClient for each usable profile, and set
                // the concurrency model and code converters. Filter out
                // clients that are equivalent to other clients we already
                // have.
                //
                for (int k = 0; k < profileInfos.length; k++) {
                    CodeConverters conv = CodeSetUtil.getCodeConverters(
                            orbInstance_, profileInfos[k]);

                    Client client = new GIOPClient(orbInstance_, connectors[j],
                            concModel_, conv, enableBidir);

                    if (!pairs.isEmpty()) {
                        int l;

                        for (l = 0; l < pairs.size(); l++) {
                            ClientProfilePair pair = (ClientProfilePair) pairs.elementAt(l);
                            if (pair.client.equal(client)) {
                                break;
                            }
                        }

                        if (l != pairs.size()) {
                            client.destroy(false);
                            continue;
                        }
                    }

                    //
                    // Add the new client to the list of all clients
                    //
                    allClients_.addElement(client);

                    //
                    // Add client/profile pairs
                    //
                    org.apache.yoko.orb.OCI.ProfileInfo[] clientProfileInfos = client
                            .getUsableProfiles(ior, policies);
                    for (int l = 0; l < clientProfileInfos.length; l++) {
                        ClientProfilePair pair = new ClientProfilePair();
                        pair.client = client;
                        pair.profile = clientProfileInfos[l];
                        pairs.addElement(pair);
                    }

                    //
                    // If no private clients have been requested, also add the
                    // client to the list of existing reusable clients
                    //
                    if (!privateClients) {
                        reusableClients_.addElement(client);
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

            java.util.Vector newPairs = new java.util.Vector();

            //
            // First, add any pairs whose clients do not have ConnectorInfo
            //
            for (int i = 0; i < pairs.size(); i++) {
                ClientProfilePair pair = (ClientProfilePair) pairs.elementAt(i);
                org.apache.yoko.orb.OCI.ConnectorInfo info = pair.client.connectorInfo();
                if (info == null) {
                    newPairs.addElement(pair);
                }
            }

            //
            // Next, add the pairs in the order specified by the policy
            //
            for (int i = 0; i < protocols.length; i++) {
                for (int j = 0; j < pairs.size(); j++) {
                    ClientProfilePair pair = (ClientProfilePair) pairs.elementAt(j);
                    org.apache.yoko.orb.OCI.ConnectorInfo info = pair.client.connectorInfo();
                    if (info != null) {
                        if (protocols[i].equals(info.id())) {
                            newPairs.addElement(pair);
                        }
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
        for (int i = 0; i < pairs.size(); i++) {
            ClientProfilePair pair = (ClientProfilePair) pairs.elementAt(i);
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
            int i;

            for (i = 0; i < reusableClients_.size(); i++) {
                Client c = (Client) reusableClients_.elementAt(i);
                if (c == client) {
                    reusableClients_.removeElementAt(i);
                    break;
                }
            }

            for (i = 0; i < allClients_.size(); i++) {
                Client c = (Client) allClients_.elementAt(i);
                if (c == client) {
                    client.destroy(terminate);
                    allClients_.removeElementAt(i);
                    return;
                }
            }

            Assert._OB_assert("Release called on unknown client");
        }
    }

    public boolean equivalent(org.omg.IOP.IOR ior1, org.omg.IOP.IOR ior2) {
        org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry = orbInstance_
                .getConFactoryRegistry();

        org.apache.yoko.orb.OCI.ConFactory[] factories = conFactoryRegistry
                .get_factories();
        for (int i = 0; i < factories.length; i++) {
            if (!factories[i].equivalent(ior1, ior2)) {
                return false;
            }
        }
        return true;
    }

    public int hash(org.omg.IOP.IOR ior, int maximum) {
        org.apache.yoko.orb.OCI.ConFactoryRegistry conFactoryRegistry = orbInstance_
                .getConFactoryRegistry();

        org.apache.yoko.orb.OCI.ConFactory[] factories = conFactoryRegistry.get_factories();
        int hash = 0;
        for (int i = 0; i < factories.length; i++) {
            hash ^= factories[i].hash(ior, maximum);
        }
        return hash % (maximum + 1);
    }
}

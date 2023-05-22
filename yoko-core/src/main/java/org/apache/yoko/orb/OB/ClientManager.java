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
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.OBPortableServer.POAManager;
import org.apache.yoko.orb.OBPortableServer.POAManagerFactory;
import org.apache.yoko.orb.OBPortableServer.POAManager_impl;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.ConFactory;
import org.apache.yoko.orb.OCI.ConFactoryRegistry;
import org.apache.yoko.orb.OCI.Connector;
import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;
import org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE;
import org.omg.BiDirPolicy.BOTH;
import org.omg.BiDirPolicy.BidirectionalPolicy;
import org.omg.BiDirPolicy.BidirectionalPolicyHelper;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAManagerPackage.State;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Collections.synchronizedSet;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public final class ClientManager {
    static final Logger logger = Logger.getLogger(ClientManager.class.getName());
    private boolean destroyed; // True if destroy() was called

    private ORBInstance orbInstance;

    private Set<Client> allClients = synchronizedSet(new HashSet<Client>());

    private Set<Client> reusableClients = synchronizedSet(new HashSet<Client>());

    //
    // The concurrency model with which new Clients are created
    //
    private int concModel_;

    // ----------------------------------------------------------------------
    // ClientManager private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert.ensure(destroyed);
        Assert.ensure(allClients.isEmpty());
        Assert.ensure(reusableClients.isEmpty());

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // ClientManager package member implementations
    // ----------------------------------------------------------------------

    synchronized void destroy() {
        //
        // Don't destroy twice
        //
        if (destroyed) {
            return;
        }

        //
        // Set the destroy flag
        //
        destroyed = true;

        //
        // Destroy all clients
        //
        for (Client c : allClients) c.destroy();

        //
        // Reset internal data
        //
        orbInstance = null;
        allClients.clear();
        reusableClients.clear();
    }

    // ----------------------------------------------------------------------
    // ClientManager public member implementations
    // ----------------------------------------------------------------------

    public ClientManager(int concModel) {
        destroyed = false;
        concModel_ = concModel;
    }

    public void setORBInstance(ORBInstance instance) {
        orbInstance = instance;
    }

    //
    // Get a list of ClientProfilePairs for an IOR and a list of policies
    //
    public synchronized Vector<ClientProfilePair> getClientProfilePairs(IOR ior, Policy[] policies) {
        Assert.ensure(ior.type_id != null);

        //
        // Can't create a Client for a nil object
        //
        if (ior.type_id.length() == 0 && ior.profiles.length == 0) {
            throw new INV_OBJREF("Object reference is nil");
        }

        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroyed) {
            throw new INITIALIZE(MinorCodes
                    .describeInitialize(MinorCodes.MinorORBDestroyed),
                    MinorCodes.MinorORBDestroyed,
                    COMPLETED_NO);
        }


        //
        // Find out whether private clients are requested
        //
        boolean reuseConnections = true;
        for (Policy pol : policies) {
            if (pol.policy_type() == CONNECTION_REUSE_POLICY_ID.value) {
                reuseConnections = ConnectionReusePolicyHelper.narrow(pol).value();
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
            if (pol.policy_type() == BIDIRECTIONAL_POLICY_TYPE.value) {
                BidirectionalPolicy p = BidirectionalPolicyHelper
                        .narrow(pol);
                if (p.value() == BOTH.value) {
                    enableBidir = true;
                }
            }
        }

        Vector<ClientProfilePair> pairs = new Vector<>();

        //
        // First try to create CollocatedClients
        //
        POAManagerFactory pmFactory = orbInstance.getPOAManagerFactory();
        for (org.omg.PortableServer.POAManager mgr : pmFactory.list()) {
            try {
                boolean local = false;
                for (Acceptor acceptor : ((POAManager)mgr).get_acceptors()) {
                    ProfileInfo[] localProfileInfos = acceptor.get_local_profiles(ior);
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
                    POAManager_impl manager = (POAManager_impl) mgr;
                    CollocatedServer collocatedServer = manager._OB_getCollocatedServer();

                    // Create and register a new CollocatedClient
                    Client client = new CollocatedClient(collocatedServer, concModel_);
                    allClients.add(client);

                    // add the information for the new client to the collection to be returned
                    for (ProfileInfo profileInfo : client.getUsableProfiles(ior, policies)) {
                        ClientProfilePair pair = new ClientProfilePair();
                        pair.client = client;
                        pair.profile = profileInfo;
                        pairs.addElement(pair);
                    }

                    //
                    // TODO: Introduce reusable CollocatedClients?
                    //
                }
            } catch (AdapterInactive ignored) {
            }
        }

        //
        // If connection reuse is permitted, add all existing reusable
        // clients which are usable for the given IOR and policies
        //
        if (reuseConnections) {
            for (Client reusableClient : reusableClients) {

                //
                // Skip any client whose protocol is not present in the
                // protocol list
                //
                if (protocolPolicy != null) {
                    ConnectorInfo info = reusableClient.connectorInfo();
                    if (info != null && !protocolPolicy.contains(info.id())) {
                        continue;
                    }
                }

                for (ProfileInfo profileInfo : reusableClient.getUsableProfiles(ior, policies)) {
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
        ConFactoryRegistry conFactoryRegistry = orbInstance.getConFactoryRegistry();
        ConFactory[] factories = conFactoryRegistry.get_factories();
        for (ConFactory factory : factories) {
            Connector[] connectors = factory.create_connectors(ior, policies);
            for (Connector connector : connectors) {
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
                ProfileInfo[] profileInfos = connector.get_usable_profiles(ior, policies);
//                Assert._OB_assert(profileInfos.length != 0);
                if (profileInfos.length == 0) {
                    continue;
                }

                //
                // Create a new GIOPClient for each usable profile, and set
                // the concurrency model and code converters. Filter out
                // clients that are equivalent to other clients we already
                // have.
                //
                for (ProfileInfo profileInfo: profileInfos) {
                    CodeConverters conv = CodeSetUtil.getCodeConverters(orbInstance, profileInfo);

                    Client newClient = new GIOPClient(orbInstance, connector, concModel_, conv, enableBidir);

                    if (!pairs.isEmpty()) {
                        boolean matched = false;

                        for (ClientProfilePair pair : pairs) {
                            if (pair.client.matches(newClient)) {
                                matched = true;
                                break;
                            }
                        }

                        if (matched) {
                            newClient.destroy();
                            continue;
                        }
                    }

                    allClients.add(newClient);

                    //
                    // Add client/profile pairs
                    //
                    for (ProfileInfo clientProfileInfo : newClient.getUsableProfiles(ior, policies)) {
                        ClientProfilePair pair = new ClientProfilePair();
                        pair.client = newClient;
                        pair.profile = clientProfileInfo;
                        pairs.addElement(pair);
                    }

                    // If no private clients have been requested, also add the
                    // client to the list of existing reusable clients
                    if (reuseConnections) reusableClients.add(newClient);
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
            throw Transients.NO_USABLE_PROFILE_IN_IOR.create();
        }

        //
        // Increment the usage count on all clients
        //
        for (ClientProfilePair pair : pairs) {
            pair.client.obtain();
        }
        return pairs;
    }

    public synchronized void releaseClient(Client client) {
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        if (destroyed) return;
        if (client.release()) destroyClient(client);
    }

    /**
     * Instructs the client manager never to reuse a client or expect any further notification regarding it
     */
    public synchronized void besmirchClient(Client client) {
        if (logger.isLoggable(Level.FINE)) logger.fine("Client besmirched: " + client);
        destroyClient(client);
    }

    private void destroyClient(Client client) {
        reusableClients.remove(client);
        allClients.remove(client);
        client.destroy();
    }

    public boolean equivalent(IOR ior1, IOR ior2) {
        ConFactoryRegistry conFactoryRegistry = orbInstance.getConFactoryRegistry();

        for (ConFactory factory : conFactoryRegistry.get_factories()) {
            if (!!!factory.equivalent(ior1, ior2)) {
                return false;
            }
        }
        return true;
    }

    public int hash(IOR ior, int maximum) {
        return Arrays.hashCode(orbInstance.getConFactoryRegistry().get_factories());
    }
}

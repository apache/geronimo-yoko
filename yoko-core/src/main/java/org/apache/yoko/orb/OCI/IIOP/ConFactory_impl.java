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

package org.apache.yoko.orb.OCI.IIOP;

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.OB.IORDump;
import org.apache.yoko.orb.OB.IORUtil;
import org.apache.yoko.orb.OB.PROTOCOL_POLICY_ID;
import org.apache.yoko.orb.OB.ProtocolPolicy;
import org.apache.yoko.orb.OB.ProtocolPolicyHelper;
import org.apache.yoko.orb.OCI.ConFactory;
import org.apache.yoko.orb.OCI.ConnectCB;
import org.apache.yoko.orb.OCI.Connector;
import org.apache.yoko.util.Assert;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.CSIIOP.TransportAddress;
import org.omg.IIOP.ProfileBody_1_0;
import org.omg.IIOP.ProfileBody_1_0Helper;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import org.omg.IOP.IOR;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedComponentHelper;
import org.omg.IOP.TaggedProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.yoko.util.Hex.formatHexPara;

final class ConFactory_impl extends LocalObject implements
        ConFactory {
    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(ConFactory.class.getName());
    private static final Encoding CDR_1_2_ENCODING = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);

    private final boolean keepAlive_; // The keepalive flag

    private final ORB orb_; // The ORB

    private final ConFactoryInfo_impl info_; // ConFactory info

    private final ListenerMap listenMap_;

    private final UnifiedConnectionHelper connectionHelper;

    private final Set<Integer> helperComponentTags;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return TAG_INTERNET_IOP.value;
    }

    public String describe_profile(TaggedProfile profile) {
        Assert.ensure(profile.tag == TAG_INTERNET_IOP.value);

        //
        // Get the IIOP profile body
        //
        InputStream in = new InputStream(profile.profile_data);
        in._OB_readEndian();
        ProfileBody_1_0 body = ProfileBody_1_0Helper.read(in);

        StringBuilder result = new StringBuilder();

        //
        // Show general info
        //
        result.append("iiop_version: " + (int) body.iiop_version.major + '.'
                + (int) body.iiop_version.minor + '\n');
        result.append("host: " + body.host + '\n');
        final int port = ((char)body.port);
        result.append("port: " + port + '\n');
        result.append("object_key: (" + body.object_key.length + ")\n");
        formatHexPara(body.object_key, 0, body.object_key.length, result);

        //
        // Print IIOP 1.1 information (components)
        //
        if (body.iiop_version.major > 1 || body.iiop_version.minor >= 1) {
            final int tcCount = in.read_ulong();

            for (int i = 0; i < tcCount; i++) {
                TaggedComponent component = TaggedComponentHelper.read(in);

                IORUtil.describe_component(component, result);
            }
        }

        return result.toString();
    }

    private static final Connector[] EMPTY_CONNECTORS = new Connector[0];

    public Connector[] create_connectors(IOR ior, Policy[] policies) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Creating connection for ior: " + IORDump.PrintObjref(orb_, ior));
        }

        //
        // Check whether policies are satisfied
        //
        for (Policy policy: policies) {
            if (policy.policy_type() == PROTOCOL_POLICY_ID.value) {
                ProtocolPolicy protocolPolicy = ProtocolPolicyHelper.narrow(policy);
                if (!protocolPolicy.contains(PLUGIN_ID.value))
                    return EMPTY_CONNECTORS;
            }
        }

        List<Connector> iopConnectors = new ArrayList<>(); // the main host and port from each profile
        List<Connector> extConnectors = new ArrayList<>(); // used for CSIv2
        //
        // Create Connectors from profiles
        //
        for (TaggedProfile profile: ior.profiles) {
            if (profile.tag != tag()) continue;

            //
            // Get the IIOP profile body
            //
            final InputStream in = new InputStream(profile.profile_data);
            in._OB_readEndian();
            final ProfileBody_1_0 body = ProfileBody_1_0Helper.read(in);
            boolean recordPortZero = false;

            //
            // Retrieve CodecFactory via ORB initial refs
            //
            Codec codec = null;
            try {
                codec = ((CodecFactory) orb_.resolve_initial_references("CodecFactory")).create_codec(CDR_1_2_ENCODING);
            } catch (InvalidName e) {
                logger.fine("Could not obtain codec factory using name 'CodecFactory'");
            } catch (UnknownEncoding e) {
                logger.fine("Could not obtain codec using encoding " + CDR_1_2_ENCODING);
            }

            if (body.port == 0) {
                // If the port is zero, this profile does not support unsecured connections.
                // if the helper provides the transport addresses for the endpoints, we won't create a connector
                // but if it does not provide the transport addresses, we will create the zero port connector
                // so that the helper gets a chance to handle the zero port later
                recordPortZero = true;
            } else {
                //
                // Create new connector for this profile
                //
                final int port = ((char)body.port);
                logger.fine("Creating connector to host=" + body.host + ", port=" + port);
                ConnectCB[] cbs = info_._OB_getConnectCBSeq();
                iopConnectors.add(createConnector(ior, policies, body.host, port, cbs, codec));
            }
            //
            // If this is a 1.1 profile, check for
            // TAG_ALTERNATE_IIOP_ADDRESS in the components
            //
            if (body.iiop_version.major == 1 && body.iiop_version.minor == 0)
                continue;
            //
            // Unmarshal the tagged components
            //
            final int tcCount = in.read_ulong();
            TaggedComponent[] components = new TaggedComponent[tcCount];
            for (int c = 0; c < tcCount; c++)
                components[c] = TaggedComponentHelper.read(in);

            //
            // Check for tag components - yoko deals with TAG_ALTERNATE_IIOP_ADDRESS
            // and the extendedConnectionHelper can express an interest in any other tagged component
            // (e.g. the TAG_CSI_SEC_MECH_LIST)
            //
            final ConnectCB[] ccbs = info_._OB_getConnectCBSeq();
            for (TaggedComponent tc: components) {
                if (tc.tag == TAG_ALTERNATE_IIOP_ADDRESS.value) {
                    final InputStream cin = new InputStream(tc.component_data);
                    cin._OB_readEndian();
                    final String host = cin.read_string();
                    final short s = cin.read_ushort();
                    final int cport = ((char)s);

                    if (logger.isLoggable(Level.FINE)) logger.fine("Creating alternate connector to host=" + host + ", port=" + cport);
                    Connector newConnector = createConnector(ior, policies, host, cport, ccbs, codec);
                    iopConnectors.add(newConnector);
                } else if (helperComponentTags.contains(tc.tag)) {
                    for (TransportAddress endpoint : connectionHelper.getEndpoints(tc, policies)) {
                        if (logger.isLoggable(Level.FINE)) logger.fine("Creating extended connector to host=" + endpoint.host_name + ", port=" + endpoint.port);
                        Connector newConnector = createConnector(ior, policies, endpoint.host_name, endpoint.port, ccbs, codec);
                        extConnectors.add(newConnector);
                        recordPortZero = false;
                    }
                }
            }

            // if there was a port value of zero in the profile and the helper did not provide alternate addresses
            // to connect to (i.e. CSIv2 ones) then we create the zero port connector to give the helper a second chance
            // to handle it
            if (recordPortZero) {
                logger.fine("Creating connector with port=0 to host=" + body.host);
                ConnectCB[] cbs = info_._OB_getConnectCBSeq();
                iopConnectors.add(createConnector(ior, policies, body.host, 0, cbs, codec));
            }
        }
        final List<Connector> connectors = extConnectors.isEmpty() ? iopConnectors : extConnectors;
        return connectors.toArray(EMPTY_CONNECTORS);
    }

    private Connector createConnector(IOR ior, Policy[] policies, String host, int port, ConnectCB[] cbs, Codec codec) {
        return new Connector_impl(ior, policies, host, port, keepAlive_, cbs, listenMap_, connectionHelper, codec);
    }

    public boolean equivalent(IOR ior1, IOR ior2) {
        return Util.equivalent(ior1, ior2);
    }

    public int hash(IOR ior, int max) {
        return Util.hash(ior, max);
    }

    public org.apache.yoko.orb.OCI.ConFactoryInfo get_info() {
        return info_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ConFactory_impl(ORB orb, boolean keepAlive, ListenerMap lm, UnifiedConnectionHelper helper) {
        orb_ = orb;
        keepAlive_ = keepAlive;
        info_ = new ConFactoryInfo_impl();
        listenMap_ = lm;
        connectionHelper = helper;
        helperComponentTags = helper.tags();
    }
}

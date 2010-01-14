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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OCI.IIOP.PLUGIN_ID;

final class ConFactory_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OCI.ConFactory {
    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(org.apache.yoko.orb.OCI.ConFactory.class.getName());

    private boolean keepAlive_; // The keepalive flag
    
    private org.omg.CORBA.ORB orb_; // The ORB

    private ConFactoryInfo_impl info_; // ConFactory info

    private ListenerMap listenMap_;

    private ConnectionHelper connectionHelper_;  // plugin for making ssl transport decisions.

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return org.omg.IOP.TAG_INTERNET_IOP.value;
    }

    public String describe_profile(org.omg.IOP.TaggedProfile profile) {
        org.apache.yoko.orb.OB.Assert
                ._OB_assert(profile.tag == org.omg.IOP.TAG_INTERNET_IOP.value);

        //
        // Get the IIOP profile body
        //
        byte[] data = profile.profile_data;
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                data, data.length);
        org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                buf);
        in._OB_readEndian();
        org.omg.IIOP.ProfileBody_1_0 body = org.omg.IIOP.ProfileBody_1_0Helper
                .read(in);

        StringBuffer result = new StringBuffer();

        //
        // Show general info
        //
        result.append("iiop_version: " + (int) body.iiop_version.major + '.'
                + (int) body.iiop_version.minor + '\n');
        result.append("host: " + body.host + '\n');
        int port;
        if (body.port < 0)
            port = 0xffff + (int) body.port + 1;
        else
            port = body.port;
        result.append("port: " + port + '\n');
        result.append("object_key: (" + body.object_key.length + ")\n");
        String tmp = org.apache.yoko.orb.OB.IORUtil.dump_octets(
                body.object_key, 0, body.object_key.length);
        result.append(tmp);

        //
        // Print IIOP 1.1 information (components)
        //
        if (body.iiop_version.major > 1 || body.iiop_version.minor >= 1) {
            int l = in.read_ulong();

            for (int i = 0; i < l; i++) {
                org.omg.IOP.TaggedComponent component = org.omg.IOP.TaggedComponentHelper
                        .read(in);

                String desc = org.apache.yoko.orb.OB.IORUtil
                        .describe_component(component);
                result.append(desc);
            }
        }

        return result.toString();
    }

    public org.apache.yoko.orb.OCI.Connector[] create_connectors(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Creating connection for ior: " + org.apache.yoko.orb.OB.IORDump.PrintObjref(orb_, ior)); 
        }
        
        //
        // Check whether policies are satisfied
        //
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.apache.yoko.orb.OB.PROTOCOL_POLICY_ID.value) {
                org.apache.yoko.orb.OB.ProtocolPolicy protocolPolicy = org.apache.yoko.orb.OB.ProtocolPolicyHelper
                        .narrow(policies[i]);
                if (!protocolPolicy.contains(PLUGIN_ID.value))
                    return new org.apache.yoko.orb.OCI.Connector[0];
            }
        }

        //
        // Create Connectors from profiles
        //
        java.util.Vector seq = new java.util.Vector();
        for (int i = 0; i < ior.profiles.length; i++) {
            if (ior.profiles[i].tag == tag()) {
                //
                // Get the IIOP profile body
                //
                byte[] data = ior.profiles[i].profile_data;
                org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                        data, data.length);
                org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                        buf, 0, false);
                in._OB_readEndian();
                org.omg.IIOP.ProfileBody_1_0 body = org.omg.IIOP.ProfileBody_1_0Helper
                        .read(in);

                //
                // Create new connector for this profile
                //
                int port;
                if (body.port < 0)
                    port = 0xffff + (int) body.port + 1;
                else
                    port = (int) body.port;
                org.apache.yoko.orb.OCI.ConnectCB[] cbs = info_
                        ._OB_getConnectCBSeq();
                logger.fine("Creating connector to host=" + body.host +", port=" + port);
                seq.addElement(new Connector_impl(ior, policies, body.host, port, keepAlive_,
                        cbs, listenMap_, connectionHelper_));

                //
                // If this is a 1.1 profile, check for
                // TAG_ALTERNATE_IIOP_ADDRESS in the components
                //
                if (body.iiop_version.major > 1 || body.iiop_version.minor > 0) {
                    //
                    // Unmarshal the tagged components
                    //
                    int len = in.read_ulong();
                    org.omg.IOP.TaggedComponent[] components = new org.omg.IOP.TaggedComponent[len];
                    for (int c = 0; c < len; c++)
                        components[c] = org.omg.IOP.TaggedComponentHelper
                                .read(in);

                    //
                    // Check for TAG_ALTERNATE_IIOP_ADDRESS
                    //
                    for (int c = 0; c < components.length; c++)
                        if (components[c].tag == org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS.value) {
                            byte[] cdata = components[c].component_data;
                            int clen = components[c].component_data.length;
                            org.apache.yoko.orb.OCI.Buffer cbuf = new org.apache.yoko.orb.OCI.Buffer(
                                    cdata, clen);
                            org.apache.yoko.orb.CORBA.InputStream cin = new org.apache.yoko.orb.CORBA.InputStream(
                                    cbuf, 0, false);
                            cin._OB_readEndian();
                            String host = cin.read_string();
                            short s = cin.read_ushort();
                            int cport;
                            if (s < 0)
                                cport = 0xffff + (int) s + 1;
                            else
                                cport = (int) s;

                            //
                            // Create new connector for this component
                            //
                            org.apache.yoko.orb.OCI.ConnectCB[] ccbs = info_
                                    ._OB_getConnectCBSeq();
                            logger.fine("Creating alternate connector to host=" + host +", port=" + cport);
                            seq.addElement(new Connector_impl(ior, policies, host, cport,
                                    keepAlive_, ccbs, listenMap_, connectionHelper_));
                        }
                }
            }
        }

        org.apache.yoko.orb.OCI.Connector[] result = new org.apache.yoko.orb.OCI.Connector[seq
                .size()];
        seq.copyInto(result);
        return result;
    }

    public boolean equivalent(org.omg.IOP.IOR ior1, org.omg.IOP.IOR ior2) {
        return Util.equivalent(ior1, ior2);
    }

    public int hash(org.omg.IOP.IOR ior, int max) {
        return Util.hash(ior, max);
    }

    public org.apache.yoko.orb.OCI.ConFactoryInfo get_info() {
        return info_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public ConFactory_impl(org.omg.CORBA.ORB orb, boolean keepAlive, ListenerMap lm, ConnectionHelper helper) {
        // System.out.println("ConFactory");
        orb_ = orb;
        keepAlive_ = keepAlive;
        info_ = new ConFactoryInfo_impl();
        listenMap_ = lm;
        connectionHelper_ = helper;
    }

    public void finalize() throws Throwable {
        // System.out.println("~ConFactory");
        super.finalize();
    }
}

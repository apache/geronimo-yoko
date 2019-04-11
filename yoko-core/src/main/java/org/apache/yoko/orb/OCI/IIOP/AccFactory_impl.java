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
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.Net;
import org.apache.yoko.orb.OCI.AccFactory;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.Buffer;
import org.apache.yoko.orb.OCI.InvalidParam;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.IIOP.ProfileBody_1_0;
import org.omg.IIOP.ProfileBody_1_0Helper;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.ENCODING_CDR_ENCAPS;
import org.omg.IOP.Encoding;
import org.omg.IOP.IORHolder;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedComponentHelper;

import java.util.Vector;
import java.util.logging.Logger;

final class AccFactory_impl extends LocalObject implements
        AccFactory {

    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(AccFactory.class.getName());
    private static final Encoding CDR_1_2_ENCODING = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);
    //
    // AccFactory information
    //
    private AccFactoryInfo_impl info_;
    
    private ORB orb_; // The ORB

    private ConnectionHelper connectionHelper_;   // client connection helper

    private ListenerMap listenMap_;
    private ExtendedConnectionHelper extendedConnectionHelper_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return TAG_INTERNET_IOP.value;
    }

    public org.apache.yoko.orb.OCI.AccFactoryInfo get_info() {
        return info_;
    }

    public Acceptor create_acceptor(String[] params) throws InvalidParam {
        String bind = null;
        String[] hosts = null;
        boolean keepAlive = true;
        boolean multiProfile = false;
        int port = 0;
        int backlog = 0;
        boolean numeric = false;
        
        int i = 0;
        while (i < params.length) {
            if (params[i].equals("--backlog")) {
                if (i + 1 >= params.length)
                    throw new InvalidParam(
                            "argument expected " + "for --backlog");
                String arg = params[i + 1];
                try {
                    backlog = Integer.valueOf(arg).intValue();
                } catch (NumberFormatException ex) {
                    throw new InvalidParam(
                            "invalid argument " + "for backlog");
                }
                if (backlog < 1 || backlog > 65535)
                    throw new InvalidParam(
                            "invalid backlog");
                i += 2;
            } else if (params[i].equals("--bind")) {
                if (i + 1 >= params.length)
                    throw new InvalidParam(
                            "argument expected " + "for --bind");
                bind = params[i + 1];
                i += 2;
            } else if (params[i].equals("--host")) {
                if (i + 1 >= params.length)
                    throw new InvalidParam(
                            "argument expected " + "for --host");
                Vector vec = new Vector();
                int start = 0;
                String str = params[i + 1];
                while (true) {
                    while (start < str.length() && str.charAt(start) == ' ')
                        start++;
                    if (start >= str.length())
                        break;
                    int comma = str.indexOf(',', start);
                    if (comma == start)
                        start++;
                    else {
                        if (comma == -1)
                            comma = str.length();
                        int end = comma - 1;
                        while (str.charAt(end) == ' ')
                            end--;
                        vec.addElement(str.substring(start, end + 1));
                        start = comma + 1;
                    }
                }
                if (vec.size() == 0)
                    throw new InvalidParam(
                            "invalid argument " + "for --host");
                hosts = new String[vec.size()];
                vec.copyInto(hosts);
                i += 2;
            } else if (params[i].equals("--multi-profile")) {
                multiProfile = true;
                i++;
            } else if (params[i].equals("--no-keepalive")) {
                keepAlive = false;
                i++;
            } else if (params[i].equals("--numeric")) {
                numeric = true;
                i++;
            } else if (params[i].equals("--port")) {
                if (i + 1 >= params.length)
                    throw new InvalidParam(
                            "argument expected " + "for --port");
                String arg = params[i + 1];
                try {
                    port = Integer.valueOf(arg).intValue();
                } catch (NumberFormatException ex) {
                    throw new InvalidParam(
                            "invalid argument " + "for port");
                }
                if (port < 1 || port > 65535)
                    throw new InvalidParam(
                            "invalid port");
                i += 2;
            } else  if (connectionHelper_ != null){
                throw new InvalidParam(
                        "unknown parameter: " + params[i]);
            } else {
                i++;
            }
        }

        if (hosts == null) {
            hosts = new String[1];
            hosts[0] = Net.getCanonicalHostname(numeric);
        }

        logger.fine("Creating acceptor for port=" + port);
        Codec codec;
        try {
                codec = ((CodecFactory) orb_.resolve_initial_references("CodecFactory")).create_codec(CDR_1_2_ENCODING);
        } catch (InvalidName e) {
            throw new InvalidParam("Could not obtain codec factory using name 'CodecFactory'");
        } catch (UnknownEncoding e) {
            throw new InvalidParam("Could not obtain codec using encoding " + CDR_1_2_ENCODING);
        }

        return new Acceptor_impl(bind, hosts, multiProfile, port, backlog, keepAlive, connectionHelper_, extendedConnectionHelper_, listenMap_, params, codec);
    }

    public void change_key(IORHolder ior, byte[] key) {
        //
        // Extract the IIOP profile information from the provided IOR
        //
        for (int profile = 0; profile < ior.value.profiles.length; profile++) {
            if (ior.value.profiles[profile].tag == TAG_INTERNET_IOP.value) {
                //
                // Extract the 1_0 profile body
                //
                Buffer buf = new Buffer(ior.value.profiles[profile].profile_data);
                InputStream in = new InputStream(buf, false, null, null);
                in._OB_readEndian();
                ProfileBody_1_0 body = ProfileBody_1_0Helper.read(in);

                //
                // Read components if the IIOP version is > 1.0
                //
                TaggedComponent[] components;
                if (body.iiop_version.major > 1 || body.iiop_version.minor > 0) {
                    int len = in.read_ulong();
                    components = new TaggedComponent[len];
                    for (int j = 0; j < len; j++)
                        components[j] = TaggedComponentHelper
                                .read(in);
                } else
                    components = new TaggedComponent[0];

                //
                // Fill in the new object-key
                //
                body.object_key = key;

                //
                // Remarshal the new body
                //
                try (OutputStream out = new OutputStream(new Buffer())) {
                    out._OB_writeEndian();
                    ProfileBody_1_0Helper.write(out, body);

                    //
                    // Remarshal the components if the IIOP version is > 1.0
                    //
                    if (body.iiop_version.major > 1 || body.iiop_version.minor > 0) {
                        out.write_ulong(components.length);
                        for (int i = 0; i < components.length; i++) {
                            TaggedComponentHelper.write(out, components[i]);
                        }
                    }
                    ior.value.profiles[profile].profile_data = out.copyWrittenBytes();
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public AccFactory_impl(ORB orb, ListenerMap lm, ConnectionHelper helper, ExtendedConnectionHelper extendedHelper) {
        Assert._OB_assert((helper == null) ^ (extendedHelper == null));
        orb_ = orb;
        info_ = new AccFactoryInfo_impl();
        listenMap_ = lm;
        connectionHelper_ = helper;
        extendedConnectionHelper_ = extendedHelper;
    }
    
}

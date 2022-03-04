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
import org.apache.yoko.orb.OB.Net;
import org.apache.yoko.orb.OCI.AccFactory;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.IIOP.Acceptor_impl.ProfileCardinality;
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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;
import static org.apache.yoko.orb.OCI.IIOP.Acceptor_impl.ProfileCardinality.MANY;
import static org.apache.yoko.orb.OCI.IIOP.Acceptor_impl.ProfileCardinality.ONE;
import static org.apache.yoko.orb.OCI.IIOP.Acceptor_impl.ProfileCardinality.ZERO;

final class AccFactory_impl extends LocalObject implements AccFactory {
    static final Logger logger = Logger.getLogger(AccFactory_impl.class.getName());
    private static final Encoding CDR_1_2_ENCODING = new Encoding(ENCODING_CDR_ENCAPS.value, (byte) 1, (byte) 2);
    private final AccFactoryInfo_impl info_;
    private final ORB orb_;
    private final UnifiedConnectionHelper connectionHelper;
    private final ListenerMap listenMap_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return TAG_INTERNET_IOP.value;
    }

    public AccFactoryInfo get_info() {
        return info_;
    }

    public Acceptor create_acceptor(String[] params) throws InvalidParam {
        String bind = null;
        String[] hosts = null;
        boolean keepAlive = true;
        ProfileCardinality numProfiles = ONE;
        int port = 0;
        int backlog = 0;
        boolean numeric = false;

        for (int i = 0; i < params.length; i++) {
            String option = params[i];
            try {
                switch (option) {
                case "--backlog":
                    i++;
                    String backlogArg = params[i];
                    try {
                        backlog = parseInt(backlogArg);
                    } catch (NumberFormatException ex) {
                        throw new InvalidParam("invalid argument for --backlog: " + backlogArg);
                    }
                    if (backlog < 1 || backlog > 65535) throw new InvalidParam("invalid backlog value: " + backlogArg);
                    break;

                case "--bind":
                    i++;
                    bind = params[i];
                    break;

                case "--host":
                    i++;
                    List<String> list = new ArrayList<>();
                    int start = 0;
                    String hostArg = params[i];
                    // TODO: use library functions instead to
                    // - split on commas
                    // - trim each element
                    // - if not empty add element to list
                    while (true) {
                        while (start < hostArg.length() && hostArg.charAt(start) == ' ')
                            start++;
                        if (start >= hostArg.length())
                            break;
                        int comma = hostArg.indexOf(',', start);
                        if (comma == start)
                            start++;
                        else {
                            if (comma == -1)
                                comma = hostArg.length();
                            int end = comma - 1;
                            while (hostArg.charAt(end) == ' ')
                                end--;
                            list.add(hostArg.substring(start, end + 1));
                            start = comma + 1;
                        }
                    }
                    if (list.isEmpty()) throw new InvalidParam("invalid argument for --host: " + hostArg);
                    hosts = list.toArray(new String[0]);
                    break;

                case "--multi-profile":
                    numProfiles = MANY;
                    break;

                case "--no-profile":
                    numProfiles = ZERO;
                    break;

                case "--no-keepalive":
                    keepAlive = false;
                    break;

                case "--numeric":
                    numeric = true;
                    break;

                case "--port":
                    i++;
                    String portArg = params[i];
                    try {
                        port = parseInt(portArg);
                    } catch (NumberFormatException ex) {
                        throw new InvalidParam("invalid argument for --port: " + portArg);
                    }
                    if (port < 1 || port > 65535)
                        throw new InvalidParam("invalid port");
                    break;

                default:
                    if (!connectionHelper.isExtended()) throw new InvalidParam("unknown parameter: " + option);
                }
            } catch (IndexOutOfBoundsException e) {
                throw (InvalidParam)new InvalidParam("argument expected for " + option).initCause(e);
            }
        }

        if (hosts == null) {
            hosts = new String[] {Net.getCanonicalHostname(numeric)};
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

        // this constructor modifies the provided ListenerMap
        return new Acceptor_impl(bind, hosts, numProfiles, port, backlog, keepAlive, connectionHelper, listenMap_, params, codec);
    }

    public void change_key(IORHolder ior, byte[] key) {
        // Extract the IIOP profile information from the provided IOR
        for (int profile = 0; profile < ior.value.profiles.length; profile++) {
            if (ior.value.profiles[profile].tag == TAG_INTERNET_IOP.value) {
                // Extract the 1_0 profile body
                InputStream in = new InputStream(ior.value.profiles[profile].profile_data);
                in._OB_readEndian();
                ProfileBody_1_0 body = ProfileBody_1_0Helper.read(in);

                // Read components if the IIOP version is > 1.0
                TaggedComponent[] components;
                if (body.iiop_version.major > 1 || body.iiop_version.minor > 0) {
                    int len = in.read_ulong();
                    components = new TaggedComponent[len];
                    for (int j = 0; j < len; j++)
                        components[j] = TaggedComponentHelper
                                .read(in);
                } else
                    components = new TaggedComponent[0];

                // Fill in the new object-key
                body.object_key = key;

                // Re-marshal the new body
                try (OutputStream out = new OutputStream()) {
                    out._OB_writeEndian();
                    ProfileBody_1_0Helper.write(out, body);

                    // Re-marshal the components if the IIOP version is > 1.0
                    if (body.iiop_version.major > 1 || body.iiop_version.minor > 0) {
                        out.write_ulong(components.length);
                        for (TaggedComponent component : components) {
                            TaggedComponentHelper.write(out, component);
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

    public AccFactory_impl(ORB orb, ListenerMap lm, UnifiedConnectionHelper helper) {
        connectionHelper = requireNonNull(helper);
        orb_ = orb;
        info_ = new AccFactoryInfo_impl();
        listenMap_ = lm;
    }

}

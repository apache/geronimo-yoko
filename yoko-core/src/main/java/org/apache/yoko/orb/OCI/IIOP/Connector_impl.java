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

import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.util.MinorCodes;
import org.apache.yoko.orb.OB.Net;
import org.apache.yoko.orb.OB.PROTOCOL_POLICY_ID;
import org.apache.yoko.orb.OB.ProtocolPolicy;
import org.apache.yoko.orb.OB.ProtocolPolicyHelper;
import org.apache.yoko.orb.OCI.ConnectCB;
import org.apache.yoko.orb.OCI.Connector;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.ProfileInfoHolder;
import org.apache.yoko.orb.OCI.ProfileInfoSeqHolder;
import org.apache.yoko.orb.OCI.Transport;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Policy;
import org.omg.CSIIOP.TAG_CSI_SEC_MECH_LIST;
import org.omg.IOP.Codec;
import org.omg.IOP.IOR;
import org.omg.IOP.TaggedComponent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static org.apache.yoko.orb.OCI.IIOP.Exceptions.asCommFailure;
import static org.apache.yoko.orb.OCI.IIOP.Util.extractProfileInfo;
import static org.apache.yoko.logging.VerboseLogging.CONN_LOG;
import static org.apache.yoko.logging.VerboseLogging.CONN_OUT_LOG;
import static org.apache.yoko.logging.VerboseLogging.logged;
import static org.apache.yoko.logging.VerboseLogging.wrapped;
import static org.apache.yoko.util.HexConverter.octetsToAscii;

final class Connector_impl extends org.omg.CORBA.LocalObject implements Connector {
    static final Logger logger = Logger.getLogger(Connector_impl.class.getName());

    private final IOR ior_;    // the target IOR we're connecting with

    private final Policy[] policies_;    // the policies used for the connection.

    private boolean keepAlive_; // The keepalive flag

    private final ConnectorInfo_impl info_; // Connector information

    private Socket socket_; // The socket

    private ListenerMap listenMap_;

    private final ConnectionHelper connectionHelper_;

    private byte[] transportInfo;

    private final ExtendedConnectionHelper extendedConnectionHelper_;

    private final Codec codec_;


    // ------------------------------------------------------------------
    // Private and protected functions
    // ------------------------------------------------------------------

    private void close() {
        logger.fine("Closing connection to host=" + this.info_.getHost() + ", port=" + this.info_.getPort());

        //
        // Close the socket
        //
        try {
            socket_.close();
            socket_ = null;
        } catch (IOException ex) {
            //ignore
        }
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return org.omg.IOP.TAG_INTERNET_IOP.value;
    }

    public Transport connect() {
        if (socket_ != null)
            close();

        final String targetDesc = ("host=" + info_.getHost() + ", port=" + info_.getPort());
        try {
            if (logger.isLoggable(FINE)) logger.fine("Connecting to " + targetDesc);
            if (connectionHelper_ != null) {
                InetAddress address = Util.getInetAddress(info_.getHost());
                socket_ = connectionHelper_.createSocket(ior_, policies_, address, info_.getPort());
            } else {
                socket_ = extendedConnectionHelper_.createSocket(info_.getHost(), info_.getPort());
            }
            if (logger.isLoggable(FINE)) logger.fine("Connection created with socket " + socket_);
        } catch (java.net.ConnectException ex) {
            throw wrapped(CONN_LOG, ex, "Error connecting to " + targetDesc, Transients.CONNECT_FAILED);
        } catch (IOException ex) {
            throw logged(CONN_LOG, (COMM_FAILURE) new COMM_FAILURE(
                    MinorCodes.describeCommFailure(MinorCodes.MinorSocket),
                    MinorCodes.MinorSocket,
                    CompletionStatus.COMPLETED_NO).initCause(ex), "Error connecting to " + targetDesc);
        }

        //
        // Set TCP_NODELAY and SO_KEEPALIVE options
        //
        try {
            socket_.setTcpNoDelay(true);

            if (keepAlive_)
                socket_.setKeepAlive(true);
        } catch (java.net.SocketException ex) {
            logger.log(FINE, "Socket setup error", ex);
            try {
                socket_.close();
            } catch (IOException e) {
            }
            throw Exceptions.asCommFailure(ex);
        }

        //
        // Create new transport
        //
        Transport tr = null;
        try {
            tr = new Transport_impl(socket_, listenMap_);
            socket_ = null;
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(FINE, "Transport creation error", ex);
            try {
                socket_.close();
            } catch (IOException e) {
            }
            throw ex;
        }

        //
        // Call callbacks
        //
        org.apache.yoko.orb.OCI.TransportInfo trInfo = tr.get_info();
        try {
            info_._OB_callConnectCB(trInfo);
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(FINE, "Connection callback error", ex);
            tr.close();
            throw ex;
        }

        //
        // Return new transport
        //
        return tr;
    }

    //
    // Helper class for connect_timeout()
    //
    private class ConnectTimeout extends Thread {
        private InetAddress address_ = null;

        private Socket so_ = null;

        private IOException ex_ = null;

        private boolean finished_ = false;

        private boolean timeout_ = false;

        ConnectTimeout(InetAddress address) {
            address_ = address;
        }

        public void run() {
            try {
                if (connectionHelper_ != null) {
                    so_ = connectionHelper_.createSocket(ior_, policies_, address_, info_.getPort());
                } else {
                    so_ = extendedConnectionHelper_.createSocket(info_.getHost(), info_.getPort());
                }
            } catch (IOException ex) {
                logger.log(FINE, "Socket creation error", ex);
                ex_ = ex;
            }

            synchronized (this) {
                if (timeout_) {
                    if (so_ != null) {
                        try {
                            so_.close();
                        } catch (IOException ex) {
                        }

                        so_ = null;
                    }
                } else {
                    finished_ = true;
                    ConnectTimeout.this.notify();
                }
            }
        }

        synchronized Socket waitForConnect(int t)
                throws IOException {
            while (!finished_) {
                try {
                    ConnectTimeout.this.wait(t);
                } catch (InterruptedException ex) {
                    continue;
                }

                if (!finished_) // Timeout
                {
                    timeout_ = true;
                    return null;
                }
            }

            if (so_ != null) // Connect succeeded
                return so_;

            if (ex_ != null) // Connect failed
                throw ex_;

            throw new InternalError();
        }
    }

    public Transport connect_timeout(int t) {
        if (socket_ != null)
            close();

        //
        // Get the address
        //
        InetAddress address = null;
        try {
            address = Util.getInetAddress(this.info_.getHost());
        } catch (UnknownHostException ex) {
            logger.log(FINE, "Host resolution error", ex);
            throw asCommFailure(ex);
        }

        //
        // Create socket and connect
        //
        try {
            ConnectTimeout connectTimeout = new ConnectTimeout(address);
            connectTimeout.start();

            socket_ = connectTimeout.waitForConnect(t);

            if (socket_ == null)
                return null;
        } catch (java.net.ConnectException ex) {
            throw wrapped(CONN_OUT_LOG, ex, "Socket connection error", Transients.CONNECT_FAILED);
        } catch (IOException ex) {
            logger.log(FINE, "Socket I/O error", ex);
            throw (COMM_FAILURE)new COMM_FAILURE(
                    MinorCodes.describeCommFailure(MinorCodes.MinorSocket) + ": " + ex.getMessage(),
                    MinorCodes.MinorSocket,
                    CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Set TCP_NODELAY and SO_KEEPALIVE options
        //
        try {
            socket_.setTcpNoDelay(true);

            if (keepAlive_)
                socket_.setKeepAlive(true);
        } catch (java.net.SocketException ex) {
            logger.log(FINE, "Socket setup error", ex);
            try {
                socket_.close();
            } catch (IOException e) {
            }
            throw Exceptions.asCommFailure(ex);
        }

        //
        // Create new transport
        //
        Transport tr = null;
        try {
            tr = new Transport_impl(socket_, listenMap_);
            socket_ = null;
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(FINE, "Transport setup error", ex);
            try {
                socket_.close();
            } catch (IOException e) {
            }
            throw ex;
        }

        //
        // Call callbacks
        //
        org.apache.yoko.orb.OCI.TransportInfo trInfo = tr.get_info();
        try {
            info_._OB_callConnectCB(trInfo);
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(FINE, "Callback setup error", ex);
            tr.close();
            throw ex;
        }

        //
        // Return new transport
        //
        return tr;
    }

    public ProfileInfo[] get_usable_profiles(IOR ior, Policy[] policies) {
        //
        // Make sure that the set of policies is met
        //
        for (Policy policy : policies) {
            if (policy.policy_type() == PROTOCOL_POLICY_ID.value) {
                ProtocolPolicy protocolPolicy = ProtocolPolicyHelper.narrow(policy);
                if (!protocolPolicy.contains(PLUGIN_ID.value)) {
                    if (logger.isLoggable(FINE)) logger.fine("Protocol policy exists but does not allow expected transport. policy = " + Arrays.toString(protocolPolicy.value()) + "\t expected transport = " + PLUGIN_ID.value);
                    return new ProfileInfo[0];
                }
            }
        }

        ProfileInfoSeqHolder profileInfoSeq = new ProfileInfoSeqHolder();
        profileInfoSeq.value = new ProfileInfo[0];
        String host = info_.getHost();
        if (Util.isEncodedHost(host)) host = Util.decodeHost(host);
        Util.extractAllProfileInfos(ior, profileInfoSeq, true, host, info_.getPort(), false, codec_);

        //check that the transport info matches ours.
        //we could return just the profiles that match rather than bailing if one doesn't match.
        for (ProfileInfo profileInfo : profileInfoSeq.value) {
            byte[] otherTransportInfo = new byte[0];
            for (TaggedComponent component : profileInfo.components) {
                if (component.tag == TAG_CSI_SEC_MECH_LIST.value) {
                    otherTransportInfo = component.component_data;
                    if (logger.isLoggable(FINE))
                        logger.fine("Found CSI_SEC_MECH_LIST: " + octetsToAscii(otherTransportInfo));
                    break;
                }
            }
            if (!Arrays.equals(transportInfo, otherTransportInfo)) {
                if (logger.isLoggable(FINE))
                    logger.fine("Transport info does not match CSI_SEC_MECH_LIST: " + octetsToAscii(otherTransportInfo));
                return new ProfileInfo[0];
            }
        }
        return profileInfoSeq.value;
    }

    public boolean equal(org.apache.yoko.orb.OCI.Connector con) {
        return (con instanceof Connector_impl) && equal0((Connector_impl)con);
    }

    private boolean equal0(Connector_impl that) {
        if (this.info_.getPort() != that.info_.getPort()) return false;
        return (Net.CompareHosts(this.info_.getHost(), that.info_.getHost()) ?
                Arrays.equals(transportInfo, that.transportInfo) : false);
    }

    private byte[] extractTransportInfo(IOR ior) {
        ProfileInfoHolder holder = new ProfileInfoHolder();
        // we need to extract the profile information from the IOR to see if this connection has
        // any transport-level security defined.
        if (extractProfileInfo(ior, holder)) {
            ProfileInfo profileInfo = holder.value;
            for (TaggedComponent component : profileInfo.components) {
                // we're looking for the security mechanism items.
                if (component.tag == TAG_CSI_SEC_MECH_LIST.value) {
                    return component.component_data;
                }
            }
        }
        return new byte[0];
    }

    public org.apache.yoko.orb.OCI.ConnectorInfo get_info() {
        return info_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    private Connector_impl(IOR ior, Policy[] policies, String host, int port, boolean keepAlive, ConnectCB[] cb, ListenerMap lm, ConnectionHelper helper, ExtendedConnectionHelper xhelper, Codec codec) {
        if ((null == helper) && (null == xhelper)) throw new IllegalArgumentException("Both connection helpers must not be null");
        ior_ = ior;
        policies_ = policies;
        keepAlive_ = keepAlive;
        info_ = new ConnectorInfo_impl(host, port, cb);
        listenMap_ = lm;
        connectionHelper_ = helper;
        extendedConnectionHelper_ = xhelper;
        codec_ = codec;
        transportInfo = extractTransportInfo(ior);
    }

    Connector_impl(IOR ior, Policy[] policies, String host, int port, boolean keepAlive, ConnectCB[] cb, ListenerMap lm, ConnectionHelper helper, Codec codec) {
        this(ior, policies, host, port, keepAlive, cb, lm, helper, null, codec);
    }

    Connector_impl(IOR ior, Policy[] policies, String host, int port, boolean keepAlive, ConnectCB[] cb, ListenerMap lm, ExtendedConnectionHelper xhelper, Codec codec) {
        this(ior, policies, host, port, keepAlive, cb, lm, null, xhelper, codec);
    }

    public void finalize() throws Throwable {
        if (socket_ != null)
            close();

        super.finalize();
    }

    @Override
    public String toString() {
        return "-> " + info_;
    }
}

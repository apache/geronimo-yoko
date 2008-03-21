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

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.ProfileInfoHolder;
import org.omg.CORBA.Policy;
import org.omg.CSIIOP.TAG_CSI_SEC_MECH_LIST;
import org.omg.IOP.IOR;
import org.omg.IOP.TaggedComponent;

final class Connector_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OCI.Connector {

    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(org.apache.yoko.orb.OCI.Connector.class.getName());

    private final IOR ior_;    // the target IOR we're connecting with

    private final Policy[] policies_;    // the policies used for the connection.

    // Some data member must not be private because the info object
    // must be able to access them
    public String host_; // The host

    public int port_; // The port

    private boolean keepAlive_; // The keepalive flag

    private ConnectorInfo_impl info_; // Connector information

    private java.net.Socket socket_; // The socket

    private ListenerMap listenMap_;

    private ConnectionHelper connectionHelper_;

    private byte[] transportInfo;


    // ------------------------------------------------------------------
    // Private and protected functions
    // ------------------------------------------------------------------

    private void close() {
        logger.fine("Closing connection to host=" + host_ + ", port=" + port_);
        //
        // Destroy the info object
        //
        info_._OB_destroy();

        //
        // Close the socket
        //
        try {
            socket_.close();
            socket_ = null;
        } catch (java.io.IOException ex) {
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

    public org.apache.yoko.orb.OCI.Transport connect() {
        if (socket_ != null)
            close();

        //
        // Get the address
        //
        java.net.InetAddress address;
        try {
            address = java.net.InetAddress.getByName(host_);
        } catch (java.net.UnknownHostException ex) {
            logger.log(Level.FINE, "Host resolution error", ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }


        //
        // Create socket and connect
        //
        try {
            logger.fine("Connecting to host=" + address + ", port=" + port_);
            socket_ = connectionHelper_.createSocket(ior_, policies_, address, port_);
            logger.fine("Connection created with socket " + socket_); 
        } catch (java.net.ConnectException ex) {
            logger.log(Level.FINE, "Error connecting to host=" + address + ", port=" + port_, ex);
            throw new org.omg.CORBA.TRANSIENT(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Error connecting to host=" + address + ", port=" + port_, ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSocket)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSocket,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Set TCP_NODELAY and SO_KEEPALIVE options
        //
        try {
            socket_.setTcpNoDelay(true);

            if (keepAlive_)
                socket_.setKeepAlive(true);
        } catch (java.net.SocketException ex) {
            logger.log(Level.FINE, "Socket setup error", ex);
            try {
                socket_.close();
            } catch (java.io.IOException e) {
            }
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSetsockopt)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSetsockopt,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Create new transport
        //
        org.apache.yoko.orb.OCI.Transport tr = null;
        try {
            tr = new Transport_impl(this, socket_, listenMap_);
            socket_ = null;
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(Level.FINE, "Transport creation error", ex);
            try {
                socket_.close();
            } catch (java.io.IOException e) {
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
            logger.log(Level.FINE, "Connection callback error", ex);
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
        private java.net.InetAddress address_ = null;

        private java.net.Socket so_ = null;

        private java.io.IOException ex_ = null;

        private boolean finished_ = false;

        private boolean timeout_ = false;

        ConnectTimeout(java.net.InetAddress address) {
            address_ = address;
        }

        public void run() {
            try {
                so_ = connectionHelper_.createSocket(ior_, policies_, address_, port_);
            } catch (java.io.IOException ex) {
                logger.log(Level.FINE, "Socket creation error", ex);
                ex_ = ex;
            }

            synchronized (this) {
                if (timeout_) {
                    if (so_ != null) {
                        try {
                            so_.close();
                        } catch (java.io.IOException ex) {
                        }

                        so_ = null;
                    }
                } else {
                    finished_ = true;
                    ConnectTimeout.this.notify();
                }
            }
        }

        synchronized java.net.Socket waitForConnect(int t)
                throws java.io.IOException {
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

    public org.apache.yoko.orb.OCI.Transport connect_timeout(int t) {
        if (socket_ != null)
            close();

        //
        // Get the address
        //
        java.net.InetAddress address = null;
        try {
            address = java.net.InetAddress.getByName(host_);
        } catch (java.net.UnknownHostException ex) {
            logger.log(Level.FINE, "Host resolution error", ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
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
            logger.log(Level.FINE, "Socket connection error", ex);
            throw new org.omg.CORBA.TRANSIENT(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Socket I/O error", ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSocket)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSocket,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Set TCP_NODELAY and SO_KEEPALIVE options
        //
        try {
            socket_.setTcpNoDelay(true);

            if (keepAlive_)
                socket_.setKeepAlive(true);
        } catch (java.net.SocketException ex) {
            logger.log(Level.FINE, "Socket setup error", ex);
            try {
                socket_.close();
            } catch (java.io.IOException e) {
            }
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSetsockopt)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSetsockopt,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Create new transport
        //
        org.apache.yoko.orb.OCI.Transport tr = null;
        try {
            tr = new Transport_impl(this, socket_, listenMap_);
            socket_ = null;
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(Level.FINE, "Transport setup error", ex);
            try {
                socket_.close();
            } catch (java.io.IOException e) {
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
            logger.log(Level.FINE, "Callback setup error", ex);
            tr.close();
            throw ex;
        }

        //
        // Return new transport
        //
        return tr;
    }

    public org.apache.yoko.orb.OCI.ProfileInfo[] get_usable_profiles(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies) {
        //
        // Make sure that the set of policies is met
        //
        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.apache.yoko.orb.OB.PROTOCOL_POLICY_ID.value) {
                org.apache.yoko.orb.OB.ProtocolPolicy protocolPolicy = org.apache.yoko.orb.OB.ProtocolPolicyHelper
                        .narrow(policies[i]);
                if (!protocolPolicy.contains(PLUGIN_ID.value))
                    return new org.apache.yoko.orb.OCI.ProfileInfo[0];
            }
        }

        org.apache.yoko.orb.OCI.ProfileInfoSeqHolder profileInfoSeq = new org.apache.yoko.orb.OCI.ProfileInfoSeqHolder();
        profileInfoSeq.value = new org.apache.yoko.orb.OCI.ProfileInfo[0];
        Util.extractAllProfileInfos(ior, profileInfoSeq, true, host_, port_,
                false);

        //check that the transport info matches ours.
        //we could return just the profiles that match rather than bailing if one doesn't match.
        for (int i = 0; i< profileInfoSeq.value.length; i++ ) {
            TaggedComponent[] components = profileInfoSeq.value[i].components;
            byte[] otherTransportInfo = new byte[0];
            for (int j = 0; j < components.length; j++ ) {
                TaggedComponent component = components[j];
                if (component.tag == TAG_CSI_SEC_MECH_LIST.value) {
                    otherTransportInfo = component.component_data;
                    break;
                }
            }
            if (!Arrays.equals(transportInfo, otherTransportInfo)) {
                return new org.apache.yoko.orb.OCI.ProfileInfo[0];
            }
        }
        return profileInfoSeq.value;
    }

    public boolean equal(org.apache.yoko.orb.OCI.Connector con) {
        Connector_impl impl = null;
        try {
            impl = (Connector_impl) con;
        } catch (ClassCastException ex) {
            return false;
        }

        //
        // Compare ports
        //
        if (port_ != impl.port_)
            return false;

        //
        // Direct host name comparison
        //
        if (!host_.equals(impl.host_)) {
            //
            // Direct host name comparision failed - must look up
            // addresses to be really sure if the hosts differ
            //
            try {
                java.net.InetAddress addr1 = java.net.InetAddress
                        .getByName(host_);

                java.net.InetAddress addr2 = java.net.InetAddress
                        .getByName(impl.host_);

                if (!addr1.equals(addr2))
                    return false;
            } catch (java.net.UnknownHostException ex) {
                //
                // Return false on hostname lookup failure
                //
                return false;
            }
        }

        return Arrays.equals(transportInfo, impl.transportInfo);

        //
        // OK, connectors are the same
        //
//        return true;
    }

    byte[] extractTransportInfo(IOR ior) {
        ProfileInfoHolder holder = new ProfileInfoHolder();
        // we need to extract the profile information from the IOR to see if this connection has
        // any transport-level security defined.
        if (org.apache.yoko.orb.OCI.IIOP.Util.extractProfileInfo(ior, holder)) {
            ProfileInfo profileInfo = holder.value;
            for (int i = 0; i < profileInfo.components.length; i++) {
                // we're lookoing for the security mechanism items.
                if (profileInfo.components[i].tag == TAG_CSI_SEC_MECH_LIST.value) {
                    return profileInfo.components[i].component_data;
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

    public Connector_impl(org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies, String host, int port, boolean keepAlive,
            org.apache.yoko.orb.OCI.ConnectCB[] cb, ListenerMap lm, ConnectionHelper helper) {
        // System.out.println("Connector_impl");
        ior_ = ior;
        policies_ = policies;
        host_ = host;
        port_ = port;
        keepAlive_ = keepAlive;
        info_ = new ConnectorInfo_impl(this, cb);
        listenMap_ = lm;
        connectionHelper_ = helper;
        transportInfo = extractTransportInfo(ior);
    }

    public void finalize() throws Throwable {
        // System.out.println("~Connector_impl");
        if (socket_ != null)
            close();

        super.finalize();
    }
}

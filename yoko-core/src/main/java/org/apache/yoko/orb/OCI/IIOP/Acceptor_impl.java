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

import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.IIOP.PLUGIN_ID;

final class Acceptor_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OCI.Acceptor {
    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(org.apache.yoko.orb.OCI.Acceptor.class.getName());

    // Some data members must not be private because the info object
    // must be able to access them
    public String[] hosts_; // The hosts

    public java.net.ServerSocket socket_; // The socket

    private boolean multiProfile_; // Use multiple profiles?

    private int port_; // The port

    private boolean keepAlive_; // The keepalive flag

    private java.net.InetAddress localAddress_; // The local address

    private AcceptorInfo_impl info_; // Acceptor information

    private ListenerMap listenMap_;

    private ConnectionHelper connectionHelper_;    // plugin for managing connection config/creation

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return org.omg.IOP.TAG_INTERNET_IOP.value;
    }

    public int handle() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void close() {
        logger.fine("Closing connection to host=" + localAddress_ + ", port=" + port_);
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
        }
    }

    public void shutdown() {
        //
        // This operation does nothing in the java implementation
        //
    }

    public void listen() {
        //
        // This operation does nothing in the java implementation
        //
    }

    public org.apache.yoko.orb.OCI.Transport accept(boolean block) {
        //
        // Accept
        //
        java.net.Socket socket;
        try {
            //
            // If non-blocking, use a timeout of 1ms
            //
            if (!block)
                socket_.setSoTimeout(1);
            else
                socket_.setSoTimeout(0);

            logger.fine("Accepting connection for host=" + localAddress_ + ", port=" + port_);
            socket = socket_.accept();
            logger.fine("Received inbound connection on socket " + socket);
        } catch (java.io.InterruptedIOException ex) {
            if (!block)
                return null; // Timeout
            else {
                logger.log(Level.FINE, "Failure accepting connection for host=" + localAddress_ + ", port=" + port_, ex);

                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorAccept)
                                + ": " + ex.getMessage(),
                        org.apache.yoko.orb.OB.MinorCodes.MinorAccept,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
            }
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Failure accepting connection for host=" + localAddress_ + ", port=" + port_, ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorAccept)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorAccept,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Set TCP_NODELAY and SO_KEEPALIVE options
        //
        try {
            socket.setTcpNoDelay(true);
            if (keepAlive_)
                socket.setKeepAlive(true);
        } catch (java.net.SocketException ex) {
            logger.log(Level.FINE, "Failure configuring server connection for host=" + localAddress_ + ", port=" + port_, ex);
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
            tr = new Transport_impl(this, socket, listenMap_);
            logger.fine("Inbound connection received from " + socket.getInetAddress()); 
        } catch (org.omg.CORBA.SystemException ex) {
            try {
                socket.close();
            } catch (java.io.IOException e) {
            }
            logger.log(Level.FINE, "error creating inbound connection", ex); 
            throw ex;
        }

        //
        // Call callbacks
        //
        org.apache.yoko.orb.OCI.TransportInfo trInfo = tr.get_info();
        try {
            info_._OB_callAcceptCB(trInfo);
        } catch (org.omg.CORBA.SystemException ex) {
            tr.close();
            logger.log(Level.FINE, "error calling connection callbacks", ex); 
            throw ex;
        }

        //
        // Return new transport
        //
        return tr;
    }

    public org.apache.yoko.orb.OCI.Transport connect_self() {
        //
        // Create socket and connect to local address
        //
        java.net.Socket socket = null;
        try {
            socket = connectionHelper_.createSelfConnection(localAddress_, port_);
        } catch (java.net.ConnectException ex) {
            logger.log(Level.FINE, "Failure making self connection for host=" + localAddress_ + ", port=" + port_, ex);
            throw new org.omg.CORBA.TRANSIENT(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeTransient(org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Failure making self connection for host=" + localAddress_ + ", port=" + port_, ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSocket)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSocket,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Set TCP_NODELAY option
        //
        try {
            socket.setTcpNoDelay(true);
        } catch (java.net.SocketException ex) {
            logger.log(Level.FINE, "Failure configuring self connection for host=" + localAddress_ + ", port=" + port_, ex);
            try {
                socket.close();
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
        // Create and return new transport
        //
        org.apache.yoko.orb.OCI.Transport tr = null;
        try {
            tr = new Transport_impl(this, socket, listenMap_);
        } catch (org.omg.CORBA.SystemException ex) {
            try {
                socket.close();
            } catch (java.io.IOException e) {
            }
            throw ex;
        }
        return tr;
    }

    public void add_profiles(org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.apache.yoko.orb.OBPortableServer.POAPolicies policies, 
            org.omg.IOP.IORHolder ior) {
        if (port_ == 0)
            throw new RuntimeException();

        //
        // Filter components according to IIOP version
        //
        java.util.Vector components = new java.util.Vector();
        if (profileInfo.major == 1 && profileInfo.minor == 0) {
            //
            // No components for IIOP 1.0
            //
        } else {
            for (int i = 0; i < profileInfo.components.length; i++)
                components.addElement(profileInfo.components[i]);
        }

        if (profileInfo.major == 1 && profileInfo.minor == 0) {
            //
            // For IIOP 1.0, we always add one profile for each host,
            // since IIOP 1.0 doesn't support tagged components in a
            // profile
            //
            for (int i = 0; i < hosts_.length; i++) {
                org.omg.IIOP.ProfileBody_1_0 body = new org.omg.IIOP.ProfileBody_1_0();
                body.iiop_version = new org.omg.IIOP.Version(profileInfo.major,
                        profileInfo.minor);
                body.host = hosts_[i];
                // the CSIv2 policy may require zeroing the port in the IOR. 
                if (policies.zeroPortPolicy()) {
                    body.port = 0; 
                }
                else {
                    if (port_ >= 0x8000)
                        body.port = (short) (port_ - 0xffff - 1);
                    else
                        body.port = (short) port_;
                }
                body.object_key = profileInfo.key;

                int len = ior.value.profiles.length + 1;
                org.omg.IOP.TaggedProfile[] profiles = new org.omg.IOP.TaggedProfile[len];
                System.arraycopy(ior.value.profiles, 0, profiles, 0,
                        ior.value.profiles.length);
                ior.value.profiles = profiles;
                ior.value.profiles[len - 1] = new org.omg.IOP.TaggedProfile();
                ior.value.profiles[len - 1].tag = org.omg.IOP.TAG_INTERNET_IOP.value;
                org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
                OutputStream out = new OutputStream(buf);
                out._OB_writeEndian();
                org.omg.IIOP.ProfileBody_1_0Helper.write(out, body);
                ior.value.profiles[len - 1].profile_data = new byte[buf
                        .length()];
                System.arraycopy(buf.data(), 0,
                        ior.value.profiles[len - 1].profile_data, 0, buf
                                .length());
            }
        } else {
            if (multiProfile_) {
                //
                // Add one profile for each host
                //

                for (int i = 0; i < hosts_.length; i++) {
                    org.omg.IIOP.ProfileBody_1_1 body = new org.omg.IIOP.ProfileBody_1_1();
                    body.iiop_version = new org.omg.IIOP.Version(
                            profileInfo.major, profileInfo.minor);
                    body.host = hosts_[i];
                    // the CSIv2 policy may require zeroing the port in the IOR. 
                    if (policies.zeroPortPolicy()) {
                        body.port = 0; 
                    }
                    else {
                        if (port_ >= 0x8000)
                            body.port = (short) (port_ - 0xffff - 1);
                        else
                            body.port = (short) port_;
                    }
                    body.object_key = profileInfo.key;
                    body.components = new org.omg.IOP.TaggedComponent[components
                            .size()];
                    components.copyInto(body.components);

                    int len = ior.value.profiles.length + 1;
                    org.omg.IOP.TaggedProfile[] profiles = new org.omg.IOP.TaggedProfile[len];
                    System.arraycopy(ior.value.profiles, 0, profiles, 0,
                            ior.value.profiles.length);
                    ior.value.profiles = profiles;
                    ior.value.profiles[len - 1] = new org.omg.IOP.TaggedProfile();
                    ior.value.profiles[len - 1].tag = org.omg.IOP.TAG_INTERNET_IOP.value;
                    org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
                    OutputStream out = new OutputStream(buf);
                    out._OB_writeEndian();
                    org.omg.IIOP.ProfileBody_1_1Helper.write(out, body);
                    ior.value.profiles[len - 1].profile_data = new byte[buf
                            .length()];
                    System.arraycopy(buf.data(), 0,
                            ior.value.profiles[len - 1].profile_data, 0, buf
                                    .length());
                }
            } else {
                //
                // Add a single tagged profile. If there are additional
                // hosts, add a tagged component for each host.
                //

                org.omg.IIOP.ProfileBody_1_1 body = new org.omg.IIOP.ProfileBody_1_1();
                body.iiop_version = new org.omg.IIOP.Version(profileInfo.major,
                        profileInfo.minor);
                body.host = hosts_[0];
                if (policies.zeroPortPolicy()) {
                    body.port = 0; 
                }
                else {
                    if (port_ >= 0x8000)
                        body.port = (short) (port_ - 0xffff - 1);
                    else
                        body.port = (short) port_;
                }
                body.object_key = profileInfo.key;

                for (int i = 1; i < hosts_.length; i++) {
                    org.omg.IOP.TaggedComponent c = new org.omg.IOP.TaggedComponent();
                    c.tag = org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS.value;
                    org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
                    OutputStream out = new OutputStream(buf);
                    out._OB_writeEndian();
                    out.write_string(hosts_[i]);
                    out.write_ushort(body.port);
                    c.component_data = new byte[buf.length()];
                    System.arraycopy(buf.data(), 0, c.component_data, 0, buf
                            .length());
                    components.addElement(c);
                }
                body.components = new org.omg.IOP.TaggedComponent[components
                        .size()];
                components.copyInto(body.components);

                int len = ior.value.profiles.length + 1;
                org.omg.IOP.TaggedProfile[] profiles = new org.omg.IOP.TaggedProfile[len];
                System.arraycopy(ior.value.profiles, 0, profiles, 0,
                        ior.value.profiles.length);
                ior.value.profiles = profiles;
                ior.value.profiles[len - 1] = new org.omg.IOP.TaggedProfile();
                ior.value.profiles[len - 1].tag = org.omg.IOP.TAG_INTERNET_IOP.value;
                org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
                OutputStream out = new OutputStream(buf);
                out._OB_writeEndian();
                org.omg.IIOP.ProfileBody_1_1Helper.write(out, body);
                ior.value.profiles[len - 1].profile_data = new byte[buf
                        .length()];
                System.arraycopy(buf.data(), 0,
                        ior.value.profiles[len - 1].profile_data, 0, buf
                                .length());
            }
        }
    }

    public org.apache.yoko.orb.OCI.ProfileInfo[] get_local_profiles(
            org.omg.IOP.IOR ior) {
        //
        // Get local profiles for all hosts
        //
        org.apache.yoko.orb.OCI.ProfileInfoSeqHolder profileInfoSeq = new org.apache.yoko.orb.OCI.ProfileInfoSeqHolder();
        profileInfoSeq.value = new org.apache.yoko.orb.OCI.ProfileInfo[0];

        for (int i = 0; i < hosts_.length; i++) {
            Util.extractAllProfileInfos(ior, profileInfoSeq, true, hosts_[i],
                    port_, true);
        }

        return profileInfoSeq.value;
    }

    public org.apache.yoko.orb.OCI.AcceptorInfo get_info() {
        return info_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Acceptor_impl(String[] hosts, boolean multiProfile, int port,
            int backlog, boolean keepAlive, ConnectionHelper helper, ListenerMap lm) {
        hosts_ = hosts;
        multiProfile_ = multiProfile;
        keepAlive_ = keepAlive;
        connectionHelper_ = helper;
        info_ = new AcceptorInfo_impl(this);
        listenMap_ = lm;
        
        if (backlog == 0)
            backlog = 50; // 50 is the JDK's default value

        //
        // Get the local address for use by connect_self. Since we are
        // binding to all network interfaces, we'll use the loopback
        // address.
        //
        try {
            localAddress_ = java.net.InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException ex) {
            logger.log(Level.FINE, "Host resolution failure", ex); 
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Create socket and bind to all network interfaces
        //
        try {
            socket_ = connectionHelper_.createServerSocket(port, backlog);

            //
            // Read back the port. This is needed if the port was selected by
            // the operating system.
            //
            port_ = socket_.getLocalPort();
            logger.fine("Acceptor created using socket " + socket_); 
        } catch (java.net.BindException ex) {
            logger.log(Level.FINE, "Failure creating server socket for host=" + localAddress_ + ", port=" + port, ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorBind)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorBind,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Failure creating server socket for host=" + localAddress_ + ", port=" + port, ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSocket)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSocket,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // add these endpoints to the listenMap_
        //
        synchronized (listenMap_) {
            for (int i = 0; i < hosts_.length; i++)
                listenMap_.add(hosts_[i], (short) port_);
        }
    }

    public Acceptor_impl(String address, String[] hosts, boolean multiProfile,
            int port, int backlog, boolean keepAlive, ConnectionHelper helper, ListenerMap lm) {
        // System.out.println("Acceptor_impl");
        hosts_ = hosts;
        multiProfile_ = multiProfile;
        keepAlive_ = keepAlive;
        connectionHelper_ = helper;
        info_ = new AcceptorInfo_impl(this);
        listenMap_ = lm;

        if (backlog == 0)
            backlog = 50; // 50 is the JDK's default value

        //
        // Get the local address for use by connect_self
        //
        try {
            localAddress_ = java.net.InetAddress.getByName(address);
        } catch (java.net.UnknownHostException ex) {
            logger.log(Level.FINE, "Host resolution failure", ex); 
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Create socket and bind to requested network interface
        //
        try {
            socket_ = connectionHelper_.createServerSocket(port, backlog, localAddress_);

            //
            // Read back the port. This is needed if the port was selected by
            // the operating system.
            //
            port_ = socket_.getLocalPort();
            logger.fine("Acceptor created using socket " + socket_); 
        } catch (java.net.BindException ex) {
            logger.log(Level.FINE, "Failure creating server socket for host=" + localAddress_ + ", port=" + port, ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorBind)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorBind,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Failure creating server socket for host=" + localAddress_ + ", port=" + port, ex);
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSocket)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSocket,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Add this entry to the listenMap_ as an endpoint to remap
        //
        synchronized (listenMap_) {
            for (int i = 0; i < hosts_.length; i++)
                listenMap_.add(hosts_[i], (short) port_);
        }
    }

    public void finalize() throws Throwable {
        // System.out.println("~Acceptor_impl");
        if (socket_ != null) {
            close();
        }

        //
        // remove this acceptor from the listenMap_
        //
        synchronized (listenMap_) {
            for (int i = 0; i < hosts_.length; i++)
                listenMap_.remove(hosts_[i], (short) port_);
        }

        super.finalize();
    }
    
    public String toString() {
        return "Acceptor listening on " + socket_; 
    }
}

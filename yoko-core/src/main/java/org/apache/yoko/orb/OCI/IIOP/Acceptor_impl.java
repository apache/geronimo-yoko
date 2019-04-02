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

import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.MinorCodes;
import org.apache.yoko.orb.OBPortableServer.POAPolicies;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.Buffer;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.ProfileInfoSeqHolder;
import org.apache.yoko.orb.OCI.Transport;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.SystemException;
import org.omg.IIOP.ProfileBody_1_0;
import org.omg.IIOP.ProfileBody_1_0Helper;
import org.omg.IIOP.ProfileBody_1_1;
import org.omg.IIOP.ProfileBody_1_1Helper;
import org.omg.IIOP.Version;
import org.omg.IOP.Codec;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHolder;
import org.omg.IOP.TAG_ALTERNATE_IIOP_ADDRESS;
import org.omg.IOP.TAG_INTERNET_IOP;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.yoko.orb.OB.MinorCodes.MinorAccept;
import static org.apache.yoko.orb.OB.MinorCodes.MinorConnectFailed;
import static org.apache.yoko.orb.OB.MinorCodes.MinorSetsockopt;
import static org.apache.yoko.orb.OB.MinorCodes.MinorSocket;
import static org.apache.yoko.orb.OCI.IIOP.Exceptions.asCommFailure;
import static org.apache.yoko.orb.OCI.IIOP.Exceptions.asTransient;

final class Acceptor_impl extends LocalObject implements
        Acceptor {
    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(Acceptor.class.getName());

    // Some data members must not be private because the info object
    // must be able to access them
    public String[] hosts_; // The hosts

    public ServerSocket socket_; // The socket

    private boolean multiProfile_; // Use multiple profiles?

    private int port_; // The port

    private boolean keepAlive_; // The keepalive flag

    private InetAddress localAddress_; // The local address

    private final AcceptorInfo_impl info_; // Acceptor information

    private ListenerMap listenMap_;

    private final ConnectionHelper connectionHelper_;    // plugin for managing connection config/creation

    private final ExtendedConnectionHelper extendedConnectionHelper_;
    
    private final Codec codec_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return TAG_INTERNET_IOP.value;
    }

    public int handle() {
        throw new NO_IMPLEMENT();
    }

    public void close() {
        logger.log(Level.FINE, "Closing server socket with host=" + localAddress_ + ", port=" + port_, new Exception("Stack trace"));
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
            logger.log(Level.FINE, "Closed server socket with host=" + localAddress_ + ", port=" + port_);
        } catch (IOException ex) {
            logger.log(Level.FINE, "Exception closing server socket with host=" + localAddress_ + ", port=" + port_, ex);
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

    public Transport accept(boolean block) {
        //
        // Accept
        //
        Socket socket;
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
        } catch (InterruptedIOException ex) {
            if (!block)
                return null; // Timeout
            else {
                logger.log(Level.FINE, "Failure accepting connection for host=" + localAddress_ + ", port=" + port_, ex);
                throw asCommFailure(ex, MinorAccept);
            }
        } catch (IOException ex) {
            logger.log(Level.FINE, "Failure accepting connection for host=" + localAddress_ + ", port=" + port_, ex);
            throw asCommFailure(ex, MinorAccept);
        }

        //
        // Set TCP_NODELAY and SO_KEEPALIVE options
        //
        try {
            socket.setTcpNoDelay(true);
            if (keepAlive_)
                socket.setKeepAlive(true);
        } catch (SocketException ex) {
            logger.log(Level.FINE, "Failure configuring server connection for host=" + localAddress_ + ", port=" + port_, ex);
            throw asCommFailure(ex, MinorSetsockopt);
        }

        //
        // Create new transport
        //
        Transport tr = null;
        try {
            tr = new Transport_impl(this, socket, listenMap_);
            logger.fine("Inbound connection received from " + socket.getInetAddress()); 
        } catch (SystemException ex) {
            try {
                socket.close();
            } catch (IOException e) {
            }
            logger.log(Level.FINE, "error creating inbound connection", ex); 
            throw ex;
        }

        //
        // Return new transport
        //
        return tr;
    }

    public Transport connect_self() {
        //
        // Create socket and connect to local address
        //
        Socket socket = null;
        try {
            if (connectionHelper_ != null) {
                socket = connectionHelper_.createSelfConnection(localAddress_, port_);
            } else {
                socket = extendedConnectionHelper_.createSelfConnection(localAddress_, port_);
            }
        } catch (ConnectException ex) {
            logger.log(Level.FINE, "Failure making self connection for host=" + localAddress_ + ", port=" + port_, ex);
            throw asTransient(ex, MinorConnectFailed);
        } catch (IOException ex) {
            logger.log(Level.FINE, "Failure making self connection for host=" + localAddress_ + ", port=" + port_, ex);
            throw asCommFailure(ex, MinorSocket);
        }

        //
        // Set TCP_NODELAY option
        //
        try {
            socket.setTcpNoDelay(true);
        } catch (SocketException ex) {
            logger.log(Level.FINE, "Failure configuring self connection for host=" + localAddress_ + ", port=" + port_, ex);
            try {
                socket.close();
            } catch (IOException e) {
            }
            throw asCommFailure(ex);
        }

        //
        // Create and return new transport
        //
        try {
            return new Transport_impl(this, socket, listenMap_);
        } catch (SystemException ex) {
            try {
                socket.close();
            } catch (IOException e) {
            }
            throw ex;
        }
    }

    public void add_profiles(ProfileInfo profileInfo,
                             POAPolicies policies,
                             IORHolder ior) {
        if (port_ == 0)
            throw new RuntimeException();

        //
        // Filter components according to IIOP version
        //
        Vector components = new Vector();
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
                ProfileBody_1_0 body = new ProfileBody_1_0();
                body.iiop_version = new Version(profileInfo.major,
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
                TaggedProfile[] profiles = new TaggedProfile[len];
                System.arraycopy(ior.value.profiles, 0, profiles, 0,
                        ior.value.profiles.length);
                ior.value.profiles = profiles;
                ior.value.profiles[len - 1] = new TaggedProfile();
                ior.value.profiles[len - 1].tag = TAG_INTERNET_IOP.value;
                Buffer buf = new Buffer();
                OutputStream out = new OutputStream(buf);
                out._OB_writeEndian();
                ProfileBody_1_0Helper.write(out, body);
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
                    ProfileBody_1_1 body = new ProfileBody_1_1();
                    body.iiop_version = new Version(
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
                    body.components = new TaggedComponent[components
                            .size()];
                    components.copyInto(body.components);

                    int len = ior.value.profiles.length + 1;
                    TaggedProfile[] profiles = new TaggedProfile[len];
                    System.arraycopy(ior.value.profiles, 0, profiles, 0,
                            ior.value.profiles.length);
                    ior.value.profiles = profiles;
                    ior.value.profiles[len - 1] = new TaggedProfile();
                    ior.value.profiles[len - 1].tag = TAG_INTERNET_IOP.value;
                    Buffer buf = new Buffer();
                    OutputStream out = new OutputStream(buf);
                    out._OB_writeEndian();
                    ProfileBody_1_1Helper.write(out, body);
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

                ProfileBody_1_1 body = new ProfileBody_1_1();
                body.iiop_version = new Version(profileInfo.major,
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
                    TaggedComponent c = new TaggedComponent();
                    c.tag = TAG_ALTERNATE_IIOP_ADDRESS.value;
                    Buffer buf = new Buffer();
                    OutputStream out = new OutputStream(buf);
                    out._OB_writeEndian();
                    out.write_string(hosts_[i]);
                    out.write_ushort(body.port);
                    c.component_data = new byte[buf.length()];
                    System.arraycopy(buf.data(), 0, c.component_data, 0, buf
                            .length());
                    components.addElement(c);
                }
                body.components = new TaggedComponent[components
                        .size()];
                components.copyInto(body.components);

                int len = ior.value.profiles.length + 1;
                TaggedProfile[] profiles = new TaggedProfile[len];
                System.arraycopy(ior.value.profiles, 0, profiles, 0,
                        ior.value.profiles.length);
                ior.value.profiles = profiles;
                ior.value.profiles[len - 1] = new TaggedProfile();
                ior.value.profiles[len - 1].tag = TAG_INTERNET_IOP.value;
                Buffer buf = new Buffer();
                OutputStream out = new OutputStream(buf);
                out._OB_writeEndian();
                ProfileBody_1_1Helper.write(out, body);
                ior.value.profiles[len - 1].profile_data = new byte[buf
                        .length()];
                System.arraycopy(buf.data(), 0,
                        ior.value.profiles[len - 1].profile_data, 0, buf
                                .length());
            }
        }
    }

    public ProfileInfo[] get_local_profiles(
            IOR ior) {
        //
        // Get local profiles for all hosts
        //
        ProfileInfoSeqHolder profileInfoSeq = new ProfileInfoSeqHolder();
        profileInfoSeq.value = new ProfileInfo[0];

        for (int i = 0; i < hosts_.length; i++) {
            Util.extractAllProfileInfos(ior, profileInfoSeq, true, hosts_[i],
                    port_, true, codec_);
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

    public Acceptor_impl(String address, String[] hosts, boolean multiProfile,
            int port, int backlog, boolean keepAlive, ConnectionHelper helper, ExtendedConnectionHelper extendedConnectionHelper, ListenerMap lm, String[] params, Codec codec) {
        // System.out.println("Acceptor_impl");
        Assert._OB_assert((helper == null) ^ (extendedConnectionHelper == null));
        hosts_ = hosts;
        multiProfile_ = multiProfile;
        keepAlive_ = keepAlive;
        connectionHelper_ = helper;
        extendedConnectionHelper_ = extendedConnectionHelper;
        codec_ = codec;
        info_ = new AcceptorInfo_impl(this);
        listenMap_ = lm;

        if (backlog == 0)
            backlog = 50; // 50 is the JDK's default value

        //
        // Get the local address for use by connect_self
        //
        try {
            if (address == null) {
                //Since we are
                // binding to all network interfaces, we'll use the loopback
                // address.
                localAddress_ = InetAddress.getLocalHost();
            } else {
                localAddress_ = Util.getInetAddress(address);
            }
        } catch (UnknownHostException ex) {
            logger.log(Level.FINE, "Host resolution failure", ex); 
            throw asCommFailure(ex);
        }

        //
        // Create socket and bind to requested network interface
        //
        try {
            if (address == null) {
                if (connectionHelper_ != null) {
                    socket_ = connectionHelper_.createServerSocket(port, backlog);
                } else {
                    socket_ = extendedConnectionHelper_.createServerSocket(port, backlog, params);
                }
            } else {
                if (connectionHelper_ != null) {
                    socket_ = connectionHelper_.createServerSocket(port, backlog, localAddress_);
                } else {
                    socket_ = extendedConnectionHelper_.createServerSocket(port, backlog, localAddress_, params);
                }
            }

            //
            // Read back the port. This is needed if the port was selected by
            // the operating system.
            //
            port_ = socket_.getLocalPort();
            logger.fine("Acceptor created using socket " + socket_); 
        } catch (BindException ex) {
            logger.log(Level.FINE, "Failure creating server socket for host=" + localAddress_ + ", port=" + port, ex);
            throw (COMM_FAILURE)new COMM_FAILURE(
                    MinorCodes
                            .describeCommFailure(MinorCodes.MinorBind)
                            + ": " + ex.getMessage(),
                    MinorCodes.MinorBind,
                    CompletionStatus.COMPLETED_NO).initCause(ex);
        } catch (IOException ex) {
            logger.log(Level.FINE, "Failure creating server socket for host=" + localAddress_ + ", port=" + port, ex);
            throw asCommFailure(ex, MinorSocket);
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

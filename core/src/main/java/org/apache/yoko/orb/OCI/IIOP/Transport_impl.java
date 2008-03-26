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

final public class Transport_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OCI.Transport {
    // This data member must not be private because the info object
    // must be able to access it
    public java.net.Socket socket_; // The socket

    private java.io.InputStream in_; // The socket's input stream

    private java.io.OutputStream out_; // The socket's output stream

    private boolean shutdown_; // True if shutdown() was called

    private int soTimeout_ = 0; // The value for setSoTimeout()

    private TransportInfo_impl info_; // Transport information
    
    // the real logger backing instance.  We use the interface class as the locator
    static final Logger logger = Logger.getLogger(org.apache.yoko.orb.OCI.Transport.class.getName());

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void setSoTimeout(int t) {
        if (soTimeout_ != t) {
            soTimeout_ = t;

            try {
                socket_.setSoTimeout(soTimeout_);
            } catch (java.net.SocketException ex) {
                logger.log(Level.FINE, "Socket setup error", ex); 
                
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSetSoTimeout)
                                + ": socket error during setSoTimeout: "
                                + ex.getMessage(),
                        org.apache.yoko.orb.OB.MinorCodes.MinorSetSoTimeout,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
            } catch (java.lang.NullPointerException ex) {
                logger.log(Level.FINE, "Socket setup error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSetSoTimeout)
                                + ": NullPointerException error during setSoTimeout: "
                                + ex.getMessage(), 
                        org.apache.yoko.orb.OB.MinorCodes.MinorSetSoTimeout,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex); 
            }
        }
    }

    private void setBlock(boolean block) {
        if (block)
            setSoTimeout(0);
        else
            setSoTimeout(1);
    }

    //
    // Shutdown the sending or receiving side of a socket. If how == 0,
    // shutdown the receiving side. If how == 1, shutdown the sending
    // side. If how == 2, shutdown both.
    //
    private static void shutdownSocket(java.net.Socket socket, int how) {
        if (socket == null) // Socket already closed
            return;

        if (how == 2) {
            shutdownSocket(socket, 0);
            shutdownSocket(socket, 1);
            return;
        }

        try {
            if (how == 0) {
                try {
                    socket.shutdownInput();
                } catch (UnsupportedOperationException e) {
                // if we're using an SSL connection, this is an unsupported operation.
                // just ignore the error and proceed to the close.
                }
            } else if (how == 1) {
                try {
                    socket.shutdownOutput();
                } catch (UnsupportedOperationException e) {
                // if we're using an SSL connection, this is an unsupported operation.
                // just ignore the error and proceed to the close.
                }
            } else {
                throw new InternalError();
            }
        } catch (java.net.SocketException ex) {
            //
            // Some VMs (namely JRockit) raise a SocketException if
            // the socket has already been closed.
            // This exception can be ignored.
            //
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Socket shutdown error", ex); 
            throw (InternalError)new InternalError().initCause(ex);
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

    public org.apache.yoko.orb.OCI.SendReceiveMode mode() {
        return org.apache.yoko.orb.OCI.SendReceiveMode.SendReceive;
    }

    public int handle() {
        throw new org.omg.CORBA.NO_IMPLEMENT();
    }

    public void close() {
        if (socket_ == null) // shutdown() may call close()
            return;

        //
        // Call callbacks
        //
        info_._OB_callCloseCB(info_);

        //
        // Destroy the info object
        //
        info_._OB_destroy();

        //
        // I must set socket_ to null *before* the close or the code
        // below, to avoid a race condition with send/receive
        //

        //
        // Close the socket
        //
        java.net.Socket saveSocket = socket_;
        socket_ = null; // Must be set to null before the shutdown/close
        shutdownSocket(saveSocket, 2); // This helps to unblock threads
        // blocking in recv()
        try {
            saveSocket.close();
        } catch (java.io.IOException ex) {
        }
    }

    public void shutdown() {
        shutdownSocket(socket_, 2); // Shutdown send side only
        if (socket_ != null) {
            // blocking in recv()
            try {
                socket_.close();
            } catch (java.io.IOException ex) {
            }
        }
    }

    public void receive(org.apache.yoko.orb.OCI.Buffer buf, boolean block) {
        setBlock(block);

        logger.fine("receiving a buffer of " + buf.rest_length() + " from " + socket_ + " using transport " + this); 
        while (!buf.is_full()) {
            try {
                int result = in_.read(buf.data(), buf.pos(), buf.rest_length());
                if (result <= 0) {
                    throw new org.omg.CORBA.COMM_FAILURE(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorRecvZero),
                            org.apache.yoko.orb.OB.MinorCodes.MinorRecvZero,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                }
                buf.advance(result);
            } catch (java.io.InterruptedIOException ex) {
                logger.log(Level.FINE, "Received interrupted exception", ex); 
                buf.advance(ex.bytesTransferred);

                if (!block)
                    return;
            } catch (java.io.IOException ex) {
                logger.log(Level.FINE, "Socket read error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorRecv)
                                + ": I/O error during read: " + ex.getMessage(), 
                        org.apache.yoko.orb.OB.MinorCodes.MinorRecv,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex); 
            } catch (java.lang.NullPointerException ex) {
                logger.log(Level.FINE, "Socket read error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorRecv)
                                + ": NullPointerException during read",
                        org.apache.yoko.orb.OB.MinorCodes.MinorRecv,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
            }
        }
    }

    public boolean receive_detect(org.apache.yoko.orb.OCI.Buffer buf,
            boolean block) {
        setBlock(block);

        while (!buf.is_full()) {
            try {
                int result = in_.read(buf.data(), buf.pos(), buf.rest_length());
                if (result <= 0)
                    return false;
                buf.advance(result);
            } catch (java.io.InterruptedIOException ex) {
                buf.advance(ex.bytesTransferred);

                if (!block)
                    return true;
            } catch (java.io.IOException ex) {
                return false;
            } catch (java.lang.NullPointerException ex) {
                return false;
            }
        }

        return true;
    }

    public void receive_timeout(org.apache.yoko.orb.OCI.Buffer buf, int t) {
        if (t < 0)
            throw new InternalError();

        if (t == 0) {
            receive(buf, false);
            return;
        }

        setSoTimeout(t);

        while (!buf.is_full()) {
            try {
                int result = in_.read(buf.data(), buf.pos(), buf.rest_length());
                if (result <= 0) {
                    throw new org.omg.CORBA.COMM_FAILURE(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorRecvZero),
                            org.apache.yoko.orb.OB.MinorCodes.MinorRecvZero,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                }
                buf.advance(result);
            } catch (java.io.InterruptedIOException ex) {
                buf.advance(ex.bytesTransferred);
                return;
            } catch (java.io.IOException ex) {
                logger.log(Level.FINE, "Socket read error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorRecv)
                                + ": I/O error during read: " + ex.getMessage(), 
                        org.apache.yoko.orb.OB.MinorCodes.MinorRecv,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex); 
            } catch (java.lang.NullPointerException ex) {
                logger.log(Level.FINE, "Socket read error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorRecv)
                                + ": NullPointerException during read",
                        org.apache.yoko.orb.OB.MinorCodes.MinorRecv,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex); 
            }
        }
    }

    public boolean receive_timeout_detect(org.apache.yoko.orb.OCI.Buffer buf,
            int t) {
        if (t < 0)
            throw new InternalError();

        if (t == 0)
            return receive_detect(buf, false);

        setSoTimeout(t);

        while (!buf.is_full()) {
            try {
                int result = in_.read(buf.data(), buf.pos(), buf.rest_length());
                if (result <= 0)
                    return false;
                buf.advance(result);
            } catch (java.io.InterruptedIOException ex) {
                buf.advance(ex.bytesTransferred);
                return true;
            } catch (java.io.IOException ex) {
                return false;
            } catch (java.lang.NullPointerException ex) {
                return false;
            }
        }

        return true;
    }

    public void send(org.apache.yoko.orb.OCI.Buffer buf, boolean block) {
        setBlock(block);
        
        logger.fine("Sending buffer of size " + buf.rest_length() + " to " + socket_); 
        
        while (!buf.is_full()) {
            try {
                out_.write(buf.data(), buf.pos(), buf.rest_length());
                out_.flush();
                buf.pos(buf.length());
            } catch (java.io.InterruptedIOException ex) {
                buf.advance(ex.bytesTransferred);

                if (!block)
                    return;
            } catch (java.io.IOException ex) {
                logger.log(Level.FINE, "Socket write error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSend)
                                + ": I/O error during write: " + ex.getMessage(),
                        org.apache.yoko.orb.OB.MinorCodes.MinorSend,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
            } catch (java.lang.NullPointerException ex) {
                logger.log(Level.FINE, "Socket write error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSend)
                                + ": NullPointerException during write",
                        org.apache.yoko.orb.OB.MinorCodes.MinorSend,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
            }
        }
    }

    public boolean send_detect(org.apache.yoko.orb.OCI.Buffer buf, boolean block) {
        setBlock(block);

        while (!buf.is_full()) {
            try {
                out_.write(buf.data(), buf.pos(), buf.rest_length());
                out_.flush();
                buf.pos(buf.length());
            } catch (java.io.InterruptedIOException ex) {
                buf.advance(ex.bytesTransferred);

                if (!block)
                    return true;
            } catch (java.io.IOException ex) {
                return false;
            } catch (java.lang.NullPointerException ex) {
                return false;
            }
        }

        return true;
    }

    public void send_timeout(org.apache.yoko.orb.OCI.Buffer buf, int t) {
        if (t < 0)
            throw new InternalError();

        if (t == 0) {
            send(buf, false);
            return;
        }

        setSoTimeout(t);

        while (!buf.is_full()) {
            try {
                out_.write(buf.data(), buf.pos(), buf.rest_length());
                out_.flush();
                buf.pos(buf.length());
            } catch (java.io.InterruptedIOException ex) {
                buf.advance(ex.bytesTransferred);
                return;
            } catch (java.io.IOException ex) {
                logger.log(Level.FINE,  "Socket write error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSend)
                                + ": I/O error during write: " + ex.getMessage(),
                        org.apache.yoko.orb.OB.MinorCodes.MinorSend,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
            } catch (java.lang.NullPointerException ex) {
                logger.log(Level.FINE, "Socket write error", ex); 
                throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSend)
                                + ": NullPointerException during write",
                        org.apache.yoko.orb.OB.MinorCodes.MinorSend,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
            }
        }
    }

    public boolean send_timeout_detect(org.apache.yoko.orb.OCI.Buffer buf, int t) {
        if (t < 0)
            throw new InternalError();

        if (t == 0)
            return send_detect(buf, false);

        setSoTimeout(t);

        while (!buf.is_full()) {                                 
            try {
                out_.write(buf.data(), buf.pos(), buf.rest_length());
                out_.flush();
                buf.pos(buf.length());
            } catch (java.io.InterruptedIOException ex) {
                buf.advance(ex.bytesTransferred);
                return true;
            } catch (java.io.IOException ex) {
                return false;
            } catch (java.lang.NullPointerException ex) {
                return false;
            }
        }

        return true;
    }

    public org.apache.yoko.orb.OCI.TransportInfo get_info() {
        return info_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Transport_impl(org.apache.yoko.orb.OCI.Connector connector,
            java.net.Socket socket, ListenerMap lm) {
        socket_ = socket;
        shutdown_ = false;

        //
        // Cache the streams associated with the socket, for
        // performance reasons
        //
        try {
            in_ = socket_.getInputStream();
            out_ = socket_.getOutputStream();
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Socket setup error", ex); 
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSocket)
                            + ": unable to obtain socket InputStream: "
                            + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSocket,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Since the Constructor of TransportInfo uses this object create
        // it after all members are initialized
        //
        info_ = new TransportInfo_impl(this, connector, lm);
    }

    public Transport_impl(org.apache.yoko.orb.OCI.Acceptor acceptor,
            java.net.Socket socket, ListenerMap lm) {
        socket_ = socket;
        shutdown_ = false;
        
        logger.fine("Creating new transport for socket " + socket); 

        //
        // Cache the streams associated with the socket, for
        // performance reasons
        //
        try {
            in_ = socket_.getInputStream();
            out_ = socket_.getOutputStream();
        } catch (java.io.IOException ex) {
            logger.log(Level.FINE, "Socket setup error", ex); 
            throw (org.omg.CORBA.COMM_FAILURE)new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorSocket)
                            + ": unable to obtain socket InputStream: "
                            + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorSocket,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO).initCause(ex);
        }

        //
        // Since the Constructor of TransportInfo uses this object create
        // it after all members are initialized
        //
        info_ = new TransportInfo_impl(this, acceptor, lm);
    }

    public void finalize() throws Throwable {
        if (socket_ != null)
            close();

        super.finalize();
    }
    
    public String toString() {
        return "iiop transport using socket " + socket_; 
    }
}

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

import org.apache.yoko.giop.MessageType;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.io.ReadBuffer;
import org.apache.yoko.io.WriteBuffer;
import org.apache.yoko.orb.OCI.SendReceiveMode;
import org.apache.yoko.orb.OCI.Transport;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.yoko.util.MinorCodes.MinorRecv;
import static org.apache.yoko.util.MinorCodes.MinorRecvZero;
import static org.apache.yoko.util.MinorCodes.MinorSend;
import static org.apache.yoko.util.MinorCodes.MinorSetSoTimeout;
import static org.apache.yoko.util.MinorCodes.MinorSocket;
import static org.apache.yoko.util.MinorCodes.describeCommFailure;
import static org.apache.yoko.orb.OCI.IIOP.Exceptions.asCommFailure;
import static org.apache.yoko.orb.OCI.SendReceiveMode.SendReceive;

final public class Transport_impl extends LocalObject implements Transport {
    // This data member must not be private because the info object
    // must be able to access it
    public final Socket socket_; // The socket

    private final InputStream in_; // The socket's input stream

    private final OutputStream out_; // The socket's output stream

    private volatile boolean shutdown_; // True if shutdown() was called

    private int soTimeout_ = 0; // The value for setSoTimeout()

    private final TransportInfo_impl info_; // Transport information

    // the real logger backing instance.  We use the interface class as the locator
    private static final Logger logger = Logger.getLogger(Transport.class.getName());

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void setSoTimeout(int t) {
        if (soTimeout_ != t) {
            soTimeout_ = t;

            try {
                socket_.setSoTimeout(soTimeout_);
            } catch (SocketException ex) {
                logger.log(Level.FINE, "Socket setup error", ex);

                throw (COMM_FAILURE)new COMM_FAILURE(
                        describeCommFailure(MinorSetSoTimeout)
                                + ": socket error during setSoTimeout: "
                                + ex.getMessage(),
                        MinorSetSoTimeout,
                        CompletionStatus.COMPLETED_NO).initCause(ex);
            } catch (NullPointerException ex) {
                logger.log(Level.FINE, "Socket setup error", ex);
                throw (COMM_FAILURE)new COMM_FAILURE(
                        describeCommFailure(MinorSetSoTimeout)
                                + ": NullPointerException error during setSoTimeout: "
                                + ex.getMessage(),
                        MinorSetSoTimeout,
                        CompletionStatus.COMPLETED_NO).initCause(ex);
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
    private void shutdownSocket() {
        try {
                try {
                    socket_.shutdownInput();
                } catch (UnsupportedOperationException e) {
                // if we're using an SSL connection, this is an unsupported operation.
                // just ignore the error and proceed to the close.
                }
                try {
                    socket_.shutdownOutput();
                } catch (UnsupportedOperationException e) {
                // if we're using an SSL connection, this is an unsupported operation.
                // just ignore the error and proceed to the close.
                }
        } catch (SocketException ex) {
            //
            // Some VMs (namely JRockit) raise a SocketException if
            // the socket has already been closed.
            // This exception can be ignored.
            //
        } catch (IOException ex) {
            logger.log(Level.FINE, "Socket shutdown error", ex);
            throw (InternalError)new InternalError().initCause(ex);
        }
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public SendReceiveMode mode() {
        return SendReceive;
    }

    public void close() {
        //
        // I must set socket_ to null *before* the close or the code
        // below, to avoid a race condition with send/receive
        //

        //
        // Close the socket
        //
        shutdownSocket(); // This helps to unblock threads
        // blocking in recv()
        try {
            socket_.close();
        } catch (IOException ignored) {
        }
    }

    public void shutdown() {
        logger.fine("shutdown: " + this);
        shutdown_ = true;
        shutdownSocket(); // Shutdown send side only
        // blocking in recv()
        try {
            socket_.close();
        } catch (IOException ignored) {
        }
    }

    public void receive(WriteBuffer writeBuffer, boolean block) {
        setBlock(block);

        logger.fine("receiving a buffer of " + writeBuffer.available() + " from " + socket_ + " using transport " + this);
        while (!writeBuffer.isComplete()) {
            try {
                if (!writeBuffer.readFrom(in_))
                    throw new COMM_FAILURE(describeCommFailure(MinorRecvZero), MinorRecvZero, CompletionStatus.COMPLETED_NO);
            } catch (InterruptedIOException ex) {
                logger.log(Level.FINE, "Received interrupted exception", ex);

                if (!block)
                    return;
                if (shutdown_)
                    throw asCommFailure(ex, MinorRecv, "Interrupted I/O exception during shutdown");
            } catch (IOException ex) {
                logger.log(Level.FINE, "Socket read error", ex);
                throw asCommFailure(ex, MinorRecv, "I/O error during read");
            } catch (NullPointerException ex) {
                logger.log(Level.FINE, "Socket read error", ex);
                throw asCommFailure(ex, MinorRecv, "NullPointerException during read");
            }
        }
    }

    public void send(ReadBuffer readBuffer, boolean block) {
        setBlock(block);

        logger.fine("Sending buffer of size " + readBuffer.available() + " to " + socket_);

        while (!readBuffer.isComplete()) {
            try {
                MessageType.logOutgoingGiopMessage(readBuffer);
                readBuffer.writeTo(out_);
            } catch (InterruptedIOException ex) {
                if (!block)
                    return;
            } catch (IOException ex) {
                logger.log(Level.FINE, "Socket write error", ex);
                throw asCommFailure(ex, MinorSend, "I/O error during write");
            } catch (NullPointerException ex) {
                logger.log(Level.FINE, "Socket write error", ex);
                throw asCommFailure(ex, MinorSend, "NullPointerException during write");
            }
        }
    }

    public boolean send_detect(ReadBuffer readBuffer, boolean block) {
        setBlock(block);

        while (!readBuffer.isComplete()) {
            try {
                MessageType.logOutgoingGiopMessage(readBuffer);
                readBuffer.writeTo(out_);
            } catch (InterruptedIOException ex) {
                if (!block)
                    return true;
            } catch (IOException | NullPointerException ex) {
                return false;
            }
        }

        return true;
    }

    public void send_timeout(ReadBuffer readBuffer, int t) {
        if (t < 0)
            throw new InternalError();

        if (t == 0) {
            send(readBuffer, false);
            return;
        }

        setSoTimeout(t);

        while (!readBuffer.isComplete()) {
            try {
                MessageType.logOutgoingGiopMessage(readBuffer);
                readBuffer.writeTo(out_);
            } catch (InterruptedIOException ex) {
                return;
            } catch (IOException ex) {
                logger.log(Level.FINE,  "Socket write error", ex);
                throw asCommFailure(ex, MinorSend, "I/O error during write");
            } catch (NullPointerException ex) {
                logger.log(Level.FINE, "Socket write error", ex);
                throw asCommFailure(ex, MinorSend, "NullPointerException during write");
            }
        }
    }

    public org.apache.yoko.orb.OCI.TransportInfo get_info() {
        return info_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Transport_impl(Socket socket, ListenerMap lm) {
        socket_ = socket;
        shutdown_ = false;

        //
        // Cache the streams associated with the socket, for
        // performance reasons
        //
        try {
            in_ = socket_.getInputStream();
            out_ = socket_.getOutputStream();
        } catch (IOException ex) {
            logger.log(Level.FINE, "Socket setup error", ex);
            throw asCommFailure(ex, MinorSocket, "unable to obtain socket InputStream");
        }

        //
        // Since the Constructor of TransportInfo uses this object create
        // it after all members are initialized
        //
        info_ = new TransportInfo_impl(this, lm);
    }

    public Transport_impl(Acceptor acceptor, Socket socket, ListenerMap lm) {
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
        } catch (IOException ex) {
            logger.log(Level.FINE, "Socket setup error", ex);
            throw asCommFailure(ex, MinorSocket, "unable to obtain socket InputStream");
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
        return String.format("Transport to %s with socket %s", info_, socket_);
    }
}

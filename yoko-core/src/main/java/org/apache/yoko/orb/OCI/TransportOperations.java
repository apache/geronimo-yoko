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

package org.apache.yoko.orb.OCI;

import org.omg.CORBA.COMM_FAILURE;

/**
 *
 * The interface for a Transport object, which provides operations
 * for sending and receiving octet streams. In addition, it is
 * possible to register callbacks with the Transport object, which
 * are invoked whenever data can be sent or received without
 * blocking.
 *
 * @see Connector
 * @see Acceptor
 *
 **/
public interface TransportOperations {

    /** The send/receive capabilities of this Transport. */
    SendReceiveMode mode();

    /**
     * Closes the Transport. After calling <code>close</code>, no
     * operations on this Transport object and its associated
     * TransportInfo object may be called. To ensure that no messages
     * get lost when <code>close</code> is called,
     * <code>shutdown</code> should be called first. Then dummy data
     * should be read from the Transport, using one of the
     * <code>receive</code> operations, until either an exception is
     * raised, or until connection closure is detected. After that its
     * save to call <code>close</code>, i.e., no messages can get
     * lost.
     *
     * @exception COMM_FAILURE In case of an error.
     **/
    void close();

    /**
     * Shutdown the Transport. Upon a successful shutdown, threads
     * blocking in the <code>receive</code> operations will return or
     * throw an exception. After calling <code>shutdown</code>, no
     * operations on associated TransportInfo object may be called. To
     * fully close the Transport, <code>close</code> must be called.
     *
     * @exception COMM_FAILURE In case of an error.
     **/
    void shutdown();

    /**
     * Receives a buffer's contents.
     * @param buf The buffer to fill.
     * @param block If set to <code>TRUE</code>, the operation blocks
     * until the buffer is full. If set to <code>FALSE</code>, the
     * operation fills as much of the buffer as possible without
     * blocking.
     *
     * @exception COMM_FAILURE In case of an error.
     **/
    void receive(BufferWriter buf, boolean block);

    /**
     * Sends a buffer's contents.
     *
     * @param buf The buffer to send.
     * @param block If set to <code>TRUE</code>, the operation blocks
     * until the buffer has completely been sent. If set to
     * <code>FALSE</code>, the operation sends as much of the buffer's
     * data as possible without blocking.
     * @exception COMM_FAILURE In case of an error.
     **/
    void send(BufferReader buf, boolean block);

    /**
     * Similar to <code>send</code>, but it signals a connection loss
     * by returning <code>FALSE</code> instead of raising
     * <code>COMM_FAILURE</code>.
     *
     * @param buf The buffer to fill.
     * @param block If set to <code>TRUE</code>, the operation blocks
     * until the entire buffer has been sent. If set to
     * <code>FALSE</code>, the operation sends as much of the buffer's
     * data as possible without blocking.
     * @return <code>FALSE</code> if a connection loss is
     * detected, <code>TRUE</code> otherwise.
     * @exception COMM_FAILURE In case of an error.
     **/
    boolean send_detect(BufferReader buf, boolean block);

    /**
     * Similar to <code>send</code>, but it is possible
     * to specify a timeout. On return the caller can test whether
     * there was a timeout by checking if the buffer has
     * been sent completely.
     *
     * @param buf The buffer to send.
     * @param timeout The timeout value in milliseconds. A zero
     * timeout is equivalent to calling <code>send(buf, FALSE)</code>.
     * @exception COMM_FAILURE In case of an error.
     **/
    void send_timeout(BufferReader buf, int timeout);

    /**
     * Returns the information object associated with
     * the Transport.
     *
     * @return The Transport information object.
     **/
    TransportInfo get_info();
}

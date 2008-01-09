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

package org.apache.yoko.orb.IMR;

//
// IDL:orb.yoko.apache.org/IMR/Server:1.0
//
/**
 *
 * The Server interface.
 *
 **/

public interface ServerOperations
{
    //
    // IDL:orb.yoko.apache.org/IMR/Server/id:1.0
    //
    /** The server id. */

    int
    id();

    //
    // IDL:orb.yoko.apache.org/IMR/Server/status:1.0
    //
    /** The server status. */

    ServerStatus
    status();

    //
    // IDL:orb.yoko.apache.org/IMR/Server/manual:1.0
    //
    /** Was this process manually started? */

    boolean
    manual();

    //
    // IDL:orb.yoko.apache.org/IMR/Server/updateTime:1.0
    //
    /** The last update time. */

    int
    updateTime();

    //
    // IDL:orb.yoko.apache.org/IMR/Server/timesForked:1.0
    //
    /** The number of times restarted. */

    short
    timesForked();

    //
    // IDL:orb.yoko.apache.org/IMR/Server/name:1.0
    //
    /** The server name. */

    String
    name();

    void
    name(String val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/host:1.0
    //
    /** The server host. */

    String
    host();

    void
    host(String val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/exec:1.0
    //
    /** The exec string for the server. */

    String
    exec();

    void
    exec(String val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/args:1.0
    //
    /** The server's command-line arguments. */

    String[]
    args();

    void
    args(String[] val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/directory:1.0
    //
    /** The server's runtime directory. */

    String
    directory();

    void
    directory(String val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/mode:1.0
    //
    /** The server mode */

    ServerActivationMode
    mode();

    void
    mode(ServerActivationMode val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/updateTimeout:1.0
    //
    /** The update timeout. */

    int
    updateTimeout();

    void
    updateTimeout(int val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/failureTimeout:1.0
    //
    /** The failure timeout. */

    int
    failureTimeout();

    void
    failureTimeout(int val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/maxForks:1.0
    //
    /** The maximum number of forks. */

    short
    maxForks();

    void
    maxForks(short val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/activatePOAs:1.0
    //
    /** Allow implicit POA activation. */

    boolean
    activatePOAs();

    void
    activatePOAs(boolean val);

    //
    // IDL:orb.yoko.apache.org/IMR/Server/create_poa_record:1.0
    //
    /**
     *
     * Create a POA record.
     *
     * @param poa The POA to create
     *
     **/

    void
    create_poa_record(String[] poa)
        throws POAAlreadyRegistered;

    //
    // IDL:orb.yoko.apache.org/IMR/Server/remove_poa_record:1.0
    //
    /**
     *
     * Remove a POA record.
     *
     * @param poa The POA to remove
     *
     **/

    void
    remove_poa_record(String[] poa)
        throws _NoSuchPOA;

    //
    // IDL:orb.yoko.apache.org/IMR/Server/get_poa_info:1.0
    //
    /**
     *
     * Retrieve POA info record.
     *
     * @param poa The POA name
     *
     * @return The POA info record.
     *
     **/

    POAInfo
    get_poa_info(String[] poa)
        throws _NoSuchPOA;

    //
    // IDL:orb.yoko.apache.org/IMR/Server/list_poas:1.0
    //
    /**
     *
     * List the poas.
     *
     * @return A sequence of poa names.
     *
     **/

    POAInfo[]
    list_poas();

    //
    // IDL:orb.yoko.apache.org/IMR/Server/clear_error_state:1.0
    //
    /**
     *
     * Clear the servers error state
     *
     **/

    void
    clear_error_state();

    //
    // IDL:orb.yoko.apache.org/IMR/Server/start:1.0
    //
    /**
     *
     * Start the server, if not running.
     *
     **/

    void
    start()
        throws ServerRunning;

    //
    // IDL:orb.yoko.apache.org/IMR/Server/stop:1.0
    //
    /**
     *
     * Stop the server, if running.
     *
     **/

    void
    stop()
        throws OADNotRunning,
               ServerNotRunning;

    //
    // IDL:orb.yoko.apache.org/IMR/Server/destroy:1.0
    //
    /**
     *
     * Destroy the server record.
     *
     **/

    void
    destroy()
        throws ServerRunning;
}

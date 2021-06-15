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

package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.util.Assert;

import java.util.Vector;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static org.apache.yoko.orb.OB.GIOPServerStarter.ServerState.CLOSED;
import static org.apache.yoko.orb.OB.GIOPServerStarter.ServerState.HOLDING;
import static org.apache.yoko.logging.VerboseLogging.CONN_IN_LOG;

abstract class GIOPServerStarter {
    static final Logger logger = Logger.getLogger(GIOPServerStarter.class.getName());
    
    protected final ORBInstance orbInstance_; // The ORBInstance

    protected final Acceptor acceptor_; // The acceptor

    protected final OAInterface oaInterface_; // The OA interface

    protected final Vector connections_ = new java.util.Vector(); // Workers

    enum ServerState {
        ACTIVE,
        HOLDING,
        CLOSED;
        public boolean cannotTransitionTo(ServerState next) {
            if (this == next) return true;
            if (this == HOLDING) return false;
            return this.compareTo(next) > 0;
        }
    }

    protected ServerState serverState;

    // ----------------------------------------------------------------------
    // GIOPServer private and protected member implementation
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert.ensure(serverState == CLOSED);

        super.finalize();
    }

    //
    // Emit a trace message when closing the acceptor
    //
    protected void logCloseAcceptor() {
        if (CONN_IN_LOG.isLoggable(FINE)) CONN_IN_LOG.fine("stopped accepting connections\n" +  acceptor_.get_info().describe());
    }

    protected void reapWorkers() {
        for (int i = 0; i < connections_.size();) {
            GIOPConnection connection = (GIOPConnection) connections_.elementAt(i);
            if (connection.destroyed())
                connections_.removeElementAt(i);
            else
                ++i;

        }
    }

    // ----------------------------------------------------------------------
    // GIOPServer public member implementation
    // ----------------------------------------------------------------------

    GIOPServerStarter(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Acceptor acceptor, OAInterface oaInterface) {
        orbInstance_ = orbInstance;
        acceptor_ = acceptor;
        oaInterface_ = oaInterface;
        serverState = HOLDING; // Must be holding initially

        try {
            // Trace acceptor creation
            if (CONN_IN_LOG.isLoggable(FINE)) CONN_IN_LOG.fine("accepting connections\n" + acceptor_.get_info().describe());

            // Start listening
            acceptor_.listen();
        } catch (org.omg.CORBA.SystemException ex) {
            acceptor_.close();
            serverState = CLOSED;
            throw ex;
        }
    }

    // given a host/port this will search the workers of this
    // GIOPServerStarter for an inbound connection transport
    // which matches the specific connection information.
    // It returns null if not found.
    public synchronized GIOPConnection getMatchingConnection(org.apache.yoko.orb.OCI.ConnectorInfo connInfo) {
        // reap the workers first since we don't want to return a destroyed transport
        reapWorkers();

        // iterate the workers
        for (int i = 0; i < connections_.size(); i++) {
            GIOPConnection worker = (GIOPConnection) connections_.elementAt(i);

            // we only want to find inbound connections
            if (worker.isOutbound())
                continue;

            org.apache.yoko.orb.OCI.Transport transport = worker.transport();

            if (transport != null && transport.get_info().endpoint_alias_match(connInfo))
                return worker;
        }

        // we never found a match for the transport
        return null;
    }

    abstract public void setState(ServerState state);
}

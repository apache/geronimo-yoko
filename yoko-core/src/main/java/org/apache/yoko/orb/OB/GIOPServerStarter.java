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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.PortableServer.*;

abstract class GIOPServerStarter {
    static final Logger logger = Logger.getLogger(GIOPServerStarter.class.getName());
    
    protected ORBInstance orbInstance_; // The ORBInstance

    protected org.apache.yoko.orb.OCI.Acceptor acceptor_; // The acceptor

    protected OAInterface oaInterface_; // The OA interface

    protected java.util.Vector connections_ = new java.util.Vector(); // Workers

    public static final int StateActive = 0;

    public static final int StateHolding = 1;

    public static final int StateClosed = 2;

    protected int state_;

    // ----------------------------------------------------------------------
    // GIOPServer private and protected member implementation
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert._OB_assert(state_ == StateClosed);

        super.finalize();
    }

    //
    // Emit a trace message when closing the acceptor
    //
    protected void logCloseAcceptor() {
        CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
        if (coreTraceLevels.traceConnections() > 0) {
            org.apache.yoko.orb.OCI.AcceptorInfo info = acceptor_.get_info();
            String msg = "stopped accepting connections\n";
            msg += info.describe();
            orbInstance_.getLogger().trace("incoming", msg);
        }
    }

    protected void reapWorkers() {
        for (int i = 0; i < connections_.size();) {
            GIOPConnection connection = (GIOPConnection) connections_
                    .elementAt(i);
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
        state_ = StateHolding; // Must be holding initially

        try {
            //
            // Trace acceptor creation
            //
            CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
            if (coreTraceLevels.traceConnections() > 0) {
                org.apache.yoko.orb.OCI.AcceptorInfo info = acceptor_
                        .get_info();
                String msg = "accepting connections\n";
                msg += info.describe();
                orbInstance_.getLogger().trace("incoming", msg);
            }

            //
            // Start listening
            //
            acceptor_.listen();
        } catch (org.omg.CORBA.SystemException ex) {
            acceptor_.close();
            state_ = StateClosed;
            throw ex;
        }
    }

    //
    // given a host/port this will search the workers of this
    // GIOPServerStarter for a transport which matches the specific
    // connection information. It returns null if not found.
    //
    public synchronized GIOPConnection getWorker(
            org.apache.yoko.orb.OCI.ConnectorInfo connInfo) {
        //
        // reap the workers first since we don't want to return a
        // destroyed transport
        //
        reapWorkers();

        // 
        // iterate the workers
        //
        for (int i = 0; i < connections_.size(); i++) {
            GIOPConnection worker = (GIOPConnection) connections_.elementAt(i);

            org.apache.yoko.orb.OCI.Transport transport = worker.transport();

            if (transport != null)
                if (transport.get_info().endpoint_alias_match(connInfo))
                    return worker;
        }

        // 
        // we never found a match for the transport
        // 
        return null;
    }

    //
    // Change the state of the worker
    //
    abstract public void setState(int state);
}

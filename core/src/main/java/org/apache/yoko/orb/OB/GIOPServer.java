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

final class GIOPServer extends Server {
    static final Logger logger = Logger.getLogger(GIOPServer.class.getName());
    protected ORBInstance orbInstance_; // The ORB Instance

    protected boolean destroy_; // True if destroy() was called

    protected org.apache.yoko.orb.OCI.Acceptor acceptor_; // The acceptor

    protected OAInterface oaInterface_; // The object adapter interface

    protected GIOPServerStarter starter_; // The server starter

    // ----------------------------------------------------------------------
    // GIOPServer private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert._OB_assert(destroy_);
        Assert._OB_assert(starter_ == null);

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // GIOPServer package member implementations
    // ----------------------------------------------------------------------

    GIOPServer(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Acceptor acceptor, OAInterface oaInterface,
            int concModel) {
        super(concModel);
        orbInstance_ = orbInstance;
        destroy_ = false;
        acceptor_ = acceptor;
        oaInterface_ = oaInterface;
        
        logger.fine("GIOPServer " + System.identityHashCode(this) + " created for orb instance " + orbInstance_.getOrbId() + " and server " + orbInstance_.getServerId() + " identityHash=" + System.identityHashCode(orbInstance_)); 

        try {
            switch (concModel_) {
            case Threaded:
                starter_ = new GIOPServerStarterThreaded(orbInstance_,
                        acceptor_, oaInterface_);
                break;
            }
        } catch (RuntimeException ex) {
            destroy_ = true;
            throw ex;
        }
    }

    //
    // Destroy the server
    //
    public void destroy() {
        logger.fine("Destroying GIOPServer " + System.identityHashCode(this) + " started for orb instance " + orbInstance_.getOrbId() + " and server " + orbInstance_.getServerId() + System.identityHashCode(orbInstance_)); 
        //
        // Don't destroy twice
        //
        if (destroy_)
            return;

        //
        // Set the destroy flag
        //
        destroy_ = true;

        //
        // Close and remove the starter
        //
        Assert._OB_assert(starter_ != null);
        starter_.setState(GIOPServerStarter.StateClosed);
        starter_ = null;
    }

    //
    // Hold any new requests that arrive for the Server
    //
    public void hold() {
        logger.fine("Holding GIOPServer " + System.identityHashCode(this) + " started for orb instance " + orbInstance_.getOrbId() + " and server " + orbInstance_.getServerId() + System.identityHashCode(orbInstance_)); 
        Assert._OB_assert(!destroy_);
        Assert._OB_assert(starter_ != null);
        starter_.setState(GIOPServerStarter.StateHolding);
    }

    //
    // Dispatch any requests that arrive for the Server
    //
    public void activate() {
        logger.fine("Activating GIOPServer " + System.identityHashCode(this) + " started for orb instance " + orbInstance_.getOrbId() + " and server " + orbInstance_.getServerId() + System.identityHashCode(orbInstance_)); 
        Assert._OB_assert(!destroy_);
        Assert._OB_assert(starter_ != null);
        starter_.setState(GIOPServerStarter.StateActive);
    }

    //
    // returns the GIOPServerStarter interface
    //
    public GIOPServerStarter _OB_getGIOPServerStarter() {
        return starter_;
    }
}

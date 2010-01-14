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

import org.apache.yoko.orb.OCI.IIOP.PLUGIN_ID;

public final class Plugin_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OCI.Plugin {
    private org.omg.CORBA.ORB orb_; // The ORB

    private ListenerMap listenMap_; // list of listenPoints

    private ConnectionHelper connectionHelper_;   // SSL connection helper

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return org.omg.IOP.TAG_INTERNET_IOP.value;
    }

    public void init_client(String[] params) {
        boolean keepAlive = true;
        int i = 0;
        while (i < params.length) {
            if (params[i].equals("--no-keepalive")) {
                keepAlive = false;
                i++;
            } else {
                throw new org.omg.CORBA.INITIALIZE("iiop: unknown client "
                        + "parameter `" + params[i] + "'");
            }
        }

        //
        // Install the ConFactory
        //
        try {
            org.omg.CORBA.Object obj = orb_
                    .resolve_initial_references("OCIConFactoryRegistry");
            org.apache.yoko.orb.OCI.ConFactoryRegistry registry = org.apache.yoko.orb.OCI.ConFactoryRegistryHelper
                    .narrow(obj);
            registry.add_factory(new ConFactory_impl(orb_, keepAlive, listenMap_, connectionHelper_));
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.apache.yoko.orb.OCI.FactoryAlreadyExists ex) {
            throw new org.omg.CORBA.INITIALIZE("OCI IIOP plug-in already "
                    + "installed");
        }

        //
        // Install the "iiop" corbaloc URL protocol
        //
        try {
            org.omg.CORBA.Object obj = orb_
                    .resolve_initial_references("URLRegistry");
            org.apache.yoko.orb.OB.URLRegistry registry = org.apache.yoko.orb.OB.URLRegistryHelper
                    .narrow(obj);
            org.apache.yoko.orb.OB.URLScheme scheme = registry
                    .find_scheme("corbaloc");
            org.apache.yoko.orb.OB.Assert._OB_assert(scheme != null);
            org.apache.yoko.orb.OB.CorbalocURLScheme corbaloc = org.apache.yoko.orb.OB.CorbalocURLSchemeHelper
                    .narrow(scheme);
            corbaloc.add_protocol(new CorbalocProtocol_impl());
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.apache.yoko.orb.OB.CorbalocURLSchemePackage.ProtocolAlreadyExists ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        }
    }

    public void init_server(String[] params) {
        try {
            org.omg.CORBA.Object obj = orb_
                    .resolve_initial_references("OCIAccFactoryRegistry");
            org.apache.yoko.orb.OCI.AccFactoryRegistry registry = org.apache.yoko.orb.OCI.AccFactoryRegistryHelper
                    .narrow(obj);
            registry.add_factory(new AccFactory_impl(orb_, listenMap_, connectionHelper_));
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
        } catch (org.apache.yoko.orb.OCI.FactoryAlreadyExists ex) {
            throw new org.omg.CORBA.INITIALIZE("OCI IIOP plug-in already "
                    + "installed");
        }
    }


    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public Plugin_impl(org.omg.CORBA.ORB orb, ConnectionHelper helper) {
        orb_ = orb;
        connectionHelper_ = helper;
        listenMap_ = new ListenerMap();
    }
}

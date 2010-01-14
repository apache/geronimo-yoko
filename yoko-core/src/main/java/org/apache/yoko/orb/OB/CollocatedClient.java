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

final public class CollocatedClient extends Client implements DowncallEmitter {
    //
    // My Server
    //
    private CollocatedServer server_;

    // ----------------------------------------------------------------------
    // CollocatedClient public member implementations
    // ----------------------------------------------------------------------

    public CollocatedClient(CollocatedServer server, int concModel,
            CodeConverters conv) {
        super(concModel, conv);
        server_ = server;
    }

    //
    // Destroy the client
    //
    public void destroy(boolean terminate) {
        // Nothing to do here
    }

    //
    // Get a new request ID
    //
    public int requestId() {
        //
        // This operation *must* delegate to CollocatedServer, because
        // request IDs must be unique per CollocatedServer, not per
        // CollocatedClient.
        //
        return server_.requestId();
    }

    //
    // Get all profiles that are usable with this client
    //
    public org.apache.yoko.orb.OCI.ProfileInfo[] getUsableProfiles(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies) {
        return server_.getUsableProfiles(ior, policies);
    }

    //
    // Get the OCI connector info
    //
    public org.apache.yoko.orb.OCI.ConnectorInfo connectorInfo() {
        return null; // There is no connector
    }

    //
    // Get the OCI transport info
    //
    public org.apache.yoko.orb.OCI.TransportInfo transportInfo() {
        return null; // There is no transport
    }

    //
    // Start a downcall, returning a downcall emitter and an
    // OutputStream for marshalling a request
    //
    public DowncallEmitter startDowncall(Downcall down,
            org.apache.yoko.orb.CORBA.OutputStreamHolder out) {
        org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
        out.value = new org.apache.yoko.orb.CORBA.OutputStream(buf);
        return this;
    }

    //
    // Checks whether this client is equal to another client
    //
    public boolean equal(Client cl) {
        CollocatedClient client = null;
        try {
            client = (CollocatedClient) cl;
        } catch (ClassCastException ex) {
            return false;
        }

        if (server_ != client.server_)
            return false;

        return true;
    }

    //
    // Force connection establishment
    //
    public void bind(int connectTimeout) {
        // nothing to do
    }

    //
    // Determines whether this client supports twoway invocations
    //
    public boolean twoway() {
        return true;
    }

    //
    // Send and receive downcalls
    //
    // - The first parameter is the downcall to send/receive
    //
    // - If the second parameter is set to false, send/receive will be
    // done non-blocking.
    //
    // - If the return value is true, it's safe to access or modify
    // the downcall object. If the return value if false, accessing
    // or modifying the downcall object is not allowed, for thread
    // safety reasons. (Because the downcall object is not thread
    // safe.)
    //
    public boolean send(Downcall down, boolean block) {
        return server_.send(down, block);
    }

    public boolean receive(Downcall down, boolean block) {
        return server_.receive(down, block);
    }

    //
    // Send and receive downcalls with one operation (for efficiency
    // reasons)
    //
    public boolean sendReceive(Downcall down) {
        return server_.sendReceive(down);
    }

    public org.omg.IOP.ServiceContext[] getAMIRouterSCL() {
        return null;
    }
}

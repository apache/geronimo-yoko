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

final class GIOPClient extends Client {
    protected ORBInstance orbInstance_; // The ORB instance

    protected int nextRequestId_; // The next request ID

    protected java.lang.Object nextRequestIdMutex_ = new java.lang.Object(); // The
                                                                                // next
                                                                                // request
                                                                                // ID
                                                                                // mutex

    protected org.apache.yoko.orb.OCI.Connector connector_; // The connector

    protected GIOPConnection connection_;

    //
    // Codesets SC
    // 
    protected org.omg.IOP.ServiceContext codeSetSC_;

    protected boolean bidirWorker_; // is the worker bidir?

    protected boolean ownsWorker_; // does 'this' own the worker?

    protected boolean destroy_; // True if destroy() was called

    // ----------------------------------------------------------------------
    // GIOPClient private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert._OB_assert(destroy_);
        Assert._OB_assert(connection_ == null);

        super.finalize();
    }

    // 
    // uses the prepopulated connector_ (not connected) to do a lookup,
    // checking if a bidir connection alias exists... it returns it if
    // it does and returns null otherwise
    //
    protected GIOPConnection find_bidir_worker() {
        try {
            //
            // Any transport that we want to query should exist when the
            // server first receives a request from the client-side. This
            // transport will have the ListenPointList populated inside of
            // its TransportInfo. So we query the list of
            // GIOPServerStarters for the correct transport and hopefully
            // find a match if we want to use bidir
            //
            org.apache.yoko.orb.OBPortableServer.POAManagerFactory pmFactoryImpl = orbInstance_
                    .getPOAManagerFactory();

            //
            // Obtain a list of POAs for this POAManager
            // 
            org.omg.PortableServer.POAManager[] pmSeq = pmFactoryImpl.list();

            for (int i = 0; i < pmSeq.length; i++) {
                org.apache.yoko.orb.OBPortableServer.POAManager_impl poaImpl = (org.apache.yoko.orb.OBPortableServer.POAManager_impl) pmSeq[i];

                // 
                // Get the server manager from the POA
                // 
                org.apache.yoko.orb.OB.ServerManager sm = poaImpl
                        ._OB_getServerManager();

                //
                // get the list of servers from the server manager
                //
                org.apache.yoko.orb.OB.Server[] servSeq = sm.getServers();

                // 
                // iterate these servers obtaining the GIOPServerStarter
                //
                for (int j = 0; j < servSeq.length; j++) {
                    org.apache.yoko.orb.OB.GIOPServer giopServer = (org.apache.yoko.orb.OB.GIOPServer) servSeq[j];

                    org.apache.yoko.orb.OB.GIOPServerStarter servStarter = giopServer
                            ._OB_getGIOPServerStarter();

                    //
                    // get the matching worker from the GIOPServerStarter
                    //
                    GIOPConnection gw = servStarter.getWorker(connectorInfo());

                    if (gw != null)
                        return gw;
                }
            }
        } catch (ClassCastException ex) {
        }

        //
        // nothing was found to return
        // 
        return null;
    }

    //
    // Get the worker. If the bool flag is true, and no worker exists,
    // a new worker is created, with the timeout specified as second
    // parameter.
    //
    protected synchronized GIOPConnection getWorker(boolean create, int t) {
        if (destroy_)
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        // 
        // first attempt to locate a reusable bidir connection
        //
        if (connection_ == null) {
            connection_ = find_bidir_worker();

            if (connection_ != null) {
                //
                // adjust the requestID to match the spec (even for
                // clients, odd for servers)
                // 
                if ((nextRequestId_ & 1) == 0)
                    nextRequestId_++;
                ownsWorker_ = false;
                connection_.activateClientSide(this);

                // 
                // log the reusing of the connection
                //
                CoreTraceLevels coreTraceLevels = orbInstance_
                        .getCoreTraceLevels();
                if (coreTraceLevels.traceConnections() > 0) {
                    org.apache.yoko.orb.OCI.TransportInfo info = connection_
                            .transport().get_info();
                    String msg = "reusing established bidir connection\n";
                    msg += info.describe();
                    orbInstance_.getLogger().trace("outgoing", msg);
                }
            }
        }

        // 
        // no bidir connection resolved so create one if the request
        // calls for it
        // 
        if (connection_ == null && create) {
            //
            // Trace connection attempt
            //
            CoreTraceLevels coreTraceLevels = orbInstance_.getCoreTraceLevels();
            if (coreTraceLevels.traceConnections() > 0) {
                org.apache.yoko.orb.OCI.ConnectorInfo info = connector_
                        .get_info();
                String msg = "trying to establish connection\n";
                msg += "timeout: ";
                if (t >= 0) {
                    msg += t;
                    msg += "ms\n";
                } else
                    msg += "none\n";
                msg += info.describe();
                orbInstance_.getLogger().trace("outgoing", msg);
            }

            //
            // Create new transport, using the connector
            //
            // For symetry reasons, GIOPClientStarterThreaded should also be
            // added, even though these classes only have a trivial
            // functionality. Or perhaps the GIOPClientStarterThreaded tries to
            // connect() in the backgound? Just an idea...
            //

            org.apache.yoko.orb.OCI.Transport transport;

            if (t >= 0) {
                transport = connector_.connect_timeout(t);

                //
                // Was there a timeout?
                //
                if (transport == null)
                    throw new org.omg.CORBA.NO_RESPONSE("Connection timeout",
                            0, org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            } else {
                transport = connector_.connect();
                Assert._OB_assert(transport != null);
            }

            //
            // Create new worker
            //
            Assert._OB_assert(concModel_ == Threaded);
            connection_ = new GIOPConnectionThreaded(orbInstance_, transport,
                    this);
            ownsWorker_ = true;

            //
            // bidirWorker_ means that this connection may be used to
            // service requests so we need to set ourselves up as a
            // server (to correct map the OAInterfaces)
            //
            if (bidirWorker_)
                connection_.activateServerSide();
        }

        //
        // Lazy initialization of codeSetSC_. We don't want to
        // initialize codeSetSC_ in the constructor, in order to
        // keep creation of GIOPClients, which might later on not
        // be used, as light-weight as possible.
        //
        initServiceContexts();

        return connection_;
    }

    //
    // initialize internal service contexts
    private void initServiceContexts() {
        if (codeSetSC_ == null) {
            //
            // Create CONV_FRAME::CodeSetContext
            //
            org.omg.CONV_FRAME.CodeSetContext ctx = new org.omg.CONV_FRAME.CodeSetContext();
            CodeConverters conv = codeConverters();

            if (conv.outputCharConverter != null)
                ctx.char_data = conv.outputCharConverter.getTo().rgy_value;
            else
                ctx.char_data = CodeSetDatabase.ISOLATIN1;

            if (conv.outputWcharConverter != null)
                ctx.wchar_data = conv.outputWcharConverter.getTo().rgy_value;
            else
                ctx.wchar_data = orbInstance_.getNativeWcs();

            //
            // Create encapsulation for CONV_FRAME::CodeSetContext
            //
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream outCSC = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);
            outCSC._OB_writeEndian();
            org.omg.CONV_FRAME.CodeSetContextHelper.write(outCSC, ctx);

            //
            // Create service context containing the
            // CONV_FRAME::CodeSetContext encapsulation
            //
            codeSetSC_ = new org.omg.IOP.ServiceContext();
            codeSetSC_.context_id = org.omg.IOP.CodeSets.value;

            int len = buf.length();
            byte[] data = buf.data();
            codeSetSC_.context_data = new byte[len];
            System.arraycopy(data, 0, codeSetSC_.context_data, 0, len);
        }

        //
        // NOTE: We don't initialize the INVOCATION_POLICIES service context
        // here because the list of policies can change from one invocation to
        // the next. Instead, we need to get the policies and build the
        // service context each time we make an invocation.
        //
    }

    // ----------------------------------------------------------------------
    // GIOPClient package member implementations
    // ----------------------------------------------------------------------

    GIOPClient(ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Connector connector, int concModel,
            CodeConverters conv, boolean bidirEnable) {
        super(concModel, conv);
        orbInstance_ = orbInstance;
        nextRequestId_ = 0;
        connector_ = connector;
        connection_ = null;
        destroy_ = false;
        bidirWorker_ = bidirEnable;
        ownsWorker_ = true;
    }

    // ----------------------------------------------------------------------
    // GIOPClient public member implementations
    // ----------------------------------------------------------------------

    //
    // Destroy the client
    //
    public void destroy(boolean terminate) {
        GIOPConnection c = null;

        synchronized (this) {
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
            // Use a copy of the worker, and destroy the worker outside
            // the synchronization, to avoid deadlocks
            //
            c = connection_;
            connection_ = null;
        }

        //
        // If there is a worker (and we exclusively own it) destroy it
        //
        if (c != null && ownsWorker_)
            c.destroy(terminate);
    }

    public synchronized void removeConnection(GIOPConnection connection) {
        if (connection_ == connection)
            connection_ = null;
    }

    //
    // Get a new request ID
    //
    public int requestId() {
        synchronized (nextRequestIdMutex_) {
            //
            // In the case of BiDir connections, the client should use
            // even numbered requestIds and the server should use odd
            // numbered requestIds... the += 2 keeps this pattern intact
            // assuming its correct at startup
            // 
            return nextRequestId_ += 2;
        }
    }

    //
    // get a list of ServiceContexts that have to be sent on an AMI router
    // request
    //
    public org.omg.IOP.ServiceContext[] getAMIRouterSCL() {
        //
        // initialize the service contexts if they haven't already been
        //
        initServiceContexts();

        org.omg.IOP.ServiceContext[] scl = new org.omg.IOP.ServiceContext[1];
        scl[0] = codeSetSC_;

        //
        // return the list
        //
        return scl;
    }

    //
    // Get all profiles that are usable with this client
    //
    public org.apache.yoko.orb.OCI.ProfileInfo[] getUsableProfiles(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies) {
        //
        // Get all profiles usable for the connector
        //
        org.apache.yoko.orb.OCI.ProfileInfo[] all = connector_
                .get_usable_profiles(ior, policies);

        //
        // Filter out profiles which would require a different code converter
        //
        java.util.Vector vec = new java.util.Vector();
        for (int i = 0; i < all.length; i++) {
            CodeConverters conv = CodeSetUtil.getCodeConverters(orbInstance_,
                    all[i]);
            if (codeConverters().equals(conv))
                vec.addElement(all[i]);
        }

        org.apache.yoko.orb.OCI.ProfileInfo[] result = new org.apache.yoko.orb.OCI.ProfileInfo[vec
                .size()];
        vec.copyInto(result);
        return result;
    }

    //
    // Get the OCI Connector info
    //
    public org.apache.yoko.orb.OCI.ConnectorInfo connectorInfo() {
        return connector_.get_info();
    }

    //
    // Get the OCI Transport info
    //
    public org.apache.yoko.orb.OCI.TransportInfo transportInfo() {
        //
        // Get the connection, but do not create a new one if there is none
        // available
        //
        GIOPConnection connection = getWorker(false, -1);

        if (connection == null)
            return null;

        org.apache.yoko.orb.OCI.Transport transport = connection.transport();
        return transport.get_info();
    }

    //
    // Start a downcall, returning a downcall emitter and an
    // OutputStream for marshalling a request
    //
    public DowncallEmitter startDowncall(Downcall down,
            org.apache.yoko.orb.CORBA.OutputStreamHolder out) {
        GIOPConnection connection = null;
        try {
            //
            // Get the worker, creating a new one if there is none
            // available
            //
            connection = getWorker(true, down.policies().connectTimeout);
        } catch (org.omg.CORBA.SystemException ex) {
            Assert
                    ._OB_assert(ex.completed == org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            down.setFailureException(ex);
            return null;
        }

        try {
            //
            // We only need to add a code set context if we're GIOP
            // version 1.1 or higher, and if no messages have been sent so
            // far
            //
            byte major = down.profileInfo().major;
            byte minor = down.profileInfo().minor;
            if (!connection.requestSent() && (major > 1 || minor >= 1)) {
                CoreTraceLevels coreTraceLevels = orbInstance_
                        .getCoreTraceLevels();
                if (coreTraceLevels.traceConnections() >= 2) {
                    CodeConverters conv = codeConverters();
                    String msg = "sending transmission code sets";
                    msg += "\nchar code set: ";
                    if (conv.outputCharConverter != null)
                        msg += conv.outputCharConverter.getTo().description;
                    else {
                        CodeSetInfo info = CodeSetDatabase.instance()
                                .getCodeSetInfo(orbInstance_.getNativeCs());
                        msg += info.description;
                    }
                    msg += "\nwchar code set: ";
                    if (conv.outputWcharConverter != null)
                        msg += conv.outputWcharConverter.getTo().description;
                    else {
                        CodeSetInfo info = CodeSetDatabase.instance()
                                .getCodeSetInfo(orbInstance_.getNativeWcs());
                        msg += info.description;
                    }
                    orbInstance_.getLogger().trace("outgoing", msg);
                }

                Assert._OB_assert(codeSetSC_ != null);
                down.addToRequestSCL(codeSetSC_);
            }

            // 
            // I don't want to send BiDir related contexts if I'm not
            // working with GIOP 1.2 or greater.
            //
            boolean validGIOPVersion = false;
            if ((major > 1) || ((major == 1) && (minor >= 2)))
                validGIOPVersion = true;

            if (validGIOPVersion
                    && (down.policies().biDirMode == org.omg.BiDirPolicy.BOTH.value)) {
                org.apache.yoko.orb.OCI.Transport t = connection.transport();

                org.omg.IOP.ServiceContext contexts[] = t.get_info()
                        .get_service_contexts(down.policies().value);
                for (int i = 0; i < contexts.length; i++)
                    down.addToRequestSCL(contexts[i]);
            }

            org.apache.yoko.orb.OCI.ProfileInfo profileInfo = down
                    .profileInfo();
            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                    12);
            buf.pos(12);
            out.value = new org.apache.yoko.orb.CORBA.OutputStream(buf,
                    codeConverters(), (profileInfo.major << 8)
                            | profileInfo.minor);

            //
            // Create GIOP outgoing message
            //
            GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(
                    orbInstance_, out.value, profileInfo);

            //
            // Write header
            //
            String op = down.operation();
            if (op.charAt(0) == '_' && op.equals("_locate"))
                outgoing.writeLocateRequestHeader(down.requestId());
            else
                outgoing.writeRequestHeader(down.requestId(), down.operation(),
                        down.responseExpected(), down.getRequestSCL());

            return connection.emitterInterface();
        } catch (org.omg.CORBA.SystemException ex) {
            Assert
                    ._OB_assert(ex.completed == org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            down.setFailureException(ex);
            return null;
        }
    }

    //
    // Checks whether this client is equal to another client
    //
    public boolean equal(Client cl) {
        GIOPClient client = null;
        try {
            client = (GIOPClient) cl;
        } catch (ClassCastException ex) {
            return false;
        }

        if (!connector_.equal(client.connector_))
            return false;

        if (!codeConverters().equals(client.codeConverters()))
            return false;

        return true;
    }

    //
    // Force connection establishment
    //
    public void bind(int connectTimeout) {
        //
        // Get the connection, creating a new one if there is none
        // available
        //
        getWorker(true, connectTimeout);
    }

    //
    // Determines whether this client supports twoway invocations
    //
    public boolean twoway() {
        //
        // Get the connection
        //
        GIOPConnection connection = getWorker(false, -1);
        Assert._OB_assert(connection != null);
        org.apache.yoko.orb.OCI.Transport transport = connection.transport();
        return transport.mode() == org.apache.yoko.orb.OCI.SendReceiveMode.SendReceive;
    }

    // 
    // determines whether this GIOPClient exclusively owns its worker or
    // if its shared with another Client/Server
    //
    public boolean sharedConnection() {
        return !ownsWorker_;
    }
}

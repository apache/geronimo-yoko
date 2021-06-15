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

import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.CORBA.OutputStreamHolder;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OBPortableServer.POAManager_impl;
import org.apache.yoko.io.Buffer;
import org.apache.yoko.orb.OCI.Connector;
import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.apache.yoko.orb.OCI.GiopVersion;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.SendReceiveMode;
import org.apache.yoko.orb.OCI.Transport;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.orb.exceptions.Transients;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.Cache;
import org.apache.yoko.util.Factory;
import org.apache.yoko.util.Reference;
import org.omg.BiDirPolicy.BOTH;
import org.omg.CONV_FRAME.CodeSetContext;
import org.omg.CONV_FRAME.CodeSetContextHelper;
import org.omg.CORBA.NO_RESPONSE;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.IOP.CodeSets;
import org.omg.IOP.IOR;
import org.omg.IOP.SendingContextRunTime;
import org.omg.IOP.ServiceContext;
import org.omg.PortableServer.POAManager;
import org.omg.SendingContext.CodeBase;
import org.omg.SendingContext.CodeBaseHelper;

import javax.rmi.CORBA.ValueHandler;
import java.util.ArrayList;
import java.util.List;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINEST;
import static javax.rmi.CORBA.Util.createValueHandler;
import static org.apache.yoko.orb.OB.CodeSetInfo.ISO_LATIN_1;
import static org.apache.yoko.logging.VerboseLogging.CONN_OUT_LOG;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

/**
 * The client-side of a GIOP connection.
 */
final class GIOPClient extends Client {

    @Override
    public String toString() {
        return super.toString() + " to " + connector_.get_info() + " -- " + connection_;
    }

    protected ORBInstance orbInstance_;

    protected final Connector connector_;

    /** A lazily final field pointing (eventually) to the single connection for the lifetime of this object */
    private GIOPConnection connection_;
    /** The connection cache reference to release on destroy().  */
    private Reference<GIOPConnection> connectionRef;

    /** Codesets SC */
    protected ServiceContext codeSetSC_;

    protected ServiceContext codeBaseSC_;

    protected boolean bidirWorker_; // is the worker bidir?

    private boolean destroy_; // True if destroy() was called

    // ----------------------------------------------------------------------
    // GIOPClient private and protected member implementations
    // ----------------------------------------------------------------------

    // uses the prepopulated connector_ (not connected) to do a lookup,
    // checking if a bidir connection alias exists... it returns it if
    // it does and returns null otherwise
    protected GIOPConnection find_bidir_worker() {
        try {
            for (POAManager poaManager : orbInstance_.getPOAManagerFactory().list()) {
                for (Server aServSeq : ((POAManager_impl) poaManager)._OB_getServerManager().getServers()) {
                    GIOPConnection conn = ((GIOPServer) aServSeq)._OB_getGIOPServerStarter().getMatchingConnection(connectorInfo());
                    if (conn != null) return conn;
                }
            }
        } catch (ClassCastException ignored) {}
        return null;
    }

    //
    // Get the worker. If the bool flag is true, and no worker exists,
    // a new worker is created, with the timeout specified as second
    // parameter.
    //
    protected synchronized GIOPConnection getWorker(boolean create, final int timeout) {
        if (destroy_)
            throw Transients.ACTIVE_CONNECTION_MANAGEMENT.create();

        if (connection_ == null)
            reuseInboundConnection();


        //
        // no inbound bidir connection resolved so lookup an existing outbound connection
        // or create one if the request calls for it
        //
        if (connection_ == null)
            reuseOrCreateOutboundConnection(create, timeout);

        //
        // Lazy initialization of codeSetSC_. We don't want to
        // initialize codeSetSC_ in the constructor, in order to
        // keep creation of GIOPClients, which might later on not
        // be used, as light-weight as possible.
        //
        initServiceContexts();

        return connection_;
    }

    private synchronized void reuseOrCreateOutboundConnection(boolean create, final int timeout) {
        Cache<ConnectorInfo, GIOPConnection> connCache = orbInstance_.getOutboundConnectionCache();
        if (create) {
            connectionRef = connCache.getOrCreate(connector_.get_info(), new Factory<GIOPConnection>() {
                @Override
                public GIOPConnection create() {
                    return createOutboundConnection(timeout);
                }
            });
        } else {
            connectionRef = connCache.get(connector_.get_info());
        }
        connCache.clean();
        connection_ = connectionRef.get();

        //
        // bidirWorker_ means that this connection may be used to
        // service requests so we need to set ourselves up as a
        // server (to correct map the OAInterfaces)
        //
        if (bidirWorker_)
            connection_.activateServerSide();
    }

    private synchronized void reuseInboundConnection() {
        //
        // first attempt to locate a reusable bidir connection
        //
        connection_ = find_bidir_worker();

        if (connection_ == null) return;

        connection_.activateClientSide();

        // log the reusing of the connection
        if (CONN_OUT_LOG.isLoggable(FINE)) CONN_OUT_LOG.fine("reusing established bidir connection\n" + connection_.transport());
    }

    private GIOPConnectionThreaded createOutboundConnection(int t) {
        // Trace connection attempt
        if (CONN_OUT_LOG.isLoggable(FINE)) {
            String timeout = t >= 0 ? t + "ms" : "none";
            String msg = String.format("trying to establish connection: %s    timeout: %s", connector_, timeout);
            CONN_OUT_LOG.fine(msg);
        }

        //
        // Create new transport, using the connector
        //
        // For symetry reasons, GIOPClientStarterThreaded should also be
        // added, even though these classes only have a trivial
        // functionality. Or perhaps the GIOPClientStarterThreaded tries to
        // connect() in the backgound? Just an idea...
        //

        Transport transport;

        if (t >= 0) {
            transport = connector_.connect_timeout(t);

            //
            // Was there a timeout?
            //
            if (transport == null)
                throw new NO_RESPONSE("Connection timeout", 0, COMPLETED_NO);
        } else {
            transport = connector_.connect();
            Assert.ensure(transport != null);
        }

        //
        // Create new worker
        //
        Assert.ensure(concurrencyModel == Threaded);
        return new GIOPConnectionThreaded(orbInstance_, transport, this);
    }

    // initialize internal service contexts
    private void initServiceContexts() {
        if (codeSetSC_ == null) {
            CodeSetContext ctx = new CodeSetContext();
            CodeConverters conv = codeConverters();

            ctx.char_data = conv.outputCharConverter == null ? ISO_LATIN_1.id : conv.outputCharConverter.getDestinationCodeSet().id;
            ctx.wchar_data = conv.outputWcharConverter == null ? orbInstance_.getNativeWcs() : conv.outputWcharConverter.getDestinationCodeSet().id;

            // Create encapsulation for CONV_FRAME::CodeSetContext
            try (OutputStream outCSC = new OutputStream()) {
                outCSC._OB_writeEndian();
                CodeSetContextHelper.write(outCSC, ctx);

                // Create service context containing the
                // CONV_FRAME::CodeSetContext encapsulation
                codeSetSC_ = new ServiceContext();
                codeSetSC_.context_id = CodeSets.value;

                codeSetSC_.context_data = outCSC.copyWrittenBytes();
            }
        }
        if (codeBaseSC_ == null) {

            ValueHandler valueHandler = createValueHandler();
            CodeBase codeBase = CodeBaseHelper.narrow(valueHandler.getRunTimeCodeBase());


            try (OutputStream outCBC = new OutputStream()) {
                outCBC._OB_writeEndian();
                CodeBaseHelper.write(outCBC, codeBase);

                codeBaseSC_ = new ServiceContext();
                codeBaseSC_.context_id = SendingContextRunTime.value;
                codeBaseSC_.context_data = outCBC.copyWrittenBytes();
            }
        }
        // NOTE: We don't initialize the INVOCATION_POLICIES service context
        // here because the list of policies can change from one invocation to
        // the next. Instead, we need to get the policies and build the
        // service context each time we make an invocation.
    }

    GIOPClient(ORBInstance orbInstance,
               Connector connector, int concModel,
               CodeConverters conv, boolean bidirEnable) {
        super(concModel, conv);
        orbInstance_ = orbInstance;
        connector_ = connector;
        connection_ = null;
        destroy_ = false;
        bidirWorker_ = bidirEnable;
    }

    /** Destroy the client */
    public synchronized void destroy() {
        if (destroy_) return;
        try (Reference<?> closeMe = connectionRef) {
            destroy_ = true;
        }
    }

    /** Get a new request ID */
    public int getNewRequestID() {
        return connection_.getNewRequestId();
    }

    // get a list of ServiceContexts that have to be sent on an AMI router request
    public ServiceContexts getAMIRouterContexts() {
        // initialize the service contexts if they haven't already been
        initServiceContexts();

        // return the list
        return ServiceContexts.unmodifiable(codeSetSC_);
    }

    /** Get all profiles that are usable with this client */
    public ProfileInfo[] getUsableProfiles(IOR ior, Policy[] policies) {
        // Get all profiles usable for the connector
        List<ProfileInfo> profileInfos = new ArrayList<>();
        for (ProfileInfo anAll : connector_.get_usable_profiles(ior, policies)) {
            CodeConverters conv = CodeSetUtil.getCodeConverters(orbInstance_, anAll);
            // Filter out profiles which would require a different code converter
            if (codeConverters().equals(conv)) profileInfos.add(anAll);
        }
        return profileInfos.toArray(new ProfileInfo[profileInfos.size()]);
    }

    /** Get the OCI Connector info */
    public ConnectorInfo connectorInfo() {
        return connector_.get_info();
    }

    /** Get the OCI Transport info */
    public TransportInfo transportInfo() {
        // Get the connection, but do not create a new one if there is none available
        GIOPConnection connection = getWorker(false, -1);

        if (connection == null)
            return null;

        Transport transport = connection.transport();
        return transport.get_info();
    }

    // Start a downcall, returning a downcall emitter and an OutputStream for marshalling a request
    public DowncallEmitter startDowncall(Downcall down, OutputStreamHolder out) {
        GIOPConnection connection;
        try {
            // Get the worker, creating a new one if there is none available
            connection = getWorker(true, down.policies().connectTimeout);
        } catch (SystemException ex) {
            Assert.ensure(ex.completed == COMPLETED_NO);
            down.setFailureException(ex);
            return null;
        }

        try {
            // We only need to add a code set context if we're GIOP
            // version 1.1 or higher, and if no messages have been sent so far
            byte major = down.profileInfo().major;
            byte minor = down.profileInfo().minor;
            if (!connection.isRequestSent() && (major > 1 || minor >= 1)) {
                if (CONN_OUT_LOG.isLoggable(FINEST)) CONN_OUT_LOG.finest("sending transmission code sets: \n" + codeConverters());

                Assert.ensure(codeSetSC_ != null);
                down.addToRequestContexts(codeSetSC_);

                Assert.ensure(codeBaseSC_ != null);
                down.addToRequestContexts(codeBaseSC_);
            }

            // 
            // I don't want to send BiDir related contexts if I'm not
            // working with GIOP 1.2 or greater.
            //
            boolean validGIOPVersion = false;
            if ((major > 1) || ((major == 1) && (minor >= 2)))
                validGIOPVersion = true;

            if (validGIOPVersion && (down.policies().biDirMode == BOTH.value)) {
                Transport t = connection.transport();

                ServiceContexts contexts = t.get_info().get_service_contexts(down.policies().value);
                for (ServiceContext context : contexts) down.addToRequestContexts(context);
            }

            ProfileInfo profileInfo = down.profileInfo();
            out.value = new OutputStream(Buffer.createWriteBuffer(12).padAll(), codeConverters(), GiopVersion.get(profileInfo.major, profileInfo.minor));

            // Create GIOP outgoing message
            GIOPOutgoingMessage outgoing = new GIOPOutgoingMessage(orbInstance_, out.value, profileInfo);

            // Write header
            if (down.operation().equals("_locate"))
                outgoing.writeLocateRequestHeader(down.requestId());
            else
                outgoing.writeRequestHeader(down.requestId(), down.operation(), down.responseExpected(), down.requestContexts);

            return connection.emitterInterface();
        } catch (SystemException ex) {
            Assert.ensure(ex.completed == COMPLETED_NO);
            down.setFailureException(ex);
            return null;
        }
    }

    /** Checks whether this client is equal to another client */
    public boolean matches(Client other) {
        if (!!!(other instanceof GIOPClient)) return false;
        GIOPClient that = (GIOPClient) other;

        return this.connector_.equal(that.connector_) && this.codeConverters().equals(that.codeConverters());
    }

    /** Force connection establishment */
    public void bind(int connectTimeout) {
        //
        // Get the connection, creating a new one if there is none
        // available
        //
        getWorker(true, connectTimeout);
    }

    /** Determines whether this client supports twoway invocations */
    public boolean twoway() {
        //
        // Get the connection
        //
        GIOPConnection connection = getWorker(false, -1);
        Assert.ensure(connection != null);
        Transport transport = connection.transport();
        return transport.mode() == SendReceiveMode.SendReceive;
    }

    @Override
    public void prepareForDowncall(RefCountPolicyList policies) {
        getWorker(true, policies.connectTimeout);
    }
}

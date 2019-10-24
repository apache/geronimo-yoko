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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OB.Net;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.CLIENT_SIDE;
import org.apache.yoko.orb.OCI.SERVER_SIDE;
import org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE;
import org.omg.BiDirPolicy.BOTH;
import org.omg.BiDirPolicy.BidirectionalPolicy;
import org.omg.BiDirPolicy.BidirectionalPolicyHelper;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.Policy;
import org.omg.IIOP.BiDirIIOPServiceContext;
import org.omg.IIOP.BiDirIIOPServiceContextHelper;
import org.omg.IIOP.ListenPoint;
import org.omg.IOP.BI_DIR_IIOP;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TAG_INTERNET_IOP;

import java.net.Socket;

public final class TransportInfo_impl extends LocalObject implements TransportInfo {
    private enum Origin{CLIENT(CLIENT_SIDE.value), SERVER(SERVER_SIDE.value); final short value; Origin(int v) {value = (short)v;}}
    private final Socket socket;
    private final Origin origin;
    private final ListenerMap listenMap_;
    private volatile ListenPoint[] listenPoints_ = null;

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return TAG_INTERNET_IOP.value;
    }

    public short origin() {
        return origin.value;
    }

    public synchronized String describe() {
        String desc = "id: " + PLUGIN_ID.value;

        String localAddr = addr();
        short localPort = port();
        desc += "\nlocal address: ";
        desc += localAddr;
        desc += ":";
        desc += (localPort < 0 ? 0xffff + (int) localPort + 1 : localPort);

        String remoteAddr = remote_addr();
        short remotePort = remote_port();
        desc += "\nremote address: ";
        desc += remoteAddr;
        desc += ":";
        desc += (remotePort < 0 ? 0xffff + (int) remotePort + 1 : remotePort);

        return desc;
    }

    public Socket getSocket() {return socket;}

    public String addr() {return socket.getLocalAddress().getHostAddress();}

    public short port() {return (short)socket.getLocalPort();}

    public String remote_addr() {return socket.getInetAddress().getHostAddress();}

    public short remote_port() {return (short)socket.getPort();}

    public ServiceContexts get_service_contexts(Policy[] policies) {
        for (Policy policy : policies) {
            if (policy.policy_type() == BIDIRECTIONAL_POLICY_TYPE.value) {
                BidirectionalPolicy p = BidirectionalPolicyHelper.narrow(policy);
                if (p.value() == BOTH.value) {
                    BiDirIIOPServiceContext biDirCtxt = new BiDirIIOPServiceContext();
                    biDirCtxt.listen_points = listenMap_.getListenPoints();

                    try (OutputStream out = new OutputStream()) {
                        out._OB_writeEndian();
                        BiDirIIOPServiceContextHelper.write(out, biDirCtxt);
                        // Create and fill the return context list
                        return ServiceContexts.unmodifiable(new ServiceContext(BI_DIR_IIOP.value, out.copyWrittenBytes()));
                    }
                }
            }
        }

        // we don't have a bidir service context so return an array of length 0
        return ServiceContexts.EMPTY;
    }

    public void handle_service_contexts(ServiceContexts contexts) {
        ServiceContext context = contexts.get(BI_DIR_IIOP.value);
        if (context == null) return;
        InputStream in = new InputStream(context.context_data);
        in._OB_readEndian();

        // unmarshal the octets back to the bidir format
        BiDirIIOPServiceContext biDirCtxt = BiDirIIOPServiceContextHelper.read(in);

        // save the listening points in the transport
        _OB_setListenPoints(biDirCtxt.listen_points);
    }

    public synchronized boolean received_bidir_service_context() {
        return listenPoints_ != null && (listenPoints_.length > 0);
    }

    public synchronized boolean endpoint_alias_match(org.apache.yoko.orb.OCI.ConnectorInfo connInfo) {
        // we only deal with Connectors that are of our specific type,
        // namely IIOP connectors (and ConnectorInfos)
        ConnectorInfo_impl infoImpl;
        try {
            infoImpl = (ConnectorInfo_impl) connInfo;
        } catch (ClassCastException ex) {
            return false;
        }

        // compare the endpoint information in this connector with the
        // various endpoint inforamtion in our listenMap_
        if (listenPoints_ == null) return false;

        short port = infoImpl.remote_port();
        String host = infoImpl.remote_addr();

        for (ListenPoint aListenPoints_ : listenPoints_) {
            if (aListenPoints_.port != port) continue;
            if (Net.CompareHosts(aListenPoints_.host, host)) return true;
        }

        return false;
    }

    private synchronized void _OB_setListenPoints(ListenPoint[] lp) {
        listenPoints_ = lp;
    }

    private TransportInfo_impl(Socket socket, Origin origin, ListenerMap lm) {
        this.socket = socket;
        this.origin = origin;
        listenMap_ = lm;
    }

    // client-side constructor
    TransportInfo_impl(Transport_impl transport, ListenerMap lm) {
        this(transport.socket_, Origin.CLIENT, lm);
    }

    //server-side constructor
    TransportInfo_impl(Transport_impl transport, Acceptor acceptor, ListenerMap lm) {
        this(transport.socket_, Origin.SERVER, lm);
    }
}

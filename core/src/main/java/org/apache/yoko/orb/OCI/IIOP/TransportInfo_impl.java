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

import java.net.Socket;

import org.apache.yoko.orb.OCI.IIOP.PLUGIN_ID;
import org.apache.yoko.orb.OCI.IIOP.TransportInfo;

public final class TransportInfo_impl extends org.omg.CORBA.LocalObject
        implements TransportInfo {
    private org.apache.yoko.orb.OCI.ConnectorInfo connectorInfo_; // connector
                                                                    // info

    private org.apache.yoko.orb.OCI.AcceptorInfo acceptorInfo_; // acceptor info

    private Transport_impl transport_; // associated transport

    private org.omg.IIOP.ListenPoint[] listenPoints_ = null;

    private ListenerMap listenMap_;

    //
    // All close callback objects
    //
    private java.util.Vector closeCBVec_ = new java.util.Vector();

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return org.omg.IOP.TAG_INTERNET_IOP.value;
    }

    public short origin() {
        if (acceptorInfo_ == null)
            return org.apache.yoko.orb.OCI.CLIENT_SIDE.value;
        else
            return org.apache.yoko.orb.OCI.SERVER_SIDE.value;
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

    public synchronized org.apache.yoko.orb.OCI.ConnectorInfo connector_info() {
        return connectorInfo_;
    }

    public synchronized org.apache.yoko.orb.OCI.AcceptorInfo acceptor_info() {
        return acceptorInfo_;
    }

    public synchronized void add_close_cb(org.apache.yoko.orb.OCI.CloseCB cb) {
        int length = closeCBVec_.size();
        for (int i = 0; i < length; i++)
            if (closeCBVec_.elementAt(i) == cb)
                return; // Already registered
        closeCBVec_.addElement(cb);
    }

    public synchronized void remove_close_cb(org.apache.yoko.orb.OCI.CloseCB cb) {
        int length = closeCBVec_.size();
        for (int i = 0; i < length; i++)
            if (closeCBVec_.elementAt(i) == cb) {
                closeCBVec_.removeElementAt(i);
                return;
            }
    }

    public synchronized java.net.Socket socket() {
        if (transport_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        return transport_.socket_;
    }


    public synchronized String addr() {
        if (transport_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        return transport_.socket_.getLocalAddress().getHostAddress();
    }

    public synchronized short port() {
        if (transport_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        int port = transport_.socket_.getLocalPort();

        if (port >= 0x8000)
            return (short) (port - 0xffff - 1);
        else
            return (short) port;
    }

    public synchronized String remote_addr() {
        if (transport_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        return transport_.socket_.getInetAddress().getHostAddress();
    }

    public synchronized short remote_port() {
        if (transport_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        int port = transport_.socket_.getPort();

        if (port >= 0x8000)
            return (short) (port - 0xffff - 1);
        else
            return (short) port;
    }

    public org.omg.IOP.ServiceContext[] get_service_contexts(
            org.omg.CORBA.Policy[] policies) {
        org.omg.IOP.ServiceContext[] scl;
        boolean bHaveBidir = false;

        for (int i = 0; i < policies.length; i++) {
            if (policies[i].policy_type() == org.omg.BiDirPolicy.BIDIRECTIONAL_POLICY_TYPE.value) {
                org.omg.BiDirPolicy.BidirectionalPolicy p = org.omg.BiDirPolicy.BidirectionalPolicyHelper
                        .narrow(policies[i]);
                if (p.value() == org.omg.BiDirPolicy.BOTH.value)
                    bHaveBidir = true;
                break;
            }
        }

        if (bHaveBidir) {
            org.omg.IIOP.BiDirIIOPServiceContext biDirCtxt = new org.omg.IIOP.BiDirIIOPServiceContext();
            biDirCtxt.listen_points = listenMap_.getListenPoints();

            org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer();
            org.apache.yoko.orb.CORBA.OutputStream out = new org.apache.yoko.orb.CORBA.OutputStream(
                    buf);

            out._OB_writeEndian();
            org.omg.IIOP.BiDirIIOPServiceContextHelper.write(out, biDirCtxt);

            //
            // Fill in the bidir service context
            //
            org.omg.IOP.ServiceContext context = new org.omg.IOP.ServiceContext();
            context.context_id = org.omg.IOP.BI_DIR_IIOP.value;
            context.context_data = buf.data();

            //
            // Create and fill the return context list
            //
            scl = new org.omg.IOP.ServiceContext[1];
            scl[0] = context;
            return scl;
        }

        //
        // we don't have a bidir service context so return an array of
        // length 0
        //
        scl = new org.omg.IOP.ServiceContext[0];
        return scl;
    }

    public void handle_service_contexts(org.omg.IOP.ServiceContext[] contexts) {
        for (int i = 0; i < contexts.length; i++) {
            if (contexts[i].context_id == org.omg.IOP.BI_DIR_IIOP.value) {
                byte[] pOct = contexts[i].context_data;
                int len = contexts[i].context_data.length;

                org.apache.yoko.orb.OCI.Buffer buf = new org.apache.yoko.orb.OCI.Buffer(
                        pOct, len);
                org.apache.yoko.orb.CORBA.InputStream in = new org.apache.yoko.orb.CORBA.InputStream(
                        buf, 0, false);
                in._OB_readEndian();

                //
                // unmarshal the octets back to the bidir format
                //
                org.omg.IIOP.BiDirIIOPServiceContext biDirCtxt = org.omg.IIOP.BiDirIIOPServiceContextHelper
                        .read(in);

                //
                // save the listening points in the transport
                //
                _OB_setListenPoints(biDirCtxt.listen_points);

                break;
            }
        }
    }

    public synchronized boolean received_bidir_SCL() {
        if (listenPoints_ == null)
            return false;

        return (listenPoints_.length > 0);
    }

    public synchronized boolean endpoint_alias_match(
            org.apache.yoko.orb.OCI.ConnectorInfo connInfo) {
        //
        // we only deal with Connectors that are of our specific type,
        // namely IIOP connectors (and ConnectorInfos)
        //
        org.apache.yoko.orb.OCI.IIOP.ConnectorInfo_impl infoImpl;
        try {
            infoImpl = (org.apache.yoko.orb.OCI.IIOP.ConnectorInfo_impl) connInfo;
        } catch (ClassCastException ex) {
            return false;
        }

        //
        // compare the endpoint information in this connector with the
        // various endpoint inforamtion in our listenMap_
        //
        if (listenPoints_ == null)
            return false;

        short port = infoImpl.remote_port();
        String host = infoImpl.remote_addr();

        for (int i = 0; i < listenPoints_.length; i++) {
            if ((listenPoints_[i].port == port)
                    && org.apache.yoko.orb.OB.Net.CompareHosts(
                            listenPoints_[i].host, host))
                return true;
        }

        return false;
    }

    public synchronized org.omg.IIOP.ListenPoint[] _OB_getListenPoints() {
        return listenPoints_;
    }

    public synchronized void _OB_setListenPoints(org.omg.IIOP.ListenPoint[] lp) {
        listenPoints_ = lp;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    TransportInfo_impl(Transport_impl transport,
            org.apache.yoko.orb.OCI.Connector connector, ListenerMap lm) {
        transport_ = transport;
        connectorInfo_ = connector.get_info();
        listenMap_ = lm;
    }

    TransportInfo_impl(Transport_impl transport,
            org.apache.yoko.orb.OCI.Acceptor acceptor, ListenerMap lm) {
        transport_ = transport;
        acceptorInfo_ = acceptor.get_info();
        listenMap_ = lm;
    }

    synchronized void _OB_callCloseCB(org.apache.yoko.orb.OCI.TransportInfo info) {
        int length = closeCBVec_.size();
        for (int i = 0; i < length; i++) {
            org.apache.yoko.orb.OCI.CloseCB cb = (org.apache.yoko.orb.OCI.CloseCB) closeCBVec_
                    .elementAt(i);
            cb.close_cb(info);
        }
    }

    synchronized void _OB_destroy() {
        transport_ = null;
    }
}

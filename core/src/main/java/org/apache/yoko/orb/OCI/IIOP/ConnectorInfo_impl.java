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

import org.apache.yoko.orb.OCI.IIOP.ConnectorInfo;
import org.apache.yoko.orb.OCI.IIOP.PLUGIN_ID;

public final class ConnectorInfo_impl extends org.omg.CORBA.LocalObject
        implements ConnectorInfo {
    private Connector_impl connector_; // The associated connector

    //
    // All connect callback objects
    //
    private java.util.Vector connectCBVec_ = new java.util.Vector();

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public String id() {
        return PLUGIN_ID.value;
    }

    public int tag() {
        return org.omg.IOP.TAG_INTERNET_IOP.value;
    }

    public synchronized String describe() {
        short remotePort = remote_port();

        String desc = "id: " + PLUGIN_ID.value;
        desc += "\nremote address: ";
        desc += remote_addr();
        desc += ":";
        desc += (remotePort < 0 ? 0xffff + (int) remotePort + 1 : remotePort);

        return desc;
    }

    public synchronized void add_connect_cb(org.apache.yoko.orb.OCI.ConnectCB cb) {
        int length = connectCBVec_.size();
        for (int i = 0; i < length; i++)
            if (connectCBVec_.elementAt(i) == cb)
                return; // Already registered
        connectCBVec_.addElement(cb);
    }

    public synchronized void remove_connect_cb(
            org.apache.yoko.orb.OCI.ConnectCB cb) {
        int length = connectCBVec_.size();
        for (int i = 0; i < length; i++)
            if (connectCBVec_.elementAt(i) == cb) {
                connectCBVec_.removeElementAt(i);
                return;
            }
    }

    public synchronized String remote_addr() {
        if (connector_ == null)
            throw new org.omg.CORBA.NO_RESOURCES("No connector");

        try {
            java.net.InetAddress address = java.net.InetAddress
                    .getByName(connector_.host_);
            return address.getHostAddress();
        } catch (java.net.UnknownHostException ex) {
            throw new org.omg.CORBA.COMM_FAILURE(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeCommFailure(org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname)
                            + ": " + ex.getMessage(),
                    org.apache.yoko.orb.OB.MinorCodes.MinorGethostbyname,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
    }

    public synchronized short remote_port() {
        if (connector_ == null)
            throw new org.omg.CORBA.NO_RESOURCES("No connector");

        int port = connector_.port_;

        if (port >= 0x8000)
            return (short) (port - 0xffff - 1);
        else
            return (short) port;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    ConnectorInfo_impl(Connector_impl connector,
            org.apache.yoko.orb.OCI.ConnectCB[] cb) {
        connector_ = connector;

        for (int i = 0; i < cb.length; i++)
            connectCBVec_.addElement(cb[i]);
    }

    synchronized void _OB_callConnectCB(
            org.apache.yoko.orb.OCI.TransportInfo info) {
        int length = connectCBVec_.size();
        for (int i = 0; i < length; i++) {
            org.apache.yoko.orb.OCI.ConnectCB cb = (org.apache.yoko.orb.OCI.ConnectCB) connectCBVec_
                    .elementAt(i);
            cb.connect_cb(info);
        }
    }

    synchronized void _OB_destroy() {
        connector_ = null;
    }
}

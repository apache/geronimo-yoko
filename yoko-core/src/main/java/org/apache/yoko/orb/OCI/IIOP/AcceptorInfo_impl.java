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

import org.apache.yoko.orb.OCI.IIOP.AcceptorInfo;
import org.apache.yoko.orb.OCI.IIOP.PLUGIN_ID;

public final class AcceptorInfo_impl extends org.omg.CORBA.LocalObject
        implements AcceptorInfo {
    private Acceptor_impl acceptor_; // The associated acceptor

    //
    // All accept callback objects
    //
    private java.util.Vector acceptCBVec_ = new java.util.Vector();

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
        String desc = "id: " + PLUGIN_ID.value;

        String localAddr = addr();
        short localPort = port();
        desc += "\nlocal address: ";
        desc += localAddr;
        desc += ":";
        desc += (localPort < 0 ? 0xffff + (int) localPort + 1 : localPort);

        desc += "\nhosts: ";
        String[] hostNames = hosts();
        for (int i = 0; i < hostNames.length; i++) {
            if (i > 0)
                desc += ", ";
            desc += hostNames[i];
        }

        return desc;
    }

    public synchronized void add_accept_cb(org.apache.yoko.orb.OCI.AcceptCB cb) {
        int length = acceptCBVec_.size();
        for (int i = 0; i < length; i++)
            if (acceptCBVec_.elementAt(i) == cb)
                return; // Already registered
        acceptCBVec_.addElement(cb);
    }

    public synchronized void remove_accept_cb(
            org.apache.yoko.orb.OCI.AcceptCB cb) {
        int length = acceptCBVec_.size();
        for (int i = 0; i < length; i++)
            if (acceptCBVec_.elementAt(i) == cb) {
                acceptCBVec_.removeElementAt(i);
                return;
            }
    }

    public synchronized String[] hosts() {
        if (acceptor_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        return acceptor_.hosts_;
    }

    public synchronized String addr() {
        if (acceptor_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        return acceptor_.socket_.getInetAddress().getHostAddress();
    }

    public synchronized short port() {
        if (acceptor_ == null)
            throw new org.omg.CORBA.NO_RESOURCES();

        int port = acceptor_.socket_.getLocalPort();

        if (port >= 0x8000)
            return (short) (port - 0xffff - 1);
        else
            return (short) port;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    AcceptorInfo_impl(Acceptor_impl acceptor) {
        acceptor_ = acceptor;
    }

    synchronized void _OB_callAcceptCB(
            org.apache.yoko.orb.OCI.TransportInfo info) {
        int length = acceptCBVec_.size();
        for (int i = 0; i < length; i++) {
            org.apache.yoko.orb.OCI.AcceptCB cb = (org.apache.yoko.orb.OCI.AcceptCB) acceptCBVec_
                    .elementAt(i);
            cb.accept_cb(info);
        }
    }

    synchronized void _OB_destroy() {
        acceptor_ = null;
    }
}

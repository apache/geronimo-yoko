/*
 * Copyright 2015 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an \"AS IS\" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.apache.yoko.orb.OCI.IIOP;

import org.apache.yoko.orb.OCI.IIOP.AcceptorInfo;
import org.apache.yoko.orb.OCI.IIOP.PLUGIN_ID;
import org.omg.CORBA.LocalObject;

public final class AcceptorInfo_impl extends LocalObject implements AcceptorInfo {
    private Acceptor_impl acceptor_; // The associated acceptor

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

    synchronized void _OB_destroy() {
        acceptor_ = null;
    }
}

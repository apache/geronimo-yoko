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

package org.apache.yoko.orb.OCI;

import org.apache.yoko.orb.OCI.TransportInfo;

final class CurrentStateHolder {
    java.util.Stack transportStack = new java.util.Stack();
}

final public class Current_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OCI.Current {
    private java.util.Hashtable stateKey_ = new java.util.Hashtable();

    // ------------------------------------------------------------------
    // Private and protected member implementations
    // ------------------------------------------------------------------

    private void destroyCurrentTSD() {
        Thread t = Thread.currentThread();
        stateKey_.remove(t);
    }

    private CurrentStateHolder getStateHolder() {
        Thread t = Thread.currentThread();
        CurrentStateHolder holder_ = (CurrentStateHolder) stateKey_.get(t);

        //
        // If the data isn't already allocated then allocate a new one
        //
        if (holder_ == null) {
            holder_ = new CurrentStateHolder();
            stateKey_.put(t, holder_);
        }

        return holder_;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public org.apache.yoko.orb.OCI.TransportInfo get_oci_transport_info() {
        CurrentStateHolder holder = getStateHolder();

        //
        // TODO: Exception?
        //
        if (holder.transportStack.isEmpty())
            return null;

        return (org.apache.yoko.orb.OCI.TransportInfo) holder.transportStack
                .firstElement();
    }

    public org.apache.yoko.orb.OCI.AcceptorInfo get_oci_acceptor_info() {
        org.apache.yoko.orb.OCI.TransportInfo info = get_oci_transport_info();
        if (info != null)
            return info.acceptor_info();
        else
            return null;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public void _OB_preinvoke(TransportInfo info) {
        CurrentStateHolder holder = getStateHolder();
        holder.transportStack.push(info);
    }

    public void _OB_postinvoke() {
        CurrentStateHolder holder = getStateHolder();
        holder.transportStack.pop();

        if (holder.transportStack.empty())
            destroyCurrentTSD();
    }
}

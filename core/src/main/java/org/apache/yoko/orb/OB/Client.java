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

public abstract class Client {
    private int usage_; // The usage counter

    private org.apache.yoko.orb.OB.CodeConverters codeConverters_; // The code
                                                                    // converters

    //
    // The concurrency model for this Client
    //
    final public static int Blocking = 0;

    final public static int Threaded = 2;

    protected int concModel_;

    // ----------------------------------------------------------------------
    // Client private and protected member implementations
    // ----------------------------------------------------------------------

    // ----------------------------------------------------------------------
    // Client package member implementations
    // ----------------------------------------------------------------------

    Client(int concModel, CodeConverters conv) {
        usage_ = 0;
        codeConverters_ = conv;
        concModel_ = concModel;
    }

    // ----------------------------------------------------------------------
    // Client public member implementations
    // ----------------------------------------------------------------------

    //
    // Destroy the client
    //
    public abstract void destroy(boolean terminate);

    //
    // Increment usage (not mutex protected)
    //
    public final void incUsage() {
        Assert._OB_assert(usage_ >= 0);
        usage_++;
    }

    //
    // Decrement usage (not mutex protected)
    //
    // Returns true if after the decrement the usage counter is larger
    // than 0, and false otherwise.
    //
    public final boolean decUsage() {
        Assert._OB_assert(usage_ > 0);
        usage_--;
        return usage_ > 0;
    }

    //
    // Get the codeset converters
    //
    public org.apache.yoko.orb.OB.CodeConverters codeConverters() {
        return codeConverters_;
    }

    //
    // Get a new request ID
    //
    public abstract int requestId();

    //
    // get a list of ServiceContexts that have to be sent on an AMI router
    // request
    //
    public abstract org.omg.IOP.ServiceContext[] getAMIRouterSCL();

    //
    // Get all profiles that are usable with this client
    //
    public abstract org.apache.yoko.orb.OCI.ProfileInfo[] getUsableProfiles(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] pl);

    //
    // Get the OCI connector info
    //
    public abstract org.apache.yoko.orb.OCI.ConnectorInfo connectorInfo();

    //
    // Get the OCI transport info
    //
    public abstract org.apache.yoko.orb.OCI.TransportInfo transportInfo();

    //
    // Start a downcall, returning a downcall emitter and an
    // OutputStream for marshalling a request
    //
    public abstract DowncallEmitter startDowncall(Downcall down,
            org.apache.yoko.orb.CORBA.OutputStreamHolder out);

    //
    // Checks whether this client is equal to another client
    //
    public abstract boolean equal(Client clt);

    //
    // Force connection establishment
    //
    public abstract void bind(int connectTimeout);

    //
    // Determines whether this client supports twoway invocations
    //
    public abstract boolean twoway();
}

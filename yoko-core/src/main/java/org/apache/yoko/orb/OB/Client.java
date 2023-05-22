/*
 * Copyright 2021 IBM Corporation and others.
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
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.OutputStreamHolder;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OCI.ConnectorInfo;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.apache.yoko.rmi.util.ObjectUtil;
import org.apache.yoko.util.Assert;
import org.omg.CORBA.Policy;
import org.omg.IOP.IOR;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Client {
    public static final int Blocking = 0;
    public static final int Threaded = 2;
    private final String label = ObjectUtil.getNextObjectLabel(this.getClass());
    private final AtomicInteger users = new AtomicInteger(0);
    private final CodeConverters codeConverters;
    final int concurrencyModel;

    Client(int concModel, CodeConverters conv) {
        codeConverters = conv;
        concurrencyModel = concModel;
    }

    public abstract void destroy();

    /** Start using this client on a particular thread */
    public final void obtain() {
        int count = users.incrementAndGet();
        Assert.ensure(count > 0);
    }

    /**
     * Stop using this client on a particular thread.
     *
     * @return true iff the client is no longer in use by any threads
     */
    public final boolean release() {
        int count = users.decrementAndGet();
        Assert.ensure(count >= 0);
        return count == 0;
    }

    final CodeConverters codeConverters() {
        return codeConverters;
    }

    public abstract int getNewRequestID();

    /**
     * get a list of ServiceContexts that have to be sent on an AMI router request
     */
    public abstract ServiceContexts getAMIRouterContexts();

    public abstract ProfileInfo[] getUsableProfiles(IOR ior, Policy[] pl);

    public abstract ConnectorInfo connectorInfo();

    public abstract TransportInfo transportInfo();

    public abstract DowncallEmitter startDowncall(Downcall down, OutputStreamHolder out);

    public abstract boolean matches(Client clt);

    // Force connection establishment
    public abstract void bind(int connectTimeout);

    // Determines whether this client supports twoway invocations
    public abstract boolean twoway();

    public void prepareForDowncall(RefCountPolicyList policies) {}

    @Override
    public String toString() {
        return label + "[users=" + users + "]";
    }
}

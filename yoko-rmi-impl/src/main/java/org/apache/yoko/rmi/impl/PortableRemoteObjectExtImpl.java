/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.yoko.rmi.impl;

import org.apache.yoko.rmi.api.PortableRemoteObjectExtDelegate;
import org.apache.yoko.rmi.api.PortableRemoteObjectState;
import org.apache.yoko.rmi.util.ClassLoaderLocal;
import org.apache.yoko.util.PrivilegedActions;
import org.omg.CORBA.ORB;

import java.security.AccessController;
import java.util.Properties;


public final class PortableRemoteObjectExtImpl implements PortableRemoteObjectExtDelegate {

    private static ClassLoaderLocal rmiState = new ClassLoaderLocal(RMIState.class.getName());

    private static ORB defaultOrb;

    private static ORB getDefaultOrb() {
        if (defaultOrb == null) {
            Properties props = AccessController.doPrivileged(PrivilegedActions.GET_SYSPROPS);
            defaultOrb = ORB.init(new String[0], props);
        }
        return defaultOrb;
    }

    private static int nextRMIStateId = 0;

    public PortableRemoteObjectState getCurrentState() {
        RMIState state = (RMIState) rmiState.get();
        if (state == null) {
            ORB orb = getDefaultOrb();
            state = new RMIState(orb, "rmi" + nextRMIStateId++);
            rmiState.set(state);
        }
        return state;
    }
}


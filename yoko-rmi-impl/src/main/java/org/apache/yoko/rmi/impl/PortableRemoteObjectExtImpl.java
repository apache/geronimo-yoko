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
package org.apache.yoko.rmi.impl;

import org.apache.yoko.rmi.api.PortableRemoteObjectExtDelegate;
import org.apache.yoko.util.PrivilegedActions;
import org.omg.CORBA.ORB;

import java.security.AccessController;
import java.util.WeakHashMap;

public final class PortableRemoteObjectExtImpl implements PortableRemoteObjectExtDelegate {
    private enum Holder {
        ;
        private static final ORB DEFAULT_ORB = ORB.init(new String[0], AccessController.doPrivileged(PrivilegedActions.GET_SYSPROPS));
    }

    private static int nextId = 0;
    private static final WeakHashMap<ClassLoader, RMIState> statePerLoader = new WeakHashMap<>();
    private static final RMIState nullLoaderRMIState = new RMIState(Holder.DEFAULT_ORB, "rmi" + nextId++);

    public RMIState getCurrentState() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (null == loader) return nullLoaderRMIState;
        synchronized (statePerLoader) {
            RMIState result = statePerLoader.get(loader);
            if (result == null) {
                result = new RMIState(Holder.DEFAULT_ORB, "rmi" + nextId++);
                statePerLoader.put(loader, result);
            }
            return result;
        }
    }
}


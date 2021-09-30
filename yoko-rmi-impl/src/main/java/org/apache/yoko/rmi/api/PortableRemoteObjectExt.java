/**
*
* Licensed to the Apache Software Foundation (ASF) under one or more
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

package org.apache.yoko.rmi.api;

import org.apache.yoko.osgi.ProviderLocator;

import static java.security.AccessController.doPrivileged;
import static org.apache.yoko.util.PrivilegedActions.GET_CONTEXT_CLASS_LOADER;
import static org.apache.yoko.util.PrivilegedActions.getNoArgInstance;
import static org.apache.yoko.util.PrivilegedActions.getSysProp;

public class PortableRemoteObjectExt {
    private static final class DelegateHolder {
        private static final PortableRemoteObjectExtDelegate delegate;

        public static final String DELEGATE_KEY = "org.apache.yoko.rmi.PortableRemoteObjectExtClass";

        static {
            Object d = null;
            final ClassLoader contextCl = doPrivileged(GET_CONTEXT_CLASS_LOADER);
            try {
                d = ProviderLocator.getService(DELEGATE_KEY, PortableRemoteObjectExt.class, contextCl);
                if (null == d) {
                    String name = doPrivileged(getSysProp(DELEGATE_KEY, "org.apache.yoko.rmi.impl.PortableRemoteObjectExtImpl"));

                    d = doPrivileged(getNoArgInstance(ProviderLocator.loadClass(name, PortableRemoteObjectExt.class, contextCl)));
                }
            } catch (Exception e) {
                throw new RuntimeException("internal problem: " + e.getMessage(), e);
            } finally {
                delegate = (PortableRemoteObjectExtDelegate)d;
            }
        }
    }

    /** Return the currently active state for this thread */
    public static PortableRemoteObjectState getState() {
        return DelegateHolder.delegate.getCurrentState();
    }
}

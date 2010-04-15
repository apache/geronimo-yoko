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

import java.security.AccessController;

import org.apache.yoko.osgi.ProviderLocator;
import org.apache.yoko.rmi.util.GetSystemPropertyAction;

public class PortableRemoteObjectExt {

    private static PortableRemoteObjectExtDelegate delegate;

    private static void init() {
        if (delegate != null)
            return;

        try {
            delegate = (PortableRemoteObjectExtDelegate) ProviderLocator.getService("org.apache.yoko.rmi.PortableRemoteObjectExtClass", PortableRemoteObjectExt.class, Thread.currentThread().getContextClassLoader());
        } catch (Exception ex) {
            throw new RuntimeException("internal problem: " + ex.getMessage(), ex);
        }

        if (delegate == null) {
            String name = (String)AccessController.doPrivileged(new GetSystemPropertyAction(
                    "org.apache.yoko.rmi.PortableRemoteObjectExtClass",
                    "org.apache.yoko.rmi.impl.PortableRemoteObjectExtImpl"));

            try {
                delegate = (PortableRemoteObjectExtDelegate)ProviderLocator.loadClass(name, PortableRemoteObjectExt.class, Thread.currentThread().getContextClassLoader()).newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    /** Return the currently active state for this thread */
    public static PortableRemoteObjectState getState() {
        init();
        return delegate.getCurrentState();
    }
}

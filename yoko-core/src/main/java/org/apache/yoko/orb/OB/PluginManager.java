/*
 * Copyright 2022 IBM Corporation and others.
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

import org.apache.yoko.orb.CORBA.ORB;
import org.apache.yoko.orb.OCI.Plugin;
import org.apache.yoko.orb.OCI.PluginInit;
import org.omg.CORBA.StringSeqHolder;
import org.omg.CORBA.SystemException;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static java.security.AccessController.doPrivileged;
import static java.util.logging.Level.SEVERE;
import static org.apache.yoko.osgi.ProviderLocator.loadClass;
import static org.apache.yoko.util.PrivilegedActions.GET_CONTEXT_CLASS_LOADER;
import static org.apache.yoko.util.PrivilegedActions.getNoArgConstructor;

public final class PluginManager {
    private ORB orb;
    private final Map<String,Plugin> pluginMap = new HashMap<>();

    public PluginManager(org.omg.CORBA.ORB orb) {
        this.orb = (ORB)orb;
    }

    public void destroy() {
        pluginMap.clear();
        orb = null;
    }

    public Plugin initPlugin(String name, StringSeqHolder args) {
        return pluginMap.computeIfAbsent(name, n -> createPlugin(n, args));
    }

    private Plugin createPlugin(String name, StringSeqHolder args) {
        final String className = orb.properties().getProperty("yoko.oci.plugin." + name, "org.apache.yoko.orb.OCI." + name);
        try {
            Class<? extends PluginInit> c = loadClass(className, this.getClass(), doPrivileged(GET_CONTEXT_CLASS_LOADER));
            PluginInit pi = doPrivileged(getNoArgConstructor(c)).newInstance();
            pi.version(orb, org.apache.yoko.orb.OCI.Version.value);
            return pi.init(orb, args);
        } catch (SystemException ex) {
            throw ex;
        } catch (Exception ex) {
            orb.logger().log(SEVERE, format("unable to load OCI plug-in `%s':%n%s", name, ex.getMessage()), ex);
            return null;
        }
    }
}

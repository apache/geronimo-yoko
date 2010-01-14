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

import org.apache.yoko.orb.OB.Logger;

public final class PluginManager {
    //
    // The ORB
    //
    private org.omg.CORBA.ORB orb_;

    //
    // Plug-in data
    //
    static class PluginData {
        public String name;

        public org.apache.yoko.orb.OCI.Plugin plugin;

        PluginData(String name, org.apache.yoko.orb.OCI.Plugin plugin) {
            this.name = name;
            this.plugin = plugin;
        }
    }

    //
    // The set of registered plug-ins
    //
    private java.util.Vector plugins_ = new java.util.Vector();

    private boolean destroy_; // True if destroy() was called

    // ----------------------------------------------------------------------
    // PluginManager public member implementations
    // ----------------------------------------------------------------------

    public PluginManager(org.omg.CORBA.ORB orb) {
        orb_ = orb;
        destroy_ = false;
    }

    //
    // Destroy the PluginManager
    //
    public void destroy() {
        Assert._OB_assert(!destroy_); // May only be destroyed once
        destroy_ = true;

        //
        // Destroy the plug-ins
        //
        plugins_.removeAllElements();

        //
        // Eliminate circular reference
        //
        orb_ = null;
    }

    //
    // Initialize the plug-in with the given name. The plug-in may be
    // loaded dynamically. If the plug-in could not be initialized,
    // nil is returned.
    //
    public org.apache.yoko.orb.OCI.Plugin initPlugin(String name,
            org.omg.CORBA.StringSeqHolder args) {
        org.apache.yoko.orb.OCI.Plugin result = null;

        for (int i = 0; i < plugins_.size(); i++) {
            PluginData data = (PluginData) plugins_.elementAt(i);
            if (name.equals(data.name)) {
                result = data.plugin;
                break;
            }
        }

        if (result == null) {
            //
            // Try to load the plug-in dynamically
            //

            String className;

            //
            // First check the properties to see if there is a property
            // with the name yoko.oci.plugin.<name>, which specifies the
            // class from which this plug-in should be loaded
            //
            org.apache.yoko.orb.CORBA.ORB oborb = (org.apache.yoko.orb.CORBA.ORB) orb_;
            java.util.Properties props = oborb.properties();
            String propName = "yoko.oci.plugin." + name;
            className = props.getProperty(propName);

            if (className == null) {
                //
                // No property was found, so compose the class name using
                // a standard format: org.apache.yoko.orb.OCI.<name>
                //
                className = "org.apache.yoko.orb.OCI." + name;
            }

            Logger logger = oborb.logger();

            //
            // Load the class
            //
            org.apache.yoko.orb.OCI.PluginInit pi = null;
            try {
                // get the appropriate class for the loading.
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                if (loader == null) {
                    loader = this.getClass().getClassLoader();
                }

                Class c = loader.loadClass(className);
                pi = (org.apache.yoko.orb.OCI.PluginInit) c.newInstance();
            } catch (org.omg.CORBA.SystemException ex) {
                throw ex;
            } catch (Exception ex) {
                String err = "unable to load OCI plug-in `" + name + "':\n"
                        + ex.getMessage();
                logger.error(err, ex);
                return null;
            }

            //
            // Invoke the version function, which allows the plug-in
            // to verify that it is compatible with the OCI version
            // in use by the ORB
            //
            pi.version(orb_, org.apache.yoko.orb.OCI.Version.value);

            //
            // Invoke the initialization function
            //
            result = pi.init(orb_, args);
            if (result != null)
                plugins_.addElement(new PluginData(name, result));
        }

        return result;
    }
}

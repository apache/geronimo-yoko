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

package org.apache.yoko.orb.OBPortableServer;

class DefaultServantHolder {
    private boolean destroyed_;

    org.omg.PortableServer.Servant servant_;

    //
    // Destroy the default servant
    //
    synchronized void destroy() {
        servant_ = null;
    }

    //
    // Set the default servant
    //
    synchronized public void setDefaultServant(
            org.omg.PortableServer.Servant servant) {
        servant_ = servant;
    }

    //
    // Retrieve the default servant
    //
    synchronized public org.omg.PortableServer.Servant getDefaultServant() {
        return servant_;
    }

    //
    // Retrieve the ObjectId associated with the servant, if necessary
    //
    synchronized byte[] servantToId(org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.PortableServer.Current_impl poaCurrent) {
        if (servant != servant_)
            return null;

        if (poaCurrent._OB_inUpcall() && poaCurrent._OB_getServant() == servant) {
            try {
                return poaCurrent.get_object_id();
            } catch (org.omg.PortableServer.CurrentPackage.NoContext ex) {
                org.apache.yoko.orb.OB.Assert._OB_assert(ex); // TODO:
                                                                    // Internal
                                                                    // error
            }
        }
        return null;
    }
}

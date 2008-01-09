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

interface ServantLocationStrategy {
    //
    // Destroy the ServantLocatioStrategy
    //
    void destroy(org.omg.PortableServer.POA poa, boolean etherealize);

    //
    // Etherealize the ServantLocatioStrategy
    //
    void etherealize(org.omg.PortableServer.POA poa);

    //
    // Register the servant with the ObjectId
    //
    void activate(byte[] oid, org.omg.PortableServer.Servant servant)
            throws org.omg.PortableServer.POAPackage.ServantAlreadyActive,
            org.omg.PortableServer.POAPackage.WrongPolicy,
            org.omg.PortableServer.POAPackage.ObjectAlreadyActive;

    //
    // Unregister the servant with the ObjectId
    //
    void deactivate(org.omg.PortableServer.POA poa, byte[] oid)
            throws org.omg.PortableServer.POAPackage.ObjectNotActive,
            org.omg.PortableServer.POAPackage.WrongPolicy;

    //
    // Return the ObjectId associated with the Servant
    //
    byte[] servantToId(org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.PortableServer.Current_impl poaCurrent);

    //
    // Return the Servant associated with the ObjectId
    //
    org.omg.PortableServer.Servant idToServant(byte[] oid,
            boolean useDefaultServant);

    //
    // Locate the servant with the ObjectId
    //
    org.omg.PortableServer.Servant locate(byte[] oid,
            org.omg.PortableServer.POA poa, String op,
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder cookie)
            throws org.apache.yoko.orb.OB.LocationForward;

    //
    // About to call a method on the Servant
    //
    void preinvoke(byte[] oid);

    //
    // Completed calling a method on the Servant
    //
    void postinvoke(byte[] oid, org.omg.PortableServer.POA poa, String op,
            java.lang.Object cookie, org.omg.PortableServer.Servant servant);

    //
    // Create a DirectStubImpl for the ObjectId
    //
    DirectServant createDirectStubImpl(org.omg.PortableServer.POA poa,
            byte[] oid, org.apache.yoko.orb.OB.RefCountPolicyList policies)
            throws org.apache.yoko.orb.OB.LocationForward;

    //
    // Destroy a DirectStubImpl for the ObjectId
    //
    void removeDirectStubImpl(byte[] oid, DirectServant directServant);

    //
    // Retrieve the servant manager strategy
    //
    ServantManagerStrategy getServantManagerStrategy();

    //
    // Retrieve the default servant holder
    //
    DefaultServantHolder getDefaultServantHolder();
}

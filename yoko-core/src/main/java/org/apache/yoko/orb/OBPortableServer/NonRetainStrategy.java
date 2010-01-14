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

//
// Strategy for NON_RETAIN and USE_SERVANT_MANAGER or
// USE_DEFAULT_SERVANT
//
class NonRetainStrategy implements ServantLocationStrategy {
    private ServantLocatorStrategy servantManager_;

    private DefaultServantHolder defaultServant_;

    NonRetainStrategy(ServantLocatorStrategy servantManager,
            DefaultServantHolder defaultServant) {
        servantManager_ = servantManager;
        defaultServant_ = defaultServant;
    }

    public void destroy(org.omg.PortableServer.POA poa, boolean etherealize) {
        if (servantManager_ != null)
            servantManager_.destroy();
        if (defaultServant_ != null)
            defaultServant_.destroy();
    }

    public void etherealize(org.omg.PortableServer.POA poa) {
        // Do nothing
    }

    public void activate(byte[] oid, org.omg.PortableServer.Servant servant)
            throws org.omg.PortableServer.POAPackage.ServantAlreadyActive,
            org.omg.PortableServer.POAPackage.WrongPolicy,
            org.omg.PortableServer.POAPackage.ObjectAlreadyActive {
        //
        // Requires the RETAIN policy.
        //
        throw new org.omg.PortableServer.POAPackage.WrongPolicy();
    }

    public void deactivate(org.omg.PortableServer.POA poa, byte[] oid)
            throws org.omg.PortableServer.POAPackage.ObjectNotActive,
            org.omg.PortableServer.POAPackage.WrongPolicy {
        //
        // Requires the RETAIN policy.
        //
        throw new org.omg.PortableServer.POAPackage.WrongPolicy();
    }

    public byte[] servantToId(org.omg.PortableServer.Servant servant,
            org.apache.yoko.orb.PortableServer.Current_impl poaCurrent) {
        if (defaultServant_ != null)
            return defaultServant_.servantToId(servant, poaCurrent);
        return null;
    }

    public org.omg.PortableServer.Servant idToServant(byte[] oid,
            boolean useDefaultServant) {
        if (defaultServant_ != null)
            return defaultServant_.getDefaultServant();
        return null;
    }

    public org.omg.PortableServer.Servant locate(byte[] oid,
            org.omg.PortableServer.POA poa, String op,
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder cookie)
            throws org.apache.yoko.orb.OB.LocationForward {
        if (defaultServant_ != null) {
            org.omg.PortableServer.Servant servant = defaultServant_
                    .getDefaultServant();
            if (servant == null)
                throw new org.omg.CORBA.OBJ_ADAPTER(
                        org.apache.yoko.orb.OB.MinorCodes
                                .describeObjAdapter(org.apache.yoko.orb.OB.MinorCodes.MinorNoDefaultServant),
                        org.apache.yoko.orb.OB.MinorCodes.MinorNoDefaultServant,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            return servant;
        }

        if (servantManager_ == null)
            throw new org.omg.CORBA.OBJ_ADAPTER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeObjAdapter(org.apache.yoko.orb.OB.MinorCodes.MinorNoServantManager),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoServantManager,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        return servantManager_.preinvoke(oid, poa, op, cookie);
    }

    public void preinvoke(byte[] oid) {
    }

    public void postinvoke(byte[] oid, org.omg.PortableServer.POA poa,
            String op, java.lang.Object cookie,
            org.omg.PortableServer.Servant servant) {
        if (servantManager_ != null)
            servantManager_.postinvoke(oid, poa, op, cookie, servant);
    }

    public DirectServant createDirectStubImpl(org.omg.PortableServer.POA poa,
            byte[] oid, org.apache.yoko.orb.OB.RefCountPolicyList policies)
            throws org.apache.yoko.orb.OB.LocationForward {
        return null;
    }

    public void removeDirectStubImpl(byte[] oid, DirectServant servant) {
    }

    public ServantManagerStrategy getServantManagerStrategy() {
        return servantManager_;
    }

    public DefaultServantHolder getDefaultServantHolder() {
        return defaultServant_;
    }
}

/*
 * Copyright 2010 IBM Corporation and others.
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
package org.apache.yoko.orb.OBPortableServer;

class ServantLocationStrategyFactory {
    static public ServantLocationStrategy createServantLocationStrategy(
            org.apache.yoko.orb.OBPortableServer.POAPolicies policies,
            org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        if (policies.requestProcessingPolicy() == org.omg.PortableServer.RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY)
            return new ActiveObjectOnlyStrategy(policies, orbInstance);

        ServantLocatorStrategy servantLocator = null;
        ServantActivatorStrategy servantActivator = null;
        DefaultServantHolder defaultServant = null;

        if (policies.requestProcessingPolicy() == org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT)
            defaultServant = new DefaultServantHolder();

        if (policies.requestProcessingPolicy() == org.omg.PortableServer.RequestProcessingPolicyValue.USE_SERVANT_MANAGER) {
            if (policies.servantRetentionPolicy() == org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN) {
                servantActivator = new ServantActivatorStrategy();
            } else {
                servantLocator = new ServantLocatorStrategy(orbInstance);
            }
        }

        if (policies.servantRetentionPolicy() == org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN)
            return new RetainStrategy(policies, orbInstance, servantActivator,
                    defaultServant);
        // NON_RETAIN
        return new NonRetainStrategy(servantLocator, defaultServant);
    }
}

/*
 * Copyright 2021 IBM Corporation and others.
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

import org.apache.yoko.util.MinorCodes;

//
// Strategy for ServantActivators
//
class ServantLocatorStrategy implements ServantManagerStrategy {
    //
    // The ORBInstance
    //
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    //
    // The servant locator
    //
    private org.omg.PortableServer.ServantLocator servantLocator_;

    public ServantLocatorStrategy(org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    synchronized public void destroy() {
        servantLocator_ = null;
    }

    synchronized public void setServantManager(
            org.omg.PortableServer.ServantManager manager) {
        //
        // Attempting to set the servant manager after one has already
        // been set will result in the BAD_INV_ORDER exception being
        // raised.
        //
        if (servantLocator_ != null) {
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    MinorCodes
                            .describeBadInvOrder(MinorCodes.MinorServantManagerAlreadySet),
                    MinorCodes.MinorServantManagerAlreadySet,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
        try {
            servantLocator_ = org.omg.PortableServer.ServantLocatorHelper
                    .narrow(manager);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
        }

        if (servantLocator_ == null) {
            throw new org.omg.CORBA.OBJ_ADAPTER(
                    MinorCodes
                            .describeObjAdapter(MinorCodes.MinorNoServantManager),
                    MinorCodes.MinorNoServantManager,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
    }

    synchronized public org.omg.PortableServer.ServantManager getServantManager() {
        return servantLocator_;
    }

    public org.omg.PortableServer.Servant preinvoke(byte[] oid,
            org.omg.PortableServer.POA poa, String op,
            org.omg.PortableServer.ServantLocatorPackage.CookieHolder cookie)
            throws org.apache.yoko.orb.OB.LocationForward {
        org.omg.PortableServer.ServantLocator locator;

        synchronized (this) {
            //
            // If no servant manager has been associated with the POA,
            // OBJ_ADAPTER is raised
            //
            if (servantLocator_ == null)
                throw new org.omg.CORBA.OBJ_ADAPTER(
                        MinorCodes
                                .describeObjAdapter(MinorCodes.MinorNoServantManager),
                        MinorCodes.MinorNoServantManager,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);

            locator = servantLocator_;
        }

        org.omg.PortableServer.Servant servant;

        try {
            servant = locator.preinvoke(oid, poa, op, cookie);
        } catch (org.omg.PortableServer.ForwardRequest ex) {
            org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) ex.forward_reference)
                    ._get_delegate());
            org.omg.IOP.IOR ior = p._OB_IOR();
            throw new org.apache.yoko.orb.OB.LocationForward(ior, false);
        }

        //
        // 11-23:
        //
        // If a ServantManager returns a null Servant (or the
        // equivalent in a language mapping) as the result of an
        // incarnate() or preinvoke() operation, the POA will return
        // the OBJ_ADAPTER system exception as the result of the
        // request.
        //
        if (servant == null) {
            throw new org.omg.CORBA.OBJ_ADAPTER(
                    MinorCodes
                            .describeObjAdapter(MinorCodes.MinorServantNotFound),
                    MinorCodes.MinorServantNotFound,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        //
        // In Java, the servant needs to be associated with the ORB
        // (i.e., to have a delegate)
        //
        ((org.omg.CORBA_2_3.ORB) orbInstance_.getORB()).set_delegate(servant);

        return servant;
    }

    public void postinvoke(byte[] oid, org.omg.PortableServer.POA poa,
            String op, java.lang.Object cookie,
            org.omg.PortableServer.Servant servant) {
        org.omg.PortableServer.ServantLocator locator;

        synchronized (this) {
            if (servantLocator_ == null) {
                return;
            }
            locator = servantLocator_;
        }

        locator.postinvoke(oid, poa, op, cookie, servant);
    }
}

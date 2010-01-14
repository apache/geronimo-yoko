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
// Strategy for ServantActivators
//
class ServantActivatorStrategy implements ServantManagerStrategy {
    //
    // The servant activator
    //
    org.omg.PortableServer.ServantActivator servantActivator_;

    synchronized public void destroy() {
        servantActivator_ = null;
    }

    synchronized public void setServantManager(
            org.omg.PortableServer.ServantManager manager) {
        //
        // Attempting to set the servant manager after one has already
        // been set will result in the BAD_INV_ORDER exception being
        // raised.
        //
        if (servantActivator_ != null)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorServantManagerAlreadySet),
                    org.apache.yoko.orb.OB.MinorCodes.MinorServantManagerAlreadySet,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        try {
            servantActivator_ = org.omg.PortableServer.ServantActivatorHelper
                    .narrow(manager);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
        }

        if (servantActivator_ == null) {
            throw new org.omg.CORBA.OBJ_ADAPTER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeObjAdapter(org.apache.yoko.orb.OB.MinorCodes.MinorNoServantManager),
                    org.apache.yoko.orb.OB.MinorCodes.MinorNoServantManager,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }
    }

    synchronized public org.omg.PortableServer.ServantManager getServantManager() {
        return servantActivator_;
    }

    //
    // Note that the synchronization in these methods ensures that
    // concurrent calls to the incarnate and etherealize do not occur
    //
    synchronized public org.omg.PortableServer.Servant incarnate(byte[] oid,
            org.omg.PortableServer.POA poa)
            throws org.apache.yoko.orb.OB.LocationForward {
        //
        // If no servant manager has been associated with the POA,
        // OBJ_ADAPTER is raised
        //
        if (servantActivator_ == null)
            throw new org.omg.CORBA.BAD_INV_ORDER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeBadInvOrder(org.apache.yoko.orb.OB.MinorCodes.MinorServantManagerAlreadySet),
                    org.apache.yoko.orb.OB.MinorCodes.MinorServantManagerAlreadySet,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        org.omg.PortableServer.Servant servant;

        try {
            servant = servantActivator_.incarnate(oid, poa);
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
        if (servant == null)
            throw new org.omg.CORBA.OBJ_ADAPTER(
                    org.apache.yoko.orb.OB.MinorCodes
                            .describeObjAdapter(org.apache.yoko.orb.OB.MinorCodes.MinorServantNotFound),
                    org.apache.yoko.orb.OB.MinorCodes.MinorServantNotFound,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        return servant;
    }

    synchronized public void etherealize(byte[] oid,
            org.omg.PortableServer.POA poa,
            org.omg.PortableServer.Servant servant, boolean cleanup,
            boolean remaining) {
        if (servantActivator_ != null) {
            try {
                servantActivator_.etherealize(oid, poa, servant, cleanup,
                        remaining);
            } catch (org.omg.CORBA.SystemException ex) {
                // Ignore
            }
        }
    }
}

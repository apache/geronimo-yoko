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
 
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OB.IORUtil;

//
// We don't need any sort of concurrency protection on this class
// since its lifecycle is tightly coupled with the POAManager. Once
// the POAManager destroys this class it should not be possible for
// any further requests to arrive (or any requests in the process of
// being dispatched).
//
final class POAOAInterface_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OB.OAInterface {
    static final Logger logger = Logger.getLogger(POAOAInterface_impl.class.getName());
    //
    // The ORBInstance
    //
    private org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    //
    // The POAManager implementation
    //
    private POAManager_impl poaManager_;

    //
    // The boot manager implementation
    //
    private org.apache.yoko.orb.OB.BootManager_impl bootManagerImpl_;

    //
    // Is the POAOAInterface discarding requests?
    //
    private boolean discard_;

    // ----------------------------------------------------------------------
    // Package member implementation
    // ----------------------------------------------------------------------

    POAOAInterface_impl(POAManager_impl poaManager,
            org.apache.yoko.orb.OB.ORBInstance orbInstance) {
        poaManager_ = poaManager;
        orbInstance_ = orbInstance;
        bootManagerImpl_ = (org.apache.yoko.orb.OB.BootManager_impl) orbInstance
                .getBootManager();
    }

    // ----------------------------------------------------------------------
    // Public member implementation
    // ----------------------------------------------------------------------

    public org.apache.yoko.orb.OB.Upcall createUpcall(
            org.apache.yoko.orb.OB.UpcallReturn upcallReturn,
            org.apache.yoko.orb.OCI.ProfileInfo profileInfo,
            org.apache.yoko.orb.OCI.TransportInfo transportInfo, int requestId,
            String op, org.apache.yoko.orb.CORBA.InputStream in,
            org.omg.IOP.ServiceContext[] requestSCL) {
        org.apache.yoko.orb.OB.Upcall upcall = null;
        logger.fine("Creating upcall for operation " + op); 
        try {
            //
            // If discarding then throw a TRANSIENT exception
            //
            if (discard_) {
                throw new org.omg.CORBA.TRANSIENT(
                        "Requests are being discarded", 0,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
            }

            org.apache.yoko.orb.OB.ObjectKeyData data = new org.apache.yoko.orb.OB.ObjectKeyData();
            if (org.apache.yoko.orb.OB.ObjectKey.ParseObjectKey(profileInfo.key, data)) {
                while (true) {
                    //
                    // Locate the POA. This may also throw a TRANSIENT
                    // exception if the POA manager is discarding.
                    //
                    org.omg.PortableServer.POA poa = poaManager_._OB_locatePOA(data);
                    if (poa != null) {
                        logger.fine("Unable to locate POA " + data + " using POAManager " + poaManager_.get_id()); 
                        POA_impl poaImpl = (POA_impl) poa;
                        upcall = poaImpl._OB_createUpcall(data.oid,
                                upcallReturn, profileInfo, transportInfo,
                                requestId, op, in, requestSCL);
                        //
                        // If _OB_createUpcall returns a nil Upcall object
                        // then we should retry since that means that the
                        // POA is being destroyed
                        //
                        if (upcall == null) {
                            continue;
                        }
                    }
                    break;
                }
            } else if (upcallReturn != null) {
                logger.fine("Error parsing object key data"); 
                //
                // Check to see if the BootManager knows of a reference
                // for the ObjectKey. If so, forward the request.
                //
                org.omg.IOP.IOR ior = bootManagerImpl_._OB_locate(profileInfo.key);
                if (ior != null) {
                    throw new org.apache.yoko.orb.OB.LocationForward(ior, false);
                }
            }
            //
            // If no upcall has been created then the object simply
            // doesn't exist
            //
            if (upcall == null) {
                if (op.equals("_non_existent") || op.equals("_not_existent")) {
                    upcall = new org.apache.yoko.orb.OB.Upcall(orbInstance_,
                            upcallReturn, profileInfo, transportInfo,
                            requestId, op, in, requestSCL);
                    upcall.preUnmarshal();
                    upcall.postUnmarshal();
                    upcall.postinvoke();
                    org.omg.CORBA.portable.OutputStream out = upcall.preMarshal();
                    out.write_boolean(true);
                    upcall.postMarshal();
                } 
                else {
                    throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                            org.apache.yoko.orb.OB.MinorCodes
                                    .describeObjectNotExist(org.apache.yoko.orb.OB.MinorCodes.MinorCannotDispatch),
                            org.apache.yoko.orb.OB.MinorCodes.MinorCannotDispatch,
                            org.omg.CORBA.CompletionStatus.COMPLETED_NO);
                }
            }
        } catch (org.omg.CORBA.SystemException ex) {
            logger.log(Level.FINE, "System exception creating upcall", ex); 
            upcall = new org.apache.yoko.orb.OB.Upcall(orbInstance_,
                    upcallReturn, profileInfo, transportInfo, requestId, op,
                    in, requestSCL);
            upcall.setSystemException(ex);
        } catch (org.apache.yoko.orb.OB.LocationForward ex) {
            logger.log(Level.FINE, "Location forward request creating upcall.", ex); 
            upcall = new org.apache.yoko.orb.OB.Upcall(orbInstance_,
                    upcallReturn, profileInfo, transportInfo, requestId, op,
                    in, requestSCL);
            upcall.setLocationForward(ex.ior, ex.perm);
        }

        org.apache.yoko.orb.OB.Assert._OB_assert(upcall != null);
        return upcall;
    }

    public int findByKey(byte[] key, org.omg.IOP.IORHolder ior) {
        org.apache.yoko.orb.OB.ObjectKeyData data = new org.apache.yoko.orb.OB.ObjectKeyData();
        if (org.apache.yoko.orb.OB.ObjectKey.ParseObjectKey(key, data)) {
            try {
                logger.fine("Locate request for object key " + data);  
                
                org.omg.PortableServer.POA poa = poaManager_._OB_locatePOA(data);
                if (poa != null) {
                    POA_impl poaImpl = (POA_impl) poa;
                    poaImpl._OB_locateServant(data.oid);
                    return org.apache.yoko.orb.OB.OAInterface.OBJECT_HERE;
                }
            } catch (org.omg.CORBA.SystemException ex) {
            } catch (org.apache.yoko.orb.OB.LocationForward fwd) {
                ior.value = fwd.ior;
                return (fwd.perm) ? org.apache.yoko.orb.OB.OAInterface.OBJECT_FORWARD_PERM
                        : org.apache.yoko.orb.OB.OAInterface.OBJECT_FORWARD;
            }
        } else {
            //
            // Check to see if the BootManager knows of a reference
            // for the ObjectKey.
            //
            logger.fine("Checking boot manager for object with key " + data);  
            ior.value = bootManagerImpl_._OB_locate(key);
            if (ior.value != null) {
                return org.apache.yoko.orb.OB.OAInterface.OBJECT_FORWARD;
            }
        }
        return org.apache.yoko.orb.OB.OAInterface.UNKNOWN_OBJECT;
    }

    public org.apache.yoko.orb.OCI.ProfileInfo[] getUsableProfiles(
            org.omg.IOP.IOR ior, org.omg.CORBA.Policy[] policies) {
        try {
            org.apache.yoko.orb.OCI.Acceptor[] acceptors = poaManager_
                    .get_acceptors();

            java.util.Vector seq = new java.util.Vector();
            for (int i = 0; i < acceptors.length; i++) {
                org.apache.yoko.orb.OCI.ProfileInfo[] seq2 = acceptors[i]
                        .get_local_profiles(ior);

                for (int j = 0; j < seq2.length; j++)
                    seq.addElement(seq2[j]);
            }

            org.apache.yoko.orb.OCI.ProfileInfo[] result = new org.apache.yoko.orb.OCI.ProfileInfo[seq
                    .size()];
            seq.copyInto(result);
            return result;
        } catch (org.omg.PortableServer.POAManagerPackage.AdapterInactive ex) {
            org.apache.yoko.orb.OB.Assert._OB_assert(ex);
            return null;
        }
    }

    //
    // Discard all incoming requests with a TRANSIENT exception
    //
    // ASYNC SAFE
    //
    public void discard() {
        discard_ = true;
    }

    //
    // Allow associated POAs to receive requests
    //
    public void activate() {
        discard_ = false;
    }
}

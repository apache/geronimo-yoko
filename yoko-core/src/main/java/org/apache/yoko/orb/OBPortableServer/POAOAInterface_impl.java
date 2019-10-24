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

import org.apache.yoko.orb.CORBA.InputStream;
import org.apache.yoko.orb.IOP.ServiceContexts;
import org.apache.yoko.orb.OB.Assert;
import org.apache.yoko.orb.OB.BootManager_impl;
import org.apache.yoko.orb.OB.LocationForward;
import org.apache.yoko.orb.OB.MinorCodes;
import org.apache.yoko.orb.OB.OAInterface;
import org.apache.yoko.orb.OB.ORBInstance;
import org.apache.yoko.orb.OB.ObjectKey;
import org.apache.yoko.orb.OB.ObjectKeyData;
import org.apache.yoko.orb.OB.Upcall;
import org.apache.yoko.orb.OB.UpcallReturn;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.ProfileInfo;
import org.apache.yoko.orb.OCI.TransportInfo;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.IOR;
import org.omg.IOP.IORHolder;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

//
// We don't need any sort of concurrency protection on this class
// since its lifecycle is tightly coupled with the POAManager. Once
// the POAManager destroys this class it should not be possible for
// any further requests to arrive (or any requests in the process of
// being dispatched).
//
final class POAOAInterface_impl extends LocalObject implements OAInterface {
    static final Logger logger = Logger.getLogger(POAOAInterface_impl.class.getName());
    //
    // The ORBInstance
    //
    private ORBInstance orbInstance_;

    //
    // The POAManager implementation
    //
    private POAManager_impl poaManager_;

    //
    // The boot manager implementation
    //
    private BootManager_impl bootManagerImpl_;

    //
    // Is the POAOAInterface discarding requests?
    //
    private boolean discard_;

    // ----------------------------------------------------------------------
    // Package member implementation
    // ----------------------------------------------------------------------

    POAOAInterface_impl(POAManager_impl poaManager,
            ORBInstance orbInstance) {
        poaManager_ = poaManager;
        orbInstance_ = orbInstance;
        bootManagerImpl_ = (BootManager_impl) orbInstance
                .getBootManager();
    }

    // ----------------------------------------------------------------------
    // Public member implementation
    // ----------------------------------------------------------------------

    public Upcall createUpcall(
            UpcallReturn upcallReturn,
            ProfileInfo profileInfo,
            TransportInfo transportInfo, int requestId,
            String op, InputStream in,
            ServiceContexts requestContexts) {
        Upcall upcall = null;
        logger.fine("Creating upcall for operation " + op); 
        try {
            //
            // If discarding then throw a TRANSIENT exception
            //
            if (discard_) {
                throw new TRANSIENT(
                        "Requests are being discarded", 0,
                        CompletionStatus.COMPLETED_NO);
            }

            ObjectKeyData data = new ObjectKeyData();
            if (ObjectKey.ParseObjectKey(profileInfo.key, data)) {
                while (true) {
                    //
                    // Locate the POA. This may also throw a TRANSIENT
                    // exception if the POA manager is discarding.
                    //
                    org.omg.PortableServer.POA poa = poaManager_._OB_locatePOA(data);
                    if (poa != null) {
                        logger.fine("Unable to locate POA " + data + " using POAManager " + poaManager_.get_id()); 
                        POA_impl poaImpl = (POA_impl) poa;
                        upcall = poaImpl._OB_createUpcall(data.oid, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts);
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
                IOR ior = bootManagerImpl_._OB_locate(profileInfo.key);
                if (ior != null) {
                    throw new LocationForward(ior, false);
                }
            }
            //
            // If no upcall has been created then the object simply
            // doesn't exist
            //
            if (upcall == null) {
                if (op.equals("_non_existent") || op.equals("_not_existent")) {
                    upcall = new Upcall(orbInstance_, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts);
                    upcall.preUnmarshal();
                    upcall.postUnmarshal();
                    upcall.postinvoke();
                    OutputStream out = upcall.preMarshal();
                    out.write_boolean(true);
                    upcall.postMarshal();
                } 
                else {
                    throw new OBJECT_NOT_EXIST(
                            MinorCodes
                                    .describeObjectNotExist(MinorCodes.MinorCannotDispatch),
                            MinorCodes.MinorCannotDispatch,
                            CompletionStatus.COMPLETED_NO);
                }
            }
        } catch (SystemException ex) {
            logger.log(Level.FINE, "System exception creating upcall", ex); 
            upcall = new Upcall(orbInstance_, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts);
            upcall.setSystemException(ex);
        } catch (LocationForward ex) {
            logger.log(Level.FINE, "Location forward request creating upcall.", ex); 
            upcall = new Upcall(orbInstance_, upcallReturn, profileInfo, transportInfo, requestId, op, in, requestContexts);
            upcall.setLocationForward(ex.ior, ex.perm);
        }

        Assert._OB_assert(upcall != null);
        return upcall;
    }

    public int findByKey(byte[] key, IORHolder ior) {
        ObjectKeyData data = new ObjectKeyData();
        if (ObjectKey.ParseObjectKey(key, data)) {
            try {
                logger.fine("Locate request for object key " + data);  
                
                org.omg.PortableServer.POA poa = poaManager_._OB_locatePOA(data);
                if (poa != null) {
                    POA_impl poaImpl = (POA_impl) poa;
                    poaImpl._OB_locateServant(data.oid);
                    return OAInterface.OBJECT_HERE;
                }
            } catch (SystemException ex) {
            } catch (LocationForward fwd) {
                ior.value = fwd.ior;
                return (fwd.perm) ? OAInterface.OBJECT_FORWARD_PERM
                        : OAInterface.OBJECT_FORWARD;
            }
        } else {
            //
            // Check to see if the BootManager knows of a reference
            // for the ObjectKey.
            //
            logger.fine("Checking boot manager for object with key " + data);  
            ior.value = bootManagerImpl_._OB_locate(key);
            if (ior.value != null) {
                return OAInterface.OBJECT_FORWARD;
            }
        }
        return OAInterface.UNKNOWN_OBJECT;
    }

    public ProfileInfo[] getUsableProfiles(IOR ior, Policy[] policies) {
        try {
            Acceptor[] acceptors = poaManager_
                    .get_acceptors();

            Vector seq = new Vector();
            for (int i = 0; i < acceptors.length; i++) {
                ProfileInfo[] seq2 = acceptors[i].get_local_profiles(ior);

                for (int j = 0; j < seq2.length; j++)
                    seq.addElement(seq2[j]);
            }

            ProfileInfo[] result = new ProfileInfo[seq
                    .size()];
            seq.copyInto(result);
            return result;
        } catch (AdapterInactive ex) {
            Assert._OB_assert(ex);
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

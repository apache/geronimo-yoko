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

final class POALocator {
    static final Logger logger = Logger.getLogger(POALocator.class.getName());
    
    java.util.Hashtable poas_ = new java.util.Hashtable(63);

    //
    // Locate a POA
    //
    synchronized org.omg.PortableServer.POA locate(
            org.apache.yoko.orb.OB.ObjectKeyData data)
            throws org.apache.yoko.orb.OB.LocationForward {
        logger.fine("Searching for POA " + data); 
        //
        // If length of poa name sequence is zero we are looking
        // for a root POA and are doomed to fail.
        //
        if (data.poaId.length == 0) {
            return null;
        }

        org.omg.PortableServer.POA poa = (org.omg.PortableServer.POA) poas_.get(new POANameHasher(data.poaId));
        if (poa == null) {
            logger.fine("POA not found by direct lookup, searching the hierarchy"); 
            //
            // Arrange for POA activation. The algorithm for this is
            // to find the first POA in the `path' that exists. We
            // then call find_POA on each remaining POA in the path to
            // cause the activation.
            //
            String[] poaId = new String[data.poaId.length];
            System.arraycopy(data.poaId, 0, poaId, 0, data.poaId.length);
            java.util.Vector remaining = new java.util.Vector();
            do {
                remaining.addElement(poaId[poaId.length - 1]);
                String[] newID = new String[poaId.length - 1];
                System.arraycopy(poaId, 0, newID, 0, poaId.length - 1);
                poaId = newID;
                POANameHasher key = new POANameHasher(poaId); 
                logger.fine("Searching POA hierarchy for " + key); 
                poa = (org.omg.PortableServer.POA) poas_.get(key);
                if (poa != null) {
                    logger.fine("Located POA using " + key); 
                    break;
                }
            } while (poaId.length > 0);

            //
            // Now find each POA in the path until we fail to find one of
            // the POA's or we're done.
            //
            for (int i = remaining.size(); i > 0 && poa != null; i--) {
                String key = (String) remaining.elementAt(i - 1); 
                try {
                    logger.fine("Searching up hierarchy using key " + key); 
                    poa = poa.find_POA(key, true);
                } catch (org.omg.PortableServer.POAPackage.AdapterNonExistent ex) {
                    logger.fine("Failure locating POA using key " + key); 
                    poa = null;
                }
            }
        }
        return poa;
    }

    //
    // Add a POA
    //
    synchronized void add(org.omg.PortableServer.POA poa, String[] id) {
        POANameHasher idkey = new POANameHasher(id);
        logger.fine("Adding POA to locater using key " + idkey); 
        org.apache.yoko.orb.OB.Assert._OB_assert(!poas_.containsKey(idkey));
        poas_.put(idkey, poa);
    }

    //
    // Remove a POA
    //
    synchronized void remove(String[] id) {
        POANameHasher idkey = new POANameHasher(id);
        logger.fine("Removing POA from locater using key " + idkey); 
        org.apache.yoko.orb.OB.Assert._OB_assert(poas_.containsKey(idkey));
        poas_.remove(idkey);
    }
}

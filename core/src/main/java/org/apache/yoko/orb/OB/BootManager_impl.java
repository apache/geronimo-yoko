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
 
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.yoko.orb.OB.BootLocator;
import org.apache.yoko.orb.OB.BootManager;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;

final public class BootManager_impl extends org.omg.CORBA.LocalObject implements
        BootManager {
    static final Logger logger = Logger.getLogger(BootManager_impl.class.getName());
    //
    // Set of known bindings
    //
    private java.util.Hashtable bindings_;

    //
    // The Boot Locator. There is no need for the BootLocatorHolder
    // since assign and read methods are atomic in Java.
    //
    private BootLocator locator_ = null;
    
    // the ORB that created us 
    private ORB orb_; 

    public BootManager_impl(ORB orb) {
        bindings_ = new java.util.Hashtable(17);
        orb_ = orb; 
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public void add_binding(byte[] id, org.omg.CORBA.Object obj)
            throws org.apache.yoko.orb.OB.BootManagerPackage.AlreadyExists {
        ObjectIdHasher oid = new ObjectIdHasher(id);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Adding binding under id " + IORUtil.dump_octets(id)); 
        }

        //
        // If binding id is not already mapped add the binding.
        //
        synchronized (bindings_) {
            if (bindings_.containsKey(oid))
                throw new org.apache.yoko.orb.OB.BootManagerPackage.AlreadyExists();

            bindings_.put(oid, obj);
        }
    }

    public void remove_binding(byte[] id)
            throws org.apache.yoko.orb.OB.BootManagerPackage.NotFound {
        ObjectIdHasher oid = new ObjectIdHasher(id);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Removing binding with id " + IORUtil.dump_octets(id)); 
        }

        //
        // If binding id is mapped remove the binding
        //
        synchronized (bindings_) {
            if (bindings_.remove(oid) == null)
                throw new org.apache.yoko.orb.OB.BootManagerPackage.NotFound();
        }
    }

    public void set_locator(BootLocator locator) {
        //
        // Set the BootLocator
        //
        locator_ = locator;
    }

    // -------------------------------------------------------------------
    // BootManager_impl internal methods
    // ------------------------------------------------------------------

    public org.omg.IOP.IOR _OB_locate(byte[] id) {
        //
        // First check the internal hash table and then the
        // registered BootLocator (if there is one) to find the
        // binding for the requested ObjectId.
        //
        ObjectIdHasher oid = new ObjectIdHasher(id);
        
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Searching for binding with id " + IORUtil.dump_octets(id)); 
        }
        org.omg.CORBA.Object obj = (org.omg.CORBA.Object) bindings_.get(oid);
        if (obj == null && locator_ != null) {
            logger.fine("Object not found, passing on to locator");
            try {
                org.omg.CORBA.ObjectHolder objHolder = new org.omg.CORBA.ObjectHolder();
                org.omg.CORBA.BooleanHolder addHolder = new org.omg.CORBA.BooleanHolder();
                locator_.locate(id, objHolder, addHolder);

                obj = objHolder.value;
                if (addHolder.value) {
                    bindings_.put(oid, obj);
                }
            } catch (org.apache.yoko.orb.OB.BootManagerPackage.NotFound ex) {
            }
        }

        if (obj == null) {
            // these should map to initial references as well when used as a corbaloc name.
            // convert the key to a string and try for one of those 
            String keyString = new String(id); 
            try {
                obj = orb_.resolve_initial_references(keyString); 
            } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
                // if this is not valid, it won't work 
                return null; 
            }
            // just return null if still not there 
            if (obj == null) {
                return null;
            }
        }

        org.apache.yoko.orb.CORBA.Delegate p = (org.apache.yoko.orb.CORBA.Delegate) (((org.omg.CORBA.portable.ObjectImpl) obj)
                ._get_delegate());
        return p._OB_IOR();
    }
}

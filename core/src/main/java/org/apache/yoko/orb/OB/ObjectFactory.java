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

public final class ObjectFactory {
    static final Logger logger = Logger.getLogger(ObjectFactory.class.getName());
    
    private boolean destroy_; // True if destroy() was called

    ORBInstance orbInstance_; // The ORBInstance object

    org.omg.CORBA.PolicyManager policyManager_; // The PolicyManager object

    // ----------------------------------------------------------------------
    // ObjectFactory private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert._OB_assert(destroy_);

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // ObjectFactory package member implementations
    // ----------------------------------------------------------------------

    void destroy() {
        Assert._OB_assert(!destroy_);
        destroy_ = true;

        //
        // Set the ORBInstance object to nil
        //
        orbInstance_ = null;

        //
        // Set the PolicyManager object to nil
        //
        policyManager_ = null;
    }

    // ----------------------------------------------------------------------
    // ObjectFactory public member implementations
    // ----------------------------------------------------------------------

    public void setORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    public void setPolicyManager(org.omg.CORBA.PolicyManager policyManager) {
        policyManager_ = policyManager;
    }

    public org.omg.CORBA.Object createObject(org.omg.IOP.IOR ior) {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_) {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        //
        // Check for nil object reference
        //
        if (ior.type_id.length() == 0 && ior.profiles.length == 0) {
            return null;
        }

        logger.fine("Creating an object of type " + ior.type_id); 
        
        //
        // Create new delegate, set policies and change delegate
        //
        RefCountPolicyList policyList = new RefCountPolicyList(policies());
        org.apache.yoko.orb.CORBA.Delegate p = new org.apache.yoko.orb.CORBA.Delegate(
                orbInstance_, ior, ior, policyList);

        //
        // Create new object, set the delegate and return
        //
        org.omg.CORBA.portable.ObjectImpl obj;
        
        if(ior.type_id.startsWith("RMI")) {
        	obj = new org.apache.yoko.orb.CORBA.StubForRemote();
        }
        else {
        	obj = new org.apache.yoko.orb.CORBA.StubForObject();
        }
        obj._set_delegate(p);
        return obj;
    }

    public org.omg.CORBA.Object stringToObject(String ior) {
        logger.fine("Creating an object from " + ior); 
        return orbInstance_.getURLRegistry().parse_url(ior);
    }

    public org.omg.CORBA.Policy[] policies() {
        int[] ts = new int[0];
        return policyManager_.get_policy_overrides(ts);
    }
}

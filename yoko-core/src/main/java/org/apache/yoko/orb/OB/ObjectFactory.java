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
package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.CORBA.Delegate;
import org.apache.yoko.orb.CORBA.StubForObject;
import org.apache.yoko.orb.CORBA.StubForRemote;
import org.apache.yoko.util.Assert;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyManager;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.IOP.IOR;

import java.util.logging.Logger;

import static org.apache.yoko.util.MinorCodes.MinorORBDestroyed;
import static org.apache.yoko.util.MinorCodes.describeInitialize;
import static org.omg.CORBA.CompletionStatus.*;

public final class ObjectFactory {
    private static final Logger logger = Logger.getLogger(ObjectFactory.class.getName());
    private boolean destroy_; // True if destroy() was called
    private ORBInstance orbInstance_; // The ORBInstance object
    private PolicyManager policyManager_; // The PolicyManager object

    void destroy() {
        Assert.ensure(!destroy_);
        destroy_ = true;
        orbInstance_ = null;
        policyManager_ = null;
    }

    public void setORBInstance(ORBInstance orbInstance) {
        orbInstance_ = orbInstance;
    }

    public void setPolicyManager(PolicyManager policyManager) {
        policyManager_ = policyManager;
    }

    public org.omg.CORBA.Object createObject(IOR ior) {
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        if (destroy_) throw new INITIALIZE(describeInitialize(MinorORBDestroyed), MinorORBDestroyed, COMPLETED_NO);

        // Check for nil object reference
        if (ior.type_id.isEmpty() && ior.profiles.length == 0) return null;

        logger.fine("Creating an object of type " + ior.type_id); 
        
        // Create new delegate, set policies and change delegate
        Delegate p = new Delegate(orbInstance_, ior, ior, policies());

        // Create new object, set the delegate and return
        ObjectImpl obj = ior.type_id.startsWith("RMI") ? new StubForRemote() : new StubForObject();
        obj._set_delegate(p);
        return obj;
    }

    public org.omg.CORBA.Object stringToObject(String ior) {
        logger.fine("Creating an object from " + ior); 
        return orbInstance_.getURLRegistry().parse_url(ior);
    }

    public Policy[] policies() {
        return policyManager_.get_policy_overrides(new int[0]);
    }
}

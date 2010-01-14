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

import org.omg.CORBA.LocalObject;

public final class InitialServiceManager {
    static final Logger logger = Logger.getLogger(InitialServiceManager.class.getName());
    
    //
    // Set of available initial services
    //
    private class Service {
        String ref;

        org.omg.CORBA.Object obj;
    }

    private java.util.Hashtable services_ = new java.util.Hashtable(37);

    private String defaultInitRef_;

    private boolean destroy_ = false; // True if destroy() was called

    private ORBInstance orbInstance_; // The ORBInstance object

    // ----------------------------------------------------------------------
    // InitialServiceManager private and protected member implementations
    // ----------------------------------------------------------------------

    protected void finalize() throws Throwable {
        Assert._OB_assert(destroy_);

        super.finalize();
    }

    // ----------------------------------------------------------------------
    // InitialServiceManager package member implementations
    // ----------------------------------------------------------------------

    synchronized void destroy() {
        Assert._OB_assert(!destroy_); // May only be destroyed once
        destroy_ = true;

        services_ = null;
        orbInstance_ = null;
    }

    // ----------------------------------------------------------------------
    // InitialServiceManager public member implementations
    // ----------------------------------------------------------------------

    public InitialServiceManager() {
    }

    //
    // Set the ORBInstance object. Note that the initial service map
    // isn't populated until this method is called.
    //
    public void setORBInstance(ORBInstance instance) {
        orbInstance_ = instance;

        //
        // Populate the services map
        //
        java.util.Properties properties = orbInstance_.getProperties();

        //
        // Obtain the INS default initial reference URL
        //
        String value = properties.getProperty("yoko.orb.default_init_ref");
        if (value == null)
            defaultInitRef_ = "";
        else
            defaultInitRef_ = value;

        //
        // Add those services configured in the "yoko.orb.service" property
        //
        String propRoot = "yoko.orb.service.";
        java.util.Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (!key.startsWith(propRoot))
                continue;

            value = properties.getProperty(key);
            Assert._OB_assert(value != null);
            key = key.substring(propRoot.length());
            try {
                addInitialReference(key, value, true);
            } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
                Assert._OB_assert(ex);
            }
        }

    }

    public synchronized String[] listInitialServices() {
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                    .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                    org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        String[] list = new String[services_.size()];

        int i = 0;
        java.util.Enumeration e = services_.keys();
        while (e.hasMoreElements())
            list[i++] = (String) e.nextElement();

        return list;
    }

    public synchronized org.omg.CORBA.Object resolveInitialReferences(
            String identifier) throws org.omg.CORBA.ORBPackage.InvalidName {
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

        Assert._OB_assert(identifier != null);
        
        logger.fine("Resolving initial ORB reference for " + identifier); 

        ObjectFactory objectFactory = orbInstance_.getObjectFactory();

        org.omg.CORBA.Object obj = null;

        //
        // Search the list of initial references
        //
        Service svc = (Service) services_.get(identifier);
        if (svc != null) {
            if (svc.obj != null) {
                obj = svc.obj;
            }
            else if (svc.ref.length() > 0) {
                obj = objectFactory.stringToObject(svc.ref);
                svc.obj = obj;
                services_.put(identifier, svc);
            }
        }
        
        logger.fine("No match found for ORB intial reference " + identifier); 

        //
        // If no match was found, and there's a default initial
        // reference "template", then try to compose a URL using
        // the identifier as the object-key. However, we only do
        // this if the service really doesn't exist in our table,
        // since there could be a service with a nil value.
        //
        if (obj == null && defaultInitRef_.length() > 0
                && !services_.containsKey(identifier)) {
            String url = defaultInitRef_ + '/' + identifier;
            obj = objectFactory.stringToObject(url);
        }

        if (obj == null) {
            logger.fine("No default initializer found for ORB intial reference " + identifier); 
            throw new org.omg.CORBA.ORBPackage.InvalidName();
        }

        //
        // If the object is a l-c object, return the object now
        //
        if (obj instanceof org.omg.CORBA.LocalObject) {
            return obj;
        }

        //
        // If the object is remote, return a new reference with the
        // current set of policies applied, but only set ORB policies
        // if they are not already set on the object
        //
        org.omg.CORBA.Policy[] orbPolicies = objectFactory.policies();
        java.util.Vector vec = new java.util.Vector();
        for (int i = 0; i < orbPolicies.length; i++) {
            org.omg.CORBA.Policy policy = null;
            try {
                policy = obj._get_policy(orbPolicies[i].policy_type());
            } catch (org.omg.CORBA.INV_POLICY ex) {
            }

            if (policy == null) {
                policy = orbPolicies[i];
            }

            vec.addElement(policy);
        }
        org.omg.CORBA.Policy[] p = new org.omg.CORBA.Policy[vec.size()];
        vec.copyInto(p);

        return obj._set_policy_override(p, org.omg.CORBA.SetOverrideType.SET_OVERRIDE);
    }

    public void addInitialReference(String name, org.omg.CORBA.Object obj)
            throws org.omg.CORBA.ORBPackage.InvalidName {
        addInitialReference(name, obj, false);
    }

    public synchronized void addInitialReference(String name, String iorString,
            boolean override) throws org.omg.CORBA.ORBPackage.InvalidName {
        logger.fine("Adding initial reference name=" + name + ", ior=" + iorString); 
        //
        // The ORB destroys this object, so it's an initialization error
        // if this operation is called after ORB destruction
        //
        if (destroy_)
        {
            throw new org.omg.CORBA.INITIALIZE(org.apache.yoko.orb.OB.MinorCodes
                                               .describeInitialize(org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed),
                                               org.apache.yoko.orb.OB.MinorCodes.MinorORBDestroyed,
                                               org.omg.CORBA.CompletionStatus.COMPLETED_NO);
        }

        Assert._OB_assert(name != null && iorString != null);

        if (services_.containsKey(name) && !override)
        {
            logger.fine("Initial reference name=" + name + "already exists"); 
            throw new org.omg.CORBA.ORBPackage.InvalidName();
        }

        Service svc = new Service();
        svc.ref = iorString;
        services_.put(name, svc);
    }

    public synchronized void addInitialReference(String name,
            org.omg.CORBA.Object p, boolean override)
            throws org.omg.CORBA.ORBPackage.InvalidName {
        if (p != null) {
            logger.fine("Adding initial reference name=" + name + " of type " + p.getClass().getName()); 
        }
        else {
            logger.fine("Adding initial reference name=" + name + " with null implementation"); 
        }
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

        Assert._OB_assert(name != null);

        if (services_.containsKey(name) && !override) {
            throw new org.omg.CORBA.ORBPackage.InvalidName();
        }

        Service svc = new Service();
        svc.ref = "";
        svc.obj = p;
        services_.put(name, svc);
    }
}

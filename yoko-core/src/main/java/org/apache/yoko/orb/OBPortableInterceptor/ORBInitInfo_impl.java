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

package org.apache.yoko.orb.OBPortableInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

//
// An ORBacus-specific derivation of PortableInterceptor::ORBInitInfo
//
final public class ORBInitInfo_impl extends org.omg.CORBA.LocalObject implements
        org.apache.yoko.orb.OBPortableInterceptor.ORBInitInfo {
    //
    // The ORB
    //
    private org.omg.CORBA.ORB orb_;

    //
    // Arguments
    //
    private String[] args_;

    //
    // The ORB id
    //
    private String id_;

    //
    // The PIManager
    //
    private org.apache.yoko.orb.OB.PIManager manager_;

    //
    // The initial service mgr
    //
    private org.apache.yoko.orb.OB.InitialServiceManager initServiceManager_;

    //
    // The Codec Factory
    //
    private org.omg.IOP.CodecFactory codecFactory_; // The codec factory

    private boolean destroy_;

    // ------------------------------------------------------------------
    // Public member implementations
    // ------------------------------------------------------------------

    public ORBInitInfo_impl(org.omg.CORBA.ORB orb, String[] args, String id,
            org.apache.yoko.orb.OB.PIManager manager,
            org.apache.yoko.orb.OB.InitialServiceManager initServiceManager,
            org.omg.IOP.CodecFactory codecFactory) {
        orb_ = orb;
        args_ = args;
        id_ = id;
        manager_ = manager;
        initServiceManager_ = initServiceManager;
        codecFactory_ = codecFactory;
        destroy_ = false;
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java mapping
    // ------------------------------------------------------------------

    public String[] arguments() {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        String[] seq = new String[args_.length];
        System.arraycopy(args_, 0, seq, 0, args_.length);
        return seq;
    }

    public String orb_id() {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        return id_;
    }

    public org.omg.IOP.CodecFactory codec_factory() {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        return codecFactory_;
    }

    public void register_initial_reference(String name, org.omg.CORBA.Object obj)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        try {
            initServiceManager_.addInitialReference(name, obj);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            throw (org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName)new 
                org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName().initCause(ex); 
        }
    }

    public org.omg.CORBA.Object resolve_initial_references(String name)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        // TODO: check state
        try {
            // we delegate this to the ORB rather than call the initial service manager directly. 
            // because the ORB has a special test to initialize the RootPOA service if it 
            // doesn't exist. 
            return orb_.resolve_initial_references(name);
        } catch (org.omg.CORBA.ORBPackage.InvalidName ex) {
            throw (org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName)new 
                org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName().initCause(ex); 
        }
    }

    public void add_client_request_interceptor(
            org.omg.PortableInterceptor.ClientRequestInterceptor i)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        manager_.addClientRequestInterceptor(i);
    }

    public void add_server_request_interceptor(
            org.omg.PortableInterceptor.ServerRequestInterceptor i)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        manager_.addServerRequestInterceptor(i);
    }

    public void add_ior_interceptor(org.omg.PortableInterceptor.IORInterceptor i)
            throws org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        manager_.addIORInterceptor(i, false);
    }

    public int allocate_slot_id() {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        return manager_.allocateSlotId();
    }

    public void register_policy_factory(int type,
            org.omg.PortableInterceptor.PolicyFactory factory) {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        manager_.registerPolicyFactory(type, factory);
    }

    public org.omg.CORBA.ORB orb() {
        if (destroy_)
            throw new org.omg.CORBA.OBJECT_NOT_EXIST(
                    "Object has been destroyed");

        return orb_;
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public void _OB_destroy() {
        destroy_ = true;
        orb_ = null;
        manager_ = null;
        initServiceManager_ = null;
        codecFactory_ = null;
    }
}

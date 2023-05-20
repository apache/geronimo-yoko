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
package org.apache.yoko.orb.PortableInterceptor;
 
import org.apache.yoko.orb.OBPortableServer.POAPolicies;
import org.apache.yoko.util.Assert;
import org.apache.yoko.util.MinorCodes;

final public class IORInfo_impl extends org.omg.CORBA.LocalObject implements
        org.omg.PortableInterceptor.IORInfo {
    private org.apache.yoko.orb.OCI.Acceptor[] acceptors_;

    private java.util.Hashtable table_;

    private java.util.Vector all_;

    private org.omg.CORBA.Policy[] policies_;
    
    private org.apache.yoko.orb.OBPortableServer.POAPolicies poaPolicies_; 

    private org.omg.PortableInterceptor.ObjectReferenceTemplate adapterTemplate_;

    private org.omg.PortableInterceptor.ObjectReferenceFactory currentFactory_;

    private String id_;

    private short state_;

    //
    // The ORB instance
    //
    protected org.apache.yoko.orb.OB.ORBInstance orbInstance_;

    public IORInfo_impl(org.apache.yoko.orb.OB.ORBInstance orbInstance,
            org.apache.yoko.orb.OCI.Acceptor[] acceptors,
            org.omg.CORBA.Policy[] policies, POAPolicies poaPolicies, String id, short state) {
        table_ = new java.util.Hashtable();
        all_ = new java.util.Vector();

        orbInstance_ = orbInstance;
        acceptors_ = acceptors;
        policies_ = policies;
        poaPolicies_ = poaPolicies; 
        id_ = id;
        state_ = state;

        //
        // Add an entry to the component table for each acceptor
        //
        for (int i = 0; i < acceptors.length; ++i)
            table_.put(acceptors[i].tag(), new java.util.Vector());
    }

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public org.omg.CORBA.Policy get_effective_policy(int type) {
        for (int i = 0; i < policies_.length; i++) {
            if (policies_[i].policy_type() == type) {
                return policies_[i];
            }
        }

        // if the target policy was not in the current policy list, check to see
        // if the type has even been registered.  If it is valid, return null
        // to indicate we ain't got one.
        if (orbInstance_.getPolicyFactoryManager().isPolicyRegistered(type)) {
            return null;
        }

        throw new org.omg.CORBA.INV_POLICY(
                MinorCodes
                        .describeInvPolicy(MinorCodes.MinorNoPolicyFactory)
                        + ": " + type,
                MinorCodes.MinorNoPolicyFactory,
                org.omg.CORBA.CompletionStatus.COMPLETED_NO);
    }

    public void add_ior_component(org.omg.IOP.TaggedComponent data) {
        all_.addElement(data);
    }

    public void add_ior_component_to_profile(org.omg.IOP.TaggedComponent data,
            int id) {
        java.util.Vector profile = (java.util.Vector) table_
                .get(id);
        if (profile == null)
            throw new org.omg.CORBA.BAD_PARAM(
                    MinorCodes
                            .describeBadParam(MinorCodes.MinorInvalidProfileId)
                            + ": " + id,
                    MinorCodes.MinorInvalidProfileId,
                    org.omg.CORBA.CompletionStatus.COMPLETED_NO);

        profile.addElement(data);
    }

    public org.omg.PortableInterceptor.ObjectReferenceTemplate adapter_template() {
        if (adapterTemplate_ == null)
            throw new org.omg.CORBA.BAD_INV_ORDER();
        return adapterTemplate_;
    }

    public org.omg.PortableInterceptor.ObjectReferenceFactory current_factory() {
        return currentFactory_;
    }

    public void current_factory(
            org.omg.PortableInterceptor.ObjectReferenceFactory currentFactory) {
        currentFactory_ = currentFactory;
    }

    public String manager_id() {
        return id_;
    }

    public short state() {
        return state_;
    }

    public void _OB_adapterTemplate(
            org.omg.PortableInterceptor.ObjectReferenceTemplate adapterTemplate) {
        Assert.ensure(adapterTemplate_ == null);
        adapterTemplate_ = adapterTemplate;
    }

    public void _OB_state(short state) {
        state_ = state;
    }

    public void _OB_addComponents(org.omg.IOP.IORHolder ior,
            org.omg.GIOP.Version version) {
        org.apache.yoko.orb.OCI.ProfileInfo profileInfo = new org.apache.yoko.orb.OCI.ProfileInfo();
        profileInfo.major = version.major;
        profileInfo.minor = version.minor;
        profileInfo.key = "".getBytes();

        for (int i = 0; i < acceptors_.length; ++i) {
            //
            // Collect the components to be supplied to add_profiles().
            // We start with the generic components (allComponents_), and
            // may have to append components specific to this acceptor's
            // profile tag.
            //
            java.util.Vector components = (java.util.Vector) table_.get(acceptors_[i].tag());
            if (!components.isEmpty()) {
                int len = all_.size() + components.size();
                profileInfo.components = new org.omg.IOP.TaggedComponent[len];
                int j;
                for (j = 0; j < all_.size(); j++)
                    profileInfo.components[j] = (org.omg.IOP.TaggedComponent) all_
                            .elementAt(j);
                for (j = 0; j < components.size(); j++)
                    profileInfo.components[all_.size() + j] = (org.omg.IOP.TaggedComponent) components
                            .elementAt(j);
            } else {
                profileInfo.components = new org.omg.IOP.TaggedComponent[all_
                        .size()];
                for (int j = 0; j < all_.size(); j++)
                    profileInfo.components[j] = (org.omg.IOP.TaggedComponent) all_
                            .elementAt(j);
            }

            acceptors_[i].add_profiles(profileInfo, poaPolicies_, ior);
        }
    }
}

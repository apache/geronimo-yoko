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

final public class MessageRoutingIORInterceptor_impl extends
        org.omg.CORBA.LocalObject implements
        org.omg.PortableInterceptor.IORInterceptor_3_0 {
    //
    // The list of AMI routers
    //
    private org.omg.MessageRouting.Router[] routerList_;

    public MessageRoutingIORInterceptor_impl(
            org.omg.MessageRouting.Router[] routers) {
        routerList_ = routers;
    }

    //
    // IDL to Java Mapping
    //

    public String name() {
        return "";
    }

    public void destroy() {
    }

    public void establish_components(org.omg.PortableInterceptor.IORInfo info) {
        //
        // Make sure we have routers in our list. If not, there is no
        // reason to add this component to the IOR.
        //
        if (routerList_.length == 0)
            return;

        //
        // Create a tagged component for the router list
        //
        org.omg.IOP.TaggedComponent routerComponent = new org.omg.IOP.TaggedComponent();
        routerComponent.tag = org.omg.IOP.TAG_MESSAGE_ROUTERS.value;

        //
        // Create an OutputStream and write all of the router IORs
        //
        org.apache.yoko.orb.OCI.Buffer routerBuf = new org.apache.yoko.orb.OCI.Buffer();
        org.apache.yoko.orb.CORBA.OutputStream routerOut = new org.apache.yoko.orb.CORBA.OutputStream(
                routerBuf);
        routerOut._OB_writeEndian();

        //
        // This list actually needs to be written in reverse order
        //
        int len = routerList_.length;
        org.omg.MessageRouting.Router[] reorderedList = new org.omg.MessageRouting.Router[len];
        for (int i = len; i > 0; --i)
            reorderedList[i - 1] = routerList_[len - i];

        org.omg.MessageRouting.RouterListHelper.write(routerOut, reorderedList);

        //
        // Write the routerlist data into the tagged component
        //
        routerComponent.component_data = new byte[routerOut._OB_pos()];
        System.arraycopy(routerBuf.data(), 0, routerComponent.component_data,
                0, routerBuf.length());

        try {
            info.add_ior_component(routerComponent);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            // Ignore - profile may not be supported
        }

        //
        // Now add a tagged component to describe any applicable policies
        // for AMI routing
        //

        //
        // Retrieve the four effective policies that can be propgated in an
        // IOR from the IORInfo object
        //
        org.omg.Messaging.PolicyValueSeqHolder policiesHolder = new org.omg.Messaging.PolicyValueSeqHolder();
        policiesHolder.value = new org.omg.Messaging.PolicyValue[0];
        org.apache.yoko.orb.OB.MessageRoutingUtil.getComponentPolicyValues(
                info, policiesHolder);

        //
        // Don't write the tagged component unless we have some policies
        // set:
        //
        if (policiesHolder.value.length == 0)
            return;

        //
        // Create a tagged component for the policy value sequence
        //
        org.omg.IOP.TaggedComponent policyComponent = new org.omg.IOP.TaggedComponent();
        policyComponent.tag = org.omg.IOP.TAG_POLICIES.value;

        //
        // Create an OutputStream and write all of the policies
        //
        org.apache.yoko.orb.OCI.Buffer policyBuf = new org.apache.yoko.orb.OCI.Buffer();
        org.apache.yoko.orb.CORBA.OutputStream policyOut = new org.apache.yoko.orb.CORBA.OutputStream(
                policyBuf);
        policyOut._OB_writeEndian();
        org.omg.Messaging.PolicyValueSeqHelper.write(policyOut,
                policiesHolder.value);

        //
        // Write the routerlist data into the tagged component
        //
        policyComponent.component_data = new byte[policyOut._OB_pos()];
        System.arraycopy(policyBuf.data(), 0, policyComponent.component_data,
                0, policyBuf.length());

        try {
            info.add_ior_component(policyComponent);
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            // Ignore - profile may not be supported
        }
    }

    public void components_established(org.omg.PortableInterceptor.IORInfo info) {
    }

    public void adapter_manager_state_changed(String id, short state) {
    }

    public void adapter_state_changed(
            org.omg.PortableInterceptor.ObjectReferenceTemplate[] templates,
            short state) {
    }
}

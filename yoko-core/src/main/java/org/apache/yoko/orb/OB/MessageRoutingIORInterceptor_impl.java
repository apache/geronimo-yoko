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

import org.apache.yoko.orb.CORBA.OutputStream;
import org.apache.yoko.orb.OCI.Buffer;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.LocalObject;
import org.omg.IOP.TAG_MESSAGE_ROUTERS;
import org.omg.IOP.TAG_POLICIES;
import org.omg.IOP.TaggedComponent;
import org.omg.MessageRouting.Router;
import org.omg.MessageRouting.RouterListHelper;
import org.omg.Messaging.PolicyValue;
import org.omg.Messaging.PolicyValueSeqHelper;
import org.omg.Messaging.PolicyValueSeqHolder;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.IORInterceptor_3_0;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

final public class MessageRoutingIORInterceptor_impl extends LocalObject implements IORInterceptor_3_0 {
    // The list of AMI routers
    private final Router[] routerList_;

    public MessageRoutingIORInterceptor_impl(Router[] routers) {
        routerList_ = routers;
    }

    public String name() {
        return "";
    }

    public void destroy() {}

    public void establish_components(IORInfo info) {
        // Make sure we have routers in our list. If not, there is no
        // reason to add this component to the IOR.
        if (routerList_.length == 0) return;

        // Create a tagged component for the router list
        TaggedComponent routerComponent = new TaggedComponent();
        routerComponent.tag = TAG_MESSAGE_ROUTERS.value;

        // Create an OutputStream and write all of the router IORs
        try (OutputStream routerOut = new OutputStream(new Buffer())) {
            routerOut._OB_writeEndian();

            // This list actually needs to be written in reverse order
            int len = routerList_.length;
            Router[] reorderedList = new Router[len];
            for (int i = len; i > 0; --i) reorderedList[i - 1] = routerList_[len - i];
            RouterListHelper.write(routerOut, reorderedList);

            // Write the routerlist data into the tagged component
            routerComponent.component_data = routerOut.copyWrittenBytes();
        }
        try {
            info.add_ior_component(routerComponent);
        } catch (BAD_PARAM ex) {
            // Ignore - profile may not be supported
        }

        // Now add a tagged component to describe any applicable policies for AMI routing

        // Retrieve the four effective policies that can be propgated in an IOR from the IORInfo object
        PolicyValueSeqHolder policiesHolder = new PolicyValueSeqHolder();
        policiesHolder.value = new PolicyValue[0];
        MessageRoutingUtil.getComponentPolicyValues(info, policiesHolder);

        // Don't write the tagged component unless we have some policies set:
        if (policiesHolder.value.length == 0) return;

        // Create a tagged component for the policy value sequence
        TaggedComponent policyComponent = new TaggedComponent();
        policyComponent.tag = TAG_POLICIES.value;

        // Create an OutputStream and write all of the policies
        //
        try (OutputStream policyOut = new OutputStream(new Buffer())) {
            policyOut._OB_writeEndian();
            PolicyValueSeqHelper.write(policyOut, policiesHolder.value);

            // Write the routerlist data into the tagged component
            policyComponent.component_data = policyOut.copyWrittenBytes();
        }
        try {
            info.add_ior_component(policyComponent);
        } catch (BAD_PARAM ignored) {
            // Ignore - profile may not be supported
        }
    }

    public void components_established(IORInfo info) {}

    public void adapter_manager_state_changed(String id, short state) { }

    public void adapter_state_changed(ObjectReferenceTemplate[] templates, short state) {}
}

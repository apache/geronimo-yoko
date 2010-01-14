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

package org.apache.yoko.orb.OAD;

import org.apache.yoko.orb.OAD.AlreadyLinked;
import org.apache.yoko.orb.OAD.ProcessEndpoint;
import org.apache.yoko.orb.OAD.ProcessEndpointManager;
import org.apache.yoko.orb.OAD.ProcessEndpointManagerHelper;
import org.apache.yoko.orb.OAD.ProcessEndpointPOA;

final public class ProcessEndpoint_impl extends ProcessEndpointPOA {
    private String name_;

    private String id_;

    private org.omg.CORBA.Policy[] pl_;

    private org.omg.PortableServer.POA poa_;

    private org.apache.yoko.orb.OB.ORBControl orbControl_;

    public ProcessEndpoint_impl(String name, String id,
            org.omg.PortableServer.POA poa,
            org.apache.yoko.orb.OB.ORBControl orbControl) {
        name_ = name;
        id_ = id;
        poa_ = poa;
        orbControl_ = orbControl;

        //
        // Create a PolicyList for RETRY_ALWAYS
        //
        pl_ = new org.omg.CORBA.Policy[1];
        pl_[0] = new org.apache.yoko.orb.OB.RetryPolicy_impl(
                org.apache.yoko.orb.OB.RETRY_ALWAYS.value, 0, 1, false);
    }

    public void reestablish_link(ProcessEndpointManager d) {
        //
        // Set the retry policy on this object
        //
        org.omg.CORBA.Object obj = d._set_policy_override(pl_,
                org.omg.CORBA.SetOverrideType.SET_OVERRIDE);
        ProcessEndpointManager manager = ProcessEndpointManagerHelper
                .narrow(obj);

        ProcessEndpoint cb = _this();

        //
        // Establish a new link with the ProcessEndpointManager
        //
        try {
            manager.establish_link(name_, id_, 0xFFFFFFFF, cb);
        } catch (AlreadyLinked ex) {
        } catch (org.omg.CORBA.SystemException ex) {
            // logger.error("connect_server failed: " + ex);
        }
    }

    public void stop() {
        orbControl_.shutdownServer(false);
    }

    public org.omg.PortableServer.POA _default_POA() {
        return poa_;
    }
}

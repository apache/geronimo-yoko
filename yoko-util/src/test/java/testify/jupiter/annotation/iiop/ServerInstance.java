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
package testify.jupiter.annotation.iiop;

import org.apache.yoko.orb.OBPortableServer.POAManager_impl;
import org.apache.yoko.orb.OCI.IIOP.AcceptorInfo;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Policy;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.IdUniquenessPolicyValue;
import org.omg.PortableServer.ImplicitActivationPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.ThreadPolicyValue;
import testify.bus.Bus;
import testify.jupiter.annotation.iiop.ConfigureServer.ServerName;
import testify.util.Maps;
import testify.util.Throw;

import java.util.Map;
import java.util.Properties;

class ServerInstance {
    final Bus bus;
    final ORB orb;
    final Map<Class<?>, Object> paramMap;
    final POA childPoa;
    final int port;
    final String host;

    ServerInstance(Bus bus, ServerName name, String[] args, Properties props) {
        this.bus = bus;
        this.orb = ORB.init(args, props);
        try {
            POA rootPoa = (POA) orb.resolve_initial_references("RootPOA");
            POAManager_impl pm = (POAManager_impl) rootPoa.the_POAManager();
            pm.activate();
            final AcceptorInfo info = (AcceptorInfo) pm.get_acceptors()[0].get_info();
            // We might have been started up without a specific port.
            // In any case, dig out the host and port number and save them away.
            this.port = info.port() & 0xFFFF;
            this.host = info.hosts()[0];
            bus.log(() -> String.format("Server listening on host %s and port %d%n", host, port));
            // create the POA policies for the server
            Policy[] policies = {
                    rootPoa.create_thread_policy(ThreadPolicyValue.ORB_CTRL_MODEL),
                    rootPoa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT),
                    rootPoa.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID),
                    rootPoa.create_id_uniqueness_policy(IdUniquenessPolicyValue.MULTIPLE_ID),
                    rootPoa.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN),
                    rootPoa.create_request_processing_policy(RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY),
                    rootPoa.create_implicit_activation_policy(ImplicitActivationPolicyValue.NO_IMPLICIT_ACTIVATION),
            };
            childPoa = rootPoa.create_POA(name.toString(), pm, policies);
            this.paramMap = Maps.of(ORB.class, orb, Bus.class, bus, POA.class, childPoa);
        } catch (InvalidName | AdapterInactive | AdapterAlreadyExists | InvalidPolicy e) {
            throw Throw.andThrowAgain(e);
        }
    }

    void stop() {
        try {
            bus.log("Calling orb.shutdown(true)");
            orb.shutdown(true);
            bus.log("ORB shutdown complete, calling orb.destroy()");
            orb.destroy();
            bus.log("orb.destroy() returned");
        } catch (BAD_INV_ORDER e) {
            // The ORB is sometimes already shut down.
            // This should not cause an error in the test.
            // TODO: find out how this happens
            if (e.minor != 4) throw e;
        }
    }
}

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
package test.ins;

import org.apache.yoko.orb.OB.BootManager;
import org.apache.yoko.orb.OB.BootManagerHelper;
import org.apache.yoko.orb.OBPortableServer.POAManagerHelper;
import org.apache.yoko.orb.OCI.Acceptor;
import org.apache.yoko.orb.OCI.AcceptorInfo;
import org.apache.yoko.orb.OCI.IIOP.AcceptorInfoHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManager;
import test.ins.URLTest.IIOPAddress;
import test.ins.URLTest.IIOPAddress_impl;
import testify.bus.Bus;
import testify.parts.ServerPart;

public final class InsServer extends ServerPart {
    // Simple server providing objects for corba URL tests
    public void run(ORB orb, Bus bus) throws UserException {
        // corbaloc key
        String keyStr = bus.get("key");

        // Resolve Root POA
        POA poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

        // Activate the POA manager
        POAManager manager = poa.the_POAManager();
        manager.activate();

        // Create POA
        Policy[] policies = new Policy[2];
        policies[0] = poa.create_id_assignment_policy(IdAssignmentPolicyValue.USER_ID);
        policies[1] = poa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
        POA testPOA = poa.create_POA("testPOA", manager, policies);

        // Resolve the Boot Manager
        BootManager bootManager = BootManagerHelper.narrow(orb.resolve_initial_references("BootManager"));

        // Find the POA Manager's Acceptor Port
        org.apache.yoko.orb.OBPortableServer.POAManager obManager = POAManagerHelper.narrow(manager);
        Acceptor[] acceptors = obManager.get_acceptors();

        org.apache.yoko.orb.OCI.IIOP.AcceptorInfo iiopInfo = null;

        for (Acceptor acceptor : acceptors) {
            AcceptorInfo info = acceptor.get_info();
            iiopInfo = AcceptorInfoHelper.narrow(info);

            if (iiopInfo != null) break;
        }

        String[] hosts = iiopInfo.hosts();
        int port = (int) (char) iiopInfo.port();

        // corbaloc test object
        IIOPAddress corbaURLObj;

        IIOPAddress_impl urlServant = new IIOPAddress_impl(orb, hosts[0], port, keyStr, "corbaloc");
        byte[] oid = urlServant.getKey().getBytes();

        testPOA.activate_object_with_id(oid, urlServant);
        corbaURLObj = urlServant._this(orb);
        bootManager.add_binding(oid, corbaURLObj);

        // Save references. This must be done after POA manager
        // activation, otherwise there is a potential for a race
        // condition between the client sending request and the server
        // not being ready yet.
        bus.put("ior", orb.object_to_string(corbaURLObj));

    }
}

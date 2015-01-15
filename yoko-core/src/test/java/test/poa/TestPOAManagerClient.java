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

package test.poa;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

public final class TestPOAManagerClient extends test.common.TestBase {
    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            org.omg.CORBA.Object obj = orb
                    .string_to_object("relfile:/Test.ref");
            if (obj == null) {
                System.err.println("cannot read IOR from Test.ref");
                System.exit(1);
            }
            TestServer server = TestServerHelper.narrow(obj);

            obj = orb.string_to_object("relfile:/POAManagerProxy.ref");
            if (obj == null) {
                System.err.println("cannot read IOR from POAManagerProxy.ref");
                System.exit(1);
            }
            POAManagerProxy manager = POAManagerProxyHelper.narrow(obj);

            try {
                manager.activate();
            } catch (test.poa.POAManagerProxyPackage.AdapterInactive ex) {
                assertTrue(false);
            }

            TestInfo[] info = server.get_info();

            new TestPOAManagerCommon(manager, info);

            server.deactivate();
        } catch (SystemException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (SystemException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(0);
    }
}

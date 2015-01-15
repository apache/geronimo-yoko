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
import org.omg.PortableServer.POAManagerPackage.*;

public final class TestCreate extends test.common.TestBase {
    static void run(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        Policy[] policies = new Policy[0];
        POA poa, parent, poa2, poa3;
        POAManager mgr;
        String str;

        POAManager rootMgr = root.the_POAManager();
        assertTrue(rootMgr != null);

        //
        // Test: POAManager should be in HOLDING state
        //
        assertTrue(rootMgr.get_state() == State.HOLDING);

        //
        // Create child POA
        //
        try {
            poa = root.create_POA("poa1", null, policies);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Test: POAManager should NOT be the same as the root's manager
        //
        mgr = poa.the_POAManager();
        assertTrue(!mgr._is_equivalent(rootMgr));

        //
        // Test: POAManager should be in HOLDING state
        //
        assertTrue(mgr.get_state() == State.HOLDING);

        //
        // Test: Confirm name
        //
        str = poa.the_name();
        assertTrue(str.equals("poa1"));

        //
        // Test: Confirm parent
        //
        parent = poa.the_parent();
        assertTrue(parent._is_equivalent(root));

        //
        // Test: AdapterAlreadyExists exception
        //
        try {
            poa2 = root.create_POA("poa1", null, policies);
            assertTrue(false); // create_POA should not have succeeded
        } catch (AdapterAlreadyExists ex) {
            // expected
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        }

        //
        // Test: InvalidPolicy exception
        //
        Policy[] invalidpolicies = new Policy[1];
        invalidpolicies[0] = root
                .create_servant_retention_policy(org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN);

        try {
            poa2 = root.create_POA("invalid", null, invalidpolicies);
            assertTrue(false); // create_POA should not have succeeded
        } catch (InvalidPolicy ex) {
            // expected
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Create another child of root POA
        //
        try {
            poa2 = root.create_POA("poa2", rootMgr, policies);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Test: POAManager should be the same as the root's manager
        //
        mgr = poa2.the_POAManager();
        assertTrue(mgr._is_equivalent(rootMgr));

        //
        // Create child of child POA
        //
        try {
            poa3 = poa2.create_POA("child", rootMgr, policies);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Test: Confirm parent
        //
        parent = poa3.the_parent();
        assertTrue(parent._is_equivalent(poa2));

        poa.destroy(true, true);
        poa2.destroy(true, true);
    }

    public static void main(String[] args) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        int status = 0;
        ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = ORB.init(args, props);

            POA root = TestUtil.GetRootPOA(orb);

            //
            // Run the test
            //
            System.out.print("Testing POA::create_POA()... ");
            System.out.flush();
            run(orb, root);
            System.out.println("Done!");
        } catch (SystemException ex) {
            ex.printStackTrace();
            status = 1;
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (SystemException ex) {
                ex.printStackTrace();
                status = 1;
            }
        }

        System.exit(status);
    }
}

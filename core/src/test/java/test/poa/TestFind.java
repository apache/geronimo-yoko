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

import java.util.Properties;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POAPackage.*;

public final class TestFind extends test.common.TestBase {
    static void run(ORB orb, POA root) {
        org.omg.CORBA.Object obj;
        Policy[] policies = new Policy[0];
        POA poa, parent, poa2, poa3;
        POAManager mgr;
        String str;

        POAManager rootMgr = root.the_POAManager();
        TEST(rootMgr != null);

        //
        // Create child POA
        //
        try {
            poa = root.create_POA("poa1", rootMgr, policies);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Test: find_POA
        //
        try {
            poa2 = root.find_POA("poa1", false);
        } catch (AdapterNonExistent ex) {
            throw new RuntimeException();
        }
        TEST(poa2 != null);
        TEST(poa2._is_equivalent(poa));

        //
        // Test: AdapterNonExistent exception
        //
        try {
            poa2 = root.find_POA("poaX", false);
            TEST(false); // find_POA should not have succeeded
        } catch (AdapterNonExistent ex) {
            // expected
        }

        //
        // Create child POA
        //
        try {
            poa2 = root.create_POA("poa2", rootMgr, policies);
        } catch (InvalidPolicy ex) {
            throw new RuntimeException();
        } catch (AdapterAlreadyExists ex) {
            throw new RuntimeException();
        }

        //
        // Test: Confirm parent knows about child
        //
        try {
            poa3 = root.find_POA("poa2", false);
        } catch (AdapterNonExistent ex) {
            throw new RuntimeException();
        }

        TEST(poa3 != null);
        TEST(poa3._is_equivalent(poa2));
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
            System.out.print("Testing POA::find_POA... ");
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

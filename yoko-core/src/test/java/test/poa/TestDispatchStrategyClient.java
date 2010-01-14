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

import java.util.*;

final public class TestDispatchStrategyClient extends test.common.TestBase {
    public static void main(String args[]) {
        java.util.Properties props = new Properties();
        props.putAll(System.getProperties());
        props.put("org.omg.CORBA.ORBClass", "org.apache.yoko.orb.CORBA.ORB");
        props.put("org.omg.CORBA.ORBSingletonClass",
                "org.apache.yoko.orb.CORBA.ORBSingleton");

        org.omg.CORBA.ORB orb = null;

        try {
            //
            // Create ORB
            //
            orb = org.omg.CORBA.ORB.init(args, props);

            //
            // Get test server object reference
            //
            org.omg.CORBA.Object obj = orb
                    .string_to_object("relfile:/Test.ref");
            if (obj == null) {
                System.err.println("TestDispatchStrategyClient: "
                        + "cannot read IOR from Test.ref");
                System.exit(1);
            }

            TestServer server = TestServerHelper.narrow(obj);
            TestInfo[] info = server.get_info();

            //
            // Test the thread dispatch strategies by calling
            // methods on each returned object reference
            //
            org.omg.CORBA.Request req;

            int idx;
            for (idx = 0; idx < info.length; idx++) {
                TEST(info[idx].obj != null);

                for (int idx2 = 0; idx2 < 4; idx2++) {
                    req = info[idx].obj._request("aMethod");
                    req.send_deferred();
                }
            }

            idx = info.length * 4;
            do {
                req = orb.get_next_response();
            } while (--idx != 0);

            server.deactivate();
        } catch (org.omg.CORBA.SystemException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (org.omg.CORBA.UserException ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        if (orb != null) {
            try {
                orb.destroy();
            } catch (org.omg.CORBA.SystemException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        System.exit(0);
    }
}

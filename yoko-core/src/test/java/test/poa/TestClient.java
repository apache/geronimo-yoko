/*
 * Copyright 2015 IBM Corporation and others.
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
package test.poa;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.omg.CORBA.*;

public class TestClient extends test.common.TestBase {
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

            //
            // Get server
            //
            org.omg.CORBA.Object obj = orb
                    .string_to_object("relfile:/Test.ref");
            if (obj == null) {
                System.err.println("Can't read from Test.ref");
                System.exit(1);
            }

            TestServer server = TestServerHelper.narrow(obj);
            assertTrue(server != null);

            TestInfo[] info = server.get_info();
            for (int i = 0; i < info.length; i++) {
                try {
                    info[i].obj.aMethod();
                    assertTrue(info[i].except_id.length() == 0);
                } catch (SystemException ex) {
                    String id = org.apache.yoko.orb.OB.Util.getExceptionId(ex);
                    if (!id.equals(info[i].except_id)) {
                        System.err.println("TestClient: Invocation on "
                                + "object #" + i + " raised an "
                                + "unexpected exception");
                        System.err.println("Expected " + info[i].except_id
                                + " but caught " + id);
                        assertTrue(false);
                    }
                }
            }

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

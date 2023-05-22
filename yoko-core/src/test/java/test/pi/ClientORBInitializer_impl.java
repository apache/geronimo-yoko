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
package test.pi;

import org.omg.CORBA.*;
import org.omg.PortableInterceptor.*;

final public class ClientORBInitializer_impl extends org.omg.CORBA.LocalObject
        implements ORBInitializer {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    static ClientProxyManager clientProxyManager;

    private static boolean local_;

    static void _OB_setLocal(boolean l) {
        local_ = l;
    }

    //
    // IDL to Java Mapping
    //

    public void pre_init(ORBInitInfo info) {
        //
        // Test: PICurrent::allocate_slot_id
        //
        if (!local_) {
            int id = info.allocate_slot_id();
            TEST(id >= 0);
        }

        //
        // Test: Register a policy factory
        //
        PolicyFactory pf = new MyClientPolicyFactory_impl();
        info.register_policy_factory(MY_CLIENT_POLICY_ID.value, pf);

        System.out.print("Registering client request interceptors... ");
        System.out.flush();
        clientProxyManager = new ClientProxyManager(info);
        System.out.println("Done!");
    }

    public void post_init(ORBInitInfo info) {
    }
}

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

import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.PolicyFactory;

final public class ServerORBInitializer_impl extends org.omg.CORBA.LocalObject
        implements ORBInitializer {
    //
    // From TestBase (no multiple inheritance)
    //
    public static void TEST(boolean expr) {
        if (!expr)
            throw new test.common.TestException();
    }

    static ServerProxyManager serverProxyManager;

    //
    // IDL to Java Mapping
    //
    public void pre_init(ORBInitInfo info) {
        //
        // Test: PICurrent::allocate_slot_id
        //
        int id = info.allocate_slot_id();
        TEST(id >= 0);

        //
        // Test: register an IORInterceptor
        //
        IORInterceptor iorInterceptor = new MyIORInterceptor_impl(info);
        try {
            info.add_ior_interceptor(iorInterceptor);
        } catch (org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName ex) {
            throw new RuntimeException();
        }

        PolicyFactory pf = new MyServerPolicyFactory_impl();
        info.register_policy_factory(MY_SERVER_POLICY_ID.value, pf);

        serverProxyManager = new ServerProxyManager(info);

        //
        // TODO: Test resolve_initial_references
        //
    }

    public void post_init(ORBInitInfo info) {
        //
        // TODO: Test resolve_initial_references
        //
    }
}

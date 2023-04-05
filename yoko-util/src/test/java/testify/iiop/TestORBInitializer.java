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
package testify.iiop;

import org.junit.jupiter.api.Assertions;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ServerRequestInterceptor;

public interface TestORBInitializer extends TestLocalObject, ORBInitializer {
    default void pre_init(ORBInitInfo info) { }
    default void post_init(ORBInitInfo info) {
        try {
            if (this instanceof ClientRequestInterceptor) info.add_client_request_interceptor((ClientRequestInterceptor) this);
            if (this instanceof ServerRequestInterceptor) info.add_server_request_interceptor((ServerRequestInterceptor) this);
            if (this instanceof IORInterceptor) info.add_ior_interceptor((IORInterceptor) this);
        } catch(DuplicateName e) {
            Assertions.fail(e);
        }
    }
}


/*
 * Copyright 2010 IBM Corporation and others.
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

final class MyServerPolicyFactory_impl extends org.omg.CORBA.LocalObject
        implements PolicyFactory {
    //
    // IDL to Java Mapping
    //

    public Policy create_policy(int type, Any any) throws PolicyError {
        if (type == MY_SERVER_POLICY_ID.value) {
            try {
                int val = any.extract_long();
                return new MyServerPolicy_impl(val);
            } catch (BAD_OPERATION ex) {
                throw new PolicyError(BAD_POLICY_TYPE.value);
            }
        }
        throw new PolicyError(BAD_POLICY.value);
    }
}

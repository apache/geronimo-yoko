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
import org.omg.PortableServer.*;
import org.omg.PortableInterceptor.*;

final class MyServerPolicy_impl extends org.omg.CORBA.LocalObject implements
        MyServerPolicy {
    private int value_;

    MyServerPolicy_impl(int value) {
        value_ = value;
    }

    //
    // Standard IDL to Java Mapping
    //

    public int value() {
        return value_;
    }

    public int policy_type() {
        return MY_SERVER_POLICY_ID.value;
    }

    public Policy copy() {
        // TODO: Is this sufficient here?
        return this;
    }

    public void destroy() {
    }
}

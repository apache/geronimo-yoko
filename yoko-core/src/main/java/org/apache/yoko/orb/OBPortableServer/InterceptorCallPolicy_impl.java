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
package org.apache.yoko.orb.OBPortableServer;

import org.apache.yoko.orb.OBPortableServer.INTERCEPTOR_CALL_POLICY_ID;
import org.apache.yoko.orb.OBPortableServer.InterceptorCallPolicy;

public final class InterceptorCallPolicy_impl extends org.omg.CORBA.LocalObject
        implements InterceptorCallPolicy {
    private boolean value_;

    public InterceptorCallPolicy_impl(boolean value) {
        value_ = value;
    }

    public boolean value() {
        return value_;
    }

    public int policy_type() {
        return INTERCEPTOR_CALL_POLICY_ID.value;
    }

    public org.omg.CORBA.Policy copy() {
        return this;
    }

    public void destroy() {
    }
}

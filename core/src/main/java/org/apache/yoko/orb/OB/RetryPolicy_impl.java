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

package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.OB.RETRY_POLICY_ID;
import org.apache.yoko.orb.OB.RetryPolicy;

final public class RetryPolicy_impl extends org.omg.CORBA.LocalObject implements
        RetryPolicy {
    private short mode_;

    private int interval_;

    private int max_;

    private boolean remote_;

    // ------------------------------------------------------------------
    // Standard IDL to Java Mapping
    // ------------------------------------------------------------------

    public short retry_mode() {
        return mode_;
    }

    public int retry_interval() {
        return interval_;
    }

    public int retry_max() {
        return max_;
    }

    public boolean retry_remote() {
        return remote_;
    }

    public int policy_type() {
        return RETRY_POLICY_ID.value;
    }

    public org.omg.CORBA.Policy copy() {
        return this;
    }

    public void destroy() {
    }

    // ------------------------------------------------------------------
    // Yoko internal functions
    // Application programs must not use these functions directly
    // ------------------------------------------------------------------

    public RetryPolicy_impl(short mode, int interval, int max, boolean remote) {
        mode_ = mode;
        interval_ = interval;
        max_ = max;
        remote_ = remote;
    }
}

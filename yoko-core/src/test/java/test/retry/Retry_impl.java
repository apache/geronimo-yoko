/*
 * Copyright 2020 IBM Corporation and others.
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
package test.retry;

import org.omg.CORBA.TRANSIENT;
import org.omg.PortableServer.POA;

import static org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE;
import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

public class Retry_impl extends RetryPOA {
    private POA poa_;

    private int count_;

    private int max_;

    private boolean maybe_;

    public Retry_impl(POA poa) {
        poa_ = poa;
    }

    public POA _default_POA() {
        return poa_ == null ? super._default_POA() : poa_;
    }

    public void aMethod() {
        count_++;
        if (max_ > 0 && count_ <= max_) {
            throw new TRANSIENT(0, maybe_ ? COMPLETED_MAYBE : COMPLETED_NO);
        }
    }

    public int get_count() {
        return count_;
    }

    public void raise_exception(int max, boolean maybe) {
        count_ = 0;
        max_ = max;
        maybe_ = maybe;
    }
}

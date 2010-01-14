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

package test.retry;

public class Retry_impl extends RetryPOA {
    private org.omg.PortableServer.POA poa_;

    private int count_;

    private int max_;

    private boolean maybe_;

    public Retry_impl(org.omg.PortableServer.POA poa) {
        poa_ = poa;
    }

    public org.omg.PortableServer.POA _default_POA() {
        if (poa_ != null)
            return poa_;
        else
            return super._default_POA();
    }

    public void aMethod() {
        count_++;

        if (max_ > 0 && count_ <= max_) {
            if (maybe_)
                throw new org.omg.CORBA.TRANSIENT(0,
                        org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
            else
                throw new org.omg.CORBA.TRANSIENT(0,
                        org.omg.CORBA.CompletionStatus.COMPLETED_NO);
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
